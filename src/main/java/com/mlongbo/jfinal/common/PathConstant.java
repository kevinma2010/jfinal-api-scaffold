package com.mlongbo.jfinal.common;


import com.mlongbo.jfinal.common.utils.PropertiesUtil;

/**
 * 文件目录定义类
 * @author tangkunyin
 * 配置文件中定义的常量，项目发布后，只需要该properties文件
 */
public class PathConstant {
	//上传的根路径，通过ip或域名访问，刚好到达这层目录
    public static final String rootPath= PropertiesUtil.getProperty("uploadRootPath");
    
    public static final String tomcatPath = PropertiesUtil.getProperty("tomcatPath");
    
    //服务器ip配置
    public static final String serverIp=PropertiesUtil.getProperty("serverIp");
    
    //图片
    public static final String images=PropertiesUtil.getProperty("images");//相对路径，可用于URL中
    public static final String imagesSavedPath=rootPath+images;//本地的绝对路径
    
    //视频
    public static final String videos=PropertiesUtil.getProperty("videos");
    public static final String videosSavedPath=rootPath+videos;
    
    //其他文件
    public static final String others=PropertiesUtil.getProperty("others");
    public static final String othersSavedPath=rootPath+others;
    
    //用户默认图片：http://115.28.129.88/0.jpg
    public static final String DefaultUserAvatar =PropertiesUtil.getProperty("DefaultUserAvatar");
    
    public static final String DefaultPetAvatar =PropertiesUtil.getProperty("DefaultPetAvatar");
}