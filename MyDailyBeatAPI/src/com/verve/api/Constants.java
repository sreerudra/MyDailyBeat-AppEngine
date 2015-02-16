package com.verve.api;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Constants {
	
	public static final String DO_NOT_REPLY_PREFIX = "DO NOT REPLY: ";
	public static final String DOMAIN_URL = "mydailybeat.com";
	public static final String DEV_EMAIL = "evervecorp@gmail.com";
	public static final String DEV_PASS = "Verve1sLife";
	public static final String DEV_NUMBER = "+12013545720";
	public static final String DEV_SID = "AC30ba74806ea7f7c244afa4f5099d968c";
	public static final String DEV_AUTHTKN = "254e3747b67e2f47eb5a4e6d53c587ce";
	
	public static String generateMD5(String to_be_md5) {
	    String md5_sum = "";
	    //If the provided String is null, then throw an Exception.
	    if (to_be_md5 == null) {
	        throw new RuntimeException("There is no string to calculate a MD5 hash from.");
	    }
	    try {
	        MessageDigest md = MessageDigest.getInstance("MD5");
	        byte[] array = md.digest(to_be_md5.getBytes("UTF-8"));
	        StringBuffer collector = new StringBuffer();
	        for (int i = 0; i < array.length; ++i) {
	            collector.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
	        }
	        md5_sum = collector.toString();
	    }//end try
	    catch (NoSuchAlgorithmException e) {
	        throw new RuntimeException("Could not find a MD5 instance: " + e.getMessage());
	    }
	    catch (UnsupportedEncodingException e) {
	        throw new RuntimeException("Could not translate UTF-8: " + e.getMessage());
	    }
	    return md5_sum;
	}//end generateMD5

}
