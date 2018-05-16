package com.inschos.cloud.account.access.http.controller.action;

import com.inschos.cloud.account.access.http.controller.bean.AccountBean.*;
import com.inschos.cloud.account.access.http.controller.bean.ActionBean;
import com.inschos.cloud.account.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.account.access.http.controller.bean.ResponseMessage;
import com.inschos.cloud.account.access.rpc.bean.AgentJobBean;
import com.inschos.cloud.account.access.rpc.bean.CompanyBean;
import com.inschos.cloud.account.access.rpc.bean.CustomerBean;
import com.inschos.cloud.account.access.rpc.bean.PersonBean;
import com.inschos.cloud.account.access.rpc.client.*;
import com.inschos.cloud.account.assist.kit.*;
import com.inschos.cloud.account.data.dao.AccountDao;
import com.inschos.cloud.account.data.dao.AccountVerifyDao;
import com.inschos.cloud.account.data.dao.PlatformSystemDao;
import com.inschos.cloud.account.extend.worker.AccountUuidWorker;
import com.inschos.cloud.account.model.Account;
import com.inschos.cloud.account.model.AccountVerify;
import com.inschos.cloud.account.model.Common;
import com.inschos.cloud.account.model.PlatformSystem;
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
    @Autowired
    private CompanyClient companyClient;
    @Autowired
    private PersonClient personClient;
    @Autowired
    private CustomerClient customerClient;
    @Autowired
    private AgentJobClient agentJobClient;
    @Autowired
    private SmsHandingClient smsHandingClient;

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
            case Account.TYPE_AGENT:
                if(account==null){
                    account = accountDao.findByAccount(system.id,request.username, accountType, Account.ACCOUNT_FILED_PHONE);
                }
                break;

        }
        if(account!=null && account.password.equals(Account.generatePwd(request.password,account.salt))){



            if(account.status==Account.STATUS_NORMAL){

                TokenData tokenData = new TokenData();
                String managerUuid = null;
                switch (accountType){
                    case Account.TYPE_CUST_USER:
                    case Account.TYPE_CUST_COM:
                        Account searchManager = new Account();
                        searchManager.sys_id =system.id;
                        searchManager.user_type = Account.TYPE_COMPANY;
                        // TODO: 2018/4/27  客户表  查询managerUuid
                        Account accountManager = accountDao.findOneBySysType(searchManager);
                        if(accountManager!=null){
                            managerUuid = accountManager.account_uuid;
                        }
                        break;
                    case Account.TYPE_COMPANY:
                        managerUuid = account.account_uuid;
                        break;
                    case Account.TYPE_AGENT:


                        List<Account> list = getExistsManagerUuid(account.user_id, system.id);
                        if(list.size()==1){
                            managerUuid = list.get(0).account_uuid;
                        }else if(list.size()>1){
                            tokenData.needManager = 1;
                        }else{
                            tokenData.needManager = 1;
                        }
                        break;
                }

                String token = null;
                long timeMillis = TimeKit.currentTimeMillis();
                account.token = getLoginToken(account.account_uuid,accountType,managerUuid,system.id,account.salt);
                account.updated_at = timeMillis;
                if(accountDao.updateTokenByUuid(account)>0){
                    token = account.token;
                }

                if(!StringKit.isEmpty(token)){
                    response.data = tokenData;
                    response.data.token = token;
                    return json(BaseResponse.CODE_SUCCESS,"登录成功", response);
                }else{
                    return json(BaseResponse.CODE_FAILURE,"请输入正确的登录密码", response);
                }
            }else{
                return json(BaseResponse.CODE_FAILURE,"账号异常，请联系管理员", response);
            }
        }else{
            return json(BaseResponse.CODE_FAILURE,"请输入正确的登录密码", response);
        }
    }

    public String registry(ActionBean bean,String requestAccountType){
        RegistryRequest request = requst2Bean(bean.body, RegistryRequest.class);
        RegistryResponse response = new RegistryResponse();

        ResponseMessage errMessage = checkParam(request);
        if(errMessage.hasError()){
            return json(BaseResponse.CODE_FAILURE,errMessage, response);
        }

        int accountType = Account.getAccountType(requestAccountType);

        PlatformSystem system = _getChannelSystem(bean.referer);
        if(system==null || system.status != PlatformSystem.STATUS_OK){
            return json(BaseResponse.CODE_FAILURE,"系统未上线，请联系管理员", response);
        }

        Account searchAManager = new Account();
        searchAManager.sys_id = system.id;
        searchAManager.user_type = Account.TYPE_COMPANY;
        Account accountManager = accountDao.findOneBySysType(searchAManager);

        if(accountManager==null){
            return json(BaseResponse.CODE_FAILURE,"系统未上线，请联系管理员", response);
        }

        CheckToken checkToken = parseCheckToken(request.verifyToken);

        if(checkToken!=null){

            String password = request.password.trim();
            String salt = StringKit.randStr(6);
            boolean accountNameExsitFlag = false;
            String errMsg = null;

            Account account = accountDao.findByAccount(system.id,request.username, accountType, Account.ACCOUNT_FILED_USERNAME);

            if(account!=null){
                errMsg = "用户名已存在";
                accountNameExsitFlag = true;
            }
            String phone = null;
            String email = null;
            if(!accountNameExsitFlag){
                switch (checkToken.method){
                    case "sms":
                        account = accountDao.findByAccount(system.id,checkToken.verifyName, accountType, Account.ACCOUNT_FILED_PHONE);
                        if(account!=null){
                            errMsg = "手机号已注册";
                            accountNameExsitFlag = true;
                        }
                        phone = checkToken.verifyName;
                        break;
                    case "mail":
                        account = accountDao.findByAccount(system.id,checkToken.verifyName, accountType, Account.ACCOUNT_FILED_EMAIL);
                        if(account!=null){
                            errMsg = "邮箱地址已注册";
                            accountNameExsitFlag = true;
                        }
                        email = checkToken.verifyName;
                        break;
                }
            }

            int resultAdd = 0;
            int custType = 0;
            String accountUuid = String.valueOf(AccountUuidWorker.getWorker(1, 1).nextId());
            if(!accountNameExsitFlag){
                String userId = null;
                switch (accountType){
                    case Account.TYPE_CUST_USER: {
                        PersonBean personBean = new PersonBean();
                        personBean.phone = phone;
                        personBean.cust_type = PersonBean.CUST_TYPE_USER;
                        int resultId = personClient.saveInfo(personBean);
                        if (resultId > 0) {
                            userId = String.valueOf(resultId);
                        }
                        custType = CustomerBean.TYPE_CUST_USER;
                        break;
                    }
                    case Account.TYPE_CUST_COM: {
                        CompanyBean companyBean = new CompanyBean();
                        companyBean.email = email;
                        long resultId = companyClient.addCompany(companyBean,accountUuid);
                        if (resultId > 0) {
                            userId = String.valueOf(resultId);
                        }
                        custType = CustomerBean.TYPE_CUST_COM;
                        break;
                    }
                    case Account.TYPE_AGENT:
                    {
                        PersonBean personBean = new PersonBean();
                        personBean.phone = phone;
                        personBean.cust_type = PersonBean.CUST_TYPE_AGENT;

                        long resultId = 0;

                        Account searchListSysType = new Account();
                        searchListSysType.user_type = Account.TYPE_COMPANY;
                        searchAManager.sys_id = system.id;
                        List<Account> accountList = accountDao.findListBySysType(searchListSysType);
                        AgentJobBean agentPerson = null;
                        if(accountList!=null && !accountList.isEmpty()){
                            List<String> list = ListKit.toColumnList(accountList, v -> v.account_uuid);
                            agentPerson = agentJobClient.getAgentPersonId(phone, list);
                        }
                        if(agentPerson==null){
                            resultId = personClient.saveInfo(personBean);
                        }else{
                            resultId = agentPerson.person_id;
                        }

                        if(resultId>0){
                            userId = String.valueOf(resultId);
                        }
                        break;
                    }

                }
                if(userId!=null){
                    Account addRecord = new Account();
                    // uuID
                    addRecord.account_uuid = accountUuid;
                    addRecord.status = Account.STATUS_NORMAL;
                    addRecord.password = Account.generatePwd(password,salt);
                    addRecord.username = request.username;
                    addRecord.phone = phone;
                    addRecord.email = email;
                    addRecord.user_type = accountType;
                    addRecord.user_id = userId;
                    addRecord.salt = salt;
                    addRecord.state = Common.STATE_ONLINE;
                    addRecord.created_at = addRecord.updated_at = TimeKit.currentTimeMillis();
                    addRecord.sys_id = system.id;
                    resultAdd = accountDao.registry(addRecord);

                    if(resultAdd>0){
                        if(custType==1 || custType==2){

                            CustomerBean customerBean = new CustomerBean();
                            customerBean.type=custType;
                            customerBean.customerId = Integer.valueOf(userId);
                            customerBean.accountUuid = addRecord.account_uuid;
                            customerBean.managerUuid = accountManager.account_uuid;
                            customerClient.saveCust(customerBean);
                        }
                    }
                }

            }

            if(resultAdd>0){
                response.data = new RegistrySuccessData();
                response.data.usernameTxt = request.username;
                response.data.passwordTxt = request.password;
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

        ResponseMessage errMessage = checkParam(request);
        if(errMessage.hasError()){
            return json(BaseResponse.CODE_FAILURE,errMessage, response);
        }

        int accountType = bean.type;

        CheckToken checkToken = parseCheckToken(request.verifyToken);

        if (checkToken!=null) {
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

        ResponseMessage errMessage = checkParam(request);
        if(errMessage.hasError()){
            return json(BaseResponse.CODE_FAILURE,errMessage, response);
        }

        int accountType = Account.getAccountType(requestAccountType);

        PlatformSystem system = _getChannelSystem(bean.referer);
        if(system==null || system.status != PlatformSystem.STATUS_OK){
            return json(BaseResponse.CODE_FAILURE,"系统未上线，请联系管理员", response);
        }
//        Account searchSystem = new Account();
//        searchSystem.sys_id = system.id;
//        searchSystem.user_type = Account.TYPE_COMPANY;
//        Account accountSystem = accountDao.findOneBySysType(searchSystem);
//        if(accountSystem==null){
//            return json(BaseResponse.CODE_FAILURE,"系统未上线，请联系管理员", response);
//        }


        CheckToken checkToken = parseCheckToken(request.verifyToken);

        if (checkToken!=null) {
            Account account;
            if("sms".equals(checkToken.method)){
                account = accountDao.findByAccount(system.id,checkToken.verifyName, accountType, Account.ACCOUNT_FILED_PHONE);
            }else{
                account = accountDao.findByAccount(system.id,checkToken.verifyName, accountType, Account.ACCOUNT_FILED_EMAIL);
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
            return json(BaseResponse.CODE_SUCCESS,"验证码已发送", response);
        }else{
            return json(BaseResponse.CODE_FAILURE,StringKit.isEmpty(errMsg)?"验证码发送失败":errMsg, response);
        }
    }


    public String checkSendCode(ActionBean bean,String requestAccountType){

        CheckCodeRequest request = requst2Bean(bean.body,CheckCodeRequest.class);
        CheckCodeResponse response = new CheckCodeResponse();

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
            if(_checkCode(verifyName, request.code,accountType,  system.id)){
                String checkToken = generateCheckToken(verifyName, request.method);
                response.data = new VerifyTokenData();
                response.data.verifyToken = checkToken;
                return json(BaseResponse.CODE_SUCCESS,"验证码发送成功", response);
            }
        }
        return json(BaseResponse.CODE_FAILURE,StringKit.isEmpty(errMsg)?"请输入正确的验证码":errMsg, response);
    }

    public String listManager(ActionBean bean){
        ListManagerRequest request = requst2Bean(bean.body,ListManagerRequest.class);
        ListManagerResponse response = new ListManagerResponse();

        ResponseMessage errMessage = checkParam(request);
        if(errMessage.hasError()){
            return json(BaseResponse.CODE_FAILURE,errMessage, response);
        }

        List<ManagerData> list = new ArrayList<>();

        Account account = accountDao.findByUuid(bean.accountUuid);
        if(account!=null){

            List<Account> listManager = getExistsManagerUuid(account.user_id, bean.sysId);
            for (Account at : listManager) {
                CompanyBean companyBean = companyClient.getCompanyById(Long.valueOf(at.user_id));
                if(companyBean!=null){
                    ManagerData managerData = new ManagerData();
                    managerData.managerName = companyBean.name;
                    managerData.managerUuid = at.account_uuid;
                    list.add(managerData);
                }
            }
        }
        if(list.isEmpty()){
            return json(BaseResponse.CODE_FAILURE,"很抱歉，您的账号尚未生效，请联系业管处理", response);
        }else{
            response.data = list;
            return json(BaseResponse.CODE_SUCCESS,"获取成功", response);
        }
    }

    public String chooseManager(ActionBean bean){
        ChooseManagerRequest request = requst2Bean(bean.body,ChooseManagerRequest.class);
        ChooseManagerResponse response = new ChooseManagerResponse();

        ResponseMessage errMessage = checkParam(request);
        if(errMessage.hasError()){
            return json(BaseResponse.CODE_FAILURE,errMessage, response);
        }
        boolean isVaild = false;
        Account accountU = accountDao.findByUuid(bean.accountUuid);
        if(accountU!=null){
            List<Account> listManager = getExistsManagerUuid(accountU.user_id, bean.sysId);
            List<String> columnList = ListKit.toColumnList(listManager, v -> v.account_uuid);
            isVaild =columnList.indexOf(request.managerUuid)>-1;
        }

        if(isVaild){
            bean.managerUuid = request.managerUuid;
            String token = null;
            Account account = new Account();
            long timeMillis = TimeKit.currentTimeMillis();
            account.token = getLoginToken(bean.accountUuid,bean.type,bean.managerUuid,bean.sysId,bean.salt);
            account.updated_at = timeMillis;
            account.account_uuid = bean.accountUuid;
            if(accountDao.updateTokenByUuid(account)>0){
                token = account.token;
            }
            if(!StringKit.isEmpty(token)){
                response.data = new TokenData();
                response.data.token = token;
                return json(BaseResponse.CODE_SUCCESS,"企业切换成功", response);
            }else{
                return json(BaseResponse.CODE_FAILURE,"企业切换失败", response);
            }
        }else{
            return json(BaseResponse.CODE_FAILURE,"企业切换失败", response);
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
        ResponseMessage errMessage = checkParam(request);
        if(errMessage.hasError()){
            return json(BaseResponse.CODE_FAILURE,errMessage, response);
        }
        CheckToken checkToken = parseCheckToken(request.verifyToken);

        if(checkToken!=null){

            Account account = accountDao.findByAccount(bean.sysId,checkToken.verifyName,bean.type,Account.ACCOUNT_FILED_PHONE);

            if(account!=null && !account.account_uuid.equals(bean.accountUuid)){
                return json(BaseResponse.CODE_FAILURE,"手机号已被占用", response);
            }

            Account updateRecord = new Account();
            updateRecord.phone = checkToken.verifyName;
            updateRecord.updated_at=TimeKit.currentTimeMillis();
            updateRecord.account_uuid = bean.accountUuid;
            if(accountDao.updatePhoneByUuid(updateRecord)>0){
                // TODO: 2018/4/20 change 联系人
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

        ResponseMessage errMessage = checkParam(request);
        if(errMessage.hasError()){
            return json(BaseResponse.CODE_FAILURE,errMessage, response);
        }

        CheckToken checkToken = parseCheckToken(request.verifyToken);
        if(checkToken!=null){
            Account account = accountDao.findByAccount(bean.sysId,checkToken.verifyName,bean.type,Account.ACCOUNT_FILED_EMAIL);

            if(account!=null && !account.account_uuid.equals(bean.accountUuid)){
                return json(BaseResponse.CODE_FAILURE,"邮箱地址已被占用", response);
            }
            Account updateRecord = new Account();
            updateRecord.email = checkToken.verifyName;
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

        AccountVerify accountVerify = accountVerifyDao.findLatestByFromVerify(verifyName,accountType,sysId);

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
//            if(ConstantKit.IS_PRODUCT){
//                code = StringKit.randNum(6);
//            }else{
//                code = "666666";
//            }
            code = StringKit.randNum(6);
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
            errMsg = null;
            if(AccountVerify.VERIFY_TYPE_PHONE==verifyType){
                if(!smsHandingClient.sendVerifyCode(verifyName,code)){
                    errMsg = "发送失败";
                }
            }
        }
        return errMsg;

    }

    private boolean _checkCode(String verifyName,String code,int accountType,long sysId){
        boolean verifyFlag = false;
        if(!StringKit.isEmpty(verifyName)) {
            long currentTime = TimeKit.currentTimeMillis();
            AccountVerify verify = accountVerifyDao.findLatestByFromVerify(verifyName, accountType,sysId);

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


    private String generateCheckToken(String verifyName,String method){
        CheckToken checkToken = new CheckToken();
        checkToken.method = method;
        checkToken.verifyName = verifyName;
        checkToken.time = TimeKit.currentTimeMillis();
        return new RC4Kit("checkToken-inshcos").encry_RC4_hex(JsonKit.bean2Json(checkToken));
    }

    private CheckToken parseCheckToken(String checkToken){
        CheckToken bean = JsonKit.json2Bean(new RC4Kit("checkToken-inshcos").decry_RC4_hex(checkToken),CheckToken.class);
        if(bean==null || StringKit.isEmpty(bean.verifyName) || StringKit.isEmpty(bean.method)){
            return null;
        }
        return bean;
    }

    private static class CheckToken{

        public long time;

        public String verifyName;

        public String method;

    }

    private List<Account> getExistsManagerUuid(String userId,long sysId){
        List<AgentJobBean> agents = agentJobClient.getAgents(Long.valueOf(userId));
        List<Account> exists = new ArrayList<>();
        if(agents!=null){
            Account searchManagers = new Account();
            searchManagers.sys_id = sysId;
            searchManagers.user_type = Account.TYPE_COMPANY;
            List<Account> accounts = accountDao.findListBySysType(searchManagers);
            List<String> columnList = ListKit.toColumnList(agents, v -> v.manager_uuid);

            for (Account account : accounts) {
                if(columnList.indexOf(account.account_uuid)>-1){
                    exists.add(account);
                }
            }
        }
        return exists;
    }






}
