package com.example.maitescalc.ui.screens.sales

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maitescalc.data.model.Sale
import com.example.maitescalc.data.model.SaleStatus
import com.example.maitescalc.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleDetailScreen(
    saleId: String,
    viewModel: SalesViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val sale = uiState.sales.find { it.id == saleId }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            MaitesTopBar(
                title = sale?.name ?: "פרטי מכירה",
                onBackClick = onNavigateBack,
                actions = {
                    if (sale != null) {
                        IconButton(onClick = onNavigateToEdit) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "ערוך מכירה",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "מחק מכירה",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (sale == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "מכירה לא נמצאה",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        } else {
            val statusColor = when (sale.status) {
                SaleStatus.DRAFT -> MaterialTheme.colorScheme.outline
                SaleStatus.CONFIRMED -> Color(0xFF42A5F5)
                SaleStatus.IN_PROGRESS -> Color(0xFFFFB74D)
                SaleStatus.COMPLETED -> MaterialTheme.colorScheme.primary
                SaleStatus.CANCELLED -> MaterialTheme.colorScheme.error
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Card
                MaitesCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sale.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Status dropdown
                            Box {
                                FilterChip(
                                    selected = true,
                                    onClick = { statusExpanded = true },
                                    label = { Text(sale.status.displayName, fontWeight = FontWeight.SemiBold) },
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = statusColor.copy(alpha = 0.15f),
                                        selectedLabelColor = statusColor
                                    )
                                )
                                DropdownMenu(
                                    expanded = statusExpanded,
                                    onDismissRequest = { statusExpanded = false },
                                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                                ) {
                                    SaleStatus.entries.forEach { s ->
                                        DropdownMenuItem(
                                            text = { Text(s.displayName) },
                                            onClick = {
                                                viewModel.updateSaleStatus(sale, s)
                                                statusExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        if (sale.customerName.isNotEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = sale.customerName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        if (sale.customerPhone.isNotEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Phone,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = sale.customerPhone,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // 3 Financial Metric Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FinancialBox(
                        title = "הכנסה",
                        price = sale.totalPrice,
                        modifier = Modifier.weight(1f)
                    )
                    FinancialBox(
                        title = "עלות",
                        price = sale.totalCost,
                        modifier = Modifier.weight(1f)
                    )
                    FinancialBox(
                        title = "רווח נקי",
                        price = sale.profit,
                        modifier = Modifier.weight(1f),
                        highlight = true
                    )
                }

                // Items list
                Text(
                    text = "פירוט פריטים ומנות (${sale.items.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                MaitesCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        sale.items.forEachIndexed { index, item ->
                            val itemTotal = item.quantity * item.unitPrice
                            val itemCost = item.quantity * item.costPerUnit
                            val itemProfit = itemTotal - itemCost

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = item.recipeName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${item.quantity} יח' x ₪${String.format("%.2f", item.unitPrice)} • רווח: ₪${String.format("%.2f", itemProfit)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                PriceDisplay(price = itemTotal, large = false)
                            }

                            if (index < sale.items.size - 1) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                            }
                        }
                    }
                }

                if (sale.notes.isNotEmpty()) {
                    Text(
                        text = "הערות",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    MaitesCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = sale.notes,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showDeleteDialog && sale != null) {
        ConfirmDialog(
            title = "מחיקת מכירה",
            message = "האם אתה בטוח שברצונך למחוק את '${sale.name}'?",
            confirmText = "מחק",
            dismissText = "ביטול",
            isDestructive = true,
            onConfirm = {
                viewModel.deleteSale(sale.id)
                showDeleteDialog = false
                onNavigateBack()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
private fun FinancialBox(
    title: String,
    price: Double,
    modifier: Modifier = Modifier,
    highlight: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(
                if (highlight) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface
            )
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = if (highlight) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            PriceDisplay(price = price, large = false)
        }
    }
}
