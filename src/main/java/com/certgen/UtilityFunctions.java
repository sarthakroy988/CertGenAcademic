package com.certgen;

import java.nio.file.Paths;
import java.util.ArrayList;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Route;

public class UtilityFunctions {

    //------Retrieve the template designed for each degree/ programme type
    public static String getCertificateTemplateString(String degType){
        String templatePath = "";
        if (degType.equals(GlobalVariables.degTypes[0])) { //"BTech"
            templatePath = "assets/templates/certificate_template_BTech.html";
        } else if (degType.equals(GlobalVariables.degTypes[1])) { //"BTech_Minor",
            templatePath = "assets/templates/certificate_template_BTech_Minor.html"; 
        } else if (degType.equals(GlobalVariables.degTypes[2])) { // "BDes", 
            templatePath = "assets/templates/certificate_template_BDes.html";
        } else if (degType.equals(GlobalVariables.degTypes[3])) { // "MSc", 
            templatePath = "assets/templates/certificate_template_MSc.html";
        } else if (degType.equals(GlobalVariables.degTypes[4])) { // "MA", 
            templatePath = "assets/templates/certificate_template_MA.html";
        } else if (degType.equals(GlobalVariables.degTypes[5])) { // "MBA", 
            templatePath = "assets/templates/certificate_template_MBA.html";
        } else if (degType.equals(GlobalVariables.degTypes[6])) { //"MTech", 
            templatePath = "assets/templates/certificate_template_MTech.html";
        } else if (degType.equals(GlobalVariables.degTypes[7])) { // "MTech_Spl", 
            templatePath = "assets/templates/certificate_template_MTech_Spl.html";
        } else if (degType.equals(GlobalVariables.degTypes[8])) { // "MDes", 
            templatePath = "assets/templates/certificate_template_MDes.html";
        } else if (degType.equals(GlobalVariables.degTypes[9])) { // "MDes_EPD", 
            templatePath = "assets/templates/certificate_template_MDes_EPD.html";
        } else if (degType.equals(GlobalVariables.degTypes[10])) { // "MSR", 
            templatePath = "assets/templates/certificate_template_MSR.html";
        } else if (degType.equals(GlobalVariables.degTypes[11])) { // "PhD", 
            templatePath = "assets/templates/certificate_template_PhD.html";
        } else if (degType.equals(GlobalVariables.degTypes[12])) { // "MTPhD", 
            templatePath = "assets/templates/certificate_template_MTPhD.html";
        } else if (degType.equals(GlobalVariables.degTypes[13])) { // "MSPhD"
            templatePath = "assets/templates/certificate_template_MSPhD.html";
        } else if (degType.equals(GlobalVariables.degTypes[14])) { //"Duplicate-BTech"
            templatePath = "assets/templates/certificate_template_BTech_Duplicate.html";
        } else if (degType.equals(GlobalVariables.degTypes[15])) { //"Duplicate-BTech_Minor",
            templatePath = "assets/templates/certificate_template_BTech_Minor_Duplicate.html"; 
        } else if (degType.equals(GlobalVariables.degTypes[16])) { // "Duplicate-BDes", 
            templatePath = "assets/templates/certificate_template_BDes_Duplicate.html";
        } else if (degType.equals(GlobalVariables.degTypes[17])) { // "Duplicate-MSc", 
            templatePath = "assets/templates/certificate_template_MSc_Duplicate.html";
        } else if (degType.equals(GlobalVariables.degTypes[18])) { // "Duplicate-MA", 
            templatePath = "assets/templates/certificate_template_MA_Duplicate.html";
        } else if (degType.equals(GlobalVariables.degTypes[19])) { // "Duplicate-MBA", 
            templatePath = "assets/templates/certificate_template_MBA_Duplicate.html";
        } else if (degType.equals(GlobalVariables.degTypes[20])) { //"Duplicate-MTech", 
            templatePath = "assets/templates/certificate_template_MTech_Duplicate.html";
        } else if (degType.equals(GlobalVariables.degTypes[21])) { // "Duplicate-MTech_Spl", 
            templatePath = "assets/templates/certificate_template_MTech_Spl_Duplicate.html";
        } else if (degType.equals(GlobalVariables.degTypes[22])) { // "Duplicate-MDes", 
            templatePath = "assets/templates/certificate_template_MDes_Duplicate.html";
        } else if (degType.equals(GlobalVariables.degTypes[23])) { // "Duplicate-MDes_EPD", 
            templatePath = "assets/templates/certificate_template_MDes_EPD_Duplicate.html";
        } else if (degType.equals(GlobalVariables.degTypes[24])) { // "Duplicate-MSR", 
            templatePath = "assets/templates/certificate_template_MSR_Duplicate.html";
        } else if (degType.equals(GlobalVariables.degTypes[25])) { // "Duplicate-PhD", 
            templatePath = "assets/templates/certificate_template_PhD_Duplicate.html";
        } else if (degType.equals(GlobalVariables.degTypes[26])) { // "Duplicate-MTPhD", 
            templatePath = "assets/templates/certificate_template_MTPhD_Duplicate.html";
        } else if (degType.equals(GlobalVariables.degTypes[27])) { // "Duplicate-MSPhD"
            templatePath = "assets/templates/certificate_template_MSPhD_Duplicate.html";
        } else if (degType.equals(GlobalVariables.degTypes[28])) { // "MSPhD_Spl", 
            templatePath = "assets/templates/certificate_template_MSPhD_Spl.html";
        } else if (degType.equals(GlobalVariables.degTypes[29])) { // "Duplicate-MSPhD_Spl"
            templatePath = "assets/templates/certificate_template_MSPhD_Spl_Duplicate.html";
        } else {
            System.out.println("Unknown degree program");
        }
        return templatePath;
    }


