/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.http;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import st.malike.service.DemographicService;
import st.malike.service.ESDemographicSummaryService;

@Controller
public class AppController {

    static Map<String, String> data = new HashMap<>();
    @Autowired
    private ESDemographicSummaryService eSDemographicSummaryService;
    @Autowired
    private DemographicService demographicService;

    @RequestMapping("/")
    @ResponseBody
    public String helloWorld() {
        return "hello 0:-]";
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

    @RequestMapping("/lns/summary/{event}")
    @ResponseBody
    public String summary(@PathVariable String event, HttpServletResponse response) {
        return "";
    }

    @RequestMapping("/lns/history/{event}")
    @ResponseBody
    public String history(@PathVariable String event, HttpServletResponse response) {
        return "";
    }
}
