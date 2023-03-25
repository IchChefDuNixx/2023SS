package de.fhro.inf.its.uebung3;

import javax.crypto.SecretKey;
import java.io.InputStream;
import java.security.KeyStore;

public class KeyStoreUtils {
    private static final String KEYSTORE_TYPE = "PKCS12";

    private final KeyStore keyStore;

    /**
     * Create new empty KeyStore.
     *
     * @param keyStorePasswd password used to encrypt KeyStore
     */
    public KeyStoreUtils(char[] keyStorePasswd) {
        // TODO use readKeystore
    }

    /**
     * Read KeyStore from file.
     *
     * @param keyStoreFile   filename of KeyStore
     * @param keyStorePasswd password used to encrypt KeyStore
     */
    public KeyStoreUtils(String keyStoreFile, char[] keyStorePasswd) {
        // TODO use readKeystore
    }

    /**
     * Create new KeyStore and load from InputStream or null.
     *
     * @param in               InputStream to read from or null to create new one
     * @param keyStorePassword password used to encrypt KeyStore
     * @return loaded or created KeyStore
     */
    private static KeyStore readKeyStore(InputStream in, char[] keyStorePassword) {
        // TODO

        return null;
    }

    /**
     * Write KeyStore to given file.
     *
     * @param keyStoreFile   destination filename
     * @param keyStorePasswd password used to encrypt KeyStore
     */
    public void writeKeyStoreToFile(String keyStoreFile, char[] keyStorePasswd) {
        // TODO
    }

    /**
     * Add a key to the KeyStore.
     *
     * @param key         secret key
     * @param keyAlias    alias for key
     * @param keyPassword password for key
     */
    public void addKey(SecretKey key, String keyAlias, char[] keyPassword) {
        // TODO
    }

    /**
     * Read a key from the KeyStore.
     *
     * @param keyAlias    alias for key (given in addKey method)
     * @param keyPassword password for key
     * @return SecretKey previously stored with keyAlias
     */
    public SecretKey getKey(String keyAlias, char[] keyPassword) {
        // TODO

        return null;
    }
}
