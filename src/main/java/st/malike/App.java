package st.malike;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import st.malike.service.kafka.KafkaProduceService;
import st.malike.service.storm.kafka.LambdaLNSTopology;

/**
 * Hello world!
 *
 */
@Configuration
@EnableAutoConfiguration
@EnableMongoRepositories(basePackages = "st.malike.repository.mongodb")
@EnableElasticsearchRepositories(basePackages = "st.malike.repository.elasticsearch")
@ComponentScan
public class App {

    //lets do the redundant process of reading a file and logging events to kafka
//    static String filePath = "K:\\PatienceNDispear";
    static String filePath = "/home/dreamadmin/Desktop/Desktop/PatienceNDispear";

    public static void main(String[] args) throws Exception {

        System.out.println("Hello World! 0:-] ");
        // submit the topology to our cluster

        ApplicationContext context = SpringApplication.run(App.class, args);

        FileReader fileReader;
        try {

            System.out.println("Working Directory = " + System.getProperty("user.dir"));

            fileReader = new FileReader(filePath);
            BufferedReader reader = new BufferedReader(fileReader);
            //read files lines and jus log it... eventually to kafka

            KafkaProduceService kafkaProduceService = context.getBean(KafkaProduceService.class);

            StormTopology stormTopology = new LambdaLNSTopology().spoutTopologySetup();

            //start storm cluster
            LocalCluster cluster = new LocalCluster();
            Config config = new Config();
            config.setDebug(true);
            config.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 60); //ample time to summarize and save data
            config.put("defaultDateFormat", "yyyy-MM-dd HH:mm:ss");
            config.put("indexName", "lambda_lns_test");
            config.put("summaryTypeName", "demographic_summary");
            config.put("baseURL", "http://localhost:8078/lns/");
            cluster.submitTopology("lns-topology", config, stormTopology);

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

//            Thread.sleep(4000);
//
//            cluster.shutdown();
        } catch (FileNotFoundException ex) {
            throw ex;
        } catch (IOException e) {
            throw e;
        }

    }
}
