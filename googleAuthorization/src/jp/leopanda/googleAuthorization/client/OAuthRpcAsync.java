package jp.leopanda.googleAuthorization.client;



import java.util.Collection;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface OAuthRpcAsync {

  void getAuthToken(AsyncCallback<String> callback);
  void getAuthTokenByScopes(Collection<String> scopes,AsyncCallback<String> callback);
}
