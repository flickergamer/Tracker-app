package com.example.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.ui.components.NeonGlassCard
import com.example.ui.components.neonGlow
import com.example.ui.components.pressScale
import com.example.ui.theme.GoldRating
import com.example.ui.theme.MutedGreyText
import com.example.ui.theme.NeonButtonDarkText
import com.example.ui.theme.NeonButtonDeep
import com.example.ui.theme.NeonButtonLight
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonFuchsia
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OffWhiteText
import com.example.ui.theme.RadialGlowCyan
import com.example.ui.theme.RadialGlowMagenta
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.VoidBackground
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.math.max

fun attemptAutoSignIn(
    context: Context,
    credentialManager: CredentialManager,
    onAuthSuccess: () -> Unit,
    onUnauthenticated: () -> Unit,
    scope: CoroutineScope
) {
    if (Firebase.auth.currentUser != null) {
        onAuthSuccess()
        return
    }
    val clientId = try {
        context.getString(R.string.default_web_client_id)
    } catch (_: Exception) {
        onUnauthenticated()
        return
    }
    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId(clientId)
        .setAutoSelectEnabled(true)
        .build()
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()
    scope.launch {
        try {
            val cred = credentialManager.getCredential(context, request).credential
            if (cred is CustomCredential && cred.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val token = GoogleIdTokenCredential.createFrom(cred.data).idToken
                Firebase.auth.signInWithCredential(GoogleAuthProvider.getCredential(token, null)).await()
                onAuthSuccess()
            } else {
                onUnauthenticated()
            }
        } catch (_: Exception) {
            onUnauthenticated()
        }
    }
}

