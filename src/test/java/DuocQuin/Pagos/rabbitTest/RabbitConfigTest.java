package DuocQuin.Pagos.rabbitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;

import DuocQuin.Pagos.rabbitmq.RabbitConfig;

class RabbitConfigTest {

    private final RabbitConfig config = new RabbitConfig();

    @Test
    void exchangeCrearse() {
        DirectExchange exchange = config.exchange();

        assertNotNull(exchange);
        assertEquals("duocquin.exchange", exchange.getName());
    }

    @Test
    void jsonMessageConverterCrearse() {
        MessageConverter converter = config.jsonMessageConverter();
        assertNotNull(converter);
    }

    @Test
    void rabbitTemplateDebeCrearse() {

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);

        RabbitTemplate template = config.rabbitTemplate(connectionFactory);
        assertNotNull(template);
    }
}