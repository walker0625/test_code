package com.testcode.testcode;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class FindSlowTestExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        String testClassName=extensionContext.getRequiredTestClass().getName();
        String testMethodName=extensionContext.getRequiredTestMethod().getName();
        extensionContext.getStore(ExtensionContext.Namespace.create());
    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {

    }
}
