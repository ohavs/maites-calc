package com.example.maitescalc.ui.components

import androidx.compose.animation.*
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
            .clip(RoundedCornerShape(percent = 50))
            .background(color = MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    } else {
        Modifier
    }

    Row(
        modifier = modifier.then(bgModifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "₪",
            style = if (large) MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp)
            else MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
            color = if (showBackground) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.width(4.dp))

        AnimatedContent(
            targetState = CostCalculator.formatPriceShort(price),
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInVertically { height -> height } + fadeIn()).togetherWith(
                        slideOutVertically { height -> -height } + fadeOut()
                    )
                } else {
                    (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                        slideOutVertically { height -> height } + fadeOut()
                    )
                }
            },
            label = "priceAnimation"
        ) { animatedPrice ->
            Text(
                text = animatedPrice,
                style = if (large) MaterialTheme.typography.displayMedium.copy(fontSize = 32.sp)
                else MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                color = if (showBackground) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}