fun onGoogleSignInClicked(
    context: Context,
    credentialManager: CredentialManager,
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit,
    scope: CoroutineScope,
    onAuthCancelled: () -> Unit = {}
) {
    val clientId = try {
        context.getString(R.string.default_web_client_id)
    } catch (_: Exception) {
        onAuthError("Google Sign-In configuration missing: default_web_client_id not found")
        return
    }
    val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(signInOption)
        .build()
    scope.launch {
        try {
            val activity = context as? Activity
                ?: throw IllegalStateException("Activity context required for Google Sign-In")
            val cred = credentialManager.getCredential(activity, request).credential
            if (cred is CustomCredential && cred.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val token = GoogleIdTokenCredential.createFrom(cred.data).idToken
                Firebase.auth.signInWithCredential(GoogleAuthProvider.getCredential(token, null)).await()
                onAuthSuccess()
            } else {
                onAuthError("Unexpected credential type returned.")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w("Auth", "Google Sign-In cancelled or dismissed: ${e.message}", e)
            onAuthCancelled()
        } catch (e: Exception) {
            Log.e("Auth", "Google Sign-In failed", e)
            onAuthError(e.localizedMessage ?: "Sign in failed. Please try again.")
        }
    }
}

fun signOut(
    credentialManager: CredentialManager,
    scope: CoroutineScope,
    onSignOutComplete: () -> Unit = {}
) {
    scope.launch {
        withContext(NonCancellable) {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                Log.w("Auth", "Failed to clear credential state: ${e.message}")
            } finally {
                Firebase.auth.signOut()
                onSignOutComplete()
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    var isCheckingAuto by remember { mutableStateOf(true) }
    var isSigningIn by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = {
                isCheckingAuto = false
                onAuthSuccess()
            },
            onUnauthenticated = {
                isCheckingAuto = false
            },
            scope = scope
        )
    }

    LoginNeonBackdrop {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.widthIn(max = 460.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Showcase: Three tilted glossy poster cards + floating glass badges
                LoginPosterShowcase()

                Spacer(modifier = Modifier.height(22.dp))

                // Title "ZENIX" 38sp bold, wide letter spacing, neon text glow
                Text(
                    text = "ZENIX",
                    style = TextStyle(
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 38.sp,
                        letterSpacing = 5.8.sp,
                        color = OffWhiteText,
                        shadow = Shadow(
                            color = NeonFuchsia.copy(alpha = 0.78f),
                            offset = Offset.Zero,
                            blurRadius = 28f
                        )
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Tagline with pink-to-cyan gradient
                Text(
                    text = "Personal Cinema & Series Sanctuary",
                    style = TextStyle(
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(NeonPink, NeonFuchsia, NeonCyan)
                        )
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // One short description line
                Text(
                    text = "Track films, series, anime & K-dramas in one luminous vault.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedGreyText,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // 4 small glass chips with neon dots
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LoginFeatureChip(label = "Smart Tracker", dotColor = NeonCyan)
                    LoginFeatureChip(label = "Live Analytics", dotColor = NeonFuchsia)
                    LoginFeatureChip(label = "Cloud Sync", dotColor = NeonPurple)
                    LoginFeatureChip(label = "Backup & Export", dotColor = NeonGreen)
                }

                Spacer(modifier = Modifier.height(26.dp))

                if (errorMessage != null) {
                    NeonGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        accentColor = NeonPink,
                        cornerRadius = 18.dp,
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Text(
                            text = errorMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = NeonPink,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Large glossy pill button "Continue with Google"
                ContinueWithGooglePillButton(
                    isLoading = isSigningIn || isCheckingAuto,
                    onClick = {
                        errorMessage = null
                        isSigningIn = true
                        onGoogleSignInClicked(
                            context = context,
                            credentialManager = credentialManager,
                            onAuthSuccess = {
                                isSigningIn = false
                                onAuthSuccess()
                            },
                            onAuthError = { msg ->
                                isSigningIn = false
                                errorMessage = msg
                            },
                            scope = scope,
                            onAuthCancelled = {
                                isSigningIn = false
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("google_sign_in_button")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Small footer text
                Text(
                    text = "Private per-account sync. Your library stays yours.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedGreyText.copy(alpha = 0.78f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun LoginNeonBackdrop(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBackground)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val maxDim = max(size.width, size.height).coerceAtLeast(1f)

            // Big radial violet glow: center-top
            val violetCenter = Offset(size.width * 0.50f, size.height * 0.16f)
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.0f to NeonPurple.copy(alpha = 0.40f),
                        0.45f to NeonPurple.copy(alpha = 0.14f),
                        1.0f to Color.Transparent
                    ),
                    center = violetCenter,
                    radius = maxDim * 0.54f
                ),
                radius = maxDim * 0.54f,
                center = violetCenter
            )

            // Big radial magenta glow: top-right
            val magentaCenter = Offset(size.width * 0.94f, size.height * 0.08f)
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.0f to RadialGlowMagenta.copy(alpha = 0.48f),
                        0.48f to RadialGlowMagenta.copy(alpha = 0.18f),
                        1.0f to Color.Transparent
                    ),
                    center = magentaCenter,
                    radius = maxDim * 0.60f
                ),
                radius = maxDim * 0.60f,
                center = magentaCenter
            )

            // Big radial cyan glow: left
            val cyanCenter = Offset(size.width * 0.04f, size.height * 0.44f)
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.0f to RadialGlowCyan.copy(alpha = 0.34f),
                        0.48f to RadialGlowCyan.copy(alpha = 0.12f),
                        1.0f to Color.Transparent
                    ),
                    center = cyanCenter,
                    radius = maxDim * 0.55f
                ),
                radius = maxDim * 0.55f,
                center = cyanCenter
            )
        }
        content()
    }
}

@Composable
private fun LoginPosterShowcase() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(254.dp),
        contentAlignment = Alignment.Center
    ) {
        // Left tilted cyan-indigo glossy poster card
        TiltedPosterCard(
            gradientColors = listOf(Color(0xFF06B6D4), Color(0xFF3730A3), Color(0xFF1E1B4B)),
            glowColor = NeonCyan,
            rotationDegrees = -11f,
            accentBarColor = NeonCyan,
            modifier = Modifier
                .offset(x = (-78).dp, y = 14.dp)
                .size(width = 116.dp, height = 168.dp)
        )

        // Right tilted fuchsia-violet glossy poster card
        TiltedPosterCard(
            gradientColors = listOf(NeonFuchsia, Color(0xFF7E22CE), Color(0xFF2E1065)),
            glowColor = NeonFuchsia,
            rotationDegrees = 11f,
            accentBarColor = NeonPink,
            modifier = Modifier
                .offset(x = 78.dp, y = 16.dp)
                .size(width = 116.dp, height = 168.dp)
        )

        // Larger center glossy poster card with glass play button
        CenterHeroPosterCard(
            modifier = Modifier
                .offset(y = (-2).dp)
                .size(width = 142.dp, height = 204.dp)
        )

        // Floating glass badge: "★ 9.1"
        FloatingGlassBadge(
            glowColor = GoldRating,
            modifier = Modifier.offset(x = (-76).dp, y = (-70).dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = GoldRating, fontWeight = FontWeight.Bold)) {
                        append("★ ")
                    }
                    withStyle(SpanStyle(color = OffWhiteText, fontWeight = FontWeight.Bold)) {
                        append("9.1")
                    }
                },
                fontFamily = SpaceGroteskFontFamily,
                fontSize = 13.sp
            )
        }

        // Floating glass badge: "Ep 8 · 64%"
        FloatingGlassBadge(
            glowColor = NeonCyan,
            modifier = Modifier.offset(x = 76.dp, y = 70.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .neonGlow(NeonCyan, cornerRadius = 4.dp, alpha = 0.7f, spread = 3.dp)
                        .clip(CircleShape)
                        .background(NeonCyan)
                )
                Text(
                    text = "Ep 8 · 64%",
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = OffWhiteText
                )
            }
        }
    }
}

