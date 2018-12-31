package cn.itcast.core.controller;

import cn.itcast.common.utils.FastDFSClient;
import entity.Result;
import org.apache.commons.io.FilenameUtils;
import org.csource.fastdfs.ClientGlobal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadController {

    //解决,路径因编码问题,Springmvc.xml加载资源文件到内存,value写的资源文件的key值
    @Value("${FILE_SERVER_URL}")
    private String url;


    //Springmvc接收图片,MultipartFile是一个接口,是InputStreamSource的子类
    //Springmvc.xml配置注入其实现类,才可以上传  formData.append("file",file.files[0]); file 根据前台对应
    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file){
        //原始名称
        //file.getOriginalFilename() /dsfsafafads.jpg  png
        //图片的二进制
        //file.getBytes()
        //.....
        try {
            //使用封装好的工具
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");

            //扩展名,apache提供切割 jpg,..
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());

            //工具类上传图片,提供多种重载方法,path只是不带域名的后段路径
            //url外部引入,解决硬编码问题
            String path = fastDFSClient.uploadFile(file.getBytes(), ext);
            System.out.println(url+path);

            return new Result(true,url+path);
        } catch (Exception e) {
            return new Result(false,"上传失败");
        }

    }

}
