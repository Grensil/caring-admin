package incar.mobile.caring.admin.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import incar.mobile.caring.admin.AdminMenu
import incar.mobile.caring.admin.AdminSubMenu

private val SidebarBg     = Color(0xFF1E1E2E)
private val SidebarText   = Color(0xFFCDD6F4)
private val ActiveBg      = Color(0xFF313244)
private val ActiveText    = Color(0xFF89B4FA)
private val SubBg         = Color(0xFF181825)
private val SubText       = Color(0xFFBAC2DE)
private val SubActiveText = Color(0xFF89DCEB)
private val HeaderBg      = Color(0xFF11111B)

@Composable
fun MainScreen(token: String, onLogout: () -> Unit) {
    var selectedMenu    by remember { mutableStateOf(AdminMenu.ADJUSTER) }
    var selectedSubMenu by remember { mutableStateOf<AdminSubMenu?>(AdminSubMenu.ADJUSTER_LIST) }
    var expandedMenu    by remember { mutableStateOf<AdminMenu?>(AdminMenu.ADJUSTER) }

    Row(modifier = Modifier.fillMaxSize()) {
            // ── 사이드바 ──────────────────────────────────────
            Column(
                modifier = Modifier
                    .width(220.dp)
                    .fillMaxHeight()
                    .background(SidebarBg),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HeaderBg)
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                ) {
                    Text(
                        text = "CARING 관리자",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 메뉴 목록 - 스크롤 가능
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                ) {
                    AdminMenu.entries.forEach { menu ->
                        val isActive    = menu == selectedMenu
                        val isExpanded  = menu == expandedMenu
                        val hasChildren = menu.children.isNotEmpty()

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isActive && !hasChildren) ActiveBg else Color.Transparent)
                                .clickable {
                                    if (hasChildren) {
                                        expandedMenu = if (isExpanded) null else menu
                                        selectedMenu = menu
                                    } else {
                                        selectedMenu    = menu
                                        selectedSubMenu = null
                                        expandedMenu    = null
                                    }
                                }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = menu.label,
                                color = if (isActive) ActiveText else SidebarText,
                                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f),
                            )
                            if (hasChildren) {
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = SidebarText,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }

                        if (hasChildren) {
                            AnimatedVisibility(
                                visible = isExpanded,
                                enter = expandVertically(),
                                exit = shrinkVertically(),
                            ) {
                                Column(modifier = Modifier.background(SubBg)) {
                                    menu.children.forEach { sub ->
                                        val isSubActive = selectedSubMenu == sub
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(if (isSubActive) ActiveBg else Color.Transparent)
                                                .clickable {
                                                    selectedSubMenu = sub
                                                    selectedMenu    = menu
                                                }
                                                .padding(start = 36.dp, end = 20.dp, top = 11.dp, bottom = 11.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text(
                                                text = "· ${sub.label}",
                                                color = if (isSubActive) SubActiveText else SubText,
                                                fontWeight = if (isSubActive) FontWeight.Medium else FontWeight.Normal,
                                                fontSize = 13.sp,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLogout() }
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                ) {
                    Text(text = "로그아웃", color = Color(0xFFF38BA8), fontSize = 14.sp)
                }
            }

            // ── 콘텐츠 영역 ───────────────────────────────────
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                val headerTitle = selectedSubMenu?.label ?: selectedMenu.label
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 28.dp, vertical = 16.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        text = headerTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
                HorizontalDivider()

                when (selectedSubMenu) {
                    AdminSubMenu.ADJUSTER_LIST       -> AdjusterListScreen(token = token, onLogout = {})
                    AdminSubMenu.CONSULTING_REQUESTS -> ConsultingRequestScreen(token = token)
                    AdminSubMenu.EDUCATION_REQUESTS  -> EducationRequestScreen(token = token)
                    null -> when (selectedMenu) {
                        AdminMenu.USER_TYPE    -> UserTypeScreen(token)
                        AdminMenu.PUSH         -> PushScreen(token)
                        AdminMenu.FORCE_UPDATE -> ForceUpdateScreen(token)
                        AdminMenu.ADJUSTER     -> AdjusterListScreen(token = token, onLogout = {})
                    }
                }
            }
        }
}
