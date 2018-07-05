package com.qxw.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.web.servlet.ModelAndView;

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
					if(!Arrays.asList(tableArr).contains(tableName)){
						 listNames.add(tableName);
					}
		           
		    }		
	        return R.ok().put("listNames", listNames);
	    }
	  
	
	  	/**
	  	 * 查询指定mongo集合的表数据
	  	 * @param pageNum
	  	 * @param pageSize
	  	 * @param request
	  	 * @return
	  	 */
		@RequestMapping("/findTableNameList")
		public ModelAndView findTableNameList(@RequestParam(value="p",defaultValue="1") int pageNum,
				@RequestParam(value="s",defaultValue="10") int pageSize, HttpServletRequest request){
			ModelAndView view=new ModelAndView();
			if(pageNum==0) pageNum=1;if(pageSize==0) pageSize=10;
			String tableName=request.getParameter("tableName");
			BasicDBObject query=  new BasicDBObject();
			MongoCollection<Document> table=MongoSdkBase.getColl(tableName);
			JSONObject data = MongoSdkBase.getPage(table, query, Sorts.descending("_id"), pageNum, pageSize);
			//获取集合的所有key
			List<JSONObject> oldList=(List<JSONObject>) data.get("data");
			JSONObject obj=oldList.get(0);
			
		
			List<String> keyOrType=new ArrayList<>();
			obj.keySet().forEach(o->{
				if(obj.get(o) instanceof Integer){
					keyOrType.add(o+",Integer");
				}else if(obj.get(o) instanceof Long){
					keyOrType.add(o+",Long");
				}else if(obj.get(o) instanceof Double){
					keyOrType.add(o+",Double");
				}else if(obj.get(o) instanceof JSONObject){
					keyOrType.add(o+",JSONObject");
				}else if(obj.get(o) instanceof JSONArray){
					keyOrType.add(o+",JSONArray");
				}else{
					keyOrType.add(o+",String");
				}
			});		
			view.setViewName("/sys/mongo/collection");
			view.addObject("data", data);
			view.addObject("keys", obj.keySet());
			view.addObject("keyOrType",keyOrType);
			return view;		
		}
		
		
		
}
