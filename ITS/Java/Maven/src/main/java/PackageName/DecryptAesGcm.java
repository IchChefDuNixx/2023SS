package PackageName;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

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
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] decrypt(byte[] cipherText, byte[] authenticationData, SecretKey key)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        // TODO
        Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BIT, cipherText, 0, IV_LENGTH_BYTE));
        cipher.updateAAD(authenticationData);

        byte[] cipher_to_array = Arrays.copyOfRange(cipherText, IV_LENGTH_BYTE, cipherText.length);

        return cipher.doFinal(cipher_to_array);
    }
}
