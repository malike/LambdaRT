/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package st.malike.repository.elasticsearch;

import java.io.Serializable;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import st.malike.model.elasticsearch.ESDemographicSummary;

/**
 *
 * @author malike_st
 */
public interface ESDemographicSummaryRepository extends ElasticsearchCrudRepository<ESDemographicSummary, Serializable>{

}
