package catan.board;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import catan.enums.EdgeState;
import catan.enums.Odds;
import catan.enums.Resource;
import catan.enums.VertexState;
import catan.player.CatanPlayer;
import catan.utils.Color;
import catan.utils.VertexAdjacent;

public class CatanBoard {
    
    private VertexState[] vertices;
    private EdgeState[] edges;
    private Tile[] tiles;
    private int[][] vertexAdjacentTiles;
    private int vertexIndex;
    private int edgeIndex;
    public CatanBoard() {
        Random rand = new Random();
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
            this.tiles[i] = new Tile(Resource.values()[rand.nextInt(6)], Odds.values()[rand.nextInt(10)], i);
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
            if(VertexAdjacent.vertexAdjacentVertices[vertex][i] == -1) {
                return true;
            }
            if(this.vertices[VertexAdjacent.vertexAdjacentVertices[vertex][i]] != VertexState.Empty) {
                return false;
            }        
        }
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
        out += "\n      " + displayEdge(4,"|") + " " + tiles[0].getOdds() + " " + displayEdge(1,"|") + " " + tiles[1].getOdds() + " " + displayEdge(7,"|") + " " + tiles[2].getOdds() + " " + displayEdge(12,"|");
        out += "\n      " + displayEdge(4,"|") + "  " + tiles[0].getResource() + "  " + displayEdge(1,"|") + "  " + tiles[1].getResource() + "  " + displayEdge(7,"|") + "  " + tiles[2].getResource() + "  " + displayEdge(12,"|");
        out += "\n      " + getVertexDisplay(4) + "     " + getVertexDisplay(2) + "     " + getVertexDisplay(8) + "     " + getVertexDisplay(12);
        out += "\n     " + displayEdge(20,"/") + " " + displayEdge(3,"\\") + "   " + displayEdge(2,"/") + " " + displayEdge(9,"\\") + "   " + displayEdge(8, "/") + " " + displayEdge(14,"\\") + "   " + displayEdge(13, "/") + " " + displayEdge(27,"\\");
        out += "\n    " + displayEdge(20,"/") + "   " + displayEdge(3,"\\") + " " + displayEdge(2,"/") + "   " + displayEdge(9,"\\") + " " + displayEdge(8, "/") + "   " + displayEdge(14,"\\") + " " + displayEdge(13, "/") + "   " + displayEdge(27,"\\");
        
        out += "\n   " + getVertexDisplay(17) + "     " + getVertexDisplay(3) + "     " + getVertexDisplay(9) + "     " + getVertexDisplay(13) + "     " + getVertexDisplay(22);
        out += "\n   " + displayEdge(19,"|") + " " + tiles[3].getOdds() + " " + displayEdge(16,"|") + " " + tiles[4].getOdds() + " " + displayEdge(21,"|") + " " + tiles[5].getOdds() + " " + displayEdge(24,"|") + " " + tiles[6].getOdds() + " " + displayEdge(28,"|");
        out += "\n   " + displayEdge(19,"|") + "  " + tiles[3].getResource() + "  " + displayEdge(16,"|") + "  " + tiles[4].getResource() + "  " + displayEdge(21,"|") + "  " + tiles[5].getResource() + "  " + displayEdge(24,"|") + "  " + tiles[6].getResource() + "  " + displayEdge(28,"|");
        
        out += "\n   " + getVertexDisplay(16) + "     " + getVertexDisplay(14) + "     " + getVertexDisplay(18) + "     " + getVertexDisplay(20) + "     " + getVertexDisplay(23);        
        out += "\n  " + displayEdge(35,"/") + " " + displayEdge(18,"\\") + "   " + displayEdge(17,"/") + " " + displayEdge(23,"\\") + "   " + displayEdge(22, "/") + " " + displayEdge(26,"\\") + "   " + displayEdge(25, "/") + " " + displayEdge(30,"\\") + "   " + displayEdge(29, "/") + " " + displayEdge(45,"\\");        
        out += "\n " + displayEdge(35,"/") + "   " + displayEdge(18,"\\") + " " + displayEdge(17,"/") + "   " + displayEdge(23,"\\") + " " + displayEdge(22, "/") + "   " + displayEdge(26,"\\") + " " + displayEdge(25, "/") + "   " + displayEdge(30,"\\") + " " + displayEdge(29, "/") + "   " + displayEdge(45,"\\");