@Composable
private fun TiltedPosterCard(
    gradientColors: List<Color>,
    glowColor: Color,
    rotationDegrees: Float,
    accentBarColor: Color,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = modifier
            .graphicsLayer { rotationZ = rotationDegrees }
            .neonGlow(color = glowColor, cornerRadius = 22.dp, alpha = 0.42f, spread = 11.dp)
            .clip(shape)
            .background(Brush.linearGradient(colors = gradientColors))
            .drawBehind {
                // Glossy highlight on top half
                drawRect(
                    brush = Brush.verticalGradient(
                        0.0f to Color.White.copy(alpha = 0.30f),
                        0.50f to Color.Transparent,
                        startY = 0f,
                        endY = size.height.coerceAtLeast(1f)
                    )
                )
            }
            .border(1.dp, Color.White.copy(alpha = 0.40f), shape)
    ) {
        // Minimalist poster silhouette lines inside card
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Box(
                modifier = Modifier
                    .width(46.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(accentBarColor.copy(alpha = 0.85f))
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.35f))
            )
        }
    }
}

@Composable
private fun CenterHeroPosterCard(modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(26.dp)
    Box(
        modifier = modifier
            .neonGlow(color = NeonPink, cornerRadius = 26.dp, alpha = 0.52f, spread = 14.dp)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF9333EA),
                        Color(0xFFDB2777),
                        Color(0xFF1E1B4B)
                    )
                )
            )
            .drawBehind {
                // Glossy highlight on the top half
                drawRect(
                    brush = Brush.verticalGradient(
                        0.0f to Color.White.copy(alpha = 0.34f),
                        0.52f to Color.Transparent,
                        startY = 0f,
                        endY = size.height.coerceAtLeast(1f)
                    )
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.55f),
                    start = Offset(20.dp.toPx(), 1.dp.toPx()),
                    end = Offset(size.width - 20.dp.toPx(), 1.dp.toPx()),
                    strokeWidth = 1.2.dp.toPx()
                )
            }
            .border(1.2.dp, Color.White.copy(alpha = 0.40f), shape),
        contentAlignment = Alignment.Center
    ) {
        // Glass Play Button
        Box(
            modifier = Modifier
                .size(56.dp)
                .neonGlow(color = NeonFuchsia, cornerRadius = 28.dp, alpha = 0.48f, spread = 8.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.34f),
                            Color.White.copy(alpha = 0.12f)
                        )
                    )
                )
                .border(1.2.dp, Color.White.copy(alpha = 0.55f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = "Cinema preview",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }

        // Subtle progress bar along the bottom edge
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.18f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.68f)
                    .height(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Brush.horizontalGradient(listOf(NeonCyan, NeonFuchsia)))
            )
        }
    }
}

