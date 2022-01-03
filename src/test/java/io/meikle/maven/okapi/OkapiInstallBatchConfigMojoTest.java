package io.meikle.maven.okapi;

import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OkapiInstallBatchConfigMojoTest extends MojoTestBase {

    /**
     * Tests the installation of a BCONF file
     * @throws Exception on any error executing the mojo
     */
    @Test
    public void testInstallBCONF() throws Exception
    {
        File pom = new File( "target/test-classes/install-bconf/" );
        assertNotNull(pom);
        assertTrue(pom.exists());
        OkapiInstallBatchConfigMojo installBconfMojo
                = (OkapiInstallBatchConfigMojo) rule.lookupConfiguredMojo(pom, "install" );
        assertNotNull(installBconfMojo);
        installBconfMojo.execute();
        assertTrue(Files.exists(Paths.get(pom.getPath(), "output")));
        assertTrue(Files.exists(Paths.get(pom.getPath(), "output", "pipeline.pln")));
        assertTrue(Files.exists(Paths.get(pom.getPath(), "output", "extensions-mapping.txt")));
        assertTrue(Files.exists(Paths.get(pom.getPath(), "output", "okf_xliff@test.fprm")));
    }

    /**
     * Tests that <code>IllegalArgumentException</code> is thrown where outputDirectory isn't provided.
     * @throws Exception on any error executing the mojo
     */
    @Test
    public void testInstallBCONF_NoOutputDir() throws Exception
    {
        File pom = new File( "target/test-classes/install-bconf/" );
        assertNotNull(pom);
        assertTrue(pom.exists());
        OkapiInstallBatchConfigMojo installBconfMojo
                = (OkapiInstallBatchConfigMojo) rule.lookupConfiguredMojo(pom, "install" );
        assertNotNull(installBconfMojo);
        installBconfMojo.outputDirectory = null;
        boolean exceptionThrown = false;
        try {
            installBconfMojo.execute();
        } catch (IllegalArgumentException ex) {
            exceptionThrown = true;
            assertEquals("Output directory cannot be blank", ex.getMessage());
        }
        assertTrue(exceptionThrown);
    }


    /**
     * Tests that <code>IllegalArgumentException</code> is thrown where BCONF isn't provided.
     * @throws Exception on any error executing the mojo
     */
    @Test
    public void testInstallBCONF_NoBconf() throws Exception
    {
        File pom = new File( "target/test-classes/install-bconf/" );
        assertNotNull(pom);
        assertTrue(pom.exists());
        OkapiInstallBatchConfigMojo installBconfMojo
                = (OkapiInstallBatchConfigMojo) rule.lookupConfiguredMojo(pom, "install" );
        assertNotNull(installBconfMojo);
        installBconfMojo.bconf = null;
        boolean exceptionThrown = false;
        try {
            installBconfMojo.execute();
        } catch (IllegalArgumentException ex) {
            exceptionThrown = true;
            assertEquals("Batch Configuration file cannot be blank", ex.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    /**
     * Tests that <code>IllegalArgumentException</code> is thrown where BCONF doesn't exist.
     * @throws Exception on any error executing the mojo
     */
    @Test
    public void testInstallBCONF_BconfDoesntExist() throws Exception
    {
        File pom = new File( "target/test-classes/install-bconf/" );
        assertNotNull(pom);
        assertTrue(pom.exists());
        OkapiInstallBatchConfigMojo installBconfMojo
                = (OkapiInstallBatchConfigMojo) rule.lookupConfiguredMojo(pom, "install" );
        assertNotNull(installBconfMojo);
        installBconfMojo.bconf = "/tmp/123okapi456.bconf";
        boolean exceptionThrown = false;
        try {
            installBconfMojo.execute();
        } catch (IllegalArgumentException ex) {
            exceptionThrown = true;
            assertEquals("Batch Configuration file must exist", ex.getMessage());
        }
        assertTrue(exceptionThrown);
    }

}
