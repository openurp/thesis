package org.openurp.thesis.service;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 论文检测服务
 * 
 * @author chaostone
 * 
 */
public interface ThesisCheckService {

	/**
	 * 登陆
	 * @param username
	 * @param password
	 * @param captcha
	 * @param params
	 * @return
	 * @throws Exception
	 */
	boolean login(String username, String password, String captcha,
			Map<String, String> params) throws Exception;

	File download(String url) throws Exception;

	/**
	 * 获取验证码
	 * @return
	 */
	File getCaptcha();

	void logout();

	List<CheckResult> search(String author, String article) throws Exception;

	/**
	 * 检查重复率
	 * @param author 姓名
	 * @param article 篇名
	 * @param file 文件
	 * @return
	 * @throws Exception
	 */
	CheckResult check(String author, String article, File file)
			throws Exception;
}