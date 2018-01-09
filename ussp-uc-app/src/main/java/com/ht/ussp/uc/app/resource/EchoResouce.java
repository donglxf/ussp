package com.ht.ussp.uc.app.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

/**
 * 
 * @ClassName: EchoResouce
 * @Description: demo
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月8日 下午8:20:32
 */
@RestController
@RequestMapping(value="/echo")
public class EchoResouce {
	private static final Logger log = LoggerFactory.getLogger(EchoResouce.class);

	
	 @ApiOperation("get demo")
	    @RequestMapping(value={""}, method=RequestMethod.GET)
	    public String getEcho() {
		 log.info("====get echo success====");
	        return "echo";
	    }
	 
	 @ApiOperation(value="post demo",notes="for create")
	    @RequestMapping(value={""}, method=RequestMethod.POST)
	    public String postEcho() {
	        return "put success";
	    }
	 
//	    @ApiOperation(value="", notes="用ID查询")
//	    @ApiImplicitParam(name = "id", value = "ID", required = true, dataType = "Long")
//	    @RequestMapping(value="/{id}", method=RequestMethod.GET)
//	    public User getUser(@PathVariable Long id) {
//	        return users.get(id);
//	    }
//
//	    @ApiOperation(value="更新用户详细信息", notes="根据url的id来指定更新对象，并根据传过来的user信息来更新用户详细信息")
//	    @ApiImplicitParams({
//	            @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long"),
//	            @ApiImplicitParam(name = "user", value = "用户详细实体user", required = true, dataType = "User")
//	    })
//	    @RequestMapping(value="/{id}", method=RequestMethod.PUT)
//	    public String putUser(@PathVariable Long id, @RequestBody User user) {
//	        User u = users.get(id);
//	        u.setName(user.getName());
//	        u.setAge(user.getAge());
//	        users.put(id, u);
//	        return "success";
//	    }
//
//	    @ApiOperation(value="删除用户", notes="根据url的id来指定删除对象")
//	    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long")
//	    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
//	    public String deleteUser(@PathVariable Long id) {
//	        users.remove(id);
//	        return "success";
//	    }

}
