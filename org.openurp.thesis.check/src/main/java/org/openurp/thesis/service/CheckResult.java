package org.openurp.thesis.service;

import java.util.Date;

public class CheckResult {

	final long id;
	final String article;
	final String author;
	final String checksum;
	final boolean inprocess;
	final Date checkAt;
	final float ratio;
	final int count;

	public CheckResult(long id, String article, String author, String checksum,
			Date checkAt, float ratio, int count) {
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

	public CheckResult(long id, String article, String author, String checksum,
			Date checkAt) {
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
		return "CheckResult [id=" + id + ", article=" + article + ", author="
				+ author + ", checksum=" + checksum + ", checkAt=" + checkAt
				+ ", ratio=" + ratio + ", count=" + count + "]";
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
