package catan.events;

public class Action {
    public enum ActionType {
        BUILD_ROAD,
        BUILD_SETTLEMENT,
        BUILD_CITY,
        PASS
    }

    private ActionType type;
    private int[] args;

    public Action(ActionType type, int[] args) {
        if(type == ActionType.BUILD_ROAD) {
            if(args.length != 1) {
                throw new IllegalArgumentException("BUILD_ROAD action requires 1 argument");
            }
            if(args[0] < 0 || args[0] > 71) {
                throw new IllegalArgumentException("BUILD_ROAD action requires a valid edge argument");
            }
        } else if(type == ActionType.BUILD_SETTLEMENT) {
            if(args.length != 1) {
                throw new IllegalArgumentException("BUILD_SETTLEMENT action requires 1 argument");
            } 
            if(args[0] < 0 || args[0] > 53) {
                throw new IllegalArgumentException("BUILD_SETTLEMENT action requires a valid vertex argument");
            }
        } else if(type == ActionType.BUILD_CITY) {
            if(args.length != 1) {
                throw new IllegalArgumentException("BUILD_CITY action requires 1 argument");
            }
            if(args[0] < 0 || args[0] > 53) {
                throw new IllegalArgumentException("BUILD_CITY action requires a valid vertex argument");
            }

        } else if(type == ActionType.PASS) {
            if(args.length != 0) {
                throw new IllegalArgumentException("PASS action requires no arguments");
            }
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
