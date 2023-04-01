package PackageName;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class StreamDecryption {

    private static final String IV_FILE = "iv.bin";
    private static final String CIPHER_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12; // 96 bit

    private StreamDecryption() {
    }

    /**
     * Decrypt the data.
     * Data is read, decoded Base64 and decrypted by nested streams.
     *
     * @param inputFileName  encrypted filename
     * @param outputFileName decrypted filename
     * @param key            symmetric AES key
     * @throws IOException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     */
    public static void decrypt(String inputFileName, String outputFileName, SecretKey key) throws IOException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        // TODO
        byte[] iv = new byte[IV_LENGTH_BYTE];
        FileInputStream fis = new FileInputStream(IV_FILE);
        fis.read(iv);
        fis.close();

        Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

        FileInputStream fis2 = new FileInputStream(inputFileName);
        InputStream base64in = Base64.getDecoder().wrap(fis2);
        CipherInputStream cipherin = new CipherInputStream(base64in, cipher);
        FileOutputStream fos = new FileOutputStream(outputFileName);

        int numRead;
        byte[] buffer = new byte[1024];
        while ((numRead = cipherin.read(buffer)) != -1) {
            fos.write(buffer, 0, numRead);
        }

        cipherin.close();
        fos.close();
    }
}