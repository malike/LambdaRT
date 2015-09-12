 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.service.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author malike_st
 */
@Service
public class KafkaConsumerService {

    private ConsumerConnector consumerConnector;
    List<KafkaStream<byte[], byte[]>> streams;
    @Value("${kafka.consumer.zookeeper.connect}")
    private String zkConnect;
    @Value("${kafka.consumer.group.id}")
    private String groupId;
    @Value("${kafka.consumer.zookeeper.session.timeout.ms}")
    private String zkTimeOut;
    @Value("${kafka.consumer.zookeeper.sync.time.ms}")
    private String zkSyncTime;
    @Value("${kafka.consumer.auto.commit.interval.ms}")
    private String consumerAutoCommitInterval;
    @Value("${kafka.consumer.auto.offset.reset}")
    private String consumerAutoOffsetReset;

    public KafkaConsumerService() {
    }

    private void start(String topic) {
        HashMap<String, String> consugmerConfigMap = new HashMap<>();
        consugmerConfigMap.put("zookeeper.connect", zkConnect);
        consugmerConfigMap.put("group.id", groupId);
        consugmerConfigMap.put("zookeeper.session.timeout.ms", zkTimeOut);
        consugmerConfigMap.put("zookeeper.sync.time.ms", zkSyncTime);
        consugmerConfigMap.put("auto.commit.interval.ms", consumerAutoCommitInterval);
        consugmerConfigMap.put("kafka.consumer.auto.offset.reset", consumerAutoOffsetReset);
        Properties props = new Properties();
        props.putAll(consugmerConfigMap);
        ConsumerConfig consumerConfig = new ConsumerConfig(props);
        consumerConnector = Consumer.createJavaConsumerConnector(consumerConfig);
        ConsumerConfig cf = new ConsumerConfig(props);
        ConsumerConnector consumer = Consumer.createJavaConsumerConnector(cf);
        Map<String, Integer> topicCountMap = new HashMap<>();
        topicCountMap.put(topic, 1);
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        streams = consumerMap.get(topic);
    }

    public String consume(String topic) {
        try {
            if (null == consumerConnector) {
                start(topic);
            }

            KafkaStream<byte[], byte[]> stream = streams.get(0);

            ConsumerIterator<byte[], byte[]> it = stream.iterator();
            while (it.hasNext()) {
                return new String(it.next().message());
            }
            stop();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    private void stop() {  //this would stop the kafka consumer
        //in effect starving storm thereby killing he lns 
        try {
            Thread.sleep(10000);
            if (consumerConnector != null) {
                consumerConnector.shutdown();
            }
        } catch (Exception ie) {
        }
    }
}
