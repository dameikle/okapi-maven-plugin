package io.meikle.maven.okapi;

import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OkapiPipelineExecuteMojoTest extends MojoTestBase {

    /**
     * Tests executing a pipeline using the <code>OkapiPipelineExecuteMojo</code>.
     * @throws Exception on any error executing the mojo
     */
    @Test
    public void textExecutePipeline() throws Exception
    {
        File pom = new File("target/test-classes/pipeline/");
        assertNotNull(pom);
        assertTrue(pom.exists());
        OkapiPipelineExecuteMojo pipelineMojo =
                (OkapiPipelineExecuteMojo) rule.lookupConfiguredMojo(pom, "pipeline" );
        assertNotNull(pipelineMojo);
        pipelineMojo.execute();
        assertTrue(Files.exists(Paths.get(pom.getPath(), "pack1", "work", "input", "test.txt.xlf")));
    }

    /**
     * Tests that <code>IllegalArgumentException</code> where source language is null.
     * @throws Exception on any error executing the mojo
     */
    @Test
    public void textExecutePipeline_SourceLangNull() throws Exception
    {
        File pom = new File("target/test-classes/pipeline/");
        assertNotNull(pom);
        assertTrue(pom.exists());
        OkapiPipelineExecuteMojo pipelineMojo =
                (OkapiPipelineExecuteMojo) rule.lookupConfiguredMojo(pom, "pipeline" );
        assertNotNull(pipelineMojo);
        pipelineMojo.sourceLang = null;

        boolean exceptionThrown = false;
        try {
            pipelineMojo.execute();
        } catch (IllegalArgumentException ex) {
            exceptionThrown = true;
            assertEquals("Source language blank or invalid", ex.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    /**
     * Tests that <code>IllegalArgumentException</code> where source language is blank.
     * @throws Exception on any error executing the mojo
     */
    @Test
    public void textExecutePipeline_SourceLangBlank() throws Exception
    {
        File pom = new File("target/test-classes/pipeline/");
        assertNotNull(pom);
        assertTrue(pom.exists());
        OkapiPipelineExecuteMojo pipelineMojo =
                (OkapiPipelineExecuteMojo) rule.lookupConfiguredMojo(pom, "pipeline" );
        assertNotNull(pipelineMojo);
        pipelineMojo.sourceLang = "";

        boolean exceptionThrown = false;
        try {
            pipelineMojo.execute();
        } catch (IllegalArgumentException ex) {
            exceptionThrown = true;
            assertEquals("Source language blank or invalid", ex.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    /**
     * Tests that <code>IllegalArgumentException</code> where target language is null.
     * @throws Exception on any error executing the mojo
     */
    @Test
    public void textExecutePipeline_TargetLangNull() throws Exception
    {
        File pom = new File("target/test-classes/pipeline/");
        assertNotNull(pom);
        assertTrue(pom.exists());
        OkapiPipelineExecuteMojo pipelineMojo =
                (OkapiPipelineExecuteMojo) rule.lookupConfiguredMojo(pom, "pipeline" );
        assertNotNull(pipelineMojo);
        pipelineMojo.targetLang = null;

        boolean exceptionThrown = false;
        try {
            pipelineMojo.execute();
        } catch (IllegalArgumentException ex) {
            exceptionThrown = true;
            assertEquals("Target language blank or invalid", ex.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    /**
     * Tests that <code>IllegalArgumentException</code> where target language is blank.
     * @throws Exception on any error executing the mojo
     */
    @Test
    public void textExecutePipeline_TargetLangBlank() throws Exception
    {
        File pom = new File("target/test-classes/pipeline/");
        assertNotNull(pom);
        assertTrue(pom.exists());
        OkapiPipelineExecuteMojo pipelineMojo =
                (OkapiPipelineExecuteMojo) rule.lookupConfiguredMojo(pom, "pipeline" );
        assertNotNull(pipelineMojo);
        pipelineMojo.targetLang = "";

        boolean exceptionThrown = false;
        try {
            pipelineMojo.execute();
        } catch (IllegalArgumentException ex) {
            exceptionThrown = true;
            assertEquals("Target language blank or invalid", ex.getMessage());
        }
        assertTrue(exceptionThrown);
    }

}
