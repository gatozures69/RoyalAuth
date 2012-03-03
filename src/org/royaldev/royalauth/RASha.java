package org.royaldev.royalauth;

import java.security.MessageDigest;

public class RASha {
    
    private static String hash(String data) throws Exception {

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(data.getBytes());

        byte byteData[] = md.digest();

        StringBuilder sb = new StringBuilder();
        for (byte aByteData : byteData) {
            sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
    
    public static String encrypt(String data) throws Exception {

        for (int i = 0; i < 25; i++) {
            data = hash(data);
        }

        return data;
    }
}
