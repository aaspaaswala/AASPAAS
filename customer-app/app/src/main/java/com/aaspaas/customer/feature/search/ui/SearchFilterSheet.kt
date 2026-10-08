package com.aaspaas.customer.feature.search.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aaspaas.customer.core.ui.theme.*

// ── Filter Bottom Sheet ───────────────────────────────────────────────────────

data class SearchFilters(
    val category: String? = null,
    val maxDistanceKm: Int = 10,
    val minPrice: Float = 0f,
    val maxPrice: Float = 10000f,
    val availableOnly: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentFilters: SearchFilters,
    resultCount: Int,
    onApply: (SearchFilters) -> Unit,
    onDismiss: () -> Unit
) {
    var category by remember { mutableStateOf(currentFilters.category) }
    var distanceKm by remember { mutableIntStateOf(currentFilters.maxDistanceKm) }
    var priceRange by remember { mutableStateOf(currentFilters.minPrice..currentFilters.maxPrice) }
    var availableOnly by remember { mutableStateOf(currentFilters.availableOnly) }

    val categories = listOf("Fashion", "Electronics", "Grocery", "Beauty", "Home", "Sports")
    val distances = listOf(1, 3, 5, 10)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Filters", style = MaterialTheme.typography.titleLarge, color = TextPrimary, modifier = Modifier.weight(1f))
                TextButton(onClick = {
                    category = null
                    distanceKm = 10
                    priceRange = 0f..10000f
                    availableOnly = false
                }) {
                    Text("Reset", style = MaterialTheme.typography.labelLarge, color = Primary)
                }
            }

            Spacer(Modifier.height(20.dp))

            // Category
            Text("Category", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            Spacer(Modifier.height(10.dp))
            categories.forEach { cat ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { category = if (category == cat) null else cat }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = category == cat,
                        onClick = { category = if (category == cat) null else cat },
                        colors = RadioButtonDefaults.colors(selectedColor = Primary)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(cat, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                }
            }

            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = Border)
            Spacer(Modifier.height(20.dp))

            // Price range
            Text("Price", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("₹${priceRange.start.toInt()}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text("₹${priceRange.endInclusive.toInt()}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            RangeSlider(
                value = priceRange,
                onValueChange = { priceRange = it },
                valueRange = 0f..10000f,
                steps = 19,
                colors = SliderDefaults.colors(
                    thumbColor = Primary,
                    activeTrackColor = Primary
                )
            )

            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = Border)
            Spacer(Modifier.height(20.dp))

            // Distance
            Text("Distance", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            Spacer(Modifier.height(10.dp))
            distances.forEach { km ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { distanceKm = km }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = distanceKm == km,
                        onClick = { distanceKm = km },
                        colors = RadioButtonDefaults.colors(selectedColor = Primary)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("$km km", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                }
            }

            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = Border)
            Spacer(Modifier.height(20.dp))

            // Availability
            Text("Availability", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = availableOnly,
                    onCheckedChange = { availableOnly = it },
                    colors = CheckboxDefaults.colors(checkedColor = Primary)
                )
                Spacer(Modifier.width(8.dp))
                Text("Available now", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
            }

            Spacer(Modifier.height(24.dp))

            // Apply button
            Button(
                onClick = {
                    onApply(SearchFilters(
                        category = category,
                        maxDistanceKm = distanceKm,
                        minPrice = priceRange.start,
                        maxPrice = priceRange.endInclusive,
                        availableOnly = availableOnly
                    ))
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(
                    "Show $resultCount Results",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

// ── Sort Bottom Sheet ─────────────────────────────────────────────────────────

enum class SortOption(val label: String) {
    RELEVANCE("Relevance"),
    DISTANCE("Distance"),
    PRICE_LOW("Price: Low to High"),
    PRICE_HIGH("Price: High to Low"),
    RATING("Rating"),
    RECENTLY_ADDED("Recently Added")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    currentSort: SortOption,
    onSelect: (SortOption) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text("Sort by", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            Spacer(Modifier.height(16.dp))

            SortOption.values().forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(option); onDismiss() }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentSort == option,
                        onClick = { onSelect(option); onDismiss() },
                        colors = RadioButtonDefaults.colors(selectedColor = Primary)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(option.label, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                }
                if (option != SortOption.values().last()) {
                    HorizontalDivider(color = Border)
                }
            }
        }
    }
}

// ── Filter Chips Row (for Search Results header) ──────────────────────────────

@Composable
fun SearchFilterBar(
    activeFilters: SearchFilters,
    activeSort: SortOption,
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit,
    onDistanceClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChipItem(
            label = "Filter",
            isActive = activeFilters != SearchFilters(),
            onClick = onFilterClick
        )
        FilterChipItem(
            label = if (activeSort == SortOption.RELEVANCE) "Sort" else activeSort.label,
            isActive = activeSort != SortOption.RELEVANCE,
            onClick = onSortClick
        )
        FilterChipItem(
            label = "${activeFilters.maxDistanceKm} km",
            isActive = activeFilters.maxDistanceKm != 10,
            onClick = onDistanceClick
        )
    }
}

@Composable
private fun FilterChipItem(label: String, isActive: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isActive) Primary.copy(alpha = 0.1f) else Surface,
        modifier = Modifier
            .border(
                1.dp,
                if (isActive) Primary else Border,
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            color = if (isActive) Primary else TextPrimary
        )
    }
}
