package de.fhro.inf.its.uebung3;

import javax.crypto.SecretKey;

public class StreamEncryption {

    private static final String IV_FILE = "iv.bin";
    private static final String CIPHER_ALG = "AES/GCM/NoPadding";

    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;  // 96 bit

    /**
     * Encrypt the data.
     * Data is read, encrypted and encoded Base64 by nested streams.
     *
     * @param inputFile file data
     * @param encFile   file with encrypted data
     * @param key       symmetric AES key
     */
    public static void encrypt(String inputFile, String encFile, SecretKey key) {
        // TODO
    }
}
