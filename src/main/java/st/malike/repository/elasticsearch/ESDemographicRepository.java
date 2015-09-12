/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package st.malike.repository.elasticsearch;

import java.io.Serializable;
import java.util.Date;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import st.malike.model.elasticsearch.ESDemographic;

/**
 *
 * @author malike_st
 */
public interface ESDemographicRepository extends ElasticsearchCrudRepository<ESDemographic, Serializable>{

    public Iterable<ESDemographic> findByEventAndDateCreated(String event, Date dateCreated, PageRequest pageRequest);

}
