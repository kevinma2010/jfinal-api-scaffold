package com.mlongbo.jfinal.bean;

import com.mlongbo.jfinal.common.PathConstant;

/**
 * @author malongbo
 * @date 2015/1/17
 * @package com.pet.project.bean
 */
public class Constant {
    private static Constant me = new Constant();
    
    private String resourceServer;
    
    /**
     * 获取单例对象
     * @return
     */
    public static Constant me() {
        return me;
    }
    
    public String getResourceServer() {
        return PathConstant.serverIp;
    }

    public void setResourceServer(String resourceServer) {
        this.resourceServer = resourceServer;
    }
}
