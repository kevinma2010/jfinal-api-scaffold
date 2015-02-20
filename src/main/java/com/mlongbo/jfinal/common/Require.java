package com.mlongbo.jfinal.common;

import com.mlongbo.jfinal.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 存放校验条件和响应信息
 * @author malongbo
 */
public class Require {
    private List<Object> conditions = new ArrayList<Object>(); //不为空的条件集合
    private List<String> messages = new ArrayList<String>(); //响应信息集合

    public Require put(Object param, String message) {
        conditions.add(param);
        messages.add(message);
        return this;
    }

    /**
     * 创建实例*
     * @return Require对象
     */
    public static final Require me() {
        return new Require();
    }

    public Object get(int index) {
        return conditions.get(index);
    }
    public String getMessage(int index) {
        return messages.get(index);
    }

    public int getLength() {
        return conditions.size();
    }

}
