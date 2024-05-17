package com.kodebloc.hospitalmanagementproject.utility;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.util.Base64;

public class SecurityUtils {
    private static final String KEY_ALIAS = "hospital_key";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    public static void generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);
        keyGenerator.init(
                new KeyGenParameterSpec.Builder(KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build());
        keyGenerator.generateKey();
    }

    private static SecretKey getSecretKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);
        return ((KeyStore.SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, null)).getSecretKey();
    }

    public static String encryptData(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
        byte[] encryptionIv = cipher.getIV();
        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        byte[] combined = new byte[encryptionIv.length + encryptedData.length];
        System.arraycopy(encryptionIv, 0, combined, 0, encryptionIv.length);
        System.arraycopy(encryptedData, 0, combined, encryptionIv.length, encryptedData.length);
        return Base64.getEncoder().encodeToString(combined);
    }

    public static String decryptData(String encryptedData) throws Exception {
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec spec = new GCMParameterSpec(128, decodedData, 0, 12);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec);
        byte[] decryptedData = cipher.doFinal(decodedData, 12, decodedData.length - 12);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }
}
