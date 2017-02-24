import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;


/**
 * TODO
 * - Réparer acces mouvement au tableau (tray)
 * -  index alloc abandon sur trayIdToTraysMap tray[]
 * <p>
 * http://stackoverflow.com/questions/430346/why-doesnt-java-support-unsigned-ints
 */
public class Core {

    private static final int _0000 = 0b0000;
    private static final int _0001 = 0b0001;
    private static final int _0010 = 0b0010;
    private static final int _0011 = 0b0011;
    private static final int _0100 = 0b0100;
    private static final int _0101 = 0b0101;
    private static final int _0110 = 0b0110;
    private static final int _0111 = 0b0111;
    private static final int _1000 = 0b1000;
    private static final int _1001 = 0b1001;
    private static final int _1010 = 0b1010;
    private static final int _1011 = 0b1011;
    private static final int _1100 = 0b1100;
    private static final int _1101 = 0b1101;
    private static final char _ONE = '1';
    private static final char _ZERO = '0';
    private static long _mod32 = (long) Math.pow(2, 32);
    // registre capable de stocker un plateau
    private int register[];
    private Map<Integer, int[]> trayIdToTraysMap;
    private int pc = 0;
    private List<Integer> removedKeys;
    private int keys;
//tree map 426
// hashTable 268
//67 linkedmap

    // A 8-6 B 5-3 C 2-0
    public Core() {
        register = new int[8];
        trayIdToTraysMap = new LinkedHashMap<>();
        removedKeys = new LinkedList<>();
        //removedKeys = new ArrayList<>();
    }

    public static void main(String args[]) {
/*        String filename = args[0];
        boolean encrypted = Boolean.parseBoolean(args[1]);
        String key;
        if (encrypted) {
            key = args[2];
        }*/
        Core core = new Core();
        core.init("file/sandmark.umz");
        long date = System.currentTimeMillis();
        System.out.println("Time start " + new Date(date));
        core.run();
        System.out.println("Time start " + (System.currentTimeMillis() - date) / 1000);
    }

    private static String toBinaryString(int i) {
        String s = Integer.toBinaryString(i);
        StringBuilder sb = new StringBuilder(s);
        int numZeros = 32 - s.length();
        while (numZeros-- > 0) {
            sb.insert(0, _ZERO);
        }
        return sb.toString();
    }

    private void run() {
        try {


            String binTab;
            while (pc < trayIdToTraysMap.get(0).length) {

                int[] ops = trayIdToTraysMap.get(0);
                if (ops == null) {
                    throw new RuntimeException("Stupid moron");
                }
            /* décodage */
                int n32 = ops[pc];
            /*binTab = toBinaryString(ops[pc]);*/
            /*int op = getOperation(binTab);*/
                int op = (n32 >> 28) & 0b1111;
                if (Objects.equals(op, _1101)) {
                /*int ia = getSegmentASpe(binTab);*/
                    int ia = (n32 >> 25) & 0b111;
                /*int segValue = getValue(binTab);*/
                    int segValue = (n32 & 0b1111111111111111111111111);
                    orthography(ia, segValue);
                    pc++;
                } else {
                /*int ia = getSegmentA(binTab);
                int ib = getSegmentB(binTab);
                int ic = getSegmentC(binTab);*/
                    //shift left 3 take the mask
                    int ia = ((n32 >> 6) & 0b111);
                    int ib = ((n32 >> 3) & 0b111);
                    int ic = (n32 & 0b111);


                    switch (op) {
                        case _0000:    //0
                            tryMove(ia, ib, ic);
                            pc++;
                            break;
                        case _0001:    //1
                            index(ia, ib, ic);
                            pc++;
                            break;
                        case _0010:    //2
                            amendment(ia, ib, ic);
                            pc++;
                            break;
                        case _0011:    //3
                            add(ia, ib, ic);
                            pc++;
                            break;
                        case _0100:    //4
                            mul(ia, ib, ic);
                            pc++;
                            break;
                        case _0101:    //5
                            div(ia, ib, ic);
                            pc++;
                            break;
                        case _0110:    //6
                            notAnd(ia, ib, ic);
                            pc++;
                            break;
                        case _0111:    //7
                            //stop();
                            throw new Exception();


                        case _1000:    //8
                            allocationTab(ib, ic);
                            pc++;
                            break;
                        case _1001:    //9
                            giveUp(ic);
                            pc++;
                            break;
                        case _1010:    //10
                            print(ic);
                            pc++;
                            break;
                        case _1011:    //11
                            input(ic);
                            pc++;
                            break;
                        case _1100:    //12
                            load(ib, ic);
//ne pas incremnte le pc ici
                            break;
                    }
                }
                // System.out.println(pc);
            }
        } catch (Exception e) {

        }
    }

