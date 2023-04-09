package PackageName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

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
     * @throws CertificateException
     * @throws SignatureException
     */
    public static byte[] sign(String filename, PrivateKey signatureKey) throws NoSuchAlgorithmException, InvalidKeyException, CertificateException, IOException, SignatureException {
        // TODO init signature with private key for signing
        Signature sgn = Signature.getInstance("SHA512withRSA");
        sgn.initSign(signatureKey);

        // TODO read data and calculate Hash-value
        byte[] data = Files.readAllBytes(Paths.get(filename));
        sgn.update(data);

        // TODO make signature        
        return sgn.sign();
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
    public static boolean verify(String filename, String sigFilename, Certificate cert) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException {
        // TODO init signature with public key (stored in a certificate) for verifying
        Signature sig = Signature.getInstance("SHA512withRSA");
        sig.initVerify(cert);

        // TODO read file and calculate Message Digest
        byte[] data = Files.readAllBytes(Paths.get(filename));
        sig.update(data);

        // TODO read the signature from file
        byte[] signature = Files.readAllBytes(Paths.get(sigFilename));

        // TODO verify the message digests and the signature
        return sig.verify(signature);
    }
}
