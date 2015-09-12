/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.service.kafka;

import java.util.List;
import kafka.consumer.ConsumerIterator;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author malike_st
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/test-kafka-config.xml"})
public class KafkaConsumerServiceTest {

    @Autowired
    private KafkaConsumerService kafkaConsumerService;

    @Test
    @Ignore
    public void consume() {
        while (true) {
            String h = kafkaConsumerService.consume("TestRunning");
            if (null == h) {
                System.out.println("Nothin to read => closing ..");                
            } else {
                System.out.println(h);
            }
        }
    }
}
