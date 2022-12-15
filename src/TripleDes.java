import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public class TripleDes {
    public String key;
    public String vectorInit;
    public String[] rounds = new String[3];

    public String encryptBlock(String block, String key) throws Exception {
        DES des1 = new DES();
        DES des2 = new DES();
        DES des3 = new DES();
        rounds[0] = des3.encrypt(block,key.substring(0, 64));
        rounds[1] = des2.encrypt(rounds[0],key.substring(64, 128));
        rounds[2] = des1.encrypt(rounds[1], key.substring(128, 192));
        return des1.encrypt(des2.encrypt(des3.encrypt(block, key.substring(0, 64)), key.substring(64, 128)), key.substring(128, 192));
    }

    public String decryptBlock(String block, String key) throws Exception {
        DES des1 = new DES();
        DES des2 = new DES();
        DES des3 = new DES();
        return des1.decrypt(des2.decrypt(des3.decrypt(block, key.substring(128, 192)), key.substring(64, 128)), key.substring(0, 64));
    }

    public byte[] encrypt(byte[] data, String key) throws Exception {
        key = generateKey(key);
        int len = data.length;
        if (data.length == 0) {
            throw new RuntimeException("data null!!");
        }
        if (data.length % 8 != 0) {
            while (len % 8 != 0) {
                len++;
            }
        }
        byte[] dataTmp = Arrays.copyOf(data, len);
        byte[] resultByte = new byte[len];
        for (int i = 0; i < len; i += 8) {
            byte[] tmp = new byte[8];
            for (int j = 0; j < 8; j++) {
                tmp[j] = dataTmp[i + j];
            }
            String strBit = Convert.utfToBin(tmp);

            String outBit = encryptBlock(strBit, key);

            byte[] outByte = Convert.binToByte(outBit);
            for (int j = 0; j < 8; j++) {
                resultByte[i + j] = outByte[j];
            }
        }
        return resultByte;
    }

        public byte[] decrypt(byte[] data, String key) throws Exception {
            if (key.equals("")) {
                throw new RuntimeException("Key error");
            }
            int len = data.length;
            if (data.length % 8 != 0) {
                while (len % 8 != 0) {
                    len++;
                }
            }
            byte[] dataTmp = Arrays.copyOf(data, len);
            byte[] resultByte = new byte[len];
            for (int i = 0; i < len; i += 8) {
                byte[] tmp = new byte[8];
                System.arraycopy(dataTmp, i, tmp, 0, 8);

                String strBit = Convert.utfToBin(tmp);

                String outBit = decryptBlock(strBit, key);

                byte[] outByte = Convert.binToByte(outBit);
                System.arraycopy(outByte, 0, resultByte, i, 8);
            }
            return resultByte;
        }

    public String generateKey(String key) {
        if (key == null || key.length() == 0) {
            key = generateRandomKey();
        }
        StringBuilder keyEnd = new StringBuilder(Convert.utfToBin(key));
        if (keyEnd.length() != 192) {
            throw new RuntimeException("key != 192 in TripleDes");
        }
        this.key = keyEnd.toString();
        return keyEnd.toString();
    }

    public String generateRandomKey() {
        return new Random().ints(24, 33, 122).mapToObj(i -> String.valueOf((char) i)).collect(Collectors.joining());
    }

    public byte[] encryptOFB(byte[] data, String key) throws Exception {
        key = generateKey(key);
        int len = data.length;
        if (data.length == 0) {
            throw new RuntimeException("data null!!");
        }
        if (data.length % 8 != 0) {
            while (len % 8 != 0) {
                len++;
            }
        }
        byte[] dataTmp = Arrays.copyOf(data, len);
        byte[] resultByte = new byte[len];

        vectorInit = "0000000000000000000000000000000000000000000000000000000000000000";
        String outBit = vectorInit;

        for (int i = 0; i < len; i += 8) {

            byte[] block = new byte[8];
            System.arraycopy(dataTmp, i, block, 0, 8);

            String blockBit = Convert.utfToBin(block);

            outBit = encryptBlock(outBit, key);

            String outBlock = Convert.stringXOR(outBit, blockBit);

            byte[] outByte = Convert.binToByte(outBlock);
            System.arraycopy(outByte, 0, resultByte, i, 8);
        }
        return resultByte;
    }

    public byte[] decryptOFB(byte[] data, String key, String vectorInit) throws Exception {
        int len = data.length;
        if (data.length == 0) {
            throw new RuntimeException("data null!!");
        }
        if (data.length % 8 != 0) {
            while (len % 8 != 0) {
                len++;
            }
        }
        byte[] dataTmp = Arrays.copyOf(data, len);
        byte[] resultByte = new byte[len];

        String outBit = vectorInit;

        for (int i = 0; i < len; i += 8) {

            byte[] block = new byte[8];
            System.arraycopy(dataTmp, i, block, 0, 8);

            String blockBit = Convert.utfToBin(block);

            outBit = encryptBlock(outBit, key);

            String outBlock = Convert.stringXOR(outBit, blockBit);

            byte[] outByte = Convert.binToByte(outBlock);
            System.arraycopy(outByte, 0, resultByte, i, 8);
        }
        return resultByte;
    }
}
