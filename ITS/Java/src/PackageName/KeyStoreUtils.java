package PackageName;

import javax.crypto.SecretKey;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    public KeyStoreUtils(char[] keyStorePasswd)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        // TODO use readKeystore: done
        keyStore = readKeyStore(null, keyStorePasswd);
    }

    /**
     * Read KeyStore from file.
     *
     * @param keyStoreFile   filename of KeyStore
     * @param keyStorePasswd password used to encrypt KeyStore
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    public KeyStoreUtils(String keyStoreFile, char[] keyStorePasswd)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        // TODO use readKeystore: done
        InputStream in = new FileInputStream(keyStoreFile);
        keyStore = readKeyStore(in, keyStorePasswd);
    }

    /**
     * Create new KeyStore and load from InputStream or null.
     *
     * @param in               InputStream to read from or null to create new one
     * @param keyStorePassword password used to encrypt KeyStore
     * @return loaded or created KeyStore
     * @throws KeyStoreException
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     */
    private static KeyStore readKeyStore(InputStream in, char[] keyStorePassword)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        // TODO: done
        KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
        ks.load(in, keyStorePassword);
        return ks;
    }

    /**
     * Write KeyStore to given file.
     *
     * @param keyStoreFile   destination filename
     * @param keyStorePasswd password used to encrypt KeyStore
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    public void writeKeyStoreToFile(String keyStoreFile, char[] keyStorePasswd)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        // TODO: done
        OutputStream out = new FileOutputStream(keyStoreFile);
        keyStore.store(out, keyStorePasswd);
    }

    /**
     * Add a key to the KeyStore.
     *
     * @param key         secret key
     * @param keyAlias    alias for key
     * @param keyPassword password for key
     * @throws KeyStoreException
     */
    public void addKey(SecretKey key, String keyAlias, char[] keyPassword) throws KeyStoreException {
        // TODO:
        KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(key);
        KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(keyPassword);
        keyStore.setEntry(keyAlias, skEntry, protParam);
    }

    /**
     * Read a key from the KeyStore.
     *
     * @param keyAlias    alias for key (given in addKey method)
     * @param keyPassword password for key
     * @return SecretKey previously stored with keyAlias
     * @throws KeyStoreException
     * @throws UnrecoverableEntryException
     * @throws NoSuchAlgorithmException
     */
    public SecretKey getKey(String keyAlias, char[] keyPassword)
            throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException {
        // TODO
        KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(keyPassword);
        KeyStore.SecretKeyEntry skEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(keyAlias, protParam);
        SecretKey secretKey = skEntry.getSecretKey();
        return secretKey;
    }
}
