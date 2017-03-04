package jp.leopanda.googleAuthorization.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.datastore.AppEngineDataStoreFactory;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.utils.SystemProperty;

import javax.servlet.http.HttpServletRequest;

/**
 * Credential生成汎用クラス
 * 
 * @author LeoPanda
 *
 */
public class CredentialUtils {

  public final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();
  public final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  private final String callbackPath = "/oauth2callback";

  /**
   * Google App Engine クライアントシークレットの生成
   * 
   * @return
   * @throws IOException
   */
  public GoogleClientSecrets getClientSecrets() throws IOException {
    if (Statics.clientSecrets == null) {
      Statics.clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
          new InputStreamReader(new FileInputStream(new File("WEB-INF/client_secrets.json"))));
    }
    return Statics.clientSecrets;
  }

  /**
   * 認証フローを得る
   * 
   * @return
   * @throws IOException
   */
  public GoogleAuthorizationCodeFlow getFlow() throws IOException {
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT,
        JSON_FACTORY, getClientSecrets(), Statics.scopes)
            .setDataStoreFactory(AppEngineDataStoreFactory
                .getDefaultInstance())
            .setAccessType("offline").build();
    return flow;
  }

  /**
   * ストアされた認証データを削除する
   * 
   * @throws IOException
   */
  public void removeCredentialStore() throws IOException {
    GoogleAuthorizationCodeFlow flow = getFlow();
    if (flow != null) {
      flow.getCredentialDataStore().delete(getCurrentUserId());
    }
  }

  /**
   * コールバックリダイレクトURIを取得する(HTTP Get用)
   * 
   * @param req
   *          HttoServletRequest
   * @return
   */
  public String getRedirectUri(HttpServletRequest req) {
    GenericUrl url = new GenericUrl(req.getRequestURL().toString());
    url.setRawPath(callbackPath);
    return url.build();
  }

  /**
   * 認証オブジェクトをロードする
   * 
   * @return 認証オブジェクト
   * @throws IOException
   */
  public Credential loadCredential() throws IOException {
    Credential credential = getFlow()
        .loadCredential(getCurrentUserId());
    return credential;
  }

  /**
   * 認証取得を申請する
   * 
   * @return 認証完了後のコールバックURL
   * @throws IOException
   */
  public String getAuthUrl() throws IOException {
    String url = getFlow().newAuthorizationUrl().setRedirectUri(getCallbackUrl()).build();
    return url;
  }

  /**
   * カレントのユーザーIDを取得する
   * 
   * @return
   */
  private String getCurrentUserId() {
    User user = UserServiceFactory.getUserService().getCurrentUser();
    return user.getUserId();
  }

  /**
   * コールバックURLを生成する（RPC用）
   * 
   * @return
   */
  private String getCallbackUrl() {
    String callbackUrl;
    if (SystemProperty.environment.get().equals("Development")) {
      callbackUrl = "http://127.0.0.1:8888" + callbackPath;
    } else {
      String version = SystemProperty.applicationVersion.get().split("\\.")[0];
      String appName = SystemProperty.applicationId.get();
      callbackUrl = "https://" + version + "-dot-" + appName + ".appspot.com" + callbackPath;
    }
    return callbackUrl;
  }
}
