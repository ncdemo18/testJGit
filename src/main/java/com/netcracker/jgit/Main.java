package com.netcracker.jgit;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.IOException;
import java.nio.file.*;
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
        List<Path> collect = Arrays.stream(Objects.requireNonNull(sourcePath.toFile().listFiles()))
                .filter((file -> !file.isDirectory()))
                .map(file -> Paths.get(file.getAbsolutePath()))
                .collect(Collectors.toList());

        addLocationOnRepository(locationName, collect);
    }

    private static void addLocationOnRepository(String locationName, List<Path> files) throws GitAPIException, IOException{
        Path localPath = Files.createTempDirectory("testJGitRepository");
        System.out.println("Cloning from " + REMOTE_URL + " to " + localPath);
        try (Git result = Git.cloneRepository()
                .setURI(REMOTE_URL)
                .setDirectory(localPath.toFile())
                .call()) {
            System.out.println("Having repository: " + result.getRepository().getDirectory());

            String strLocationFolder = "//mockup//" + locationName + "//bg";
            String strTargetFolder = result.getRepository().getDirectory().getParent() + strLocationFolder;
            Path directory = Files.createDirectories(Paths.get(strTargetFolder));
            for(Path source : files){
                Path target = directory.resolve(source.getFileName());
                Files.copy(source, target);
            }

            result.add().addFilepattern(".").call();

            result.commit().setMessage("Added files").call();
            System.out.println("Committed files to repository at " + result.getRepository().getDirectory());

            result.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(USERNAME, PASSWORD)).call();
            System.out.println("Pushed from repository: " + result.getRepository().getDirectory() + " to remote repository at " + REMOTE_URL);
        }
        FileUtils.deleteDirectory(localPath.toFile());
    }
}
