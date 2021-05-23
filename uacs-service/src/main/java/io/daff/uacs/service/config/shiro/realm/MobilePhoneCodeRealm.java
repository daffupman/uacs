package io.daff.uacs.service.config.shiro.realm;

import io.daff.uacs.service.config.shiro.token.MobilePhoneCodeToken;
import io.daff.uacs.service.entity.po.UserThings;
import io.daff.uacs.service.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * 手机号的realm
 *
 * @author daff
 * @since 2020/7/12
 */
@Slf4j
public class MobilePhoneCodeRealm extends StatelessRealm {

    public MobilePhoneCodeRealm(BaseService baseService) {
        super(baseService);
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof MobilePhoneCodeToken;
    }

    @Override
    protected UserThings verifyToken(AuthenticationToken token) {
        String mobilePhoneNum = ((String) token.getPrincipal());
        UserThings userThings = baseService.getUserByCondition(
                UserThings.builder().mobilePhoneNo(mobilePhoneNum).build()
        );
        userThingsStatusValidate(userThings, mobilePhoneNum);

        return userThings;
    }
}
