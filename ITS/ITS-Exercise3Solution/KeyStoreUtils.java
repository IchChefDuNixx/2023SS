package de.fhro.inf.its.uebung3;

import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

public class KeyStoreUtils {
    private static final String KEYSTORE_TYPE = "PKCS12";

    private final KeyStore keyStore;

    /**
     * Create new empty KeyStore.
     *
     * @param keyStorePasswd password used to encrypt KeyStore
     */
    public KeyStoreUtils(char[] keyStorePasswd) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        keyStore = KeyStoreUtils.readKeyStore(null, keyStorePasswd);
    }

    /**
     * Read KeyStore from file.
     *
     * @param keyStoreFile   filename of KeyStore
     * @param keyStorePasswd password used to encrypt KeyStore
     */
    public KeyStoreUtils(String keyStoreFile, char[] keyStorePasswd) throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException {
        try (FileInputStream in = new FileInputStream(keyStoreFile)) {
            keyStore = KeyStoreUtils.readKeyStore(in, keyStorePasswd);
        }
    }

    /**
     * Create new KeyStore and load from InputStream or null.
     *
     * @param in               InputStream to read from or null to create new one
     * @param keyStorePassword password used to encrypt KeyStore
     * @return loaded or created KeyStore
     */
    private static KeyStore readKeyStore(InputStream in, char[] keyStorePassword) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        keyStore.load(in, keyStorePassword);

        return keyStore;
    }

    /**
     * Write KeyStore to given file.
     *
     * @param keyStoreFile   destination filename
     * @param keyStorePasswd password used to encrypt KeyStore
     */
    public void writeKeyStoreToFile(String keyStoreFile, char[] keyStorePasswd) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        try (FileOutputStream fos = new FileOutputStream(keyStoreFile)) {
            keyStore.store(fos, keyStorePasswd);
        }
    }

    /**
     * Add a key to the KeyStore.
     *
     * @param key         secret key
     * @param keyAlias    alias for key
     * @param keyPassword password for key
     */
    public void addKey(SecretKey key, String keyAlias, char[] keyPassword) throws KeyStoreException {

        keyStore.setKeyEntry(keyAlias, key, keyPassword, null);

        // alternative solution for storing the key
//        KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry((SecretKey) key);
//        keyStore.setEntry(keyAlias, skEntry, new KeyStore.PasswordProtection(keyPassword));
    }

    /**
     * Read a key from the KeyStore.
     *
     * @param keyAlias    alias for key (given in addKey method)
     * @param keyPassword password for key
     * @return SecretKey previously stored with keyAlias
     */
    public SecretKey getKey(String keyAlias, char[] keyPassword) throws UnrecoverableEntryException, KeyStoreException, NoSuchAlgorithmException {
        return (SecretKey) keyStore.getKey(keyAlias, keyPassword);

        // alternative solution to get key
//        KeyStore.SecretKeyEntry skEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(keyAlias, new KeyStore.PasswordProtection(keyPassword));
//        return skEntry.getSecretKey();
    }
}
