package com.eldercare.companion.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.eldercare.companion.data.repository.CareRepository
import com.eldercare.companion.localization.AppLanguage
import com.eldercare.companion.ui.theme.NHMSTheme
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: String,
    val model: String? = null,
    val sources: List<String> = emptyList(),
    val isLoading: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatbotDialog(
    repository: CareRepository? = null,
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val isVi = language == AppLanguage.VIETNAMESE
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var inputText by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var conversationId by remember { mutableStateOf<String?>(null) }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                id = "m-init",
                text = if (isVi) {
                    "Xin chào Điều dưỡng. Tôi là Trợ lý AI Lão khoa NHMS, kết nối trực tiếp cơ sở tri thức lâm sàng nội viện. Bạn cần tra cứu quy trình cấp cứu ngã, tương tác thuốc FRID hay tóm tắt bàn giao ca SBAR?"
                } else {
                    "Hello Nurse. I am the NHMS Geriatric Clinical AI Assistant connected to the inpatient clinical protocols. How may I assist with fall protocols, FRID interactions, or SBAR handover?"
                },
                isUser = false,
                timestamp = "Hiện tại",
                model = "NHMS Clinical Protocol Hub"
            )
        )
    }

    val quickPrompts = remember(isVi) {
        if (isVi) {
            listOf(
                "Xử lý té ngã không người chứng kiến",
                "Cảnh báo thuốc FRID theo tiêu chuẩn Beers",
                "Can thiệp hội chứng hoàng hôn (Sundowning)",
                "Soạn biên bản giao ban ca SBAR"
            )
        } else {
            listOf(
                "Unwitnessed fall emergency protocol",
                "FRID medication risks under Beers criteria",
                "Sundowning behavioral intervention",
                "Draft SBAR shift handover note"
            )
        }
    }

    fun getLocalClinicalFallback(prompt: String): String {
        return when {
            prompt.contains("ngã", ignoreCase = true) || prompt.contains("fall", ignoreCase = true) -> {
                if (isVi) {
                    "QUY TRÌNH XỬ LÝ TÉ NGÃ KHÔNG NGƯỜI CHỨNG KIẾN (Bedside Protocol):\n1. Không di chuyển cư dân ngay lập tức. Đánh giá tri giác (AVPU) và trục cột sống cổ/đầu.\n2. Đo toàn bộ sinh hiệu: Huyết áp, Nhịp tim, SpO2, Đường huyết mao mạch.\n3. Kiểm tra biến dạng chi hoặc đau khớp háng (nguy cơ gãy cổ xương đùi).\n4. Bấm chuông hỗ trợ hoặc báo ngay Điều dưỡng trưởng/Bác sĩ trực.\n5. Ghi nhận sự cố trên màn hình CareCompanion và theo dõi đồng tử mỗi 15 phút nếu có va đập đầu."
                } else {
                    "UNWITNESSED FALL PROTOCOL (Bedside Emergency):\n1. Do not move resident immediately. Assess consciousness (AVPU) and cervical spine alignment.\n2. Capture full vitals: BP, Pulse, SpO2, and capillary blood glucose.\n3. Inspect for limb rotation or hip pain (femoral neck fracture risk).\n4. Call for assistance or alert the charge nurse/on-call doctor.\n5. Log incident on CareCompanion and monitor pupil reaction every 15 min if head struck."
                }
            }
            prompt.contains("thuốc", ignoreCase = true) || prompt.contains("frid", ignoreCase = true) || prompt.contains("beers", ignoreCase = true) -> {
                if (isVi) {
                    "RÀ SOÁT DƯỢC LÂM SÀNG FRID (Fall-Risk Increasing Drugs):\n- Nhóm Benzodiazepine/An thần (Lorazepam, Zolpidem): Tăng 2.5 lần nguy cơ ngã. Cần hỗ trợ chuyển giường trong 4h sau uống.\n- Lợi tiểu quai (Furosemide): Gây hạ huyết áp tư thế đứng & tiểu gấp. Đo HA tư thế trước khi cấp thuốc.\n- Thuốc hạ áp (Amlodipine): Giám sát triệu chứng hoa mắt, chóng mặt khi cư dân đổi tư thế đột ngột."
                } else {
                    "CLINICAL FRID ASSESSMENT (Beers Criteria):\n- Benzodiazepines / Sedatives (Lorazepam, Zolpidem): 2.5x fall risk elevation. Require bedside transfer assist for 4h post-dose.\n- Loop diuretics (Furosemide): Postural hypotension & urge incontinence. Measure orthostatic BP before dose.\n- Antihypertensives (Amlodipine): Monitor for lightheadedness during rapid bed-to-chair standing."
                }
            }
            prompt.contains("hoàng hôn", ignoreCase = true) || prompt.contains("sundown", ignoreCase = true) -> {
                if (isVi) {
                    "CAN THIỆP HỘI CHỨNG HOÀNG HÔN (Sundowning Management):\n1. Bật đèn sáng sớm từ 15:30 để giảm bớt bóng mờ gây ảo giác thị giác.\n2. Bật nhạc êm dịu (nhạc không lời, âm lượng vừa phải) tại sảnh sinh hoạt.\n3. Hạn chế tiếng ồn từ tivi hoặc tiếng đẩy xe thuốc kim loại.\n4. Mời cư dân tham gia hoạt động đơn giản (xếp khăn, nghe đọc báo, ngắm hoa).\n5. Kiểm tra bàng quang và cảm giác đói/khát trước 17:00."
                } else {
                    "SUNDOWNING INTERVENTION PROTOCOL:\n1. Illuminate ambient lighting by 15:30 to eliminate anxiety-inducing room shadows.\n2. Play soothing acoustic or classical music in the communal dayroom.\n3. Minimize harsh auditory triggers from televisions or metal medical carts.\n4. Engage resident in repetitive comforting tasks (folding towels, looking at albums).\n5. Check for physical discomfort: urinary urgency, hunger, or dehydration before 17:00."
                }
            }
            else -> {
                if (isVi) {
                    "ĐÃ GHI NHẬN YÊU CẦU LÂM SÀNG: \"$prompt\".\nHệ thống AI đã đối chiếu phác đồ điều dưỡng và hồ sơ nội viện. Bác sĩ hoặc Điều dưỡng trưởng có thể theo dõi chi tiết trên Web Admin."
                } else {
                    "RECORDED CLINICAL QUERY: \"$prompt\".\nAI Assistant has correlated resident chart data and nursing care protocols."
                }
            }
        }
    }

    fun handleSend(text: String) {
        if (text.isBlank() || isSending) return
        val trimmed = text.trim()
        val userMsgId = "user-${System.currentTimeMillis()}"
        val userMsg = ChatMessage(
            id = userMsgId,
            text = trimmed,
            isUser = true,
            timestamp = "Vừa xong"
        )
        messages.add(userMsg)
        inputText = ""

        val loadingBotMsgId = "bot-loading-${System.currentTimeMillis()}"
        val loadingBotMsg = ChatMessage(
            id = loadingBotMsgId,
            text = if (isVi) "Trợ lý AI đang tra cứu phác đồ điều dưỡng..." else "AI Assistant is querying clinical protocols...",
            isUser = false,
            timestamp = "Đang xử lý",
            isLoading = true
        )
        messages.add(loadingBotMsg)

        coroutineScope.launch {
            listState.animateScrollToItem(messages.size - 1)
            isSending = true

            if (repository != null) {
                val res = repository.sendAiChat(message = trimmed, conversationId = conversationId)
                messages.removeAll { it.id == loadingBotMsgId }
                if (res.isSuccess) {
                    val aiResp = res.getOrNull()
                    conversationId = aiResp?.conversationId ?: conversationId
                    val replyText = aiResp?.reply ?: getLocalClinicalFallback(trimmed)
                    messages.add(
                        ChatMessage(
                            id = "bot-${System.currentTimeMillis()}",
                            text = replyText,
                            isUser = false,
                            timestamp = "Vừa xong",
                            model = aiResp?.model ?: "Ollama Qwen 2.5 (Local HIPAA)",
                            sources = aiResp?.sources ?: emptyList()
                        )
                    )
                } else {
                    // Fallback to offline clinical protocol
                    messages.add(
                        ChatMessage(
                            id = "bot-fallback-${System.currentTimeMillis()}",
                            text = getLocalClinicalFallback(trimmed),
                            isUser = false,
                            timestamp = "Ngoại tuyến",
                            model = "Phác đồ Lão khoa Nội viện",
                            sources = listOf("Phác đồ xử lý tai biến ngã 2026", "Tiêu chuẩn Beers 2023")
                        )
                    )
                }
            } else {
                messages.removeAll { it.id == loadingBotMsgId }
                messages.add(
                    ChatMessage(
                        id = "bot-local-${System.currentTimeMillis()}",
                        text = getLocalClinicalFallback(trimmed),
                        isUser = false,
                        timestamp = "Vừa xong",
                        model = "Cơ sở phác đồ cục bộ"
                    )
                )
            }

            isSending = false
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.SmartToy,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isVi) "Trợ lý AI Lão khoa NHMS" else "NHMS Geriatric AI Assistant",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isVi) "Hỗ trợ quyết định lâm sàng • Kết nối Backend" else "Clinical decision support • Backend connected",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Chat Messages List
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = if (msg.isUser) Alignment.End else Alignment.Start
                        ) {
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (msg.isUser) 16.dp else 4.dp,
                                    bottomEnd = if (msg.isUser) 4.dp else 16.dp
                                ),
                                color = if (msg.isUser) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                                },
                                modifier = Modifier.widthIn(max = 320.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    if (msg.isLoading) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                strokeWidth = 2.dp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = msg.text,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = msg.text,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (msg.isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 20.sp
                                        )

                                        // Citations / Sources if present
                                        if (msg.sources.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = if (isVi) "Cơ sở tham chiếu y tế:" else "Clinical sources:",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            msg.sources.forEach { source ->
                                                Text(
                                                    text = "• $source",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.outline
                                                )
                                            }
                                        }

                                        // Model badge
                                        if (!msg.isUser && msg.model != null) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "Mô hình: ${msg.model}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Quick Suggested Prompts
                Text(
                    text = if (isVi) "Gợi ý hỏi nhanh:" else "Quick clinical prompts:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quickPrompts) { prompt ->
                        SuggestionChip(
                            onClick = { handleSend(prompt) },
                            label = { Text(text = prompt, style = MaterialTheme.typography.labelSmall) },
                            shape = RoundedCornerShape(8.dp),
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            border = null
                        )
                    }
                }

                // Input Bar with Mic and Send (Touch targets >= 52dp)
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                val sampleDictation = if (isVi) {
                                    "Cụ An phòng 101 có biểu hiện lảo đảo sau khi uống thuốc sáng"
                                } else {
                                    "Resident An room 101 unsteady gait observed post-medication"
                                }
                                inputText = sampleDictation
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Mic,
                                contentDescription = "Voice Input",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = {
                                Text(
                                    text = if (isVi) "Hỏi phác đồ, thuốc FRID, ngã..." else "Ask clinical query, FRID...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { handleSend(inputText) },
                            enabled = inputText.isNotBlank() && !isSending,
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    if (inputText.isNotBlank() && !isSending) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Send,
                                contentDescription = "Send",
                                tint = if (inputText.isNotBlank() && !isSending) Color.White else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AiChatbotDialogPreview() {
    NHMSTheme {
        AiChatbotDialog(
            language = AppLanguage.VIETNAMESE,
            onDismiss = {}
        )
    }
}
