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
import com.omniflow.R
import com.omniflow.core.designsystem.theme.OmniTokens
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
    val colors = OmniTokens.colors
    val spacing = OmniTokens.spacing

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
                .height(OmniTokens.dimens.onboardingHeroShadeHeight)
                .background(
                    Brush.verticalGradient(
                        listOf(colors.heroShade, Color.Transparent),
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
                .padding(horizontal = spacing.xl, vertical = spacing.xl + spacing.tiny),
        )
    }
}

@Composable
private fun OnboardingTopBar(
    page: OnboardingPage,
    isSaving: Boolean,
    onSkip: () -> Unit,
) {
    val spacing = OmniTokens.spacing
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = OmniTokens.dimens.onboardingTopStartPadding, top = spacing.base, end = OmniTokens.dimens.onboardingTopEndPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(page.heroTitleRes),
            color = colorScheme.onPrimary,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )
        TextButton(onClick = onSkip, enabled = !isSaving) {
            Text(
                text = stringResource(R.string.onboarding_skip),
                color = colorScheme.onPrimary.copy(alpha = if (isSaving) 0.55f else 0.92f),
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
    val colors = OmniTokens.colors
    val spacing = OmniTokens.spacing
    val colorScheme = MaterialTheme.colorScheme
    val cardShape = MaterialTheme.shapes.extraLarge

    Column(
        modifier = modifier
            .widthIn(max = OmniTokens.dimens.authContentMaxWidth)
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(CARD_ANIMATION_DURATION_MS))
            .shadow(spacing.l, cardShape, ambientColor = colors.cardShadow)
            .clip(cardShape)
            .background(colorScheme.surface.copy(alpha = 0.98f))
            .clickable(enabled = !isSaving, onClick = onToggle)
            .padding(horizontal = OmniTokens.dimens.onboardingTopStartPadding, vertical = spacing.xl),
    ) {
        Text(
            text = stringResource(page.eyebrowRes),
            color = colorScheme.primary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(spacing.s + spacing.tiny))
        Text(
            text = stringResource(page.titleRes),
            color = colorScheme.onSurface,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(tween(CARD_ANIMATION_DURATION_MS)) + expandVertically(),
            exit = fadeOut(tween(CARD_ANIMATION_DURATION_MS)) + shrinkVertically(),
        ) {
            Column {
                Spacer(Modifier.height(spacing.s + spacing.tiny))
                Text(
                    text = stringResource(page.bodyRes),
                    color = colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        Spacer(Modifier.height(spacing.s + spacing.tiny))
        PageIndicator(
            pagerProgress = pagerProgress,
            pageCount = pageCount,
        )
        if (showPersistenceError) {
            Spacer(Modifier.height(spacing.s + spacing.tiny))
            Text(
                text = stringResource(R.string.onboarding_persistence_error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Spacer(Modifier.height(spacing.base - spacing.tiny))
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
    val spacing = OmniTokens.spacing

    Button(
        onClick = onClick,
        enabled = !loading,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = OmniTokens.dimens.authControlHeight),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(spacing.l),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = spacing.tiny,
            )
            Spacer(Modifier.width(spacing.s))
        }
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PageIndicator(pagerProgress: Float, pageCount: Int) {
    val colors = OmniTokens.colors
    val indicatorStep = OmniTokens.dimens.onboardingIndicatorActiveWidth + OmniTokens.dimens.onboardingIndicatorGap
    val indicatorDotOffset = (OmniTokens.dimens.onboardingIndicatorActiveWidth - OmniTokens.dimens.onboardingIndicatorDotSize) / 2
    val currentPage = pagerProgress.roundToInt().coerceIn(0, pageCount - 1)
    val description = stringResource(
        R.string.onboarding_page_description,
        currentPage + 1,
        pageCount,
    )
    Box(
        modifier = Modifier
            .width(OmniTokens.dimens.onboardingIndicatorActiveWidth * pageCount + OmniTokens.dimens.onboardingIndicatorGap * (pageCount - 1))
            .height(OmniTokens.dimens.onboardingIndicatorHeight)
            .semantics { contentDescription = description },
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = indicatorStep * index + indicatorDotOffset)
                    .size(OmniTokens.dimens.onboardingIndicatorDotSize)
                    .clip(CircleShape)
                    .background(colors.indicatorInactive),
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = indicatorStep * pagerProgress)
                .width(OmniTokens.dimens.onboardingIndicatorActiveWidth)
                .height(OmniTokens.dimens.onboardingIndicatorDotSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
        )
    }
}
private const val CARD_ANIMATION_DURATION_MS = 280
