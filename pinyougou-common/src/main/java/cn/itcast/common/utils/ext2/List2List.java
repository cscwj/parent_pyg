package cn.itcast.common.utils.ext2;

//Created by Wang on itCast.  

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Wang
 * @version 1.0
 * -description step :
 * -date        2018-12-30--11:25 @_@ ~~
 */

public class List2List {
  public static <T> ArrayList<ArrayList<String>> getFormData(String[] fieldNames, List<T> objList, String pattern){
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
