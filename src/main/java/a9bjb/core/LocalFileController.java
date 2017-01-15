package a9bjb.core;

import java.io.File;
import java.util.ArrayList;

public class LocalFileController {
	public String makeLocalDirectory(String directoryPath){
		File temp = new File(directoryPath);
		String parentDir = null;
		if(temp.isDirectory()){
			parentDir = new File(directoryPath).getAbsolutePath();
		} else {
			parentDir = new File(directoryPath).getParent();
		}
				
		File theDir = new File(parentDir+"/hatchery_output");
		System.out.println("Created parentDir : " + parentDir+"/hatchery_output");

		// if the directory does not exist, create it
		if (!theDir.exists()) {
		    System.out.println("creating directory: " + directoryPath);
		    theDir.mkdir();
	        System.out.println("DIR created");  
		}
		
		return parentDir;
	}
	
	public ArrayList<String> getLocalFileList(String path){
		System.out.println("LocalFileController : " + path);
		ArrayList<String> fileList = new ArrayList<String>();
		
		File Directory = new File(path);
		File[] DirectoryList = Directory.listFiles();
		if(DirectoryList.length > 0){
		    for(int i=0; i < DirectoryList.length; i++){
		    	if(DirectoryList[i].isFile()){
		    		System.out.println(DirectoryList[i].getName());
			        fileList.add(0, DirectoryList[i].getName());			       
		    	}
		    }
		} else {
			System.out.println("List is empty");
		}
		
		return fileList;
	}
	
	public boolean checkDir(String path){
		File file = new File(path);
		return file.isDirectory();
	}
}
