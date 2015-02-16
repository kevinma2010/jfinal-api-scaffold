package com.mlongbo.jfinal.common.utils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 短信相关的工具类*
 * @author malongbo
 */
public class SMSUtils {


    /**
     * 检测手机号有效性*
     * @param mobile 手机号码
     * @return 是否有效
     */
    public static final boolean isMobileNo(String mobile){
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobile);
        return m.matches();
    }
    
    /**
     * 生成短信验证码*
     * @param length 长度
     * @return 指定长度的随机短信验证码
     */
    public static final String randomSMSCode(int length) {
        boolean numberFlag = true;
        String retStr = "";
        String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
        int len = strTable.length();
        boolean bDone = true;
        do {
            retStr = "";
            int count = 0;
            for (int i = 0; i < length; i++) {
                double dblR = Math.random() * len;
                int intR = (int) Math.floor(dblR);
                char c = strTable.charAt(intR);
                if (('0' <= c) && (c <= '9')) {
                    count++;
                }
                retStr += strTable.charAt(intR);
            }
            if (count >= 2) {
                bDone = false;
            }
        } while (bDone);
        return retStr;
    }

    /**
     * 发送短信验证码*
     * @param mobile 手机号码
     * @param code 验证码
     * @return 是否发送成功
     */
    public static final boolean sendCode(String mobile, String code) {
        
        //todo 这里实现短信发送功能

        return true;
    }
}
