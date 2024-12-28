package catan.board;

import catan.enums.*;
import catan.utils.*;

class Tile {
    private Resource resource;
    private Odds odds;
    private int[] vertices;
    private int[] edges;
    private int id; 
    private boolean isRobbed;
    public Tile(Resource resource, Odds odds, int id) {
        this.resource = resource;
        this.odds = odds;
        this.vertices = new int[6];
        this.edges = new int[6];
        this.id = id;
        this.isRobbed = false;
    }
    public Odds getOdds() {
        return this.odds;
    }
    public Resource getResource() {
        return this.resource;
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