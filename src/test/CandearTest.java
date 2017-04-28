package test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
public class CandearTest {
	static String startDate = "";
	//22222
	public static void main(String[] args) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  		
		Calendar cal = Calendar.getInstance(); 
//		cal.add(Calendar.DATE,-1);    
		startDate = sdf.format(cal.getTime());
		
		startDate = "2016-02-01";
		if(!"".equals(startDate)){
			
		cal.set(Integer.parseInt(startDate.split("-")[0]), Integer.parseInt(startDate.split("-")[1])-1, Integer.parseInt(startDate.split("-")[2]) );

		System.out.println(cal.getTime());
		
		System.out.println(startDate);
		System.out.println(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
	}
	}
}
