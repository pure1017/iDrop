package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.Gson;
import controllers.PlayGame;
import kong.unirest.GetRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import models.GameBoard;
import models.Message;
import models.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;


@TestMethodOrder(OrderAnnotation.class) 
public class GameTest {
  
  /**
  * Runs only once before the testing starts.
  */
  @BeforeAll
  public static void init() {
    // Start Server
    PlayGame.main(null);
    System.out.println("Before All");
  }
    
  /**
  * This method starts a new game before every test run. It will run every time before a test.
  */
  @BeforeEach
  public void startNewGame() {
    // Test if server is running. You need to have an end point 
    // If you do not wish to have this end point, it is okay to not have anything in this method.
    HttpResponse<?> response = Unirest.get("http://localhost:8080/").asString();
    int restStatus = response.getStatus();

    System.out.println("Before Each");
  }
    
  /**
  * This is a test case to evaluate the newgame endpoint.
  */
  @Test
  @Order(1)
  public void newGameTest() {
    System.out.println("------Test New Game Start------");
    // Create HTTP request and get response
    HttpResponse<?> response = Unirest.get("http://localhost:8080/newgame").asString();
    int restStatus = response.getStatus();
    System.out.println(restStatus);
    // Check assert statement (New Game has started)
    assertEquals(200, restStatus);
    System.out.println("------Test New Game End------");
  }
    
  /**
  * This is a test case to evaluate the startgame endpoint.
  */
  @Test
  @Order(2)
  public void startGameTest() {
    System.out.println("------Test Start Game Start------");
        
    // Create a POST request to startgame endpoint and get the body
    // Remember to use asString() only once for an endpoint call. Every time you call asString(), 
    // a new request will be sent to the endpoint. Call it once and then use the data in the object.
    HttpResponse<?> response = Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    String responseBody = (String) response.getBody();
    
    // --------------------------- JSONObject Parsing ----------------------------------
    
    System.out.println("Start Game Response: " + responseBody);
    
    // Parse the response to JSON object
    JSONObject jsonObject = new JSONObject(responseBody);

    // Check if game started after player 1 joins: Game should not start at this point
    assertEquals(false, jsonObject.get("gameStarted"));
    
    // ---------------------------- GSON Parsing -------------------------
    
    // GSON use to parse data to object
    Gson gson = new Gson();
    GameBoard gameBoard = gson.fromJson(jsonObject.toString(), GameBoard.class);
    Player player1 = gameBoard.getP1();
        
    // Check if player type is correct
    assertEquals('X', player1.getType());
    
    System.out.println("------Test Start Game End------");
  }
  
  /**
   * This is a test case to evaluate the joingame endpoint.
   */
  @Test
  @Order(3)
  public void joinGameTest() {
    System.out.println("------Test Join Game Start------");
    
    HttpResponse<?> response = Unirest.get("http://localhost:8080/joingame").asString();
    int restStatus = response.getStatus();
    System.out.println(restStatus);
    
    assertEquals(200, restStatus);
    
    System.out.println("------Test Join Game End------");
  }
  
  /**
   * This is a test case to evaluate the move endpoint. It makes sure that player1 makes the
   * first move. And other player cannot move to the place that's already been clicked.
   */
  @Test
  @Order(4)
  public void moveTest() {
    System.out.println("------Test Move Start------");
    
    HttpResponse<?> response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    String responseBody = (String) response.getBody();
    
    // Parse the response to JSON object
    JSONObject jsonObject = new JSONObject(responseBody);
    System.out.println(jsonObject.get("code"));
    assertEquals(100, jsonObject.get("code"));
    
    HttpResponse<?> response2 = Unirest.post("http://localhost:8080/move/2").body("x=0&y=0").asString();
    responseBody = (String) response2.getBody();
    jsonObject = new JSONObject(responseBody);
    System.out.println(jsonObject.get("code"));
    assertEquals(0, jsonObject.get("code"));
    
    
    System.out.println("------Test Move End------");
  }
  
  /**
   * This is a test case for situation 1:
   * A player cannot make a move until both players have joined the game.
   */
  @Test
  @Order(5)
  public void testCase1() {
    System.out.println("------Test testCase1 Start------");
    
    // After newgame, player1 still cannot make a move
    Unirest.get("http://localhost:8080/newgame").asString();
    HttpResponse<?> response1 = Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    String responseBody = (String) response1.getBody();  
    JSONObject jsonObject = new JSONObject(responseBody);
    System.out.println("Start Game Response: " + responseBody);
    
    // Check if game started after player 1 joins: Game should not start at this point
    assertEquals(0, jsonObject.get("code"));
    
    // After startgame but before player2 join, player1 still cannot make a move
    HttpResponse<?> response2 = Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    HttpResponse<?> response3 = Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    responseBody = (String) response3.getBody();  
    jsonObject = new JSONObject(responseBody);
    System.out.println("Start Game Response: " + responseBody);
    
    // Check if game started after player 1 joins: Game should not start at this point
    assertEquals(0, jsonObject.get("code"));
    
    System.out.println("------Test testCase1 End------");
  }
  
  /**
   * This is a test case for situation 2:
   * After game has started Player 1 always makes the first move.
   */
  @Test
  @Order(6)
  public void testCase2() {
    System.out.println("------Test testCase2 Start------");
    
    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    
    HttpResponse<?> response = Unirest.post("http://localhost:8080/move/2").body("x=0&y=0").asString();
    String responseBody = (String) response.getBody();  
    JSONObject jsonObject = new JSONObject(responseBody);
    
    // Check whether player2 can make a move at this point: he cannot.
    assertEquals(false, jsonObject.get("moveValidity"));
    
    response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    responseBody = (String) response.getBody();  
    jsonObject = new JSONObject(responseBody);
    
    // Check whether player1 can make a move at this point: he can.
    assertEquals(true, jsonObject.get("moveValidity"));
    
    System.out.println("------Test testCase2 End------");
  }
  
