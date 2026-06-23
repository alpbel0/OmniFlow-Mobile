package com.omniflow.ui.auth.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omniflow.R
import kotlin.math.roundToInt

@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    pageIndex: Int,
    pageCount: Int,
    pagerProgress: Float,
    isCardExpanded: Boolean,
    isSaving: Boolean,
    showPersistenceError: Boolean,
    onSkip: () -> Unit,
    onCardToggle: () -> Unit,
    onPrimaryClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(page.imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0x8A0A1C2E), Color.Transparent),
                    ),
                ),
        )
        OnboardingTopBar(page = page, isSaving = isSaving, onSkip = onSkip)
        OnboardingCard(
            page = page,
            pageIndex = pageIndex,
            pageCount = pageCount,
            pagerProgress = pagerProgress,
            isExpanded = isCardExpanded,
            isSaving = isSaving,
            showPersistenceError = showPersistenceError,
            onToggle = onCardToggle,
            onPrimaryClick = onPrimaryClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 26.dp),
        )
    }
}

@Composable
private fun OnboardingTopBar(
    page: OnboardingPage,
    isSaving: Boolean,
    onSkip: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 28.dp, top = 16.dp, end = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(page.heroTitleRes),
            color = Color.White,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.Bold,
        )
        TextButton(onClick = onSkip, enabled = !isSaving) {
            Text(
                text = stringResource(R.string.onboarding_skip),
                color = Color.White.copy(alpha = if (isSaving) 0.55f else 0.92f),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun OnboardingCard(
    page: OnboardingPage,
    pageIndex: Int,
    pageCount: Int,
    pagerProgress: Float,
    isExpanded: Boolean,
    isSaving: Boolean,
    showPersistenceError: Boolean,
    onToggle: () -> Unit,
    onPrimaryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .widthIn(max = 345.dp)
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(CARD_ANIMATION_DURATION_MS))
            .shadow(20.dp, RoundedCornerShape(30.dp), ambientColor = CardShadow)
            .clip(RoundedCornerShape(30.dp))
            .background(Color.White.copy(alpha = 0.98f))
            .clickable(enabled = !isSaving, onClick = onToggle)
            .padding(horizontal = 28.dp, vertical = 24.dp),
    ) {
        Text(
            text = stringResource(page.eyebrowRes),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 13.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.52.sp,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(page.titleRes),
            color = CardTitle,
            fontSize = 27.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.54).sp,
        )
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(tween(CARD_ANIMATION_DURATION_MS)) + expandVertically(),
            exit = fadeOut(tween(CARD_ANIMATION_DURATION_MS)) + shrinkVertically(),
        ) {
            Column {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = stringResource(page.bodyRes),
                    color = CardBody,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        PageIndicator(
            pagerProgress = pagerProgress,
            pageCount = pageCount,
        )
        if (showPersistenceError) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.onboarding_persistence_error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Spacer(Modifier.height(14.dp))
        OnboardingPrimaryButton(
            text = stringResource(
                if (pageIndex == pageCount - 1) {
                    R.string.onboarding_start
                } else {
                    R.string.onboarding_next
                },
            ),
            loading = isSaving,
            onClick = onPrimaryClick,
        )
    }
}

@Composable
private fun OnboardingPrimaryButton(
    text: String,
    loading: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = !loading,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 58.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp,
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PageIndicator(pagerProgress: Float, pageCount: Int) {
    val currentPage = pagerProgress.roundToInt().coerceIn(0, pageCount - 1)
    val description = stringResource(
        R.string.onboarding_page_description,
        currentPage + 1,
        pageCount,
    )
    Box(
        modifier = Modifier
            .width(IndicatorActiveWidth * pageCount + IndicatorGap * (pageCount - 1))
            .height(12.dp)
            .semantics { contentDescription = description },
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = IndicatorStep * index + IndicatorDotOffset)
                    .size(IndicatorDotSize)
                    .clip(CircleShape)
                    .background(IndicatorInactive),
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = IndicatorStep * pagerProgress)
                .width(IndicatorActiveWidth)
                .height(IndicatorDotSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
        )
    }
}

private val CardTitle = Color(0xFF102033)
private val CardBody = Color(0xFF6F7F95)
private val IndicatorInactive = Color(0xFFDCECFF)
private val CardShadow = Color(0x1F0D1F33)
private val IndicatorActiveWidth = 28.dp
private val IndicatorDotSize = 8.dp
private val IndicatorGap = 8.dp
private val IndicatorStep = IndicatorActiveWidth + IndicatorGap
private val IndicatorDotOffset = (IndicatorActiveWidth - IndicatorDotSize) / 2
private const val CARD_ANIMATION_DURATION_MS = 280
