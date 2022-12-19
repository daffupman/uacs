package io.daff.uacs.service.util;

import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author daff
 * @since 2021/11/15
 */
public class Md5Util {

    public static String encode(String str) {
        if (StringUtils.isEmpty(str)) {
            return "";
        }
        byte[] digest;
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            digest  = md5.digest(str.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
        //16是表示转换为16进制数
        return new BigInteger(1, digest).toString(16);
    }
}
