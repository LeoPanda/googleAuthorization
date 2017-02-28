package jp.leopanda.googleAuthorization.server;

import java.util.Collection;
import java.util.HashSet;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;

/**
 * スタティック変数保持クラス
 * @author LeoPanda
 *
 */
public class Statics {
  public static GoogleClientSecrets clientSecrets = null;
  public static Collection<String> scopes = new HashSet<String>();

  public static void addScope(String scope) {
    Statics.scopes.add(scope);
  }

}
