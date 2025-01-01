package catan.board;

import java.io.FileOutputStream;
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

import javax.swing.text.StyledEditorKit;

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
import catan.utils.StringUtils;

public class CatanBoard {
    // Board structure
    private Tile[] tiles;
    private VertexState[] vertices;
    private EdgeState[] edges;
    
    // Board state tracking
    private Set<Integer> openVertices;
    private Set<Integer> settlableVertices;
    private Set<Integer> cityableVertices;
    private Set<Integer> openEdges;
    private int vertexIndex;
    private int edgeIndex;
    
    // Player resources and trades
    private int[][] playerHands;          // [player][resource]
    private int[] resourceCounts;         // Bank's resource counts
    private int[][] bankTradesOffered;    // [player][resource]
    private int[] twoForOnePorts; 
    private int[] threeForOnePorts; 

    // Development cards
    private int[][] developmentCardsInHand;    // [player][cardType]
    private Queue<Integer> developmentCardsInDeck;
    private boolean[][] canPlayDevelopmentCardTypeThisTurn;
    
    // Player state
    private int[] playerScores;
    private int[] buildableRoads;
    private int[] buildableSettlements;
    private int[] buildableCities;
    private int[] knightsPlayed;
    private int[] roadLengths;
    private CatanPlayer[] players;
    
    // Game state
    private Random rand;
    private int scoreToWin;
    private int largestArmyPlayer;
    private int longestRoadPlayer;

