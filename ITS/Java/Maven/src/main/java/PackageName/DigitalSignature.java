package PackageName;

import java.security.*;
import java.security.cert.Certificate;

/**
 * Class to Create and verify a digital signature with the algorithm
 * SHA-512 as hash-function for computing the digest
 * RSA for encryption
 */
public class DigitalSignature {

    /**
     * Sign the contents of a file.
     *
     * @param filename     name of the file to be signed
     * @param signatureKey private key for signing
     * @return signature
     */
    public static byte[] sign(String filename, PrivateKey signatureKey) {
        // TODO init signature with private key for signing

        // TODO read data and calculate Hash-value

        // TODO make signature

        return null;
    }

    /**
     * Verify the signature of a signed file.
     *
     * @param filename    name of the signed file
     * @param sigFilename name of the file where the signature is stored
     * @return true if signature is correct, false if not
     */
    public static boolean verify(String filename, String sigFilename, Certificate cert) {
        // TODO init signature with public key (stored in a certificate) for verifying

        // TODO read file and calculate Message Digest

        // TODO read the signature from file

        // TODO verify the message digests and the signature

        return false;
    }
}
