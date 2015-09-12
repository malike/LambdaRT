/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.service;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import st.malike.model.elasticsearch.ESDemographic;
import st.malike.repository.elasticsearch.ESDemographicRepository;

/**
 *
 * @author malike_st
 */
@Service
public class ESDemographicService {

    @Autowired
    private ESDemographicRepository esDemographicRepository;

    public ESDemographic saveCrunchedData(ESDemographic esDemographic) {
        return esDemographicRepository.save(esDemographic);
    }

    public Iterable<ESDemographic> saveCrunchedData(List<ESDemographic> esDemographic) {
        return esDemographicRepository.save(esDemographic);
    }

    public ESDemographic findCrunchedData(String id) {
        return esDemographicRepository.findOne(id);
    }

    public long count() {
        return esDemographicRepository.count();
    }

    public void deleteCrunchedData(ESDemographic esDemographic) {
        esDemographicRepository.delete(esDemographic);
    }

    public Iterable<ESDemographic> findAll() {
        return esDemographicRepository.findAll();
    }

    public Iterable<ESDemographic> findByEventAndDate(String event, Date dateCreated, Integer offset, Integer limit) {
        return esDemographicRepository.findByEventAndDateCreated(event, dateCreated, new PageRequest(offset, limit, Sort.Direction.ASC));
    }

}
