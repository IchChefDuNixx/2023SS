package de.fhro.inf.its.uebung3;

import de.fhro.inf.its.uebung3.utils.CryptoUtils;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class EncryptAesGcm {

    private static final String CIPHER_ALG = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128; // 16 Byte
    private static final int IV_LENGTH_BYTE = 12; // 96 bit

    private EncryptAesGcm() {
    }

    /**
     * Encrypt data with AES and GCM.
     *
     * @param plainText          text to encrypt
     * @param authenticationData additional authentication data used in GCM mode
     * @param key                symmetric AES key
     * @return IV and encrypted data
     */
    public static byte[] encrypt(byte[] plainText, byte[] authenticationData, SecretKey key)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException,
            IOException {

        Cipher cipher = Cipher.getInstance(CIPHER_ALG);

        byte[] iv = CryptoUtils.getRandomNonce(IV_LENGTH_BYTE); // provide initialization vector for GCM mode
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
        cipher.updateAAD(authenticationData);

        byte[] encryptedText = cipher.doFinal(plainText);

        return CryptoUtils.concatenateIvAndCipherText(iv, encryptedText);
    }
}
