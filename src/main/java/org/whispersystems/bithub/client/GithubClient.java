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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.core.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.bithub.BithubDAO;
import org.whispersystems.bithub.entities.Commit;
import org.whispersystems.bithub.entities.CommitComment;
import org.whispersystems.bithub.entities.Issue;
import org.whispersystems.bithub.entities.Repository;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles interaction with the GitHub API.
 *
 * @author Moxie Marlinspike
 */
public class GithubClient {
  public static class LinkHeaders {
    Map<String, String> parsed = new HashMap<>();

    public LinkHeaders(String header) {
      String[] links = header.split(",");
      for (String link : links) {
        String[] parts = link.split(";");
        String linkPart = parts[0].trim();
        String relPart = parts[1].trim();
        if (linkPart.startsWith("<") && linkPart.endsWith(">")) {
          linkPart = linkPart.substring(1, linkPart.length() - 1);
        }
        String[] relParts = relPart.split("=");
        if (relParts.length == 2) {
          String rel = relParts[1];
          if (rel.startsWith("\"") && rel.endsWith("\"")) {
            rel = rel.substring(1, rel.length() - 1);
            parsed.put(rel, linkPart);
          }
        }
      }
    }

    public String getLink(String relation) {
      return parsed.get(relation);
    }
  }

  private static final String GITHUB_URL = "https://api.github.com";
  private static final String COMMENT_PATH = "/repos/%s/%s/commits/%s/comments";
  private static final String COMMIT_PATH = "/repos/%s/%s/git/commits/%s";
  private static final String REPOSITORY_PATH = "/repos/%s/%s";
  private static final String ISSUES_PATH = "/repos/%s/%s/issues";

  private final Logger logger = LoggerFactory.getLogger(GithubClient.class);

  private final String authorizationHeader;
  private final Client client;

  public GithubClient(String user, String token) {
    this.authorizationHeader = getAuthorizationHeader(user, token);
    this.client = Client.create(getClientConfig());
  }

  public String getCommitDescription(String commitUrl) {
    String[] commitUrlParts = commitUrl.split("/");
    String owner = commitUrlParts[commitUrlParts.length - 4];
    String repository = commitUrlParts[commitUrlParts.length - 3];
    String commit = commitUrlParts[commitUrlParts.length - 1];

    String path = String.format(COMMIT_PATH, owner, repository, commit);
    WebResource resource = client.resource(GITHUB_URL).path(path);
    Commit response = appendAuthorization(resource).get(Commit.class);

    return response.getMessage();
  }

  public Repository getRepository(String url) {
    String[] urlParts = url.split("/");
    String owner = urlParts[urlParts.length - 2];
    String name = urlParts[urlParts.length - 1];

    String path = String.format(REPOSITORY_PATH, owner, name);
    WebResource resource = client.resource(GITHUB_URL).path(path);

    return appendAuthorization(resource).get(Repository.class);
  }

  public List<Issue> getOpenIssues(String url) {
    String[] urlParts = url.split("/");
    String owner = urlParts[0];
    String name = urlParts[1];

    List<Issue> results = new ArrayList<>();
    String path = String.format(ISSUES_PATH, owner, name);

    WebResource resource = client.resource(GITHUB_URL).path(path)
            .queryParam("state", "open");

    while (resource != null) {
      WebResource.Builder authorized = appendAuthorization(resource);
      results.addAll(authorized.get(new GenericType<List<Issue>>() {}));
      List<String> linkHeader = authorized.head().getHeaders().get("Link");
      if (linkHeader != null) {
        LinkHeaders headers = new LinkHeaders(linkHeader.get(0));
        if (headers.getLink("next") != null) {
          resource = client.resource(headers.getLink("next"));
        } else {
          break;
        }
      } else {
        break;
      }
    }

    return results.stream().filter(r -> r.getPull_request() == null).collect(Collectors.toList());
  }

  private WebResource.Builder appendAuthorization(WebResource resource) {
    return resource.type(MediaType.APPLICATION_JSON_TYPE)
            .accept(MediaType.APPLICATION_JSON_TYPE)
            .header("Authorization", authorizationHeader);
  }

  public void addCommitComment(Repository repository, Commit commit, String comment) {
    try {
      String path = String.format(COMMENT_PATH, repository.getOwner().getName(),
              repository.getName(), commit.getSha());

      WebResource resource = client.resource(GITHUB_URL).path(path);
      ClientResponse response = appendAuthorization(resource)
              .entity(new CommitComment(comment))
              .post(ClientResponse.class);

      if (response.getStatus() < 200 || response.getStatus() >= 300) {
        logger.warn("Commit comment failed: " + response.getClientResponseStatus().getReasonPhrase());
      }
    } catch (UniformInterfaceException | ClientHandlerException e) {
      logger.warn("Comment failed", e);
    }
  }

  private ClientConfig getClientConfig() {
    ClientConfig config = new DefaultClientConfig();
    config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

    return config;
  }

  private String getAuthorizationHeader(String user, String token) {
    return "Basic " + new String(Base64.encode(user + ":" + token));
  }
}
