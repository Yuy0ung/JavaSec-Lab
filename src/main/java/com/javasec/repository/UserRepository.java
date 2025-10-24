package com.javasec.repository;

import com.javasec.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户数据访问层
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查找用户（安全方式）
     */
    User findByUsername(String username);
    
    /**
     * 根据用户名和密码查找用户（安全方式）
     */
    User findByUsernameAndPassword(String username, String password);
    
    /**
     * 根据邮箱查找用户
     */
    User findByEmail(String email);
    
    /**
     * 根据角色查找用户列表
     */
    List<User> findByRole(String role);
    
    /**
     * 原生SQL查询 - 用于演示SQL注入漏洞（不安全）
     */
    @Query(value = "SELECT * FROM users WHERE username = ?1 AND password = ?2", nativeQuery = true)
    List<User> findByUsernameAndPasswordUnsafe(String username, String password);
}