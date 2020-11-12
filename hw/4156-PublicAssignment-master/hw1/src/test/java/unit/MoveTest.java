package unit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import models.GameBoard;
import models.Move;
import models.Player;
import org.junit.jupiter.api.Test;


public class MoveTest {
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
   * Test conduct function.
   */
  @Test
  public void testConduct1() {
    Move move = new Move(player1, 1, 1);
    char[][] boardState1 = {
        {'X', 'O', 'X'}, 
        {'\u0000', '\u0000', '\u0000'}, 
        {'\u0000', 'O', '\u0000'}};
    gameboard.setBoardState(boardState1);
    char[][] nextBoardState = move.conduct(gameboard);

    char[][] trueNextBoardState = {
        {'X', 'O', 'X'}, 
        {'\u0000', 'X', '\u0000'}, 
        {'\u0000', 'O', '\u0000'}};

    assertArrayEquals(trueNextBoardState, nextBoardState);
  }
  
  /*
   * Test conduct function.
   */
  @Test
  public void testConduct2() {
    Move move = new Move(player2, 2, 0);
    char[][] boardState2 = {
        {'X', 'O', 'X'}, 
        {'\u0000', 'X', '\u0000'}, 
        {'\u0000', 'O', '\u0000'}};
    gameboard.setBoardState(boardState2);
    char[][] nextBoardState = move.conduct(gameboard);

    char[][] trueNextBoardState = {
        {'X', 'O', 'X'}, 
        {'\u0000', 'X', '\u0000'}, 
        {'O', 'O', '\u0000'}};

    assertArrayEquals(trueNextBoardState, nextBoardState);
  }
}