import SwiftUI
import shared  // KMP shared framework

/// iOS App 入口 — SwiftUI 原生壳，嵌入 KMP Compose UI
///
/// 性能优化:
///   - URLSession 默认 HTTP/2 多路复用 + 连接池
///   - Network.framework 自动管理连接生命周期
///   - Ktor Darwin 引擎共享 shared 模块的连接池

@main
struct CinephileApp: App {
    init() {
        // 启动时预热 API 连接
        Task {
            do {
                try await shared.CinephileHttpClient.shared.warmup()
                print("[Cinephile] 预热完成")
            } catch {
                print("[Cinephile] 预热失败 (可忽略): \(error)")
            }
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

/// SwiftUI ContentView — 嵌入 KMP Compose UI
struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all)
    }
}

/// Compose UI 桥接
struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
