package catan.events;

public class Action {
    public enum ActionType {
        BUILD_ROAD,
        BUILD_SETTLEMENT,
        BUILD_CITY,
        TRADE_WITH_BANK,
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
