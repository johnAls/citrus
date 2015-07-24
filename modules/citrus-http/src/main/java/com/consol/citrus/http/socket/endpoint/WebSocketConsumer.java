/*
 * Copyright 2006-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.http.socket.endpoint;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.ActionTimeoutException;
import com.consol.citrus.message.Message;
import com.consol.citrus.messaging.AbstractSelectiveMessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.AbstractWebSocketMessage;

/**
 * @author Martin Maher
 * @since 2.2.1
 */
public class WebSocketConsumer extends AbstractSelectiveMessageConsumer {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketConsumer.class);

    private static final long POLL_INTERVAL = 500L;

    /**
     * Endpoint configuration
     */
    private final WebSocketEndpointConfiguration endpointConfiguration;

    /**
     * Default constructor using receive timeout setting.
     *
     * @param name
     * @param endpointConfiguration
     */
    public WebSocketConsumer(String name, WebSocketEndpointConfiguration endpointConfiguration) {
        super(name, endpointConfiguration);
        this.endpointConfiguration = endpointConfiguration;
    }

    @Override
    public Message receive(String selector, TestContext context, long timeout) {
        LOG.info(String.format("Waiting %s for Web Socket message ...", timeout));

        AbstractWebSocketMessage<?> message = receive(endpointConfiguration, timeout);
        Message receivedMessage = endpointConfiguration.getMessageConverter().convertInbound(message, endpointConfiguration);

        LOG.info("Received Web Socket message");
        context.onInboundMessage(receivedMessage);

        return receivedMessage;
    }

    private AbstractWebSocketMessage<?> receive(WebSocketEndpointConfiguration config, long timeout) {
        long timeLeft = timeout;

        AbstractWebSocketMessage<?> message = config.getHandler().getMessage();
        String path = endpointConfiguration.getEndpointUri();
        while (message == null && timeLeft > 0) {
            timeLeft -= POLL_INTERVAL;
            long sleep = timeLeft > 0 ? POLL_INTERVAL : POLL_INTERVAL + timeLeft;
            if (LOG.isDebugEnabled()) {
                String msg = "Waiting for message on '%s' - retrying in %s ms";
                LOG.debug(String.format(msg, path, (sleep)));
            }

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                LOG.warn(String.format("Thread interrupted while waiting for message on '%s'", path), e);
            }

            message = config.getHandler().getMessage();
        }

        if (message == null) {
            throw new ActionTimeoutException(String.format("Action timed out while receiving message on '%s'", path));
        }
        return message;
    }
}
