package incar.mobile.caring.admin.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import caring_admin.admin.generated.resources.NotoSansKR_Regular
import caring_admin.admin.generated.resources.Res
import org.jetbrains.compose.resources.Font

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
        typography = Typography(
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
