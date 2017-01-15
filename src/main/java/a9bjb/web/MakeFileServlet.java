package a9bjb.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import a9bjb.core.DrillController;

@SuppressWarnings("serial")
@WebServlet("/makeFile")
public class MakeFileServlet extends HttpServlet //파일 생성을 위한 서블릿 
{		
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	String colIdx[] = request.getParameterValues("colIdx[]");
    	String colName[] = request.getParameterValues("colName[]");
    	String endFileType = request.getParameter("fileType");
    	System.out.println(endFileType);
    	String outputName = request.getParameter("outputName");
    	
    	ServletContext sc = request.getSession().getServletContext();
		
    	for(int i = 0; i < colName.length; i++) System.out.println("MakeFileServelt colName : " + colName[i]);
    	
		DrillController dc = (DrillController) sc.getAttribute("drillController");

		try {
			long startTime = System.nanoTime();

			dc.makeFile(endFileType, colIdx, colName, outputName);
			
			long endTime = System.nanoTime();			 
			// Total time
			long lTime = endTime - startTime;
			System.out.println("Make File TIME : " + lTime/1000000.0 + "(ms)");
			
			
			response.setContentType("text/plain; charset=UTF-8");
	    	
	    	PrintWriter out = response.getWriter();
	    	out.print("finish");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.sendError(500, "Error : " + e.getMessage());
		}
    }
}
