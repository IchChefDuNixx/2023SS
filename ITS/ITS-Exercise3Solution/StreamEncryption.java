package de.fhro.inf.its.uebung3;

import de.fhro.inf.its.uebung3.utils.CryptoUtils;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class StreamEncryption {

    private static final String IV_FILE = "iv.bin";
    private static final String CIPHER_ALG = "AES/GCM/NoPadding";

    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12; // 96 bit

    /**
     * Encrypt the data.
     * Data is read, encrypted and encoded Base64 by nested streams.
     *
     * @param inputFile file data
     * @param encFile   file with encrypted data
     * @param key       symmetric AES key
     */
    public static void encrypt(String inputFile, String encFile, SecretKey key) throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            IOException {

        Cipher cipher = Cipher.getInstance(CIPHER_ALG);
        // get initialization vector for GCM mode
        byte[] iv = CryptoUtils.getRandomNonce(IV_LENGTH_BYTE);
        // init cipher with IV and key
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

        // save IV for decryption
        try (FileOutputStream fos = new FileOutputStream(IV_FILE)) {
            fos.write(iv);
        }

        try (
                // Input stream
                FileInputStream in = new FileInputStream(inputFile);
                // Chaining of output streams
                FileOutputStream out = new FileOutputStream(encFile);
                OutputStream base64os = Base64.getEncoder().wrap(out);
                CipherOutputStream cipherOs = new CipherOutputStream(base64os, cipher);) {
            // chunked reading and writing: File-in -> Cipher-out -> Base64-out -> File -out
            int numRead = 0;
            byte[] buffer = new byte[1024];
            while ((numRead = in.read(buffer)) != -1) {
                // write only as much as we read
                cipherOs.write(buffer, 0, numRead);
            }
        } // try-with statement closes all streams
    }
}
