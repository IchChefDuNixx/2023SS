package PackageName;

import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

class TestEncryption {
    private static final String INPUT_FILENAME = "input.txt";
    private static final String ENC_FILENAME = "cipher.txt";
    private static final String KEY_FILENAME = "key.txt";
    private static final Logger logger = Logger.getLogger(TestEncryption.class.getName());

    // This is not a good test setup, but it is simple and easy to understand.
    // In the real world you would most likely test each method in a separate test
    // file and make some integration tests
    // in another file.
    @Test
    void testEncryptionAndDecryption() {
        System.out.print(System.getProperty("user.dir"));
        encryptData();
        decryptData();
    }

    private void encryptData() {
        try {
            logger.info("*****************************************************************");
            logger.info("Encrypt");
            logger.info("Read Text From File: " + INPUT_FILENAME);
            byte[] data = FileUtils.readFromFile(INPUT_FILENAME);

            logger.info("Generate Key");
            SecretKey key = Encryption.generateKey();

            logger.info("encrypting...");
            byte[] encryptedData = Encryption.encrypt(key, data);

            logger.info("Write Key and Encrypted Text to File");
            FileUtils.writeToFile(ENC_FILENAME, encryptedData);
            Encryption.saveKey(KEY_FILENAME, key);

        } catch (Exception e) {
            logger.severe("Error in encryption!");
            logger.severe(e.toString());

            fail();
        }
    }

    private void decryptData() {
        try {
            logger.info("***************************************************************");
            logger.info("Decrypt");
            logger.info("Load encrypt from File: " + ENC_FILENAME);
            byte[] encryptedData = FileUtils.readFromFile(ENC_FILENAME);

            logger.info("Read Key and Text from File");
            SecretKey key = Decryption.readKey(KEY_FILENAME);

            logger.info("Decrypting...");
            byte[] decryptedData = Decryption.decrypt(key, encryptedData);
            logger.info(() -> "Decrypted File: " + new String(decryptedData));

            logger.info("***************************************************************");
            byte[] originalData = Files.readAllBytes(Paths.get(INPUT_FILENAME));
            assertArrayEquals(decryptedData, originalData);
            logger.info("Operation SUCCESSFUL");

        } catch (Exception e) {
            logger.info("error in decryption");
            logger.info(e.toString());

            fail();
        }
    }
}
