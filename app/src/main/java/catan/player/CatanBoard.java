package catan.player;

import java.io.FileWriter;
import java.io.IOException;

class Color { 
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String BROWN = "\u001B[38;5;130m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
}
class VertexAdjacent {
    public static final int[][] vertexAdjacentTiles = {
        {0, -1, -1},    // Vertex 0
        {0, 1, -1},     // Vertex 1
        {0, 1, 4},      // Vertex 2
        {0, 3, 4},      // Vertex 3
        {0, 3, -1},     // Vertex 4
        {0, -1, -1},    // Vertex 5
        {1, -1, -1},    // Vertex 6
        {1, 2, -1},     // Vertex 7
        {1, 2, 5},      // Vertex 8
        {1, 4, 5},      // Vertex 9
        {2, -1, -1},    // Vertex 10
        {2, -1, -1},    // Vertex 11
        {2, 6, -1},     // Vertex 12
        {2, 5, 6},      // Vertex 13
        {3, 4, 8},      // Vertex 14
        {3, 7, 8},      // Vertex 15
        {3, 7, -1},     // Vertex 16
        {3, -1, -1},    // Vertex 17
        {4, 5, 9},      // Vertex 18
        {4, 8, 9},      // Vertex 19
        {5, 6, 10},     // Vertex 20
        {5, 9, 10},     // Vertex 21
        {6, -1, -1},    // Vertex 22
        {6, 11, -1},    // Vertex 23
        {6, 10, 11},    // Vertex 24
        {7, 8, 12},     // Vertex 25
        {7, 12, -1},    // Vertex 26
        {7, -1, -1},    // Vertex 27
        {7, -1, -1},    // Vertex 28
        {8, 9, 13},     // Vertex 29
        {8, 12, 13},    // Vertex 30
        {9, 10, 14},    // Vertex 31
        {9, 13, 14},    // Vertex 32
        {10, 11, 15},   // Vertex 33
        {10, 14, 15},   // Vertex 34
        {11, -1, -1},   // Vertex 35
        {11, -1, -1},   // Vertex 36
        {11, 15, -1},   // Vertex 37
        {12, 13, 16},   // Vertex 38
        {12, 16, -1},   // Vertex 39
        {12, -1, -1},   // Vertex 40
        {13, 14, 17},   // Vertex 41
        {13, 16, 17},   // Vertex 42
        {14, 15, 18},   // Vertex 43
        {14, 17, 18},   // Vertex 44
        {15, -1, -1},   // Vertex 45
        {15, 18, -1},   // Vertex 46
        {16, 17, -1},   // Vertex 47
        {16, -1, -1},   // Vertex 48
        {16, -1, -1},   // Vertex 49
        {17, 18, -1},   // Vertex 50
        {17, -1, -1},   // Vertex 51
        {18, -1, -1},   // Vertex 52
        {18, -1, -1}    // Vertex 53
    };
    public static final int[][] vertexAdjacentEdges = {
        {0, 5, -1},     // Vertex 0
        {1, 0, 10},     // Vertex 1
        {2, 1, 9},      // Vertex 2
        {3, 2, 16},     // Vertex 3
        {4, 3, 20},     // Vertex 4
        {5, 4, -1},     // Vertex 5
        {6, 10, -1},    // Vertex 6
        {7, 6, 15},     // Vertex 7
        {8, 7, 14},     // Vertex 8
        {9, 8, 21},     // Vertex 9
        {11, 15, -1},   // Vertex 10
        {12, 11, -1},   // Vertex 11
        {13, 12, 27},   // Vertex 12
        {14, 13, 24},   // Vertex 13
        {17, 16, 23},   // Vertex 14
        {18, 17, 31},   // Vertex 15
        {19, 18, 35},   // Vertex 16
        {20, 19, -1},   // Vertex 17
        {22, 21, 26},   // Vertex 18
        {23, 22, 36},   // Vertex 19
        {25, 24, 30},   // Vertex 20
        {26, 25, 39},   // Vertex 21
        {28, 27, -1},   // Vertex 22
        {29, 28, 45},   // Vertex 23
        {30, 29, 42},   // Vertex 24
        {32, 31, 38},   // Vertex 25
        {33, 32, 52},   // Vertex 26
        {34, 33, -1},   // Vertex 27
        {35, 34, -1},   // Vertex 28
        {37, 36, 41},   // Vertex 29
        {38, 37, 49},   // Vertex 30
        {40, 39, 44},   // Vertex 31
        {41, 40, 53},   // Vertex 32
        {43, 42, 48},   // Vertex 33
        {44, 43, 56},   // Vertex 34
        {46, 45, -1},   // Vertex 35
        {47, 46, -1},   // Vertex 36
        {48, 47, 59},   // Vertex 37
        {50, 49, 55},   // Vertex 38
        {51, 50, 65},   // Vertex 39
        {52, 51, -1},   // Vertex 40
        {54, 53, 58},   // Vertex 41
        {55, 54, 62},   // Vertex 42
        {57, 56, 61},   // Vertex 43
        {58, 57, 66},   // Vertex 44
        {60, 59, -1},   // Vertex 45
        {61, 60, 69},   // Vertex 46
        {63, 62, 68},   // Vertex 47
        {64, 63, -1},   // Vertex 48
        {65, 64, -1},   // Vertex 49
        {67, 66, 71},   // Vertex 50
        {68, 67, -1},   // Vertex 51
        {70, 69, -1},   // Vertex 52
        {71, 70, -1}    // Vertex 53
    };
    public static final int[][] vertexAdjacentVertices = {
        {1, 5, -1},      // Vertex 0
        {0, 2, 6},       // Vertex 1
        {1, 3, 9},       // Vertex 2
        {2, 4, 14},      // Vertex 3
        {3, 5, 17},      // Vertex 4
        {0, 4, -1},      // Vertex 5
        {1, 7, -1},      // Vertex 6
        {6, 8, 10},      // Vertex 7
        {7, 9, 13},      // Vertex 8
        {2, 8, 18},      // Vertex 9
        {7, 11, -1},     // Vertex 10
        {10, 12, -1},    // Vertex 11
        {11, 13, 22},    // Vertex 12
        {8, 12, 20},     // Vertex 13
        {3, 15, 19},     // Vertex 14
        {14, 16, 25},    // Vertex 15
        {15, 17, 28},    // Vertex 16
        {4, 16, -1},     // Vertex 17
        {9, 19, 21},     // Vertex 18
        {14, 18, 29},    // Vertex 19
        {13, 21, 24},    // Vertex 20
        {18, 20, 31},    // Vertex 21
        {12, 23, -1},    // Vertex 22
        {22, 24, 35},    // Vertex 23
        {20, 23, 33},    // Vertex 24
        {15, 26, 30},    // Vertex 25
        {25, 27, 40},    // Vertex 26
        {26, 28, -1},    // Vertex 27
        {16, 27, -1},    // Vertex 28
        {19, 30, 32},    // Vertex 29
        {25, 29, 38},    // Vertex 30
        {21, 32, 34},    // Vertex 31
        {29, 31, 41},    // Vertex 32
        {24, 34, 37},    // Vertex 33
        {31, 33, 43},    // Vertex 34
        {23, 36, -1},    // Vertex 35
        {35, 37, -1},    // Vertex 36
        {33, 36, 45},    // Vertex 37
        {30, 39, 42},    // Vertex 38
        {38, 40, 49},    // Vertex 39
        {26, 39, -1},    // Vertex 40
        {32, 42, 44},    // Vertex 41
        {38, 41, 47},    // Vertex 42
        {34, 44, 46},    // Vertex 43
        {41, 43, 50},    // Vertex 44
        {37, 46, -1},    // Vertex 45
        {43, 45, 52},    // Vertex 46
        {42, 48, 51},    // Vertex 47
        {47, 49, -1},    // Vertex 48
        {39, 48, -1},    // Vertex 49
        {44, 51, 53},    // Vertex 50
        {47, 50, -1},    // Vertex 51
        {46, 53, -1},    // Vertex 52
        {50, 52, -1}     // Vertex 53
    };
}

