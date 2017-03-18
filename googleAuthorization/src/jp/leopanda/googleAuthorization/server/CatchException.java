package jp.leopanda.googleAuthorization.server;

import javax.servlet.http.HttpServletResponse;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;

import jp.leopanda.googleAuthorization.client.ForbiddenException;

/**
 * Auth認証を利用したAPIコール使用時の共通例外処理
 * 
 * @author LeoPanda
 *
 */
public abstract class CatchException<E> {

  /**
   * 実装したAPIコールを実行する
   * 
   * @return E 指定した戻り値
   * @throws Exception
   */
  public E execute() throws Exception {
    E result = null;
    try {
      result = tryApiCall();
    } catch (Throwable e) {
      afterProcess(e);
    }
    return result;
  }

  /**
   * Auth認証を使用したAPIコールを実装する
   * 
   * @return
   * @throws Exception
   */
  public abstract E tryApiCall() throws Exception;

  /**
   * 認証エラーの場合はストアされた認証を削除し認証例外を発生させる
   * 
   * @param e
   * @throws Exception
   */
  private void afterProcess(Throwable e) throws Exception {
    if (e instanceof GoogleJsonResponseException) {
      int statusCode = ((GoogleJsonResponseException) e).getStatusCode();
      if (statusCode == HttpServletResponse.SC_FORBIDDEN || statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
        new CredentialUtils().removeCredentialStore();
        throw new ForbiddenException(e);
      } else {
        e.printStackTrace();
        throw new Exception(e);
      }
    } else {
      e.printStackTrace();
      throw new Exception(e);

    }
  }

}
