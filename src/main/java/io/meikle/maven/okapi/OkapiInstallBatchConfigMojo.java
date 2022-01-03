package io.meikle.maven.okapi;

import net.sf.okapi.applications.rainbow.batchconfig.BatchConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;

/**
 * Installs a Batch Configuration file to the specified location.
 */
@Mojo(name = "install")
public class OkapiInstallBatchConfigMojo extends BaseMojo  {

    public void execute() throws MojoExecutionException {
        if (StringUtils.isEmpty(bconf)) {
            throw new IllegalArgumentException("Batch Configuration file cannot be blank");
        }
        if (StringUtils.isEmpty(outputDirectory)) {
            throw new IllegalArgumentException("Output directory cannot be blank");
        }
        File batchConfigFile = new File(bconf);
        if (!batchConfigFile.exists()) {
            throw new IllegalArgumentException("Batch Configuration file must exist");
        }
        getLog().info(String.format("Installing Batch Configuration %s", bconf));
        new BatchConfiguration().installConfiguration(bconf, outputDirectory, getPipelineWrapper());
        getLog().info("Install complete");
    }

}
