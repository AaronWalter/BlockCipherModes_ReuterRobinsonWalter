import java.io.*;
import java.util.Scanner;

public class BlockCipherModes {

    public static void main(String[] args) {
        String inFileName = args[0];

        try {
            File toRead = new File(inFileName);
            Scanner sc = new Scanner(toRead);
            String iv = sc.nextLine();
            String key = sc.nextLine();
            String plaintext = "";
            while(sc.hasNext()) {
                plaintext += sc.next();
            }
            System.out.println(iv);
            System.out.println(key);
            System.out.println(plaintext);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void encrypt(String plaintext, String key, String initializationVector, int mode) {

    }

    private static void decrypt(String ciphertext, String key, String initializationVector, int mode) {

    }

}
