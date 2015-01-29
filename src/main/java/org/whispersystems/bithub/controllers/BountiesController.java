package org.whispersystems.bithub.controllers;

import com.codahale.metrics.annotation.Timed;
import org.whispersystems.bithub.storage.CacheManager;
import org.whispersystems.bithub.views.BountiesView;
import org.whispersystems.bithub.views.DashboardView;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by tina on 1/28/15.
 */
@Path("/bounties")
public class BountiesController {
  private CacheManager cacheManager;

  public BountiesController(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @Timed
  @GET
  @Produces(MediaType.TEXT_HTML)
  public BountiesView getBounties() {
    return new BountiesView(cacheManager.getRepositories());
  }

}
