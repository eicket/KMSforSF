import org.miracl.core.BN158.BLS;
import org.miracl.core.RAND;

// adapted from core/test/java/org/miracl/core/BN158/TestBLS.java
public class TestBLS
{

    public static void main(String[] args)
    {
        System.out.printf("go ...");

        testBLS();
    }

    private static void printBinary(byte[] array)
    {
        int i;
        for (i = 0; i < array.length; i++)
        {
            System.out.printf("%02x", array[i]);
        }
        System.out.println();
    }

    public static void testBLS()
    {
        RAND rng = new RAND();

        int BGS = BLS.BGS; //20
        int BFS = BLS.BFS; // 20
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

        int res = BLS.init();
        if (res != 0)
        {
            System.out.println("Failed to initialize");
        }

        String mess = new String("This is a test message from erik");
        System.out.println("Message : " + mess);

        res = BLS.KeyPairGenerate(IKM, S, W);
        if (res != 0)
        {
            System.out.println("Failed to Generate Keys");
        }
        System.out.print("Private key : 0x");
        printBinary(S);
        System.out.print("Public  key : 0x");
        printBinary(W);

        BLS.core_sign(SIG, mess.getBytes(), S);
        System.out.print("Signature : 0x");
        printBinary(SIG);

        res = BLS.core_verify(SIG, mess.getBytes(), W);

        if (res == 0)
        {
            System.out.println("Signature is OK");
        }
        else
        {
            System.out.println("Signature is *NOT* OK");
        }

    }
}
