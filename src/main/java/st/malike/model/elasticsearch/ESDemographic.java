/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.model.elasticsearch;

import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;


/**
 *
 * @author malike_st
 */
@Document(indexName = "lambda_rt", type = "demographic")
public class ESDemographic {

    @Id
    private String id;   
    private String event;   
    private Date dateCreated;

    public ESDemographic() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

}
