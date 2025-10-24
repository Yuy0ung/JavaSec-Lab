package com.javasec.config;

import com.javasec.entity.User;
import com.javasec.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // 初始化测试用户数据
        if (userRepository.count() == 0) {
            userRepository.save(new User("admin", "admin123", "admin@javasec.com", "admin"));
            userRepository.save(new User("user", "user123", "user@javasec.com", "user"));
            userRepository.save(new User("test", "test123", "test@javasec.com", "user"));
            userRepository.save(new User("guest", "guest123", "guest@javasec.com", "guest"));
            
            System.out.println("初始化用户数据完成！");
            System.out.println("测试账号: admin/admin123, user/user123, test/test123, guest/guest123");
        }
    }
}