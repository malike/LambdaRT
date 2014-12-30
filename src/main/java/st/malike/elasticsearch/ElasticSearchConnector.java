/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.elasticsearch;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

/**
 *
 * @author malike_st
 */
public class ElasticSearchConnector {

    private static ElasticSearchConnector instance = null;
    private static final Object lock = new Object();
    private static Client client;
    private static Node node;

    public static ElasticSearchConnector instance() {
        synchronized (lock) {
            if (null == instance) {
                prepareClient();
                instance = new ElasticSearchConnector();
            }
        }
        return instance;
    }

    public static void prepareClient() {
        node = nodeBuilder().node();
        client = node.client();
    }

    public void closeNode() {
        if (!node.isClosed()) {
            node.close();
        }
    }

    public Client getClient() {
        return client;
    }
}
