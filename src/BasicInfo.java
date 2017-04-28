import lotus.domino.*;
import lotus.notes.Session;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
/*
 * ������Ϣ��
 * ��˵�����ڼ��գ����������գ���Ϣʱ�����Ϣ
 */
public class BasicInfo{
	//���嵱ǰ��Ϣʱ��,����ĵ�0��Ԫ���ǵ�ǰ�������ƣ���1��Ԫ�������翪ʼʱ�䣬��2��Ԫ�����������ʱ�䣬������Ԫ�����������ʱ�䣬���ĸ�Ԫ�������翪ʼʱ�䣬�����Ԫ���ǳٵ�ʱ�䣬������Ԫ��������ʱ�䡣
	private String[] workTime =  new String[8];
	//�ڼ�����Ϣ,����Ϊÿ���ڼ�������
	private ArrayList holidays =  new ArrayList();
	//����������,����Ϊÿ����������������
	private ArrayList specWorkDay =  new ArrayList();
	//���������°�ʱ�����������eg�����
	private String changeDay = null;
	//�Ƿ�ȫ����Ч
	private String IsEffectAll = null;
	
	private Database dbcof = null;   //��ʱû��
	private Database dbareacof = null;
	private Configuration config = null;
	private Database dbwork =null;
	public BasicInfo(){
		
	}
	public BasicInfo(Database db ,Database dbareacof,Configuration config){	
		try{
			this.dbcof = db;
			this.dbareacof = dbareacof;
			this.config = config;
			initWorkTime();
			initHolidays();
			initSpecWorkDay();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	//��ʼ����Ϣʱ��
	private void initWorkTime()throws NotesException{
		if(dbareacof != null){
			//������ͼ                SeasonalTLBySRQYView                                                  
			//View vwSeason = dbareacof.getView("SeasonalBySRQYView");
			View vwSeason = dbareacof.getView("SeasonalByWorkPlace");
			//��ȡ�Ѽ���ļ����ĵ�
			//Document docSeason  = vwSeason.getDocumentByKey("yes");
			Document docSeason  = vwSeason.getDocumentByKey(config.getStWorkPlaceCode()+config.getStartDate(),true);
			if(docSeason == null){
				docSeason  = vwSeason.getDocumentByKey(config.getStWorkPlaceCode(),true);
			}
			if(docSeason != null){
				workTime[0] = docSeason.getItemValueString("StJJ");
				workTime[1] = docSeason.getItemValueString("StStartTime");
				workTime[2] = docSeason.getItemValueString("StEndTime");
				//workTime[2] = "16:00";
				workTime[3] = docSeason.getItemValueString("StEndTimeAM");
				workTime[4] = docSeason.getItemValueString("StStartTimePM");
				workTime[5] = docSeason.getItemValueString("StLateTime");
				//workTime[5] = "10:10";
				workTime[6] = docSeason.getItemValueString("StLevEarlyTime");
				//workTime[6] = "15:00";
				workTime[7] = docSeason.getItemValueString("StLateStartTime");
				//workTime[7] = "10:05";
//				System.out.println("��������===  " + config.getStartDate());
//				System.out.println(config.getStartDate().equalsIgnoreCase("2016-07-21"));
//				if(config.getStartDate().equalsIgnoreCase("2016-07-21")){
//					//�����°�ʱ��
//					//workTime[2] = "16:00";
//					//�ٵ�ʱ��
//					workTime[5] = "10:10";
//					//��������ʱ��
//					//workTime[6] = "15:00";
//					//�����ϰ�ʱ��
//					workTime[7] = "10:05";
//				}
//				if(config.getStartDate().equalsIgnoreCase("2016-07-20")){
//					//�����°�ʱ��
//					workTime[2] = "16:00";
//					//�ٵ�ʱ��
//					workTime[5] = "10:10";
//					//��������ʱ��
//					workTime[6] = "15:00";
//					//�����ϰ�ʱ��
//					workTime[7] = "10:05";
//				}
			}
		}
	}
	//��ʼ���ڼ�����Ϣ
	private void initHolidays() throws NotesException{
		if(dbareacof != null){
			//�ڼ�����ͼ
			View vwHolidays = dbareacof.getView("HolidaysByEndDateYearView");
			//��ȡ�ڼ���
			Document docHolidays  = vwHolidays.getFirstDocument();
			while(docHolidays != null){					
				String[] aAllDate = docHolidays.getItemValueString("StAllDate").split("\\^");
				for(int i = 0;i<aAllDate.length;i++)
				{
					holidays.add(aAllDate[i]+docHolidays.getItemValueString("StWorkPlace"));
				}
				docHolidays = vwHolidays.getNextDocument(docHolidays);
			}
		}		
	}
	//��ʼ������������
	private void initSpecWorkDay() throws NotesException {
		if(dbareacof != null){
			//����������ͼ
			View vwSpec = dbareacof.getView("SpecWorkDayByEndDataYearView");
			//��ȡ�����������ĵ�				
			Document docSpec  = vwSpec.getFirstDocument();
			while(docSpec != null){					
				String[] aAllDate = docSpec.getItemValueString("StAllDate").split("\\^");
				for(int i = 0;i<aAllDate.length;i++)
				{
					specWorkDay.add(aAllDate[i]+docSpec.getItemValueString("StWorkPlace"));
				}
				docSpec = vwSpec.getNextDocument(docSpec);
			}
		}
	}
	//�ж�һ�������Ƿ��ǹ�����
	public boolean isWorkDay(String date, String workPlace){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
			Date dateInfo = sdf.parse(date);   
			Calendar cal = Calendar.getInstance();   
			cal.setTime(dateInfo);
			int day = cal.get(Calendar.DAY_OF_WEEK);
			date = sdf.format(cal.getTime());
			//����ǽڼ�����˵�����ǹ�����
			if(holidays.contains(date+workPlace)){
				return false;
			}
			if(holidays.contains(date)){
				return false;
			}
			else if(day == 1 || day == 7){	
				//�������ĩ�����ж������ĩ�Ƿ��Ѿ�����Ϊ�����ա�
				if( !specWorkDay.contains(date) ){
					//���ǵ����Ĺ�����
					if( !specWorkDay.contains(date+workPlace) ){
						//���ǵ����Ĺ�����
						return false;
					}else{
						return true;
					}
				}
			
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public String[] getWorkTime(){
		return workTime;
	}
	
	//������Ա��Ż�ȡ������Ϣ����˳��Ϊ��ITCode���������ƣ����ű�ţ�Ա�����ͣ������أ���ְʱ��1�7���ְʱ��1�7
	public String[] getMappingInfo(String workerNum,Database dbwork) {
		String[] Info = null;
		try{			
			//�����ڿ�������Ĺ�ϵӳ����ͼ
			View vwMapping = dbwork.getView("psnInfoByWorkerNumber");
			View tspzvw = dbareacof.getView("SpecialWorkerPlaceView");
			
			//��ȡӳ���ĵ�
			Document docMapping  = vwMapping.getDocumentByKey(workerNum,true);
			if(docMapping != null){
				Info = new String[10];
				Info[0] = docMapping.getItemValueString("itcode"); //ITCODE
				Info[1] = docMapping.getItemValueString("psnDeptShortText"); //��������
				Info[2] = docMapping.getItemValueString("psnDeptItcode");  //���ű��
				Info[3] = docMapping.getItemValueString("hrSubEmploeeType"); //Ա������
				Document tspzdoc = tspzvw.getDocumentByKey(docMapping.getItemValueString("itcode"),true);
				if (tspzdoc != null){
					Info[4] = tspzdoc.getItemValueString("StWorkerPlaceCodeAttendanceCode");  //�����ر���
				}else{
					Info[4] = docMapping.getItemValueString("hrWorkNum");  //�����ر���
				}
				Info[5] = docMapping.getItemValueString("hrDate");  //��ְʱ��
				Info[6] = docMapping.getItemValueString("hrEndDate");  //��ְʱ��
				Info[7] = docMapping.getItemValueString("hrCompNum");  //������˾
				Info[8] = docMapping.getItemValueString("hrWork");  //������
				Info[9] = docMapping.getItemValueString("personNum_HR");  //��Ա�����
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return Info;
	}
}