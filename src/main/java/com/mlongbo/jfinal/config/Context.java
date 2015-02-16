package com.mlongbo.jfinal.config;

import javax.servlet.http.HttpServletRequest;

/**
 * @author malongbo
 */
public class Context {
    private static final Context instance = new Context();
    private ThreadLocal<HttpServletRequest> request = new ThreadLocal<HttpServletRequest>();
    private AppProperty config;
    private boolean initialized = false;
    
    public static final Context me() {
        return instance;
    }
    
    public synchronized void setRequest(HttpServletRequest servletRequest) {
        if (request != null) {
            request.set(servletRequest);
        }
    }

    public AppProperty getConfig() {
        return config;
    }

    public HttpServletRequest getRequest() {
        if (request != null) {
            return request.get();
        }
        return null;
    }
    
    public synchronized void init() {
        if (initialized) {
            return;
        }
        
        config = AppProperty.me().init();

        initialized = true;
        
    }
    
    public synchronized void destroy () {
        config.destroy();
        config = null;
        request = null;
    }
}
