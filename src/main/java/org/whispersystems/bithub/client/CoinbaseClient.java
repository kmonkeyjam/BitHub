/**
 * Copyright (C) 2013 Open WhisperSystems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.whispersystems.bithub.client;

import com.coinbase.api.Coinbase;
import com.coinbase.api.CoinbaseBuilder;
import com.coinbase.api.entity.Account;
import com.coinbase.api.entity.Transaction;
import com.coinbase.api.exception.CoinbaseException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import org.apache.commons.codec.binary.Hex;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.money.Money;
import org.whispersystems.bithub.entities.Author;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Handles interaction with the Coinbase API.
 *
 * @author Moxie Marlinspike
 */
public class CoinbaseClient {
  private static final String COINBASE_URL             = "https://coinbase.com";
  private static final String BALANCE_PATH             = "/api/v1/account/balance";
  private static final String PAYMENT_PATH             = "/api/v1/transactions/send_money";
  private static final String RECENT_TRANSACTIONS_PATH = "/api/v1/transactions";

  private final String apiKey;
  private final String apiSecret;
  private final Client client;
  private final Coinbase coinbase;
  private final String primaryAccountId;

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public CoinbaseClient(String apiKey, String apiSecret) {
    this.apiKey = apiKey;
    this.apiSecret = apiSecret;
    this.client = Client.create(getClientConfig());
    coinbase = new CoinbaseBuilder().withApiKey(apiKey, apiSecret).build();
    try {
      Account accounts = coinbase.getAccounts().getAccounts().stream().filter(Account::isPrimary).findFirst().get();
      primaryAccountId = accounts.getId();
    } catch (IOException | CoinbaseException e) {
      throw new IllegalStateException("Coinbase account must have a primary wallet specified");
    }
  }

  public List<Transaction> getRecentTransactions() throws IOException, TransferFailedException {
    try {
      return coinbase.getTransactions().getTransactions();
    } catch (CoinbaseException e) {
      throw new TransferFailedException();
    }
  }

  public BigDecimal getExchangeRate() throws IOException, TransferFailedException {
    try {
      return coinbase.getExchangeRates().get("btc_to_usd");
    } catch (CoinbaseException e) {
      throw new TransferFailedException();
    }
  }

  public void sendPayment(Author author, BigDecimal amount, String url)
      throws TransferFailedException {
    try {
      String note = "Commit payment:\n__" + author.getUsername() + "__ " + url;

      Transaction transaction = new Transaction();
      transaction.setTo(author.getEmail());
      transaction.setAmount(Money.parse("BTC " + amount.toPlainString()));
      transaction.setNotes(note);
      Transaction response = coinbase.sendMoney(transaction);

      if (response.getStatus() != Transaction.Status.COMPLETE) {
        throw new TransferFailedException();
      }

    } catch (UniformInterfaceException | ClientHandlerException | IOException | CoinbaseException e) {
      throw new TransferFailedException();
    }
  }

  public BigDecimal getAccountBalance() throws IOException, TransferFailedException {
    try {
      return coinbase.getBalance(primaryAccountId).getAmount();
    } catch (CoinbaseException e) {
      throw new IOException(e);
    }
  }

  private WebResource.Builder getAuthenticatedWebResource(String path, Object body) throws TransferFailedException {
    try {
      String json = body == null ? "" : objectMapper.writeValueAsString(body);
      String nonce = String.valueOf(System.currentTimeMillis());
      String message = nonce + COINBASE_URL + path + json;
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(apiSecret.getBytes(), "HmacSHA256"));

      String signature = new String(Hex.encodeHex(mac.doFinal(message.getBytes())));
      return client.resource(COINBASE_URL)
              .path(path)
              .accept(MediaType.APPLICATION_JSON)
              .header("ACCESS_SIGNATURE", signature)
              .header("ACCESS_NONCE", nonce)
              .header("ACCESS_KEY", apiKey);
    } catch (NoSuchAlgorithmException | InvalidKeyException | IOException e) {
      throw new TransferFailedException();
    }
  }

  private ClientConfig getClientConfig() {
    ClientConfig config = new DefaultClientConfig();
    config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

    return config;
  }

}
