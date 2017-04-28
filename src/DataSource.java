import java.util.ArrayList;
/*
 * 刷卡记录数据源
 * 接口说明：该接口可以提供OA考勤系统所需要的数据
 */
public interface DataSource {
	//获取员工考勤卡号
	public ArrayList getCardID();
	//获取员工姓名
	public ArrayList getUserName();
	//获取打卡日期
	public ArrayList getRecordDate();
	//获取最早打卡时间
	public ArrayList getEarliestTime();
	//获取最晚打卡时间
	public ArrayList getLatestTime();
	//获取员工编号
	public ArrayList getWorkerNumber();
	public void setCardID(ArrayList cardID);
	public void setUserName(ArrayList userName);
	public void setRecordDate(ArrayList recordDate);
	public void setEarliestTime(ArrayList earliestTime);
	public void setLatestTime(ArrayList latestTime);
	public void setWorkerNumber(ArrayList workerNumber);
	//数组长度一致性检查,如果一致返回true，否则返回false
	public boolean isConsistency();
}
