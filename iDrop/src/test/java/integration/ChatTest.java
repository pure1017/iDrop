package integration;


import controllers.Start;
import database.MysqlTableCreation;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;


@TestMethodOrder(OrderAnnotation.class) 
public class ChatTest {
  /**
  * Runs only once before the testing starts.
  */
  
  @BeforeAll
  public static void init() {
    // Start Server
    MysqlTableCreation.main(null);
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

  /**
   * This is to evaluate setfavorite end point.
   */
  @Test
  @Order(1)
  public void setFavoriteTest() {
    String destUri = "ws://localhost:8080/chat?userName=user1&picture=picture1";
    WebSocketClient client = new WebSocketClient();
    SimpleEchoSocket socket = new SimpleEchoSocket();
    try {
      client.start();
      URI echoUri = new URI(destUri);
      ClientUpgradeRequest request = new ClientUpgradeRequest();
      client.connect(socket, echoUri, request);
      socket.onMessage("test message");
      System.out.printf("Connecting to : %s%n", echoUri);
      socket.awaitClose(10, TimeUnit.SECONDS);
    } catch (Throwable t) {
      t.printStackTrace();
    } finally {
      try {
        client.stop();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
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
 * @throws InterruptedException . 
     */
  @AfterAll
  public static void close() {
    // Stop Server
    Start.stop(); 
    System.out.println("After All");
  }
}
