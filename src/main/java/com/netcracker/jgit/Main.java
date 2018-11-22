package com.netcracker.jgit;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final String REMOTE_URL = "https://github.com/ncdemo18/testJGitRepository.git";
    private static final String USERNAME = "ncdemo18";
    private static final String PASSWORD = "****";

    public static void main(String[] args) throws GitAPIException, IOException {
        System.out.print("Enter location name: ");
        Scanner scanner = new Scanner(System.in);
        String locationName = scanner.nextLine();

        Path sourcePath = Paths.get("mockup//london//bg");
        List<Path> collect = Arrays
                .stream(Objects.requireNonNull(sourcePath.toFile().listFiles()))
                .filter((file -> !file.isDirectory()))
                .map(file -> Paths.get(file.getAbsolutePath()))
                .collect(Collectors.toList());

        for (Path path : collect) {
            System.out.println(path.toString());
        }

        String strTargetPath = "mockup//" + locationName + "//bg";
        Path directory = Files.createDirectories(Paths.get(strTargetPath));
        // Files.createFile(Paths.get(strTargetPath + "//" + "file.txt"));
        Files.copy(sourcePath.resolve("file.txt"), Paths.get(strTargetPath + "//" + "file.txt"));
        // addOnRepository();
    }

    public static void addLocationOnRepository(String locationName, List<Path> files) throws GitAPIException, IOException{
        File localPath = File.createTempFile("testJGitRepository", "");
        if (!localPath.delete()) {
            throw new IOException("Could not delete temporary file " + localPath);
        }

        System.out.println("Cloning from " + REMOTE_URL + " to " + localPath);
        try (Git result = Git.cloneRepository()
                .setURI(REMOTE_URL)
                .setDirectory(localPath)
                .call()) {
            System.out.println("Having repository: " + result.getRepository().getDirectory());

            File myFile = new File(result.getRepository().getDirectory().getParent(), "testfile");
            if (!myFile.createNewFile()) {
                throw new IOException("Could not create file " + myFile);
            }

            result.add().addFilepattern("testfile").call();
            result.commit().setMessage("Added testfile").call();
            System.out.println("Committed file " + myFile + " to repository at " + result.getRepository().getDirectory());

            result.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(USERNAME, PASSWORD)).call();
            System.out.println("Pushed from repository: " + result.getRepository().getDirectory() + " to remote repository at " + REMOTE_URL);
        }
        FileUtils.deleteDirectory(localPath);
    }
}
