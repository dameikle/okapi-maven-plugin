package io.meikle.maven.okapi;

import net.sf.okapi.applications.rainbow.pipeline.PipelineWrapper;
import net.sf.okapi.common.ExecutionContext;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.plugins.PluginsManager;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;

import java.io.File;

public abstract class MojoTestBase {

    @Rule
    public MojoRule rule = new MojoRule()
    {
        @Override
        protected void before() throws Throwable
        {
        }

        @Override
        protected void after()
        {
        }
    };

    PipelineWrapper getTestPipelineWrapper(File pom) {
        ExecutionContext context = new ExecutionContext();
        context.setApplicationName("Okapi Maven Plugin");
        context.setIsNoPrompt(true);
        return new PipelineWrapper(new FilterConfigurationMapper(),
                pom.getPath(), new PluginsManager(), pom.getPath(),
                pom.getPath(), pom.getPath(), null, context);
    }
}
