package io.daff.uacs.service.config.shiro.realm;

import io.daff.uacs.service.config.shiro.token.JwtToken;
import io.daff.uacs.service.entity.po.UserThings;
import io.daff.uacs.service.service.BaseService;
import io.daff.uacs.service.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * 使用jwt token的realm
 *
 * @author daff
 * @since 2020/7/12
 */
@Slf4j
public class JwtTokenRealm extends StatelessRealm {

    public JwtTokenRealm(BaseService baseService) {
        super(baseService);
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    protected UserThings verifyToken(AuthenticationToken token) {
        String jwtToken = (String) token.getPrincipal();
        String subjectId = JwtUtil.getSubjectId(jwtToken);
        UserThings userThings = baseService.getUserByCondition(UserThings.builder().id(Long.parseLong(subjectId)).build());
        userThingsStatusValidate(userThings, subjectId);
        return userThings;
    }

}
