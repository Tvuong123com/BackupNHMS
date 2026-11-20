package com.eldercare.modules.ai.website.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Public, source-traceable website facts. No resident information belongs here. */
@Component
public class WebsiteKnowledgeBase {
    private static final Set<String> DIRECT_FAQ_IDS = Set.of(
            "admission-documents", "availability", "visiting", "privacy", "online-tool-limits");
    private final List<Fact> facts;
    private final WebsitePricingKnowledge pricingKnowledge;

    @Autowired
    public WebsiteKnowledgeBase(ObjectMapper mapper, WebsitePricingKnowledge pricingKnowledge) {
        this.pricingKnowledge = pricingKnowledge;
        try (var input = new ClassPathResource("ai/website-knowledge.json").getInputStream()) {
            facts = List.copyOf(mapper.readValue(input, new TypeReference<List<Fact>>() {}));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load public website knowledge", e);
        }
    }

    public String contextFor(String query) {
        if (isPricingOrInsurance(query)) return pricingAnswer(query);
        List<Fact> matches = matches(query);
        if (matches.isEmpty()) return "No verified website fact matched this question.";
        StringBuilder result = new StringBuilder("Relevant public website facts (source paths for review):\n");
        for (Fact fact : matches.stream().limit(3).toList()) {
            result.append("- [").append(fact.id()).append("] ")
                    .append(fact.answerEn()).append(" Source: ").append(fact.source()).append("\n");
        }
        return result.toString();
    }

    public String fallbackFor(String query) {
        if (isPricingOrInsurance(query)) return pricingAnswer(query);
        List<Fact> matches = matches(query);
        boolean vietnamese = query != null && query.matches("(?s).*[À-ỹà-ỹ].*");
        if (matches.isEmpty()) {
            return vietnamese
                    ? "Tôi chưa có thông tin đã xác nhận cho câu hỏi này. Vui lòng liên hệ cơ sở qua trang Liên hệ."
                    : "I don't have confirmed information for that question. Please contact the facility through the Contact page.";
        }
        Fact fact = matches.get(0);
        return vietnamese ? fact.answerVi() : fact.answerEn();
    }

    /** Exact published answers for simple, single-topic questions. Other questions go to AI. */
    public String directFaqAnswerFor(String query) {
        List<Fact> specific = matches(query).stream()
                .filter(f -> DIRECT_FAQ_IDS.contains(f.id()))
                .toList();
        if (specific.size() != 1) return null;
        Fact fact = specific.get(0);
        boolean vietnamese = query != null && query.matches("(?s).*[À-ỹà-ỹ].*");
        return vietnamese ? fact.answerVi() : fact.answerEn();
    }

    public boolean isPricingOrInsurance(String query) {
        if (query == null || query.isBlank()) return false;
        String normalized = query.toLowerCase(Locale.ROOT);
        return facts.stream()
                .filter(f -> f.id().equals("pricing") || f.id().equals("insurance"))
                .anyMatch(f -> score(f, normalized) > 0);
    }

    public String pricingAnswer(String query) {
        boolean vietnamese = query != null && query.matches("(?s).*[À-ỹà-ỹ].*");
        String normalized = query == null ? "" : query.toLowerCase(Locale.ROOT);
        boolean asksPrice = facts.stream().filter(f -> f.id().equals("pricing"))
                .anyMatch(f -> score(f, normalized) > 0);
        boolean asksInsurance = facts.stream().filter(f -> f.id().equals("insurance"))
                .anyMatch(f -> score(f, normalized) > 0);
        StringBuilder answer = new StringBuilder();
        if (asksPrice) answer.append(pricingKnowledge.answer(vietnamese));
        if (asksInsurance) {
            if (!answer.isEmpty()) answer.append("\n\n");
            answer.append(vietnamese
                    ? "Medicare thường không trả cho lưu trú dài hạn tại viện dưỡng lão; Medicare Part A có thể trả chăm sóc điều dưỡng chuyên sâu/phục hồi ngắn hạn tại cơ sở đủ điều kiện, tối đa 100 ngày mỗi kỳ quyền lợi nếu đáp ứng điều kiện. Medicaid có thể chi trả chăm sóc tại cơ sở điều dưỡng được chứng nhận cho người đủ điều kiện theo tiểu bang; bảo hiểm tư nhân tùy hợp đồng. Giá LOC theo ngày không phải mức đồng chi trả hoặc quyền lợi bảo hiểm. Kiểm tra điều kiện, cơ sở tham gia và phần tự trả với đơn vị bảo hiểm và bộ phận tiếp nhận. Nguồn: https://www.medicare.gov/coverage/nursing-home-care ; https://www.medicare.gov/coverage/skilled-nursing-facility-care ; https://www.medicaid.gov/medicaid/long-term-services-supports/institutional-long-term-care/nursing-facilities"
                    : "Medicare generally does not cover long-term nursing home stays. Medicare Part A may cover short-term skilled nursing or rehabilitation in an eligible facility, for up to 100 days per benefit period when requirements are met. Medicaid may cover care in a certified nursing facility for eligible people under state rules; private insurance depends on the policy. A daily LOC rate is not a copay or insurance benefit amount. Confirm eligibility, facility participation, and your share with the insurer and Admissions. Sources: https://www.medicare.gov/coverage/nursing-home-care ; https://www.medicare.gov/coverage/skilled-nursing-facility-care ; https://www.medicaid.gov/medicaid/long-term-services-supports/institutional-long-term-care/nursing-facilities");
        }
        return answer.toString();
    }

    private List<Fact> matches(String query) {
        if (query == null || query.isBlank()) return List.of();
        String normalized = query.toLowerCase(Locale.ROOT);
        return facts.stream()
                .filter(f -> score(f, normalized) > 0)
                .sorted(Comparator.comparingInt((Fact f) -> score(f, normalized)).reversed())
                .toList();
    }

    private int score(Fact fact, String query) {
        return fact.keywords().stream()
                .filter(k -> query.contains(k.toLowerCase(Locale.ROOT)))
                .mapToInt(String::length)
                .sum();
    }

    public record Fact(String id, List<String> keywords, String answerEn, String answerVi, String source) {}
}
