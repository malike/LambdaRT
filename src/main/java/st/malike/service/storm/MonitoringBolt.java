/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.service.storm;

import backtype.storm.Constants;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import storm.starter.util.TupleHelpers;

/**
 *
 * @author malike_st
 */
@Component
public class MonitoringBolt extends BaseBasicBolt {

    private static Logger logger = Logger.getLogger(MonitoringBolt.class);
    Map config = new HashMap<>();

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        try {
            if (!isTickTuple(input)) {
                String event = input.getString(0);
                String baseURL = (String) config.get("baseURL");
                if (null != baseURL) { //don't emit any data if baseURL is not configured          
                    String rtDataURL = baseURL + "realtime/" + event;
                    String realTimeData = input.getString(1) ;
                    try {
                        RestTemplate rest = new RestTemplate();
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                        HttpEntity<String> entity = new HttpEntity<>(realTimeData, headers);
                        rest.exchange(rtDataURL, HttpMethod.PUT, entity, String.class);
                    } catch (Exception e) {
                        System.out.println("Error ==> " + e);
                        logger.error("Error ==> ", e);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error in MONITORINGBOLT ===> " + e);
        }
    }

    private boolean isTickTuple(Tuple tuple) { //check if tuple is from system and a tick tuple
        if (TupleHelpers.isTickTuple(tuple) && tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)) {
            if (tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        this.config = stormConf;
    }
}
