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

package org.whispersystems.bithub;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.db.DataSourceFactory;
import org.whispersystems.bithub.config.BithubConfiguration;
import org.whispersystems.bithub.config.CoinbaseConfiguration;
import org.whispersystems.bithub.config.GithubConfiguration;
import org.whispersystems.bithub.config.OrganizationConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.dropwizard.Configuration;

public class BithubServerConfiguration extends Configuration {

  @Valid
  @NotNull
  @JsonProperty
  private GithubConfiguration github;

  @Valid
  @NotNull
  @JsonProperty
  private CoinbaseConfiguration coinbase;

  @JsonProperty
  @Valid
  private BithubConfiguration bithub = new BithubConfiguration();

  @Valid
  @NotNull
  @JsonProperty
  private OrganizationConfiguration organization;

  @Valid
  @NotNull
  @JsonProperty
  private DataSourceFactory database = new DataSourceFactory();

  public DataSourceFactory getDataSourceFactory() {
    return database;
  }

  public GithubConfiguration getGithubConfiguration() {
    return github;
  }

  public CoinbaseConfiguration getCoinbaseConfiguration() {
    return coinbase;
  }

  public BithubConfiguration getBithubConfiguration() {
    return bithub;
  }

  public OrganizationConfiguration getOrganizationConfiguration() {
    return organization;
  }
}
