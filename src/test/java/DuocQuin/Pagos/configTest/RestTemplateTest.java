package DuocQuin.Pagos.configTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import DuocQuin.Pagos.config.RestTemplateConfig;

class RestTemplateConfigTest {

    @Test
    void crearRestTemplate() {
        RestTemplateConfig config = new RestTemplateConfig();

        RestTemplate restTemplate = config.restTemplate();

        assertNotNull(restTemplate);
    }
}
