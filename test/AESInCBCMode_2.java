
import static crypto.Crypto.computeSHA256;
import org.miracl.core.AES;
import java.util.Arrays;

public class AESInCBCMode_2
{

    public static void main(String[] args)
    {
        // String plainText = "This is a very long message for padding xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111122222222222222222222222222222222222222222222222222222222222222222222222233333333333333333333333333333333333333333333333333333333333333333333333999999999999999999999999999999999999999999999999999999999999999999999x";
        
        // Create a StringBuilder to build the long string
        StringBuilder longString = new StringBuilder();

        // Define the number of repetitions (e.g., 1 million times)
        int repetitions = 1_000_000;
        
        // Append a pattern to the StringBuilder repeatedly
        for (int i = 0; i < repetitions; i++) {
            longString.append("This is a very long string. ");
        }

        // Convert StringBuilder to String
        String plainText = longString.toString();

        // Output the length of the string
        System.out.println("The length of the long string is: " + plainText.length());
        
        String password = "NotTooStrong";
        byte[] passwordHash = computeSHA256(password.getBytes());

        // Generate a not really random IV (16 bytes for AES block size)
        byte[] iv = new byte[16];
        for (int i = 0; i < 16; i++)
        {
            iv[i] = 0;
        }

        // Encrypt the plaintext
        byte[] encrypted = encrypt(plainText.getBytes(), passwordHash, iv);
        // System.out.println("Encrypted (Base64): " + Base64.getEncoder().encodeToString(encrypted));

        // Decrypt the ciphertext
        byte[] decrypted = decrypt(encrypted, passwordHash, iv);
        System.out.println("Decrypted Text, len : " + decrypted.length + ", string : " + new String(decrypted));
    }

    // AES Encryption in CBC mode with PKCS7 padding
    public static byte[] encrypt(byte[] plainText, byte[] key, byte[] iv)
    {
        AES aes = new AES();
        aes.init(AES.CBC, key.length * 8, key, iv); // Initialize AES in CBC mode with key and IV

        // Apply PKCS7 padding
        plainText = pkcs7Padding(plainText, 16);

        //  byte[] cipherText = new byte[plainText.length];
        if (aes.encrypt(plainText) == 0) // Perform encryption in place
        {
            return plainText;
        }
        else
        {
            System.out.println("Encryption failed");
            return null;
        }
    }

    // AES Decryption in CBC mode with PKCS7 padding removal
    public static byte[] decrypt(byte[] cipherText, byte[] key, byte[] iv)
    {
        AES aes = new AES();
        aes.init(AES.CBC, key.length * 8, key, iv); // Initialize AES in CBC mode with key and IV

        //  byte[] decryptedText = new byte[cipherText.length];
        if (aes.decrypt(cipherText) == 0)
        {
            // Remove PKCS7 padding
            byte[] decryptedText = removePkcs7Padding(cipherText);
            return decryptedText;
        }
        else
        {
            System.out.println("Decryption failed");
            return null;
        }

        // Remove PKCS7 padding
        //  decryptedText = removePkcs7Padding(decryptedText);
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
