package com.eldercare.config;

import com.eldercare.modules.ai.careplan.service.CarePlanAiService;
import com.eldercare.modules.ai.chat.service.AiChatService;
import com.eldercare.modules.ai.risk.service.ResidentReviewPriorityService;
import com.eldercare.modules.ai.risk.service.FallScreeningService;
import com.eldercare.modules.ai.summary.service.ShiftSummaryDraftService;
import com.eldercare.modules.ai.rag.service.SemanticSearchService;
import com.eldercare.modules.ai.voice.service.VoiceObservationConfirmationService;
import com.eldercare.modules.ai.controller.AiController;
import com.eldercare.modules.ai.elopement.service.ElopementRiskAiService;
import com.eldercare.modules.ai.engine.AiSettingsService;
import com.eldercare.modules.ai.incident.service.IncidentAiService;
import com.eldercare.modules.ai.medication.service.MedicationSafetyAiService;
import com.eldercare.modules.ai.summary.service.SummaryAiService;
import com.eldercare.modules.ai.voice.service.VoiceCareAiService;
import com.eldercare.modules.ai.website.dto.WebsiteChatResponse;
import com.eldercare.modules.ai.website.service.AdmissionScreenerAiService;
import com.eldercare.modules.ai.website.service.CareQuizAiService;
import com.eldercare.modules.ai.website.service.MemoryRiskAiService;
import com.eldercare.modules.ai.website.service.WebsiteChatService;
import com.eldercare.modules.ai.website.service.WebsiteFaqAiService;
import com.eldercare.modules.auth.controller.AuthController;
import com.eldercare.modules.auth.service.AuthService;
import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.admin.user_management.RoleEntity;
import com.eldercare.modules.security.SessionStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import java.util.Optional;

