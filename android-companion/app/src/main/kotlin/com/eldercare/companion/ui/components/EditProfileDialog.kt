package com.eldercare.companion.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.eldercare.companion.localization.AppLanguage
import com.eldercare.companion.ui.theme.NHMSTheme

@Composable
fun EditProfileDialog(
    initialName: String,
    initialPhone: String,
    initialNotes: String,
    language: AppLanguage,
    onSave: (name: String, phone: String, notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var phone by remember { mutableStateOf(initialPhone) }
    var notes by remember { mutableStateOf(initialNotes) }

    val isVi = language == AppLanguage.VIETNAMESE

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Dialog Title
                Text(
                    text = if (isVi) "Chỉnh sửa thông tin ca trực" else "Edit Staff Shift Profile",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = if (isVi) "Cập nhật tên hiển thị, số liên lạc và phân công" else "Update display name, contact phone and shift notes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Full Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isVi) "Họ và tên điều dưỡng" else "Full Name") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Phone
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(if (isVi) "Số điện thoại liên lạc" else "Contact Phone") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Phone,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Shift Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(if (isVi) "Ghi chú ca trực / Khu vực" else "Shift Assignment Notes") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (isVi) "Hủy" else "Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onSave(name.trim(), phone.trim(), notes.trim())
                                onDismiss()
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        enabled = name.isNotBlank()
                    ) {
                        Text(if (isVi) "Lưu thay đổi" else "Save Changes")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileDialogPreview() {
    NHMSTheme {
        EditProfileDialog(
            initialName = "Điều dưỡng Nguyễn Thị Mai",
            initialPhone = "0912 345 678",
            initialNotes = "Phụ trách chăm sóc phân khu Lão khoa Tầng 1 (P101 - P104)",
            language = AppLanguage.VIETNAMESE,
            onSave = { _, _, _ -> },
            onDismiss = {}
        )
    }
}
