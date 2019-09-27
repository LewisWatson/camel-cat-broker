# Camel Cat Broker

Looking for a suitable [Cattery] for your furry friend? With the power of [Apache Camel],
[Apache Kafka], and advanced algorithms, **Camel Cat Broker** will route your cat to the purr-fect
[Cattery] based on your cats unique paw-perties!

![logo](docs/img/camel-cat-broker.svg)

An implementation of the `cat-broker` component in the [Cat Broker System High Level Design] using. For more information please see the
[Low Level Design].

## Build

```sh
./gradlew build
```

## Run

```sh
java -jar camel-cat-broker-0.1.0.jar
```

### Enabling the `kafka` Profile

By default the [Apache Camel] route will consume from a [Direct Component] endpoint and route to
[Mock Component] endpoints. This is great for automated testing within a single Java Virtual
Machine, but not so great of integrating with external services.

Enabling the `kafka` profile will result in [Kafka Component] endpoints being used instead, which
is a great option for scaleable, decoupled systems.

```sh
java -jar camel-cat-broker-0.1.0.jar --spring-profiles-active=kafka
```

## Quick start

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

[Cattery]: https://en.wikipedia.org/wiki/Cattery "A cattery is where cats are commercially housed. Catteries come in two varieties – boarding catteries and breeding catteries."
[Apache Camel]: https://camel.apache.org/ "Camel is an open source integration framework that empowers you to quickly and easily integrate various systems consuming or producing data."
[Apache Kafka]: https://kafka.apache.org/ "A distributed streaming platform"
[Direct Component]: https://camel.apache.org/components/latest/direct-component.html "The Direct component provides direct, synchronous invocation of any consumers when a producer sends a message exchange."
[Mock Component]: https://camel.apache.org/components/latest/mock-component.html "The Mock component provides a powerful declarative testing mechanism, which is similar to jMock in that it allows declarative expectations to be created on any Mock endpoint before a test begins."
[Kafka Component]: https://camel.apache.org/components/latest/kafka-component.html "The Kafka component is used for communicating with Apache Kafka message broker."
[Cat Broker System High Level Design]: docs/cat-broker-system-high-level-design.md
[Low Level Design]: docs/camel-cat-broker-low-level-design.md
