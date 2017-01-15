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
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import a9bjb.core.StringParser;
import a9bjb.core.DrillController;

@SuppressWarnings("serial")
@WebServlet("/clusterSetup")
public class ClusterServlet extends HttpServlet //Drill JDBC URI를 생성합니다.
{
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    	PrintWriter out = response.getWriter();
    	StringParser sp = new StringParser();
    	String inputVal = request.getParameter("clusterData");
    	String clusterType = request.getParameter("clusterType");
    	ArrayList<String> clusterData = sp.getClusterDatas(inputVal);
    	
		try {
			ServletContext sc = request.getSession().getServletContext();			
			DrillController dc = (DrillController) sc.getAttribute("drillController");
			
			//JDBC URI 생성.
	    	dc.setJDBC_URI(clusterData, clusterType);
	    	response.setContentType("text/plain; charset=UTF-8");
	    	out.println("finish");
	    	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			response.sendError(500, "Error : " + e.getMessage());
		}
    }
}
