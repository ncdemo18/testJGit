package com.netcracker.jgit;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;

public class Main {
    private static final String REMOTE_URL = "https://github.com/user/testJGitRepository.git";

    public static void main(String[] args) throws GitAPIException, IOException {
        File localPath = File.createTempFile("TestGitRepository", "");
        if (!localPath.delete()) {
            throw new IOException("Could not delete temporary file " + localPath);
        }

        // clone repository
        System.out.println("Cloning from " + REMOTE_URL + " to " + localPath);
        try (Git result = Git.cloneRepository()
                .setURI(REMOTE_URL)
                .setDirectory(localPath)
                .call()) {
           System.out.println("Having repository: " + result.getRepository().getDirectory());

            File myFile = new File(result.getRepository().getDirectory().getParent(), "testfile");
            if(!myFile.createNewFile()) {
                throw new IOException("Could not create file " + myFile);
            }

            // run the add
            result.add().addFilepattern("testfile").call();

            // and then commit the changes
            result.commit().setMessage("Added testfile").call();
            System.out.println("Committed file " + myFile + " to repository at " + result.getRepository().getDirectory());

            result.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider("user", "password")).call();
            System.out.println("Pushed from repository: " + result.getRepository().getDirectory() + " to remote repository at " + REMOTE_URL);

        }

        FileUtils.deleteDirectory(localPath);
    }
}
