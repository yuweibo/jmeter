package com.xy.rmi;

import com.demo.restapi.controller.v2.test.TestService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.rmi.Naming;

/**
 * RMI 方法测试用例脚本 每个线程创建一个对象的实例，然后顺序执行 setupTest(),runTest() Created
 * by yuwb on 2017/9/6.
 */
public class RmiTest extends AbstractJavaSamplerClient {

    static Log log = LogFactory.getLog(RmiTest.class);

    private String name;
    private TestService testService;

    @Override
    public Arguments getDefaultParameters() {
        super.getDefaultParameters();

        Arguments arguments = new Arguments();
        arguments.addArgument("name", "//localhost:9137/TestRMIService");
        return arguments;
    }

    /**
     * 每个线程初始化一遍
     */
    @Override
    public void setupTest(JavaSamplerContext context) {
        super.setupTest(context);
        name = context.getParameter("name");
        try {
            testService = (TestService) Naming.lookup(name);
        } catch (Exception e) {
            log.error("lookup TestServiceError", e);
        }
    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult result = new SampleResult();
        try {
            result.sampleStart();
            String rv = testService.remote();
            result.setResponseData(rv, "utf-8");
            if (StringUtils.isNotBlank(rv)) {
                result.setSuccessful(true);
            } else {
                result.setSuccessful(false);
            }
        } catch (Exception e) {
            result.setSuccessful(false);
            log.error(e);
        } finally {
            result.sampleEnd();
        }
        return result;
    }
}
