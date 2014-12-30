/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.service.kafka;

import kafka.producer.Partitioner;

/**
 *
 * @author malike_st
 */
public class KafkaEventPartitioner implements Partitioner {

    
    
    public KafkaEventPartitioner() {
    }

    
    
    public int partition(Object o, int i) {
        int partition = 0;
        String stringKey = (String) o;
        int offset = stringKey.lastIndexOf('.');
        if (offset > 0) {
            partition = Integer.parseInt(stringKey.substring(offset + 1)) % i;
        }
        return partition;
    }
}
