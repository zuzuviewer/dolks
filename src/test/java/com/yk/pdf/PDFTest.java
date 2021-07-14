package com.yk.pdf;

public class PDFTest {

    public static void main(String[] args) {
        PDF pdf = new PDF("C:/Users/User/Documents/lishi/test.pdf");
        try {
            pdf.toWord();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
