/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.service.mongo;

import st.malike.service.DemographicSummaryService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import st.malike.model.mongodb.DemographicSummary;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 *
 * @author malike_st
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/test-mongo-config.xml"})
@Ignore
public class DemographicSummaryServiceTest {

    @Autowired
    @Qualifier(value = "demographicSummaryService")
    DemographicSummaryService demographicSummaryService;
    static PodamFactory podamFactory;
    private static final String DATEFORMAT = "MMM dd, yyyy HH:mm:ss";

    @BeforeClass
    public static void setUp() {
        podamFactory = new PodamFactoryImpl();
    }

    @Test
    // @Ignore
    public void getDemographicSummaryById() {
        DemographicSummary d = podamFactory.manufacturePojo(DemographicSummary.class);
        demographicSummaryService.saveDemographicSummary(d);
        DemographicSummary de = demographicSummaryService.getDemographicSummaryById(d.getId());
        assertEquals(d.getEvent(), de.getEvent());
    }

    @Test
    // @Ignore
    public void saveDemographicSummary() {
        DemographicSummary d = podamFactory.manufacturePojo(DemographicSummary.class);
        DemographicSummary dSaved = demographicSummaryService.saveDemographicSummary(d);
        assertEquals(d.getEvent(), dSaved.getEvent());
        assertEquals(d.getId(), dSaved.getId());
    }

    @Test
    // @Ignore
    public void getDemographicSummaryNotFoundReturnNull() {
        assertNull(demographicSummaryService.getDemographicSummaryById(1L));
    }

    @Test
     @Ignore
    public void deleteDemographicSummary() {
        DemographicSummary d = podamFactory.manufacturePojo(DemographicSummary.class);
        DemographicSummary dSaved = demographicSummaryService.saveDemographicSummary(d);
        assertNotNull(dSaved);

        demographicSummaryService.deleteDemographicSummary(d.getId());

        assertNull(demographicSummaryService.getDemographicSummaryById(d.getId()));
    }

    @Test
    // @Ignore
    public void getDemographicSummaryByEvent() {

        DemographicSummary d = podamFactory.manufacturePojo(DemographicSummary.class);
        DemographicSummary dSaved = demographicSummaryService.saveDemographicSummary(d);
        assertNotNull(dSaved);

        List<DemographicSummary> ds = demographicSummaryService.getDemographicSummaryByEvent(d.getEvent());
        assertEquals(ds.get(0).getId(), d.getId());
        assertEquals(ds.get(0).getEvent(), d.getEvent());
        assertEquals(ds.get(0).getDateCreated(), d.getDateCreated());
        assertEquals(ds.get(0).getOverAllTotal(), d.getOverAllTotal());

    }

    @Test
    // @Ignore
    public void getDemographicSummaryByDate() {

        DemographicSummary d = podamFactory.manufacturePojo(DemographicSummary.class);
        DemographicSummary dSaved = demographicSummaryService.saveDemographicSummary(d);
        assertNotNull(dSaved);

        List<DemographicSummary> ds = demographicSummaryService.getDemographicSummaryByEvent(d.getEvent());
        assertEquals(ds.get(0).getId(), d.getId());
        assertEquals(ds.get(0).getEvent(), d.getEvent());
        assertEquals(ds.get(0).getDateCreated(), d.getDateCreated());
        assertEquals(ds.get(0).getOverAllTotal(), d.getOverAllTotal());
    }

    @Test
    // @Ignore
    public void getDemographicSummaryByEventAndDate() {
        DemographicSummary d = podamFactory.manufacturePojo(DemographicSummary.class);
        DemographicSummary dSaved = demographicSummaryService.saveDemographicSummary(d);
        assertNotNull(dSaved);

        DemographicSummary ds = demographicSummaryService.getDemographicSummaryByEventAndDate(d.getEvent(), dSaved.getDateCreated());
        assertEquals(ds.getOverAllTotal(), d.getOverAllTotal());
        assertEquals(ds.getId(), d.getId());
        assertEquals(ds.getEvent(), d.getEvent());
        assertEquals(ds.getDateCreated(), d.getDateCreated());

    }

    @Test
    public void getDemographicSummaryLastInsertDate() { 
        Date date = demographicSummaryService.getLastInsertDate();
        if (date != null) {
            System.out.println("Last insert Date ==> " + new SimpleDateFormat(DATEFORMAT, Locale.getDefault()).format(date));
        } else {
            System.out.println("Null");
        }
    }
}
