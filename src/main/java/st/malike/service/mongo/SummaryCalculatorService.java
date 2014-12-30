/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.service.mongo;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import st.malike.model.DemographicSummary;
import st.malike.util.HourTimeCount;
import st.malike.util.MinTimeCount;

/**
 *
 * @author malike_st
 */
@Service
public class SummaryCalculatorService implements Serializable {

    @Autowired
    private MongoTemplate mongoTemplate;
    private static String mapByDayFunction =
            " function(){ "
            + "     emit(this.dateCreated.getHours(), {minute: this.dateCreated.getMinutes() , count:1 , date:this.dateCreated});"
            + "}";
//    private static String mapByDayFunction =
//            " function(){ "
//            + "     emit(this.dateCreated.getHours(), {minute:this.dateCreated.getMinutes(), count:1 });"
//            + "}";
//    private static String reduceByDayFunction =
//            " function(key, values) { "
//            + "     var result = { minutesum : [] , count : 0};"
//            + "     for(var i= 0;i<values.length;i++) {"
//            + "       var vl = values[i];"
//            + "       var ob = {min:vl.minute,count:vl.count};"
//            + "       result.minutesum[i] = ob; "          
//            + "     }"
//            + "   return result;"
//            + "} ";
    private static String reduceByDayFunction =
            " function(key, values) { "
            + "     var result = { minutesum : {} , count : 0 ,err: {}};"
            + "     var j = 0;"
            + "     for(var i = 0; i < values.length; i++) {"
            + "       j= j + 1;"
            + "       var vl = values[i];"
            + "       if(vl.minute in result.minutesum){"
            + "          result.minutesum[vl.minute] = vl.count + result.minutesum[vl.minute];"
            + "       } else {"
            + "            if(!isNaN(parseFloat(vl.minute))){"
            //            + "         if(typeof vl.date !== \"undefined\"){ "
            + "         if(true){ "
            + "           result.minutesum[vl.minute] = vl.count; "
            + "         }else{"
            //            + "             result.minutesum[new Date().getMinutes()] = vl.count;"           
            + "         }"
            + "         }"
            + "       }"
            + "       result.count +=  vl.count;"
            //            + "     });"
            + "     }"
            + "   return result;"
            + "} ";

    //vl.minute in result.minutesum typeof vl.minute !== \"undefined\"
    public DemographicSummary getSummaryByDay(DemographicSummary prevDemographicSummary, String event, Date dateCreated) {
        try {
            if (null == prevDemographicSummary) {
                prevDemographicSummary = new DemographicSummary();
                prevDemographicSummary.setId(UUID.randomUUID().toString());
                prevDemographicSummary.setDateCreated(dateCreated);
                prevDemographicSummary.setEvent(event);
            }
            List<HourTimeCount> hourTimeCounts = new ArrayList<>();
            int overAllCount = 0;
            //set stats
            Iterator it = getSummaryForDay(event, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateCreated));
            while (it.hasNext()) {
                Map map = (Map) it.next();
                String key = map.get("_id").toString();
                HourTimeCount htc = new HourTimeCount();
                htc.setHour(Double.valueOf(key.trim()).intValue());
                List<MinTimeCount> mtcs = new ArrayList<>();
                int hrCnt = 0;
                if (map.get("value") instanceof Map) {
                    Map summary = (Map) map.get("value");
                    overAllCount += Double.valueOf(summary.get("count").toString().trim());
                    HashMap<String, Double> it1 = (LinkedHashMap<String, Double>) summary.get("minutesum");
                    if (null != it1) {
                        for (Map.Entry<String, Double> entry : it1.entrySet()) {
                            MinTimeCount count = new MinTimeCount();
                            Integer min = Integer.parseInt(entry.getKey());
                            Integer cnt = entry.getValue().intValue();
                            count.setCount(cnt);
                            count.setTime(min);
                            mtcs.add(count);
                            hrCnt += cnt;
                        }
                    } else {
                        Double min = (Double) summary.get("minute");
                        Double minCnt = (Double) summary.get("count");
                        if (null != min && null != minCnt) {
                            MinTimeCount count = new MinTimeCount();
                            count.setCount(minCnt.intValue());
                            count.setTime(min.intValue());
                            mtcs.add(count);
                            hrCnt += minCnt;
                        }
                    }
                }
                htc.setMinSummary(mtcs);
                htc.setHourTotal(hrCnt);
                hourTimeCounts.add(htc);
            }
            prevDemographicSummary.setHourTimeCount(hourTimeCounts);
            prevDemographicSummary.setOverAllTotal(overAllCount);
            return prevDemographicSummary;
        } catch (ParseException | NumberFormatException ex) {
            System.out.println("Error ==> " + ex);
        }
        return null;
    }

    public Iterator getSummaryForDay(String event, String date) throws ParseException {
        Date startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        Date endDate = new DateTime(startDate).plusDays(1).toDate();
        Query query = new Query();
        query.addCriteria(Criteria.where("event").is(event));
        query.addCriteria(Criteria.where("dateCreated").gte(startDate).lte(endDate));
        query.with(new Sort(Sort.Direction.DESC, "dateCreated"));
        return mongoTemplate.mapReduce(query, "demographic", mapByDayFunction, reduceByDayFunction, Map.class).iterator();
    }
}
//private static String reduceByDayFunction =
//            " function(key, values) { "
//            + "     var result = { minutesum : {} , count : 0};"
//            + "     values.forEach(function(vl) {"
//            + "       if(vl.minute in result.minutesum){"
//            + "          result.minutesum[vl.minute] = 1 + result.minutesum[vl.minute];"
//            + "       } else {"
//            + "         if(true){ "
//            + "           result.minutesum[vl.minute] = 1; "
//            + "         }"
//            + "       }"
//            + "       result.count +=  vl.count;"
//            + "     });"
//            + "   return result;"
//            + "} ";