package com.payment.ITs;

import com.common.dto.orderDto.OrderDataDTO;
import com.common.dto.paymentDto.PaymentEventDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.payment.TestAuditingConfiguration;
import com.payment.dto.CreatePaymentDTO;
import com.payment.dto.PaymentDTO;
import com.payment.enums.PaymentStatus;
import com.payment.repositories.PaymentRepository;
import com.payment.services.PaymentService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;


@AutoConfigureWireMock(port = 9999)
@Import(TestAuditingConfiguration.class)
public class PaymentServiceIT extends BaseIT {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ObjectMapper objectMapper;


    @DynamicPropertySource
    static void wireMockProperties(DynamicPropertyRegistry registry) {
        String wireMockUrl = "http://localhost:9999";

        registry.add("application.config.order-service-url", () -> wireMockUrl);

        registry.add("application.config.user-service-url", () -> wireMockUrl);

        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
                () -> wireMockUrl + "/realms/PaycardSystem/protocol/openid-connect/certs");
    }

    @BeforeEach
    void cleanUp() {
        paymentRepository.deleteAll();
    }

    @Test
    @DisplayName("Create Payment: Should save to DB and send event to Kafka")
    void createPayment_Success() throws Exception {
        Integer userId = 10;
        Integer orderId = 555;
        BigDecimal amount = new BigDecimal("150.00");

        CreatePaymentDTO createDto = new CreatePaymentDTO();
        createDto.setUserId(userId);
        createDto.setOrderId(orderId);
        createDto.setStatus(PaymentStatus.SUCCESS);

        OrderDataDTO mockOrderResponse = new OrderDataDTO();
        mockOrderResponse.setId(orderId);
        mockOrderResponse.setUserId(userId);
        mockOrderResponse.setTotalPrice(amount);

        stubFor(get(urlEqualTo("/api/users/" + userId + "/orders/" + orderId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(mockOrderResponse))
                        .withStatus(200)));

        PaymentDTO result = paymentService.createPayment(createDto);

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(result.getPaymentAmount()).isEqualByComparingTo(amount);
        assertThat(paymentRepository.findAll()).hasSize(1);

        Consumer<String, PaymentEventDTO> consumer = createTestConsumer();
        consumer.subscribe(Collections.singleton("payment-events"));

        ConsumerRecord<String, PaymentEventDTO> record = KafkaTestUtils.getSingleRecord(consumer, "payment-events", Duration.ofSeconds(10));

        assertThat(record.key()).isEqualTo(orderId.toString());
        assertThat(record.value().getOrderId()).isEqualTo(orderId);
        assertThat(record.value().getUserId()).isEqualTo(userId);
        assertThat(record.value().getStatus()).isEqualTo("SUCCESS");

        consumer.close();
    }

    private Consumer<String, PaymentEventDTO> createTestConsumer() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(BaseIT.kafka.getBootstrapServers(), "test-group", "false");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        DefaultKafkaConsumerFactory<String, PaymentEventDTO> factory = new DefaultKafkaConsumerFactory<>(
                consumerProps,
                new StringDeserializer(),
                new JsonDeserializer<>(PaymentEventDTO.class, false));

        return factory.createConsumer();
    }
}