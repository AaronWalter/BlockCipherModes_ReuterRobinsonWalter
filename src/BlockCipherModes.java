import java.io.*;
import java.util.Scanner;

public class BlockCipherModes {

    public static void main(String[] args) {
        //String inFileName = args[0];
       String  inFileName = "src\\data.txt";
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
            String binaryText = stringTo7Bit(plaintext);
            System.out.println(binaryText);
            String originalText = binaryToString(binaryText);
            System.out.println(originalText +"\n" + plaintext.equals(originalText));
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    //------------------ENCRYPTION AND DECRYPTION METHODS----------------------------------//
    private static void encrypt(String plaintext, String key, String initializationVector, int mode) {

    }

    private static void decrypt(String ciphertext, String key, String initializationVector, int mode) {

    }
    //------------------BIT CONVERSION METHODS--------------------------------//
    private static String stringTo7Bit(String textToChange){
        String result = "";
        int lengthOfString = textToChange.length();
        if(lengthOfString == 0){
            return result;
        }
        for(int  i = 0; i < lengthOfString;i++){
            int charValue = (int) textToChange.charAt(i);
            result = result + charTo7bit(charValue);
        }
        return result;
    }
    private static String charTo7bit(int valueOfChar){
        String binaryString = Integer.toBinaryString(valueOfChar);
        if(binaryString.length() > 7){
            return "Something went very wrong"; 
        }
        while(binaryString.length() < 7 ){
            binaryString = "0" + binaryString;
        }
        return binaryString;
    }
    private static String binaryToString(String binaryText){
        String textToConvert = "";
        String result = "";
        for(int i = 0; i < binaryText.length(); i++){
            textToConvert += binaryText.charAt(i);
            if(textToConvert.length() == 7){
            int decimal = Integer.parseInt(textToConvert,2);
            char characterToAdd = (char) decimal;
            result = result + characterToAdd;
            textToConvert = "";
            }
            
        }
        return result;
    }
}
