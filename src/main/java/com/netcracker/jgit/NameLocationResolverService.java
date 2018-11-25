package com.netcracker.jgit;

import org.springframework.stereotype.Service;

@Service
public class NameLocationResolverService {
    private static final String FIRST_PAGE = "1 Lock Screen.png";
    private static final String SECOND_PAGE = "2 Lock Screen.png";
    private static final String THIRD_PAGE = "3 Media Center.png";
    private static final String FOURTH_PAGE = "5 Match page.png";

    public String resolveName(String name) {
        switch (deleteExtension(name)) {
            case "1" : return FIRST_PAGE;
            case "2" : return SECOND_PAGE;
            case "3" : return THIRD_PAGE;
            case "4" : return FOURTH_PAGE;
            default: throw new InvalidLocationDateException("Incorrect file name");
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
