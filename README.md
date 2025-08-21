# 認證伺服器 (Auth Server)

這是一個基於 Spring Boot OAuth2 Authorization Server 的認證伺服器，提供安全的身份驗證和授權服務。

## 功能特色

- 🔐 OAuth2 和 OpenID Connect 支援
- 🎨 現代化的 UI 設計，參考 auth-client 風格
- 🔒 安全的表單登入
- 📱 響應式設計，支援各種設備
- 🚀 基於 Spring Boot 3.5.4

## 快速開始

### 1. 啟動認證伺服器

```bash
# 在 auth-server 目錄下
mvn spring-boot:run
```

認證伺服器將在 `http://localhost:9000` 啟動。

### 2. 啟動客戶端應用

```bash
# 在 auth-client 目錄下
mvn spring-boot:run
```

客戶端應用將在 `http://localhost:8080` 啟動。

## 預設帳戶

- **使用者名稱**: `user`
- **密碼**: `password`

## 主要端點

- **登入頁面**: `http://localhost:9000/login`
- **OAuth2 授權端點**: `http://localhost:9000/oauth2/authorize`
- **OAuth2 Token 端點**: `http://localhost:9000/oauth2/token`

## 使用流程

1. 直接訪問客戶端應用 `http://localhost:8080/`
2. 在客戶端點擊「登入」按鈕
3. 系統會重定向到認證伺服器的登入頁面
4. 使用預設帳戶登入
5. 授權後會重定向回客戶端

## 技術棧

- **後端**: Spring Boot 3.5.4
- **安全**: Spring Security + OAuth2 Authorization Server
- **前端**: Thymeleaf + Bootstrap 5 + Font Awesome
- **資料庫**: H2 (記憶體資料庫)
- **Java 版本**: 17

## 專案結構

```
auth-server/
├── src/main/java/com/niqdev/authserver/
│   ├── config/          # 配置類別
│   ├── controller/      # 控制器
│   ├── entity/          # 實體類別
│   ├── repository/      # 資料存取層
│   └── service/         # 服務層
├── src/main/resources/
│   ├── templates/       # Thymeleaf 模板 (登入頁面)
│   ├── static/          # 靜態資源 (CSS 樣式)
│   └── application.yml  # 應用配置
└── pom.xml
```

## 開發說明

### 添加新的依賴

如果需要添加新的功能，請在 `pom.xml` 中添加相應的依賴。

### 自定義樣式

樣式文件位於 `src/main/resources/static/css/style.css`，可以根據需要進行修改。

### 配置修改

主要配置在 `src/main/resources/application.yml` 中，包括：
- 伺服器端口
- 資料庫配置
- 日誌級別

## 故障排除

### 常見問題

1. **端口衝突**: 確保 9000 端口未被其他應用佔用
2. **編譯錯誤**: 確保使用 Java 17 或更高版本
3. **登入失敗**: 檢查預設帳戶是否正確

### 日誌查看

應用程式會輸出詳細的日誌，可以通過以下方式查看：
- 控制台輸出
- 日誌文件（如果配置了）

## 授權

本專案僅供學習和開發使用。
