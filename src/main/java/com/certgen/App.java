package com.certgen;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

///-----------headless temp
import com.microsoft.playwright.*;

/**
 * Hello world!
 *
 */
public class App extends JFrame
{   
    public static String DEST = "files/pdf/";
    private JComboBox<String> dropdown;
    private JButton uploadButton;
    private JButton uploadButton2;
    private static JTextArea textarea = new JTextArea();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new App();
            }
        });
    }

    

    public App() {
        super("Certificate Generator - IITG Academic Affairs");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700); // Increase the size for better visibility
        setLocationRelativeTo(null);
    
        // Dropdown setup
        String[] options = GlobalVariables.degTypes;
        dropdown = new JComboBox<>(options);
    
        // Upload button setup
        uploadButton = new JButton("Upload and Generate with Name TitleCase (Recommended)");
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textarea.setText("Started, Please wait...");

                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                String degType = String.valueOf(dropdown.getSelectedItem()); // Selected Dropdown value
    
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    // uploadAndGenerate(selectedFile, degType); //Single
                    uploadAndGenerateFromArray(selectedFile, degType , true);
                }
            }
        });
        // Upload button setup
        uploadButton2 = new JButton("Upload and Generate without Name TitleCase");
        uploadButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textarea.setText("Started, Please wait...");

                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                String degType = String.valueOf(dropdown.getSelectedItem()); // Selected Dropdown value
    
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    // uploadAndGenerate(selectedFile, degType); //Single
                    uploadAndGenerateFromArray(selectedFile, degType, false);
                }
            }
        });
        // Help text setup
        JEditorPane helpText = new JEditorPane();
        helpText.setContentType("text/html");
        helpText.setText(loadHelpText("assets/templates/helpText.html"));
        helpText.setEditable(false);
        JScrollPane helpScrollPane = new JScrollPane(helpText);
        // helpScrollPane.setSize(850, 100);
        helpScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // TextArea setup
        textarea = new JTextArea(10, 40); // Set rows and columns for the text area
        textarea.setText("Please select template and upload a file...");
        textarea.setLineWrap(true); // Enable line wrapping
        textarea.setWrapStyleWord(true); // Wrap lines at word boundaries
        JScrollPane scrollPane = new JScrollPane(textarea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    
        // Panel for dropdown and button
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(dropdown);
        controlPanel.add(uploadButton);
        controlPanel.add(uploadButton2);
    
        // Layout setup
        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(helpScrollPane, BorderLayout.SOUTH);
    
        setVisible(true);
    }

    private String loadHelpText(String filePath) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return "<html><body><p>Error loading help text.</p></body></html>";
        }
    }

    private static void uploadAndGenerateFromArray(File tempFile, String degType, boolean titleCaseFlag){
         // Initialize Playwright
         try (Playwright playwright = Playwright.create()) {
            // Launch the browser
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));

            // Create a new page
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            try {

                
                DEST = tempFile.getParentFile().getAbsolutePath()+"\\";
                System.out.println("---------");
                System.out.println(DEST);
                
                ArrayList<String> htmlPages = getHtmlPages(tempFile, degType, titleCaseFlag);
                 
                // UtilityFunctions.createAssetsRoute(context); //not needed as of now as images and fonts are loading.
                
                // Combine all HTML pages into a single document
                StringBuilder combinedHtml = new StringBuilder();
                combinedHtml.append("<html><body>");
                
                for (String htmlContent : htmlPages) {
                    combinedHtml.append(htmlContent);
                }
                
                combinedHtml.append("</body></html>");

                //Create HTML file and get its absolute path----start
                File htmlFile = new File("output.html");
                String htmlFilePath = new String();
                try {
                    // Write the HTML content to the file
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(htmlFile), StandardCharsets.UTF_8));
                    writer.write(combinedHtml.toString());
                    writer.close();

                    // Get the absolute path of the created file
                    htmlFilePath = htmlFile.getAbsolutePath();
                    System.out.println("HTML file created at: " + htmlFilePath);
                } catch (IOException e) {
                    StringWriter stackTraceWriter = new StringWriter();
                    e.printStackTrace(new PrintWriter(stackTraceWriter));
                    textarea.append(e.toString() + "\n" + stackTraceWriter.toString());
                }
                //Create HTML file and get its absolute path----end
                page.navigate("file:///"+htmlFilePath); //this is used for proper rendering of images
                // page.setContent(combinedHtml.toString());

                // Wait for MathJax to finish rendering
                // page.waitForSelector(".mjx-chtml"); 
                String renderedHtml = page.content();
                    java.nio.file.Files.write(Paths.get("debug-rendered.html"), renderedHtml.getBytes());

                page.waitForSelector("body[data-mathjax-rendered='true']", new Page.WaitForSelectorOptions().setTimeout(60000));
                // Wait for images to load by checking if the images have loaded
                // Adding a delay to ensure everything is rendered properly
                page.waitForTimeout(1000);

                /* ---DEBUG CODE----
                 // Debugging step: take a screenshot
                    page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("debug-screenshot.png")));
                
                    // Debugging step: save the HTML content
                    String renderedHtml = page.content();
                    java.nio.file.Files.write(Paths.get("debug-rendered.html"), renderedHtml.getBytes());
                
                */

                String fileName = tempFile.getName().split("\\.")[0];
                
                // Generate PDF
                page.pdf(new Page.PdfOptions()
                    .setPrintBackground(true)
                    .setPath(Paths.get(DEST+fileName+".pdf"))
                    .setFormat("A4"));

                // Close the browser
                browser.close();
                textarea.append("\n"+"Done-"+fileName+".pdf");
                System.out.println("Done-"+fileName+".pdf");
                textarea.append("\n"+"All Done. Finito.");
                textarea.append("\n"+"====================================");
                System.out.println("All Done. Finito.");
            } catch (IOException e) {
                StringWriter stackTraceWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTraceWriter));
                textarea.append(e.toString() + "\n" + stackTraceWriter.toString());
            }
        }
    }

    //----------create and return arraylist containing the html pages created------------//
    private static ArrayList<String> getHtmlPages(File tempFile, String degType, boolean titleCaseFlag){

        ArrayList<String> htmlPages = new ArrayList<String>();

        try {
            
            // FileInputStream excelFile = new FileInputStream(new File(DEST+"input.xlsx"));
            FileInputStream excelFile = new FileInputStream(tempFile);
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet sheet = workbook.getSheetAt(0); // Assuming the data is in the first sheet
            // XSSFDrawing drawing = (XSSFDrawing) sheet.getDrawingPatriarch();
            
            for (Row row : sheet) {
                ArrayList<String> argString = new ArrayList<String>();
                argString.add(String.valueOf((long)row.getCell(0).getNumericCellValue())); //Rollno
                if(titleCaseFlag){
                    argString.add(UtilityFunctions.toUpperCamelCase(row.getCell(1).getStringCellValue())); //Name with TitleCase
                }else{
                    argString.add(row.getCell(1).getStringCellValue()); //Name w/o TitleCase
                }
                argString.add(row.getCell(2).getStringCellValue()); //HindiName
                argString.add(row.getCell(3).getStringCellValue()); //Subject
                argString.add(row.getCell(4).getStringCellValue()); //Subject Hindi
                argString.add(
                    FormatCellUtilityFunctions.getMathML(row.getCell(5))
                    ); //Minor/Spl./Thesis
                argString.add(
                    FormatCellUtilityFunctions.getMathML(row.getCell(6))
                    ); //Minor/Spl. Hindi
                argString.add(row.getCell(7).getStringCellValue()); //compDate
                argString.add(row.getCell(8).getStringCellValue()); //compDateHindi
                argString.add(row.getCell(9).getStringCellValue()); //awardDate
                argString.add(row.getCell(10).getStringCellValue()); //awardDateHindi
                argString.add(row.getCell(11).getStringCellValue()); //issueDate

                //String name = row.getCell(0).getStringCellValue(); // Assuming name is in the first column
                //String hindiName = row.getCell(1).getStringCellValue(); // Assuming hindiName is in the second column
                // hindiName = getFormattedText(row.getCell(1));
                // hindiName = getCellContentAsHtml(row.getCell(1));
                //hindiName = FormatCellUtilityFunctions.getMathML(row.getCell(1));
                
                htmlPages.add(generateCertificateHtml(argString, degType));
            }
            workbook.close();
        } catch (Exception e) {
            StringWriter stackTraceWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTraceWriter));
            textarea.append(e.toString() + "\n" + stackTraceWriter.toString());
        }

        return htmlPages;
    }

    //-------Generate individual pages html to be added to the array list--------------//
    private static String generateCertificateHtml(ArrayList<String> argList, String degType) {
        
        StringBuilder htmlContentBuilder= new StringBuilder();
        String htmlContent = "";
        try {
                // String templatePath = "assets/templates/certificate_template.html";
                String templatePath = UtilityFunctions.getCertificateTemplateString(degType);
            
                htmlContent = new String(Files.readAllBytes(Paths.get(templatePath)), StandardCharsets.UTF_8);
                
                // html variables replacement function
                htmlContent = UtilityFunctions.populateHtmlVariables(argList, htmlContent, degType);
                
                textarea.append("\n"+"Done-"+argList.get(1)); // Name
                System.out.println("Done-"+argList.get(1));

            } catch (IOException e) {
                StringWriter stackTraceWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTraceWriter));
                textarea.append(e.toString() + "\n" + stackTraceWriter.toString());
            }

            htmlContentBuilder.append("<div class='mainContainer' style='max-height:100%;page-break-after:always; padding-top: 140pt; padding-bottom: 1.5cm;'>");
            htmlContentBuilder.append(htmlContent);
            htmlContentBuilder.append("</div>");
            return htmlContentBuilder.toString();
    }

    
