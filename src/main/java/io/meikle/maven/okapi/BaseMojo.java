package io.meikle.maven.okapi;

import net.sf.okapi.applications.rainbow.pipeline.PipelineWrapper;
import net.sf.okapi.common.ExecutionContext;
import net.sf.okapi.common.filters.DefaultFilters;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.plugins.PluginsManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

import java.io.File;
import java.io.IOException;
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

    /**
     * Plugins to be included in the BCONF.
     */
    @Parameter(property = "plugins")
    FileSet plugins;


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

    void configurePlugins(PipelineWrapper pipelineWrapper) throws MojoExecutionException {
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

    String[] getIncludedFiles(FileSet fileSet) {
        FileSetManager fileSetManager = new FileSetManager();
        if (fileSet.getDirectory() == null) {
            fileSet.setDirectory(mavenProject.getBasedir().getAbsolutePath());
        }
        if (!new File(fileSet.getDirectory()).isAbsolute()) {
            fileSet.setDirectory(Paths.get(mavenProject.getBasedir().getAbsolutePath(),
                    fileSet.getDirectory()).toString());
        }
        return fileSetManager.getIncludedFiles(fileSet);
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
