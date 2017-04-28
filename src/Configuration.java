import lotus.domino.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;
/**
 * ��˵�����������ڴ�domino���ݿ��ж�ȡ���ӺͲ�ѯ��ϵ���ݿ��������Ϣ��
 */
public class Configuration {
	private Database db = null;
	private String driver = null;
	private String url = null;
	private String userName = null;
	private String password = null;
	private String startDate = null;		//��ʼ���ڣ��趨��ѯ��Χ��
	private String endDate = null;		    //�������ڣ��趨��ѯ��Χ��
	private String deptId = null;			//���ţ���˾����ţ��趨��ѯ��Χ��
	private String procName = null;		    //�洢������
	private String procText = null;		    //�洢��������
	private String[] columnName = null;		//Ҫ��ѯ������	
	public String getStartDate;
	private String StWorkPlaceCode = null;			//�����ر���
	private String StWorkPlaceName = null;			//����������
	//��ʼ�����ÿ�
	public Configuration(Database db){
		if(db != null){
			this.db = db;
			initConfig();
		}
	}
	//��ʼ��������Ϣ
	private void initConfig(){
		try{			
			if(db != null){
				View vwConfig = db.getView("DbConfigView");
				Document docConfig  = vwConfig.getFirstDocument();
				if(docConfig != null){
					driver = docConfig.getItemValueString("StDriverType");
					url = docConfig.getItemValueString("StURL");
					userName = docConfig.getItemValueString("StUserName");
					password = docConfig.getItemValueString("StPassword");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  		
					Calendar cal = Calendar.getInstance(); 
					cal.add(Calendar.DATE,-1);    
					startDate = sdf.format(cal.getTime());
					endDate = startDate;
					columnName = docConfig.getItemValueString("StColumnName").split(";");
					deptId = docConfig.getItemValueString("StDeptId");
					procName = docConfig.getItemValueString("StProcedure");
					procText = docConfig.getItemValueString("RtfProcText");
					StWorkPlaceCode = docConfig.getItemValueString("StWorkPlaceCode");
					StWorkPlaceName = docConfig.getItemValueString("StWorkPlaceName");
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getStWorkPlaceCode() {
		return StWorkPlaceCode;
	}

	public void setStWorkPlaceCode(String StWorkPlaceCode) {
		this.StWorkPlaceCode = StWorkPlaceCode;
	}
	public String getStWorkPlaceName() {
		return StWorkPlaceName;
	}

	public void setStWorkPlaceName(String StWorkPlaceName) {
		this.StWorkPlaceName = StWorkPlaceName;
	}
	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public String getProcName() {
		return procName;
	}

	public void setProcName(String procName) {
		this.procName = procName;
	}

	public String getProcText() {
		return procText;
	}

	public void setProcText(String procText) {
		this.procText = procText;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	public String[] getColumnName() {
		return columnName;
	}

	public void setColumnName(String[] columnName) {
		this.columnName = columnName;
	}
}
