# Cat Broker System - High Level Design

The Cat Broker system routes [Cat Messages][Cat Message] to third party [Cattery] systems according
to a set of rules.

## Component Diagram

![Component Diagram](img/cat-broker-component-diagram.svg)

## Glossary

Definitions for terms used later in the document.

### Cat

Felis catus. A small carnivorous mammal.

### Cattery

A cattery is where [cats][Cat] are commercially housed. Catteries come in two varieties – boarding
catteries and breeding catteries.

### Cattery Adapter

A class of component that translates internal Cat Broker messages into appropriate API calls to a third
party [Cattery] system.

### Enterprise Integration Pattern ([EIP])

Patterns for the use of enterprise application integration and message-oriented middleware in the
form of a pattern language.

## Message Dictionary

This section describes the messages that flow through the system.

### Cat Message

Represents a [Cat].

Field Name | Type                               | Description
-----------|------------------------------------|-------------
name       | String                             | The given name of the beast
breed      | Enum: TABBY, BRITISH_BLUE, UNKNOWN | Domestic cats can be bred and shown as registered pedigreed cats, a hobby known as cat fancy.
temperment | Enum: FIESTY, CHILL, UNKNOWN       | Disposition of the beast

## [EIP] Route Diagram

![EIP](img/cat-broker-HLD-EIP.svg)

## Components

A high level description of the intended behavior of the components

> note: there are a million ways we can implement each component, that information goes into Low
> Level Designs for particular implementations of those components. We are only interested in high
> level behavior in a High Level Design.

### Cat Broker Channel Adapter

Exposes an API that accepts [Cat Messages][Cat Message], converts them into [JSON] and passes them
onto an internal message channel.

### Cat Broker

A [Content-Based Router] for [Cat Messages][Cat Message]. If an appropriate channel cannot be found
then the [Cat Message] is routed to a [Dead Letter Channel].

### Cats-r-us Cattery Endpoint

Accepts [Cat Messages][Cat Message] and converts them into an appropriate `Cats-r-us` API call.

### Paws For Thought Cattery Endpoint

Accepts [Cat Messages][Cat Message] and converts them into an appropriate `Paws For Thought` API call.

### Feline Fine Cattery Endpoint

Accepts [Cat Messages][Cat Message] and converts them into an appropriate `Feline Fine` API call.

### Dead Letter Channel

A place to store [Cat Messages][Cat Message] that cannot be routed to a [Cattery].

[Cat]: #cat
[Cattery]: #cattery
[Cat Message]: #cat-message
[Dead Letter Channel]: #dead-letter-channel
[Apache Kafka]: https://kafka.apache.org/ "A distributed streaming platform"
[Content-Based Router]: https://www.enterpriseintegrationpatterns.com/patterns/messaging/ContentBasedRouter.html
[EIP]: https://www.enterpriseintegrationpatterns.com/ "patterns for the use of enterprise application integration and message-oriented middleware in the form of a pattern language"
[JSON]: https://json.org/ "(JavaScript Object Notation) is a lightweight data-interchange format"
