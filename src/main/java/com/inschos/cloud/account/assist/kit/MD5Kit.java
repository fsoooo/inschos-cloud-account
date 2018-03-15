package com.inschos.cloud.account.assist.kit;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Kit {
    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static String toHexString(byte[] b) {
        // String to byte
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    public static byte[] MD5Digest(byte[] input) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (md != null && input != null) {
            md.update(input);
            return md.digest();
        } else
            return null;
    }

    public static String MD5Digest(String input) {
        if (input != null) {
            byte[] b = MD5Digest(input.getBytes());
            if (b != null) {
                return toHexString(b);
            }
        }
        return null;
    }
}
