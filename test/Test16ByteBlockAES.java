
import org.miracl.core.AES;
import static org.miracl.core.AES.CTR16;

public class Test16ByteBlockAES
{

    public static void main(String[] args)
    {
        String plainText = "This is a secret message";

        int i;

        byte[] key = new byte[32];
        byte[] block = new byte[16];
        byte[] iv = new byte[16];

        for (i = 0; i < 32; i++)
        {
            key[i] = 0;
        }
        key[0] = 1;
        for (i = 0; i < 16; i++)
        {
            iv[i] = (byte) i;
        }
        for (i = 0; i < 16; i++)
        {
            block[i] = (byte) i;
        }

        AES a = new AES();

        a.init(CTR16, 32, key, iv);
        System.out.println("Plain= ");
        for (i = 0; i < 16; i++)
        {
            System.out.format("%02X ", block[i] & 0xff);
        }
        System.out.println("");

        a.encrypt(block);

        System.out.println("Encrypt= ");
        for (i = 0; i < 16; i++)
        {
            System.out.format("%02X ", block[i] & 0xff);
        }
        System.out.println("");

        a.reset(CTR16, iv);
        a.decrypt(block);

        System.out.println("Decrypt= ");
        for (i = 0; i < 16; i++)
        {
            System.out.format("%02X ", block[i] & 0xff);
        }
        System.out.println("");

        a.end();
    }
}

/*

        // Generate a random key (128-bit for AES-128)
        byte[] key = new byte[16];
        RAND rng = new RAND();
        rng.clean();
        rng.seed(100, new byte[100]);
        rng.getBytes(key);

        // Generate random IV (16 bytes)
        byte[] iv = new byte[16];
        rng.getBytes(iv);

        // Encrypt the plaintext
        byte[] encrypted = encrypt(plainText.getBytes(), key, iv);
        System.out.println("Encrypted (Base64): " + Base64.getEncoder().encodeToString(encrypted));

        // Decrypt the ciphertext
        byte[] decrypted = decrypt(encrypted, key, iv);
        System.out.println("Decrypted Text: " + new String(decrypted));
    }

    // AES Encryption in CBC mode with PKCS7 padding
    public static byte[] encrypt(byte[] plainText, byte[] key, byte[] iv)
    {
        AES aes = new AES();
        aes.init(AES.CBC, key.length * 8, key, iv); // AES initialization (CBC mode)

        // Apply PKCS7 padding (manually, as MIRACL doesn't add padding)
        plainText = pkcs7Padding(plainText, 16);

        byte[] cipherText = new byte[plainText.length];
        aes.encrypt(plainText, cipherText);  // Perform encryption

        return cipherText;
    }

    // AES Decryption in CBC mode with PKCS7 padding removal
    public static byte[] decrypt(byte[] cipherText, byte[] key, byte[] iv)
    {
        AES aes = new AES();
        aes.init(AES.CBC, key.length * 8, key, iv); // AES initialization (CBC mode)

        byte[] decryptedText = new byte[cipherText.length];
        aes.decrypt(cipherText, decryptedText);  // Perform decryption

        // Remove PKCS7 padding
        decryptedText = removePkcs7Padding(decryptedText);
        return decryptedText;
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

}*/
