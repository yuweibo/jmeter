package com.xy;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) throws GitAPIException {
        Git git = Git.cloneRepository()
                .setURI("git@github.com:yuweibo/jmeter.git")
                .setDirectory(new File("/Users/yuweibo/Downloads/git_clone_test/0"))
                .call();
        System.out.println(git);
    }
}