enum Resource {
    BRICK,
    WHEAT,
    WOOD,
    ORE,
    SHEEP,
    NONE
}

enum EdgeState {
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
}

enum VertexState {
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



class TileDirection {
    public static final int NE = 0;
    public static final int E = 1;
    public static final int SE = 2;
    public static final int SW = 3;
    public static final int W = 4;
    public static final int NW = 5;
}

class VertexDirection {
    public static final int N = 0;
    public static final int NE = 1;
    public static final int SE = 2;
    public static final int S = 3;
    public static final int SW = 4;
    public static final int NW = 5;

    public static int[] getAdjacentEdges(int vertex) {
        if(vertex < 0 || vertex > 5 ) {
            throw new IllegalArgumentException("Invalid vertex");
        }
        return new int[] {((vertex - 1) + 6) % 6, vertex % 6};
    }
}

class Tile {
    private Resource resource;
    private int odds;
    private int[] vertices;
    private int[] edges;
    private int id; 
    private boolean isRobbed;
    public Tile(Resource resource, int odds, int id) {
        this.resource = resource;
        this.odds = odds;
        this.vertices = new int[6];
        this.edges = new int[6];
        this.id = id;
        this.isRobbed = false;
    }
    public int getVertexId(int id) {
        return this.vertices[id];
    }
    public int getEdgeId(int id) {
        return this.edges[id];
    }
    public void populateVerticesAndEdges(Tile[] tiles, CatanBoard board) {
        for (int i = 0; i < 6; i++) {
            int[] adjacentEdges = VertexDirection.getAdjacentEdges(i);
            if(tiles[adjacentEdges[0]] != null) {
                this.vertices[i] = tiles[adjacentEdges[0]].getVertexId((i + 2) % 6);
            } else if (tiles[adjacentEdges[1]] != null) {
                this.vertices[i] = tiles[adjacentEdges[1]].getVertexId(((i - 2) + 6) % 6);
            }
            else {
                this.vertices[i] = board.allocateNewVertex();
            }
        }
        for(int i = 0; i < 6; i++) {
            if(tiles[i] != null) {
                this.edges[i] = tiles[i].getEdgeId((i + 3) % 6);
            }
            else {
                this.edges[i] = board.allocateNewEdge();
            }
        }

    }
    public void printBoard() {
        for(int i = 0; i < 6; i++) {
            System.out.println("Tile " + this.id + " Vertex " + this.vertices[i] + " Edge " + this.edges[i]);
        }
    }   
            
}   


public class CatanBoard {
    
