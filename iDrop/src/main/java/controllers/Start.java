package controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import database.MysqlConnection;
import entity.Item;
import external.OpenLibraryApi;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import login.GoogleApiLogin;
import recommendation.BookRecommend;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    
    //Get favorite item
    app.post("/getfavorite", ctx -> {
      String userId = ctx.queryParam("userId");
      JSONArray arr = new JSONArray();
      
      MysqlConnection conn = new MysqlConnection();
      Set<Item> items  = conn.getFavoriteItems(userId);
      conn.close();
      for (Item item : items) {
        JSONObject obj = item.toJsonObject();
        try {
          obj.append("favorite", true);
        } catch (JSONException e) {
          e.printStackTrace();
        }
        arr.put(obj);
      }
      Gson gson = new Gson();
      ctx.result(gson.toJson(arr));
    });
    
    //Set favorite item
    app.post("/setfavorite", ctx -> {
      String userId = ctx.queryParam("userId");
      
      HttpServletRequest request = ctx.req;
      JSONObject input = RpcHelper.readJsonObject(request);
      
      JSONArray array = input.getJSONArray("favorite");
      List<String> itemIds = new ArrayList<>();
      for (int i = 0; i < array.length(); ++i) {
        itemIds.add(array.get(i).toString());
      }
      
      MysqlConnection conn = new MysqlConnection();
      conn.setFavoriteItems(userId, itemIds);
      conn.close();
      ctx.result("set favorite item successfully");
    });
    
    //Unset favorite item
    app.delete("/unsetfavorite", ctx -> {
      String userId = ctx.queryParam("userId");
        
      HttpServletRequest request = ctx.req;
      JSONObject input = RpcHelper.readJsonObject(request);
        
      JSONArray array = input.getJSONArray("favorite");
      List<String> itemIds = new ArrayList<>();
      for (int i = 0; i < array.length(); ++i) {
        itemIds.add(array.get(i).toString());
      }
        
      MysqlConnection conn = new MysqlConnection();
      conn.unsetFavoriteItems(userId, itemIds);
      conn.close();
      ctx.result("unset favorite item successfully");
    });
    
    //get recommendation
    app.post("/recommend", ctx -> {
      String userId = ctx.queryParam("userId");
      BookRecommend br = new BookRecommend();
      List<String> res = br.recommendItems(userId);
      ctx.result("sent");
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
      String bookName = ctx.queryParam("bookName");
      System.out.println(bookName);
      OpenLibraryApi olApi = new OpenLibraryApi();
      List<Item> items = olApi.search(bookName, "title");
      // to get JSON version of items
      List<JSONObject> list = new ArrayList<>();
      try {
        for (Item item : items) {
          System.out.println(item);
          JSONObject obj = item.toJsonObject();
          list.add(obj);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      JSONArray array = new JSONArray(list);
      ctx.result("Petter");
    });
    
    //Like a book
    app.post("/like", ctx -> {
      //To Do
    });
    
    //Cancel_Like a book
    app.post("/cancel_like", ctx -> {
      //To Do
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
