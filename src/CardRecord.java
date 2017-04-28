import lotus.domino.*;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
/*
 * ˢ����¼
 * ��˵����������Ҫ���ڽ������ݼ�¼��ˢ����¼�ĵ���ͳ���ĵ��У�������ͳ��
 */
public class CardRecord {
	private Database dbAttendance = null;	//ͳ�����ݿ�
	private Database dbRecode = null;       //ˢ����¼���ݿ�
	private Database dbwork= null;	        //��Ա���ݿ�
	private Database dbcof = null;          //�������ÿ�
	private Database dbareacof = null;      //�������ÿ�
	private BasicInfo bInfo = null;		    //��������
	private Configuration config = null;   // ���ݿ����Ӳ�ѯ������Ϣ
	
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
	 * ��ͳ���ĵ�����ͳ��
	 * ������docStatͳ���ĵ�
	 */
	private void takeStatistics(Document docStat) throws NotesException{
		if(docStat == null) return;
		String[] dayInfo = null;
		String LeaveInfo;
		String[] LeaveInfoarr = null;
		String tempDate = null;
		java.text.DecimalFormat timecbslwxs = new java.text.DecimalFormat("#0.00");
		//ͳ��ǰ�������ǰ��ͳ�ƽ��
		docStat.replaceItemValue("StPerfect","yes");
		docStat.replaceItemValue("StZCworkdays","0"); 			//Ӧ��������
		docStat.replaceItemValue("StSJworkdays","0"); 			//ʵ�ʳ�������
		docStat.replaceItemValue("StAbsence","0");    			//������
		docStat.replaceItemValue("StAbsenceTime","0");    		//�󹤣�Сʱ��
		docStat.replaceItemValue("StNormal","0");    			//�����������������ʲ��ã�
		docStat.replaceItemValue("StLate","0");      			//�ٵ�����
		docStat.replaceItemValue("StLeaveEarly","0");  			//���˴���
		docStat.replaceItemValue("StLateAndLeaveEarly","0");  	//���˺ͳٵ�������
		int lslate = 0;
		int lsearly = 0;
		int lslateandearly = 0;
		int lsmormaldays = 0;
		int lszcworkdays = 0;
		double lssjworkdays = 0;
		double lsAm = 0.00;
		double lsPm = 0.00;
		double lsAPM = 0.00;
		//����parseInt��������ʾStDaysΪ""��û�и��ֹ������ģ�
		for(int i = 1; i <= Integer.parseInt(docStat.getItemValueString("StDays")); i++){
			tempDate = docStat.getItemValueString("StDocID").substring(0,8) + i;
			//����ͳ��
			if( !docStat.getItemValueString("StC" + i).equals("")){					
				//�ǹ����������ͳ��
				if(bInfo.isWorkDay(tempDate,docStat.getItemValueString("StWorkerPlace"))){
					lslate = 0;
					lsearly = 0;
					lslateandearly = 0;
					lsmormaldays = 0;
					lsAm = 0.00;
					lsPm = 0.00;
					lsAPM = 0.00;
					//��ʽ�������ʱ��;�����ʱ��;�ٵ�/�������/��������(3ѡ1);����ʱ�䣻����/�������/��������(3ѡ1)����ʱ��
					//���ݣ�HH:mm;HH:mm;Late/AbsenceAM/NormalAM/LateandAbsenceAM;times;LeaveEarly/AbsencePM/LeaveEarlyandAbsencePM/NormalPM;times
					dayInfo = docStat.getItemValueString("StC" + i).split(";",7);
					//System.out.println("==== " + docStat.getItemValueString("StC" + i));
					if(dayInfo.length > 1){
						//����ٵ�����
						//System.out.println("----- " + docStat.getItemValueString("StPsnCN"));
						if(dayInfo[2].equals("Late") || dayInfo[2].equals("LateandAbsenceAM")){
							lslate = Integer.parseInt(docStat.getItemValueString("StLate")) + 1;
							lslateandearly = Integer.parseInt(docStat.getItemValueString("StLateAndLeaveEarly")) + 1;
				    		docStat.replaceItemValue("StLate",String.valueOf(lslate));
				    		docStat.replaceItemValue("StLateAndLeaveEarly",String.valueOf(lslateandearly));
						}
						//�������˴���
						if(dayInfo[4].equals("LeaveEarly") || dayInfo[4].equals("LeaveEarlyandAbsencePM")){
							lsearly = Integer.parseInt(docStat.getItemValueString("StLeaveEarly")) + 1;
							lslateandearly = Integer.parseInt(docStat.getItemValueString("StLateAndLeaveEarly")) + 1;
							docStat.replaceItemValue("StLeaveEarly",String.valueOf(lsearly));
							docStat.replaceItemValue("StLateAndLeaveEarly",String.valueOf(lslateandearly));
						}
						//�������ʱ��(Сʱ)
						lsAm = Double.parseDouble(dayInfo[3]);
						lsPm = Double.parseDouble(dayInfo[5]);
						lsAPM = Double.parseDouble(docStat.getItemValueString("StAbsenceTime"))+lsAm+lsPm;
						docStat.replaceItemValue("StAbsenceTime",String.valueOf(timecbslwxs.format(lsAPM)));
						//�������ʱ��(����)
						docStat.replaceItemValue("StAbsence",String.valueOf(timecbslwxs.format(lsAPM/8)));
						//�����������������������������綼����
						if(docStat.getItemValueString("StC" + i).indexOf("NormalAM") > 0 && docStat.getItemValueString("StC" + i).indexOf("NormalPM") > 0 ){
							lsmormaldays = Integer.parseInt(docStat.getItemValueString("StNormal")) + 1;
							docStat.replaceItemValue("StNormal",String.valueOf(lsmormaldays));
						}else{
							docStat.replaceItemValue("StPerfect","no");
						}
					}
					//Ӧ��������
					lszcworkdays = Integer.parseInt(docStat.getItemValueString("StZCworkdays")) + 1;
					docStat.replaceItemValue("StZCworkdays",String.valueOf(lszcworkdays));
					//ʵ�ʳ�������
					lssjworkdays = Double.parseDouble(docStat.getItemValueString("StZCworkdays"))-Double.parseDouble(docStat.getItemValueString("StAbsence"));
					docStat.replaceItemValue("StSJworkdays",String.valueOf(timecbslwxs.format(lssjworkdays)));
				}
			}
			docStat.save(true,true);
		}
	}
	/*
	 * �ж��Ƿ�ٵ������ˣ�������
	 * ������startTime�������ʱ�䣬endTime�����ʱ��
	 */
	private String judgeTime(String startTime,String endTime,String name)
	{
		String result = null;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		java.text.DecimalFormat timecbslwxs = new java.text.DecimalFormat("#0.00");
		String[] state = new String[6];
		long between = 0L;	//ʱ���
		long betweeno = 0L;	//ʱ���
		long betweent = 0L;	//ʱ���
		long betweens = 0L;	//ʱ���
		long betweenf = 0L;	//ʱ���
		long betweensi = 0L;	//ʱ���
		long betweensix = 0L;	//ʱ���
		long betweensev = 0L;	//ʱ���
		long betweeneig = 0L;	//ʱ���
		long betweenten = 0L;	//ʱ���
		double timecbs = 0.00;
		try{				
			//��Ϣʱ��
			//�����ϰ�ʱ��
			Date workTimeAM = sdf.parse(bInfo.getWorkTime()[1]);
			//�����°�ʱ��
			Date workTimePM = sdf.parse(bInfo.getWorkTime()[2]);
			//�������ʱ��
			Date workTimeEndAM = sdf.parse(bInfo.getWorkTime()[3]);
			//���翪ʼʱ��
			Date workTimeStartPM = sdf.parse(bInfo.getWorkTime()[4]);
			//�ٵ�ʱ��
			Date workTimeLateAM = sdf.parse(bInfo.getWorkTime()[5]);
			//����ʱ��
			Date workTimeLevEarlyPM = sdf.parse(bInfo.getWorkTime()[6]);
			//�ٵ���ʼʱ��
			Date workTimeStartLatePM = sdf.parse(bInfo.getWorkTime()[7]);
			//һ���ʱ���Ϊ9:00��10:00-12:00-13:00-17:00-18:00
			/*
			 * �������ʱ�����
			 * 1�����startTime(��ʼʱ��)=""�����������������ʱ��Ϊ3��Сʱ��
			 * 2�����startTime(��ʼʱ��)> workTimeEndAM(�������ʱ��),��Ϊ�������������ʱ��Ϊ3��Сʱ��
			 * 3�����workTimeLateAM(�ٵ�ʱ��)<startTime(��ʼʱ��)< workTimeEndAM(�������ʱ��) �� ��Ϊ����������������ʱ���м��������
			 * <1>�����endTime(����ʱ��)< workTimeEndAM(�������ʱ��)������ʱ�� = starttime - workTimeAM + workTimeEndAM - endTime ��
			 * <2>�����endtime(����ʱ��)> workTimeEndAM(�������ʱ��),����ʱ�� = starttime - workTimeAM
			 * */
			if(startTime.equals("")){
				state[0] = "AbsenceAM";
				state[1] = String.valueOf("3.00");
			}else{
				//��ʼʱ��
				Date earliest = sdf.parse(startTime);
				//����ʱ��
				if(endTime.equals("")){
					endTime = startTime;
				}
				Date lastest = sdf.parse(endTime);
				//��ʼʱ��-9:00
				between = earliest.getTime() - workTimeAM.getTime();
				//��ʼʱ��-9:05
				betweenten = earliest.getTime() - workTimeStartLatePM.getTime();
				//��ʼʱ��-12:00
				betweent = earliest.getTime() - workTimeEndAM.getTime();
				//����ʱ��-12:00
				betweeno = lastest.getTime() - workTimeEndAM.getTime();
				//��ʼʱ��-10��00
				betweens = earliest.getTime() - workTimeLateAM.getTime();
				//12��00-����ʱ��
				betweenf = workTimeEndAM.getTime() - lastest.getTime();
				//12:00-��ʼʱ��
				betweensi =  workTimeEndAM.getTime() - earliest.getTime();
				//����ʱ��-9:00
				betweensix = lastest.getTime() - workTimeAM.getTime();
				//��ʼʱ�� -����ʱ��
				betweensev = earliest.getTime() - lastest.getTime();
				//����ʱ��-10:00
				betweeneig = lastest.getTime() -  workTimeLateAM.getTime();
				//�ٵ�����ʼʱ��>9:00 and ��ʼʱ��<=10:00
				if(betweenten > 0 && betweens <= 0){
					state[0] = "Late";
					state[1] = String.valueOf("0.00");
				}
				//��������ʼʱ��>=12:00������3��Сʱ
				if (betweent>=0)
				{
					state[0] = "AbsenceAM";
					state[1] = String.valueOf("3.00");
				}
				//��������ʼʱ��>10:00
				if (betweens>0){
					state[0] = "AbsenceAM";
					//��ʼʱ��>=12��00;����3��Сʱ
					if (betweent>=0)
					{
						state[1] = String.valueOf("3.00");
					//��ʼʱ��<12:00
					}else{
						//����ʱ��>=12:00������ʱ��=��ʼʱ��-9:00
						if (betweeno>=0){
							timecbs = Double.parseDouble(String.valueOf(between))/Double.parseDouble(String.valueOf(3600000)); 
							state[1] = String.valueOf(timecbslwxs.format(timecbs));
						//����ʱ��<12:00;����ʱ�� = ��ʼʱ��-9:00+12:00-����ʱ��
						}else{
							timecbs = Double.parseDouble(String.valueOf(between))/Double.parseDouble(String.valueOf(3600000)); 
							timecbs = timecbs + Double.parseDouble(String.valueOf(betweenf)) / Double.parseDouble(String.valueOf(3600000));
							state[1] = String.valueOf(timecbslwxs.format(timecbs));
						}
					}
				}
				//����������ʱ��<12:00
				if (betweeno<0){
					//����ʱ��<=9:00;����ʱ�� = 3��Сʱ
					
					if (betweensix<=0)
					{
						state[0] = "AbsenceAM";
						state[1] = String.valueOf("3.00");
					//����ʱ��>9:00
					}else{
				
						//'��ʼʱ��>10:00;����ʱ��=12:00-����ʱ��+��ʼʱ��-9:00
						if (betweens>0){
							state[0] = "AbsenceAM";
							timecbs = Double.parseDouble(String.valueOf(between))/Double.parseDouble(String.valueOf(3600000)); 
							timecbs = timecbs + Double.parseDouble(String.valueOf(betweenf)) / Double.parseDouble(String.valueOf(3600000));
							state[1] = String.valueOf(timecbslwxs.format(timecbs));
						}//��ʼʱ��<=10:00;����ʱ��=12:00-����ʱ��
						else{
							//��ʼʱ��>9:05 ��һ�γٵ��Ϳ���
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
				//��������ʼʱ��=����ʱ�䣻����3��Сʱ
				if (betweensev==0)
				{
					state[0] = "AbsenceAM";
					state[1] = String.valueOf("3.00");
				}
		        if (betweenten<=0 && betweeno>=0){
					//����
					state[0] = "NormalAM";
					state[1] = String.valueOf("0.00");
				}
			}
			/*
			 * �������ʱ�����
			 * 1�����endTime(��ʼʱ��)=""�����������������ʱ��Ϊ5��Сʱ��
			 * 2���endTime(��ʼʱ��)< workTimeEndAM(�������ʱ��),��Ϊ�������������ʱ��Ϊ5��Сʱ��
			 * 3�����workTimeEndAM(�������ʱ��)<endTime(��ʼʱ��)< workTimeLevEarlyPM(����ʱ��) �� ��Ϊ�¿������������ʱ���м������			 
			 * <1>�����starttime(��ʼʱ��)<workTimeStartPM (��翪ʼʱ�������ʱ�� = workTimePM-endTime
			 * <2>�����starttime(��ʼʱ��)>workTimeStartPM (���翪ʼʱ��),����ʱ�� = endTime - workTimeStartPM + workTimePM- endtime
			 * */
			if(endTime.equals("")){
				state[2] = "AbsencePM";
				state[3] = String.valueOf("5.00");
			}else{
				//��ʼʱ��
				if(startTime.equals("")){
					startTime = endTime;
				}
				Date earliest = sdf.parse(startTime);
				//����ʱ��
				Date lastest = sdf.parse(endTime);
				//18:00-������
				between = workTimePM.getTime() - lastest.getTime();
				//����ʱ��-12:00
				betweens = lastest.getTime() - workTimeEndAM.getTime();
				//��ʼʱ��-13:00
				betweeno = earliest.getTime() - workTimeStartPM.getTime();
				//����ʱ��-17:00
				betweent = lastest.getTime() - workTimeLevEarlyPM.getTime();
				//13:00-��			betweensi =  workTimeStartPM.getTime() - lastest.getTime();
				//��ʼʱ��-18��00
				betweensix = earliest.getTime() - workTimePM.getTime();
				//����ʱ��-13:00
				betweensev = lastest.getTime() - workTimeStartPM.getTime();
				//��ʼʱ�� -����ʱ��
				betweeneig = earliest.getTime() - lastest.getTime();
				//�����ʱ������翪ʼʱ��(13:00)��ʱ��
				//����������ʱ��>�����ʱ�� and  �����ʱ��>����ʱ�� ��Ϊ����
				//18:00���ڽ���ʱ�� and ����ʱ��>=17:00
				if(between > 0 && betweent >= 0){
					state[2] = "LeaveEarly";
					state[3] = String.valueOf("0.00");
				}
				//����ʱ��<13:00;����5��Сʱ
				if(betweensev<0){
					state[2] = "AbsencePM";
					state[3] = String.valueOf("5.00");
				}
				//����ʱ��<17:00
				if (betweent<0 ){
					state[2] = "AbsencePM";
					//jssj<=13:00;����ʱ�� = 5
					if (betweensev<=0){
						state[3] = String.valueOf("5.00");
						//jssj>13:00
					}else{
						//kssj<=13:00������ʱ�� = 18:00-����ʱ�䣬�������ʱ�� = 18:00-����ʱ��+��ʼʱ��-13:00
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
				//��ʼʱ��>13:00
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
							//18:00С�ڽ���ʱ�䣻����ʱ�� = ��ʼʱ��-13:00
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
			//��ʽ�� ����ˢ��ʱ��;���ˢ��ʱ���翼����Ϣ;���翼����Ϣ���ٵ�/��������������;����/�������/����������
			//���ݣ� YYYY-MM-DD;YYYY-MM-DD;Late/AbsenceAM/NormalAM;LeaveEarly/AbsencePM/NormalPM
			result = startTime.trim() + ";" + endTime.trim() + ";" + state[0] + ";" + state[1]+ ";" + state[2]+ ";" + state[3];
		}catch(java.text.ParseException e){ 
			e.printStackTrace();
		}
		return result;
	}
	/*
	 * ����ˢ����¼�����㿼��ͳ����Ϣ����������ͳ����Ϣ��¼��ͳ���ĵ���
	 * ������docCardˢ����¼�ĵ���Ϣ,docStatͳ���ĵ�
	 */
	private Document recordAttendance(Document docCard,Document docStat){		
		try{
			if(dbAttendance != null && docCard != null){
				//-------------------------���ڼ���---------------------------
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				
				Calendar cal = Calendar.getInstance();
				String date = docCard.getItemValueString("StRecordDate");
			
				String[] LeaveInfoarr = null;
				Date dateInfo = sdf.parse(date);
				cal.setTime(dateInfo);
				//���ͳ���ĵ������ھʹ���һ��
				if(docStat == null){
					//ͳ���ĵ���������ʽyyyy-mm-���ڿ��ţ���������ȡ����ǰ8���ַ����ټ��Ͽ��ڿ���
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
					//��ݣ�yyyy
					docStat.replaceItemValue("StYear",date.substring(0,4));
					//�·ݣ�mm
					docStat.replaceItemValue("StMonth",date.substring(5,7));
					//�·ݵ�����
					docStat.replaceItemValue("StDays",String.valueOf(cal.getActualMaximum(Calendar.DAY_OF_MONTH)));
					
				}
				/*��������ݼ���Ϣ����ʼʱ��ͽ���ʱ��ȡ���ݼ���Ϣ��ʵ�ʿ�ʼʱ��ͽ���ʱ��*/
				
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
	 * ������Դ��ȡˢ����¼������ˢ����¼���浽domino�ĵ���
	 * ����������Դ
	 */	 
	public void receiveData(DataSource data){	
		try{
			if(data != null && data.isConsistency()){
				//�ؼ��֣������ڵ�����&Ա����ţ�
				String oldStatId = ((String)data.getRecordDate().get(0)).substring(0,8) + (String)data.getWorkerNumber().get(0);
				String newStatId = oldStatId;
				View vwStat = dbAttendance.getView("AttendanceListView");
				//����ͳ���ĵ�	
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
							//�ж���ְʱ�����ְʱ�� �����ְʱ��С�ڵ��ڽ��� ��ִ�� �����ְʱ����ڽ�����ִ�� ����ִ��
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
											//����ǲ�ͬ��ͳ���ĵ������ǰһ��ͳ���ĵ�����ͳ��
											takeStatistics(docStat);
											docStat  = vwStat.getDocumentByKey(newStatId.trim(),true);
											oldStatId = newStatId;
										}
										docStat = recordAttendance(recordToDoc(info),docStat);
								 }else{
									 if (noworkBegin.getTime()>crcofigdate.getTime()){
										 newStatId = info[2].substring(0,8) + info[5];					
											if(!newStatId.equals(oldStatId)){
												//����ǲ�ͬ��ͳ���ĵ������ǰһ������ĵ�����ͳ�
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
				//�����һ��ͳ���ĵ�����ͳ��
				takeStatistics(docStat);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * ��ˢ����¼���浽�ĵ���
	 * ������Info�򿨼�¼�������Ϊ���ڿ��ţ�Ա�������������ڣ������ʱ�䣬�����ʱ�
	 * ������isStat�Ƿ���Ҫͳ��
	 */
	public Document recordToDoc(String[] Info){
		Document docCard = null;
		try{		
			if(dbRecode != null && Info != null){
				View vwCard = dbRecode.getView("CardRecordByWorkNumAndRecordDateView");
				docCard  = vwCard.getDocumentByKey(Info[5] + Info[2],true);
				String[] mappingInfo = bInfo.getMappingInfo(Info[5].trim(),dbwork);
				//�Ѵ�ʱ���ʽ����Ϊ"Сʱ:����"
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
					//�����ǰ�ļ�¼��Դ���Զ����룬��ֱ���滻��ǰ�ļ�¼��
					if( docCard.getItemValueString("StSource").equals("AutoImportBeijing") ){
						docCard.replaceItemValue("StStartTime",eTime);
						docCard.replaceItemValue("StEndTime",lTime);
					}else{
						//"-"��������Ϊ��
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
					//����֯���л�ȡitcode���������ơ�����ID������
					if(mappingInfo != null){
						if(mappingInfo[0].trim().equals("ZHIMING"))
						{
							System.out.print(mappingInfo[0]);
							System.out.print(mappingInfo[4]);
						}
				
						if(mappingInfo[4].trim().equals("0001"))
						{
							
							//�ж���ְʱ�����ְʱ�� �����ְʱ��С�ڵ��ڽ��� ��ִ�� �����ְʱ����ڽ�����ִ�� ����ִ��
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
							
									//������Դ��Ϊ�Զ�����
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
										//������Դ��Ϊ�Զ�����
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


