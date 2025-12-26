package ru.kuznetsov.shop.kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.kuznetsov.shop.represent.dto.AbstractDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KafkaService {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    Logger logger = LoggerFactory.getLogger(KafkaService.class);

    public <E extends AbstractDto> boolean sendMessageWithEntity(E object, String topic, Map<String, byte[]> headersMap) {
        return sendMessage(object, topic, headersMap);
    }

    public boolean sendMessage(Object object, String topic, Map<String, byte[]> headersMap) {
        logger.info("Sending save message to topic: {}, with object: {}", topic, object);
        try {
            String value = objectMapper.writeValueAsString(object);

            List<Header> headers = new ArrayList<>();
            for (Map.Entry<String, byte[]> entry : headersMap.entrySet()) {
                headers.add(new RecordHeader(entry.getKey(), entry.getValue()));
            }

            ProducerRecord<String, String> record = new ProducerRecord<>(topic, null, "message", value, headers);

            kafkaTemplate.send(record);
            return true;
        } catch (JsonProcessingException e) {
            logger.error("Error during sending of the message to topic: {}, with object: {}", topic, object);
            logger.error(e.getMessage());
            return false;
        }
    }
}
