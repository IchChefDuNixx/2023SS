package PackageName;

// import PackageName.KeyStoreUtils;
// import PackageName.CryptoUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.fail;

public class TestKeyStoreUtils {
    private static final String KEYSTORE_FILE = "keystore.dat";
    private static final String KEY_ALIAS = "PrivateKey";

    // Passwords for Keystore and Key
    // ATTENTION: in real applications DO NOT WRITE PASSWORDS OR SECRETS IN SOURCE
    // CODE,
    // they must be passed to the application in a secure way
    private static final char[] KEY_PASSWORD = "Password".toCharArray();
    private static final char[] KEYSTORE_PASSWORD = "test".toCharArray();

    private static final Logger logger = Logger.getLogger(TestAesGcmEncryption.class.getName());

    @Test
    public void testKeyStore() {
        createAndWriteKeystore();
        addKeyToKeystore();
        readKeystoreAndGetKey();
    }

    /**
     * Create an empty keystore
     */
    private void createAndWriteKeystore() {
        try {
            var keyStore = new KeyStoreUtils(KEYSTORE_PASSWORD);
            keyStore.writeKeyStoreToFile(KEYSTORE_FILE, KEYSTORE_PASSWORD);
        } catch (Exception e) {
            logger.severe("Error when creating KeyStore");
            logger.severe(e.toString());
            fail();
        }
    }

    /**
     * Generate an AES Key and store it encrypted by an password in a keystore
     * Store the keystore protected by an password
     */
    private void addKeyToKeystore() {
        try {
            var keyStore = new KeyStoreUtils(KEYSTORE_FILE, KEYSTORE_PASSWORD);
            SecretKey key = CryptoUtils.generateAESKey();
            keyStore.addKey(key, KEY_ALIAS, KEY_PASSWORD);
            keyStore.writeKeyStoreToFile(KEYSTORE_FILE, KEYSTORE_PASSWORD);
        } catch (Exception e) {
            logger.severe("Error when adding Key");
            logger.severe(e.toString());
            fail();
        }
    }

    /**
     * Open a password protected keystore and read a password encrypted key
     */
    private void readKeystoreAndGetKey() {
        try {
            var keyStore = new KeyStoreUtils(KEYSTORE_FILE, KEYSTORE_PASSWORD);
            SecretKey key = keyStore.getKey(KEY_ALIAS, KEY_PASSWORD);
            Assertions.assertEquals(key.getAlgorithm(), "AES");
        } catch (Exception e) {
            logger.severe("Error when reading Key");
            logger.severe(e.toString());
            fail();
        }
    }
};
