package catan.utils;

public class StringUtils {
    public static String toPaddedString(int number, int length) {
        return String.format("%" + length + "s", number);
    }   
}