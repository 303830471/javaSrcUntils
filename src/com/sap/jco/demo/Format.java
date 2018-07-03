package com.sap.jco.demo;


import java.lang.*;

public class Format extends Object {
	public String repDate(String date){
		//输入格式：2008-01-31输出20080130，输入20080130输出20080130
		
		if(!date.equals("")){
			String[] tA=date.split("-");
			if(tA.length>1)
				date=tA[0]+tA[1]+tA[2];
		}
		return date;
	}
	public String transUnicode(String str){
		//把Unicode字符串转换成decode格式，输入%u95EB%u4E3D输出老王
		//-unicode编码用16进制汉字，编码时在汉字的编码前面加"%u"，汉字占两个字节
		//-转码时，把字符串中的汉字的编码提取出来，先转换为10进制码，再转换为字符，最后转换为字符串
			
		String[] tA=str.split("%u");
		String tStr=tA[0];
		for(int i=1;i<tA.length;i++){
			tStr=tStr + String.valueOf((char)Integer.parseInt(tA[i].substring(0,4),16))+ tA[i].substring(4,tA[i].length());
		}

        	return tStr;
	}

}