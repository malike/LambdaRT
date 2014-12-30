/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.service.lns;

import com.google.gson.Gson;
import java.io.Serializable;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import st.malike.model.DemographicSummary;
import st.malike.service.AppService;
import st.malike.util.LNSConfig;

/**
 *
 * @author malike_st
 */
@Service
public class LiveNotificationService implements Serializable{

    private static Logger logger = Logger.getLogger(LiveNotificationService.class);
    private Map<String, String> lnsConfigMap;

    public void send(DemographicSummary currentSummary, Map<String, String> args, String message) {
        if (lnsConfigMap.containsKey(currentSummary.getEvent())) {
            LNSConfig lnsConfig = new Gson().fromJson(lnsConfigMap.get(currentSummary.getEvent()), LNSConfig.class); //map our config json to object
            //pick the class we would use to run service
            if (null != lnsConfig) {
                if (null != lnsConfig.getClassFile()) {
                    try {
                        if (currentSummary.getOverAllTotal() == lnsConfig.getCount()) {
                            Class svcClass = Class.forName(lnsConfig.getClassFile());
                            AppService svc = (AppService) svcClass.newInstance();
                            svc.run(args);
                        }
                        // you can check if hour and/or minute counts match
                    } catch (SecurityException e) {
                        System.out.println("Error ==> " + e);
                        logger.error("Error ==> ", e);
                    } catch (IllegalAccessException e) {
                        System.out.println("Error ==> " + e);
                        logger.error("Error ==> ", e);
                    } catch (InstantiationException e) {
                        System.out.println("Error ==> " + e);
                        logger.error("Error ==> ", e);
                    } catch (ClassNotFoundException e) {
                        System.out.println("Error ==> " + e);
                        logger.error("Error ==> ", e);
                    }
                }
            }
        }
    }
}
