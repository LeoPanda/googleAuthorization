package jp.leopanda.googleAuthorization.client;

/**
 * 権限不足例外
 * @author LeoPanda
 *
 */
public class ForbiddenException extends Exception {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  public ForbiddenException() {
    super();
  }
  public ForbiddenException(Throwable e){
    super(e);
  }

}
