package catan.enums;

public enum Resource {
    BRICK,
    WHEAT,
    WOOD,
    ORE,
    SHEEP,
    NONE;
    @Override
    public String toString() {
        switch(this) {
            case BRICK: return "B";
            case WHEAT: return "W";
            case WOOD: return "G";
            case ORE: return "O";
            case SHEEP: return "S";
            default: return " ";
        }
    }
    public static final Resource[] resources = {BRICK, WHEAT, WOOD, ORE, SHEEP, NONE};
}