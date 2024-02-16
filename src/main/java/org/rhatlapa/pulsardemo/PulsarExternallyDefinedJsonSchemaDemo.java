package org.rhatlapa.pulsardemo;

import static org.rhatlapa.pulsardemo.PulsarAdminDemo.TOPIC_WITH_AVRO;
import static org.rhatlapa.pulsardemo.PulsarAdminDemo.TOPIC_WITH_JSON;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.schema.SchemaDefinition;
import org.apache.pulsar.shade.org.apache.commons.io.IOUtils;
import org.rhatlapa.pulsardemo.avro.TestDataAvro;
import org.rhatlapa.pulsardemo.dto.TestData;
import org.rhatlapa.pulsardemo.dto.generated.TestDataJsonSchemaGenerated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PulsarExternallyDefinedJsonSchemaDemo {

	private static final Logger LOGGER = LoggerFactory.getLogger(PulsarExternallyDefinedJsonSchemaDemo.class);
	public static void main(String[] args) throws IOException {
		var schemaDefResource = PulsarExternallyDefinedJsonSchemaDemo.class.getResource("/json/test_data_json_schema_generated.schema");
		var schemaRef = IOUtils.toString(schemaDefResource, StandardCharsets.UTF_8);
		var schemaInfo = Schema.JSON(SchemaDefinition.builder().withPojo(TestDataJsonSchemaGenerated.class).build());
		// variant with passing the JSON schema definition directly defined using https://json-schema.org/draft/2020-12/schema,
		// but that is not accepted, failing on expecting AVRO schema format
		// var schemaInfo = Schema.JSON(SchemaDefinition.builder().withJsonDef(schemaRef).build());
		LOGGER.debug("Loaded schema: {}", schemaRef);
		try (var pulsarClient = PulsarClient.builder()
				.serviceUrl("pulsar://localhost:6650")
					.build()) {

			try (var testDataProducer = pulsarClient.newProducer(schemaInfo)
					.topic(TOPIC_WITH_JSON).create()) {

				var messageId = testDataProducer.newMessage()
						.key("id1")
						.value(TestDataJsonSchemaGenerated.builder().withAttribute("some test data").withValue(10).build())
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
