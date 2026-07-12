// CinephileApp.kt — 影迷主导航 + 首页
package com.cinephile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.cinephile.data.CinephileHttpClient
import com.cinephile.data.MovieRepository
import com.cinephile.model.FeaturedResponse
import com.cinephile.model.Movie
import com.cinephile.ui.theme.CinephileRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class BottomTab(val label: String, val route: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    Home("首页", "/home", Icons.Filled.Home, Icons.Outlined.Home),
    Discover("发现", "/discover", Icons.Filled.Explore, Icons.Outlined.Explore),
    Theater("放映厅", "/theater", Icons.Filled.PlayCircle, Icons.Outlined.PlayCircle),
    Community("社区", "/community", Icons.Filled.ChatBubble, Icons.Outlined.ChatBubble),
    Profile("我的", "/profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun CinephileApp() {
    var selectedTab by remember { mutableStateOf(BottomTab.Home) }

    Scaffold(
        topBar = {
            if (selectedTab == BottomTab.Home) {
                TopAppBar(
                    title = { Text("影迷", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                    actions = {
                        IconButton(onClick = {}) { Icon(Icons.Filled.Search, "搜索") }
                        IconButton(onClick = {}) { Icon(Icons.Outlined.Notifications, "通知") }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            } else {
                TopAppBar(
                    title = { Text(selectedTab.label) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            NavigationBar {
                BottomTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(if (selectedTab == tab) tab.selectedIcon else tab.unselectedIcon, tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (selectedTab) {
                BottomTab.Home -> HomeScreen()
                else -> PlaceholderScreen(selectedTab.label, selectedTab.selectedIcon)
            }
        }
    }
}

// ── 首页 ──────────────────────────────────────────────

@Composable
fun HomeScreen() {
    var featured by remember { mutableStateOf<FeaturedResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val repo = remember { MovieRepository() }

    fun load() {
        error = null
        isLoading = true
        val scope = rememberCoroutineScope()
        scope.launch {
            try {
                featured = repo.getFeatured()
                isLoading = false
            } catch (e: Exception) {
                error = e.message ?: "加载失败"
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { load() }

    when {
        isLoading -> HomeShimmer()
        error != null -> ErrorView(error!!, onRetry = { load() })
        featured != null -> {
            val data = featured!!
            Column(Modifier.verticalScroll(rememberScrollState())) {
                // Banner 轮播
                if (data.banners.isNotEmpty()) {
                    BannerCarousel(data.banners)
                }

                // 热映中
                if (data.hot.isNotEmpty()) {
                    SectionHeader("热映中", "全部 ›")
                    HorizontalMovieList(data.hot)
                }

                // 即将上映
                if (data.upcoming.isNotEmpty()) {
                    SectionHeader("即将上映", "全部 ›")
                    HorizontalMovieList(data.upcoming, showDate = true)
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

// ── Banner 轮播 ───────────────────────────────────────

@Composable
fun BannerCarousel(banners: List<Movie>) {
    val pageCount = banners.size
    var currentPage by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    // 自动轮播
    LaunchedEffect(pageCount) {
        if (pageCount > 1) {
            while (true) {
                delay(4000)
                currentPage = (currentPage + 1) % pageCount
            }
        }
    }

    val pagerState = rememberPagerState(pageCount = { pageCount })

    LaunchedEffect(currentPage) {
        if (pagerState.currentPage != currentPage) {
            pagerState.animateScrollToPage(currentPage)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        currentPage = pagerState.currentPage
    }

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.height(180.dp)
        ) { page ->
            val movie = banners[page]
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                AsyncImage(
                    model = movie.backdropUrl.orEmpty(),
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // 渐变遮罩
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                            )
                        )
                )

                // 影片信息
                Column(
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(movie.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    movie.ratingAvg?.let { rating ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("★", color = Color(0xFFFFC107), fontSize = 14.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(rating.toString(), color = Color(0xFFFFC107), fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // 指示器
        if (pageCount > 1) {
            Row(
                Modifier.padding(top = 8.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pageCount) { i ->
                    Box(
                        Modifier
                            .padding(horizontal = 3.dp)
                            .width(if (currentPage == i) 16.dp else 6.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (currentPage == i) MaterialTheme.colorScheme.primary
                                else Color.Gray.copy(alpha = 0.5f)
                            )
                    )
                }
            }
        }
    }
}

// ── 分段标题 ───────────────────────────────────────────

@Composable
fun SectionHeader(title: String, trailing: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Text(trailing, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
    }
}

// ── 水平电影列表 ───────────────────────────────────────

@Composable
fun HorizontalMovieList(movies: List<Movie>, showDate: Boolean = false) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(movies) { _, movie ->
            MovieCard(movie, showDate)
        }
    }
}

// ── 电影卡片 ───────────────────────────────────────────

@Composable
fun MovieCard(movie: Movie, showDate: Boolean = false) {
    Card(
        modifier = Modifier.width(120.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Box(
                Modifier
                    .width(120.dp)
                    .height(160.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray.copy(alpha = 0.2f))
            ) {
                AsyncImage(
                    model = movie.posterUrl.orEmpty(),
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                movie.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 13.sp
            )

            if (showDate && movie.releaseDate != null) {
                Text(
                    movie.releaseDate!!,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            if (movie.ratingAvg != null && !showDate) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("★", color = Color(0xFFFFC107), fontSize = 12.sp)
                    Spacer(Modifier.width(2.dp))
                    Text(
                        movie.ratingAvg.toString(),
                        fontSize = 11.sp,
                        color = Color(0xFFFFC107)
                    )
                }
            }
        }
    }
}

// ── 骨架屏 ─────────────────────────────────────────────

@Composable
fun HomeShimmer() {
    Column {
        Box(
            Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Gray.copy(alpha = 0.15f))
        )

        Spacer(Modifier.height(28.dp))

        Box(
            Modifier
                .padding(horizontal = 16.dp)
                .height(16.dp)
                .width(80.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Gray.copy(alpha = 0.15f))
        )

        Spacer(Modifier.height(8.dp))

        LazyRow(contentPadding = PaddingValues(horizontal = 12.dp)) {
            items(6) {
                Box(
                    Modifier
                        .padding(horizontal = 5.dp)
                        .width(120.dp)
                        .height(160.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray.copy(alpha = 0.15f))
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Box(
            Modifier
                .padding(horizontal = 16.dp)
                .height(16.dp)
                .width(100.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Gray.copy(alpha = 0.15f))
        )

        Spacer(Modifier.height(8.dp))

        LazyRow(contentPadding = PaddingValues(horizontal = 12.dp)) {
            items(6) {
                Box(
                    Modifier
                        .padding(horizontal = 5.dp)
                        .width(120.dp)
                        .height(160.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray.copy(alpha = 0.15f))
                )
            }
        }
    }
}

// ── 错误视图 ───────────────────────────────────────────

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.CloudOff, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
            Spacer(Modifier.height(12.dp))
            Text("加载失败", color = Color.Gray)
            Text(message, color = Color.Gray.copy(alpha = 0.6f), fontSize = 13.sp)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRetry) { Text("重试") }
        }
    }
}

// ── 占位页面 ───────────────────────────────────────────

@Composable
fun PlaceholderScreen(title: String, icon: ImageVector) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
            Spacer(Modifier.height(16.dp))
            Text(title, style = MaterialTheme.typography.headlineMedium)
        }
    }
}
