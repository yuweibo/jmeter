package com.xy.rmi;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * git clone 用例脚本 每个线程创建一个对象的实例，然后顺序执行 setupTest(),runTest() Created
 * by tangqi on 2019/04/19.
 */
public class GitCloneTest extends AbstractJavaSamplerClient {

    static Log log = LogFactory.getLog(GitCloneTest.class);

    private String gitURI;
    //测试根目录，每个线程会再创建一级子目录
    private String gitBaseDir;
    private Integer index;


    @Override
    public Arguments getDefaultParameters() {
        super.getDefaultParameters();

        Arguments arguments = new Arguments();
        arguments.addArgument("gitURI", "git@github.com:yuweibo/jmeter.git");
        arguments.addArgument("gitBaseDir", "D:\\git_clone_test");
        arguments.addArgument("index", "0");
        return arguments;
    }

    /**
     * 每个线程初始化一遍
     */
    @Override
    public void setupTest(JavaSamplerContext context) {
        super.setupTest(context);
        gitURI = context.getParameter("gitURI");
        gitBaseDir = context.getParameter("gitBaseDir");
        File baseDir = new File(gitBaseDir);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        index = context.getIntParameter("index");
    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult result = new SampleResult();
        Git git=null;
        try {
            //prepare subDir
            String subDirStr = gitBaseDir + File.separator + index;
            File subDir = new File(subDirStr);
            if (subDir.exists()) {
                FileUtils.deleteDirectory(subDir);
            }
            //test logic start
            result.sampleStart();
            git = Git.cloneRepository()
                    .setURI(gitURI)
                    .setDirectory(subDir)
                    .call();
            result.setResponseData(git.getRepository().getDirectory().getPath() + ":" + new Date(), "utf-8");
            result.setSuccessful(true);
        } catch (Exception e) {
            result.setSuccessful(false);
            log.error(e);
        } finally {
            if (null != git){
                git.close();
            }
            result.sampleEnd();
        }
        return result;
    }

}
