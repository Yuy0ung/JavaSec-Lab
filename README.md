# Java Security Lab - Java 漏洞靶场

## 项目简介

Java Security Lab 是一个基于 Spring Boot 和 Ant Design 的 Java 安全漏洞学习平台。该项目集成了常见的 Web 安全漏洞，包括 SQL 注入、XSS、SSRF、XXE 和命令执行漏洞，旨在帮助开发者和安全研究人员学习和理解这些漏洞的原理、利用方式以及防护措施。

## 技术栈

- **后端**: Spring Boot 2.7.0, Spring Data JPA, MyBatis
- **前端**: Ant Design Vue 3.x, Vue.js 3.x
- **数据库**: H2 (内存数据库)
- **构建工具**: Maven

## 快速开始

### 环境要求

- JDK 8 或更高版本
- Maven 3.6 或更高版本

### 启动项目

1. 克隆项目到本地
2. 进入项目目录
3. 运行以下命令启动项目：

```bash
mvn spring-boot:run
```

4. 访问 http://localhost:8080 查看主页面

### 默认用户数据

系统启动时会自动初始化以下测试用户：

| 用户名 | 密码 | 邮箱 | 角色 |
|--------|------|------|------|
| admin | admin123 | admin@example.com | ADMIN |
| user | user123 | user@example.com | USER |
| test | test123 | test@example.com | USER |
| guest | guest123 | guest@example.com | GUEST |

## 漏洞功能详解

### 1. SQL 注入漏洞 (SQL Injection)

#### 功能描述
演示 JDBC 和 MyBatis 两种方式的 SQL 注入漏洞。

#### 测试端点

**JDBC SQL 注入登录**
- **URL**: `POST /sqli/jdbc/login`
- **参数**: `username`, `password`
- **漏洞原理**: 直接拼接 SQL 语句，未使用参数化查询

**测试 Payload**:
```json
{
  "username": "admin' OR '1'='1' --",
  "password": "任意密码"
}
```

**MyBatis SQL 注入搜索**
- **URL**: `POST /sqli/mybatis/search`
- **参数**: `keyword`
- **漏洞原理**: 模拟 MyBatis `${}` 语法的 SQL 注入

**测试 Payload**:
```json
{
  "keyword": "admin' UNION SELECT 'injected','data','test','HACKED' --"
}
```

**安全示例**
- **URL**: `POST /sqli/safe/login`
- **参数**: `username`, `password`
- **防护措施**: 使用参数化查询

#### 防护建议
1. 使用参数化查询或预编译语句
2. 对用户输入进行严格验证和过滤
3. 使用 MyBatis 时避免使用 `${}` 语法，改用 `#{}`
4. 实施最小权限原则

### 2. 跨站脚本攻击 (XSS)

#### 功能描述
演示反射型和存储型 XSS 漏洞。

#### 测试端点

**反射型 XSS**
- **URL**: `POST /xss/reflected`
- **参数**: `input`
- **漏洞原理**: 直接将用户输入返回到页面，未进行 HTML 编码

**测试 Payload**:
```json
{
  "input": "<script>alert('XSS')</script>"
}
```

**存储型 XSS**
- **URL**: `POST /xss/stored`
- **参数**: `message`
- **漏洞原理**: 将用户输入存储到内存中，读取时未进行编码

**测试 Payload**:
```json
{
  "message": "<img src=x onerror=alert('Stored XSS')>"
}
```

**获取存储的消息**
- **URL**: `GET /xss/messages`
- **功能**: 获取所有存储的消息

**安全示例**
- **URL**: `POST /xss/safe`
- **参数**: `content`
- **防护措施**: 对输出进行 HTML 编码

#### 防护建议
1. 对所有用户输入进行 HTML 编码
2. 使用内容安全策略 (CSP)
3. 验证和过滤用户输入
4. 使用安全的模板引擎

### 3. 服务端请求伪造 (SSRF)

#### 功能描述
演示 SSRF 漏洞，允许攻击者从服务器端发起请求。

#### 测试端点

**SSRF 漏洞**
- **URL**: `POST /ssrf/fetch`
- **参数**: `url`
- **漏洞原理**: 直接请求用户提供的 URL，未进行验证

**测试 Payload**:
```json
{
  "url": "http://localhost:8080/ssrf/admin"
}
```

**内部管理页面**
- **URL**: `GET /ssrf/admin`
- **功能**: 模拟内部敏感信息页面

**文件读取测试**:
```json
{
  "url": "file:///etc/passwd"
}
```

**安全示例**
- **URL**: `POST /ssrf/safe-fetch`
- **参数**: `url`
- **防护措施**: URL 白名单验证，禁止内网访问

