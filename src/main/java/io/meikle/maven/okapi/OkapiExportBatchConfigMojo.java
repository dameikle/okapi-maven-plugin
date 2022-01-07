package io.meikle.maven.okapi;

import io.meikle.maven.okapi.config.FilterMapping;
import net.sf.okapi.applications.rainbow.Input;
import net.sf.okapi.applications.rainbow.Project;
import net.sf.okapi.applications.rainbow.batchconfig.BatchConfiguration;
import net.sf.okapi.applications.rainbow.lib.LanguageManager;
import net.sf.okapi.applications.rainbow.pipeline.PipelineWrapper;
import net.sf.okapi.common.filters.FilterConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.shared.model.fileset.FileSet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exports a Batch Configuration file to the specified location.
 */
@Mojo(name = "export")
public class OkapiExportBatchConfigMojo extends BaseMojo  {

    /**
     * Plugins to be included in the BCONF.
     */
    @Parameter(property = "plugins")
    FileSet plugins;

    /**
     * Filter mappings.
     */
    @Parameter(alias = "filterMappings")
    FilterMapping[] filterMappings;

    public void execute() throws MojoExecutionException {
        if (StringUtils.isEmpty(bconf)) {
            throw new IllegalArgumentException("Batch Configuration path cannot be blank");
        }
        if (StringUtils.isEmpty(project) && StringUtils.isEmpty(pipeline)) {
            throw new IllegalArgumentException("Must have project or pipeline set");
        }

        // Setup Pipeline Wrapper
        PipelineWrapper pipelineWrapper = getPipelineWrapper();
        Project project = new Project(new LanguageManager());
        if (plugins != null) {
            // Explicit plugins set, so attempt to merge with any from pluginsDirectory
            configurePlugins(pipelineWrapper);
        }

        // Load from Project File
        if (this.project != null && !this.project.isEmpty()) {
            try {
                project.load(this.project);
            } catch (Exception ex) {
                throw new MojoExecutionException(String.format("Cannot load project from %s", this.project), ex);
            }
        }

        // Load from Pipeline
        if (this.pipeline != null && !this.pipeline.isEmpty()) {
            loadPipeline(pipelineWrapper);
        }

        // Workout the path to use
        List<Input> inputFiles = buildFilterConfiguration();
        Path bconfPath = Paths.get(bconf);
        if (!bconfPath.isAbsolute()) {
            bconfPath = Paths.get(getOutputDirectory(), bconf);
        }

        // Make sure path exists, if not attempt to create it
        if (!Files.exists(bconfPath.getParent())) {
            try {
                Files.createDirectories(bconfPath.getParent());
            } catch (IOException ex) {
                getLog().error(String.format("Error creating path %s", bconfPath.getParent()), ex);
                throw new MojoExecutionException(String.format("Error creating path %s", bconfPath.getParent()), ex);
            }
        }

        BatchConfiguration config = new BatchConfiguration();
        getLog().info("Writing batch configuration to " + bconf);
        config.exportConfiguration(bconfPath.toString(), pipelineWrapper, filterConfigurationMapper, inputFiles);
        getLog().info("Export complete");
    }

    private List<Input> buildFilterConfiguration() throws MojoExecutionException {
        if (this.filterMappings == null) {
            return Collections.emptyList();
        }
        List<Input> inputFiles = new ArrayList<>();
        for (FilterMapping mapping : this.filterMappings) {
            FilterConfiguration config = filterConfigurationMapper.getConfiguration(mapping.getConfiguration());
            if (config == null) {
                getLog().info(String.format("Configuring custom configuration for config id '%s'",
                        mapping.getConfiguration()));
                filterConfigurationMapper.addCustomConfiguration(mapping.getConfiguration());
                // Check if it was loaded
                config = filterConfigurationMapper.getConfiguration(mapping.getConfiguration());
                if (config == null) {
                    getLog().error(String.format("Error loading custom configuration for config id '%s'",
                            mapping.getConfiguration()));
                    throw new MojoExecutionException(String.format("Error loading custom configuration for id: %s",
                            mapping.getConfiguration()));
                }
            }
            getLog().info(String.format("Adding filter mapping for config id '%s' for extension '%s'",
                    config.configId, mapping.getExtension()));
            Input input = new Input();
            input.filterConfigId = config.configId;
            input.relativePath = "fake" + mapping.getExtension();
            inputFiles.add(input);
        }
        return inputFiles;
    }

    private void configurePlugins(PipelineWrapper pipelineWrapper) throws MojoExecutionException {
        Path tmpPluginDir;
        try {
            tmpPluginDir = Files.createTempDirectory("okapi-maven");
        } catch (IOException ex) {
            throw new MojoExecutionException("Cannot create temporary plugin folder", ex);
        }
        try {
            if (pluginsDirectory != null && !pluginsDirectory.isEmpty()) {
                File pluginsDir = new File(pluginsDirectory);
                if (!Files.exists(pluginsDir.toPath())) {
                    throw new MojoExecutionException(String.format("Plugins directory %s doesn't exist",
                            pluginsDirectory));
                }
                for (File srcFile : pluginsDir.listFiles()) {
                    if (!srcFile.isDirectory()) {
                        FileUtils.copyFileToDirectory(srcFile, tmpPluginDir.toFile());
                    }
                }
            }
            String[] files = getIncludedFiles(plugins);
            for (String file : files) {
                String relFile = Paths.get(plugins.getDirectory(), file).toString();
                File tmpFile = new File(relFile);
                if (!Files.exists(tmpFile.toPath())) {
                    getLog().info(String.format("Skipping file %s as it does not exist", file));
                    continue;
                }
                FileUtils.copyFileToDirectory(tmpFile, tmpPluginDir.toFile());
            }
        }
        catch(IOException ex) {
            throw new MojoExecutionException("Cannot copy plugin to plugin folder", ex);
        }
        pipelineWrapper.getPluginsManager().discover(tmpPluginDir.toFile(), true);
    }

}
