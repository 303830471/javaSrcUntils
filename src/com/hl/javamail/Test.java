package com.hl.javamail;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args){
        //�����ʼ���Ϣ
	  MailSenderInfo mailInfo = new MailSenderInfo(); 
	  /*
	  mailInfo.setMailServerHost("smtp.163.com"); 
	  mailInfo.setMailServerPort("25"); 
	  mailInfo.setValidate(true); 
	  mailInfo.setUserName("hulei229268@163.com"); //�������
	  mailInfo.setPassword("");//����������� 
	  mailInfo.setFromAddress("hulei229268@163.com"); // �������
	  mailInfo.setToAddress("303830471@qq.com");  
	  mailInfo.setSubject("�������ʼ�����"); 
	  mailInfo.setContent("<a href='http://www.baidu.com'>�������ʼ�����</a>");  //html��ʽ
//	  mailInfo.setContent("�������ʼ�����");   //�ı���ʽ
 * */
 
	  mailInfo.setMailServerHost("smtp.sugon.com"); 
	  mailInfo.setMailServerPort("25"); 
	  mailInfo.setValidate(true); 
	  mailInfo.setUserName("hulei@sugon.com"); //�������
	  mailInfo.setPassword("");//����������� 
	  mailInfo.setFromAddress("hulei@sugon.com"); // �������
	  mailInfo.setToAddress("hulei@sugon.com");  
	  mailInfo.setSubject("�������ʼ�����"); 
	  mailInfo.setContent("<a href='http://www.baidu.com'>�������ʼ�����</a>");  //html��ʽ
	  
        //�������Ҫ�������ʼ�
	  SimpleMailSender sms = new SimpleMailSender();
//    sms.sendTextMail(mailInfo);//���������ʽ 
      sms.sendHtmlMail(mailInfo);//����html��ʽ
	}


}