@WebMvcTest({AiController.class, AuthController.class, com.eldercare.modules.ai.summary.controller.HandoverController.class})
@Import(SecurityConfig.class)
class AiEndpointSecurityTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private SessionStore sessionStore;
    @MockitoBean private AuthService authService;
    @MockitoBean private UserRepository userRepository;
    @MockitoBean private AiChatService aiChatService;
    @MockitoBean private ResidentReviewPriorityService residentReviewPriorityService;
    @MockitoBean private FallScreeningService fallScreeningService;
    @MockitoBean private ShiftSummaryDraftService shiftSummaryDraftService;
    @MockitoBean private com.eldercare.modules.ai.summary.service.HandoverWorkflowService handoverWorkflowService;
    @MockitoBean private com.eldercare.modules.ai.risk.service.DynamicFallRiskService dynamicFallRiskService;
    @MockitoBean private SemanticSearchService semanticSearchService;
    @MockitoBean private VoiceObservationConfirmationService voiceObservationConfirmationService;
    @MockitoBean private IncidentAiService incidentAiService;
    @MockitoBean private CarePlanAiService carePlanAiService;
    @MockitoBean private SummaryAiService summaryAiService;
    @MockitoBean private VoiceCareAiService voiceCareAiService;
    @MockitoBean private MedicationSafetyAiService medicationSafetyAiService;
    @MockitoBean private ElopementRiskAiService elopementRiskAiService;
    @MockitoBean private AiSettingsService aiSettingsService;
    @MockitoBean private WebsiteChatService websiteChatService;
    @MockitoBean private AdmissionScreenerAiService admissionScreenerAiService;
    @MockitoBean private WebsiteFaqAiService websiteFaqAiService;
    @MockitoBean private CareQuizAiService careQuizAiService;
    @MockitoBean private MemoryRiskAiService memoryRiskAiService;

    @ParameterizedTest
    @CsvSource({
            "/api/v1/ai/website/chat, '{\"message\":\"Hello\"}'",
            "/api/v1/ai/website/screen, '{}'",
            "/api/v1/ai/website/faq, '{\"question\":\"What are visiting hours?\"}'",
            "/api/v1/ai/website/care-quiz, '{}'",
            "/api/v1/ai/website/memory-risk, '{}'"
    })
    void websiteEndpointsAllowAnonymousPost(String path, String body) throws Exception {
        mockMvc.perform(post(path).with(request -> {
                    request.setRemoteAddr("192.0.2.10");
                    return request;
                }).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
    }

    @Test
    void websiteBodyIsPreservedForController() throws Exception {
        when(websiteChatService.chat(any())).thenReturn(WebsiteChatResponse.builder().reply("Hello").success(true).build());

        mockMvc.perform(post("/api/v1/ai/website/chat")
                        .with(request -> { request.setRemoteAddr("192.0.2.11"); return request; })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"Hello\"}"))
                .andExpect(status().isOk());

        verify(websiteChatService).chat(org.mockito.ArgumentMatchers.argThat(
                request -> "Hello".equals(request.getMessage())));
    }

    @Test
    void settingsAndClinicalAiDenyAnonymousRequests() throws Exception {
        mockMvc.perform(get("/auth/me")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/ai/search").param("q", "fall")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/residents/1")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/ai/settings")).andExpect(status().isForbidden());
        mockMvc.perform(put("/api/v1/ai/settings").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/ai/chat").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/ai/incident/classify").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/ai/website/unknown").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/ai/website/chat")).andExpect(status().isForbidden());
    }

    @Test
    void currentUserUsesAuthenticatedIdentityAndActualRole() throws Exception {
        UserEntity nurse = new UserEntity();
        nurse.setId(5L);
        nurse.setEmail("nurse@example.test");
        nurse.setFirstName("Lan");
        nurse.setLastName("Tran");
        nurse.setRole(RoleEntity.builder().roleName("Nurse_(LPN/RN)").build());
        when(userRepository.findActiveByEmailWithRole("nurse@example.test"))
                .thenReturn(Optional.of(nurse));

        mockMvc.perform(get("/auth/me")
                        .with(user("nurse@example.test").roles("NURSE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.roleName").value("Nurse_(LPN/RN)"));
    }

    @Test
    void staffCanUseClinicalAiButOnlyAdminCanChangeSettings() throws Exception {
        mockMvc.perform(get("/api/v1/ai/search").param("q", "fall")
                        .with(user("nurse").roles("NURSE")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/ai/risk/review-priorities")
                        .with(user("nurse").roles("NURSE")))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/ai/search/reindex")
                        .with(user("nurse").roles("NURSE")))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/ai/chat")
                        .with(user("nurse").roles("NURSE"))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/ai/settings")
                        .with(user("nurse").roles("NURSE"))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/api/v1/ai/settings")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void cnaCannotRunNursingReviewsOrCreateFamilyDraft() throws Exception {
        mockMvc.perform(get("/api/v1/ai/handover/shared").with(user("cna").roles("CNA"))).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/ai/summary/drafts").with(user("cna").roles("CNA"))).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/ai/risk/review-priorities")
                        .with(user("cna").roles("CNA")))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/ai/careplan/suggest")
                        .with(user("cna").roles("CNA"))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/ai/medication/check")
                        .with(user("cna").roles("CNA"))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/ai/risk/fall-screenings")
                        .with(user("cna").roles("CNA"))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/ai/summary/records-draft")
                        .with(user("cna").roles("CNA"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"audience\":\"FAMILY\"}"))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(strings = {"DON", "NURSE", "System_Administrator"})
    void clinicalStaffCanReadSharedHandovers(String role) throws Exception {
        mockMvc.perform(get("/api/v1/ai/handover/shared").with(user("staff").roles(role))).andExpect(status().isOk());
    }

    @Test
    void websiteEndpointsLimitRequestsAndBodySize() throws Exception {
        String path = "/api/v1/ai/website/chat";
        String body = "{\"message\":\"Hello\"}";
        for (int i = 0; i < 20; i++) {
            mockMvc.perform(post(path)
                            .with(request -> { request.setRemoteAddr("192.0.2.20"); return request; })
                            .contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isOk());
        }
        mockMvc.perform(post(path)
                        .with(request -> { request.setRemoteAddr("192.0.2.20"); return request; })
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isTooManyRequests());

        mockMvc.perform(post(path)
                        .with(request -> { request.setRemoteAddr("192.0.2.21"); return request; })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"" + "x".repeat(17_000) + "\"}"))
                .andExpect(status().isPayloadTooLarge());
    }

    @Test
    void websiteChatRejectsBlankMessage() throws Exception {
        mockMvc.perform(post("/api/v1/ai/website/chat")
                        .with(request -> { request.setRemoteAddr("192.0.2.22"); return request; })
                        .contentType(MediaType.APPLICATION_JSON).content("{\"message\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
