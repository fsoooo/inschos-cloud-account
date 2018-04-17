package com.inschos.cloud.account.access.http.controller.action;

import com.inschos.cloud.account.access.http.controller.bean.AccountBean.*;
import com.inschos.cloud.account.access.http.controller.bean.ActionBean;
import com.inschos.cloud.account.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.account.access.http.controller.bean.ResponseMessage;
import com.inschos.cloud.account.assist.kit.ConstantKit;
import com.inschos.cloud.account.assist.kit.StringKit;
import com.inschos.cloud.account.assist.kit.TimeKit;
import com.inschos.cloud.account.data.dao.AccountDao;
import com.inschos.cloud.account.data.dao.AccountVerifyDao;
import com.inschos.cloud.account.data.dao.PlatformSystemDao;
import com.inschos.cloud.account.extend.worker.AccountUuidWorker;
import com.inschos.cloud.account.model.Account;
import com.inschos.cloud.account.model.AccountVerify;
import com.inschos.cloud.account.model.PlatformSystem;
import com.inschos.cloud.account.model.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

;

/**
 * Created by IceAnt on 2018/3/23.
 */
@Component
public class AccountAction extends BaseAction {

    @Autowired
    private AccountDao accountDao;
    @Autowired
    private AccountVerifyDao accountVerifyDao;
    @Autowired
    private PlatformSystemDao platformSystemDao;

    private final long CODE_VALID_TIME = 10*60*1000L;

    private final long CODE_UNIQUE_TIME = 5*60*1000L;

    private final long CODE_LIMIT_NO_RESEND_TIME = 60*1000l;

    public String login(ActionBean bean,String requestAccountType){
        LoginRequest request = requst2Bean(bean.body, LoginRequest.class);
        LoginResponse response = new LoginResponse();
        ResponseMessage errMessage = checkParam(request);
        if(errMessage.hasError()){
            return json(BaseResponse.CODE_FAILURE,errMessage, response);
        }
        int accountType = Account.getAccountType(requestAccountType);

        PlatformSystem system = _getChannelSystem(bean.referer);
        if(system==null || system.status != PlatformSystem.STATUS_OK){
            return json(BaseResponse.CODE_FAILURE,"系统未上线，请联系管理员", response);
        }


        Account account = accountDao.findByAccount(system.id,request.username, accountType, Account.ACCOUNT_FILED_USERNAME);
        switch (accountType){
            case Account.TYPE_CUST_USER:
                if(account==null){
                    account = accountDao.findByAccount(system.id,request.username, accountType, Account.ACCOUNT_FILED_PHONE);
                }
                break;
            case Account.TYPE_CUST_COM:
                if(account==null){
                    account = accountDao.findByAccount(system.id,request.username, accountType, Account.ACCOUNT_FILED_EMAIL);
                }
                break;
        }
        if(account!=null && account.password.equals(Account.generatePwd(request.password,account.salt))){

            String managerUuid = null;
            if(accountType==Account.TYPE_COMPANY){
                managerUuid = account.account_uuid;
            }

            if(account.status==Account.STATUS_NORMAL){
                String token = null;
                long timeMillis = TimeKit.currentTimeMillis();
                account.token = getLoginToken(account.account_uuid,accountType,managerUuid,system.id,account.salt);
                account.updated_at = timeMillis;
                if(accountDao.updateTokenByUuid(account)>0){
                    token = account.token;
                }

                if(!StringKit.isEmpty(token)){
                    response.data = new TokenData();
                    response.data.token = token;
                    return json(BaseResponse.CODE_SUCCESS,"登录成功", response);
                }else{
                    return json(BaseResponse.CODE_FAILURE,"登录失败", response);
                }
            }else{
                return json(BaseResponse.CODE_FAILURE,"账号异常，请联系管理员", response);
            }
        }else{
            return json(BaseResponse.CODE_FAILURE,"登录失败", response);
        }
    }

