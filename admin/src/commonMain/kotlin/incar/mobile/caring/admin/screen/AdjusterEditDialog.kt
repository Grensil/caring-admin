package incar.mobile.caring.admin.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import incar.mobile.caring.admin.model.Adjuster

@Composable
fun AdjusterEditDialog(
    adjuster: Adjuster,
    isSaving: Boolean,
    onSave: (Map<String, Any?>) -> Unit,
    onDismiss: () -> Unit,
) {
    var name        by remember { mutableStateOf(adjuster.name) }
    var company     by remember { mutableStateOf(adjuster.company) }
    var phone       by remember { mutableStateOf(adjuster.phone) }
    var officePhone by remember { mutableStateOf(adjuster.officePhone ?: "") }
    var fax         by remember { mutableStateOf(adjuster.fax ?: "") }
    var email       by remember { mutableStateOf(adjuster.email ?: "") }
    var address     by remember { mutableStateOf(adjuster.address ?: "") }
    var addressDetail by remember { mutableStateOf(adjuster.addressDetail ?: "") }
    var careerYears by remember { mutableStateOf(adjuster.careerYears.toString()) }
    var isVisible   by remember { mutableStateOf(adjuster.isVisible) }
    var qualifications by remember { mutableStateOf(adjuster.qualifications.joinToString(", ")) }
    var mainCareer  by remember { mutableStateOf(adjuster.mainCareer ?: "") }

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        confirmButton = {
            Button(
                onClick = {
                    val fields = mutableMapOf<String, Any?>()
                    fields["name"]           = name.trim()
                    fields["company"]        = company.trim()
                    fields["phone"]          = phone.trim()
                    fields["office_phone"]   = officePhone.trim().ifEmpty { null }
                    fields["fax"]            = fax.trim().ifEmpty { null }
                    fields["email"]          = email.trim().ifEmpty { null }
                    fields["address"]        = address.trim().ifEmpty { null }
                    fields["address_detail"] = addressDetail.trim().ifEmpty { null }
                    fields["career_years"]   = careerYears.trim().toIntOrNull() ?: adjuster.careerYears
                    fields["is_visible"]     = isVisible
                    fields["qualifications"] = qualifications.split(",")
                        .map { it.trim() }.filter { it.isNotEmpty() }
                    fields["main_career"]    = mainCareer.trim().ifEmpty { null }
                    onSave(fields)
                },
                enabled = !isSaving,
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                } else {
                    Text("저장")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, enabled = !isSaving) { Text("취소") }
        },
        title = { Text("손해사정사 정보 편집 — ${adjuster.name}") },
        text = {
            Column(
                modifier = Modifier
                    .width(560.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    EditField("이름 *", name, Modifier.weight(1f)) { name = it }
                    EditField("소속", company, Modifier.weight(1f)) { company = it }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    EditField("연락처 *", phone, Modifier.weight(1f)) { phone = it }
                    EditField("사무실 전화", officePhone, Modifier.weight(1f)) { officePhone = it }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    EditField("팩스", fax, Modifier.weight(1f)) { fax = it }
                    EditField("이메일", email, Modifier.weight(1f)) { email = it }
                }
                EditField("주소", address, Modifier.fillMaxWidth()) { address = it }
                EditField("상세주소", addressDetail, Modifier.fillMaxWidth()) { addressDetail = it }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    EditField("경력(년)", careerYears, Modifier.weight(1f)) { careerYears = it }
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Switch(checked = isVisible, onCheckedChange = { isVisible = it })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (isVisible) "노출" else "숨김",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
                EditField(
                    "자격증 (쉼표 구분)",
                    qualifications,
                    Modifier.fillMaxWidth(),
                ) { qualifications = it }
                EditField(
                    "주요 경력",
                    mainCareer,
                    Modifier.fillMaxWidth(),
                    singleLine = false,
                ) { mainCareer = it }

                HorizontalDivider()
                Text(
                    "* 수정 불가: 평점, 리뷰수, 담당지역, 교육분야, 계정연결 (별도 기능으로 제공 예정)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
    )
}

@Composable
private fun EditField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        singleLine = singleLine,
        modifier = modifier,
        minLines = if (singleLine) 1 else 3,
    )
}
