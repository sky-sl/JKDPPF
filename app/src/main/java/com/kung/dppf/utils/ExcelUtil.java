package com.kung.dppf.utils;

import android.os.Environment;

import com.kung.dppf.entity.WeighRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelUtil {
    private static WritableFont arial14font = null;
    private static WritableCellFormat arial14format = null;
    private static WritableFont arial10font = null;
    private static WritableCellFormat arial10format = null;
    private static WritableFont arial12font = null;
    private static WritableCellFormat arial12format = null;

    private static WritableFont arialfontTitle = null;
    private static WritableCellFormat arialformatTitle = null;

    private static WritableFont arialfontContent = null;
    private static WritableCellFormat arialformatContent = null;

    private static WritableFont arialfontContentBL = null;
    private static WritableCellFormat arialformatContentBL = null;
    private final static String UTF8_ENCODING = "UTF-8";

    public static String savePath = Environment.getExternalStorageDirectory() + File.separator + "JcWeightList";

    /**
     * 单元格的格式设置 字体大小 颜色 对齐方式、背景颜色等...
     */
    private static void format() {
        try {
            arial14font = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
            arial14font.setColour(Colour.LIGHT_BLUE);
            arial14format = new WritableCellFormat(arial14font);
            arial14format.setAlignment(Alignment.CENTRE);
            arial14format.setBorder(Border.ALL, jxl.format.BorderLineStyle.THIN);
            arial14format.setBackground(Colour.VERY_LIGHT_YELLOW);

            arial10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            arial10format = new WritableCellFormat(arial10font);
            arial10format.setAlignment(Alignment.CENTRE);
            arial10format.setBorder(Border.ALL, jxl.format.BorderLineStyle.THIN);
            arial10format.setBackground(Colour.GRAY_25);

            arial12font = new WritableFont(WritableFont.ARIAL, 12);
            arial12format = new WritableCellFormat(arial12font);
            //对齐格式
            arial12format.setAlignment(Alignment.CENTRE);
            //设置边框
            arial12format.setBorder(Border.BOTTOM, jxl.format.BorderLineStyle.THIN);

            arialfontTitle = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            arialformatTitle = new WritableCellFormat(arialfontTitle);
            //对齐格式
            arialformatTitle.setAlignment(Alignment.LEFT);
            //设置边框
//            arialformatTitle.setBorder(Border.BOTTOM, jxl.format.BorderLineStyle.THIN);
            arialfontContent = new WritableFont(WritableFont.ARIAL, 10);
            arialformatContent = new WritableCellFormat(arialfontContent);
            //对齐格式
            arialformatContent.setAlignment(Alignment.CENTRE);

            arialfontContentBL = new WritableFont(WritableFont.ARIAL, 10);
            arialformatContentBL = new WritableCellFormat(arialfontContentBL);
            //对齐格式
            arialformatContentBL.setAlignment(Alignment.CENTRE);
            arialformatContentBL.setBorder(Border.BOTTOM, jxl.format.BorderLineStyle.THIN);
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }


    /**
     * 初始化Excel表格
     *
     * @param filePath  存放excel文件的路径（MPCMS/Export/demo.xls）
     * @param sheetName Excel表格的表名
     * @param colName   excel中包含的列名（可以有多个）
     */
    public static void initExcel(String filePath, String sheetName, String[] colName, int sheetNum) {
        format();
        WritableWorkbook workbook = null;

        try {
            File file = new File(savePath);
            makeDir(file);

            File saveFile = new File(file, filePath);
            if (!saveFile.exists()) {
                saveFile.createNewFile();
            }

            workbook = Workbook.createWorkbook(saveFile);
            for (int i = 0; i < sheetNum; i++) {
                //设置表格的名字
                WritableSheet sheet = workbook.createSheet(sheetName + (i+1), i);
//                //创建标题栏
//                sheet.addCell((WritableCell) new Label(0, 0, filePath, arial14format));
//                for (int col = 0; col < colName.length; col++) {
//                    sheet.addCell(new Label(col, 0, colName[col], arial10format));
//                }
                //设置行高
//                sheet.setRowView(0, 340);

            }
            workbook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将制定类型的List写入Excel中
     * @param weighBeans
     * @param fileName
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean writeObjListToExcel(List<WeighRecord> weighBeans, String fileName) {
        if (weighBeans != null && weighBeans.size() > 0) {
            WritableWorkbook writebook = null;
            InputStream in = null;
            try {
                WorkbookSettings setEncode = new WorkbookSettings();
                setEncode.setEncoding(UTF8_ENCODING);
                File file = new File(savePath);
                makeDir(file);
                File saveFile = new File(file, fileName);
                if (!saveFile.exists()) {
                    saveFile.createNewFile();
                }

                in = new FileInputStream(saveFile);
                Workbook workbook = Workbook.getWorkbook(in);
                writebook = Workbook.createWorkbook(saveFile, workbook);

                WritableSheet sheet = writebook.getSheet(0);    //获取第一个sheet

                //保存的列：日期、产线号、产品名称、重量、标准重量、上限、下限、状态、批次号
                sheet.addCell(new Label(0, 0, "日期", arial10format));
                sheet.addCell(new Label(1, 0, "PLU编码", arial10format));
                sheet.addCell(new Label(2, 0, "产品名称", arial10format));
                sheet.addCell(new Label(3, 0, "产品单重", arial10format));
                sheet.addCell(new Label(4, 0, "颜色", arial10format));
                sheet.addCell(new Label(5, 0, "数量", arial10format));
                sheet.addCell(new Label(6, 0, "净重", arial10format));
                sheet.addCell(new Label(7, 0, "毛重", arial10format));
                sheet.addCell(new Label(8, 0, "标签抬头", arial10format));
                sheet.setColumnView(0, 20);
                sheet.setColumnView(2, 20);
                sheet.setColumnView(8, 26);


                //生成内容
                int startLine = 1;  //第2行开始
                for (int i = 0; i < weighBeans.size(); i++) {
//                    sheet.addCell(new Label(0, i + startLine, weighBeans.get(i).getWeighTime(), arialformatContent));
//                    sheet.addCell(new Label(1, i + startLine, weighBeans.get(i).getProductCode(), arialformatContent));
//                    sheet.addCell(new Label(2, i + startLine, weighBeans.get(i).getProductName(), arialformatContent));
//                    sheet.addCell(new Label(3, i + startLine, weighBeans.get(i).getSingleWeight(), arialformatContent));
//                    sheet.addCell(new Label(4, i + startLine, weighBeans.get(i).getProductColor(), arialformatContent));
//                    sheet.addCell(new Label(5, i + startLine, weighBeans.get(i).getQuantity(), arialformatContent));
//                    sheet.addCell(new Label(6, i + startLine, weighBeans.get(i).getWeight(), arialformatContent));
//                    sheet.addCell(new Label(7, i + startLine, weighBeans.get(i).getGrossWeight(), arialformatContent));
//                    sheet.addCell(new Label(8, i + startLine, weighBeans.get(i).getTagTitle(), arialformatContent));

                }

                writebook.write();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writebook != null) {
                    try {
                        writebook.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return true;
    }

    public static void makeDir(File dir) {
        if (!dir.getParentFile().exists()) {
            makeDir(dir.getParentFile());
        }
        dir.mkdir();
    }

    public static void writeExcel(File file, FileOutputStream fileOutputStream) {
        try {
            WorkbookSettings workbookSettings = new WorkbookSettings();
            workbookSettings.setEncoding(UTF8_ENCODING);
            Workbook workbook = Workbook.getWorkbook(file);
            WritableWorkbook copy = Workbook.createWorkbook(fileOutputStream, workbook);
            WritableSheet sheet = copy.getSheet(0);
            WritableCellFormat cellFormat = new WritableCellFormat();
            cellFormat.setAlignment(Alignment.CENTRE);
            cellFormat.setBorder(Border.ALL, jxl.format.BorderLineStyle.THIN);
            cellFormat.setBackground(Colour.GRAY_25);
            int rows = sheet.getRows();
            for (int i = 0; i < rows; i++) {
                sheet.setRowView(i, 340);
                for (int j = 0; j < sheet.getColumns(); j++) {
                    sheet.addCell(new Label(j, i, sheet.getCell(j, i).getContents(), cellFormat));
                }
            }
            copy.write();
            copy.close();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