    /**
     * Initialize the 0 platter with a list of instruction
     */
    private void init(String fileName) {
        /*
         * All registers shall be initialized with platters of value '0'.
         */
        Arrays.fill(register, 0);
        /*
         * The execution finger shall point to the first platter of the '0' array, which has offset zero.
         */
        pc = 0;

        /*
         * The machine shall be initialized with a '0' array whose contents shall be read from a "program" scroll.
         */
        int[] ints = loadUmz(fileName);
        trayIdToTraysMap.put(0, ints);


    }

    private int getOperation(String binTab) {
        return Integer.parseUnsignedInt(binTab.substring(0, 4), 2);
    }

    private int getSegmentASpe(String binTab) {
        return Integer.parseUnsignedInt(binTab.substring(4, 7), 2);
    }

    private int getValue(String binTab) {
        return Integer.parseUnsignedInt(binTab.substring(7, binTab.length()), 2);

    }

    /**
     * #13 Orthography
     * The value indicated is loaded into the register A
     * forthwith.
     *
     * @param ia    index of register A 3bit
     * @param value value 25 bit
     */
    private void orthography(int ia, int value) {
        register[ia] = value;

    }

    //Transform binary string
    private int getSegmentA(String binTab) {
        return Integer.parseUnsignedInt(binTab.substring(23, 26), 2);
    }

    private int getSegmentB(String binTab) {
        return Integer.parseUnsignedInt(binTab.substring(26, 29), 2);
    }

    private int getSegmentC(String binTab) {
        return Integer.parseUnsignedInt(binTab.substring(29, binTab.length()), 2);
    }

    /**
     * #0 Conditional Move
     * The register A receives the value in register B,
     * unless the register C contains 0.
     *
     * @param ia index of register A 3bit
     * @param ib index of register B 3bit
     * @param ic index of register C 3bit
     */
    private void tryMove(int ia, int ib, int ic) {
        if (register[ic] != 0) {
            register[ia] = register[ib];
        }
    }

    /**
     * #1 Array Index
     * The register A receives the value stored at offset
     * in register C in the array identified by B.
     *
     * @param ia index of register A 3bit
     * @param ib index of register B 3bit
     * @param ic index of register C 3bit
     */
    private void index(int ia, int ib, int ic) {
        //index of the array in the map
        int rc = register[ic];
        //index of the platter in the array
        int rb = register[ib];
        //store the array in register A
        int[] lIntegers = trayIdToTraysMap.get(rb);
        register[ia] = lIntegers[rc];
    }

    /**
     * #2 Array amendment
     * The array identified by A is amended at the offset
     * in register B to store the value in register C.
     *
     * @param ia index of register A 3bit
     * @param ib index of register B 3bit
     * @param ic index of register C 3bit
     */
    private void amendment(int ia, int ib, int ic) {
        //index of the array in the map
        int ra = register[ia];
        //index of the platter in the array
        int rb = register[ib];
        //get the array
        int[] lIntegers = trayIdToTraysMap.get(ra);
        //put value of register C in the array at pos rb
        lIntegers[rb] = register[ic];


    }

    /**
     * #3 Addition
     * The register A receives the value in register B plus
     * the value in register C, modulo 2^32.
     *
     * @param ia index of register A 3bit
     * @param ib index of register B 3bit
     * @param ic index of register C 3bit
     */
    private void add(int ia, int ib, int ic) {
        int rb = register[ib];
        int rc = register[ic];

        register[ia] = (rb + rc);
    }

    /**
     * #4 Multiplication
     * The register A receives the value in register B times
     * the value in register C, modulo 2^32.
     *
     * @param ia index of register A 3bit
     * @param ib index of register B 3bit
     * @param ic index of register C 3bit
     */
    private void mul(int ia, int ib, int ic) {
        int rb = register[ib];
        int rc = register[ic];
        register[ia] = (rb * rc);
    }

    /**
     * #5 Division
     * The register A receives the value in register B
     * divided by the value in register C, if any, where
     * each quantity is treated treated as an unsigned 32
     * bit number.
     *
     * @param ia index of register A 3bit
     * @param ib index of register B 3bit
     * @param ic index of register C 3bit
     */
    private void div(int ia, int ib, int ic) {
        int rb = register[ib];
        int rc = register[ic];

        register[ia] = Integer.divideUnsigned(rb, rc);
    }

