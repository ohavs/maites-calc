package com.example.maitescalc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            .clip(RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    } else {
        Modifier
    }

    Row(
        modifier = modifier.then(bgModifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "₪",
            style = if (large) MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
            else MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
            color = if (showBackground) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(3.dp))

        Text(
            text = CostCalculator.formatPriceShort(price),
            style = if (large) MaterialTheme.typography.displaySmall.copy(fontSize = 28.sp)
            else MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
            color = if (showBackground) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
