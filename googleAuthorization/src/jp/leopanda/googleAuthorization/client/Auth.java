package jp.leopanda.googleAuthorization.client;

import java.util.Collection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * OAuth2認証取得指示クラス
 * 
 * 使用方法
 * 
 * <pre>
 * １）web.xmlに以下のセクションを追加
 * <servlet>
    <servlet-name>OAuthRpcImpl</servlet-name>
    <servlet-class>jp.leopanda.googleAuthorization.server.OAuthRpcImpl</servlet-class>
  </servlet>
  <servlet-mapping>
    <servlet-name>OAuthRpcImpl</servlet-name>
    <url-pattern>/(アプリケーション名)/oauth</url-pattern>
  </servlet-mapping>
  <servlet>
    <servlet-name>OAuth2Callback</servlet-name>
    <servlet-class>jp.leopanda.googleAuthorization.server.OAuth2Callback</servlet-class>
  </servlet>
  <servlet-mapping>
    <servlet-name>OAuth2Callback</servlet-name>
    <url-pattern>/oauth2callback</url-pattern>
  </servlet-mapping>
  <!-- security constraint -->
  <security-constraint>
    <web-resource-collection>
      <web-resource-name>any</web-resource-name>
      <url-pattern>/*</url-pattern>
    </web-resource-collection>
    <auth-constraint>
      <role-name>*</role-name>
    </auth-constraint>
  </security-constraint>
  <!-- resource file -->
  <resource-files>
    <include path="/**.json" />
  </resource-files>

  2)gwt.xmlファイルに以下のセクションを追加
   <inherits name="jp.leopanda.googleAuthorization.GoogleAuthorization" />

  ３）AppEngine APIコンソールからWebアプリ用の認証クライアントIDとシークレットキーを作成し、jsonファイルをダウンロードする。
  ４）ダウンロードしたファイルを client_secrets.json　とリネームし、 /WEB-INF の直下に置く。
  ５）このクラスを生成し、認証取得後の処理を onGetToken()　に記述する。
 * 
 * </pre>
 * 
 * @author LeoPanda
 *
 */
public abstract class Auth {

  private OAuthRpcAsync rpc = GWT.create(OAuthRpc.class);
  private String authToken = null;

  /**
   * 通常コンストラクタ
   * PLUS_MEスコープのみの認証を取得する
   */
  public Auth() {
    rpc.getAuthToken(new AsyncCallback<String>() {

      @Override
      public void onSuccess(String result) {
        onGoing(result);
      }

      @Override
      public void onFailure(Throwable caught) {
        onException(caught);
      }
    });
  }

  /**
   * スコープを指定して認証を得るためのコンストラクタ
   * @param scopes
   */
  public Auth(Collection<String> scopes) {
    rpc.getAuthTokenByScopes(scopes, new AsyncCallback<String>() {

      @Override
      public void onSuccess(String result) {
        onGoing(result);
      }

      @Override
      public void onFailure(Throwable caught) {
        onException(caught);
      }
    });
}
  
  /**
   * 認証を正常取得した後の処理を記述する
   */
  public abstract void onGetToken();

  /**
   * 認証取得に失敗した場合の処理（独自に処理を記述するにはオーバライドしてください）
   * @param caught
   */
  public void onError(Throwable caught) {
    Window.alert("authError:" + caught.getMessage());
  }

  /**
   * 認証トークンを取得する
   * （サーバーサイドでは new Utils().loadCredential()で認証オブジェクトを取得できます。
   * 
   * @return
   */
  public String getToken() {
    return this.authToken;
  }

  private void onException(Throwable caught) {
    if (caught instanceof NoCredential) {
      Window.Location.replace(((NoCredential) caught).getRollbackUrl());
    } else {
      onError(caught);
    }
  }

  private void onGoing(String result) {
    this.authToken = result;
    onGetToken();
  }

}
