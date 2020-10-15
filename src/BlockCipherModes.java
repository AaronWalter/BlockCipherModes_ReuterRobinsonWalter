import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;


public class BlockCipherModes {

    public static void main(String[] args) {
        //String inFileName = "src\\"+args[0];
       String  inFileName = "src\\cbcToDecrypt.txt";
        // String  inFileName = "src\\cbcData.txt";
       int mode = 6;
       //mode 0 = ecb
        //mode 1 = cbc
        //mode 2 = cfb
        //mode 3 = ofb
        //mode 4 = ctr
        //mode 5 decyrpt a specified ofb encryption
        //mode 6 decrypt cbc encryption
        try {
            File toRead = new File(inFileName);
            Scanner sc = new Scanner(toRead);
            String iv = sc.nextLine();
            String key = sc.nextLine();
            String plaintext = "";
            while(sc.hasNextLine()) {
                plaintext += sc.nextLine();// I think nextLine should be used to preserve spaces
            }
            System.out.println(plaintext.length());
            System.out.println("IV: "+ iv);
            System.out.println("KEY: "+key);
            String binaryKey = key;
            if(key.length()< 35){
                binaryKey = stringTo7Bit(key);
            }
            System.out.println(binaryKey);
            System.out.println(plaintext);

            String cipherText = "";
            String decipherText = "";
            if (mode == 0) {
                cipherText = ecb(plaintext, binaryKey);
            } else if (mode == 1) {
                String newiv = generateRandomIV();
                cipherText = cbc(plaintext, binaryKey, newiv);
                String decipheredPlainTextBinary = cbcDecrypt(cipherText, binaryKey, newiv);
                String decipheredPlainText = binaryToString(decipheredPlainTextBinary);
                System.out.println(decipheredPlainText);
            } else if (mode == 2) {
                cipherText = cfb(plaintext, binaryKey);
            } else if (mode == 3) {
               cipherText = ofb(plaintext, binaryKey);
            } else if (mode == 4) {
                //cipherText = ctr(plaintext, binaryKey);
            } else if(mode == 5) {
                decryptOFB(plaintext, iv, binaryKey);
            } else if(mode == 6){
                cbcDecrypt(plaintext, binaryKey, iv);
            }
            else {
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
        //check for if padding was not required.
        if(blockToAdd.length() > 0 && blockToAdd.length()<35){
            blocks.add(blockToAdd);
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
        System.out.println("CIPHER " + cipherText +" " + binaryToString(cipherText));
        blocks.clear();
        blocks = createBlocks(cipherText);
        String decipherText = "";
        for(int i = 0; i <blocks.size();i++){
            decipherText = decipherText + decipherBlock(blocks.get(i),binaryKey);
        }
        System.out.println(decipherText + " " + binaryToString(decipherText));
        return cipherText;
    }

    private static String cbc(String plaintext, String binaryKey, String iv) {
        String paddedText = addNullChar(plaintext);
        String plaintextBitString = stringTo7Bit(paddedText);
        ArrayList<String> blocks = createBlocks(plaintextBitString);

        String ciphertext = "";
        String nextIV = iv;

        for(int i = 0; i<blocks.size(); i++) {
            String xored = xorStrings(blocks.get(i), nextIV);
            String result = blockCipher(xored, binaryKey);
            nextIV = result;
            ciphertext += result;
        }
        return ciphertext;
    }

    private static String cbcDecrypt(String ciphertext, String binaryKey, String iv) {
        String plaintext = "";
        ArrayList<String> blocks = createBlocks(ciphertext);

        String nextIV = iv;

        for(int i = 0; i<blocks.size(); i++) {
            String result = decipherBlock(blocks.get(i), binaryKey);
            String plaintextChunk = xorStrings(result,nextIV);
            nextIV = blocks.get(i);
            plaintext += plaintextChunk;
        }
        System.out.println("Decrypted: "+binaryToString(plaintext));
        return plaintext;
    }

    private static String cfb(String plaintext, String binaryKey) {
        String editedtext = addNullChar(plaintext);
        editedtext = stringTo7Bit(editedtext);

        ArrayList<String> blocks = createBlocks(editedtext);
        String cipherText ="";

        String updatedIV = generateRandomIV();
        String originalIV = updatedIV;

        for(int i =0; i < blocks.size();i++) { 
            // XOR IV and Key
            String encryptedIV = blockCipher(updatedIV, binaryKey);

            String encryptedText = xorStrings(blocks.get(i), encryptedIV);
            
            updatedIV = encryptedText;
            cipherText = cipherText + encryptedText;
        }
        System.out.println(cipherText +" " + binaryToString(cipherText));
        blocks.clear();
        blocks = createBlocks(cipherText);
        updatedIV = originalIV;
        String decipherText = "";
        for(int i =0; i < blocks.size();i++) { 
            // XOR IV and Key
            String encryptedIV = blockCipher(updatedIV, binaryKey);

            // Get a block from the blockCIpher
            String encryptedText = xorStrings(blocks.get(i), encryptedIV);

            // XOR IV and 35 bits from the plaintext
            updatedIV = blocks.get(i);
            decipherText = decipherText + encryptedText;
            updatedIV = xorStrings(blocks.get(i), binaryKey);
            decipherText = decipherText + updatedIV;
        }
        System.out.println("DECIPHERED: "  + " " + binaryToString(decipherText));
        return cipherText;
    }

    private static String ofb(String plaintext, String binaryKey) {
        String editedtext = stringTo7Bit(plaintext);

        ArrayList<String> blocks = createBlocks(editedtext);
        String cipherText ="";

        String updatedIV = generateRandomIV();
        String originalIV = updatedIV;
        System.out.println("IV: " + originalIV);
        for(int i =0; i < blocks.size();i++) { 
            // XOR IV and Key 
            String encryptedIV = blockCipher(updatedIV, binaryKey);
            updatedIV = encryptedIV;

            // XOR IV and 35 bits from the plaintext
            String encryptedText = xorStrings(blocks.get(i), encryptedIV);

            cipherText = cipherText + encryptedText;
        }
        System.out.println(" CIPHER " + cipherText + "\n" + binaryToString(cipherText));
        System.out.println("DECRYPTED " + decryptOFB(cipherText, originalIV, binaryKey) +"\n");
        return cipherText;
    }
    private static String decryptOFB(String cipher, String IV, String key){
        String decipherText = "";
        ArrayList<String> blocks = createBlocks(cipher);
        String updatedIV = IV;

        for(int i =0; i < blocks.size();i++) {
            // XOR IV and Key
            String encryptedIV = blockCipher(updatedIV, key);
            updatedIV = encryptedIV;

            // XOR IV and 35 bits from the plaintext
            String encryptedText = xorStrings(blocks.get(i), encryptedIV);

            decipherText = decipherText + encryptedText;
        }
        System.out.println( "DECIPHER TEXT"+ " " + binaryToString(decipherText));
        System.out.println(decipherText);
        return decipherText;
    }

    private static String ctr(String plaintext, String binaryKey) {
        return "";
    }

    private static String ctrDecrypt(String cipherBinText, String binaryKey, String iv) {
        String plaintext = "";
        int count = 0;
        iv = makeCountStr(iv,count);
        ArrayList<String> cipherBlocks = createBlocks(cipherBinText);
        for(String s : cipherBlocks) {
            String result = blockCipher(iv,binaryKey);
            plaintext += xorStrings(s,result);
            count += 1;
            iv = makeCountStr(iv,count);
        }
        return plaintext;
    }

        private static String makeCountStr(String iv, int count) {
            count = count % (2^16);
            String countStr = Integer.toBinaryString(count);
            if (countStr.length() < 16) {
                countStr = "0".repeat(16 - countStr.length()) + countStr;
            }
            iv = iv.substring(0, 19) + countStr;
            return iv;
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
        for(int i = 0; i < blockString.length(); i++){
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

    public static int xorStringsToFindDifferences(String encryptedText, String encryptedTextWithBitChange){
        int numberOfDifferences = 0;
        Long encryptedTextLong = Long.parseLong(encryptedText,2);
        Long encryptedTextWithBitChangeLong =Long.parseLong(encryptedTextWithBitChange,2);
        Long result = encryptedTextWithBitChangeLong ^ encryptedTextLong;
        String result2 = Long.toBinaryString(result);
        for (int i =0; i < result2.length(); i++) {
            if ( result2.charAt(i) ==1) {
                numberOfDifferences++;
            }
        }
       System.out.println("Result of xoring the two plaintexts " + result2);
        return numberOfDifferences;
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

    private static String changeBitInBinaryText(String binaryText) {
        char zero = '0';
        char one = '1';
        if(binaryText.charAt(0) == zero) {
            binaryText = one + binaryText.substring(1);
            
        }
        else {
            binaryText = zero + binaryText.substring(1);
        }
        System.out.println("Changed BinaryText" + binaryText);
    return binaryText;
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