    public String registry(ActionBean bean,String requestAccountType){
        RegistryRequest request = requst2Bean(bean.body, RegistryRequest.class);
        RegistryResponse response = new RegistryResponse();

        int accountType = Account.getAccountType(requestAccountType);

        PlatformSystem system = _getChannelSystem(bean.referer);
        if(system==null || system.status != PlatformSystem.STATUS_OK){
            return json(BaseResponse.CODE_FAILURE,"系统未上线，请联系管理员", response);
        }

        List<String> ignore = new ArrayList<>();
        String errMsg = null;
        String verifyNameInput = null;
        switch (accountType){
            case Account.TYPE_CUST_USER:
                ignore.add("email");
                verifyNameInput = request.phone;
                break;
            case Account.TYPE_CUST_COM:
                ignore.add("phone");
                verifyNameInput = request.email;
                break;
            default:
                errMsg = "注册失败";
                break;
        }
        if(errMsg==null){
            ResponseMessage errMessage = checkParam(request,ignore);
            if(errMessage.hasError()){
                return json(BaseResponse.CODE_FAILURE,errMessage, response);
            }
        }else{
            return json(BaseResponse.CODE_FAILURE,errMsg, response);
        }

        boolean verifyFlag = _checkCode( verifyNameInput, request.code, accountType,null);

        if(verifyFlag){
            String password = request.password.trim();
            String salt = StringKit.randStr(6);
            String userId = null;
            boolean accountNameExsitFlag = false;

            Account account = accountDao.findByAccount(system.id,request.username, accountType, Account.ACCOUNT_FILED_USERNAME);

            if(account!=null){
                errMsg = "用户名已存在";
                accountNameExsitFlag = true;
            }
            if(!accountNameExsitFlag){
                switch (accountType){
                    case Account.TYPE_CUST_USER:
                        account = accountDao.findByAccount(system.id,request.phone, accountType, Account.ACCOUNT_FILED_PHONE);
                        if(account!=null){
                            errMsg = "手机号已注册";
                            accountNameExsitFlag = true;
                        }else{
                            // TODO: 2018/3/28  rpc  create user info
                        }
                        break;
                    case Account.TYPE_CUST_COM:
                        account = accountDao.findByAccount(system.id,request.email, accountType, Account.ACCOUNT_FILED_EMAIL);
                        if(account!=null){
                            errMsg = "邮箱地址已注册";
                            accountNameExsitFlag = true;
                        }else{
                            // TODO: 2018/3/28 rpc create company info
                        }
                        break;
                }
            }

            int resultAdd = 0;
            if(!accountNameExsitFlag&&!StringKit.isEmpty(userId)){
                Account addRecord = new Account();
                // uuID
                addRecord.account_uuid = String.valueOf(AccountUuidWorker.getWorker(1,1).nextId());
                addRecord.status = Account.STATUS_NORMAL;
                addRecord.password = Account.generatePwd(password,salt);
                addRecord.username = request.username;
                addRecord.phone = request.phone;
                addRecord.email = request.email;
                addRecord.user_type = accountType;
                addRecord.account_uuid = "";
                addRecord.user_id = userId;
                addRecord.salt = salt;
                addRecord.state = Common.STATE_ONLINE;
                addRecord.created_at = addRecord.updated_at = TimeKit.currentTimeMillis();
                addRecord.sys_id = system.id;
                resultAdd = accountDao.registry(addRecord);
                if(resultAdd==0){
                    // TODO: 2018/3/28 try to delete user info
                }else{
                    // TODO: 2018/4/12 try to add 客户关系
                }
            }

            if(resultAdd>0){
                response.data = new RegistrySuccessData();
                response.data.usernameTxt = "您的账号："+request.username;
                response.data.passwordTxt = "登录密码："+request.password;
                return json(BaseResponse.CODE_SUCCESS,"注册成功", response);
            }else{
                return json(BaseResponse.CODE_FAILURE,StringKit.isEmpty(errMsg)?"注册失败":errMsg, response);
            }

        }else{
            return json(BaseResponse.CODE_FAILURE,"请输入正确的验证码", response);
        }

    }

