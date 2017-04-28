import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
/*
 * SQL Server ˢ����¼����Դ
 * ��˵���������SQL Server���ݿ���ȡ����
 */
public class SQLDataSource implements DataSource {
	
	private DbConnection dbcon = null;     // ���ݿ�����
	private Configuration config = null;   // ���ݿ����Ӳ�ѯ������Ϣ
	private ArrayList cardID = null;
	private ArrayList userName = null;
	private ArrayList recordDate = null;
	private ArrayList earliestTime = null;
	private ArrayList latestTime = null;
	private ArrayList workerNumber = null;
	
	public SQLDataSource() {
	}

	// ��������Ϣ�Ĺ��캯�������ڴӹ�ϵ���ݿ��в�ѯ����
	public SQLDataSource(Configuration config) {
		this.config = config;
		this.dbcon = new DbConnection();
		initData();
	}

	// �������ݿ�
	private void connect() {
		try {
			dbcon.getConn(config.getDriver(), config.getUrl(), config
					.getUserName(), config.getPassword());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ��ѯ����
	private void query() {
		try {
			// ����洢���̲��洢�ڣ��򴴽��洢����
			if (!isHaveProcedure(config.getProcName())) {
				dbcon.exeUpdate(config.getProcText());
			}
			// SQLִ�д洢����
			dbcon.exeQuery("EXECUTE " + config.getProcName()
					+ " '"+ config.getStartDate() + "', '"+ config.getEndDate() + "', '"+ config.getDeptId() + "';");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// ������������
	public void setConfig(Configuration config) {
		this.config = config;
	}

	// ���鳤��һ���Լ��,���һ�·���true�����򷵻�false
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
	 * �жϴ洢�����Ƿ��Ѿ�����������ڷ���true�����򷵻�false ������procName�洢������
	 */
	public boolean isHaveProcedure(String procName) {
		try {
			// ��sysobjects�в�ѯָ���Ĵ洢���̣������ҵ��洢���̵�����
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
		// ��ѯ�ֶα���Ϊ6����ڿ���,��˳��Ϊ��Ա�������������ڣ������ʱ�䣬�����ʱ��,Ա�����
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

	// ��ȡ���ڿ���
	public ArrayList getCardID() {
		return this.cardID;
	}

	// ��ȡԱ������
	public ArrayList getUserName() {
		return this.userName;
	}

	// ��ȡ������
	public ArrayList getRecordDate() {
		return this.recordDate;
	}

	// ��ȡ�����ʱ��
	public ArrayList getEarliestTime() {
		return this.earliestTime;
	}

	// ��ȡ�����ʱ��
	public ArrayList getLatestTime() {
		return this.latestTime;
	}
	
	// ��ȡԱ�����
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