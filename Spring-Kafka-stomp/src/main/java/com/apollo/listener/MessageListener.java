package com.apollo.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

public class MessageListener {

  private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);

  private SimpMessageSendingOperations messagingTemplate;

  public MessageListener(SimpMessageSendingOperations messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  @KafkaListener(id = "stomp-test-topic-listener-0", group = "quake-topic-group",
      topicPartitions = {@TopicPartition(topic = "quake-topic", partitions = {"0"})})
  public void listenPartition_0(ConsumerRecord<?, ?> record) {
    logger.debug("reading from partition 0");
    logger.debug(String.format("Key ==> %s and value ==> %s", record.key(), record.value()));
    messagingTemplate.convertAndSend("/topic/data", record.value());
  }

  @KafkaListener(id = "stomp-test-topic-listener-1", group = "quake-topic-group",
      topicPartitions = {@TopicPartition(topic = "quake-topic", partitions = {"1"})})
  public void listenPartition_1(ConsumerRecord<?, ?> record) {
    logger.debug("reading from partition 1");
    logger.debug(String.format("Key ==> %s and value ==> %s", record.key(), record.value()));
    messagingTemplate.convertAndSend("/topic/data", record.value());
  }
}
