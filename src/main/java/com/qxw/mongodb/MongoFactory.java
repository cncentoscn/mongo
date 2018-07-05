package com.qxw.mongodb;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.qxw.utils.CommonApi;



/**
 * v=15_3.2
 * @author c_abc
 *
 */
public class MongoFactory {
	private static final Logger logger = LoggerFactory.getLogger(MongoFactory.class);
    private static String mongo_host_port=CommonApi.mongo_host_port;;//域名 端口
    private static String user_pass_db=CommonApi.mongo_user_pass_db;
    
    
    private static Map<String,MongoDatabase> mongoDb_map=new HashMap<String, MongoDatabase>();// 数据库连接到
    private static MongoClient mongoClient = null;  
    
    /***
     * 设置mongo参数
     * @param mongo_host_port //域名 端口
     * @param upd  用户名 密码 数据  “:”分隔
     * @return
     */
	public static boolean setMongoConfig(String host_port,String upd){
		if(StringUtils.isEmpty(mongo_host_port)){	
			mongo_host_port=host_port;user_pass_db=upd;
		}
		return false;
	}
    
	 /** 
     * 初始化连接池 
     */  
	 @SuppressWarnings("deprecation")
	private static synchronized void initDBPrompties() {
	    	if(mongoClient==null){
	    		try {
	    			List<MongoCredential> mongoCredential=Collections.<MongoCredential>emptyList();
	    			if(user_pass_db!=null&&!"".equals(user_pass_db)){//arr 0,1,2 用户名  密码  数据名
	        			String[] arr = user_pass_db.split(":");
	        			MongoCredential credential = MongoCredential.createScramSha1Credential(arr[0],arr[2],arr[1].toCharArray());
	        			mongoCredential=new ArrayList<MongoCredential>();
	        			mongoCredential.add(credential);
	        		}
//	    			MongoClientOptions option=new MongoClientOptions.Builder()
//	    				.connectionsPerHost(10).threadsAllowedToBlockForConnectionMultiplier(1000).build();
	    			String[] hostps = mongo_host_port.split(";");
	    			if(hostps.length==1){//只有一个主 副本集
	    				String[] h =hostps[0].split(":");
	    				mongoClient = new MongoClient(new ServerAddress(h[0],Integer.parseInt(h[1])),mongoCredential);
	    			}else{
	    				List<ServerAddress> serverAddress = new ArrayList<ServerAddress>();
	    				for (String hp:hostps) {
	        				String[] h =hp.split(":");
	        				serverAddress.add(new ServerAddress(h[0],Integer.parseInt(h[1])));
	    				}
	    				mongoClient = new MongoClient(serverAddress,mongoCredential);
	    			}
	        		logger.debug("mongoClient 偏好为="+mongoClient.getReadPreference().toString());
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("********mongodb 配置错误");
				}
	    		
	    	}
	    }
    /***
     * 获取数据库 名为shake
     * @return
     */
    public static MongoDatabase getMongoDb(){
    	return getMongoDb("unpo");
    }
    
 
    public static MongoDatabase getMongoDb(String databaseName){
    	if(mongoDb_map.get(databaseName)==null){
    		synchronized (databaseName) {
    			if(mongoDb_map.get(databaseName)==null){
    				if(mongoClient==null){ 	initDBPrompties();  }
    				MongoDatabase mongoDb=mongoClient.getDatabase(databaseName);
            		mongoDb_map.put(databaseName,mongoDb);
    			}
			}
    	}
		return mongoDb_map.get(databaseName);
    }
    
    /***
     * 获取数据连接，自己获取库/collections
     * @return
     */
    public static MongoClient getMongoClient(){
    	if(mongoClient==null){
    		initDBPrompties();
    	}
    	return mongoClient;
    }
}
