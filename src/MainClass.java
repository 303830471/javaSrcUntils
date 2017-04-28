import lotus.domino.*;

import java.io.PrintWriter;
/*
 * ��˵���� 1����ȡ���ݿ��������ã������������ӹ�ϵ���ݿ⣬����ȡ���ݣ���¼���ݵ�domino���ݿ⣬�������ݽ���ͳ�ơ�
 * 		   2��������Ա��֯��-Application/DawningPsnInfo.nsf�����ݹ�ϵ�����ݿ��е���Ա��Ż�ȡ���ŵ���Ϣ��
 */
public class MainClass extends AgentBase{
	public void NotesMain() {
		try {
			Session session = getSession();
			AgentContext agentContext = session.getAgentContext();
			//ͳ�����ݿ⼴��ǰ���ݿ⡰AttendanceManage/BJAttendanceManage/AttendanceStatistics.nsf��
			Database dbCurrent = agentContext.getCurrentDatabase();
			
			Database dbareacof = null;
			Database dbrecord = null;
			
			if(dbCurrent.getFilePath().contains("BJAttendanceManage")){
				 dbrecord = session.getDatabase("", "hr/BJAttendanceManage/AttendanceRecord.nsf"); //ˢ����¼���ݿ�
				 dbareacof = session.getDatabase("", "hr/BJAttendanceManage/AttendanceConfig.nsf"); //�������ÿ�����⣨Ŀǰ�ֱ��������[�ڼ��ա������յ��������ڡ����ݿ���������]
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
			//��Ա��֯��===������Ա��Ż�ȡ������Ϣ
			Database dbwork = session.getDatabase("", "Application/sap_hr.nsf");
			//�������ÿ⹫����(�쳣״̬��ʱ������á��������)
			Database dbcof = session.getDatabase("", "hr/AttendanceConfig.nsf");
			//���ݿ���������
			Configuration config = new Configuration(dbareacof);
			//��ǰ�ĵ���ǰ̨AJAX���÷�ʽ��
			Document docContext = agentContext.getDocumentContext();
			//��ȡ��ʼʱ��ͽ���ʱ��
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
			//������������
			//pw.println(ds.getUserName().toString());
			pw.println(ds.getCardID().size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}