//
//  ========================================================================
//  Copyright (c) 1995-2013 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package a9bjb.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import a9bjb.core.StringParser;
import a9bjb.core.DrillController;

@SuppressWarnings("serial")
@WebServlet("/fileInput")
public class FilePathServlet extends HttpServlet //인풋으로 가져온 패스를 이용하여 JDBC에 실제로 연결하는 단계입니다.
{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	StringParser sp = new StringParser();
    	PrintWriter out = response.getWriter();
    	
    	HashMap<String, String> inputVal;
		try {
			System.out.println(request.getParameter("inputData"));
			//로컬 파일 패스라면 String 배열 1, hdfs 주소가 들어오면 총 4의 크기의 String 배열 생성.
			inputVal = sp.getHTMLComponant(request.getParameter("inputData"));
			
			ServletContext sc = request.getSession().getServletContext();			
			DrillController dc = (DrillController) sc.getAttribute("drillController");

			//Drill JDBC에 실질적인 연결을 시작함.
			dc.connectDrillJDBC(inputVal);
	    		    	
	    	response.setContentType("text/plain; charset=UTF-8");
	    	
	    	if(dc.getIsDir()){
		    	out.print("Directory");
	    	} else {
	    		if(sp.checkIsJSONFile(dc.getRawFilePath())){
			    	out.print("JSONFile");	    			
	    		} else {
			    	out.print("NormalFile");
	    		}
	    	}
	    	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			response.sendError(500, "Error : " + e.getMessage());
		}
    }
}
