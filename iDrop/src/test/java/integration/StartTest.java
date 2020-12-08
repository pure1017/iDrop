package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.Gson;
import controllers.Start;
import database.MysqlRealData;
import database.MysqlTableCreation;

import java.util.concurrent.TimeUnit;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;


@TestMethodOrder(OrderAnnotation.class) 
public class StartTest {
  /**
  * Runs only once before the testing starts.
  */
  
  @BeforeAll
  public static void init() {
    // Start Server
    MysqlTableCreation.main(null);
    MysqlRealData.main(null);
    Start.main(null);
    System.out.println("Before All");
  }

  /**
  * This method starts a new game before every test run. It will run every time before a test.
  */
  @BeforeEach
  public void startNewPage() {
    // Test if server is running. You need to have an endpoint /
    // If you do not wish to have this end point, it is okay to not have anything in this method.
    //MysqlTableCreation.main(null);
    HttpResponse<String> response = Unirest.get("http://localhost:8080/").asString();
    int restStatus = response.getStatus();

    System.out.println("Before Each");
  }
  
//  /**
//   * This is a test case to evaluate the login endpoint.
//   */
//  @Test
//  @Order(1)
//  public void loginTest() {
//
//    System.out.println("----------------Test google login----------------");
//    
//    // Create HTTP request and get response
//    HttpResponse<String> response = Unirest.get("http://localhost:8080/newgame").asString();
//    int restStatus = response.getStatus();
//       
//    // Check assert statement (New Game has started)
//    assertEquals(restStatus, 200);
//  }
   
  /**
  * This is a test case to evaluate the getfavorite endpoint.
  */
  @Test
  @Order(2)
  public void getFavoriteTest() {
      
    System.out.println("----------------Test Get Favorite----------------");  

    // Create a POST request to startgame endpoint and get the body
    // Remember to use asString() only once for an endpoint call. 
    // Every time you call asString(), a new request will be sent to the endpoint. 
    // Call it once and then use the data in the object.
    HttpResponse<String> response = Unirest.get("http://localhost:8080/getfavorite?userId=11111").asString();
    assertEquals(200, response.getStatus());
//    String responseBody = response.getBody();
//       
//    // --------------------------- JSONObject Parsing ----------------------------------
//       
//    System.out.println("Get Favorite Response: " + responseBody);
//       
//    // Parse the response to JSON object
//    JSONObject jsonObject = new JSONObject(responseBody);
//
//    // Check if game started after player 1 joins: Game should not start at this point
//    Object s1 = jsonObject.get("myArrayList");
//    JSONArray js = jsonObject.getJSONArray("myArrayList");
//    JSONObject ob = (JSONObject) js.get(0);
//    JSONObject ob2 = (JSONObject) ob.getJSONObject("map");
//    String check = ob2.getString("item_id");
//    System.out.println(check);
//    assertEquals(true, check.equals("111"));
  }
   
  /**
   * This is to evaluate setfavorite end point.
   */
  @Test
  @Order(3)
  public void setFavoriteTest() {
    System.out.println("----------------Test set favorite----------------");  
    HttpResponse<String> response = Unirest.post("http://localhost:8080/setfavorite?userId=11111&itemId=222").asString();
    // Check the status of response of calling joingame endpoint
    assertEquals(200, response.getStatus());
  }
  
  /**
   *  This is to evaluate setfavorite end point.
   */
  @Test
  @Order(4)
  public void unsetFavoriteTest() {
   
    System.out.println("----------------Test unsetFavorite----------------");

    // Create a POST request to start endpoint
    HttpResponse<String> response = Unirest.delete("http://localhost:8080/unsetfavorite?userId=11111&itemId=333").asString();    
    assertEquals(200, response.getStatus());
  }
  
  /**
   * this is to test recommand.
   */
  @Test
  @Order(5)
  public void recommendTest() {

    System.out.println("----------------Test Recommand----------------");
    HttpResponse<String> response = Unirest.post("http://localhost:8080/recommend?userId=11111").asString();    
    assertEquals(200, response.getStatus());
  }
  