    public String resetPassword(ActionBean bean){
        ResetPasswordRequest request = requst2Bean(bean.body, ResetPasswordRequest.class);
        ResetPasswordResponse response = new ResetPasswordResponse();

        int accountType = bean.type;
        List<String> ignore = new ArrayList<>();
        String errMsg = null;
        String verifyNameInput = null;
        int accountField = 0;
        switch (accountType){
            case Account.TYPE_CUST_USER:
                ignore.add("email");
                verifyNameInput = request.phone;
                accountField = Account.ACCOUNT_FILED_PHONE;
                break;
            case Account.TYPE_CUST_COM:
                ignore.add("phone");
                verifyNameInput = request.email;
                accountField = Account.ACCOUNT_FILED_EMAIL;
                break;
            default:
                errMsg = "重置密码失败";
                break;
        }
        if(errMsg==null){
            ResponseMessage errMessage = checkParam(request,ignore);
            if(errMessage.hasError()){
                return json(BaseResponse.CODE_FAILURE,errMessage, response);
            }
        }else{
            return json(BaseResponse.CODE_FAILURE,errMsg, response);
        }

        boolean verifyFlag = _checkCode( verifyNameInput, request.code, accountType,bean.accountUuid);

        if (verifyFlag) {
            Account updateRecord = new Account();
            String salt = StringKit.randStr(6);
            updateRecord.account_uuid=bean.accountUuid;
            updateRecord.salt = salt;
            updateRecord.password = Account.generatePwd(request.password,salt);
            String token = getLoginToken(bean.accountUuid, accountType,bean.managerUuid,bean.sysId,salt);
            updateRecord.token = token;
            updateRecord.updated_at = TimeKit.currentTimeMillis();
            if(accountDao.updatePasswordTokenByUuid(updateRecord)>0){
                response.data = new TokenData();
                response.data.token = token;
                return json(BaseResponse.CODE_SUCCESS,"重设密码成功", response);
            }else{
                return json(BaseResponse.CODE_FAILURE,"重设密码失败", response);
            }
        }else{
            return json(BaseResponse.CODE_FAILURE,"请输入正确的验证码", response);
        }
    }

    public String forgetPassword(ActionBean bean,String requestAccountType){
        ForgetPasswordRequest request = requst2Bean(bean.body, ForgetPasswordRequest.class);
        BaseResponse response = new ResetPasswordResponse();

        int accountType = Account.getAccountType(requestAccountType);

        PlatformSystem system = _getChannelSystem(bean.referer);
        if(system==null || system.status != PlatformSystem.STATUS_OK){
            return json(BaseResponse.CODE_FAILURE,"系统未上线，请联系管理员", response);
        }
//        Account searchSystem = new Account();
//        searchSystem.sys_id = system.id;
//        searchSystem.user_type = Account.TYPE_COMPANY;
//        Account accountSystem = accountDao.findOneChannelSystem(searchSystem);
//        if(accountSystem==null){
//            return json(BaseResponse.CODE_FAILURE,"系统未上线，请联系管理员", response);
//        }

        List<String> ignore = new ArrayList<>();
        String errMsg = null;
        String verifyNameInput = null;
        switch (accountType){
            case Account.TYPE_CUST_USER:
                ignore.add("email");
                verifyNameInput = request.phone;
                break;
            case Account.TYPE_CUST_COM:
                ignore.add("phone");
                verifyNameInput = request.email;
                break;
            default:
                errMsg = "重设密码失败";
                break;
        }
        if(errMsg==null){
            ResponseMessage errMessage = checkParam(request,ignore);
            if(errMessage.hasError()){
                return json(BaseResponse.CODE_FAILURE,errMessage, response);
            }
        }else{
            return json(BaseResponse.CODE_FAILURE,errMsg, response);
        }

        boolean verifyFlag = _checkCode( verifyNameInput, request.code, accountType,bean.accountUuid);

        if (verifyFlag) {
            Account account;
            if(accountType==Account.TYPE_CUST_USER){
                account = accountDao.findByAccount(system.id,verifyNameInput, accountType, Account.ACCOUNT_FILED_PHONE);
            }else{
                account = accountDao.findByAccount(system.id,verifyNameInput, accountType, Account.ACCOUNT_FILED_EMAIL);
            }
            if(account==null){

                return json(BaseResponse.CODE_FAILURE,"手机号未注册", response);
            }
            String managerUuid = null;
            if(accountType==Account.TYPE_COMPANY){
                managerUuid = account.account_uuid;
            }
            Account updateRecord = new Account();
            String salt = StringKit.randStr(6);
            updateRecord.account_uuid=account.account_uuid;
            updateRecord.salt = salt;
            updateRecord.password = Account.generatePwd(request.password,salt);
            String token = getLoginToken(bean.accountUuid, accountType,managerUuid,system.id,salt);
            updateRecord.token = token;
            updateRecord.updated_at = TimeKit.currentTimeMillis();
            if(accountDao.updatePasswordTokenByUuid(updateRecord)>0){
                return json(BaseResponse.CODE_SUCCESS,"重设密码成功", response);
            }else{
                return json(BaseResponse.CODE_FAILURE,"重设密码失败", response);
            }
        }else{
            return json(BaseResponse.CODE_FAILURE,"请输入正确的验证码", response);
        }
    }

