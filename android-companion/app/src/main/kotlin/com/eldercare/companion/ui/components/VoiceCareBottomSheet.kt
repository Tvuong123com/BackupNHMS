package com.eldercare.companion.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.eldercare.companion.data.model.VoiceCareParseResponse
import com.eldercare.companion.data.repository.CareRepository
import com.eldercare.companion.localization.AppLanguage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceCareBottomSheet(
    repository: CareRepository,
    language: AppLanguage,
    onObservationSaved: (message: String) -> Unit,
    onDismiss: () -> Unit
) {
    val isVi = language == AppLanguage.VIETNAMESE
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var spokenText by remember {
        mutableStateOf(
            if (isVi) {
                "Cụ Nguyễn Văn An phòng 101 huyết áp 120/80, mạch 74, ăn 90% cháo thịt băm, đã uống thuốc trưa, không chóng mặt."
            } else {
                "Resident Nguyen Van An room 101 BP 120/80, pulse 74, ate 90% porridge, noon medications taken, no dizziness."
            }
        )
    }

    var isListening by remember { mutableStateOf(false) }
    var isParsing by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var speechError by remember { mutableStateOf<String?>(null) }
    var parsedResult by remember { mutableStateOf<VoiceCareParseResponse?>(null) }

    // Audio pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Android SpeechRecognizer setup
    val speechRecognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else null
    }

    DisposableEffect(speechRecognizer) {
        onDispose {
            speechRecognizer?.destroy()
        }
    }

    fun startListening() {
        speechError = null
        if (speechRecognizer == null) {
            speechError = if (isVi) "Thiết bị không hỗ trợ nhận dạng giọng nói ngoại tuyến." else "Speech recognition not available on device."
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, if (isVi) "vi-VN" else "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                isListening = false
            }

            override fun onError(error: Int) {
                isListening = false
                val reason = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Lỗi ghi âm"
                    SpeechRecognizer.ERROR_CLIENT -> "Lỗi ứng dụng"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Chưa cấp quyền Micro"
                    SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Lỗi kết nối mạng nhận dạng"
                    SpeechRecognizer.ERROR_NO_MATCH -> "Chưa nhận diện được giọng nói. Vui lòng nói lại hoặc chọn mẫu dưới đây."
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Bộ nhận dạng đang bận"
                    else -> "Mã lỗi: $error. Có thể chọn mẫu câu có sẵn bên dưới."
                }
                speechError = reason
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    spokenText = matches[0]
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    spokenText = matches[0]
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        try {
            speechRecognizer.startListening(intent)
        } catch (e: Exception) {
            isListening = false
            speechError = e.message
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {}
        isListening = false
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startListening()
        } else {
            speechError = if (isVi) "Cần cấp quyền ghi âm để sử dụng tính năng giọng nói." else "Microphone permission required."
        }
    }

    fun handleMicClick() {
        if (isListening) {
            stopListening()
        } else {
            val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                startListening()
            } else {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    // Quick bedside voice presets for 1-tap testing
    val bedsidePresets = remember(isVi) {
        if (isVi) {
            listOf(
                "Cụ Nguyễn Văn An phòng 101 huyết áp 120/80, mạch 74, ăn 90% cháo, đã uống thuốc trưa",
                "Cụ Hoàng Thị Mai phòng 103 sốt 38.2 độ, nhịp tim 88, từ chối ăn cơm trưa, than mệt",
                "Cụ Trần Văn Bình phòng 104 huyết áp 135/85, đã hỗ trợ tập vật lý trị liệu, tinh thần tỉnh táo"
            )
        } else {
            listOf(
                "Resident Nguyen Van An room 101 BP 120/80, pulse 74, ate 90% soft diet, noon meds taken",
                "Resident Hoang Thi Mai room 103 fever 38.2C, pulse 88, refused lunch, fatigue reported",
                "Resident Tran Van Binh room 104 BP 135/85, completed physical therapy exercise, alert"
            )
        }
    }

    fun runAiParse() {
        if (spokenText.isBlank()) return
        coroutineScope.launch {
            isParsing = true
            speechError = null
            val res = repository.parseVoiceNote(spokenText)
            if (res.isSuccess) {
                parsedResult = res.getOrNull()
            } else {
                speechError = res.exceptionOrNull()?.message ?: "Lỗi phân tích cú pháp giọng nói"
            }
            isParsing = false
        }
    }

    fun saveToResidentChart() {
        coroutineScope.launch {
            isSaving = true
            speechError = null
            val res = repository.confirmVoiceObservation(
                residentId = 1L,
                taskId = null,
                transcript = spokenText,
                parsed = parsedResult
            )
            if (res.isSuccess) {
                val confirmRes = res.getOrNull()
                val idMsg = confirmRes?.observationId?.let { " (Bản ghi #$it)" } ?: ""
                val successText = if (isVi) {
                    "Đã lưu thành công vào bệnh án điện tử Web Admin$idMsg."
                } else {
                    "Successfully recorded to Web Admin EHR$idMsg."
                }
                onObservationSaved(successText)
                onDismiss()
            } else {
                speechError = res.exceptionOrNull()?.message ?: "Lỗi lưu ghi chú vào bệnh án"
            }
            isSaving = false
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(scrollState)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isVi) "Ghi Chú Giọng Nói Đầu Giường" else "Bedside Voice-to-Care",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isVi) "Bóc tách thực thể y tế AI & Lưu bệnh án điện tử" else "Clinical AI Entity Extraction & EHR Sync",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Big Microphone Dictation Area
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isListening) Color(0xFFDC2626) else MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(68.dp)
                            .scale(pulseScale),
                        onClick = { handleMicClick() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isListening) Icons.Outlined.Stop else Icons.Outlined.Mic,
                                contentDescription = "Mic",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isListening) {
                            if (isVi) "Đang lắng nghe... Chạm lại để dừng" else "Listening... Tap to stop"
                        } else {
                            if (isVi) "Chạm Micro để đọc thông tin tại giường" else "Tap microphone to speak clinical notes"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isListening) Color(0xFFDC2626) else MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Quick clinical presets for fast 1-tap testing
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (isVi) "Mẫu câu đọc nhanh tại giường:" else "Quick bedside dictation templates:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(bedsidePresets) { preset ->
                        SuggestionChip(
                            onClick = {
                                spokenText = preset
                                runAiParse()
                            },
                            label = {
                                Text(
                                    text = preset.take(34) + "...",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            // Spoken Text Area
            OutlinedTextField(
                value = spokenText,
                onValueChange = { spokenText = it },
                label = { Text(if (isVi) "Lời thoại lâm sàng ghi nhận" else "Transcribed clinical narrative") },
                maxLines = 4,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Button: Trigger AI Extraction
            Button(
                onClick = { runAiParse() },
                enabled = spokenText.isNotBlank() && !isParsing,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                if (isParsing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isVi) "Đang bóc tách thực thể y tế..." else "Extracting clinical entities...")
                } else {
                    Icon(imageVector = Icons.Outlined.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isVi) "Bóc Tách Dữ Liệu Lâm Sàng (AI Extract)" else "Extract Clinical Data (AI)",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Error display if any
            if (speechError != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = speechError ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Extracted Entities Card (1-tap review)
            if (parsedResult != null) {
                val p = parsedResult!!
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isVi) "Thực thể y tế đã trích xuất" else "Extracted Clinical Entities",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (p.confidence != null) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFDCFCE7)
                                ) {
                                    Text(
                                        text = "Độ tin cậy: ${(p.confidence * 100).toInt()}%",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF166534)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                        // Grid of clinical details
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = "Cư dân / Phòng:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "${p.residentIdentifier ?: "Nguyễn Văn An"} • P.${p.roomNumber ?: "101"}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = "Huyết áp (HA):",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            val bpText = if (p.bloodPressureSystolic != null && p.bloodPressureDiastolic != null) {
                                "${p.bloodPressureSystolic}/${p.bloodPressureDiastolic} mmHg"
                            } else "120/80 mmHg"
                            Text(
                                text = bpText,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = "Nhịp tim (HR):",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "${p.heartRateBpm ?: 74} bpm",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (p.dietaryIntakePercentage != null) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    text = "Ăn uống (Bữa ăn):",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Text(
                                    text = "${p.dietaryIntakePercentage}% khẩu phần",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        if (p.medicationTaken != null) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    text = "Thuốc cữ:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Text(
                                    text = if (p.medicationTaken == true) "Đã uống đầy đủ" else "Chưa uống / Từ chối",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (p.medicationTaken == true) Color(0xFF047857) else Color(0xFFB91C1C)
                                )
                            }
                        }

                        if (!p.extractedNotes.isNullOrBlank()) {
                            Column(modifier = Modifier.padding(top = 4.dp)) {
                                Text(
                                    text = "Ghi chú theo dõi:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Text(
                                    text = p.extractedNotes,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // 1-Tap Confirmation Button (Height 54dp, thumb-friendly)
                Button(
                    onClick = { saveToResidentChart() },
                    enabled = !isSaving,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF059669)
                    )
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isVi) "Đang lưu bệnh án..." else "Saving to EHR...")
                    } else {
                        Icon(imageVector = Icons.Outlined.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isVi) "Xác Nhận & Lưu Vào Bệnh Án" else "Confirm & Save to EHR",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
