package org.rhatlapa.pulsardemo;

import static org.rhatlapa.pulsardemo.PulsarAdminDemo.TOPIC_WITH_JSON;

import java.io.IOException;

import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.Schema;
import org.rhatlapa.pulsardemo.dto.TestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PulsarClassBasedJsonSchemaDemo {

	private static final Logger LOGGER = LoggerFactory.getLogger(PulsarClassBasedJsonSchemaDemo.class);

	public static void main(String[] args) throws IOException {
		var schemaInfo = Schema.JSON(TestData.class);
		try (var pulsarClient = PulsarClient.builder()
				.serviceUrl("pulsar://localhost:6650")
					.build()) {

			try (var testDataProducer = pulsarClient.newProducer(schemaInfo).topic(TOPIC_WITH_JSON).create()) {

				var messageId = testDataProducer.newMessage()
						.key("id1")
						.value(TestData.builder().attribute("some test data").value(5).build())
						.disableReplication()
						.send();
				LOGGER.info("Sent message '{}'", messageId);
			}

			try (var reader = pulsarClient.newReader(schemaInfo).topic(TOPIC_WITH_JSON)
					.subscriptionName("demo-2")
					.readerName("demo-reader")
					.startMessageId(MessageId.earliest)
					.create()) {
				while (reader.hasMessageAvailable()) {
					var data = reader.readNext();
					LOGGER.info("Received message {} with messageId '{}' & schema version {}",
							data.getValue(), data.getMessageId(), data.getSchemaVersion());
				}
			}
		}
	}
}
