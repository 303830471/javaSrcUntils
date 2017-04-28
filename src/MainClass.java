import lotus.domino.*;

import java.io.PrintWriter;
/*
 * 类说明： 1、获取数据库连接配置，根据配置连接关系数据库，并读取数据，记录数据到domino数据库，并对数据进行统计。
 * 		   2、定义人员组织库-Application/DawningPsnInfo.nsf，根据关系型数据库中的人员编号获取部门等信息。
 */
public class MainClass extends AgentBase{
	public void NotesMain() {
		try {
			Session session = getSession();
			AgentContext agentContext = session.getAgentContext();
			//统计数据库即当前数据库“AttendanceManage/BJAttendanceManage/AttendanceStatistics.nsf”
			Database dbCurrent = agentContext.getCurrentDatabase();
			
			Database dbareacof = null;
			Database dbrecord = null;
			
			if(dbCurrent.getFilePath().contains("BJAttendanceManage")){
				 dbrecord = session.getDatabase("", "hr/BJAttendanceManage/AttendanceRecord.nsf"); //刷卡记录数据库
				 dbareacof = session.getDatabase("", "hr/BJAttendanceManage/AttendanceConfig.nsf"); //管理配置库地区库（目前分北京和天津）[节假日、工作日调整、季节、数据库连接配置]
			}else if(dbCurrent.getFilePath().contains("TJAttendanceManage")){
				 dbrecord = session.getDatabase("", "hr/TJAttendanceManage/AttendanceRecord.nsf");
				 dbareacof = session.getDatabase("", "hr/TJAttendanceManage/AttendanceConfig.nsf");
			}else if(dbCurrent.getFilePath().contains("PTNEWAttendanceManage")){
				 dbrecord = session.getDatabase("", "hr/PTNEWAttendanceManage/AttendanceRecord.nsf");
				 dbareacof = session.getDatabase("", "hr/PTNEWAttendanceManage/AttendanceConfig.nsf");
			}else{
				 dbrecord = session.getDatabase("", "hr/TJAttendanceManage/AttendanceRecord.nsf");
				 dbareacof = session.getDatabase("", "hr/TJAttendanceManage/AttendanceConfig.nsf");
			}
			//人员组织库===根据人员编号获取部门信息
			Database dbwork = session.getDatabase("", "Application/sap_hr.nsf");
			//管理配置库公共库(异常状态、时间段配置、假期类别)
			Database dbcof = session.getDatabase("", "hr/AttendanceConfig.nsf");
			//数据库链接配置
			Configuration config = new Configuration(dbareacof);
			//当前文档（前台AJAX调用方式）
			Document docContext = agentContext.getDocumentContext();
			//获取开始时间和结束时间
			if(docContext != null){
				String[] params = docContext.getItemValueString("Request_Content").split(",");	
				if(params[0].equals("manual")){
					config.setDeptId(params[1]);
					config.setStartDate(params[2]);
					config.setEndDate(params[2]);
				}
			}
			PrintWriter pw = getAgentOutput();
			pw.println("content-type:text/plain;charset=UTF-8;");
			DataSource ds = new SQLDataSource(config);
			CardRecord card = new CardRecord(dbCurrent,dbwork,dbcof,dbareacof,dbrecord,config);	
			card.receiveData(ds);
			//导入数据条数
			//pw.println(ds.getUserName().toString());
			pw.println(ds.getCardID().size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}