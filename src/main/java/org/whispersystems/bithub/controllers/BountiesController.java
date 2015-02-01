package org.whispersystems.bithub.controllers;

import com.codahale.metrics.annotation.Timed;
import org.whispersystems.bithub.client.CoinbaseClient;
import org.whispersystems.bithub.entities.Issue;
import org.whispersystems.bithub.entities.IssueWrapper;
import org.whispersystems.bithub.storage.CacheManager;
import org.whispersystems.bithub.views.BountiesView;
import org.whispersystems.bithub.views.DashboardView;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

/**
 * Created by tina on 1/28/15.
 */
@Path("/bounties")
public class BountiesController {
  public class IssueBounty {
    public Issue issue;
    public String code;

    public IssueBounty(Issue issue) {
      this.issue = issue;

    }
  }
  private CacheManager cacheManager;

  public BountiesController(CacheManager cacheManager, CoinbaseClient coinbaseClient) {
    this.cacheManager = cacheManager;
  }

  @Timed
  @GET
  @Produces(MediaType.TEXT_HTML)
  public BountiesView getBounties() {
    Map<String, List<IssueWrapper>> issues = cacheManager.getIssues();
    return new BountiesView(cacheManager.getRepositories(), issues);
  }
}