    /**
     * #6 NotAnd
     * Each bit in the register A receives the 1 bit if
     * either register B or register C has a 0 bit in that
     * position.  Otherwise the bit in register A receives
     * the 0 bit.
     *
     * @param ia index of register A 3bit
     * @param ib index of register B 3bit
     * @param ic index of register C 3bit
     */
    private void notAnd(int ia, int ib, int ic) {
        /*String rb = toBinaryString(register[ib]);
        String rc = toBinaryString(register[ic]);
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < rb.length(); i++) {
            if ((rb.charAt(i) == '1') && (rc.charAt(i) == '1')) {
                tmp.append(_ZERO);
            } else {
                tmp.append(_ONE);
            }
        }
        register[ia] = Integer.parseUnsignedInt(tmp.toString(), 2);*/
        register[ia] = ~(register[ib] & register[ic]);
    }

    /**
     * #7 Stop
     */
    private void stop() {
        System.exit(3);

    }

    /**
     * #8
     * create new array
     *
     * @param ib index of the new array in the map
     * @param ic size of the new array
     */
    private void allocationTab(int ib, int ic) {
        //System.out.println("ib = [" + ib + "], ic = [" + ic + "]");
        //get register at index ic
        int rc = register[ic];
        int[] newArray = new int[rc];
        int newID = genArrayID();
        //insert into map
        trayIdToTraysMap.put(newID, newArray);
        //store the new id in register b
        register[ib] = newID;
    }

    /**
     * #9 Give Up
     *
     * @param ic Id of the array in the map
     */
    private void giveUp(int ic) {
        //retrieve the corresponding key
        int rc = register[ic];
        trayIdToTraysMap.remove(rc);
        //update removedkeys
        removedKeys.add(rc);
    }

    /**
     * #10 Print ascii character store in register
     *
     * @param ic index of register c 3bit
     */
    private void print(int ic) {
        int rc = register[ic];
        if (rc < 0 || rc > 255) throw new RuntimeException("Stupid moron");
        System.out.print((char) rc);
    }

    /**
     * #11. Input.
     * The universal machine waits for input on the console.
     * When input arrives, the register C is loaded with the
     * input, which must be between and including 0 and 255.
     * If the end of input has been signaled, then the
     * register C is endowed with a uniform value pattern
     * where every place is pregnant with the 1 bit.
     */

    private void input(int ic) {
        /*TODO                   If the end of input has been signaled */
        Scanner scanner = new Scanner(System.in);
        System.out.println("ic = [" + ic + "] enter");
        int rc = scanner.nextInt();
        if (rc < 0 || rc > 255) throw new RuntimeException("Stupid moron");
        register[ic] = rc;


    }

    /**
     * #12 Load Program and move the pc to the value of register C
     *
     * @param ib index of register B 3bit
     * @param ic index of register C 3bit
     */
    private void load(int ib, int ic) {

        //load register B
        int idB = register[ib];

        //retrieve the array associated to idb(register[ib])
        int array[] = trayIdToTraysMap.get(idB);
        //clone array
        int[] arrayClone = array;
        if (idB != 0) {
            arrayClone = array.clone();
        }
        //insert cloned array at index 0 of the mastercollection

        trayIdToTraysMap.put(0, arrayClone);
        //execution finger is moved to the value register C
        pc = register[ic];
    }

    /**
     * Generate a unique id for trayIdToTraysMap
     * Throw runtimeException if the trayIdToTraysMap is full
     *
     * @return int 32bit int
     */
    private int genArrayID() {
        Integer key;
        if (!removedKeys.isEmpty()) {
            key = removedKeys.get(0);
            removedKeys.remove(0);
        } else {
            if (keys == Integer.MAX_VALUE) {
                throw new RuntimeException("No more space for allocation");
            }

            keys++;
            key = keys;
        }
        return key;
    }

    private int[] loadUmz(String filePath) {
        File f;
        FileInputStream br = null;
        Byte defaultByte = (byte) 0;
        long start = System.currentTimeMillis();
        try {
            f = new File(filePath);
            byte[] bytes = new byte[(int) f.length()];
            br = new FileInputStream(f);
            System.out.println(br.read(bytes));

            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            for (int i = 0; i < f.length() % 4; ++i) {
                byteBuffer = byteBuffer.put(defaultByte);
            }
            IntBuffer intBuffer = byteBuffer.asIntBuffer();
            int[] array = new int[intBuffer.limit()];
            System.out.println(intBuffer.get(array));
            System.out.println("Load umz in " + (System.currentTimeMillis() - start) / 1000);
            return array;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return new int[0];
    }
}