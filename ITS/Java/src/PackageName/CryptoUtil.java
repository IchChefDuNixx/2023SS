package PackageName;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;


public class CryptoUtil {
    /**
     * read Base64 encoded Certificate from a file (format PEM)
     *
     * @param certificateFile filename of certificate file
     */
    public static Certificate readCertificate(String certificateFile) throws CertificateException, IOException {
        try (FileInputStream fis = new FileInputStream(certificateFile)) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return cf.generateCertificate(fis);
        }
    }

    /**
     * read private key from file
     *
     * @param filename filename of private key file
     */
    public static PrivateKey getPrivateKey(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String privateKeyPEM = Files.readString(Path.of(filename));

        // strip of header, footer, newlines, whitespaces
        privateKeyPEM = privateKeyPEM
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        // decode to get the binary DER representation
        byte[] privateKeyDER = Base64.getDecoder().decode(privateKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyDER));
    }

    /**
     * open keystore with a password and read a password protected private key
     * @throws KeyStoreException
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableKeyException
     * @param alias alias for the private key
     * @param keypass password for the private key
     * @param keystore filename of the keystore
     * @param storepass password for the keystore
     */
    public static Key getPrivateKey(String keystore, char[] storepass,
                                    char[] keypass, String alias) throws KeyStoreException,
            NoSuchAlgorithmException, CertificateException, IOException,
            UnrecoverableKeyException {

        //open KeyStore with password and create instance
        KeyStore ks = KeyStore.getInstance("JKS");
        FileInputStream in = new FileInputStream(keystore);
        ks.load(in, storepass);
        in.close();
        //get password protected private key from KeyStore
        Key key = ks.getKey(alias, keypass);
        return key;
    }
}
