# Pulsar Demo

1) first you need to start the Apache Pulsar, how to do that see https://pulsar.apache.org/docs/3.1.x/getting-started-docker/
2) The PulsarAdminDemo used admin client, and it sets up all the needed topics

Then you can run the individual demo examples showcasing different features:

| Demo main method                | Description                                                                                   |
|---------------------------------|-----------------------------------------------------------------------------------------------|
| PulsarClassBasedJsonSchemaDemo  | Showcase how to create producer & subscriber with JSON schema defined based on provided class |
| PulsarClassBasedAvroSchemaDemo  | Showcase how to create producer & subscriber with AVRO schema defined based on provided class |
