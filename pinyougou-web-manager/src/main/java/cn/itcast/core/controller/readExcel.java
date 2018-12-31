package cn.itcast.core.controller;

import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.user.User;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class readExcel {
  public readExcel() {
  }

  public List<Brand> readExcelToBrand(String fileName, InputStream is) throws Exception {
//        InputStream is = new FileInputStream(new File("D:\\code\\phase6\\poiDemo\\src\\main\\java\\b.xls"));
    Workbook hssfWorkbook = null;
//        if (fileName.endsWith("xlsx")){
    hssfWorkbook = new XSSFWorkbook(is);//Excel 2007
//        }else if (fileName.endsWith("xls")){
//            hssfWorkbook = new HSSFWorkbook(is);//Excel 2003
//
//        }
//         HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
//         XSSFWorkbook hssfWorkbook = new XSSFWorkbook(is);
    Brand brand = null;
    List<Brand> list = new ArrayList<Brand>();
    // 循环工作表Sheet
    for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
      //HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
      Sheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
      if (hssfSheet == null) {
        continue;
      }
      // 循环行Row
      for (int rowNum = 2; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
        //HSSFRow hssfRow = hssfSheet.getRow(rowNum);
        Row hssfRow = hssfSheet.getRow(rowNum);
        if (hssfRow != null) {
          brand = new Brand();
          //HSSFCell name = hssfRow.getCell(0);
          //HSSFCell pwd = hssfRow.getCell(1);
          Cell idStr = hssfRow.getCell(0);
          Cell name = hssfRow.getCell(1);
          Cell firstchar = hssfRow.getCell(2);
          Cell birthday = hssfRow.getCell(3);
          //这里是自己的逻辑
          int i = idStr.toString().indexOf(".");
          String id = idStr.toString().substring(0, i);
          brand.setId(Long.parseLong(id));
          brand.setFirstChar(firstchar.toString());
          brand.setName(name.toString());
          //date
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          if(birthday!=null && "".equalsIgnoreCase(birthday.toString()) && !"null".equalsIgnoreCase(birthday.toString())){
            Date date = sdf.parse(birthday.toString());
            brand.setBirthday(date);
          }else{
            brand.setBirthday(null);
          }
          list.add(brand);
        }
      }
    }
    System.out.println(list);
    return list;

  }
//    /**
//     * 类目ID
//     */
//    private Long id;
//
//    /**
//     * 父类目ID=0时，代表的是一级的类目
//     */
//    private Long parentId;
//
//    /**
//     * 类目名称
//     */
//    private String name;
//
//    /**
//     * 类型id
//     */
//    private Long typeId;
  public List<ItemCat> readExcelToItemCat(String fileName, InputStream is) throws Exception {
//        InputStream is = new FileInputStream(new File("D:\\code\\phase6\\poiDemo\\src\\main\\java\\b.xls"));
    Workbook hssfWorkbook = null;
//        if (fileName.endsWith("xlsx")){
    hssfWorkbook = new XSSFWorkbook(is);//Excel 2007
//        }else if (fileName.endsWith("xls")){
//            hssfWorkbook = new HSSFWorkbook(is);//Excel 2003
//
//        }
//         HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
//         XSSFWorkbook hssfWorkbook = new XSSFWorkbook(is);
    ItemCat itemCat = null;
    List<ItemCat> list = new ArrayList<ItemCat>();
    // 循环工作表Sheet
    for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
      //HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
      Sheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
      if (hssfSheet == null) {
        continue;
      }
      // 循环行Row
      for (int rowNum = 2; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
        //HSSFRow hssfRow = hssfSheet.getRow(rowNum);
        Row hssfRow = hssfSheet.getRow(rowNum);
        if (hssfRow != null) {
          itemCat = new ItemCat();
          //HSSFCell name = hssfRow.getCell(0);
          //HSSFCell pwd = hssfRow.getCell(1);
          Cell idStr = hssfRow.getCell(0);
          Cell parentId = hssfRow.getCell(1);
          Cell name = hssfRow.getCell(2);
          Cell tid = hssfRow.getCell(3);
          //这里是自己的逻辑

          String id = getLongStr(idStr.toString());
          itemCat.setId(Long.parseLong(id));
          String pid = getLongStr(parentId.toString());
          itemCat.setParentId(Long.parseLong(pid));
          itemCat.setName(name.toString());
          String tid_1 = getLongStr(tid.toString());
          itemCat.setParentId(Long.parseLong(tid_1));

          list.add(itemCat);
        }
      }
    }
    System.out.println(list);
    return list;

  }

  private String getLongStr(String s){
    int i = s.toString().indexOf(".");
    return s.toString().substring(0, i);
  }


}
