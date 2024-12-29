package catan.utils;

public class StringUtils {
    public static String toPaddedString(int number, int length) {
        return String.format("%" + length + "s", number);
    }   
    public static int sum(int[] array) {
        int sum = 0;
        for(int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        return sum;
    }
}