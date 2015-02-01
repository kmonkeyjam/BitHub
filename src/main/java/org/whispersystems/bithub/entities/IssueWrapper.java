/**
 * Copyright (C) 2015 Open WhisperSystems
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
package org.whispersystems.bithub.entities;

import com.coinbase.api.entity.Account;
import org.whispersystems.bithub.storage.CurrentPayment;

public class IssueWrapper {
  private Issue issue;
  private String buttonCode;
  private Account wallet;
  private CurrentPayment currentPayment;
  private CurrentPayment perCommitPayment;

  public IssueWrapper(Issue issue, String buttonCode, Account wallet, CurrentPayment currentPayment,
                      CurrentPayment perCommitPayment) {
    this.issue = issue;
    this.buttonCode = buttonCode;
    this.wallet = wallet;
    this.currentPayment = currentPayment;
    this.perCommitPayment = perCommitPayment;
  }

  public Account getWallet() {
    return wallet;
  }

  public Issue getIssue() {
    return issue;
  }

  public String getPayment() {
    return currentPayment.getEntity().getPayment() + perCommitPayment.getEntity().getPayment();
  }

  public String getButtonCode() {
    return buttonCode;
  }
}
