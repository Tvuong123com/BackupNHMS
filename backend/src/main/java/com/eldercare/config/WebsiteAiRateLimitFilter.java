package com.eldercare.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** Limits anonymous website AI traffic by the address observed by the server. */
public class WebsiteAiRateLimitFilter extends OncePerRequestFilter {

    public static final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/ai/website/chat",
            "/api/v1/ai/website/screen",
            "/api/v1/ai/website/faq",
            "/api/v1/ai/website/care-quiz",
            "/api/v1/ai/website/memory-risk"
    };

    private static final Set<String> PUBLIC_PATHS = Set.copyOf(Arrays.asList(PUBLIC_ENDPOINTS));
    private static final int REQUESTS_PER_MINUTE = 20;
    private static final long WINDOW_MILLIS = 60_000;
    private static final int MAX_BODY_BYTES = 16 * 1024;

    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();
    private final AtomicLong lastCleanup = new AtomicLong();
    private final Clock clock;

    public WebsiteAiRateLimitFilter() {
        this(Clock.systemUTC());
    }

    WebsiteAiRateLimitFilter(Clock clock) {
        this.clock = clock;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return !HttpMethod.POST.matches(request.getMethod())
                || !PUBLIC_PATHS.contains(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (request.getContentLengthLong() > MAX_BODY_BYTES) {
            response.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, "Website AI request is too large");
            return;
        }

        byte[] body = request.getInputStream().readNBytes(MAX_BODY_BYTES + 1);
        if (body.length > MAX_BODY_BYTES) {
            response.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, "Website AI request is too large");
            return;
        }

        long now = clock.millis();
        long currentWindow = now / WINDOW_MILLIS;
        String clientIp = request.getRemoteAddr();
        Window window = windows.compute(clientIp, (ip, previous) -> {
            if (previous == null || previous.minute != currentWindow) {
                return new Window(currentWindow, 1);
            }
            return new Window(currentWindow, previous.count + 1);
        });

        if (window.count > REQUESTS_PER_MINUTE) {
            response.setHeader("Retry-After", Long.toString(60 - (now % WINDOW_MILLIS) / 1_000));
            response.sendError(429, "Website AI request limit exceeded");
            return;
        }

        // Keep the per-address cache bounded over time without a scheduled task.
        long previousCleanup = lastCleanup.get();
        if (now - previousCleanup > WINDOW_MILLIS && lastCleanup.compareAndSet(previousCleanup, now)) {
            windows.entrySet().removeIf(entry -> entry.getValue().minute < currentWindow);
        }

        chain.doFilter(new CachedBodyRequest(request, body), response);
    }

    private record Window(long minute, int count) {}

    private static final class CachedBodyRequest extends HttpServletRequestWrapper {
        private final byte[] body;

        private CachedBodyRequest(HttpServletRequest request, byte[] body) {
            super(request);
            this.body = body;
        }

        @Override
        public int getContentLength() {
            return body.length;
        }

        @Override
        public long getContentLengthLong() {
            return body.length;
        }

        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream input = new ByteArrayInputStream(body);
            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return input.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                    throw new UnsupportedOperationException("Asynchronous reads are not supported");
                }

                @Override
                public int read() {
                    return input.read();
                }
            };
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
        }
    }
}
