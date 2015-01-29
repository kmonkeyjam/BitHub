package org.whispersystems.bithub;

import org.whispersystems.bithub.client.GithubClient;

import java.util.List;

/**
 * Created by tina on 1/28/15.
 */
public class TestMain {
  public static void main(String[] args) {

    //new GithubClient.LinkHeaders("<https://api.github.com/repositories/19429698/issues?state=open&page=2>; rel=\"next\", <https://api.github.com/repositories/19429698/issues?state=open&page=3>; rel=\"last\"");
    GithubClient client = new GithubClient("kmonkeyjam", "bc30eea5a46608d8a20c506ecae1c97e1248322e", null);
    List issues = client.getOpenIssues("/repos/WhisperSystems/Signal-iOS");
    System.out.println(issues);
//    String key = "E7opgtIZvWEC3U9R";
//    String secret = "tLBVtXdHYPSPQJtnKMRvu2LnHK1yvFMJ";
//    CoinbaseClient coinbaseClient = new CoinbaseClient(key, secret);
//    try {
//      ObjectMapper mapper = new ObjectMapper();
//      Author author = mapper.readValue(mapper.getJsonFactory().createJsonParser("{\n" +
//              "  \"name\": \"Tina\",\n" +
//              "  \"email\": \"tina@monkey.name\",\n" +
//              "  \"username\": \"kmonkeyjam\"\n" +
//              "}"), Author.class);
//      // coinbaseClient.sendPayment(author, new BigDecimal(0.01), "");
//      System.out.println(coinbaseClient.getRecentTransactions());
//    } catch (IOException e) {
//      e.printStackTrace();
//    } catch (TransferFailedException e) {
//      e.printStackTrace();
//    }
  }
}
