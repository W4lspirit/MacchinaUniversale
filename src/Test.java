/**
 * Created by Ismael on 13/02/2017.
 */
public class Test {
    public static void main(String args[]) {
        String lS = "00100001010001011010000101000100";
        int anInt1 = 0b00100001010001011010000101000100;
        int lI = Integer.parseUnsignedInt(lS, 0b10);
        System.out.println(lS + "==" + lI + (anInt1 == lI) + " bianry" + anInt1 + " " + Integer.toBinaryString(lI));
        Integer lInteger = new Integer(anInt1);
        System.out.println(lInteger.byteValue());


        int first = (lI & 1);
        System.out.println(Integer.rotateRight(lI, 4));
        print(first);
        int second = ((lI >> 4) & 1);
        print(second);
        int third = ((lI >> 8) & 1);
        print(third);
        int fourth = ((lI >> 12) & 1);
        print(fourth);
        print(lI);
        System.out.println(first + " " + second + " " + third + " " + fourth);
        System.out.println("VUTSRQPONMLKJIHGFEDCBA9876543210".substring(7, 32));
        System.out.println((int) ConstantHolder._mod32 - 1);
        int rb = (int) ((anInt1 * lI) % ConstantHolder._mod32);
        //TODO  negative value ... need to be unsigned
        int n = 0b11111111111111111111111111111100;
        System.out.println(n);
        System.out.println(Integer.parseUnsignedInt("+1111111111111111111111111111100"));

    }


    private static void print(int pS) {
        System.out.println(Integer.toBinaryString(pS));
    }
}
