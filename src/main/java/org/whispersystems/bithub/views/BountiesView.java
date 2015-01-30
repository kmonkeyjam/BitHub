package org.whispersystems.bithub.views;

import io.dropwizard.views.View;
import org.whispersystems.bithub.entities.Issue;
import org.whispersystems.bithub.entities.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by tina on 1/28/15.
 */
public class BountiesView extends View {
  public static class RepositoryView {
    public Repository repository;
    public List<Issue> issues;

    public RepositoryView(Repository repository, List<Issue> issues) {
      this.repository = repository;
      this.issues = issues;
    }
  }

  private List<Repository> repositories;
  private Map<String, List<Issue>> issues;

  public BountiesView(List<Repository> repositories, Map<String, List<Issue>> issues) {
    super("bounties.mustache");
    this.repositories = repositories;
    this.issues = issues;
  }

  public List<RepositoryView> getRepositories() {
    return repositories.stream().map(r -> new RepositoryView(r, issues.get(r.getUrl()))).collect(Collectors.toList());
  }

  public Map<String, List<Issue>> getIssues() {
    return issues;
  }
}
