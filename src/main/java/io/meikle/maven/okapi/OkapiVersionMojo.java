package io.meikle.maven.okapi;

import net.sf.okapi.applications.rainbow.CommandLine;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Gets the Okapi version used in the plugin.
 */
@Mojo(name = "version")
public class OkapiVersionMojo extends BaseMojo
{

    public void execute() throws MojoExecutionException
    {
        getLog().info(String.format("Okapi Version %s.",
                CommandLine.class.getPackage().getImplementationVersion()));
    }
}