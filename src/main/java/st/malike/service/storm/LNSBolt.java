/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.stereotype.Component;
import st.malike.model.mongodb.Demographic;
import st.malike.model.mongodb.DemographicSummary;
import st.malike.service.DemographicService;
import st.malike.service.DemographicSummaryService;
import st.malike.service.AggregatorService;
import st.malike.util.RealTimeData;
import storm.starter.util.TupleHelpers;

/**
 *
 * @author malike_st
 */
@Component
public class LNSBolt extends BaseBasicBolt {

    Map<String, List<Demographic>> events = new HashMap<>();
    Map config = new HashMap<>();
    RealTimeData rtData = new RealTimeData();
    private DemographicSummaryService demographicSummaryService;
    private DemographicService demographicService;
    private DemographicSummaryService summaryElasticSearchService;
    private AggregatorService aggregatorService;

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("event", "data"));
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        try {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            String indexName = (String) config.get("indexName");
            String summaryTypeName = (String) config.get("summaryTypeName");
            Object evLogData = null;
            Object lnsargs = null;
            String lnsMessage = null;
            Map<String, String> lnsArguments = new HashMap<>();
            Demographic realTimeData;
            DemographicSummary batchSummary;
            try {
                evLogData = input.getValueByField("eventlog");
                lnsargs = input.getValueByField("lnsargs");
                lnsMessage = input.getString(3);
            } catch (IllegalArgumentException e) {
            }

            if (null != evLogData) {
                //add to collection
                realTimeData = (Demographic) evLogData;
                lnsArguments = (Map<String, String>) lnsargs;
                String event = input.getString(0);
                rtData.setCount(1);
                rtData.setEvent(event);
                rtData.setDateCreated(realTimeData.getDateCreated());
                List<Demographic> demographics = (null == events.get(event)) ? new LinkedList<Demographic>() : events.get(event);
                demographics.add(realTimeData);
                events.put(event, demographics);

                collector.emit(new Values(event, gson.toJson(rtData))); //emit real time data
            }

            if (isTickTuple(input)) {
                if (!events.isEmpty()) {

                    //Date dateToSummarize = (null == demographicSummary) ? new Date() : demographicSummary.getDateCreated();
                    Date dateToSummarize = new Date();
                    for (Map.Entry<String, List<Demographic>> eventEntry : events.entrySet()) {
//              
                        //check if summary already exist in elastic search
                       // DemographicSummary demographicSummary = summaryElasticSearchService.findByEventAndDate(indexName, summaryTypeName, eventEntry.getKey(), dateToSummarize);

                        //save batch of events
                        demographicService.saveDemographic(eventEntry.getValue());

                        //batchSummary = aggregatorService.getSummaryByDay(demographicSummary, eventEntry.getKey(), dateToSummarize);
                        //summarise current batch of events                   

                        //save new summary if exist
                        demographicSummaryService.saveDemographicSummary(batchSummary);

                        //send to lns service                     
//                    liveNotificationService.send(batchSummary, lnsArguments, lnsMessage);

                    }

                    events = new HashMap<>(); //reset arrays
                }
            }

        } catch (Exception e) {
            System.out.println("Error in LNSBOLT ===> " + e);
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
        try {
            this.config = stormConf;
            // reinitialize Spring IoC-- a hack
            AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
            ctx.getEnvironment().getPropertySources().addFirst(new ResourcePropertySource("classpath:application.properties"));
            ctx.scan("st.malike");
            ctx.refresh();
            demographicSummaryService = ctx.getBean(DemographicSummaryService.class);
            demographicService = ctx.getBean(DemographicService.class);
            summaryElasticSearchService = ctx.getBean(DemographicSummaryService.class);           
            aggregatorService = ctx.getBean(AggregatorService.class);
        } catch (IOException ex) {
            Logger.getLogger(LNSBolt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
