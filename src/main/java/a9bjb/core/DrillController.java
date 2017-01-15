package a9bjb.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.apache.drill.jdbc.Driver;

import a9bjb.wrapper.JoinInfo;

public class DrillController {
	
	/* Drill JDBC Uri for local/cluster zookeeper */
	public static String DRILL_JDBC_LOCAL_URI = "initial Val";

	/* Sample query used by Drill */
	public static String DRILL_GET_COLUMNS_QUERY;
	
	//make file From Query
	public static String makeFileFromQuery;
	
	//HDFS or normal file system
	private int FS_TYPE;
	private final int LOCAL = 0;
	private final int HDFS = 1;
	private final int HIVE = 2;
	private String localDir;
	
	
	//DrillConnection
	private Connection con = null;
	Statement stmt;

	//input Data
	private String rawFilePath;
	private HashMap<String, String> inputDatas;
	
	//Controller Object
	private HadoopController hc;
	private LocalFileController lc;
	
	//Counstructor Function
	public DrillController(){
		hc = new HadoopController();
		lc = new LocalFileController();
	}
	
	//isDir, isJson
	private boolean isDir;
	
	//JDBC URI 셋팅
	public void setJDBC_URI(ArrayList<String> clusterData, String clusterType){
		if(clusterType.equals("Drillbit")){
			DRILL_JDBC_LOCAL_URI = "jdbc:drill:drillbit=";
		} else {
			DRILL_JDBC_LOCAL_URI = "jdbc:drill:zk=";
		}		
		for(int i = 0; i < clusterData.size(); i++){
			if(i == 0){
				DRILL_JDBC_LOCAL_URI += clusterData.get(i);
			} else {
				DRILL_JDBC_LOCAL_URI += ","+clusterData.get(i);
			}
		}
		System.out.println("JDBC URI : " + DRILL_JDBC_LOCAL_URI);
	}
	//http 리퀘스트를 이용하여 Drill web console로 필요한 Storage configuration을 수행합니다. 
	public void setStorageConfiguration(String connection_information) throws Exception{
		
		System.out.println("connection inform : " + connection_information);
		
		//Drillbit가 실행중인(?) 웹주소. 로컬환경에서 돌아가는것을 가정하였기 때문에 로컬호스트로 고정하였습니다.(DrillEndPoint)
		String url = "http://localhost:8047/storage/hatchery.json";

		URL obj = new URL(url);
		HttpURLConnection httpcon = (HttpURLConnection) obj.openConnection();
		
		httpcon.setRequestProperty("Content-Type", "application/json");
		httpcon.setDoOutput(true);
		httpcon.setRequestMethod("POST");
		String hatchery_output = null;
		String data = null;
		String configName = "hatchery";
		OutputStreamWriter out = new OutputStreamWriter(httpcon.getOutputStream());
		
		if(FS_TYPE == HDFS){
			hatchery_output = "/hatchery_output";
		} else if(FS_TYPE == LOCAL){
			System.out.println("setStorageConfig local dir : " + localDir+"/hatchery_output");
			hatchery_output = localDir+"/hatchery_output";
		} else if(FS_TYPE == HIVE){
			hatchery_output = "/hatchery_output";
			configName = "hatchery_hive";
			url = "http://localhost:8047/storage/hatchery_hive.json";
		}
		
		data = "{\"name\":\""+configName+"\", "
					+ "\"config\": "
								+ "{\"type\": \"file\", "
								+ "\"enabled\": true, "
								+ "\"connection\": \""+connection_information+"\", "
								+ "\"workspaces\": { "
									+ "\"root\": { "
													+ "\"location\": \"/\", "
													+ "\"writable\": false, "
													+ "\"defaultInputFormat\": null},"
									+ "\"hatchery_output\": { "
													+ "\"location\": \"" + hatchery_output + "\", "
													+ "\"writable\": true,"
													+ " \"defaultInputFormat\": null}},"
					+ " \"formats\": {\"csv\":{\"type\":\"text\",\"extensions\":[\"csv\"],\"delimiter\":\",\",\"extractHeader\":true},\"json\":{\"type\":\"json\"},\"parquet\":{\"type\":\"parquet\"}}"
					+ "}}";
		out.write(data);
		out.close();

		
		StringBuilder sb = new StringBuilder();  
		int HttpResult = httpcon.getResponseCode(); 
		if (HttpResult == HttpURLConnection.HTTP_OK) {
		    BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream(), "utf-8"));
		    String line = null;  
		    while ((line = br.readLine()) != null) {  
		        sb.append(line + "\n");  
		    }
		    br.close();
		    System.out.println("drill web console : " + sb.toString());  
		} else {
		    System.out.println("drill web console(failed) : " + httpcon.getResponseMessage() + ", " + httpcon.getResponseCode());  
		}  
		
		
		if(inputDatas.get("hiveIsUse").equals("true")){
			System.out.println("Hive Config");
			url = "http://localhost:8047/storage/hatchery.json";
			obj = new URL(url);
			httpcon = (HttpURLConnection) obj.openConnection();			
			httpcon.setRequestProperty("Content-Type", "application/json");
			httpcon.setDoOutput(true);
			httpcon.setRequestMethod("POST");			
			out = new OutputStreamWriter(httpcon.getOutputStream());
			
			HiveController hvc = new HiveController();
		
			String hivedata = "{\"name\":\"hatchery\", "
					+ "\"config\": "
								+ "{\"type\": \"hive\", "
								+ "\"enabled\": true, "
								+ hvc.NoPort_Config(inputDatas.get("DB_URL"), inputDatas.get("hiveMetastorePath"), inputDatas.get("defaultFs"))								
					+ "}}";
			
			System.out.println(hivedata);	
			out.write(hivedata);
			out.close();
			
			sb = new StringBuilder();  
			HttpResult = httpcon.getResponseCode(); 
			if (HttpResult == HttpURLConnection.HTTP_OK) {
			    BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream(), "utf-8"));
			    String line = null;  
			    while ((line = br.readLine()) != null) {  
			        sb.append(line + "\n");  
			    }
			    br.close();
			    System.out.println("drill web console(hive) : " + sb.toString());  
			} else {
			    System.out.println("drill web console(failed) : " + httpcon.getResponseMessage() + ", " + httpcon.getResponseCode());  
			} 
		}			
	}
	
	//JDBC를 실제로 연결하는 파트입니다.
	public void connectDrillJDBC(HashMap<String, String> Datas) throws Exception{
		System.out.println("connectDrillJDBC");
		inputDatas = Datas;
						
		//Local인지, HDFS 인지.
		if(!inputDatas.containsKey("protocal")){
			if(inputDatas.get("hiveIsUse").equals("true")){
				FS_TYPE = HIVE;				
			} else {
				FS_TYPE = LOCAL;	
			}		
		} else {
			FS_TYPE = HDFS;
		}
		rawFilePath = inputDatas.get("path");
		
		//Drill 에 연결.
		con = new Driver().connect(DRILL_JDBC_LOCAL_URI, getDefaultProperties());
		//Statement 생성.
		stmt = con.createStatement();

		//HDFS안에 hatchery_output 폴더를 생성합니다.
		//인풋으로 받은 HDFS 네임노드 주소를 바탕으로 Drill Storage configuraion을 수행합니다.
		if(FS_TYPE == LOCAL){
			localDir = lc.makeLocalDirectory(rawFilePath);
			isDir = lc.checkDir(rawFilePath);
			setStorageConfiguration("file:///");
		} else if(FS_TYPE == HDFS) {
			hc.makeHDFSDirectory(inputDatas.get("protocal")+"://"+inputDatas.get("authority")+"/hatchery_output");	//hdfs://localhost:9000/hatchery_output
			isDir = hc.checkDir(rawFilePath);
			setStorageConfiguration(inputDatas.get("protocal")+"://"+inputDatas.get("authority"));
		} else if(FS_TYPE == HIVE) {
			if(inputDatas.get("defaultFs").charAt(inputDatas.get("defaultFs").length() - 1) == '/'){
				hc.makeHDFSDirectory(inputDatas.get("defaultFs")+"hatchery_output");	//hdfs://localhost:9000/hatchery_output
			} else {
				hc.makeHDFSDirectory(inputDatas.get("defaultFs")+"/hatchery_output");	//hdfs://localhost:9000/hatchery_output
			}
			setStorageConfiguration(inputDatas.get("defaultFs"));
		}
						
		System.out.println("jdbc connect complete");
		
	}
	
	public boolean getIsDir() {
		return isDir;
	}
	
	public String getRawFilePath(){
		return rawFilePath;
	}
	
	public void makeGetColumnsQuery(ArrayList<JoinInfo> joinInfos){
		//젬지 코드.
		if(rawFilePath.charAt(rawFilePath.length() - 1) != '/'){
			rawFilePath += "/";
		}
		DRILL_GET_COLUMNS_QUERY = Make_Join_Q(joinInfos);
		System.out.println(DRILL_GET_COLUMNS_QUERY);
	}
	
	public void makeGetColumnsQuery(){
		DRILL_GET_COLUMNS_QUERY  = "select * from hatchery." + "`" + rawFilePath + "` limit 1";
		makeFileFromQuery = "hatchery." + "`" + rawFilePath + "`";
		System.out.println(DRILL_GET_COLUMNS_QUERY);
	}
	
	public void setMakeFileFromQuery_json(String query){
		makeFileFromQuery = "("+query+")";
		makeFileFromQuery = makeFileFromQuery.replace("limit 1", "");
	}
