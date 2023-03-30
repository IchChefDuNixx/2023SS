import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class CryptoUtils {
    private static final int KEY_SIZE = 256;

    private static final SecureRandom rand = new SecureRandom();

    private CryptoUtils() {
    }

    /**
     * Generate a random byte array.
     *
     * @param numBytes length of byte array
     * @return byte array filled with random values
     */
    public static byte[] getRandomNonce(int numBytes) {
        byte[] nonce = new byte[numBytes];
        CryptoUtils.rand.nextBytes(nonce);

        return nonce;
    }

    /**
     * Generate an AES symmetric key with 256 bit length.
     *
     * @return AES key
     */
    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(KEY_SIZE, CryptoUtils.rand);

        return keyGen.generateKey();
    }

    /**
     * Concatenate IV and ciphertext.
     *
     * @return new byte Array with IV and ciphertext
     */
    public static byte[] concatenateIvAndCipherText(byte[] iv, byte[] cipherText) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        output.write(iv);
        output.write(cipherText);

        return output.toByteArray();
    }
}
