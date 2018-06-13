package com.xk.service;

import java.util.List;
import java.util.Map;

import com.xk.mapper.UserMapper;
import com.xk.model.User;
import com.xk.model.UserVO;

/**
 * 用户service接口
 * @author shuzheng
 * @date 2016年7月6日 下午6:03:45
 */
public interface UserService extends BaseService<UserMapper> {
	
	/**
	 * 获取带book数据的用户
	 * @param id
	 * @return
	 */
	UserVO selectUserWithBook(int id);

	/**
	 * 根据条件获取用户列表
	 * @param map
	 * @return
	 */
	List<User> selectAll(Map<String, Object> map);

	/**
	 * 插入用户并返回主键
	 * @param user
	 */
	void insertAutoKey(User user);
	
}