# Jangomi — 모바일 가계부 앱

## 프로젝트 개요

Kotlin 기반 Android 가계부 앱. 개인 전용 로컬 가계부.

- **패키지**: `com.example.jangomi`
- **minSdk**: 24 (Android 7.0)
- **targetSdk**: 36
- **UI**: Jetpack Compose + Material3

## 기술 스택

- Kotlin + Jetpack Compose
- Material3
- Hilt (DI)
- Room (로컬 DB)
- DataStore (설정 저장)
- Navigation Compose (Single Activity)
- MVVM + Repository 패턴

## 디자인 시스템

Toss 스타일. mokona-ui(https://github.com/jratul/mokona-ui) 참고.

- **폰트**: Pretendard (TTF 번들)
- **Primary color**: #3182F6 (Toss Blue)
- **색상 토큰**: Color.kt / Typography.kt / Shape.kt / Spacing.kt / Motion.kt
- **테마 진입점**: `JangomiTheme` composable
- 컴포넌트는 `ui/components/`에 모아두고, 화면에서 직접 MaterialTheme 값을 쓰지 않는다.

## 패키지 구조

```
com.example.jangomi/
├── ui/
│   ├── theme/          # Color, Typography, Shape, Spacing, Motion, Theme
│   ├── components/     # 재사용 컴포넌트 (JangomiButton, AmountText 등)
│   ├── navigation/     # NavGraph, Screen route 정의
│   ├── home/
│   ├── transaction/
│   ├── statistics/
│   └── settings/
├── domain/
│   ├── model/          # Transaction, Category, BudgetSummary
│   └── repository/     # Repository 인터페이스
└── data/
    ├── local/          # Room DB, DAO, Entity
    ├── repository/     # Repository 구현체
    ├── di/             # Hilt 모듈
    └── datastore/      # 설정 DataStore
```

## 화면 구성

### MVP
| 화면 | 설명 |
|---|---|
| 홈 | 이번 달 수입/지출 요약, 최근 거래 내역 |
| 거래 내역 | 날짜별 리스트, 키워드 검색 (상호명·메모·카테고리) |
| 거래 입력/수정 | 금액, 카테고리, 상호명, 메모, 날짜, 수입/지출 구분 |
| 통계 | 월별/연도별 카테고리 차트 |

### v1.1 예정
- 예산 설정 (카테고리별 월 예산)
- 카테고리 커스터마이징
- 고정 지출 등록 (반복 거래)

## 데이터 모델

- `amount`: `Long` (KRW, 소수점 없음 — Double/Float 금지)
- `date`: `LocalDate` / `LocalDateTime` (coreLibraryDesugaring 적용)
- 기본 카테고리: 식비, 교통, 쇼핑, 문화/여가, 의료, 주거, 통신, 금융, 교육, 기타 (지출) / 급여, 부업, 용돈, 금융수입, 기타수입 (수입)

## 코드 규칙

- Fragment 없음. Single Activity + NavHost.
- 화면(Screen)은 렌더링만 담당. 비즈니스 로직은 ViewModel + Repository로 분리.
- ViewModel은 `StateFlow<UiState>` + `fun onEvent(UiEvent)` 패턴 사용.
- 통계 차트는 외부 라이브러리 없이 Compose Canvas로 직접 구현.
- 작업 완료 전 빌드 에러·lint 경고를 모두 제거한다.
