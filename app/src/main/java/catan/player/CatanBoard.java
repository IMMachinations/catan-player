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
    P4Road,
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
    P4Settlement,
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
    // ANSI escape codes for colors
      // Using 256-color mode for brown
    
    private VertexState[] vertices;
    private EdgeState[] edges;
    private Tile[] tiles;
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
        out += "\n" + getVertexDisplay(27) + "   " + getVertexDisplay(25) + "   " + getVertexDisplay(29) + "   " + getVertexDisplay(31) + "   " + getVertexDisplay(33) + "   " + getVertexDisplay(36);
        out += "\n " + displayEdge(33,"\\") + " " + displayEdge(32,"/") + " " + displayEdge(38,"\\") + " " + displayEdge(37,"/") + " " + displayEdge(41, "\\") + " " + displayEdge(40,"/") + " " + displayEdge(44, "\\") + " " + displayEdge(43,"/") + " " + displayEdge(48, "\\") + " " + displayEdge(47,"/"); 
        out += "\n  " + getVertexDisplay(26) + "   " + getVertexDisplay(30) + "   " + getVertexDisplay(32) + "   " + getVertexDisplay(34) + "   " + getVertexDisplay(37);
        out += "\n  " + displayEdge(52,"|") + "   " + displayEdge(49,"|") + "   " + displayEdge(53,"|") + "   " + displayEdge(56,"|") + "   " + displayEdge(59,"|");
        out += "\n  " + getVertexDisplay(40) + "   " + getVertexDisplay(38) + "   " + getVertexDisplay(41) + "   " + getVertexDisplay(43) + "   " + getVertexDisplay(45);
        out += "\n   " + displayEdge(51,"\\") + " " + displayEdge(50,"/") + " " + displayEdge(55,"\\") + " " + displayEdge(54,"/") + " " + displayEdge(58, "\\") + " " + displayEdge(57,"/") + " " + displayEdge(61, "\\") + " " + displayEdge(60,"/");
        out += "\n    " + getVertexDisplay(39) + "   " + getVertexDisplay(42) + "   " + getVertexDisplay(44) + "   " + getVertexDisplay(46);
        out += "\n    " + displayEdge(65,"|") + "   " + displayEdge(62,"|") + "   " + displayEdge(66,"|") + "   " + displayEdge(69,"|");
        out += "\n    " + getVertexDisplay(49) + "   " + getVertexDisplay(47) + "   " + getVertexDisplay(50) + "   " + getVertexDisplay(52);
        out += "\n     " + displayEdge(64,"\\") + " " + displayEdge(63,"/") + " " + displayEdge(68,"\\") + " " + displayEdge(67,"/") + " " + displayEdge(71, "\\") + " " + displayEdge(70,"/");
        out += "\n      " + getVertexDisplay(48) + "   " + getVertexDisplay(51) + "   " + getVertexDisplay(53);
        try {
            FileWriter writer = new FileWriter("board.txt");
            writer.write(out);
            writer.close();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