///////////////////////////////////Columns을 구하기 위한 쿼리를 만들기 위한 작업입니다.////////////////////////////////////
	public String Make_Join_Q(ArrayList<JoinInfo> join_info){
		String ret = "select * from ";
		String from = "";
		String on = "";
		String temp="";
		
		ArrayList<String> usedTable = new ArrayList<String>();
		
		for(int i = 0; i < join_info.size(); i++){
			if(join_info.size()==1){
				makeFileFromQuery = Single_Q(join_info.get(0));
				ret += makeFileFromQuery;
				return ret;
			}else{
				if(i==0){
					temp = Single_Q(join_info.get(i));
					usedTable.add(join_info.get(i).getFilename1());
					usedTable.add(join_info.get(i).getFilename2());
				}else{
					String filename1 = join_info.get(i).getFilename1();
					String filename2 = join_info.get(i).getFilename2();
					String fileTable1 = filename1.replaceAll("[.]", "");
					String fileTable2 = filename2.replaceAll("[.]", "");					
					if(usedTable.contains(filename1)){
						from = temp + " join hatchery.`"+ rawFilePath +filename2+"` as " + fileTable2 + " ";
					} else {
						from = temp + " join hatchery.`"+ rawFilePath +filename1+"` as " + fileTable1 + " ";
					}					
					on = "on "+fileTable1+"."+join_info.get(i).getColumnName()+" = "+fileTable2+"."+join_info.get(i).getColumnName()+ " ";
					temp = from + on;
					ret = "select * from ";

				}
			}
			ret += temp;
		}
		makeFileFromQuery = ret.substring(13);
		return ret;
	}
	public String Single_Q(JoinInfo join_info){
		
		String ret = "";
		String on ="";
		String filename1 = join_info.getFilename1();
		String filename2 = join_info.getFilename2();
		String condition = join_info.getColumnName();
		String fileTable1 = filename1.replaceAll("[.]", "");
		String fileTable2 = filename2.replaceAll("[.]", "");
		
		on += "hatchery.`" + rawFilePath + filename1 + "` as "+fileTable1+" join " + "hatchery.`" + rawFilePath + filename2 + "` as "+fileTable2+" on " + fileTable1 + "." +condition+ " = "+fileTable2+"."+condition;
		ret += on;
		return ret;
	}
