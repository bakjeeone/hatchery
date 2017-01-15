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

import a9bjb.core.DrillController;
import a9bjb.core.StringParser;
import a9bjb.wrapper.JoinInfo;

@SuppressWarnings("serial")
@WebServlet("/getColumnList")
public class GetColumnListServlet extends HttpServlet //컬럼정보를 가져옵니다.
{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	StringParser sp = new StringParser();
    	PrintWriter out = response.getWriter();
    	
		try {
			
			ServletContext sc = request.getSession().getServletContext();			
			DrillController dc = (DrillController) sc.getAttribute("drillController");
	    	
	    	String inputVal = request.getParameter("joinData");			
	    	String jsonFlag = request.getParameter("jsonFlag");			

			if(inputVal == null){//join을 하지 않는 상태에서 column list를 불러온다면?
				System.out.println("input Val null! : no Join");
				dc.makeGetColumnsQuery();
			} else {
				System.out.println("input Val not null! : do Join");
		    	ArrayList<JoinInfo> joinInfos = sp.getJoinDatas(inputVal);
				dc.makeGetColumnsQuery(joinInfos);
			}

			ArrayList<String> columns = dc.getColumnList();

			if(jsonFlag == null){ //만약 json 파일이 아니라면?
		    	response.setContentType("text/plain; charset=UTF-8");	    			    	
		    	for(int i = 0; i < columns.size(); i++){
		    		out.print("<option value="+i+">"+columns.get(i)+"</option>");
		    	}
			} else {
				if(jsonFlag.equals("getDetail")){
			    	response.setContentType("application/json; charset=UTF-8");
			    	ArrayList<String> columnDatas = dc.getFirstColumnData();
		    		out.print(sp.makeJSONString(columns, columnDatas));
				} else if(jsonFlag.equals("getTempTable")) { //attributes, datas
					System.out.println("getTempTable start");
					
					String originQuery = dc.getDrillGetColumnQuery();
																			
			    	String[] flattenDataTemp = request.getParameterValues("flattenDataTemp[]");
			    	String[] flattenNameTemp = request.getParameterValues("flattenNameTemp[]");
			    	
			    	ArrayList<String> columnDatas;

			    	boolean emptyFlag = true;
			    	
			    	String tempQuery = "initial";
			    	if(flattenDataTemp != null){			    		
				    	String tempQuertFlatten = "";
						for(int i = 0; i < flattenDataTemp.length; i++){
							System.out.println("flattenDataTemp : " + flattenDataTemp[i]);
							tempQuertFlatten += ", flatten("+flattenDataTemp[i]+") as "+flattenNameTemp[i];
						}
						System.out.println("getFirstColumnData!!!!");
						tempQuery  = "select *" + tempQuertFlatten + " " + originQuery.substring(9);						
						dc.setDrillGetColumnQuery(tempQuery);
						emptyFlag = false;
			    	}
			    	
			    	columnDatas = dc.getFirstColumnData();
			    	columns = dc.getColumnList();

			    	if(request.getParameter("tabOrSave").equals("li")){ //table Tab이면 
				    	if(!emptyFlag){
				    		dc.setDrillGetColumnQuery(originQuery);
				    		System.out.println("Drill Query Restored!!!!");
				    	}
			    	} else {
			    		System.out.println("Drill Query Changed!!!!");
			    		if(!tempQuery.equals("initial")) dc.setMakeFileFromQuery_json(tempQuery);
			    	}
			    	
			    	response.setContentType("application/json; charset=UTF-8");
		    		out.print(sp.makeTempTableJSON(columns, columnDatas));
				}
		    	
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			response.sendError(500, "Error : " + e.getMessage());
		}
    }
}