    private VertexState[] vertices;
    private EdgeState[] edges;
    private Tile[] tiles;
    private int[][] vertexAdjacentTiles;
    private int vertexIndex;
    private int edgeIndex;
    public CatanBoard() {
        this.vertices = new VertexState[54];
        for (int i = 0; i < 54; i++) {
            this.vertices[i] = VertexState.Empty;
        }
        this.edges = new EdgeState[72];
        for (int i = 0; i < 72; i++) {
            this.edges[i] = EdgeState.Empty;
        }
        this.tiles = new Tile[19];
        for (int i = 0; i < 19; i++) {
            this.tiles[i] = new Tile(Resource.NONE, 0, i);
        }
        this.vertexIndex = 0;
        this.edgeIndex = 0;
        this.vertexAdjacentTiles = new int[54][3];

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
            if(this.vertexAdjacentTiles[vertex][i] == -1) {
                return true;
            }
            if(this.vertices[this.vertexAdjacentTiles[vertex][i]] != VertexState.Empty) {
                return false;
            }        }
        return true;
    }

    public boolean areAdjacent(int vertex, int edge) {
        for(int i = 0; i < 3; i++) {
            if(VertexAdjacent.vertexAdjacentEdges[vertex][i] == edge) {
                return true;
            }
        }
        return false;
    }
    
