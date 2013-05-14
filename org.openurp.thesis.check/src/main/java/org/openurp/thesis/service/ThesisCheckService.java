package org.openurp.thesis.service;

import java.io.File;
import java.util.List;

/**
 * 论文检测服务
 * 
 * @author chaostone
 * 
 */
public interface ThesisCheckService {

	/**
	 * 登陆
	 * 
	 * @param username
	 * @param password
	 * @param captcha
	 * @param params
	 * @return
	 */
	boolean login(String username, String password, String captcha);

	/**
	 * 获取验证码
	 * 
	 * @return
	 */
	File getCaptcha();

	/**
	 * 登出
	 */
	void logout();

	/**
	 * 查询单个作者和篇名的结果
	 * 
	 * @param author
	 * @param article
	 * @return
	 */
	CheckResult get(String author, String article);

	/**
	 * 查询检测结果
	 * 
	 * @param author
	 * @param article
	 * @return
	 * @throws Exception
	 */
	List<CheckResult> search(String author, String article);

	/**
	 * 上传文件
	 * 
	 * @param author
	 * @param article
	 * @param file
	 * @return
	 */
	boolean upload(String author, String article, File file);

	/**
	 * 下载报表
	 * @param id
	 * @param style
	 * @return
	 */
	String report(long id, ReportStyle style);

}