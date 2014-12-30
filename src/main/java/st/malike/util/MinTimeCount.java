/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.util;

import java.io.Serializable;

/**
 *
 * @author malike_st
 */
public class MinTimeCount implements Serializable{

    private Integer time;
    private Integer count;

    public MinTimeCount() {
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
