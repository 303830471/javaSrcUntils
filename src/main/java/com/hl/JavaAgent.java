package com.hl;

/*
编写人：hl
功能：修改动态表格数据
*/
import lotus.domino.*;
import java.lang.String;
import java.net.URLDecoder;
import org.json.*;

import com.hl.StringUtils;
public class JavaAgent extends AgentBase{
	private Session session;
	private Database db;
	private Database appdb;
	private lotus.domino.Document appdoc;
	private View v;
	private lotus.domino.Document doc;
	public void NotesMain() {
		String qstr = "";
		String UNID = "";
		JSONArray jsonarray = null ;
		try {
			session = getSession();
			AgentContext agentContext = session.getAgentContext();
			
			lotus.domino.Document cdoc = agentContext.getDocumentContext();
			qstr = cdoc.getItemValueString("Query_String_Decoded");
			UNID = getPrameter("UNID",qstr);
			
			if (cdoc.hasItem("Request_Content")){
				qstr = java.net.URLDecoder.decode(cdoc.getItemValueString("Request_Content"),"utf-8");
				jsonarray = new JSONArray( qstr );
			}else{
				qstr = "";
				for(int n=0;n<6;n++){
					if (cdoc.hasItem("Request_Content_00"+n)){
						qstr = qstr + cdoc.getItemValueString("Request_Content_00"+n);
					}
				}
				qstr = java.net.URLDecoder.decode(qstr,"utf-8");
				jsonarray = new JSONArray( qstr );
			}
			System.out.println(jsonarray.toString());
			appdb = session.getCurrentDatabase();
			if(UNID.length()==32)appdoc = appdb.getDocumentByUNID(UNID);
			if(appdoc.getItemValueString("T_Rtf_tbl_sapold").equals("")){
				PrintMsg("error", "回传表为空！");
				return;
			}
			StringUtils strutil = new StringUtils();
			
			JSONArray sapoldArr = new JSONArray( strutil.replaceBlank( appdoc.getItemValueString("T_Rtf_tbl_sapold")) );
			
			for (int i=0;i<jsonarray.length();i++){
				
				JSONObject jsonobj = jsonarray.getJSONObject(i); 
				if(!"".equals(jsonobj.getString("VBELN"))&&!"".equals(jsonobj.getString("key_POSNR"))){
					for (int m=0;m<sapoldArr.length();m++){
						JSONObject oldjsonobj = sapoldArr.getJSONObject(m); 
						if(oldjsonobj.getString("key_POSNR").equals(jsonobj.getString("key_POSNR"))){
//							Object object = jsonobj.get("MATXP");
							oldjsonobj.put("oa_MATXP",jsonobj.get("MATXP"));//更新值
						}
					}
				}
			}
			appdoc.replaceItemValue("T_Rtf_tbl_sapold", sapoldArr.toString());
			appdoc.replaceItemValue("T_Rtf_tbl_sap", jsonarray.toString());
			appdoc.save(true);
			PrintMsg("ok", "操作成功！");
		
		}catch(Exception e) {
			e.printStackTrace();
			try {
				PrintMsg("error", e.getMessage());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}finally{
			try{
				if(appdoc!=null)appdoc.recycle();
				if(doc!=null)doc.recycle();
				if(v!=null)v.recycle();
				if(appdb!=null)appdb.recycle();
				if(db!=null)db.recycle();
				if(session!=null)session.recycle();	
				
			}catch(Exception de){}
		}
	}
	
	protected String getPrameter(String str, String src) {
		try {
			if (src.equals("")) {return "";}
			String rs="";
			String srcArray[] = src.split("&");
			for (int i = 0; i < srcArray.length; i++) {
				if(srcArray[i].indexOf(str)>=0){
					int po = srcArray[i].indexOf("=");
					if (po >= 0) {
						 rs = srcArray[i].substring(po+1, srcArray[i].length());
						try {
							return URLDecoder.decode(rs, "UTF-8");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	/*
	函数功能：向前台输出返回信息
	*/
	public void PrintMsg(String result, String info) throws Exception {
		java.io.PrintWriter pw = getAgentOutput();
		pw.println("Content-type: application/json;charset=GB2312");
		JSONObject jrowObj=new JSONObject();
		try{
			jrowObj.put("result", result);
			jrowObj.put("info", info);
			pw.println(jrowObj.toString());	
		}catch (Exception e){
			jrowObj.put("result", result);
			jrowObj.put("info", info);
			pw.println(jrowObj.toString());	
		}
	}	


}
