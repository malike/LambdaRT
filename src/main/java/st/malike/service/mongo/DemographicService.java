/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.service.mongo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import st.malike.model.Demographic;
import st.malike.repository.DemographicRepository;

/**
 *
 * @author malike_st
 */
@Service(value = "demographicService")
public class DemographicService implements Serializable {

    @Autowired(required = true)
    private DemographicRepository demographicRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    public Demographic getDemographicById(Serializable id) {
        return demographicRepository.findOne(id);
    }

    public Demographic saveDemographic(Demographic demographic) {
        return demographicRepository.save(demographic);
    }

    public List<Demographic> saveDemographic(List<Demographic> demographic) {
        return demographicRepository.save(demographic);
    }

    public List getDemographic() {
        return demographicRepository.findAll();
    }

    public void deleteDemographic(Serializable id) {
        demographicRepository.delete(id);
    }

    public List getDemographicByEvent(String event) {
        Query query = new Query();
        query.addCriteria(Criteria.where("event").is(event));
        return mongoTemplate.find(query, Demographic.class, "demographic");
    }

    public List getDemographicByDate(Date dateCreated) {
        Query query = new Query();
        query.addCriteria(Criteria.where("dateCreated").is(dateCreated));
        return mongoTemplate.find(query, Demographic.class, "demographic");
    }

    public Demographic getDemographicByEventAndDate(String event, Date dateCreated) {
        Query query = new Query();
        query.addCriteria(Criteria.where("event").is(event));
        query.addCriteria(Criteria.where("dateCreated").is(dateCreated));
        return mongoTemplate.findOne(query, Demographic.class, "demographic");
    }
}
