package io.daff.uacs.cms.config.shiro.realm;

import io.daff.logging.DaffLogger;
import io.daff.uacs.cms.service.BaseService;
import io.daff.uacs.core.enums.BaseModule;
import io.daff.uacs.service.entity.po.Permission;
import io.daff.uacs.service.entity.po.Role;
import io.daff.uacs.service.entity.po.UserThings;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author daff
 * @since 2020/7/14
 */
public abstract class StatelessRealm extends AuthorizingRealm {

    private static final DaffLogger log = DaffLogger.getLogger(StatelessRealm.class);

    protected BaseService baseService;

    public StatelessRealm(BaseService baseService) {
        this.baseService = baseService;
        super.setCachingEnabled(false);
    }

    /**
     * jwt授权逻辑
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        UserThings loginUserThings = (UserThings) principals.getPrimaryPrincipal();

        // 查询用户所赋予的角色和权限
        List<Role> roleVoList = baseService.getUserRolesBySsoId(loginUserThings.getId());
        List<Permission> permissionVoList = baseService.getUserPermissionsBySsoId(loginUserThings.getId());

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setStringPermissions(
                permissionVoList.stream().map(Permission::getCode).collect(Collectors.toSet())
        );
        simpleAuthorizationInfo.setRoles(
                roleVoList.stream().map(Role::getName).collect(Collectors.toSet())
        );

        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        UserThings userThings = verifyToken(token);
        /*
            principal：认证的实体信息，可以是username，也可以是数据库表对应的用户的实体对象
            credentials：数据库中的密码（经过加密的密码）
            credentialsSalt：盐值（使用用户名）
            realmName：当前realm对象的name，调用父类的getName()方法即可
         */
        return new SimpleAuthenticationInfo(
                userThings,
                userThings.getPassword(),
                ByteSource.Util.bytes(userThings.getSalt()),
                getName()
        );
    }

    /**
     * 不同的token，验证需要自己实现
     */
    protected abstract UserThings verifyToken(AuthenticationToken token);

    /**
     * 检查用户的存在性和状态合法性
     */
    protected void userThingsStatusValidate(UserThings userThings, String subjectId) {

        if (userThings == null) {
            log.error("不存在的用户：{}！", BaseModule.AUTHC, subjectId);
            throw new UnknownAccountException("用户不存在！");
        }

        if (!userThings.getStatus().equals(Byte.parseByte("1"))) {
            log.error("用户状态异常：{}", BaseModule.AUTHC, subjectId);
            throw new LockedAccountException("用户状态异常");
        }
    }
}
