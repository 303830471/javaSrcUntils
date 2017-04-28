import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.CallableStatement;
/*
 * ���ݿ�����
 * ��˵���������������ӹ�ϵ���ݿ�
 */
public class DbConnection {
	
	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	private CallableStatement cstmt = null;
	
	//������ӡ�������classNameStr�������ͣ�urlStr���ӵ�ַ��userNameStr�û�����passWordStr����
	public void getConn(String classNameStr, String urlStr, String userNameStr, String passWordStr) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		Class.forName(classNameStr).newInstance();
		//System.out.println("���ݿ���������ע��ɹ���"); 
		conn = DriverManager.getConnection(urlStr, userNameStr, passWordStr);
		stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
	}
	
	//ִ�в�ѯ��䡣
	public ResultSet exeQuery(String sqlStr) throws SQLException {
		rs = stmt.executeQuery(sqlStr);
		return rs;
	}
	//ִ�и������
	public void exeUpdate(String sqlStr) throws SQLException {
		stmt.executeUpdate(sqlStr);
	}
	
	//�ر��������ӡ�
	public void closeAll(){
		try {
			if( rs != null){
				rs.close();
			}
			if( stmt!= null){
				stmt.close();
			}
			if( conn!= null){
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ִ��ָ���Ĵ洢����
	 * ������procName�洢��������ins[]�洢���̵Ĳ���
	 */
	public void execProcedure(String procName,String ins[]){
		try {
			cstmt = conn.prepareCall(procName,ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			for(int i = 0;i < ins.length; i++){
				cstmt.setString(i + 1, ins[i]);
			}
			rs = cstmt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	//��ý����
	public ResultSet getResultSet(){
		return rs;
	}
}


