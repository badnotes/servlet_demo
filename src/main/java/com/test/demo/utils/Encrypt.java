package com.test.demo.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encrypt {

	private static final String ALGORITHM = "SHA-256";

	/**
	 * string to md5
	 */
	public static String md5(String s) {
		byte[] unencodedPassword = null;
		try {
			unencodedPassword = s.getBytes("UTF-8");
		} catch (Exception e1) {
		}
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			return s;
		}
		if (null == md){
			return null;
		}
		md.reset();
		md.update(unencodedPassword);
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

	/**
	 * string to sha256
	 */
	public static String SHA256(String str){
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		byte[] data = str.getBytes();
        if(data == null || data.length == 0){
            return  null;
        }
		md.update(data);
		byte[] digest = md.digest();
		StringBuffer sb = new StringBuffer();
		// 按位与是为了把字节转整时候取其正确的整数，java中一个int是4个字节
        // 如果digest[i]最高位为1，则转为int时，int的前三个字节都被1填充了
		for (int i = 0; i < digest.length; i++) {
			String tmp = Integer.toHexString(digest[i] & 0xff);
			if(tmp.length() == 1){
				sb.append("0");
			}
			sb.append(tmp);
		}
		return sb.toString();
	}

    public static void main (String[] args){
        System.out.println(Encrypt.SHA256("abcdef7b2fbc56"));
    }

}
