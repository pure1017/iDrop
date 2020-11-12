package unit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import models.GameBoard;
import models.Move;
import models.Player;
import org.junit.jupiter.api.Test;
import utils.DatabaseJDBC;

public class DataBaseTest {
  DatabaseJDBC jbdc = new DatabaseJDBC();
  Connection con = jbdc.createConnection();
  
  /*
   * Test createMoveTable function
   */
  @Test
  public void testCreateMoveTable() {
    boolean moveTableCreated = jbdc.createMoveTable(con, "ASE_I3_MOVE");
    assertEquals(true, moveTableCreated);
  }
  
  /*
   * Test createBoardTable function
   */
  @Test
  public void testCreateBoardTable() {
    boolean boardTableCreated = jbdc.createMoveTable(con, "ASE_I3_BOARD");
    assertEquals(true, boardTableCreated);
  }

  /*
   * Test createPlayerTable function
   */
  @Test
  public void testCreatePlayerTable() {
    boolean playerTableCreated = jbdc.createMoveTable(con, "ASE_I3_PLAYER");
    assertEquals(true, playerTableCreated);
  }
  
  /*
   * Test cleanTable function
   */
  @Test
  public void testCleanTable() {
    boolean moveTableCleared = jbdc.cleanTable(con, "ASE_I3_MOVE");
    assertEquals(true, moveTableCleared);
    boolean boardTableCleared = jbdc.cleanTable(con, "ASE_I3_BOARD");
    assertEquals(true, boardTableCleared);
    boolean playerTableCleared = jbdc.cleanTable(con, "ASE_I3_PLAYER");
    assertEquals(true, playerTableCleared);
    boolean testTableCleaned = jbdc.cleanTable(con, "ASE_I3_TEST");
    assertEquals(false, testTableCleaned);
  }
  
  /*
   * Test addMoveData function
   */
  @Test
  public void testAddMoveData() {
    Move move = new Move(new Player('X', 2), 0, 0);
    boolean tupleAdded = jbdc.addMoveData(con, move);
    assertEquals(true, tupleAdded);
  }
  
  /*
   * Test arrayToString function
   */
  @Test
  public void testArrayToString() {
    char[][] boardState = {{'\u0000', 'X', 'O'},
                           {'X', 'O', '\u0000'},
                           {'\u0000', 'X', '\u0000'}};
    String result = jbdc.arrayToString(boardState);
    assertEquals("NXOXONNXN", result);
  }
  
  /*
   * Test stringToArray function
   */
  @Test
  public void testStringToArray() {
    String test = "NXOXONNXN";
    char[][] boardState = jbdc.stringToArray(test);
    char[][] expectedArray = {{'\u0000', 'X', 'O'},
                              {'X', 'O', '\u0000'},
                              {'\u0000', 'X', '\u0000'}};
    assertArrayEquals(expectedArray, boardState);
  }
  
  /*
   * Test addBoardData function
   */
  @Test
  public void testAddBoardData() {
    boolean gameStarted = true;
    int turn = 2;
    char[][] boardState = {{'\u0000', 'X', 'O'},
                           {'X', 'O', '\u0000'},
                           {'\u0000', 'X', '\u0000'}};
    int winner = 0;
    boolean isDraw = false;
    Player p1 = new Player('X', 1);
    Player p2 = new Player('O', 2);
    GameBoard board = new GameBoard(p1, p2, gameStarted, turn, boardState, winner, isDraw);
    boolean tupleAdded = jbdc.addBoardData(con, board);
    assertEquals(true, tupleAdded);
  }
  
  /*
   * Test addPlayerData function
   */
  @Test
  public void testAddPlayerData() {
    Player p1 = new Player('X', 1);
    boolean tupleAddedP1 = jbdc.addPlayerData(con, p1);
    assertEquals(true, tupleAddedP1);
    Player p2 = new Player('O', 2);
    boolean tupleAddedP2 = jbdc.addPlayerData(con, p2);
    assertEquals(true, tupleAddedP2);
  }
  
  /*
   * Test selectPlayerData function
   */
  @Test
  public void testSelectPlayerData() {
    char typeP1 = jbdc.selectPlayerData(con, "TYPE", "WHERE PLAYER_ID = 1");
    char typeP2 = jbdc.selectPlayerData(con, "TYPE", "WHERE PLAYER_ID = 2");
    char typeP3 = jbdc.selectPlayerData(con, "TYPE", "WHERE PLAYER_ID = 3");
    assertEquals('X', typeP1);
    assertEquals('O', typeP2);
    assertEquals('\u0000', typeP3);
  }
  
  /*
   * Test selectBoardData function
   */
  @Test
  public void testSelectBoardData() {
    String board = jbdc.selectBoardData(con, "BOARDSTATE", "");
    assertEquals("NXOXONNXN", board);
    String board2 = jbdc.selectBoardData(con, "BOARDSTATE", "WHERE TURN = 3");
    assertEquals("null", board2);
  }
  
  /*
   * Test selectBoardIntData function
   */
  @Test
  public void testSelectBoardIntData() {
    int turn = jbdc.selectBoardIntData(con, "TURN", "");
    assertEquals(2, turn);
    int board2 = jbdc.selectBoardIntData(con, "TURN", "WHERE WINNER = 1");
    assertEquals(0, board2);
  }
  
  /*
   * Test selectBoardBooleanData function
   */
  @Test
  public void testSelectBoardBooleanData() {
    boolean currentIsDraw = jbdc.selectBoardBooleanData(con, "ISDRAW", "");
    assertEquals(false, currentIsDraw);
    boolean currentIsDraw2 = jbdc.selectBoardBooleanData(con, "ISDRAW", "WHERE WINNER = 1");
    assertEquals(false, currentIsDraw2);
    boolean gameStarted = true;
    int turn = 2;
    char[][] boardState = {{'X', 'X', 'O'},
                           {'X', 'O', 'O'},
                           {'\u0000', 'X', '\u0000'}};
    int winner = 0;
    boolean isDraw = false;
    Player p1 = new Player('X', 1);
    Player p2 = new Player('O', 2);
    GameBoard board = new GameBoard(p1, p2, gameStarted, turn, boardState, winner, isDraw);
    jbdc.addBoardData(con, board);
    boolean currentIsGameStarted = jbdc.selectBoardBooleanData(con, "GETSTARTED", "");
    assertEquals(true, currentIsGameStarted);
  }
  
  /*
   * Test selectMoveData function
   */
  @Test
  public void testSelectMoveData() {
    Move move = new Move(new Player('X', 2), 0, 0);
    jbdc.addMoveData(con, move);
    int playerId = jbdc.selectMoveData(con, "PLAYER_ID", "");
    assertEquals(2, playerId);
    int playerId2 = jbdc.selectMoveData(con, "PLAYER_ID", "WHERE MOVE_X = 4");
    assertEquals(0, playerId2);
  }
}