        out += "\n" + getVertexDisplay(28) + "     " + getVertexDisplay(15) + "     " + getVertexDisplay(19) + "     " + getVertexDisplay(21) + "     " + getVertexDisplay(24) + "     " + getVertexDisplay(35);
        out += "\n" + displayEdge(34,"|") + " " + tiles[7].getOdds() + " " + displayEdge(31,"|") + " " + tiles[8].getOdds() + " " + displayEdge(36,"|") + " " + tiles[9].getOdds() + " " + displayEdge(39,"|") + " " + tiles[10].getOdds() + " " + displayEdge(42,"|") + " " + tiles[11].getOdds() + " " + displayEdge(46,"|");
        out += "\n" + displayEdge(34,"|") + "  " + tiles[7].getResource() + "  " + displayEdge(31,"|") + "  " + tiles[8].getResource() + "  " + displayEdge(36,"|") + "  " + tiles[9].getResource() + "  " + displayEdge(39,"|") + "  " + tiles[10].getResource() + "  " + displayEdge(42,"|") + "  " + tiles[11].getResource() + "  " + displayEdge(46,"|");
        out += "\n" + getVertexDisplay(27) + "     " + getVertexDisplay(25) + "     " + getVertexDisplay(29) + "     " + getVertexDisplay(31) + "     " + getVertexDisplay(33) + "     " + getVertexDisplay(36);
        out += "\n " + displayEdge(33,"\\") + "   " + displayEdge(32,"/") + " " + displayEdge(38,"\\") + "   " + displayEdge(37,"/") + " " + displayEdge(41, "\\") + "   " + displayEdge(40,"/") + " " + displayEdge(44, "\\") + "   " + displayEdge(43,"/") + " " + displayEdge(48, "\\") + "   " + displayEdge(47,"/");
        out += "\n  " + displayEdge(33,"\\") + " " + displayEdge(32,"/") + "   " + displayEdge(38,"\\") + " " + displayEdge(37,"/") + "   " + displayEdge(41, "\\") + " " + displayEdge(40,"/") + "   " + displayEdge(44, "\\") + " " + displayEdge(43,"/") + "   " + displayEdge(48, "\\") + " " + displayEdge(47,"/"); 
        
        out += "\n   " + getVertexDisplay(26) + "     " + getVertexDisplay(30) + "     " + getVertexDisplay(32) + "     " + getVertexDisplay(34) + "     " + getVertexDisplay(37);
        out += "\n   " + displayEdge(52,"|") + " " + tiles[12].getOdds() + " " + displayEdge(49,"|") + " " + tiles[13].getOdds() + " " + displayEdge(53,"|") + " " + tiles[14].getOdds() + " " + displayEdge(56,"|") + " " + tiles[15].getOdds() + " " + displayEdge(59,"|");
        out += "\n   " + displayEdge(52,"|") + "  " + tiles[12].getResource() + "  " + displayEdge(49,"|") + "  " + tiles[13].getResource() + "  " + displayEdge(53,"|") + "  " + tiles[14].getResource() + "  " + displayEdge(56,"|") + "  " + tiles[15].getResource() + "  " + displayEdge(59,"|");
        out += "\n   " + getVertexDisplay(40) + "     " + getVertexDisplay(38) + "     " + getVertexDisplay(41) + "     " + getVertexDisplay(43) + "     " + getVertexDisplay(45);
        
        out += "\n    " + displayEdge(51,"\\") + "   " + displayEdge(50,"/") + " " + displayEdge(55,"\\") + "   " + displayEdge(54,"/") + " " + displayEdge(58, "\\") + "   " + displayEdge(57,"/") + " " + displayEdge(61, "\\") + "   " + displayEdge(60,"/");
        out += "\n     " + displayEdge(51,"\\") + " " + displayEdge(50,"/") + "   " + displayEdge(55,"\\") + " " + displayEdge(54,"/") + "   " + displayEdge(58, "\\") + " " + displayEdge(57,"/") + "   " + displayEdge(61, "\\") + " " + displayEdge(60,"/");
        out += "\n      " + getVertexDisplay(39) + "     " + getVertexDisplay(42) + "     " + getVertexDisplay(44) + "     " + getVertexDisplay(46);
        out += "\n      " + displayEdge(65,"|") + " " + tiles[16].getOdds() + " " + displayEdge(62,"|") + " " + tiles[17].getOdds() + " " + displayEdge(66,"|") + " " + tiles[18].getOdds() + " " + displayEdge(69,"|");
        out += "\n      " + displayEdge(65,"|") + "  " + tiles[16].getResource() + "  " + displayEdge(62,"|") + "  " + tiles[17].getResource() + "  " + displayEdge(66,"|") + "  " + tiles[18].getResource() + "  " + displayEdge(69,"|");
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