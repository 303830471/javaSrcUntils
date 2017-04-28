import lotus.domino.*;
import lotus.notes.Session;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
/*
 * 基础信息类
 * 类说明：节假日，调整工作日，作息时间等信息
 */
public class BasicInfo{
	//定义当前作息时间,数组的第0个元素是当前季节名称，第1个元素是上午开始时间，第2个元素是下午结束时间，第三个元素是上午结束时间，第四个元素是下午开始时间，第五个元素是迟到时间，第六个元素是早退时间。
	private String[] workTime =  new String[8];
	//节假日信息,数组为每个节假日日期
	private ArrayList holidays =  new ArrayList();
	//调整工作日,数组为每个调整工作日日期
	private ArrayList specWorkDay =  new ArrayList();
	//工作日上下班时间特殊调整，eg下雨等
	private String changeDay = null;
	//是否全部生效
	private String IsEffectAll = null;
	
	private Database dbcof = null;   //暂时没用
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
	//初始化作息时间
	private void initWorkTime()throws NotesException{
		if(dbareacof != null){
			//季节视图                SeasonalTLBySRQYView                                                  
			//View vwSeason = dbareacof.getView("SeasonalBySRQYView");
			View vwSeason = dbareacof.getView("SeasonalByWorkPlace");
			//获取已激活的季节文档
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
//				System.out.println("考勤日期===  " + config.getStartDate());
//				System.out.println(config.getStartDate().equalsIgnoreCase("2016-07-21"));
//				if(config.getStartDate().equalsIgnoreCase("2016-07-21")){
//					//正常下班时间
//					//workTime[2] = "16:00";
//					//迟到时间
//					workTime[5] = "10:10";
//					//正常早退时间
//					//workTime[6] = "15:00";
//					//正常上班时间
//					workTime[7] = "10:05";
//				}
//				if(config.getStartDate().equalsIgnoreCase("2016-07-20")){
//					//正常下班时间
//					workTime[2] = "16:00";
//					//迟到时间
//					workTime[5] = "10:10";
//					//正常早退时间
//					workTime[6] = "15:00";
//					//正常上班时间
//					workTime[7] = "10:05";
//				}
			}
		}
	}
	//初始化节假日信息
	private void initHolidays() throws NotesException{
		if(dbareacof != null){
			//节假日视图
			View vwHolidays = dbareacof.getView("HolidaysByEndDateYearView");
			//获取节假日
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
	//初始化调整工作日
	private void initSpecWorkDay() throws NotesException {
		if(dbareacof != null){
			//调整工作视图
			View vwSpec = dbareacof.getView("SpecWorkDayByEndDataYearView");
			//获取调整工作日文档				
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
	//判断一个日期是否是工作日
	public boolean isWorkDay(String date, String workPlace){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
			Date dateInfo = sdf.parse(date);   
			Calendar cal = Calendar.getInstance();   
			cal.setTime(dateInfo);
			int day = cal.get(Calendar.DAY_OF_WEEK);
			date = sdf.format(cal.getTime());
			//如果是节假日则说明不是工作日
			if(holidays.contains(date+workPlace)){
				return false;
			}
			if(holidays.contains(date)){
				return false;
			}
			else if(day == 1 || day == 7){	
				//如果是周末，再判断这个周末是否已经调整为工作日。
				if( !specWorkDay.contains(date) ){
					//不是调整的工作日
					if( !specWorkDay.contains(date+workPlace) ){
						//不是调整的工作日
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
	
	//根据人员编号获取部门信息，按顺序为：ITCode，部门名称，部门编号，员工类型，工作地，入职时间�胫笆奔�
	public String[] getMappingInfo(String workerNum,Database dbwork) {
		String[] Info = null;
		try{			
			//按考勤卡号排序的关系映射视图
			View vwMapping = dbwork.getView("psnInfoByWorkerNumber");
			View tspzvw = dbareacof.getView("SpecialWorkerPlaceView");
			
			//获取映射文档
			Document docMapping  = vwMapping.getDocumentByKey(workerNum,true);
			if(docMapping != null){
				Info = new String[10];
				Info[0] = docMapping.getItemValueString("itcode"); //ITCODE
				Info[1] = docMapping.getItemValueString("psnDeptShortText"); //部门名称
				Info[2] = docMapping.getItemValueString("psnDeptItcode");  //部门编号
				Info[3] = docMapping.getItemValueString("hrSubEmploeeType"); //员工类型
				Document tspzdoc = tspzvw.getDocumentByKey(docMapping.getItemValueString("itcode"),true);
				if (tspzdoc != null){
					Info[4] = tspzdoc.getItemValueString("StWorkerPlaceCodeAttendanceCode");  //工作地编码
				}else{
					Info[4] = docMapping.getItemValueString("hrWorkNum");  //工作地编码
				}
				Info[5] = docMapping.getItemValueString("hrDate");  //入职时间
				Info[6] = docMapping.getItemValueString("hrEndDate");  //离职时间
				Info[7] = docMapping.getItemValueString("hrCompNum");  //所属公司
				Info[8] = docMapping.getItemValueString("hrWork");  //工作地
				Info[9] = docMapping.getItemValueString("personNum_HR");  //新员工编号
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return Info;
	}
}