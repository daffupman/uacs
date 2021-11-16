package io.daff.uacs.cms.config.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.realm.Realm;

import java.util.Collection;

/**
 * 不同的token只交由自己的realm验证
 *
 * @author daff
 * @since 2020/10/10
 */
public class AcsModularRealmAuthenticator extends ModularRealmAuthenticator {

    @Override
    protected AuthenticationInfo doMultiRealmAuthentication(Collection<Realm> realms, AuthenticationToken token) {
        // 空realm校验
        assertRealmsConfigured();

        Realm targetRealm = null;
        for (Realm realm : realms) {
            if (realm.supports(token)) {
                targetRealm = realm;
                break;
            }
        }
        if (targetRealm == null) {
            throw new UnsupportedTokenException();
        }

        return targetRealm.getAuthenticationInfo(token);
    }
}
