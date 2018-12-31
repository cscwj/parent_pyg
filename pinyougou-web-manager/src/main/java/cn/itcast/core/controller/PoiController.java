package cn.itcast.core.controller;

//Created by Wang on itCast.  

import cn.itcast.common.utils.FastDFSClient;
import cn.itcast.common.utils.ext2.ExcelFileGenerator;
import cn.itcast.common.utils.ext2.List2List;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.*;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Wang
 * @version 1.0
 * -description step :
 * -date        2018-12-30--14:44 @_@ ~~
 */
@RestController
@RequestMapping("/poi")
public class PoiController {

  //解决,路径因编码问题,Springmvc.xml加载资源文件到内存,value写的资源文件的key值
  @Value("${FILE_SERVER_URL}")
  private String url;
  @Reference
  private PoiUserService poiUserService;
  @Reference
  private PoiGoodsService poiGoodsService;
  @Reference
  private PoiOrderService poiOrderService;

//  //导出
//  @RequestMapping("/user/aExcelOut")
//  public Result userExcelOut() {
//    String path = "e:/user.xlsx";
//    if (createUserFile(path)) {
//      Result result = uploadFile(path);
//      return result;
//    } else {
//      return new Result(false, "失败");
//    }
//  }

  //导出 + 优化
  @RequestMapping("/user/aExcelOut")
  @ResponseBody
  public void userExcelOut(HttpServletResponse response) {
    try {
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Content-disposition", "attachment;filename=user.xlsx");
      createUserFile(response.getOutputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //导出
//  @RequestMapping("/goods/aExcelOut")
//  public Result goodsExcelOut() {
//    //MultipartFile multipartFile = new MockMultipartFile("", new FileInputStream(""))
//    String path = "e:/goods.xlsx";
//    if (createGoodsFile(path)) {
//      Result result = uploadFile(path);
//      return result;
//    }else {
//      return new Result(false, "失败");
//    }
//  }

  //导出 +优化
  @RequestMapping("/goods/aExcelOut")
  @ResponseBody
  public void goodsExcelOut(HttpServletResponse response) {
    try {
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Content-disposition", "attachment;filename=goods.xlsx");
      createGoodsFile(response.getOutputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //导出 优化
  @RequestMapping("/order/aExcelOut")
  @ResponseBody
  public Result orderExcelOut(HttpServletResponse response) {
//    String path = "e:/orders.xlsx";
    try {
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Content-disposition", "attachment;filename=orders.xlsx");
      boolean b = createOrderFile(response.getOutputStream());

      if (b) {
//        Result result = uploadFile(path);
        return new Result(true, "成功");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new Result(false, "失败");
  }

  //创建文件   //导出优化1
  private boolean createOrderFile(OutputStream os) {
//    OutputStream os = null;
    try {
      Order order = new Order();
      List<Order> orderList = poiOrderService.selectList(order);
      String[] fieldNames = {"orderId", "payment", "paymentType", "postFee", "status", "createTime", "updateTime", "paymentTime", "consignTime", "userId", "buyerNick", "receiverAreaName", "receiverMobile", "receiver", "sourceType", "sellerId"};

      ArrayList<ArrayList<String>> formData = List2List.getFormData(fieldNames, orderList, "yyyy-MM-dd HH:mm:ss");
      ArrayList<String> list = new ArrayList<>();
      list.add("订单id");
      list.add("实付金额");
      list.add("支付类型");
      list.add("邮费");
      list.add("状态");
      list.add("创建时间");
      list.add("更新时间");
      list.add("付款时间");
      list.add("发货时间");
//      list.add("交易完成时间");
//      list.add("交易关闭时间");
//      list.add("物流名称");
//      list.add("物流单号");
      list.add("用户id");
      list.add("买家昵称");
      list.add("收货人地区名称");
      list.add("收货人手机");
      list.add("收货人");
      list.add("订单来源");
      list.add("商家ID");
      ExcelFileGenerator efg = new ExcelFileGenerator("订单数据", list, formData);
      efg.createWorkbook();

//      os = new FileOutputStream(path);
      efg.expordExcel(os);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    } finally {
      if (os != null) {
        try {
          os.flush();
          os.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  //创建文件
  private boolean createGoodsFile(OutputStream os) {
//    OutputStream os = null;
    try {
      Goods goods = new Goods();
      List<Goods> goodsList = poiGoodsService.selectList(goods);
      String[] fieldNames = {"id", "sellerId", "goodsName", "defaultItemId", "auditStatus", "isMarketable", "brandId", "caption", "category1Id", "category2Id", "category3Id", "smallPic", "price", "typeTemplateId", "isEnableSpec", "isDelete"};

      ArrayList<ArrayList<String>> formData = List2List.getFormData(fieldNames, goodsList, "yyyy-MM-dd HH:mm:ss");
      ArrayList<String> list = new ArrayList<>();
      list.add("id");
      list.add("商家ID");
      list.add("SPU名");
      list.add("默认SKU");
      list.add("状态");
      list.add("是否上架");
      list.add("品牌");
      list.add("副标题");
      list.add("一级类目");
      list.add("二级类目");
      list.add("三级类目");
      list.add("小图");
      list.add("商城价");
      list.add("分类模板ID");
      list.add("是否启用规格");
      list.add("是否删除");
      ExcelFileGenerator efg = new ExcelFileGenerator("商品数据", list, formData);
      efg.createWorkbook();

//      os = new FileOutputStream(path);
      efg.expordExcel(os);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    } finally {
      if (os != null) {
        try {
          os.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  //创建文件
  private boolean createUserFile(OutputStream os) {
//    OutputStream os = null;
    try {
      User user = new User();
      List<User> userList = poiUserService.selectByQuery(user);
      String[] fieldNames = {"id", "username", "password", "phone", "email", "created", "updated", "sourceType", "nickName", "name", "status", "headPic", "qq", "accountBalance", "isMobileCheck", "isEmailCheck", "sex", "userLevel", "points", "experienceValue", "birthday", "lastLoginTime"};

      ArrayList<ArrayList<String>> formData = List2List.getFormData(fieldNames, userList, "yyyy-MM-dd HH:mm:ss");
      ArrayList<String> list = new ArrayList<>();
      list.add("id");
      list.add("用户名");
      list.add("密码");
      list.add("电话");
      list.add("邮箱");
      list.add("创建时间");
      list.add("更新时间");
      list.add("来源");
      list.add("昵称");
      list.add("真实姓名");
      list.add("状态");
      list.add("头像");
      list.add("qq");
      list.add("账户余额");
      list.add("手机是否验证");
      list.add("邮箱是否检测");
      list.add("性别");
      list.add("会员等级");
      list.add("积分");
      list.add("经验值");
      list.add("生日");
      list.add("最后登录时间");
      ExcelFileGenerator efg = new ExcelFileGenerator("用户数据", list, formData);
      efg.createWorkbook();

//      os = new FileOutputStream(path);
      efg.expordExcel(os);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    } finally {
      if (os != null) {
        try {
          os.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  //上传到fastfds用户下载
  private Result uploadFile(String path) {
    try {
      //使用封装好的工具
      FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");
      //工具类上传图片,提供多种重载方法,path只是不带域名的后段路径
      //url外部引入,解决硬编码问题
      String fastPath = fastDFSClient.uploadFile(path, "xlsx");
      System.out.println(url + fastPath);

      return new Result(true, url + fastPath);
    } catch (Exception e) {
      return new Result(false, "导出失败!");
    }
  }


// step ------------------------------------------------------------------------------------------------

  @Autowired
  private UploadController uploadController;

  @Reference
  private BrandService brandService;
  //导入品牌
  @RequestMapping("/brand/uploadExcel")
  public Result brand(MultipartFile file) {
    FileOutputStream fos = null;
    FileInputStream fis = null;
    try {
      Result result = uploadController.uploadExcel(file);
      if (result.isFlag()) {
        //成功
        String path = result.getMessage();
        System.out.println(path);

        //使用封装好的工具
        FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");
        //工具类上传图片,提供多种重载方法,path只是不带域名的后段路径
        //group1/M00/00/01/wKjIgFwphIKAWRAwAAAN0B1PWy011.xlsx
        //http://192.168.200.128/group1/M00/00/01/wKjIgFwphIKAWRAwAAAN0B1PWy011.xlsx
        //01234567890123456789012
        // group1
        //"M00/00/00/wKgRcFV_08OAK_KCAAAA5fm_sy874.conf"
        //url外部引入,解决硬编码问题
        String substring = path.substring(23);
        int i = substring.indexOf("/");
        String group = substring.substring(0, i);
        String filename = substring.substring(i + 1);
        fos = new FileOutputStream("e:/u_brand.xlsx");
        boolean b = fastDFSClient.downloadFile(group, filename, fos);
        if (b) {
          fis = new FileInputStream("e:/u_brand.xlsx");
          readExcel readExcel = new readExcel();
          List<Brand> brands = readExcel.readExcelToBrand("u_brand.xlsx", fis);
          brandService.insertAll(brands);
        } else {
          return new Result(false, "解析失败");
        }

        return new Result(true, "上传成功,后台正在解析");
      }
      return result;
    } catch (Exception e) {
      return new Result(false, "上传失败!");
    } finally {
      try {
        if (null!=fos) {
          fos.close();
        }
        if (null!=fis) {
          fis.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }

    }
  }

  //导入规格
  @RequestMapping("/specification/uploadExcel")
  public Result specification(MultipartFile file) {
    return new Result(true, "上传成功");

  }

  //导入模板
  @RequestMapping("/type_template/uploadExcel")
  public Result type_template(MultipartFile file) {
    return new Result(true, "上传成功");

  }

  @Reference
  private ItemCatService itemCatService;

  //导入分类
  @RequestMapping("/item_cat/uploadExcel")
  public Result item_cat(MultipartFile file) {
    FileOutputStream fos = null;
    FileInputStream fis = null;
    try {
      Result result = uploadController.uploadExcel(file);
      if (result.isFlag()) {
        //成功
        String path = result.getMessage();
        System.out.println(path);

        //使用封装好的工具
        FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");
        //工具类上传图片,提供多种重载方法,path只是不带域名的后段路径
        //group1/M00/00/01/wKjIgFwphIKAWRAwAAAN0B1PWy011.xlsx
        //http://192.168.200.128/group1/M00/00/01/wKjIgFwphIKAWRAwAAAN0B1PWy011.xlsx
        //01234567890123456789012
        // group1
        //"M00/00/00/wKgRcFV_08OAK_KCAAAA5fm_sy874.conf"
        //url外部引入,解决硬编码问题
        String substring = path.substring(23);
        int i = substring.indexOf("/");
        String group = substring.substring(0, i);
        String filename = substring.substring(i + 1);
        fos = new FileOutputStream("e:/u_itemCat.xlsx");
        boolean b = fastDFSClient.downloadFile(group, filename, fos);
        if (b) {
          fis = new FileInputStream("e:/u_itemCat.xlsx");
          readExcel readExcel = new readExcel();
          List<ItemCat> itemCatList = readExcel.readExcelToItemCat("u_itemCat.xlsx", fis);
          itemCatService.insertAll(itemCatList);
        } else {
          return new Result(false, "解析失败");
        }

        return new Result(true, "上传成功,后台正在解析");
      }
      return result;
    } catch (Exception e) {
      return new Result(false, "上传失败!");
    } finally {
      try {
        if (null!=fos) {
          fos.close();
        }
        if (null!=fis) {
          fis.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }

    }

  }

}
