package jp.leopanda.googleAuthorization.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;

/**
 * 組み込みサンプル
 * 
 * @author LeoPanda
 *
 */
public class GoogleAuthorizationSample implements EntryPoint {

  Auth auth;

  @Override
  public void onModuleLoad() {
    //認証のリクエスト
    auth = new Auth(() -> onGetToken()).requestToken();
  }

  /**
   * 認証取得後の処理
   */
  private void onGetToken() {
    Window.alert("token=" + auth.getToken());
  }

}
