/**
 * 系统数据导出Excel 生成器
 *
 * @version 1.0
 */
package cn.itcast.common.utils.ext2;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExcelFileGenerator_V2 {

  private final int SPLIT_COUNT = 100; //Excel每个工作簿的行数

  private ArrayList<String> fieldName = null; //excel标题数据集

  private ArrayList<ArrayList<String>> fieldData = null; //excel数据内容

  private XSSFWorkbook workBook = null;

  private String title;

  /**
   * 构造器
   * @param fieldName 结果集的字段名
   * @ param //data
   */
  public ExcelFileGenerator_V2(String title, ArrayList<String> fieldName, ArrayList<ArrayList<String>> fieldData) {
    this.title = title;
    this.fieldName = fieldName;
    this.fieldData = fieldData;
  }

  /**
   * 创建HSSFWorkbook对象
   * @return HSSFWorkbook
   */
  public XSSFWorkbook createWorkbook() {

    workBook = new XSSFWorkbook();//创建一个工作薄对象
    int rows = fieldData.size();//总的记录数
    int sheetNum = 0;           //指定sheet的页数

    if (rows % SPLIT_COUNT == 0) {
      sheetNum = rows / SPLIT_COUNT;
    } else {
      sheetNum = rows / SPLIT_COUNT + 1;
    }
    for (int i = 1; i <= sheetNum; i++) {//循环2个sheet的值
      XSSFSheet sheet = workBook.createSheet("Page " + i);//使用workbook对象创建sheet对象
      /**title*/
      sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, fieldName.size()-1));
      XSSFRow titleRow = sheet.createRow(0);
      titleRow.setHeightInPoints(20);
      XSSFCell titleCell = titleRow.createCell(0);
      titleCell.setCellValue(title+" ( "+ i +" )");
      XSSFCellStyle titleStyle = workBook.createCellStyle();
      style_title(titleStyle);
      titleCell.setCellStyle(titleStyle);

      /**title结束*/
      XSSFRow headRow = sheet.createRow((short) 1); //创建行，0表示第一行（本例是excel的标题）
      for (int j = 0; j < fieldName.size(); j++) {//循环excel的标题
        XSSFCell cell = headRow.createCell(j);//使用行对象创建列对象，0表示第1列
        /**************对标题添加样式begin********************/

        //设置列的宽度/
        sheet.setColumnWidth(j, 6000);
        XSSFCellStyle cellStyle = workBook.createCellStyle();//创建列的样式对象
        XSSFFont font = workBook.createFont();//创建字体对象
        //字体加粗
//				font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setBold(true);
        //字体颜色变红
        font.setColor(IndexedColors.RED.index);
        //如果font中存在设置后的字体，并放置到cellStyle对象中，此时该单元格中就具有了样式字体
        cellStyle.setFont(font);
        style_head(cellStyle);

        /**************对标题添加样式end********************/

        //添加样式
        cell.setCellType(CellType.STRING);
        if (fieldName.get(j) != null) {
          //将创建好的样式放置到对应的单元格中
          cell.setCellStyle(cellStyle);
          cell.setCellValue((String) fieldName.get(j));//为标题中的单元格设置值
        } else {
          cell.setCellValue("-");
        }
      }
      XSSFCellStyle dataStyle = workBook.createCellStyle();
      style_data(dataStyle);
      //分页处理excel的数据，遍历所有的结果
      for (int k = 0; k < (rows < SPLIT_COUNT ? rows : SPLIT_COUNT); k++) {
        if (((i - 1) * SPLIT_COUNT + k) >= rows)//如果数据超出总的记录数的时候，就退出循环
          break;
        XSSFRow row = sheet.createRow((short) (k + 2));//创建1行
        //分页处理，获取每页的结果集，并将数据内容放入excel单元格
        ArrayList<String> rowList = (ArrayList<String>) fieldData.get((i - 1) * SPLIT_COUNT + k);
        for (int n = 0; n < rowList.size(); n++) {//遍历某一行的结果
          XSSFCell cell = row.createCell(n);//使用行创建列对象
          cell.setCellStyle(dataStyle);
          if (rowList.get(n) != null) {
            cell.setCellValue((String) rowList.get(n).toString());
          } else {
            cell.setCellValue("");
          }
        }
      }
    }
    return workBook;
  }

  public void expordExcel(OutputStream os) throws Exception {

    try {
      workBook = createWorkbook();
      workBook.write(os);//将excel中的数据写到输出流中，用于文件的输出
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      os.close();
    }
  }

  private void style_head(XSSFCellStyle cellStyle) {
    cellStyle.setAlignment(HorizontalAlignment.CENTER);
    cellStyle.setBorderBottom(BorderStyle.THIN);
    cellStyle.setBorderLeft(BorderStyle.THIN);
    cellStyle.setBorderRight(BorderStyle.THIN);
    cellStyle.setBorderTop(BorderStyle.THIN);
//    cellStyle.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.index);
    cellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.index);
    cellStyle.setFillPattern(FillPatternType.LEAST_DOTS);
  }

  private void style_data(XSSFCellStyle cellStyle) {
    cellStyle.setAlignment(HorizontalAlignment.CENTER);
    cellStyle.setBorderBottom(BorderStyle.THIN);
    cellStyle.setBorderLeft(BorderStyle.THIN);
    cellStyle.setBorderRight(BorderStyle.THIN);
    cellStyle.setBorderTop(BorderStyle.THIN);
  }

  private void style_title(XSSFCellStyle cellStyle) {
    cellStyle.setAlignment(HorizontalAlignment.CENTER);
    cellStyle.setBorderBottom(BorderStyle.THIN);
    cellStyle.setBorderLeft(BorderStyle.THIN);
    cellStyle.setBorderRight(BorderStyle.THIN);
    cellStyle.setBorderTop(BorderStyle.THIN);
    XSSFFont titleFont = workBook.createFont();//创建字体对象
    //字体加粗
//				font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    titleFont.setBold(true);
    titleFont.setFamily(FontFamily.MODERN);
    titleFont.setFontHeightInPoints((short) 15);
    cellStyle.setFont(titleFont);
  }

  private <T> ArrayList<ArrayList<String>> getFormData(String[] fieldNames, List<T> objList, String pattern){
    ArrayList<ArrayList<String>> result = new ArrayList<>();

    // 创建表中数据行-增加样式-赋值
//    Iterator<?> it = objList.iterator();
    for (T t : objList) {
      // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
//      Field[] fields = t.getClass().getDeclaredFields();
//      for (short i = 0; i < fields.length; i++) {
//        Field field = fields[i];
//        String genericString = field.toGenericString();
//        int aFinal = genericString.indexOf("final");
//        if(aFinal == -1){
//          break;
//        }
//        String fieldName = field.getName();
      ArrayList<String> fieldValueList = new ArrayList<>();
      for (String fieldName : fieldNames) {
//        fieldValueList = new ArrayList<>();
        String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        try {
          Class tCls = t.getClass();
          Method getMethod = tCls.getMethod(getMethodName, new Class[]{});
          Object value = getMethod.invoke(t, new Object[]{});
          if(value != null){
            //不为空
            // 如果是时间类型,按照格式转换
            String textValue = null;
            if (value instanceof Date) {
              Date date = (Date) value;
              SimpleDateFormat sdf = new SimpleDateFormat(pattern);
              textValue = sdf.format(date);
            } else {
              // 其它数据类型都当作字符串简单处理
              textValue = value.toString();
            }

            // 利用正则表达式判断textValue是否全部由数字组成
//            if (textValue != null) {
//              Pattern p = Pattern.compile("^\\d+(\\.\\d+)?$");
//              Matcher matcher = p.matcher(textValue);
//              if (matcher.matches()) {
//                // 是数字当作double处理
//              } else {
//                // 不是数字做普通处理
//              }
//            }
            //填上数据
            fieldValueList.add(textValue);
//            OutputStream out = null;
//            try {
//              out = new FileOutputStream(resultUrl);
//              workbook.write(out);
//            } catch (IOException e) {
//              e.printStackTrace();
//            } finally {
//              try {
//                out.close();
//              } catch (IOException e) {
//                e.printStackTrace();
//              }
//            }
          }else{
            //为空
            fieldValueList.add("null");
          }


        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          //清理资源
//          try {
//            workbook.close();
//          } catch (IOException e) {
//            e.printStackTrace();
//          }
        }
      }
      result.add(fieldValueList);
    }
    return result;
  }

}
