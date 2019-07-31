// Copyright (c) 2018 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerina/kafka;
import ballerina/transactions;

string topic = "abort-transaction-topic";

kafka:ProducerConfig producerConfigs = {
    bootstrapServers:"localhost:9144, localhost:9145, localhost:9146",
    clientId:"abort-transaction-producer",
    acks:"all",
    noRetries:3,
    transactionalId:"abort-transaction-test-producer"
};

function funcKafkaAbortTransactionTest() returns boolean {
    string msg = "Hello World Transaction";
    byte[] serializedMsg = msg.toBytes();
    var result = kafkaAdvancedTransactionalProduce(serializedMsg);
    return !(result is error);
}

function kafkaAdvancedTransactionalProduce(byte[] msg) returns error? {
    kafka:Producer kafkaProducer = new(producerConfigs);
    error? returnValue = ();
    error err = error("custom error");
    transaction {
        var result = kafkaProducer->send(msg, topic, (), 0, ());
        result = kafkaProducer->send(msg, topic, (), 0, ());
    } committed {
        returnValue = ();
    } aborted {
        return err;
    }
    return returnValue;
}