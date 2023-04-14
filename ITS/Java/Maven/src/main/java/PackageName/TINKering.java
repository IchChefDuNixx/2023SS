package PackageName;

import java.security.GeneralSecurityException;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.HybridDecrypt;
import com.google.crypto.tink.HybridEncrypt;
import com.google.crypto.tink.KeyTemplates;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.Mac;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.config.TinkConfig;

/**
 * Hello world!
 *
 */
public class TINKering {
    public static void main(String[] args) throws GeneralSecurityException {
        TinkConfig.register();
        mac();
        hybrid();
        symmetric();
        sign("This is definitely legit from nVidia".getBytes());
    }

    static void mac() throws GeneralSecurityException {
        System.out.println("MAC >>>");
        byte[] data = "Hello World!".getBytes();
        byte[] fakedata = "Hello World!!!".getBytes();

        KeysetHandle keysetHandle = KeysetHandle.generateNew(KeyTemplates.get("HMAC_SHA256_128BITTAG"));

        Mac mac = keysetHandle.getPrimitive(Mac.class);

        byte[] tag = mac.computeMac(data);

        try {
            mac.verifyMac(tag, data);
        } catch (Exception e) {
            // TODO: handle exception
            return;
        } finally {
            System.out.println("Verified \"" + new String(data) + "\"\n");
        }

        try {
            mac.verifyMac(tag, fakedata);
        } catch (Exception e) {
            System.out.println(e);
            System.out.println();
        }
    }

    static void hybrid() throws GeneralSecurityException {
        System.out.println("HYBRID >>>");
        KeysetHandle privateKeysetHandle = KeysetHandle
                .generateNew(KeyTemplates.get("ECIES_P256_COMPRESSED_HKDF_HMAC_SHA256_AES128_GCM"));
        KeysetHandle publicKeysetHandle = privateKeysetHandle.getPublicKeysetHandle();

        HybridEncrypt hybridEncrypt = publicKeysetHandle.getPrimitive(HybridEncrypt.class);
        byte[] ciphertext = hybridEncrypt.encrypt("encrypt me!".getBytes(), "context info".getBytes());

        HybridDecrypt hybridDecrypt = privateKeysetHandle.getPrimitive(HybridDecrypt.class);
        byte[] plaintext = hybridDecrypt.decrypt(ciphertext, "context info".getBytes());

        System.out.println("Decrypted \"" + new String(plaintext) + "\"\n");

    }

    static void symmetric() throws GeneralSecurityException {
        System.out.println("SYMMETRIC >>>");
        KeysetHandle keysetHandle = KeysetHandle.generateNew(KeyTemplates.get("AES128_GCM"));

        Aead aead = keysetHandle.getPrimitive(Aead.class);

        byte[] ciphertext = aead.encrypt("encrypt me!".getBytes(), "password".getBytes());

        byte[] decrypted = aead.decrypt(ciphertext, "password".getBytes());

        System.out.println("Decrypted\"" + new String(decrypted) + "\"\n");

        try {
            byte[] not_decrypted = aead.decrypt(ciphertext, "wrong_password".getBytes());
            System.out.println("Decrypted\"" + new String(not_decrypted) + "\"\n");
        } catch (Exception e) {
            System.out.println(e);
            System.out.println();
        }
    }

    static void sign(byte[] data) throws GeneralSecurityException {
        System.out.println("SIGNATURE >>>");
        KeysetHandle privateKeysetHandle = KeysetHandle.generateNew(KeyTemplates.get("ECDSA_P256"));

        PublicKeySign signer = privateKeysetHandle.getPrimitive(PublicKeySign.class);

        byte[] signature = signer.sign(data);

        KeysetHandle publicKeysetHandle = privateKeysetHandle.getPublicKeysetHandle();

        PublicKeyVerify verifier = publicKeysetHandle.getPrimitive(PublicKeyVerify.class);

        try {
            verifier.verify(signature, data);
            System.out.println("Successfully verified \"" + new String(data) + "\"\n");
        } catch (Exception e) {
            System.out.println("Verification failed!\n" + e);
        }
    }
}
