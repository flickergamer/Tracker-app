package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.components.LiquidGlassBottomBar
import com.example.ui.components.LiquidNavTab
import com.example.ui.components.MediaDetailDialog
import com.example.ui.components.MediaEditorDialog
import com.example.ui.components.NeonGlassCard
import com.example.ui.components.NeonPrimaryButton
import com.example.ui.components.neonGlow
import com.example.ui.components.rememberSafeHazeState
import com.example.ui.components.safeHazeSource
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LibraryMode
import com.example.ui.screens.MediaLibraryScreen
import com.example.ui.screens.RankingsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SmartHubScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.screens.WatchlistScreen
import com.example.ui.theme.AfterglowTheme
import com.example.ui.theme.MutedGreyText
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonFuchsia
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OffWhiteText
import com.example.ui.theme.SpaceGroteskFontFamily
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
    SETTINGS("Settings & Data", Icons.Rounded.Settings)
}

@Composable
fun AfterglowApp() {
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose { Firebase.auth.removeAuthStateListener(listener) }
    }

    val user = currentUser
    if (user == null) {
        AfterglowTheme {
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
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

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
    var smartHubTab by remember { mutableIntStateOf(0) }

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
    val hazeState = rememberSafeHazeState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    BackHandler(enabled = currentSection != AfterglowSection.DASHBOARD) {
        currentSection = AfterglowSection.DASHBOARD
    }

    AfterglowTheme(accentPreset = profile.accentPreset) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isExpandedScreen = maxWidth >= 840.dp
            val screenBottomPadding = if (isExpandedScreen) 24.dp else FloatingBottomBarClearance

            val openAddEditor: (MediaCategory, Boolean) -> Unit = { cat, isWatchlist ->
                editorItem = null
                editorDefaultCategory = cat
                editorDefaultWatchlist = isWatchlist
                showEditor = true
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
                    // Backdrop + scrolling content marked as safe Haze blur source when floating bar is active
                    AfterglowBackdrop(
                        modifier = Modifier
                            .fillMaxSize()
                            .safeHazeSource(if (!isExpandedScreen) hazeState else null)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            if (!isExpandedScreen) {
                                AfterglowTopBar(
                                    onOpenMenu = { scope.launch { drawerState.open() } },
                                    onAddClick = {
                                        val cat = when (currentSection) {
                                            AfterglowSection.WEB_SERIES -> MediaCategory.WEB_SERIES
                                            AfterglowSection.ANIME -> MediaCategory.ANIME
                                            AfterglowSection.K_DRAMA -> MediaCategory.K_DRAMA
                                            else -> MediaCategory.MOVIE
                                        }
                                        openAddEditor(cat, currentSection == AfterglowSection.WATCHLIST)
                                    }
                                )
                            } else {
                                Spacer(modifier = Modifier.statusBarsPadding().height(8.dp))
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                when {
                                    mediaItemsState is UiState.Loading -> {
                                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(color = NeonFuchsia)
                                        }
                                    }
                                    mediaItemsState is UiState.Error -> {
                                        val err = (mediaItemsState as UiState.Error).message
                                        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                                            NeonGlassCard(
                                                accentColor = NeonPink,
                                                cornerRadius = 22.dp,
                                                contentPadding = PaddingValues(18.dp)
                                            ) {
                                                Text(
                                                    text = "Unable to sync library: $err",
                                                    color = NeonPink
                                                )
                                            }
                                        }
                                    }
                                    else -> {
                                        Crossfade(
                                            targetState = currentSection,
                                            animationSpec = tween(200),
                                            label = "section_crossfade"
                                        ) { section ->
                                            when (section) {
                                                AfterglowSection.DASHBOARD -> DashboardScreen(
                                                    profile = profile,
                                                    stats = stats,
                                                    onAddTitle = openAddEditor,
                                                    onSelectItem = { detailItemId = it.id },
                                                    onStepEpisode = viewModel::stepEpisode,
                                                    onToggleFavourite = viewModel::toggleFavourite,
                                                    onOpenWatchlist = { currentSection = AfterglowSection.WATCHLIST },
                                                    onOpenSmartHub = { tab ->
                                                        smartHubTab = tab
                                                        currentSection = AfterglowSection.SMART_HUB
                                                    },
                                                    onOpenStatistics = { currentSection = AfterglowSection.STATISTICS },
                                                    bottomPadding = screenBottomPadding
                                                )

                                                AfterglowSection.MOVIES -> MediaLibraryScreen(
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

                                                AfterglowSection.WEB_SERIES -> MediaLibraryScreen(
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

                                                AfterglowSection.ANIME -> MediaLibraryScreen(
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

                                                AfterglowSection.K_DRAMA -> MediaLibraryScreen(
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

                                                AfterglowSection.WATCHLIST -> WatchlistScreen(
                                                    allItems = allItems,
                                                    onAddWatchlistItem = { openAddEditor(MediaCategory.MOVIE, true) },
                                                    onSelectItem = { detailItemId = it.id },
                                                    onMoveToLibrary = viewModel::moveWatchlistToLibrary,
                                                    onDeleteItem = viewModel::deleteMediaItem,
                                                    bottomPadding = screenBottomPadding
                                                )

                                                AfterglowSection.RANKINGS -> RankingsScreen(
                                                    allItems = allItems,
                                                    onSelectItem = { detailItemId = it.id },
                                                    onUpdateManualRank = viewModel::updateManualRank,
                                                    onToggleFavourite = viewModel::toggleFavourite,
                                                    onToggleAllTime = viewModel::toggleAllTimeFavourite,
                                                    bottomPadding = screenBottomPadding
                                                )

                                                AfterglowSection.STATISTICS -> StatisticsScreen(
                                                    stats = stats,
                                                    profile = profile,
                                                    allItems = allItems,
                                                    onSelectItem = { detailItemId = it.id },
                                                    bottomPadding = screenBottomPadding
                                                )

                                                AfterglowSection.SMART_HUB -> SmartHubScreen(
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

                                                AfterglowSection.SETTINGS -> SettingsScreen(
                                                    userEmail = userEmail,
                                                    profile = profile,
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

                    // Neon Glass Snackbar positioned cleanly above floating bar
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = if (isExpandedScreen) 20.dp else 94.dp
                            )
                    ) { data ->
                        NeonGlassCard(
                            accentColor = NeonCyan,
                            cornerRadius = 18.dp,
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = data.visuals.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = OffWhiteText
                            )
                        }
                    }

                    // Floating Liquid Glass Bottom Bar (Home, Movies, Series, Anime, K-Drama)
                    if (!isExpandedScreen) {
                        val selectedIndex = bottomSections.indexOf(currentSection)
                        LiquidGlassBottomBar(
                            tabs = bottomTabs,
                            selectedIndex = selectedIndex,
                            onSelectTab = { idx -> currentSection = bottomSections[idx] },
                            hazeState = hazeState,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    } else {
                        NeonPrimaryButton(
                            onClick = {
                                val cat = when (currentSection) {
                                    AfterglowSection.WEB_SERIES -> MediaCategory.WEB_SERIES
                                    AfterglowSection.ANIME -> MediaCategory.ANIME
                                    AfterglowSection.K_DRAMA -> MediaCategory.K_DRAMA
                                    else -> MediaCategory.MOVIE
                                }
                                openAddEditor(cat, currentSection == AfterglowSection.WATCHLIST)
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
                            drawerContainerColor = Color(0xFF0A0614),
                            modifier = Modifier
                                .width(268.dp)
                                .border(1.dp, Color.White.copy(alpha = 0.14f))
                        ) {
                            DrawerMenuContent(
                                profile = profile,
                                currentSection = currentSection,
                                onSelectSection = { currentSection = it }
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
                            drawerContainerColor = Color(0xFF0B0718),
                            modifier = Modifier
                                .width(284.dp)
                                .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp))
                        ) {
                            DrawerMenuContent(
                                profile = profile,
                                currentSection = currentSection,
                                onSelectSection = {
                                    currentSection = it
                                    scope.launch { drawerState.close() }
                                }
                            )
                        }
                    },
                    content = mainShellContent
                )
            }
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
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .neonGlow(NeonPink, cornerRadius = 12.dp, alpha = 0.42f, spread = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(listOf(NeonPurple, NeonPink)))
                    .border(1.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = "Afterglow logo",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = "AFTERGLOW",
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = 2.2.sp,
                    color = OffWhiteText
                )
                Text(
                    text = profile.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonFuchsia
                )
            }
        }
        HorizontalDivider(color = Color.White.copy(alpha = 0.14f), modifier = Modifier.padding(vertical = 4.dp))

        AfterglowSection.entries.forEach { section ->
            val selected = currentSection == section
            val shape = RoundedCornerShape(16.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (selected) {
                            Modifier
                                .neonGlow(NeonPurple, cornerRadius = 16.dp, alpha = 0.30f, spread = 5.dp)
                                .clip(shape)
                                .background(Brush.horizontalGradient(listOf(NeonFuchsia.copy(alpha = 0.85f), NeonPurple.copy(alpha = 0.85f))))
                                .border(1.dp, Color.White.copy(alpha = 0.38f), shape)
                        } else {
                            Modifier
                                .clip(shape)
                                .background(Color.White.copy(alpha = 0.04f))
                        }
                    )
                    .clickable { onSelectSection(section) }
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .testTag("nav_${section.name.lowercase()}"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = section.icon,
                    contentDescription = null,
                    tint = if (selected) Color.White else MutedGreyText,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = section.label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = if (selected) Color.White else OffWhiteText
                )
            }
        }
    }
}
