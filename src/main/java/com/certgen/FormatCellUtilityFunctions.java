package com.certgen;



import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

/*
import javax.xml.bind.JAXBException;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.docx4j.XmlUtils;
import org.docx4j.math.CTOMathPara;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
*/

public class FormatCellUtilityFunctions {
    public static String getMathML(Cell cell) throws Exception {
       
        String mathML = cell.getStringCellValue();

        //The native OMML2MML.XSL transforms OMML into MathML as XML having special name spaces.
        //We don't need this since we want using the MathML in HTML, not in XML.
        //So ideally we should changing the OMML2MML.XSL to not do so.
        //But to take this example as simple as possible, we are using replace to get rid of the XML specialities.
        mathML = mathML.replaceAll("xmlns:mml=\"\"http://www.w3.org/1998/Math/MathML\"", "");
        mathML = mathML.replaceAll("xmlns:mml", "xmlns");
        mathML = mathML.replaceAll("mml:", "");
        mathML = mathML.replaceAll("xmlns=\"http://www.w3.org/1998/Math/MathML\" display=\"block\"", ""); 
        System.out.println(mathML);
        return mathML;
    }
    public static String getFormattedText(Cell cell) {
        XSSFRichTextString richText = (XSSFRichTextString) cell.getRichStringCellValue();
        StringBuilder latexBuilder = new StringBuilder();
        for (int i = 0; i < richText.length(); i++) {
            String charText = richText.getString().substring(i, i + 1);
            Font font = richText.getFontAtIndex(i);
            if (font != null && font.getTypeOffset() == Font.SS_SUB) {
                latexBuilder.append("_{").append(charText).append("}");
            } else if (font != null && font.getTypeOffset() == Font.SS_SUPER) {
                latexBuilder.append("^{").append(charText).append("}");
            } else {
                latexBuilder.append(charText);
            }
        }
        return latexBuilder.toString();
    }
    /* 
    public static String getCellContentAsHtml(Cell cell) throws Exception {
        StringBuilder htmlContent = new StringBuilder();

        try {
            // Try to extract OMML content
            String omml = extractOMMLFromCell(cell);
            htmlContent.append(convertOMMLToHtml(omml));
        } catch (Exception e) {
            // Handle as rich text or plain text if OMML content is not found
            htmlContent.append(convertRichTextToHtml(cell));
        }

        return htmlContent.toString();
    }
    private static String extractOMMLFromCell(Cell cell) throws Exception {
        if (cell.getCellType() == CellType.STRING) {
            XSSFCell xssfCell = (XSSFCell) cell;
            String xmlContent = xssfCell.getRawValue();
            if (xmlContent != null && xmlContent.contains("http://schemas.openxmlformats.org/officeDocument/2006/math")) {
                return xmlContent;
            }
        }
        throw new Exception("OMML content not found in cell.");
    }
    private static String convertOMMLToHtml(String omml) throws JAXBException, Docx4JException {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        wordMLPackage.getMainDocumentPart().addObject(XmlUtils.unmarshalString(omml));

        CTOMathPara mathPara = (CTOMathPara) wordMLPackage.getMainDocumentPart().getContent().get(0);
        org.docx4j.math.CTOMath math = mathPara.getOMath().get(0);
        return XmlUtils.marshaltoString(math, true, true).replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
    private static String convertRichTextToHtml(Cell cell) {
        XSSFRichTextString richText = (XSSFRichTextString) cell.getRichStringCellValue();
        StringBuilder htmlBuilder = new StringBuilder();

        for (int i = 0; i < richText.length(); i++) {
            String charText = richText.getString().substring(i, i + 1);
            Font font = richText.getFontAtIndex(i);
            if (font != null && font.getTypeOffset() == Font.SS_SUB) {
                htmlBuilder.append("<sub>").append(charText).append("</sub>");
            } else if (font != null && font.getTypeOffset() == Font.SS_SUPER) {
                htmlBuilder.append("<sup>").append(charText).append("</sup>");
            } else {
                htmlBuilder.append(charText);
            }
        }
        return htmlBuilder.toString();
    }
    */
}
