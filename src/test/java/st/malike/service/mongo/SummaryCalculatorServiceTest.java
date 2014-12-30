/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.service.mongo;

import com.google.gson.Gson;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import st.malike.model.DemographicSummary;

/**
 *
 * @author malike_st
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/test-mongo-config.xml"})
//@Ignore
public class SummaryCalculatorServiceTest {

    @Autowired
    @Qualifier(value = "summaryCalculatorService")
    SummaryCalculatorService summaryCalculatorService;

    @Test
    public void getSummary() throws ParseException {
        DemographicSummary ds = summaryCalculatorService.getSummaryByDay(null, "the", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-11-14 00:00:00"));
        System.out.println("Data ==> " + new Gson().toJson(ds));
    }
}
