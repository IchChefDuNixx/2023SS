package PackageName;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.fail;

public class TestStreamEncryption {
    private static final String DIRECTORY = "C:\\Users\\Felix\\Desktop\\auf-zu-win-11\\th\\2023SS\\ITS\\Java\\src\\PackageName\\";
    private static final String INPUT_FILE = DIRECTORY + "input.txt";
    private static final String ENCRYPTED_FILE = DIRECTORY + "encryptedTest.txt";
    private static final String DECRYPTED_FILE = DIRECTORY + "decryptedTest.txt";
    private static final String KEYSTORE_FILE = DIRECTORY + "keystore.dat";
    private static final String KEY_ALIAS = DIRECTORY + "PrivateKey";

    // Passwords for Keystore and Key
    // ATTENTION: in real applications DO NOT WRITE PASSWORDS OR SECRETS IN SOURCE
    // CODE,
    // they must be passed to the application by a secure way
    private static final char[] KEY_PASSWORD = "Password".toCharArray();
    private static final char[] KEYSTORE_PASSWORD = "test".toCharArray();

    private static final Logger logger = Logger.getLogger(TestStreamEncryption.class.getName());

    @Test
    public void testEncryption() {
        createAndFillKeystore();

        encryption();
        decryption();
    }

    private void createAndFillKeystore() {
        try {
            var keyStore = new KeyStoreUtils(KEYSTORE_PASSWORD);

            SecretKey key = CryptoUtils.generateAESKey();
            keyStore.addKey(key, KEY_ALIAS, KEY_PASSWORD);

            keyStore.writeKeyStoreToFile(KEYSTORE_FILE, KEYSTORE_PASSWORD);
        } catch (Exception e) {
            logger.severe("Error when creating KeyStore");
            logger.severe(e.toString());

            fail();
        }
    }

    public void encryption() {
        try {
            var keyStore = new KeyStoreUtils(KEYSTORE_FILE, KEYSTORE_PASSWORD);

            SecretKey key = keyStore.getKey(KEY_ALIAS, KEY_PASSWORD);
            StreamEncryption.encrypt(INPUT_FILE, ENCRYPTED_FILE, key);

        } catch (Exception e) {
            logger.info("Error at encryption");
            logger.info(e.getMessage());
            fail("exception at encryption");
        }
    }

    public void decryption() {
        try {
            var keyStore = new KeyStoreUtils(KEYSTORE_FILE, KEYSTORE_PASSWORD);

            SecretKey key = keyStore.getKey(KEY_ALIAS, KEY_PASSWORD);
            StreamDecryption.decrypt(ENCRYPTED_FILE, DECRYPTED_FILE, key);

        } catch (Exception e) {
            logger.info("Error at decryption");
            logger.info(e.getMessage());

            fail();
        }

        // read unencrypted file and compare with result
        logger.info("Comparing Files...");
        try (
                FileInputStream fis1 = new FileInputStream(DECRYPTED_FILE);
                FileInputStream fis2 = new FileInputStream(INPUT_FILE);) {
            byte[] data1 = new byte[1024];
            byte[] data2 = new byte[1024];
            while (fis1.read(data1) >= 0 && fis2.read(data2) >= 0) {
                Assertions.assertArrayEquals(data1, data2);
            }
        } catch (Exception e) {
            logger.info("Error when comparing decryption");
            logger.info(e.getMessage());

            fail();
        }
        logger.info("Decryption successful");
    }
}
