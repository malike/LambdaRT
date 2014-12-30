/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.web;

import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import st.malike.elasticsearch.service.SummaryElasticSearchService;
import st.malike.model.DemographicSummary;

@Controller
public class AppController {

    static Map<String, String> data = new HashMap<>();
    @Autowired
    private SummaryElasticSearchService summaryElasticSearchService;
    static String SHARDINDEX = "lambda_lns_test";
    static String TYPENAME = "demographic_summary";

    @RequestMapping("/")
    @ResponseBody
    public String helloWorld() {
        return "hello";
    }

    @RequestMapping("/lns/realtime/{event}")
    @ResponseBody
    public String realtime(@PathVariable String event,
            @RequestBody(required = false) String dat,
            HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/event-stream");
        if (null == dat) {
            return "data:" + data.get(event) + "\n\n";
        } else {
            data.put(event, dat);
            return "data:[]\n\n";
        }
    }

    @RequestMapping("/lns/history/{event}")
    @ResponseBody
    public String history(@PathVariable String event, HttpServletResponse response) {
        DateTime currentDate = new DateTime();
//        String date = new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(currentDate.toDate());
//        DemographicSummary demographicSummary = demographicSummaryService.getDemographicSummaryHistoryByEventAndDateForMin(event, date);
        DemographicSummary demographicSummary = summaryElasticSearchService.findByEventAndDate(SHARDINDEX, TYPENAME, event, currentDate.toDate());
        if (null != demographicSummary) {
            return new Gson().toJson(demographicSummary);
        } else {
            return "[]";
        }
    }
}
