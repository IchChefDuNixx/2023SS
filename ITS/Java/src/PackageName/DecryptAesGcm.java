package PackageName;

import javax.crypto.SecretKey;

public class DecryptAesGcm {
    private static final String CIPHER_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12; // 96 bit

    private DecryptAesGcm() {
    }

    /**
     * decrypt text with AES and GCM
     *
     * @param cipherText         ciphertext and IV
     * @param authenticationData additional authentication data used in GCM mode
     * @param key                symmetric AES key
     * @return plaintext
     */
    public static byte[] decrypt(byte[] cipherText, byte[] authenticationData, SecretKey key) {
        // TODO

        return null;
    }
}
