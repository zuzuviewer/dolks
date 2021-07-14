package com.yk.pdf;

import com.spire.doc.Document;
import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import com.spire.pdf.graphics.PdfMargins;
import com.yk.Constants.Constant;
import com.yk.utils.StringUtils;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PDF {
    // 需要处理的pdf文档
    protected String pdfFile;

    public PDF(String pdfFile) {
        this.pdfFile = pdfFile;
    }

    /**
     * 将pdf文档转化成doc文档，生成的doc文档和pdf同目录，名称相同
     *
     * @throws Exception
     */
    public void toWord() throws Exception {
        toWord(null);
    }

    /**
     * 将pdf文档转化成doc文档，并指定生成的doc文档位置及名称
     *
     * @param docFileName 生成的doc文档位置及名称，如果为空的话则和pdf同目录同名
     * @throws Exception
     */
    public void toWord(String docFileName) throws Exception {
        if (StringUtils.isEmpty(pdfFile) || !pdfFile.endsWith(".pdf")){
            throw new Exception("pdf file name '" + pdfFile + "' is invalid");
        }
        if (StringUtils.isEmpty(docFileName)) {
            final String fileName = pdfFile.substring(0, pdfFile.lastIndexOf("."));
            docFileName = fileName + ".docx";
        }
        PdfDocument pdfDocument = null;
        try {
            pdfDocument = new PdfDocument();
            pdfDocument.loadFromFile(pdfFile);
            final int pages = pdfDocument.getPages().getCount();
            // 10页是免费的，直接转化成doc
            if (pages <= 10) {
                pdfDocument.saveToFile(docFileName, FileFormat.DOCX);
                return;
            }
            // 十页以上需要分段转化
            splitTransferToDoc(pdfDocument, docFileName);
        } finally {
            if (null != pdfDocument) {
                pdfDocument.close();
            }
        }
    }

    /**
     * 分段转化成docx
     *
     * @param pdfDocument
     * @param docFile
     * @throws Exception
     */
    private static void splitTransferToDoc(final PdfDocument pdfDocument, final String docFile) throws Exception {
        // 分割后的pdf文档集合
        List<PdfDocument> childPdfDocuments = new ArrayList<PdfDocument>();
        PdfDocument currentPdfDocument = null; // 当前正在操作的子pdf文档
        PdfPageBase pdfPageBase;
        for (int i = 0; i < pdfDocument.getPages().getCount(); ++i) {
            // 每十页pdf分割成一个子pdf文档
            if (0 == i % 10) {
                currentPdfDocument = new PdfDocument();
                childPdfDocuments.add(currentPdfDocument);
            }
            pdfPageBase = currentPdfDocument.getPages().add(pdfDocument.getPages().get(i).getSize(), new PdfMargins(0));
            pdfDocument.getPages().get(i).createTemplate().draw(pdfPageBase, new Point2D.Float(0, 0));
        }

        if (childPdfDocuments.isEmpty()) {
            throw new Exception("split pdf document is empty");
        }

        // 转化子pdf文档为doc文档并合并
        // 先单独转化第一个子pdf文档并得到Document
        String currentDocName = Constant.TEMP_PATH + "split0.docx"; // 当前生成的子doc文档路径
        childPdfDocuments.get(0).saveToFile(currentDocName, FileFormat.DOCX);
        Document document = new Document(currentDocName);
        // 删除临时doc文档
        File tempFile = new File(currentDocName);
        tempFile.delete();
        // 将第一个后面的子pdf文档转化成doc并合并到document中
        for (int i = 1; i < childPdfDocuments.size(); ++i) {
            currentDocName = Constant.TEMP_PATH + "split" + i + ".docx";
            childPdfDocuments.get(i).saveToFile(currentDocName, FileFormat.DOCX);
            document.insertTextFromFile(currentDocName, com.spire.doc.FileFormat.Docx_2013);
            // 删除临时doc文档
            tempFile = new File(currentDocName);
            tempFile.delete();
        }
        // 保存整体的doc文档
        document.saveToFile(docFile);
    }

    public String getPdfFile() {
        return pdfFile;
    }

    public void setPdfFile(String pdfFile) {
        this.pdfFile = pdfFile;
    }
}