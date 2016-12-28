package cn.itcast.test;

import java.io.Serializable;

public class ResourceObj implements Serializable{

	private static final long serialVersionUID = 1L;
	private String name ;
	private String path ;
	private String content ;
	
	public ResourceObj() {
		
	}
	
	public ResourceObj(String name, String path, String content) {
		this.name = name ;
		this.path = path ;
		this.content = content ;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
