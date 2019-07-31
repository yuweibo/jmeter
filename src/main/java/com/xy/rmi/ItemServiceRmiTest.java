package com.xy.rmi;

import cloud.multi.tenant.TenantParam;
import com.rkhd.ienterprise.base.dbcustomize.model.Item;
import com.rkhd.ienterprise.base.dbcustomize.service.ItemService;
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
public class ItemServiceRmiTest extends AbstractJavaSamplerClient {

    static Log log = LogFactory.getLog(ItemServiceRmiTest.class);

    private String name;
    private ItemService itemService;

    @Override
    public Arguments getDefaultParameters() {
        super.getDefaultParameters();

        Arguments arguments = new Arguments();
        arguments.addArgument("name", "//localhost:9524/ItemRMIService");
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
            itemService = (ItemService) Naming.lookup(name);
        } catch (Exception e) {
            log.error("lookup TestServiceError", e);
        }
    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult result = new SampleResult();
        try {
            result.sampleStart();
            TenantParam tenantParam = new TenantParam(12135L, 11403L);
            Item rv = itemService.get(713236L, tenantParam);
            if (null != rv && null != rv.getId()) {
                result.setResponseData(String.valueOf(rv.getId()), "utf-8");
                result.setSuccessful(true);
            } else {
                result.setSuccessful(false);
            }
        } catch (Throwable e) {
            String errorMsg = null != e.getMessage() ? e.getMessage() : "error";
            result.setResponseData(errorMsg, "utf-8");
            result.setSuccessful(false);
            log.error(e);
        } finally {
            result.sampleEnd();
        }
        return result;
    }
}
