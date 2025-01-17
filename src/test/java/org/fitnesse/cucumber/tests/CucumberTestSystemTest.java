package org.fitnesse.cucumber.tests;

import fitnesse.testrunner.WikiTestPage;
import fitnesse.testsystems.Assertion;
import fitnesse.testsystems.ExceptionResult;
import fitnesse.testsystems.ExecutionLogListener;
import fitnesse.testsystems.TestSummary;
import fitnesse.testsystems.TestSystem;
import fitnesse.testsystems.TestSystemListener;
import org.fitnesse.cucumber.CucumberTestSystem;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import static org.hamcrest.Matchers.containsString;

public class CucumberTestSystemTest {

    @Test
    public void shouldHaveAName() {
        TestSystem testSystem = new CucumberTestSystem("name", null, getClassLoader());

        Assert.assertThat(testSystem.getName(), CoreMatchers.is("name"));
    }

    @Test
    public void canPerformAPassingTest() throws IOException, InterruptedException {
        TestSystemListener listener = testWithPage("FitNesseRoot/CucumberTestSystem/PassingCucumberTest/content.txt");
        String output = concatOutput(listener);
        Assert.assertThat(output, containsString("<span class='pass'>Given a variable x with value 2</span>"));
    }

    @Test
    public void canPerformAFailingTest() throws IOException, InterruptedException {
        TestSystemListener listener = testWithPage("FitNesseRoot/CucumberTestSystem/FailingCucumberTest/content.txt");
        String output = concatOutput(listener);
        Assert.assertThat(output, containsString("<span class='fail'>Then x should equal 10</span>"));
    }

    @Test
    public void canHandlePendingSteps() throws IOException, InterruptedException {
        TestSystemListener listener = testWithPage("FitNesseRoot/CucumberTestSystem/FeatureWithoutCandidateSteps/content.txt");
        String output = concatOutput(listener);
        Assert.assertThat(output, containsString("<span class='error'>Undefined step: Given a situation</span>"));
    }

    @Test
    public void canHandleBeforeStep() throws IOException, InterruptedException {
        TestSystemListener listener = testWithPage("features/substory/withBefore.feature");
        String output = concatOutput(listener);
        Assert.assertThat(output, containsString("<span class='pass'>Given a variable x with value 2</span>"));
    }

    @Test
    public void canHandleAfterStep() throws IOException, InterruptedException {
        TestSystemListener listener = testWithPage("features/substory/withAfter.feature");
        String output = concatOutput(listener);
        Assert.assertThat(output, containsString("<span class='pass'>Given a variable x with value 2</span>"));
    }

    @Test
    public void canHandleFailingBeforeStep() throws IOException, InterruptedException {
        TestSystemListener listener = testWithPage("features/substory/withFailingBefore.feature");
        String output = concatOutput(listener);
        Assert.assertThat(output, containsString("<span class='error'>Error before scenario: Something went wrong at runtime. See Execution Log for details.</span>"));
    }

    @Test
    public void canHandleFailingAfterStep() throws IOException, InterruptedException {
        TestSystemListener listener = testWithPage("features/substory/withFailingAfter.feature");
        String output = concatOutput(listener);
        Assert.assertThat(output, containsString("<span class='error'>Error after scenario: Something went wrong at runtime. See Execution Log for details.</span>"));
    }


    @Test
    public void canHandleScenarioOutLineWithExamples() throws IOException, InterruptedException {
        TestSystemListener listener = testWithPage("features/substory/scenarioOutline.feature");
        String output = concatOutput(listener);
        Assert.assertThat(output, containsString("<h4>Scenario Outline: squared numbers (last one fails)</h4>"));
        Assert.assertThat(output, containsString("<h5>Examples: value = 2, outcome = 4</h5>"));
    }

    // Perform test execution, assume no errors happen.
    private TestSystemListener testWithPage(final String path) throws IOException, InterruptedException {
        ExecutionLogListener executionLogListener = Mockito.mock(ExecutionLogListener.class);
        WikiTestPage pageToTest = getWikiTestPage(path);
        TestSystemListener listener = Mockito.mock(TestSystemListener.class);

        CucumberTestSystem testSystem = new CucumberTestSystem("", executionLogListener, getClassLoader());
        testSystem.addTestSystemListener(listener);

        testSystem.start();
        testSystem.runTests(pageToTest);
        testSystem.bye();

        Mockito.verify(listener).testSystemStarted(testSystem);
        Mockito.verify(listener).testSystemStopped(ArgumentMatchers.eq(testSystem), ArgumentMatchers.eq((Throwable) null));
        Mockito.verify(listener).testStarted(pageToTest);
        Mockito.verify(listener).testComplete(ArgumentMatchers.eq(pageToTest), ArgumentMatchers.any(TestSummary.class));
        Mockito.verify(listener, Mockito.never()).testExceptionOccurred(ArgumentMatchers.eq((Assertion) null), ArgumentMatchers.any(ExceptionResult.class));

        return listener;
    }


    protected ClassLoader getClassLoader() {
        return new URLClassLoader(new URL[] {}, Thread.currentThread().getContextClassLoader());
    }

    private WikiTestPage getWikiTestPage(String path) throws IOException {
        WikiTestPage pageToTest = Mockito.mock(WikiTestPage.class);
        Mockito.when(pageToTest.getContent()).thenReturn(FileUtil.getFileContent(new File(path)));
        Mockito.when(pageToTest.getVariable(ArgumentMatchers.eq("cucumber.glue"))).thenReturn("main.java.org.fitnesse.cucumber");
        return pageToTest;
    }

    private String concatOutput(final TestSystemListener listener) throws IOException {
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(listener, Mockito.atLeastOnce()).testOutputChunk(captor.capture());

        StringBuilder b = new StringBuilder();
        for (String s : captor.getAllValues()) {
            b.append(s);
        }
        return b.toString();
    }

}