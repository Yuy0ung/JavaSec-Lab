package com.javasec.controller;

import com.javasec.service.SqlInjectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * SQL注入漏洞控制器
 */
@RestController
@RequestMapping("/sqli")
public class SqlInjectionController {

    @Autowired
    private SqlInjectionService sqlInjectionService;

    /**
     * JDBC方式的SQL注入漏洞 - 用户登录
     */
    @PostMapping("/jdbc/login")
    public String jdbcLogin(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        
        try {
            String result = sqlInjectionService.loginWithJdbc(username, password);
            return result;
        } catch (Exception e) {
            return "登录失败: " + e.getMessage();
        }
    }

    /**
     * MyBatis方式的SQL注入漏洞 - 用户搜索
     */
    @GetMapping("/mybatis/search")
    public String mybatisSearch(@RequestParam String keyword) {
        try {
            String result = sqlInjectionService.searchUsersWithMyBatis(keyword);
            return result;
        } catch (Exception e) {
            return "搜索失败: " + e.getMessage();
        }
    }

    /**
     * 安全的参数化查询示例
     */
    @PostMapping("/safe/login")
    public String safeLogin(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        
        try {
            String result = sqlInjectionService.safeLogin(username, password);
            return result;
        } catch (Exception e) {
            return "登录失败: " + e.getMessage();
        }
    }
}