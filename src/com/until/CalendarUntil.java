package com.until;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarUntil {
	static Calendar calendar = Calendar.getInstance();
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
	/** 
     * 得到上月最后一天的日期 
     * @Methods Name getLastDayOfupMonth 
     * @return Date 
     */  
    public static String getyyyyMMdd(int year,int month, int day){  
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
    	calendar.set(year, month-1, day);
		String CurDateStr = sdf.format( calendar.getTime() );
		
        return CurDateStr;     
    }  
	/** 
     * 得到上月最后一天的日期 
     * @Methods Name getLastDayOfupMonth 
     * @return Date 
     */  
    public static String getLastDayOfupMonth()   {    
    	
    	calendar.set(Calendar.DAY_OF_MONTH, 1); 
    	calendar.add(Calendar.DATE, -1);
		String CurDateStr = sdf.format(calendar.getTime());
//		System.out.println("CurDateStr= " + CurDateStr);  
        return CurDateStr;     
    }  
	/** 
     * 得到当前时间
     * @Methods Name getLastDayOfupMonth 
     * @return Date 
     */  
    public static String getNowTime()   {    
    	String time = "";
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddhhmmss");

    	try {
    	   time= sdf.format(new Date());

    	} catch (Exception e) {
    	   e.printStackTrace();
    	}
        return time;     
    } 
	/** 
     * 得到本月第一天的日期 
     * @Methods Name getLastDayOfMonth 
     * @return Date 
     */  
    public static String getFirstDayOfMonth(Date date)   {     
        Calendar cDay = Calendar.getInstance();     
        cDay.setTime(date);  
        cDay.set(Calendar.DAY_OF_MONTH, cDay.getActualMinimum(Calendar.DAY_OF_MONTH)); 
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		String CurDateStr = sdf.format(cDay.getTime());
//		System.out.println("CurDateStr= " + CurDateStr);  
        return CurDateStr;     
    }  
	/** 
     * 得到本月最后一天的日期 
     * @Methods Name getLastDayOfMonth 
     * @return Date 
     */  
    public static String getLastDayOfMonth(Date date)   {     
        Calendar cDay = Calendar.getInstance();     
        cDay.setTime(date);  
        cDay.set(Calendar.DAY_OF_MONTH, cDay.getActualMaximum(Calendar.DAY_OF_MONTH)); 
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		String CurDateStr = sdf.format(cDay.getTime());
//		System.out.println("CurDateStr= " + CurDateStr);  
        return CurDateStr;     
    }  
	/** 
     * 比较两个日期间隔多少天
     * @Methods Name getLastDayOfMonth 
     * @return Date 
     */  
    public static String  BetweenTwoDates(String date1,String date2)   {  
    	DecimalFormat df  = new DecimalFormat("###.00");//保持2位  
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Calendar calendar = Calendar.getInstance();
    	double betweenDate = 0.00;
    	long nowDate = calendar.getTime().getTime(); //Date.getTime() 获得毫秒型日期
    	try {
    		if(date1.length()==10)date1 = date1 + " 00:00:00";
    		if(date2.length()==10)date2 = date2 + " 00:00:00";
    		if(date1.length()==16)date1 = date1 + ":00";
    		if(date2.length()==16)date2 = date2 + ":00";
	        long ldate2 = sdf.parse(date2).getTime();
	        long ldate1 = sdf.parse(date1).getTime();
//	        betweenDate = (double)(ldate2 - ldate1) / (1000 * 60 * 60 * 24); //计算间隔多少天，则除以毫秒到天的转换公式
	        betweenDate = (double)(ldate2 - ldate1); 
//	        System.out.print(df.format(betweenDate));
    	} catch (ParseException e) {
    	         e.printStackTrace();
    	}
    	return  df.format(betweenDate);
    } 
	/** 
     * 比较两个时间间隔多少个小时
     * @Methods Name BetweenTwoTime 
     * @return double类型 保留两位小数点 
	 * @throws ParseException 
     */  
    public double BetweenTwoTimeOfHour(String startTime,String endTime) throws ParseException   {  
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		java.text.DecimalFormat timecbslwxs = new java.text.DecimalFormat("#0.00");
		timecbslwxs.setRoundingMode(RoundingMode.HALF_UP);
    	long between = 0L;	//时间差
    	Date st = sdf.parse(startTime);
    	Date et = sdf.parse(endTime);
    	between = et.getTime()-st.getTime();
//    	System.out.println("between---== " + between);
//    	System.out.println( Double.parseDouble( String.valueOf(between) )/ Double.parseDouble( String.valueOf(3600000)) );
    	double db = Double.parseDouble( timecbslwxs.format( Double.parseDouble( String.valueOf(between) )/ Double.parseDouble( String.valueOf(3600000)) ) );
    	return  db;
    }
	/** 
     * 比较两个时间间隔多少
     * @Methods Name BetweenTwoTime 
     * @return long类型 
	 * @throws ParseException 
     */  
    public long BetweenTwoTime(String startTime,String endTime) throws ParseException   {  
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		java.text.DecimalFormat timecbslwxs = new java.text.DecimalFormat("#0.00");
		timecbslwxs.setRoundingMode(RoundingMode.HALF_UP);
    	long between = 0L;	//时间差
    	Date st = sdf.parse(startTime);
    	Date et = sdf.parse(endTime);
    	between = et.getTime()-st.getTime();
//    	System.out.println("BetweenTwoTime== " +between);
//    	double db = Double.parseDouble( timecbslwxs.format(between/ (1000 * 60 * 60)) );
    	return  between;
    }
	/** 
     * 比较两个时间的大小，
     * @Methods Name CompareTwoTime 
     * @return endTime>startTime 返回true；否则返回false 
	 * @throws ParseException 
     */  
    public boolean CompareTwoTime(String startTime,String endTime) throws ParseException {  
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    	long between = 0L;	//时间差
    	Date st = sdf.parse(startTime);
    	Date et = sdf.parse(endTime);
    	between = et.getTime()-st.getTime();
//    	System.out.println("CompareTwoTime== " +between);
    	if(between>0){
    		return true;
    	}else{
    		return false;
    	}
    }
	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException {
		// TODO 自动生成的方法存根
		CalendarUntil calUtil = new CalendarUntil();

//		long bt = BetweenTwoTime("10:05","10:06");
//		System.out.println("bt= " + bt);
//		
//		System.out.println("CompareTwoTime= " + CompareTwoTime("07:00","07:00"));
		
		System.out.println("BetweenTwoTimeOfHour= " + calUtil.BetweenTwoTimeOfHour("10:05","10:17"));
		
		
	}
}
