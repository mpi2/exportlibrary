/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mousephenotype.dcc.exportlibrary.exportworker;

import java.io.File;
import java.util.Date;
import javax.persistence.EntityManager;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.exportcommunication.CentreContainer;
import org.mousephenotype.dcc.exportlibrary.exportcommunication.DataReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author duncan
 */
public class ExperimentWritingTest {

    private static final Logger logger = LoggerFactory.getLogger(ExperimentWritingTest.class);
    private ExportWorker ew;
    private CentreContainer cc;
    private EntityManager em;

    @Before
    public void setup() {
        File f = new File("src/main/resources/phenodcc_raw.properties");
        ew = new ExportWorker();
        em = ew.getEntityManagerFromHibernate(f);
        cc = new CentreContainer(CentreILARcode.WTSI, "MGP_001", new Date());
        cc.getDataReference().add(new DataReference(14091025l, DataReference.ObjectType.EXPERIMENT));
        cc.getDataReference().add(new DataReference(14091051l, DataReference.ObjectType.EXPERIMENT));
        cc.getDataReference().add(new DataReference(14091077l, DataReference.ObjectType.EXPERIMENT));
        cc.getDataReference().add(new DataReference(15311957l, DataReference.ObjectType.EXPERIMENT));
        cc.getDataReference().add(new DataReference(15442377l, DataReference.ObjectType.EXPERIMENT));
    }

    @Test
    @Ignore // This is disabled since there is typically no configutation in git
    public void testExperimentWriting() {
        ew._testExperimentFileWriting(em, cc);
    }
}
