package cn.itcast.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.csource.common.MyException;
import org.junit.Test;
// https://zhidao.baidu.com/question/682930406281613972.html
// http://www.tuicool.com/articles/M7ZvyuR
public class FastDFSTest {

	/**
	 *  testFileUpload:(文件上传测试). 
	 *  @return_type:void
	 *  @author zhangtian  
	 *  @throws Exception
	 */
	@Test
	public void testFileUpload() throws Exception {
		InputStream in = new FileInputStream(new File("C:\\Users\\zhangtian\\Desktop\\pic\\CgAAilT0FFyALxv6AA2dv2DHoO4199.gif")) ;
		Map<String, String> result = FastDFSClient.fileUpload(in, "我的测试文件.gif", null) ;
		System.out.println(result);
	}
	
	/**
	 *  testFileUpload1:(测试加文件描述符). 
	 *  @return_type:void
	 *  @author zhangtian  
	 *  @throws Exception
	 */
	@Test
	public void testFileUpload1() throws Exception {
		File file = new File("C:\\Users\\zhangtian\\Desktop\\pic\\CgAAilT0FFyALxv6AA2dv2DHoO4199.gif") ;
		Map<String,String> metaList = new HashMap<String, String>();
		metaList.put("width","1024");
        metaList.put("height","768");
        metaList.put("author",URLEncoder.encode("张田", "UTF-8"));
        metaList.put("date","20161018");
        String fieldId = FastDFSClient.fileUpload1(file, "测试.gif", metaList) ;
		System.out.println(fieldId);
	}
	
	/**
	 *  testGetFileMetadata:(测试获取文件描述符). 
	 *  @return_type:void
	 *  @author zhangtian
	 * @throws UnsupportedEncodingException 
	 */
	@Test
	public void testGetFileMetadata() throws UnsupportedEncodingException {
		Map<String, String> metaList = FastDFSClient.getFileMetadata("group2/M00/00/00/rBDIvVhh0weEVhxrAAAAAGDHoO4251.gif") ;
		for (Iterator<Map.Entry<String,String>>  iterator = metaList.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String,String> entry = iterator.next();
            String name = entry.getKey();
            String value = entry.getValue();
            if(name.equals("author")) {
            	System.out.println(name + " = " + URLDecoder.decode(value, "UTF-8"));
            } else {
            	System.out.println(name + " = " + value );
            }
        }
	}
	
	/**
	 *  testDownloadFile:(文件下载测试). 
	 *  @return_type:void
	 *  @author zhangtian
	 * @throws MyException 
	 * @throws IOException 
	 */
	@Test
	public void testDownloadFile() throws IOException, MyException {
		OutputStream output = new FileOutputStream(new File("C:\\Users\\zhangtian\\Desktop\\zhangtian.gif")) ;
		byte[] b = FastDFSClient.fileDownLoad("group2", "M00/00/00/rBDIvVhg066Aa1v_AA2dv2DHoO4168.gif") ;
		IOUtils.write(b, output);
	}
	
	/**
	 *  testDeleteFile:(文件删除测试). 
	 *  @return_type:void
	 *  @author zhangtian
	 * @throws MyException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testDeleteFile() throws FileNotFoundException, IOException, MyException {
		// int r = FastDFSClient.fileDelete("group2", "M00/00/00/rBDIvVhg066Aa1v_AA2dv2DHoO4168.gif");
		int r = FastDFSClient.fileDelete1("group2/M00/00/00/rBDIvVhhzfOETMT0AAAAAGDHoO4713.gif");
		System.out.println(r == 0 ? "删除成功" : "删除失败");
	}
}
