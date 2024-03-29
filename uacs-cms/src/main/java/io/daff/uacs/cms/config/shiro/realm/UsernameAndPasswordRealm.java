package io.daff.uacs.cms.config.shiro.realm;

import io.daff.uacs.cms.service.BaseService;
import io.daff.uacs.service.entity.po.UserThings;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 使用帐密的realm
 *
 * @author daff
 * @since 2020/7/12
 */
public class UsernameAndPasswordRealm extends StatelessRealm {

    public UsernameAndPasswordRealm(BaseService baseService) {
        super(baseService);
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    @Override
    protected UserThings verifyToken(AuthenticationToken token) {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        String username = usernamePasswordToken.getUsername();
        UserThings userThings = baseService.matchAccount(username);
        if (userThings == null) {
            throw new UnknownAccountException("用户不存在");
        }
        userThingsStatusValidate(userThings, username);
        return userThings;
    }
}
