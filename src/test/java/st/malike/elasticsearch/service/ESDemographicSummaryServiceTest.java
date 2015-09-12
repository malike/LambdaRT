/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.elasticsearch.service;

import st.malike.service.ESDemographicSummaryService;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.elasticsearch.index.engine.DocumentMissingException;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import st.malike.model.mongodb.DemographicSummary;
import st.malike.util.HourTimeCount;
import st.malike.util.MinTimeCount;
import uk.co.jemos.podam.annotations.PodamCollection;
import uk.co.jemos.podam.annotations.PodamIntValue;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 *
 * @author malike_st
 */
//@Ignore - 
public class ESDemographicSummaryServiceTest {

    static ESDemographicSummaryService summaryBufferService;
    static PodamFactory podamFactory;
    @SuppressWarnings("unchecked")
    @PodamCollection(nbrElements = 6)
    List<MinTimeCount> minTimeCount = podamFactory.manufacturePojo(ArrayList.class, MinTimeCount.class);
    @PodamCollection(nbrElements = 6)
    List<HourTimeCount> hourTimeCount = podamFactory.manufacturePojo(ArrayList.class, HourTimeCount.class);
    @PodamIntValue
    int overAllTotal = podamFactory.manufacturePojo(Integer.class);
    static String SHARDINDEX = "lambda_lns_test";
    static String TYPENAME = "demographic_summary";

    @BeforeClass
    public static void setupClass() {
        podamFactory = new PodamFactoryImpl();
        summaryBufferService = new ESDemographicSummaryService();
    }
 
    @Test
    @Ignore
    public void saveRecord() {

        DemographicSummary ds = new DemographicSummary();
        ds.setDateCreated(new Date());
        ds.setId("1");
        ds.setEvent("test");
        ds.setOverAllTotal(overAllTotal);
        ds.setHourTimeCount(hourTimeCount);
        Object dt = summaryBufferService.saveRecord(SHARDINDEX, TYPENAME, ds);

        System.out.println("\n\n T ==> " + new Gson().toJson(dt));

        assertNotEquals(null, dt);

    }

    @Test
    @Ignore
    public void updateRecord() {
        DemographicSummary ds = new DemographicSummary();
        List<HourTimeCount> forHour = podamFactory.manufacturePojo(ArrayList.class, HourTimeCount.class);

        ds.setId("1");
        ds.setOverAllTotal((overAllTotal + 2));
        ds.setHourTimeCount(forHour);

        summaryBufferService.updateRecord(SHARDINDEX, TYPENAME, ds);

        DemographicSummary dat = summaryBufferService.getRecord(SHARDINDEX, TYPENAME, ds.getId());
        System.out.println("\n\nItem ==> " + new Gson().toJson(ds));
        System.out.println("\n\nUpdated ==> " + new Gson().toJson(dat));
        assertNotNull(dat);
        assertEquals((overAllTotal + 2), dat.getOverAllTotal());
        assertEquals(forHour.size(), dat.getHourTimeCount().size());
        assertEquals(forHour.get(0).getHour(), dat.getHourTimeCount().get(0).getHour());
    }

    @Test(expected = DocumentMissingException.class)
    @Ignore
    public void updateRecordNotFoundException() {
        DemographicSummary ds = new DemographicSummary();
        ds.setId(UUID.randomUUID().toString());
        ds.setOverAllTotal((overAllTotal + 2));
        summaryBufferService.updateRecord(SHARDINDEX, TYPENAME, ds);
    }

    @Test
    @Ignore
    public void getCountRecord() {
        Long dat = summaryBufferService.getCountRecord(SHARDINDEX, TYPENAME);
        assertNotNull(dat);
        assertTrue(dat > 0);
    }

    @Test
    @Ignore
    public void deleteRecord() {
        String id = UUID.randomUUID().toString();
        DemographicSummary d = new DemographicSummary();
        d.setDateCreated(new Date());
        d.setId(id);
        d.setEvent("testdata");
        Object dat = summaryBufferService.saveRecord(SHARDINDEX, TYPENAME, d);
        assertNotEquals(null, dat);

        DemographicSummary getdat = (DemographicSummary) summaryBufferService.getRecord(SHARDINDEX, TYPENAME, id);
        assertNotEquals(null, getdat);

        summaryBufferService.deleteRecord(SHARDINDEX, TYPENAME, getdat.getId());
    }

    @Test
    @Ignore
    public void getRecord() {
        DemographicSummary ds = summaryBufferService.getRecord(SHARDINDEX, TYPENAME, "3");
        assertNotNull(ds);
        System.out.println("Data Read ==> " + new Gson().toJson(ds));
    }

    @Test(expected = DocumentMissingException.class)
    @Ignore
    public void getRecordNotFoundException() {
        String id = UUID.randomUUID().toString();
        summaryBufferService.getRecord(SHARDINDEX, TYPENAME, id);
    }

    @Test
    @Ignore
    public void getRecords() {
        List<DemographicSummary> list = summaryBufferService.getRecords(SHARDINDEX, TYPENAME, 0, 3);
        assertNotEquals(null, list);
        System.out.println(new Gson().toJson(list));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    @Ignore
    public void findByEventAndDateWithOutofIndexException() {//       
        DemographicSummary data = summaryBufferService.findByEventAndDate(SHARDINDEX, TYPENAME, "test", new Date());
    }

    @Test
    @Ignore
    public void findByEventAndDate() {
        DemographicSummary ds = podamFactory.manufacturePojo(DemographicSummary.class);
        ds.setDateCreated(new Date());
        Object dt = summaryBufferService.saveRecord(SHARDINDEX, TYPENAME, ds);

        assertNotEquals(null, dt);

        DemographicSummary data = summaryBufferService.findByEventAndDate(SHARDINDEX, TYPENAME, ds.getEvent(), new Date());
        assertNotNull(data);
//        assertTrue(data.getEvent().equals(ds.getEvent()));
        System.out.println("Find By ==> " + new Gson().toJson(data));
    }

    @Test
    @Ignore
    public void searchTextRecord() {
        String searchParam = "tes";
        List<DemographicSummary> data = summaryBufferService.searchTextRecord(SHARDINDEX, TYPENAME, searchParam, null, null);
        assertNotNull(data);
        assertTrue(data.size() > 0);
        assertTrue(data.get(0).getEvent().startsWith(searchParam));
        System.out.println("Search Results ==> " + new Gson().toJson(data));
    }
}
