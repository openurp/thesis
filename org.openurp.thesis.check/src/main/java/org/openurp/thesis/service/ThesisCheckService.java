/*
 * OpenURP,Open Source University Resource Plan Solution
 *
 * Copyright (c) 2013-2013, OpenURP Software.
 *
 * OpenURP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenURP is distributed in the hope that it will be useful.
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenURP.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openurp.thesis.service;

import java.io.File;
import java.util.List;

/**
 * 论文检测服务
 * 
 * @author chaostone
 * @since 2003-5-10
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
   * 
   * @param id
   * @param style
   * @return
   */
  File report(long id, ReportStyle style);

}
