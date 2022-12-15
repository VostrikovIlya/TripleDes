import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import com.sun.imageio.plugins.jpeg.*;
import com.sun.imageio.plugins.png.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import java.awt.image.BufferedImage;
import java.io.*;


public class Convert {
    public static String binToHex(String bin) {
        BigInteger b = new BigInteger(bin, 2);
        return b.toString(16);
    }


    public static String hexToBin(String hex) {
        BigInteger b = new BigInteger(hex, 16);
        return b.toString(2);
    }

    public static byte[] binToByte(String bin) {
        byte[] out = new byte[bin.length() / 8];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) Integer.parseInt(bin.substring(8 * i, 8 * i + 8), 2);
        }
        return out;
    }

    public static String binToUTF(String bin) {

        int[] ciphertextBytes = new int[bin.length() / 8];
        String ciphertext = null;
        for (int j = 0; j < ciphertextBytes.length; j++) {
            String temp = bin.substring(0, 8);
            int b = Integer.parseInt(temp, 2);
            ciphertextBytes[j] = b;
            bin = bin.substring(8);
        }

        ciphertext = new String(Arrays.toString(ciphertextBytes));
        ciphertext = ciphertext.replaceAll("[,'['']']", "");

        ciphertext = ciphertext.replace("[", "");
        ciphertext = ciphertext.replace("]", "");

        return ciphertext.trim();
    }

    public static int[][] convertTo2DUsingGetRGB(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] result = new int[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                result[row][col] = image.getRGB(col, row);
            }
        }

        return result;
    }

    public static String utfToBin(String utf) {
        byte[] bytes = new byte[utf.length()];
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < utf.length(); i++) {
            bytes[i] = (byte) utf.charAt(i);
        }

        StringBuilder bin = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] < 0) {
                bytes[i] = (byte) ((int) bytes[i] + 128);
            }
            int value = bytes[i];
            for (int j = 0; j < 8; j++) {
                bin.append((value & 128) == 0 ? 0 : 1);
                value <<= 1;
            }
        }
        return bin.toString();
    }

    public static String utfToBin(byte[] utf) {
        StringBuilder bin = new StringBuilder();
        for (int b : utf) {
            int value = b;
            for (int j = 0; j < utf.length; j++) {
                bin.append((value & 128) == 0 ? 0 : 1);
                value <<= 1;
            }
        }
//        return bin.toString();
        return new String(bin);
    }

    public static int hexToDec(String inputHexNumber) {
        String hexDigits = "0123456789ABCDEF";
        inputHexNumber = inputHexNumber.toUpperCase();
        int result = 0;
        for (int i = 0; i < inputHexNumber.length(); i++) {
            char c = inputHexNumber.charAt(i);
            int hexhDigit = hexDigits.indexOf(c);
            result += 16 * result + hexhDigit;
        }
        return result;
    }

    public static int[] byteToInt(byte[] data) {
        int[] newData = new int[data.length / 4];
        for (int i = 0, j = 0; i < data.length; i += 4, j++) {
            newData[j] = ((data[i] & 0xFF) << 24) | ((data[i + 1] & 0xFF) << 16) | ((data[i + 2] & 0xFF) << 8) | (data[i + 3] & 0xFF);
        }
        return newData;
    }

//    public static int[] byteToInt(byte[] data) {
//        IntBuffer intBuf =
//                ByteBuffer.wrap(data)
//                        .order(ByteOrder.BIG_ENDIAN)
//                        .asIntBuffer();
//        int[] array = new int[intBuf.remaining()];
//        intBuf.get(array);
//        return array;
//    }

    public static String stringXOR(String a, String b) {
        if (a.length() != b.length()) {
            throw new RuntimeException("Len bin Error");
        }
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < a.length(); i++) {
            if(a.charAt(i)  == '0' && b.charAt(i) == '0') {
                res.append('0');
            }
            if(a.charAt(i)  == '1' && b.charAt(i) == '0') {
                res.append('1');
            }
            if(a.charAt(i)  == '0' && b.charAt(i) == '1') {
                res.append('1');
            }
            if(a.charAt(i)  == '1' && b.charAt(i) == '1') {
                res.append('0');
            }
        }
        return res.toString();
    }
    public static int[] copyFromBufferedImage(BufferedImage bi)  {
        int height = bi.getHeight();
        int width = bi.getWidth();
        int[] pict = new int[height*width];
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                pict[i*width + j] = bi.getRGB(j, i); // 0xFFFFFF: записываем только 3 младших байта RGB
        return pict;
    }
    public static BufferedImage copyToBufferedImage(int width, int height, int[] pixels)  {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                bi.setRGB(j, i, pixels[i*width +j]);
        return bi;
    }
    public static void saveAsJpeg(String fileName, int w, int h, int[] pix) throws IOException {
        ImageWriter writer = new JPEGImageWriter(new JPEGImageWriterSpi());
        saveToImageFile(writer, fileName,w,h,pix);
    }
    private static void saveToImageFile(ImageWriter iw, String fileName, int w, int h, int[] pix) throws IOException {
        iw.setOutput(new FileImageOutputStream(new File(fileName)));
        iw.write(copyToBufferedImage(w,h,pix));
        ((FileImageOutputStream) iw.getOutput()).close();
    }

    static String ShiftRightString64(String key, int n) {
        StringBuilder shafting = new StringBuilder();
        shafting.append(key.substring(n,64)).append(key.substring(0,n));
        return shafting.toString();
    }

}
