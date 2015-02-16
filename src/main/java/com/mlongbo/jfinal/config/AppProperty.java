package com.mlongbo.jfinal.config;

import com.mlongbo.jfinal.common.XmlProperty;

import java.io.IOException;

/**
 * @author malongbo
 */
public class AppProperty {
    private XmlProperty property;
    private String propertyName = "configure.xml";
    private static AppProperty instance = new AppProperty();

    public AppProperty() {
    }
    
    public AppProperty(String propertyName) {
        this.propertyName = propertyName;
    }


    public static final AppProperty me() {
        return instance;

    }

    protected AppProperty init() {

        try {
            property = new XmlProperty(AppProperty.class.getResource("/"+propertyName).getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    protected void destroy() {
        property.destroy();
        property = null;
    }
    
    public String getProperty(String key) {
        if (property == null) {
            return null;
        }
        return property.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        
        return value;

    }
    
    public Integer getPropertyToInt(String key, Integer defaultValue) {
        try {
            return Integer.valueOf(getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    public Integer getPropertyToInt(String key) {
        return getPropertyToInt(key, null);
    }

    public Double getPropertyToDouble(String key, Double defaultValue) {
        try {
            return Double.valueOf(getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Double getPropertyToDouble(String key) {
        return getPropertyToDouble(key, null);
    }
    
    public Float getPropertyToFloat(String key, Float defaultValue) {
        try {
            return Float.valueOf(getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Float getPropertyToFloat(String key) {
        return getPropertyToFloat(key, null);
    }
    
    public Boolean getPropertyToBoolean(String key, Boolean defaultValue) {
        try {
            return Boolean.valueOf(getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Boolean getPropertyToBoolean(String key) {
        return getPropertyToBoolean(key, null);
    }
    
    public String resourcePrefix() {
        return getProperty(AppConstant.RES_PREFIX);
    }

    public int appPath() {
        return getPropertyToInt(AppConstant.RES_APP_PATH, 1);
    }
    
    public String uploadRooPath() {
        return getProperty(AppConstant.RES_UPLOAD_ROOT_PATH, "upload");
    }
    
    public String imagePath() {
        return getProperty(AppConstant.RES_IMAGE_PATH, "/images");
    }
    
    public String videoPath() {
        return getProperty(AppConstant.RES_VIDEO_PATH, "/videoPath");
    }
    
    public String otherPath() {
        return getProperty(AppConstant.RES_OTHER_PATH, "/otherPath");
    }
    
    public String defaultUserAvatar() {
        return getProperty(AppConstant.RES_DEFAULT_USER_AVATAR, "/defaultUserAvatar");
    }
}
