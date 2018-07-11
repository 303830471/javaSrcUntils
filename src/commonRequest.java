import java.io.*;
import java.net.*;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;

public class commonRequest extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public commonRequest() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("commonRequest Servlet doGet Begin");
		doPost(request,response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//		System.out.println(" ==commonRequest====post=====begin===");
		
		String LtpaToken = "";
		String requestType = request.getParameter("requesttype");
		String requesturl = request.getParameter("requesturl");
		String parametername = request.getParameter("parametername");
		String parametervalue = request.getParameter("parametervalue");
		Cookie[] cookies = request.getCookies();
		if( cookies != null && cookies.length>0 ) {
			for( Cookie c:cookies ){
//				System.out.println(c.getName()+  " === " + c.getValue());
				if(c.getName().equals("LtpaToken")){
					LtpaToken = c.getValue();
				}
			}
		}
		FileWriter fw1=new FileWriter("C:\\commonRequestLog.txt");
		fw1.write("requestType="+requestType);
		fw1.write("requesturl="+requesturl);
		fw1.write("parametername="+parametername);
		fw1.write("parametervalue="+parametervalue);
		fw1.close();
		HttpURLConnection connection = null;	
		java.io.OutputStream os=null; 
		OutputStreamWriter osw=null;
		java.io.InputStream is =null;
		BufferedWriter bWriter = null;
		URL httpurl = new URL(requesturl);
		connection = (HttpURLConnection) httpurl.openConnection();	
		//connection.HTTP_CLIENT_TIMEOUT=30000;
		//System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
		//System.setProperty("sun.net.client.defaultReadTimeout", "60000"); 	
		//connection.setConnectionTimeout("60000");
		connection.setRequestMethod(requestType);
		connection.setRequestProperty("Cookie", "LtpaToken="+LtpaToken);

		if(requesturl.toLowerCase().indexOf("Produce/DigiFlowScriptCss.nsf/X_GridDialogGetData.xsp".toLowerCase())>0){
			//获取请假类型
			response.setContentType("application/json; charset=utf-8");  
			connection.setRequestProperty("Content-Type","multipart/form-data; boundary=----footfoodapplicationrequestnetwork; charset=UTF-8");
			connection.setDoOutput(true);	
			//以流的方式将信息加入
			StringBuffer buffer = new StringBuffer();
	        if(parametername != null){  
	        	buffer.append("------footfoodapplicationrequestnetwork\r\n");  
	            buffer.append("Content-Disposition: form-data; name=\"");
	            buffer.append(parametername);
	            buffer.append("\"\r\n\r\n");
	            buffer.append(parametervalue);
	            buffer.append("\r\n");  
	            buffer.append("------footfoodapplicationrequestnetwork--\r\n");  
	        }  
			os=connection.getOutputStream();
			os.write(buffer.toString().getBytes());
		}else if(requesturl.toLowerCase().indexOf("X_GetSerialNumber.xsp".toLowerCase())>0){
			//生成申请单号application/x-www-form-urlencoded; charset=UTF-8
			response.setContentType("application/json; charset=utf-8"); 
			connection.setRequestProperty("Content-Type","multipart/form-data; boundary=----footfoodapplicationrequestnetwork; charset=UTF-8");
			connection.setDoOutput(true);	
			//以流的方式将信息加入
			StringBuffer buffer = new StringBuffer();
	        if(parametervalue != null){  
	        	try {
					JSONObject jsonObj = new JSONObject(parametervalue);
					System.out.println("==startStr--value="+jsonObj.get("startStr"));
					Iterator itkeys = jsonObj.keys();
					while(itkeys.hasNext()) {  
						String keyname = (String) itkeys.next();
					    System.out.print(keyname + " " + jsonObj.get(keyname));  
			        	buffer.append("------footfoodapplicationrequestnetwork\r\n");  
			            buffer.append("Content-Disposition: form-data; name=\"");
			            buffer.append(keyname);
			            buffer.append("\"\r\n\r\n");
			            buffer.append(jsonObj.get(keyname));
			            buffer.append("\r\n");  
					}  
		            buffer.append("------footfoodapplicationrequestnetwork--\r\n");  
				} catch (JSONException e) {
					e.printStackTrace();
				}
	        	
	        }  
			os=connection.getOutputStream();
			os.write(buffer.toString().getBytes());
		}else{
			/*
			 * countDateTimeNew“计算请假时间@*@请假天数@*@是否含有重复请假天数；
			 * getMonthEndYearMonth：获取考勤截止日
			 * countRemainYearVacationDays：获取剩余年假天数
			 */
			response.setContentType("text/html;charset=utf-8");
			connection.setDoOutput(true);	
			os=connection.getOutputStream();
			osw=new OutputStreamWriter(os);
			bWriter = new BufferedWriter(osw);
			bWriter.write(parametervalue);
			bWriter.flush();
		}
		
		String sCurrentLine = "";
		String sTotalString = "";
		int res = connection.getResponseCode();			
		if (res == 200) {
			is = connection.getInputStream();
			sTotalString=inputStream2String(is);
		} else {
			try {
				throw new Exception("failing connect");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		FileWriter fw=new FileWriter("C:\\commonRequestreposeLog.txt");
		fw.write("sTotalString="+sTotalString);
		fw.close();
		
		PrintWriter out = response.getWriter();
//		out.println("Content-type: application/json;charset=UTF-8");
		out.println(sTotalString);
		out.flush();
		out.close();
	}

	/**
	 * Returns information about the servlet, such as 
	 * author, version, and copyright. 
	 *
	 * @return String information about this servlet
	 */
	public String getServletInfo() {
		return "This is my default servlet created by Eclipse";
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}
	public static String inputStream2String(InputStream is) throws IOException{ 
		ByteArrayOutputStream baos=new ByteArrayOutputStream(); 
		int i=-1; 
		while((i=is.read())!=-1){ 
			if ((i!=13) && (i!=10)) { 		
				baos.write(i);
			}
		} 
		return new String(baos.toByteArray(),"UTF-8");
	} 
	/**
	 * 函数功能：计算请假时间、请假天数、是否含有重复请假天数
	 * @return 三个字段用“@*@”分割
	 */
	public String getCountDateTime() {
		return "This is my default servlet created by Eclipse";
	}
}
