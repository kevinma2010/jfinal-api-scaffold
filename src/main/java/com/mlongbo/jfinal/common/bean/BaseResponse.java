package com.mlongbo.jfinal.common.bean;

/**
 * @author malongbo
 * @date 2015/1/17
 * @package com.pet.project.bean
 */
public class BaseResponse {
    
    private Integer code = Code.SUCCESS;
    
    private String message;

    public BaseResponse() {
    }

    public BaseResponse(String message) {
        this.message = message;
    }

    public BaseResponse(Integer code) {
        this.code = code;
    }

    public BaseResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseResponse setCode(Integer code) {
        this.code = code;
        return this;
    }

    public BaseResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
