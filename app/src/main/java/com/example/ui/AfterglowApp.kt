package com.example.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.R
import com.example.auth.AuthScreen
import com.example.auth.signOut
import com.example.data.firebase.AfterglowRepository
import com.example.data.firebase.CloudBackupStore
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.data.model.UiState
import com.example.data.model.UserProfile
import com.example.ui.components.AddCategoryBottomSheet
import com.example.ui.components.AfterglowBackdrop
import com.example.ui.components.AfterglowTopBar
import com.example.ui.components.LiquidNavTab
import com.example.ui.components.MediaEditorDialog
import com.example.ui.components.NeonGlassCard
import com.example.ui.components.NeonGlassDialog
import com.example.ui.components.ZenixBottomBar
import com.example.ui.components.ZenixErrorState
import com.example.ui.components.ZenixHomeSkeleton
import com.example.ui.components.ZenixOfflineBanner
import com.example.ui.components.neonGlow
import com.example.ui.components.rememberFloatingBottomBarClearance
import com.example.ui.components.rememberIsOnline
import com.example.ui.components.rememberSafeHazeState
import com.example.ui.components.safeHazeSource
import com.example.ui.components.zenixFocusRing
import com.example.ui.screens.APP_VERSION
import com.example.ui.screens.AppearanceScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.GlobalSearchScreen
import com.example.ui.screens.LibraryMode
import com.example.ui.screens.MediaDetailScreen
import com.example.ui.screens.MediaLibraryScreen
import com.example.ui.screens.RankingsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SmartHubScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.screens.UpdatesScreen
import com.example.ui.screens.WatchlistScreen
import com.example.ui.screens.YearInReviewScreen
import com.example.ui.screens.ZenixUpdateConfig
import com.example.ui.theme.AfterglowTheme
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.LocalReducedMotion
import com.example.ui.theme.LocalZenixRadii
import com.example.ui.theme.LocalZenixSpacing
import com.example.ui.theme.LocalZenixTokens
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.ThemePreferences
import com.example.ui.theme.ZenixAccentColor
import com.example.ui.viewmodel.AfterglowViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AfterglowSection(val label: String, val icon: ImageVector) {
    DASHBOARD("Home", Icons.Rounded.Home),
    MOVIES("Movies", Icons.Rounded.Movie),
    WEB_SERIES("Series", Icons.Rounded.Tv),
    ANIME("Anime", Icons.Rounded.AutoAwesome),
    K_DRAMA("K-Drama", Icons.Rounded.Favorite),
    WATCHLIST("Watchlist", Icons.Rounded.Bookmark),
    RANKINGS("Favourites & Rankings", Icons.Rounded.EmojiEvents),
    STATISTICS("Statistics", Icons.Rounded.BarChart),
    SMART_HUB("Smart Discovery", Icons.Rounded.Casino),
    UPDATES("Updates", Icons.Rounded.SystemUpdate),
    SETTINGS("Settings & Data", Icons.Rounded.Settings)
}

@Composable
fun AfterglowApp() {
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    val view = LocalView.current

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose { Firebase.auth.removeAuthStateListener(listener) }
    }

    val user = currentUser
    if (user == null) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
        AfterglowTheme(themeMode = AppThemeMode.NEON_GLOSSY) {
            AuthScreen(onAuthSuccess = { currentUser = Firebase.auth.currentUser })
        }
    } else {
        key(user.uid) {
            AuthenticatedAfterglowShell(
                currentUserId = user.uid,
                userEmail = user.email,
                defaultDisplayName = user.displayName,
                onSignedOut = { currentUser = null }
            )
        }
    }
}

