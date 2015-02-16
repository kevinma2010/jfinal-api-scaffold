package com.mlongbo.jfinal.common.utils;

import com.jfinal.log.Logger;

import java.io.IOException;
import java.util.Properties;

/**
 * 配置文件读取工具
 * @author tang
 */
public class PropertiesUtil {
	private static Logger log= Logger.getLogger(PropertiesUtil.class);
	/**
	 * 根据key获取配置文件value
	 * @return
	 */
	public static String getProperty(String key){
		try {
			Properties pro=new Properties();
			pro.load(PropertiesUtil.class.getClassLoader().getResourceAsStream("UploadConfig.properties"));
			return pro.getProperty(key)!=null?pro.getProperty(key):null;
		} catch (IOException e) {
			log.error("###>> 获取"+key+"的配置项失败...");
			e.printStackTrace();
			return null;
		}
	}
}
