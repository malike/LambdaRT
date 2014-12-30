/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import st.malike.util.HourTimeCount;

/**
 *
 * @author malike_st
 */
@Document(collection = "demographic_summary")
public class DemographicSummary implements Serializable {

    @Id
    private String id;
    @Indexed
    private String event;
    private int overAllTotal;
    private List<HourTimeCount> hourTimeCount;
    @Indexed
    private Date dateCreated;

    public DemographicSummary() {
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

    public int getOverAllTotal() {
        return overAllTotal;
    }

    public void setOverAllTotal(int overAllTotal) {
        this.overAllTotal = overAllTotal;
    }

    public List<HourTimeCount> getHourTimeCount() {
        return hourTimeCount;
    }

    public void setHourTimeCount(List<HourTimeCount> hourTimeCount) {
        this.hourTimeCount = hourTimeCount;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
