/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.util;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author malike_st
 */
public class RealTimeData implements Serializable {

    private String event;
    private int count;
    private Date dateCreated;

    public RealTimeData() {
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
