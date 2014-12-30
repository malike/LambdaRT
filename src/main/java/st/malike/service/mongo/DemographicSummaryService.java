/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.service.mongo;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import st.malike.model.DemographicSummary;
import st.malike.repository.DemographicSummaryRepository;

/**
 *
 * @author malike_st
 */
@Service
public class DemographicSummaryService implements Serializable {

    @Autowired
    private DemographicSummaryRepository demographicSummaryRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    public DemographicSummary getDemographicSummaryById(Serializable id) {
        return demographicSummaryRepository.findOne(id);
    }

    public DemographicSummary saveDemographicSummary(DemographicSummary demographicSummary) {
        return demographicSummaryRepository.save(demographicSummary);
    }

    public List getDemographicSummary() {
        return demographicSummaryRepository.findAll();
    }

    public void deleteDemographicSummary(Serializable id) {
        demographicSummaryRepository.delete(id);
    }

    public List getDemographicSummaryByEvent(String event) {
        Query query = new Query();
        query.addCriteria(Criteria.where("event").is(event));
        return mongoTemplate.find(query, DemographicSummary.class, "demographic_summary");
    }

    public List getDemographicSummaryByDate(Date dateCreated) {
        Query query = new Query();
        query.addCriteria(Criteria.where("dateCreated").is(dateCreated));
        return mongoTemplate.find(query, DemographicSummary.class, "demographic_summary");
    }

    public DemographicSummary getDemographicSummaryByEventAndDate(String event, Date dateCreated) {
        Query query = new Query();
        query.addCriteria(Criteria.where("event").is(event));
        query.addCriteria(Criteria.where("dateCreated").is(dateCreated));
        return mongoTemplate.findOne(query, DemographicSummary.class, "demographic_summary");
    }

    public DemographicSummary getDemographicSummaryByEventAndDateForMin(String event, String dateCreated) {
        try {
            Date startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:00").parse(dateCreated);
            Date endDate = new DateTime(startDate).plusMinutes(1).toDate();
            Query query = new Query();
            query.addCriteria(Criteria.where("event").is(event));
            query.addCriteria(Criteria.where("dateCreated").gte(startDate).lte(endDate));
            return mongoTemplate.findOne(query, DemographicSummary.class, "demographic_summary");
        } catch (ParseException ex) {
            Logger.getLogger(DemographicSummaryService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Date getLastInsertDate() {
        Query query = new Query();
        query.skip(0).limit(1).with(new Sort(Sort.Direction.DESC, "dateCreated")); //add the limit so mongodb does not throw an exeception of too much data
        List<DemographicSummary> demographicSummary = mongoTemplate.find(query, DemographicSummary.class, "demographic_summary");
        if (demographicSummary == null || demographicSummary.isEmpty()) {
            return null;
        } else {
            return demographicSummary.get(0).getDateCreated();
        }
    }

    public DemographicSummary getDemographicSummaryHistoryByEventAndDateForMin(String event, String date) {
        try {
            Date startDate = new SimpleDateFormat("yyyy-MM-dd 00:00:00").parse(date);
            Date endDate = new DateTime(startDate).plusDays(1).toDate();
            Query query = new Query();
            query.addCriteria(Criteria.where("event").is(event));
            query.addCriteria(Criteria.where("dateCreated").gte(startDate).lt(endDate));
            return mongoTemplate.findOne(query, DemographicSummary.class, "demographic_summary");
        } catch (ParseException ex) {
            Logger.getLogger(DemographicSummaryService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
