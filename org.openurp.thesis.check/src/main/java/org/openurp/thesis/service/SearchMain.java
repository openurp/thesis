package org.openurp.thesis.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.openurp.thesis.service.impl.CnkiThesisCheckServiceImpl;

public class SearchMain {

	public static void main(String[] args) throws Exception {
		CnkiThesisCheckServiceImpl check = new CnkiThesisCheckServiceImpl();
		File captchaFile = check.getCaptcha();
		BufferedReader stdin = new BufferedReader(new InputStreamReader(
				System.in));
		System.out.print("UserName:");
		String username = stdin.readLine();
		System.out.print("Password:");
		String password = stdin.readLine();
		System.out.print("Captcha(" + captchaFile.getAbsolutePath() + "):");
		String captcha = stdin.readLine();

		if (check.login(username, password, captcha)) {
			System.out
					.println("login success,and start query.\nPlease enter query/report/exist(or exit).");
			String mode = "query";
			while (true) {
				System.out.print(mode + ":");
				String input = stdin.readLine();
				if (input.equals("exit"))
					break;
				if (input.equals("report")) {
					mode = input;
					continue;
				}
				if (input.equals("query")) {
					mode = input;
					continue;
				}
				if (mode.equals("query")) {
					String article = null;
					String author = input;
					if (input.contains(" ")) {
						article = StringUtils.substringAfter(input, " ");
						author = StringUtils.substringBefore(input, " ");
					}
					List<CheckResult> rs = check.search(author, article);
					for (CheckResult cr : rs)
						System.out.println(cr);
				} else {
					if (!NumberUtils.isNumber(input)) {
						System.out.println("invalid report id");
						continue;
					}
					Long id = Long.valueOf(input);
					String content = check.report(id, ReportStyle.Simple);
					File tmp = File.createTempFile("report", ".html");
					FileUtils.writeStringToFile(tmp, content);
					System.out.println("save report in "
							+ tmp.getAbsolutePath());
				}
			}
			// check.logout();
		} else {
			System.out.println("login failure");
		}
	}
}
