import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


/**
 * TODO
 * - Réparer acces mouvement au tableau (tray)
 * -  index alloc abandon sur trayIdToTraysMap tray[]
 * <p>
 * http://stackoverflow.com/questions/430346/why-doesnt-java-support-unsigned-ints
 */
public class Core {

    // registre capable de stocker un plateau
    private int register[];
    private Map<Integer, int[]> trayIdToTraysMap;
    private int pc = 0;


    private List<Integer> removedKeys;
    private int keys;
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    // A 8-6 B 5-3 C 2-0
    public Core() {
        register = new int[8];
        trayIdToTraysMap = new HashMap<>();
        removedKeys = new ArrayList<>();
    }


    public void run() {
        init();
        String binTab = "";
        while (true) {
            /* décodage */
            int op = getOperation(binTab);
            if (Objects.equals(op, ConstantHolder._1101)) {
                int ia = getSegmentASpe(binTab);
                int segValue = getValue(binTab);
                orthography(ia, segValue);
            } else {
                int ia = getSegmentA(binTab);
                int ib = getSegmentB(binTab);
                int ic = getSegmentC(binTab);


                switch (op) {
                    case ConstantHolder._0000:    //0
                        tryMove(ia, ib, ic);
                        pc++;
                        break;
                    case ConstantHolder._0001:    //1
                        index(ia, ib, ic);
                        pc++;
                        break;
                    case ConstantHolder._0010:    //2
                        amendment(ia, ib, ic);
                        pc++;
                        break;
                    case ConstantHolder._0011:    //3
                        add(ia, ib, ic);
                        pc++;
                        break;
                    case ConstantHolder._0100:    //4
                        mul(ia, ib, ic);
                        pc++;
                        break;
                    case ConstantHolder._0101:    //5
                        div(ia, ib, ic);
                        pc++;
                        break;
                    case ConstantHolder._0110:    //6
                        notAnd(ia, ib, ic);
                        pc++;
                        break;
                    case ConstantHolder._0111:    //7
                        stop();
                        pc++;
                        break;
                    case ConstantHolder._1000:    //8
                        allocationTab(ib, ic);
                        pc++;
                        break;
                    case ConstantHolder._1001:    //9
                        giveUp(ic);
                        pc++;
                        break;
                    case ConstantHolder._1010:    //10
                        print(ic);
                        pc++;
                        break;
                    case ConstantHolder._1011:    //11
                        input(ic, getValue(binTab));
                        pc++;
                        break;
                    case ConstantHolder._1100:    //12
                        load(ib, ic);
                        pc++;
                        break;
                }
            }
        }
    }

    private void init() {
        pc = 0;
        // TODO load all scrolls into trayIdToTraysMap start at index 0

    }

    private int getOperation(String binTab) {
        return Integer.parseUnsignedInt(binTab.substring(0, 4));
    }

    private int getSegmentASpe(String binTab) {
        return Integer.parseUnsignedInt(binTab.substring(4, 7));
    }

    private int getValue(String binTab) {
        return Integer.parseUnsignedInt(binTab.substring(7, binTab.length()));

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
        return Integer.parseUnsignedInt(binTab.substring(23, 26));
    }

    private int getSegmentB(String binTab) {
        return Integer.parseUnsignedInt(binTab.substring(26, 29));
    }

    private int getSegmentC(String binTab) {
        return Integer.parseUnsignedInt(binTab.substring(29, binTab.length()));
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
        int[] lIntegers = trayIdToTraysMap.get(rc);
        register[ia] = lIntegers[rb];
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

        register[ia] = (int) ((rb + rc) % ConstantHolder._mod32);
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
        register[ia] = (int) ((rb * rc) % ConstantHolder._mod32);
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
        String rb = Integer.toBinaryString(register[ib]);
        String rc = Integer.toBinaryString(register[ic]);
        String tmp = "";
        for(int i = 0; i < rb.length(); i++) {
        	if ((rb.charAt(i) == '0') && (rc.charAt(i) == '0')) {
        		tmp += "1";
        	} else {
        		tmp += "0";
        	}
        }
        register[ia] = Integer.parseUnsignedInt(tmp);
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
        System.out.println((char) rc);
    }
    
    public static String hexlify(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        String ret = new String(hexChars);
        return ret;
}

    private void loadUmz(File file_path) {

    	BufferedReader br = null;
    	ArrayList<Integer> res = new ArrayList<>();
    	try {
    		br = new BufferedReader(new FileReader(file_path));
    		int tmp = Integer.parseInt(hexlify(br.toString().getBytes()));
    	    res.add(tmp);
    	} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
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

    private void input(int ic, int rc) {
        /*TODO                   If the end of input has been signaled */
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
        int[] arrayClone = array.clone();
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


    public void main(String args[]) {
        String lS = "10100001010001011010000101000101";
        int anInt1 = 0b10100001010001011010000101000101;
        int lI = Integer.parseUnsignedInt(lS);
        System.out.println(lS + "==" + lI + (anInt1 == lI));


    }
}