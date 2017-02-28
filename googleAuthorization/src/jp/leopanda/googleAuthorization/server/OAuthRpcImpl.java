package jp.leopanda.googleAuthorization.server;

import java.io.IOException;
import java.util.Collection;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.plus.PlusScopes;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import jp.leopanda.googleAuthorization.client.NoCredential;
import jp.leopanda.googleAuthorization.client.OAuthRpc;

/**
 * Google API 認証を取得する RPCサーバーモジュール
 * @author LeoPanda
 *
 */
public class OAuthRpcImpl extends RemoteServiceServlet implements OAuthRpc {

  private static final long serialVersionUID = 1L;

  /* 
   * API認証を取得して認証トークンを返す（PLUS_MEスコープ限定）
   */
  public String getAuthToken() throws IOException, NoCredential {
    Statics.addScope(PlusScopes.PLUS_ME);
    Utils utils = new Utils();
    Credential credential = utils.loadCredential();
    if (credential != null) {
      return credential.getAccessToken();
    } else {
      throw new NoCredential(utils.getAuthUrl());
    }
  }
  
  /* 
   * API認証を取得して認証トークンを返す（スコープを指定）
   */
  public String getAuthTokenByScopes(Collection<String> scopes) throws IOException, NoCredential{
    for (String scope : scopes) {
      Statics.addScope(scope);
    }
    return getAuthToken();
    
  }
}
