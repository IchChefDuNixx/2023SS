import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Decryption {

    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String ALGORITHM = "AES";

    private Decryption() {
    }

    /**
     * Read symmetric key from file and decode it base64.
     *
     * @param inputFile filename
     * @return symmetric SecretKey
     */
    public static SecretKey readKey(String inputFile) throws IOException {
        return new SecretKeySpec(FileUtils.readFromFileBase64(inputFile), ALGORITHM);
    }

    /**
     * Decrypt data with given key.
     *
     * @param key  SecretKey, read with readKey
     * @param data byte Array to decrypt
     * @return decrypted data
     */
    public static byte[] decrypt(SecretKey key, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(data);
    }
}