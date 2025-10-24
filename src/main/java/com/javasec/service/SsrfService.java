package com.javasec.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * SSRF漏洞服务类
 */
@Service
public class SsrfService {

    /**
     * SSRF漏洞 - 直接请求用户提供的URL
     */
    public String fetchUrlContent(String url) {
        if (url == null || url.trim().isEmpty()) {
            return "请提供URL";
        }

        try {
            System.out.println("SSRF请求URL: " + url);

            // 危险：直接请求用户提供的URL，未进行任何验证
            if (url.startsWith("file://")) {
                return handleFileProtocol(url);
            } else if (url.startsWith("http://") || url.startsWith("https://")) {
                return handleHttpProtocol(url);
            } else {
                // 尝试添加http://前缀
                return handleHttpProtocol("http://" + url);
            }

        } catch (Exception e) {
            return "请求失败: " + e.getMessage();
        }
    }

    /**
     * 处理HTTP协议请求
     */
    private String handleHttpProtocol(String url) {
        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            
            // 设置超时时间
            httpGet.setHeader("User-Agent", "JavaSec-Lab/1.0");
            
            HttpResponse response = httpClient.execute(httpGet);
            String content = EntityUtils.toString(response.getEntity(), "UTF-8");
            
            StringBuilder result = new StringBuilder();
            result.append("请求URL: ").append(url).append("\n");
            result.append("响应状态: ").append(response.getStatusLine().getStatusCode()).append("\n");
            result.append("响应内容:\n");
            result.append("----------------------------------------\n");
            result.append(content.length() > 2000 ? content.substring(0, 2000) + "\n...(内容过长，已截断)" : content);
            
            return result.toString();
            
        } catch (Exception e) {
            return "HTTP请求失败: " + e.getMessage();
        }
    }

    /**
     * 处理文件协议请求 - 危险的文件读取
     */
    private String handleFileProtocol(String url) {
        try {
            URL fileUrl = new URL(url);
            String filePath = fileUrl.getPath();
            
            System.out.println("尝试读取文件: " + filePath);
            
            StringBuilder result = new StringBuilder();
            result.append("文件路径: ").append(filePath).append("\n");
            result.append("文件内容:\n");
            result.append("----------------------------------------\n");
            
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                int lineCount = 0;
                while ((line = reader.readLine()) != null && lineCount < 50) {
                    result.append(line).append("\n");
                    lineCount++;
                }
                if (lineCount >= 50) {
                    result.append("...(文件过长，已截断)");
                }
            }
            
            return result.toString();
            
        } catch (Exception e) {
            return "文件读取失败: " + e.getMessage();
        }
    }

    /**
     * 安全的URL获取 - 包含白名单验证
     */
    public String safeFetchUrlContent(String url) {
        if (url == null || url.trim().isEmpty()) {
            return "请提供URL";
        }

        try {
            // 安全检查：URL白名单
            List<String> allowedDomains = Arrays.asList(
                "httpbin.org",
                "jsonplaceholder.typicode.com",
                "api.github.com"
            );

            URL urlObj = new URL(url);
            String host = urlObj.getHost();

            // 检查是否在白名单中
            boolean isAllowed = false;
            for (String domain : allowedDomains) {
                if (host.equals(domain) || host.endsWith("." + domain)) {
                    isAllowed = true;
                    break;
                }
            }

            if (!isAllowed) {
                return "安全检查失败: 不允许访问域名 " + host + "\n允许的域名: " + String.join(", ", allowedDomains);
            }

            // 禁止文件协议
            if (url.startsWith("file://")) {
                return "安全检查失败: 不允许使用file://协议";
            }

            // 禁止内网地址
            if (isInternalAddress(host)) {
                return "安全检查失败: 不允许访问内网地址";
            }

            return handleHttpProtocol(url);

        } catch (Exception e) {
            return "安全请求失败: " + e.getMessage();
        }
    }

    /**
     * 检查是否为内网地址
     */
    private boolean isInternalAddress(String host) {
        return host.equals("localhost") ||
               host.equals("127.0.0.1") ||
               host.startsWith("192.168.") ||
               host.startsWith("10.") ||
               host.startsWith("172.16.") ||
               host.startsWith("172.17.") ||
               host.startsWith("172.18.") ||
               host.startsWith("172.19.") ||
               host.startsWith("172.20.") ||
               host.startsWith("172.21.") ||
               host.startsWith("172.22.") ||
               host.startsWith("172.23.") ||
               host.startsWith("172.24.") ||
               host.startsWith("172.25.") ||
               host.startsWith("172.26.") ||
               host.startsWith("172.27.") ||
               host.startsWith("172.28.") ||
               host.startsWith("172.29.") ||
               host.startsWith("172.30.") ||
               host.startsWith("172.31.");
    }
}