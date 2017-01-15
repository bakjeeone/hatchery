package a9bjb.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.*;
import java.util.*;

import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import a9bjb.wrapper.JoinInfo;


public class StringParser {
	
	boolean isDir = false;
	
	static{
		URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
	}
	public HashMap<String, String> getHTMLComponant(String inputData) throws FileNotFoundException { //0:hdfs or fs?, 1:address and port, 2:specific location
		HashMap<String, String> temp = new HashMap<>();
		JSONParser jp = new JSONParser();
		
		URL inputPathURL;	
		JSONObject jo = null;
		try {
			jo = (JSONObject) jp.parse(inputData);
		} catch (ParseException e1) {
			System.out.println("JSON Parse Err");
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if((boolean) jo.get("hiveIsUse")){
			temp.put("hiveIsUse", "true");
		} else {
			temp.put("hiveIsUse", "false");
		}
		

		try {
			inputPathURL = new URL((String) jo.get("inputPath"));
			temp.put("protocal", inputPathURL.getProtocol());
			temp.put("authority", inputPathURL.getAuthority());
			temp.put("path", inputPathURL.getPath());
			temp.put("host", inputPathURL.getHost());			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			File f = new File((String) jo.get("inputPath"));
		    // 파일 존재 여부 판단
		    if (f.exists()) {
	    		System.out.println("File exist!");
				temp.put("path", (String) jo.get("inputPath"));			
		    } else {
		    	System.out.println("File not exist!");
		    	if(temp.get("hiveIsUse").equals("false")){
		    		throw new FileNotFoundException();
		    	} else {
					System.out.println("gogogo1");
		    		temp.put("path", (String) jo.get("inputPath"));
		    	}
		    }
		}
		
		if(temp.get("hiveIsUse").equals("true")){
			temp.put("DB_URL", (String) jo.get("DB_URL"));
			temp.put("hiveMetastorePath", (String) jo.get("hiveMetastorePath"));
			temp.put("defaultFs", (String) jo.get("defaultFs"));
		} 
		
		for(String values : temp.keySet()){
			System.out.println("key : " + values);
		}
		
		for(String values : temp.values()){
			System.out.println("inputDatas : " + values);
		}
		
		return temp;
	}

	
	public ArrayList<String> getClusterDatas(String inputVal){
		ArrayList<String> result = new ArrayList<String>();
		JSONParser jp = new JSONParser();
		try {
			JSONArray ja = (JSONArray) jp.parse(inputVal);
			for(int i = 0; i < ja.size(); i++){
				JSONObject jo = (JSONObject)ja.get(i);

				result.add(jo.get("URL").toString());
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return result;
	}
	
	public ArrayList<JoinInfo> getJoinDatas(String inputVal){
		ArrayList<JoinInfo> result = new ArrayList<JoinInfo>();
		JSONParser jp = new JSONParser();
		try {
			JSONArray ja = (JSONArray) jp.parse(inputVal);

			for(int i = 0; i < ja.size(); i++){
				JSONObject jo = (JSONObject)ja.get(i);
				JoinInfo tmp = new JoinInfo(jo.get("File name 1").toString(),jo.get("File name 2").toString(),jo.get("on").toString());
				result.add(tmp);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return result;
	}
	
	public boolean checkIsJSONFile(String rawFilePath){
		boolean result = false;
		int pos = rawFilePath.lastIndexOf( "." );
		String ext = rawFilePath.substring( pos + 1 );
		if(ext.equalsIgnoreCase("json")) result = true;
		return result;
	}
	@SuppressWarnings("unchecked")
	public String makeJSONString(ArrayList<String> columns, ArrayList<String> datas){
		JSONParser jp = new JSONParser();
		JSONObject root = new JSONObject();
		for(int i = 0; i < columns.size(); i ++){
			JSONArray ja;
			JSONObject jo;
			try {
				ja = (JSONArray) jp.parse(datas.get(i));
				root.put(columns.get(i), ja);
			} catch (Exception e) {
				try {
					jo = (JSONObject) jp.parse(datas.get(i));
					root.put(columns.get(i), jo);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					root.put(columns.get(i), datas.get(i));
				}
			}
		}
		return root.toJSONString();
	}
	
	@SuppressWarnings("unchecked")	
	public String makeTempTableJSON(ArrayList<String> columns, ArrayList<String> datas){
		JSONObject root = new JSONObject();
		JSONArray columnsJA = new JSONArray();
		for(int i = 0; i < columns.size(); i++){
			columnsJA.add(columns.get(i));
		}
		root.put("columnNames", columnsJA);
		JSONArray datasJA = new JSONArray();
		for(int i = 0; i < datas.size(); i++){
			datasJA.add(datas.get(i));
		}
		root.put("columnDatas", datas);
		return root.toJSONString();
	}
}
