package com.qxw.handler;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 拦截器
 * @author qxw
 * @data 2018年6月15日下午5:11:13
 */
@Component
public class LoginInterceptor implements HandlerInterceptor{


	
	private  final Logger LOG = LoggerFactory.getLogger(getClass());
	
	/**
	 * 该方法将在请求处理之前进行调用，只有该方法返回true，才会继续执行后续的Interceptor和Controller，
	 * 当返回值为true 时就会继续调用下一个Interceptor的preHandle 方法
	 * ，如果已经是最后一个Interceptor的时候就会是调用当前请求的Controller方法； 
	 */
	 @Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String contextPath = request.getContextPath();
		String requestUrl = request.getRequestURI().toString();
		String path=requestUrl.replaceAll(contextPath, "");	
		
		HttpSession session = request.getSession(true);  

		session.setAttribute("userInfo_openid", "o5BJV1N3exzCbI_tgSHQLc6a2pDA");
        // 从session 里面获取用户名的信息  
        Object obj = session.getAttribute("userInfo_openid"); 
        // 判断如果没有取到用户信息，就跳转到重新授权
        if (obj == null || "".equals(obj.toString())) {
        	StringBuilder paramURL=new StringBuilder();
        	String paramPageURL=requestUrl.replaceAll(contextPath, "");
        	paramURL.append(paramPageURL);

        	//参数拼接url?key=1&value=0;
        	Map<String, String[]> parames=request.getParameterMap();       	
        	Set<Entry<String, String[]>> itermap=parames.entrySet();
        	if(itermap!=null){
        		paramURL.append("?param=true");
        		for (Entry<String, String[]> entry : itermap) {
      	            String key = entry.getKey();  	         
      	            String[] values = entry.getValue();
      	            String value="";
      	            if(values.length>0){
      	            	value=values[0];
      	            }
      	            paramURL.append("&"+key+"="+value);
      	        }
        	}

           	return false;
        }  
        return true;  
	}

	/**
	 * 该方法也是需要当前对应的Interceptor的preHandle方法的返回值为true时才会执行，
	 * 该方法将在整个请求结束之后，也就是在DispatcherServlet 渲染了对应的视图之后执行。用于进行资源清理。
	 */
	 @Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3) throws Exception {}
	
	
	/**
	 * 该方法将在请求处理之后，DispatcherServlet进行视图返回渲染之前进行调用，可以在这个方法中对Controller 处理之后的ModelAndView 对象进行操作。 
	 */
	 @Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3) throws Exception {}



}
