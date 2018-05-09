//ackage com.ht.ussp.ouc.app.feignclients;
//
//import org.springframework.cloud.netflix.feign.FeignClient;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import com.ht.ussp.ouc.app.model.ResponseModal;
//
// 
//@FeignClient(value = "ussp-uaa-app")
//public interface UaaClient {
//
//	/**
//	 * 
//	 * @Title: saveResources 
//	 * @Description: 内部API jwt验证 
//	 * @return ResponseModal  userId & orgCode
//	 * @throws
//	 * @author wim qiuwenwu@hongte.info 
//	 * @date 2018年3月12日 下午3:50:31
////	 */
//	@RequestMapping(value = "/validateJwt")
//	public ResponseModal validateJwt(@RequestParam("tokenPayload") String tokenPayload);
//	
//	@PostMapping(value = "/external/createUCToken")
//	public ResponseModal createUCToken(@RequestParam("userId")String userId,@RequestParam("bmUserId")String bmUserId,@RequestParam("tokenTime")Integer tokenTime,@RequestParam("refreshTime")Integer refreshtime);
//	 
//}
