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
package org.openurp.thesis.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openurp.thesis.service.CheckResult;
import org.testng.annotations.Test;

@Test
public class TextExtract {

  public void textExtract() throws IOException, URISyntaxException {
    CnkiThesisCheckServiceImpl check = new CnkiThesisCheckServiceImpl();
    String text = FileUtils.readFileToString(new File(TextExtract.class.getResource("/queryresult.html")
        .toURI()));
    List<CheckResult> results = check.extract(text);
    for (CheckResult cr : results) {
      System.out.println(cr);
    }
  }
}
