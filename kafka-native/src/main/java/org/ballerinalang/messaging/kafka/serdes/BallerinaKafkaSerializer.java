/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.messaging.kafka.serdes;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serializer;
import org.ballerinalang.jvm.api.BRuntime;
import org.ballerinalang.jvm.api.values.BArray;
import org.ballerinalang.jvm.api.values.BObject;
import org.ballerinalang.messaging.kafka.utils.KafkaConstants;
import org.ballerinalang.messaging.kafka.utils.KafkaUtils;

import java.util.Map;

import static org.ballerinalang.messaging.kafka.utils.KafkaConstants.ON_CLOSE_METADATA;
import static org.ballerinalang.messaging.kafka.utils.KafkaConstants.ON_SERIALIZE_METADATA;

/**
 * Represents a serializer class for ballerina kafka module.
 */
public class BallerinaKafkaSerializer implements Serializer {

    private BObject serializerObject = null;
    private int timeout = 30000;

    @Override
    public void configure(Map configs, boolean isKey) {
        if (isKey) {
            this.serializerObject = (BObject) configs.get(KafkaConstants.PRODUCER_KEY_SERIALIZER_CONFIG);
        } else {
            this.serializerObject = (BObject) configs.get(KafkaConstants.PRODUCER_VALUE_SERIALIZER_CONFIG);
        }
        this.timeout = (int) configs.get(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG);
    }

    @Override
    public byte[] serialize(String topic, Object data) {
        Object[] args = new Object[]{data, false};
        BArray result = (BArray) KafkaUtils.invokeMethodSync(BRuntime.getCurrentRuntime(), this.serializerObject,
                                                             KafkaConstants.FUNCTION_SERIALIZE, null,
                                                             ON_SERIALIZE_METADATA, timeout, args);
        return result.getBytes();
    }

    @Override
    public void close() {
        KafkaUtils.invokeMethodSync(BRuntime.getCurrentRuntime(), this.serializerObject, KafkaConstants.FUNCTION_CLOSE,
                                    null, ON_CLOSE_METADATA, timeout);
    }
}
