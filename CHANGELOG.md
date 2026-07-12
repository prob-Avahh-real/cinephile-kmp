# CHANGELOG

## [1.0.0] — 2025-06-07

### Added
- Initial KMP project with shared business logic module
- Compose Multiplatform UI with Material3 dark theme
- Movie data models: Movie, MovieDetail, FeaturedResponse, PaginatedMovies
- Auth models: AuthUser, AuthResponse
- CinephileHttpClient with Ktor (HTTP/2, Gzip/Brotli, timeout, retry)
- MovieRepository and AuthRepository for API interaction
- Authentication screens: LoginScreen, RegisterScreen
- HomeScreen with banner carousel, horizontal movie lists, shimmer loading
- Bottom navigation with 5 tabs (Home, Discover, Theater, Community, Profile)
- CI/CD pipeline for Android and iOS builds
- Coil3 for async image loading with caching

### Fixed
- HomeScreen: `rememberCoroutineScope()` call scope bug — moved `LaunchedEffect` after `load()` function definition
- APP.md corrected from generic "LaGe" template to describe cinephile-kmp project

### Security
- INTERNET and ACCESS_NETWORK_STATE permissions declared
- DefaultRequest URL uses 10.0.2.2 (emulator localhost), needs config for production
- Token stored in memory (not persistent), cleared on logout
