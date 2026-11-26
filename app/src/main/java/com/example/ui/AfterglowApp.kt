package com.example.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.data.model.UiState
import com.example.data.model.UserProfile
import com.example.ui.components.AfterglowBackdrop
import com.example.ui.components.AfterglowTopBar
import com.example.ui.components.FloatingBottomBarClearance
import com.example.ui.components.LiquidNavTab
import com.example.ui.components.MediaDetailDialog
import com.example.ui.components.MediaEditorDialog
import com.example.ui.components.NeonGlassCard
import com.example.ui.components.NeonGlassDialog
import com.example.ui.components.NeonGlassTextField
import com.example.ui.components.NeonPrimaryButton
import com.example.ui.components.PosterArtwork
import com.example.ui.components.RatingPill
import com.example.ui.components.ZenixBottomBar
import com.example.ui.components.ZenixBrandLogo
import com.example.ui.components.ZenixEmptyState
import com.example.ui.components.ZenixErrorState
import com.example.ui.components.ZenixHomeSkeleton
import com.example.ui.components.ZenixOfflineBanner
import com.example.ui.components.neonGlow
import com.example.ui.components.rememberIsOnline
import com.example.ui.components.rememberSafeHazeState
import com.example.ui.components.safeHazeSource
import com.example.ui.components.zenixFocusRing
import com.example.ui.screens.AppearanceScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LibraryMode
import com.example.ui.screens.MediaLibraryScreen
import com.example.ui.screens.RankingsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SmartHubScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.screens.UpdatesScreen
import com.example.ui.screens.WatchlistScreen
import com.example.ui.theme.AfterglowTheme
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.LocalReducedMotion
import com.example.ui.theme.LocalZenixRadii
import com.example.ui.theme.LocalZenixSpacing
import com.example.ui.theme.LocalZenixTokens
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.ThemePreferences
import com.example.ui.theme.ZenixAccentColor
import com.example.ui.viewmodel.AfterglowViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
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
                    defaultDisplayName = defaultDisplayName
                )
            }
        }
    )

    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val mediaItemsState by viewModel.mediaItemsState.collectAsStateWithLifecycle()
    val quotesState by viewModel.quotesState.collectAsStateWithLifecycle()
    val stats by viewModel.dashboardStats.collectAsStateWithLifecycle()
    val achievements by viewModel.achievements.collectAsStateWithLifecycle()

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

    var editorItem by remember { mutableStateOf<MediaItem?>(null) }
    var editorDefaultCategory by remember { mutableStateOf(MediaCategory.MOVIE) }
    var editorDefaultWatchlist by remember { mutableStateOf(false) }
    var showEditor by remember { mutableStateOf(false) }

    var detailItemId by remember { mutableStateOf<String?>(null) }
    val detailItem = remember(allItems, detailItemId) {
        detailItemId?.let { id -> allItems.firstOrNull { it.id == id } }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val navigateToSection: (AfterglowSection) -> Unit = { target ->
        showAppearanceScreen = false
        currentSection = target
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            val result = snackbarHostState.showSnackbar(
                message = event.message,
                actionLabel = event.undoLabel,
                duration = if (event.onUndo != null) SnackbarDuration.Long else SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                event.onUndo?.invoke()
            }
        }
    }

    BackHandler(enabled = showAppearanceScreen || currentSection != AfterglowSection.DASHBOARD) {
        if (showAppearanceScreen) {
            showAppearanceScreen = false
        } else {
            currentSection = AfterglowSection.DASHBOARD
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
        val slideOffsetPx = with(LocalDensity.current) { 8.dp.roundToPx() }
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isExpandedScreen = maxWidth >= 840.dp
            val screenBottomPadding = if (isExpandedScreen) 24.dp else FloatingBottomBarClearance
            val hazeState = rememberSafeHazeState(
                enabled = !isExpandedScreen && themeMode == AppThemeMode.NEON_GLOSSY
            )

            val openAddEditor: (MediaCategory, Boolean) -> Unit = { cat, isWatchlist ->
                editorItem = null
                editorDefaultCategory = cat
                editorDefaultWatchlist = isWatchlist
                showEditor = true
            }

            val defaultCategoryForSection: () -> MediaCategory = {
                when (currentSection) {
                    AfterglowSection.WEB_SERIES -> MediaCategory.WEB_SERIES
                    AfterglowSection.ANIME -> MediaCategory.ANIME
                    AfterglowSection.K_DRAMA -> MediaCategory.K_DRAMA
                    else -> MediaCategory.MOVIE
                }
            }

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

            val mainShellContent: @Composable () -> Unit = {
                Box(modifier = Modifier.fillMaxSize()) {
                    AfterglowBackdrop(
                        modifier = Modifier
                            .fillMaxSize()
                            .safeHazeSource(hazeState)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            if (!isExpandedScreen) {
                                AfterglowTopBar(
                                    onOpenMenu = { scope.launch { drawerState.open() } },
                                    onSearchClick = { showGlobalSearch = true },
                                    onAddClick = {
                                        openAddEditor(
                                            defaultCategoryForSection(),
                                            currentSection == AfterglowSection.WATCHLIST
                                        )
                                    }
                                )
                            } else {
                                Spacer(modifier = Modifier.statusBarsPadding().height(spacing.space2))
                            }

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
                                        val renderKey = Pair(currentSection, showAppearanceScreen)
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
                                        ) { (section, isAppearance) ->
                                            when {
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
                                                    bottomPadding = screenBottomPadding
                                                )

                                                section == AfterglowSection.DASHBOARD -> DashboardScreen(
                                                    profile = profile,
                                                    stats = stats,
                                                    onAddTitle = openAddEditor,
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
                                                    bottomPadding = screenBottomPadding
                                                )

                                                section == AfterglowSection.MOVIES -> MediaLibraryScreen(
                                                    mode = LibraryMode.MOVIES,
                                                    allItems = allItems,
                                                    compact = profile.compactMode,
                                                    onAdd = { openAddEditor(it, false) },
                                                    onSelect = { detailItemId = it.id },
                                                    onEdit = {
                                                        editorItem = it
                                                        showEditor = true
                                                    },
                                                    onDelete = viewModel::deleteMediaItem,
                                                    onStepEpisode = viewModel::stepEpisode,
                                                    onToggleFavourite = viewModel::toggleFavourite,
                                                    bottomPadding = screenBottomPadding
                                                )

                                                section == AfterglowSection.WEB_SERIES -> MediaLibraryScreen(
                                                    mode = LibraryMode.WEB_SERIES,
                                                    allItems = allItems,
                                                    compact = profile.compactMode,
                                                    onAdd = { openAddEditor(it, false) },
                                                    onSelect = { detailItemId = it.id },
                                                    onEdit = {
                                                        editorItem = it
                                                        showEditor = true
                                                    },
                                                    onDelete = viewModel::deleteMediaItem,
                                                    onStepEpisode = viewModel::stepEpisode,
                                                    onToggleFavourite = viewModel::toggleFavourite,
                                                    bottomPadding = screenBottomPadding
                                                )

                                                section == AfterglowSection.ANIME -> MediaLibraryScreen(
                                                    mode = LibraryMode.ANIME,
                                                    allItems = allItems,
                                                    compact = profile.compactMode,
                                                    onAdd = { openAddEditor(it, false) },
                                                    onSelect = { detailItemId = it.id },
                                                    onEdit = {
                                                        editorItem = it
                                                        showEditor = true
                                                    },
                                                    onDelete = viewModel::deleteMediaItem,
                                                    onStepEpisode = viewModel::stepEpisode,
                                                    onToggleFavourite = viewModel::toggleFavourite,
                                                    bottomPadding = screenBottomPadding
                                                )

                                                section == AfterglowSection.K_DRAMA -> MediaLibraryScreen(
                                                    mode = LibraryMode.K_DRAMA,
                                                    allItems = allItems,
                                                    compact = profile.compactMode,
                                                    onAdd = { openAddEditor(it, false) },
                                                    onSelect = { detailItemId = it.id },
                                                    onEdit = {
                                                        editorItem = it
                                                        showEditor = true
                                                    },
                                                    onDelete = viewModel::deleteMediaItem,
                                                    onStepEpisode = viewModel::stepEpisode,
                                                    onToggleFavourite = viewModel::toggleFavourite,
                                                    bottomPadding = screenBottomPadding
                                                )

                                                section == AfterglowSection.WATCHLIST -> WatchlistScreen(
                                                    allItems = allItems,
                                                    onAddWatchlistItem = { openAddEditor(MediaCategory.MOVIE, true) },
                                                    onSelectItem = { detailItemId = it.id },
                                                    onMoveToLibrary = viewModel::moveWatchlistToLibrary,
                                                    onDeleteItem = viewModel::deleteMediaItem,
                                                    onBack = { navigateToSection(AfterglowSection.DASHBOARD) },
                                                    onTryRandomPicker = {
                                                        smartHubTab = 0
                                                        navigateToSection(AfterglowSection.SMART_HUB)
                                                    },
                                                    bottomPadding = screenBottomPadding
                                                )

                                                section == AfterglowSection.RANKINGS -> RankingsScreen(
                                                    allItems = allItems,
                                                    onSelectItem = { detailItemId = it.id },
                                                    onUpdateManualRank = viewModel::updateManualRank,
                                                    onToggleFavourite = viewModel::toggleFavourite,
                                                    onToggleAllTime = viewModel::toggleAllTimeFavourite,
                                                    onOpenLibrary = { navigateToSection(AfterglowSection.MOVIES) },
                                                    bottomPadding = screenBottomPadding
                                                )

                                                section == AfterglowSection.STATISTICS -> StatisticsScreen(
                                                    stats = stats,
                                                    profile = profile,
                                                    allItems = allItems,
                                                    onSelectItem = { detailItemId = it.id },
                                                    onAddTitle = { openAddEditor(MediaCategory.MOVIE, false) },
                                                    bottomPadding = screenBottomPadding
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
                                                    bottomPadding = screenBottomPadding
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
                                                    bottomPadding = screenBottomPadding
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
                                                    onSignOut = {
                                                        signOut(credentialManager, scope, onSignOutComplete = onSignedOut)
                                                    },
                                                    bottomPadding = screenBottomPadding
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
                                bottom = if (isExpandedScreen) 20.dp else 98.dp
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

                    if (!isExpandedScreen) {
                        val selectedIndex = bottomSections.indexOf(currentSection)
                        ZenixBottomBar(
                            tabs = bottomTabs,
                            selectedIndex = selectedIndex,
                            onSelectTab = { idx -> navigateToSection(bottomSections[idx]) },
                            hazeState = hazeState,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    } else {
                        NeonPrimaryButton(
                            onClick = {
                                openAddEditor(
                                    defaultCategoryForSection(),
                                    currentSection == AfterglowSection.WATCHLIST
                                )
                            },
                            cornerRadius = 18.dp,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .navigationBarsPadding()
                                .padding(20.dp)
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = "Add title")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Title", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (isExpandedScreen) {
                PermanentNavigationDrawer(
                    drawerContent = {
                        PermanentDrawerSheet(
                            drawerContainerColor = tokens.drawerBackground,
                            modifier = Modifier
                                .width(276.dp)
                                .border(1.dp, tokens.outlineSubtle)
                        ) {
                            DrawerMenuContent(
                                profile = profile,
                                currentSection = currentSection,
                                onSelectSection = navigateToSection
                            )
                        }
                    },
                    content = mainShellContent
                )
            } else {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            drawerContainerColor = tokens.drawerBackground,
                            modifier = Modifier
                                .width(292.dp)
                                .border(
                                    1.dp,
                                    tokens.outlineSubtle,
                                    RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
                                )
                        ) {
                            DrawerMenuContent(
                                profile = profile,
                                currentSection = currentSection,
                                onSelectSection = { sec ->
                                    navigateToSection(sec)
                                    scope.launch { drawerState.close() }
                                }
                            )
                        }
                    },
                    content = mainShellContent
                )
            }
        }

        if (showGlobalSearch) {
            GlobalSearchDialog(
                allItems = allItems,
                onDismiss = { showGlobalSearch = false },
                onSelectItem = { item ->
                    showGlobalSearch = false
                    detailItemId = item.id
                }
            )
        }

        if (showEditor) {
            MediaEditorDialog(
                initialItem = editorItem,
                defaultCategory = editorDefaultCategory,
                defaultWatchlist = editorDefaultWatchlist,
                onSearchTmdb = viewModel::searchTmdb,
                onFetchTmdbDetails = viewModel::fetchTmdbDetails,
                onDismiss = { showEditor = false },
                onSave = { item, isEdit ->
                    viewModel.saveMediaItem(item, isEdit)
                    showEditor = false
                }
            )
        }

        if (detailItem != null) {
            MediaDetailDialog(
                item = detailItem,
                onDismiss = { detailItemId = null },
                onEdit = {
                    editorItem = detailItem
                    detailItemId = null
                    showEditor = true
                },
                onDelete = {
                    viewModel.deleteMediaItem(detailItem)
                    detailItemId = null
                },
                onStepEpisode = { delta -> viewModel.stepEpisode(detailItem, delta) },
                onToggleFavourite = { viewModel.toggleFavourite(detailItem) },
                onToggleAllTimeFavourite = { viewModel.toggleAllTimeFavourite(detailItem) },
                onToggleHiddenGem = { viewModel.toggleHiddenGem(detailItem) },
                onIncrementRewatch = { viewModel.incrementRewatch(detailItem) }
            )
        }
    }
}

@Composable
private fun DrawerMenuContent(
    profile: UserProfile,
    currentSection: AfterglowSection,
    onSelectSection: (AfterglowSection) -> Unit
) {
    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current
    val radii = LocalZenixRadii.current

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(spacing.space4),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.space1)
        ) {
            // Drawer Header: Z logo, ZENIX, username in cyan
            Row(
                modifier = Modifier.padding(horizontal = spacing.space2, vertical = spacing.space3),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.space3)
            ) {
                ZenixBrandLogo(size = 40.dp)
                Column {
                    Text(
                        text = "ZENIX",
                        fontFamily = tokens.fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = 2.2.sp,
                        color = tokens.textPrimary
                    )
                    Text(
                        text = profile.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = NeonCyan,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            HorizontalDivider(color = tokens.outlineSubtle, modifier = Modifier.padding(vertical = spacing.space1))

            val activeEnd = lerp(NeonPurple, Color(0xFF4C1D95), 0.38f)

            AfterglowSection.entries.forEach { section ->
                val selected = currentSection == section
                val isUpdates = section == AfterglowSection.UPDATES
                val shape = RoundedCornerShape(radii.md)

                val rowMod = when {
                    selected -> Modifier
                        .then(
                            if (tokens.useGlow) {
                                Modifier.neonGlow(NeonPurple, cornerRadius = radii.md, alpha = 0.34f, spread = 5.dp)
                            } else Modifier
                        )
                        .clip(shape)
                        .background(Brush.horizontalGradient(listOf(NeonPurple, activeEnd)))
                        .border(1.dp, Color.White.copy(alpha = 0.30f), shape)

                    isUpdates -> Modifier
                        .clip(shape)
                        .background(NeonCyan.copy(alpha = 0.08f))
                        .border(1.dp, NeonCyan.copy(alpha = 0.65f), shape)

                    else -> Modifier
                        .clip(shape)
                        .background(Color.White.copy(alpha = 0.03f))
                }

                val labelColor = when {
                    selected -> Color.White
                    isUpdates -> NeonCyan
                    else -> tokens.textPrimary
                }
                val iconColor = when {
                    selected -> Color.White
                    isUpdates -> NeonCyan
                    else -> tokens.textSecondary
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                        .then(rowMod)
                        .zenixFocusRing(NeonPurple, radii.md)
                        .clickable(role = Role.Button) { onSelectSection(section) }
                        .padding(horizontal = spacing.space3, vertical = spacing.space3)
                        .testTag("nav_${section.name.lowercase()}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.space3)
                ) {
                    Icon(
                        imageVector = section.icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = section.label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (selected || isUpdates) FontWeight.Bold else FontWeight.Medium,
                        color = labelColor,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isUpdates) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(NeonPink.copy(alpha = 0.22f))
                                .border(1.dp, NeonPink.copy(alpha = 0.85f), RoundedCornerShape(50))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
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
        }

        Spacer(modifier = Modifier.height(spacing.space3))

        // Bottom Drawer Update Card
        NeonGlassCard(
            modifier = Modifier.fillMaxWidth(),
            accentColor = NeonCyan,
            cornerRadius = radii.md,
            contentPadding = PaddingValues(spacing.space3)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "A new update is available",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = tokens.textPrimary
                )
                Text(
                    text = "See what's new in ZENIX",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.textSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                NeonPrimaryButton(
                    onClick = { onSelectSection(AfterglowSection.UPDATES) },
                    accentColor = NeonPurple,
                    cornerRadius = 12.dp,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("drawer_view_updates_button")
                ) {
                    Text("View updates", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun GlobalSearchDialog(
    allItems: List<MediaItem>,
    onDismiss: () -> Unit,
    onSelectItem: (MediaItem) -> Unit
) {
    val tokens = LocalZenixTokens.current
    var query by remember { mutableStateOf("") }
    val results = remember(allItems, query) {
        val q = query.trim().lowercase()
        if (q.isBlank()) {
            allItems.sortedByDescending { it.personalRating }.take(8)
        } else {
            allItems.filter { item ->
                item.title.lowercase().contains(q) ||
                    item.genre.lowercase().contains(q) ||
                    item.streamingPlatform.lowercase().contains(q) ||
                    item.director.lowercase().contains(q) ||
                    item.mainCast.lowercase().contains(q)
            }.take(20)
        }
    }

    NeonGlassDialog(
        onDismissRequest = onDismiss,
        title = "Search Library",
        accentColor = NeonCyan,
        dismissText = "Close"
    ) {
        NeonGlassTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = "Search titles, genres, cast, platforms...",
            leadingIcon = {
                Icon(Icons.Rounded.Search, contentDescription = null, modifier = Modifier.size(18.dp))
            },
            focusNeonColor = NeonCyan,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("global_search_input")
        )

        if (results.isEmpty()) {
            ZenixEmptyState(
                icon = Icons.Rounded.SearchOff,
                title = if (allItems.isEmpty()) "Library is Empty" else "No Results Found",
                subtitle = if (allItems.isEmpty())
                    "Add a movie, series, anime, or K-drama to start searching."
                else
                    "Nothing matched \"$query\". Try another title, genre, or cast name.",
                accentColor = NeonCyan,
                primaryActionLabel = if (query.isNotBlank()) "Clear search" else null,
                onPrimaryAction = if (query.isNotBlank()) ({ query = "" }) else null
            )
        } else {
            val spacing = LocalZenixSpacing.current
            val radii = LocalZenixRadii.current
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp),
                verticalArrangement = Arrangement.spacedBy(spacing.space2)
            ) {
                items(results, key = { "gs_${it.id}" }) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp)
                            .clip(RoundedCornerShape(radii.sm))
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(1.dp, tokens.outlineSubtle, RoundedCornerShape(radii.sm))
                            .zenixFocusRing(NeonCyan, radii.sm)
                            .clickable(role = Role.Button) { onSelectItem(item) }
                            .padding(spacing.space3),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.space3)
                    ) {
                        PosterArtwork(
                            posterUrl = item.posterUrl,
                            title = item.title,
                            category = item.categoryEnum,
                            modifier = Modifier.size(width = 38.dp, height = 54.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleSmall,
                                color = tokens.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${item.categoryEnum.label} · ${item.releaseYear}",
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.textSecondary
                            )
                        }
                        if (item.personalRating > 0.0) {
                            RatingPill(rating = item.personalRating)
                        }
                    }
                }
            }
        }
    }
}
