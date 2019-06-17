package spring.demo.service.impl;


import spring.demo.service.IModifyService;
import spring.framework.annotation.GPService;

/**
 * 增删改业务
 * @author Tom
 *
 */
@GPService
public class ModifyService implements IModifyService {

	/**
	 * 增加
	 */
	public String add(String name,String addr) throws Exception{
//		return "modifyService add,name=" + name + ",addr=" + addr;
        System.out.println("999");
		throw new Exception("这是古琴抛出的异常！");
	}

	/**
	 * 修改
	 */
	public String edit(Integer id,String name) {
		return "modifyService edit,id=" + id + ",name=" + name;
	}

	/**
	 * 删除
	 */
	public String remove(Integer id) {
		return "modifyService id=" + id;
	}
	
}
