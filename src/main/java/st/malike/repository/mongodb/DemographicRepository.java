/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package st.malike.repository.mongodb;

import java.io.Serializable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import st.malike.model.mongodb.Demographic;

/**
 *
 * @author malike_st
 */
@Repository
public interface DemographicRepository extends MongoRepository<Demographic, Serializable>{

}
