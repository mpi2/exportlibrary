/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mousephenotype.dcc.exportlibrary.exportworker;

import java.io.File;
import javax.persistence.EntityManager;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author duncan
 */
public class ExportWorkerTest {

    private static final Logger logger = LoggerFactory.getLogger(ExportWorkerTest.class);

    @Test
    @Ignore
    public void testEMCreation(){
        File f = new File("phenodcc_raw.properties");
        ExportWorker ew = new ExportWorker();
        EntityManager em = ew.getEntityManagerFromHibernate(f);
    }
}