  /**
   * This is a test case for situation 3:
   * A player cannot make two moves in their turn.
   */
  @Test
  @Order(7)
  public void testCase3() {
    System.out.println("------Test testCase3 Start------");
    
    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    HttpResponse<?> response = Unirest.post("http://localhost:8080/move/1").body("x=1&y=0").asString();
    String responseBody = (String) response.getBody();  
    JSONObject jsonObject = new JSONObject(responseBody);
    
    // Check whether player1 can make two moves in his turn: he cannot.
    assertEquals(false, jsonObject.get("moveValidity"));
    
    System.out.println("------Test testCase3 End------");
  }
  
  /**
   * This is a test case for situation 4:
   * A player should be able to win a game.
   * X X X 
   * O \ \
   * O \ \
   */
  @Test
  @Order(8)
  public void testCase4() {
    System.out.println("------Test testCase4 Start------");
    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=1&y=0").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=1").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=2&y=0").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=2").asString();
    
    HttpResponse<?> response = Unirest.post("http://localhost:8080/move/2").body("x=2&y=2").asString();
    String responseBody = (String) response.getBody();  
    JSONObject jsonObject = new JSONObject(responseBody);
    System.out.println("Start Game Response: " + responseBody);
    
    // Check whether player2 can move after someone win, he cannot, which indicates someone wins 
    // and the game ends. And the message shows the winner.
    assertEquals(false, jsonObject.get("moveValidity"));
    assertEquals("1false", jsonObject.get("message"));
    
    System.out.println("------Test testCase4 End------");
  }
  
  /**
   * This is a test case for situation 5:
   * A game should be a draw if all the positions are exhausted and no one has won.
   * X O X
   * X O X
   * O X O
   */
  @Test
  @Order(9)
  public void testCase5() {
    System.out.println("------Test testCase5 Start------");
    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=0&y=1").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=2").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=1&y=1").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=1&y=0").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=2&y=0").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=1&y=2").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=2&y=2").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=2&y=1").asString();
    
    HttpResponse<?> response = Unirest.post("http://localhost:8080/move/2").body("x=2&y=2").asString();
    String responseBody = (String) response.getBody();  
    JSONObject jsonObject = new JSONObject(responseBody);
    System.out.println("Start Game Response: " + responseBody);
    
    // Check whether player2 can move after the game is draw, he cannot, and the message 
    // shows 0true, which indicates that the game is draw.
    assertEquals(false, jsonObject.get("moveValidity"));
    assertEquals("0true", jsonObject.get("message"));
    System.out.println("------Test testCase5 End------");
  }
  
  /**
   * This is for the test when game crash.
   */
  @Test
  @Order(10)
  public void testCrash1() {
    System.out.println("------Test testCrash1 Start------");
    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    PlayGame.stop();
    System.out.println("Game Crash");
    PlayGame.main(null);
    System.out.println("Game Reboot");
    Unirest.get("http://localhost:8080/").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=0&y=1").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=2").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=1&y=1").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=1&y=0").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=2&y=0").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=1&y=2").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=2&y=2").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=2&y=1").asString();
    
    HttpResponse<?> response = Unirest.post("http://localhost:8080/move/2").body("x=2&y=2").asString();
    String responseBody = (String) response.getBody();  
    JSONObject jsonObject = new JSONObject(responseBody);
    System.out.println("Start Game Response: " + responseBody);
    
    // Check whether player2 can move after the game is draw, he cannot, and the message 
    // shows 0true, which indicates that the game is draw.
    assertEquals(false, jsonObject.get("moveValidity"));
    assertEquals("0true", jsonObject.get("message"));
    System.out.println("------Test testCrash1 End------");
  }
  
  /**
   * This is for the test when game crash.
   */
  @Test
  @Order(11)
  public void testCrash2() {
    System.out.println("------Test testCrash2 Start------");
    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=0&y=1").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=2").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=1&y=1").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=1&y=0").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=2&y=0").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=1&y=2").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=2&y=2").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=2&y=1").asString();
    PlayGame.stop();
    System.out.println("Game Crash");
    PlayGame.main(null);
    System.out.println("Game Reboot");
    Unirest.get("http://localhost:8080/").asString();
    
    HttpResponse<?> response = Unirest.post("http://localhost:8080/move/2").body("x=2&y=2").asString();
    String responseBody = (String) response.getBody();  
    JSONObject jsonObject = new JSONObject(responseBody);
    System.out.println("Start Game Response: " + responseBody);
    
    // Check whether player2 can move after the game is draw, he cannot, and the message 
    // shows 0true, which indicates that the game is draw.
    assertEquals(false, jsonObject.get("moveValidity"));
    assertEquals("0true", jsonObject.get("message"));
    System.out.println("------Test testCrash2 End------");
  }
  
  /**
  * This will run every time after a test has finished.
  */
  @AfterEach
  public void finishGame() {
    System.out.println("After Each");
  }
    
  /**
   * This method runs only once after all the test cases have been executed.
   */
  @AfterAll
  public static void close() {
    // Stop Server
    PlayGame.stop();
    System.out.println("After All");
  }
}