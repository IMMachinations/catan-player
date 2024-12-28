package catan.enums;

import catan.utils.Color;
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
            case BRICK: return Color.RED + "BRK" + Color.RESET;
            case WHEAT: return Color.YELLOW + "WHT" + Color.RESET;
            case WOOD: return Color.BROWN + "WOD" + Color.RESET;
            case ORE: return Color.CYAN + "ORE" + Color.RESET;
            case SHEEP: return Color.WHITE + "SHP" + Color.RESET;
            default: return "   ";
        }
    }
    public static final Resource[] resources = {BRICK, WHEAT, WOOD, ORE, SHEEP, NONE};
    public static final Resource[] standardResources = {BRICK, BRICK, BRICK, WHEAT, WHEAT, WHEAT, WHEAT, WOOD, WOOD, WOOD, WOOD, ORE, ORE, ORE, SHEEP, SHEEP, SHEEP, SHEEP};
    public static final int[] resourceCounts = {19, 19, 19, 19, 19};
    public static final int toInt(Resource resource) {
        switch(resource) {
            case BRICK: return 0;
            case WHEAT: return 1;
            case WOOD: return 2;
            case ORE: return 3;
            case SHEEP: return 4;
            default: return -1;
        }
    }   
}