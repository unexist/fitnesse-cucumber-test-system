package org.fitnesse.cucumber;

/*import cucumber.runtime.*;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.Resource;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.model.CucumberFeature;*/
import fitnesse.testrunner.WikiTestPage;
import fitnesse.testsystems.CompositeTestSystemListener;
import fitnesse.testsystems.ExecutionLogListener;
import fitnesse.testsystems.ExecutionResult;
import fitnesse.testsystems.TestPage;
import fitnesse.testsystems.TestSummary;
import fitnesse.testsystems.TestSystem;
import fitnesse.testsystems.TestSystemListener;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.fs.FileSystemPage;
import io.cucumber.core.options.RuntimeOptions;
import io.cucumber.core.resource.Resource;
import util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>Cucumber test system.</p>
 */
public class CucumberTestSystem implements TestSystem {

    public static final String TEST_SYSTEM_NAME = "cucumber";
    private final String name;
    private final ExecutionLogListener executionLogListener;
    private final ClassLoader classLoader;
    private final CompositeTestSystemListener testSystemListener;

    private boolean started = false;
    private TestSummary testSummary;

    public CucumberTestSystem(String name, final ExecutionLogListener executionLogListener, ClassLoader classLoader) {
        super();
        this.name = name;
        this.executionLogListener = executionLogListener;
        this.classLoader = classLoader;
        this.testSystemListener = new CompositeTestSystemListener();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void start() {
        started = true;

        testSystemListener.testSystemStarted(this);
    }

    @Override
    public void bye() {
        kill();
    }

    @Override
    public void kill() {
        testSystemListener.testSystemStopped(this, null);

        if (classLoader instanceof Closeable) {
            FileUtil.close((Closeable) classLoader);
        }
    }

    @Override
    public void runTests(TestPage testPage) {
        final ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        String gluePath = testPage.getVariable("cucumber.glue");
        final TestSummary testSummary = new TestSummary();

        final FitNesseResultFormatter formatter = new FitNesseResultFormatter(testSummary,
                new Printer() {
                    public void write(final String text) {
                        testOutputChunk(text);
                    }
                }, new Printer() {
                    public void write(final String text) {
                        executionLogListener.stdErr(text);
                    }
                });

        testSystemListener.testStarted(testPage);

        try {
            Thread.currentThread().setContextClassLoader(classLoader);

            /*RuntimeOptions runtimeOptions = new RuntimeOptions(Arrays.asList("--glue", gluePath));

            final List<CucumberFeature> cucumberFeatures = new ArrayList<>();
            final List<Object> filters = new ArrayList<>();
            final FeatureBuilder builder = new FeatureBuilder(cucumberFeatures);

            builder.parse(new PageResource(testPage), filters);
            ResourceLoader resourceLoader = new MultiLoader(classLoader);
            ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
            Runtime runtime = new Runtime(resourceLoader, classFinder, classLoader, runtimeOptions);

            for (CucumberFeature cucumberFeature : cucumberFeatures) {
                cucumberFeature.run(formatter, formatter, runtime);
            }

            formatter.missing(runtime.getSnippets());*/
        } catch (Exception e) {
            testSummary.add(ExecutionResult.ERROR);
            testSystemListener.testOutputChunk("<span class='error'>Test execution failed: " +
                    (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()) + "</span>");
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
            testSystemListener.testComplete(testPage, testSummary);
        }
    }

    public String getPath(TestPage testPage) {
        WikiPage sourcePage = ((WikiTestPage) testPage).getSourcePage();
        if (sourcePage instanceof FileSystemPage) {
            return ((FileSystemPage) sourcePage).getFileSystemPath().getPath();
        } else if (sourcePage instanceof CucumberFeaturePage) {
            return ((CucumberFeaturePage) sourcePage).getFileSystemPath().getPath();
        }
        throw new RuntimeException("Can not parse file as Cucumber feature file: " + sourcePage);
    }

    @Override
    public boolean isSuccessfullyStarted() {
        return started;
    }

    @Override
    public void addTestSystemListener(TestSystemListener listener) {
        testSystemListener.addTestSystemListener(listener);
    }

    private void testOutputChunk(final String text) {
        testSystemListener.testOutputChunk(text);
    }

    private static class PageResource implements Resource {
        private final TestPage testPage;

        public PageResource(final TestPage testPage) {
            this.testPage = testPage;
        }

        public URI getUri() {
            return null;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(testPage.getContent().getBytes());
        }
    }
}
