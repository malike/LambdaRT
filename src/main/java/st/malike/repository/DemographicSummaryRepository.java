/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.repository;

import java.io.Serializable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import st.malike.model.DemographicSummary;

/**
 *
 * @author malike_st
 */
@Repository
public interface DemographicSummaryRepository extends MongoRepository<DemographicSummary, Serializable> {
    
}
