package com.lezic.core.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class UtilExcel {

    /**
     * 获取excel中的结果集。不包含第一行
     *
     * @param inputStream
     * @param startRowIndex 从第几行开始。【注意】若是从第0行，则可能包含列标题，保存数据时，请不要加入保存
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @author cielo
     */
    public static List<List<String>> getRows(InputStream inputStream, int startRowIndex) throws FileNotFoundException,
            IOException {
//        Workbook work = WorkbookFactory.create(inputStream);// 用工程方法可以兼容 .xls .xlsx
        HSSFWorkbook work = new HSSFWorkbook(inputStream);
        List<List<String>> result = new ArrayList<List<String>>();
        for (int i = 0; i < work.getNumberOfSheets(); i++) {
            Sheet sheet = work.getSheetAt(i);
            if (sheet == null) {
                continue;
            }
            for (int j = startRowIndex; j <= sheet.getLastRowNum(); j++) {
                Row rows = sheet.getRow(j);
                List<String> row = new ArrayList<String>();
                if (rows == null) {
                    continue;
                }
                for (short k = 0; k < rows.getLastCellNum(); k++) {
                    Cell cell = rows.getCell(k);
                    row.add(getValue(cell));
                }
                // 判断是否一整行都是空的，若是则不加入集合
                boolean empty = true;
                for (int k = 0; k < row.size(); k++) {
                    if (UtilData.isNotNull(row.get(k))) {
                        empty = false;
                        break;
                    }
                }
                if (empty == false) {
                    result.add(row);
                }
            }
        }
        return result;
    }

    /**
     * 获取cell的值
     *
     * @param cell
     * @return
     * @author cielo
     */
    private static String getValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {// 返回布尔类型的值
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {// 返回日期
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String str = sdf.format(DateUtil.getJavaDate(cell.getNumericCellValue()));
                if (str != null) {
                    str = str.replaceAll(" 00:00:00$", "");
                }
                return str;
            } else {// 返回数值类型的值
                Double d = cell.getNumericCellValue();
                if (d != null) {
                    DecimalFormat df = new DecimalFormat("0");
                    String str = df.format(d);
                    if (str != null) {
                        str = str.replaceAll("\\.0$", "");
                    }
                    return str;
                }
                return null;
            }
        } else {
            // 返回字符串类型的值
            return String.valueOf(cell.getStringCellValue());
        }
    }

    /**
     * 创建标题行
     *
     * @param sheet
     * @param headers
     * @param title
     */
    public static void createHeader(HSSFWorkbook workbook, HSSFSheet sheet, String[] headers, String title) {
        if (title == null || "".equals(title)) {
            HSSFHeader header = sheet.getHeader();
            header.setCenter(title);
        }
        HSSFRow headerRow = sheet.createRow(0);
        if (headers == null)
            return;

        // 设置这些样式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        // 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.BLACK.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        style.setFont(font);

        for (short i = 0; i < headers.length; i++) {
            HSSFCell headerCell = headerRow.createCell(i);
            headerCell.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            headerCell.setCellValue(text);
        }
    }

    /**
     * 创建行
     *
     * @param sheet
     * @param data
     * @param rowindex
     */
    public static void createRow(HSSFSheet sheet, List<Object> data, short rowindex) {
        if (data == null || "".equals(data)) {
            return;
        }

        HSSFRow row = sheet.createRow(rowindex);
        for (short i = 0; i < data.size(); i++) {
            HSSFCell cell = row.createCell(i);

            Object obj = data.get(i);
            if (obj instanceof String) {
                HSSFRichTextString text = new HSSFRichTextString((String) obj);
                cell.setCellValue(text);
            } else if (obj instanceof Integer) {
                cell.setCellValue((Integer) obj);
            } else if (obj instanceof Long) {
                cell.setCellValue((Long) obj);
            } else if (obj instanceof Date) {
                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) obj);
                HSSFRichTextString text = new HSSFRichTextString(date);
                cell.setCellValue(text);
            }

        }
    }

    /**
     * 生成Excel文件
     *
     * @param path
     * @param fileName
     */
    public static File writeExcelFile(HSSFWorkbook workbook, String path, String fileName) {
        if (fileName == null || "".equals(fileName)) {
            fileName = System.currentTimeMillis() + ".xls";
        }
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        if (!fileName.endsWith(".xls")) {
            fileName += ".xls";
        }
        String tempName = path + fileName;
        FileOutputStream fos = null;
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(tempName);
            fos = new FileOutputStream(file);

            workbook.write(fos);
            fos.flush();
            fos.close();
            fos = null;
            workbook = null;
            Runtime.getRuntime().gc();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                fos.close();
                fos = null;
                workbook = null;
                Runtime.getRuntime().gc();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 将List<List<Object>> excel行的结果集写入指定的excel文件中
     *
     * @param path     文件路径
     * @param fileName 文件名
     * @param params   参数， 包括：1.sheetName 工作薄名称 2.headers 列标题行 String[] 3.headerName 标题
     * @param rows     List<List<Object>> excel行的结果集
     * @return
     * @author cielo
     */
    public static File createExcelFile(String path, String fileName, Map<String, Object> params, List<List<Object>> rows) {
        String sheetName = "工作簿1";
        String headerName = null;
        String[] headers = null;
        short headerFlag = 0;
        if (params.get("sheetName") != null) {// 工作薄名称
            sheetName = params.get("sheetName").toString();
        }
        if (params.get("headers") != null) {// 列标题行
            headers = (String[]) params.get("headers");
            headerFlag++;
        }
        if (params.get("headerName") != null) {// 标题
            headerName = params.get("headerName").toString();
            headerFlag++;
        }

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);

        UtilExcel.createHeader(workbook, sheet, headers, headerName);
        int size = rows.size();
        for (short i = 0; i < size; i++) {
            UtilExcel.createRow(sheet, rows.get(i), (short) (i + headerFlag));
        }
        //设置列宽度
        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, headers[i].getBytes().length * 2 * 256);
        }
        return UtilExcel.writeExcelFile(workbook, path, fileName);
    }

    public static void main(String[] args) throws FileNotFoundException, IOException{
        File file = new File("F:\\123.xlsx");
        System.out.println(file.exists());
        List<List<String>> list = UtilExcel.getRows(new FileInputStream(file), 1);
        System.out.println(list.size());
        System.out.println(list.get(0).get(0));
    }
}