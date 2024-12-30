package catan.events;

public class Action {
    public enum ActionType {
        BUILD_ROAD,
        BUILD_SETTLEMENT,
        BUILD_SETTLEMENT_START,
        BUILD_CITY,
        TRADE_WITH_BANK,
        TRADE_WITH_PLAYER,
        PURCHASE_DEVELOPMENT_CARD,
        MOVE_ROBBER,
        STEAL_RESOURCE,
        PLAY_KNIGHT,
        PLAY_MONOPOLY,
        PLAY_YEAR_OF_PLENTY,
        PLAY_ROAD_BUILDING,
        PASS
    }

    private ActionType type;
    private int[] args;

    public Action(ActionType type, int[] args) {
        switch(type) {
            case BUILD_ROAD:
                if(args.length != 1) {
                    throw new IllegalArgumentException("BUILD_ROAD action requires 1 argument");
                }
                if(args[0] < 0 || args[0] > 71) {
                    throw new IllegalArgumentException("BUILD_ROAD action requires a valid edge argument");
                }
                break;

            case BUILD_SETTLEMENT:
            case BUILD_SETTLEMENT_START:
            case BUILD_CITY:
                if(args.length != 1) {
                    throw new IllegalArgumentException(type + " action requires 1 argument");
                }
                if(args[0] < 0 || args[0] > 53) {
                    throw new IllegalArgumentException(type + " action requires a valid vertex argument");
                }
                break;

            case PASS:
                if(args.length != 0) {
                    throw new IllegalArgumentException("PASS action requires no arguments");
                }
                break;

            case TRADE_WITH_BANK:
                if(args.length != 3) {
                    throw new IllegalArgumentException("TRADE_WITH_BANK action requires 3 arguments: [giveResource, amountGiven, takeResource]");
                }
                if(args[0] < 0 || args[0] > 4) {
                    throw new IllegalArgumentException("TRADE_WITH_BANK action requires a valid give resource argument");
                }
                if(args[1] < 2 || args[1] > 4) {
                    throw new IllegalArgumentException("TRADE_WITH_BANK action requires a valid amount argument");
                }
                if(args[2] < 0 || args[2] > 4) {
                    throw new IllegalArgumentException("TRADE_WITH_BANK action requires a valid take resource argument");
                }
                break;
            case PURCHASE_DEVELOPMENT_CARD:
                if(args.length != 0) {
                    throw new IllegalArgumentException("PURCHASE_DEVELOPMENT_CARD action requires no arguments");
                }
                break;
            case PLAY_KNIGHT:
                if(args.length != 1) {
                    throw new IllegalArgumentException("PLAY_KNIGHT action requires 1 argument");
                }
                if(args[0] < 0 || args[0] > 18)   {
                    throw new IllegalArgumentException("PLAY_KNIGHT action requires a valid tile argument");
                }
                break;
            case PLAY_MONOPOLY:
                if(args.length != 1) {
                    throw new IllegalArgumentException("PLAY_MONOPOLY action requires 1 argument");
                }
                if(args[0] < 0 || args[0] > 4) {
                    throw new IllegalArgumentException("PLAY_MONOPOLY action requires a valid resource argument");
                }
                break;
            case PLAY_YEAR_OF_PLENTY:
                if(args.length > 2  || args.length < 1) {
                    throw new IllegalArgumentException("PLAY_YEAR_OF_PLENTY action requires 1 or 2 arguments");
                }
                if(args.length == 1) {
                    if(args[0] < 0 || args[0] > 4) {
                        throw new IllegalArgumentException("PLAY_YEAR_OF_PLENTY action requires a valid resource argument");
                    }
                } else {
                    if(args[0] < 0 || args[0] > 4 || args[1] < 0 || args[1] > 4) {
                        throw new IllegalArgumentException("PLAY_YEAR_OF_PLENTY action requires valid resource arguments");
                    }
                }
                break;
            case PLAY_ROAD_BUILDING:
                if(args.length != 2) {
                    throw new IllegalArgumentException("PLAY_ROAD_BUILDING action requires 2 arguments");
                }
                if(args[0] < 0 || args[0] > 71 || args[1] < 0 || args[1] > 71) {
                    throw new IllegalArgumentException("PLAY_ROAD_BUILDING action requires valid edge arguments");
                }
                break;
            case STEAL_RESOURCE:
                if(args.length != 1 ) {
                    throw new IllegalArgumentException("STEAL_RESOURCE action requires 1 argument");
                }
                if(args[0] < 0 || args[0] > 3) {
                    throw new IllegalArgumentException("STEAL_RESOURCE action requires a valid player argument");
                }
                break;
        }
        this.type = type;
        this.args = args;
    }
    public ActionType getType() {
        return this.type;
    }
    public int[] getArgs() {
        return this.args;
    }
}
