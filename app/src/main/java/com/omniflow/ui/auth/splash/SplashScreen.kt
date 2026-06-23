package com.omniflow.ui.auth.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.omniflow.R

@Composable
fun SplashScreen(
    paddingValues: PaddingValues,
    onDestination: (SplashDestination) -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        (uiState as? SplashUiState.Ready)?.let { onDestination(it.destination) }
    }

    SplashContent(modifier = Modifier.padding(paddingValues))
}

@Composable
private fun SplashContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        SplashGlow(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 72.dp, y = (-72).dp)
                .size(260.dp),
            alpha = 0.28f,
        )
        SplashGlow(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-88).dp, y = 72.dp)
                .size(220.dp),
            alpha = 0.18f,
        )
        SplashBrand(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
private fun SplashGlow(modifier: Modifier, alpha: Float) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = alpha),
                        Color.Transparent,
                    ),
                ),
            ),
    )
}

@Composable
private fun SplashBrand(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(104.dp)
                .shadow(20.dp, RoundedCornerShape(30.dp), ambientColor = MaterialTheme.colorScheme.primary)
                .clip(RoundedCornerShape(30.dp))
                .background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, SkyBlue))),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "O",
                color = Color.White,
                fontSize = 46.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.app_name),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            letterSpacing = (-0.64).sp,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.splash_tagline),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(20.dp))
        JourneyProgress()
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.splash_status),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun JourneyProgress() {
    val transition = rememberInfiniteTransition(label = "splash-progress")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1_250, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "splash-progress-position",
    )
    val density = LocalDensity.current
    val translation = with(density) { (-104).dp.toPx() + 276.dp.toPx() * progress }

    Box(
        modifier = Modifier
            .width(172.dp)
            .height(8.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.outlineVariant)
            .semantics { progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate },
    ) {
        Box(
            modifier = Modifier
                .width(104.dp)
                .height(8.dp)
                .graphicsLayer { translationX = translation }
                .clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.primary, AquaBlue))),
        )
    }
}

private val SkyBlue = Color(0xFF38A1FF)
private val AquaBlue = Color(0xFF4FBDFF)
