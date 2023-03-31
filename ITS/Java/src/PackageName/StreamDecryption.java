package PackageName;

import javax.crypto.SecretKey;

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
    public static void decrypt(String inputFileName, String outputFileName, SecretKey key) {
        // TODO
    }
}
xx