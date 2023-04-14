package PackageName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws SignatureException
     */
    public static byte[] sign(String filename, PrivateKey signatureKey)
            throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException {
        // TODO init signature with private key for signing
        Signature sign = Signature.getInstance("SHA512withRSA");
        sign.initSign(signatureKey);

        // TODO read data and calculate Hash-value
        byte[] data = Files.readAllBytes(Paths.get(filename));
        sign.update(data);

        // TODO make signature
        return sign.sign();
    }

    /**
     * Verify the signature of a signed file.
     *
     * @param filename    name of the signed file
     * @param sigFilename name of the file where the signature is stored
     * @return true if signature is correct, false if not
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws SignatureException
     */
    public static boolean verify(String filename, String sigFilename, Certificate cert)
            throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException {
        // TODO init signature with public key (stored in a certificate) for verifying
        Signature sign = Signature.getInstance("SHA512withRSA");
        sign.initVerify(cert);
        // sign.initVerify(cert.getPublicKey());

        // TODO read file and calculate Message Digest
        sign.update(Files.readAllBytes(Paths.get(filename)));

        // TODO read the signature from file
        byte[] signature = Files.readAllBytes(Paths.get(sigFilename));

        // TODO verify the message digests and the signature
        return sign.verify(signature);
    }
}
