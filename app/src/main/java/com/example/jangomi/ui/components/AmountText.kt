package com.example.jangomi.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.jangomi.domain.model.TransactionType
import com.example.jangomi.ui.theme.ExpenseRed
import com.example.jangomi.ui.theme.IncomeBlue
import java.text.NumberFormat
import java.util.Locale

fun Long.formatAmount(): String =
    NumberFormat.getNumberInstance(Locale.KOREA).format(this) + "원"

@Composable
fun AmountText(
    amount: Long,
    type: TransactionType,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleLarge,
    showSign: Boolean = true
) {
    val color = if (type == TransactionType.EXPENSE) ExpenseRed else IncomeBlue
    val sign = if (showSign) if (type == TransactionType.EXPENSE) "-" else "+" else ""
    Text(
        text = "$sign${amount.formatAmount()}",
        style = style.copy(fontWeight = FontWeight.SemiBold),
        color = color,
        modifier = modifier
    )
}

@Composable
fun AmountText(
    amount: Long,
    color: Color,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleLarge
) {
    Text(
        text = amount.formatAmount(),
        style = style.copy(fontWeight = FontWeight.SemiBold),
        color = color,
        modifier = modifier
    )
}
