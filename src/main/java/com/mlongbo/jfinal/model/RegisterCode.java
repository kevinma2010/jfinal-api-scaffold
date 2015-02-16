package com.mlongbo.jfinal.model;

import com.jfinal.plugin.activerecord.Model;

/**
 * 短信注册验证码*
 * @author malongbo
 */
public class RegisterCode extends Model<RegisterCode> {
    
    public static final String MOBILE = "mobile";
    
    public static final String CODE = "code";
    
    public static RegisterCode dao = new RegisterCode();
    
}
