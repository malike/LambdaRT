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
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import st.malike.model.elasticsearch.ESDemographic;
import storm.starter.util.TupleHelpers;

/**
 *
 * @author malike_st
 */
@Component
public class EventNormalizerBolt extends BaseBasicBolt {

    private static Logger logger = Logger.getLogger(EventNormalizerBolt.class);
    Map config = new HashMap<>();

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("event", "eventlog", "lnsargs", "message"));
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        try {
            Map<String, String> lnsArguments = new HashMap<>();
            if (!isTickTuple(input)) {
                String message = input.getString(0);
                String[] eventString = message.split("##");
                if (eventString.length >= 4) {
                    String event = eventString[0].trim().toLowerCase();
                    String date = eventString[1];
                    String args = eventString[2];
                    String msg = eventString[3];
                    Date dt = new SimpleDateFormat(config.get("defaultDateFormat").toString(), Locale.getDefault()).parse(date);

                    //create a model of event
                    ESDemographic ed = new ESDemographic();
                    ed.setId(UUID.randomUUID().toString());
                    ed.setEvent(event);
                    ed.setDateCreated(new Date());

                    if (null != args) {
                        String[] argString = args.split("&&");
                        for (String a : argString) {
                            String[] param = a.split("\\|");
                            lnsArguments.put(param[0], param[1]);
                        }
                    }
                    collector.emit(new Values(event, ed, lnsArguments, msg));
                }
            }
        } catch (ParseException | IllegalArgumentException ex) {
            System.out.println("Error ==> " + ex);
            logger.error("Error ==> ", ex);
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
