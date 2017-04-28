package com.hl;

import java.io.*;
import jxl.*;
import jxl.write.*;
import jxl.format.*;
import java.util.*;
import java.awt.Color;

public class TestExcel {
	public static void writeExcel(File f) throws Exception {
		jxl.write.WritableWorkbook wwb = Workbook.createWorkbook(f);
		jxl.write.WritableSheet ws = wwb.createSheet("考勤报表", 0);
		jxl.write.Label labelC ;
		for(int i=0;i<2000;i++){
//			labelC = new jxl.write.Label(0, i, "0000"+i);
//			ws.addCell(labelC);
			for(int j=0;j<15;j++){
				labelC = new jxl.write.Label(j, i, i + "0000" + j) ;
				ws.addCell(labelC);
			}
		}	
//		jxl.write.WritableFont wfc = new jxl.write.WritableFont(
//				WritableFont.ARIAL, 20, WritableFont.BOLD, false,
//				UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.GREEN);
//		jxl.write.WritableCellFormat wcfFC = new jxl.write.WritableCellFormat(
//				wfc);
//		wcfFC.setBackground(jxl.format.Colour.RED);
//		labelC = new jxl.write.Label(6, 0, "111", wcfFC);
//		ws.addCell(labelC);
		// 写入Exel工作表
		wwb.write();
		// 关闭Excel工作薄对象
		wwb.close();
	}

	public static void readExcel(File os) throws Exception {
		Workbook wb = Workbook.getWorkbook(os);
		Sheet s = wb.getSheet("Sheet1");
		Cell c = s.getCell(0, 0);
		System.out.println(c.getContents());
	}

	// 最好写一个这样的main方法来测试一下你的这个class是否写好了。
	public static void main(String[] args) throws Exception {
		File f = new File("c:\\reportExcelData.xls");
		f.createNewFile();
		writeExcel(f);
//		readExcel(f);
	}
}
