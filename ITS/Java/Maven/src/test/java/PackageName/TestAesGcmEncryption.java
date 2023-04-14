package PackageName;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.fail;

public class TestAesGcmEncryption {
    private static final String FILENAME = "input.txt";
    private static final String ENCRYPTED_FILE = "encryptedTest.bin";
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
    public void testEncryption() {
        createAndFillKeystore();

        encryption();
        decryption();
    }

    private void createAndFillKeystore() {
        try {
            KeyStoreUtils keyStore = new KeyStoreUtils(KEYSTORE_PASSWORD);

            SecretKey key = CryptoUtils.generateAESKey();
            keyStore.addKey(key, KEY_ALIAS, KEY_PASSWORD);

            keyStore.writeKeyStoreToFile(KEYSTORE_FILE, KEYSTORE_PASSWORD);
        } catch (Exception e) {
            logger.severe("Error when creating KeyStore");
            logger.severe(e.toString());

            fail();
        }
    }

    private void encryption() {
        try {
            KeyStoreUtils keyStore = new KeyStoreUtils(KEYSTORE_FILE, KEYSTORE_PASSWORD);

            SecretKey key = keyStore.getKey(KEY_ALIAS, KEY_PASSWORD);

            byte[] data = FileUtils.readFromFile(FILENAME);

            // Attention: Authentication data must be known at decryption and is not
            // send/stored with the encrypted data
            // if no data is provided the MAC will be calculated only for the ciphertext
            byte[] aad = "Any Authentication Data".getBytes();
            byte[] encryptedData = EncryptAesGcm.encrypt(data, aad, key);

            FileUtils.writeToFile(ENCRYPTED_FILE, encryptedData);

        } catch (Exception e) {
            logger.severe("Error in encryption");
            logger.severe(e.toString());

            fail();
        }
    }

    private void decryption() {
        try {
            KeyStoreUtils keyStore = new KeyStoreUtils(KEYSTORE_FILE, KEYSTORE_PASSWORD);

            SecretKey key = keyStore.getKey(KEY_ALIAS, KEY_PASSWORD);

            byte[] data = FileUtils.readFromFile(ENCRYPTED_FILE);

            // Authentication data is a shared secret between encrypter and decrypter and is
            // not stored with the encrypted data
            byte[] aad = "Any Authentication Data".getBytes();
            byte[] decryptedData = DecryptAesGcm.decrypt(data, aad, key);

            logger.info(() -> "Decrypted File: " + new String(decryptedData));

            byte[] fileContent = FileUtils.readFromFile(FILENAME);
            Assertions.assertArrayEquals(decryptedData, fileContent);
            logger.info("Decryption successful");

        } catch (Exception e) {
            logger.severe("Error in decryption");
            logger.severe(e.toString());

            fail();
        }
    }
}
