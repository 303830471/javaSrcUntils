package com.sap.jco.demo;


import java.lang.*;

public class Format extends Object {
	public String repDate(String date){
		//�����ʽ��2008-01-31���20080130������20080130���20080130
		
		if(!date.equals("")){
			String[] tA=date.split("-");
			if(tA.length>1)
				date=tA[0]+tA[1]+tA[2];
		}
		return date;
	}
	public String transUnicode(String str){
		//��Unicode�ַ���ת����decode��ʽ������%u95EB%u4E3D�������
		//-unicode������16���ƺ��֣�����ʱ�ں��ֵı���ǰ���"%u"������ռ�����ֽ�
		//-ת��ʱ�����ַ����еĺ��ֵı�����ȡ��������ת��Ϊ10�����룬��ת��Ϊ�ַ������ת��Ϊ�ַ���
			
		String[] tA=str.split("%u");
		String tStr=tA[0];
		for(int i=1;i<tA.length;i++){
			tStr=tStr + String.valueOf((char)Integer.parseInt(tA[i].substring(0,4),16))+ tA[i].substring(4,tA[i].length());
		}

        	return tStr;
	}

}