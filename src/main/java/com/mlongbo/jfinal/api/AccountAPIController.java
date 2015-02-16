package com.mlongbo.jfinal.api;

import com.jfinal.aop.Before;
import com.jfinal.aop.ClearInterceptor;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;
import com.mlongbo.jfinal.common.bean.*;
import com.mlongbo.jfinal.common.utils.SMSUtils;
import com.mlongbo.jfinal.common.Require;
import com.mlongbo.jfinal.common.token.TokenManager;
import com.mlongbo.jfinal.common.utils.DateUtils;
import com.mlongbo.jfinal.common.utils.RandomUtils;
import com.mlongbo.jfinal.common.utils.StringUtils;
import com.mlongbo.jfinal.config.AppProperty;
import com.mlongbo.jfinal.interceptor.TokenInterceptor;
import com.mlongbo.jfinal.model.RegisterCode;
import com.mlongbo.jfinal.model.User;

import static com.mlongbo.jfinal.model.User.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户账号相关的接口*
 *
 * 检查账号是否被注册: GET /api/account/checkUser
 * 发送注册验证码: POST /api/account/sendCode
 * 注册: POST /api/account/register
 * 登录： POST /api/account/login
 * 查询用户资料: GET /api/account/profile
 * 修改用户资料: PUT /api/account/profile
 * 修改密码: PUT /api/account/password
 * 修改头像: PUT /api/account/avatar
 *
 * @author malongbo
 */
@Before(TokenInterceptor.class)
public class AccountAPIController extends BaseAPIController {
	private static Logger log = Logger.getLogger(AccountAPIController.class);

    /**
     * 检查用户账号是否被注册*
     */
    @ClearInterceptor
    public void checkUser() {
        String loginName = getPara("loginName");
        if (StringUtils.isEmpty(loginName)) {
            renderArgumentError("loginName can not be null");
            return;
        }
        //检查手机号码是否被注册
        boolean exists = Db.findFirst("SELECT * FROM t_user WHERE loginName=?", loginName) != null;
        renderJson(new BaseResponse(exists ? Code.SUCCESS:Code.FAIL, exists ? "registered" : "unregistered"));
    }
    
    /**
     * 1. 检查是否被注册*
     * 2. 发送短信验证码*
     */
    @ClearInterceptor
    public void sendCode() {
        String loginName = getPara("loginName");
        if (StringUtils.isEmpty(loginName)) {
            renderArgumentError("loginName can not be null");
            return;
        }

        //检查手机号码有效性
        if (!SMSUtils.isMobileNo(loginName)) {
            renderArgumentError("mobile number is invalid");
            return;
        }

        //检查手机号码是否被注册
        if (Db.findFirst("SELECT * FROM t_user WHERE loginName=?", loginName) != null) {
            renderJson(new BaseResponse(Code.ACCOUNT_EXISTS,"mobile already registered"));
            return;
        }

        String smsCode = SMSUtils.randomSMSCode(4);
        //发送短信验证码
        if (!SMSUtils.sendCode(loginName, smsCode)) {
            renderFailed("sms send failed");
            return;
        }
        
        //保存验证码数据
        RegisterCode registerCode = new RegisterCode()
                .set(RegisterCode.MOBILE, loginName)
                .set(RegisterCode.CODE, smsCode);

        //保存数据
        if (Db.findFirst("SELECT * FROM t_register_code WHERE mobile=?", loginName) == null) {
            registerCode.save();
        } else {
            registerCode.update();
        }
        
        renderJson(new BaseResponse("sms sended"));
        
    }
    
	/**
	 * 用户注册
	 */
    @ClearInterceptor()
	public void register(){
		//必填信息
		String loginName = getPara("loginName");//登录帐号
        int code = getParaToInt("code", 0);//手机验证码
        int sex = getParaToInt("sex", 0);//性别
        String password = getPara("password");//密码
		String nickName = getPara("nickName");//昵称
    	//头像信息，为空则使用默认头像地址
    	String avatar = getPara("avatar", AppProperty.me().defaultUserAvatar());

        //校验必填项参数
		if(!notNull(Require.me()
                .put(loginName, "loginName can not be null")
                .put(code, "code can not be null")//根据业务需求决定是否使用此字段
                .put(password, "password can not be null")
                .put(nickName, "nickName can not be null"))){
			return;
		}

        //检查账户是否已被注册
        if (Db.findFirst("SELECT * FROM t_user WHERE loginName=?", loginName) != null) {
            renderJson(new BaseResponse(Code.ACCOUNT_EXISTS, "mobile already registered"));
            return;
        }
        
        //检查验证码是否有效, 如果业务不需要，则无需保存此段代码
        if (Db.findFirst("SELECT * FROM t_register_code WHERE mobile=? AND code = ?", loginName, code) == null) {
            renderJson(new BaseResponse(Code.CODE_ERROR,"code is invalid"));
            return;
        }
        
		//保存用户数据
		String userId = RandomUtils.randomCustomUUID();

		new User()
                .set("userId", userId)
                .set(User.LOGIN_NAME, loginName)
		        .set(User.PASSWORD, StringUtils.encodePassword(password, "md5"))
                .set(User.NICK_NAME, nickName)
		        .set(User.CREATION_DATE, DateUtils.getNowTimeStamp())
		        .set(User.SEX, sex)
                .set(User.AVATAR, avatar)
                .save();
		
        //删除验证码记录
        Db.update("DELETE FROM t_register_code WHERE mobile=? AND code = ?", loginName, code);
        
		//返回数据
		renderJson(new BaseResponse("success"));
	}
	
	
    /**
     * 登录接口
     */
    @ClearInterceptor()
    public void login() {
        String loginName = getPara("loginName");
        String password = getPara("password");
        //校验参数, 确保不能为空
        if (!notNull(Require.me()
                .put(loginName, "loginName can not be null")
                .put(password, "password can not be null")
        )) {
            return;
        }
        String sql = "SELECT * FROM t_user WHERE loginName=? AND password=?";
        User nowUser = User.user.findFirst(sql, loginName, StringUtils.encodePassword(password, "md5"));
        LoginResponse response = new LoginResponse();
        if (nowUser == null) {
            response.setCode(Code.FAIL).setMessage("userName or password is error");
            renderJson(response);
            return;
        }
        Map<String, Object> userInfo = new HashMap<String, Object>(nowUser.getAttrs());
        userInfo.remove(PASSWORD);
        response.setInfo(userInfo);
        response.setMessage("login success");
        response.setToken(TokenManager.getMe().generateToken(nowUser));
        response.setConstant(Constant.me());
        renderJson(response);
    }

