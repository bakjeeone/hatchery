package a9bjb.core;

public class HiveController {
		
	String output = "";
	String Hive_conf_start = "\"configProps\" : {\n";
	String Metastore_URIS = "\"hive.metastore.uris\" : ";
	String ConnectionURL = "\"javax.jdo.option.ConnectionURL\": ";
	String Connection_Driver = "\"javax.jdo.option.ConnectionDriverName\" : ";
	String Warehouse_Dir = "\"hive.metastore.warehouse.dir\" : ";
	String fs_default = "\"fs.default.name\" : ";
	String Node_Cluster = "\"hbase.zookeeper.quorum\" : ";
	String Client_Port = "\"hbase.zookeeper.property.clientPort\" : ";
	String Sals = "\"hive.metastore.sasl.enabled\" : \"false\"";

	public String NoPort_Config(String ConnectionURL_i, String Warehouse_Dir_i, String fs_default_i){
		Metastore_URIS += "\"\",\n";
		ConnectionURL = ConnectionURL + "\"" + ConnectionURL_i + "\",\n";
		Warehouse_Dir = Warehouse_Dir + "\"" + Warehouse_Dir_i + "\",\n";
		fs_default = fs_default + "\"" + fs_default_i + "\",\n";
		output = Hive_conf_start + Metastore_URIS + ConnectionURL + Warehouse_Dir + fs_default + Sals + "\n}";
		return output;
	}
	
//	public void Remote_Config_Using_HbaseStorageHandler(){
//		//need zk node info
//		System.out.println("URIS : "); Metastore_URIS = Metastore_URIS + sc.next()+",\n";
//		System.out.println("File system Protocol :// Port name : port # : "); fs_default = fs_default + sc.next()+",\n";
//		System.out.println("Cluster Nodes : "); Node_Cluster = Node_Cluster + sc.next() + ",\n";
//		System.out.println("Client Node : "); Client_Port = Client_Port + sc.next() +"\n}";
//		output = Hive_conf_start + Metastore_URIS + Sals + ",\n" + fs_default + Node_Cluster + Client_Port;
//		System.out.println(output);
//	}
//	public void Remote_Config_Not_Using_HbaseStorageHandler(){
//		//not need
//		System.out.println("URIS : "); Metastore_URIS = Metastore_URIS + sc.next()+",\n";
//		System.out.println("File system Protocol, Port name and port # : "); fs_default = fs_default + sc.next()+"\n}";
//		output = Hive_conf_start + Metastore_URIS + Sals + ",\n" + fs_default;
//		System.out.println(output);
//	}
//	public void Cluster_Config(){
//		
//	}
}

//{
//	  "type": "hive",
//	  "enabled": true,
//	  "configProps": {
//	    "hive.metastore.uris": "",
//	    "javax.jdo.option.ConnectionURL": "jdbc:derby:;databaseName=/usr/local/hadoop/hive/metastore_db;create=true",
//	    "hive.metastore.warehouse.dir": "/user/hive/warehouse",
//	    "fs.default.name": "file:///",
//	    "hive.metastore.sasl.enabled": "false"
//	  }
//	}

//
//{
//    "type": "hive",
//    "enabled": false,
//    "configProps": {
//    "hive.metastore.uris": "thrift://hdfs41:9083",
//    "hive.metastore.sasl.enabled": "false",
//    "fs.default.name": "hdfs://10.10.10.41/".
//    "hbase.zookeeper.quorum": "zkhost1,zkhost2,zkhost3", => 여기부터 2개는 hbase 여부에 따라 다
//	  "hbase.zookeeper.property.clientPort:" "2181"		
//}
//  }