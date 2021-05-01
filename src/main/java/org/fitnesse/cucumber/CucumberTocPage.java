package org.fitnesse.cucumber;

import fitnesse.wiki.BaseWikitextPage;
import fitnesse.wiki.PageData;
import fitnesse.wiki.PageType;
import fitnesse.wiki.VersionInfo;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.fs.WikiPageProperties;
import fitnesse.wikitext.VariableSource;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class CucumberTocPage extends BaseWikitextPage {
    private static final String CONTENTS = "!contents";

    private final File path;

    public CucumberTocPage(File path, String name, WikiPage parent, VariableSource variableSource) {
        super(name, parent, variableSource);
        this.path = path;
    }

    public WikiPage addChildPage(String name) {
        return null;
    }

    @Override
    public boolean hasChildPage(String name) {
        return getChildPage(name) != null;
    }

    @Override
    public WikiPage getChildPage(String name) {
        for (WikiPage child : getChildren()) {
            if (name.equals(child.getName())) {
                return child;
            }
        }
        return null;
    }

    @Override
    public void removeChildPage(String name) {
    }

    @Override
    public List<WikiPage> getChildren() {
        List<WikiPage> children = new ArrayList<WikiPage>();
        for (String child : Objects.requireNonNull(path.list())) {
            File childPath = new File(path, child);
            if (CucumberPageFactory.isFeatureFile(childPath)) {
                children.add(new CucumberFeaturePage(childPath,
                        child.split("\\.", 2)[0], this));
            } else if (childPath.isDirectory()) {
                children.add(new CucumberTocPage(childPath,
                        childPath.getName(), this, getVariableSource()));
            }
        }
        return children;
    }

    @Override
    public PageData getData() {
        return new PageData(CONTENTS, wikiPageProperties());
    }

    private WikiPageProperties wikiPageProperties() {
        WikiPageProperties properties = new WikiPageProperties();
        properties.set(PageType.SUITE.toString());
        return properties;
    }

    @Override
    public Collection<VersionInfo> getVersions() {
        return null;
    }

    @Override
    public WikiPage getVersion(String versionName) {
        return null;
    }

    @Override
    public VersionInfo commit(PageData data) {
        return null;
    }
}
