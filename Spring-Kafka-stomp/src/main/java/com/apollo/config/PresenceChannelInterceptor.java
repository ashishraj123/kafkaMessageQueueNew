package com.apollo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

public class PresenceChannelInterceptor extends ChannelInterceptorAdapter {

	private static final Logger logger = LoggerFactory.getLogger(PresenceChannelInterceptor.class);

	@Override
	public void postSend(Message<?> message, MessageChannel channel, boolean sent) {

		StompHeaderAccessor sha = StompHeaderAccessor.wrap(message);

		if (sha.getCommand() == null) {
			return;
		}

		String sessionId = sha.getSessionId();
		switch (sha.getCommand()) {
		case CONNECT:
			logger.debug("STOMP Connect [sessionId: " + sessionId + "]");
			break;
		case CONNECTED:
			logger.debug("STOMP Connected [sessionId: " + sessionId + "]");
			break;
		case DISCONNECT:
			logger.debug("STOMP Disconnect [sessionId: " + sessionId + "]");
			break;
		default:
			break;

		}
	}

}
