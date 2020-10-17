How to run me:

Put the data you want to encrypt in a txt file in the src directory of this project.
At the top of the file, put the IV you want to use (even if the mode does not use an IV).
The IV is a binary string with length 35.
Put the key on the next line.
The key is a binary string with length 35 or 5 regular characters (your choice).
Then put your data. For encryption, use plain text. For decryption, use binary text.

The command line arguments for the program are as such:
args[0]: the name of the txt file where your data is.
args[1]: which cipher mode to run.
    0 = ecb
    1 = cbc
    2 = cfb
    3 = ofb
    4 = ctr
args[2]: which way to run the mode.
    0 = encrypt the plaintext with the given mode and print the resulting binary text.
    1 = decrypt the ciphertext with the given mode and print the resulting binary and plain texts.
    2 = encrypt the plaintext, then decrypt it, in each mode. Check to see that we get what's expected.
    3 = run task 4 on the plaintext, in each mode.

