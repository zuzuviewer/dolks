package com.yk.pdf;

import com.spire.doc.Document;
import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import com.spire.pdf.graphics.PdfMargins;
import com.yk.Constants.Constant;
import com.yk.utils.FileUtils;
import com.yk.utils.StringUtils;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class PDF {
    // 需要处理的pdf文档
    protected String pdfFile;
    private PdfDocument pdfDocument;

    public PDF(String pdfFile) {
        this.pdfFile = pdfFile;
    }

    public void close() {
        if (null != pdfDocument) {
            pdfDocument.close();
            pdfDocument = null;
        }
    }

    /**
     * 提取pdf文档中的图片并写入到png文件中，
     *
     * @param outPath 图片生成的文件夹目录，生成的图片文件名称是pdf文档名称加序号，序号从1开始
     * @throws Exception
     * @note it is unable with free spire office version
     */
    public void extractImage(String outPath) throws Exception {
        if (StringUtils.isEmpty(pdfFile) || !pdfFile.endsWith(".pdf")) {
            throw new Exception("pdf file name '" + pdfFile + "' is invalid");
        }
        loadPdf();
        outPath = FileUtils.AddPathSeparator(outPath);
        final String fileNamePrefix = pdfFile.substring(0, pdfFile.lastIndexOf("."));
        int index = 1;
        String fileName;
        File output;
        for (PdfPageBase page : (Iterable<PdfPageBase>) pdfDocument.getPages()) {
            BufferedImage[] images = page.extractImages();
            // with free spire,is is always null
            if (null == images || 0 == images.length) {
                return;
            }
            for (BufferedImage image : page.extractImages()) {
                fileName = new StringBuilder().append(outPath).append(fileNamePrefix).append(index++).append(".png").toString();
                output = new File(fileName);
                ImageIO.write(image, "PNG", output);
            }
        }
    }

    /**
     * 提取pdf文档中的文字并写入指定文件
     *
     * @param outFile
     * @throws Exception
     */
    public void extractText(final String outFile) throws Exception {
        final String text = extractText();
        FileWriter writer = new FileWriter(outFile);
        writer.write(text);
        writer.flush();
        writer.close();
    }

    /**
     * 提取pdf文档中的文字
     *
     * @return 返回提取的文字
     */
    public String extractText() throws Exception {
        if (StringUtils.isEmpty(pdfFile) || !pdfFile.endsWith(".pdf")) {
            throw new Exception("pdf file name '" + pdfFile + "' is invalid");
        }
        loadPdf();
        StringBuilder sb = new StringBuilder();
        for (PdfPageBase pdfPageBase : (Iterable<PdfPageBase>) pdfDocument.getPages()) {
            sb.append(pdfPageBase.extractText(true));
        }
        return sb.toString();
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
        if (StringUtils.isEmpty(pdfFile) || !pdfFile.endsWith(".pdf")) {
            throw new Exception("pdf file name '" + pdfFile + "' is invalid");
        }
        if (StringUtils.isEmpty(docFileName)) {
            final String fileName = pdfFile.substring(0, pdfFile.lastIndexOf("."));
            docFileName = fileName + ".docx";
        }
        loadPdf();
        final int pages = pdfDocument.getPages().getCount();
        // 10页是免费的，直接转化成doc
        if (pages <= 10) {
            pdfDocument.saveToFile(docFileName, FileFormat.DOCX);
            return;
        }
        // 十页以上需要分段转化
        splitTransferToDoc(pdfDocument, docFileName);
    }

    /**
     * 分段转化成docx
     *
     * @param pdfDocument
     * @param docFile
     * @throws Exception
     */
    private void splitTransferToDoc(final PdfDocument pdfDocument, final String docFile) throws Exception {
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

    private void loadPdf() {
        if (null != pdfDocument) {
            return;
        }
        pdfDocument = new PdfDocument();
        pdfDocument.loadFromFile(pdfFile);
    }

    public String getPdfFile() {
        return pdfFile;
    }

    public void setPdfFile(String pdfFile) {
        this.pdfFile = pdfFile;
        // 防止改变文件后没有改变文档缓存
        if (null != pdfDocument) {
            pdfDocument.close();
            pdfDocument = null;
        }
    }
}
