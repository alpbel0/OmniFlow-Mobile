package com.omniflow.core.navigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavDestination.Companion.hierarchy
import com.omniflow.R
import com.omniflow.core.designsystem.theme.OmniTokens

private data class BottomNavItem(
    val route: String,
    @DrawableRes val iconRes: Int,
)

private val bottomNavItems = listOf(
    BottomNavItem(Routes.Home.route, R.drawable.ic_home),
    BottomNavItem(Routes.Explore.route, R.drawable.ic_explore),
    BottomNavItem(Routes.Trips.route, R.drawable.ic_trips),
    BottomNavItem(Routes.Community.route, R.drawable.ic_community),
)

@Composable
fun BottomNavPill(
    navController: NavHostController,
    onCreateTrip: () -> Unit,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val s = OmniTokens.spacing

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = s.m),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Box(
            modifier = Modifier.offset(y = (-s.xs)),
        ) {
            Row(
                modifier = Modifier
                    .width(336.dp)
                    .height(64.dp)
                    .shadow(s.xxl, RoundedCornerShape(32.dp))
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = s.s),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                bottomNavItems.forEachIndexed { index, item ->
                    if (index == 2) {
                        Spacer(Modifier.size(56.dp))
                    }
                    val isActive = currentDestination?.hierarchy?.any { it.route == item.route } == true
                    NavIconBtn(
                        iconRes = item.iconRes,
                        isActive = isActive,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            }

            // Center FAB — elevated above pill
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = (-s.xs))
                    .shadow(
                        elevation = s.s,
                        shape = CircleShape,
                        ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    )
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(onClick = onCreateTrip),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "Gezi oluştur",
                    tint = Color.White,
                    modifier = Modifier.size(s.xxl),
                )
            }
        }
    }
}

@Composable
private fun NavIconBtn(
    @DrawableRes iconRes: Int,
    isActive: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else Color.Transparent,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = if (isActive) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(22.dp),
        )
    }
}
