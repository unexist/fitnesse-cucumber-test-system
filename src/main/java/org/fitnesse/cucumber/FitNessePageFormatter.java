package org.fitnesse.cucumber;

import java.util.List;

import fitnesse.util.StringUtils;
//import gherkin.formatter.Formatter;
//import gherkin.formatter.model.*;
import io.cucumber.core.gherkin.Feature;
import io.cucumber.gherkin.Parser;
import io.cucumber.gherkin.Token;
import io.cucumber.java.Scenario;
import io.cucumber.messages.Messages;
import io.cucumber.plugin.event.Node;

import static fitnesse.html.HtmlUtil.escapeHTML;
import static java.lang.String.format;

class FitNessePageFormatter implements Parser.Builder<Messages.GherkinDocument> {

    private final Printer outputPrinter;

    public FitNessePageFormatter(Printer outputPrinter) {
        this.outputPrinter = outputPrinter;
    }

    @Override
    public void uri(final String uri) {
    }

    @Override
    public void syntaxError(final String state, final String event,
                            final List<String> legalEvents, final String uri, final Integer line)
    {
        write("syntaxError " + escapeHTML(state) + " " + escapeHTML(event) + "<br/>");
    }

    @Override
    public void feature(final Feature feature) {
        write("h3", feature);
    }

    @Override
    public void scenarioOutline(final Node.ScenarioOutline scenarioOutline) {
        write("h4", scenarioOutline);
    }

    @Override
    public void scenario(final Messages.GherkinDocument.Feature.Scenario scenario) {
        write("h4", scenario);
    }

    @Override
    public void examples(final Messages.GherkinDocument.Feature.Scenario.Examples examples) {
        write("<h4>Examples</h4><table>");
        for (ExamplesTableRow row : examples.getRows()) {
            write("<tr>");
            for (String cell : row.getCells()) {
                write("<td>" + escapeHTML(cell) + "</td>");
            }
            write("</tr>");
        }
        write("</table>");
    }

    @Override
    public void startOfScenarioLifeCycle(final Scenario scenario) {

    }

    @Override
    public void step(final Step step) {
        write(format("%s%s<br/>", escapeHTML(step.getKeyword()), escapeHTML(step.getName())));
    }

    @Override
    public void endOfScenarioLifeCycle(final Scenario scenario) {
    }

    @Override
    public void done() {
    }

    @Override
    public void close() {
    }

    @Override
    public void eof() {
    }

    private void write(final String text) {
        outputPrinter.write(text);
    }

    private void write(String tag, DescribedStatement statement) {
        write("<" + tag + ">" + statement.getKeyword()+ ": " + escapeHTML(statement.getName()) + "</" + tag + ">");
        if (!StringUtils.isBlank(statement.getDescription())) {
            write("<p style='white-space: pre-line'>" + escapeHTML(statement.getDescription()) + "</p>");
        }
    }



    /* ------------- */

    @Override
    public void build(Token token) {

    }

    @Override
    public void startRule(Parser.RuleType ruleType) {

    }

    @Override
    public void endRule(Parser.RuleType ruleType) {

    }

    @Override
    public Messages.GherkinDocument getResult() {
        return null;
    }

    @Override
    public void reset() {

    }
}
