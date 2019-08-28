package com.xy.git;

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

/**
 * git push 用例脚本
 * 每个线程创建一个对象的实例，然后顺序执行 setupTest(),runTest() Created
 * by tangqi on 2019/04/19.
 */
public class GitPushTest extends AbstractJavaSamplerClient {

    static Log log = LogFactory.getLog(GitPushTest.class);

    private String gitURI;
    //测试根目录，每个线程会再创建一级子目录
    private String gitBaseDir;
    private Integer index;
    private String path;

    @Override
    public Arguments getDefaultParameters() {
        super.getDefaultParameters();

        Arguments arguments = new Arguments();
        arguments.addArgument("gitURI", "git@github.com:yuweibo/jmeter.git");
        arguments.addArgument("gitBaseDir", "D:\\git_clone_test");
        arguments.addArgument("index", "0");
        arguments.addArgument("path", "D:\\git_clone_test\\testFile.txt");

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
        path = context.getParameter("path");
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
            File testFile = new File(path);
            //validate testFile
            if (!testFile.exists() || testFile.isDirectory()){
                result.setSuccessful(false);
            }else {
//                git = Git.open(subDir);
                git = Git.cloneRepository()
                        .setURI(gitURI)
                        .setDirectory(subDir)
                        .call();
                //添加文件
                FileUtils.copyFileToDirectory(testFile,subDir);
                git.add().addFilepattern(".").call();
                git.commit().setMessage("testFile").call();
                result.sampleStart();
                git.push().call();
                //同步一下
                git.fetch().call();
                //删除文件
                FileUtils.forceDelete(new File(subDir.getPath() + File.separator+ testFile.getName()));
                git.add().addFilepattern(".").call();
                git.commit().setMessage("deleteTestFile").call();
                git.push().call();
                result.setResponseData(git.getRepository().getDirectory().getPath() + ":" + new Date(), "utf-8");
                result.setSuccessful(true);
            }
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
