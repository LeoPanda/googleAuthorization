package jp.leopanda.googleAuthorization.client;

import java.io.Serializable;

/**
 * GoogleCredentialが取得できなかった場合の例外
 * 
 * @author LeoPanda
 *
 */
@SuppressWarnings("serial")
public class NoCredentialException extends Exception implements Serializable {
  private String rollbackUrl;

  public NoCredentialException() {
    super();
  }

  public NoCredentialException(String rollbackUrl) {
    this.rollbackUrl = rollbackUrl;
  }

  public String getRollbackUrl() {
    return this.rollbackUrl;
  }
}
