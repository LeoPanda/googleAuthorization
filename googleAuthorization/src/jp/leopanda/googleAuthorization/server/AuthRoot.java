package jp.leopanda.googleAuthorization.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeServlet;

/**
 * @author LeoPanda
 *
 */
public class AuthRoot extends AbstractAppEngineAuthorizationCodeServlet {
  private static final long serialVersionUID = 1L;
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
}
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException ,IOException {
  }

  /* 
   * 
   */
  @Override
  protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException {
    return new Utils().getFlow();
  }

  /* 
   * 
   */
  @Override
  protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
    return new Utils().getRedirectUri(req);
  }

}
