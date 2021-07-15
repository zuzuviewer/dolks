package com.yk.pdf;

public class PDFTest {

    public static void main(String[] args) {
        PDF pdf = new PDF("C:/Users/User/Documents/lishi/test.pdf");
        try {
            //pdf.toWord("C:/Users/User/Documents/lishi/1.docx");
            pdf.extractText("C:/Users/User/Documents/lishi/test.txt");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pdf.close();
        }
    }
}
