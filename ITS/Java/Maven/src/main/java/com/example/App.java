package com.example;

import java.security.GeneralSecurityException;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.DeterministicAead;
import com.google.crypto.tink.HybridDecrypt;
import com.google.crypto.tink.HybridEncrypt;
import com.google.crypto.tink.KeyTemplates;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.Mac;
import com.google.crypto.tink.config.TinkConfig;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws GeneralSecurityException
    {
        TinkConfig.register();
        //mac();
        //hybrid();
        symmetric();
        //sign();
    }

    static void mac() throws GeneralSecurityException {
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
            System.out.println("Verified " + data);
        }

        try {
            mac.verifyMac(tag, fakedata);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    static void hybrid() throws GeneralSecurityException {
        KeysetHandle privateKeysetHandle = KeysetHandle.generateNew(KeyTemplates.get("ECIES_P256_COMPRESSED_HKDF_HMAC_SHA256_AES128_GCM"));    
        KeysetHandle publicKeysetHandle = privateKeysetHandle.getPublicKeysetHandle();

        HybridEncrypt hybridEncrypt = publicKeysetHandle.getPrimitive(HybridEncrypt.class);
        byte[] ciphertext = hybridEncrypt.encrypt("encrypt me!".getBytes(), "context info".getBytes());

        HybridDecrypt hybridDecrypt = privateKeysetHandle.getPrimitive(HybridDecrypt.class);
        byte[] plaintext = hybridDecrypt.decrypt(ciphertext, "context info".getBytes());

        System.out.println(plaintext.toString());

    }

    static void symmetric() throws GeneralSecurityException {
        KeysetHandle keysetHandle = KeysetHandle.generateNew(KeyTemplates.get("AES256_SIV"));

        DeterministicAead daead = keysetHandle.getPrimitive(DeterministicAead.class);

        byte[] ciphertext = daead.encryptDeterministically("encrypt me!".getBytes(), "pass".getBytes());

        byte[] decrypted = daead.decryptDeterministically(ciphertext, "password?".getBytes());

        System.out.println(decrypted);
    }

    static void sign() {
        return;
    }
}
