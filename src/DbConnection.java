import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.CallableStatement;
/*
 * 数据库连接
 * 类说明：该类用于连接关系数据库
 */
public class DbConnection {
	
	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	private CallableStatement cstmt = null;
	
	//获得连接。参数：classNameStr连接类型，urlStr连接地址，userNameStr用户名，passWordStr密码
	public void getConn(String classNameStr, String urlStr, String userNameStr, String passWordStr) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		Class.forName(classNameStr).newInstance();
		//System.out.println("数据库驱动程序注册成功！"); 
		conn = DriverManager.getConnection(urlStr, userNameStr, passWordStr);
		stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
	}
	
	//执行查询语句。
	public ResultSet exeQuery(String sqlStr) throws SQLException {
		rs = stmt.executeQuery(sqlStr);
		return rs;
	}
	//执行更新语句
	public void exeUpdate(String sqlStr) throws SQLException {
		stmt.executeUpdate(sqlStr);
	}
	
	//关闭所有连接。
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
	 * 执行指定的存储过程
	 * 参数：procName存储过程名，ins[]存储过程的参数
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
	//获得结果集
	public ResultSet getResultSet(){
		return rs;
	}
}


