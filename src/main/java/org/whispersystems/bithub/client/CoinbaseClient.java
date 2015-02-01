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
import com.coinbase.api.entity.AccountsResponse;
import com.coinbase.api.entity.Button;
import com.coinbase.api.entity.Transaction;
import com.coinbase.api.exception.CoinbaseException;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import org.joda.money.Money;
import org.whispersystems.bithub.entities.Author;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles interaction with the Coinbase API.
 *
 * @author Moxie Marlinspike
 */
public class CoinbaseClient {
  private final Coinbase coinbase;
  private final String primaryAccountId;
  private String apiKey;
  private String apiSecret;

  public CoinbaseClient(String apiKey, String apiSecret) {
    this.apiKey = apiKey;
    this.apiSecret = apiSecret;
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

  public Account createWallet(String name) throws CoinbaseException, IOException {
    Account account = new Account();
    account.setName(name);
    return coinbase.createAccount(account);
  }

  public List<Account> getAccounts() {
    List<Account> allAccounts = new ArrayList<>();
    try {
      AccountsResponse accounts = coinbase.getAccounts();
      allAccounts.addAll(accounts.getAccounts());
      int numPages = accounts.getNumPages();
      while (accounts.getCurrentPage() < numPages) {
        accounts = coinbase.getAccounts(accounts.getCurrentPage() + 1);
        allAccounts.addAll(accounts.getAccounts());
      }
    } catch (IOException | CoinbaseException e) {
      e.printStackTrace();
    }
    return allAccounts;
  }

  public String createButton(String accountId) {
    Button button = new Button();
    button.setName("Donate now");
    button.setPrice(Money.parse("USD 20.0"));
    button.setType(Button.Type.DONATION);
    button.setStyle(Button.Style.DONATION_LARGE);
    button.setText("Donate");

    Coinbase accountClient = new CoinbaseBuilder().withApiKey(apiKey, apiSecret).withAccountId(accountId).build();

    // try {
      // return coinbase.createButton(button).getCode();
      // XXX
      return "17278c0e3e28344cf313e43d03df4b71";
    // } catch (CoinbaseException | IOException e) {
    //   return null;
    // }
  }
}
