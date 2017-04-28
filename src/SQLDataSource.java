import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
/*
 * SQL Server 刷卡记录数据源
 * 类说明：该类从SQL Server数据库提取数据
 */
public class SQLDataSource implements DataSource {
	
	private DbConnection dbcon = null;     // 数据库连接
	private Configuration config = null;   // 数据库连接查询配置信息
	private ArrayList cardID = null;
	private ArrayList userName = null;
	private ArrayList recordDate = null;
	private ArrayList earliestTime = null;
	private ArrayList latestTime = null;
	private ArrayList workerNumber = null;
	
	public SQLDataSource() {
	}

	// 带配置信息的构造函数，用于从关系数据库中查询数据
	public SQLDataSource(Configuration config) {
		this.config = config;
		this.dbcon = new DbConnection();
		initData();
	}

	// 连接数据库
	private void connect() {
		try {
			dbcon.getConn(config.getDriver(), config.getUrl(), config
					.getUserName(), config.getPassword());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 查询数据
	private void query() {
		try {
			// 如果存储过程不存储在，则创建存储过程
			if (!isHaveProcedure(config.getProcName())) {
				dbcon.exeUpdate(config.getProcText());
			}
			// SQL执行存储过程
			dbcon.exeQuery("EXECUTE " + config.getProcName()
					+ " '"+ config.getStartDate() + "', '"+ config.getEndDate() + "', '"+ config.getDeptId() + "';");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 设置连接配置
	public void setConfig(Configuration config) {
		this.config = config;
	}

	// 数组长度一致性检查,如果一致返回true，否则返回false
	public boolean isConsistency() {
		if (cardID.size() == userName.size()
				&& cardID.size() == recordDate.size()
				&& cardID.size() == earliestTime.size()
				&& cardID.size() == latestTime.size()
				&& cardID.size() == workerNumber.size()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断存储过程是否已经存在如果存在返回true，否则返回false 参数：procName存储过程名
	 */
	public boolean isHaveProcedure(String procName) {
		try {
			// 在sysobjects中查询指定的存储过程，返回找到存储过程的数量
			if (dbcon != null) {
				dbcon.exeQuery("SELECT Count(name) AS Expr1 FROM sysobjects WHERE (name = '"
								+ procName + "') AND (type = 'P')");
				ResultSet rs = dbcon.getResultSet();
				if (rs.next() && rs.getInt("Expr1") > 0) {
					return true;
				} else {
					return false;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void initData() {
		connect();
		query();
		ResultSet rs = dbcon.getResultSet();
		// 查询字段必须为6项，考勤卡号,按顺序为，员工姓名，打卡日期，最早打卡时间，最晚打卡时间,员工编号
		if (config.getColumnName().length == 6) {
			cardID = new ArrayList();
			userName = new ArrayList();
			recordDate = new ArrayList();
			earliestTime = new ArrayList();
			latestTime = new ArrayList();
			workerNumber = new ArrayList();
			try {
				while (rs.next()) {
					cardID.add(rs.getString(config.getColumnName()[0]));
					userName.add(rs.getString(config.getColumnName()[1]));
					recordDate.add(rs.getString(config.getColumnName()[2]));
					earliestTime.add(rs.getString(config.getColumnName()[3]));
					latestTime.add(rs.getString(config.getColumnName()[4]));	
				    workerNumber.add(rs.getString(config.getColumnName()[5]));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{				
				dbcon.closeAll();
			}
		}

	}

	// 获取考勤卡号
	public ArrayList getCardID() {
		return this.cardID;
	}

	// 获取员工姓名
	public ArrayList getUserName() {
		return this.userName;
	}

	// 获取打卡日期
	public ArrayList getRecordDate() {
		return this.recordDate;
	}

	// 获取最早打卡时间
	public ArrayList getEarliestTime() {
		return this.earliestTime;
	}

	// 获取最晚打卡时间
	public ArrayList getLatestTime() {
		return this.latestTime;
	}
	
	// 获取员工编号
	public ArrayList getWorkerNumber() {
		return this.workerNumber;
	}

	public void setCardID(ArrayList cardID) {
		this.cardID = cardID;
	}

	public void setUserName(ArrayList userName) {
		this.userName = userName;
	}

	public void setRecordDate(ArrayList recordDate) {
		this.recordDate = recordDate;
	}

	public void setEarliestTime(ArrayList earliestTime) {
		this.earliestTime = earliestTime;
	}

	public void setLatestTime(ArrayList latestTime) {
		this.latestTime = latestTime;
	}
	public void setWorkerNumber(ArrayList workerNumber) {
		this.workerNumber = workerNumber;
	}
}