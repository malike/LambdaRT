/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.service.kafka;

import java.util.HashMap;
import java.util.Properties;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author malike_st
 */
@Service
public class KafkaProduceService {

    @Value("${kafka.metadata.broker.list}")
    private String metadataBrokerList;
    @Value("${kafka.serializer.class}")
    private String serializerClass;
    @Value("${kafka.partitioner.class}")
    private String partionerClass;
    @Value("${kafka.request.required.acks}")
    private String requestRequiredAcks;
    Producer<String, String> producer;

    public KafkaProduceService() {
    }

    private void start() { // start feeding the ...
        Properties props = new Properties();
        HashMap<String, String> producerConfigMap = new HashMap<String, String>();
        producerConfigMap.put("metadata.broker.list", metadataBrokerList);
        producerConfigMap.put("serializer.class", serializerClass);
//        producerConfigMap.put("partitioner.class", partionerClass);
        producerConfigMap.put("request.required.acks", requestRequiredAcks);
        props.putAll(producerConfigMap);
        ProducerConfig producerConfig = new ProducerConfig(props);
        producer = new Producer<String, String>(producerConfig);
    }

    public boolean send(String topic, String partition, String message) throws Exception {
        if (null == producer) {
            start();
        }
        try {
            KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, partition, message);
            System.out.println(topic + " " + partition + " " + " " + message);
            producer.send(data);
            return true;
        } catch (Exception e) {
            System.out.println("Err ==> " + e);
            throw e;
        }
    }
}
