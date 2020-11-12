package controllers;

import com.google.gson.Gson;
import io.javalin.Javalin;
import java.io.IOException;
import java.sql.Connection;
import java.util.Queue;
import models.GameBoard;
import models.Message;
import models.Move;
import models.Player;
import org.eclipse.jetty.websocket.api.Session;
import utils.DatabaseJDBC;



public class PlayGame {

  private static final int PORT_NUMBER = 8080;

  private static Javalin app;

  /** Main method of the application.
   * @param args Command line arguments
   */

  public static void main(final String[] args) {
    DatabaseJDBC jbdc = new DatabaseJDBC();
    Connection con = jbdc.createConnection();
    boolean gameStarted = false;
    int turn = 0;
    char[][] boardState = new char[3][3];
    int winner = 0;
    boolean isDraw = false;
    final GameBoard board = new GameBoard(null, null, gameStarted, turn, 
        boardState, winner, isDraw);
    Gson gson = new Gson();
    sendGameBoardToAllPlayers(gson.toJson(board));
    
    
    jbdc.createMoveTable(con, "ASE_I3_MOVE");
    jbdc.createBoardTable(con, "ASE_I3_BOARD");
    jbdc.createPlayerTable(con, "ASE_I3_PLAYER");
    
    app = Javalin.create(config -> {
      config.addStaticFiles("/public");
    }).start(PORT_NUMBER);

    // Test Echo Server
    app.post("/echo", ctx -> {
      ctx.result(ctx.body());
    });
    
    
    // New Game
    app.get("/newgame", ctx -> {
      ctx.redirect("/tictactoe.html");
      boolean moveTableCleared = jbdc.cleanTable(con, "ASE_I3_MOVE");
      boolean boardTableCleared = jbdc.cleanTable(con, "ASE_I3_BOARD");
      boolean playerTableCleared = jbdc.cleanTable(con, "ASE_I3_PLAYER");
    });
    
    // Start Game
    app.post("/startgame", ctx -> {
      char type = jbdc.selectPlayerData(con, "TYPE", "WHERE PLAYER_ID = 1");
      Player p1 = new Player(type, 1);
      if (type == '\u0000') {
        type = ctx.body().charAt(5);
        p1.setType(type);
        boolean playerTableAdded = jbdc.addPlayerData(con, p1);
      }
      board.setP1(p1);
      board.setP2(null);
      board.setGameStarted(false);
      board.setTurn(1);
      board.setBoardState(new char[3][3]);
      board.setWinner(0);
      board.setDraw(false);
      ctx.result(gson.toJson(board));
      boolean boardTableAdded = jbdc.addBoardData(con, board);
    });
    
    // Join Game
    app.get("/joingame", ctx -> {
      ctx.redirect("/tictactoe.html?p=2");
      char type1 = jbdc.selectPlayerData(con, "TYPE", "WHERE PLAYER_ID = 1");
      char type2 = jbdc.selectPlayerData(con, "TYPE", "WHERE PLAYER_ID = 2");
      Player p2 = new Player(type2, 2);
      if (type2 == '\u0000') {
        type2 = 'X';
        if (type1 == 'X') {
          type2 = 'O';
        }
        p2.setType(type2);
        boolean playerTableAdded = jbdc.addPlayerData(con, p2);
      } 
      board.setGameStarted(true);
      board.setP2(p2);
      boolean boardTableAdded = jbdc.addBoardData(con, board);
      sendGameBoardToAllPlayers(gson.toJson(board));
    });
    
    // Move
    app.post("/move/:playerId", ctx -> {
      // get current player's info
      int playerId = 0;
      int currentTurn = 0;
      int currentWinner = 0;
      char type1;
      char type2;
      Player currentPlayer = board.getP1();  
      
      if (jbdc.selectMoveData(con, "PLAYER_ID", "") != 0 
          && jbdc.arrayToString(board.getBoardState()).equals("NNNNNNNNN")) {
        playerId = jbdc.selectMoveData(con, "PLAYER_ID", "");
        type1 = jbdc.selectPlayerData(con, "TYPE", "WHERE PLAYER_ID = 1");
        type2 = jbdc.selectPlayerData(con, "TYPE", "WHERE PLAYER_ID = 2");
        Player p1 = new Player(type1, 1);
        Player p2 = new Player(type2, 2);
        board.setP1(p1);
        board.setP2(p2);
        if (playerId == 1) {
          playerId = 2;
          currentPlayer = board.getP2();
        } else {
          playerId = 1;
          currentPlayer = board.getP1();
        }
        currentTurn = 2 - ((jbdc.selectBoardIntData(con, "TURN", "") + 1) % 2);
        board.setTurn(currentTurn);
        currentWinner = jbdc.selectBoardIntData(con, "WINNER", "");
        board.setWinner(currentWinner);
        boolean currentIsDraw = jbdc.selectBoardBooleanData(con, "ISDRAW", "");
        board.setDraw(currentIsDraw);
        boolean currentIsGameStarted = jbdc.selectBoardBooleanData(con, "GETSTARTED", "");
        board.setGameStarted(currentIsGameStarted);
        
      } else {
        playerId = (int) ctx.pathParam("playerId").charAt(0) - (int) ('0');
        if (playerId == 2) {
          currentPlayer = board.getP2();
        }
        currentTurn = board.getTurn();
        currentWinner = board.getWinner();
      }
      
      // get coordinate of moving
      int coordinateX = (int) ctx.body().charAt(2) - (int) ('0');
      int coordinateY = (int) ctx.body().charAt(6) - (int) ('0');

      char[][] currentBoardState;
      if (jbdc.selectBoardData(con, "BOARDSTATE", "") == "null") {
        currentBoardState = board.getBoardState();
      } else {
        currentBoardState = jbdc.stringToArray(jbdc.selectBoardData(con, "BOARDSTATE", ""));
        board.setBoardState(currentBoardState);
      }
      
      Message message = new Message(false, 0, "" + board.getWinner() + board.isDraw());
      // run the following if the game is not ended
      if (board.isGameStarted() && currentWinner == 0 && board.isDraw() == false) {
        if (currentBoardState[coordinateX][coordinateY] == '\u0000' 
            && playerId == 2 - (currentTurn % 2)) {
          message.setMoveValidity(true);
          message.setMessage("This move is valid.");
          message.setCode(100);
          
          
          Move move = new Move(currentPlayer, coordinateX, coordinateY);
          boolean moveTableAdded = jbdc.addMoveData(con, move);
          char[][] nextBoardState = move.conduct(board);
          board.setBoardState(nextBoardState);    
          
          currentWinner  = board.checkWinner(currentTurn);
          if (currentWinner != 0) {
            board.updateWinner(currentWinner);
          }

          boolean boardTableAdded = jbdc.addBoardData(con, board); 
          currentTurn = 2 - (currentTurn + 1) % 2;
          board.setTurn(currentTurn);
          sendGameBoardToAllPlayers(gson.toJson(board));
          
        } else if (playerId != 2 - (currentTurn % 2)) {
          message.setMessage("Please wait for Player" + (2 - (currentTurn % 2)));
        } else {
          message.setMessage("Invalid move. Please click again.");
        }
        
      }

      ctx.result(gson.toJson(message));
    });

    // Web sockets - DO NOT DELETE or CHANGE
    app.ws("/gameboard", new UiWebSocket());
  }
 
  /** Send message to all players.
   * @param gameBoardJson Gameboard JSON
   * @throws IOException Websocket message send IO Exception
   */
  private static void sendGameBoardToAllPlayers(final String gameBoardJson) {
    Queue<Session> sessions = UiWebSocket.getSessions();
    for (Session sessionPlayer : sessions) {
      try {
        sessionPlayer.getRemote().sendString(gameBoardJson);
      } catch (IOException e) {
        // Add logger here
      }
    }
  }

  public static void stop() {
    app.stop();
  }
}
