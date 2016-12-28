package cn.itcast.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class FastDFSClient {

	/** fdfs初始化文件路径 **/
	private static Resource r = null ;
	private static StorageClient1 storageClient1 = null;
	
	private FastDFSClient(){
		
	}
	
	static {
		try {
			r = new ClassPathResource("fdfs_client.properties") ;
			ClientGlobal.init(r.getFile().getAbsolutePath());
			TrackerClient trackerClient = new TrackerClient(ClientGlobal.g_tracker_group);
			TrackerServer trackerServer = trackerClient.getConnection();
			if (trackerServer == null) {
				throw new IllegalStateException("getConnection return null");
			}
			
			StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
			if (storageServer == null) {
				throw new IllegalStateException("getStoreStorage return null");
			}
			
			storageClient1 = new StorageClient1(trackerServer, storageServer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  fileUpload:(文件上传). 
	 *  @return_type:Map<String,String>
	 *  @author zhangtian  
	 *  @param isEmpty
	 *  @param inputStream
	 *  @param fileName
	 *  @return
	 * @throws IOException 
	 * @throws MyException 
	 *  @throws Exception
	 */
	public static Map<String, String> fileUpload(InputStream inputStream, String fileName, Map<String, String> metaList) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			byte[] file_buff = null;
			if (inputStream != null) {
				file_buff = new byte[inputStream.available()];
				inputStream.read(file_buff);
			}
			
			NameValuePair[] nameValuePairs = null;
			if (metaList != null) {
				nameValuePairs = new NameValuePair[metaList.size()];
				int index = 0;
				for (Iterator<Map.Entry<String,String>> iterator = metaList.entrySet().iterator(); iterator.hasNext();) {
					Map.Entry<String,String> entry = iterator.next();
					String name = entry.getKey();
					String value = entry.getValue();
					nameValuePairs[index++] = new NameValuePair(name,value);
				}
			}
			
			String[] results = storageClient1.upload_file(file_buff,
					FilenameUtils.getExtension(fileName), null);
			if (results == null) {
				throw new RuntimeException("文件不存在...");
			}
			map.put("fileType", FilenameUtils.getExtension(fileName));
			map.put("original", fileName);
			map.put("url", results[0] + "/" + results[1]);
			map.put("state", "SUCCESS");
			map.put("groupName", results[0]);
			map.put("fileName", results[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        return map;
	}
	
	/**
	 *  fileUpload1:(文件上传). 
	 *  @return_type:String
	 *  @author zhangtian  
	 *  @param inputStream
	 *  @param fileName
	 *  @param metaList
	 *  @return
	 *  @throws RuntimeException
	 *  @throws IOException
	 *  @throws MyException
	 */
	public static String fileUpload1(File file, String fileName, Map<String, String> metaList) {
		
		InputStream inputStream = null ;
		String fieldId = null ;
        try {
        	inputStream = new FileInputStream(file) ;
        	byte[] file_buff = null;
        	if (inputStream != null) {
        		file_buff = new byte[inputStream.available()];
        		inputStream.read(file_buff);
        	}
        	
        	NameValuePair[] nameValuePairs = null;
        	if (metaList != null) {
        		nameValuePairs = new NameValuePair[metaList.size()];
        		int index = 0;
        		for (Iterator<Map.Entry<String,String>> iterator = metaList.entrySet().iterator(); iterator.hasNext();) {
        			Map.Entry<String,String> entry = iterator.next();
        			String name = entry.getKey();
        			String value = entry.getValue();
        			nameValuePairs[index++] = new NameValuePair(name,value);
        		}
        	}
        	fieldId = storageClient1.upload_appender_file1(file_buff, getFileExt(fileName), nameValuePairs) ;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        
        return fieldId;
	}
	
	/**
	 *  fileDownLoad:(文件下载). 
	 *  @return_type:byte[]
	 *  @author zhangtian  
	 *  @param groupName
	 *  @param fileName
	 *  @return
	 *  @throws IOException
	 *  @throws MyException
	 */
	public static byte[] fileDownLoad(String groupName, String fileName){
		try {
			byte[] fileBytes = storageClient1.download_file(groupName, fileName);
			return fileBytes;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null ;
	}
	
	/**
	 * 
	 *  fileDownLoad1:(下载文件，根据fieldId下载). 
	 *  @return_type:InputStream
	 *  @author zhangtian  
	 *  @param fileId
	 *  @return
	 *  @throws IOException
	 *  @throws MyException
	 */
	public static InputStream fileDownLoad1(String fileId) {
		try {
			byte[] fileBytes = storageClient1.download_file1(fileId);
			InputStream inputStream = new ByteArrayInputStream(fileBytes);
			return inputStream;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null ;
    }
	
	/**
	 *  fileDelete:(文件删除). 
	 *  @return_type:void
	 *  @author zhangtian  
	 *  @param groupName
	 *  @param fileName
	 *  @throws FileNotFoundException
	 *  @throws IOException
	 *  @throws MyException
	 */
	public static int fileDelete(String groupName, String fileName) {
		try {
			int result = storageClient1.delete_file(groupName == null ? "group1" : groupName, fileName);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1 ;
	}
	
	/**
	 *  fileDelete1:(根据fileId来删除一个文件（我们现在用的就是这样的方式，上传文件时直接将fileId保存在了数据库中）). 
	 *  @return_type:int
	 *  @author zhangtian  
	 *  @param fileId
	 *  @return
	 *  @throws FileNotFoundException
	 *  @throws IOException
	 *  @throws MyException
	 */
	public static int fileDelete1(String fileId) {
		try {
			int result = storageClient1.delete_file1(fileId);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1 ;
	}
	
	/**
	 *  getFileMetadata:(获取文件元数据). 
	 *  @return_type:Map<String,String>
	 *  @author zhangtian  
	 *  @param fileId
	 *  @return
	 *  @throws IOException
	 *  @throws MyException
	 */
	public static Map<String, String> getFileMetadata(String fileId) {
		try {
			NameValuePair[] metaList = storageClient1.get_metadata1(fileId);
			if (metaList != null) {
				HashMap<String,String> map = new HashMap<String, String>();
	            for (NameValuePair metaItem : metaList) {
	                map.put(metaItem.getName(),metaItem.getValue());
	            }
	            return map;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null ;
	}
	
	/**
	 * 修改一个已经存在的文件
	 * 
	 * @param oldFileId
	 *            原来旧文件的fileId, file_id源码中的解释file_id the file id(including group name and filename);
	 *            例如 group1/M00/00/00/ooYBAFM6MpmAHM91AAAEgdpiRC0012.xml
	 * @param file
	 *            新文件
	 * @param filePath
	 *            新文件路径
	 * @return 返回空则为失败
	 */
	public static String modifyFile(String oldFileId, File file, String filePath) {
		String fileid = null;
		try {
			// 先上传
			fileid = fileUpload1(file, filePath, null);
			if (fileid == null) {
				return null;
			}
			// 再删除
			int delResult = fileDelete1(oldFileId);
			if (delResult != 0) {
				return null;
			}
		} catch (Exception ex) {
			return null;
		}
		return fileid;
	}
	
	/**
	 * 获取文件后缀名（不带点）.
	 * 
	 * @return 如："jpg" or "".
	 */
	private static String getFileExt(String fileName) {
		if (fileName == null || !fileName.contains(".")) {
			return "";
		} else {
			return fileName.substring(fileName.lastIndexOf(".") + 1); // 不带最后的点
		}
	}
}
