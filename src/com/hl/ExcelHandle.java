package com.hl;

import java.io.*;
import jxl.*;
import jxl.write.*;
import jxl.format.*;
import java.util.*;
import java.awt.Color;

public class ExcelHandle {
	public static void writeExcel(File f, int i,jxl.write.WritableSheet ws) throws Exception {
		
		jxl.write.Label labelC ;
		
			for(int j=0;j<15;j++){
				labelC = new jxl.write.Label(j, i, "0000" + j) ;
				ws.addCell(labelC);
			}
	}
	public static void CreateExcel() throws Exception {
		
		File f = new File("c:\\reportExcelData.xls");
		f.createNewFile();
		jxl.write.WritableWorkbook wwb = Workbook.createWorkbook(f);
		jxl.write.WritableSheet ws = wwb.createSheet("���ڱ���", 0);
		for(int i=0;i<2000;i++){
			writeExcel(f,i,ws);
			// д��Exel������
			wwb.write();
		}

		// �ر�Excel����������
		wwb.close();
	}

	//f:��Ҫд����ļ���staticDataVecͳ�����ݣ�hrCount��������
	public static void writeDataToExcel(jxl.write.WritableSheet ws, Vector staticDataVec, int hrCount) throws Exception {

		jxl.write.Label labelC ;
		//д������
		for(int j=0;j<staticDataVec.size();j++){
			labelC = new jxl.write.Label(j, hrCount, staticDataVec.get(j).toString()) ;
			ws.addCell(labelC);
		}

	}
	//д��ͷ
	public static void writeExcelHeader(jxl.write.WritableSheet ws) throws Exception {

		jxl.write.Label labelC ;
		//д��ͷ
		CreateStaticDatas csd = new CreateStaticDatas();
		for(int j=0;j<csd.InitStaticDataHeader().size();j++){
			labelC = new jxl.write.Label(j, 0, csd.InitStaticDataHeader().get(j).toString() ) ;
			ws.addCell(labelC);
		}

	}
	public static void readExcel(File os) throws Exception {
		Workbook wb = Workbook.getWorkbook(os);
		Sheet s = wb.getSheet("Sheet1");
		Cell c = s.getCell(0, 0);
		System.out.println(c.getContents());
	}
	public static void closeExcel(jxl.write.WritableWorkbook wwb) throws Exception {
		// д��Exel������
		wwb.write();
		// �ر�Excel����������
		wwb.close();
	}

	public static void main(String[] args) throws Exception {

		CreateExcel();
//		readExcel(f);
	}
}
