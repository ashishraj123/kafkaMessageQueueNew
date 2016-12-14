package com.apollo.producer;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageProducer {

	private static final Logger logger = LoggerFactory.getLogger(MessageProducer.class);

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final String topicName;
	private final RestTemplate restTemplate;

	private final static ObjectMapper mapper = new ObjectMapper();

	public MessageProducer(KafkaTemplate<String, String> kafkaTemplate, String topicName, RestTemplate restTemplate) {
		this.kafkaTemplate = kafkaTemplate;
		this.topicName = topicName;
		this.restTemplate = restTemplate;
	}

	@Scheduled(fixedRate = 3000)
	public void sendMessage() throws JsonProcessingException, IOException {
		logger.debug("Starting sending message over topic : ", topicName);

		JsonNode result = fetchData();

		if (result == null) {
			logger.info("No data at the moment");
			return;
		}

		JsonNode dataNode = result.get("features");

		if (dataNode.isArray()) {
			for (JsonNode node : dataNode) {
				kafkaTemplate.send(topicName, node.toString());
			}
		} else {
			kafkaTemplate.send(topicName, dataNode.toString());
		}
		logger.debug("Message sent over topic : ", topicName);
	}

	private JsonNode fetchData() throws JsonProcessingException, IOException {
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(
				"http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson", String.class);
		String response = responseEntity.getBody();
		if (StringUtils.isEmpty(response)) {
			return null;
		}
		return mapper.readTree(response);
	}
}
