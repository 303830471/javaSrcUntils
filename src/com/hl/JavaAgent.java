package com.hl;
import java.io.File;
import java.io.PrintWriter;
import java.util.Vector;

import jxl.Workbook;
import lotus.domino.*;
//说明：将统计到的数据导出到excel
public class JavaAgent extends AgentBase{

	public void NotesMain() {
		try {
			Session session = getSession();
			AgentContext agentContext = session.getAgentContext();
			//统计数据库即当前数据库
			Database dbCurrent = agentContext.getCurrentDatabase();
			//当前文档（前台AJAX调用方式）
			Document docContext = agentContext.getDocumentContext();
			Database hrDb = session.getDatabase("", "Application/sap_hr.nsf");
			PrintWriter pw = getAgentOutput();
//			pw.println("content-type:text/plain;charset=UTF-8;");
			InitConfigInfo ConfigInfo = new InitConfigInfo(hrDb, session);
			//获取开始时间和结束时间
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
			hrformula = "Form = 'hr_person' & hrEmploeeType != '离职'";
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
				ws = wwb.createSheet("考勤报表", 0);
				jxl.write.Label labelC ;
				//写表头
				excelHandle.writeExcelHeader(ws);
				while(hrDoc != null){	
					String formulas = formula + "& StNewWorkerNumber='"+ hrDoc.getItemValueString("personNum_HR") + "' ";
					statDocCol = dbCurrent.search(formulas, StStartDate);
					if(statDocCol.getCount()>0){
						CreateStaticDatas csd = new CreateStaticDatas();
						Vector staticDataVec = csd.InitStaticData(statDocCol, ConfigInfo);
						excelHandle.writeDataToExcel(ws,staticDataVec,hrCount);
					}else{
						System.out.println("未找到符合条件的统计文档！formulas= "+formulas); 
						pw.println("未找到符合条件的统计文档！");
					}
					hrDoc = hrDocCol.getNextDocument();
				}
				excelHandle.closeExcel(wwb);
			}else{
				System.out.println("未找到符合条件的人员信息文档！"); 
				pw.println("未找到符合条件的人员信息文档！");
			}
			pw.println("执行完毕！");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
