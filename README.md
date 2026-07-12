# 光影志 (Cinephile)

影迷电影应用 — 发现、评分、收藏影片。

## 技术架构

```
cinephile-kmp/
├── composeApp/              # Compose Multiplatform UI
│   └── src/commonMain/      # 跨平台 UI (Compose)
│       └── kotlin/com/cinephile/ui/
│           ├── screens/     # Auth, 首页, 详情
│           └── theme/       # Material3 主题
├── shared/                  # KMP 共享模块
│   └── src/commonMain/      # 业务逻辑
│       └── kotlin/com/cinephile/
│           ├── model/       # Movie, Auth 模型
│           └── data/        # 仓库层 + HTTP 客户端
├── iosApp/                  # iOS 原生入口
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## 构建命令

```bash
./gradlew build                # 构建全部
./gradlew :shared:build        # 共享模块
./gradlew :composeApp:build    # UI 模块
```

## 测试命令

```bash
./gradlew :shared:check        # 共享模块测试
./gradlew :composeApp:check    # UI 模块测试
./gradlew allTests             # 全平台测试
```
