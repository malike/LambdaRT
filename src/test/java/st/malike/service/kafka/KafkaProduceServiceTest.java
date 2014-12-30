/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.service.kafka;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.co.jemos.podam.annotations.PodamCollection;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 *
 * @author malike_st
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/test-kafka-config.xml"})
public class KafkaProduceServiceTest {

    static PodamFactory podamFactory;
    @Autowired
    private KafkaProduceService kafkaProduceService;
    @PodamCollection(nbrElements = 14)
    List<String> randomString = podamFactory.manufacturePojo(ArrayList.class, String.class);

    @BeforeClass
    public static void setupClass() {
        podamFactory = new PodamFactoryImpl();
    }

    @Test
    public void send() throws Exception {
        int ii = 0;
        while (ii < 1000) {
            assertTrue(kafkaProduceService.send("TestRunning", "TestEvent" + ii, ii + " Malike "
                    + new SimpleDateFormat("MMM dd, yyyy HH:mm:ss").format(new Date())));
            ii++;
        }
    }
}
