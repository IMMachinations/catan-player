package catan.enums;

public enum EdgeState {
    Empty,
    P1Road,
    P2Road,
    P3Road,
    P4Road;

    public static EdgeState getRoadState(int player) {
        if (player < 0 || player > 3) {
            throw new IllegalArgumentException("Player must be between 0 and 3");
        }
        return values()[player + 1]; // +1 to skip Empty
    }

    public static EdgeState roadFromPlayer(int player) {
        if (player < 0 || player > 3) {
            throw new IllegalArgumentException("Player must be between 0 and 3");
        }
        return values()[player + 1]; // +1 to skip Empty
    }
    public int getPlayer() {
        return this.ordinal() - 1;
    }
}