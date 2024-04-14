package com.group06.bsms.auth;

import com.group06.bsms.Main;
import java.util.Base64;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.mindrot.jbcrypt.BCrypt;

public class Hasher {

    public static String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    private static SecretKey getSecret(String configFile) throws Exception {
        var props = new Properties();
        props.load(Main.class.getClassLoader().getResourceAsStream(configFile));

        var secretKey = new SecretKeySpec(
                ((String) props.get("secret")).getBytes(),
                "AES"
        );

        return secretKey;
    }

    public static String encryptKey(String key) throws Exception {
        SecretKey secretKey = getSecret("env/bsms.properties");
        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(key.getBytes());

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decryptKey(String key) throws Exception {
        SecretKey secretKey = getSecret("env/bsms.properties");
        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(key));

        return new String(decryptedBytes);
    }
}
