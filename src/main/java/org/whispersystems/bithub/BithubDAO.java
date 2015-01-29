package org.whispersystems.bithub;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

/**
 * Created by tina on 1/28/15.
 */
public interface BithubDAO {
  @SqlQuery("select name from something where id = :id")
  String findNameById(@Bind("id") int id);

  /**
   * close with no args is used to close the connection
   */
  void close();
}
