#/bin/bash

if [ -z $1 ]; then
	port='3000';
else
	port=$1
fi

java -Dport=$port -cp target/bank-transfer-1.0-SNAPSHOT-jar-with-dependencies.jar com.david.bank.Application
