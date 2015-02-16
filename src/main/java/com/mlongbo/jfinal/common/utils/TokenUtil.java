package com.mlongbo.jfinal.common.utils;

/**
 * @author malongbo
 * @date 15-1-18
 * @package com.pet.project.common.token
 */
public class TokenUtil {
    /**
     * 生成token号码
     * @return token号码
     */
    public static String generateToken() {
        return RandomUtils.randomCustomUUID().concat(RandomUtils.randomString(6));
    }
}
