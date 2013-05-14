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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.openurp.thesis.service.impl.CnkiThesisCheckServiceImpl;

public class SearchMain {

  public static void main(String[] args) throws Exception {
    CnkiThesisCheckServiceImpl check = new CnkiThesisCheckServiceImpl();
    File captchaFile = check.getCaptcha();
    BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    System.out.print("UserName:");
    String username = stdin.readLine();
    System.out.print("Password:");
    String password = stdin.readLine();
    System.out.print("Captcha(" + captchaFile.getAbsolutePath() + "):");
    String captcha = stdin.readLine();

    if (check.login(username, password, captcha)) {
      captchaFile.delete();
      System.out.println("login success,and start query.\nPlease enter query/report/exist(or exit).");
      String mode = "query";
      while (true) {
        System.out.print(mode + ":");
        String input = stdin.readLine();
        if (input.equals("exit")) break;
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
          File tmp = check.report(id, ReportStyle.Detail);
          System.out.println("save report in " + tmp.getAbsolutePath());
        }
      }
      // check.logout();
    } else {
      System.out.println("login failure");
    }
  }
}
