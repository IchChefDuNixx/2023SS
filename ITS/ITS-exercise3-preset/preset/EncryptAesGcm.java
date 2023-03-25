package de.fhro.inf.its.uebung3;

import javax.crypto.SecretKey;

public class EncryptAesGcm {

    private static final String CIPHER_ALG = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;  // 16 Byte
    private static final int IV_LENGTH_BYTE = 12;  // 96 bit

    private EncryptAesGcm() {
    }

    /**
     * Encrypt data with AES and GCM.
     *
     * @param plainText          text to encrypt
     * @param authenticationData additional authentication data used in GCM mode
     * @param key                symmetric AES key
     * @return encrypted data and IV
     */
    public static byte[] encrypt(byte[] plainText, byte[] authenticationData, SecretKey key) {
        // TODO

        return null;
    }
}
