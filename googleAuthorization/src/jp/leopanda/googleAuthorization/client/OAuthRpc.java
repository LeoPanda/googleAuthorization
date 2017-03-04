package jp.leopanda.googleAuthorization.client;


import java.io.IOException;
import java.util.Collection;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("oauth")
public interface OAuthRpc extends RemoteService {

  String getAuthToken() throws  IOException, NoCredentialException;
  String getAuthTokenByScopes(Collection<String> scopes) throws  IOException, NoCredentialException;


}
