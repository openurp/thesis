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

	public void textExtract() throws IOException, URISyntaxException{
		CnkiThesisCheckServiceImpl check= new CnkiThesisCheckServiceImpl();
		String text = FileUtils.readFileToString(new File(TextExtract.class.getResource("/queryresult.html").toURI()));
		List<CheckResult> results = check.extract(text);
		for(CheckResult cr:results){
			System.out.println(cr);
		}
	}
}
