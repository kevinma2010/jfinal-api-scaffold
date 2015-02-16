package com.mlongbo.jfinal.common.bean;

import java.util.Map;

/**
 * @author malongbo
 * @date 2015/1/17
 * @package com.pet.project.bean
 */
public class LoginResponse extends BaseResponse {
    private Map<String, Object> info;
    private String token;
    private Constant constant = Constant.me();

    public LoginResponse() {
        super();
    }

    public LoginResponse(Integer code) {
        super(code);
    }

    public LoginResponse(Integer code, String message) {
        super(code, message);
    }

    public Map<String, Object> getInfo() {
        return info;
    }


    public String getToken() {
        return token;
    }

    public Constant getConstant() {
        return constant;
    }

    public void setInfo(Map<String, Object> info) {
        this.info = info;
    }


    public void setToken(String token) {
        this.token = token;
    }

    public void setConstant(Constant constant) {
        this.constant = constant;
    }
}