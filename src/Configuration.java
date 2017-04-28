import lotus.domino.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;
/**
 * 类说明：该类用于从domino数据库中读取连接和查询关系数据库的配置信息。
 */
public class Configuration {
	private Database db = null;
	private String driver = null;
	private String url = null;
	private String userName = null;
	private String password = null;
	private String startDate = null;		//开始日期（设定查询范围）
	private String endDate = null;		    //结束日期（设定查询范围）
	private String deptId = null;			//部门（公司）编号（设定查询范围）
	private String procName = null;		    //存储过程名
	private String procText = null;		    //存储过程内容
	private String[] columnName = null;		//要查询的列名	
	public String getStartDate;
	private String StWorkPlaceCode = null;			//工作地编码
	private String StWorkPlaceName = null;			//工作地名称
	//初始化配置库
	public Configuration(Database db){
		if(db != null){
			this.db = db;
			initConfig();
		}
	}
	//初始化配置信息
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