///////////////////////////////////////////////////////////////////////

	
	public void makeFile(String type, String[] colIdx, String[] colName, String outputName) throws SQLException, IOException{				
		
		System.out.println("endFile type : " + type);		
		System.out.println("output name : " + outputName);		
		
		String makeFileQuery = "create table "+outputName+" as select ";
		for(int i = 0; i < colIdx.length; i++){				
			if(i == colIdx.length - 1){
				makeFileQuery += colName[i] +" ";
			} else {
				makeFileQuery += colName[i] +", ";
			}
			System.out.println("make File colName : " + colName[i]);
		}
		makeFileQuery += "from " + makeFileFromQuery;
		
		System.out.println("makeFile QUERY : " + makeFileQuery);
		if(FS_TYPE == HIVE){
			stmt.execute("use hatchery_hive.hatchery_output");
		} else {
			stmt.execute("use hatchery.hatchery_output");	
		}

		System.out.println("alter session set `store.format`='"+type+"'");
		stmt.execute("alter session set `store.format`='"+type+"'");
		
		stmt.execute(makeFileQuery);			
		
	}
	
	public ArrayList<String> getColumnList() throws Exception{
		
		ArrayList<String> columnList = new ArrayList<String>();
    
		System.out.println(DRILL_GET_COLUMNS_QUERY);
		ResultSet rs = stmt.executeQuery(DRILL_GET_COLUMNS_QUERY);
		//ResultSetMetaData를 이용하여 컬럼정보를 가져옵니다.
		ResultSetMetaData md = rs.getMetaData();

		for (int i = 1; i <= md.getColumnCount(); i++) {
			System.out.print(md.getColumnName(i));
			System.out.print('\t');
			columnList.add(md.getColumnName(i));
		}	
		
		return columnList;
	}
		
	public ArrayList<String> getFirstColumnData() throws Exception{
		
		
		ArrayList<String> firstColumnData = new ArrayList<String>();
    
		System.out.println(DRILL_GET_COLUMNS_QUERY);
		ResultSet rs = stmt.executeQuery(DRILL_GET_COLUMNS_QUERY);
		ResultSetMetaData md = rs.getMetaData();
		while(rs.next()){ //limit 1이기 때문에 한번만 돔.
			for (int i = 1; i <= md.getColumnCount(); i++) {
				System.out.print(rs.getString(i));
				System.out.print('\t');
				firstColumnData.add(rs.getString(i));
			}
		}									
		return firstColumnData;
	}
	
	public void setDrillGetColumnQuery(String input){
		DRILL_GET_COLUMNS_QUERY = input;
	}
	
	public String getDrillGetColumnQuery(){
		return DRILL_GET_COLUMNS_QUERY;
	}
	
	
	public ArrayList<String> getFileList() throws Exception{
		ArrayList<String> fileList = new ArrayList<String>();
		System.out.println("file Path for list : " + rawFilePath);
		if(FS_TYPE == LOCAL){
			fileList = lc.getLocalFileList(rawFilePath);
		} else if (FS_TYPE == HDFS){
			fileList = hc.getHDFSFileList(rawFilePath);
		}
		
		return fileList;
	}
	
	public static Properties getDefaultProperties() {
		final Properties properties = new Properties();
		//Drill 프로퍼티를 설정합니다.
//		properties.setProperty(ExecConstants.HTTP_ENABLE, "false");
		return properties;
	}
}
