package com.qxw.utils;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class CommonApi {
	
	private static final Logger logger = LoggerFactory.getLogger(CommonApi.class);
	
	
	/***mongodb 连接参数*/
	public static  String mongo_host_port;
	public static String mongo_user_pass_db;	

	
	static{
		try{
			InputStream is = CommonApi.class.getResourceAsStream("/application.properties");
			Properties properties = new Properties();
			properties.load(is);
			mongo_host_port = properties.getProperty("mongo_host_port");
			mongo_user_pass_db = properties.getProperty("mongo_user_pass_db");
			
			is.close(); 
		}catch(Exception ex){
			logger.debug("加载配置文件出错："+ex.getMessage());
		}
	}
}
