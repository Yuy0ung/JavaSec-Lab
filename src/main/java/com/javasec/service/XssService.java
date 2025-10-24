package com.javasec.service;

import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XSS漏洞服务类
 */
@Service
public class XssService {

    // 模拟存储型XSS的数据存储
    private List<Map<String, String>> messages = new ArrayList<>();

    /**
     * 反射型XSS漏洞 - 直接返回用户输入
     */
    public String processReflectedXss(String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            return "请输入评论内容";
        }

        // 危险：直接返回用户输入，未进行任何过滤
        String response = "<div style='padding: 10px; border: 1px solid #ddd; margin: 10px 0;'>" +
                         "<h4>用户评论:</h4>" +
                         "<p>" + comment + "</p>" +
                         "<small>评论时间: " + new java.util.Date() + "</small>" +
                         "</div>";

        System.out.println("反射型XSS - 用户输入: " + comment);
        return response;
    }

    /**
     * 存储型XSS漏洞 - 存储用户输入到内存
     */
    public String processStoredXss(String name, String message) {
        if (name == null || name.trim().isEmpty() || message == null || message.trim().isEmpty()) {
            return "姓名和留言内容不能为空";
        }

        // 危险：直接存储用户输入，未进行任何过滤
        Map<String, String> msg = new HashMap<>();
        msg.put("id", String.valueOf(System.currentTimeMillis()));
        msg.put("name", name);
        msg.put("message", message);
        msg.put("time", new java.util.Date().toString());

        messages.add(msg);

        System.out.println("存储型XSS - 姓名: " + name + ", 留言: " + message);
        return "留言提交成功！";
    }

    /**
     * 获取所有留言 - 用于展示存储型XSS
     */
    public String getAllMessages() {
        if (messages.isEmpty()) {
            return "<p>暂无留言</p>";
        }

        StringBuilder html = new StringBuilder();
        html.append("<div style='max-height: 400px; overflow-y: auto;'>");
        
        for (Map<String, String> msg : messages) {
            html.append("<div style='padding: 10px; border: 1px solid #ddd; margin: 10px 0; border-radius: 5px;'>");
            html.append("<strong>").append(msg.get("name")).append("</strong>: ");
            html.append(msg.get("message"));
            html.append("<br><small style='color: #666;'>").append(msg.get("time")).append("</small>");
            html.append("</div>");
        }
        
        html.append("</div>");
        return html.toString();
    }

    /**
     * 安全的内容处理 - HTML转义
     */
    public String processSafeContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "请输入内容";
        }

        // 安全：使用HTML转义
        String safeContent = HtmlUtils.htmlEscape(content);
        
        String response = "<div style='padding: 10px; border: 1px solid #4CAF50; margin: 10px 0; background: #f1f8e9;'>" +
                         "<h4>安全处理后的内容:</h4>" +
                         "<p>" + safeContent + "</p>" +
                         "<small>处理时间: " + new java.util.Date() + "</small>" +
                         "</div>";

        System.out.println("安全XSS处理 - 原始输入: " + content + ", 转义后: " + safeContent);
        return response;
    }

    /**
     * 清空所有留言
     */
    public void clearMessages() {
        messages.clear();
    }

    /**
     * 获取留言数量
     */
    public int getMessageCount() {
        return messages.size();
    }
}