    public String sendCode(ActionBean bean,String requestAccountType){
        GetCodeRequest request = requst2Bean(bean.body, GetCodeRequest.class);
        GetCodeResponse response = new GetCodeResponse();
        ResponseMessage errMessage = checkParam(request);
        if(errMessage.hasError()){
            return json(BaseResponse.CODE_FAILURE,errMessage, response);
        }

        int accountType = Account.getAccountType(requestAccountType);

        PlatformSystem system = _getChannelSystem(bean.referer);
        if(system==null || system.status != PlatformSystem.STATUS_OK){
            return json(BaseResponse.CODE_FAILURE,"系统未上线，请联系管理员", response);
        }


        String method = request.method;
        boolean flag = false;
        String errMsg = null;
        AccountVerify accountVerify = null;
        int verifyType = 0;
        String verifyName = null;
        if("sms".equals(method)){
            flag = StringKit.isMobileNO(request.phone);
            if(flag){
                verifyName = request.phone;
                verifyType = AccountVerify.VERIFY_TYPE_PHONE;
            }else{
                errMsg = "请输入正确的手机号";
            }
        }else if("mail".equals(method)){
            flag = StringKit.isEmail(request.email);
            if(flag){
                verifyName = request.email;
                verifyType = AccountVerify.VERIFY_TYPE_EMAIL;
            }else{
                errMsg = "请输入正确的邮箱地址";
            }
        }

        if(flag){
            errMsg = _toSendCode(verifyName, accountType, verifyType, system.id);
            if(errMsg==null){
                flag = true;
            }
        }

        if(flag){
            if(ConstantKit.IS_PRODUCT){
                // TODO: 2018/3/28  send code
            }
            return json(BaseResponse.CODE_SUCCESS,"验证码发送成功", response);
        }else{
            return json(BaseResponse.CODE_SUCCESS,StringKit.isEmpty(errMsg)?"验证码发送失败":errMsg, response);
        }
    }



    public String modifyPassword(ActionBean bean){
        ModifyPasswordRequest request = requst2Bean(bean.body, ModifyPasswordRequest.class);
        ModifyPasswordResponse response = new ModifyPasswordResponse();
        ResponseMessage errMessage = checkParam(request);
        if(errMessage.hasError()){
            return json(BaseResponse.CODE_FAILURE,errMessage, response);
        }
        int accountType = bean.type;
        Account account = accountDao.findByUuid(bean.accountUuid);
        if(account!=null && account.password.equals(Account.generatePwd(request.oldpsword,account.salt))){

            String salt = StringKit.randStr(6);
            Account updateRecord = new Account();
            updateRecord.salt = salt;
            updateRecord.password = Account.generatePwd(request.password,salt);
            updateRecord.account_uuid = account.account_uuid;
            updateRecord.updated_at = TimeKit.currentTimeMillis();
            String token = getLoginToken(account.account_uuid, accountType,bean.managerUuid,bean.sysId,salt);
            updateRecord.token = token;

            if(accountDao.updatePasswordTokenByUuid(updateRecord)>0){
                response.data = new TokenData();
                response.data.token = token;
                return json(BaseResponse.CODE_SUCCESS,"修改密码成功", response);
            }else{
                return json(BaseResponse.CODE_FAILURE,"密码修改失败", response);
            }
        }else{
            return json(BaseResponse.CODE_FAILURE,"原密码错误", response);
        }
    }

