package com.mlongbo.jfinal.version;

import com.mlongbo.jfinal.common.utils.StringUtils;

import java.io.IOException;

/**
 * 版本管理器*
 * @author malongbo
 */
public class VersionManager {
    private static VersionManager me = new VersionManager();
    private VersionProperty property; //文件配置
    private static String propertyName = "/version.xml"; //默认的配置文件

    public VersionManager() {
        this(propertyName);
    }
    
    public VersionManager(String propertyName) {
        try {
            property = new VersionProperty(VersionManager.class.getResource(propertyName).getPath());
        } catch (IOException e) {
            throw new RuntimeException(propertyName + " can not found", e);
        }
    }
    
    public static VersionManager me() {
        return me;
    }

    /**
     * 检查版本*
     * @param version 版本号
     * @param client 终端类型
     * @return 当前最新版本
     */
    public Version check(String version, String client) {
        if (property == null || StringUtils.isEmpty(version) || StringUtils.isEmpty(client)) {
            return null;
        }

        Version nowVersion = property.getNowVersion(ClientType.getClientType(client));
        
        if (nowVersion == null || version.equalsIgnoreCase(nowVersion.getVersion())) {
            return null;
        }
        
        return nowVersion;
    }
}
