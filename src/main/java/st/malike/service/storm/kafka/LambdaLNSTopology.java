/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.service.storm.kafka;

import backtype.storm.generated.StormTopology;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import st.malike.service.storm.EventNormalizerBolt;
import st.malike.service.storm.LNSBolt;
import st.malike.service.storm.MonitoringBolt;
import storm.kafka.BrokerHosts;
import storm.kafka.HostPort;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StaticHosts;
import storm.kafka.StringScheme;
import storm.kafka.trident.GlobalPartitionInformation;

/**
 *
 * @author malike_st
 */

public class LambdaLNSTopology {

    public StormTopology spoutTopologySetup() { //setup our topology
        GlobalPartitionInformation hostsAndPartitions = new GlobalPartitionInformation(); //add brokers for 3 partitions
        hostsAndPartitions.addPartition(0, new HostPort("localhost", 9092));
        hostsAndPartitions.addPartition(1, new HostPort("localhost", 9092));
        hostsAndPartitions.addPartition(2, new HostPort("localhost", 9092));
        BrokerHosts brokerHosts = new StaticHosts(hostsAndPartitions);

        //configuration
        SpoutConfig spoutConfig = new SpoutConfig(brokerHosts, "TestRunning", "", "groupz1");
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        spoutConfig.forceFromStart = false;

        //topology definition
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("eventstream", new KafkaSpout(spoutConfig), 3);
        builder.setBolt("normalizer", new EventNormalizerBolt()).shuffleGrouping("eventstream");
        builder.setBolt("lns", new LNSBolt()).fieldsGrouping("normalizer", new Fields("event")); // to make sure events are sent to the same bolt             
        builder.setBolt("notification", new MonitoringBolt()).fieldsGrouping("lns", new Fields("event"));//group lns tuples by events
        return builder.createTopology(); //return the topology to be used submited to our cluster
    }
}
