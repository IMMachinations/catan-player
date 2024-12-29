package catan.board;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import catan.enums.EdgeState;
import catan.enums.Odds;
import catan.enums.Resource;
import catan.enums.VertexState;
import catan.events.Action;
import catan.player.CatanPlayer;
import catan.utils.AdjacentDicts;
import catan.utils.Color;
import catan.utils.ResourceGeneration;
import catan.utils.Tuple;


public class CatanBoard {
    private Random rand;
    private VertexState[] vertices;
    private Set<Integer> openVertices;
    private Set<Integer> settlableVertices;
    private Set<Integer> cityableVertices;
    private EdgeState[] edges;
    private Set<Integer> openEdges;
    private Tile[] tiles;
    private int[][] vertexAdjacentTiles;
    private int vertexIndex;
    private int edgeIndex;

    private int[][] playerHands;
    private int[][] bankTradesOffered;
    private int[][] developmentCardsInHand;
    private Queue<Integer> developmentCardsInDeck;
    private int[] resourceCounts;
    private int[] playerScores;
    private int scoreToWin;
    private CatanPlayer[] players;

    
    public CatanBoard() {

        rand = new Random();
        this.vertices = new VertexState[54];
        this.openVertices = new HashSet<>();
        this.settlableVertices = new HashSet<>();
        this.cityableVertices = new HashSet<>();
        for (int i = 0; i < 54; i++) {
            this.vertices[i] = VertexState.Empty;
            this.openVertices.add(i);
            this.settlableVertices.add(i);
            this.cityableVertices.add(i);
        }
        this.edges = new EdgeState[72];
        this.openEdges = new HashSet<>();
        for (int i = 0; i < 72; i++) {
            this.edges[i] = EdgeState.Empty;
            this.openEdges.add(i);
        }
        //create pairs of odds and resources
        //there should be 2 of each odds (excluding 7, which there should be none of, and 2 and 12, which there should be 1 of)
        //there should be 3 of each resource (excluding 6, which there should be none of)

        List<Tuple<Odds, Resource>> tileResourcesOdds = ResourceGeneration.generateResources();
        
        this.tiles = new Tile[19];
        for (int i = 0; i < 19; i++) {
            this.tiles[i] = new Tile(tileResourcesOdds.get(i).y, tileResourcesOdds.get(i).x, i);
        }
        this.vertexIndex = 0;
        this.edgeIndex = 0;
        this.vertexAdjacentTiles = new int[54][3];
        this.playerHands = new int[4][5];
        this.resourceCounts = new int[] {19, 19, 19, 19, 19};
        this.bankTradesOffered = new int[][] {{4, 4, 4, 4, 4},{4, 4, 4, 4, 4},{4, 4, 4, 4, 4},{4, 4, 4, 4, 4}};
        this.developmentCardsInHand = new int[4][5];
        ArrayList storeList = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 3, 3, 4, 4)); 
        Collections.shuffle(storeList);
        this.developmentCardsInDeck = new LinkedList<>(storeList); 
        this.playerScores = new int[] {0, 0, 0, 0};
        this.scoreToWin = 10;
        this.players = new CatanPlayer[4];
    }
    

    public int giveResource(int player, Resource resource, int amount) {
        if(amount <= 0) {
            return 0;
        }
        if(this.resourceCounts[Resource.toInt(resource)] - amount < 0) {
            return giveResource(player, resource, this.resourceCounts[Resource.toInt(resource)]);
        }
        this.resourceCounts[Resource.toInt(resource)] -= amount;
        this.playerHands[player][Resource.toInt(resource)] += amount;
        return 0;
    }
    public int payResourceToBank(int player, Resource resource, int amount) {
        if(amount <= 0) {
            return 0;
        }
        if(this.playerHands[player][Resource.toInt(resource)] - amount < 0) {
            return payResourceToBank(player, resource, this.playerHands[player][Resource.toInt(resource)]);
        }
        this.playerHands[player][Resource.toInt(resource)] -= amount;
        this.resourceCounts[Resource.toInt(resource)] += amount;
        return 0;
    }

    public List<Action> getValidActions(int catanPlayer) {
        List<Action> actions = new ArrayList<>();
        if(this.playerHands[catanPlayer][0] > 0 && this.playerHands[catanPlayer][2] > 0) {
            EdgeState roadType = EdgeState.roadFromPlayer(catanPlayer);
            for(int edge : this.openEdges) {
                for(int adjacentEdge : AdjacentDicts.edgeAdjacentEdges[edge]) {
                    if(adjacentEdge != -1 && this.edges[adjacentEdge] == roadType) {
                        actions.add(new Action(Action.ActionType.BUILD_ROAD, new int[] {edge}));
                    }
                }
            }
        } 
        if(this.playerHands[catanPlayer][0] > 0 && this.playerHands[catanPlayer][1] > 0 && this.playerHands[catanPlayer][2] > 0 && this.playerHands[catanPlayer][4] > 0) {
            EdgeState roadType = EdgeState.roadFromPlayer(catanPlayer);
            for(int vertex : this.settlableVertices) {
                for(int adjacentEdge: AdjacentDicts.vertexAdjacentEdges[vertex]) {
                    if(adjacentEdge != -1 && this.edges[adjacentEdge] == roadType) {
                        actions.add(new Action(Action.ActionType.BUILD_SETTLEMENT, new int[] {vertex}));
                        System.out.println("Giving player P" + (catanPlayer + 1) + " option to place settlement at vertex " + vertex);    
                    }
                }
            }
        }
        if(this.playerHands[catanPlayer][1] > 1 && this.playerHands[catanPlayer][3] > 2) {
            VertexState settlementType = VertexState.settlementFromPlayer(catanPlayer);
            for(int vertex : this.cityableVertices) {
                if(this.vertices[vertex] == settlementType) {
                    actions.add(new Action(Action.ActionType.BUILD_CITY, new int[] {vertex}));
                    System.out.println("Giving player P" + (catanPlayer + 1) + " option to place city at vertex " + vertex);
                }
            }
        }
        for(int resource = 0; resource < 5; resource++) {
            if(this.playerHands[catanPlayer][resource] >= this.bankTradesOffered[catanPlayer][resource]) {
                for(int remainingResource = 0; remainingResource < 5; remainingResource++) {
                    if(remainingResource != resource && this.resourceCounts[remainingResource] > 0) {
                        actions.add(new Action(Action.ActionType.TRADE_WITH_BANK, new int[] {resource, this.bankTradesOffered[catanPlayer][resource], remainingResource}));
                    }
                }
            }
        }
        actions.add(new Action(Action.ActionType.PASS, new int[] {}));
        return actions;
    }

    public void populateBoard() {
        this.tiles[0].populateVerticesAndEdges(new Tile[] {null, null, null, null, null, null}, this);
        this.tiles[1].populateVerticesAndEdges(new Tile[] {null, null, null, null, tiles[0], null}, this);
        this.tiles[2].populateVerticesAndEdges(new Tile[] {null, null, null, null, tiles[1], null}, this);

        this.tiles[3].populateVerticesAndEdges(new Tile[] {tiles[0], null, null, null, null, null}, this);
        this.tiles[4].populateVerticesAndEdges(new Tile[] {tiles[1], null, null, null, tiles[3], tiles[0]}, this);
        this.tiles[5].populateVerticesAndEdges(new Tile[] {tiles[2], null, null, null, tiles[4], tiles[1]}, this);
        this.tiles[6].populateVerticesAndEdges(new Tile[] {null, null, null, null, tiles[5], tiles[2]}, this);

        this.tiles[7].populateVerticesAndEdges(new Tile[] {tiles[3], null, null, null, null, null}, this);
        this.tiles[8].populateVerticesAndEdges(new Tile[] {tiles[4], null, null, null, tiles[7], tiles[3]}, this);
        this.tiles[9].populateVerticesAndEdges(new Tile[] {tiles[5], null, null, null, tiles[8], tiles[4]}, this);
        this.tiles[10].populateVerticesAndEdges(new Tile[] {tiles[6], null, null, null, tiles[9], tiles[5]}, this);
        this.tiles[11].populateVerticesAndEdges(new Tile[] {null, null, null, null, tiles[10], tiles[6]}, this);

        this.tiles[12].populateVerticesAndEdges(new Tile[] {tiles[8], null, null, null, null, tiles[7]}, this);
        this.tiles[13].populateVerticesAndEdges(new Tile[] {tiles[9], null, null, null, tiles[12], tiles[8]}, this);
        this.tiles[14].populateVerticesAndEdges(new Tile[] {tiles[10], null, null, null, tiles[13], tiles[9]}, this);
        this.tiles[15].populateVerticesAndEdges(new Tile[] {tiles[11], null, null, null, tiles[14], tiles[10]}, this);
        
        this.tiles[16].populateVerticesAndEdges(new Tile[] {tiles[13], null, null, null, null, tiles[12]}, this);
        this.tiles[17].populateVerticesAndEdges(new Tile[] {tiles[14], null, null, null, tiles[16], tiles[13]}, this);
        this.tiles[18].populateVerticesAndEdges(new Tile[] {tiles[15], null, null, null, tiles[17], tiles[14]}, this);
    }


    public int allocateNewVertex() {
        int newVertex = this.vertexIndex;
        this.vertexIndex++;
        return newVertex;
    }    
    public int allocateNewEdge() {
        int newEdge = this.edgeIndex;
        this.edgeIndex++;
        return newEdge;
    }
    
    public boolean hasAdjacentRoad(int vertex, int player) {
        return true;
    }

    public boolean isValidSettlementPlacement(int vertex) {
        if(vertex < 0 || vertex > 53) {
            throw new IllegalArgumentException("Invalid vertex");
        }
        if(this.vertices[vertex] != VertexState.Empty) {
            return false;
        }
        for(int i = 0; i < 3; i++) {
            if(AdjacentDicts.vertexAdjacentVertices[vertex][i] == -1) {
                return true;
            }
            if(this.vertices[AdjacentDicts.vertexAdjacentVertices[vertex][i]] != VertexState.Empty) {
                return false;
            }        
        }
        return true;
    }

    public boolean areAdjacent(int vertex, int edge) {
        for(int i = 0; i < 3; i++) {
            if(AdjacentDicts.vertexAdjacentEdges[vertex][i] == edge) {
                return true;
            }
        }
        return false;
    }
    
    public void moveResourceFromPlayerToPlayer(int givePlayer, int receivePlayer, int amount, Resource resource) {
        if(this.playerHands[givePlayer][Resource.toInt(resource)] - amount < 0) {
            throw new IllegalArgumentException("Not enough resources to move");
        }
        this.playerHands[givePlayer][Resource.toInt(resource)] -= amount;
        this.playerHands[receivePlayer][Resource.toInt(resource)] += amount;
    }
    public Odds rollDice(int playerIndex) {
        int roll1 = rand.nextInt(6);
        int roll2 = rand.nextInt(6);
        Odds roll = Odds.values()[roll1 + roll2];
        if (roll == Odds.SEVEN) {
            int sum;
            for(int i = 0; i < 4; i++) {
                sum = 0;
                for(int j = 0; j < 5; j++) {
                    sum += this.playerHands[i][j];
                }
                if(sum >= 7) {
                    int[] discarded = this.players[i].discardHalfOfHand(this, this.playerHands[i], (sum + 1) / 2);
                    for(int j = 0; j < 5; j++) {
                        this.playerHands[i][j] -= discarded[j];
                    }
                }
            }

            List<Action> actions = new ArrayList<Action>();
            for(int i = 0; i < 19; i++) {
                if(this.tiles[i].isUnRobbed()) {
                    actions.add(new Action(Action.ActionType.MOVE_ROBBER, new int[] {i}));
                }
            }
            Action chosen = this.players[playerIndex].chooseAction(this, actions);
            executeAction(chosen, playerIndex);

            return roll;
        } 
        for (int i = 0; i < 19; i++) {
            if(this.tiles[i].getOdds() == roll) { 
                for(int j = 0; j < 6; j++) {
                    int vertexId = this.tiles[i].getVertexId(j);
                    if(this.vertices[vertexId] != VertexState.Empty) {
                        VertexState building = this.vertices[vertexId];    
                        int player = VertexState.getPlayer(building);
                        giveResource(player, this.tiles[i].getResource(), VertexState.stateToValue(building));
                    }
                }
            }
        }
        return roll;
    }

    public int takeTurn(int catanPlayer, CatanPlayer player) {
        rollDice(catanPlayer);
        List<Action> actions = getValidActions(catanPlayer);
        Action action = player.chooseAction(this, actions);
        executeAction(action, catanPlayer);
        return (catanPlayer + 1) % 4;
    }

    public void giveScore(int player) {
        this.playerScores[player]++;
        if(this.playerScores[player] >= this.scoreToWin) {
            endGame(player);
        }
    }

    public void endGame(int player) {
        System.out.println("Player P" + (player + 1) + " wins!");
        System.exit(0);
    }

    public void executeAction(Action action, int catanPlayer) {
        switch(action.getType()) {
            case BUILD_ROAD:
                this.edges[action.getArgs()[0]] = EdgeState.roadFromPlayer(catanPlayer);
                this.openEdges.remove(action.getArgs()[0]);
                payResourceToBank(catanPlayer, Resource.BRICK, 1);
                payResourceToBank(catanPlayer, Resource.WOOD, 1);
                System.out.println("Player P" + (catanPlayer + 1) + " Building road at edge " + action.getArgs()[0]);
                break;
            case BUILD_SETTLEMENT:
                this.vertices[action.getArgs()[0]] = VertexState.settlementFromPlayer(catanPlayer);
                this.openVertices.remove(action.getArgs()[0]);
                this.settlableVertices.remove(action.getArgs()[0]);
                this.cityableVertices.add(action.getArgs()[0]);
                for(int j = 0; j < 3; j++) {
                    this.settlableVertices.remove(AdjacentDicts.vertexAdjacentVertices[action.getArgs()[0]][j]);
                }
                payResourceToBank(catanPlayer, Resource.BRICK, 1);
                payResourceToBank(catanPlayer, Resource.WOOD, 1);
                payResourceToBank(catanPlayer, Resource.WHEAT, 1);
                payResourceToBank(catanPlayer, Resource.SHEEP, 1);
                System.out.println("Player P" + (catanPlayer + 1) + " Building settlement at vertex " + action.getArgs()[0]);
                giveScore(catanPlayer);
                break;  
            case BUILD_CITY:
                this.vertices[action.getArgs()[0]] = VertexState.cityFromPlayer(catanPlayer);
                this.cityableVertices.remove(action.getArgs()[0]);
                giveScore(catanPlayer);
                System.out.println("Player P" + (catanPlayer + 1) + " Building city at vertex " + action.getArgs()[0]);
                break;
            case TRADE_WITH_BANK:
                int giveResource = action.getArgs()[0];
                int amount = action.getArgs()[1];
                int receiveResource = action.getArgs()[2];
                payResourceToBank(catanPlayer, Resource.values()[giveResource], amount);
                giveResource(catanPlayer, Resource.values()[receiveResource], 1);
                System.out.println("Player P" + (catanPlayer + 1) + " Trading with bank " + amount + " " + Resource.values()[giveResource] + " for " + 1 + " " + Resource.values()[receiveResource]);
                break;
            case PURCHASE_DEVELOPMENT_CARD:
                int card = this.developmentCardsInDeck.poll();
                this.developmentCardsInHand[catanPlayer][card]++;
                payResourceToBank(catanPlayer, Resource.WHEAT, 1);
                payResourceToBank(catanPlayer, Resource.SHEEP, 1);
                payResourceToBank(catanPlayer, Resource.ORE, 1);
                System.out.println("Player P" + (catanPlayer + 1) + " Buying development card " + card);
                break;
            case MOVE_ROBBER:
                for (int i = 0; i < 19; i++) {
                    this.tiles[i].setRobbed(false);
                }
                this.tiles[action.getArgs()[0]].setRobbed(true);
                System.out.println("Player P" + (catanPlayer + 1) + " Moving robber to tile " + action.getArgs()[0]);
                List<Action> stealActions = new ArrayList<Action>();
                for(int i = 0; i < 6; i++) {
                    int vertexId = this.tiles[action.getArgs()[0]].getVertexId(i);
                    if(this.vertices[vertexId] != VertexState.Empty) {
                        int player = VertexState.getPlayer(this.vertices[vertexId]);
                        if(player != catanPlayer) {
                            stealActions.add(new Action(Action.ActionType.STEAL_RESOURCE, new int[] {player}));
                        }
                    }
                    if(!stealActions.isEmpty()) {
                        Action chosen = this.players[catanPlayer].chooseAction(this, stealActions);
                        executeAction(chosen, catanPlayer);
                    }
                }
                break;
            case STEAL_RESOURCE:
                int fromPlayer = action.getArgs()[0];
                int handSize = 0;
                for(int i = 0; i < 5; i++) {
                    handSize += this.playerHands[fromPlayer][i];
                }
                if(handSize <=0) {
                    break;
                }
                int toStealId = rand.nextInt(handSize);
                for(int i = 0; i < 5; i++) {
                    toStealId -= this.playerHands[fromPlayer][i];
                    if(toStealId < 0) {
                        moveResourceFromPlayerToPlayer(fromPlayer, catanPlayer, 1, Resource.values()[i]);
                        break;
                    }
                }
                System.out.println("Player P" + (catanPlayer + 1) + " Stealing resource from player P" + (fromPlayer + 1));
                break;
            case PASS:
                break;  
        }
    }

    public void placeStartingPositions(CatanPlayer[] players) {
        if(players.length != 4) {
            throw new IllegalArgumentException("Invalid number of players");
        }

        // First round - players place first settlement and road
        for(int i = 0; i < 4; i++) {
            System.out.println("Player P" + (i + 1) + " placing first starting position");
            List<Action> validActions = getValidStartingPositions();
            Action chosen = players[i].chooseAction(this, validActions);
            
            if(chosen.getType() != Action.ActionType.BUILD_SETTLEMENT) {    
                //how could this happen?
                throw new IllegalArgumentException("Invalid starting position action");
            }
            
            // Place settlement and road
            int vertex = chosen.getArgs()[0];
            executeAction(chosen, i);

            validActions = new ArrayList<Action> ();
            for (int edge : AdjacentDicts.vertexAdjacentEdges[vertex]) {
                if(edge != -1) {
                    validActions.add(new Action(Action.ActionType.BUILD_ROAD, new int[]{edge}));
                }
            }
            chosen = players[i].chooseAction(this, validActions);
            if(chosen.getType() != Action.ActionType.BUILD_ROAD) {
                //how could this happen?
                throw new IllegalArgumentException("Invalid starting position action");
            }
            executeAction(chosen, i);
        }

        // Second round - players place second settlement and road (reverse order)
        for(int i = 3; i >= 0; i--) {
            System.out.println("Player P" + (i + 1) + " placing second starting position");
            List<Action> validActions = getValidStartingPositions();
            Action chosen = players[i].chooseAction(this, validActions);
            
            if(chosen.getType() != Action.ActionType.BUILD_SETTLEMENT) {    
                //how could this happen?
                throw new IllegalArgumentException("Invalid starting position action");
            }
            
            // Place settlement and road
            int vertex = chosen.getArgs()[0];
            executeAction(chosen, i);

            for(int j = 0; j < 3; j++) {
                if(AdjacentDicts.vertexAdjacentTiles[vertex][j] != -1 && this.tiles[AdjacentDicts.vertexAdjacentTiles[vertex][j]].getOdds() != Odds.SEVEN) {
                    giveResource(i, this.tiles[AdjacentDicts.vertexAdjacentTiles[vertex][j]].getResource(), 1);
                }
            }

            validActions = new ArrayList<Action> ();
            for (int edge : AdjacentDicts.vertexAdjacentEdges[vertex]) {
                if(edge != -1) {
                    validActions.add(new Action(Action.ActionType.BUILD_ROAD, new int[]{edge}));
                }
            }
            chosen = players[i].chooseAction(this, validActions);
            if(chosen.getType() != Action.ActionType.BUILD_ROAD) {
                //how could this happen?
                throw new IllegalArgumentException("Invalid starting position action");
            }
            executeAction(chosen, i);
        }
        this.players = players;
    }

    // Helper method to get valid starting positions
    private List<Action> getValidStartingPositions() {
        List<Action> actions = new ArrayList<>();
        
        // For each valid settlement location
        for(int vertex : this.settlableVertices) {
            actions.add(new Action(Action.ActionType.BUILD_SETTLEMENT, new int[]{vertex}));
        }
        return actions;
    }

    

    public void printBoard() {
        for(int i = 0; i < 19; i++) {
            this.tiles[i].printBoard();
        }
    }

    private String getVertexDisplay(int vertex) {
        switch (this.vertices[vertex]) {
            case Empty:
                return Color.WHITE + "O" + Color.RESET;
            case P1Settlement:
                return Color.RED + "S" + Color.RESET;
            case P1City:
                return Color.RED + "C" + Color.RESET;
            case P2Settlement:
                return Color.GREEN + "S" + Color.RESET;
            case P2City:
                return Color.GREEN + "C" + Color.RESET;
            case P3Settlement:
                return Color.YELLOW + "S" + Color.RESET;
            case P3City:
                return Color.YELLOW + "C" + Color.RESET;
            case P4Settlement:
                return Color.BLUE + "S" + Color.RESET;
            case P4City:
                return Color.BLUE + "C" + Color.RESET;
            default:
                throw new IllegalArgumentException("Invalid vertex state");
        }
    }

    private String displayEdge(int edge, String edgeDirection) {
        switch (this.edges[edge]) {
            case Empty:
                return Color.WHITE + edgeDirection + Color.RESET;
            case P1Road:
                return Color.RED + edgeDirection + Color.RESET;
            case P2Road:
                return Color.GREEN + edgeDirection + Color.RESET;
            case P3Road:
                return Color.YELLOW + edgeDirection + Color.RESET;
            case P4Road:
                return Color.BLUE + edgeDirection + Color.RESET;
            default:
                throw new IllegalArgumentException("Invalid edge state");
        }
    }

    public void displayBoard() {
        String out = "         " + getVertexDisplay(0) + "     " + getVertexDisplay(6) + "     " + getVertexDisplay(10);
        out += "\n        " + displayEdge(5,"/") + " " + displayEdge(0,"\\") + "   " + displayEdge(10,"/") + " " + displayEdge(6,"\\") + "   " + displayEdge(15, "/") + " " + displayEdge(11,"\\");
        out += "\n       " + displayEdge(5,"/") + "   " + displayEdge(0,"\\") + " " + displayEdge(10,"/") + "   " + displayEdge(6,"\\") + " " + displayEdge(15, "/") + "   " + displayEdge(11,"\\");
        out += "\n      " + getVertexDisplay(5) + "     " + getVertexDisplay(1) + "     " + getVertexDisplay(7) + "     " + getVertexDisplay(11);
        out += "\n      " + displayEdge(4,"|") + " " + tiles[0].getOdds() + " " + displayEdge(1,"|") + " " + tiles[1].getOdds() + " " + displayEdge(7,"|") + " " + tiles[2].getOdds() + " " + displayEdge(12,"|");
        out += "\n      " + displayEdge(4,"|") + " " + tiles[0].getResource() + " " + displayEdge(1,"|") + " " + tiles[1].getResource() + " " + displayEdge(7,"|") + " " + tiles[2].getResource() + " " + displayEdge(12,"|");
        out += "\n      " + getVertexDisplay(4) + "     " + getVertexDisplay(2) + "     " + getVertexDisplay(8) + "     " + getVertexDisplay(12);
        out += "\n     " + displayEdge(20,"/") + " " + displayEdge(3,"\\") + "   " + displayEdge(2,"/") + " " + displayEdge(9,"\\") + "   " + displayEdge(8, "/") + " " + displayEdge(14,"\\") + "   " + displayEdge(13, "/") + " " + displayEdge(27,"\\");
        out += "\n    " + displayEdge(20,"/") + "   " + displayEdge(3,"\\") + " " + displayEdge(2,"/") + "   " + displayEdge(9,"\\") + " " + displayEdge(8, "/") + "   " + displayEdge(14,"\\") + " " + displayEdge(13, "/") + "   " + displayEdge(27,"\\");
        
        out += "\n   " + getVertexDisplay(17) + "     " + getVertexDisplay(3) + "     " + getVertexDisplay(9) + "     " + getVertexDisplay(13) + "     " + getVertexDisplay(22);
        out += "\n   " + displayEdge(19,"|") + " " + tiles[3].getOdds() + " " + displayEdge(16,"|") + " " + tiles[4].getOdds() + " " + displayEdge(21,"|") + " " + tiles[5].getOdds() + " " + displayEdge(24,"|") + " " + tiles[6].getOdds() + " " + displayEdge(28,"|");
        out += "\n   " + displayEdge(19,"|") + " " + tiles[3].getResource() + " " + displayEdge(16,"|") + " " + tiles[4].getResource() + " " + displayEdge(21,"|") + " " + tiles[5].getResource() + " " + displayEdge(24,"|") + " " + tiles[6].getResource() + " " + displayEdge(28,"|");
        
        out += "\n   " + getVertexDisplay(16) + "     " + getVertexDisplay(14) + "     " + getVertexDisplay(18) + "     " + getVertexDisplay(20) + "     " + getVertexDisplay(23);        
        out += "\n  " + displayEdge(35,"/") + " " + displayEdge(18,"\\") + "   " + displayEdge(17,"/") + " " + displayEdge(23,"\\") + "   " + displayEdge(22, "/") + " " + displayEdge(26,"\\") + "   " + displayEdge(25, "/") + " " + displayEdge(30,"\\") + "   " + displayEdge(29, "/") + " " + displayEdge(45,"\\");        
        out += "\n " + displayEdge(35,"/") + "   " + displayEdge(18,"\\") + " " + displayEdge(17,"/") + "   " + displayEdge(23,"\\") + " " + displayEdge(22, "/") + "   " + displayEdge(26,"\\") + " " + displayEdge(25, "/") + "   " + displayEdge(30,"\\") + " " + displayEdge(29, "/") + "   " + displayEdge(45,"\\");

        out += "\n" + getVertexDisplay(28) + "     " + getVertexDisplay(15) + "     " + getVertexDisplay(19) + "     " + getVertexDisplay(21) + "     " + getVertexDisplay(24) + "     " + getVertexDisplay(35);
        out += "\n" + displayEdge(34,"|") + " " + tiles[7].getOdds() + " " + displayEdge(31,"|") + " " + tiles[8].getOdds() + " " + displayEdge(36,"|") + " " + tiles[9].getOdds() + " " + displayEdge(39,"|") + " " + tiles[10].getOdds() + " " + displayEdge(42,"|") + " " + tiles[11].getOdds() + " " + displayEdge(46,"|");
        out += "\n" + displayEdge(34,"|") + " " + tiles[7].getResource() + " " + displayEdge(31,"|") + " " + tiles[8].getResource() + " " + displayEdge(36,"|") + " " + tiles[9].getResource() + " " + displayEdge(39,"|") + " " + tiles[10].getResource() + " " + displayEdge(42,"|") + " " + tiles[11].getResource() + " " + displayEdge(46,"|");
        out += "\n" + getVertexDisplay(27) + "     " + getVertexDisplay(25) + "     " + getVertexDisplay(29) + "     " + getVertexDisplay(31) + "     " + getVertexDisplay(33) + "     " + getVertexDisplay(36);
        out += "\n " + displayEdge(33,"\\") + "   " + displayEdge(32,"/") + " " + displayEdge(38,"\\") + "   " + displayEdge(37,"/") + " " + displayEdge(41, "\\") + "   " + displayEdge(40,"/") + " " + displayEdge(44, "\\") + "   " + displayEdge(43,"/") + " " + displayEdge(48, "\\") + "   " + displayEdge(47,"/");
        out += "\n  " + displayEdge(33,"\\") + " " + displayEdge(32,"/") + "   " + displayEdge(38,"\\") + " " + displayEdge(37,"/") + "   " + displayEdge(41, "\\") + " " + displayEdge(40,"/") + "   " + displayEdge(44, "\\") + " " + displayEdge(43,"/") + "   " + displayEdge(48, "\\") + " " + displayEdge(47,"/"); 
        
        out += "\n   " + getVertexDisplay(26) + "     " + getVertexDisplay(30) + "     " + getVertexDisplay(32) + "     " + getVertexDisplay(34) + "     " + getVertexDisplay(37);
        out += "\n   " + displayEdge(52,"|") + " " + tiles[12].getOdds() + " " + displayEdge(49,"|") + " " + tiles[13].getOdds() + " " + displayEdge(53,"|") + " " + tiles[14].getOdds() + " " + displayEdge(56,"|") + " " + tiles[15].getOdds() + " " + displayEdge(59,"|");
        out += "\n   " + displayEdge(52,"|") + " " + tiles[12].getResource() + " " + displayEdge(49,"|") + " " + tiles[13].getResource() + " " + displayEdge(53,"|") + " " + tiles[14].getResource() + " " + displayEdge(56,"|") + " " + tiles[15].getResource() + " " + displayEdge(59,"|");
        out += "\n   " + getVertexDisplay(40) + "     " + getVertexDisplay(38) + "     " + getVertexDisplay(41) + "     " + getVertexDisplay(43) + "     " + getVertexDisplay(45);
        
        out += "\n    " + displayEdge(51,"\\") + "   " + displayEdge(50,"/") + " " + displayEdge(55,"\\") + "   " + displayEdge(54,"/") + " " + displayEdge(58, "\\") + "   " + displayEdge(57,"/") + " " + displayEdge(61, "\\") + "   " + displayEdge(60,"/");
        out += "\n     " + displayEdge(51,"\\") + " " + displayEdge(50,"/") + "   " + displayEdge(55,"\\") + " " + displayEdge(54,"/") + "   " + displayEdge(58, "\\") + " " + displayEdge(57,"/") + "   " + displayEdge(61, "\\") + " " + displayEdge(60,"/");
        out += "\n      " + getVertexDisplay(39) + "     " + getVertexDisplay(42) + "     " + getVertexDisplay(44) + "     " + getVertexDisplay(46);
        out += "\n      " + displayEdge(65,"|") + " " + tiles[16].getOdds() + " " + displayEdge(62,"|") + " " + tiles[17].getOdds() + " " + displayEdge(66,"|") + " " + tiles[18].getOdds() + " " + displayEdge(69,"|");
        out += "\n      " + displayEdge(65,"|") + " " + tiles[16].getResource() + " " + displayEdge(62,"|") + " " + tiles[17].getResource() + " " + displayEdge(66,"|") + " " + tiles[18].getResource() + " " + displayEdge(69,"|");
        out += "\n      " + getVertexDisplay(49) + "     " + getVertexDisplay(47) + "     " + getVertexDisplay(50) + "     " + getVertexDisplay(52);
        out += "\n       " + displayEdge(64,"\\") + "   " + displayEdge(63,"/") + " " + displayEdge(68,"\\") + "   " + displayEdge(67,"/") + " " + displayEdge(71, "\\") + "   " + displayEdge(70,"/");
        out += "\n        " + displayEdge(64,"\\") + " " + displayEdge(63,"/") + "   " + displayEdge(68,"\\") + " " + displayEdge(67,"/") + "   " + displayEdge(71, "\\") + " " + displayEdge(70,"/");
        out += "\n         " + getVertexDisplay(48) + "     " + getVertexDisplay(51) + "     " + getVertexDisplay(53);
        out += "\n     " + Resource.BRICK + " " + Resource.WHEAT + " " + 
               Resource.WOOD + " " + Resource.ORE + " " + Resource.SHEEP + " PTS";
        
        out += Color.RED + "\nP1: " + Color.RESET + 
               String.format("%3d %3d %3d %3d %3d %3d", 
               this.playerHands[0][0], this.playerHands[0][1], this.playerHands[0][2], 
               this.playerHands[0][3], this.playerHands[0][4], this.playerScores[0]);
        
        out += Color.GREEN + "\nP2: " + Color.RESET + 
               String.format("%3d %3d %3d %3d %3d %3d", 
               this.playerHands[1][0], this.playerHands[1][1], this.playerHands[1][2], 
               this.playerHands[1][3], this.playerHands[1][4], this.playerScores[1]);
        
        out += Color.YELLOW + "\nP3: " + Color.RESET + 
               String.format("%3d %3d %3d %3d %3d %3d", 
               this.playerHands[2][0], this.playerHands[2][1], this.playerHands[2][2], 
               this.playerHands[2][3], this.playerHands[2][4], this.playerScores[2]);
        
        out += Color.BLUE + "\nP4: " + Color.RESET + 
               String.format("%3d %3d %3d %3d %3d %3d", 
               this.playerHands[3][0], this.playerHands[3][1], this.playerHands[3][2], 
               this.playerHands[3][3], this.playerHands[3][4], this.playerScores[3]);
        out += "\nBNK:" + String.format("%3d %3d %3d %3d %3d", 
               this.resourceCounts[0], this.resourceCounts[1], this.resourceCounts[2], this.resourceCounts[3], this.resourceCounts[4]);
        try {
            FileWriter writer = new FileWriter("board.txt");
            writer.write(out);
            writer.close();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
    public void printAdjacentTilesAndEdges() {
        for(int i = 0; i < 54; i++) {
            for(int j = 0; j < 19; j++) {
                for(int k = 0; k < 6; k++) {
                    if(this.tiles[j].getVertexId(k) == i) {
                        System.out.println("Vertex " + i + " Adjacent Tile " + j + " and Edges " + this.tiles[j].getEdgeId(k) + " and " + this.tiles[j].getEdgeId((k + 5) % 6) + " and vertices " + this.tiles[j].getVertexId((k + 1) % 6) + " and " + this.tiles[j].getVertexId((k - 1 + 6) % 6));
                    }
                }
            }
        }
    }
    
    public void printAvailableVerticesAndEdges() {
        System.out.println("Available Vertices: " + this.settlableVertices);
        System.out.println("Available Edges: " + this.openEdges);
    }   

}
