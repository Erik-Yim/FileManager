package cn.itcast.file.test;

import java.io.File;

import org.junit.Test;

import cn.itcast.file.utils.SplitFileUtils;

public class SplitMergeFileTest {

	@Test
	public void testSplitMergeFileTest() {
		//File f = new File("D:\\Developer\\CentOS-7-x86_64-DVD-1511.iso") ;
		//SplitFileUtils.splitFile(f,"D:\\demo");
		
		SplitFileUtils.mergeFile(new File("D:\\demo"));
	}
}
