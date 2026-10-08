package com.aaspaas.customer.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aaspaas.customer.core.ui.theme.*
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val visual: @Composable () -> Unit
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pages = listOf(
        OnboardingPage(
            emoji = "🔍",
            title = "Find what you need nearby.",
            subtitle = "Search any product and instantly see which stores around you have it in stock.",
            visual = { OnboardingVisual1() }
        ),
        OnboardingPage(
            emoji = "🏪",
            title = "Compare nearby stores",
            subtitle = "See prices from multiple stores and pick the best deal closest to you.",
            visual = { OnboardingVisual2() }
        ),
        OnboardingPage(
            emoji = "✅",
            title = "Reserve. Visit. Done.",
            subtitle = "Hold the product for 6 hours. Walk in, try it, buy it — no delivery wait.",
            visual = { OnboardingVisual3() }
        )
    )

    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Skip button
        TextButton(
            onClick = onFinish,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text("Skip", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPageContent(pages[page])
            }

            // Dot indicators
            Row(
                modifier = Modifier.padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pages.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 24.dp else 8.dp, 8.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Primary else Border)
                    )
                }
            }

            // CTA button
            Button(
                onClick = {
                    if (pagerState.currentPage < pages.size - 1) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onFinish()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(
                    text = when (pagerState.currentPage) {
                        0 -> "Get Started"
                        pages.size - 1 -> "Start Exploring"
                        else -> "Next"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Visual area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            contentAlignment = Alignment.Center
        ) {
            page.visual()
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = TextPrimary
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = page.subtitle,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = TextSecondary
        )
    }
}

// ── Onboarding Visuals ────────────────────────────────────────────────────────

@Composable
private fun OnboardingVisual1() {
    // Product discovery concept
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("🔍", style = MaterialTheme.typography.displayLarge)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("👟", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Nike Running Shoes", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                    Text("3 stores nearby", style = MaterialTheme.typography.bodySmall, color = Primary)
                }
                Text("₹2,499", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            }
        }
    }
}

@Composable
private fun OnboardingVisual2() {
    // Store comparison concept
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Nike Running Shoes", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Text("Nearby Stores", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Spacer(Modifier.height(4.dp))

        listOf(
            Triple("Store A", "₹749", true),
            Triple("Store B", "₹799", false),
            Triple("Store C", "₹829", false)
        ).forEach { (name, price, isBest) ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isBest) Primary.copy(alpha = 0.06f) else Surface,
                tonalElevation = if (isBest) 0.dp else 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🏪", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.width(10.dp))
                    Text(name, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, modifier = Modifier.weight(1f))
                    Text(price, style = MaterialTheme.typography.titleSmall, color = if (isBest) Primary else TextPrimary)
                    if (isBest) {
                        Spacer(Modifier.width(8.dp))
                        Surface(shape = RoundedCornerShape(4.dp), color = Success.copy(alpha = 0.12f)) {
                            Text("BEST", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = Success)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingVisual3() {
    // Reservation concept
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(50.dp),
            color = Success.copy(alpha = 0.12f),
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("✅", style = MaterialTheme.typography.displaySmall)
            }
        }
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Reservation confirmed", style = MaterialTheme.typography.titleSmall, color = Color.White)
                Spacer(Modifier.height(4.dp))
                Text("Reserved until 8:30 PM", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("Reserve 🔒", "Visit 🚶", "Buy ✓").forEach { step ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Surface,
                    tonalElevation = 1.dp,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        step,
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
