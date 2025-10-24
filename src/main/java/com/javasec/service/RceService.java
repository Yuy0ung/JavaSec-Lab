package com.javasec.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 命令执行漏洞服务
 */
@Service
public class RceService {

    private static final Set<String> ALLOWED_COMMANDS = new HashSet<>(Arrays.asList(
            "ls", "pwd", "whoami", "date", "echo"
    ));

    /**
     * 不安全的命令执行 - 直接执行用户输入
     */
    public String executeCommandUnsafe(String command) {
        if (command == null || command.trim().isEmpty()) {
            return "错误：命令不能为空";
        }

        try {
            StringBuilder result = new StringBuilder();
            result.append("执行命令: ").append(command).append("\n");
            result.append("执行结果:\n");

            // 危险：直接执行用户输入的命令
            Process process = Runtime.getRuntime().exec(command);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            
            while ((line = errorReader.readLine()) != null) {
                result.append("错误: ").append(line).append("\n");
            }
            
            int exitCode = process.waitFor();
            result.append("退出代码: ").append(exitCode);
            
            return result.toString();
        } catch (Exception e) {
            return "命令执行失败: " + e.getMessage();
        }
    }

    /**
     * 文件操作 - 存在命令注入风险
     */
    public String fileOperation(String filename, String operation) {
        if (filename == null || filename.trim().isEmpty()) {
            return "错误：文件名不能为空";
        }
        
        if (operation == null || operation.trim().isEmpty()) {
            return "错误：操作类型不能为空";
        }

        try {
            String command;
            switch (operation.toLowerCase()) {
                case "list":
                    // 危险：直接拼接用户输入到命令中
                    command = "ls -la " + filename;
                    break;
                case "read":
                    // 危险：可能读取敏感文件
                    command = "cat " + filename;
                    break;
                case "delete":
                    // 危险：可能删除重要文件
                    command = "rm " + filename;
                    break;
                default:
                    return "不支持的操作类型: " + operation;
            }

            return executeCommand(command);
        } catch (Exception e) {
            return "文件操作失败: " + e.getMessage();
        }
    }

    /**
     * 网络工具 - 存在命令注入风险
     */
    public String networkTool(String host, String tool) {
        if (host == null || host.trim().isEmpty()) {
            return "错误：主机地址不能为空";
        }
        
        if (tool == null || tool.trim().isEmpty()) {
            return "错误：工具类型不能为空";
        }

        try {
            String command;
            switch (tool.toLowerCase()) {
                case "ping":
                    // 危险：可能被注入其他命令
                    command = "ping -c 4 " + host;
                    break;
                case "nslookup":
                    command = "nslookup " + host;
                    break;
                case "curl":
                    command = "curl -I " + host;
                    break;
                default:
                    return "不支持的网络工具: " + tool;
            }

            return executeCommand(command);
        } catch (Exception e) {
            return "网络工具执行失败: " + e.getMessage();
        }
    }

    /**
     * 安全的命令执行示例
     */
    public String executeCommandSafe(String command) {
        if (command == null || command.trim().isEmpty()) {
            return "错误：命令不能为空";
        }

        // 安全措施1：命令白名单
        String[] parts = command.trim().split("\\s+");
        String baseCommand = parts[0];
        
        if (!ALLOWED_COMMANDS.contains(baseCommand)) {
            return "错误：不允许执行的命令: " + baseCommand + 
                   "\n允许的命令: " + String.join(", ", ALLOWED_COMMANDS);
        }

        // 安全措施2：参数验证
        for (String part : parts) {
            if (part.contains(";") || part.contains("&") || part.contains("|") || 
                part.contains("`") || part.contains("$") || part.contains(">") || 
                part.contains("<")) {
                return "错误：检测到危险字符，命令被拒绝";
            }
        }

        try {
            // 安全措施3：使用ProcessBuilder而不是Runtime.exec()
            ProcessBuilder pb = new ProcessBuilder(parts);
            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            
            StringBuilder result = new StringBuilder();
            result.append("安全执行命令: ").append(command).append("\n");
            result.append("执行结果:\n");
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            
            int exitCode = process.waitFor();
            result.append("退出代码: ").append(exitCode);
            
            return result.toString();
        } catch (Exception e) {
            return "安全命令执行失败: " + e.getMessage();
        }
    }

    /**
     * 执行命令的辅助方法
     */
    private String executeCommand(String command) throws IOException, InterruptedException {
        StringBuilder result = new StringBuilder();
        result.append("执行命令: ").append(command).append("\n");
        result.append("执行结果:\n");

        Process process = Runtime.getRuntime().exec(command);
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line).append("\n");
        }
        
        while ((line = errorReader.readLine()) != null) {
            result.append("错误: ").append(line).append("\n");
        }
        
        int exitCode = process.waitFor();
        result.append("退出代码: ").append(exitCode);
        
        return result.toString();
    }
}