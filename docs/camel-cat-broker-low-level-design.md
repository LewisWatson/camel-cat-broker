# Camel Cat Broker - Low Level Design

A [Content-Based Router] for routing [Cat Messages][Cat Message] to
[Cattery Channels][Cattery Channel].

An implementation of the `cat-broker` component in the [Cat Broker System High Level Design] using
[Java], [Spring Boot], [Apache Camel] and [Apache Kafka].

## Component Diagram

![component diagram](img/camel-cat-broker-component-diagram.svg)

## EIP Route Diagram

![EIP diagram](img/camel-cat-broker-route-EIP.svg)

## Glossary

Definitions for terms used later in the document.

### Cat

Felis catus. A small carnivorous mammal.

### Cattery Channel

An [Apache Kafka] topic for sending [Cat Messages][Cat Message] onwards to a third party Cattery
service.

### Dead Letter Channel

An [Apache Kafka] topic to send [Cat Messages][Cat Message] that for some reason cannot be routed
to a [Cattery Channel].

## Message Dictionary

This section describes the messages that flow through the system.

### Cat Message

Represents a [Cat] through the medium of [JSON].

Field Name | Type                               | Description
-----------|------------------------------------|-------------
breed      | Enum: TABBY, BRITISH_BLUE, UNKNOWN | Domestic cats can be bred and shown as registered pedigreed cats, a hobby known as cat fancy.

> note: the message has more fields in the [HLD][Cat Broker System High Level Design], but they are
> not relevant to this component, so we don't list them!

## Components

### Cat Broker Route

An [Apache Camel] route that implements a [Content-Based Router] which takes
[Cat Messages][Cat Message] from a `Cat Broker` [Apache Kafka] topic and routes them to a
[Cattery Channel] based on advice from a [Destination Cattery Enricher].

### Destination Cattery Enricher

Given a [Cat Message], will return an identifier indicating which [Cattery Channel] to route the
message to.

## Business Rules

### BR001: Tabby Cats Should Go To Cats-r-us

#### Given

A [Cat Message]: `cat`

- `breed` = `TABBY`

#### Then

Route `cat` to the [Cattery Channel] for `cats-r-us`

[Cat]: #cat
[Cattery Channel]: #cattery-channel
[Cat Message]: #cat-message
[Destination Cattery Enricher]: #destination-cattery-enricher
[JSON]: https://json.org/ "(JavaScript Object Notation) is a lightweight data-interchange format"
[Content-Based Router]: https://www.enterpriseintegrationpatterns.com/patterns/messaging/ContentBasedRouter.html
[Dead Letter Channel]: #dead-letter-channel
[Cat Broker System High Level Design]: cat-broker-system-high-level-design.md
[Java]: https://www.java.com/en/ "general purpose programming language"
[Spring Boot]: https://spring.io/projects/spring-boot "a framework for creating standalone Java applications"
[Apache Camel]: https://camel.apache.org/ "Camel is an open source integration framework that empowers you to quickly and easily integrate various systems consuming or producing data."
[Apache Kafka]: https://kafka.apache.org/ "A distributed streaming platform"
