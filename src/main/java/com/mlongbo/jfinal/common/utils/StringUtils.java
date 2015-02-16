package com.mlongbo.jfinal.common.utils;

import java.security.MessageDigest;

/**
 * 字符串操作工具
 * @author malongbo
 */
public final class StringUtils {

    /**
     * 判断字符串是否为空
     * @param str
     * @return
     */
    public static boolean isEmpty (String str) {
        if (str == null)
            return true;

        if ("".equals(str.trim()))
            return true;

        return false;
    }

    /**
     * 判断字符串是否不为空
     * @param str
     * @return
     */
    public static boolean isNotEmpty (String str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否为空字符串
     * @param str
     * @return
     */
    public static boolean isBlank (String str) {
        return "".equals(str.trim());
    }

    /**
     * 判断字符串是否为非空字符串
     * @param str
     * @return
     */
    public static boolean isNotBlank (String str) {
        return !isBlank(str);
    }


    /**
     * 将字符串转为boolean类型
     * @param value
     * @param defaultValue  设置默认值，默认使用false
     * @return
     */
    public static Boolean toBoolean(String value, boolean defaultValue) {
        if (isEmpty(value))
            return defaultValue;

        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 将字符串转为boolean类型
     * @param value
     * @return
     */
    public static Boolean toBoolean(String value) {
        return toBoolean(value, false);
    }

    /**
     * 将字符串转为long类型
     * @param value
     * @param defaultValue 设置默认值
     * @return
     */
    public static Long toLong(String value, Long defaultValue) {
        if (isEmpty(value))
            return defaultValue;
        try {
            return Long.parseLong(value);
        }catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 将字符串转为int类型
     * @param value
     * @param defaultValue 设置默认值
     * @return
     */
    public static Integer toInt(String value, Integer defaultValue) {
        if (isEmpty(value))
            return defaultValue;
        try {
            return Integer.parseInt(value);
        }catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 将字符串转为double类型
     * @param value
     * @param defaultValue
     * @return
     */
    public static Double toDouble(String value, Double defaultValue) {
        if (isEmpty(value))
            return defaultValue;
        try {
            return Double.parseDouble(value);
        }catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 将字符串转为float类型
     * @param value
     * @param defaultValue
     * @return
     */
    public static Float toFloat(String value, Float defaultValue) {
        if (isEmpty(value))
            return defaultValue;
        try {
            return Float.parseFloat(value);
        }catch (Exception e) {
            return defaultValue;
        }
    }
    /**
     * 将数组值按英文逗号拼接成字符串
     * @param array
     * @return
     */
    public static String join(Object[] array) {
        return join(array, ",","");
    }
    
    /**
     * 将数组值按指定分隔符拼接成字符串
     * @param array
     * @param delimiter 分割符，默认使用英文逗号
     * @return
     */
    public static String join(Object[] array, String delimiter) {

        return join(array, delimiter,"");
    }

    /**
     *  将数组值按指定分隔符拼接成字符串
     *       <br></br>
     *      <b>示例</b>： <br></br>
     *      array等于new String[]{"a","b"} <br></br>
     *      delimiter等于, <br></br>
     *      surround等于' <br></br>
     *      转换结果为：'a','b'
     * @param array
     * @param delimiter 分割符，默认使用英文逗号
     * @param surround 每个值左右符号，默认无
     * @return
     */
    public static String join(Object[] array, String delimiter, String surround) {
        if (array == null)
            throw new IllegalArgumentException("Array can not be null");

        if (array.length == 0) return "";

        if (surround == null) surround = "";

        if (delimiter == null) surround = ",";

        StringBuffer buffer = new StringBuffer();

        for (Object item : array) {
           buffer.append(surround).append(item.toString()).append(surround).append(delimiter);
        }

        buffer.delete(buffer.length() - delimiter.length(), buffer.length());

        return buffer.toString();
    }

    /**
     * Encode a string using algorithm specified in web.xml and return the
     * resulting encrypted password. If exception, the plain credentials
     * string is returned
     *
     * @param password Password or other credentials to use in authenticating
     *        this username
     * @param algorithm Algorithm used to do the digest
     *
     * @return encypted password based on the algorithm.
     */
    public static String encodePassword(String password, String algorithm) {
        byte[] unencodedPassword = password.getBytes();

        MessageDigest md = null;

        try {
            // first create an instance, given the provider
            md = MessageDigest.getInstance(algorithm);
        } catch (Exception e) {
            return password;
        }

        md.reset();

        // call the update method one or more times
        // (useful when you don't know the size of your data, eg. stream)
        md.update(unencodedPassword);

        // now calculate the hash
        byte[] encodedPassword = md.digest();

        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < encodedPassword.length; i++) {
            if ((encodedPassword[i] & 0xff) < 0x10) {
                buf.append("0");
            }

            buf.append(Long.toString(encodedPassword[i] & 0xff, 16));
        }

        return buf.toString();
    }
}
