package jp.leopanda.googleAuthorization.server;

/**
 * Auth認証を使用したAPIコールを実装する
 * @author LeoPanda
 *
 */
public interface TryApiCall<R> {
  R apply() throws Exception;

}
