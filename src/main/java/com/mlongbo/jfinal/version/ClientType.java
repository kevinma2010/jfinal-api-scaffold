package com.mlongbo.jfinal.version;

import java.io.Serializable;

/**
 * 终端类型枚举对象*
 * @author malongbo
 */
public enum ClientType  implements Serializable {
    ANDROID("android"),IPHONE("iphone");

    private String type;

    ClientType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static ClientType  getClientType(String type) {
        if (ANDROID.type.equalsIgnoreCase(type) ) {
            return ANDROID;
        }

        if (IPHONE.type.equalsIgnoreCase(type) ) {
            return IPHONE;
        }

        return null;
    }
}