package com.netcracker.jgit;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ValidateLocationService {
    private static final int FIRST_PAGE = 0b0001;
    private static final int SECOND_PAGE = 0b0010;
    private static final int THIRD_PAGE = 0b0100;
    private static final int FOURTH_PAGE = 0b1000;
    private static final int ALL_PAGES = 0b1111;

    public void checkCorrectLocationDate(String nameLocation, List<MultipartFile> files) throws InvalidLocationDateException {
        if(nameLocation == null || nameLocation.isEmpty()) {
            throw new InvalidLocationDateException("Location name don't enter");
        }
        if(files == null || files.size() != 4) {
            throw new InvalidLocationDateException("Count of files don't correct ");
        }
        checkCorrectNames(files);
    }

    private void checkCorrectNames(List <MultipartFile> files) throws InvalidLocationDateException {
        int existPages = 0;
        for (MultipartFile file : files) {
            switch (deleteExtension(file.getOriginalFilename())) {
                case "1" :
                    existPages += FIRST_PAGE;
                    break;
                case "2" :
                    existPages += SECOND_PAGE;
                    break;
                case "3" :
                    existPages += THIRD_PAGE;
                    break;
                case "4" :
                    existPages += FOURTH_PAGE;
                    break;
            }
        }
        if(existPages != ALL_PAGES) {
            throw new InvalidLocationDateException("Names of files don't correct");
        }
    }

    private String deleteExtension(String name){
        int extensionPosition = name.lastIndexOf('.');
        if(extensionPosition > 0) {
            name =  name.substring(0, extensionPosition);
        }
        return name;
    }
}
