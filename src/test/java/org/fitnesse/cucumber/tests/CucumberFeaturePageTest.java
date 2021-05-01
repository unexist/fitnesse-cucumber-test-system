package org.fitnesse.cucumber.tests;

import fitnesse.wiki.WikiPage;
import org.fitnesse.cucumber.CucumberFeaturePage;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class CucumberFeaturePageTest {

    @Test
    public void scenarioNamesShouldBeRenderedAsHeaders() {
        WikiPage storyPage = new CucumberFeaturePage(new File("features/simplefeature.feature"), "simplefeature", null);
        String html = storyPage.getHtml();
        Assert.assertThat(html, Matchers.containsString("<h4>Scenario: 2 squared</h4>"));
    }
}