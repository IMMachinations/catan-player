package catan.enums;

public enum VertexState {
    Empty,
    P1City,
    P2City,
    P3City,
    P4City,
    P1Settlement,
    P2Settlement,
    P3Settlement,
    P4Settlement;

    public static VertexState getSettlementState(int player) {
        if (player < 0 || player > 3) {
            throw new IllegalArgumentException("Player must be between 0 and 3");
        }
        return values()[player + 5]; // +5 to skip Empty and Cities
    }

    public static VertexState getCityState(int player) {
        if (player < 0 || player > 3) {
            throw new IllegalArgumentException("Player must be between 0 and 3");
        }
        return values()[player + 1]; // +1 to skip Empty
    }
    public static int getPlayer(VertexState state) {
        switch(state) {
            case P1Settlement:
            case P1City:
                return 0;
            case P2Settlement:
            case P2City:
                return 1;
            case P3Settlement:
            case P3City:
                return 2;
            case P4Settlement:
            case P4City:
                return 3;
            default:
                return -1;
        }
    }

    public static int stateToValue(VertexState state) {
        switch(state) {
            case P1Settlement:
            case P2Settlement:
            case P3Settlement:
            case P4Settlement:
                return 1;
            case P1City:
            case P2City:
            case P3City:
            case P4City:
                return 2;
            default:
                return 0;   
        }
    }

    public static VertexState settlementFromPlayer(int player) {
        if (player < 0 || player > 3) {
            throw new IllegalArgumentException("Player must be between 0 and 3");
        }
        return values()[player + 5];
    }

    public static VertexState cityFromPlayer(int player) {
        if (player < 0 || player > 3) {
            throw new IllegalArgumentException("Player must be between 0 and 3");
        }
        return values()[player + 1];
    }

}