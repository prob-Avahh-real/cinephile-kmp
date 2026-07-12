// App.kt — 影迷 KMP 入口
package com.cinephile

import androidx.compose.runtime.*
import com.cinephile.data.CinephileHttpClient
import com.cinephile.ui.theme.CinephileTheme
import com.cinephile.ui.screens.CinephileApp

@Composable
fun App() {
    LaunchedEffect(Unit) {
        CinephileHttpClient.shared.warmup()
    }

    CinephileTheme {
        CinephileApp()
    }
}
