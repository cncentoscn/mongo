package com.qxw.web;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Sorts;
import com.qxw.mongodb.MongoFactory;
import com.qxw.mongodb.MongoSdkBase;


@Controller
@RequestMapping("mongo")
public class MongoWebController {
	/**
	 * 需要过滤的表
	 */
	private final static String[] tableArr={"TTL_common_cache","ids","sys_menu","sys_user","system.indexes","mobile"};
	
	
	  @ResponseBody
	  @RequestMapping("/index")
	  public R index(HttpServletRequest request){
		    MongoDatabase mogo=MongoFactory.getMongoDb();
			//获取所有集合的名称
			MongoIterable<String> collectionNames=mogo.listCollectionNames();
			MongoCursor<String> i =collectionNames.iterator();
			List<String> listNames=new ArrayList<String>();
			 while (i.hasNext()) {
					String tableName=i.next();
//					if(!Arrays.asList(tableArr).contains(tableName)){
						 listNames.add(tableName);
//					}
		           
		    }		
	        return R.ok().put("listNames", listNames);
	    }
	  

		
		
		  @ResponseBody
		  @RequestMapping("/getCollection")
		  public R getCollection(@RequestParam(value="p",defaultValue="1") int pageNum,
					@RequestParam(value="s",defaultValue="10") int pageSize, HttpServletRequest request){
			  if(pageNum==0) pageNum=1;if(pageSize==0) pageSize=10;
				String tableName=request.getParameter("tableName");
				String jsonStr=request.getParameter("jsonStr");
				BasicDBObject query=  new BasicDBObject();
				if(!StringUtils.isEmpty(jsonStr)){
					JSONObject obj=JSONObject.parseObject(jsonStr);
					obj.keySet().forEach(o->{
						if(obj.get(o) instanceof Integer){
							query.put(o, obj.get(o));
						}else if(obj.get(o) instanceof JSONObject){
							
						}else if(obj.get(o) instanceof JSONArray){
							
						}else{
							//其余默认为字符串查询
							if(obj.get(o) instanceof String){
								String parm=obj.getString(o);
								parm.contains(",");
							}
						}
					});		
					
				}
				MongoCollection<Document> table=MongoSdkBase.getColl(tableName);
				JSONObject data = MongoSdkBase.getPage(table, query, Sorts.descending("_id"), pageNum, pageSize);
				
				//获取集合的所有key
				Document obj=MongoSdkBase.getColl(tableName).find().first();	
				Map<String, Object> m=new HashMap<String, Object>();
				m.put("data", data);
				m.put("keys", obj.keySet());
		        return R.ok(m);
		   }
		  
		  
		  @ResponseBody
		  @RequestMapping("/getCollectionKeys")
		  public R getCollectionKeys(HttpServletRequest request){
				String tableName=request.getParameter("tableName");				
				Document doc=MongoSdkBase.getColl(tableName).find().first();
				Map<String, Object> map=new HashMap<>();
				map.put("doc", doc);			
		        return R.ok(map);
		   }
		  
		  
}
