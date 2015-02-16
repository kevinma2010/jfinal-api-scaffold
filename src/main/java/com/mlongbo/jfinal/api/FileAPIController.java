package com.mlongbo.jfinal.api;

import com.jfinal.aop.Before;
import com.jfinal.upload.UploadFile;
import com.mlongbo.jfinal.bean.Code;
import com.mlongbo.jfinal.bean.FileResponse;
import com.mlongbo.jfinal.common.PathConstant;
import com.mlongbo.jfinal.common.utils.FileUtils;
import com.mlongbo.jfinal.common.utils.StringUtils;
import com.mlongbo.jfinal.interceptor.TokenInterceptor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件上传总的控制器，所有文件上传类表单均拆分成文件上传和文本提交
 * 
 * 路径说明：
 * 	rootPath：当前服务器上传的根目录
 *  serverIp：服务器外网ip
 *  
 *  images：图片相对路径，用于url中
 *  imagesSavedPath：图片绝对路径，用于保存图片到本地某个位置
 *  
 *  videos
 *  videosSavedPath
 *  
 *  others
 *  othersSavedPath
 *  
 *  DefaultUserAvatar：用户默认的头像(如果有需要)
 */

@Before(TokenInterceptor.class)
public class FileAPIController extends BaseAPIController {
	
	/**
	 * 处理单文件或多文件上传，上传成功后，返回url集合
	 */
	public void upload(){
        if (!methodType("post")) {
            render404();
            return;
        }
        FileResponse response = new FileResponse();
		try {
			List<UploadFile> fileList = getFiles();//已接收到的文件
			if(fileList != null && !fileList.isEmpty()){
			    Map<String, String> urls = new HashMap<String, String>();//用于保存上传成功的文件地址
				List<String> failedFiles = new ArrayList<String>(); //用于保存未成功上传的文件名
			    String saveFilePath = "";
                //表示存放在tomcat应用目录中
                if ("1".equals(PathConstant.tomcatPath)) {
                    saveFilePath = getRequest().getSession().getServletContext().getRealPath("/");
                }
                
                for(UploadFile uploadFile : fileList){
					File file=uploadFile.getFile();
                    String urlPath = FileUtils.saveUploadFile(file, saveFilePath);
                    if (StringUtils.isEmpty(urlPath)) {
                        failedFiles.add(uploadFile.getParameterName());//标记为上传失败
                    } else {
                        //返回相对路径,用于响应
                        urls.put(uploadFile.getParameterName(), urlPath + file.getName());
                    }
				}
			    response.setDatum(urls);
			    if (failedFiles.size() > 0) {
			        response.setCode(Code.FAIL);//表示此次上传有未上传成功的文件
			        response.setFailed(failedFiles);
			    }
			}else{
				response.setCode(Code.ARGUMENT_ERROR).setMessage("uploadFileName can not be null");
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setCode(Code.ERROR);
		}
		renderJson(response);
	}
}