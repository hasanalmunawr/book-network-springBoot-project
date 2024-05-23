package com.hasanalmunawr.booknetwork.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class EmailUtils {

    public static String ACTIVATION_ACCOUNT = "Account activation";

    public static String emailMessage(String username, String activationCode, String confirmationUrl) {
        try {
            // Assuming pathToHtmlFile is the path to your HTML file
            Path pathToHtmlFile = Path.of("src/main/resources/templates/activate_account.html");
            // Read the contents of the HTML file into a String
            String htmlContent = Files.readString(pathToHtmlFile);

            // Perform string manipulation to replace variables
//            String username = "John Doe"; // Example username
//            String activationCode = "123456"; // Example activation code

            // Replace variables with actual values in the HTML content
            htmlContent = htmlContent.replace("${username}", username);
            htmlContent = htmlContent.replace("${activation_code}", activationCode);
            htmlContent = htmlContent.replace("${confirmationUrl}", confirmationUrl);


            // Print the modified HTML content
//            System.out.println(htmlContent);
            return htmlContent;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
