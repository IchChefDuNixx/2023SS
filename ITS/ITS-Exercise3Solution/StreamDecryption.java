package de.fhro.inf.its.uebung3;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class StreamDecryption {

    private static final String IV_FILE = "iv.bin";
    private static final String CIPHER_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;  // 96 bit

    private StreamDecryption() {
    }

    /**
     * Decrypt the data.
     * Data is read, decoded Base64 and decrypted by nested streams.
     *
     * @param inputFileName  encrypted filename
     * @param outputFileName decrypted filename
     * @param key            symmetric AES key
     */
    public static void decrypt(String inputFileName, String outputFileName, SecretKey key) throws
            IOException,
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException {

        //read IV from file
        byte[] iv = new byte[IV_LENGTH_BYTE];
        try (FileInputStream fis = new FileInputStream(IV_FILE)) {
            fis.read(iv);
        }

        Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
        // init Cipher with key and IV
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

        try (
                // chaining of input streams
                FileInputStream fis = new FileInputStream(inputFileName);
                InputStream base64Is = Base64.getDecoder().wrap(fis);
                CipherInputStream cipherIs = new CipherInputStream(base64Is, cipher);
                // output stream
                FileOutputStream fos = new FileOutputStream(outputFileName);
        ) {
            // chunked reading and writing: file-in -> base64-in -> cipher-in -> file-out
            int numRead;
            byte[] buffer = new byte[1024];
            while ((numRead = cipherIs.read(buffer)) != -1) {
                // write only the number of bytes read
                fos.write(buffer, 0, numRead);
            }
        } // try-with statement closes all streams
    }
}