@Composable
private fun AuthenticatedAfterglowShell(
    currentUserId: String,
    userEmail: String?,
    defaultDisplayName: String?,
    onSignedOut: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val themeMode by ThemePreferences.observeTheme(context)
        .collectAsStateWithLifecycle(initialValue = AppThemeMode.NEON_GLOSSY)
    val accentColor by ThemePreferences.observeAccentColor(context)
        .collectAsStateWithLifecycle(initialValue = ZenixAccentColor.PURPLE)
    val notifyUpdates by ThemePreferences.observeNotifyUpdates(context)
        .collectAsStateWithLifecycle(initialValue = true)
    val lastSeenVersion by ThemePreferences.observeLastSeenVersion(context)
        .collectAsStateWithLifecycle(initialValue = null)
    val lastLaunchedVersion by ThemePreferences.observeLastLaunchedVersion(context)
        .collectAsStateWithLifecycle(initialValue = null)

    val isUpdateAvailable = remember { ZenixUpdateConfig.isUpdateAvailable(APP_VERSION) }
    val showUpdateBadge = remember(lastSeenVersion) {
        ZenixUpdateConfig.shouldShowUpdateBadge(lastSeenVersion, APP_VERSION)
    }

    SideEffect {
        val window = (view.context as? Activity)?.window
        if (window != null) {
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false
        }
    }

    val viewModel: AfterglowViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                AfterglowViewModel(
                    repository = AfterglowRepository(db),
                    currentUserId = currentUserId,
                    defaultDisplayName = defaultDisplayName,
                    cloudBackupStore = CloudBackupStore(app)
                )
            }
        }
    )

    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val mediaItemsState by viewModel.mediaItemsState.collectAsStateWithLifecycle()
    val quotesState by viewModel.quotesState.collectAsStateWithLifecycle()
    val stats by viewModel.dashboardStats.collectAsStateWithLifecycle()
    val achievements by viewModel.achievements.collectAsStateWithLifecycle()
    val cloudSyncState by viewModel.cloudSyncUiState.collectAsStateWithLifecycle()

    val profile = (profileState as? UiState.Success)?.data ?: UserProfile(
        userId = currentUserId,
        displayName = defaultDisplayName?.takeIf { it.isNotBlank() } ?: "Cinephile"
    )
    val allItems = (mediaItemsState as? UiState.Success)?.data ?: emptyList()
    val quotes = (quotesState as? UiState.Success)?.data ?: emptyList()

    var currentSection by remember { mutableStateOf(AfterglowSection.DASHBOARD) }
    var showAppearanceScreen by remember { mutableStateOf(false) }
    var smartHubTab by remember { mutableIntStateOf(0) }
    var showGlobalSearch by remember { mutableStateOf(false) }
    var globalSearchInitialCategory by remember { mutableStateOf<MediaCategory?>(null) }
    var globalSearchInitialWatchlistOnly by remember { mutableStateOf(false) }
    var showYearInReview by remember { mutableStateOf(false) }
    var showWhatsNewDialog by remember { mutableStateOf(false) }

    var showAddSheet by remember { mutableStateOf(false) }
    var addSheetPreselected by remember { mutableStateOf<MediaCategory?>(null) }
    var addSheetWatchlist by remember { mutableStateOf(false) }

    var editorItem by remember { mutableStateOf<MediaItem?>(null) }
    var editorDefaultCategory by remember { mutableStateOf(MediaCategory.MOVIE) }
    var editorDefaultWatchlist by remember { mutableStateOf(false) }
    var showEditor by remember { mutableStateOf(false) }

    var detailItemId by remember { mutableStateOf<String?>(null) }
    val detailItem = remember(allItems, detailItemId) {
        detailItemId?.let { id -> allItems.firstOrNull { it.id == id } }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    var showSettingsMenu by remember { mutableStateOf(false) }
    var showSignOutConfirm by remember { mutableStateOf(false) }
    val settingsButtonFocusRequester = remember { FocusRequester() }
    var wasSettingsMenuOpen by remember { mutableStateOf(false) }

    LaunchedEffect(showSettingsMenu) {
        if (wasSettingsMenuOpen && !showSettingsMenu) {
            runCatching { settingsButtonFocusRequester.requestFocus() }
        }
        wasSettingsMenuOpen = showSettingsMenu
    }

    val navigateToSection: (AfterglowSection) -> Unit = { target ->
        showSettingsMenu = false
        showAppearanceScreen = false
        showGlobalSearch = false
        showYearInReview = false
        currentSection = target
    }

    val openAddEditor: (MediaCategory, Boolean) -> Unit = { cat, isWatchlist ->
        editorItem = null
        editorDefaultCategory = cat
        editorDefaultWatchlist = isWatchlist
        showEditor = true
    }

    val openAddPicker: (MediaCategory?, Boolean) -> Unit = { preselected, isWatchlist ->
        addSheetPreselected = preselected
        addSheetWatchlist = isWatchlist
        showAddSheet = true
    }

    LaunchedEffect(lastLaunchedVersion) {
        val launched = lastLaunchedVersion
        if (launched != null && launched != APP_VERSION) {
            ThemePreferences.saveLastLaunchedVersion(context, APP_VERSION)
            if (!isUpdateAvailable) {
                ThemePreferences.saveLastSeenVersion(context, ZenixUpdateConfig.latestVersion)
            }
            if (notifyUpdates) {
                showWhatsNewDialog = true
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            val dismissJob = if (event.onUndo != null) {
                launch {
                    delay(5000L)
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
            } else null
            val result = snackbarHostState.showSnackbar(
                message = event.message,
                actionLabel = event.undoLabel,
                duration = if (event.onUndo != null) SnackbarDuration.Indefinite else SnackbarDuration.Short
            )
            dismissJob?.cancel()
            if (result == SnackbarResult.ActionPerformed) {
                event.onUndo?.invoke()
            }
        }
    }

    BackHandler(
        enabled = showSettingsMenu ||
            detailItem != null ||
            showYearInReview ||
            showGlobalSearch ||
            showAppearanceScreen ||
            currentSection != AfterglowSection.DASHBOARD
    ) {
        when {
            showSettingsMenu -> showSettingsMenu = false
            detailItem != null -> detailItemId = null
            showYearInReview -> showYearInReview = false
            showGlobalSearch -> showGlobalSearch = false
            showAppearanceScreen -> showAppearanceScreen = false
            else -> currentSection = AfterglowSection.DASHBOARD
        }
    }

    AfterglowTheme(
        themeMode = themeMode,
        accentColor = accentColor
    ) {
        val tokens = LocalZenixTokens.current
        val spacing = LocalZenixSpacing.current
        val radii = LocalZenixRadii.current
        val reducedMotion = LocalReducedMotion.current
        val isOnline = rememberIsOnline()
        val density = LocalDensity.current
        val slideOffsetPx = with(density) { 8.dp.roundToPx() }
        val mobileBottomClearance = rememberFloatingBottomBarClearance()
        var topBarHeightPx by remember { mutableIntStateOf(0) }
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isExpandedScreen = maxWidth >= 840.dp
            val hazeState = rememberSafeHazeState(
                enabled = themeMode == AppThemeMode.NEON_GLOSSY
            )

            val bottomSections = remember {
                listOf(
                    AfterglowSection.DASHBOARD,
                    AfterglowSection.MOVIES,
                    AfterglowSection.WEB_SERIES,
                    AfterglowSection.ANIME,
                    AfterglowSection.K_DRAMA
                )
            }
            val bottomTabs = remember {
                bottomSections.map { sec ->
                    LiquidNavTab(
                        id = sec.name.lowercase(),
                        label = sec.label,
                        icon = sec.icon
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                AfterglowBackdrop(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeHazeSource(hazeState)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Global Header on every screen
                        AfterglowTopBar(
                            onSearchClick = {
                                showSettingsMenu = false
                                globalSearchInitialCategory = when (currentSection) {
                                    AfterglowSection.MOVIES -> MediaCategory.MOVIE
                                    AfterglowSection.WEB_SERIES -> MediaCategory.WEB_SERIES
                                    AfterglowSection.ANIME -> MediaCategory.ANIME
                                    AfterglowSection.K_DRAMA -> MediaCategory.K_DRAMA
                                    else -> null
                                }
                                globalSearchInitialWatchlistOnly = (currentSection == AfterglowSection.WATCHLIST)
                                showGlobalSearch = true
                            },
                            onSettingsMenuClick = {
                                showSettingsMenu = !showSettingsMenu
                            },
                            settingsMenuExpanded = showSettingsMenu,
                            showUpdateBadge = showUpdateBadge,
                            settingsButtonFocusRequester = settingsButtonFocusRequester,
                            modifier = Modifier
                                .onSizeChanged { topBarHeightPx = it.height }
                                .then(
                                    if (showSettingsMenu) {
                                        Modifier.clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                            onClick = { showSettingsMenu = false }
                                        )
                                    } else Modifier
                                )
                        )

                        AnimatedVisibility(visible = !isOnline) {
                            ZenixOfflineBanner()
                        }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                when {
                                    mediaItemsState is UiState.Loading -> {
                                        ZenixHomeSkeleton()
                                    }
                                    mediaItemsState is UiState.Error -> {
                                        val err = (mediaItemsState as UiState.Error).message
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = spacing.space4, vertical = spacing.space5),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            ZenixErrorState(
                                                message = err,
                                                onRetry = viewModel::retrySync
                                            )
                                        }
                                    }
                                    else -> {
                                        val renderKey = Triple(currentSection, showAppearanceScreen, showGlobalSearch)
                                        AnimatedContent(
                                            targetState = renderKey,
                                            transitionSpec = {
                                                if (reducedMotion) {
                                                    EnterTransition.None togetherWith ExitTransition.None
                                                } else {
                                                    (fadeIn(tween(250)) + slideInVertically(tween(250)) { slideOffsetPx }) togetherWith
                                                        fadeOut(tween(200))
                                                }
                                            },
                                            label = "section_transition"
                                        ) { (section, isAppearance, isSearch) ->
                                            when {
                                                isSearch -> GlobalSearchScreen(
                                                    allItems = allItems,
                                                    onBack = { showGlobalSearch = false },
                                                    onSelectItem = { item -> detailItemId = item.id },
                                                    initialCategory = globalSearchInitialCategory,
                                                    initialWatchlistOnly = globalSearchInitialWatchlistOnly,
                                                    bottomPadding = mobileBottomClearance
                                                )

                                                section == AfterglowSection.SETTINGS && isAppearance -> AppearanceScreen(
                                                    currentTheme = themeMode,
                                                    currentAccent = accentColor,
                                                    onApplyAppearance = { newMode, newAccent ->
                                                        scope.launch {
                                                            ThemePreferences.saveAppearance(context, newMode, newAccent)
                                                            snackbarHostState.showSnackbar(
                                                                "Applied ${newMode.displayName} · ${newAccent.label}"
                                                            )
                                                        }
                                                    },
                                                    onBack = { showAppearanceScreen = false },
                                                    bottomPadding = mobileBottomClearance
                                                )

                                                section == AfterglowSection.DASHBOARD -> DashboardScreen(
                                                    profile = profile,
                                                    stats = stats,
                                                    onOpenAddPicker = { openAddPicker(null, false) },
                                                    onSelectItem = { detailItemId = it.id },
                                                    onStepEpisode = viewModel::stepEpisode,
                                                    onToggleFavourite = viewModel::toggleFavourite,
                                                    onOpenWatchlist = { navigateToSection(AfterglowSection.WATCHLIST) },
                                                    onOpenSmartHub = { tab ->
                                                        smartHubTab = tab
                                                        navigateToSection(AfterglowSection.SMART_HUB)
                                                    },
                                                    onOpenStatistics = { navigateToSection(AfterglowSection.STATISTICS) },
                                                    onOpenRankings = { navigateToSection(AfterglowSection.RANKINGS) },
                                                    bottomPadding = mobileBottomClearance
                                                )

                                                section == AfterglowSection.MOVIES -> MediaLibraryScreen(
                                                    mode = LibraryMode.MOVIES,
                                                    allItems = allItems,
                                                    compact = profile.compactMode,
                                                    onAdd = { cat -> openAddPicker(cat, false) },
                                                    onSelect = { detailItemId = it.id },
                                                    onEdit = {
                                                        editorItem = it
                                                        showEditor = true
                                                    },
                                                    onDelete = viewModel::deleteMediaItem,
                                                    onStepEpisode = viewModel::stepEpisode,
                                                    onToggleFavourite = viewModel::toggleFavourite,
                                                    onBulkUpdateStatus = viewModel::bulkUpdateStatus,
                                                    onBulkAddToWatchlist = viewModel::bulkAddToWatchlist,
                                                    onBulkDelete = viewModel::bulkDeleteMediaItems,
                                                    bottomPadding = mobileBottomClearance
                                                )

                                                section == AfterglowSection.WEB_SERIES -> MediaLibraryScreen(
                                                    mode = LibraryMode.WEB_SERIES,
                                                    allItems = allItems,
                                                    compact = profile.compactMode,
                                                    onAdd = { cat -> openAddPicker(cat, false) },
                                                    onSelect = { detailItemId = it.id },
                                                    onEdit = {
                                                        editorItem = it
                                                        showEditor = true
                                                    },
                                                    onDelete = viewModel::deleteMediaItem,
                                                    onStepEpisode = viewModel::stepEpisode,
                                                    onToggleFavourite = viewModel::toggleFavourite,
                                                    onBulkUpdateStatus = viewModel::bulkUpdateStatus,
                                                    onBulkAddToWatchlist = viewModel::bulkAddToWatchlist,
                                                    onBulkDelete = viewModel::bulkDeleteMediaItems,
                                                    bottomPadding = mobileBottomClearance
                                                )

                                                section == AfterglowSection.ANIME -> MediaLibraryScreen(
                                                    mode = LibraryMode.ANIME,
                                                    allItems = allItems,
                                                    compact = profile.compactMode,
                                                    onAdd = { cat -> openAddPicker(cat, false) },
                                                    onSelect = { detailItemId = it.id },
                                                    onEdit = {
                                                        editorItem = it
                                                        showEditor = true
                                                    },
                                                    onDelete = viewModel::deleteMediaItem,
                                                    onStepEpisode = viewModel::stepEpisode,
                                                    onToggleFavourite = viewModel::toggleFavourite,
                                                    onBulkUpdateStatus = viewModel::bulkUpdateStatus,
                                                    onBulkAddToWatchlist = viewModel::bulkAddToWatchlist,
                                                    onBulkDelete = viewModel::bulkDeleteMediaItems,
                                                    bottomPadding = mobileBottomClearance
                                                )

                                                section == AfterglowSection.K_DRAMA -> MediaLibraryScreen(
                                                    mode = LibraryMode.K_DRAMA,
                                                    allItems = allItems,
                                                    compact = profile.compactMode,
                                                    onAdd = { cat -> openAddPicker(cat, false) },
                                                    onSelect = { detailItemId = it.id },
                                                    onEdit = {
                                                        editorItem = it
                                                        showEditor = true
                                                    },
                                                    onDelete = viewModel::deleteMediaItem,
                                                    onStepEpisode = viewModel::stepEpisode,
                                                    onToggleFavourite = viewModel::toggleFavourite,
                                                    onBulkUpdateStatus = viewModel::bulkUpdateStatus,
                                                    onBulkAddToWatchlist = viewModel::bulkAddToWatchlist,
                                                    onBulkDelete = viewModel::bulkDeleteMediaItems,
                                                    bottomPadding = mobileBottomClearance
                                                )

                                                section == AfterglowSection.WATCHLIST -> WatchlistScreen(
                                                    allItems = allItems,
                                                    onAddWatchlistItem = { openAddPicker(MediaCategory.MOVIE, true) },
                                                    onSelectItem = { detailItemId = it.id },
                                                    onMoveToLibrary = viewModel::moveWatchlistToLibrary,
                                                    onDeleteItem = viewModel::deleteMediaItem,
                                                    onBack = { navigateToSection(AfterglowSection.DASHBOARD) },
                                                    onTryRandomPicker = {
                                                        smartHubTab = 0
                                                        navigateToSection(AfterglowSection.SMART_HUB)
                                                    },
                                                    bottomPadding = mobileBottomClearance
                                                )

                                                section == AfterglowSection.RANKINGS -> RankingsScreen(
                                                    allItems = allItems,
                                                    onSelectItem = { detailItemId = it.id },
                                                    onUpdateManualRank = viewModel::updateManualRank,
                                                    onToggleFavourite = viewModel::toggleFavourite,
                                                    onToggleAllTime = viewModel::toggleAllTimeFavourite,
                                                    onOpenLibrary = { navigateToSection(AfterglowSection.MOVIES) },
                                                    bottomPadding = mobileBottomClearance
                                                )

                                                section == AfterglowSection.STATISTICS -> StatisticsScreen(
                                                    stats = stats,
                                                    profile = profile,
                                                    allItems = allItems,
                                                    onSelectItem = { detailItemId = it.id },
                                                    onAddTitle = { openAddPicker(null, false) },
                                                    onOpenYearInReview = { showYearInReview = true },
                                                    bottomPadding = mobileBottomClearance
                                                )

                                                section == AfterglowSection.SMART_HUB -> SmartHubScreen(
                                                    initialTab = smartHubTab,
                                                    allItems = allItems,
                                                    quotes = quotes,
                                                    achievements = achievements,
                                                    stats = stats,
                                                    profile = profile,
                                                    onSelectItem = { detailItemId = it.id },
                                                    onResumeDropped = viewModel::resumeDroppedItem,
                                                    onIncrementRewatch = viewModel::incrementRewatch,
                                                    onSaveQuote = viewModel::saveQuote,
                                                    onDeleteQuote = viewModel::deleteQuote,
                                                    bottomPadding = mobileBottomClearance
                                                )

                                                section == AfterglowSection.UPDATES -> UpdatesScreen(
                                                    notifyUpdates = notifyUpdates,
                                                    onToggleNotify = { enabled ->
                                                        scope.launch {
                                                            ThemePreferences.saveNotifyUpdates(context, enabled)
                                                        }
                                                    },
                                                    onBack = { navigateToSection(AfterglowSection.DASHBOARD) },
                                                    onNotify = { msg -> scope.launch { snackbarHostState.showSnackbar(msg) } },
                                                    lastSeenVersion = lastSeenVersion,
                                                    onMarkUpdatesSeen = { version ->
                                                        scope.launch {
                                                            ThemePreferences.saveLastSeenVersion(context, version)
                                                        }
                                                    },
                                                    onUpdateApplied = { version ->
                                                        scope.launch {
                                                            ThemePreferences.saveLastSeenVersion(context, version)
                                                            ThemePreferences.saveLastLaunchedVersion(context, version)
                                                        }
                                                    },
                                                    bottomPadding = mobileBottomClearance
                                                )

                                                section == AfterglowSection.SETTINGS -> SettingsScreen(
                                                    userEmail = userEmail,
                                                    profile = profile,
                                                    currentTheme = themeMode,
                                                    currentAccent = accentColor,
                                                    onOpenAppearance = { showAppearanceScreen = true },
                                                    onOpenUpdates = { navigateToSection(AfterglowSection.UPDATES) },
                                                    mediaItems = allItems,
                                                    quotes = quotes,
                                                    onUpdateProfile = viewModel::updateProfile,
                                                    onImportBackup = viewModel::importBackup,
                                                    onClearAllData = viewModel::clearAllData,
                                                    onNotify = { msg -> scope.launch { snackbarHostState.showSnackbar(msg) } },
                                                    cloudSyncState = cloudSyncState,
                                                    isOnline = isOnline,
                                                    onSyncNow = { viewModel.requestCloudSync(isOnline) },
                                                    onConfirmFirstSync = viewModel::confirmFirstSyncMerge,
                                                    onCancelFirstSync = viewModel::cancelFirstSyncMerge,
                                                    onRestoreFromCloud = { viewModel.restoreFromCloud(isOnline) },
                                                    showUpdateBadge = showUpdateBadge,
                                                    bottomPadding = mobileBottomClearance
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(
                            start = spacing.space4,
                            end = spacing.space4,
                            bottom = 98.dp
                        )
                ) { data ->
                    NeonGlassCard(
                        accentColor = tokens.tertiaryAccent,
                        cornerRadius = radii.md,
                        contentPadding = PaddingValues(horizontal = spacing.space4, vertical = spacing.space3)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = data.visuals.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = tokens.textPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            val actionLabel = data.visuals.actionLabel
                            if (actionLabel != null) {
                                TextButton(
                                    onClick = { data.performAction() },
                                    modifier = Modifier
                                        .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                                        .zenixFocusRing(NeonCyan, radii.sm)
                                ) {
                                    Text(
                                        text = actionLabel,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan
                                    )
                                }
                            }
                        }
                    }
                }

                val selectedIndex = if (showGlobalSearch) -1 else bottomSections.indexOf(currentSection)
                ZenixBottomBar(
                    tabs = bottomTabs,
                    selectedIndex = selectedIndex,
                    onSelectTab = { idx -> navigateToSection(bottomSections[idx]) },
                    hazeState = hazeState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .then(if (isExpandedScreen) Modifier.widthIn(max = 560.dp) else Modifier)
                )

                // Dimmed backdrop below AfterglowTopBar when Settings popup menu is open
                AnimatedVisibility(
                    visible = showSettingsMenu,
                    enter = if (reducedMotion) EnterTransition.None else fadeIn(tween(150, easing = FastOutSlowInEasing)),
                    exit = if (reducedMotion) ExitTransition.None else fadeOut(tween(120))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = with(density) { topBarHeightPx.toDp() })
                            .background(Color.Black.copy(alpha = 0.45f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { showSettingsMenu = false }
                            )
                    )
                }

                // Anchored Settings & Navigation Dropdown Popup (top-right, 16dp from edge, 248dp wide)
                AnimatedVisibility(
                    visible = showSettingsMenu,
                    enter = if (reducedMotion) EnterTransition.None else {
                        fadeIn(tween(150, easing = FastOutSlowInEasing)) + scaleIn(
                            animationSpec = tween(150, easing = FastOutSlowInEasing),
                            initialScale = 0.90f,
                            transformOrigin = TransformOrigin(1f, 0f)
                        )
                    },
                    exit = if (reducedMotion) ExitTransition.None else {
                        fadeOut(tween(120)) + scaleOut(
                            animationSpec = tween(120),
                            targetScale = 0.92f,
                            transformOrigin = TransformOrigin(1f, 0f)
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(top = 60.dp, end = 16.dp)
                ) {
                    SettingsPopupMenu(
                        profile = profile,
                        userEmail = userEmail,
                        currentSection = currentSection,
                        isRootSection = !showGlobalSearch && !showAppearanceScreen,
                        showUpdateBadge = showUpdateBadge,
                        onSelectSection = navigateToSection,
                        onDismiss = { showSettingsMenu = false },
                        onRequestSignOut = {
                            showSettingsMenu = false
                            showSignOutConfirm = true
                        }
                    )
                }
            }
        }

        if (showYearInReview) {
            YearInReviewScreen(
                allItems = allItems,
                profile = profile,
                onBack = { showYearInReview = false },
                onNotify = { msg -> scope.launch { snackbarHostState.showSnackbar(msg) } }
            )
        }

        if (detailItem != null) {
            MediaDetailScreen(
                item = detailItem,
                onBack = { detailItemId = null },
                onEdit = {
                    editorItem = detailItem
                    detailItemId = null
                    showEditor = true
                },
                onDelete = {
                    viewModel.deleteMediaItem(detailItem)
                    detailItemId = null
                },
                onStepSeasonEpisode = { seasonNum, delta ->
                    viewModel.stepSeasonEpisode(detailItem, seasonNum, delta)
                },
                onAddSeason = { epCount ->
                    viewModel.addSeasonToSeries(detailItem, epCount)
                },
                onToggleFavourite = { viewModel.toggleFavourite(detailItem) },
                onToggleAllTimeFavourite = { viewModel.toggleAllTimeFavourite(detailItem) },
                onToggleHiddenGem = { viewModel.toggleHiddenGem(detailItem) },
                onIncrementRewatch = { viewModel.incrementRewatch(detailItem) },
                onDecrementRewatch = { viewModel.decrementRewatch(detailItem) },
                onNotify = { msg -> scope.launch { snackbarHostState.showSnackbar(msg) } }
            )
        }

        if (showAddSheet) {
            AddCategoryBottomSheet(
                preselectedCategory = addSheetPreselected,
                onDismiss = { showAddSheet = false },
                onSelectCategory = { selectedCategory ->
                    showAddSheet = false
                    openAddEditor(selectedCategory, addSheetWatchlist)
                }
            )
        }

        if (showEditor) {
            val activeEditorItem = editorItem
            MediaEditorDialog(
                initialItem = activeEditorItem,
                defaultCategory = editorDefaultCategory,
                defaultWatchlist = editorDefaultWatchlist,
                onSearchTmdb = viewModel::searchTmdb,
                onFetchTmdbDetails = viewModel::fetchTmdbDetails,
                onDismiss = { showEditor = false },
                onSave = { item, isEdit ->
                    viewModel.saveMediaItem(item, isEdit)
                    showEditor = false
                },
                onDelete = activeEditorItem?.takeIf { it.id.isNotBlank() }?.let { target ->
                    {
                        viewModel.deleteMediaItem(target)
                        showEditor = false
                    }
                }
            )
        }

        if (showSignOutConfirm) {
            NeonGlassDialog(
                onDismissRequest = { showSignOutConfirm = false },
                title = "Sign out of ZENIX?",
                accentColor = NeonPink,
                confirmText = "Sign out",
                confirmDangerous = true,
                onConfirm = {
                    showSignOutConfirm = false
                    signOut(credentialManager, scope, onSignOutComplete = onSignedOut)
                }
            ) {
                Text(
                    text = "Your library stays synced to your account. You can sign back in anytime.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = tokens.textSecondary
                )
            }
        }

        if (showWhatsNewDialog) {
            NeonGlassDialog(
                onDismissRequest = { showWhatsNewDialog = false },
                title = "What's new in ZENIX v$APP_VERSION",
                accentColor = tokens.tertiaryAccent,
                confirmText = "Got it",
                onConfirm = { showWhatsNewDialog = false },
                dismissText = null,
                secondaryText = "Release Notes",
                onSecondary = {
                    showWhatsNewDialog = false
                    navigateToSection(AfterglowSection.UPDATES)
                }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space2)) {
                    Text(
                        text = "Released ${ZenixUpdateConfig.releaseDate}",
                        style = MaterialTheme.typography.labelMedium,
                        color = tokens.tertiaryAccent
                    )
                    ZenixUpdateConfig.features.forEach { feature ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(spacing.space2)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = tokens.tertiaryAccent,
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(16.dp)
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = feature.name,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = tokens.textPrimary
                                )
                                Text(
                                    text = feature.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = tokens.textSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class PopupMenuItemSpec(
    val section: AfterglowSection,
    val label: String,
    val icon: ImageVector
)

@Composable
private fun SettingsPopupMenu(
    profile: UserProfile,
    userEmail: String?,
    currentSection: AfterglowSection,
    isRootSection: Boolean,
    showUpdateBadge: Boolean,
    onSelectSection: (AfterglowSection) -> Unit,
    onDismiss: () -> Unit,
    onRequestSignOut: () -> Unit
) {
    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current

    val menuItems = remember {
        listOf(
            PopupMenuItemSpec(AfterglowSection.WATCHLIST, "Watchlist", Icons.Outlined.BookmarkBorder),
            PopupMenuItemSpec(AfterglowSection.RANKINGS, "Favourites and rankings", Icons.Outlined.EmojiEvents),
            PopupMenuItemSpec(AfterglowSection.STATISTICS, "Statistics", Icons.Outlined.BarChart),
            PopupMenuItemSpec(AfterglowSection.SMART_HUB, "Smart discovery", Icons.Outlined.Casino),
            PopupMenuItemSpec(AfterglowSection.UPDATES, "Updates", Icons.Outlined.Download),
            PopupMenuItemSpec(AfterglowSection.SETTINGS, "Settings and data", Icons.Outlined.Settings)
        )
    }

    // 6 nav items + Sign out button = 7 focusable targets
    val focusRequesters = remember { List(7) { FocusRequester() } }
    var focusedIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        delay(16L)
        runCatching { focusRequesters[0].requestFocus() }
    }

    val popupBg = tokens.dialogBackground
    val popupBorder = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> Color.White.copy(alpha = 0.20f)
        else -> tokens.outline
    }
    val popupShape = RoundedCornerShape(16.dp)
    val initialChar = profile.displayName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "Z"

    Column(
        modifier = Modifier
            .width(248.dp)
            .semantics { contentDescription = "Settings and navigation menu" }
            .onPreviewKeyEvent { keyEvent ->
                if (keyEvent.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                when (keyEvent.key) {
                    Key.Escape -> {
                        onDismiss()
                        true
                    }
                    Key.DirectionDown -> {
                        val next = (focusedIndex + 1) % focusRequesters.size
                        focusedIndex = next
                        runCatching { focusRequesters[next].requestFocus() }
                        true
                    }
                    Key.DirectionUp -> {
                        val prev = (focusedIndex - 1 + focusRequesters.size) % focusRequesters.size
                        focusedIndex = prev
                        runCatching { focusRequesters[prev].requestFocus() }
                        true
                    }
                    else -> false
                }
            }
            .testTag("settings_popup_menu"),
        horizontalAlignment = Alignment.End
    ) {
        // Small caret pointing up to the Settings gear button center (22dp from popup right edge)
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
        ) {
            val tipX = size.width - 22.dp.toPx()
            val halfW = 6.dp.toPx()
            val bottomY = size.height + 0.5.dp.toPx()
            val fillPath = Path().apply {
                moveTo(tipX, 0f)
                lineTo(tipX + halfW, bottomY)
                lineTo(tipX - halfW, bottomY)
                close()
            }
            drawPath(path = fillPath, color = popupBg)
            val strokePath = Path().apply {
                moveTo(tipX - halfW, size.height)
                lineTo(tipX, 0f)
                lineTo(tipX + halfW, size.height)
            }
            drawPath(
                path = strokePath,
                color = popupBorder,
                style = Stroke(width = 0.75.dp.toPx())
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (tokens.useGlow) {
                        Modifier.neonGlow(tokens.primaryAccent, cornerRadius = 16.dp, alpha = 0.24f, spread = 8.dp)
                    } else Modifier
                )
                .clip(popupShape)
                .background(popupBg)
                .border(0.5.dp, popupBorder, popupShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
                .padding(vertical = spacing.space2)
        ) {
            // a) Profile row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.space3, vertical = spacing.space2),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.space3)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(tokens.primaryAccent.copy(alpha = 0.22f))
                        .border(1.dp, tokens.primaryAccent.copy(alpha = 0.55f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initialChar,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = tokens.primaryAccent
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = profile.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = tokens.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!userEmail.isNullOrBlank()) {
                        Text(
                            text = userEmail,
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            HorizontalDivider(
                color = tokens.outlineSubtle,
                modifier = Modifier.padding(vertical = spacing.space1)
            )

            // b) 6 menu rows (44dp tap target >= 44px, outline icon on left, highlight current screen)
            menuItems.forEachIndexed { index, spec ->
                val isSelected = isRootSection && currentSection == spec.section
                val isUpdatesRow = spec.section == AfterglowSection.UPDATES
                val rowShape = RoundedCornerShape(10.dp)
                val rowBg = if (isSelected) tokens.primaryAccent.copy(alpha = 0.16f) else Color.Transparent
                val itemColor = if (isSelected) tokens.primaryAccent else tokens.textPrimary
                val iconColor = if (isSelected) tokens.primaryAccent else tokens.textSecondary
                val interaction = remember { MutableInteractionSource() }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.space2)
                        .defaultMinSize(minHeight = 44.dp)
                        .clip(rowShape)
                        .background(rowBg)
                        .focusRequester(focusRequesters[index])
                        .onFocusChanged { if (it.isFocused) focusedIndex = index }
                        .zenixFocusRing(tokens.primaryAccent, 10.dp)
                        .clickable(
                            interactionSource = interaction,
                            indication = ripple(color = tokens.primaryAccent),
                            role = Role.Button,
                            onClick = { onSelectSection(spec.section) }
                        )
                        .padding(horizontal = spacing.space2, vertical = 10.dp)
                        .testTag("menu_${spec.section.name.lowercase()}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.space3)
                ) {
                    Icon(
                        imageVector = spec.icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(19.dp)
                    )
                    Text(
                        text = spec.label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = itemColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (isUpdatesRow && showUpdateBadge) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(NeonPink.copy(alpha = 0.20f))
                                .border(0.5.dp, NeonPink.copy(alpha = 0.85f), RoundedCornerShape(50))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "NEW",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = NeonPink
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                color = tokens.outlineSubtle,
                modifier = Modifier.padding(vertical = spacing.space1)
            )

            // c) Footer row: APP_VERSION on left, pink Sign out on right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.space3),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "v$APP_VERSION",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = tokens.textSecondary
                )
                val signOutInteraction = remember { MutableInteractionSource() }
                Row(
                    modifier = Modifier
                        .defaultMinSize(minHeight = 44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .focusRequester(focusRequesters[6])
                        .onFocusChanged { if (it.isFocused) focusedIndex = 6 }
                        .zenixFocusRing(NeonPink, 10.dp)
                        .clickable(
                            interactionSource = signOutInteraction,
                            indication = ripple(color = NeonPink),
                            role = Role.Button,
                            onClick = onRequestSignOut
                        )
                        .padding(horizontal = spacing.space2, vertical = 8.dp)
                        .testTag("menu_sign_out_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Logout,
                        contentDescription = null,
                        tint = NeonPink,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Sign out",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = NeonPink
                    )
                }
            }
        }
    }
}
