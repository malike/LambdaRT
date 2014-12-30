/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.service.storm.kafka;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import st.malike.service.kafka.KafkaProduceService;

/**
 *
 * @author dreamadmin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/test-kafka-config.xml"})
//@Ignore
public class EventLoggerTest {

    static String filePath = "/home/dreamadmin/Desktop/Desktop/PatienceNDispear";
    /**
     * @param args the command line arguments
     */
    @Autowired
    private KafkaProduceService kafkaProduceService;

    @BeforeClass
    public static void setupClass() {
    }

    @Test
    public void send() throws Exception {
        // submit the topology to our cluster
        FileReader fileReader;
        try {
            System.out.println("Working Directory = " + System.getProperty("user.dir"));
            fileReader = new FileReader(filePath);
            BufferedReader reader = new BufferedReader(fileReader);
            //read files lines and jus log it... eventually to kafka

            Thread.sleep(100);
            String str;
            while ((str = reader.readLine()) != null) {
                //log here ==>
                for (String ev : str.split(" ")) {
                    if (!ev.trim().isEmpty()) {
                        String event = ev + "##" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date())
                                + "##email|st.malike@dreamoval.com&&subject|Word Found&&phone|+233201234567##The word the was found is " + ev;
                        kafkaProduceService.send("TestRunning", ev, event);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            throw ex;
        } catch (IOException e) {
            throw e;
        }
    }
}
