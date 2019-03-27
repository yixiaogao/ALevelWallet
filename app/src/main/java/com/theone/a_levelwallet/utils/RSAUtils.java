package com.theone.a_levelwallet.utils;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;


public class RSAUtils {
    private static final String strModulus = "ae1804c7582bb9a56efdebd810c5e708d1913d8cd715b1dba054fc9cb921e079dcea37f36df7189e2df618c33cd17aec92dcf229e4a4acc58550747a4c109c74a67078e033a8301366fc072df2a5048148ae713b153dff8fa22f4f183874afcd9f6f47fc6cb951c5cb1e53991f3ac2ce62b06fcee3cc826abc470fcd094a251f";
    private static final String strPublicExponent = "10001";

    // 用私鑰解密Android加密的密文，須使用"RSA/ECB/PKCS1Padding"
    private final static String RSA = "RSA";

    public static RSAPublicKey publickey;// 公鑰

    static{
        try {
            publickey = generatePublicKey(strModulus, strPublicExponent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //由模和指数创建公钥
    public static RSAPublicKey generatePublicKey(String publicModulus,
                                          String publicExponent) throws Exception {
        KeyFactory keyFac = null;
        try {
            keyFac = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException ex) {
            throw new Exception(ex.getMessage());
        }
        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(new BigInteger(
                publicModulus, 16), new BigInteger(publicExponent, 16));
        try {
            return (RSAPublicKey) keyFac.generatePublic(pubKeySpec);
        } catch (InvalidKeySpecException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    // 公鑰加密
    private static byte[] encrypt(String text, PublicKey pubRSA)
            throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, pubRSA);
        return cipher.doFinal(text.getBytes());
    }

    // 加密明文文本
    public static final String encrypt(String text) {
        try {
            return byte2hex(encrypt(text, publickey));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //字節到十六進制串轉換
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs += ("0" + stmp);
            else
                hs += stmp;
        }
        return hs.toLowerCase();
    }

    /**
     * @param args
     */
//    public static void main(String[] args) {
//        // TODO Auto-generated method stub
//        try {
//            RSAUtils rsaUtils= new RSAUtils();
//            String miwen=rsaUtils.encrypt("123456");
//            System.out.println("miwen："+miwen);
//
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

}
