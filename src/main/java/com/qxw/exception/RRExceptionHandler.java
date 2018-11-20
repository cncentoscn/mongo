package com.qxw.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.qxw.utils.R;

/**
 * 通用异常处理器
 * @author qxw
 * @data 2018年11月20日上午11:22:52
 */
@RestControllerAdvice
public class RRExceptionHandler {
	  private Logger logger = LoggerFactory.getLogger(getClass());

	    /**
	     * 处理自定义异常
	     */
	    @ExceptionHandler(RRException.class)
	    public R handleRRException(RRException e) {
	        R r = new R();
	        r.put("code", e.getCode());
	        r.put("msg", e.getMessage());
	        logger.error("自定义RRException异常：{}", e);
	        return r;
	    }

	    @ExceptionHandler(Exception.class)
	    public R handleException(Exception e) {
	        logger.error("全局Exception异常：{}", e);
	        return R.error();
	    }
}
