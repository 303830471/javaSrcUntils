package com.sap.jco.demo;


import com.sap.mw.jco.*;
import lotus.domino.*;

public class SapPubInfo {
	private String ErrorMsg="";
	private Session session ;
	private Database db;
	private View view;
	private Document doc;
	public JCO.Client getMConnection(String SapConName) {
		JCO.Client mConnection=null;
		
		String client 		= "";
		String userid 		= "";
		String password 	= "";
		String language 	= "";
		String host 		= "";
		String system 		= "";
		String ConName      = SapConName;
		String flag         ="";
		try {
			session = NotesFactory.createSession();
			AgentContext agentContext = session.getAgentContext();	
			//Database db = agentContext.getCurrentDatabase();
			db = session.getDatabase("", "Produce/DigiSapJcoConfig.nsf");
			view = db.getView("ConListView");
			doc = view.getFirstDocument();
			while(doc != null)
			{
			if (doc.getItemValueString("StConName").equals(ConName))
			     {
				client 	= (String)doc.getItemValueString("STClient");
				userid 	= (String)doc.getItemValueString("StUserID");
				password 	= (String)doc.getItemValueString("StPassword");
				language 	= (String)doc.getItemValueString("StLanguage");
				host 	= (String)doc.getItemValueString("StHost");
				system 	= (String)doc.getItemValueString("StSystem");
				System.out.println("password"+password);
				flag      ="1";
				break;
		           }
				 doc = view.getNextDocument(doc);
			 }
			 if(!flag.equals("1"))
			 {
			 	ErrorMsg = "没有找到相应的Jco连接！";
				System.out.println(ErrorMsg);
			 	return null;
			 	
			 }
			

			try {
				mConnection = JCO.createClient( client,       // SAP client
    				 userid,		// userid
    				 password,	// password
    				 language,	// language
    				 host, 		// host name
    				 system );	// system number
				mConnection.connect();
			} catch (Exception ex) {
				ErrorMsg="Jco连接配置不正确！";
				System.out.println(ErrorMsg);
				return null;
			}
			return mConnection;

		} catch(Exception e) 
		{
			ErrorMsg="发生意外错误";	
			System.out.println(ErrorMsg);	
			e.printStackTrace();
			return null;
		}
		finally
		{
			try 
			{
				if(doc!=null)
					doc.recycle();
				if(view!=null)
					view.recycle();
				if(db!=null)
					db.recycle();
				if(session!=null)
					session.recycle();
				System.gc();
			} 
			catch (Exception e) 
			{}
		}
	}
	public String getErrorMsg()
	{
		return ErrorMsg;
	}
	
}