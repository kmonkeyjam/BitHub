package org.whispersystems.bithub.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by tina on 1/28/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Issue {
  @JsonProperty
  @NotEmpty
  private String id;

  @JsonProperty
  @NotEmpty
  private String title;

  @JsonProperty
  @NotEmpty
  private String state;

  @JsonProperty
  @NotEmpty
  private String number;

  @JsonProperty
  @NotEmpty
  private String url;

  @JsonProperty
  @NotEmpty
  private String html_url;

  @JsonProperty
  @NotEmpty
  private PullRequest pull_request;

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getState() {
    return state;
  }

  public String getNumber() {
    return number;
  }

  public String getUrl() {
    return url;
  }

  public String getHtml_url() {
    return html_url;
  }

  public PullRequest getPull_request() {
    return pull_request;
  }
}
