package com.netcracker.jgit;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class GitService {
    private static final Logger logger = LoggerFactory.getLogger(GitService.class);

    private static final String REMOTE_URL = "https://github.com/ncdemo18/testJGitRepository.git";
    private static final String USERNAME = "ncdemo18";
    private static final String PASSWORD = "123Abc++";

    @Autowired
    private ValidateLocationService validateService;

    @Autowired
    private NameLocationResolverService nameService;

    public void addLocationOnRepository(String nameLocation, List<MultipartFile> files) throws GitAPIException, IOException, InvalidLocationDateException {
        validateService.checkCorrectLocationDate(nameLocation, files);

        Path localPath = Files.createTempDirectory("testJGitRepository");
        logger.info("Cloning from {} to {}", REMOTE_URL, localPath);
        try (Git result = Git.cloneRepository()
                .setURI(REMOTE_URL)
                .setDirectory(localPath.toFile())
                .call()) {
            logger.info("Having repository: {}", result.getRepository().getDirectory());

            String strLocationFolder = "//mockup//" + nameLocation + "//bg";
            String strTargetFolder = result.getRepository().getDirectory().getParent() + strLocationFolder;
            Path directory = Files.createDirectories(Paths.get(strTargetFolder));
            for (MultipartFile file : files) {
                Path targetPath = directory.resolve(nameService.resolveName(file.getOriginalFilename()));
                Files.write(targetPath, file.getBytes());
            }

            result.add().addFilepattern(".").call();

            result.commit().setMessage("Added location files").call();
            logger.info("Committed files to repository at {}", result.getRepository().getDirectory());

            result.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(USERNAME, PASSWORD)).call();
            logger.info("Pushed from repository {} to remote repository at {}", result.getRepository().getDirectory(), REMOTE_URL);
        }
        FileUtils.deleteDirectory(localPath.toFile());
    }
}
