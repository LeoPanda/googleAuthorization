package jp.leopanda.googleAuthorization.server;

import java.io.IOException;

import javax.xml.ws.http.HTTPException;

import org.apache.http.HttpException;

/**
 * @author LeoPanda
 *
 */
public class GoogleApiException extends Exception {
  private static final long serialVersionUID = 1L;
  public GoogleApiException() {
    super();
  }
  private void removeStoredCredential(){
  }
}
