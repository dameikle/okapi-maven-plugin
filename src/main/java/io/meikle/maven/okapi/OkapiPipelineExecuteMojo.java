package io.meikle.maven.okapi;

import net.sf.okapi.applications.rainbow.Project;
import net.sf.okapi.applications.rainbow.lib.LanguageManager;
import net.sf.okapi.applications.rainbow.pipeline.PipelineWrapper;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filters.FilterConfiguration;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Executes an Okapi Pipeline.
 */
@Mojo(name = "pipeline")
public class OkapiPipelineExecuteMojo extends BaseMojo  {

    private static final int BAD_ROOT = 1;

    public void execute() throws MojoExecutionException {
        PipelineWrapper wrapper = getPipelineWrapper();
        if (StringUtils.isEmpty(pipeline)) {
            throw new IllegalArgumentException("Pipeline cannot be blank");
        }
        validateLanguages(sourceLang, targetLang);

        loadPipeline(wrapper);
        getLog().info(String.format("Loading pipeline %s", pipeline));
        Project project = createProject();

        String inputDir = inputFiles;
        if (!Paths.get(inputDir).isAbsolute()) {
            inputDir = Paths.get(baseDirectory.getPath(), inputFiles).toString();
        }
        File[] files = new File(inputDir).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    addDocument(project, file, sourceEncoding, targetEncoding);
                }
            }
        }
        List<String> steps = wrapper.getSteps().stream()
                .flatMap(s -> Stream.of(s.name))
                .collect(Collectors.toList());
        getLog().info(String.format("Executing pipeline with steps %s", steps));
        wrapper.execute(project);
        getLog().info("Pipeline complete");
    }

    private Project createProject() {
        Project project = new Project(new LanguageManager());
        String projectPath = baseDirectory.getPath();
        project.setSourceLanguage(LocaleId.fromString(sourceLang));
        project.setTargetLanguage(LocaleId.fromString(targetLang));
        project.setInputRoot(0, projectPath, true);
        project.setOutputRoot(getOutputDirectory());
        project.setUseOutputRoot(true);
        return project;
    }

    private void addDocument(Project project, File document, String sourceEncoding, String targetEncoding)
            throws MojoExecutionException {
        String extension = FilenameUtils.getExtension(document.getName());
        FilterConfiguration config = getFilterMapper().getDefaultConfigurationFromExtension(extension);
        String filterConfigId = config.configId;
        if (filterConfigId == null) {
            throw new MojoExecutionException(
                    String.format("Filter configuration for extension %s not found", extension));
        }
        int result = project.addDocument(0, document.getAbsolutePath(), sourceEncoding, targetEncoding,
                filterConfigId, false);
        if (result == BAD_ROOT) {
            throw new MojoExecutionException(String.format("Cannot add file %s to project", document.getName()));
        }
        getLog().info(String.format("Added input file %s", document.getName()));
    }

}
