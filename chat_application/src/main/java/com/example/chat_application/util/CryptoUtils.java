package com.example.chat_application.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class CryptoUtils {

    /* ===============================
       🔐 AES KEY GENERATION
       =============================== */
    public static SecretKey generateAESKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // AES-256
            return keyGen.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate AES key", e);
        }
    }

    /* ===============================
       🔑 IMPORT RSA PUBLIC KEY
       =============================== */
    public static PublicKey importPublicKey(String base64PublicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to import public key", e);
        }
    }

    /* ===============================
       🔐 ENCRYPT AES KEY WITH RSA
       =============================== */
    public static String encryptAESKeyWithRSA(
            SecretKey aesKey,
            PublicKey rsaPublicKey
    ) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
            byte[] encryptedKey = cipher.doFinal(aesKey.getEncoded());
            return Base64.getEncoder().encodeToString(encryptedKey);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt AES key with RSA", e);
        }
    }

    /* ===============================
       🔓 (OPTIONAL) DECRYPT AES KEY
       =============================== */
    public static SecretKey decryptAESKeyWithRSA(
            String encryptedKeyBase64,
            PrivateKey privateKey
    ) {
        try {
            byte[] encryptedKey = Base64.getDecoder().decode(encryptedKeyBase64);
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedKey = cipher.doFinal(encryptedKey);
            return new SecretKeySpec(decryptedKey, "AES");
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt AES key", e);
        }
    }
}
