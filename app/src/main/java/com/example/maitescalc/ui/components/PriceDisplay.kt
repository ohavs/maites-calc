package com.example.maitescalc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maitescalc.util.CostCalculator

@Composable
fun PriceDisplay(
    price: Double,
    modifier: Modifier = Modifier,
    large: Boolean = false,
    showBackground: Boolean = false
) {
    val bgModifier = if (showBackground) {
        Modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    } else {
        Modifier
    }

    Row(
        modifier = modifier.then(bgModifier),
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = "₪",
            style = if (large) MaterialTheme.typography.titleMedium
                    else MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = CostCalculator.formatPriceShort(price),
            style = if (large) MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp)
                    else MaterialTheme.typography.titleLarge,
            color = if (showBackground) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}
