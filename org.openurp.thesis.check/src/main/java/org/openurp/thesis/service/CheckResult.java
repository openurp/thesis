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

import java.util.Date;

/**
 * 论文检测结果
 * 
 * @author chaostone
 */
public class CheckResult {

  /** 知网检测ID */
  final long id;
  /** 篇名 */
  final String article;
  /** 作者姓名 */
  final String author;
  /** 知网验证的md5 */
  final String checksum;
  /** 正在验证过程中 */
  final boolean inprocess;
  /** 知网检测日期 */
  final Date checkAt;
  /** 重复比 */
  final float ratio;
  /** 重合字数 */
  final int count;

  public CheckResult(long id, String article, String author, String checksum, Date checkAt, float ratio,
      int count) {
    super();
    this.id = id;
    this.article = article;
    this.author = author;
    this.checksum = checksum;
    this.checkAt = checkAt;
    this.ratio = ratio;
    this.count = count;
    inprocess = false;
  }

  public CheckResult(long id, String article, String author, String checksum, Date checkAt) {
    super();
    this.id = id;
    this.article = article;
    this.author = author;
    this.checksum = checksum;
    this.checkAt = checkAt;
    this.ratio = 0;
    this.count = 0;
    this.inprocess = true;
  }

  @Override
  public String toString() {
    return "CheckResult [id=" + id + ", article=" + article + ", author=" + author + ", checksum=" + checksum
        + ", checkAt=" + checkAt + ", ratio=" + ratio + ", count=" + count + "]";
  }

  public long getId() {
    return id;
  }

  public String getArticle() {
    return article;
  }

  public String getAuthor() {
    return author;
  }

  public String getChecksum() {
    return checksum;
  }

  public Date getCheckAt() {
    return checkAt;
  }

  public float getRatio() {
    return ratio;
  }

  public int getCount() {
    return count;
  }
}
