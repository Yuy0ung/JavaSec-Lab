package com.javasec.service;

import com.javasec.entity.User;
import com.javasec.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * SQL注入漏洞服务类
 */
@Service
public class SqlInjectionService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserRepository userRepository;

    /**
     * JDBC方式的SQL注入漏洞 - 直接拼接SQL
     */
    public String loginWithJdbc(String username, String password) {
        try (Connection conn = dataSource.getConnection()) {
            // 危险的SQL拼接 - 存在SQL注入漏洞
            String sql = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
            
            System.out.println("执行的SQL: " + sql);
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            StringBuilder result = new StringBuilder();
            result.append("执行的SQL: ").append(sql).append("\n\n");
            result.append("查询结果:\n");
            
            boolean hasResult = false;
            while (rs.next()) {
                hasResult = true;
                result.append("用户ID: ").append(rs.getLong("id")).append("\n");
                result.append("用户名: ").append(rs.getString("username")).append("\n");
                result.append("密码: ").append(rs.getString("password")).append("\n");
                result.append("邮箱: ").append(rs.getString("email")).append("\n");
                result.append("角色: ").append(rs.getString("role")).append("\n");
                result.append("------------------------\n");
            }
            
            if (hasResult) {
                return "登录成功！\n\n" + result.toString();
            } else {
                return "登录失败！用户名或密码错误。\n\n" + result.toString();
            }
            
        } catch (Exception e) {
            return "数据库错误: " + e.getMessage();
        }
    }

    /**
     * MyBatis方式的SQL注入漏洞 - 使用${}而不是#{}
     */
    public String searchUsersWithMyBatis(String keyword) {
        try (Connection conn = dataSource.getConnection()) {
            // 模拟MyBatis的${}方式 - 存在SQL注入漏洞
            String sql = "SELECT * FROM users WHERE username LIKE '%" + keyword + "%' OR email LIKE '%" + keyword + "%'";
            
            System.out.println("执行的SQL: " + sql);
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            StringBuilder result = new StringBuilder();
            result.append("执行的SQL: ").append(sql).append("\n\n");
            result.append("搜索结果:\n");
            
            boolean hasResult = false;
            while (rs.next()) {
                hasResult = true;
                result.append("用户ID: ").append(rs.getLong("id")).append("\n");
                result.append("用户名: ").append(rs.getString("username")).append("\n");
                result.append("邮箱: ").append(rs.getString("email")).append("\n");
                result.append("角色: ").append(rs.getString("role")).append("\n");
                result.append("------------------------\n");
            }
            
            if (!hasResult) {
                result.append("未找到匹配的用户\n");
            }
            
            return result.toString();
            
        } catch (Exception e) {
            return "搜索失败: " + e.getMessage();
        }
    }

    /**
     * 安全的参数化查询示例
     */
    public String safeLogin(String username, String password) {
        try {
            User user = userRepository.findByUsernameAndPassword(username, password);
            if (user != null) {
                return "安全登录成功！欢迎 " + user.getUsername();
            } else {
                return "安全登录失败！用户名或密码错误。";
            }
        } catch (Exception e) {
            return "登录失败: " + e.getMessage();
        }
    }
}