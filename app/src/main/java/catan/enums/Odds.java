package catan.enums;

public enum Odds {
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    EIGHT,
    NINE,
    TEN,
    ELEVEN,
    TWELVE;
    @Override
    public String toString() {
        switch(this) {
            case TWO: return " 2 ";
            case THREE: return " 3 ";
            case FOUR: return " 4 ";
            case FIVE: return " 5 ";
            case SIX: return " 6 ";
            case EIGHT: return " 8 ";
            case NINE: return " 9 ";
            case TEN: return "1 0";
            case ELEVEN: return "1 1";
            case TWELVE: return "1 2";
            default: return "   ";
        }
    }
    public static final Odds[] odds = {TWO, THREE, FOUR, FIVE, SIX, EIGHT, NINE, TEN, ELEVEN, TWELVE};
}