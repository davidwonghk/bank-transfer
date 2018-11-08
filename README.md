# README

[Introduction](#markdown-header-introduction)

[Setup](#markdown-header-setup)

[Functional-Test](#markdown-header-functional-test)

[RESTful-API](#markdown-header-restful-api)

[Directory-Structure](#markdown-header-directory-structure)

[System-Design](#markdown-header-system-design)

[Depended-Libraries](#markdown-header-depended-libraries)

[TODO](#markdown-header-todo)

## Introduction
This project implements a RESTful API in JAVA for money transfers between accounts as for a coding demo.
All the interface are kept to be minimal, and only one currency(GBP) is supported, while the exchange rate keeps constant over time.

### Requirements and constraints
- Spring is not allowed to be used
- the datastore should run in-memory for the sake of this demo
- the final result should be executable as a standalone program


---

## Setup 
### build
This project use [Maven](https://maven.apache.org/) as for the software project management tool.

1. Install [maven](https://maven.apache.org/install.html) and [jdk8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

2. In command line interface, run ```mvn clean compile assembly:single```

After that, you will see three jars file are built in the target/ directory:

- bank-transfer-xxx-jar-with-dependencies.jar
the standalone java program with all the dependencies embed

- bank-transfer-xxx-javadoc.jar
the auto generated java documentation

- bank-transfer-xxx.jar
the java program without dependencies
(note that xxx is the version number)


### unit test

In command line interface, run 
```mvn -Dtest=com.david.bank.service.* test```

expected output:
```
Tests run: 7, Failures: 0, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```


### run
In command line interface, run 
```java -Dport=3000 -cp target/bank-transfer-*-jar-with-dependencies.jar com.david.bank.Application```

while 3000 is the port number you would like the webserver to be ran on, you may change it to whatever you like

expected output:

```
INFO [main] [com.david.bank.Application] [main] [com.david.bank.Application.main(Application.java:131)] - Jersey app started at http://localhost:3000/
Hit enter to stop it...
```

to monitor the application log during running:

``` tail -f log/app.log ```

--

## Fucntional-Test

Right after [running the server](#markdown-header-run) at localhost:3000 (configurable)

(to ensure the account balances are in default values)

In command line interface, run

``` mvn -Dtest=com.david.bank.* -Dtest=test.api.* test -Dserver.host='http://localhost' -Dserver.port=3000 ```

expected output:

```
Results :

Tests run: 9, Failures: 0, Errors: 0, Skipped: 0

```

or to run it manually, see [TEST.md](TEST.md)

---

## RESTful-API

for details, please see [TEST.md](TEST.md)

#### GET /api/account

Return a list of bank accounts. 

In this demo there are always 3 accounts and they cannot be removed/edit (except their balance can be changed by money transfer).


#### GET /api/account/{accountNumber}

Return a particular bank account by the unique identifier {accountNumber}.

##### expected error

* 404: bank account does not not exists


#### POST /api/transaction

make a money transaction request between accounts (sender and receiver)

fields of json request body

* senderAccountNumber (mandatory)
* receiverAccountNumber (mandatory)
* currency (mandatory, currently only support GBP)
* amount (mandatory, in string format for accuracy)
* description (optional)

example request body:
```
{
	"senderAccountNumber": "a0002",
	"receiverAccountNumber": "a0001",
	"currency": "GBP",
	"amount": "17",
	"description": "our last dinner"
}
```

##### expected error

* 400: invalid money amount, eg. sender does not have enough fund
* 404: bank account does not exists (sender/receiver)
* 422: unsupported currency

#### GET /api/transaction

Return a list of money transfer transactions. 


#### GET /api/transaction/{transactionNumber}
Return a particular money transfer transaction by the unique identifier {transactionNumber}.

##### expected error

* 404: transaction does not not exists

---

## Directory-Structure

Java Doc: [link here](./javadoc)

Main class: [src/main/controllers/com/david/bank/Application.java](src/main/controllers/com/david/bank/Application.java)

Controllers of API: [src/main/controllers/com/david/bank/*Resource.java](src/main/controllers/com/david/bank/)

Services (to provide business logic): [src/main/services/com/david/bank/*Service.java](src/main/services/com/david/bank/)

- BankAccountService: to provide business about bank accounts (readonly for this demo)
- TransactionService (minimal API provied for money transfer)
- CurrencyService (a mock only, assume there should be another api (server) for this)


Domain Objects (Entity):[src/main/domain/com/david/bank/*.java](src/main/domain/com/david/bank/)

Data Access Objects(Mock only, in memory): [src/main/services/com/david/bank/dao/*Dao.java](src/main/services/com/david/bank/dao/)

logfile properties: [src/main/resources/log4j2.properties](src/main/resources/log4j2.properties)

---

## System-Design

#### Data model

The data model is stored in memony and keep as minimal for demo purpose

![ER Diagram](https://bitbucket.org/cornsdev/bank-transfer/raw/41d50b02fc4f5d2474ed22a90d7efc3d9c5f2f52/diagram/er.png)

#### Sequence Diagram for current design
![Producer Consumer pattern iwth a blocking queue](https://bitbucket.org/cornsdev/bank-transfer/raw/23c17147e7182a9c8787e51d9783352ec03068c7/diagram/sequence.png)

#### Transactional ensuring architecture by Producer-Consumer pattern
Under the hood this bankend uses the Producer-Consumer(with blocking queues) architecture to ensure the money transfers are transactional, even under the stateless HTTP RESTFul protocal. For demo purpose there is only one consumer, but in practice there can be multiple consumers consuming pending transactions base on the sender/receiver account numbers. 

![Producer Consumer pattern with a blocking queue](https://bitbucket.org/cornsdev/bank-transfer/raw/23c17147e7182a9c8787e51d9783352ec03068c7/diagram/producer-consumer.png)

For example, when the consumer#1 is consuming money transfer from ac1 to ac2, all the forth-coming pending transactions realted to ac1 and ac2 should assign to consumer#1 (actually, each consumer should has their own queue)

---

## Depended-Libraries

the list below is for references only. The built jar file (bank-transfer-xxx-jar-with-dependencies.jar) should have embed all the dependecies inside.

* [jersey](https://jersey.github.io/)
* [log4j2](https://logging.apache.org/log4j/2.x/)
* [junit4](https://junit.org/junit4/)
* [mockito](https://site.mockito.org/)
* [rest-assured](http://rest-assured.io/)

---


# TODO

* more unit testings
* support different currency
* support pagination
