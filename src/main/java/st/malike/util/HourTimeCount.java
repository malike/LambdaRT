/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.util;

import java.util.List;

/**
 *
 * @author malike_st
 */
public class HourTimeCount {

    private Integer hour;
    private Integer hourTotal;
    private List<MinTimeCount> minSummary;

    public HourTimeCount() {
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getHourTotal() {
        return hourTotal;
    }

    public void setHourTotal(Integer hourTotal) {
        this.hourTotal = hourTotal;
    }

    public List<MinTimeCount> getMinSummary() {
        return minSummary;
    }

    public void setMinSummary(List<MinTimeCount> minSummary) {
        this.minSummary = minSummary;
    }
}
