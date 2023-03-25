package de.fhro.inf.its.uebung2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class FileUtils {

    private FileUtils() {
    }

    /**
     * Read contents of file.
     *
     * @param inputFile filename
     * @return contents of file as byte Array
     */
    public static byte[] readFromFile(String inputFile) throws IOException {
        return Files.readAllBytes(Paths.get(inputFile));
    }

    /**
     * Write data into a file.
     *
     * @param destFile filename
     * @param data     byte Array to save
     */
    public static void writeToFile(String destFile, byte[] data) throws IOException {
        Path path = Paths.get(destFile);
        Files.write(path, data);
    }

    /**
     * Read contents from file and decode it base64.
     *
     * @param inputFile filename
     * @return decoded contents of file
     */
    public static byte[] readFromFileBase64(String inputFile) throws IOException {
        return Base64.getDecoder().decode(FileUtils.readFromFile(inputFile));
    }

    /**
     * Encode data with base64 and write it into a file.
     *
     * @param destFile filename
     * @param data     byte Array to encode and save
     */
    public static void writeToFileBase64(String destFile, byte[] data) throws IOException {
        FileUtils.writeToFile(destFile, Base64.getEncoder().encode(data));
    }
}
