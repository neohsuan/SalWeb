package gov.vghks.sal.util;
import java.net.InetAddress;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

public class SendMail {
	/**
	 * 寄發電子郵件，通知失敗訊息
	 * @param reqno
	 * @param username
	 */
	static Logger log = Logger.getLogger(SendMail.class);
	
	public static void sendMail(String subject, String content){
		Properties props;
		MimeMessage msg;
		Session session;
		Multipart mp;
		MimeBodyPart mbp1;
		
		try {
			//設定 SMTP 伺服器位址
			props = System.getProperties();
			props.put("mail.smtp.host", "isca.vghks.gov.tw");

			//建立 Session   
			session = Session.getDefaultInstance(props, null);
			session.setDebug(false);

			//建立新的 MimeMessage物件
			msg = new MimeMessage(session);

			//設定送件者資料
			//msg.setFrom(new InternetAddress(myAddr, myName, "ISO8859-1"));
			msg.setFrom(new InternetAddress("salsys@vghks.gov.tw", "psmsys", "Big5"));
			
			//設定收件者資料     
			InternetAddress[] address;
			address = new InternetAddress[1];
			address[0] = new InternetAddress((String) "hjtsao@vghks.gov.tw");
			msg.setRecipients(Message.RecipientType.TO, address);

			//設定郵件標題        
			//msg.setSubject(subject, "ISO8859-1");
			msg.setSubject(subject, "Big5");

			//設定發送時間
			msg.setSentDate(new java.util.Date());

			//建立多內容郵件物件
			mp = new MimeMultipart();

			//建立郵件內文物件
			mbp1 = new MimeBodyPart();
			mbp1.setText(content, "Big5");		
			mp.addBodyPart(mbp1);
			
			//將MimeMultipart物件加入MimeMessage物件之中
			msg.setContent(mp);

			//儲存設定值
			msg.saveChanges();

			//發送電子郵件
			Transport.send(msg);
			log.info("Send mail to administrator success...");
		}
		catch (Exception e) {
			 log.error(e.toString()+"SendMail失敗!---SmtpDemo---");
		}
	}
	
	/**
	 * 寄發電子郵件，通知失敗訊息(單人)
	 * @param subject
	 * @param content
	 * @param adress
	 */
	public static void sendMail(String subject, String content, String receiver){
		try{
			InternetAddress[] address = new InternetAddress[1];
			address[0] = new InternetAddress((String) receiver);
			sendMail(subject, content, address);
		}catch(Throwable e){
			
		}
	}
	
	/**
	 * 寄發電子郵件，通知失敗訊息
	 * @param reqno
	 * @param username
	 */
	public static void sendMail(String subject, String content, InternetAddress[] address){
		Properties props;
		MimeMessage msg;
		Session session;
		Multipart mp;
		MimeBodyPart mbp1;
		
		try {
			//補上訊息的發送位置
			subject += "-" + InetAddress.getLocalHost().getHostName();
			content += "\n from " + InetAddress.getLocalHost().getHostName();
			
			//設定 SMTP 伺服器位址
			props = System.getProperties();
			props.put("mail.smtp.host", "isca.vghks.gov.tw");

			//建立 Session   
			session = Session.getDefaultInstance(props, null);
			session.setDebug(false);

			//建立新的 MimeMessage物件
			msg = new MimeMessage(session);

			//設定送件者資料
			//msg.setFrom(new InternetAddress(myAddr, myName, "ISO8859-1"));
			msg.setFrom(new InternetAddress("salsys@vghks.gov.tw", "psmsys", "Big5"));
			
			//設定收件者資料     
			msg.setRecipients(Message.RecipientType.TO, address);

			//設定郵件標題        
			//msg.setSubject(subject, "ISO8859-1");
			msg.setSubject(subject, "Big5");

			//設定發送時間
			msg.setSentDate(new java.util.Date());

			//建立多內容郵件物件
			mp = new MimeMultipart();

			//建立郵件內文物件
			mbp1 = new MimeBodyPart();
			mbp1.setText(content, "Big5");		
			mp.addBodyPart(mbp1);
			
			//將MimeMultipart物件加入MimeMessage物件之中
			msg.setContent(mp);

			//儲存設定值
			msg.saveChanges();

			//發送電子郵件
			Transport.send(msg);
		}
		catch (Exception e) {
			//nothing
		}
	}		
}
