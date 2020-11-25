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
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import login.GoogleApiLogin;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import recommendation.BookRecommend;
import io.javalin.websocket.WsContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONObject;
import static j2html.TagCreator.article;
import static j2html.TagCreator.attrs;
import static j2html.TagCreator.b;
import static j2html.TagCreator.p;
import static j2html.TagCreator.span;
import static j2html.TagCreator.div;
import static j2html.TagCreator.img;

class Start {

  private static final int PORT_NUMBER = 8080;

  private static Javalin app;

  private static Map<WsContext, String> userUsernameMap = new ConcurrentHashMap<>();
  
  private static String currentUserName = "";

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
      Map<String, String> map = GoogleApiLogin.login(authCode);
      Gson gson = new Gson();
      ctx.result(gson.toJson(map));
    });
    
    //Get favorite item
    app.get("/getfavorite", ctx -> {

      //sample request from frontend
      //http://localhost:8080/getfavorite?userId=11111
      String userId = ctx.queryParam("userId");
      JSONArray arr = new JSONArray();
      
      System.out.println(userId);
      
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

      //sample request
      //http://localhost:8080/setfavorite?userId=11111&itemId=222
      String userId = ctx.queryParam("userId");
      String itemId = ctx.queryParam("itemId");
      System.out.println(userId);
      System.out.println(itemId);
      List<String> itemIds = new ArrayList<>();
      itemIds.add(itemId);
      //      HttpServletRequest request = ctx.req;
      //      JSONObject input = RpcHelper.readJsonObject(request);
      //      System.out.println(input);
      //      
      //      JSONArray array = input.getJSONArray("favorite");
      //      List<String> itemIds = new ArrayList<>();
      //      for (int i = 0; i < array.length(); ++i) {
      //        itemIds.add(array.get(i).toString());
      //      }
      
      MysqlConnection conn = new MysqlConnection();
      conn.setFavoriteItems(userId, itemIds);
      conn.close();
      ctx.result("set favorite item successfully");
    });
    
    //Unset favorite item
    app.delete("/unsetfavorite", ctx -> {
      
      //sample request
      //http://localhost:8080/setfavorite?userId=11111&itemId=333
      String userId = ctx.queryParam("userId");
      String itemId = ctx.queryParam("itemId");
      System.out.println(userId);
      System.out.println(itemId);
      List<String> itemIds = new ArrayList<>();
      itemIds.add(itemId);
      
      //      HttpServletRequest request = ctx.req;
      //      JSONObject input = RpcHelper.readJsonObject(request);
      //        
      //      JSONArray array = input.getJSONArray("favorite");
      //      List<String> itemIds = new ArrayList<>();
      //      for (int i = 0; i < array.length(); ++i) {
      //        itemIds.add(array.get(i).toString());
      //      }
        
      MysqlConnection conn = new MysqlConnection();
      conn.unsetFavoriteItems(userId, itemIds);
      conn.close();
      
      ctx.result("unset favorite item successfully");
    });
    
    //get recommendation
    app.post("/recommend", ctx -> {
      String userId = ctx.queryParam("userId");
      Set<Item> items = BookRecommend.recommendItems(userId);
      JSONArray arr = new JSONArray();
      int count = 0;
      for (Item item : items) {
        JSONObject obj = item.toJsonObject();
        if (count >= 5) {
          break;
        }
        count = count + 1;
        arr.put(obj);
      }
      Gson gson = new Gson();
      ctx.result(gson.toJson(arr));
    });
    
    //Host Group
    app.post("/hostGroup", ctx -> {
      //http://localhost:8080/hostgroup?userId=33333&bookName=book3&groupName=group3&
      //beginDate=2020&groupSize=5&groupDescription=description4
      String hostId = ctx.queryParam("userId");
      String bookName = ctx.queryParam("bookName").toLowerCase();
      String groupName = ctx.queryParam("groupName");
      String beginDate = ctx.queryParam("beginDate");
      String groupSize = ctx.queryParam("groupSize");
      String groupDescription = ctx.queryParam("groupDescription");
      if (hostId == null || bookName == null || groupName == null
          || beginDate == null || groupSize == null || groupDescription == null) {
        ctx.result("input error");
        return;
      }
      MysqlConnection conn = new MysqlConnection();
      conn.createGroup(hostId, bookName, groupName, beginDate, groupSize, groupDescription);
      ctx.result("group created");
    });
    
    //Join Group
    app.post("/joinGroup", ctx -> {
      //
      String userId = ctx.queryParam("userId");
      String bookName = ctx.queryParam("bookName").toLowerCase();
      String groupName = ctx.queryParam("groupName");
      String joinMessage = ctx.queryParam("joinMessage");
      if (userId == null || bookName == null || groupName == null || joinMessage == null) {
        ctx.result("input error");
        return;
      }
      MysqlConnection conn = new MysqlConnection();
      int flag = conn.joinGroup(userId, groupName, joinMessage);
      if (flag == 1) {
        ctx.result("Group does not exist");
        return;
      }
      if (flag == 2) {
        ctx.result("The group is full");
        return;
      }
      ctx.result("join successfully");
    });
    
    //get user group profile
    app.get("/getusergroup", ctx -> {
      //http://localhost:8080/getusergroup?userId=11111
      String userId = ctx.queryParam("userId");
      MysqlConnection conn = new MysqlConnection();
      List<Map<String, String>> groupsByHost = conn.getGroupsByHost(userId);
      List<Map<String, String>> groupsByMember = conn.getGroupsByMember(userId);
      List<List<Map<String, String>>> result = new ArrayList<>();
      result.add(groupsByHost);
      result.add(groupsByMember);
      Gson gson = new Gson();
      ctx.result(gson.toJson(result));
    });
    
    //get join message
    app.get("/getjoinmessage", ctx -> {
      //http://localhost:8080/getjoinmessage?userId=11111
      String userId = ctx.queryParam("userId");
      MysqlConnection conn = new MysqlConnection();
      List<Map<String, Map<String, Map<String, String>>>> messages = conn.getJoinMessages(userId);
      Gson gson = new Gson();
      ctx.result(gson.toJson(messages));
    });
    
    //Search Book
    app.post("/search", ctx -> {
      String userId = ctx.queryParam("userId");
      String bookName = ctx.queryParam("bookName").toLowerCase();
      System.out.println(bookName);
      MysqlConnection conn = new MysqlConnection();
      List<Item> items = conn.searchItems(bookName, "title");
      String realbookName = "";
      for (Item item : items) {
        realbookName = item.getTitle();
      }
      boolean rerating = conn.ifRerating(userId, realbookName);
      // to get JSON version of items
      JSONObject obj = new JSONObject();
      try {
        for (Item item : items) {
          System.out.println(item);
          obj = item.toJsonObject();
          //list.add(obj);
          break;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      //JSONArray array = new JSONArray(list);
      Gson gson = new Gson();
      String json = gson.toJson(obj);
      String result;
      if (rerating) {
        result = String.format("{\"rerating\":\"true\",\"map\":%s}", json);
      } else {
        result = String.format("{\"rerating\":\"false\",\"map\":%s}", json);
      }
      ctx.result(gson.toJson(result));
    });
    
    // Rating book
    app.post("/rating", ctx -> {
      String userId = ctx.queryParam("userId");
      String itemId = ctx.queryParam("itemId");
      String time = ctx.queryParam("time");
      float rating = Float.parseFloat(ctx.queryParam("rating"));
      String comment = ctx.queryParam("comment");
      if (userId == null || itemId == null || rating == 0.0) {
        ctx.result("input error");
        return;
      }
      MysqlConnection conn = new MysqlConnection();
      conn.ratingBook(userId, itemId, time, rating, comment);
      conn.close();
      ctx.result("book rated");
    });
    
    //get ratings
    app.get("/getrating", ctx -> {
      //http://localhost:8080/getrating?itemId=333
      String itemId = ctx.queryParam("itemId");
      if (itemId == null) {
        ctx.result("input error");
        return;
      }
      MysqlConnection conn = new MysqlConnection();
      List<List<String>> result = conn.getRatingsAndComments(itemId);
      conn.close();
      Gson gson = new Gson();
      ctx.result(gson.toJson(result));
    }); 
    
    // handle join group requests
    app.post("/handleapplication", ctx -> {
      // String hostId = ctx.queryParam("hostId");
      String applicantId = ctx.queryParam("applicantId");
      String groupName = ctx.queryParam("groupName");
      boolean add = Boolean.parseBoolean(ctx.queryParam("add"));
      if (applicantId == null || groupName == null) {
        ctx.result("input error");
        return;
      }
      if (add == false) {
        ctx.result("reject success");
        return;
      }
      MysqlConnection conn = new MysqlConnection();
      boolean isAvailable = conn.handleJoinRequests(applicantId, groupName);
      conn.close();
      if (isAvailable) {
        ctx.result("add success");
      } else {
        ctx.result("reject success");
      }
      
    });
    
    
    
    app.ws("/chat", ws -> {
      ws.onConnect(ctx -> {
        String username = ctx.queryParam("userName");
        currentUserName = username;
        userUsernameMap.put(ctx, username);
        broadcastMessage("Server", (username + " joined the chat"));
      });
      ws.onClose(ctx -> {
        String username = userUsernameMap.get(ctx);
        userUsernameMap.remove(ctx);
        broadcastMessage("Server", (username + " left the chat"));
      });
      ws.onMessage(ctx -> {
        broadcastMessage(userUsernameMap.get(ctx), ctx.message());
      });
    });
  }
  
  //Sends a message from one user to all users, along with a list of current usernames
  private static void broadcastMessage(String sender, String message) {
    userUsernameMap.keySet().stream().filter(ctx -> ctx.session.isOpen()).forEach(session -> {
      session.send(
          new JSONObject()
              .put("userMessage", createHtmlMessageFromSender(sender, message))
              .put("userlist", userUsernameMap.values()).toString()
      );
    });
  }

  // Builds a HTML element with a sender-name, a message, and a timestamp
  private static String createHtmlMessageFromSender(String sender, String message) {
    if (sender == currentUserName) {
      return article(
        div(attrs(".outgoing-chats"),
          div(attrs(".outgoing-chats-msg"),
            p(message),
            span(attrs(".time"), new SimpleDateFormat("HH:mm:ss").format(new Date()))),
          div(attrs(".outgoing-chats-img"), img().withSrc("assets/img/user2.JPG")))
        ).render();
    } else if (sender == "Server") {
      return article(
        div(attrs(".server_msg_box"),
          p(attrs("#server_msg"), message))
        ).render();
    }
    return article(
      div(attrs(".received-chats"),
        div(attrs(".received-chats-img"), img().withSrc("assets/img/user1.JPG")),
        div(attrs(".received-msg"),
          div(attrs(".received-msg-inbox"),
          p(message),
          span(attrs(".time"), new SimpleDateFormat("HH:mm:ss").format(new Date()))
          )
        )
      )
    ).render();
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