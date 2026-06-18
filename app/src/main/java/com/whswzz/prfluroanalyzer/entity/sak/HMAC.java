package com.whswzz.prfluroanalyzer.entity.sak;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import top.jemen.utils.LogUtil;

public class HMAC {
    /**
     * HMAC_SHA1 签名
     * @param content 待签名内容
     * @param key 签名Key
     * @return
     */
    public static String hmacsha1(String content, String key) {
//        LogUtil.d("hmacsha1 content: " + content + " key: " + key);
        return getHmacSign(content, "UTF-8", key, "HmacSHA1");
    }
 
    /**
     * MAC签名
     * @param content 待签名内容
     * @param charset 编码
     * @param key 签名Key
     * @param hamaAlgorithm Mac算法
     * @return
     */
    public static String getHmacSign(String content, String charset, String key, String hamaAlgorithm) {
        String result = null;
        try {
            //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
            SecretKeySpec signinKey = new SecretKeySpec(key.getBytes(charset), hamaAlgorithm);
            //生成一个指定 Mac 算法 的 Mac 对象
            Mac mac = Mac.getInstance(hamaAlgorithm);
            //用给定密钥初始化 Mac 对象
            mac.init(signinKey);
            //完成 Mac 操作
            byte[] rawHmac = mac.doFinal(content.getBytes(charset));
            result = toHexString(rawHmac);
 
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        } catch (InvalidKeyException e) {
            System.err.println(e.getMessage());
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (null != result) {
            return result;
        } else {
            return null;
        }
    }
 
    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
 
}
