package com.javasec.controller;

import com.javasec.service.SsrfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * SSRF漏洞控制器
 */
@RestController
@RequestMapping("/ssrf")
public class SsrfController {

    @Autowired
    private SsrfService ssrfService;

    /**
     * SSRF漏洞 - URL内容获取
     */
    @PostMapping("/fetch")
    public String fetchUrl(@RequestBody Map<String, String> params) {
        String url = params.get("url");
        return ssrfService.fetchUrlContent(url);
    }

    /**
     * 模拟内网管理页面
     */
    @GetMapping("/admin")
    public String adminPage() {
        return "内网管理页面 - 敏感信息：\n" +
               "数据库密码: db_secret_123\n" +
               "API密钥: api_key_456789\n" +
               "内网服务列表:\n" +
               "- 192.168.1.100:3306 (MySQL)\n" +
               "- 192.168.1.101:6379 (Redis)\n" +
               "- 192.168.1.102:9200 (Elasticsearch)";
    }

    /**
     * 安全的URL获取示例
     */
    @PostMapping("/safe-fetch")
    public String safeFetchUrl(@RequestBody Map<String, String> params) {
        String url = params.get("url");
        return ssrfService.safeFetchUrlContent(url);
    }
}