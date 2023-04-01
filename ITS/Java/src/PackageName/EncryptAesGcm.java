package PackageName;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class EncryptAesGcm {
    private static final String CIPHER_ALG = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128; // 16 Byte
    private static final int IV_LENGTH_BYTE = 12; // 96 bit

    private EncryptAesGcm() throws NoSuchAlgorithmException {
    }

    /**
     * Encrypt data with AES and GCM.
     *
     * @param plainText          text to encrypt
     * @param authenticationData additional authentication data used in GCM mode
     * @param key                symmetric AES key
     * @return encrypted data and IV
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws IOException
     */
    public static byte[] encrypt(byte[] plainText, byte[] authenticationData, SecretKey key)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, InvalidAlgorithmParameterException, IOException {
        // TODO: done
        Cipher cipher = Cipher.getInstance(CIPHER_ALG);

        byte[] iv = CryptoUtils.getRandomNonce(IV_LENGTH_BYTE);
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
        cipher.updateAAD(authenticationData);
        byte[] encrypted = cipher.doFinal(plainText);

        return CryptoUtils.concatenateIvAndCipherText(iv, encrypted);
    }
}
