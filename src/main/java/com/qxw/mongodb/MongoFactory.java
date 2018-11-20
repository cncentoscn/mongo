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
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoDatabase;
import com.qxw.utils.CommonApi;

/**
 * mongodb工厂类
 * @author qxw
 * @data 2018年11月19日上午10:02:31
 */
public class MongoFactory {
    private static final Logger logger = LoggerFactory.getLogger(MongoFactory.class);
    private static String mongo_host_port = CommonApi.mongo_host_port;
    private static String user_pass_db = CommonApi.mongo_user_pass_db;
    private static Map<String, MongoDatabase> mongoDb_map = new HashMap<String, MongoDatabase>();// 数据库连接到
    private static MongoClient mongoClient = null;

    /***
     * 设置mongo参数
     * @param mongo_host_port //域名 端口
     * @param upd  用户名 密码 数据  “:”分隔
     * @return
     */
    public static boolean setMongoConfig(String host_port, String upd) {
        if (StringUtils.isEmpty(mongo_host_port)) {
            mongo_host_port = host_port;
            user_pass_db = upd;
        }
        return false;
    }

    /**
     * 初始化连接池
     */
    @SuppressWarnings("deprecation")
    private static synchronized void initDBPrompties() {
        if (mongoClient == null) {
            try {
            	List<MongoCredential> mongoCredential=Collections.<MongoCredential>emptyList();
    			if(user_pass_db!=null&&!"".equals(user_pass_db)){//arr 0,1,2 用户名  密码  数据名
        			String[] arr = user_pass_db.split(":");
        			MongoCredential credential = MongoCredential.createScramSha1Credential(arr[0],arr[2],arr[1].toCharArray());
        			mongoCredential=new ArrayList<MongoCredential>();
        			mongoCredential.add(credential);
        		}
    			//.connectionsPerHost(200)
    			MongoClientOptions option=new MongoClientOptions.Builder().threadsAllowedToBlockForConnectionMultiplier(10).build();
                String[] hostps = mongo_host_port.split(";");
                if (hostps.length == 1) {
                	//只有一个主 副本集
                    String[] h = hostps[0].split(":");
                    mongoClient = new MongoClient(new ServerAddress(h[0], Integer.parseInt(h[1])),mongoCredential,option);
                } else {
                    List<ServerAddress> serverAddress = new ArrayList<ServerAddress>();
                    for (String hp : hostps) {
                        String[] h = hp.split(":");
                        serverAddress.add(new ServerAddress(h[0], Integer.parseInt(h[1])));
                    }
                    mongoClient = new MongoClient(serverAddress, mongoCredential,option);
                }
                logger.info("mongoClient 偏好为=" + mongoClient.getReadPreference().toString());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("********mongodb 配置错误");
            }

        }
    }

   @SuppressWarnings("unused")
private static MongoClientOptions getConfOptions(){
	   return new MongoClientOptions.Builder()
	   			// 链接超时时间
			   .connectTimeout(5000) 
			   // read数据超时时间
			   .socketTimeout(5000) 
			   // 大多数情况下到从节点读取数据，从节点不可用时到主节点读取数据。写少读多的场景可以考虑使用这个值
			   .readPreference(ReadPreference.secondaryPreferred()) 
			    // 每个地址最大请求数
			   .connectionsPerHost(30)
			   // 长链接的最大等待时间
			   .maxWaitTime(1000 * 60 * 2) 
			   // 一个socket最大的等待请求数
			   .threadsAllowedToBlockForConnectionMultiplier(50) 
			   .writeConcern(WriteConcern.UNACKNOWLEDGED).build();
   }
    
    
    /***
     * 获取数据库 名为shake
     * @return
     */
    public static MongoDatabase getMongoDb() {
        return getMongoDb("testDb");
    }


    public static MongoDatabase getMongoDb(String databaseName) {
        if (mongoDb_map.get(databaseName) == null) {
            synchronized (databaseName) {
                if (mongoDb_map.get(databaseName) == null) {
                    if (mongoClient == null) {
                        initDBPrompties();
                    }
                    MongoDatabase mongoDb = mongoClient.getDatabase(databaseName);
                    mongoDb_map.put(databaseName, mongoDb);
                }
            }
        }
        return mongoDb_map.get(databaseName);
    }
    
    /**
     * 获取所有数据库名
     * @return
     */
    @SuppressWarnings("deprecation")
	public static List<String> getDbList() {
    	if(mongoClient==null) {
    		initDBPrompties();
    	}
    	return mongoClient.getDatabaseNames();
    }

    /***
     * 获取数据连接，自己获取库/collections
     * @return
     */
    public static MongoClient getMongoClient() {
        if (mongoClient == null) {
            initDBPrompties();
        }
        return mongoClient;
    }
}
