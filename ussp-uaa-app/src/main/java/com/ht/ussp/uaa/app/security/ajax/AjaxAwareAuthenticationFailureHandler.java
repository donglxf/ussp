package com.ht.ussp.uaa.app.security.ajax;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.ussp.common.SysStatus;
import com.ht.ussp.uaa.app.exception.AuthMethodNotSupportedException;
import com.ht.ussp.uaa.app.exception.JwtExpiredTokenException;
import com.ht.ussp.uaa.app.model.ResponseModal;

import lombok.extern.log4j.Log4j2;

/**
 * 
* @ClassName: AjaxAwareAuthenticationFailureHandler
* @Description: 授权失败处理
* @author wim qiuwenwu@hongte.info
* @date 2018年1月6日 上午11:47:17
 */
@Component
@Log4j2
public class AjaxAwareAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper mapper;
    
    @Autowired
    public AjaxAwareAuthenticationFailureHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }	
    
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException e) throws ServletException {
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		
		if (e instanceof BadCredentialsException) {
			String es= e.getMessage();
			if(e!=null) {
				if(SysStatus.INVALID_USER.getStatus().equals(es)) {
					try {
						mapper.writeValue(response.getWriter(),new ResponseModal(SysStatus.INVALID_USER));
					} catch (IOException e1) {
						log.debug("write response result INVALID_USER:"+e.getMessage());
					}finally {
						try {
							response.getWriter().close();
						} catch (IOException e1) {
							log.debug("close io exception:"+e.getMessage());
						}
					}
				}else if(SysStatus.METHOD_NOT_SUPPORTED.getStatus().equals(es)) {
					try {
						mapper.writeValue(response.getWriter(),new ResponseModal(SysStatus.METHOD_NOT_SUPPORTED));
					} catch (IOException e1) {
						log.debug("write response result METHOD_NOT_SUPPORTED:"+e.getMessage());
					}finally {
						try {
							response.getWriter().close();
						} catch (IOException e1) {
							log.debug("close io exception:"+e.getMessage());
						}
					}
				}else if(SysStatus.USER_NOT_FOUND.getStatus().equals(es)) {
					try {
						mapper.writeValue(response.getWriter(),new ResponseModal(SysStatus.USER_NOT_FOUND));
					} catch (IOException e1) {
						log.debug("write response result USER_NOT_FOUND:"+e.getMessage());
					}finally {
						try {
							response.getWriter().close();
						} catch (IOException e1) {
							log.debug("close io exception:"+e.getMessage());
						}
					}
				}else if(SysStatus.USER_HAS_DELETED.getStatus().equals(es)) {
					try {
						mapper.writeValue(response.getWriter(),new ResponseModal(SysStatus.USER_HAS_DELETED));
					} catch (IOException e1) {
						log.debug("write response result USER_HAS_DELETED:"+e.getMessage());
					}finally {
						try {
							response.getWriter().close();
						} catch (IOException e1) {
							log.debug("close io exception:"+e.getMessage());
						}
					}
				}else if(SysStatus.USER_NOT_RELATE_APP.getStatus().equals(es)) {
					try {
						mapper.writeValue(response.getWriter(),new ResponseModal(SysStatus.USER_NOT_RELATE_APP));
					} catch (IOException e1) {
						log.debug("write response result USER_NOT_RELATE_APP:"+e.getMessage());
					}finally {
						try {
							response.getWriter().close();
						} catch (IOException e1) {
							log.debug("close io exception:"+e.getMessage());
						}
					}
				}else if(SysStatus.USER_NOT_MATCH_APP.getStatus().equals(es)) {
					try {
						mapper.writeValue(response.getWriter(),new ResponseModal(SysStatus.USER_NOT_MATCH_APP));
					} catch (IOException e1) {
						log.debug("write response result USER_NOT_MATCH_APP:"+e.getMessage());
					}finally {
						try {
							response.getWriter().close();
						} catch (IOException e1) {
							log.debug("close io exception:"+e.getMessage());
						}
					}
				}else if(SysStatus.NO_ROLE.getStatus().equals(es)) {
					try {
						mapper.writeValue(response.getWriter(),new ResponseModal(SysStatus.NO_ROLE));
					} catch (IOException e1) {
						log.debug("write response result NO_ROLE:"+e.getMessage());
					}finally {
						try {
							response.getWriter().close();
						} catch (IOException e1) {
							log.debug("close io exception:"+e.getMessage());
						}
					}
				}else if(SysStatus.METHOD_NOT_SUPPORTED.getStatus().equals(es)) {
					try {
						mapper.writeValue(response.getWriter(),new ResponseModal(SysStatus.METHOD_NOT_SUPPORTED));
					} catch (IOException e1) {
						log.debug("write response result METHOD_NOT_SUPPORTED:"+e.getMessage());
					}finally {
						try {
							response.getWriter().close();
						} catch (IOException e1) {
							log.debug("close io exception:"+e.getMessage());
						}
					}
				}else {
					try {
						mapper.writeValue(response.getWriter(),new ResponseModal(SysStatus.INVALID_USER));
					} catch (IOException e1) {
						log.debug("write response result INVALID_USER:"+e.getMessage());
					}finally {
						try {
							response.getWriter().close();
						} catch (IOException e1) {
							log.debug("close io exception:"+e.getMessage());
						}
					}
				}
			}
		} else
			if (e instanceof JwtExpiredTokenException) {
				try {
					mapper.writeValue(response.getWriter(),new ResponseModal(SysStatus.TOKEN_IS_EXPIRED));
				} catch (IOException e1) {
					log.debug("write response result TOKEN_IS_EXPIRED:"+e.getMessage());
				}finally {
					try {
						response.getWriter().close();
					} catch (IOException e1) {
						log.debug("close io exception:"+e.getMessage());
					}
				}
		} else if (e instanceof AuthMethodNotSupportedException) {
			try {
				mapper.writeValue(response.getWriter(),new ResponseModal(SysStatus.METHOD_NOT_SUPPORTED));
			} catch (IOException e1) {
				log.debug("write response result METHOD_NOT_SUPPORTED:"+e.getMessage());
			}finally {
				try {
					response.getWriter().close();
				} catch (IOException e1) {
					log.debug("close io exception:"+e.getMessage());
				}
			}
		}
		try {
			mapper.writeValue(response.getWriter(),new ResponseModal(SysStatus.AUTH_FAILED));
		} catch (IOException e1) {
			log.debug("write response result AUTH_FAILED:"+e.getMessage());
		}finally {
			try {
				response.getWriter().close();
			} catch (IOException e1) {
				log.debug("close io exception:"+e.getMessage());
			}
		}
	}
}
