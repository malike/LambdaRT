/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.elasticsearch.service;

import com.google.gson.Gson;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.elasticsearch.index.engine.DocumentMissingException;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import st.malike.model.Demographic;

/**
 *
 * @author malike_st
 */
//@Ignore  -- for this lets actually save
public class DemographicElasticSearchServiceTest {

    static DemographicElasticSearchService demographicBufferService;
    static String SHARDINDEX = "lambda_lns_test";
    static String TYPENAME = "demographic";

    @BeforeClass
    public static void setupClass() {
        demographicBufferService = new DemographicElasticSearchService();
    }

    @Test
//    @Ignore
    public void saveRecord() {
        Demographic d = new Demographic();
        d.setDateCreated(new Date());
        d.setId("1");
        d.setEvent("test");
        Object dat = demographicBufferService.saveRecord(SHARDINDEX, TYPENAME, d);
        assertNotEquals(null, dat);
    }

    @Test
    @Ignore
    public void updateRecord() throws DocumentMissingException {
        Demographic d = new Demographic();
        d.setId("1");
        d.setEvent("updated");
        demographicBufferService.updateRecord(SHARDINDEX, TYPENAME, d);
        Demographic dat = (Demographic) demographicBufferService.getRecord(SHARDINDEX, TYPENAME, d.getId());
        System.out.println("Updated => " + new Gson().toJson(dat));
        assertNotEquals(null, dat);
        assertEquals("updated", dat.getEvent());
    }

    @Test(expected = DocumentMissingException.class)
    @Ignore
    public void updateRecordNotFoundExeption() throws DocumentMissingException {
        Demographic d = new Demographic();
        d.setDateCreated(new Date());
        d.setId(UUID.randomUUID().toString());
        d.setEvent("test");
        demographicBufferService.updateRecord(SHARDINDEX, TYPENAME, d);
    }

    @Test
    @Ignore
    public void getCountRecord() {
        Long dat = demographicBufferService.getCountRecord(SHARDINDEX, TYPENAME);
        assertNotNull(dat);
        assertTrue(dat > 0);
    }

    @Test
    @Ignore
    public void deleteRecord() throws ParseException, DocumentMissingException, Exception {
        String id = UUID.randomUUID().toString();
        Demographic d = new Demographic();
        d.setDateCreated(new Date());
        d.setId(id);
        d.setEvent("testdata");
        Object dat = demographicBufferService.saveRecord(SHARDINDEX, TYPENAME, d);
        assertNotEquals(null, dat);

        Demographic getdat = (Demographic) demographicBufferService.getRecord(SHARDINDEX, TYPENAME, id);
        assertNotEquals(null, getdat);

        demographicBufferService.deleteRecord(SHARDINDEX, TYPENAME, getdat.getId());
    }

    @Test(expected = DocumentMissingException.class)
    @Ignore
    public void deleteRecordNotFoundWithException() throws DocumentMissingException, Exception {
        String id = UUID.randomUUID().toString();
        demographicBufferService.deleteRecord(SHARDINDEX, TYPENAME, id);

    }

    @Test
    @Ignore
    public void getRecord() throws ParseException {
        Object dat = demographicBufferService.getRecord(SHARDINDEX, TYPENAME, "1");
        assertNotEquals(null, dat);
    }

    @Test(expected = DocumentMissingException.class)
    @Ignore
    public void getRecordNotFoundException() throws ParseException {
        demographicBufferService.getRecord(SHARDINDEX, TYPENAME, UUID.randomUUID().toString());
    }

    @Test
    @Ignore
    public void getRecords() throws ParseException {
        List<Demographic> data = demographicBufferService.getRecords(SHARDINDEX, TYPENAME, null, null);
        assertNotNull(data);
        assertTrue(data.size() > 0);
        System.out.println(new Gson().toJson(data));
    }

    @Test
    @Ignore
    public void searchTextRecord() {
        String searchParam = "te";
        List<Demographic> data = demographicBufferService.searchTextRecord(SHARDINDEX, TYPENAME, searchParam, 0, 3);
        assertNotNull(data);
        assertTrue(data.size() > 0);
        assertTrue(data.get(0).getEvent().startsWith(searchParam));
        System.out.println("Search Results ==> " + new Gson().toJson(data));
    }
}
