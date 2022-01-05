package io.meikle.maven.okapi;

import net.sf.okapi.applications.rainbow.pipeline.PipelineWrapper;
import net.sf.okapi.common.ExecutionContext;
import net.sf.okapi.common.filters.DefaultFilters;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.plugins.PluginsManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class BaseMojo extends AbstractMojo  {

    final String sourceEncoding = Charset.defaultCharset().name();
    final String targetEncoding = Charset.defaultCharset().name();
    FilterConfigurationMapper filterConfigurationMapper;

    @Parameter(readonly = true, defaultValue = "${project}")
    MavenProject mavenProject;

    /**
     * Base directory.
     */
    @Parameter(property = "baseDirectory", defaultValue = "." )
    File baseDirectory;

    /**
     * Source language.
     */
    @Parameter(property = "sourceLang", defaultValue = "" )
    String sourceLang;

    /**
     * Target language.
     */
    @Parameter(property = "targetLang", defaultValue = "" )
    String targetLang;

    /**
     * Input files.
     */
    @Parameter(property = "inputFiles")
    FileSet inputFiles;

    /**
     * Pipeline file.
     */
    @Parameter(property = "pipeline", defaultValue = "" )
    String pipeline;

    /**
     * Project file.
     */
    @Parameter(property = "project", defaultValue = "" )
    String project;

    /**
     * Plugins directory.
     */
    @Parameter(property = "pluginsDirectory", defaultValue = "" )
    String pluginsDirectory;

    /**
     * Filters directory.
     */
    @Parameter(property = "filtersDirectory", defaultValue = "" )
    String filtersDirectory;

    /**
     * Output directory.
     */
    @Parameter(property = "outputDirectory", defaultValue = "" )
    String outputDirectory;

    /**
     * BCONF file.
     */
    @Parameter(property = "bconf", defaultValue = "" )
    String bconf;


    PipelineWrapper getPipelineWrapper() {
        ExecutionContext context = new ExecutionContext();
        context.setApplicationName("Okapi Maven Plugin");
        context.setIsNoPrompt(true);
        return new PipelineWrapper(getFilterMapper(),
                baseDirectory.getPath(), getPluginsManager(), baseDirectory.getPath(),
                baseDirectory.getPath(), getOutputDirectory(), null, context);
    }

    void loadPipeline(PipelineWrapper wrapper) throws MojoExecutionException {
        Path pipelinePath = Paths.get(this.pipeline);
        if (!Files.exists(pipelinePath)) {
            pipelinePath = Paths.get(this.baseDirectory.getPath(), this.pipeline);
        }
        if (!Files.exists(pipelinePath)) {
            throw new MojoExecutionException(String.format("Cannot load pipeline from '%s'", this.pipeline));
        }
        wrapper.load(pipelinePath.toString());
    }

    PluginsManager getPluginsManager() {
        PluginsManager pluginsManager = new PluginsManager();
        if (!StringUtils.isEmpty(pluginsDirectory) && Files.exists(Paths.get(pluginsDirectory))) {
            pluginsManager.discover(new File(pluginsDirectory), true);
        } else {
            pluginsManager.discover(new File(baseDirectory.getPath()), true);
        }
        return pluginsManager;
    }

    FilterConfigurationMapper getFilterMapper() {
        if (filterConfigurationMapper == null) {
            filterConfigurationMapper = new FilterConfigurationMapper();
            DefaultFilters.setMappings(filterConfigurationMapper, false, true);
            if (this.filtersDirectory != null && !this.filtersDirectory.isEmpty()) {
                getLog().info(String.format("Loading custom filters from '%s' directory", filtersDirectory));
                filterConfigurationMapper.setCustomConfigurationsDirectory(filtersDirectory);
            } else {
                getLog().info(String.format("Loading custom filters from base directory '%s'", baseDirectory));
                filterConfigurationMapper.setCustomConfigurationsDirectory(baseDirectory.getPath());
            }
            filterConfigurationMapper.updateCustomConfigurations();
        }
        return filterConfigurationMapper;
    }

    String getOutputDirectory() {
        if (outputDirectory != null && !outputDirectory.isEmpty()) {
            return outputDirectory;
        } else {
            return mavenProject.getBuild().getDirectory();
        }
    }

    void validateLanguages(String sourceLanguage, String targetLanguage) {
        if (StringUtils.isEmpty(sourceLanguage)) {
            throw new IllegalArgumentException("Source language blank or invalid");
        }
        if (StringUtils.isEmpty(targetLanguage)) {
            throw new IllegalArgumentException("Target language blank or invalid");
        }
    }

}
