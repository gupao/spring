package spring.demo.action;

import com.sun.corba.se.impl.ior.OldJIDLObjectKeyTemplate;
import lombok.Data;
import spring.demo.service.IModifyService;
import spring.demo.service.IQueryService;
import spring.demo.service.impl.ModifyService;
import spring.demo.service.impl.QueryService;
import spring.framework.annotation.GPAutowired;
import spring.framework.annotation.GPController;
import spring.framework.annotation.GPRequestMapping;
import spring.framework.annotation.GPRequestParam;
import spring.framework.webmvc.servlet.GPModelAndView;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;


/**
 * 公布接口url
 * @author Tom
 *
 */
@Data
@GPController
@GPRequestMapping("/web")
public class MyAction {

	@GPAutowired IQueryService queryService;
	@GPAutowired IModifyService modifyService;


	@GPRequestMapping("/query.json")
	public GPModelAndView query(HttpServletRequest request, HttpServletResponse response,
								@GPRequestParam("name") String name){
		String result = queryService.query(name);
		return out(response,result);
	}
	
	@GPRequestMapping("/add.json")
	public GPModelAndView add(HttpServletRequest request,HttpServletResponse response,
			   @GPRequestParam("name") String name,@GPRequestParam("addr") String addr){
		String result = null;
		try {
			result = modifyService.add(name,addr);
			return out(response,result);
		} catch (Exception e) {
			log.println(e);
			Map<String,Object> model = new HashMap<>();
//			model.put("detail",e.getMessage());
			model.put("detail",e.getCause().getMessage());
			model.put("stackTrace", Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]",""));
			return new GPModelAndView("500",model);
		}
	}
	
	@GPRequestMapping("/remove.json")
	public GPModelAndView remove(HttpServletRequest request,HttpServletResponse response,
		   @GPRequestParam("id") Integer id){
		String result = modifyService.remove(id);
		return out(response,result);
	}
	
	@GPRequestMapping("/edit.json")
	public GPModelAndView edit(HttpServletRequest request,HttpServletResponse response,
			@GPRequestParam("id") Integer id,
			@GPRequestParam("name") String name){
		String result = modifyService.edit(id,name);
		return out(response,result);
	}
	
	
	
	private GPModelAndView out(HttpServletResponse resp,String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
