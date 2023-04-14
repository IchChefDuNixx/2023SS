package PackageName;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.logging.Logger;

public class TestDigitalSignature {
    private static final Logger logger = Logger.getLogger(TestDigitalSignature.class.getName());

    private static final String PRIVATE_KEY_FILE = "private.pkcs8";
    private static final String CERTIFICATE_FILE = "certificate.pem";
    private static final String FILE_TO_SIGN = "testFile.txt";
    // append .signature to the filename of the file we want to create a signature
    // for
    private static final String SIGNATURE_FILE = FILE_TO_SIGN + ".signature";
    private static final String PKCS7FILE = "PKCS7file.txt";

    @Test
    public void testSignature() throws Exception {
        logger.info("Message Signature------------------------------------------------------------");
        logger.info("Reading From: " + FILE_TO_SIGN);
        logger.info("Signature writing to: " + SIGNATURE_FILE);
        // signing needs private key
        PrivateKey signatureKey = CryptoUtil.getPrivateKey(PRIVATE_KEY_FILE); // version for OpenSSL generated key
        byte[] signedData = DigitalSignature.sign(FILE_TO_SIGN, signatureKey);
        Files.write(Path.of(SIGNATURE_FILE), signedData);
        logger.info("Signature creation successful");

        logger.info("Message Verify---------------------------------------------------------------");
        logger.info("Reading From: " + FILE_TO_SIGN);
        logger.info("Reading Signature from: " + SIGNATURE_FILE);

        // verification needs certificate with embedded public key
        Certificate certificate = CryptoUtil.readCertificate(CERTIFICATE_FILE);
        boolean result = false;
        try {
            result = DigitalSignature.verify(FILE_TO_SIGN, SIGNATURE_FILE, certificate);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Assertions.assertTrue(result);
        logger.info("Signature.verify()successful");

        // store the signature in PKCS#7 format
        // PKCS7Signature.createPKCS7(PKCS7FILE,certificate,signedData);

    }
}
