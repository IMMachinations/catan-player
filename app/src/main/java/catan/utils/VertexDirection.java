package catan.utils;

public class VertexDirection {
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