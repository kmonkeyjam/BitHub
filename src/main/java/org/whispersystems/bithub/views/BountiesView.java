package org.whispersystems.bithub.views;

import io.dropwizard.views.View;
import org.whispersystems.bithub.entities.Repository;

import java.util.List;

/**
 * Created by tina on 1/28/15.
 */
public class BountiesView extends View {
  private List<Repository> repositories;

  public BountiesView(List<Repository> repositories) {
    super("bounties.mustache");
    this.repositories = repositories;
  }

  public List<Repository> getRepositories() {
    return repositories;
  }
}