    public CatanBoard() {
        // Board structure initialization
        this.tiles = new Tile[19];
        List<Tuple<Odds, Resource>> tileResourcesOdds = ResourceGeneration.generateResources();       
        for (int i = 0; i < 19; i++) {
            this.tiles[i] = new Tile(tileResourcesOdds.get(i).y, tileResourcesOdds.get(i).x, i);
        }

        this.vertices = new VertexState[54];
        this.edges = new EdgeState[72];

        // Board state tracking initialization
        this.openVertices = new HashSet<>();
        this.settlableVertices = new HashSet<>();
        this.cityableVertices = new HashSet<>();
        for (int i = 0; i < 54; i++) {
            this.vertices[i] = VertexState.Empty;
            this.openVertices.add(i);
            this.settlableVertices.add(i);
            this.cityableVertices.add(i);
        }

        this.twoForOnePorts = new int[10];
        this.threeForOnePorts = new int[8];

        ArrayList<Integer[]> ports = new ArrayList<Integer[]>(Arrays.asList(
            new Integer[] {0,5},
            new Integer[] {6,7},
            new Integer[] {12,22},
            new Integer[] {35,36},
            new Integer[] {45,46},
            new Integer[] {50,51},
            new Integer[] {48,49},
            new Integer[] {26,40},
            new Integer[] {16,17}
        ));

        Collections.shuffle(ports);
        for(int i = 0; i < 5; i++) {
            this.twoForOnePorts[2*i] = ports.get(i)[1];
            this.twoForOnePorts[2*i + 1] = ports.get(i)[0];
        }
        for(int i = 0; i < 4; i++) {
            this.threeForOnePorts[2*i] = ports.get(i + 5)[1];
            this.threeForOnePorts[2*i + 1] = ports.get(i + 5)[0];
        }

        this.openEdges = new HashSet<>();
        for (int i = 0; i < 72; i++) {
            this.edges[i] = EdgeState.Empty;
            this.openEdges.add(i);
        }
        this.vertexIndex = 0;
        this.edgeIndex = 0;

        // Player resources and trades initialization
        this.playerHands = new int[4][5];
        this.resourceCounts = new int[] {19, 19, 19, 19, 19};
        this.bankTradesOffered = new int[][] {
            {4, 4, 4, 4, 4},
            {4, 4, 4, 4, 4},
            {4, 4, 4, 4, 4},
            {4, 4, 4, 4, 4}
        };

        // Development cards initialization
        this.developmentCardsInHand = new int[4][5];
        //0: knight, 1: road building, 2: year of plenty, 3: monopoly 4: victory point
        ArrayList storeList = new ArrayList<>(Arrays.asList(
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // 14 knights
            1, 1,                                        // 2 road building
            2, 2,                                        // 2 year of plenty
            3, 3,                                        // 2 monopoly
            4, 4, 4, 4, 4                               // 5 victory points
        )); 
        Collections.shuffle(storeList);
        this.developmentCardsInDeck = new LinkedList<>(storeList); 
        this.canPlayDevelopmentCardTypeThisTurn = new boolean[4][4];

        // Player state initialization
        this.playerScores = new int[4];
        this.buildableRoads = new int[] {15, 15, 15, 15};
        this.buildableSettlements = new int[] {5, 5, 5, 5};
        this.buildableCities = new int[] {4, 4, 4, 4};
        this.knightsPlayed = new int[4];
        this.roadLengths = new int[4];
        this.players = new CatanPlayer[4];

        // Game state initialization
        rand = new Random();
        this.scoreToWin = 10;
        this.largestArmyPlayer = -1;
        this.longestRoadPlayer = -1;
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
        if(this.buildableRoads[catanPlayer] > 0 && this.playerHands[catanPlayer][0] > 0 && this.playerHands[catanPlayer][2] > 0) {
            EdgeState roadType = EdgeState.roadFromPlayer(catanPlayer);
            for(int edge : this.openEdges) {
                for(int adjacentEdge : AdjacentDicts.edgeAdjacentEdges[edge]) {
                    if(adjacentEdge != -1 && this.edges[adjacentEdge] == roadType) {
                        actions.add(new Action(Action.ActionType.BUILD_ROAD, new int[] {edge}));
                    }
                }
            }
        } 
        if(this.buildableSettlements[catanPlayer] > 0 && this.playerHands[catanPlayer][0] > 0 && this.playerHands[catanPlayer][1] > 0 && this.playerHands[catanPlayer][2] > 0 && this.playerHands[catanPlayer][4] > 0) {
            EdgeState roadType = EdgeState.roadFromPlayer(catanPlayer);
            for(int vertex : this.settlableVertices) {
                for(int adjacentEdge: AdjacentDicts.vertexAdjacentEdges[vertex]) {
                    if(adjacentEdge != -1 && this.edges[adjacentEdge] == roadType) {
                        actions.add(new Action(Action.ActionType.BUILD_SETTLEMENT, new int[] {vertex}));
                        //System.out.println("Giving player P" + (catanPlayer + 1) + " option to place settlement at vertex " + vertex);    
                    }
                }
            }
        }
        if(this.buildableCities[catanPlayer] > 0 && this.playerHands[catanPlayer][1] > 1 && this.playerHands[catanPlayer][3] > 2) {
            VertexState settlementType = VertexState.settlementFromPlayer(catanPlayer);
            for(int vertex : this.cityableVertices) {
                if(this.vertices[vertex] == settlementType) {
                    actions.add(new Action(Action.ActionType.BUILD_CITY, new int[] {vertex}));
                    //System.out.println("Giving player P" + (catanPlayer + 1) + " option to place city at vertex " + vertex);
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
        if(this.playerHands[catanPlayer][Resource.toInt(Resource.WHEAT)] > 0 && this.playerHands[catanPlayer][Resource.toInt(Resource.SHEEP)] > 0 && this.playerHands[catanPlayer][Resource.toInt(Resource.ORE)] > 0 && this.developmentCardsInDeck.size() > 0) {
            actions.add(new Action(Action.ActionType.PURCHASE_DEVELOPMENT_CARD, new int[] {}));
        }
        if(this.canPlayDevelopmentCardTypeThisTurn[catanPlayer][0]) {
            for(int i = 0; i < 19; i++) {
                if(this.tiles[i].isUnRobbed()) {
                    actions.add(new Action(Action.ActionType.PLAY_KNIGHT, new int[] {i}));
                }
            }
        }
        if(this.buildableRoads[catanPlayer] > 1 && this.canPlayDevelopmentCardTypeThisTurn[catanPlayer][1]) {
            Set<Integer> placableRoads = getPlacableRoads(catanPlayer);
            for(int road : placableRoads) {
                for(int adjacentEdge : AdjacentDicts.edgeAdjacentEdges[road]) {
                    if(adjacentEdge != -1 && this.edges[adjacentEdge] == EdgeState.Empty && !placableRoads.contains(adjacentEdge)) {
                        actions.add(new Action(Action.ActionType.PLAY_ROAD_BUILDING, new int[] {road, adjacentEdge}));
                    }
                }
            }
            List<Integer> placableRoadsList = new ArrayList<>(placableRoads);
            for(int i = 0; i < placableRoadsList.size(); i++) {
                for(int j = i + 1; j < placableRoadsList.size(); j++) {
                    actions.add(new Action(Action.ActionType.PLAY_ROAD_BUILDING, new int[] {placableRoadsList.get(i), placableRoadsList.get(j)}));
                }
            }
        }
        if(this.canPlayDevelopmentCardTypeThisTurn[catanPlayer][2]) {
            for(int i = 0; i < 5; i++) {
                if(this.resourceCounts[i] >= 2) {
                    actions.add(new Action(Action.ActionType.PLAY_YEAR_OF_PLENTY, new int[] {i}));
                }
            }
            for(int i = 0; i < 5; i++) {
                for(int j = i + 1; j < 5; j++) {
                    if(this.resourceCounts[i] >= 1 && this.resourceCounts[j] >= 1) {
                        actions.add(new Action(Action.ActionType.PLAY_YEAR_OF_PLENTY, new int[] {i, j}));
                    }
                }
            }
        }
        if(this.canPlayDevelopmentCardTypeThisTurn[catanPlayer][3]) {
            for(int i = 0; i < 5; i++) {
                actions.add(new Action(Action.ActionType.PLAY_MONOPOLY, new int[] {i}));
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
        for(int adjacentEdge : AdjacentDicts.vertexAdjacentEdges[vertex]) {
            if(adjacentEdge != -1 && this.edges[adjacentEdge] == EdgeState.roadFromPlayer(player)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isValidRoadPlacement(int edge, int player) {
        if(edge < 0 || edge > 71) {
            throw new IllegalArgumentException("Invalid edge: Out of bounds");
        }
        if(this.edges[edge] != EdgeState.Empty) {
            return false; 
        }
        for(int adjacentEdge : AdjacentDicts.edgeAdjacentEdges[edge]) {
            if(adjacentEdge != -1 && this.edges[adjacentEdge] == EdgeState.roadFromPlayer(player)) {
                return true;
            }
        }
        for(int adjacentVertex : AdjacentDicts.edgeAdjacentVertices[edge]) {
            if(adjacentVertex != -1 && (this.vertices[adjacentVertex] == VertexState.settlementFromPlayer(player) || this.vertices[adjacentVertex] == VertexState.cityFromPlayer(player))) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidSettlementPlacement(int vertex) {
        if(vertex < 0 || vertex > 53) {
            throw new IllegalArgumentException("Invalid vertex: Out of bounds");
        }
        if(this.vertices[vertex] != VertexState.Empty) {
            return false;
        }
        for(int i = 0; i < 3; i++) {
            if(AdjacentDicts.vertexAdjacentVertices[vertex][i] != -1 
            && this.vertices[AdjacentDicts.vertexAdjacentVertices[vertex][i]] != VertexState.Empty) {
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

    public Set<Integer> getPlacableRoads(int catanPlayer) {
        Set<Integer> placableRoads = new HashSet<>();
        for(int edge : this.openEdges) {
            for (int adjacentEdge : AdjacentDicts.edgeAdjacentEdges[edge]) {
                if(isValidRoadPlacement(edge, catanPlayer)) {
                    placableRoads.add(edge);
                }
            }
        }
        return placableRoads;
    }
    
    public void moveResourceFromPlayerToPlayer(int givePlayer, int receivePlayer, int amount, Resource resource) {
        if(this.playerHands[givePlayer][Resource.toInt(resource)] - amount < 0) {
            throw new IllegalArgumentException("Not enough resources to move");
        }
        this.playerHands[givePlayer][Resource.toInt(resource)] -= amount;
        this.playerHands[receivePlayer][Resource.toInt(resource)] += amount;
    }

    public void updateTradesFromPort(int player, int vertex) {
        for(int i = 0; i < 10; i++) {
            if(this.twoForOnePorts[i] == vertex) {
                this.bankTradesOffered[player][i / 2] = 2;
            }
        }
        for(int i = 0; i < 8; i++) {
            if(this.threeForOnePorts[i] == vertex) {
                for(int j = 0; j < 5; j++) {
                    if(this.bankTradesOffered[player][j] == 4) {
                        this.bankTradesOffered[player][j] = 3;
                    }
                }
            }
        }
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
                    int[] discarded = this.players[i].discardHalfOfHand(new PublicBoard(this, i), Arrays.copyOf(this.playerHands[i], 5), (sum + 1) / 2);
                    int count = 0;
                    for(int j = 0; j < 5; j++) {
                        count += discarded[j];
                    }
                    if(count != (sum + 1) / 2) {
                        throw new IllegalArgumentException("Invalid discard: Incorrect number of resources discarded");
                    }
                    for(int j = 0; j < 5; j++) {
                        if(this.playerHands[i][j] < discarded[j]) {
                            throw new IllegalArgumentException("Invalid discard: Not enough resources in hand");
                        }
                        payResourceToBank(i, Resource.values()[j], discarded[j]);
                    }
                }
            }

            List<Action> actions = new ArrayList<Action>();
            for(int i = 0; i < 19; i++) {
                if(this.tiles[i].isUnRobbed()) {
                    actions.add(new Action(Action.ActionType.MOVE_ROBBER, new int[] {i}));
                }
            }
            Action chosen = this.players[playerIndex].chooseAction(new PublicBoard(this,playerIndex), actions);
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
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                this.canPlayDevelopmentCardTypeThisTurn[i][j] = this.developmentCardsInHand[i][j] > 0;
            }
        }
        rollDice(catanPlayer);
        Action chosenAction;
        do {
            List<Action> actions = getValidActions(catanPlayer);
            chosenAction = player.chooseAction(new PublicBoard(this, catanPlayer), actions);
            if(executeAction(chosenAction, catanPlayer)) {
                return -1;
            }
        } while(chosenAction.getType() != Action.ActionType.PASS);
        return (catanPlayer + 1) % 4;
    }

    public boolean giveScore(int player, int amount) {
        this.playerScores[player] += amount;
        if(this.playerScores[player] >= this.scoreToWin) {
            endGame(player);
            return true;
        }
        return false;
    }
    public void takeScore(int player) {
        this.playerScores[player]--;
        if(this.playerScores[player] < 2) {
            throw new IllegalArgumentException("Player has less than 2 points");
        }
    }

    public boolean checkLargestArmy(int player) {
        if(this.knightsPlayed[player] >= 3) {
            if(this.largestArmyPlayer == -1) { return true;}
            if(this.knightsPlayed[player] > this.knightsPlayed[this.largestArmyPlayer]) {
                return true;
            }
        }
        return false;
    }

    public boolean moveLargestArmy(int player) {
        if(this.largestArmyPlayer != -1) {
            takeScore(this.largestArmyPlayer);
            takeScore(this.largestArmyPlayer);
        }
        this.largestArmyPlayer = player;
        return giveScore(player, 2);
    }
    

    public int getMaxRoadChainLength(int player) {
        List<Integer> roads = getPlayerRoads(player);
        int maxLength = 0;
        for(int road : roads) {
            Set<Integer> initialSet = new HashSet<>();  // Create empty set
            initialSet.add(road);                       // Add the initial road
            maxLength = Math.max(maxLength, getRoadChainLength(road, EdgeState.roadFromPlayer(player), initialSet, new HashSet<>()));
        }
        return maxLength;
    }

    public int getRoadChainLength(int currentRoad, EdgeState playerRoad, Set<Integer> visited, Set<Integer> backtracked) {
        int maxLength = visited.size();
        for(int edge : AdjacentDicts.edgeAdjacentEdges[currentRoad]) {
            if(edge != -1 && this.edges[edge] == playerRoad && !visited.contains(edge) && !backtracked.contains(edge)) {
                Set<Integer> newVisited = new HashSet<>(visited);
                newVisited.add(edge);
                Set<Integer> newBacktracked = new HashSet<>();
                for(int otherEdge : AdjacentDicts.edgeAdjacentEdges[currentRoad]) {
                    if(otherEdge != -1) {
                        newBacktracked.add(otherEdge);
                    }
                }
                maxLength = Math.max(maxLength, getRoadChainLength(edge, playerRoad, newVisited, newBacktracked));
            }
        }
        return maxLength;
    }
    

    public List<Integer> getPlayerRoads(int player) {
        List<Integer> roads = new ArrayList<Integer>();
        for(int i = 0; i < 72; i++) {
            if(this.edges[i] == EdgeState.roadFromPlayer(player)) {
                roads.add(i);
            }
        }
        return roads;
    }
    
    public boolean checkLongestRoad(int player) {
        if(this.roadLengths[player] >= 5) {
            if(this.longestRoadPlayer == -1) { return true;}
            if(this.roadLengths[player] > this.roadLengths[this.longestRoadPlayer]) {
                return true;
            }
        }
        return false;
    }

    public boolean moveLongestRoad(int player) {
        if(this.longestRoadPlayer != -1) {
            takeScore(this.longestRoadPlayer);
            takeScore(this.longestRoadPlayer);
        }
        this.longestRoadPlayer = player;
        
        return giveScore(player, 2);
    }

    public void endGame(int player) {
        System.out.println("Player P" + (player + 1) + " wins!");
        this.displayBoard("endboard.txt");
        if(!checkRoadsHaveAdjacentRoadsOrSettlements()) {
            System.out.println("Roads do not have adjacent roads or settlements");
            System.exit(0);
        }
    }

    public boolean executeAction(Action action, int catanPlayer) {
        Action chosen;
        switch(action.getType()) {
            case BUILD_ROAD:
                System.out.println("Player P" + (catanPlayer + 1) + " Building road at edge " + action.getArgs()[0]);
                if(!isValidRoadPlacement(action.getArgs()[0], catanPlayer)) {
                    throw new IllegalArgumentException("Invalid road placement");
                }
                this.edges[action.getArgs()[0]] = EdgeState.roadFromPlayer(catanPlayer);
                this.openEdges.remove(action.getArgs()[0]);
                this.buildableRoads[catanPlayer]--;
                payResourceToBank(catanPlayer, Resource.BRICK, 1);
                payResourceToBank(catanPlayer, Resource.WOOD, 1);
                this.roadLengths[catanPlayer] = getMaxRoadChainLength(catanPlayer);
                if(checkLongestRoad(catanPlayer)) {
                    return moveLongestRoad(catanPlayer);
                }
                return false;
            case BUILD_SETTLEMENT:
                if(!hasAdjacentRoad(action.getArgs()[0], catanPlayer)) {
                    throw new IllegalArgumentException("No adjacent road");
                }
            case BUILD_SETTLEMENT_START:
                System.out.println("Player P" + (catanPlayer + 1) + " Building settlement at vertex " + action.getArgs()[0]);
                if(!isValidSettlementPlacement(action.getArgs()[0])) {
                    throw new IllegalArgumentException("Invalid settlement placement");
                }
                this.vertices[action.getArgs()[0]] = VertexState.settlementFromPlayer(catanPlayer);
                this.openVertices.remove(action.getArgs()[0]);
                this.settlableVertices.remove(action.getArgs()[0]);
                this.cityableVertices.add(action.getArgs()[0]);
                this.buildableSettlements[catanPlayer]--;
                for(int j = 0; j < 3; j++) {
                    this.settlableVertices.remove(AdjacentDicts.vertexAdjacentVertices[action.getArgs()[0]][j]);
                }
                payResourceToBank(catanPlayer, Resource.BRICK, 1);
                payResourceToBank(catanPlayer, Resource.WOOD, 1);
                payResourceToBank(catanPlayer, Resource.WHEAT, 1);
                payResourceToBank(catanPlayer, Resource.SHEEP, 1);
                updateTradesFromPort(catanPlayer, action.getArgs()[0]);
                return giveScore(catanPlayer, 1);
                  
            case BUILD_CITY:
                System.out.println("Player P" + (catanPlayer + 1) + " Building city at vertex " + action.getArgs()[0]);
                this.vertices[action.getArgs()[0]] = VertexState.cityFromPlayer(catanPlayer);
                this.cityableVertices.remove(action.getArgs()[0]);
                
                this.buildableCities[catanPlayer]--;
                this.buildableSettlements[catanPlayer]++;
                return giveScore(catanPlayer, 1);
            case TRADE_WITH_BANK:
                int giveResource = action.getArgs()[0];
                int amount = action.getArgs()[1];
                int receiveResource = action.getArgs()[2];
                payResourceToBank(catanPlayer, Resource.values()[giveResource], amount);
                giveResource(catanPlayer, Resource.values()[receiveResource], 1);
                System.out.println("Player P" + (catanPlayer + 1) + " Trading with bank " + amount + " " + Resource.values()[giveResource] + " for " + 1 + " " + Resource.values()[receiveResource]);
                return false;
            case PROPOSE_PLAYER_TRADE:
                System.out.println("Player P" + (catanPlayer + 1) + " Proposing trade with " + (action.getArgs()));
                for(int i = 0; i < 5; i++) {
                    if(action.getArgs()[i] * -1 > this.playerHands[catanPlayer][i]) {
                        throw new IllegalArgumentException("Not enough resources to trade");
                    }
                }
                Action[] responses = new Action[4];
                List<Action> validResponses = new ArrayList<Action>();
                validResponses.add(new Action(Action.ActionType.REJECT_PLAYER_TRADE, new int[] {}));
                for(int i = 0; i < 4; i++) {
                    if(i != catanPlayer) {
                        responses[i] = this.players[i].respondToTrade(new PublicBoard(this, i), action);
                        if(responses[i].getType() != Action.ActionType.RESPOND_TO_PLAYER_TRADE && responses[i].getType() != Action.ActionType.REJECT_PLAYER_TRADE) {
                            throw new IllegalArgumentException("Invalid response to trade");
                        }
                        if(responses[i].getType() == Action.ActionType.RESPOND_TO_PLAYER_TRADE) {
                            boolean valid = true;
                            for(int j = 0; j < 5; j++) {
                                if(responses[i].getArgs()[j] * -1 > this.playerHands[i][j]) {
                                    valid = false;
                                }
                                if(responses[i].getArgs()[j] > this.playerHands[i][j]) {
                                    valid = false;
                                }
                            }
                            if(responses[i].getArgs()[5] != i) {
                                valid = false;
                            }
                            if(valid) {
                                validResponses.add(responses[i]);
                            }
                        }
                    }
                }
                chosen = this.players[catanPlayer].chooseAction(new PublicBoard(this, catanPlayer), Arrays.asList(responses));
                if(chosen.getType() == Action.ActionType.REJECT_PLAYER_TRADE) {
                    return false;
                }
                if(chosen.getType() == Action.ActionType.RESPOND_TO_PLAYER_TRADE) {
                    for(int i = 0; i < 5; i++) {
                        if(chosen.getArgs()[i] > 0) {
                            moveResourceFromPlayerToPlayer(catanPlayer, chosen.getArgs()[5], chosen.getArgs()[i], Resource.values()[i]);
                        }
                        if(chosen.getArgs()[i] < 0) {
                            moveResourceFromPlayerToPlayer(chosen.getArgs()[5], catanPlayer, chosen.getArgs()[i] * -1, Resource.values()[i]);
                        }
                    }
                }

                return false;
            case PURCHASE_DEVELOPMENT_CARD:
                System.out.println("Player P" + (catanPlayer + 1) + " Buying development card");
                int card = this.developmentCardsInDeck.poll();
                this.developmentCardsInHand[catanPlayer][card]++;
                System.out.println("Drew development card " + card);
                
                payResourceToBank(catanPlayer, Resource.WHEAT, 1);
                payResourceToBank(catanPlayer, Resource.SHEEP, 1);
                payResourceToBank(catanPlayer, Resource.ORE, 1);
                if(card == 4) {
                    return giveScore(catanPlayer, 1);
                }
                return false;
            case MOVE_ROBBER:
                System.out.println("Player P" + (catanPlayer + 1) + " Moving robber to tile " + action.getArgs()[0]);
                for (int i = 0; i < 19; i++) {
                    this.tiles[i].setRobbed(false);
                }
                this.tiles[action.getArgs()[0]].setRobbed(true);
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
                        chosen = this.players[catanPlayer].chooseAction(new PublicBoard(this, catanPlayer), stealActions);
                        return executeAction(chosen, catanPlayer);
                    }
                }
                return false;
            case STEAL_RESOURCE:
                int fromPlayer = action.getArgs()[0];
                int handSize = 0;
                for(int i = 0; i < 5; i++) {
                    handSize += this.playerHands[fromPlayer][i];
                }
                if(handSize <=0) {
                    return false;
                }
                int toStealId = rand.nextInt(handSize);
                for(int i = 0; i < 5; i++) {
                    toStealId -= this.playerHands[fromPlayer][i];
                    if(toStealId < 0) {
                        moveResourceFromPlayerToPlayer(fromPlayer, catanPlayer, 1, Resource.values()[i]);
                        return false;
                    }
                }
                System.out.println("Player P" + (catanPlayer + 1) + " Stealing resource from player P" + (fromPlayer + 1));
                return false;
            case PLAY_KNIGHT:
                System.out.println("Player P" + (catanPlayer + 1) + " Playing knight on tile " + action.getArgs()[0]);
                for(int i = 0; i < 4; i++) {
                    this.canPlayDevelopmentCardTypeThisTurn[catanPlayer][i] = false;
                }
                this.knightsPlayed[catanPlayer]++;
                if(checkLargestArmy(catanPlayer)) {
                    if(moveLargestArmy(catanPlayer)) {
                        return true;
                    }
                }
                for (int i = 0; i < 19; i++) {
                    this.tiles[i].setRobbed(false);
                }
                this.tiles[action.getArgs()[0]].setRobbed(true);
                List<Action> knightStealActions = new ArrayList<Action>();
                for(int i = 0; i < 6; i++) {
                    int vertexId = this.tiles[action.getArgs()[0]].getVertexId(i);
                    if(this.vertices[vertexId] != VertexState.Empty) {
                        int player = VertexState.getPlayer(this.vertices[vertexId]);
                        if(player != catanPlayer) {
                            knightStealActions.add(new Action(Action.ActionType.STEAL_RESOURCE, new int[] {player}));
                        }
                    }
                    if(!knightStealActions.isEmpty()) {
                        chosen = this.players[catanPlayer].chooseAction(new PublicBoard(this, catanPlayer), knightStealActions);
                        executeAction(chosen, catanPlayer);
                    }
                }
                this.developmentCardsInHand[catanPlayer][0]--;
                break;
            case PLAY_ROAD_BUILDING:
                System.out.println("ROAD BUILDING: Player P" + (catanPlayer + 1) + " Building road at edge " + action.getArgs()[0] + " and " + action.getArgs()[1]);
                if(this.edges[action.getArgs()[0]] != EdgeState.Empty || this.edges[action.getArgs()[1]] != EdgeState.Empty) {
                    throw new IllegalArgumentException("Roads must be empty to build");
                }
                if(!isValidRoadPlacement(action.getArgs()[0], catanPlayer)) {
                    throw new IllegalArgumentException("Invalid first road placement");
                }
                if(!isValidRoadPlacement(action.getArgs()[1], catanPlayer)) {
                    // Convert array to List for proper contains() check
                    List<Integer> adjacentEdges = new ArrayList<>();
                    for (int edge : AdjacentDicts.edgeAdjacentEdges[action.getArgs()[0]]) {
                        if (edge != -1) {
                            adjacentEdges.add(edge);
                        }
                    }
                    
                    if(!adjacentEdges.contains(action.getArgs()[1])) {
                        throw new IllegalArgumentException("Invalid second road placement - roads must be adjacent");
                    }
                }
                for(int i = 0; i < 4; i++) {
                    this.canPlayDevelopmentCardTypeThisTurn[catanPlayer][i] = false;
                }
                for(int i = 0; i < 2; i++) {
                    this.edges[action.getArgs()[i]] = EdgeState.roadFromPlayer(catanPlayer);
                    this.openEdges.remove(action.getArgs()[i]);
                }
                this.buildableRoads[catanPlayer] -= 2;
                this.roadLengths[catanPlayer] = getMaxRoadChainLength(catanPlayer);
                if(checkLongestRoad(catanPlayer)) {
                    moveLongestRoad(catanPlayer);
                }
                this.developmentCardsInHand[catanPlayer][1]--;
                
                break;
            case PLAY_YEAR_OF_PLENTY:
                System.out.println("Player P" + (catanPlayer + 1) + " Playing year of plenty");
                for(int i = 0; i < 4; i++) {
                    this.canPlayDevelopmentCardTypeThisTurn[catanPlayer][i] = false;
                }
                if(action.getArgs().length == 1) {
                    System.out.println("Player P" + (catanPlayer + 1) + " Playing year of plenty with " + Resource.values()[action.getArgs()[0]]);
                    giveResource(catanPlayer, Resource.values()[action.getArgs()[0]], 2);
                } else {
                    System.out.println("Player P" + (catanPlayer + 1) + " Playing year of plenty with " + Resource.values()[action.getArgs()[0]] + " and " + Resource.values()[action.getArgs()[1]]);
                    giveResource(catanPlayer, Resource.values()[action.getArgs()[0]], 1);
                    giveResource(catanPlayer, Resource.values()[action.getArgs()[1]], 1);
                }
                this.developmentCardsInHand[catanPlayer][2]--;
                break;
            case PLAY_MONOPOLY:
                System.out.println("Player P" + (catanPlayer + 1) + " Playing monopoly with " + Resource.values()[action.getArgs()[0]]);
                for(int i = 0; i < 4; i++) {
                    this.canPlayDevelopmentCardTypeThisTurn[catanPlayer][i] = false;
                }
                for(int i = 0; i < 4; i++) {
                    if(i != catanPlayer) {
                        moveResourceFromPlayerToPlayer(i, catanPlayer, this.playerHands[i][action.getArgs()[0]], Resource.values()[action.getArgs()[0]]);
                    }
                }
                this.developmentCardsInHand[catanPlayer][3]--;
                
                break;
            case PASS:
                break;  
        }
        return false;
    }

    public void placeStartingPositions(CatanPlayer[] players) {
        if(players.length != 4) {
            throw new IllegalArgumentException("Invalid number of players");
        }

        // First round - players place first settlement and road
        for(int i = 0; i < 4; i++) {
            System.out.println("Player P" + (i + 1) + " placing first starting position");
            List<Action> validActions = getValidStartingPositions();
            Action chosen = players[i].chooseAction(new PublicBoard(this, i), validActions);
            
            if(chosen.getType() != Action.ActionType.BUILD_SETTLEMENT_START) {    
                throw new IllegalArgumentException("Invalid starting position action");
            }
            
            // Place settlement and road
            int vertex = chosen.getArgs()[0];
            executeAction(chosen, i);

            // Modified this section to skip road validation during initial placement
            validActions = new ArrayList<Action>();
            for (int edge : AdjacentDicts.vertexAdjacentEdges[vertex]) {
                if(edge != -1 && this.edges[edge] == EdgeState.Empty) {  // Only check if edge exists and is empty
                    validActions.add(new Action(Action.ActionType.BUILD_ROAD, new int[]{edge}));
                }
            }
            chosen = players[i].chooseAction(new PublicBoard(this, i), validActions);
            if(chosen.getType() != Action.ActionType.BUILD_ROAD) {
                throw new IllegalArgumentException("Invalid starting position action");
            }
            
            // Skip normal road validation for starting positions
            this.edges[chosen.getArgs()[0]] = EdgeState.roadFromPlayer(i);
            this.openEdges.remove(chosen.getArgs()[0]);
            this.buildableRoads[i]--;
        }

        // Second round - players place second settlement and road (reverse order)
        for(int i = 3; i >= 0; i--) {
            System.out.println("Player P" + (i + 1) + " placing second starting position");
            List<Action> validActions = getValidStartingPositions();
            Action chosen = players[i].chooseAction(new PublicBoard(this, i), validActions);
            
            if(chosen.getType() != Action.ActionType.BUILD_SETTLEMENT_START) {    
                throw new IllegalArgumentException("Invalid starting position action");
            }
            
            // Place settlement and road
            int vertex = chosen.getArgs()[0];
            executeAction(chosen, i);

            // Give initial resources for second settlement
            for(int j = 0; j < 3; j++) {
                if(AdjacentDicts.vertexAdjacentTiles[vertex][j] != -1 && this.tiles[AdjacentDicts.vertexAdjacentTiles[vertex][j]].getOdds() != Odds.SEVEN) {
                    giveResource(i, this.tiles[AdjacentDicts.vertexAdjacentTiles[vertex][j]].getResource(), 1);
                }
            }

            // Modified this section to match first round logic
            validActions = new ArrayList<Action>();
            for (int edge : AdjacentDicts.vertexAdjacentEdges[vertex]) {
                if(edge != -1 && this.edges[edge] == EdgeState.Empty) {  // Only check if edge exists and is empty
                    validActions.add(new Action(Action.ActionType.BUILD_ROAD, new int[]{edge}));
                }
            }
            chosen = players[i].chooseAction(new PublicBoard(this, i), validActions);
            if(chosen.getType() != Action.ActionType.BUILD_ROAD) {
                throw new IllegalArgumentException("Invalid starting position action");
            }
            
            // Skip normal road validation for starting positions
            this.edges[chosen.getArgs()[0]] = EdgeState.roadFromPlayer(i);
            this.openEdges.remove(chosen.getArgs()[0]);
            this.buildableRoads[i]--;
        }
        this.players = players;
    }

    // Helper method to get valid starting positions
    private List<Action> getValidStartingPositions() {
        List<Action> actions = new ArrayList<>();
        
        // For each valid settlement location
        for(int vertex : this.settlableVertices) {
            actions.add(new Action(Action.ActionType.BUILD_SETTLEMENT_START, new int[]{vertex}));
        }
        return actions;
    }

    

    public EdgeState getEdge(int edge) {
        if(edge < 0 || edge > 71) {
            throw new IllegalArgumentException("Edge must be between 0 and 71 inclusive");
        }
        return this.edges[edge];
    }

    public VertexState getVertex(int vertex) {
        if(vertex < 0 || vertex > 53) {
            throw new IllegalArgumentException("Vertex must be between 0 and 53 inclusive");
        }
        return this.vertices[vertex];
    }

    public Tile getTile(int tile) {
        if(tile < 0 || tile > 18) {
            throw new IllegalArgumentException("Tile must be between 0 and 18 inclusive");
        }
        return this.tiles[tile];
    }

    public boolean checkRoadHasAdjacentRoadsOrSettlements(int edge, int player) {
        for(int adjacentEdge : AdjacentDicts.edgeAdjacentEdges[edge]) {
            if(adjacentEdge != -1 && (this.edges[adjacentEdge] == EdgeState.roadFromPlayer(player))) {
                return true;
            }
        }
        for(int adjacentVertex : AdjacentDicts.edgeAdjacentVertices[edge]) {
            if(adjacentVertex != -1 && (this.vertices[adjacentVertex] == VertexState.settlementFromPlayer(player) || this.vertices[adjacentVertex] == VertexState.cityFromPlayer(player))) {
                return true;
            }
        }
        return false;
    }
    public boolean checkRoadsHaveAdjacentRoadsOrSettlements() {
        for(int edge = 0; edge < 72; edge++) {
            if(this.edges[edge] != EdgeState.Empty && !checkRoadHasAdjacentRoadsOrSettlements(edge, this.edges[edge].getPlayer())) {
                System.out.println("Edge " + edge + " does not have adjacent roads or settlements");
                return false;
            }
        }
        return true;
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

    public void displayBoard(String filename) {
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
               Resource.WOOD + " " + Resource.ORE + " " + Resource.SHEEP + " DEV PTS";
        
        out += Color.RED + "\nP1: " + Color.RESET + 
               String.format("%3d %3d %3d %3d %3d %3d %3d", 
               this.playerHands[0][0], this.playerHands[0][1], this.playerHands[0][2], 
               this.playerHands[0][3], this.playerHands[0][4], StringUtils.sum(this.developmentCardsInHand[0]), this.playerScores[0]);
        
        out += Color.GREEN + "\nP2: " + Color.RESET + 
               String.format("%3d %3d %3d %3d %3d %3d %3d", 
               this.playerHands[1][0], this.playerHands[1][1], this.playerHands[1][2], 
               this.playerHands[1][3], this.playerHands[1][4], StringUtils.sum(this.developmentCardsInHand[1]), this.playerScores[1]);
        
        out += Color.YELLOW + "\nP3: " + Color.RESET + 
               String.format("%3d %3d %3d %3d %3d %3d %3d", 
               this.playerHands[2][0], this.playerHands[2][1], this.playerHands[2][2], 
               this.playerHands[2][3], this.playerHands[2][4], StringUtils.sum(this.developmentCardsInHand[2]), this.playerScores[2]);
        
        out += Color.BLUE + "\nP4: " + Color.RESET + 
               String.format("%3d %3d %3d %3d %3d %3d %3d", 
               this.playerHands[3][0], this.playerHands[3][1], this.playerHands[3][2], 
               this.playerHands[3][3], this.playerHands[3][4], StringUtils.sum(this.developmentCardsInHand[3]), this.playerScores[3]);
        out += "\nBNK:" + String.format("%3d %3d %3d %3d %3d %3d", 
               this.resourceCounts[0], this.resourceCounts[1], this.resourceCounts[2], this.resourceCounts[3], this.resourceCounts[4], this.developmentCardsInDeck.size());
        if(this.longestRoadPlayer != -1) {
            String playerNameColor;
            switch(this.longestRoadPlayer) {
                case 0:
                    playerNameColor = Color.RED + "P1" + Color.RESET;
                    break;
                case 1:
                    playerNameColor = Color.GREEN + "P2" + Color.RESET;
                    break;
                case 2:
                    playerNameColor = Color.YELLOW + "P3" + Color.RESET;
                    break;
                case 3:
                    playerNameColor = Color.BLUE + "P4" + Color.RESET;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid longest road player");
            }
            out += "\nLONGEST ROAD: " + playerNameColor + " with length " + this.roadLengths[this.longestRoadPlayer];
        }
        if(this.largestArmyPlayer != -1) {
            String playerNameColor;
            switch(this.largestArmyPlayer) {
                case 0:
                    playerNameColor = Color.RED + "P1" + Color.RESET;
                    break;
                case 1:
                    playerNameColor = Color.GREEN + "P2" + Color.RESET;
                    break;
                case 2:
                    playerNameColor = Color.YELLOW + "P3" + Color.RESET;
                    break;
                case 3:
                    playerNameColor = Color.BLUE + "P4" + Color.RESET;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid largest army player");
            }
            out += "\nLARGEST ARMY: " + playerNameColor + " with " + this.knightsPlayed[this.largestArmyPlayer] + " knights";
        }
        try {
            FileWriter writer = new FileWriter(filename);
            writer.write(out);
            writer.close();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    

    public void displayBoardIndexes(String filename) {
        String out = "          X       X       X";
        out += "\n        " + String.format("%2d", 5) + " " + String.format("%2d", 0) + "   " + String.format("%2d", 10) + " " + String.format("%2d", 6) + "   " + String.format("%2d", 15) + " " + String.format("%2d", 11);
        out += "\n       " + String.format("%2d", 5) + "   " + String.format("%2d", 0) + " " + String.format("%2d", 10) + "   " + String.format("%2d", 6) + " " + String.format("%2d", 15) + "   " + String.format("%2d", 11);
        out += "\n       X      X       X       X";
        out += "\n      " + String.format("%2d", 4) + "      " + String.format("%2d", 1) + "      " + String.format("%2d", 7) + "      " + String.format("%2d", 12);
        out += "\n      " + String.format("%2d", 4) + "      " + String.format("%2d", 1) + "      " + String.format("%2d", 7) + "      " + String.format("%2d", 12);
        out += "\n       X       X       X       X";
        out += "\n     " + String.format("%2d", 20) + " " + String.format("%2d", 3) + "   " + String.format("%2d", 2) + " " + String.format("%2d", 9) + "   " + String.format("%2d", 8) + " " + String.format("%2d", 14) + "   " + String.format("%2d", 13) + " " + String.format("%2d", 27);
        out += "\n    " + String.format("%2d", 20) + "   " + String.format("%2d", 3) + " " + String.format("%2d", 2) + "   " + String.format("%2d", 9) + " " + String.format("%2d", 8) + "   " + String.format("%2d", 14) + " " + String.format("%2d", 13) + "   " + String.format("%2d", 27);
        
        out += "\n    X       X       X       X       X";
        out += "\n   " + String.format("%2d", 19) + "      " + String.format("%2d", 16) + "      " + String.format("%2d", 21) + "      " + String.format("%2d", 24) + "      " + String.format("%2d", 28);
        out += "\n   " + String.format("%2d", 19) + "      " + String.format("%2d", 16) + "      " + String.format("%2d", 21) + "      " + String.format("%2d", 24) + "      " + String.format("%2d", 28);
        
        out += "\n    X       X       X       X       X";
        out += "\n  " + String.format("%2d", 35) + " " + String.format("%2d", 18) + "   " + String.format("%2d", 17) + " " + String.format("%2d", 23) + "   " + String.format("%2d", 22) + " " + String.format("%2d", 26) + "   " + String.format("%2d", 25) + " " + String.format("%2d", 30) + "   " + String.format("%2d", 29) + " " + String.format("%2d", 45);
        out += "\n " + String.format("%2d", 35) + "   " + String.format("%2d", 18) + " " + String.format("%2d", 17) + "   " + String.format("%2d", 23) + " " + String.format("%2d", 22) + "   " + String.format("%2d", 26) + " " + String.format("%2d", 25) + "   " + String.format("%2d", 30) + " " + String.format("%2d", 29) + "   " + String.format("%2d", 45);

        out += "\nX       X       X       X       X       X";
        out += "\n" + String.format("%2d", 34) + "      " + String.format("%2d", 31) + "      " + String.format("%2d", 36) + "      " + String.format("%2d", 39) + "      " + String.format("%2d", 42) + "      " + String.format("%2d", 46);
        out += "\n" + String.format("%2d", 34) + "      " + String.format("%2d", 31) + "      " + String.format("%2d", 36) + "      " + String.format("%2d", 39) + "      " + String.format("%2d", 42) + "      " + String.format("%2d", 46);
        out += "\nX       X       X       X       X       X";
        out += "\n " + String.format("%2d", 33) + "   " + String.format("%2d", 32) + " " + String.format("%2d", 38) + "   " + String.format("%2d", 37) + " " + String.format("%2d", 41) + "   " + String.format("%2d", 40) + " " + String.format("%2d", 44) + "   " + String.format("%2d", 43) + " " + String.format("%2d", 48) + "   " + String.format("%2d", 47);
        out += "\n  " + String.format("%2d", 33) + " " + String.format("%2d", 32) + "   " + String.format("%2d", 38) + " " + String.format("%2d", 37) + "   " + String.format("%2d", 41) + " " + String.format("%2d", 40) + "   " + String.format("%2d", 44) + " " + String.format("%2d", 43) + "   " + String.format("%2d", 48) + " " + String.format("%2d", 47);
        
        out += "\n    X       X       X       X       X";
        out += "\n   " + String.format("%2d", 52) + "      " + String.format("%2d", 49) + "      " + String.format("%2d", 53) + "      " + String.format("%2d", 56) + "      " + String.format("%2d", 59);
        out += "\n   " + String.format("%2d", 52) + "      " + String.format("%2d", 49) + "      " + String.format("%2d", 53) + "      " + String.format("%2d", 56) + "      " + String.format("%2d", 59);
        out += "\n    X      X       X       X       X";
        
        out += "\n    " + String.format("%2d", 51) + "   " + String.format("%2d", 50) + " " + String.format("%2d", 55) + "   " + String.format("%2d", 54) + " " + String.format("%2d", 58) + "   " + String.format("%2d", 57) + " " + String.format("%2d", 61) + "   " + String.format("%2d", 60);
        out += "\n     " + String.format("%2d", 51) + " " + String.format("%2d", 50) + "   " + String.format("%2d", 55) + " " + String.format("%2d", 54) + "   " + String.format("%2d", 58) + " " + String.format("%2d", 57) + "   " + String.format("%2d", 61) + " " + String.format("%2d", 60);
        out += "\n      X        X       X       X";
        out += "\n      " + String.format("%2d", 65) + "      " + String.format("%2d", 62) + "      " + String.format("%2d", 66) + "      " + String.format("%2d", 69);
        out += "\n      " + String.format("%2d", 65) + "      " + String.format("%2d", 62) + "      " + String.format("%2d", 66) + "      " + String.format("%2d", 69);
        out += "\n       X      X       X       X";
        out += "\n       " + String.format("%2d", 64) + "   " + String.format("%2d", 63) + " " + String.format("%2d", 68) + "   " + String.format("%2d", 67) + " " + String.format("%2d", 71) + "   " + String.format("%2d", 70);
        out += "\n        " + String.format("%2d", 64) + " " + String.format("%2d", 63) + "   " + String.format("%2d", 68) + " " + String.format("%2d", 67) + "   " + String.format("%2d", 71) + " " + String.format("%2d", 70);
        out += "\n          X       X       X";
        
        try {
            FileWriter writer = new FileWriter(filename);
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

    public int[] getPlayerHand(int player) {
        if(player < 0 || player > 3) {
            throw new IllegalArgumentException("Player must be between 0 and 3 inclusive");
        }
        return this.playerHands[player];
    }

}
