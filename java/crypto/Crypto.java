package crypto;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Logger;
import org.miracl.core.HASH256;
import org.miracl.core.RAND;

public class Crypto
{

    static final Logger logger = Logger.getLogger(Crypto.class.getName());

    public String encodedPrivateKey;
    public String encodedPublicKey;

    public Crypto()
    {
        int res = org.miracl.core.BN158.BLS.init();
        if (res != 0)
        {
            logger.severe("Failed to initialize");
        }
    }

    public void createKeyPair()
    {
        RAND rng = new RAND();

        int BGS = org.miracl.core.BN158.BLS.BGS; //20
        int BFS = org.miracl.core.BN158.BLS.BFS; // 20
        int G1S = BFS + 1;
        /* Group 1 Size - compressed */
        int G2S = 2 * BFS + 1;
        /* Group 2 Size - compressed */

        byte[] S = new byte[BGS];
        byte[] W = new byte[G2S];
        byte[] SIG = new byte[G1S];
        byte[] RAW = new byte[100];
        byte[] IKM = new byte[32];

        rng.clean();
        SecureRandom secureRandom = new SecureRandom();

        secureRandom.nextBytes(RAW);

        rng.seed(100, RAW);

        for (int i = 0; i < IKM.length; i++)
        //IKM[i]=(byte)(i+1);
        {
            IKM[i] = (byte) rng.getByte();
        }

        /*
        int res = org.miracl.core.BN158.BLS.init();
        if (res != 0)
        {
            logger.severe("Failed to initialize");
        }
         */
        int res = org.miracl.core.BN158.BLS.KeyPairGenerate(IKM, S, W);
        if (res != 0)
        {
            logger.severe("Failed to Generate Keys");
        }

        logger.info("Private key : 0x" + bytesToHex(S));
        encodedPrivateKey = Base64.getEncoder().encodeToString(S);

        logger.info("Public key : 0x" + bytesToHex(W));
        encodedPublicKey = Base64.getEncoder().encodeToString(W);

    }

    public void sign(byte[] signature, String message, String encodedPrivateKey)
    {
        byte[] decodedPrivateKey = Base64.getDecoder().decode(encodedPrivateKey);
        logger.info("sign private key : 0x" + bytesToHex(decodedPrivateKey) + ", length : " + decodedPrivateKey.length);

        org.miracl.core.BN158.BLS.core_sign(signature, message.getBytes(), decodedPrivateKey);
        logger.info("sign signature : 0x" + bytesToHex(signature) + ", length : " + signature.length);
    }

    // returns 0 for ok, 1 for nok
    public int verify(byte[] signature, String message, String encodedPublicKey)
    {
        byte[] decodedPublicKey = Base64.getDecoder().decode(encodedPublicKey);
        logger.info("verify message : " + message);
        logger.info("verify public key : 0x" + bytesToHex(decodedPublicKey) + ", length : " + decodedPublicKey.length);
        logger.info("verify signature : 0x" + bytesToHex(signature) + ", length : " + signature.length);

        return (org.miracl.core.BN158.BLS.core_verify(signature, message.getBytes(), decodedPublicKey));
    }

    // used to make a 256 bit encryption key from the password
    public static byte[] computeSHA256(byte[] input)
    {
        HASH256 sha256 = new HASH256();

        for (int i = 0; i < input.length; i++)
        {
            sha256.process(input[i]);
        }

        byte[] digest = sha256.hash();
        
        /*
        for (int i = 0; i < 32; i++)
        {
            System.out.format("%02x", digest[i]);
        }
*/        
        return digest;
    }

    public static void testBLS()
    {
        RAND rng = new RAND();

        int BGS = org.miracl.core.BN158.BLS.BGS; //20
        int BFS = org.miracl.core.BN158.BLS.BFS; // 20
        int G1S = BFS + 1;
        /* Group 1 Size - compressed */
        int G2S = 2 * BFS + 1;
        /* Group 2 Size - compressed */

        byte[] S = new byte[BGS];
        byte[] W = new byte[G2S];
        byte[] SIG = new byte[G1S];
        byte[] RAW = new byte[100];
        byte[] IKM = new byte[32];

        rng.clean();
        for (int i = 0; i < 100; i++)
        {
            RAW[i] = (byte) (i);
        }
        rng.seed(100, RAW);

        for (int i = 0; i < IKM.length; i++)
        //IKM[i]=(byte)(i+1);
        {
            IKM[i] = (byte) rng.getByte();
        }

        System.out.println("\nTesting BLS code");

        int res = org.miracl.core.BN158.BLS.init();
        if (res != 0)
        {
            System.out.println("Failed to initialize");
        }

        String mess = new String("This is a test message from erik");
        System.out.println("Message : " + mess);

        res = org.miracl.core.BN158.BLS.KeyPairGenerate(IKM, S, W);
        if (res != 0)
        {
            System.out.println("Failed to Generate Keys");
        }
        System.out.print("Private key : 0x");
        printBinary(S);
        System.out.print("Public  key : 0x");
        printBinary(W);

        org.miracl.core.BN158.BLS.core_sign(SIG, mess.getBytes(), S);
        System.out.print("Signature : 0x");
        printBinary(SIG);

        res = org.miracl.core.BN158.BLS.core_verify(SIG, mess.getBytes(), W);

        if (res == 0)
        {
            System.out.println("Signature is OK");
        }
        else
        {
            System.out.println("Signature is *NOT* OK");
        }

    }

    public static void printBinary(byte[] array)
    {
        int i;
        for (i = 0; i < array.length; i++)
        {
            System.out.printf("%02x", array[i]);
        }
        System.out.println();
    }

    public static String bytesToHex(byte[] bytes)
    {
        final char[] hexArray =
        {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++)
        {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
