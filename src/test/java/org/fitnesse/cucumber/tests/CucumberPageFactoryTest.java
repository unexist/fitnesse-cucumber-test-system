package org.fitnesse.cucumber.tests;

import fitnesse.wiki.SymbolicPage;
import fitnesse.wiki.SystemVariableSource;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.fs.FileSystemPage;
import fitnesse.wiki.fs.FileSystemPageFactory;
import org.fitnesse.cucumber.CucumberPageFactory;
import org.fitnesse.cucumber.CucumberTocPage;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class CucumberPageFactoryTest {
    private FileSystemPageFactory factory;
    private FileSystemPage root;

    @Before
    public void setUp() {
        factory = new FileSystemPageFactory();
        factory.registerWikiPageFactory(new CucumberPageFactory());
        root = factory.makePage(new File("./FitNesseRoot"),
                "FitNesseRoot", null, new SystemVariableSource());
    }

    @Test
    public void shouldLoadTocPage() {
        WikiPage page = root.getChildPage("FeatureFiles");
        Assert.assertThat(page, CoreMatchers.not(CoreMatchers.nullValue()));
        Assert.assertThat(page.getName(), CoreMatchers.is("FeatureFiles"));
        Assert.assertThat(page.getData().getContent(), CoreMatchers.is("!contents"));
    }

    @Test
    public void tocPageShouldHaveChildren() {
        WikiPage page = root.getChildPage("FeatureFiles");
        List<WikiPage> children = page.getChildren();
        Assert.assertThat(children, CoreMatchers.not(CoreMatchers.nullValue()));
        Assert.assertThat(children.size(), CoreMatchers.is(1));
        Assert.assertThat(children.get(0).getName(), CoreMatchers.is("SimpleFeature"));
    }

    @Test
    public void tocPageCanRender() {
        WikiPage page = root.getChildPage("FeatureFiles");
        Assert.assertThat(page.getHtml(), CoreMatchers.not(CoreMatchers.nullValue()));
    }

    @Test
    public void canResolveVariablesDefinedInAParentPage() {
        WikiPage features = root.getChildPage("FeatureFiles");
        WikiPage simpleStory = features.getChildPage("SimpleFeature");
        Assert.assertThat(features.getVariable("TEST_VAR"), CoreMatchers.is("my value"));
        Assert.assertThat(simpleStory.getVariable("TEST_VAR"), CoreMatchers.is("my value"));
    }

    @Test
    public void testPageWillAlwaysResolveTestSystemToCucumber() {
        WikiPage features = root.getChildPage("FeatureFiles");
        WikiPage simpleStory = features.getChildPage("SimpleFeature");
        Assert.assertNull("TEST_SYSTEM should not be defined", features.getVariable("TEST_SYSTEM"));
        Assert.assertThat(simpleStory.getVariable("TEST_SYSTEM"), CoreMatchers.is("cucumber"));
    }

    @Test
    public void shouldLoadSymlinkedFeaturesFolder() {
        WikiPage symlinked = root.getChildPage("SymLinked");
        WikiPage page = symlinked.getChildPage("FeatureFiles");
        List<WikiPage> children = page.getChildren();

        Assert.assertThat(page, CoreMatchers.instanceOf(SymbolicPage.class));
        Assert.assertThat(((SymbolicPage) page).getRealPage(), CoreMatchers.instanceOf(CucumberTocPage.class));

        Assert.assertThat(children, CoreMatchers.not(CoreMatchers.nullValue()));
        Assert.assertThat(children.size(), CoreMatchers.is(2));
        Assert.assertThat(children.get(0).getName(), CoreMatchers.is("simplefeature"));
    }
}
