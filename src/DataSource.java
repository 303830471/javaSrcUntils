import java.util.ArrayList;
/*
 * ˢ����¼����Դ
 * �ӿ�˵�����ýӿڿ����ṩOA����ϵͳ����Ҫ������
 */
public interface DataSource {
	//��ȡԱ�����ڿ���
	public ArrayList getCardID();
	//��ȡԱ������
	public ArrayList getUserName();
	//��ȡ������
	public ArrayList getRecordDate();
	//��ȡ�����ʱ��
	public ArrayList getEarliestTime();
	//��ȡ�����ʱ��
	public ArrayList getLatestTime();
	//��ȡԱ�����
	public ArrayList getWorkerNumber();
	public void setCardID(ArrayList cardID);
	public void setUserName(ArrayList userName);
	public void setRecordDate(ArrayList recordDate);
	public void setEarliestTime(ArrayList earliestTime);
	public void setLatestTime(ArrayList latestTime);
	public void setWorkerNumber(ArrayList workerNumber);
	//���鳤��һ���Լ��,���һ�·���true�����򷵻�false
	public boolean isConsistency();
}
