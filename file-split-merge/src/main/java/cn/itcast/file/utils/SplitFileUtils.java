package cn.itcast.file.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * ClassName: SplitFileUtils  
 * (大文件切割以及合并)
 * @author zhangtian  
 * @version
 */
public class SplitFileUtils {
	private static final int SIZE = 500 * 1024 * 1024;// 定义单个文件的大小这里采用300M
	
	public static void splitFile(File file, String saveDir) {
		FileInputStream fs = null ;
		FileOutputStream fo = null ;
		try {
			fs = new FileInputStream(file) ;
			// 定义缓冲区
			byte[] b = new byte[SIZE] ;
			int len = 0; 
			int count = 0 ;
			
			/**
			 * 切割文件时，记录 切割文件的名称和切割的子文件个数以方便合并
			 * 这个信息为了简单描述，使用键值对的方式，用到了properties对象
			*/
			Properties pro = new Properties();
			// 定义输出的文件夹路径
			File dir = new File(saveDir);
			// 判断文件夹是否存在，不存在则创建
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			// 切割文件
			while((len = fs.read(b)) != -1) {
				fo = new FileOutputStream(new File(dir, (count++)+".dat")) ;
				fo.write(b, 0, len);
				fo.close();
			}
			
			// 将被切割的文件信息保存到properties中
			pro.setProperty("partCount", count + "");
			pro.setProperty("fileName", file.getName());
			fo = new FileOutputStream(new File(dir, (count++) + ".properties"));
			// 写入properties文件
			pro.store(fo, "save file info");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(fs != null) {
				try {
					fs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(fo != null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 *  mergeFile:(文件合并). 
	 *  @return_type:void
	 *  @author zhangtian 
	 *  @param dir
	 * @throws Exception 
	 */
	public static void mergeFile(File dir) throws RuntimeException {
		// 读取properties文件的拆分信息
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".properties");
			}
		}) ;
		
		File file = files[0] ;
		
		// 获取该文件的信息
		Properties pro = new Properties();
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			pro.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String fileName = pro.getProperty("fileName");
		int splitCount = Integer.valueOf(pro.getProperty("partCount"));
		if(files.length != 1) {
			throw new RuntimeException(dir + ",该目录下没有解析的properties文件或不唯一");
		}
		
		// 获取该目录下所有的碎片文件
		File[] partFiles = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".dat");
			}
		}) ;
		
		// 将碎片文件存入到集合中
		List<FileInputStream> al = new ArrayList<FileInputStream>();
		for (int i = 0; i < splitCount; i++) {
			try {
				al.add(new FileInputStream(partFiles[i]));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		SequenceInputStream sis = null ;
		FileOutputStream fos = null ;
		try {
			// 构建文件流集合
			Enumeration<FileInputStream> en = Collections.enumeration(al) ;
			// 将多个流合成序列流
			sis = new SequenceInputStream(en);
			fos = new FileOutputStream(new File(dir,fileName)) ;
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = sis.read(b)) != -1) {
				fos.write(b, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(sis != null) {
				try {
					sis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
