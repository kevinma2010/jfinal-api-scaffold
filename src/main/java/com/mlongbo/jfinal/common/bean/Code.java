package com.mlongbo.jfinal.common.bean;

/**
 * @author malongbo
 */
public class Code {

    /**
     * 成功
     */
    public static final int SUCCESS = 1;

    /**
     * 失败 
     */
    public static final int FAIL = 0;

    /**
     * 参数错误: 一般是缺少或参数值不符合要求
     */
    public static final int ARGUMENT_ERROR = 2;

    /**
     * 服务器错误
     */
    public static final int ERROR = 500;

    /**
     * 接口不存在
     */
    public static final int NOT_FOUND = 404;

    /**
     * token无效
     */
    public static final int TOKEN_INVALID = 422;

    /**
     * 帐号已存在*
     */
    public static final int ACCOUNT_EXISTS = 3;

    /**
     * 验证码错误
     */
    public static final int CODE_ERROR = 4;
}
