package cn.itcast.common.utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.formula.functions.T;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class FastDFSClient {

  private TrackerClient trackerClient = null;
  private TrackerServer trackerServer = null;
  private StorageServer storageServer = null;
  private StorageClient1 storageClient = null;


  //                    classpath:fastDFS/fdfs_client.conf
  public FastDFSClient(String conf) throws Exception {
    if (conf.contains("classpath:")) {
      conf = conf.replace("classpath:", this.getClass().getResource("/").getPath());
    }
    ClientGlobal.init(conf);
    trackerClient = new TrackerClient();
    trackerServer = trackerClient.getConnection();
    storageServer = null;
    storageClient = new StorageClient1(trackerServer, storageServer);
  }

  /**
   * 上传文件方法
   * <p>Title: uploadFile</p>
   * <p>Description: </p>
   *
   * @param fileName 文件全路径
   * @param extName  文件扩展名，不包含（.）
   * @param metas    文件扩展信息
   * @return
   * @throws Exception
   */
  public String uploadFile(String fileName, String extName, NameValuePair[] metas) throws Exception {
    String result = storageClient.upload_file1(fileName, extName, metas);
    return result;
  }

  public String uploadFile(byte[] file, String fileName, long fileSize) throws Exception {
    NameValuePair[] metas = new NameValuePair[3];
    metas[0] = new NameValuePair("fileName", fileName);
    metas[1] = new NameValuePair("fileSize", String.valueOf(fileSize));
    metas[2] = new NameValuePair("fileExt", FilenameUtils.getExtension(fileName));
    String result = storageClient.upload_file1(file, FilenameUtils.getExtension(fileName), metas);
    return result;
  }

  public String uploadFile(String fileName) throws Exception {
    return uploadFile(fileName, null, null);
  }

  public String uploadFile(String fileName, String extName) throws Exception {
    return uploadFile(fileName, extName, null);
  }

  /**
   * 上传文件方法
   * <p>Title: uploadFile</p>
   * <p>Description: </p>
   *
   * @param fileContent 文件的内容，字节数组
   * @param extName     文件扩展名
   * @param metas       文件扩展信息
   * @return
   * @throws Exception
   */
  public String uploadFile(byte[] fileContent, String extName, NameValuePair[] metas) throws Exception {

    String result = storageClient.upload_file1(fileContent, extName, metas);
    return result;
  }

  public String uploadFile(byte[] fileContent) throws Exception {
    return uploadFile(fileContent, null, null);
  }

  public String uploadFile(byte[] fileContent, String extName) throws Exception {
    return uploadFile(fileContent, extName, null);
  }

  /**
   * 下载
   * 王 2018年12月31日00:22:22
   *
   * @param groupName "group1"
   * @param remoteFileName "M00/00/00/wKgRcFV_08OAK_KCAAAA5fm_sy874.conf"
   * @param fos
   * @return
   */
  public boolean downloadFile(String groupName, String remoteFileName, OutputStream fos) {
    try {
      //byte[] b = storageClient.download_file("group1", "M00/00/00/wKgRcFV_08OAK_KCAAAA5fm_sy874.conf");
      byte[] b = this.getBytes(groupName, remoteFileName);
      IOUtils.write(b, fos);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (null != fos) {
          fos.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  /**
   * 获取二进制数组
   * 王-2018年12月31日00:22:47
   *
   * @param groupName
   * @param remoteFileName
   * @return
   */
  public byte[] getBytes(String groupName, String remoteFileName) {
    try {
      byte[] bytes = storageClient.download_file(groupName, remoteFileName);
      return bytes;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 删除文件
   * 王- 2018年12月31日00:27:06
   * @param groupName
   * @param remoteFileName
   * @return
   */
  public boolean deleteRemoteFile(String groupName, String remoteFileName) {
    try {
      int i = storageClient.delete_file(groupName, remoteFileName);
      System.out.println(i == 0 ? "删除成功" : "删除失败:" + i);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

}
