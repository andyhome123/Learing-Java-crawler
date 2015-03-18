package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by 俊毅 on 2015/3/13.
 */
public class Util {
    public static void putShort2ByteArray(short value, byte[] data, int start, int stop) {
        for (int i = start; i < stop; i++) {
            int offest = i * 8;
            data[i] = (byte) ((value >>> offest) & 0xFF);
        }
    }

    public static void putInt2ByteArray(int value, byte[] data, int start, int stop) {
        for (int i = start; i < stop; i++) {
            int offest = (5 - i) * 8;
            data[i] = (byte) ((value >>> offest) & 0xFF);
        }
    }

    public static short SbyteArray2Short(byte[] data) {
        short value = 0;
        for (int i = 0; i < 2; i++) {
            int shift = i * 8;
            value += (data[i] & 0x00FF) << shift;
        }
        return value;
    }

    public static int SbyteArray2Int(byte[] data) {
        int value = 0;
        for (int i = 2; i < 6; i++) {
            int shift = (5 - i) * 8;
            value += (data[i] & 0x000000FF ) << shift;
        }
        return value;
    }
    public static int byteArray2Int(byte[] data){
        int value = 0;
        for(int i=0;i<4;i++){
            int shift = (3 - i) * 8;
            value += (data[i] & 0x000000FF ) << shift;
        }
        return value;
    }
    public static byte[] int2ByteArray(int value){
        byte[] data = new byte[4];
        for(int i=0;i<4;i++){
            int offest = (3 - i) * 8;
            data[i] = (byte) ((value >>> offest) & 0xFF);
        }
        return data;
    }
    public static byte[] string2MD5(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data.getBytes());
        byte[] digest = md.digest();
        return digest;
    }
    public static byte[] long2ByteArray(long l) {
        byte[] array = new byte[8];
        int i;
        int shift;
        for (i = 0, shift = 56; i < 8; i++, shift -= 8) {
            array[i] = (byte) (0xFF & (l >> shift));
        }
        return array;
    }
    public static long byteArray2Long(byte[] b) {
        int value = 0;
        for (int i = 0; i < 8; i++) {
            int shift = (8 - 1 - i) * 8;
            value += (b[i] & 0x000000FF) << shift;
        }
        return value;
    }
}


