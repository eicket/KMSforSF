

import static crypto.Crypto.computeSHA256;
import java.util.Arrays;
import java.util.Base64;
import static org.miracl.core.AES.CBC_IV0_DECRYPT;
import static org.miracl.core.AES.CBC_IV0_ENCRYPT;

public class AESInCBCMode_1
{

    public static void main(String[] args)
    {
        String plainText = "This is a very long message for padding xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111122222222222222222222222222222222222222222222222222222222222222222222222233333333333333333333333333333333333333333333333333333333333333333333333999999999999999999999999999999999999999999999999999999999999999999999x";
        String password = "NotTooStrong";
        byte[] passwordHash = computeSHA256(password.getBytes());

        // test the padding 
        // byte[] padded = pkcs7Padding(plainText.getBytes(), 32);
        // System.out.println("Padded : " + new String(padded) + "|");

        // Encrypt the plaintext
         System.out.println("Plain text len : " + plainText.length());
        byte[] encrypted = encrypt(plainText.getBytes(), passwordHash);
        System.out.println("Encrypted (Base64): " + Base64.getEncoder().encodeToString(encrypted));

        // Decrypt the ciphertext
        byte[] decrypted = decrypt(encrypted, passwordHash);
        System.out.println("Decrypted Text: " + (new String(decrypted)).trim() + "|");
    }

    // AES Encryption in CBC mode with PKCS7 padding
    public static byte[] encrypt(byte[] plainText, byte[] key)
    {
        // Generate a not really random IV (16 bytes for AES block size)
        /*
        byte[] iv = new byte[16];
        for (int i = 0; i < 16; i++)
        {
            iv[i] = (byte) i;
        }

        AES aes = new AES();
        aes.init(AES.CBC, key.length * 8, key, iv); // Initialize AES in CBC mode with key and IV

        // Apply PKCS7 padding
        plainText = pkcs7Padding(plainText, 32);

        //   byte[] cipherText = new byte[plainText.length];
        //      aes.encrypt(plainText, cipherText);  // Perform encryption
        aes.encrypt(plainText);  // Perform encryption

        return plainText;
         */

        return CBC_IV0_ENCRYPT(key, plainText);
    }

    // AES Decryption in CBC mode with PKCS7 padding removal
    public static byte[] decrypt(byte[] cipherText, byte[] key)
    {
        // Generate a not really random IV (16 bytes for AES block size)
        /*
        byte[] iv = new byte[16];
        for (int i = 0; i < 16; i++)
        {
            iv[i] = (byte) i;
        }

        AES aes = new AES();
        aes.init(AES.CBC, key.length * 8, key, iv); // Initialize AES in CBC mode with key and IV

        byte[] decryptedText = new byte[cipherText.length];
        aes.decrypt(decryptedText);  // Perform decryption

        // Remove PKCS7 padding
        decryptedText = removePkcs7Padding(decryptedText);
        return decryptedText;
         */
        //    return CBC_IV0_DECRYPT(key, cipherText);

        return CBC_IV0_DECRYPT(key, cipherText);
    }

    // PKCS7 Padding (adds padding to the plaintext)
    public static byte[] pkcs7Padding(byte[] input, int blockSize)
    {
        int paddingLength = blockSize - (input.length % blockSize);
        byte[] padded = Arrays.copyOf(input, input.length + paddingLength);
        Arrays.fill(padded, input.length, padded.length, (byte) paddingLength);
        return padded;
    }

    // Remove PKCS7 Padding (removes padding after decryption)
    public static byte[] removePkcs7Padding(byte[] input)
    {
        int paddingLength = input[input.length - 1];
        return Arrays.copyOf(input, input.length - paddingLength);
    }
}
