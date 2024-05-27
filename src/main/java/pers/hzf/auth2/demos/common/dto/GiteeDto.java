package pers.hzf.auth2.demos.common.dto;

import cn.hutool.json.JSONUtil;
import lombok.Data;

/**
 * @author houzhifang
 * @date 2024/5/24 15:11
 */
public class GiteeDto {

    @Data
    public static class UserInfo {
        private String avatar_url;
        private String bio;
        private String blog;
        private String created_at;
        private String email;
        private String events_url;
        private Integer followers;
        private String followers_url;
        private Integer following;
        private String following_url;
        private String gists_url;
        private String html_url;
        private Integer id;
        private String login;
        private String member_role;
        private String name;
        private String organizations_url;
        private Integer public_gists;
        private Integer public_repos;
        private String received_events_url;
        /**
         * 企业备注名
         */
        private String remark;
        private String repos_url;
        private Integer stared;
        private String starred_url;
        private String subscriptions_url;
        private String type;
        private String updated_at;
        private String url;
        private Integer watched;
        private String weibo;

        @Override
        public String toString() {
            return JSONUtil.toJsonStr(this);
        }

    }

}
