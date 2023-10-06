# secure-sms-broker

SMS Broker with scheduling and decent confidentiality guarantees

** WORK IN PROGRESS **

Homed at: https://github.com/zwets/secure-sms-broker


## Background

This SMS Broker was developed for a health research project with strong
data confidentiality requirements.

It offers near-end-to-encryption of SMS messages between client and SMS
centre.  The SMS destination number and message contents are decrypted 
only temporarily when the message leaves for the SMSC.

The project evolved from an earlier iteration that had
[SMSTools3 Server](https://git.kcri.it/sms/smstools-resources) as its
backend, and JMS message queues as its interface.

This version has *JMS* or *Kafka* as its front-end and *JSMPP* to an
*SMSC* as its back-end.

> **Important**
>
> SMS is an seriously unreliable and insecure communication mechanism.
>
> This broker offers high reliability and confidentiality between client
> and service provider, but that's were the issues are.
>
> Please take note of the serious warnings in the JSMPP and Camel SMPP
> project documentation.

## Installation

Refer to the README files in the component projects.

## Architecture Overview

@TODO@

Two main components: the _scheduler_ and the _broker_, plus utilities.

### Functional

The SMS Broker exposes its services over @TODO@ JMS/Kafka.

### Components

Apart from the SMS Broker Server component, this project includes:

* The **SMS Utils JAR** (`sms-utils`) contains shared and generic components
  such the class `SmsMessage` to conveniently generate and parse SMS messages.

* The `kafka-message-scheduler` from <https://github.com/etf1/kafka-message-scheduler>

* The `kafka-message-scheduler-admin` <https://github.com/etf1/kafka-message-scheduler-admin>
