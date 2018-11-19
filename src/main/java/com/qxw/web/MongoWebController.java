package com.qxw.web;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.bson.Document;
import org.springframework.stereotype.Controller;
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
import com.qxw.utils.R;


@Controller
@RequestMapping("mongo")
public class MongoWebController {
    /**
     * 需要过滤的表
     */
    private final static String[] tableArr = {"system.indexes"};


    	
    /**
     * 数据源列表
     * @param db
     * @return
     */
    @ResponseBody
    @RequestMapping("/index")
    public R index(String db) {
        List<String> listNames =MongoFactory.getDbList();
        return R.ok().put("listNames", listNames);
    }

    /**
     * 数据库对应的数据集合列表
     * @param dbName
     * @return
     */
    @ResponseBody
    @RequestMapping("/db")
    public R db(String dbName) {
        MongoDatabase mogo = MongoFactory.getMongoDb(dbName);
        //获取所有集合的名称
        MongoIterable<String> collectionNames = mogo.listCollectionNames();
        MongoCursor<String> i = collectionNames.iterator();
        List<String> listNames = new ArrayList<String>();
        while (i.hasNext()) {
            String tableName = i.next();
			if(!Arrays.asList(tableArr).contains(tableName)){
	            listNames.add(tableName);
			}

        }
        return R.ok().put("listNames", listNames);
    }

    /***
     * 集合下的数据列表
     * @param pageNum
     * @param pageSize
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/getCollection")
    public R getCollection(@RequestParam(value = "p", defaultValue = "1") int pageNum,@RequestParam(value = "s", defaultValue = "10") int pageSize,String dbName,String tableName) {
        BasicDBObject query = new BasicDBObject();
        MongoCollection<Document> table = MongoSdkBase.getColl(dbName,tableName);
        JSONObject data = MongoSdkBase.getPage(table, query, Sorts.descending("_id"), pageNum, pageSize);

        //获取集合的所有key
        Document obj = MongoSdkBase.getColl(dbName,tableName).find().first();
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("data", data);
        if(obj!=null) {
        	m.put("keys", obj.keySet());
        }
        return R.ok(m);
    }





}
