package com.example.super_secure_login;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PasswordHashing {
    private static final int SALT_LENGTH = 16;
    private static final String HASH_DELIMITER = ":";

    // These are just the default values from the library.
    // You have to decide yourself if this is a good level of security and tweak these values.
    private static final PasswordEncoder scryptEncoder = new SCryptPasswordEncoder(16384, 8, 1, 32, 64);
    private static final PasswordEncoder bcryptEncoder = new BCryptPasswordEncoder(10);
    private static final PasswordEncoder argon2Encoder = new Argon2PasswordEncoder(16, 32, 1, 4096, 10);

    private static final SecureRandom rand = new SecureRandom();

    private PasswordHashing() {
    }

    /**
     * Generates a random salt.
     * @return salt with SALT_LENGTH
     */
    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        rand.nextBytes(salt);
        return salt;
    }

    /**
     * Encodes salt and hash with Base64 and concatenates the two Strings separated by a delimiter.
     *
     * @param salt random salt
     * @param hash hashed password
     * @return Base64 encoded salt concatenated with DELIMITER and Base64 encoded password hash
     */
    private static String concatenateSaltAndPassword(byte[] salt, byte[] hash) {
        var encoder = Base64.getEncoder();
        return encoder.encodeToString(salt) + HASH_DELIMITER + encoder.encodeToString(hash);
    }

    /**
     * Splits Base64 encoded stored password into the salt and hashed password.
     *
     * @param hash concatenated and Base64 encoded salt and password hash
     * @return decoded salt and decoded password hash
     */
    private static byte[][] splitHash(String hash) {
        Base64.Decoder decoder = Base64.getDecoder();
        var parts = hash.split(HASH_DELIMITER);
        return new byte[][]{decoder.decode(parts[0]), decoder.decode(parts[1])};
    }

    /**
     * Concatenates password with a salt and then perform hashing.
     *
     * @param password to be hashed
     * @return String containing salt and hashed password, all information need to verify a password
     */
    public static String hashWithSalt(String password) throws NoSuchAlgorithmException {
        byte[] salt = generateSalt();
        byte[] hashedPassword = hashPassword(password, salt);
        return concatenateSaltAndPassword(salt, hashedPassword);
    }

    /**
     * Computes SHA512 hash of salt and password.
     *
     * @param password password from user
     * @param salt     generated salt
     * @return password hash
     */
    private static byte[] hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(salt);
        return md.digest(password.getBytes(UTF_8));
    }

    /**
     * Verifies given password by combining it with the stored salt before hashing and comparing the output with
     * the stored hash.
     *
     * @param password       password input from user
     * @param storedPassword password stored in system (salt:hashedPassword)
     * @return true if password hashes are the same
     */
    public static boolean verifyPasswordHash(String password, String storedPassword) throws NoSuchAlgorithmException {
        byte[][] parts = splitHash(storedPassword);
        byte[] salt = parts[0];
        byte[] hashValue = hashPassword(password, salt);
        return Arrays.equals(hashValue, parts[1]);
    }

    /**
     * Generates a salt and hashes a password with PBKDF2 algorithm.
     *
     * @param password password from user
     * @return String containing salt and PBKDF2-hashed password
     */
    public static String hashWithPBKDF2(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = generateSalt();
        byte[] hashedPassword = hashPBKDF2(password, salt);
        return concatenateSaltAndPassword(salt, hashedPassword);
    }

    /**
     * Computes PBKDF2 hash of salt and password.
     *
     * @param password password from user
     * @param salt     generated salt
     * @return hash of salt and password
     */
    private static byte[] hashPBKDF2(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 20000, 192);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        return skf.generateSecret(spec).getEncoded();
    }

    /**
     * Verifies given password by combining it with the stored salt before hashing with PBKDF2 and comparing the output with
     * the stored hash.
     *
     * @param password       password input from user
     * @param storedPassword password stored in system (salt:hashedPassword)
     * @return true if password hashes are the same
     */
    public static boolean verifyPasswordPBKDF2(String password, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[][] parts = splitHash(storedPassword);
        byte[] salt = parts[0];
        byte[] hashValue = hashPBKDF2(password, salt);
        return Arrays.equals(hashValue, parts[1]);
    }

    /**
     * Hashes a password with Bcrypt hashing algorithm.
     * a salt is automatically generated and stored
     *
     * @param password password from user
     * @return password hash
     */
    public static String hashWithBcrypt(String password) {
        return bcryptEncoder.encode(password);
    }

    /**
     * Verifies a password hashed with Bcrypt.
     *
     * @param password       password input from user
     * @param storedPassword password stored in system
     * @return true if encoded passwords are the same
     */
    public static boolean verifyBcrypt(String password, String storedPassword) {
        return bcryptEncoder.matches(password, storedPassword);
    }

    /**
     * Hashes a password with SCrypt hashing algorithm.
     * a salt is automatically generated and stored
     *
     * @param password password from user
     * @return String containing salt and SCrypt password
     */
    public static String hashWithScrypt(String password) {
        return scryptEncoder.encode(password);
    }

    /**
     * Verifies a password hashed with Scrypt.
     *
     * @param password       password input from user
     * @param storedPassword password stored in system
     * @return true if encoded passwords are the same
     */
    public static boolean verifyScrypt(String password, String storedPassword) {
        return scryptEncoder.matches(password, storedPassword);
    }

    /**
     * Hashes a password with salt and Argon2 hashing algorithm.
     *
     * @param password password from user
     * @return String containing salt and Argon2-hashed password
     */
    public static String hashWithArgon2(String password) {
        return argon2Encoder.encode(password);
    }

    /**
     * Verifies a password hashed with Argon2.
     *
     * @param password       password input from user
     * @param storedPassword password stored in system
     * @return true if password hashes are the same
     */
    public static boolean verifyArgon2(String password, String storedPassword) {
        return argon2Encoder.matches(password, storedPassword);
    }
}
