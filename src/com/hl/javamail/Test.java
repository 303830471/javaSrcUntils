package com.hl.javamail;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args){
        //设置邮件信息
	  MailSenderInfo mailInfo = new MailSenderInfo(); 
	  /*
	  mailInfo.setMailServerHost("smtp.163.com"); 
	  mailInfo.setMailServerPort("25"); 
	  mailInfo.setValidate(true); 
	  mailInfo.setUserName("hulei229268@163.com"); //你的邮箱
	  mailInfo.setPassword("");//你的邮箱密码 
	  mailInfo.setFromAddress("hulei229268@163.com"); // 你的邮箱
	  mailInfo.setToAddress("303830471@qq.com");  
	  mailInfo.setSubject("这里是邮件标题"); 
	  mailInfo.setContent("<a href='http://www.baidu.com'>这里是邮件内容</a>");  //html格式
//	  mailInfo.setContent("这里是邮件内容");   //文本格式
 * */
 
	  mailInfo.setMailServerHost("smtp.sugon.com"); 
	  mailInfo.setMailServerPort("25"); 
	  mailInfo.setValidate(true); 
	  mailInfo.setUserName("hulei@sugon.com"); //你的邮箱
	  mailInfo.setPassword("");//你的邮箱密码 
	  mailInfo.setFromAddress("hulei@sugon.com"); // 你的邮箱
	  mailInfo.setToAddress("hulei@sugon.com");  
	  mailInfo.setSubject("这里是邮件标题"); 
	  mailInfo.setContent("<a href='http://www.baidu.com'>这里是邮件内容</a>");  //html格式
	  
        //这个类主要来发送邮件
	  SimpleMailSender sms = new SimpleMailSender();
//    sms.sendTextMail(mailInfo);//发送文体格式 
      sms.sendHtmlMail(mailInfo);//发送html格式
	}


}
