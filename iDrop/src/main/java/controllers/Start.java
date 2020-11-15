package controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.javalin.Javalin;
import io.javalin.http.Context;
import login.GoogleApiLogin;

import java.io.IOException;
import java.util.Queue;
import org.eclipse.jetty.websocket.api.Session;

class Start {

  private static final int PORT_NUMBER = 8080;

  private static Javalin app;

  /** Main method of the application.
   * @param args Command line arguments
   */

  public static void main(final String[] args) {

    app = Javalin.create(config -> {
      config.addStaticFiles("/public");
    }).start(PORT_NUMBER);

    // Test Echo Server
    app.post("/echo", ctx -> {
      ctx.result(ctx.body());
    });
    
    // New Game
    app.get("/", ctx -> {
      ctx.redirect("/start.html");
    });
    
    //Sign in with Google
    app.post("/storeauthcode", ctx -> {
      //System.out.println(ctx.queryParam("code"));
      String authCode = ctx.queryParam("code");
      System.out.println(authCode);
      String userId = GoogleApiLogin.login(authCode);
      System.out.println(userId);
      ctx.result("code received");
    });
    
    //Host Group
    app.post("/hostGroup", ctx -> {
      System.out.println(ctx.queryParam("bookName"));
      ctx.result("sent");
    });
    
    //Join Group
    app.post("/joinGroup", ctx -> {
      System.out.println(ctx.queryParam("bookName"));
      ctx.result("sent");
    });
    
    //Search Book
    app.post("/search", ctx -> {
      System.out.println(ctx.queryParam("bookName"));
      ctx.result("Petter");
    });
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
