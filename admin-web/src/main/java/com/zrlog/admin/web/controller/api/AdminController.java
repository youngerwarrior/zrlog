package com.zrlog.admin.web.controller.api;

import com.zrlog.business.rest.request.LoginRequest;
import com.zrlog.business.rest.response.LoginResponse;
import com.zrlog.business.rest.response.StatisticsInfoResponse;
import com.zrlog.business.rest.response.UpdateRecordResponse;
import com.zrlog.business.service.UserService;
import com.zrlog.business.util.InstallUtils;
import com.zrlog.model.Comment;
import com.zrlog.model.Log;
import com.zrlog.model.User;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.blog.web.controller.BaseController;
import com.zrlog.admin.web.token.AdminTokenService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AdminController extends BaseController {

    private final UserService userService = new UserService();

    private final AdminTokenService adminTokenService = new AdminTokenService();

    private static final AtomicInteger sessionAtomicInteger = new AtomicInteger();

    public LoginResponse login() {
        LoginRequest loginRequest = ZrLogUtil.convertRequestBody(getRequest(), LoginRequest.class);
        userService.login(loginRequest);
        adminTokenService.setAdminToken(new User().getUserByUserName(loginRequest.getUserName().toLowerCase()),
                sessionAtomicInteger.incrementAndGet(), loginRequest.getHttps() ? "https" : "http", getRequest(), getResponse());
        return new LoginResponse();
    }

    /**
     * 插件调用这个方法
     */
    @RefreshCache
    public UpdateRecordResponse refreshCache() {
        return new UpdateRecordResponse();
    }

    public Map<String, Object> serverInfo() {
        Map<String, Object> info = new HashMap<>();
        InstallUtils.getSystemProp().forEach((key, value) -> info.put(key.toString(), value));
        BlogBuildInfoUtil.getBlogProp().forEach((key, value) -> info.put("zrlog." + key.toString(), value));
        return info;
    }

    public StatisticsInfoResponse statisticsInfo() {
        StatisticsInfoResponse info = new StatisticsInfoResponse();
        info.setCommCount(new Comment().count());
        info.setToDayCommCount(new Comment().countToDayComment());
        info.setClickCount(new Log().sumClick().longValue());
        info.setArticleCount(new Log().adminCount());
        return info;
    }
}