    public String changePhone(ActionBean bean){
        ChangePhoneEmailRequest request = requst2Bean(bean.body,ChangePhoneEmailRequest.class);
        BaseResponse response = new BaseResponse();
        List<String> ignore = new ArrayList<>();
        ignore.add("email");
        ResponseMessage errMessage = checkParam(request,ignore);
        if(errMessage.hasError()){
            return json(BaseResponse.CODE_FAILURE,errMessage, response);
        }
        boolean verifyFlag = _checkCode( request.phone,request.code, bean.type, bean.accountUuid);

        if(verifyFlag){
            Account account = accountDao.findByAccount(bean.sysId,request.phone,bean.type,Account.ACCOUNT_FILED_PHONE);

            if(account!=null && !account.account_uuid.equals(bean.accountUuid)){
                return json(BaseResponse.CODE_FAILURE,"手机号已被占用", response);
            }

            Account updateRecord = new Account();
            updateRecord.phone = request.phone;
            updateRecord.updated_at=TimeKit.currentTimeMillis();
            updateRecord.account_uuid = bean.accountUuid;
            if(accountDao.updatePhoneByUuid(updateRecord)>0){
                return json(BaseResponse.CODE_SUCCESS,"更换手机号成功", response);
            }else{
                return json(BaseResponse.CODE_FAILURE,"更换手机号失败", response);
            }
        }else{
            return json(BaseResponse.CODE_FAILURE,"请输入正确的验证码", response);
        }
    }

    public String changeEmail(ActionBean bean){
        ChangePhoneEmailRequest request = requst2Bean(bean.body,ChangePhoneEmailRequest.class);
        BaseResponse response = new BaseResponse();
        List<String> ignore = new ArrayList<>();
        ignore.add("phone");
        ResponseMessage errMessage = checkParam(request,ignore);
        if(errMessage.hasError()){
            return json(BaseResponse.CODE_FAILURE,errMessage, response);
        }

        boolean verifyFlag = _checkCode( request.email,request.code, bean.type, bean.accountUuid);
        if(verifyFlag){
            Account account = accountDao.findByAccount(bean.sysId,request.email,bean.type,Account.ACCOUNT_FILED_EMAIL);

            if(account!=null && !account.account_uuid.equals(bean.accountUuid)){
                return json(BaseResponse.CODE_FAILURE,"邮箱地址已被占用", response);
            }
            Account updateRecord = new Account();
            updateRecord.email = request.email;
            updateRecord.updated_at=TimeKit.currentTimeMillis();
            updateRecord.account_uuid = bean.accountUuid;
            if(accountDao.updateEmailByUuid(updateRecord)>0){
                return json(BaseResponse.CODE_SUCCESS,"更换邮箱地址成功", response);
            }else{
                return json(BaseResponse.CODE_FAILURE,"更换邮箱地址失败", response);
            }
        }else{
            return json(BaseResponse.CODE_FAILURE,"请输入正确的验证码", response);
        }
    }

    public String loginOut(ActionBean bean){
        BaseResponse response = new BaseResponse();
        return json(BaseResponse.CODE_SUCCESS,"成功退出", response);
    }


    private String getLoginToken(String uuid,int accountType,String sysUuid,long sysId,String salt){
        ActionBean loginAction = new ActionBean();
        loginAction.accountUuid = uuid;
        loginAction.managerUuid = sysUuid;
        loginAction.tokenTime = TimeKit.currentTimeMillis();
        loginAction.salt = ActionBean.getSalt(salt);
        loginAction.sysId = sysId;
        loginAction.type = accountType;
        return ActionBean.packageToken(loginAction);
    }


