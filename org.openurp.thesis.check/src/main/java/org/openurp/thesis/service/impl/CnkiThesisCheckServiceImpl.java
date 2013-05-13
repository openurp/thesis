package org.openurp.thesis.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.comparators.PropertyComparator;
import org.openurp.thesis.service.CheckResult;
import org.openurp.thesis.service.ThesisCheckService;

/**
 * 
 * 基于中国知网论文检测系统的论文检测服务
 * 
 * @see http://pmlc.cnki.net/school/Login.aspx
 * @author chaostone
 * 
 */
public class CnkiThesisCheckServiceImpl implements ThesisCheckService {

	DefaultHttpClient httpclient = new DefaultHttpClient();

	/** 登陆地址 */
	String loginUrl = "http://pmlc.cnki.net/school/Login.aspx";
	/** 登陆验证码地址 */
	String loginCaptchaUrl = "http://pmlc.cnki.net/school/Users/LoginCheckCode.aspx";
	String logoutUrl;
	/** 上传论文地址 */
	String uploadUrl = "http://pmlc.cnki.net/school/upload/receivefiles.aspx";
	/** 查询信息地址 */
	String infoUrl = "http://pmlc.cnki.net/school/SimResult.aspx";

	/** 针对查询结果匹配的正则表达式 */
	Pattern checkPattern = Pattern
			.compile("<tr([\\s\\S]*?)SR_FileNameS([\\s\\S]*?)</td>([\\s\\S]*?)<td([\\s\\S]*?)>([\\s\\S]*?)</td>([\\s\\S]*?)<td([\\s\\S]*?)>([\\s\\S]*?)</td>([\\s\\S]*?)<td([\\s\\S]*?)>([\\s\\S]*?)</td>([\\s\\S]*?)<td([\\s\\S]*?)>([\\s\\S]*?)</td>([\\s\\S]*?)<td([\\s\\S]*?)>([\\s\\S]*?)</td>([\\s\\S]*?)</tr>");

	/** 论方存放的文件夹ID */
	String foldId = null;
	/** 登陆成功后的sessionId */
	String sessionId = null;

	/** 查询信息项目的默认参数 */
	Map<String, String> infoParams = CollectUtils.newHashMap();