  /**
   * This is to test hostgroup endpoint.
   */
  @Test
  @Order(6)
  public void hostGroupTest() {

    System.out.println("----------------Test Host Group-------");

    HttpResponse<String> response = Unirest.post("http://localhost:8080/hostGroup?"
        + "userId=33333&bookName=book3&groupName=group3&"
        + "beginDate=2020&groupSize=5&groupDescription=description4").asString();
    System.out.println(response.getStatus());
    assertEquals(200, response.getStatus());
  }
  
  /**
   * This is to test join group.
   */
  @Test
  @Order(7)
  public void joinGroupTest() {

    System.out.println("----------------Test Join Group----------------");

    //Create a POST request to start endpoint
    HttpResponse<String> response = Unirest.post("http://localhost:8080/joinGroup?userId=22222&bookName=book1"
        + "&groupName=group1&joinMessage=22222join").asString();
    assertEquals(200, response.getStatus());
  }
  
  /**
   * This is to test get urser gourp endpoint.
   */
  @Test
  @Order(8)
  public void getUserGroupTest() {

    System.out.println("----------------Test Get User Group----------------");
    HttpResponse<String> response =  Unirest.get("http://localhost:8080/getusergroup?"
        + "userId=22222").asString();
    assertEquals(200, response.getStatus());  
  }
  
  /**
   * This is to test getjoinmessage endpoint.
   */
  @Test
  @Order(9)
  public void getJoinMessageTest() {
    
    System.out.println("----------------Test Get Join Message----------------");
    HttpResponse<String> response =  Unirest.get("http://localhost:8080/getjoinmessage?"
        + "userId=11111").asString();
    assertEquals(200, response.getStatus());  
  }
  
  /**
   * This is to test search endpoint.
   */
  @Test
  @Order(10)
  public void searchTest1() {

    System.out.println("----------------Test Search----------------");
    HttpResponse<String> response =  Unirest.post("http://localhost:8080/search?"
        + "userId=11111&bookName=book1").asString();
    assertEquals(200, response.getStatus());  
  }
  
  /**
   * This is to test search endpoint.
   */
  @Test
  @Order(11)
  public void searchTest2() {

    System.out.println("----------------Test Search2----------------");
    HttpResponse<String> response =  Unirest.post("http://localhost:8080/search?"
        + "userId=11111&bookName=the+lord+of+ring").asString();
    assertEquals(200, response.getStatus());  
  }
  
  
  
  /**
   * This is to test rating endpoint.
   */
  @Test
  @Order(12)
  public void ratingTest() {

    System.out.println("----------------Test Rating----------------");
    HttpResponse<String> response =  Unirest.post("http://localhost:8080/rating?"
        + "userId=11111&itemId=111&time=2020&rating=5.0&comment=this+is+commend").asString();
    assertEquals(200, response.getStatus()); 
  }
  
  /**
   * This is to test getrating endpoint.
   */
  @Test
  @Order(13)
  public void getratingTest() {

    System.out.println("----------------Test Get Rating----------------");
    HttpResponse<String> response =  Unirest.get("http://localhost:8080/getrating?"
        + "itemId=111").asString();
    System.out.println(response.getStatus());
    assertEquals(200, response.getStatus()); 
  }
  
  /**
   * This is to test handleapplication endpoint.
   */
  @Test
  @Order(14)
  public void handleApplicationTest() {

    System.out.println("----------------Test Handle Application----------------");
    HttpResponse<String> response =  Unirest.post("http://localhost:8080/handleapplication?"
        + "applicantId=22222&groupName=group1").asString();
    assertEquals(200, response.getStatus()); 
  }
  
//  /**
//   * This is to test chat endpoint.
//   */
//  @Test
//  @Order(15)
//  public void chatTest() {
//
//    System.out.println("----------------Test Chat----------------");
//    HttpResponse<String> response =  Unirest.post("http://localhost:8080/chat?"
//        + "userName=user1&picture=picture1").asString();
//    assertEquals(200, response.getStatus()); 
//    HttpResponse<String> response2 =  Unirest.post("http://localhost:8080/chat?"
//        + "userName=user2&picture=picture2").asString();
//    assertEquals(200, response2.getStatus()); 
//  }
  
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
    Start.stop(); 
    System.out.println("After All");
  }
}
