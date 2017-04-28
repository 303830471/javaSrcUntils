package com.hl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import lotus.domino.*;

public class CreateStaticDatas {


	public  Vector InitStaticDataHeader(){
		
			Vector vec = new Vector();
			vec.add( "部门名称" );
			vec.add( "姓名" );
			vec.add( "员工编号" );
			vec.add( "员工类型" );
			vec.add( "工作地" );
			vec.add( "公司代码" );
			vec.add( "应出天数" );
			vec.add( "实出天数" );
			vec.add( "公假天数" );
			vec.add( "旷工天数" );
			vec.add( "迟到早退次数和" );
			vec.add( "病假" );
			vec.add( "事假" );
			vec.add( "旷工(申请中)" );
			vec.add( "事假合计" );
			vec.add( "年假" );
			vec.add( "调休" );
			vec.add( "公出" );
			vec.add( "产假" );
			vec.add( "婚假" );
			vec.add( "丧假" );
			return vec;
		
	}
	public Vector InitStaticData(DocumentCollection statDocCol,InitConfigInfo ConfigInfo){
	
		Vector vec = new Vector();
		try {
			Document statDoc  = statDocCol.getFirstDocument();
			
			vec.add( statDoc.getItemValueString("StDeptName") );
			vec.add( statDoc.getItemValueString("StPsnCN") );
			if (statDoc.getItemValueString("StNewWorkerNumber") != null){
				vec.add( statDoc.getItemValueString("StNewWorkerNumber") );
			}else{
				vec.add( statDoc.getItemValueString("StWorkerNumber") );
			}
			vec.add( statDoc.getItemValueString("StWorkerType") );
			vec.add( statDoc.getItemValueString("StWorkerPlace") );
			vec.add( statDoc.getItemValueString("StWorkerPlaceNum") );
			
			Double StZCworkdays = 0.00;
			Double StSJworkdays = 0.00;
			Double Stgjts = 0.00;   //公假天数
			Double Stkgts = 0.00;  //旷工天数
			Double Stkgts_sqz = 0.00; //旷工(申请中)
			int Stcdzt = 0;  //迟到早退次数
			Double Stsjhj = 0.00; //事假合计
			Double StA002 = 0.00;
			Double StA006 = 0.00;
			Double StA001 = 0.00;
			Double StA007 = 0.00;
			Double StA008 = 0.00;
			Double StA003 = 0.00;
			Double StA004 = 0.00;
			Double StA005 = 0.00;
			while(statDoc != null){	
				//工作天数
				Vector ycsctsdaysVec=GetWorkerDays(statDoc,ConfigInfo);
				if(ycsctsdaysVec.get(0).equals("0")){
					StZCworkdays = StZCworkdays + Double.parseDouble( statDoc.getItemValueString("StZCworkdays") );  //ycsctsdaysarr(0应出天数
				}else{
					StZCworkdays = StZCworkdays + Double.parseDouble( (String) ycsctsdaysVec.get(0) );  
				}
				StSJworkdays = StSJworkdays + Double.parseDouble( statDoc.getItemValueString("StSJworkdays")  ); 
				Stgjts = Stgjts + Double.parseDouble( (String) ycsctsdaysVec.get(1)  ); 
				Stkgts_sqz = Double.parseDouble( CountAppDoingAbsentDays(statDoc) ) ;
				Stkgts = Double.parseDouble( CountAbsentDays( statDoc, Stkgts_sqz ) );
				Stcdzt = Stcdzt + countcdztcsh(statDoc);
				StA002 = StA002 + Double.parseDouble( statDoc.getItemValueString("StA002") );
				StA006 = StA006 + Double.parseDouble( statDoc.getItemValueString("StA006") );
				Stsjhj = Stsjhj + StA006 + Stkgts_sqz ;
				StA001 = StA001 + Double.parseDouble( statDoc.getItemValueString("StA001") );
				StA007 = StA007 + Double.parseDouble( statDoc.getItemValueString("StA007") );
				StA008 = StA008 + Double.parseDouble( statDoc.getItemValueString("StA008") );
				StA003 = StA003 + Double.parseDouble( statDoc.getItemValueString("StA003") );
				StA004 = StA004 + Double.parseDouble( statDoc.getItemValueString("StA004") );
				StA005 = StA005 + Double.parseDouble( statDoc.getItemValueString("StA005") );
				
				statDoc = statDocCol.getNextDocument();
			}
			
			
			
			vec.add( StZCworkdays.toString() );    //应出天数
			vec.add( StSJworkdays.toString() );   //实出天数
			vec.add( Stgjts.toString() );   //ycsctsdaysarr(1)公假天数
			vec.add( Stkgts.toString() );  //旷工天数
			vec.add(  Integer.toString(Stcdzt) );  //迟到早退次数和
			vec.add( StA002.toString() ); //病假
			vec.add( StA006.toString()  ); //事假
			vec.add( Stkgts_sqz.toString() );  //旷工(申请中)
			vec.add( Stsjhj.toString() );   //事假合计
			vec.add( StA001.toString() );  //年假
			vec.add( StA007.toString() );   //调休
			vec.add( StA008.toString() );  //公出
			vec.add( StA003.toString() );  //产假
			vec.add( StA004.toString() );  //婚假
			vec.add( StA005.toString() );   //丧假
			
			return vec;
		} catch (NotesException e) {
			
			e.printStackTrace();
		}
		return vec;
		
	}
	public Vector GetWorkerDays(Document statDoc,InitConfigInfo ConfigInfo ){
		//获取工作天数--应出天数、
		//计算实出天数--实出天数=实出天数+节假日天数（满足入职时间在节假日之前才相加节假日天数）
		SimpleDateFormat sdffeui = new SimpleDateFormat("yyyy-MM-dd");  
		Date rzDate = null;
		Date hodate = null;
		String ysct = "0";
		Double sctsandh = 0.00;
		Vector WorkerDay = new Vector();
		try {
			rzDate = sdffeui.parse(statDoc.getItemValueString("StEntryDate"));
			String key = statDoc.getItemValueString("StMonth") + statDoc.getItemValueString("StMonth");
			View ptDfView = ConfigInfo.getPtDfDatabase().getView("EveryMonthWorkDaysViewByYearMonth");
			DocumentCollection ptDfDoccl = ptDfView.getAllDocumentsByKey(key);
			if(ptDfDoccl.getCount()>0){
				Document ptDfDoc = ptDfDoccl.getFirstDocument();
				while(ptDfDoc != null){	
					ysct = ptDfDoc.getItemValueString("StDays");
					if(!ptDfDoc.getItemValueString("StartDate").equals("")) {
						hodate = sdffeui.parse(ptDfDoc.getItemValueString("StartDate"));
						//节假日日期大于入职日期
						if(hodate.getTime()>rzDate.getTime()){
							if(!ptDfDoc.getItemValueString("StHolidayDays").equals("")) {
								sctsandh = sctsandh + Double.parseDouble(ptDfDoc.getItemValueString("StHolidayDays") );
							}
							
						}
					}
					ptDfDoc = ptDfDoccl.getNextDocument();
				}
			}
			WorkerDay.add(ysct);
			WorkerDay.add(sctsandh.toString());

		} catch (NotesException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return WorkerDay;
	}
	public String CountAppDoingAbsentDays(Document statDoc ){
		//获取审批中的旷工天数
		Double CountAppDoingAbsentDays = 0.00;
		try {
			for(int i = 1; i <= Integer.parseInt(statDoc.getItemValueString("StDays")); i++){
				
				Vector stlVec = statDoc.getItemValue("StL" + i);
				Enumeration enu = stlVec.elements();
				while(enu.hasMoreElements()){
					String stl = (String) enu.nextElement();
					String[] stlArr = stl.split("/");
					if(stlArr.length>0){
						if(stlArr[2].equals("审批中")){
							if(stlArr[9].equals("DX003")){
								CountAppDoingAbsentDays = CountAppDoingAbsentDays +Double.parseDouble(stlArr[5]);
							}
						}
					}
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return CountAppDoingAbsentDays.toString();
	}
	public String CountAbsentDays(Document statDoc,Double appdoingattends ){
		//获取旷工天数  '旷工天数  =  旷工天数-年假-公出-调休-病假-产假-婚假-丧家-事假-请休中旷工天数
		
		Double kgts = 0.00 ;
		Double njts = 0.00 ;
		Double gcts = 0.00 ;
		Double txts = 0.00 ;
		Double bjts = 0.00 ;
		Double cjts = 0.00 ;
		Double hjts = 0.00 ;
		Double sjts = 0.00 ;
		Double ssjts = 0.00 ;
		Double CountAbsentDays = 0.00;
		try {
			
			if(!statDoc.getItemValueString("StAbsence").equals("")) kgts = Double.parseDouble(statDoc.getItemValueString("StAbsence"));
			if(!statDoc.getItemValueString("StA001").equals("")) njts = Double.parseDouble(statDoc.getItemValueString("StA001"));
			if(!statDoc.getItemValueString("StA008").equals("")) gcts = Double.parseDouble(statDoc.getItemValueString("StA008"));
			if(!statDoc.getItemValueString("StA007").equals("")) txts = Double.parseDouble(statDoc.getItemValueString("StA007"));
			if(!statDoc.getItemValueString("StA002").equals("")) bjts = Double.parseDouble(statDoc.getItemValueString("StA002"));
			if(!statDoc.getItemValueString("StA003").equals("")) cjts = Double.parseDouble(statDoc.getItemValueString("StA003"));
			if(!statDoc.getItemValueString("StA004").equals("")) hjts = Double.parseDouble(statDoc.getItemValueString("StA004"));
			if(!statDoc.getItemValueString("StA005").equals("")) sjts = Double.parseDouble(statDoc.getItemValueString("StA005"));
			if(!statDoc.getItemValueString("StA006").equals("")) ssjts = Double.parseDouble(statDoc.getItemValueString("StA006"));
			
			CountAbsentDays = kgts - njts - gcts - txts - bjts - cjts - hjts -  sjts - ssjts - appdoingattends;
			
			if(CountAbsentDays < 0 ){
				CountAbsentDays = 0.00;
			}
			if(CountAbsentDays > 0 & CountAbsentDays < 0.1  ){
				CountAbsentDays = 0.00;
			}
		} catch (NotesException e) {
			
			e.printStackTrace();
		}
		
		return CountAbsentDays.toString();
	}	
	
	public int countcdztcsh( Document statDoc ){
		//	'计算迟到早退次数和
		//'算法 = 迟到早退次数和+忘打卡次数-抵消迟到-抵消早退
		int cdztcsh = 0;
		int wdkcs = 0;
		int dxcdcs = 0;
		int dxztcs = 0;
		int countcdztcsh = 0;
		try {
			if(!statDoc.getItemValueString("StLateAndLeaveEarly").equals("")) cdztcsh = Integer.parseInt(statDoc.getItemValueString("StLateAndLeaveEarly"));
			if(!statDoc.getItemValueString("StForgetCard").equals("")) wdkcs = Integer.parseInt(statDoc.getItemValueString("StForgetCard"));
			if(!statDoc.getItemValueString("StOffSetLate").equals("")) dxcdcs = Integer.parseInt(statDoc.getItemValueString("StOffSetLate"));
			if(!statDoc.getItemValueString("StOffSetLeaveEarly").equals("")) dxztcs = Integer.parseInt(statDoc.getItemValueString("StOffSetLeaveEarly"));
			
			countcdztcsh = cdztcsh + wdkcs - dxcdcs - dxztcs ;
			if( countcdztcsh < 0 ){
				countcdztcsh = 0;
			}
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (NotesException e) {
			e.printStackTrace();
		}
		
		return  countcdztcsh;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
