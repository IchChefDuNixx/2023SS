package PackageName;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class StreamEncryption {

    private static final String IV_FILE = "iv.bin";
    // "C:\\Users\\Felix\\AppData\\Roaming\\Code\\User\\workspaceStorage\\02bad5ee48ecee333be48b01accba756\\redhat.java\\jdt_ws\\iv.bin";
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
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws IOException
     */
    public static void encrypt(String inputFile, String encFile, SecretKey key) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IOException {
        // TODO:
        System.out.println(System.getProperty("user.dir"));
        Cipher cipher = Cipher.getInstance(CIPHER_ALG);
        byte[] iv = CryptoUtils.getRandomNonce(IV_LENGTH_BYTE);
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

        FileOutputStream fos = new FileOutputStream(IV_FILE);
        fos.write(iv);
        fos.close();

        FileInputStream in = new FileInputStream(inputFile);
        FileOutputStream out = new FileOutputStream(encFile);
        OutputStream base64out = Base64.getEncoder().wrap(out);
        CipherOutputStream cipherout = new CipherOutputStream(base64out, cipher);

        int numRead = 0;
        byte[] buffer = new byte[1024];
        while ((numRead = in.read(buffer)) != -1) {
            cipherout.write(buffer, 0, numRead);
        }

        in.close();
        cipherout.close();
    }
}
