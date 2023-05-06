package com.example.super_secure_login;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestPasswordHash {
    @Test
    void testPasswordHash() throws Exception {
        String hashedPassword = PasswordHashing.hashWithSalt("test1234");
        Assertions.assertTrue(PasswordHashing.verifyPasswordHash("test1234", hashedPassword));
        System.out.println("Hash verification with salt successful: " + hashedPassword);
    }

    @Test
    void testPasswordPBKDF2() throws Exception {
        String hashedPassword = PasswordHashing.hashWithPBKDF2("test1234");
        Assertions.assertTrue(PasswordHashing.verifyPasswordPBKDF2("test1234", hashedPassword));
        System.out.println("Hash verification with PKDF2 successful: " + hashedPassword);
    }

    @Test
    void testBcrypt() {
        String hashedPassword = PasswordHashing.hashWithBcrypt(("test1234"));
        Assertions.assertTrue(PasswordHashing.verifyBcrypt("test1234", hashedPassword));
        System.out.println("Hash verification with Bcrypt successful: " + hashedPassword);
    }

    @Test
    void testScrypt() {
        String hashedPassword = PasswordHashing.hashWithScrypt(("test1234"));
        Assertions.assertTrue(PasswordHashing.verifyScrypt("test1234", hashedPassword));
        System.out.println("Hash verification with Scrypt successful: " + hashedPassword);
    }

    @Test
    void testPasswordArgon2() {
        String hashedPassword = PasswordHashing.hashWithArgon2("test1234");
        Assertions.assertTrue(PasswordHashing.verifyArgon2("test1234", hashedPassword));
        System.out.println("Hash verification with Argon2 successful: " + hashedPassword);
    }
}