    public void placeStartingPositions(CatanPlayer[] players) {
        if(players.length != 4) {
            throw new IllegalArgumentException("Invalid number of players");
        }
        int[] returnValue;

        for(int i = 0; i < 4; i++) {
            returnValue = players[i].getStartingPosition(this);
            if(returnValue.length != 2) {
                throw new IllegalArgumentException("Invalid starting position");
            }
            if(!isValidSettlementPlacement(returnValue[0])) {
                throw new IllegalArgumentException("Invalid starting vertex");
            }
            if(!areAdjacent(returnValue[0], returnValue[1])) {
                throw new IllegalArgumentException("Invalid starting edge");
            }
            this.vertices[returnValue[0]] = VertexState.settlementFromPlayer(i);
            this.edges[returnValue[1]] = EdgeState.roadFromPlayer(i);   
        }
        for(int i = 3; i >= 0; i--) {
            returnValue = players[i].getStartingPosition(this);
            if(returnValue.length != 2) {
                throw new IllegalArgumentException("Invalid starting position");
            }
            if(!isValidSettlementPlacement(returnValue[0])) {
                throw new IllegalArgumentException("Invalid starting vertex");
            }
            if(!areAdjacent(returnValue[0], returnValue[1])) {
                throw new IllegalArgumentException("Invalid starting edge");
            }
            this.vertices[returnValue[0]] = VertexState.settlementFromPlayer(i);
            this.edges[returnValue[1]] = EdgeState.roadFromPlayer(i);   
        }
        
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
        out += "\n      " + displayEdge(4,"|") + "     " + displayEdge(1,"|") + "     " + displayEdge(7,"|") + "     " + displayEdge(12,"|");
        out += "\n      " + displayEdge(4,"|") + "     " + displayEdge(1,"|") + "     " + displayEdge(7,"|") + "     " + displayEdge(12,"|");
        out += "\n      " + getVertexDisplay(4) + "     " + getVertexDisplay(2) + "     " + getVertexDisplay(8) + "     " + getVertexDisplay(12);
        out += "\n     " + displayEdge(20,"/") + " " + displayEdge(3,"\\") + "   " + displayEdge(2,"/") + " " + displayEdge(9,"\\") + "   " + displayEdge(8, "/") + " " + displayEdge(14,"\\") + "   " + displayEdge(13, "/") + " " + displayEdge(27,"\\");
        out += "\n    " + displayEdge(20,"/") + "   " + displayEdge(3,"\\") + " " + displayEdge(2,"/") + "   " + displayEdge(9,"\\") + " " + displayEdge(8, "/") + "   " + displayEdge(14,"\\") + " " + displayEdge(13, "/") + "   " + displayEdge(27,"\\");
        
        out += "\n   " + getVertexDisplay(17) + "     " + getVertexDisplay(3) + "     " + getVertexDisplay(9) + "     " + getVertexDisplay(13) + "     " + getVertexDisplay(22);
        out += "\n   " + displayEdge(19,"|") + "     " + displayEdge(16,"|") + "     " + displayEdge(21,"|") + "     " + displayEdge(24,"|") + "     " + displayEdge(28,"|");
        out += "\n   " + displayEdge(19,"|") + "     " + displayEdge(16,"|") + "     " + displayEdge(21,"|") + "     " + displayEdge(24,"|") + "     " + displayEdge(28,"|");
        
        out += "\n   " + getVertexDisplay(16) + "     " + getVertexDisplay(14) + "     " + getVertexDisplay(18) + "     " + getVertexDisplay(20) + "     " + getVertexDisplay(23);        
        out += "\n  " + displayEdge(35,"/") + " " + displayEdge(18,"\\") + "   " + displayEdge(17,"/") + " " + displayEdge(23,"\\") + "   " + displayEdge(22, "/") + " " + displayEdge(26,"\\") + "   " + displayEdge(25, "/") + " " + displayEdge(30,"\\") + "   " + displayEdge(29, "/") + " " + displayEdge(45,"\\");        
        out += "\n " + displayEdge(35,"/") + "   " + displayEdge(18,"\\") + " " + displayEdge(17,"/") + "   " + displayEdge(23,"\\") + " " + displayEdge(22, "/") + "   " + displayEdge(26,"\\") + " " + displayEdge(25, "/") + "   " + displayEdge(30,"\\") + " " + displayEdge(29, "/") + "   " + displayEdge(45,"\\");

        out += "\n" + getVertexDisplay(28) + "     " + getVertexDisplay(15) + "     " + getVertexDisplay(19) + "     " + getVertexDisplay(21) + "     " + getVertexDisplay(24) + "     " + getVertexDisplay(35);
        out += "\n" + displayEdge(34,"|") + "     " + displayEdge(31,"|") + "     " + displayEdge(36,"|") + "     " + displayEdge(39,"|") + "     " + displayEdge(42,"|") + "     " + displayEdge(46,"|");
        out += "\n" + displayEdge(34,"|") + "     " + displayEdge(31,"|") + "     " + displayEdge(36,"|") + "     " + displayEdge(39,"|") + "     " + displayEdge(42,"|") + "     " + displayEdge(46,"|");
        out += "\n" + getVertexDisplay(27) + "     " + getVertexDisplay(25) + "     " + getVertexDisplay(29) + "     " + getVertexDisplay(31) + "     " + getVertexDisplay(33) + "     " + getVertexDisplay(36);
        out += "\n " + displayEdge(33,"\\") + "   " + displayEdge(32,"/") + " " + displayEdge(38,"\\") + "   " + displayEdge(37,"/") + " " + displayEdge(41, "\\") + "   " + displayEdge(40,"/") + " " + displayEdge(44, "\\") + "   " + displayEdge(43,"/") + " " + displayEdge(48, "\\") + "   " + displayEdge(47,"/");
        out += "\n  " + displayEdge(33,"\\") + " " + displayEdge(32,"/") + "   " + displayEdge(38,"\\") + " " + displayEdge(37,"/") + "   " + displayEdge(41, "\\") + " " + displayEdge(40,"/") + "   " + displayEdge(44, "\\") + " " + displayEdge(43,"/") + "   " + displayEdge(48, "\\") + " " + displayEdge(47,"/"); 
        
        out += "\n   " + getVertexDisplay(26) + "     " + getVertexDisplay(30) + "     " + getVertexDisplay(32) + "     " + getVertexDisplay(34) + "     " + getVertexDisplay(37);
        out += "\n   " + displayEdge(52,"|") + "     " + displayEdge(49,"|") + "     " + displayEdge(53,"|") + "     " + displayEdge(56,"|") + "     " + displayEdge(59,"|");
        out += "\n   " + displayEdge(52,"|") + "     " + displayEdge(49,"|") + "     " + displayEdge(53,"|") + "     " + displayEdge(56,"|") + "     " + displayEdge(59,"|");
        out += "\n   " + getVertexDisplay(40) + "     " + getVertexDisplay(38) + "     " + getVertexDisplay(41) + "     " + getVertexDisplay(43) + "     " + getVertexDisplay(45);
        
        out += "\n    " + displayEdge(51,"\\") + "   " + displayEdge(50,"/") + " " + displayEdge(55,"\\") + "   " + displayEdge(54,"/") + " " + displayEdge(58, "\\") + "   " + displayEdge(57,"/") + " " + displayEdge(61, "\\") + "   " + displayEdge(60,"/");
        out += "\n     " + displayEdge(51,"\\") + " " + displayEdge(50,"/") + "   " + displayEdge(55,"\\") + " " + displayEdge(54,"/") + "   " + displayEdge(58, "\\") + " " + displayEdge(57,"/") + "   " + displayEdge(61, "\\") + " " + displayEdge(60,"/");
        out += "\n      " + getVertexDisplay(39) + "     " + getVertexDisplay(42) + "     " + getVertexDisplay(44) + "     " + getVertexDisplay(46);
        out += "\n      " + displayEdge(65,"|") + "     " + displayEdge(62,"|") + "     " + displayEdge(66,"|") + "     " + displayEdge(69,"|");
        out += "\n      " + displayEdge(65,"|") + "     " + displayEdge(62,"|") + "     " + displayEdge(66,"|") + "     " + displayEdge(69,"|");
        out += "\n      " + getVertexDisplay(49) + "     " + getVertexDisplay(47) + "     " + getVertexDisplay(50) + "     " + getVertexDisplay(52);
        out += "\n       " + displayEdge(64,"\\") + "   " + displayEdge(63,"/") + " " + displayEdge(68,"\\") + "   " + displayEdge(67,"/") + " " + displayEdge(71, "\\") + "   " + displayEdge(70,"/");
        out += "\n        " + displayEdge(64,"\\") + " " + displayEdge(63,"/") + "   " + displayEdge(68,"\\") + " " + displayEdge(67,"/") + "   " + displayEdge(71, "\\") + " " + displayEdge(70,"/");
        out += "\n         " + getVertexDisplay(48) + "     " + getVertexDisplay(51) + "     " + getVertexDisplay(53);
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

}
