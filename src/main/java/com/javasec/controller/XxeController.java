package com.javasec.controller;

import com.javasec.service.XxeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * XXE漏洞控制器
 */
@RestController
@RequestMapping("/xxe")
public class XxeController {

    @Autowired
    private XxeService xxeService;

    /**
     * XXE漏洞 - XML解析
     */
    @PostMapping(value = "/parse", consumes = {"application/xml", "text/xml"})
    public String parseXml(@RequestBody String xmlContent) {
        return xxeService.parseXmlUnsafe(xmlContent);
    }

    /**
     * 安全的XML解析示例
     */
    @PostMapping(value = "/safe-parse", consumes = {"application/xml", "text/xml"})
    public String safeParseXml(@RequestBody String xmlContent) {
        return xxeService.parseXmlSafe(xmlContent);
    }

    /**
     * 模拟敏感文件
     */
    @GetMapping("/sensitive-data")
    public String getSensitiveData() {
        return "敏感数据:\n" +
               "数据库连接字符串: jdbc:mysql://localhost:3306/sensitive_db\n" +
               "管理员密码: admin_secret_password_123\n" +
               "API密钥: sk-1234567890abcdef\n" +
               "内部服务地址: http://internal.company.com:8080";
    }
}