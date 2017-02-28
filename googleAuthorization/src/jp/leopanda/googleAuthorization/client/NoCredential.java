package jp.leopanda.googleAuthorization.client;

import java.io.Serializable;

/**
 * GoogleCredentialが取得できなかった場合の例外
 * 
 * @author LeoPanda
 *
 */
@SuppressWarnings("serial")
public class NoCredential extends Exception implements Serializable {
  private String rollbackUrl;

  public NoCredential() {
    super();
  }

  public NoCredential(String rollbackUrl) {
    this.rollbackUrl = rollbackUrl;
  }

  public String getRollbackUrl() {
    return this.rollbackUrl;
  }
}
