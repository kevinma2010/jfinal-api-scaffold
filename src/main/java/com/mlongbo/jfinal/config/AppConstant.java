package com.mlongbo.jfinal.config;

/**
 * 常量类*
 * @author malongbo
 */
public final class AppConstant {
    private AppConstant(){}

    /**
     * 文件下载的地址前缀*
     */
    public static final String RES_PREFIX = "resource.prefix";

    /**
     * 标记是否将上传的文件存储在应用目录，如tomcat. 1表示是，0表示否*
     */
    public static final String RES_APP_PATH = "resource.appPath";

    /**
     * 上传文件存储目录, 如果appPath值为0,需填写目录的绝对路径，否则填写应用目录的相对路径*
     */
    public static final String RES_UPLOAD_ROOT_PATH = "resource.uploadRootPath";

    /**
     * 图片文件存储的相对目录*
     */
    public static final String RES_IMAGE_PATH = "resource.imagePath";

    /**
     * 视频文件存储的相对目录*
     */
    public static final String RES_VIDEO_PATH = "resource.videoPath";

    /**
     * 其它文件存储的相对目录*
     */
    public static final String RES_OTHER_PATH = "resource.otherPath";

    /**
     * 默认的头像路径, 与prefix值拼接后可访问*
     */
    public static final String RES_DEFAULT_USER_AVATAR = "resource.defaultUserAvatar";
}
