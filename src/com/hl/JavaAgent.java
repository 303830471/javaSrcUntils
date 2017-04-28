package com.hl;
import java.io.File;
import java.io.PrintWriter;
import java.util.Vector;

import jxl.Workbook;
import lotus.domino.*;
//˵������ͳ�Ƶ������ݵ�����excel
public class JavaAgent extends AgentBase{

	public void NotesMain() {
		try {
			Session session = getSession();
			AgentContext agentContext = session.getAgentContext();
			//ͳ�����ݿ⼴��ǰ���ݿ�
			Database dbCurrent = agentContext.getCurrentDatabase();
			//��ǰ�ĵ���ǰ̨AJAX���÷�ʽ��
			Document docContext = agentContext.getDocumentContext();
			Database hrDb = session.getDatabase("", "Application/sap_hr.nsf");
			PrintWriter pw = getAgentOutput();
//			pw.println("content-type:text/plain;charset=UTF-8;");
			InitConfigInfo ConfigInfo = new InitConfigInfo(hrDb, session);
			//��ȡ��ʼʱ��ͽ���ʱ��
			if(docContext != null){
				System.out.println("Request_Content=" + docContext.getItemValueString("Request_Content"));
				String[] params = docContext.getItemValueString("Request_Content").split(",");	
				if(params[0].equals("manual")){
					ConfigInfo.setStStartDate(params[1]);
					ConfigInfo.setStEndDate(params[2]);
					ConfigInfo.setStWorkPlace(params[3]);
					ConfigInfo.setStWorkPlaceNum(params[4]);
					ConfigInfo.setStDeptName(params[5]);
					ConfigInfo.setStDeptID(params[6]);
				}
			}
			View curView = dbCurrent.getView("AttendanceListView");
			View hrView = hrDb.getView("psnInfoByNewWorkerNumberAtJob");
			DateTime StStartDate = session.createDateTime(ConfigInfo.getStStartDate());
			DateTime StEndDate = session.createDateTime(ConfigInfo.getStEndDate());
			String formula = "";
			String hrformula = "";
			DocumentCollection hrDocCol = null;
			DocumentCollection statDocCol = null;
			hrformula = "Form = 'hr_person' & hrEmploeeType != '��ְ'";
			formula = "Form = 'AttendanceListForm' & startDate >= [" + StStartDate + "] & startDate<= ["+ StEndDate + "]";
			String DeptName = ConfigInfo.getStDeptName().replace("%20", "");
			String WorkPlace = ConfigInfo.getStWorkPlace().replace("%20", "");
			if(!DeptName.trim().equals("")){
				hrformula = hrformula + "&psnDeptItcode = '" + ConfigInfo.getStDeptID() + "' ";
				formula = formula + "&StDeptName = '" + DeptName + "' ";
			}
			if(!WorkPlace.trim().equals("")){
				hrformula = hrformula + "& hrWork='"+ WorkPlace + "' ";
				formula = formula + "& StWorkerPlace='"+ WorkPlace + "' ";
			}
			System.out.println("hrformula= " + hrformula ); 
			System.out.println("formula= " + formula ); 
			hrDocCol = hrDb.search(hrformula, null);
			int hrCount = 0;
			if (hrDocCol.getCount()>0){
				hrCount = hrCount + 1;
				Document hrDoc  = hrDocCol.getFirstDocument();
				
				ExcelHandle excelHandle = new ExcelHandle();
				jxl.write.WritableWorkbook wwb = null;
				jxl.write.WritableSheet ws = null;
				File file = new File("c:\\reportExcelData.xls");
				file.createNewFile();
				wwb = Workbook.createWorkbook(file);
				ws = wwb.createSheet("���ڱ���", 0);
				jxl.write.Label labelC ;
				//д��ͷ
				excelHandle.writeExcelHeader(ws);
				while(hrDoc != null){	
					String formulas = formula + "& StNewWorkerNumber='"+ hrDoc.getItemValueString("personNum_HR") + "' ";
					statDocCol = dbCurrent.search(formulas, StStartDate);
					if(statDocCol.getCount()>0){
						CreateStaticDatas csd = new CreateStaticDatas();
						Vector staticDataVec = csd.InitStaticData(statDocCol, ConfigInfo);
						excelHandle.writeDataToExcel(ws,staticDataVec,hrCount);
					}else{
						System.out.println("δ�ҵ�����������ͳ���ĵ���formulas= "+formulas); 
						pw.println("δ�ҵ�����������ͳ���ĵ���");
					}
					hrDoc = hrDocCol.getNextDocument();
				}
				excelHandle.closeExcel(wwb);
			}else{
				System.out.println("δ�ҵ�������������Ա��Ϣ�ĵ���"); 
				pw.println("δ�ҵ�������������Ա��Ϣ�ĵ���");
			}
			pw.println("ִ����ϣ�");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
