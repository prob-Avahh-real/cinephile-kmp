# Cinephile (光影志) — KMP 项目

## 项目概述
影迷电影应用，Kotlin Multiplatform + Compose Multiplatform。

## 构建命令
- 全量构建: `./gradlew build`
- 共享模块: `./gradlew :shared:build`
- UI 模块: `./gradlew :composeApp:build`
- 全平台测试: `./gradlew allTests`
- 清理: `./gradlew clean`

## 文件组织
```
composeApp/src/commonMain/  → Compose UI (screens, theme)
shared/src/commonMain/      → 共享业务逻辑 (model, data)
iosApp/                     → iOS 原生入口
```

## 关键配置
- 项目名: `Cinephile` (settings.gradle.kts)
- 模块: `:composeApp`, `:shared`
- 序列化: kotlinx.serialization
- HTTP: Ktor Client
- UI: Compose Multiplatform + Material3
- 数据源: Supabase (PostgREST API)
