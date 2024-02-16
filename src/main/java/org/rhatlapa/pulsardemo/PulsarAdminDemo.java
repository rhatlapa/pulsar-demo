package org.rhatlapa.pulsardemo;

import java.util.HashSet;
import java.util.List;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.common.policies.data.TenantInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PulsarAdminDemo {

	private static final Logger LOGGER = LoggerFactory.getLogger(PulsarAdminDemo.class);
	public static final String TENANT_NAME = "new-tenant";
	public static final String NAMESPACE_NAME = TENANT_NAME + "/" + "ns";
	public static final String TOPIC_WITH_JSON = "persistent://" + NAMESPACE_NAME + "/" + "test-topic-json";
	public static final String TOPIC_WITH_AVRO = "persistent://" + NAMESPACE_NAME + "/" + "test-topic-avro";

	public static void main(String[] args) throws PulsarClientException, PulsarAdminException {
		try (var admin = PulsarAdmin.builder()
					.serviceHttpUrl("http://localhost:8080")
					.allowTlsInsecureConnection(true)
					.build()) {
			var clusters = admin.clusters().getClusters();
			var tenants = admin.tenants().getTenants();
			LOGGER.info("Available tenants: {}", tenants);
			if (!tenants.contains(TENANT_NAME)) {
				admin.tenants().createTenant(TENANT_NAME, TenantInfo.builder().allowedClusters(new HashSet<>(clusters)).build());
			}
			var tenantsNamespaces = admin.namespaces().getNamespaces(TENANT_NAME);
			if (!tenantsNamespaces.contains(NAMESPACE_NAME)) {
				admin.namespaces().createNamespace(NAMESPACE_NAME);
			}
			LOGGER.info("Available tenants: {}, namespaces: {}", admin.tenants().getTenants(), admin.namespaces().getNamespaces(TENANT_NAME));
			var topics = admin.topics().getPartitionedTopicList(NAMESPACE_NAME);
			LOGGER.debug("Available partitioned topics: {}", topics);
			if (topics.contains(TOPIC_WITH_JSON)) {
				LOGGER.info("Removing topic: {}", TOPIC_WITH_JSON);
				admin.topics().deletePartitionedTopic(TOPIC_WITH_JSON);
			}
			if (topics.contains(TOPIC_WITH_AVRO)) {
				LOGGER.info("Removing topic: {}", TOPIC_WITH_AVRO);
				admin.topics().deletePartitionedTopic(TOPIC_WITH_AVRO);
			}
			LOGGER.info("Creating partitioned topics: {}", List.of(TOPIC_WITH_JSON, TOPIC_WITH_AVRO));
			admin.topics().createPartitionedTopic(TOPIC_WITH_JSON, 3);
			admin.topics().createPartitionedTopic(TOPIC_WITH_AVRO, 3);
		}
	}
}
