package jp.leopanda.googleAuthorization.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;

/**
 * 組み込みサンプル
 * @author LeoPanda
 *
 */
public class GoogleAuthorization implements EntryPoint {

  @Override
  public void onModuleLoad() {
    @SuppressWarnings("unused")
    Auth auth = new Auth() {
      
      @Override
      public void onGetToken() {
        Window.alert(getToken());
        
      }
    };
  }

}
