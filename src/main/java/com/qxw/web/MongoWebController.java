package com.qxw.web;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Sorts;
import com.qxw.mongodb.MongoFactory;
import com.qxw.mongodb.MongoSdkBase;
import com.qxw.utils.Res;

/**
 * mongodb web
 * @author qxw
 * @data 2018年11月20日下午1:03:32
 */
@Controller
@RequestMapping("mongo")
public class MongoWebController {
	/**
	 * 条件操作符
	 */
//	private final static String GTE="$gte";
//	private final static String LTE="$lte";
//	private final static String IN="$in";
//	private final static String REGEXS="$regex";
	
    /**
     * 需要过滤的表
     */
    private final static String[] TAVLEARR = {"system.indexes"};


    	
    /**
     * 数据源列表
     * @param db
     * @return
     */
    @ResponseBody
    @RequestMapping("/index")
    public Res index() {
        List<String> listNames =MongoFactory.getDbList();
        return Res.ok().put("listNames", listNames);
    }

    /**
     * 数据库对应的数据集合列表
     * @param dbName
     * @return
     */
    @ResponseBody
    @RequestMapping("/db")
    public Res db(String dbName) {
    	if(StringUtils.isEmpty(dbName)){
    		return Res.error("dbName参数不能为空");
    	}
        MongoDatabase mogo = MongoFactory.getMongoDb(dbName);
        //获取所有集合的名称
        MongoIterable<String> collectionNames = mogo.listCollectionNames();
        MongoCursor<String> i = collectionNames.iterator();
        List<String> listNames = new ArrayList<String>();
        while (i.hasNext()) {
            String tableName = i.next();
			if(!Arrays.asList(TAVLEARR).contains(tableName)){
	            listNames.add(tableName);
			}

        }
        return Res.ok().put("listNames", listNames);
    }

    /***
     * 集合下的数据列表
     * @param pageNum
     * @param pageSize
     * @param parame 查询条件 json字符串
     * @return
     */
    @ResponseBody
    @RequestMapping("/getCollection")
    public Res getCollection(@RequestParam(value = "p", defaultValue = "1") int pageNum,@RequestParam(value = "s", defaultValue = "10") int pageSize,
    		String dbName,String tableName,String parame) {
    	if(StringUtils.isEmpty(dbName)||StringUtils.isEmpty(tableName)){
    		return Res.error("dbName,tableName参数不能为空");
    	}
        BasicDBObject query = new BasicDBObject();
        if(!StringUtils.isEmpty(parame)){
        	JSONObject obj=JSONObject.parseObject(parame);
        	 Set<String> kyes=obj.keySet();
        	 kyes.forEach(key->{
        		    query.put(key,obj.get(key));
        	  });
        }
        MongoCollection<Document> table = MongoSdkBase.getColl(dbName,tableName);
        JSONObject data = MongoSdkBase.getPage(table, query, Sorts.descending("_id"), pageNum, pageSize);
        //获取集合的所有key
        Document obj = MongoSdkBase.getColl(dbName,tableName).find().first();
        Map<String, Object> m = new HashMap<String, Object>(16);
        m.put("data", data);
        if(obj!=null) {
        	m.put("keys", obj.keySet());
        }
        return Res.ok(m);
    }     
   
    /**
     * 删除集合
     * @param collectionName
     * @param tableName
     * @param id  主键
     * @return
     */
    @ResponseBody
    @RequestMapping("/deleteCollection")
    public Res deleteCollection(String dbName,String tableName, String id){
    	if(StringUtils.isEmpty(dbName)||StringUtils.isEmpty(tableName)||StringUtils.isEmpty(id)){
    		return Res.error("dbName,tableName,id,参数不能为空");
    	}
    	int count=MongoSdkBase.deleteOne(MongoSdkBase.getColl(dbName,tableName),id);
        return count>0?Res.ok():Res.error("删除失败");
    }

    /**
     * 更新集合
     * @param collectionName
     * @param tableName
     * @param data  json字符串
     * @return
     */
    @ResponseBody
    @RequestMapping("/updateCollection")
    public Res updateCollection(String dbName,String tableName, String parame){
    	if(StringUtils.isEmpty(dbName)||StringUtils.isEmpty(tableName)||StringUtils.isEmpty(parame)){
    		return Res.error("dbName,tableName,parame,参数不能为空");
    	}
    	JSONObject info=JSONObject.parseObject(parame);
    	String id=info.getString("_id");
    	boolean falg=MongoSdkBase.updateOne(MongoSdkBase.getColl(dbName,tableName), id, info);
        return falg==true?Res.ok():Res.error("更新失败");
    }
    
    /**
     * 添加集合
     * @param collectionName
     * @param tableName
     * @param data  json字符串
     * @return
     */
    @ResponseBody
    @RequestMapping("/saveCollection")
    public Res saveCollection(String dbName,String tableName, String parame){
    	if(StringUtils.isEmpty(dbName)||StringUtils.isEmpty(tableName)||StringUtils.isEmpty(parame)){
    		return Res.error("dbName,tableName,parame,参数不能为空");
    	}
    	JSONObject info=JSONObject.parseObject(parame);
    	String id=MongoSdkBase.insertOne(MongoSdkBase.getColl(dbName,tableName), info);
        return StringUtils.isEmpty(id)?Res.error("添加失败"):Res.ok();
    }
    
 
    /**
     * 根据ID查询集合
     * @param collectionName
     * @param tableName
     * @param data  json字符串
     * @return
     */
    @ResponseBody
    @RequestMapping("/findOne")
    public Res findOne(String dbName,String tableName, String id){
    	if(StringUtils.isEmpty(dbName)||StringUtils.isEmpty(tableName)||StringUtils.isEmpty(id)){
    		return Res.error("dbName,tableName,id,参数不能为空");
    	}
    	String result=MongoSdkBase.seleteOne(MongoSdkBase.getColl(dbName,tableName), id);
        return Res.ok().put("data", JSONObject.parseObject(result));
    }
    
  
    
}
