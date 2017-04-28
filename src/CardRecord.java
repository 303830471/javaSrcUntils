import lotus.domino.*;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
/*
 * 刷卡记录
 * 类说明：该类主要用于将打卡数据记录到刷卡记录文档和统计文档中，并进行统计
 */
public class CardRecord {
	private Database dbAttendance = null;	//统计数据库
	private Database dbRecode = null;       //刷卡记录数据库
	private Database dbwork= null;	        //人员数据库
	private Database dbcof = null;          //公共配置库
	private Database dbareacof = null;      //地区配置库
	private BasicInfo bInfo = null;		    //基础数据
	private Configuration config = null;   // 数据库连接查询配置信息
	
	public CardRecord(Database db , Database dbwork ,Database dbcof, Database dbareacof,Database dbRecode,Configuration config){
		this.dbAttendance = db;
		this.dbRecode = dbRecode;
		this.dbwork = dbwork;
		this.dbcof = dbcof;
		this.dbareacof = dbareacof;
		this.bInfo = new BasicInfo(dbcof,dbareacof,config);
		this.config = config;
	}
	/*
	 * 对统计文档进行统计
	 * 参数：docStat统计文档
	 */
	private void takeStatistics(Document docStat) throws NotesException{
		if(docStat == null) return;
		String[] dayInfo = null;
		String LeaveInfo;
		String[] LeaveInfoarr = null;
		String tempDate = null;
		java.text.DecimalFormat timecbslwxs = new java.text.DecimalFormat("#0.00");
		//统计前先清空以前的统计结果
		docStat.replaceItemValue("StPerfect","yes");
		docStat.replaceItemValue("StZCworkdays","0"); 			//应出勤天数
		docStat.replaceItemValue("StSJworkdays","0"); 			//实际出勤天数
		docStat.replaceItemValue("StAbsence","0");    			//矿工天数
		docStat.replaceItemValue("StAbsenceTime","0");    		//矿工（小时）
		docStat.replaceItemValue("StNormal","0");    			//正常出勤天数（工资不用）
		docStat.replaceItemValue("StLate","0");      			//迟到次数
		docStat.replaceItemValue("StLeaveEarly","0");  			//早退次数
		docStat.replaceItemValue("StLateAndLeaveEarly","0");  	//早退和迟到次数和
		int lslate = 0;
		int lsearly = 0;
		int lslateandearly = 0;
		int lsmormaldays = 0;
		int lszcworkdays = 0;
		double lssjworkdays = 0;
		double lsAm = 0.00;
		double lsPm = 0.00;
		double lsAPM = 0.00;
		//这里parseInt报过错，显示StDays为""，没有复现过，待改！
		for(int i = 1; i <= Integer.parseInt(docStat.getItemValueString("StDays")); i++){
			tempDate = docStat.getItemValueString("StDocID").substring(0,8) + i;
			//进行统计
			if( !docStat.getItemValueString("StC" + i).equals("")){					
				//是工作日则进行统计
				if(bInfo.isWorkDay(tempDate,docStat.getItemValueString("StWorkerPlace"))){
					lslate = 0;
					lsearly = 0;
					lslateandearly = 0;
					lsmormaldays = 0;
					lsAm = 0.00;
					lsPm = 0.00;
					lsAPM = 0.00;
					//格式：最早打卡时间;最晚打卡时间;迟到/上午旷工/上午正常(3选1);旷工时间；早退/下午旷工/下午正常(3选1)旷工时间
					//数据：HH:mm;HH:mm;Late/AbsenceAM/NormalAM/LateandAbsenceAM;times;LeaveEarly/AbsencePM/LeaveEarlyandAbsencePM/NormalPM;times
					dayInfo = docStat.getItemValueString("StC" + i).split(";",7);
					//System.out.println("==== " + docStat.getItemValueString("StC" + i));
					if(dayInfo.length > 1){
						//计算迟到次数
						//System.out.println("----- " + docStat.getItemValueString("StPsnCN"));
						if(dayInfo[2].equals("Late") || dayInfo[2].equals("LateandAbsenceAM")){
							lslate = Integer.parseInt(docStat.getItemValueString("StLate")) + 1;
							lslateandearly = Integer.parseInt(docStat.getItemValueString("StLateAndLeaveEarly")) + 1;
				    		docStat.replaceItemValue("StLate",String.valueOf(lslate));
				    		docStat.replaceItemValue("StLateAndLeaveEarly",String.valueOf(lslateandearly));
						}
						//计算早退次数
						if(dayInfo[4].equals("LeaveEarly") || dayInfo[4].equals("LeaveEarlyandAbsencePM")){
							lsearly = Integer.parseInt(docStat.getItemValueString("StLeaveEarly")) + 1;
							lslateandearly = Integer.parseInt(docStat.getItemValueString("StLateAndLeaveEarly")) + 1;
							docStat.replaceItemValue("StLeaveEarly",String.valueOf(lsearly));
							docStat.replaceItemValue("StLateAndLeaveEarly",String.valueOf(lslateandearly));
						}
						//计算旷工时间(小时)
						lsAm = Double.parseDouble(dayInfo[3]);
						lsPm = Double.parseDouble(dayInfo[5]);
						lsAPM = Double.parseDouble(docStat.getItemValueString("StAbsenceTime"))+lsAm+lsPm;
						docStat.replaceItemValue("StAbsenceTime",String.valueOf(timecbslwxs.format(lsAPM)));
						//计算旷工时间(天数)
						docStat.replaceItemValue("StAbsence",String.valueOf(timecbslwxs.format(lsAPM/8)));
						//计算正常出勤天数，必须是上下午都正常
						if(docStat.getItemValueString("StC" + i).indexOf("NormalAM") > 0 && docStat.getItemValueString("StC" + i).indexOf("NormalPM") > 0 ){
							lsmormaldays = Integer.parseInt(docStat.getItemValueString("StNormal")) + 1;
							docStat.replaceItemValue("StNormal",String.valueOf(lsmormaldays));
						}else{
							docStat.replaceItemValue("StPerfect","no");
						}
					}
					//应出勤天数
					lszcworkdays = Integer.parseInt(docStat.getItemValueString("StZCworkdays")) + 1;
					docStat.replaceItemValue("StZCworkdays",String.valueOf(lszcworkdays));
					//实际出勤天数
					lssjworkdays = Double.parseDouble(docStat.getItemValueString("StZCworkdays"))-Double.parseDouble(docStat.getItemValueString("StAbsence"));
					docStat.replaceItemValue("StSJworkdays",String.valueOf(timecbslwxs.format(lssjworkdays)));
				}
			}
			docStat.save(true,true);
		}
	}
	/*
	 * 判断是否迟到，早退，旷工等
	 * 参数：startTime：最早打卡时间，endTime最晚打卡时间
	 */
	private String judgeTime(String startTime,String endTime,String name)
	{
		String result = null;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		java.text.DecimalFormat timecbslwxs = new java.text.DecimalFormat("#0.00");
		String[] state = new String[6];
		long between = 0L;	//时间差
		long betweeno = 0L;	//时间差
		long betweent = 0L;	//时间差
		long betweens = 0L;	//时间差
		long betweenf = 0L;	//时间差
		long betweensi = 0L;	//时间差
		long betweensix = 0L;	//时间差
		long betweensev = 0L;	//时间差
		long betweeneig = 0L;	//时间差
		long betweenten = 0L;	//时间差
		double timecbs = 0.00;
		try{				
			//作息时间
			//上午上班时间
			Date workTimeAM = sdf.parse(bInfo.getWorkTime()[1]);
			//下午下班时间
			Date workTimePM = sdf.parse(bInfo.getWorkTime()[2]);
			//上午结束时间
			Date workTimeEndAM = sdf.parse(bInfo.getWorkTime()[3]);
			//下午开始时间
			Date workTimeStartPM = sdf.parse(bInfo.getWorkTime()[4]);
			//迟到时间
			Date workTimeLateAM = sdf.parse(bInfo.getWorkTime()[5]);
			//早退时间
			Date workTimeLevEarlyPM = sdf.parse(bInfo.getWorkTime()[6]);
			//迟到开始时间
			Date workTimeStartLatePM = sdf.parse(bInfo.getWorkTime()[7]);
			//一天的时间分为9:00—10:00-12:00-13:00-17:00-18:00
			/*
			 * 上午旷工时间计算
			 * 1、如果startTime(开始时间)=""，则上午旷工，旷工时间为3个小时；
			 * 2、如果startTime(开始时间)> workTimeEndAM(上午结束时间),则为上午旷工，旷工时间为3个小时；
			 * 3、如果workTimeLateAM(迟到时间)<startTime(开始时间)< workTimeEndAM(上午结束时间) ， 则为上午旷工：计算旷工时间有几种情况；
			 * <1>、如果endTime(结束时间)< workTimeEndAM(上午结束时间)，旷工时间 = starttime - workTimeAM + workTimeEndAM - endTime ；
			 * <2>、如果endtime(结束时间)> workTimeEndAM(上午结束时间),旷工时间 = starttime - workTimeAM
			 * */
			if(startTime.equals("")){
				state[0] = "AbsenceAM";
				state[1] = String.valueOf("3.00");
			}else{
				//开始时间
				Date earliest = sdf.parse(startTime);
				//结束时间
				if(endTime.equals("")){
					endTime = startTime;
				}
				Date lastest = sdf.parse(endTime);
				//开始时间-9:00
				between = earliest.getTime() - workTimeAM.getTime();
				//开始时间-9:05
				betweenten = earliest.getTime() - workTimeStartLatePM.getTime();
				//开始时间-12:00
				betweent = earliest.getTime() - workTimeEndAM.getTime();
				//结束时间-12:00
				betweeno = lastest.getTime() - workTimeEndAM.getTime();
				//开始时间-10：00
				betweens = earliest.getTime() - workTimeLateAM.getTime();
				//12：00-结束时间
				betweenf = workTimeEndAM.getTime() - lastest.getTime();
				//12:00-开始时间
				betweensi =  workTimeEndAM.getTime() - earliest.getTime();
				//结束时间-9:00
				betweensix = lastest.getTime() - workTimeAM.getTime();
				//开始时间 -结束时间
				betweensev = earliest.getTime() - lastest.getTime();
				//结束时间-10:00
				betweeneig = lastest.getTime() -  workTimeLateAM.getTime();
				//迟到：开始时间>9:00 and 开始时间<=10:00
				if(betweenten > 0 && betweens <= 0){
					state[0] = "Late";
					state[1] = String.valueOf("0.00");
				}
				//旷工：开始时间>=12:00；旷工3个小时
				if (betweent>=0)
				{
					state[0] = "AbsenceAM";
					state[1] = String.valueOf("3.00");
				}
				//旷工：开始时间>10:00
				if (betweens>0){
					state[0] = "AbsenceAM";
					//开始时间>=12：00;旷工3个小时
					if (betweent>=0)
					{
						state[1] = String.valueOf("3.00");
					//开始时间<12:00
					}else{
						//结束时间>=12:00；旷工时间=开始时间-9:00
						if (betweeno>=0){
							timecbs = Double.parseDouble(String.valueOf(between))/Double.parseDouble(String.valueOf(3600000)); 
							state[1] = String.valueOf(timecbslwxs.format(timecbs));
						//结束时间<12:00;旷工时间 = 开始时间-9:00+12:00-结束时间
						}else{
							timecbs = Double.parseDouble(String.valueOf(between))/Double.parseDouble(String.valueOf(3600000)); 
							timecbs = timecbs + Double.parseDouble(String.valueOf(betweenf)) / Double.parseDouble(String.valueOf(3600000));
							state[1] = String.valueOf(timecbslwxs.format(timecbs));
						}
					}
				}
				//旷工：结束时间<12:00
				if (betweeno<0){
					//结束时间<=9:00;旷工时间 = 3个小时
					
					if (betweensix<=0)
					{
						state[0] = "AbsenceAM";
						state[1] = String.valueOf("3.00");
					//结束时间>9:00
					}else{
				
						//'开始时间>10:00;旷工时间=12:00-结束时间+开始时间-9:00
						if (betweens>0){
							state[0] = "AbsenceAM";
							timecbs = Double.parseDouble(String.valueOf(between))/Double.parseDouble(String.valueOf(3600000)); 
							timecbs = timecbs + Double.parseDouble(String.valueOf(betweenf)) / Double.parseDouble(String.valueOf(3600000));
							state[1] = String.valueOf(timecbslwxs.format(timecbs));
						}//开始时间<=10:00;旷工时间=12:00-结束时间
						else{
							//开始时间>9:05 算一次迟到和旷工
							if (betweenten>0){
								state[0] = "LateandAbsenceAM";
								timecbs = Double.parseDouble(String.valueOf(betweenf)) / Double.parseDouble(String.valueOf(3600000));
								state[1] = String.valueOf(timecbslwxs.format(timecbs));
							}else{
								state[0] = "AbsenceAM";
								timecbs = Double.parseDouble(String.valueOf(betweenf)) / Double.parseDouble(String.valueOf(3600000));
								state[1] = String.valueOf(timecbslwxs.format(timecbs));
							}
						}	
					}
				}
				//旷工：开始时间=结束时间；旷工3个小时
				if (betweensev==0)
				{
					state[0] = "AbsenceAM";
					state[1] = String.valueOf("3.00");
				}
		        if (betweenten<=0 && betweeno>=0){
					//正常
					state[0] = "NormalAM";
					state[1] = String.valueOf("0.00");
				}
			}
			/*
			 * 下午旷工时间计算
			 * 1、如果endTime(开始时间)=""，则下午旷工，旷工时间为5个小时；
			 * 2如果endTime(开始时间)< workTimeEndAM(上午结束时间),则为下午旷工，旷工时间为5个小时；
			 * 3、如果workTimeEndAM(上午结束时间)<endTime(开始时间)< workTimeLevEarlyPM(早退时间) ， 则为下旷工：计算旷工时间有几种情况			 
			 * <1>、如果starttime(开始时间)<workTimeStartPM (挛缈际奔，旷工时间 = workTimePM-endTime
			 * <2>、如果starttime(开始时间)>workTimeStartPM (下午开始时间),旷工时间 = endTime - workTimeStartPM + workTimePM- endtime
			 * */
			if(endTime.equals("")){
				state[2] = "AbsencePM";
				state[3] = String.valueOf("5.00");
			}else{
				//开始时间
				if(startTime.equals("")){
					startTime = endTime;
				}
				Date earliest = sdf.parse(startTime);
				//结束时间
				Date lastest = sdf.parse(endTime);
				//18:00-结束奔
				between = workTimePM.getTime() - lastest.getTime();
				//结束时间-12:00
				betweens = lastest.getTime() - workTimeEndAM.getTime();
				//开始时间-13:00
				betweeno = earliest.getTime() - workTimeStartPM.getTime();
				//结束时间-17:00
				betweent = lastest.getTime() - workTimeLevEarlyPM.getTime();
				//13:00-结			betweensi =  workTimeStartPM.getTime() - lastest.getTime();
				//开始时间-18：00
				betweensix = earliest.getTime() - workTimePM.getTime();
				//结束时间-13:00
				betweensev = lastest.getTime() - workTimeStartPM.getTime();
				//开始时间 -结束时间
				betweeneig = earliest.getTime() - lastest.getTime();
				//最早打卡时间和下午开始时间(13:00)的时间
				//如果下午结束时间>最晚打卡时间 and  最晚打卡时间>早退时间 则为早退
				//18:00大于结束时间 and 结束时间>=17:00
				if(between > 0 && betweent >= 0){
					state[2] = "LeaveEarly";
					state[3] = String.valueOf("0.00");
				}
				//结束时间<13:00;旷工5个小时
				if(betweensev<0){
					state[2] = "AbsencePM";
					state[3] = String.valueOf("5.00");
				}
				//结束时间<17:00
				if (betweent<0 ){
					state[2] = "AbsencePM";
					//jssj<=13:00;旷工时间 = 5
					if (betweensev<=0){
						state[3] = String.valueOf("5.00");
						//jssj>13:00
					}else{
						//kssj<=13:00；旷工时间 = 18:00-结束时间，否则旷工时间 = 18:00-结束时间+开始时间-13:00
						if (betweeno<=0){
							timecbs = Double.parseDouble(String.valueOf(between))/Double.parseDouble(String.valueOf(3600000)); 
							state[3] = String.valueOf(timecbslwxs.format(timecbs));
						}else{
							timecbs = Double.parseDouble(String.valueOf(between))/Double.parseDouble(String.valueOf(3600000)); 
							timecbs = timecbs + Double.parseDouble(String.valueOf(betweeno)) / Double.parseDouble(String.valueOf(3600000));
							state[3] = String.valueOf(timecbslwxs.format(timecbs));	
						}	
					}
				}
				//开始时间>13:00
				if (betweeno>0){
					//kssj>=18:00;kgsj=5
					if (betweensix>=0){
						state[2] = "AbsencePM";
						state[3] = String.valueOf("5.00");
						//kssj<18:00
					}else{
						//jssj<17:00 kssj = 18:00 -jssj+kssj-13:00
						if (betweent<0){
							state[2] = "AbsencePM";
							timecbs = Double.parseDouble(String.valueOf(between))/Double.parseDouble(String.valueOf(3600000)); 
							timecbs = timecbs + Double.parseDouble(String.valueOf(betweeno)) / Double.parseDouble(String.valueOf(3600000));
							state[3] = String.valueOf(timecbslwxs.format(timecbs));	
						//jssj>=17:00
						}else{
							//18:00小于结束时间；旷工时间 = 开始时间-13:00
							if (between<0){
								state[2] = "AbsencePM";
								timecbs = Double.parseDouble(String.valueOf(betweeno)) / Double.parseDouble(String.valueOf(3600000));
								state[3] = String.valueOf(timecbslwxs.format(timecbs));	
							}else{
								if (betweeneig == 0){
									state[2] = "AbsencePM";
									state[3] = String.valueOf("5.00");
								}else{
								state[2] = "LeaveEarlyandAbsencePM";
								timecbs = Double.parseDouble(String.valueOf(betweeno)) / Double.parseDouble(String.valueOf(3600000));
								state[3] = String.valueOf(timecbslwxs.format(timecbs));	
								}
							}	
						}
					}
				}
			   if (between<=0 && betweeno<=0){
					state[2] = "NormalPM";
					state[3] = String.valueOf("0.00");
				}
			}
			//格式： 最早刷卡时间;最迟刷卡时上午考勤信息;下午考勤信息（迟到/上午豕上午正常;早退/下午旷工/下午正常）
			//数据： YYYY-MM-DD;YYYY-MM-DD;Late/AbsenceAM/NormalAM;LeaveEarly/AbsencePM/NormalPM
			result = startTime.trim() + ";" + endTime.trim() + ";" + state[0] + ";" + state[1]+ ";" + state[2]+ ";" + state[3];
		}catch(java.text.ParseException e){ 
			e.printStackTrace();
		}
		return result;
	}
	/*
	 * 根据刷卡记录保计算考勤统计信息，并将考勤统计信息记录到统计文档中
	 * 参数：docCard刷卡记录文档信息,docStat统计文档
	 */
	private Document recordAttendance(Document docCard,Document docStat){		
		try{
			if(dbAttendance != null && docCard != null){
				//-------------------------日期计算---------------------------
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				
				Calendar cal = Calendar.getInstance();
				String date = docCard.getItemValueString("StRecordDate");
			
				String[] LeaveInfoarr = null;
				Date dateInfo = sdf.parse(date);
				cal.setTime(dateInfo);
				//如果统计文档不存在就创建一个
				if(docStat == null){
					//统计文档主键，格式yyyy-mm-考勤卡号，所以这里取日期前8个字符，再加上考勤考号
					String docID = docCard.getItemValueString("StRecordDate").substring(0,8) + docCard.getItemValueString("StWorkerNumber");
					docStat = dbAttendance.createDocument();
					                                 
					docStat.replaceItemValue("Form","AttendanceListForm");
					docStat.replaceItemValue("StDocID",docID.trim());
					docStat.replaceItemValue("StDeptID",docCard.getItemValueString("StDeptID"));
					docStat.replaceItemValue("StDeptName",docCard.getItemValueString("StDeptName"));
					docStat.replaceItemValue("StCardCode",docCard.getItemValueString("StCardCode"));
					docStat.replaceItemValue("StPsnCN",docCard.getItemValueString("StPsnCN"));
					docStat.replaceItemValue("StPsnEN",docCard.getItemValueString("StPsnEN"));
					docStat.replaceItemValue("StWorkerNumber",docCard.getItemValueString("StWorkerNumber"));
					docStat.replaceItemValue("StWorkerType",docCard.getItemValueString("StWorkerType"));
					docStat.replaceItemValue("StWorkerPlaceNum",docCard.getItemValueString("StWorkerPlaceNum"));
					docStat.replaceItemValue("StWorkerPlace",docCard.getItemValueString("StWorkerPlace"));
					docStat.replaceItemValue("StEntryDate",docCard.getItemValueString("StEntryDate"));
					docStat.replaceItemValue("StLeaveDate",docCard.getItemValueString("StLeaveDate"));
					docStat.replaceItemValue("StCompanyCode",docCard.getItemValueString("StCompanyCode"));
					docStat.replaceItemValue("StNewWorkerNumber",docCard.getItemValueString("StNewWorkerNumber"));
					//年份：yyyy
					docStat.replaceItemValue("StYear",date.substring(0,4));
					//月份：mm
					docStat.replaceItemValue("StMonth",date.substring(5,7));
					//月份的天数
					docStat.replaceItemValue("StDays",String.valueOf(cal.getActualMaximum(Calendar.DAY_OF_MONTH)));
					
				}
				/*如果有请休假信息，则开始时间和结束时间取请休假信息的实际开始时间和结束时间*/
				
				if( !docStat.getItemValueString("StL" + cal.get(Calendar.DAY_OF_MONTH)).equals("")){	
					
					LeaveInfoarr = docStat.getItemValueString("StL" + cal.get(Calendar.DAY_OF_MONTH)).split("/");
					
					if (LeaveInfoarr.length>14){
						String ssjstarttime = LeaveInfoarr[14];
						String ssjendtime = LeaveInfoarr[15];
						if (ssjstarttime.equals("")){
							ssjstarttime = docCard.getItemValueString("StStartTime");
						}
						
						if (ssjendtime.equals("")){
							ssjendtime = docCard.getItemValueString("StEndTime");
						}
						
						docStat.replaceItemValue("StC" + cal.get(Calendar.DAY_OF_MONTH),judgeTime(ssjstarttime,ssjendtime,docCard.getItemValueString("StUserName")) + ";");
						docStat.save(true,true);
					}else{
						docStat.replaceItemValue("StC" + cal.get(Calendar.DAY_OF_MONTH),judgeTime(docCard.getItemValueString("StStartTime"),docCard.getItemValueString("StEndTime"),docCard.getItemValueString("StUserName")) + ";");
						docStat.save(true,true);
					}
				}else{
					docStat.replaceItemValue("StC" + cal.get(Calendar.DAY_OF_MONTH),judgeTime(docCard.getItemValueString("StStartTime"),docCard.getItemValueString("StEndTime"),docCard.getItemValueString("StUserName")) + ";");
					docStat.save(true,true);
				}
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return docStat;
	}
	/*
	 * 从数据源获取刷卡记录，并将刷卡记录保存到domino文档中
	 * 参数：数据源
	 */	 
	public void receiveData(DataSource data){	
		try{
			if(data != null && data.isConsistency()){
				//关键字（打卡日期的年月&员工编号）
				String oldStatId = ((String)data.getRecordDate().get(0)).substring(0,8) + (String)data.getWorkerNumber().get(0);
				String newStatId = oldStatId;
				View vwStat = dbAttendance.getView("AttendanceListView");
				//考勤统计文档	
				Document docStat  = vwStat.getDocumentByKey(oldStatId.trim(),true);
				String[] info = new String[6];
				for(int i = 0; i < data.getCardID().size(); i++){
				
					info[0] = (String)data.getCardID().get(i); 
					info[1] = (String)data.getUserName().get(i); 
					info[2] = (String)data.getRecordDate().get(i);
					info[3] = (String)data.getEarliestTime().get(i);
					info[4] = (String)data.getLatestTime().get(i);
					info[5] = (String)data.getWorkerNumber().get(i);
					
					String[] mappingInfo = bInfo.getMappingInfo(info[5].trim(),dbwork);
					if(mappingInfo != null){
						if(mappingInfo[0].trim().equals("ZHIMING"))
						{
							System.out.print(mappingInfo[0]);
							System.out.print(mappingInfo[4]);
						}
						if(mappingInfo[4].trim().equals("0001")){
							//判断入职时间和离职时间 如果入职时间小于等于今天 则执行 如果离职时间大于今天则执行 否则不执行
							SimpleDateFormat sdffeui = new SimpleDateFormat("yyyy-MM-dd");  		
							Date workBegin = sdffeui.parse(mappingInfo[5].trim());
							Date noworkBegin = sdffeui.parse(mappingInfo[6].trim());
							Calendar cal = Calendar.getInstance(); 
							cal.add(Calendar.DATE,-1);    
							Date Currentyestoday = sdffeui.parse(sdffeui.format(cal.getTime()));
							Date crcofigdate = sdffeui.parse(config.getStartDate().trim());
							if (workBegin.getTime()<=crcofigdate.getTime()){
								 if( mappingInfo[6].equals("0000-00-00")){
										newStatId = info[2].substring(0,8) + info[5];					
										if(!newStatId.equals(oldStatId)){
											//如果是不同的统计文档，则对前一个统计文档进行统计
											takeStatistics(docStat);
											docStat  = vwStat.getDocumentByKey(newStatId.trim(),true);
											oldStatId = newStatId;
										}
										docStat = recordAttendance(recordToDoc(info),docStat);
								 }else{
									 if (noworkBegin.getTime()>crcofigdate.getTime()){
										 newStatId = info[2].substring(0,8) + info[5];					
											if(!newStatId.equals(oldStatId)){
												//如果是不同的统计文档，则对前一个臣莆牡到型臣
												takeStatistics(docStat);
												docStat  = vwStat.getDocumentByKey(newStatId.trim(),true);
												oldStatId = newStatId;
											}
											docStat = recordAttendance(recordToDoc(info),docStat);	
									}
								 }
							}
						}
					}
				}
				//对最后一个统计文档进行统计
				takeStatistics(docStat);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * 将刷卡记录保存到文档中
	 * 参数：Info打卡记录，按承蛭记诳ê牛惫ば彰蚩ㄈ掌冢钤绱蚩ㄊ奔洌钔泶蚩ㄊ奔
	 * 参数：isStat是否需要统计
	 */
	public Document recordToDoc(String[] Info){
		Document docCard = null;
		try{		
			if(dbRecode != null && Info != null){
				View vwCard = dbRecode.getView("CardRecordByWorkNumAndRecordDateView");
				docCard  = vwCard.getDocumentByKey(Info[5] + Info[2],true);
				String[] mappingInfo = bInfo.getMappingInfo(Info[5].trim(),dbwork);
				//把打卡时间格式调整为"小时:分钟"
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");				
				String eTime = "";
				String lTime = "";
				if(!Info[3].trim().equals("")){
					Date earliest = sdf.parse(Info[3].trim());
					eTime = sdf.format(earliest);
				}
				if(!Info[4].trim().equals("")){
					Date latest = sdf.parse(Info[4].trim());
					lTime = sdf.format(latest);
				}
				if( docCard != null){
					//如果以前的记录来源是自动导入，则直接替换以前的记录。
					if( docCard.getItemValueString("StSource").equals("AutoImportBeijing") ){
						docCard.replaceItemValue("StStartTime",eTime);
						docCard.replaceItemValue("StEndTime",lTime);
					}else{
						//"-"代表数据为空
						if( docCard.getItemValueString("StStartTime").equals("-") ){
							docCard.replaceItemValue("StStartTime",eTime);
						}
						if( docCard.getItemValueString("StEndTime").equals("-") ){
							docCard.replaceItemValue("StEndTime",lTime);
						}
					}
					docCard.replaceItemValue("StCardCode",Info[0].trim());
					docCard.replaceItemValue("StPsnCN",Info[1].replace(" ",""));
					docCard.replaceItemValue("StRecordDate",Info[2].trim());
					docCard.replaceItemValue("StWorkerNumber", Info[5].trim());
			
					docCard.replaceItemValue("StPsnEN",mappingInfo[0].trim());
					docCard.replaceItemValue("StDeptName",mappingInfo[1].trim());
					docCard.replaceItemValue("StDeptID",mappingInfo[2].trim());
					docCard.replaceItemValue("StWorkerType",mappingInfo[3].trim());
					docCard.replaceItemValue("StWorkerPlaceNum",mappingInfo[4].trim());
					docCard.replaceItemValue("StEntryDate",mappingInfo[5].trim());
					docCard.replaceItemValue("StLeaveDate",mappingInfo[6].trim());
					docCard.replaceItemValue("StCompanyCode",mappingInfo[7].trim());
					docCard.replaceItemValue("StWorkerPlace",mappingInfo[8].trim());
					docCard.replaceItemValue("StNewWorkerNumber",mappingInfo[9].trim());
				}
				else{
					//从组织库中获取itcode、部门名称、部门ID等内容
					if(mappingInfo != null){
						if(mappingInfo[0].trim().equals("ZHIMING"))
						{
							System.out.print(mappingInfo[0]);
							System.out.print(mappingInfo[4]);
						}
				
						if(mappingInfo[4].trim().equals("0001"))
						{
							
							//判断入职时间和离职时间 如果入职时间小于等于今天 则执行 如果离职时间大于今天则执行 否则不执行
							SimpleDateFormat sdffeui = new SimpleDateFormat("yyyy-MM-dd");  		
							Date workBegin = sdffeui.parse(mappingInfo[5].trim());
							Date noworkBegin = sdffeui.parse(mappingInfo[6].trim());
							Calendar cal = Calendar.getInstance(); 
							cal.add(Calendar.DATE,-1);    
							Date Currentyestoday = sdffeui.parse(sdffeui.format(cal.getTime()));
							Date crcofigdate = sdffeui.parse(config.getStartDate().trim());
							//if (!(mappingInfo[2].trim().equals("BM_ZZWLZX_ZZB") || mappingInfo[2].trim().equals("BM_ZZWLZX_CCB") || mappingInfo[2].trim().equals("BM_ZZWLZX_SMT") || mappingInfo[2].trim().equals("BM_PZGLB")) ){
								
							if (workBegin.getTime()<=crcofigdate.getTime())
							{
								if( mappingInfo[6].equals("0000-00-00")){	
									docCard = dbRecode.createDocument();
									docCard.replaceItemValue("Form","CardRecordForm");
									docCard.replaceItemValue("StCardCode",Info[0].trim());
									docCard.replaceItemValue("StPsnCN",Info[1].replace(" ",""));
									docCard.replaceItemValue("StRecordDate",Info[2].trim());
									docCard.replaceItemValue("StStartTime",eTime);
									docCard.replaceItemValue("StEndTime",lTime);
									docCard.replaceItemValue("StWorkerNumber", Info[5].trim());
							
									docCard.replaceItemValue("StPsnEN",mappingInfo[0].trim());
									docCard.replaceItemValue("StDeptName",mappingInfo[1].trim());
									docCard.replaceItemValue("StDeptID",mappingInfo[2].trim());
									docCard.replaceItemValue("StWorkerType",mappingInfo[3].trim());
									docCard.replaceItemValue("StWorkerPlaceNum",mappingInfo[4].trim());
									docCard.replaceItemValue("StEntryDate",mappingInfo[5].trim());
									docCard.replaceItemValue("StLeaveDate",mappingInfo[6].trim());
									docCard.replaceItemValue("StCompanyCode",mappingInfo[7].trim());
									docCard.replaceItemValue("StWorkerPlace",mappingInfo[8].trim());
									docCard.replaceItemValue("StNewWorkerNumber",mappingInfo[9].trim());
							
									//数据来源设为自动导入
									docCard.replaceItemValue("StSource","AutoImportBeijing");
								}else{
									if (noworkBegin.getTime()>crcofigdate.getTime())
									{
										docCard = dbRecode.createDocument();
										docCard.replaceItemValue("Form","CardRecordForm");
										docCard.replaceItemValue("StCardCode",Info[0].trim());
										docCard.replaceItemValue("StPsnCN",Info[1].replace(" ",""));
										docCard.replaceItemValue("StRecordDate",Info[2].trim());
										docCard.replaceItemValue("StStartTime",eTime);
										docCard.replaceItemValue("StEndTime",lTime);
										docCard.replaceItemValue("StWorkerNumber", Info[5].trim());
								
										docCard.replaceItemValue("StPsnEN",mappingInfo[0].trim());
										docCard.replaceItemValue("StDeptName",mappingInfo[1].trim());
										docCard.replaceItemValue("StDeptID",mappingInfo[2].trim());
										docCard.replaceItemValue("StWorkerType",mappingInfo[3].trim());
										docCard.replaceItemValue("StWorkerPlaceNum",mappingInfo[4].trim());
										docCard.replaceItemValue("StEntryDate",mappingInfo[5].trim());
										docCard.replaceItemValue("StLeaveDate",mappingInfo[6].trim());
										docCard.replaceItemValue("StCompanyCode",mappingInfo[7].trim());
										docCard.replaceItemValue("StWorkerPlace",mappingInfo[8].trim());
										docCard.replaceItemValue("StNewWorkerNumber",mappingInfo[9].trim());
										//数据来源设为自动导入
										docCard.replaceItemValue("StSource","AutoImportBeijing");
									}
								}
							}
							}
						//}
					}
					
				}
				docCard.save(true,true);
				return docCard;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return docCard;
	}
}


