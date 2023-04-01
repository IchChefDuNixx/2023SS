package de.fhro.inf.its.uebung3;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class DecryptAesGcm {
    private static final String CIPHER_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12; // 96 bit

    private DecryptAesGcm() {
    }

    /**
     * decrypt text with AES and GCM
     *
     * @param cipherText         IV and ciphertext
     * @param authenticationData additional authentication data used in GCM mode
     * @param key                symmetric AES key
     * @return plaintext
     */
    public static byte[] decrypt(byte[] cipherText, byte[] authenticationData, SecretKey key)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {

        Cipher cipher = Cipher.getInstance(CIPHER_ALGO);

        // byte[] iv = Arrays.copyOfRange(cipherText, 0, IV_LENGTH_BYTE);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BIT, cipherText, 0, IV_LENGTH_BYTE));
        cipher.updateAAD(authenticationData);

        byte[] cText = Arrays.copyOfRange(cipherText, IV_LENGTH_BYTE, cipherText.length);

        return cipher.doFinal(cText);
    }
}
