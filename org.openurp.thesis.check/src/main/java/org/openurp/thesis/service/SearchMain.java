package org.openurp.thesis.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openurp.thesis.service.impl.CnkiThesisCheckServiceImpl;

public class SearchMain {

	public static void main(String[] args) throws Exception {
		CnkiThesisCheckServiceImpl check = new CnkiThesisCheckServiceImpl();
		File captchaFile = check.getCaptcha();
		Map<String, String> params = new HashMap<String, String>();
		BufferedReader stdin = new BufferedReader(new InputStreamReader(
				System.in));
		System.out.print("UserName:");
		String username = stdin.readLine();
		System.out.print("Password:");
		String password = stdin.readLine();
		System.out.print("Captcha(" + captchaFile.getAbsolutePath() + "):");
		String captcha = stdin.readLine();

		if (check.login(username, password, captcha, params)) {
			System.out
					.println("login success,and start query please enter author and article(or exit).");
			while (true) {
				System.out.print("author:");
				String author = stdin.readLine();
				if (author.equals("exist"))
					break;
				String article = null;
				if (author.contains(" ")) {
					article = StringUtils.substringAfter(author, " ");
					author = StringUtils.substringBefore(author, " ");
				}
				List<CheckResult> rs = check.search(author, article);
				for (CheckResult cr : rs)
					System.out.println(cr);
			}
			// check.logout();
		} else {
			System.out.println("login failure");
		}
	}
}
