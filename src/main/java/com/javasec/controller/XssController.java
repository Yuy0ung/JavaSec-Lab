package com.javasec.controller;

import com.javasec.service.XssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * XSS漏洞控制器
 */
@RestController
@RequestMapping("/xss")
public class XssController {

    @Autowired
    private XssService xssService;

    /**
     * 反射型XSS漏洞 - 评论功能
     */
    @PostMapping("/reflected")
    public String reflectedXss(@RequestBody Map<String, String> params) {
        String input = params.get("input");
        return xssService.processReflectedXss(input);
    }

    /**
     * 存储型XSS漏洞 - 留言板功能
     */
    @PostMapping("/stored")
    public String storedXss(@RequestBody Map<String, String> params) {
        String name = params.get("name");
        String message = params.get("message");
        
        // 如果前端只发送了message参数，使用默认name
        if (name == null || name.trim().isEmpty()) {
            name = "匿名用户";
        }
        
        return xssService.processStoredXss(name, message);
    }

    /**
     * 获取所有留言 - 用于展示存储型XSS
     */
    @GetMapping("/messages")
    public String getMessages() {
        return xssService.getAllMessages();
    }

    /**
     * 安全的XSS防护示例
     */
    @PostMapping("/safe")
    public String safeXss(@RequestBody Map<String, String> params) {
        String content = params.get("content");
        return xssService.processSafeContent(content);
    }
}