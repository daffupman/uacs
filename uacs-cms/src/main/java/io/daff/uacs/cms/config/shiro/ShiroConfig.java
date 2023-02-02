package io.daff.uacs.cms.config.shiro;

import io.daff.uacs.cms.config.shiro.realm.JwtTokenRealm;
import io.daff.uacs.cms.config.shiro.realm.MobilePhoneCodeRealm;
import io.daff.uacs.cms.config.shiro.realm.UsernameAndPasswordRealm;
import io.daff.uacs.cms.service.BaseService;
import io.daff.uacs.service.util.SimpleRedisUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author daff
 * @since 2020/7/12
 */
@Configuration
public class ShiroConfig {

    @Lazy
    @Resource
    private BaseService baseService;
    @Resource
    private StatelessDefaultSubjectFactory statelessDefaultSubjectFactory;
    @Resource
    private SimpleRedisUtil simpleRedisUtil;

    /**
     * shiro注解支持的bean
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor advisor() {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(defaultWebSecurityManager());
        return advisor;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator creator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean() {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager());

        // 配置自定义的过滤器
        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("sauthc", new StatelessAuthcFilter(simpleRedisUtil));
        shiroFilterFactoryBean.setFilters(filters);

        // 配置访问规则
        LinkedHashMap<String, String> filterMap = new LinkedHashMap<>();
        // 无需认证的url
        filterMap.put("/static/**", "anon");
        filterMap.put("/passport/sign-in", "anon");
        filterMap.put("/*/oauth/**", "anon");
        filterMap.put("/cas/**", "anon");
        // swagger相关
        filterMap.put("/swagger-ui.html", "anon");
        filterMap.put("/swagger-resources/**", "anon");
        filterMap.put("/v2/api-docs", "anon");
        filterMap.put("/webjars/**", "anon");
        filterMap.put("/configuration/security", "anon");
        filterMap.put("/configuration/ui", "anon");
        filterMap.put("/", "anon");

        filterMap.put("/**", "sauthc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);

        return shiroFilterFactoryBean;
    }

    @Bean
    public DefaultWebSecurityManager defaultWebSecurityManager() {

        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 设置DefaultWebSecurityManager
        SecurityUtils.setSecurityManager(securityManager);
        securityManager.setAuthenticator(acsModularRealmAuthenticator());
        // 设置realm
        securityManager.setRealms(Arrays.asList(usernameAndPasswordRealm(), mobilePhoneRealm(), jwtTokenRealm()));

        // 关闭session
        DefaultSessionStorageEvaluator evaluator = new DefaultSessionStorageEvaluator();
        evaluator.setSessionStorageEnabled(false);
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        subjectDAO.setSessionStorageEvaluator(evaluator);
        securityManager.setSubjectDAO(subjectDAO);

        // 不创建session的Subject工厂
        securityManager.setSubjectFactory(statelessDefaultSubjectFactory);

        // 禁用会话调度器
        DefaultSessionManager defaultSessionManager = new DefaultSessionManager();
        defaultSessionManager.setSessionValidationSchedulerEnabled(false);
        securityManager.setSessionManager(defaultSessionManager);

        // 注入redisCacheManager
        // securityManager.setCacheManager(redisCacheManager);

        return securityManager;
    }

    /**
     * 针对多Realm，使用自定义身份验证器
     */
    @Bean
    public ModularRealmAuthenticator acsModularRealmAuthenticator(){
        return new UacsModularRealmAuthenticator();
    }

    @Bean
    public JwtTokenRealm jwtTokenRealm() {
        JwtTokenRealm realm = new JwtTokenRealm(baseService);
        realm.setCredentialsMatcher(multiTokenCredentialsMatcher());
        return realm;
    }

    @Bean
    public MobilePhoneCodeRealm mobilePhoneRealm() {
        MobilePhoneCodeRealm realm = new MobilePhoneCodeRealm(baseService);
        realm.setCredentialsMatcher(multiTokenCredentialsMatcher());
        return realm;
    }

    @Bean
    public UsernameAndPasswordRealm usernameAndPasswordRealm() {
        UsernameAndPasswordRealm realm = new UsernameAndPasswordRealm(baseService);
        realm.setCredentialsMatcher(multiTokenCredentialsMatcher());
        return realm;
    }

    @Bean
    public MultiTokenCredentialsMatcher multiTokenCredentialsMatcher() {
        return new MultiTokenCredentialsMatcher(simpleRedisUtil);
    }
}
