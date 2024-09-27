
import org.miracl.core.HASH256;

public class TestSHA256
{

    /* test program: should produce digest */
    // 248d6a61 d20638b8 e5c02693 0c3e6039 a33ce459 64ff2167 f6ecedd4 19db06c1
    public static void main(String[] args)
    {
        byte[] test = "o3iR0WM8I".getBytes();
        byte[] digest;
        int i;
        HASH256 sh = new HASH256();

        for (i = 0; i < test.length; i++)
        {
            sh.process(test[i]);
        }

        digest = sh.hash();
        for (i = 0; i < 32; i++)
        {
            System.out.format("%02x", digest[i]);
        }

        //	for (i=0;i<32;i++) System.out.format("%d ",digest[i]);
        System.out.println("");
    }
}