    private String _toSendCode(String verifyName,int accountType,int verifyType,long sysId){

        AccountVerify accountVerify = accountVerifyDao.findLatestByFromVerify(verifyName,accountType);;

        long currentTime = TimeKit.currentTimeMillis();
        String code = null;
        boolean flag = false;
        boolean needInsert = false;
        String errMsg = null;
        if(accountVerify !=null && accountVerify.status==AccountVerify.STATUS_NOT_USE && accountVerify.code_time>currentTime){
            //存在一条未验证的短信
            if(accountVerify.updated_at+ CODE_LIMIT_NO_RESEND_TIME > currentTime){
                //发送间隔时间 不能小于60s
                flag = false;
                int extraTime = (int)(accountVerify.updated_at+ CODE_LIMIT_NO_RESEND_TIME - currentTime)/1000;
                errMsg = extraTime+"秒后重试";
            }else if(accountVerify.created_at+ CODE_UNIQUE_TIME >currentTime){

                AccountVerify updateVerify = new AccountVerify();
                updateVerify.id = accountVerify.id;
                updateVerify.updated_at = currentTime;
                updateVerify.code = accountVerify.code;
                updateVerify.code_time = currentTime +CODE_VALID_TIME;
                code = accountVerify.code;
                flag = accountVerifyDao.updateCodeTime(updateVerify)>0;
            }else{
                AccountVerify updateVerify = new AccountVerify();
                updateVerify.id = accountVerify.id;
                updateVerify.updated_at = currentTime;
                updateVerify.code = accountVerify.code;
                updateVerify.code_time = currentTime;
                accountVerifyDao.updateCodeTime(updateVerify);
                needInsert = true;
            }
        }else{
            needInsert = true;
        }

        if(needInsert){
            if(ConstantKit.IS_PRODUCT){
                code = StringKit.randNum(6);
            }else{
                code = "666666";
            }
            AccountVerify addRecord = new AccountVerify();
            addRecord.sys_id = sysId;
            addRecord.from_type=accountType;
            addRecord.verify_name = verifyName;
            addRecord.verify_type = verifyType;
            addRecord.code = code;
            addRecord.code_time = currentTime+CODE_VALID_TIME;
            addRecord.status = AccountVerify.STATUS_NOT_USE;
            addRecord.created_at = currentTime;
            addRecord.updated_at = currentTime;
            flag = accountVerifyDao.insert(addRecord)>0;
        }
        if(flag){
            return errMsg;
        }else{
            return null;
        }

    }

    private boolean _checkCode(String verifyName,String code,int accountType,String accountUuid){
        boolean verifyFlag = false;
        if(!StringKit.isEmpty(verifyName)) {
            long currentTime = TimeKit.currentTimeMillis();
            AccountVerify verify = accountVerifyDao.findLatestByFromVerify(verifyName, accountType);

            if (verify != null && verify.status == AccountVerify.STATUS_NOT_USE && currentTime < verify.code_time) {
                verifyFlag = verify.code.equals(code);
                if(verifyFlag){
                    AccountVerify updateUsed = new AccountVerify();
                    updateUsed.updated_at = currentTime;
                    updateUsed.status = AccountVerify.STATUS_USED;
                    updateUsed.id= verify.id;
                    accountVerifyDao.updateStatus(updateUsed);
                }
            }

        }
        return verifyFlag;
    }

    private PlatformSystem _getChannelSystem(String referer){
        if(ConstantKit.IS_PRODUCT){
            String[] domains = StringKit.parseDomain(referer);
            if(domains!=null){
                String domain = domains[0];
                return  platformSystemDao.findDomain(domain);
            }
        }else{
            PlatformSystem system = new PlatformSystem();
            system.id = 1;
            system.status = PlatformSystem.STATUS_OK;
            return system;
        }


        return null;
    }



}
