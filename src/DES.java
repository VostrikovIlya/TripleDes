public class DES {
    public long[] key = new long[16];
    public String[] rounds = new String[16];

    public  String encrypt(String block, String key) throws Exception {
        buildKeySchedule(Long.parseLong(key,2));
        return encryptBlock(block);
    }

    public String decrypt(String block, String key) throws Exception {
        buildKeySchedule(Long.parseLong(key,2));
        return decryptBlock(block);
    }

    public String encryptBlock(String plaintextBlock) throws Exception {
        int length = plaintextBlock.length();
        if (length != 64)
            throw new RuntimeException("Input block length is not 64 bits!");

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < Table.IP.length; i++) {
            out.append(plaintextBlock.charAt(Table.IP[i] - 1));
        }
        String mL = out.substring(0, 32);
        String mR = out.substring(32);

        for (int i = 0; i < 16; i++) {
            String fResult =  f(mR, key[i]);

            long f = Long.parseLong(fResult, 2);
            long cmL = Long.parseLong(mL, 2);

            long m2 = cmL ^ f;
            StringBuilder m2String = new StringBuilder(Long.toBinaryString(m2));

            while (m2String.length() < 32)
                m2String.insert(0, "0");

            mL = mR;
            mR = m2String.toString();
            rounds[i] = mR + mL;
        }

        String in = mR + mL;
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < Table.IPi.length; i++) {
            output.append(in.charAt(Table.IPi[i] - 1));
        }
        return output.toString();
    }

    public String decryptBlock(String plaintextBlock) throws RuntimeException {
        int length = plaintextBlock.length();
        if (length != 64)
            throw new RuntimeException("Input block length is not 64 bits!");

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < Table.IP.length; i++) {
            out.append(plaintextBlock.charAt(Table.IP[i] - 1));
        }
        String mL = out.substring(0, 32);
        String mR = out.substring(32);

        for (int i = 16; i > 0; i--) {
            String fResult = f(mR, key[i-1]);

            long f = Long.parseLong(fResult, 2);
            long cmL = Long.parseLong(mL, 2);

            long m2 = cmL ^ f;
            StringBuilder m2String = new StringBuilder(Long.toBinaryString(m2));

            while (m2String.length() < 32)
                m2String.insert(0, "0");

            mL = mR;
            mR = m2String.toString();

        }

        String in = mR + mL;
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < Table.IPi.length; i++) {
            output.append(in.charAt(Table.IPi[i] - 1));
        }
        return output.toString();
    }

    public void buildKeySchedule(long basicKey) {
        StringBuilder binKey = new StringBuilder(Long.toBinaryString(basicKey));
        while (binKey.length() < 64)
            binKey.insert(0, "0");

        StringBuilder binKey_PC1 = new StringBuilder();
        for (int i = 0; i < Table.PC1.length; i++)
            binKey_PC1.append(binKey.charAt(Table.PC1[i] - 1));

        String sL, sR;
        sL = binKey_PC1.substring(0, 28);
        sR = binKey_PC1.substring(28);

        for (int i = 0; i < key.length; i++) {
            sL = rotateLeft(sL,Table.KEY_SHIFTS[i]);
            sR = rotateLeft(sR,Table.KEY_SHIFTS[i]);
            String sMerged = sL + sR;

            StringBuilder binKey_PC2 = new StringBuilder();
            for (int j = 0; j < Table.PC2.length; j++)
                binKey_PC2.append(sMerged.charAt(Table.PC2[j] - 1));

            key[i] = Long.parseLong(binKey_PC2.toString(), 2);
        }
    }
    public String rotateLeft(String str, int n) {
        StringBuilder newStr = new StringBuilder();
        newStr.append(str.substring(n)).append(str, 0, n);
        return newStr.toString();
    }

    public String f(String mi, long key) {
        StringBuilder gMi = new StringBuilder();
        for (int i = 0; i < Table.g.length; i++) {
            gMi.append(mi.charAt(Table.g[i] - 1));
        }

        long m = Long.parseLong(gMi.toString(), 2);
        long result = m ^ key;

        StringBuilder bin = new StringBuilder(Long.toBinaryString(result));

        while (bin.length() < 48) {
            bin.insert(0, "0");
        }

        String[] sin = new String[8];
        String[] sout = new String[8];
        for (int i = 0; i < 8; i++) {
            sin[i] = bin.substring(6*i, 6*(i+1));

            int row = Integer.parseInt(sin[i].charAt(0) + "" + sin[i].charAt(5), 2);
            int col = Integer.parseInt(sin[i].substring(1, 5), 2);

            sout[i] = Integer.toBinaryString(Table.s[i][row][col]);

            while (sout[i].length() < 4) {
                sout[i] = "0" + sout[i];
            }
        }

        StringBuilder merged = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            merged.append(sout[i]);
        }

        StringBuilder mergedP = new StringBuilder();
        for (int i = 0; i < Table.p.length; i++) {
            mergedP.append(merged.charAt(Table.p[i] - 1));
        }
        return mergedP.toString();
    }
}
