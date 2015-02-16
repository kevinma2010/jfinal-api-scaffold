package com.mlongbo.jfinal.common;

import com.mlongbo.jfinal.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * *
 * @author malongbo
 */
public class Require {
    private List<Object> conditions = new ArrayList<Object>();
    private List<String> messages = new ArrayList<String>();

    public Require put(Object param, String message) {
        conditions.add(param);
        messages.add(message);
        return this;
    }

    public static Require me() {
        return new Require();
    }

    public List<Object> getConditions() {
        return conditions;
    }

    public List<String> getMessages() {
        return messages;
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

    public boolean validate() {

        for (Object obj : conditions) {
            if (obj == null) {
                return true;
            }

            if (obj instanceof String && StringUtils.isEmpty((String) obj)) {
                return true;
            }
        }

        return false;
    }
}
