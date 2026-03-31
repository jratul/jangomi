package com.example.jangomi.ui.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jangomi.ui.components.formatAmount
import com.example.jangomi.ui.theme.CategoryColors
import com.example.jangomi.ui.theme.ExpenseRed
import com.example.jangomi.ui.theme.IncomeBlue
import com.example.jangomi.ui.theme.Spacing
import com.example.jangomi.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("통계") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                // 월/연도별 뷰 선택
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md, vertical = Spacing.sm)
                ) {
                    StatisticsViewMode.entries.forEachIndexed { index, mode ->
                        SegmentedButton(
                            selected = state.viewMode == mode,
                            onClick = {
                                viewModel.onEvent(StatisticsUiEvent.ViewModeChanged(mode))
                            },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = StatisticsViewMode.entries.size
                            ),
                            label = { Text(if (mode == StatisticsViewMode.MONTHLY) "월별" else "연도별") }
                        )
                    }
                }
            }

            item {
                // 기간 네비게이션
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        viewModel.onEvent(
                            if (state.viewMode == StatisticsViewMode.MONTHLY)
                                StatisticsUiEvent.PreviousMonth
                            else StatisticsUiEvent.PreviousYear
                        )
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "이전")
                    }
                    Text(
                        text = if (state.viewMode == StatisticsViewMode.MONTHLY)
                            "${state.year}년 ${state.month}월"
                        else "${state.year}년",
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(onClick = {
                        viewModel.onEvent(
                            if (state.viewMode == StatisticsViewMode.MONTHLY)
                                StatisticsUiEvent.NextMonth
                            else StatisticsUiEvent.NextYear
                        )
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "다음")
                    }
                }
            }

            if (state.viewMode == StatisticsViewMode.MONTHLY) {
                item {
                    // 지출 도넛 차트
                    if (state.expenseBreakdown.isNotEmpty()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.md),
                            shape = MaterialTheme.shapes.large,
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 1.dp
                        ) {
                            Column(modifier = Modifier.padding(Spacing.md)) {
                                Text(
                                    text = "지출 카테고리",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = state.totalExpense.formatAmount(),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = ExpenseRed
                                )
                                Spacer(Modifier.height(Spacing.md))
                                DonutChart(
                                    data = state.expenseBreakdown.map { it.amount.toFloat() },
                                    colors = CategoryColors.take(state.expenseBreakdown.size),
                                    modifier = Modifier
                                        .size(180.dp)
                                        .align(Alignment.CenterHorizontally)
                                )
                                Spacer(Modifier.height(Spacing.md))
                            }
                        }
                    }
                }

                // 지출 카테고리 목록
                items(state.expenseBreakdown.take(10)) { item ->
                    val index = state.expenseBreakdown.indexOf(item)
                    val color = CategoryColors.getOrElse(index) { Color.Gray }
                    CategoryRow(
                        label = item.category.label,
                        amount = item.amount,
                        total = state.totalExpense,
                        color = color
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = Spacing.md),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                }
            } else {
                // 연도별 월간 막대 차트
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md),
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(Spacing.md)) {
                            Text(
                                text = "${state.year}년 월별 현황",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(Modifier.height(Spacing.md))
                            MonthlyBarChart(
                                data = state.monthlyTrend,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(Spacing.lg)) }
        }
    }
}

@Composable
private fun DonutChart(
    data: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val total = data.sum().takeIf { it > 0f } ?: 1f
    Canvas(modifier = modifier) {
        val strokeWidth = size.minDimension * 0.18f
        val radius = (size.minDimension - strokeWidth) / 2f
        val topLeft = Offset(center.x - radius, center.y - radius)
        val arcSize = Size(radius * 2, radius * 2)
        var startAngle = -90f
        data.forEachIndexed { index, value ->
            val sweep = (value / total) * 360f
            drawArc(
                color = colors.getOrElse(index) { Color.Gray },
                startAngle = startAngle,
                sweepAngle = sweep - 1f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth)
            )
            startAngle += sweep
        }
    }
}

@Composable
private fun MonthlyBarChart(
    data: List<MonthlyAmount>,
    modifier: Modifier = Modifier
) {
    val maxAmount = data.maxOf { maxOf(it.income, it.expense) }.takeIf { it > 0L } ?: 1L

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val barAreaWidth = size.width / 12
            val maxHeight = size.height * 0.85f
            val baselineY = size.height

            data.forEach { monthly ->
                val centerX = barAreaWidth * (monthly.month - 1) + barAreaWidth / 2
                val barWidth = barAreaWidth * 0.35f

                // 지출 막대
                val expenseHeight = (monthly.expense.toFloat() / maxAmount) * maxHeight
                if (expenseHeight > 0f) {
                    drawRect(
                        color = ExpenseRed.copy(alpha = 0.8f),
                        topLeft = Offset(centerX - barWidth, baselineY - expenseHeight),
                        size = Size(barWidth, expenseHeight)
                    )
                }

                // 수입 막대
                val incomeHeight = (monthly.income.toFloat() / maxAmount) * maxHeight
                if (incomeHeight > 0f) {
                    drawRect(
                        color = IncomeBlue.copy(alpha = 0.8f),
                        topLeft = Offset(centerX, baselineY - incomeHeight),
                        size = Size(barWidth, incomeHeight)
                    )
                }
            }
        }

        // 월 레이블
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            (1..12).forEach { month ->
                Text(
                    text = "${month}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    modifier = Modifier.width(20.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun CategoryRow(
    label: String,
    amount: Long,
    total: Long,
    color: Color
) {
    val ratio = if (total > 0) amount.toFloat() / total else 0f
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Canvas(modifier = Modifier.size(10.dp)) {
                drawCircle(color = color)
            }
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = amount.formatAmount(), style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "%.1f%%".format(ratio * 100),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}
