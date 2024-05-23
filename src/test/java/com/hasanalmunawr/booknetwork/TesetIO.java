package com.hasanalmunawr.booknetwork;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class TesetIO {


    @Test
    void createPath() {
        Path path = Path.of("src/main/resources/templates/activate_account.html");
//        Assertions.assertEquals("pom.xml", path.toString());
        Assertions.assertFalse(path.isAbsolute());
    }

    @Test
    void usingFiles() {
        Path path = Path.of("src/main/resources/templates/activate_account.html");
//        Assertions.assertEquals("pom.xml", path.toString());
        Assertions.assertFalse(path.isAbsolute());
        Assertions.assertTrue(Files.exists(path));
    }
}
