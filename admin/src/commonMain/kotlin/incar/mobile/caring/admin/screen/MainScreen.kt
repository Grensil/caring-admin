package incar.mobile.caring.admin.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import incar.mobile.caring.admin.AdminMenu
import incar.mobile.caring.admin.AdminSubMenu
import incar.mobile.caring.admin.storage.AdminStorage
import kotlin.math.roundToInt
import org.koin.compose.koinInject

@Composable
private fun HomeScreen(
    menuOrder: List<AdminMenu>,
    onMenuSelect: (AdminMenu) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
    ) {
        Text(text = "전체 메뉴", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(text = "관리할 항목을 선택하세요", color = Color(0xFF6C7086), fontSize = 13.sp)
        Spacer(modifier = Modifier.height(24.dp))

        val cols = 3
        menuOrder.chunked(cols).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                rowItems.forEach { menu ->
                    Card(
                        modifier  = Modifier.weight(1f).clickable { onMenuSelect(menu) },
                        colors    = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape     = MaterialTheme.shapes.medium,
                    ) {
                        Row(
                            modifier          = Modifier.padding(20.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier         = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFE8F0FE), shape = MaterialTheme.shapes.small),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector        = menuIcons[menu] ?: Icons.Default.Circle,
                                    contentDescription = null,
                                    tint               = Color(0xFF4A6CF7),
                                    modifier           = Modifier.size(20.dp),
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = menu.label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                if (menu.children.isNotEmpty()) {
                                    Text(text = "${menu.children.size}개 항목", color = Color(0xFF6C7086), fontSize = 12.sp)
                                }
                            }
                            Icon(
                                imageVector        = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint               = Color(0xFFBAC2DE),
                                modifier           = Modifier.size(16.dp),
                            )
                        }
                    }
                }
                repeat(cols - rowItems.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SubMenuGridScreen(
    menu: incar.mobile.caring.admin.AdminMenu,
    icon: ImageVector?,
    onSelect: (AdminSubMenu) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(32.dp),
    ) {
        // 섹션 헤더
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Box(
                    modifier         = Modifier
                        .size(40.dp)
                        .background(Color(0xFFE8F0FE), shape = MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(imageVector = icon, contentDescription = null,
                        tint = Color(0xFF4A6CF7), modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
            }
            Column {
                Text(text = menu.label, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = "항목을 선택하세요", color = Color(0xFF6C7086), fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // 서브메뉴 카드 그리드
        val cols = 3
        menu.children.chunked(cols).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                rowItems.forEach { sub ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelect(sub) },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Column(
                            modifier             = Modifier.padding(20.dp).fillMaxWidth(),
                            horizontalAlignment  = Alignment.Start,
                        ) {
                            Box(
                                modifier         = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFFF0F4FF), shape = MaterialTheme.shapes.small),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector        = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint               = Color(0xFF4A6CF7),
                                    modifier           = Modifier.size(18.dp),
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = sub.label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                    }
                }
                // 빈 칸 채우기 (마지막 행이 cols보다 적을 때)
                repeat(cols - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private val SidebarBg     = Color(0xFF1E1E2E)
private val SidebarText   = Color(0xFFCDD6F4)
private val ActiveBg      = Color(0xFF313244)
private val ActiveText    = Color(0xFF89B4FA)
private val SubBg         = Color(0xFF181825)
private val SubText       = Color(0xFFBAC2DE)
private val SubActiveText = Color(0xFF89DCEB)
private val HeaderBg      = Color(0xFF11111B)

private val menuIcons: Map<AdminMenu, ImageVector> = mapOf(
    AdminMenu.MEMBER        to Icons.Default.Person,
    AdminMenu.FA            to Icons.Default.Groups,
    AdminMenu.FA_S          to Icons.Default.Star,
    AdminMenu.ADJUSTER      to Icons.Default.LocalHospital,
    AdminMenu.USED_CAR      to Icons.Default.DirectionsCar,
    AdminMenu.CAPOS         to Icons.Default.Build,
    AdminMenu.ACCIDENT      to Icons.Default.Warning,
    AdminMenu.APP_SETTINGS  to Icons.Default.Settings,
    AdminMenu.NOTIFICATION  to Icons.Default.Notifications,
)

private const val KEY_MENU_ORDER = "menu_order"
private const val KEY_IS_COMPACT  = "is_compact"

private fun loadMenuOrder(storage: AdminStorage): List<AdminMenu> {
    val saved = storage.read(KEY_MENU_ORDER) ?: return AdminMenu.entries.toList()
    val names = saved.split(",").mapNotNull { name ->
        AdminMenu.entries.firstOrNull { it.name == name }
    }
    // 저장된 순서에 없는 새 메뉴는 뒤에 추가
    val missing = AdminMenu.entries.filter { it !in names }
    return names + missing
}

@Composable
fun MainScreen(token: String, onLogout: () -> Unit) {
    val storage: AdminStorage = koinInject()

    var selectedMenu    by remember { mutableStateOf<AdminMenu?>(null) }
    var selectedSubMenu by remember { mutableStateOf<AdminSubMenu?>(null) }
    var expandedMenu    by remember { mutableStateOf<AdminMenu?>(null) }
    var isCompact       by remember { mutableStateOf(storage.read(KEY_IS_COMPACT) == "true") }
    var menuOrder       by remember { mutableStateOf(loadMenuOrder(storage)) }
    var draggingMenu    by remember { mutableStateOf<AdminMenu?>(null) }
    var dragOffsetY     by remember { mutableStateOf(0f) }
    val itemHeights     = remember { mutableStateMapOf<AdminMenu, Float>() }
    var searchQuery by remember { mutableStateOf("") }

    // 순서 변경 시 저장
    LaunchedEffect(menuOrder) {
        storage.save(KEY_MENU_ORDER, menuOrder.joinToString(",") { it.name })
    }

    // 컴팩트 상태 변경 시 저장
    LaunchedEffect(isCompact) {
        storage.save(KEY_IS_COMPACT, isCompact.toString())
    }

    // 검색 결과: 메뉴명 또는 서브메뉴명 매칭
    val searchResults: List<Pair<AdminMenu, AdminSubMenu?>> = remember(searchQuery, menuOrder) {
        if (searchQuery.isBlank()) emptyList()
        else menuOrder.flatMap { menu ->
            val menuMatches   = menu.label.contains(searchQuery, ignoreCase = true)
            val subMatches    = menu.children.filter { it.label.contains(searchQuery, ignoreCase = true) }
            when {
                subMatches.isNotEmpty() -> subMatches.map { menu to it }
                menuMatches             -> listOf(menu to null)
                else                    -> emptyList()
            }
        }
    }

    val sidebarWidth by animateDpAsState(targetValue = if (isCompact) 60.dp else 280.dp)

    Row(modifier = Modifier.fillMaxSize()) {
        // ── 사이드바 ──────────────────────────────────────
        Column(
            modifier = Modifier
                .width(sidebarWidth)
                .fillMaxHeight()
                .background(SidebarBg),
        ) {
            // 헤더
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeaderBg)
                    .padding(horizontal = if (isCompact) 8.dp else 16.dp, vertical = 18.dp),
                verticalAlignment    = Alignment.CenterVertically,
                horizontalArrangement = if (isCompact) Arrangement.Center else Arrangement.SpaceBetween,
            ) {
                if (!isCompact) {
                    Text(
                        text       = "CARING 관리자",
                        color      = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp,
                        modifier   = Modifier.weight(1f),
                    )
                }
                IconButton(
                    onClick  = { isCompact = !isCompact },
                    modifier = Modifier.size(24.dp),
                ) {
                    Icon(
                        imageVector        = if (isCompact) Icons.Default.ChevronRight else Icons.Default.ChevronLeft,
                        contentDescription = if (isCompact) "펼치기" else "접기",
                        tint               = Color.White,
                        modifier           = Modifier.size(18.dp),
                    )
                }
            }

            // 검색창 (컴팩트 모드에서는 숨김)
            if (!isCompact) {
                OutlinedTextField(
                    value         = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder   = { Text("메뉴 검색", fontSize = 13.sp, color = Color(0xFF6C7086)) },
                    singleLine    = true,
                    leadingIcon   = {
                        Icon(Icons.Default.Search, contentDescription = null,
                            tint = Color(0xFF6C7086), modifier = Modifier.size(16.dp))
                    },
                    trailingIcon = if (searchQuery.isNotEmpty()) ({
                        IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "초기화",
                                tint = Color(0xFF6C7086), modifier = Modifier.size(14.dp))
                        }
                    }) else null,
                    colors    = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = ActiveText,
                        unfocusedBorderColor = Color(0xFF313244),
                        focusedTextColor     = SidebarText,
                        unfocusedTextColor   = SidebarText,
                        cursorColor          = ActiveText,
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
                    modifier  = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 메뉴 리스트 / 검색 결과
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                if (searchQuery.isNotBlank()) {
                    // ── 검색 결과 ──
                    if (searchResults.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                            Text("검색 결과 없음", color = Color(0xFF6C7086), fontSize = 12.sp)
                        }
                    } else {
                        searchResults.forEach { (menu, sub) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (sub != null) {
                                            selectedMenu    = menu
                                            selectedSubMenu = sub
                                            expandedMenu    = menu
                                        } else {
                                            selectedMenu    = menu
                                            selectedSubMenu = null
                                            expandedMenu    = if (menu.children.isNotEmpty()) menu else null
                                        }
                                        searchQuery = ""
                                    }
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector        = if (sub != null) Icons.Default.SubdirectoryArrowRight else (menuIcons[menu] ?: Icons.Default.Circle),
                                    contentDescription = null,
                                    tint               = if (sub != null) SubActiveText else ActiveText,
                                    modifier           = Modifier.size(14.dp),
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    if (sub != null) {
                                        Text(text = menu.label, color = Color(0xFF6C7086), fontSize = 10.sp)
                                        Text(text = sub.label, color = SubActiveText, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    } else {
                                        Text(text = menu.label, color = ActiveText, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                            HorizontalDivider(color = Color(0xFF313244).copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 12.dp))
                        }
                    }
                    return@Column
                }

                menuOrder.forEach { menu ->
                    val isDragging  = draggingMenu == menu
                    val isActive    = menu == selectedMenu
                    val isExpanded  = menu == expandedMenu
                    val hasChildren = menu.children.isNotEmpty()
                    val icon        = menuIcons[menu]

                    key(menu) {
                        Column(
                            modifier = Modifier
                                .zIndex(if (isDragging) 1f else 0f)
                                .offset { IntOffset(0, if (isDragging) dragOffsetY.roundToInt() else 0) }
                                .onGloballyPositioned { coords ->
                                    itemHeights[menu] = coords.size.height.toFloat()
                                },
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        when {
                                            isDragging               -> ActiveBg.copy(alpha = 0.8f)
                                            isActive && !hasChildren -> ActiveBg
                                            else                     -> Color.Transparent
                                        }
                                    )
                                    .clickable {
                                        if (hasChildren) {
                                            expandedMenu    = if (isExpanded && !isCompact) null else menu
                                            selectedMenu    = menu
                                            selectedSubMenu = null
                                        } else {
                                            selectedMenu    = menu
                                            selectedSubMenu = null
                                            expandedMenu    = null
                                        }
                                    }
                                    .padding(
                                        start  = if (isCompact) 0.dp else 12.dp,
                                        end    = if (isCompact) 0.dp else 16.dp,
                                        top    = 14.dp,
                                        bottom = 14.dp,
                                    ),
                                verticalAlignment    = Alignment.CenterVertically,
                                horizontalArrangement = if (isCompact) Arrangement.Center else Arrangement.Start,
                            ) {
                                // 드래그 핸들 (펼침 모드만)
                                if (!isCompact) {
                                    Icon(
                                        imageVector        = Icons.Default.DragHandle,
                                        contentDescription = "순서 변경",
                                        tint               = Color(0xFF45475A),
                                        modifier           = Modifier
                                            .size(18.dp)
                                            .pointerInput(menu) {
                                                detectDragGestures(
                                                    onDragStart = {
                                                        draggingMenu = menu
                                                        dragOffsetY  = 0f
                                                    },
                                                    onDragEnd = {
                                                        draggingMenu = null
                                                        dragOffsetY  = 0f
                                                    },
                                                    onDragCancel = {
                                                        draggingMenu = null
                                                        dragOffsetY  = 0f
                                                    },
                                                    onDrag = { change, dragAmount ->
                                                        change.consume()
                                                        dragOffsetY += dragAmount.y

                                                        val currentMenu = draggingMenu ?: return@detectDragGestures
                                                        val currentIdx  = menuOrder.indexOf(currentMenu)
                                                        if (currentIdx < 0) return@detectDragGestures
                                                        val currentH = itemHeights[currentMenu] ?: 48f

                                                        if (dragOffsetY > currentH / 2f) {
                                                            val nextIdx = currentIdx + 1
                                                            if (nextIdx < menuOrder.size) {
                                                                val nextMenu = menuOrder[nextIdx]
                                                                menuOrder = menuOrder.toMutableList().apply {
                                                                    this[currentIdx] = nextMenu
                                                                    this[nextIdx]    = currentMenu
                                                                }
                                                                dragOffsetY -= (itemHeights[nextMenu] ?: currentH)
                                                            }
                                                        } else if (dragOffsetY < -currentH / 2f) {
                                                            val prevIdx = currentIdx - 1
                                                            if (prevIdx >= 0) {
                                                                val prevMenu = menuOrder[prevIdx]
                                                                menuOrder = menuOrder.toMutableList().apply {
                                                                    this[currentIdx] = prevMenu
                                                                    this[prevIdx]    = currentMenu
                                                                }
                                                                dragOffsetY += (itemHeights[prevMenu] ?: currentH)
                                                            }
                                                        }
                                                    },
                                                )
                                            },
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }

                                // 아이콘
                                if (icon != null) {
                                    Icon(
                                        imageVector        = icon,
                                        contentDescription = menu.label,
                                        tint               = if (isActive) ActiveText else SidebarText,
                                        modifier           = Modifier.size(18.dp),
                                    )
                                    if (!isCompact) Spacer(modifier = Modifier.width(8.dp))
                                }

                                // 라벨 + 드롭다운 화살표 (펼침 모드만)
                                if (!isCompact) {
                                    Text(
                                        text       = menu.label,
                                        color      = if (isActive) ActiveText else SidebarText,
                                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                                        fontSize   = 14.sp,
                                        modifier   = Modifier.weight(1f),
                                    )
                                    if (hasChildren) {
                                        Icon(
                                            imageVector        = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = null,
                                            tint               = SidebarText,
                                            modifier           = Modifier.size(16.dp),
                                        )
                                    }
                                }
                            }

                            // 서브메뉴 (펼침 모드 + 확장 상태만)
                            if (hasChildren && !isCompact) {
                                AnimatedVisibility(
                                    visible = isExpanded,
                                    enter   = expandVertically(),
                                    exit    = shrinkVertically(),
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
                                                    text       = "· ${sub.label}",
                                                    color      = if (isSubActive) SubActiveText else SubText,
                                                    fontWeight = if (isSubActive) FontWeight.Medium else FontWeight.Normal,
                                                    fontSize   = 13.sp,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 로그아웃
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLogout() }
                    .padding(
                        horizontal = if (isCompact) 0.dp else 20.dp,
                        vertical   = 16.dp,
                    ),
                contentAlignment = if (isCompact) Alignment.Center else Alignment.CenterStart,
            ) {
                if (isCompact) {
                    Icon(
                        imageVector        = Icons.Default.Logout,
                        contentDescription = "로그아웃",
                        tint               = Color(0xFFF38BA8),
                        modifier           = Modifier.size(18.dp),
                    )
                } else {
                    Text(text = "로그아웃", color = Color(0xFFF38BA8), fontSize = 14.sp)
                }
            }
        }

        // ── 콘텐츠 영역 ───────────────────────────────────
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            // 브레드크럼
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 28.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 홈
                Text(
                    text       = "홈",
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = if (selectedMenu == null) FontWeight.Bold else FontWeight.Normal,
                    color      = if (selectedMenu == null) Color(0xFF1A1A2E) else Color(0xFF6C7086),
                    modifier   = if (selectedMenu != null) Modifier.clickable {
                        selectedMenu    = null
                        selectedSubMenu = null
                        expandedMenu    = null
                    } else Modifier,
                )
                if (selectedMenu != null) {
                    Text("  >  ", color = Color(0xFF6C7086), fontSize = 18.sp)
                    Text(
                        text       = selectedMenu!!.label,
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = if (selectedSubMenu == null) FontWeight.Bold else FontWeight.Normal,
                        color      = if (selectedSubMenu == null) Color(0xFF1A1A2E) else Color(0xFF6C7086),
                        modifier   = if (selectedSubMenu != null) Modifier.clickable { selectedSubMenu = null } else Modifier,
                    )
                }
                if (selectedSubMenu != null) {
                    Text("  >  ", color = Color(0xFF6C7086), fontSize = 18.sp)
                    Text(
                        text       = selectedSubMenu!!.label,
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color      = Color(0xFF1A1A2E),
                    )
                }
            }
            HorizontalDivider()

            when (selectedSubMenu) {
                AdminSubMenu.ADJUSTER_LIST       -> AdjusterListScreen(token = token, onLogout = {})
                AdminSubMenu.CONSULTING_REQUESTS -> ConsultingRequestScreen(token = token)
                AdminSubMenu.EDUCATION_REQUESTS  -> EducationRequestScreen(token = token)
                null -> {
                    val menu = selectedMenu
                    when {
                        menu == null -> HomeScreen(
                            menuOrder    = menuOrder,
                            onMenuSelect = { selected ->
                                selectedMenu    = selected
                                selectedSubMenu = null
                                expandedMenu    = selected
                            },
                        )
                        menu.children.isNotEmpty() -> SubMenuGridScreen(
                            menu     = menu,
                            icon     = menuIcons[menu],
                            onSelect = { sub -> selectedSubMenu = sub },
                        )
                        else -> PlaceholderScreen()
                    }
                }
                else -> PlaceholderScreen()
            }
        }
    }
}