/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.elasticsearch.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.engine.DocumentMissingException;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.indices.IndexMissingException;
import org.springframework.stereotype.Service;
import st.malike.elasticsearch.ElasticSearchConnector;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.search.SearchHit;
import org.joda.time.DateTime;
import st.malike.model.Demographic;

/**
 *
 * @author malike_st
 */
@Service
public class DemographicElasticSearchService {

    private static final String DATEFORMAT = "yyyy-MM-dd";

    public Object saveRecord(String indexName, String typeName, Demographic data) {
        if (null == data) {
            return new NullPointerException();
        }
        Gson gson = new GsonBuilder()
                .setDateFormat(DATEFORMAT).create();
        String json = gson.toJson(data);
        return ElasticSearchConnector.instance().getClient().prepareIndex(indexName, typeName, data.getId()).setSource(json).execute().actionGet();
    }

    public Object updateRecord(String indexName, String typeName, Demographic data) throws DocumentMissingException {
        if (null == data) {
            return new NullPointerException();
        }
        Gson gson = new GsonBuilder()
                .setDateFormat(DATEFORMAT).create();
        String json = gson.toJson(data);
        UpdateResponse response = ElasticSearchConnector.instance().getClient().prepareUpdate(indexName, typeName, data.getId())
                .setDoc(json).execute().actionGet();
        return response;
    }

    public Long getCountRecord(String indexName, String typeName) {
        try {
            CountResponse countResponse = ElasticSearchConnector.instance().getClient()
                    .prepareCount(indexName)
                    .setQuery(termQuery("_type", typeName))
                    .execute().actionGet();
            return countResponse.getCount();
        } catch (IndexMissingException ex) {
            System.out.println("IndexMissingException: " + ex.toString());
        }
        return null;
    }

    public boolean deleteRecord(String indexName, String typeName, String id) throws DocumentMissingException, Exception {
        try {
            DeleteResponse deleteResponse = ElasticSearchConnector.instance().getClient().prepareDelete(indexName, typeName, id).execute().actionGet();
            if (deleteResponse.isFound()) {
                return true;
            } else {
                throw new DocumentMissingException(null, typeName, id);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public Object getRecord(String indexName, String typeName, String id) {
        try {
            GetResponse response = ElasticSearchConnector.instance().getClient().prepareGet(indexName, typeName, id).execute().actionGet();
            if (response.isExists()) {
                Gson gson = new GsonBuilder()
                        .setDateFormat(DATEFORMAT).create();
                Demographic d = gson.fromJson(response.getSource().toString(), Demographic.class);
                d.setId((String) response.getSource().get("_id"));  //for mongodb id's
                return d;
            } else {
                throw new DocumentMissingException(null, typeName, id);
            }
        } catch (DocumentMissingException e) {
            System.out.println("Error ==> " + e);
            throw e;
        }
    }

    public List getRecords(String indexName, String typeName, Integer offset, Integer limit) throws ParseException {
        QueryBuilder queryBuilder = matchAllQuery();
        SearchRequestBuilder searchRequestBuilder = ElasticSearchConnector.instance().getClient().prepareSearch(indexName);
        searchRequestBuilder.setTypes(typeName);
        searchRequestBuilder.setSearchType(SearchType.DEFAULT);
        searchRequestBuilder.setQuery(queryBuilder);
        if (offset != null && limit != null) {
            searchRequestBuilder.setFrom(offset).setSize(limit).setExplain(true);
        }
        SearchResponse response = searchRequestBuilder.execute().actionGet();
        List<Demographic> demographics = new LinkedList<>();
        if (null != response) {
            for (SearchHit hit : response.getHits()) {
                Gson gson = new GsonBuilder()
                        .setDateFormat(DATEFORMAT).create();
                Demographic d = gson.fromJson(hit.getSource().toString(), Demographic.class);
//                d.setId((String) hit.getSource().get("_id"));  //for mongodb id's
                demographics.add(d);
            }
        }
        return demographics;
    }

    public List<Demographic> findByEventAndDate(String indexName, String typeName, String event, Date dateCreated) {
        List<Demographic> demographics = new LinkedList<>();
        try {
            SearchRequestBuilder searchRequestBuilder = ElasticSearchConnector.instance().getClient().prepareSearch(indexName);
            String startDate = new SimpleDateFormat(DATEFORMAT).format(dateCreated);
            String endDate = new SimpleDateFormat(DATEFORMAT).format(new DateTime(dateCreated).plusDays(1).toDate());
            QueryBuilder queryBuilder = QueryBuilders.matchQuery("event", event);            
            RangeFilterBuilder filterBuilders = FilterBuilders
                    .rangeFilter("dateCreated").gte(startDate)
                    .lte(endDate).includeUpper(true).includeLower(true);
            searchRequestBuilder.setTypes(typeName);
            searchRequestBuilder.setSearchType(SearchType.QUERY_AND_FETCH);
            searchRequestBuilder.setQuery(queryBuilder);
            searchRequestBuilder.setPostFilter(filterBuilders);
            SearchResponse response = searchRequestBuilder.execute().actionGet();
            if (null != response.getHits()) {
                if (0 < response.getHits().totalHits()) {
                    for (SearchHit hit : response.getHits()) {
                        Gson gson = new GsonBuilder()
                                .setDateFormat(DATEFORMAT).create();
                        Demographic d = gson.fromJson(hit.getSource().toString(), Demographic.class);
                        d.setId((String) hit.getSource().get("_id"));  //for mongodb id's
                        demographics.add(d);
                    }
                }
            }

        } catch (IndexMissingException ex) {
            System.out.println("IndexMissingException: " + ex.toString());
        }
        return demographics;
    }

    public List searchTextRecord(String indexName, String typeName, String param, Integer offset, Integer limit) {
        List<Demographic> demographics = new LinkedList<>();
        try {
            QueryBuilder queryBuilder = QueryBuilders.queryString("*" + param + "*");
            SearchRequestBuilder searchRequestBuilder = ElasticSearchConnector.instance().getClient().prepareSearch(indexName);
            searchRequestBuilder.setTypes(typeName);
            searchRequestBuilder.setSearchType(SearchType.DEFAULT);
            searchRequestBuilder.setQuery(queryBuilder);
            if (offset != null && limit != null) {
                searchRequestBuilder.setFrom(offset).setSize(limit).setExplain(true);
            }
            SearchResponse response = searchRequestBuilder.execute().actionGet();
            if (null != response) {
                for (SearchHit hit : response.getHits()) {
                    Gson gson = new GsonBuilder()
                            .setDateFormat(DATEFORMAT).create();
                    Demographic d = gson.fromJson(hit.getSource().toString(), Demographic.class);
                    d.setId((String) hit.getSource().get("_id"));  //for mongodb id's
                    demographics.add(d);
                }
            }
        } catch (IndexMissingException ex) {
            System.out.println("IndexMissingException: " + ex.toString());
        }
        return demographics;
    }
}
