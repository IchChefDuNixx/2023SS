import javax.crypto.SecretKey;

public class App {
    public static void main(String[] args) throws Exception {
        // goal: read, encrypt, encode, write,
        // ------read, decode, decrypt, write
        String CURR_DIRECTORY = "C:\\Users\\Felix\\Desktop\\auf-zu-win-11\\th\\2023SS\\ITS\\Java\\src\\";

        // create key
        // TODO: Store the Base64 encoded key in a file
        SecretKey sk = Encryption.generateKey();
        System.out.println("key: " + sk);

        // read in file and store a Base64 version of it
        byte[] plaintext = FileUtils.readFromFile(CURR_DIRECTORY + "input.txt");
        printEach(plaintext);
        FileUtils.writeToFileBase64(CURR_DIRECTORY + "plain64.txt", plaintext);

        // encrypt file and store both, normal and Base64 version of it
        byte[] ciphertext = Encryption.encrypt(sk, plaintext);
        printEach(ciphertext);
        FileUtils.writeToFile(CURR_DIRECTORY + "cipher.txt", ciphertext);
        FileUtils.writeToFileBase64(CURR_DIRECTORY + "cipher64.txt", ciphertext);

        // read in file, auto decode and store on disk
        byte[] ciphertext_hopefully = FileUtils.readFromFileBase64(CURR_DIRECTORY + "cipher64.txt");
        printEach(ciphertext_hopefully);
        FileUtils.writeToFile(CURR_DIRECTORY + "decodedCipher.txt", ciphertext_hopefully);

        // decrypt file and store on disk
        byte[] plaintext_hopefully = Decryption.decrypt(sk, ciphertext_hopefully);
        printEach(plaintext_hopefully);
        FileUtils.writeToFile(CURR_DIRECTORY + "decryptedCipher.txt", plaintext_hopefully);

    }

    static void printEach(byte[] byteArr) {
        String temp = "";
        for (byte b : byteArr) {
            temp += b + ", ";
        }
        System.out.println(temp + byteArr);
    }
}
