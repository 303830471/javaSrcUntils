package com.sap.jco.demo;

/*
功能：生成固资编号
*/
import lotus.domino.*;
import com.sap.mw.jco.*;
import java.util.regex.*;
import java.lang.String;
import java.net.URLDecoder;

import org.json.JSONObject;
public class JavaAgent extends AgentBase {
	private JCO.Repository mRepository;
	private JCO.Client mConnection;
	private Session session;
	private Database db;
	private View v;
	private lotus.domino.Document doc;
	private Database appDb;
	
	lotus.domino.Database mbdb = null;
	lotus.domino.Document appdoc = null;
	lotus.domino.Document mbdoc = null;
	lotus.domino.Document mbdocnext = null;
	lotus.domino.Document AssetDetailApprovalSubDoc = null;
	lotus.domino.DocumentCollection AssetDetailApprovalSubDc;
	lotus.domino.DocumentCollection mbdc;
	lotus.domino.View appvw;
	lotus.domino.View mbvw;
	lotus.domino.View AssetDetailApprovalSubvw;
	String StUserCompanyCode;
	String StInternalOrderHRID;
	String StPayTypeHRID;
	String StUserDeptName;
	String StUserCn;
	String StPsnCN;
	String StPsnEN;
	String WorkPlace;
	String BusinessScope;
	String EquityInvestmentProjectsText;
	String queryString = "";
	public void NotesMain() {
		String ErrorMsg="";
		String[] UNIDS = null;
		try {
			session = getSession();
			AgentContext agentContext = session.getAgentContext();
			db=session.getDatabase("","Produce/DigiSapJcoConfig.nsf");
			v=db.getView("BasicDataConfigView");
			doc=v.getDocumentByKey("CreateItem",true); //具体应用中修改本句代码，getDocumentByKey第一个参数修改为“基本数据导出配置表单”文档中相应的配置名称
			if(doc!=null){
				String inputFieldXml=doc.getItemValueString("RTFInputXML_1");
				String outputTableXml=doc.getItemValueString("RTFOutputTableXML_1");
				//2009-1-15-----------------bug修改-----------
				String s=outputTableXml;
				String reg="[\n-\r]";
				Pattern p2=Pattern.compile(reg);
				Matcher m=p2.matcher(s);
				outputTableXml=m.replaceAll("");
				Matcher m2=p2.matcher(inputFieldXml);
				inputFieldXml=m2.replaceAll("");
				//2009-1-15-----------------bug修改-----------
				String functionName=doc.getItemValueString("StFunName");
				String connectName=doc.getItemValueString("STxtSapFunctionText");
				String dbName=doc.getItemValueString("STxtDatabase");
				String formName=doc.getItemValueString("STxtFormID");
				String viewName=doc.getItemValueString("STxtViewText");
				String TableNum=doc.getItemValueString("StTableNum").trim();
				Format fmt=new Format();
				inputFieldXml=fmt.transUnicode(inputFieldXml);
				outputTableXml=fmt.transUnicode(outputTableXml);
				connectName=fmt.transUnicode(connectName);
				
				SapPubInfo spi = new SapPubInfo();
				mConnection = spi.getMConnection(connectName);
				ErrorMsg=spi.getErrorMsg();
				if(!ErrorMsg.equals("")){
					PrintMsg("error", "连接Sap失败！");
					return;
				}else{
					mRepository = new JCO.Repository("ARAsoft", mConnection);
					JCO.Function function = null;
					JCO.ParameterList inputParameterList;
					function = this.createFunction(functionName);
					if (function == null){
						ErrorMsg=functionName+"函数在SAP中不存在！";
						PrintMsg("error", ErrorMsg);
						return;
					}else{
						inputParameterList = function.getImportParameterList();
						lotus.domino.Document cdoc = agentContext.getDocumentContext();
						
						String materialkey = "";
						queryString = cdoc.getItemValueString("Query_String_Decoded");
						materialkey = getPrameter("StRelateMatreialID",queryString).trim();
						if (cdoc!=null && cdoc.hasItem("Request_Content")){
							UNIDS = cdoc.getItemValueString("Request_Content").split(",");
							System.out.print("cdoc.getItemValueString(Request_Content)= "+cdoc.getItemValueString("Request_Content"));
						}
						System.out.print("materialkey= "+materialkey);
						mbdb = session.getDatabase("","AD/AssetDetailData.nsf");
						mbvw = mbdb.getView("AllViewByStRelateMatreialID");
						AssetDetailApprovalSubvw = mbdb.getView("AssetDetailApprovalSub");					
						mbdc = mbvw.getAllDocumentsByKey(materialkey,true);
						mbdoc = mbdc.getFirstDocument();
						appDb=session.getDatabase("","AD/InternalCollar.nsf");
						appvw = appDb.getView("AllViewByStRelateMatreialID");
						appdoc = appvw.getDocumentByKey(materialkey,true);
						
						if (appdoc != null){
							 StUserCompanyCode = appdoc.getItemValueString("StCompany");  //使用公司编码
							 StInternalOrderHRID = appdoc.getItemValueString("StInternalOrderHRID");  //内部订单号
							 if (!appdoc.getItemValueString("StPayTypeHRID").equals("")){   //资产所属成本中心号
								 StPayTypeHRID = appdoc.getItemValueString("StPayTypeHRID"); 
							 }else{
								 StPayTypeHRID = appdoc.getItemValueString("StPayTypeHRID");  //成本中心号
							 }
							 StUserDeptName = appdoc.getItemValueString("yewu_zichansuoshubumen_mingcheng");  //资产所属部门
							 StUserCn = appdoc.getItemValueString("StUsePeopleCN");   //使用人StUserCn
							 StPsnCN = appdoc.getItemValueString("PersonCN");   //申请人
							 StPsnEN = appdoc.getItemValueString("PersonEN");   //申请人ITCODE
							 WorkPlace = appdoc.getItemValueString("WorkPlace");   //申请人所在工作地
							 BusinessScope = appdoc.getItemValueString("ScopeBusiness");   //业务范围BusinessScope
							 EquityInvestmentProjectsText = appdoc.getItemValueString("EquityInvestmentProjectsText");   //项目号EquityInvestmentProjectsText
							 if(EquityInvestmentProjectsText.equals(""))EquityInvestmentProjectsText = appdoc.getItemValueString("StProjectNum"); 
						}else{  PrintMsg("error", "流程文档未找到！ "); return; }
						
						System.out.print( "使用人= " +StUserCn+" 业务范围= "+BusinessScope+" 使用人部门= "+StUserDeptName+" 项目号= "+EquityInvestmentProjectsText+" 使用人公司= "+StUserCompanyCode+" 成本中心号= "+StPayTypeHRID+" 内部订单号= "+StInternalOrderHRID );
						if(UNIDS==null){PrintMsg("error", "unid参数传递失败，请联系系统管理员！"); return;}
						for(int i=0;i<UNIDS.length;i++){
							mbdoc = mbdb.getDocumentByUNID(UNIDS[i]);
							if (mbdoc !=null){
								if (mbdoc.getItemValueString("MtMaterialDeion").equals("") || mbdoc.getItemValueString("MtMaterialNummer").equals("") ){
									PrintMsg("error", "物料描述、物料号不能为空！");
									return;
								}
								if (mbdoc.getItemValueString("MtappAssetNum").equals("")){
									System.out.print("资产分类= " + mbdoc.getItemValueString("StITEMClass"));
									System.out.print("产品名称/厂家= " + mbdoc.getItemValueString("MtMaterialDeion").replace(" ", "")+"/"+mbdoc.getItemValueString("ManufacturerBuy")+" 配置= "+mbdoc.getItemValueString("MtMaterialDeion")+" 规格型号= "+mbdoc.getItemValueString("MtMaterialNummer")+"00"+mbdoc.getItemValueString("MtOrderNummer"));
									
									JCO.Structure sFrom = inputParameterList.getStructure("ANLA");
								
									sFrom.setValue(StUserCompanyCode, "BUKRS");
									sFrom.setValue(StPayTypeHRID, "KOSTL");
									sFrom.setValue(StInternalOrderHRID, "CAUFN");
									sFrom.setValue(mbdoc.getItemValueString("StITEMClass"), "ANLKL");  //资产分类StITEMClassCode
									sFrom.setValue(mbdoc.getItemValueString("MtMaterialDeion"), "TXT50");  //资产描述
									sFrom.setValue(mbdoc.getItemValueString("MtMaterialDeion"), "TXA50");  //附加资产描述
									sFrom.setValue(mbdoc.getItemValueString("MtMaterialNummer")+"00"+mbdoc.getItemValueString("MtOrderNummer"), "ANLHTXT");  //规格型号SizeModelBuy【资产主号说明】
									sFrom.setValue(StUserDeptName, "INVNR");
									if(StUserCn.equals(StPsnCN))sFrom.setValue(StUserCn, "INVZU");
									else sFrom.setValue(StPsnCN+"/"+StUserCn, "INVZU");  //申请人/使用人
									sFrom.setValue(WorkPlace, "RAUMN");  //工作地
									sFrom.setValue(StPsnEN, "TYPBZ");  //申请人ITcode
									sFrom.setValue(BusinessScope, "GSBER");
									sFrom.setValue(EquityInvestmentProjectsText, "EAUFN");
									
									inputParameterList.setValue(sFrom, "ANLA");
									mConnection.execute(function);	
									System.out.print("sap返回信息==========="+function.getExportParameterList().toString());
									if(function.getExportParameterList().hasField("STATU")){
										String Statu = function.getExportParameterList().getField("STATU").getString();
										if("E".equals(Statu)){
											String Mess = function.getExportParameterList().getField("MESS").getString();
											System.out.print("sap返回失败= " + Statu + Mess);
											PrintMsg("error", Mess);
											return;
										}
									}
									JCO.Field mminfo = function.getExportParameterList().getField("ANLN1");
									System.out.print("固资编号= " + mminfo.getString().replaceAll("^(0+)", ""));
									mbdoc.replaceItemValue("MtappAssetNum", mminfo.getString().replaceAll("^(0+)", ""));
									mbdoc.save(true,true);
									//为合并后的子表赋值
									AssetDetailApprovalSubDc = AssetDetailApprovalSubvw.getAllDocumentsByKey(mbdoc.getItemValueString("UNID"), true);
									if (AssetDetailApprovalSubDc.getCount()>0){
										AssetDetailApprovalSubDoc = AssetDetailApprovalSubDc.getFirstDocument();
										while(AssetDetailApprovalSubDoc != null){
											AssetDetailApprovalSubDoc.replaceItemValue("MtappAssetNum", mminfo.getString().replaceAll("^(0+)", ""));
											AssetDetailApprovalSubDoc.save(true,true);
											AssetDetailApprovalSubDoc = AssetDetailApprovalSubDc.getNextDocument();
										}
									}
								}else{
									PrintMsg("error", "已存在固资编号，不允许重复创建!");
									return;
								}
								mbdoc = null;
							}
						}
						PrintMsg("ok", "生成固资编号成功！");
					}
				}
			}else{PrintMsg("error", "sap函数配置文档不存在，请联系系统管理员！"); }
			} catch(Exception e) {
				e.printStackTrace();
			}finally{
				try{
					mConnection.disconnect();
					if(mbdocnext!=null)mbdocnext.recycle();
					if (mbdoc!=null) mbdoc.recycle();
					if(doc!=null) doc.recycle();
					if (appdoc!=null) appdoc.recycle();
					if (mbdc!=null) mbdc.recycle();
					if (mbvw!=null) mbvw.recycle();	
					if (appvw!=null) appvw.recycle();
					if(v!=null) v.recycle();
					if (mbdb!=null) mbdb.recycle();
					if(db!=null) db.recycle();
					if(session!=null) session.recycle();
				}catch(Exception de){}
			}
	}
public JCO.Function createFunction(String name) throws Exception {
		try {
				IFunctionTemplate ft = mRepository.getFunctionTemplate(name.toUpperCase());
				if (ft == null) return null;
				return ft.getFunction();
			}catch (Exception e) {
				throw new Exception("创建RFC函数对象失败。");
			}
	}
	
	protected String getPrameter(String str, String src) {
		try {
			if (src.equals("")) return "";
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