	/**
	 * 登陆方法，登陆成功后会获取foldId和sessionId
	 */
	@Override
	public boolean login(String username, String password, String captcha,
			Map<String, String> params) throws Exception {
		foldId = null;
		sessionId = null;
		Map<String, String> merged = new HashMap<String, String>();
		if (null != params)
			merged.putAll(params);

		merged.put("UserName", username);
		merged.put("UserPwd", password);
		if (null != captcha)
			merged.put("TextBox_Check", captcha);

		merged.put("__EVENTVALIDATION",
				"/wEWBQKf9pzyCQKvruq2CAKEzp2FBwKf06GLAQLSwpnTCOrkpBeWGp1IoUnLJnsl8k/Cet1H");
		merged.put(
				"__VIEWSTATE",
				"/wEPDwUKLTY1NDc5ODY2NWQYAQUeX19Db250cm9sc1JlcXVpcmVQb3N0QmFja0tleV9fFgEFDEltYWdlQnV0dG9uMbIaUF+dr62e/YiWT95H6zzf5pIB");
		merged.put("ImageButton1.x", "0");
		merged.put("ImageButton1.y", "0");

		HttpPost httpost = new HttpPost(loginUrl);
		httpost.setEntity(new UrlEncodedFormEntity(convertToValuePairs(merged),
				"UTF-8"));
		HttpResponse response = httpclient.execute(httpost);
		HttpEntity entity = response.getEntity();
		// FIXME debug
		// System.out.println(httpost.getRequestLine() + " "
		// + response.getStatusLine().getStatusCode());
		EntityUtils.consume(entity);

		boolean success = (302 == response.getStatusLine().getStatusCode());
		if (success) {
			for (Cookie ck : httpclient.getCookieStore().getCookies()) {
				if (ck.getName().equals("ASP.NET_SessionId")) {
					sessionId = ck.getValue();
					break;
				}
			}
			response = httpclient.execute(new HttpGet(infoUrl));
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				buildInfoParams(EntityUtils.toString(response.getEntity()));
			} else if (HttpStatus.SC_MOVED_TEMPORARILY == response
					.getStatusLine().getStatusCode()) {
				String location = response.getFirstHeader("Location")
						.getValue();
				buildInfoParams(access(location));
			}
			EntityUtils.consume(response.getEntity());
			success = (StringUtils.isNotEmpty(foldId) && StringUtils
					.isNotEmpty(sessionId));
		}
		return success;
	}

	public void logout() {
		if (null != logoutUrl)
			accessTo(logoutUrl);
		// When HttpClient instance is no longer needed,
		// shut down the connection manager to ensure
		// immediate deallocation of all system resources
		httpclient.getConnectionManager().shutdown();
	}

	@Override
	public File download(String url) throws Exception {
		HttpGet innerget = new HttpGet(url);
		HttpResponse response = httpclient.execute(innerget);
		if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
			File tmp = File.createTempFile("download", "tmp");
			FileOutputStream output = new FileOutputStream(tmp);
			IOUtils.copy(response.getEntity().getContent(), output);
			output.flush();
			IOUtils.closeQuietly(output);
			return tmp;
		} else
			return null;
	}

	public File getCaptcha() {
		try {
			return download(loginCaptchaUrl);
		} catch (Exception e) {
			return null;
		}
	}

	public CheckResult check(String author, String article, File file)
			throws Exception {
		CheckResult result = searchFirst(author, article);
		if (null != result)
			return result;
		upload(author, article, file);
		Thread.sleep(1000);
		return searchFirst(author, article);
	}

	@Override
	public List<CheckResult> search(String author, String article)
			throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("DDL1", "作者");
		params.put("TB1", author);
		params.put("DDLJCZT", "0");
		params.put("ImageButton5.x", "-762");
		params.put("ImageButton5.y", "-178");
		params.putAll(infoParams);
		HttpPost httpost = new HttpPost(infoUrl);
		// params.put("__ASYNCPOST","true");
		// httpost.addHeader("X-MicrosoftAjax", "Delta=true");
		httpost.setEntity(new UrlEncodedFormEntity(convertToValuePairs(params),
				"UTF-8"));
		HttpResponse response = httpclient.execute(httpost);
		HttpEntity entity = response.getEntity();
		String text = EntityUtils.toString(entity);
		EntityUtils.consume(entity);
		text = StringUtils.substringBetween(text, "GridView2", "</table>");
		if (StringUtils.isEmpty(text))
			Collections.emptyList();
		List<CheckResult> results = extract(text);
		if (null == article)
			return results;

		List<CheckResult> rs = new ArrayList<CheckResult>();
		for (CheckResult cr : results) {
			if (null != cr.getArticle() && cr.getArticle().contains(article))
				rs.add(cr);
		}
		return rs;
	}

	protected String access(String url) {
		HttpGet innerget = new HttpGet(url);
		try {
			HttpResponse response = httpclient.execute(innerget);
			HttpEntity entity = response.getEntity();
			String content = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
			return content;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 查找符合条件的第一个
	 * 
	 * @param author
	 * @param article
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected CheckResult searchFirst(String author, String article)
			throws Exception {
		List<CheckResult> results = search(author, article);
		if (!results.isEmpty()) {
			if (results.size() > 1)
				Collections.sort(results,
						new PropertyComparator("checkAt desc"));
			return results.get(0);
		} else
			return null;
	}

	/**
	 * 上传文件
	 * 
	 * @param author
	 * @param article
	 * @param file
	 * @return
	 * @throws Exception
	 * @see http://pmlc.cnki.net/school/SwfUpload/handlers.js#uploadSuccess
	 */
	protected boolean upload(String author, String article, File file)
			throws Exception {
		Charset utf8 = Charset.forName("UTF-8");
		MultipartEntity reqEntity = new MultipartEntity();
		reqEntity.addPart("JJ", new StringBody("", utf8));
		reqEntity.addPart("DW", new StringBody("", utf8));
		reqEntity.addPart("FL", new StringBody("", utf8));
		reqEntity.addPart("PM", new StringBody(article, utf8));
		reqEntity.addPart("ZZ", new StringBody(author, utf8));
		reqEntity.addPart("FD", new StringBody(foldId, utf8));
		reqEntity.addPart("ASPSESSID", new StringBody(sessionId, utf8));
		reqEntity.addPart("Filedata", new FileBody(file));
		HttpPost httpost = new HttpPost(uploadUrl);
		httpost.setEntity(reqEntity);

		HttpResponse response = httpclient.execute(httpost);
		HttpEntity entity = response.getEntity();
		String content = EntityUtils.toString(entity);
		EntityUtils.consume(entity);

		System.out.println("upload " + file.getName() + " response is "
				+ content);
		/* 只有200是成功的，其他错误码，在handlers.js中的uploadSuccess删除中 */
		return StringUtils.trim(content).equals("200");
	}

	/**
	 * 解析返回文本中的检测结果
	 * 
	 * @param text
	 * @return
	 */
	protected List<CheckResult> extract(String text) {
		Matcher m = checkPattern.matcher(text);
		List<CheckResult> results = new ArrayList<CheckResult>();
		// [2]id:value="id"
		// [5]article:<a>article</a>
		// [8]author:<a>author</a>
		// [11]content:<div title="文字复制比">0%</div><div title="重合字数">0</div>
		// [14]date
		// [17]downloadurl:<a target="_blank"
		// href=" http://checkdownload.cnki.net/thesisdownload/?downType=0&user=yourname&userServerID=1&fileID=fileId&check=5f6cad62f7fa352955321bc1b4989912"
		// >
		while (m.find()) {
			String content = m.group(11);
			long id = Long.valueOf(StringUtils.substringBetween(m.group(2),
					"value=\"", "\""));
			String thesis = StringUtils.substringBetween(m.group(5), ">",
					"</a>");
			String author = StringUtils.substringBetween(m.group(8), ">",
					"</a>");
			if (content.contains("文字复制比")) {
				String ratioStr = StringUtils.substringBetween(content,
						"文字复制比\">", "%</div>");
				float ratio = Float.valueOf(ratioStr) / 100;
				int count = Integer.valueOf(StringUtils.substringBetween(
						content, "重合字数\">", "</div>"));
				Date checkOn = Date.valueOf(m.group(14).trim());
				String checksum = StringUtils.substringBetween(m.group(17),
						"check=", "\"");
				results.add(new CheckResult(id, thesis, author, checksum,
						checkOn, ratio, count));
			} else {
				Date checkOn = Date.valueOf(m.group(14).trim());
				String checksum = StringUtils.substringBetween(m.group(17),
						"check=", "\"");
				results.add(new CheckResult(id, thesis, author, checksum,
						checkOn));
			}

		}
		return results;
	}

	/**
	 * 根据网页内容构建查询信息的参数
	 * 
	 * @param content
	 */
	private void buildInfoParams(String content) {
		Pattern pattern = Pattern
				.compile("<select([\\s\\S]*?)\"(\\d*?)\"([\\s\\S]*?)</select>");
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			foldId = matcher.group(2);
		}
		infoParams.put("ID", foldId);
		String[] paramNames = new String[] { "__VIEWSTATE",
				"__EVENTVALIDATION", "__VIEWSTATEENCRYPTED" };
		for (String paramName : paramNames) {
			String v = getHiddenValue(content, paramName);
			infoParams.put(paramName, v);
		}
	}

	private List<NameValuePair> convertToValuePairs(Map<String, String> merged) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : merged.entrySet()) {
			nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		return nvps;
	}

	private HttpResponse accessTo(String url) {
		HttpGet innerget = new HttpGet(url);
		try {
			HttpResponse response = httpclient.execute(innerget);
			HttpEntity entity = response.getEntity();
			System.out.println(EntityUtils.toString(entity));
			EntityUtils.consume(entity);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getHiddenValue(String text, String name) {
		String prefix = "id=\"" + name + "\" value=\"";
		int startIndex = text.indexOf(prefix);
		if (startIndex > 0) {
			int endIndex = text.indexOf("\"", startIndex + prefix.length());
			if (endIndex > 0)
				return text.substring(startIndex + prefix.length(), endIndex);
		}
		return null;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
	}

	public String getInfoUrl() {
		return infoUrl;
	}

	public void setInfoUrl(String infoUrl) {
		this.infoUrl = infoUrl;
	}

	public String getLoginCaptchaUrl() {
		return loginCaptchaUrl;
	}

	public void setLoginCaptchaUrl(String loginCaptchaUrl) {
		this.loginCaptchaUrl = loginCaptchaUrl;
	}

}