#### 防护建议
1. 实施 URL 白名单机制
2. 禁止访问内网地址
3. 禁用危险的协议 (如 file://, gopher://)
4. 使用代理服务器进行外部请求

### 4. XML 外部实体注入 (XXE)

#### 功能描述
演示 XXE 漏洞，可能导致文件读取、内网探测等安全问题。

#### 测试端点

**XXE 漏洞**
- **URL**: `POST /xxe/parse`
- **参数**: `xml`
- **漏洞原理**: XML 解析器未禁用外部实体解析

**测试 Payload**:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE root [
  <!ENTITY xxe SYSTEM "file:///etc/passwd">
]>
<root>
  <data>&xxe;</data>
</root>
```

**内网探测 Payload**:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE root [
  <!ENTITY xxe SYSTEM "http://localhost:8080/xxe/sensitive-data">
]>
<root>
  <data>&xxe;</data>
</root>
```

**敏感数据端点**
- **URL**: `GET /xxe/sensitive-data`
- **功能**: 模拟敏感文件内容

**安全示例**
- **URL**: `POST /xxe/safe-parse`
- **参数**: `xml`
- **防护措施**: 禁用外部实体解析

#### 防护建议
1. 禁用 XML 外部实体解析
2. 使用安全的 XML 解析器配置
3. 验证和过滤 XML 输入
4. 使用 JSON 替代 XML (如果可能)

### 5. 命令执行漏洞 (RCE)

#### 功能描述
演示命令执行漏洞，攻击者可以在服务器上执行任意命令。

#### 测试端点

**直接命令执行**
- **URL**: `POST /rce/execute`
- **参数**: `command`
- **漏洞原理**: 直接执行用户输入的命令

**测试 Payload**:
```json
{
  "command": "whoami"
}
```

**危险 Payload**:
```json
{
  "command": "ls -la /etc"
}
```

**文件操作**
- **URL**: `POST /rce/file-operation`
- **参数**: `filename`, `operation`
- **漏洞原理**: 文件操作命令拼接用户输入

**测试 Payload**:
```json
{
  "filename": "/etc/passwd",
  "operation": "read"
}
```

**命令注入 Payload**:
```json
{
  "filename": "/etc/passwd; whoami",
  "operation": "read"
}
```

**网络工具**
- **URL**: `POST /rce/network-tool`
- **参数**: `host`, `tool`
- **漏洞原理**: 网络工具命令拼接用户输入

**测试 Payload**:
```json
{
  "host": "127.0.0.1",
  "tool": "ping"
}
```

**命令注入 Payload**:
```json
{
  "host": "127.0.0.1; cat /etc/passwd",
  "tool": "ping"
}
```

**安全示例**
- **URL**: `POST /rce/safe-execute`
- **参数**: `command`
- **防护措施**: 命令白名单、参数验证、使用 ProcessBuilder

#### 防护建议
1. 避免直接执行用户输入
2. 使用命令白名单机制
3. 对用户输入进行严格验证
4. 使用 ProcessBuilder 替代 Runtime.exec()
5. 实施最小权限原则

## 安全测试建议

### 测试环境
- 仅在隔离的测试环境中使用
- 不要在生产环境中部署此项目
- 建议使用虚拟机或容器进行测试

### 学习路径
1. 理解每种漏洞的原理和危害
2. 尝试不同的攻击 Payload
3. 分析安全防护措施的实现
4. 学习如何在实际项目中应用这些防护措施

### 扩展练习
1. 尝试绕过现有的安全防护措施
2. 研究更复杂的攻击向量
3. 实现额外的安全防护功能
4. 集成安全扫描工具

## 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── com/javasec/
│   │       ├── JavaSecLabApplication.java    # 主启动类
│   │       ├── config/
│   │       │   └── DataInitializer.java      # 数据初始化
│   │       ├── controller/                   # 控制器层
│   │       │   ├── HomeController.java
│   │       │   ├── SqlInjectionController.java
│   │       │   ├── XssController.java
│   │       │   ├── SsrfController.java
│   │       │   ├── XxeController.java
│   │       │   └── RceController.java
│   │       ├── service/                      # 服务层
│   │       │   ├── SqlInjectionService.java
│   │       │   ├── XssService.java
│   │       │   ├── SsrfService.java
│   │       │   ├── XxeService.java
│   │       │   └── RceService.java
│   │       ├── entity/                       # 实体类
│   │       │   └── User.java
│   │       └── repository/                   # 数据访问层
│   │           └── UserRepository.java
│   └── resources/
│       ├── application.yml                   # 配置文件
│       └── templates/
│           └── index.html                    # 主页面
```

## 注意事项

⚠️ **安全警告**
- 此项目仅用于教育和学习目的
- 包含真实的安全漏洞，请勿在生产环境使用
- 请在隔离的测试环境中运行
- 不要将此项目暴露在公网上

## 贡献指南

欢迎提交 Issue 和 Pull Request 来改进这个项目。在贡献代码时，请确保：
1. 遵循现有的代码风格
2. 添加适当的注释和文档
3. 确保新功能有相应的安全说明

## 许可证

本项目采用 MIT 许可证，详情请参阅 LICENSE 文件。

## 联系方式

如有问题或建议，请通过 GitHub Issues 联系我们。