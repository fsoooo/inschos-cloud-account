package com.inschos.cloud.account.access.http.controller.action;

import com.inschos.cloud.account.access.http.controller.bean.AccountBean.*;
import com.inschos.cloud.account.access.http.controller.bean.ActionBean;
import com.inschos.cloud.account.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.account.access.http.controller.bean.ResponseMessage;
import com.inschos.cloud.account.access.rpc.bean.*;
import com.inschos.cloud.account.access.rpc.client.*;
import com.inschos.cloud.account.assist.kit.*;
import com.inschos.cloud.account.data.dao.AccountDao;
import com.inschos.cloud.account.data.dao.AccountVerifyDao;
import com.inschos.cloud.account.data.dao.PlatformSystemDao;
import com.inschos.cloud.account.extend.worker.AccountUuidWorker;
import com.inschos.cloud.account.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private ChannelServiceClient channelServiceClient;
    @Autowired
    private PersonClient personClient;
    @Autowired
    private CustomerClient customerClient;
    @Autowired
    private AgentJobClient agentJobClient;
    @Autowired
    private SmsHandingClient smsHandingClient;
    @Autowired
    private EmailSendClient emailSendClient;
    @Autowired
    private FileClient fileClient;

    private final long CODE_VALID_TIME = 10 * 60 * 1000L;

    private final long CODE_UNIQUE_TIME = 5 * 60 * 1000L;

    private final long CODE_LIMIT_NO_RESEND_TIME = 60 * 1000L;

    public String login(ActionBean bean, String requestAccountType) {
        LoginRequest request = requst2Bean(bean.body, LoginRequest.class);
        LoginResponse response = new LoginResponse();
        ResponseMessage errMessage = checkParam(request);
        if (errMessage.hasError()) {
            return json(BaseResponse.CODE_FAILURE, errMessage, response);
        }
        int accountType = Account.getAccountType(requestAccountType);

        PlatformSystem system = _getChannelSystem(bean.domain);
        if (system == null || system.status != PlatformSystem.STATUS_OK) {
            return json(BaseResponse.CODE_FAILURE, "系统未上线，请联系管理员", response);
        }

        Account account = accountDao.findByAccount(system.id, request.username, accountType, Account.getAccountFiled(request.method));

        if (account != null && account.password.equals(Account.generatePwd(request.password, account.salt))) {


            boolean needManage = false;
            boolean isBind = false;
            Account accountManager = null;
            if (account.status == Account.STATUS_NORMAL) {

                TokenData tokenData = new TokenData();
                String managerUuid = null;
                switch (accountType) {
                    case Account.TYPE_CUST_USER:
                    case Account.TYPE_CUST_COM:
                        Account searchManager = new Account();
                        searchManager.sys_id = system.id;
                        searchManager.user_type = Account.TYPE_COMPANY;
                        // TODO: 2018/4/27  客户表  查询managerUuid
                        accountManager = accountDao.findOneBySysType(searchManager);
                        if (accountManager != null) {
                            managerUuid = accountManager.account_uuid;
                        }
                        isBind = true;
                        break;
                    case Account.TYPE_COMPANY:
                        isBind = true;
                        managerUuid = account.account_uuid;
                        break;
                    case Account.TYPE_AGENT:

                        AccountDefault accountDefault = accountDao.findAccountDefault(account.account_uuid);
                        if (accountDefault != null) {
                            if (isValidAgent(accountDefault.manager_uuid, account.phone, account.user_id) > 0) {
                                isBind = true;
                                managerUuid = accountDefault.manager_uuid;
                                accountManager = accountDao.findByUuid(managerUuid);
                            }
                        }
                        //未绑定
                        if (!isBind) {

                            List<Account> list = getExistsManagerUuid(account.phone, system.id);
                            if (list.size() == 1) {
                                managerUuid = list.get(0).account_uuid;
                                accountManager = list.get(0);
//                        }else if(list.size()>1){
//                            tokenData.needManager = 1;
                            } else {
                                needManage = true;
                            }
                        }
                        break;
                }

                String token = null;
                long timeMillis = TimeKit.currentTimeMillis();
                account.token = getLoginToken(account.account_uuid, accountType, managerUuid, system.id, account.salt);
                account.updated_at = timeMillis;
                if (accountDao.updateTokenByUuid(account) > 0) {
                    token = account.token;
                    if (!isBind) {

//                        if(accountManager!=null){
//                            CompanyBean companyBean = companyClient.getCompanyById(Long.valueOf(accountManager.user_id));
//                            if(companyBean!=null){
//                                tokenData.compLogo = fileClient.getFileUrl(companyBean.head,100,100,80);
//                                tokenData.compName = companyBean.name;
//                            }
//
//                        }

                        if (!needManage) {
                            int bindFlag = bindAgent(account.account_uuid, account.phone, managerUuid, account.user_id);
                            needManage = bindFlag == 0;
                        }
                    }
                }

                if (!StringKit.isEmpty(token)) {
                    response.data = tokenData;
                    response.data.token = token;
                    if (needManage) {
                        return json(BaseResponse.CODE_NEED_CHOOSE_MANAGER, "请选择业管", response);
                    } else {
                        return json(BaseResponse.CODE_SUCCESS, "登录成功", response);
                    }
                } else {
                    return json(BaseResponse.CODE_FAILURE, "请输入正确的用户名或密码", response);
                }
            } else {
                return json(BaseResponse.CODE_FAILURE, "账号异常，请联系管理员", response);
            }
        } else {
            return json(BaseResponse.CODE_FAILURE, "请输入正确的用户名或密码", response);
        }
    }

    public String registry(ActionBean bean, String requestAccountType) {
        RegistryRequest request = requst2Bean(bean.body, RegistryRequest.class);
        RegistryResponse response = new RegistryResponse();

        ResponseMessage errMessage = checkParam(request);
        if (errMessage.hasError()) {
            return json(BaseResponse.CODE_FAILURE, errMessage, response);
        }

        int accountType = Account.getAccountType(requestAccountType);

        PlatformSystem system = _getChannelSystem(bean.domain);
        if (system == null || system.status != PlatformSystem.STATUS_OK) {
            return json(BaseResponse.CODE_FAILURE, "系统未上线，请联系管理员", response);
        }

        Account searchAManager = new Account();
        searchAManager.sys_id = system.id;
        searchAManager.user_type = Account.TYPE_COMPANY;
        Account accountManager = accountDao.findOneBySysType(searchAManager);

        if (accountManager == null) {
            return json(BaseResponse.CODE_FAILURE, "系统未上线，请联系管理员", response);
        }

        CheckToken checkToken = parseCheckToken(request.verifyToken);
        int accountFiled = Account.getAccountFiled(request.method);
        boolean checkOk = false;
        switch (accountFiled) {
            case Account.ACCOUNT_FILED_USERNAME:
                checkOk = accountType==Account.TYPE_CUST_COM;
                break;
            case Account.ACCOUNT_FILED_EMAIL:
                checkOk = checkToken != null && "mail".equals(checkToken.method) && request.username.equals(checkToken.verifyName);
                break;
            case Account.ACCOUNT_FILED_PHONE:
                checkOk = checkToken != null && "sms".equals(checkToken.method) && request.username.equals(checkToken.verifyName);
                break;
        }

        if (checkOk) {

            String password = request.password;
            String salt = StringKit.randStr(6);
            boolean accountNameExsitFlag = false;
            String errMsg = null;

            Account account = accountDao.findByAccount(system.id, request.username, accountType, accountFiled);

            if (account != null) {
                errMsg = "用户已存在";
                accountNameExsitFlag = true;
            }


            int resultAdd = 0;
            int custType = 0;
            String accountUuid = String.valueOf(AccountUuidWorker.getWorker(1, 1).nextId());
            if (!accountNameExsitFlag) {
                String userId = null;
                switch (accountType) {
                    case Account.TYPE_CUST_USER: {
                        PersonBean personBean = new PersonBean();
                        personBean.phone = request.username;
                        int resultId = personClient.saveInfo(personBean);
                        if (resultId > 0) {
                            userId = String.valueOf(resultId);
                        }
                        custType = CustomerBean.TYPE_CUST_USER;
                        break;
                    }
                    case Account.TYPE_CUST_COM: {
                        CompanyBean companyBean = new CompanyBean();
                        if(accountFiled==Account.ACCOUNT_FILED_EMAIL){
                            companyBean.email = request.username;
                        }
                        long resultId = companyClient.addCompany(companyBean, accountUuid);
                        if (resultId > 0) {
                            userId = String.valueOf(resultId);
                            channelServiceClient.addSystemDefaultChannel(accountUuid);

                        }
                        custType = CustomerBean.TYPE_CUST_COM;

                        break;
                    }
                    case Account.TYPE_AGENT: {
                        PersonBean personBean = new PersonBean();
                        personBean.phone = request.username;

                        long resultId = 0;

                        resultId = personClient.saveInfo(personBean);

                        if (resultId > 0) {
                            userId = String.valueOf(resultId);
                        }
                        break;
                    }

                }
                if (userId != null) {
                    Account addRecord = new Account();
                    // uuID
                    addRecord.account_uuid = accountUuid;
                    addRecord.status = Account.STATUS_NORMAL;
                    addRecord.password = Account.generatePwd(password, salt);
                    switch (accountFiled) {
                        case Account.ACCOUNT_FILED_USERNAME:
                            addRecord.username = request.username;
                            break;
                        case Account.ACCOUNT_FILED_EMAIL:
                            addRecord.email = request.username;
                            break;
                        case Account.ACCOUNT_FILED_PHONE:
                            addRecord.phone = request.username;
                            break;
                    }
                    addRecord.user_type = accountType;
                    addRecord.user_id = userId;
                    addRecord.salt = salt;
                    addRecord.state = Common.STATE_ONLINE;
                    addRecord.created_at = addRecord.updated_at = TimeKit.currentTimeMillis();
                    addRecord.sys_id = system.id;
                    addRecord.origin = "REGISTER";
                    resultAdd = accountDao.registry(addRecord);

                    if (resultAdd > 0) {
                        if (custType == 1 || custType == 2) {

                            CustomerBean customerBean = new CustomerBean();
                            customerBean.type = custType;
                            customerBean.customerId = Integer.valueOf(userId);
                            customerBean.accountUuid = addRecord.account_uuid;
                            customerBean.managerUuid = accountManager.account_uuid;
                            customerClient.saveCust(customerBean);
                        }
                    }
                }
            }

            if (resultAdd > 0) {
                response.data = new RegistrySuccessData();
                response.data.usernameTxt = request.username;
                response.data.passwordTxt = request.password;
                return json(BaseResponse.CODE_SUCCESS, "注册成功", response);
            } else {
                return json(BaseResponse.CODE_FAILURE, StringKit.isEmpty(errMsg) ? "注册失败" : errMsg, response);
            }

        } else {
            return json(BaseResponse.CODE_FAILURE, "请输入正确的验证码", response);
        }

    }

    public String resetPassword(ActionBean bean) {
        ResetPasswordRequest request = requst2Bean(bean.body, ResetPasswordRequest.class);
        ResetPasswordResponse response = new ResetPasswordResponse();

        ResponseMessage errMessage = checkParam(request);
        if (errMessage.hasError()) {
            return json(BaseResponse.CODE_FAILURE, errMessage, response);
        }

        int accountType = bean.type;

        CheckToken checkToken = parseCheckToken(request.verifyToken);

        if (checkToken != null) {
            Account updateRecord = new Account();
            String salt = StringKit.randStr(6);
            updateRecord.account_uuid = bean.accountUuid;
            updateRecord.salt = salt;
            updateRecord.password = Account.generatePwd(request.password, salt);
            String token = getLoginToken(bean.accountUuid, accountType, bean.managerUuid, bean.sysId, salt);
            updateRecord.token = token;
            updateRecord.updated_at = TimeKit.currentTimeMillis();
            if (accountDao.updatePasswordTokenByUuid(updateRecord) > 0) {
                response.data = new TokenData();
                response.data.token = token;
                return json(BaseResponse.CODE_SUCCESS, "重设密码成功", response);
            } else {
                return json(BaseResponse.CODE_FAILURE, "重设密码失败", response);
            }
        } else {
            return json(BaseResponse.CODE_FAILURE, "请输入正确的验证码", response);
        }
    }

    public String forgetPassword(ActionBean bean, String requestAccountType) {
        ForgetPasswordRequest request = requst2Bean(bean.body, ForgetPasswordRequest.class);
        BaseResponse response = new ResetPasswordResponse();

        ResponseMessage errMessage = checkParam(request);
        if (errMessage.hasError()) {
            return json(BaseResponse.CODE_FAILURE, errMessage, response);
        }

        int accountType = Account.getAccountType(requestAccountType);

        PlatformSystem system = _getChannelSystem(bean.domain);
        if (system == null || system.status != PlatformSystem.STATUS_OK) {
            return json(BaseResponse.CODE_FAILURE, "系统未上线，请联系管理员", response);
        }
//        Account searchSystem = new Account();
//        searchSystem.sys_id = system.id;
//        searchSystem.user_type = Account.TYPE_COMPANY;
//        Account accountSystem = accountDao.findOneBySysType(searchSystem);
//        if(accountSystem==null){
//            return json(BaseResponse.CODE_FAILURE,"系统未上线，请联系管理员", response);
//        }


        CheckToken checkToken = parseCheckToken(request.verifyToken);

        if (checkToken != null) {
            Account account;
            if ("sms".equals(checkToken.method)) {
                account = accountDao.findByAccount(system.id, checkToken.verifyName, accountType, Account.ACCOUNT_FILED_PHONE);
            } else {
                account = accountDao.findByAccount(system.id, checkToken.verifyName, accountType, Account.ACCOUNT_FILED_EMAIL);
            }
            if (account == null) {

                return json(BaseResponse.CODE_FAILURE, "手机号未注册", response);
            }
            String managerUuid = null;
            if (accountType == Account.TYPE_COMPANY) {
                managerUuid = account.account_uuid;
            }
            Account updateRecord = new Account();
            String salt = StringKit.randStr(6);
            updateRecord.account_uuid = account.account_uuid;
            updateRecord.salt = salt;
            updateRecord.password = Account.generatePwd(request.password, salt);
            String token = getLoginToken(bean.accountUuid, accountType, managerUuid, system.id, salt);
            updateRecord.token = token;
            updateRecord.updated_at = TimeKit.currentTimeMillis();
            if (accountDao.updatePasswordTokenByUuid(updateRecord) > 0) {
                return json(BaseResponse.CODE_SUCCESS, "重设密码成功", response);
            } else {
                return json(BaseResponse.CODE_FAILURE, "重设密码失败", response);
            }
        } else {
            return json(BaseResponse.CODE_FAILURE, "请输入正确的验证码", response);
        }
    }

    public String sendCode(ActionBean bean, String requestAccountType) {
        GetCodeRequest request = requst2Bean(bean.body, GetCodeRequest.class);
        GetCodeResponse response = new GetCodeResponse();
        ResponseMessage errMessage = checkParam(request);
        if (errMessage.hasError()) {
            return json(BaseResponse.CODE_FAILURE, errMessage, response);
        }

        int accountType = Account.getAccountType(requestAccountType);

        PlatformSystem system = _getChannelSystem(bean.domain);
        if (system == null || system.status != PlatformSystem.STATUS_OK) {
            return json(BaseResponse.CODE_FAILURE, "系统未上线，请联系管理员", response);
        }


        String method = request.method;
        boolean flag = false;
        String errMsg = null;
        int verifyType = 0;
        String verifyName = null;
        if ("sms".equals(method)) {
            flag = StringKit.isMobileNO(request.phone);
            if (flag) {
                verifyName = request.phone;
                verifyType = AccountVerify.VERIFY_TYPE_PHONE;
            } else {
                errMsg = "请输入正确的手机号";
            }
        } else if ("mail".equals(method)) {
            flag = StringKit.isEmail(request.email);
            if (flag) {
                verifyName = request.email;
                verifyType = AccountVerify.VERIFY_TYPE_EMAIL;
            } else {
                errMsg = "请输入正确的邮箱地址";
            }
        }

        if (flag) {
            errMsg = _toSendCode(verifyName, accountType, verifyType, system.id);
            if (errMsg == null) {
                flag = true;
            }
        }

        if (flag) {
            return json(BaseResponse.CODE_SUCCESS, "验证码已发送", response);
        } else {
            return json(BaseResponse.CODE_FAILURE, StringKit.isEmpty(errMsg) ? "验证码发送失败" : errMsg, response);
        }
    }


    public String checkSendCode(ActionBean bean, String requestAccountType) {

        CheckCodeRequest request = requst2Bean(bean.body, CheckCodeRequest.class);
        CheckCodeResponse response = new CheckCodeResponse();

        ResponseMessage errMessage = checkParam(request);
        if (errMessage.hasError()) {
            return json(BaseResponse.CODE_FAILURE, errMessage, response);
        }


        int accountType = Account.getAccountType(requestAccountType);

        PlatformSystem system = _getChannelSystem(bean.domain);
        if (system == null || system.status != PlatformSystem.STATUS_OK) {
            return json(BaseResponse.CODE_FAILURE, "系统未上线，请联系管理员", response);
        }

        String method = request.method;
        boolean flag = false;
        String errMsg = null;
        int verifyType = 0;
        String verifyName = null;
        if ("sms".equals(method)) {
            flag = StringKit.isMobileNO(request.phone);
            if (flag) {
                verifyName = request.phone;
                verifyType = AccountVerify.VERIFY_TYPE_PHONE;
            } else {
                errMsg = "请输入正确的手机号";
            }
        } else if ("mail".equals(method)) {
            flag = StringKit.isEmail(request.email);
            if (flag) {
                verifyName = request.email;
                verifyType = AccountVerify.VERIFY_TYPE_EMAIL;
            } else {
                errMsg = "请输入正确的邮箱地址";
            }
        }

        if (flag) {
            if (_checkCode(verifyName, request.code, accountType, system.id)) {
                String checkToken = generateCheckToken(verifyName, request.method);
                response.data = new VerifyTokenData();
                response.data.verifyToken = checkToken;
                return json(BaseResponse.CODE_SUCCESS, "验证码发送成功", response);
            }
        }
        return json(BaseResponse.CODE_FAILURE, StringKit.isEmpty(errMsg) ? "请输入正确的验证码" : errMsg, response);
    }

    public String listManager(ActionBean bean) {
        ListManagerRequest request = requst2Bean(bean.body, ListManagerRequest.class);
        ListManagerResponse response = new ListManagerResponse();

        ResponseMessage errMessage = checkParam(request);
        if (errMessage.hasError()) {
            return json(BaseResponse.CODE_FAILURE, errMessage, response);
        }

        List<ManagerData> list = new ArrayList<>();

        Account account = accountDao.findByUuid(bean.accountUuid);
        if (account != null) {

            List<Account> listManager = getExistsManagerUuid(account.phone, bean.sysId);
            for (Account at : listManager) {
                CompanyBean companyBean = companyClient.getCompanyById(Long.valueOf(at.user_id));
                if (companyBean != null) {
                    ManagerData managerData = new ManagerData();
                    managerData.managerName = companyBean.name;
                    managerData.managerUuid = at.account_uuid;
                    list.add(managerData);
                }
            }
        }
        if (list.isEmpty()) {
            return json(BaseResponse.CODE_AGENT_ACCOUNT_INVALID, "很抱歉，您的账号尚未生效，请联系业管处理", response);
        } else {
            response.data = list;
            return json(BaseResponse.CODE_SUCCESS, "获取成功", response);
        }
    }

    public String chooseManager(ActionBean bean) {
        ChooseManagerRequest request = requst2Bean(bean.body, ChooseManagerRequest.class);
        ChooseManagerResponse response = new ChooseManagerResponse();

        ResponseMessage errMessage = checkParam(request);
        if (errMessage.hasError()) {
            return json(BaseResponse.CODE_FAILURE, errMessage, response);
        }
        boolean isValid = false;
        Account accountU = accountDao.findByUuid(bean.accountUuid);
        Account accountManager = null;
        if (accountU != null) {
            List<Account> listManager = getExistsManagerUuid(accountU.phone, bean.sysId);
            if (listManager != null) {
                Map<String, Account> accountMap = ListKit.toMap(listManager, v -> v.account_uuid);
                if (accountMap.containsKey(request.managerUuid)) {
                    accountManager = accountMap.get(request.managerUuid);
                    if (accountU.user_type == Account.TYPE_AGENT) {
                        int validAgent = isValidAgent(accountU.phone, request.managerUuid, accountU.user_id);
                        if (validAgent == -1) {
                            return json(BaseResponse.CODE_FAILURE, "不能选择已离职的业管", response);
                        } else {
                            isValid = validAgent > 0;
                        }
                    } else {
                        isValid = true;
                    }
                }
            }

        }

        if (isValid) {
            bean.managerUuid = request.managerUuid;
            String token = null;
            Account account = new Account();
            long timeMillis = TimeKit.currentTimeMillis();
            account.token = getLoginToken(bean.accountUuid, bean.type, bean.managerUuid, bean.sysId, bean.salt);
            account.updated_at = timeMillis;
            account.account_uuid = bean.accountUuid;
            if (accountDao.updateTokenByUuid(account) > 0) {
                token = account.token;
                bindAgent(accountU.account_uuid, accountU.phone, bean.managerUuid, accountU.user_id);
            }
            if (!StringKit.isEmpty(token)) {

                response.data = new TokenData();
//                CompanyBean companyBean = companyClient.getCompanyById(Long.valueOf(accountManager.user_id));
//                if(companyBean!=null){
//                    response.data.compLogo = fileClient.getFileUrl(companyBean.head,100,100,80);
//                    response.data.compName = companyBean.name;
//                }
                response.data.token = token;
                return json(BaseResponse.CODE_SUCCESS, "企业切换成功", response);
            } else {
                return json(BaseResponse.CODE_FAILURE, "企业切换失败", response);
            }
        } else {
            return json(BaseResponse.CODE_FAILURE, "企业切换失败", response);
        }

    }


    public String modifyPassword(ActionBean bean) {
        ModifyPasswordRequest request = requst2Bean(bean.body, ModifyPasswordRequest.class);
        ModifyPasswordResponse response = new ModifyPasswordResponse();
        ResponseMessage errMessage = checkParam(request);
        if (errMessage.hasError()) {
            return json(BaseResponse.CODE_FAILURE, errMessage, response);
        }
        int accountType = bean.type;
        Account account = accountDao.findByUuid(bean.accountUuid);
        if (account != null && account.password.equals(Account.generatePwd(request.oldpsword, account.salt))) {

            String salt = StringKit.randStr(6);
            Account updateRecord = new Account();
            updateRecord.salt = salt;
            updateRecord.password = Account.generatePwd(request.password, salt);
            updateRecord.account_uuid = account.account_uuid;
            updateRecord.updated_at = TimeKit.currentTimeMillis();
            String token = getLoginToken(account.account_uuid, accountType, bean.managerUuid, bean.sysId, salt);
            updateRecord.token = token;

            if (accountDao.updatePasswordTokenByUuid(updateRecord) > 0) {
                response.data = new TokenData();
                response.data.token = token;
                return json(BaseResponse.CODE_SUCCESS, "修改密码成功", response);
            } else {
                return json(BaseResponse.CODE_FAILURE, "密码修改失败", response);
            }
        } else {
            return json(BaseResponse.CODE_FAILURE, "原密码错误", response);
        }
    }

    public String changePhone(ActionBean bean) {
        ChangePhoneEmailRequest request = requst2Bean(bean.body, ChangePhoneEmailRequest.class);
        BaseResponse response = new BaseResponse();
        ResponseMessage errMessage = checkParam(request);
        if (errMessage.hasError()) {
            return json(BaseResponse.CODE_FAILURE, errMessage, response);
        }
        CheckToken checkToken = parseCheckToken(request.verifyToken);

        if (checkToken != null) {

            Account account = accountDao.findByAccount(bean.sysId, checkToken.verifyName, bean.type, Account.ACCOUNT_FILED_PHONE);

            if (account != null && !account.account_uuid.equals(bean.accountUuid)) {
                return json(BaseResponse.CODE_FAILURE, "手机号已被占用", response);
            }

            Account updateRecord = new Account();
            updateRecord.phone = checkToken.verifyName;
            updateRecord.updated_at = TimeKit.currentTimeMillis();
            updateRecord.account_uuid = bean.accountUuid;
            if (accountDao.updatePhoneByUuid(updateRecord) > 0) {
                // TODO: 2018/4/20 change 联系人
                return json(BaseResponse.CODE_SUCCESS, "更换手机号成功", response);
            } else {
                return json(BaseResponse.CODE_FAILURE, "更换手机号失败", response);
            }
        } else {
            return json(BaseResponse.CODE_FAILURE, "请输入正确的验证码", response);
        }
    }

    public String changeEmail(ActionBean bean) {
        ChangePhoneEmailRequest request = requst2Bean(bean.body, ChangePhoneEmailRequest.class);
        BaseResponse response = new BaseResponse();

        ResponseMessage errMessage = checkParam(request);
        if (errMessage.hasError()) {
            return json(BaseResponse.CODE_FAILURE, errMessage, response);
        }

        CheckToken checkToken = parseCheckToken(request.verifyToken);
        if (checkToken != null) {
            Account account = accountDao.findByAccount(bean.sysId, checkToken.verifyName, bean.type, Account.ACCOUNT_FILED_EMAIL);

            if (account != null && !account.account_uuid.equals(bean.accountUuid)) {
                return json(BaseResponse.CODE_FAILURE, "邮箱地址已被占用", response);
            }
            Account updateRecord = new Account();
            updateRecord.email = checkToken.verifyName;
            updateRecord.updated_at = TimeKit.currentTimeMillis();
            updateRecord.account_uuid = bean.accountUuid;
            if (accountDao.updateEmailByUuid(updateRecord) > 0) {
                return json(BaseResponse.CODE_SUCCESS, "更换邮箱地址成功", response);
            } else {
                return json(BaseResponse.CODE_FAILURE, "更换邮箱地址失败", response);
            }
        } else {
            return json(BaseResponse.CODE_FAILURE, "请输入正确的验证码", response);
        }
    }

    public String loginOut(ActionBean bean) {
        BaseResponse response = new BaseResponse();
        return json(BaseResponse.CODE_SUCCESS, "成功退出", response);
    }

    public String home(ActionBean bean) {
        boolean hasOneManager = false;
        BaseResponse response = new BaseResponse();
        HomeData homeData = new HomeData();
        if (!StringKit.isEmpty(bean.managerUuid)) {
            Account account = accountDao.findByUuid(bean.managerUuid);
            if (account != null) {
                CompanyBean companyBean = companyClient.getCompanyById(Long.valueOf(account.user_id));
                if (companyBean != null) {
                    hasOneManager = true;
                    homeData.compLogo = fileClient.getFileUrl(companyBean.head, 100, 100, 80);
                    homeData.compName = companyBean.name;
                }
            }
        }

        if (!hasOneManager) {
            // TODO: 2018/7/4
        }
        response.data = homeData;
        return json(BaseResponse.CODE_SUCCESS, "获取成功", response);
    }

    /**
     * 联合登录
     */
    public String jointLogin(ActionBean bean) {

        JointLoginRequest request = requst2Bean(bean.body, JointLoginRequest.class);

        LoginResponse response = new LoginResponse();

        ResponseMessage errMessage = checkParam(request);
        if (errMessage.hasError()) {
            return json(BaseResponse.CODE_FAILURE, errMessage, response);
        }
        PlatformSystem system = platformSystemDao.findCode(request.platform);

        if (system == null) {
            return json(BaseResponse.CODE_FAILURE, "平台[" + request.platform + "]暂不支持联合登录", response);
        }


        Account searchAManager = new Account();
        L.log.info(system.id+"-id");
        L.log.info(Account.TYPE_COMPANY+"-user_type");
        searchAManager.sys_id = system.id;
        searchAManager.user_type = Account.TYPE_COMPANY;
        Account accountManager = accountDao.findOneBySysType(searchAManager);
        L.log.info("searchAManager:"+JsonKit.bean2Json(searchAManager));
        if (accountManager == null) {
            return json(BaseResponse.CODE_FAILURE, "系统未上线，请联系管理员", response);
        }

        Account account = new Account();
        boolean needAdd = false;
        switch (request.platform) {

            default:
                if (!StringKit.isMobileNO(request.phone)) {
                    return json(BaseResponse.CODE_FAILURE, "手机号错误", response);
                }
                Account accountExists = accountDao.findByAccount(system.id, request.phone, Account.TYPE_CUST_USER, Account.ACCOUNT_FILED_PHONE);
                if (accountExists == null) {
                    account.phone = request.phone;
                    account.user_type = Account.TYPE_CUST_USER;
                    needAdd = true;
                } else {
                    account = accountExists;
                }
                break;
        }
        L.log.info(needAdd+"needAdd");

        boolean isLogin = false;

        if (needAdd) {

            PersonBean addPerson = new PersonBean();
            addPerson.name = request.name;
            addPerson.phone = request.phone;
            addPerson.email = request.email;
            addPerson.address_detail = request.address;
//            addPerson.address = request.area;
            // TODO: 2018/7/5 省市县
            addPerson.cert_code = request.certCode;
            if (StringKit.isInteger(request.certType)) {
                addPerson.cert_type = Integer.valueOf(request.certType);
            }
            L.log.info("addPerson"+JsonKit.bean2Json(addPerson));
            int userId = personClient.saveInfo(addPerson);
            L.log.info("user_id"+userId);
            if(userId>0){
                String accountUuid = String.valueOf(AccountUuidWorker.getWorker(1, 1).nextId());
                String salt = StringKit.randStr(6);
                account.account_uuid = accountUuid;
                account.status = Account.STATUS_NORMAL;
                account.password = Account.generatePwd("", salt);
                account.salt = salt;
                account.token = getLoginToken(account.account_uuid, account.user_type, accountManager.account_uuid, account.sys_id, account.salt);
                account.user_id = String.valueOf(userId);
                account.sys_id = system.id;
                account.state = Common.STATE_ONLINE;
                account.origin = request.origin;
                account.created_at = account.updated_at = TimeKit.currentTimeMillis();
                L.log.info("accountLogin"+JsonKit.bean2Json(account));
                isLogin = accountDao.registry(account)>0;
                if (isLogin){
                    CustomerBean customerBean = new CustomerBean();
                    customerBean.type = 1;
                    customerBean.customerId = userId;
                    customerBean.accountUuid = accountUuid;
                    customerBean.managerUuid = accountManager.account_uuid;
                    customerClient.saveCust(customerBean);
                }
            }

            L.log.info(isLogin+"login-1");
        } else {
            account.token = getLoginToken(account.account_uuid, account.user_type, accountManager.account_uuid, account.sys_id, account.salt);
            account.updated_at = TimeKit.currentTimeMillis();
            isLogin = accountDao.updateTokenByUuid(account)>0;
            L.log.info(isLogin+"login-2");
        }
        L.log.info(isLogin+"login-3");
        if(isLogin){
            response.data = new TokenData();
            response.data.token = account.token;
            response.data.accountUuid = account.account_uuid;
            response.data.managerUuid = accountManager.account_uuid;
            response.data.userId = account.user_id;
            response.data.userType = account.user_type;

            return json(BaseResponse.CODE_SUCCESS,"登录成功",response);
        }else{
            return json(BaseResponse.CODE_FAILURE,"登录失败",response);
        }
    }


    private String getLoginToken(String uuid, int accountType, String sysUuid, long sysId, String salt) {
        ActionBean loginAction = new ActionBean();
        loginAction.accountUuid = uuid;
        loginAction.managerUuid = sysUuid;
        loginAction.tokenTime = TimeKit.currentTimeMillis();
        loginAction.salt = ActionBean.getSalt(salt);
        loginAction.sysId = sysId;
        loginAction.type = accountType;
        return ActionBean.packageToken(loginAction);
    }


    private String _toSendCode(String verifyName, int accountType, int verifyType, long sysId) {

        AccountVerify accountVerify = accountVerifyDao.findLatestByFromVerify(verifyName, accountType, sysId);

        long currentTime = TimeKit.currentTimeMillis();
        String code = null;
        boolean flag = false;
        boolean needInsert = false;
        String errMsg = null;
        if (accountVerify != null && accountVerify.status == AccountVerify.STATUS_NOT_USE && accountVerify.code_time > currentTime) {
            //存在一条未验证的短信
            if (accountVerify.updated_at + CODE_LIMIT_NO_RESEND_TIME > currentTime) {
                //发送间隔时间 不能小于60s
                flag = false;
                int extraTime = (int) (accountVerify.updated_at + CODE_LIMIT_NO_RESEND_TIME - currentTime) / 1000;
                errMsg = extraTime + "秒后重试";
            } else if (accountVerify.created_at + CODE_UNIQUE_TIME > currentTime) {

                AccountVerify updateVerify = new AccountVerify();
                updateVerify.id = accountVerify.id;
                updateVerify.updated_at = currentTime;
                updateVerify.code = accountVerify.code;
                updateVerify.code_time = currentTime + CODE_VALID_TIME;
                code = accountVerify.code;
                flag = accountVerifyDao.updateCodeTime(updateVerify) > 0;
            } else {
                AccountVerify updateVerify = new AccountVerify();
                updateVerify.id = accountVerify.id;
                updateVerify.updated_at = currentTime;
                updateVerify.code = accountVerify.code;
                updateVerify.code_time = currentTime;
                accountVerifyDao.updateCodeTime(updateVerify);
                needInsert = true;
            }
        } else {
            needInsert = true;
        }

        if (needInsert) {
//            if(ConstantKit.IS_PRODUCT){
//                code = StringKit.randNum(6);
//            }else{
//                code = "666666";
//            }
            code = StringKit.randNum(6);
            AccountVerify addRecord = new AccountVerify();
            addRecord.sys_id = sysId;
            addRecord.from_type = accountType;
            addRecord.verify_name = verifyName;
            addRecord.verify_type = verifyType;
            addRecord.code = code;
            addRecord.code_time = currentTime + CODE_VALID_TIME;
            addRecord.status = AccountVerify.STATUS_NOT_USE;
            addRecord.created_at = currentTime;
            addRecord.updated_at = currentTime;
            flag = accountVerifyDao.insert(addRecord) > 0;
        }
        if (flag) {
            errMsg = null;
            if (AccountVerify.VERIFY_TYPE_PHONE == verifyType) {
                if (!smsHandingClient.sendVerifyCode(verifyName, code)) {
                    errMsg = "发送失败";
                }
            }else if(AccountVerify.VERIFY_TYPE_EMAIL == verifyType){
                EmailInfoBean infoBean = new EmailInfoBean();
                infoBean.bekongs = "TY";
                infoBean.source_code = "VCODE";
                infoBean.send_type = 1;
                infoBean.type = 1;
                infoBean.title = "天眼互联";
                infoBean.html = "您的验证码是："+code+"，请于10分钟内输入，切勿泄漏他人。";
                infoBean.to_email = new ArrayList<>();
                infoBean.to_email.add(verifyName);
                if(emailSendClient.send(infoBean)<=0){
                    errMsg = "发送失败";
                }
            }
        }
        return errMsg;

    }

    private boolean _checkCode(String verifyName, String code, int accountType, long sysId) {
        boolean verifyFlag = false;
        if (!StringKit.isEmpty(verifyName)) {
            long currentTime = TimeKit.currentTimeMillis();
            AccountVerify verify = accountVerifyDao.findLatestByFromVerify(verifyName, accountType, sysId);

            if (verify != null && verify.status == AccountVerify.STATUS_NOT_USE && currentTime < verify.code_time) {
                verifyFlag = verify.code.equals(code);
                if (verifyFlag) {
                    AccountVerify updateUsed = new AccountVerify();
                    updateUsed.updated_at = currentTime;
                    updateUsed.status = AccountVerify.STATUS_USED;
                    updateUsed.id = verify.id;
                    accountVerifyDao.updateStatus(updateUsed);
                }
            }

        }
        return verifyFlag;
    }

    private PlatformSystem _getChannelSystem(String referer) {
        if (ConstantKit.IS_PRODUCT) {
            String domain = StringKit.parseDomain(referer);
            if (domain != null) {
                return platformSystemDao.findDomain(domain);
            }
        } else {
            PlatformSystem system = new PlatformSystem();
            system.id = 1;
            system.status = PlatformSystem.STATUS_OK;
            return system;
        }


        return null;
    }


    private String generateCheckToken(String verifyName, String method) {
        CheckToken checkToken = new CheckToken();
        checkToken.method = method;
        checkToken.verifyName = verifyName;
        checkToken.time = TimeKit.currentTimeMillis();
        return new RC4Kit("checkToken-inshcos").encry_RC4_hex(JsonKit.bean2Json(checkToken));
    }

    private CheckToken parseCheckToken(String checkToken) {
        CheckToken bean = JsonKit.json2Bean(new RC4Kit("checkToken-inshcos").decry_RC4_hex(checkToken), CheckToken.class);
        if (bean == null || StringKit.isEmpty(bean.verifyName) || StringKit.isEmpty(bean.method)) {
            return null;
        }
        return bean;
    }

    private static class CheckToken {

        public long time;

        public String verifyName;

        public String method;

    }

    private List<Account> getExistsManagerUuid(String phone, long sysId) {
        Account searchManagers = new Account();
        searchManagers.sys_id = sysId;
        searchManagers.user_type = Account.TYPE_COMPANY;
        List<Account> accounts = accountDao.findListBySysType(searchManagers);

        List<Account> exists = new ArrayList<>();

        if (accounts != null) {
            List<String> columnList = ListKit.toColumnList(accounts, v -> v.account_uuid);
            List<AgentJobBean> beanList = agentJobClient.getInviteAgents(phone, columnList);
            if (beanList != null && !beanList.isEmpty()) {
                List<String> columnList1 = ListKit.toColumnList(beanList, v -> v.manager_uuid);
                for (Account account : accounts) {
                    if (columnList1.contains(account.account_uuid)) {
                        exists.add(account);
                    }
                }
            }
        }
        return exists;
    }


    private int bindAgent(String accountUuid, String phone, String managerUuid, String userId) {

        AccountDefault aDefault = new AccountDefault();
        aDefault.account_uuid = accountUuid;
        aDefault.manager_uuid = managerUuid;
        aDefault.updated_at = TimeKit.currentTimeMillis();
        if (agentJobClient.bindPerson(phone, managerUuid, Long.valueOf(userId)) > 0) {
            return accountDao.addOrUpdate(aDefault);
        } else {
            return 0;
        }

    }

    private int isValidAgent(String managerUuid, String phone, String userId) {
        int flag = 0;
        AgentJobBean agentJobBean = agentJobClient.getAgentInfoInAndOut(managerUuid, phone);
        if (agentJobBean != null) {
            if (agentJobBean.out_time < TimeKit.currentTimeMillis()) {
                flag = -1;
                if (agentJobBean.bind_status == 1) {
                    agentJobClient.unBindPerson(phone, managerUuid, Long.valueOf(userId));
                }
            } else {
                flag = agentJobBean.bind_status;
            }

        }
        return flag;
    }


}
