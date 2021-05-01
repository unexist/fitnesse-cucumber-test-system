package org.fitnesse.cucumber.tests;

import fitnesse.testsystems.ClassPath;
import fitnesse.testsystems.Descriptor;
import fitnesse.testsystems.TestSystem;
import org.fitnesse.cucumber.CucumberTestSystemFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;

public class CucumberTestSystemFactoryTest {

    @Test
    public void factoryReturnsRunningTestSystemInstance() throws IOException {
        CucumberTestSystemFactory factory = new CucumberTestSystemFactory();
        Descriptor descriptor = Mockito.mock(Descriptor.class);
        Mockito.when(descriptor.getClassPath()).thenReturn(new ClassPath(Arrays.asList("classes"), ":"));

        TestSystem testSystem = factory.create(descriptor);

        Assert.assertThat(testSystem.isSuccessfullyStarted(), CoreMatchers.is(false));
    }
}
