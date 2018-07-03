package com.sap.jco.demo;

/*
���ܣ����ɹ��ʱ��
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
			doc=v.getDocumentByKey("CreateItem",true); //����Ӧ�����޸ı�����룬getDocumentByKey��һ�������޸�Ϊ���������ݵ������ñ����ĵ�����Ӧ����������
			if(doc!=null){
				String inputFieldXml=doc.getItemValueString("RTFInputXML_1");
				String outputTableXml=doc.getItemValueString("RTFOutputTableXML_1");
				//2009-1-15-----------------bug�޸�-----------
				String s=outputTableXml;
				String reg="[\n-\r]";
				Pattern p2=Pattern.compile(reg);
				Matcher m=p2.matcher(s);
				outputTableXml=m.replaceAll("");
				Matcher m2=p2.matcher(inputFieldXml);
				inputFieldXml=m2.replaceAll("");
				//2009-1-15-----------------bug�޸�-----------
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
					PrintMsg("error", "����Sapʧ�ܣ�");
					return;
				}else{
					mRepository = new JCO.Repository("ARAsoft", mConnection);
					JCO.Function function = null;
					JCO.ParameterList inputParameterList;
					function = this.createFunction(functionName);
					if (function == null){
						ErrorMsg=functionName+"������SAP�в����ڣ�";
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
							 StUserCompanyCode = appdoc.getItemValueString("StCompany");  //ʹ�ù�˾����
							 StInternalOrderHRID = appdoc.getItemValueString("StInternalOrderHRID");  //�ڲ�������
							 if (!appdoc.getItemValueString("StPayTypeHRID").equals("")){   //�ʲ������ɱ����ĺ�
								 StPayTypeHRID = appdoc.getItemValueString("StPayTypeHRID"); 
							 }else{
								 StPayTypeHRID = appdoc.getItemValueString("StPayTypeHRID");  //�ɱ����ĺ�
							 }
							 StUserDeptName = appdoc.getItemValueString("yewu_zichansuoshubumen_mingcheng");  //�ʲ���������
							 StUserCn = appdoc.getItemValueString("StUsePeopleCN");   //ʹ����StUserCn
							 StPsnCN = appdoc.getItemValueString("PersonCN");   //������
							 StPsnEN = appdoc.getItemValueString("PersonEN");   //������ITCODE
							 WorkPlace = appdoc.getItemValueString("WorkPlace");   //���������ڹ�����
							 BusinessScope = appdoc.getItemValueString("ScopeBusiness");   //ҵ��ΧBusinessScope
							 EquityInvestmentProjectsText = appdoc.getItemValueString("EquityInvestmentProjectsText");   //��Ŀ��EquityInvestmentProjectsText
							 if(EquityInvestmentProjectsText.equals(""))EquityInvestmentProjectsText = appdoc.getItemValueString("StProjectNum"); 
						}else{  PrintMsg("error", "�����ĵ�δ�ҵ��� "); return; }
						
						System.out.print( "ʹ����= " +StUserCn+" ҵ��Χ= "+BusinessScope+" ʹ���˲���= "+StUserDeptName+" ��Ŀ��= "+EquityInvestmentProjectsText+" ʹ���˹�˾= "+StUserCompanyCode+" �ɱ����ĺ�= "+StPayTypeHRID+" �ڲ�������= "+StInternalOrderHRID );
						if(UNIDS==null){PrintMsg("error", "unid��������ʧ�ܣ�����ϵϵͳ����Ա��"); return;}
						for(int i=0;i<UNIDS.length;i++){
							mbdoc = mbdb.getDocumentByUNID(UNIDS[i]);
							if (mbdoc !=null){
								if (mbdoc.getItemValueString("MtMaterialDeion").equals("") || mbdoc.getItemValueString("MtMaterialNummer").equals("") ){
									PrintMsg("error", "�������������ϺŲ���Ϊ�գ�");
									return;
								}
								if (mbdoc.getItemValueString("MtappAssetNum").equals("")){
									System.out.print("�ʲ�����= " + mbdoc.getItemValueString("StITEMClass"));
									System.out.print("��Ʒ����/����= " + mbdoc.getItemValueString("MtMaterialDeion").replace(" ", "")+"/"+mbdoc.getItemValueString("ManufacturerBuy")+" ����= "+mbdoc.getItemValueString("MtMaterialDeion")+" ����ͺ�= "+mbdoc.getItemValueString("MtMaterialNummer")+"00"+mbdoc.getItemValueString("MtOrderNummer"));
									
									JCO.Structure sFrom = inputParameterList.getStructure("ANLA");
								
									sFrom.setValue(StUserCompanyCode, "BUKRS");
									sFrom.setValue(StPayTypeHRID, "KOSTL");
									sFrom.setValue(StInternalOrderHRID, "CAUFN");
									sFrom.setValue(mbdoc.getItemValueString("StITEMClass"), "ANLKL");  //�ʲ�����StITEMClassCode
									sFrom.setValue(mbdoc.getItemValueString("MtMaterialDeion"), "TXT50");  //�ʲ�����
									sFrom.setValue(mbdoc.getItemValueString("MtMaterialDeion"), "TXA50");  //�����ʲ�����
									sFrom.setValue(mbdoc.getItemValueString("MtMaterialNummer")+"00"+mbdoc.getItemValueString("MtOrderNummer"), "ANLHTXT");  //����ͺ�SizeModelBuy���ʲ�����˵����
									sFrom.setValue(StUserDeptName, "INVNR");
									if(StUserCn.equals(StPsnCN))sFrom.setValue(StUserCn, "INVZU");
									else sFrom.setValue(StPsnCN+"/"+StUserCn, "INVZU");  //������/ʹ����
									sFrom.setValue(WorkPlace, "RAUMN");  //������
									sFrom.setValue(StPsnEN, "TYPBZ");  //������ITcode
									sFrom.setValue(BusinessScope, "GSBER");
									sFrom.setValue(EquityInvestmentProjectsText, "EAUFN");
									
									inputParameterList.setValue(sFrom, "ANLA");
									mConnection.execute(function);	
									System.out.print("sap������Ϣ==========="+function.getExportParameterList().toString());
									if(function.getExportParameterList().hasField("STATU")){
										String Statu = function.getExportParameterList().getField("STATU").getString();
										if("E".equals(Statu)){
											String Mess = function.getExportParameterList().getField("MESS").getString();
											System.out.print("sap����ʧ��= " + Statu + Mess);
											PrintMsg("error", Mess);
											return;
										}
									}
									JCO.Field mminfo = function.getExportParameterList().getField("ANLN1");
									System.out.print("���ʱ��= " + mminfo.getString().replaceAll("^(0+)", ""));
									mbdoc.replaceItemValue("MtappAssetNum", mminfo.getString().replaceAll("^(0+)", ""));
									mbdoc.save(true,true);
									//Ϊ�ϲ�����ӱ�ֵ
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
									PrintMsg("error", "�Ѵ��ڹ��ʱ�ţ��������ظ�����!");
									return;
								}
								mbdoc = null;
							}
						}
						PrintMsg("ok", "���ɹ��ʱ�ųɹ���");
					}
				}
			}else{PrintMsg("error", "sap���������ĵ������ڣ�����ϵϵͳ����Ա��"); }
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
				throw new Exception("����RFC��������ʧ�ܡ�");
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
	�������ܣ���ǰ̨���������Ϣ
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