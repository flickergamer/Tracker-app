package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
import com.example.ui.components.MediaDetailDialog
import com.example.ui.components.MediaEditorDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LibraryMode
import com.example.ui.screens.MediaLibraryScreen
import com.example.ui.screens.RankingsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SmartHubScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.screens.WatchlistScreen
import com.example.ui.theme.AfterglowTheme
import com.example.ui.viewmodel.AfterglowViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

enum class AfterglowSection(val label: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Rounded.Dashboard),
    MOVIES("Movies", Icons.Rounded.Movie),
    WEB_SERIES("Web Series", Icons.Rounded.Tv),
    ANIME_KDRAMA("Anime & K-Drama", Icons.Rounded.AutoAwesome),
    WATCHLIST("Watchlist", Icons.Rounded.Bookmark),
    RANKINGS("Favourites & Rankings", Icons.Rounded.EmojiEvents),
    STATISTICS("Statistics", Icons.Rounded.BarChart),
    SMART_HUB("Smart Discovery", Icons.Rounded.AutoAwesome),
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

@OptIn(ExperimentalMaterial3Api::class)
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

            val openAddEditor: (MediaCategory, Boolean) -> Unit = { cat, isWatchlist ->
                editorItem = null
                editorDefaultCategory = cat
                editorDefaultWatchlist = isWatchlist
                showEditor = true
            }

            val mainScaffold: @Composable () -> Unit = {
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        if (!isExpandedScreen) {
                            TopAppBar(
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.background,
                                    titleContentColor = MaterialTheme.colorScheme.onBackground
                                ),
                                navigationIcon = {
                                    IconButton(
                                        onClick = { scope.launch { drawerState.open() } },
                                        modifier = Modifier.testTag("open_drawer_button")
                                    ) {
                                        Icon(Icons.Rounded.Menu, contentDescription = "Open navigation menu")
                                    }
                                },
                                title = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_afterglow_icon),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(26.dp)
                                                .clip(RoundedCornerShape(7.dp))
                                        )
                                        Text(
                                            text = "AFTERGLOW",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(
                                        onClick = {
                                            val cat = when (currentSection) {
                                                AfterglowSection.WEB_SERIES -> MediaCategory.WEB_SERIES
                                                AfterglowSection.ANIME_KDRAMA -> MediaCategory.ANIME
                                                else -> MediaCategory.MOVIE
                                            }
                                            openAddEditor(cat, currentSection == AfterglowSection.WATCHLIST)
                                        },
                                        modifier = Modifier.testTag("topbar_add_button")
                                    ) {
                                        Icon(Icons.Rounded.Add, contentDescription = "Add title", tint = MaterialTheme.colorScheme.secondary)
                                    }
                                }
                            )
                        }
                    },
                    bottomBar = {
                        if (!isExpandedScreen) {
                            val bottomTabs = listOf(
                                AfterglowSection.DASHBOARD,
                                AfterglowSection.MOVIES,
                                AfterglowSection.WEB_SERIES,
                                AfterglowSection.WATCHLIST,
                                AfterglowSection.STATISTICS
                            )
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface
                            ) {
                                bottomTabs.forEach { sec ->
                                    NavigationBarItem(
                                        selected = currentSection == sec,
                                        onClick = { currentSection = sec },
                                        icon = { Icon(sec.icon, contentDescription = sec.label) },
                                        label = {
                                            Text(
                                                text = when (sec) {
                                                    AfterglowSection.WEB_SERIES -> "Series"
                                                    AfterglowSection.STATISTICS -> "Stats"
                                                    else -> sec.label
                                                },
                                                maxLines = 1
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    },
                    floatingActionButton = {
                        if (isExpandedScreen) {
                            FloatingActionButton(
                                onClick = { openAddEditor(MediaCategory.MOVIE, currentSection == AfterglowSection.WATCHLIST) },
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Icon(Icons.Rounded.Add, contentDescription = "Add title")
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when {
                            mediaItemsState is UiState.Loading -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                            mediaItemsState is UiState.Error -> {
                                val err = (mediaItemsState as UiState.Error).message
                                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                                    Surface(
                                        shape = RoundedCornerShape(18.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(18.dp))
                                    ) {
                                        Text(
                                            text = "Unable to sync library: $err",
                                            color = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.padding(18.dp)
                                        )
                                    }
                                }
                            }
                            else -> {
                                when (currentSection) {
                                    AfterglowSection.DASHBOARD -> DashboardScreen(
                                        profile = profile,
                                        stats = stats,
                                        onAddTitle = openAddEditor,
                                        onSelectItem = { detailItemId = it.id },
                                        onStepEpisode = viewModel::stepEpisode,
                                        onToggleFavourite = viewModel::toggleFavourite,
                                        onOpenSmartHub = { tab ->
                                            smartHubTab = tab
                                            currentSection = AfterglowSection.SMART_HUB
                                        },
                                        onOpenStatistics = { currentSection = AfterglowSection.STATISTICS }
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
                                        onToggleFavourite = viewModel::toggleFavourite
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
                                        onToggleFavourite = viewModel::toggleFavourite
                                    )

                                    AfterglowSection.ANIME_KDRAMA -> MediaLibraryScreen(
                                        mode = LibraryMode.ANIME_KDRAMA,
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
                                        onToggleFavourite = viewModel::toggleFavourite
                                    )

                                    AfterglowSection.WATCHLIST -> WatchlistScreen(
                                        allItems = allItems,
                                        onAddWatchlistItem = { openAddEditor(MediaCategory.MOVIE, true) },
                                        onSelectItem = { detailItemId = it.id },
                                        onMoveToLibrary = viewModel::moveWatchlistToLibrary,
                                        onDeleteItem = viewModel::deleteMediaItem
                                    )

                                    AfterglowSection.RANKINGS -> RankingsScreen(
                                        allItems = allItems,
                                        onSelectItem = { detailItemId = it.id },
                                        onUpdateManualRank = viewModel::updateManualRank,
                                        onToggleFavourite = viewModel::toggleFavourite,
                                        onToggleAllTime = viewModel::toggleAllTimeFavourite
                                    )

                                    AfterglowSection.STATISTICS -> StatisticsScreen(
                                        stats = stats,
                                        profile = profile,
                                        allItems = allItems,
                                        onSelectItem = { detailItemId = it.id }
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
                                        onDeleteQuote = viewModel::deleteQuote
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
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (isExpandedScreen) {
                PermanentNavigationDrawer(
                    drawerContent = {
                        PermanentDrawerSheet(
                            drawerContainerColor = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.width(268.dp)
                        ) {
                            DrawerMenuContent(
                                profile = profile,
                                currentSection = currentSection,
                                onSelectSection = { currentSection = it }
                            )
                        }
                    },
                    content = mainScaffold
                )
            } else {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            drawerContainerColor = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.width(284.dp)
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
                    content = mainScaffold
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
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_afterglow_icon),
                contentDescription = "Afterglow logo",
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Column {
                Text("AFTERGLOW", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                Text(profile.displayName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 6.dp))

        AfterglowSection.entries.forEach { section ->
            NavigationDrawerItem(
                selected = currentSection == section,
                onClick = { onSelectSection(section) },
                icon = { Icon(section.icon, contentDescription = null) },
                label = { Text(section.label) },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.secondary,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.testTag("nav_${section.name.lowercase()}")
            )
        }
    }
}
