# secure-sms-broker

SMS Broker with decent confidentiality guarantees

Homed at: https://github.com/zwets/secure-sms-broker


## Background

This SMS Broker was developed for a health research project with strong
data confidentiality requirements.

It offers the confidentiality guarantee that SMS data is stored fully
encrypted, and has a mechanism for securely hashing phone numbers.

The project evolved from an earlier iteration that had
[SMSTools3 Server](https://git.kcri.it/sms/smstools-resources) as its
backend, and JMS message queues as its interface.

> **Important** SMS is an inherently unreliable and insecure communication
> mechanism (see the JSMPP project documentation for details).  This broker
> offers certain guarantees for 

## Installation

Refer to the README files in the component projects.


## Architecture Overview

This document provides an overview of the SMS Broker architecture.

### Functional

The SMS Broker Server exposes SMS services over @TODO@ JMS/Kafka.

### Components

Apart from the SMS Broker Server component, this project includes:

* The **SMS Utils JAR** (`sms-utils`) contains shared and generic components
  such the class `SmsMessage` to conveniently generate and parse SMS messages.