//=====================================================OLDER==============================================================//
//==========================================GENERATES INDIVIDUAL PDF FILES================================================//
     //------old
     private static void uploadAndGenerate(File tempFile, String degType){
        try {
            
            // FileInputStream excelFile = new FileInputStream(new File(DEST+"input.xlsx"));
            FileInputStream excelFile = new FileInputStream(tempFile);
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet sheet = workbook.getSheetAt(0); // Assuming the data is in the first sheet
            // XSSFDrawing drawing = (XSSFDrawing) sheet.getDrawingPatriarch();

            for (Row row : sheet) {
                ArrayList<String> argString = new ArrayList<String>();
                argString.add(row.getCell(0).getStringCellValue()); //Rollno
                argString.add(row.getCell(1).getStringCellValue()); //Name
                argString.add(row.getCell(2).getStringCellValue()); //HindiName
                argString.add(row.getCell(3).getStringCellValue()); //Subject
                argString.add(row.getCell(4).getStringCellValue()); //Subject Hindi
                argString.add(
                    FormatCellUtilityFunctions.getMathML(row.getCell(5))
                    ); //Minor/Spl./Thesis
                argString.add(
                    FormatCellUtilityFunctions.getMathML(row.getCell(6))
                    ); //Minor/Spl. Hindi


                //String name = row.getCell(0).getStringCellValue(); // Assuming name is in the first column
                //String hindiName = row.getCell(1).getStringCellValue(); // Assuming hindiName is in the second column
                // hindiName = getFormattedText(row.getCell(1));
                // hindiName = getCellContentAsHtml(row.getCell(1));
                //hindiName = FormatCellUtilityFunctions.getMathML(row.getCell(1));
                
                generateCertificate(argString, degType);
            }
            textarea.append("\n"+"All Done. Finito.");
            System.out.println("All Done. Finito.");
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    //------old
    private static void generateCertificate(ArrayList<String> argList, String degType) {
        // Initialize Playwright
        try (Playwright playwright = Playwright.create()) {
            // Launch the browser
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));

            // Create a new page
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            try {
                // String templatePath = "assets/templates/certificate_template.html";
                String templatePath = UtilityFunctions.getCertificateTemplateString(degType);
                
                String htmlContent="";
            
                htmlContent = new String(Files.readAllBytes(Paths.get(templatePath)), StandardCharsets.UTF_8);
                
                // replace function
                htmlContent = UtilityFunctions.populateHtmlVariables(argList, htmlContent, degType);
                // Replace placeholders with actual values ( done in the above function)
                // htmlContent = htmlContent.replace("${name}", hindiName);
                // htmlContent = htmlContent.replace("${course}", name);
                // htmlContent = htmlContent.replace("${degType}", degType);
                // htmlContent = htmlContent.replace("${date}", java.time.LocalDate.now().toString());


                page.setContent(htmlContent);

                // Wait for MathJax to finish rendering
                // page.waitForSelector(".mjx-chtml"); 
                String renderedHtml = page.content();
                    java.nio.file.Files.write(Paths.get("debug-rendered.html"), renderedHtml.getBytes());

                page.waitForSelector("body[data-mathjax-rendered='true']", new Page.WaitForSelectorOptions().setTimeout(60000));
                
                // Adding a delay to ensure everything is rendered properly
                page.waitForTimeout(1000);

                /* ---DEBUG CODE----
                 // Debugging step: take a screenshot
                    page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("debug-screenshot.png")));
                
                    // Debugging step: save the HTML content
                    String renderedHtml = page.content();
                    java.nio.file.Files.write(Paths.get("debug-rendered.html"), renderedHtml.getBytes());
                
                */

                String name = argList.get(1);
                // Generate PDF
                page.pdf(new Page.PdfOptions()
                    .setPath(Paths.get(DEST+name+".pdf"))
                    .setFormat("A4"));

                // Close the browser
                browser.close();
                textarea.append("\n"+"Done-"+name);
                System.out.println("Done-"+name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    

    
}
