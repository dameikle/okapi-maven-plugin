package io.meikle.maven.okapi;

import net.sf.okapi.applications.rainbow.batchconfig.BatchConfiguration;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OkapiExportBatchConfigMojoTest extends MojoTestBase
{
    /**
     * Tests exporting a <i>BCONF</i>.
     * @throws Exception on any error executing the mojo
     */
    @Test
    public void testExportBCONF() throws Exception
    {
        File pom = new File( "target/test-classes/export-bconf/" );
        assertNotNull(pom);
        assertTrue(pom.exists());

        OkapiExportBatchConfigMojo exportBconfMojo
                = (OkapiExportBatchConfigMojo) rule.lookupConfiguredMojo(pom, "export" );
        assertNotNull(exportBconfMojo);
        exportBconfMojo.execute();

        assertTrue(Files.exists(Paths.get(pom.getPath(), "export.bconf")));
        new BatchConfiguration().installConfiguration(Paths.get(pom.getPath(), "export.bconf").toString(),
                Paths.get(pom.getPath(), "output").toString(), getTestPipelineWrapper(pom));
        assertTrue(Files.exists(Paths.get(pom.getPath(), "output")));
        assertTrue(Files.exists(Paths.get(pom.getPath(), "output", "pipeline.pln")));
        assertTrue(Files.exists(Paths.get(pom.getPath(), "output", "extensions-mapping.txt")));
        assertTrue(Files.exists(Paths.get(pom.getPath(), "output", "okf_html@test.fprm")));
    }

    /**
     * Tests that <code>IllegalArgumentException</code> is thrown where BCONF isn't provided.
     * @throws Exception on any error executing the mojo
     */
    @Test
    public void testExportBCONF_BlankBCONF() throws Exception
    {
        File pom = new File( "target/test-classes/export-bconf/" );
        assertNotNull(pom);
        assertTrue(pom.exists());

        OkapiExportBatchConfigMojo exportBconfMojo
                = (OkapiExportBatchConfigMojo) rule.lookupConfiguredMojo(pom, "export" );
        assertNotNull(exportBconfMojo);
        exportBconfMojo.bconf = null;

        boolean exceptionThrown = false;
        try {
            exportBconfMojo.execute();
        } catch (IllegalArgumentException ex) {
            exceptionThrown = true;
            assertEquals("Batch Configuration path cannot be blank", ex.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    /**
     * Tests that <code>IllegalArgumentException</code> is thrown when neither a pipeline
     * or project is provided.
     * @throws Exception on any error executing the mojo
     */
    @Test
    public void testExportBCONF_BlankPipeline() throws Exception
    {
        File pom = new File( "target/test-classes/export-bconf/" );
        assertNotNull(pom);
        assertTrue(pom.exists());

        OkapiExportBatchConfigMojo exportBconfMojo
                = (OkapiExportBatchConfigMojo) rule.lookupConfiguredMojo(pom, "export" );
        assertNotNull(exportBconfMojo);
        exportBconfMojo.pipeline = null;
        exportBconfMojo.project = null;

        boolean exceptionThrown = false;
        try {
            exportBconfMojo.execute();
        } catch (IllegalArgumentException ex) {
            exceptionThrown = true;
            assertEquals("Must have project or pipeline set", ex.getMessage());
        }
        assertTrue(exceptionThrown);
    }


}

