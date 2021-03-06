import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;


public class BlockCipherModes {

    public static void main(String[] args) {
        String inFileName = "src/"+args[0];
        int mode = Integer.parseInt(args[1]);
        //mode 0 = ecb
        //mode 1 = cbc
        //mode 2 = cfb
        //mode 3 = ofb
        //mode 4 = ctr
        int encrypt = Integer.parseInt(args[2]);
        //encrypt 0 = only encrypt
        //encrypt 1 = only decrypt
        //encrypt 2 = encrypt a text, then decrypt it, checking that the result is the original plaintext (all modes)
        //encrypt 3 = perform the analysis for task 4 (all modes)

        try {
            File toRead = new File(inFileName);
            Scanner sc = new Scanner(toRead);
            String iv = sc.nextLine();
            String key = sc.nextLine();
            String plaintext = "";
            while(sc.hasNextLine()) {
                plaintext += sc.nextLine();// I think nextLine should be used to preserve spaces
            }
      
            String binaryKey = key;
            if(key.length()< 35){
                binaryKey = stringTo7Bit(key);
            }
            System.out.println(binaryKey);
            System.out.println(plaintext);

            if(encrypt == 0) {
                String cipherText = encrypt(plaintext,binaryKey,iv,mode);
                System.out.println("Result of encryption:\n" + cipherText);
            } else if (encrypt == 1) {
                String decipherText = decrypt(plaintext,binaryKey,iv,mode);
                System.out.println("Result of decryption:\n" + decipherText);
                System.out.println("Decryption to plaintext:\n" + binaryToString(decipherText));
            } else if (encrypt == 2) {
                for(int i = 0; i<5; i++) {
                    String cipherText = encrypt(plaintext, binaryKey, iv, i);
                    System.out.println("Result of encryption:\n" + cipherText);
                    String decipherText = decrypt(cipherText, binaryKey, iv, i);
                    System.out.println("Result of decryption:\n" + decipherText);
                    System.out.println("Decryption to plaintext:\n" + binaryToString(decipherText));
                    System.out.println(stringTo7Bit(plaintext).equals(decipherText));
                    System.out.println(binaryToString(stringTo7Bit(plaintext)).equals(binaryToString(decipherText)));
                }
            } else if (encrypt == 3) {
                for(int i = 0; i < 5; i++) {
                    System.out.println("Mode " + i + ":");
                    String cipherText = encrypt(plaintext,binaryKey,iv,i);
                    System.out.println("Encryption result:\n" + cipherText);
                    String errorText = changeBitInBinaryText(cipherText);
                    String decipherText = decrypt(cipherText,binaryKey,iv,i);
                    String decipherError = decrypt(errorText,binaryKey,iv,i);
                    System.out.println("Normal decryption:\n" + decipherText);
                    System.out.println("Decryption with error:\n" + decipherError);
                    ArrayList<String> aBlocks = createBlocks(decipherText);
                    ArrayList<String> bBlocks = createBlocks(decipherError);
                    int totalErrors = 0;
                    int blocksChanged = 0;
                    for(int j = 0; j<aBlocks.size(); j++) {
                        String a = aBlocks.get(j);
                        String b = bBlocks.get(j);
                        //System.out.println("Regular block: " +a);
                        //System.out.println("Error block: " +b);
                        String result = xorStrings(a,b);
                        //System.out.println("result: " + result);
                        int blockErrors = 0;
                        boolean blockError = false;
                        for(int k = 0; k<result.length(); k++) {
                            if (result.charAt(k) == '1') {
                                blockErrors += 1;
                                blockError = true;
                            }
                        }
                        totalErrors += blockErrors;
                        if (blockError) {
                            blocksChanged += 1;
                        }
                    }
                    System.out.println("Total number of bits changed: " + totalErrors);
                    System.out.println("Number of blocks with an error: " + blocksChanged);
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                }
            }

        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    //------------------ENCRYPTION AND DECRYPTION METHODS----------------------------------//
    private static String encrypt(String plaintext, String key, String initializationVector, int mode) {
        if(mode == 0) {
            return ecb(plaintext,key);
        } else if (mode == 1) {
            return cbc(plaintext,key,initializationVector);
        } else if (mode == 2) {
            return cfb(plaintext,key,initializationVector);
        } else if (mode == 3) {
            return ofb(plaintext,key,initializationVector);
        } else if (mode == 4) {
            return ctr(plaintext,key,initializationVector);
        } else return ("Invalid mode");
    }

    private static String decrypt(String ciphertext, String key, String initializationVector, int mode) {
        if(mode == 0) {
            return ecbDecrypt(ciphertext,key);
        } else if (mode == 1) {
            return cbcDecrypt(ciphertext,key,initializationVector);
        } else if (mode == 2) {
            return cfbDecrypt(ciphertext,key,initializationVector);
        } else if (mode == 3) {
            return ofbDecrypt(ciphertext,key,initializationVector);
        } else if (mode == 4) {
            return ctrDecrypt(ciphertext,key,initializationVector);
        } else return ("Invalid mode");
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
        }// this shift is actually a swap but does the same thing in practice
        String first32 = block.substring(0,32);
        String last3 = block.substring(32,35);
        String shifted =  last3 + first32;
        return shifted;
        }

        //MODES//

    private static String ecb(String plaintext, String binaryKey) {
        String editedtext = addNullChar(plaintext);
        editedtext = stringTo7Bit(editedtext);

        //System.out.println("Plaintext binary:\n" + editedtext);

        ArrayList<String> blocks = createBlocks(editedtext);
        String cipherText ="";

        for(int i =0; i < blocks.size();i++){
            //System.out.println("Block " + i + ": " + blocks.get(i));
            cipherText = cipherText + blockCipher(blocks.get(i), binaryKey);
        }
        return cipherText;
    }

    private static String ecbDecrypt(String cipherText, String binaryKey) {
        ArrayList<String> blocks = createBlocks(cipherText);
        String plainText = "";
        for(int i = 0; i <blocks.size();i++){
            plainText = plainText + decipherBlock(blocks.get(i),binaryKey);
        }
        return plainText;
    }

    private static String cbc(String plaintext, String binaryKey, String iv) {
        String paddedText = addNullChar(plaintext);
        String plaintextBitString = stringTo7Bit(paddedText);
        ArrayList<String> blocks = createBlocks(plaintextBitString);

        String ciphertext = "";
        String nextIV = iv;

        for(int i = 0; i < blocks.size(); i++) {
            String encryptedIV = xorStrings(blocks.get(i), nextIV);
            String result = blockCipher(encryptedIV, binaryKey);
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
        return plaintext;
    }

    private static String cfb(String plaintext, String binaryKey, String iv) {
        String editedtext = addNullChar(plaintext);
        editedtext = stringTo7Bit(editedtext);

        ArrayList<String> blocks = createBlocks(editedtext);
        String cipherText ="";

        String nextIV = iv;

        for(int i =0; i < blocks.size();i++) { 
            // XOR IV and Key
            String encryptedIV = blockCipher(nextIV, binaryKey);

            String encryptedText = xorStrings(blocks.get(i), encryptedIV);
            
            nextIV = encryptedText;
            cipherText = cipherText + encryptedText;
        }
        return cipherText;
    }

    private static String cfbDecrypt(String cipherText,String binaryKey,String iv) {
        ArrayList<String> blocks = createBlocks(cipherText);
        String nextIV = iv;
        String decipherText = "";
        for(int i =0; i < blocks.size();i++) {
            // XOR IV and Key
            String encryptedIV = blockCipher(nextIV, binaryKey);

            String encryptedText = xorStrings(blocks.get(i), encryptedIV);

            // XOR IV and 35 bits from the plaintext
            decipherText = decipherText + encryptedText;
            nextIV = blocks.get(i);
        }
        return decipherText;
    }

    private static String ofb(String plaintext, String binaryKey, String iv) {
        String editedtext = stringTo7Bit(plaintext);

        ArrayList<String> blocks = createBlocks(editedtext);
        String cipherText ="";

        String nextIV = iv;
        for(int i =0; i < blocks.size();i++) { 
            // XOR IV and Key 
            String encryptedIV = blockCipher(nextIV, binaryKey);
            nextIV = encryptedIV;

            // XOR IV and 35 bits from the plaintext
            String encryptedText = xorStrings(blocks.get(i), encryptedIV);

            cipherText = cipherText + encryptedText;
        }
        return cipherText;
    }
    private static String ofbDecrypt(String cipherText, String binaryKey, String iv){
        String decipherText = "";
        ArrayList<String> blocks = createBlocks(cipherText);
        String nextIV = iv;

        for(int i =0; i < blocks.size();i++) {
            // XOR IV and Key
            String encryptedIV = blockCipher(nextIV, binaryKey);
            nextIV = encryptedIV;

            // XOR IV and 35 bits from the plaintext
            String encryptedText = xorStrings(blocks.get(i), encryptedIV);

            decipherText = decipherText + encryptedText;
        }
        return decipherText;
    }

    private static String ctr(String plaintext, String binaryKey, String iv) {
        int count = 0;
        String cipherText = "";
        iv = makeCountStr(iv,0);
        String binText = stringTo7Bit(plaintext);
        ArrayList<String> binStrings = createBlocks(binText);
        for (String s : binStrings) {
            String result = blockCipher(iv,binaryKey);
            cipherText += xorStrings(s,result);
            count += 1;
            iv = makeCountStr(iv,count);
        }
        return cipherText;
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
            return binaryString.substring(binaryString.length()-7);
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
        //System.out.println("Changed BinaryText" + binaryText);
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

