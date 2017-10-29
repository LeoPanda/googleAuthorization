package jp.leopanda.googleAuthorization.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Date;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.datastore.AppEngineDataStoreFactory;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.utils.SystemProperty;

import jp.leopanda.googleAuthorization.client.NoCredentialException;

import javax.servlet.http.HttpServletRequest;

/**
 * Credential生成汎用クラス
 * 
 * @author LeoPanda
 *
 */
public class CredentialUtils {

  public final HttpTransport httpTransport = new UrlFetchTransport();
  public final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

  private final String callbackPath = "/oauth2callback";
  private final String clientSecretFilePath = "WEB-INF/client_secrets.json";

  /**
   * 認証オブジェクトをロードする
   * 
   * @return 認証オブジェクト
   * @throws IOException
   * @throws NoCredentialException 
   */
  public Credential loadCredential() throws IOException, NoCredentialException {
    Credential credential = getFlow()
        .loadCredential(getCurrentUserId());
    return checkExpired(credential);
  }

  /**
   * 認証オブジェクトの有効期限をチェックし、切れていればデータストアから削除する
   * 
   * @param credential
   * @return
   * @throws IOException
   * @throws NoCredentialException 
   */
  private Credential checkExpired(Credential credential) throws IOException, NoCredentialException {
    if (credential == null) {
      return null;
    }
    Long expirationTIme = credential.getExpirationTimeMilliseconds();
    if (expirationTIme == null) {
      return credential;
    }
    Long nowTime = new Date().getTime();
    if (expirationTIme - nowTime < 10 * 1000) {
      removeCredentialStore();
      throw new NoCredentialException(getAuthUrl());
    } else {
      return credential;
    }
  }

  /**
   * キーファイル認証生成
   * 
   * @param p12key
   *          File 認証キー
   * @param emailAdress
   *          String サービスアカウントID
   * @return GoogleCredential Google認証オブジェクト
   * @throws GeneralSecurityException
   *           認証れ～概
   * @throws IOException
   *           IO例外
   */
  public GoogleCredential getCredentialByP12(File p12key, String emailAdress, String scope)
      throws GeneralSecurityException, IOException {
    GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport)
        .setJsonFactory(jsonFactory)
        .setServiceAccountId(emailAdress)
        .setServiceAccountScopes(Collections.singleton(scope))
        .setServiceAccountPrivateKeyFromP12File(p12key)
        .build();
    return credential;
  }

  /**
   * 認証フローを得る
   * 
   * @return
   * @throws IOException
   */
  public GoogleAuthorizationCodeFlow getFlow() throws IOException {
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport,
        jsonFactory, getClientSecrets(), Statics.scopes)
            .setDataStoreFactory(AppEngineDataStoreFactory
                .getDefaultInstance())
            .setAccessType("offline").build();
    return flow;
  }

  /**
   * Google App Engine クライアントシークレットの生成
   * 
   * @return
   * @throws IOException
   */
  private GoogleClientSecrets getClientSecrets() throws IOException {
    if (Statics.clientSecrets == null) {
      Statics.clientSecrets = GoogleClientSecrets.load(jsonFactory,
          new InputStreamReader(new FileInputStream(new File(clientSecretFilePath))));
    }
    return Statics.clientSecrets;
  }

  /**
   * HTTP Transportを取得する
   * 
   * @return
   */
  public HttpTransport getTransport() {
    return this.httpTransport;
  }

  /**
   * JsonFactoryを所得する
   * 
   * @return
   */
  public JsonFactory getJsonFactory() {
    return this.jsonFactory;
  }

  /**
   * カレントのユーザーIDを取得する
   * 
   * @return
   */
  public String getCurrentUserId() {
    User user = UserServiceFactory.getUserService().getCurrentUser();
    return user.getUserId();
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
   * コールバックURLを生成する（RPC用）
   * 
   * @return
   */
  private String getCallbackUrl() {
    String callbackUrl;
    if (SystemProperty.environment.get().equals("Development")) {
      callbackUrl = "http://127.0.0.1:8080" + callbackPath;
    } else {
      String version = SystemProperty.applicationVersion.get().split("\\.")[0];
      String appName = SystemProperty.applicationId.get();
      callbackUrl = "https://" + version + "-dot-" + appName + ".appspot.com" + callbackPath;
    }
    return callbackUrl;
  }
}
