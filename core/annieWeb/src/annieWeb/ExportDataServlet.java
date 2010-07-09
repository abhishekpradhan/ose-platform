package annieWeb;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lbjse.data.DocumentFromTrec;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

import ose.database.DocTag;
import ose.database.DocTagManager;
import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.html.HtmlParser;
import ose.html.ParsedHtmlDocument;
import ose.index.IndexFieldConstant;
import ose.query.OQuery;
import ose.tools.IndexExporter;

/**
 * Servlet implementation class for Servlet: FeatureInfoServlet
 *
 */
 public class ExportDataServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public ExportDataServlet() {
		super();
		//initialize database to connect to the right one
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		int domainId = 0;
		if (request.getParameter("domainId") != null){
			domainId = Integer.parseInt(request.getParameter("domainId"));
		}
		else {
			response.getWriter().println("domainId is required");
			return;
		}
		
		int indexId = 0;
		if (request.getParameter("indexId") != null){
			indexId = Integer.parseInt(request.getParameter("indexId"));
		}
		else {
			response.getWriter().println("indexId is required");
			return;
		}
		
		try {
			IndexExporter exporter = new IndexExporter();
			String fileName = exporter.exportTrecFileFromIndex(indexId, domainId);
			System.out.println("Trecs exported to " + fileName);
			returnFileToDownload(fileName, response);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	private void returnFileToDownload(String fileName, HttpServletResponse resp) throws IOException{
		File f = new File(fileName);
		int length   = 0;
		ServletOutputStream op       = resp.getOutputStream();
		//
		//  Set the response and go!
		//
		//
		resp.setContentType( "application/octet-stream" );
		resp.setContentLength( (int)f.length() );
		resp.setHeader( "Content-Disposition", "attachment; filename=\"" + f.getName() + "\"" );
		
		//
		//  Stream to the requester.
		//
		byte[] bbuf = new byte[10000];
		DataInputStream in = new DataInputStream(new FileInputStream(f));
		
		while ((in != null) && ((length = in.read(bbuf)) != -1))
		{
		    op.write(bbuf,0,length);
		}
		
		in.close();
		op.flush();
		op.close();

	}
	
}