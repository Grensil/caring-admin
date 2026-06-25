package incar.mobile.caring.admin.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import caring_admin.admin.generated.resources.NotoSansKR_Regular
import caring_admin.admin.generated.resources.Res
import org.jetbrains.compose.resources.Font

// ── 5개 핵심 컬러 ───────────────────────────────────────────────────
object AdminColors {
    val Blue    = Color(0xFF76B6EE)   // 메인 브랜드 블루 — 버튼, 활성, 아이콘
    val White   = Color(0xFFFFFFFF)   // 카드, 배경
    val Dark    = Color(0xFF1A1A2E)   // 제목, 주요 텍스트
    val Gray    = Color(0xFF6C7086)   // 보조 텍스트, 비활성
    val GrayBg  = Color(0xFFF2F4F8)   // 페이지/테이블 헤더 배경, 컨테이너

    // 파생 (직접 쓰지 말고 MaterialTheme.colorScheme 통해 사용)
    internal val BlueDark   = Color(0xFF4A8EC4)  // 버튼 눌림 / 진한 강조
    internal val BlueLight  = Color(0xFFD6EAFA)  // 선택된 컨테이너 배경
}

private val AdminColorScheme = lightColorScheme(
    primary              = AdminColors.Blue,
    onPrimary            = AdminColors.White,
    primaryContainer     = AdminColors.BlueLight,
    onPrimaryContainer   = AdminColors.Dark,
    secondary            = AdminColors.BlueDark,
    onSecondary          = AdminColors.White,
    secondaryContainer   = AdminColors.GrayBg,
    onSecondaryContainer = AdminColors.Dark,
    background           = AdminColors.White,
    onBackground         = AdminColors.Dark,
    surface              = AdminColors.White,
    onSurface            = AdminColors.Dark,
    surfaceVariant       = AdminColors.GrayBg,
    onSurfaceVariant     = AdminColors.Gray,
    error                = Color(0xFFE53E3E),
    onError              = AdminColors.White,
)

@Composable
fun adminFontFamily() = FontFamily(
    Font(Res.font.NotoSansKR_Regular, weight = FontWeight.Normal),
    Font(Res.font.NotoSansKR_Regular, weight = FontWeight.Medium),
    Font(Res.font.NotoSansKR_Regular, weight = FontWeight.Bold),
)

@Composable
fun AdminTheme(content: @Composable () -> Unit) {
    val fontFamily = adminFontFamily()
    val baseTypography = MaterialTheme.typography
    MaterialTheme(
        colorScheme = AdminColorScheme,
        typography  = Typography(
            displayLarge   = baseTypography.displayLarge.copy(fontFamily = fontFamily),
            displayMedium  = baseTypography.displayMedium.copy(fontFamily = fontFamily),
            displaySmall   = baseTypography.displaySmall.copy(fontFamily = fontFamily),
            headlineLarge  = baseTypography.headlineLarge.copy(fontFamily = fontFamily),
            headlineMedium = baseTypography.headlineMedium.copy(fontFamily = fontFamily),
            headlineSmall  = baseTypography.headlineSmall.copy(fontFamily = fontFamily),
            titleLarge     = baseTypography.titleLarge.copy(fontFamily = fontFamily),
            titleMedium    = baseTypography.titleMedium.copy(fontFamily = fontFamily),
            titleSmall     = baseTypography.titleSmall.copy(fontFamily = fontFamily),
            bodyLarge      = baseTypography.bodyLarge.copy(fontFamily = fontFamily),
            bodyMedium     = baseTypography.bodyMedium.copy(fontFamily = fontFamily),
            bodySmall      = baseTypography.bodySmall.copy(fontFamily = fontFamily),
            labelLarge     = baseTypography.labelLarge.copy(fontFamily = fontFamily),
            labelMedium    = baseTypography.labelMedium.copy(fontFamily = fontFamily),
            labelSmall     = baseTypography.labelSmall.copy(fontFamily = fontFamily),
        ),
        content = content,
    )
}
