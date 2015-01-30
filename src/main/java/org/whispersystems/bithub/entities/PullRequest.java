package org.whispersystems.bithub.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by tina on 1/29/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequest {
  @JsonProperty
  @NotEmpty
  private String url;

  @JsonProperty
  @NotEmpty
  private String html_url;

  public String getUrl() {
    return url;
  }

  public String getHtml_url() {
    return html_url;
  }
}
