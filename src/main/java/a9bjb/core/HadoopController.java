package a9bjb.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HadoopController {
	Configuration conf;
	FileSystem hdfs;
	public void makeHDFSDirectory(String inputPath) throws IOException{
		System.out.println("make hatchery_output in HDFS : " + inputPath);
		conf = new Configuration();
		conf.set("fs.defaultFS", inputPath);
		FileSystem hdfs = FileSystem.get(conf);		
		Path path = new Path(inputPath);
		hdfs.mkdirs(path);
	}
	public ArrayList<String> getHDFSFileList(String inputPath) throws FileNotFoundException,IOException{

		ArrayList<String> fileList = new ArrayList<String>();
		
		Configuration Dir_conf = new Configuration();
		Dir_conf.set("fs.defaultFS", inputPath);
		FileSystem Dir = FileSystem.get(conf);
		FileStatus[] Dir_List = Dir.listStatus(new Path(inputPath));
		for(int i=0; i<Dir_List.length;i++){
			if(Dir_List[i].isFile()){
				fileList.add(Dir_List[i].getPath().getName().toString());
			}
		}
		return fileList;
	}
	
	public boolean checkDir(String inputpath) throws IOException{
		Configuration Dir_conf = new Configuration();
		Dir_conf.set("fs.defaultFS", inputpath);
		FileSystem file = FileSystem.get(conf);
		Path path = new Path(inputpath);
		return file.isDirectory(path);
	}
}