package com.dayflow.util;

import java.security.*;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil {
    private static final int ITERATIONS = 600_000;
    private PasswordUtil() {}
    public static String hash(String password) throws Exception {
        byte[] salt = new byte[16]; new SecureRandom().nextBytes(salt);
        byte[] hash = derive(password.toCharArray(), salt, ITERATIONS, 256);
        return "PBKDF2WithHmacSHA256$"+ITERATIONS+"$"+Base64.getEncoder().encodeToString(salt)+"$"+Base64.getEncoder().encodeToString(hash);
    }
    public static boolean verify(String password, String encoded) throws Exception {
        String[] p=encoded.split("\\$"); if(p.length!=4||!"PBKDF2WithHmacSHA256".equals(p[0])) return false;
        byte[] salt=Base64.getDecoder().decode(p[2]); byte[] expected=Base64.getDecoder().decode(p[3]);
        int iterations=Integer.parseInt(p[1]);if(iterations<1||iterations>2_000_000||salt.length<16||expected.length!=32)return false;
        byte[] actual=derive(password.toCharArray(), salt, iterations, expected.length*8);
        return MessageDigest.isEqual(expected, actual);
    }
    public static boolean needsRehash(String encoded) {try {String[] p=encoded.split("\\$");return p.length!=4||!"PBKDF2WithHmacSHA256".equals(p[0])||Integer.parseInt(p[1])<ITERATIONS;}catch(RuntimeException e){return true;}}
    private static byte[] derive(char[] pwd, byte[] salt, int iter, int bits) throws Exception { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(new PBEKeySpec(pwd,salt,iter,bits)).getEncoded(); }
}
