package com.zzj.miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;

public class
MD5Util {

    //对明文字符串做MD5
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    //鉴于安全起见，不直接把密码进行MD5，而是和salt进行一定规则的拼装
    private static final String salt = "1a2b3c4d";

    //第一次MD5
    public static String inputPassFormPass(String inputPass){

        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPass +
                salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }


    //第二次MD5
    public static String formPassDBPass(String formPass, String salt){

        String str = "" + salt.charAt(0) + salt.charAt(2) + formPass +
                salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }


    public static String inputPassDBPass(String input, String saltDB){
        String formPass = inputPassFormPass(input);
        String dbPass = formPassDBPass(formPass, saltDB);
        return dbPass;
    }



    public static void main(String[] args) {
        System.out.println(inputPassDBPass("123456", "1a2b3c4d"));
    }
}