@Composable
private fun FloatingGlassBadge(
    glowColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .neonGlow(color = glowColor, cornerRadius = 18.dp, alpha = 0.42f, spread = 8.dp)
            .clip(shape)
            .background(Color(0xFF120B24).copy(alpha = 0.82f))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.22f),
                        Color.White.copy(alpha = 0.05f)
                    )
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.40f), shape)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun LoginFeatureChip(
    label: String,
    dotColor: Color
) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier = Modifier
            .neonGlow(color = dotColor, cornerRadius = 16.dp, alpha = 0.22f, spread = 5.dp)
            .clip(shape)
            .background(Color(0xFF110C22).copy(alpha = 0.72f))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.14f),
                        Color.White.copy(alpha = 0.03f)
                    )
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.24f), shape)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .neonGlow(color = dotColor, cornerRadius = 4.dp, alpha = 0.8f, spread = 3.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Text(
            text = label,
            fontFamily = SpaceGroteskFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = OffWhiteText
        )
    }
}

@Composable
private fun ContinueWithGooglePillButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(50)
    val interactionSource = remember { MutableInteractionSource() }
    val enabled = !isLoading
    val alphaMult = if (enabled) 1f else 0.72f

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 56.dp)
            .height(56.dp)
            .then(if (enabled) Modifier.pressScale(interactionSource, 0.97f) else Modifier)
            .neonGlow(
                color = NeonFuchsia,
                cornerRadius = 28.dp,
                alpha = if (enabled) 0.64f else 0.30f,
                spread = 14.dp
            )
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFFBCFE8).copy(alpha = alphaMult),
                        NeonButtonLight.copy(alpha = alphaMult),
                        NeonButtonDeep.copy(alpha = alphaMult),
                        NeonPurple.copy(alpha = alphaMult)
                    )
                )
            )
            .drawBehind {
                // Glossy white top highlight
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        0.0f to Color.White.copy(alpha = 0.56f * alphaMult),
                        0.48f to Color.Transparent,
                        startY = 0f,
                        endY = size.height.coerceAtLeast(1f)
                    ),
                    cornerRadius = CornerRadius(28.dp.toPx(), 28.dp.toPx())
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.75f * alphaMult),
                    start = Offset(28.dp.toPx(), 1.2.dp.toPx()),
                    end = Offset(size.width - 28.dp.toPx(), 1.2.dp.toPx()),
                    strokeWidth = 1.4.dp.toPx()
                )
            }
            .border(1.dp, Color.White.copy(alpha = 0.48f * alphaMult), shape)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(color = NeonButtonDarkText),
                onClick = onClick
            )
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = NeonButtonDarkText,
                strokeWidth = 2.4.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // White circular Google "G" badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    GoogleGMark(modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Continue with Google",
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NeonButtonDarkText
                )
            }
        }
    }
}

@Composable
private fun GoogleGMark(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = size.minDimension * 0.21f
        val inset = stroke / 2f
        val arcSize = Size(size.width - stroke, size.height - stroke)
        val topLeft = Offset(inset, inset)

        // Red top-left arc
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = -150f,
            sweepAngle = 105f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Butt)
        )
        // Yellow left arc
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 145f,
            sweepAngle = 68f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Butt)
        )
        // Green bottom arc
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 40f,
            sweepAngle = 108f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Butt)
        )
        // Blue right-bottom arc
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = -8f,
            sweepAngle = 52f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Butt)
        )
        // Blue horizontal crossbar
        val cy = size.height * 0.50f
        drawLine(
            color = Color(0xFF4285F4),
            start = Offset(size.width * 0.50f, cy),
            end = Offset(size.width - inset * 0.35f, cy),
            strokeWidth = stroke,
            cap = StrokeCap.Butt
        )
    }
}

