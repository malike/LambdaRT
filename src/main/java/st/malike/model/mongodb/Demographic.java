/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.model.mongodb;

import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author malike_st
 */
@Document(collection = "demographic")
public class Demographic {

    @Id
    private String id;
    @Indexed
    private String event;
    @Indexed
    private Date dateCreated;

    public Demographic() {
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