    //-----------Parse and replace the variables in html according to the degree type needed
    public static String populateHtmlVariables(ArrayList<String> argList, String htmlContent, String degtype){
        
        htmlContent = htmlContent.replace("${rollno}", argList.get(0));
        htmlContent = htmlContent.replace("${name}", argList.get(1));
        htmlContent = htmlContent.replace("${hindiName}", argList.get(2));
        htmlContent = htmlContent.replace("${subject}", argList.get(3));
        htmlContent = htmlContent.replace("${subjectHindi}", argList.get(4));
        htmlContent = htmlContent.replace("${minorSplThesis}", argList.get(5));
        htmlContent = htmlContent.replace("${minorSplThesisHindi}", argList.get(6));

        htmlContent = htmlContent.replace("${compDate}", argList.get(7));
        htmlContent = htmlContent.replace("${compDateHindi}", argList.get(8));
        htmlContent = htmlContent.replace("${awardDate}", argList.get(9));
        htmlContent = htmlContent.replace("${awardDateHindi}", argList.get(10));

        htmlContent = htmlContent.replace("${issueDate}", argList.get(11));

        if (degtype.equals(GlobalVariables.degTypes[25])) { // "Duplicate-PhD"
            String tempHTML = "";
            String tempHindiHTML = "";
            String subject = argList.get(3);
            String subjectHindi = argList.get(4);
            if (subject != null && !subject.trim().isEmpty()) {
                tempHTML = "<p style=\"text-align:center; font-size: calc(12pt * var(--scale-factor))\">in</p>"
                        + "<p style=\"text-align:center; font-size: calc(15pt * var(--scale-factor))\"><strong>"
                        + subject + "</strong></p>";
            }
            htmlContent = htmlContent.replace("${subjectHTML}", tempHTML);
            if (subjectHindi != null && !subjectHindi.trim().isEmpty()) {
                tempHindiHTML = "<p style=\"text-align:center; font-size: calc(12pt * var(--scale-factor));\">"+
                                "<strong style=\"font-size: calc(15pt * var(--scale-factor))\">" 
                                + subjectHindi + "</strong> में</p>";
            }
            htmlContent = htmlContent.replace("${subjectHindiHTML}", tempHindiHTML);
        }
        
            /* 
        if (degType.equals(GlobalVariables.degTypes[0])) { //"BTech"

            // Replace placeholders with actual values
        
        } else if (degType.equals(GlobalVariables.degTypes[1])) { //"BTech_Minor",
            templatePath = "assets/templates/certificate_template.html"; 
        } else if (degType.equals(GlobalVariables.degTypes[2])) { // "BDes", 
            templatePath = "assets/templates/certificate_template.html";
        } else if (degType.equals(GlobalVariables.degTypes[3])) { // "MSc", 
            templatePath = "assets/templates/certificate_template.html";
        } else if (degType.equals(GlobalVariables.degTypes[4])) { // "MA", 
            templatePath = "assets/templates/certificate_template.html";
        } else if (degType.equals(GlobalVariables.degTypes[5])) { // "MBA", 
            templatePath = "assets/templates/certificate_template.html";
        } else if (degType.equals(GlobalVariables.degTypes[6])) { //"MTech", 
            templatePath = "assets/templates/certificate_template.html";
        } else if (degType.equals(GlobalVariables.degTypes[7])) { // "MTech_Spl", 
            templatePath = "assets/templates/certificate_template.html";
        } else if (degType.equals(GlobalVariables.degTypes[8])) { // "MDes", 
            templatePath = "assets/templates/certificate_template.html";
        } else if (degType.equals(GlobalVariables.degTypes[9])) { // "MSR", 
            templatePath = "assets/templates/certificate_template.html";
        } else if (degType.equals(GlobalVariables.degTypes[10])) { // "PhD", 
            templatePath = "assets/templates/certificate_template.html";
        } else if (degType.equals(GlobalVariables.degTypes[11])) { // "MTPhD", 
            templatePath = "assets/templates/certificate_template.html";
        } else if (degType.equals(GlobalVariables.degTypes[12])) { // "MSPhD"
            templatePath = "assets/templates/certificate_template.html";
        } else {
            System.out.println("Unknown degree program");
        }
        */

        return htmlContent;
    }

    //---Capitalize the names---------------------//
    public static String toUpperCamelCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        String[] words = input.split("\\s+"); // Split by one or more spaces

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1).toLowerCase())
                      .append(" ");
            }
        }

        return result.toString();
    }


    //Routing function to serve local assets. This funcn is not used now but may be needed later.
    public static void createAssetsRoute(BrowserContext context){
        // Set up a route to serve local assets
                context.route("**/*", route -> {
                    String url = route.request().url();
                    System.out.println("Request URL: " + url); // Debugging to see what URLs are being requested
    
                    // Serve local assets only
                    if (url.startsWith("file://") || url.startsWith("http://localhost") || url.startsWith("https://localhost")) {
                        java.nio.file.Path path = null;
    
                        if (url.contains("/assets/")) {
                            path = Paths.get("assets" + url.substring(url.indexOf("/assets/") + "/assets".length()));
                        } else if (url.contains("/templates/")) {
                            path = Paths.get("templates" + url.substring(url.indexOf("/templates/") + "/templates".length()));
                        }
    
                        if (path != null) {
                            path = path.toAbsolutePath();
                            System.out.println("Serving local file: " + path); // Debugging to see what local file is being served
    
                            if (path.toFile().exists()) {
                                route.fulfill(new Route.FulfillOptions().setPath(path));
                                return;
                            }
                        }
                    }
    
                    // Resume the request if it's not a local asset
                    route.resume();
                });
    }
}
