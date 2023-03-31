package PackageName;

import javax.crypto.*;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
//import java.security.SecureRandom;

public class Encryption {

    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;

    private Encryption() {
    }

    /**
     * Generate a symmetric key with selected algorithm and key size.
     *
     * @return symmetric SecretKey
     */
    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keygen = KeyGenerator.getInstance(ALGORITHM);
        keygen.init(KEY_SIZE);

        return keygen.generateKey();
    }

    /**
     * Encrypt data with given key.
     *
     * @param key  SecretKey, generated with generateKey
     * @param data byte Array to encrypt
     * @return encrypted data
     */
    public static byte[] encrypt(SecretKey key, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(data); // .update for bigger data
    }

    /**
     * Save key base64 encoded in a file.
     *
     * @param destFile destination filename
     * @param key      SecretKey to save
     */
    public static void saveKey(String destFile, SecretKey key) throws IOException {
        // TODO: WIP, writeToFile?
        FileUtils.writeToFileBase64(destFile, key.getEncoded());

    }
}