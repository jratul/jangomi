package com.example.jangomi.domain.model

enum class Category(val label: String, val type: TransactionType) {
    FOOD("식비", TransactionType.EXPENSE),
    TRANSPORT("교통", TransactionType.EXPENSE),
    SHOPPING("쇼핑", TransactionType.EXPENSE),
    CULTURE("문화/여가", TransactionType.EXPENSE),
    MEDICAL("의료", TransactionType.EXPENSE),
    HOUSING("주거", TransactionType.EXPENSE),
    COMMUNICATION("통신", TransactionType.EXPENSE),
    FINANCE("금융", TransactionType.EXPENSE),
    EDUCATION("교육", TransactionType.EXPENSE),
    OTHER_EXPENSE("기타", TransactionType.EXPENSE),
    SALARY("급여", TransactionType.INCOME),
    SIDE_JOB("부업", TransactionType.INCOME),
    ALLOWANCE("용돈", TransactionType.INCOME),
    FINANCIAL_INCOME("금융수입", TransactionType.INCOME),
    OTHER_INCOME("기타수입", TransactionType.INCOME);

    companion object {
        fun forType(type: TransactionType): List<Category> =
            entries.filter { it.type == type }

        fun defaultFor(type: TransactionType): Category =
            if (type == TransactionType.EXPENSE) FOOD else SALARY
    }
}
