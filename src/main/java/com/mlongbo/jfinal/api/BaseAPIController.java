package com.mlongbo.jfinal.api;

import com.jfinal.core.Controller;
import com.mlongbo.jfinal.common.bean.DataResponse;
import com.mlongbo.jfinal.common.Require;
import com.mlongbo.jfinal.common.utils.StringUtils;
import com.mlongbo.jfinal.model.User;
import com.mlongbo.jfinal.common.bean.BaseResponse;
import com.mlongbo.jfinal.common.bean.Code;
import com.mlongbo.jfinal.common.token.TokenManager;

import java.lang.reflect.Array;
import java.util.List;

/**
 * 基本的api
 * 基于jfinal controller做一些封装
 * @author malongbo
 */
public class BaseAPIController extends Controller {

    /**
     * 获取当前用户对象
     * @return
     */
    protected User getUser() {
        User user = getAttr("user");
        if (user == null) {
            String token = getPara("token");
            return StringUtils.isEmpty(token) ? null : TokenManager.getMe().validate(token);
        }
        return getAttr("user");
    }

    /**
     * 响应接口不存在*
     */
    public void render404() {

        renderJson(new BaseResponse(Code.NOT_FOUND));
        
    }

    /**
     * 响应请求参数有误*
     * @param message 错误信息
     */
    public void renderArgumentError(String message) {

        renderJson(new BaseResponse(Code.ARGUMENT_ERROR, message));

    }

    /**
     * 响应数组类型*
     * @param list 结果集合
     */
    public void renderDataResponse(List<?> list) {
        DataResponse resp = new DataResponse();
        resp.setData(list);
        if (list == null || list.size() == 0) {
            resp.setMessage("未查询到数据");
        } else {
            resp.setMessage("success");
        }
        renderJson(resp);
        
    }

    /**
     * 响应操作成功*
     * @param message 响应信息
     */
    public void renderSuccess(String message) {
        renderJson(new BaseResponse().setMessage(message));
        
    }

    /**
     * 响应操作失败*
     * @param message 响应信息
     */
    public void renderFailed(String message) {
        renderJson(new BaseResponse(Code.FAIL, message));
        
    }
    /**
     * 判断请求类型是否相同*
     * @param name
     * @return
     */
    protected boolean methodType(String name) {
        return getRequest().getMethod().equalsIgnoreCase(name);
    }
    
    /**
     * 判断参数值是否为空
     * @param rules
     * @return
     */
    public boolean notNull(Require rules) {

        if (rules == null || rules.getLength() < 1) {
            return true;
        }

        for (int i = 0, total = rules.getLength(); i < total; i++) {
            Object key = rules.get(i);
            String message = rules.getMessage(i);
            BaseResponse response = new BaseResponse(Code.ARGUMENT_ERROR);
            
            if (key == null) {
                renderJson(response.setMessage(message));
                return false;
            }

            if (key instanceof String && StringUtils.isEmpty((String) key)) {
                renderJson(response.setMessage(message));
                return false;
            }

            if (key instanceof Array) {
                Object[] arr = (Object[]) key;

                if (arr.length < 1) {
                    renderJson(response.setMessage(message));
                    return false;
                }
            }
        }

        return true;
    }
}
