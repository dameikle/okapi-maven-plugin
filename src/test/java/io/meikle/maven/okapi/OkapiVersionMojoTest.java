package io.meikle.maven.okapi;

import org.junit.Test;
import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OkapiVersionMojoTest extends MojoTestBase
{

    /**
     * Tests executing getting the version.
     * @throws Exception on any error executing the mojo
     */
    @Test
    public void testExecuteVersion() throws Exception
    {
        File pom = new File("target/test-classes/pipeline/");
        assertNotNull(pom);
        assertTrue(pom.exists());
        OkapiVersionMojo versionMojo = (OkapiVersionMojo) rule.lookupConfiguredMojo(pom,"version" );
        assertNotNull(versionMojo);
        versionMojo.execute();
    }

}

