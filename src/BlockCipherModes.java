import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;


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
            while(sc.hasNextLine()) {
                plaintext += sc.nextLine();// I think nextLine should be used to preserve spaces
            }
            System.out.println(iv);
            System.out.println(key);
            String binaryKey = stringTo7Bit(key);
            System.out.println(binaryKey);
            System.out.println(plaintext);
            
            String editedtext = addNullChar(plaintext);
            editedtext = stringTo7Bit(editedtext);
            
            ArrayList<String> blocks = createBlocks(editedtext);
            String cipherText ="";

            for(int i =0; i < blocks.size();i++){
                cipherText = cipherText + blockCipher(blocks.get(i), binaryKey);
            }
            System.out.println(cipherText +" " + binaryToString(cipherText));
            blocks.clear();
            blocks = createBlocks(cipherText);
            String decipherText = "";
            for(int i = 0; i <blocks.size();i++){
                decipherText = decipherText + decipherBlock(blocks.get(i),binaryKey);
            }
            System.out.println(decipherText + " " + binaryToString(decipherText));

            //cipherText = decipherBlock(cipherText, binaryKey);
           // System.out.println(cipherText +" " + binaryToString(cipherText));

            
           // String originalText = binaryToString(binaryText);
          //  System.out.println(originalText +"\n" + plaintext.equals(originalText));
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    //------------------ENCRYPTION AND DECRYPTION METHODS----------------------------------//
    private static void encrypt(String plaintext, String key, String initializationVector, int mode) {

    }

    private static void decrypt(String ciphertext, String key, String initializationVector, int mode) {

    }
    //----------------BLOCK CIPHER METHODS--------------------------------
    private static ArrayList<String> createBlocks(String binaryText){
        ArrayList<String> blocks = new ArrayList<>();
        String blockToAdd = "";
        for(int i = 0; i < binaryText.length(); i++){
            blockToAdd = blockToAdd + binaryText.charAt(i);
            if(blockToAdd.length()==35){
                blocks.add(blockToAdd);
                blockToAdd = "";
            }
        }
        return blocks; 
    }
    private static String blockCipher(String block, String key){// encrypted one block of text
        String cipherText = "";
        cipherText = shiftBlock(block);
        cipherText = xorStrings(cipherText, key);
        return cipherText;
    }
    private static String decipherBlock(String block, String key){// decrypts one block of text
        String plaintext = "";
        plaintext = xorStrings(block, key);
        plaintext = unShiftBlock(plaintext);
        return plaintext;
    }
    private static String shiftBlock(String block){// shifts a block by 3
        if(block.length() < 35){
            return "failure";
        }// this shift is actually a swap but does the same thing inpractice
        String first32 = block.substring(0,32);
        String last3 = block.substring(32,35);
        String shifted =  last3 + first32;
        return shifted;
        }
    //DECRYPTION//
    private static String unShiftBlock(String block){// same process as shiftblock
          if(block.length() < 35){
            return "failure";
        }
        String first3 = block.substring(0,3);
        String last32 = block.substring(3,35);
        String unshifted = last32 + first3;
        return unshifted;
    }
    //USED IN BOTH//
    public static String xorStrings(String blockString, String keyString){
        String result = "";
        String  blockChar = "";
        String keyChar = "";
        for(int i = 0; i < 35; i++){
            blockChar +=  blockString.charAt(i);
            keyChar +=  keyString.charAt(i);
            if(blockChar.length() == 7){
                result = result + xorChars(blockChar,keyChar);
                blockChar = "";
                keyChar = "";
            }
        }
        return result;
    }

    private static String xorChars(String blockChar, String keyChar){
        int blockInt = Integer.parseInt(blockChar,2);
        int keyInt = Integer.parseInt(keyChar,2);
        int xorResult = blockInt ^ keyInt; /// the ^ is an xor operator
        String resultBinary = Integer.toBinaryString(xorResult);
        while(resultBinary.length() < 7){
            resultBinary = "0" + resultBinary;
        }
        return resultBinary;
    }

    //------------------BIT CONVERSION METHODS--------------------------------//
    private static String addNullChar(String text){
        String result = text;
        while(result.length()%5 != 0){
            result = result + '\u0000';
        }
        return result;
    }
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

    //------------------Methods Used by Different Modes--------------------------------//

    private static String generateRandomIV() {
        String randomIV = "";
        Random rand = new Random();
        for (int i=0; i < 35; i++){
            int randomBit =  rand.nextInt(2);
            String singleBit = String.valueOf(randomBit);
            randomIV = singleBit.concat(randomIV);
        }
        return randomIV;
    }
}

