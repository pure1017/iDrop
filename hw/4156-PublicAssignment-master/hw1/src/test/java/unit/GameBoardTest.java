package unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import models.GameBoard;
import models.Player;
import org.junit.jupiter.api.Test;

public class GameBoardTest {
  Player player1 = new Player('X', 1);
  Player player2 = new Player('O', 2);
  boolean gameStarted = false;
  int turn = 0;
  char[][] boardState = new char[3][3];
  int winner = 0;
  boolean isDraw = false;
  GameBoard gameboard = new GameBoard(player1, player2, gameStarted, turn, 
      boardState, winner, isDraw); 
  
  /*
   * This is a situation where the game is not end, no winner.
   */
  @Test
  public void testCheckWinner1() {
    int currentTurn = 1;
    char[][] boardState1 = {{'X', 'O', 'X'}, {'\u0000', 'O', '\u0000'}, {'\u0000', 'X', '\u0000'}};
    gameboard.setBoardState(boardState1);
    int currentWinner = gameboard.checkWinner(currentTurn);
    
    assertEquals(0, currentWinner);
  }
  
  /*
   * This is a test case to test the judge of winner. Winner is player1. Winning case is horizontal.
   */
  @Test
  public void testCheckWinner2() {
    int currentTurn = 1;
    char[][] boardState2 = {{'X', 'X', 'X'}, {'O', '\u0000', '\u0000'}, {'O', '\u0000', '\u0000'}};
    gameboard.setBoardState(boardState2);
    int currentWinner = gameboard.checkWinner(currentTurn);
    
    assertEquals(1, currentWinner);
  }
  
  /*
   * This is a test case to test the judge of winner. Winner is player2. Winning case is diagonal.
   */
  @Test
  public void testCheckWinner3() {
    int currentTurn = 2;
    char[][] boardState3 = {{'X', 'X', 'O'}, {'X', 'O', '\u0000'}, {'O', '\u0000', '\u0000'}};
    gameboard.setBoardState(boardState3);
    int currentWinner = gameboard.checkWinner(currentTurn);
    
    assertEquals(2, currentWinner);
  }
  
  /*
   * This is a test case to test the judge of winner. It is a draw here. No winner.
   */
  @Test
  public void testCheckWinner4() {
    int currentTurn = 1;
    char[][] boardState4 = {{'X', 'X', 'O'}, {'O', 'O', 'X'}, {'X', 'O', 'X'}};
    gameboard.setBoardState(boardState4);
    int currentWinner = gameboard.checkWinner(currentTurn);
    
    assertEquals(3, currentWinner);
  }
  
  /*
   * This is a test case to test the judge of winner. 
   * The winner is player1. Winning case is diagonal.
   */
  @Test
  public void testCheckWinner5() {
    int currentTurn = 1;
    char[][] boardState5 = {{'X', 'O', '\u0000'}, {'\u0000', 'X', '\u0000'}, {'O', '\u0000', 'X'}};
    gameboard.setBoardState(boardState5);
    int currentWinner = gameboard.checkWinner(currentTurn);
    
    assertEquals(1, currentWinner);
  }
  
  /*
   * This is a test case to test the judge of winner. 
   * The winner is player1. Winning case is vertical.
   */
  @Test
  public void testCheckWinner6() {
    int currentTurn = 1;
    char[][] boardState5 = {{'X', 'O', '\u0000'}, {'X', '\u0000', '\u0000'}, {'X', '\u0000', 'O'}};
    gameboard.setBoardState(boardState5);
    int currentWinner = gameboard.checkWinner(currentTurn);
    
    assertEquals(1, currentWinner);
  }
  
  /*
   * This is a test case to test the updateWinner function. Set the winner to player1.
   */
  @Test
  public void testUpdateWinner1() {
    gameboard.updateWinner(1);

    assertEquals(1, gameboard.getWinner()); 
  }
  
  /*
   * This is a test case to test the updateWinner function. Set the winner to player2.
   */
  @Test
  public void testUpdateWinner2() {
    gameboard.updateWinner(2);

    assertEquals(2, gameboard.getWinner()); 
  }
  
  /*
   * This is a test case to test the updateWinner function. 
   * Since 3 represents a draw. It set isDraw instead.
   */
  @Test
  public void testUpdateWinner3() {
    gameboard.updateWinner(3);

    assertEquals(true, gameboard.isDraw()); 
  }
}