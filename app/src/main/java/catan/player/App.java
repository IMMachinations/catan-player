/*
 * This source file was generated by the Gradle 'init' task
 */
package catan.player;


public class App {
    
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        CatanBoard board = new CatanBoard();
        board.populateBoard();
        board.printBoard();
        board.displayBoard();

    }
}