    /**
     * 资料相关的接口
     */
    public void profile() {
        String method = getRequest().getMethod();
        if ("get".equalsIgnoreCase(method)) { //查询资料
            getProfile();
        } else if ("put".equalsIgnoreCase(method)) { //修改资料
            updateProfile();
        } else {
            render404();
        }
    }


    /**
     * 查询用户资料
     */
    private void getProfile() {
        String userId = getPara("userId");
        User resultUser = null;
        if (StringUtils.isNotEmpty(userId)) {
            resultUser = User.user.findById(userId);
        } else {
            resultUser = getUser();
        }

        DatumResponse response = new DatumResponse();
        
        if (resultUser == null) {
            response.setCode(Code.FAIL).setMessage("user is not found");
        } else {
            HashMap<String, Object> map = new HashMap<String, Object>(resultUser.getAttrs());
            map.remove(PASSWORD);
            response.setDatum(map);
        }

        renderJson(response);
    }

    /**
     * 修改用户资料
     */
    private void updateProfile() {
        boolean flag = false;
        BaseResponse response = new BaseResponse();
        User user = getUser();
        String nickName = getPara("nickName");
        if (StringUtils.isNotEmpty(nickName)) {
            user.set(NICK_NAME, nickName);
            flag = true;
        }

        String email = getPara("email");
        if (StringUtils.isNotEmpty(email)) {
            user.set(EMAIL, email);
            flag = true;
        }
        
        String avatar = getPara("avatar");
        if (StringUtils.isNotEmpty(avatar)) {
            user.set(AVATAR, avatar);
            flag = true;
        }

        //修改性别
        Integer sex = getParaToInt("sex", null);
        if (null != sex) {
            if (!User.checkSex(sex)) {
                renderArgumentError("sex is invalid");
                return;
            }
            user.set(SEX, sex);
            flag = true;
        }

        if (flag) {
            boolean update = user.update();
            renderJson(response.setCode(update ? Code.SUCCESS : Code.FAIL).setMessage(update ? "update success" : "update failed"));
        } else {
            renderArgumentError("must set profile");
        }
    }

    /**
     * 修改密码
     */
    public void password(){
        if (!"put".equalsIgnoreCase(getRequest().getMethod())) {
            render404();
            return;
        }
    	//根据用户id，查出这个用户的密码，再跟传递的旧密码对比，一样就更新，否则提示旧密码错误
    	String oldPwd = getPara("oldPwd");
    	String newPwd = getPara("newPwd");
    	if(!notNull(Require.me()
    			.put(oldPwd, "old password can not be null")
    			.put(newPwd, "new password can not be null"))){
    		return;
    	}
    	//用户真实的密码
        User nowUser = getUser();
    	if(StringUtils.encodePassword(oldPwd, "md5").equalsIgnoreCase(nowUser.getStr(PASSWORD))){
    		boolean flag = nowUser.set(User.PASSWORD, StringUtils.encodePassword(newPwd, "md5")).update();
            renderJson(new BaseResponse(flag?Code.SUCCESS:Code.FAIL, flag?"success":"failed"));
    	}else{
            renderJson(new BaseResponse(Code.FAIL, "oldPwd is invalid"));
    	}
    }
    
    /**
     * 修改头像接口
     * /api/account/avatar
     */
    public void avatar() {
        if (!"put".equalsIgnoreCase(getRequest().getMethod())) {
            renderJson(new BaseResponse(Code.NOT_FOUND));
            return;
        }
    	String avatar=getPara("avatar");
    	if(!notNull(Require.me()
    			.put(avatar, "avatar url can not be null"))){
    		return;
    	}
    	getUser().set(User.AVATAR, avatar).update();
    	renderSuccess("success");
    }
}

