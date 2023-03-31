package PackageName;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import javax.crypto.*;
import java.nio.file.Files;
import java.nio.file.Paths;

class CryptoTest {

    private static final String TEST_DIRECTORY = "C:\\Users\\Felix\\Desktop\\auf-zu-win-11\\th\\2023SS\\ITS\\Java\\src\\PackageName\\";

    @Test
    void testGenerateKey() throws NoSuchAlgorithmException {
        SecretKey key = Encryption.generateKey();
        System.out.print(key.getEncoded());
        assertNotNull(key);
        assertEquals("AES", key.getAlgorithm());
        assertEquals(32, key.getEncoded().length);
    }

    @Test
    void testEncryptDecrypt() throws Exception {
        // Generate a key
        SecretKey key = Encryption.generateKey();

        // Encrypt some data
        byte[] plaintext = Files.readAllBytes(Paths.get(TEST_DIRECTORY + "input.txt"));
        byte[] ciphertext = Encryption.encrypt(key, plaintext);
        assertNotEquals(plaintext, ciphertext);
        FileUtils.writeToFileBase64(TEST_DIRECTORY + "testCipher.txt", ciphertext);

        // Decrypt the data
        byte[] ciphertext_hopefully = FileUtils.readFromFileBase64(TEST_DIRECTORY + "testCipher.txt");
        byte[] plaintext_hopefully = Decryption.decrypt(key, ciphertext_hopefully);
        assertEquals(new String(plaintext), new String(plaintext_hopefully));

    }
}