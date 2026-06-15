package DuocQuin.Pagos.configTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import DuocQuin.Pagos.config.SecurityConfig;

class SecurityConfigTest {

    @Test
    void corsConfigurationCrearse() {
        SecurityConfig config = new SecurityConfig();

        CorsConfigurationSource corsSource = config.corsConfigurationSource();

        CorsConfiguration cors = corsSource.getCorsConfiguration(new MockHttpServletRequest());
        assertNotNull(cors);
    }

    @Test
    void corsPermitirOrigenes() {
        SecurityConfig config = new SecurityConfig();

        CorsConfiguration cors = config.corsConfigurationSource().getCorsConfiguration(new MockHttpServletRequest());
        assertTrue(cors.getAllowedOrigins().size() > 0);
    }

    @Test
    void corsPermitirMetodos() {
        SecurityConfig config = new SecurityConfig();

        CorsConfiguration cors = config.corsConfigurationSource().getCorsConfiguration(new MockHttpServletRequest());
        List<String> methods = cors.getAllowedMethods();

        assertTrue(methods.contains("GET"));
        assertTrue(methods.contains("POST"));
        assertTrue(methods.contains("PUT"));
        assertTrue(methods.contains("DELETE"));
    }

    @Test
    void corsPermitirHeaders() {
        SecurityConfig config = new SecurityConfig();

        CorsConfiguration cors = config.corsConfigurationSource().getCorsConfiguration(new MockHttpServletRequest());

        assertNotNull(cors.getAllowedHeaders());
    }

    @Test
    void securityFilterChainDebeCrearse() throws Exception {
        SecurityConfig config = new SecurityConfig();

        HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);

        SecurityFilterChain chain = config.securityFilterChain(http);
        assertNotNull(chain);
    }
}