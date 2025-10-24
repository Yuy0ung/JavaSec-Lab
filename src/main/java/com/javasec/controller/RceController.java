package com.javasec.controller;

import com.javasec.service.RceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 命令执行漏洞控制器
 */
@RestController
@RequestMapping("/rce")
public class RceController {

    @Autowired
    private RceService rceService;

    /**
     * 命令执行漏洞 - 直接执行用户输入的命令
     */
    @PostMapping("/execute")
    public String executeCommand(@RequestBody Map<String, String> params) {
        String command = params.get("command");
        return rceService.executeCommandUnsafe(command);
    }

    /**
     * 文件操作 - 存在命令注入风险
     */
    @PostMapping("/file-operation")
    public String fileOperation(@RequestBody Map<String, String> params) {
        String filename = params.get("filename");
        String operation = params.get("operation"); // list, read, delete
        return rceService.fileOperation(filename, operation);
    }

    /**
     * 网络工具 - 存在命令注入风险
     */
    @PostMapping("/network-tool")
    public String networkTool(@RequestBody Map<String, String> params) {
        String host = params.get("host");
        String tool = params.get("tool"); // ping, nslookup, curl
        return rceService.networkTool(host, tool);
    }

    /**
     * 安全的命令执行示例
     */
    @PostMapping("/safe-execute")
    public String safeExecuteCommand(@RequestBody Map<String, String> params) {
        String command = params.get("command");
        return rceService.executeCommandSafe(command);
    }
}