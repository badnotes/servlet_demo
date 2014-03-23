package com.test.demo.utils;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: wanjun
 * Date: 7/11/13
 * Time: 10:23 AM
 */
public class StringUtils {

    public static char[] numberAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" +
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();

    public static String randomString(int length){
        if(length < 1){
            return null;
        }
        char[] randomBuffer = new char[length];
        for(int i = 0;i < randomBuffer.length; i++){
            randomBuffer[i] = numberAndLetters[new Random().nextInt(71)];
        }
        return new String(randomBuffer);
    }

    public static boolean hasText(String user) {
        return !(user == null || "".equals(user.trim()));
    }
}
