package io.daff.uacs.service.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * app_info表的实体类
 *
 * @author wangzhengjin
 * @since 2021/05/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppInfo {
    private String id;

    private String appName;

    private String appId;

    private String appSecret;

    private String grantType;

    private String scope;

    private String redirectUrl;

    private String description;

    private Byte status;

    private String createBy;

    private String updateBy;

    private Date createAt;

    private Date updateAt;

    public List<String> getBindScopes() {
        List<String> scopes = new ArrayList<>();
        if (!
                StringUtils.isEmpty(this.scope)) {
            if (scope.charAt(0) == '1') { scopes.add("insert"); }
            if (scope.charAt(1) == '1') { scopes.add("delete"); }
            if (scope.charAt(2) == '1') { scopes.add("update"); }
            if (scope.charAt(3) == '1') { scopes.add("select"); }
        }
        return scopes;
    }
}