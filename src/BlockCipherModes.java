import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;


public class BlockCipherModes {

    public static void main(String[] args) {
        //String inFileName = args[0];
       String  inFileName = "src\\data.txt";
       int mode = Integer.parseInt(args[1]);
       //mode 0 = ecb
        //mode 1 = cbc
        //mode 2 = cfb
        //mode 3 = ofb
        //mode 4 = ctr
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

            String cipherText = "";

            if (mode == 0) {
                cipherText = ecb(plaintext, binaryKey);
            } else if (mode == 1) {
                //cipherText = cbc(plaintext, binaryKey);
            } else if (mode == 2) {
                cipherText = cfb(plaintext, binaryKey);
            } else if (mode == 3) {
                //cipherText = ofb(plaintext, binaryKey);
            } else if (mode == 4) {
                //cipherText = ctr(plaintext, binaryKey);
            } else {
                System.out.println("Invalid mode");
            }
            System.out.println(cipherText);

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

        //MODES//

    private static String ecb(String plaintext, String binaryKey) {
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
        return cipherText;
    }

    private static String cbc(String plaintext, String binaryKey) {
        return "";
    }

    private static String cfb(String plaintext, String binaryKey) {
        String editedtext = addNullChar(plaintext);
        editedtext = stringTo7Bit(editedtext);

        ArrayList<String> blocks = createBlocks(editedtext);
        String cipherText ="";

        String updatedIV = generateRandomIV();

        for(int i =0; i < blocks.size();i++) { 
            // XOR IV and Key
            String encryptedIV = xorStrings(updatedIV, binaryKey);

            // Get a block from the blockCIpher
            String encryptedText = blockCipher(blocks.get(i), binaryKey);
            
            // XOR IV and 35 bits from the plaintext
            updatedIV = xorStrings(encryptedIV, encryptedText );
            cipherText = cipherText + updatedIV;
        }
        System.out.println(cipherText +" " + binaryToString(cipherText));
        blocks.clear();
        blocks = createBlocks(cipherText);
        String decipherText = "";
        for(int i =0; i < blocks.size();i++) { 
            // XOR IV and Key
            String encryptedIV = xorStrings(updatedIV, binaryKey);

            // Get a block from the blockCIpher
            String encryptedText = blockCipher(blocks.get(i), binaryKey);
            
            // XOR IV and 35 bits from the plaintext
            updatedIV = xorStrings(encryptedIV, encryptedText );
            decipherText = decipherText + updatedIV;
        }
        System.out.println(decipherText + " " + binaryToString(decipherText));
        return cipherText;
    }

    private static String ofb(String plaintext, String binaryKey) {
        return "";
    }

    private static String ctr(String plaintext, String binaryKey) {
        return "";
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

    private static String charTo7bit(int valueOfChar){ //does the padding for stringTo7Bit
        String binaryString = Integer.toBinaryString(valueOfChar);
        if(binaryString.length() > 7){
            return "Something went very wrong"; 
        }
        while(binaryString.length() < 7 ){
            binaryString = "0" + binaryString;
        }
        return binaryString;
    }
    private static String binaryToString(String binaryText){ //interprets a string of zeroes and ones as a string of 7-bit ASCII characters
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

