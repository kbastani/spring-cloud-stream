/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.interceptor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.stream.annotation.Bindings;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.utils.MockBinderConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Verifies that interceptors used by modules are applied correctly to generated channels.
 *
 * @author Marius Bogoevici
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(BoundChannelsInterceptedTest.Foo.class)
public class BoundChannelsInterceptedTest {

	public static final Message<?> TEST_MESSAGE = MessageBuilder.withPayload("bar").build();

	@Autowired
	ChannelInterceptor channelInterceptor;

	@Autowired
	@Bindings(BoundChannelsInterceptedTest.Foo.class)
	public Sink fooSink;

	@Test
	public void testBoundChannelsIntercepted() {
		fooSink.input().send(TEST_MESSAGE);
		verify(channelInterceptor).preSend(TEST_MESSAGE, fooSink.input());
		verifyNoMoreInteractions(channelInterceptor);
	}


	@SpringBootApplication
	@EnableBinding(Sink.class)
	@Import(MockBinderConfiguration.class)
	public static class Foo {

		@ServiceActivator(inputChannel = Sink.INPUT)
		public void fooSink(Message<?> message) {
		}

		@GlobalChannelInterceptor @Bean
		public ChannelInterceptor globalChannelInterceptor() {
			return mock(ChannelInterceptor.class);
		}

	}
}
