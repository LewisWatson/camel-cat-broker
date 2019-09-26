# camel-cat-broker

## Integration Test

All command examples were tested with kafka_2.12-2.3.0 and assume they are run from the root directory of a
kafka installation.

### Set up Kafka server

If you don't already have a server you can follow the instructions at <https://kafka.apache.org/quickstart>

Create topics named `cat-broker`, `cats-r-us`, and `dlq` with a single partition and only one replica:

```sh
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic cat-broker
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic cats-r-us
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic dlq
```

Confirm that the topics have been successfully created

```sh
bin/kafka-topics.sh --list --bootstrap-server localhost:9092
cat-broker
cats-r-us
dlq
```

### Run `camel-cat-broker` connected to kafka

```sh
SPRING_PROFILES_ACTIVE=kafka ./gradlew bootRun
```

### Set up some cattery consumers

Run kafka-console-consumers in separate terminals so you can see where the cats get routed

```sh
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic cats-r-us --from-beginning
```

```sh
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic dlq --from-beginning
```

### Send some cats to the `camel-cat-broker`

You are now ready to route some cats to catteries using kafka-console-producer!

```sh
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic cat-broker
```

Send the following strings

- `{ name: "bitey", breed: "TABBY", temperment: "FIESTY" }`
- `{ name: "scratchy", breed: "BRITISH_BLUE", temperment: "FIESTY" }`
- `{ name: "mystery cat", breed: "UNKNOWN", temperment: "UNKNOWN" }`

### Confirm that the cats have been routed as expected

If all works as expected then Tabby cats will always go to `cats-r-us`, other cats get routed to the dead letter queue (DLQ).
