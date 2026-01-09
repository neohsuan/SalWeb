package gov.vghks.sal.pdf;

import gov.vghks.common.util.StringUtil;
import gov.vghks.sal.Hospitals;
import gov.vghks.sal.dao.SalNTDAO;
import gov.vghks.sal.model.Paprtdtl;
import gov.vghks.sal.util.FileUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Date;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import org.apache.log4j.Logger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.util.NumberUtils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.HyphenationAuto;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
//扣款表_高榮
public class Deduct_1A0 {
	Logger log = Logger.getLogger(SalNTDAO.class);  //呼叫log4j
	String ttfDir = "font\\VGHKH_KAI.TTF";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat signDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	NumberFormat fm = NumberFormat.getNumberInstance();  //千分位
	public Deduct_1A0() {
		
	}

	public Deduct_1A0(String strDir,String pdfFileName,boolean blnSum,ArrayList<Paprtdtl> paprtdtlAry ,String hid) throws Exception {
		String me = getClass().getName() + ".Deduct_1A0()";
		Document document = null;
		PdfWriter writer = null;
		Paprtdtl prtobj=null;
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		
		FileUtil fileUtil = new FileUtil();
		String tmp="";
		int intPer=0;//人數合計
		int intCellSum_1A=0,intCellSum_1B=0,intCellSum_2A=0,intCellSum_2B=0;
		int intCellSum_3A=0,intCellSum_3B=0,intCellSum_4A=0,intCellSum_4B=0;
		int intCellSum_5A=0,intCellSum_5B=0,intCellSum_6A=0,intCellSum_6B=0;
		int intCellSum_7A=0,intCellSum_7B=0,intCellSum_8A=0,intCellSum_8B=0;
		int intCellSum_9A=0,intCellSum_9B=0,intCellSum_10A=0,intCellSum_10B=0;
		int intCellSum_11A=0,intCellSum_12A=0,intCellSum_13A=0;
		try {
			//新增資料夾路徑
			if (!FileUtil.FileToSmbFile(strDir,hid).exists()){
				fileUtil.FileToSmbFile(strDir,hid).mkdirs();
			}	
			//建立PDF檔案			
			SmbFile payfile = fileUtil.FileToSmbFile(pdfFileName,hid);
			
			// 步驟1 - 設定產生的PDF文件大小
			document = new Document(PageSize.A4.rotate(), 30,25,25,20);//left, right, top, bottom
			
			String titleName[]={"職號\n卡號","階級","職稱","姓名","公保費\n公共設施使用費","健保費\n代扣停車費","勞保費\n眷舍管理費","退撫離職金\n餐費","所得稅\n製卡費","公教儲金\n惠民捐款","醫療互助金\n醫療糾紛","國宅貸款\n體適能中心費用","自繳勞退\n　","補充保險費\n　","房屋津貼\n　","清償債款\n　","扣款合計"};
			int pdfCol = 17; // 內文有幾欄
			float[] floatCol = {0.045f, 0.06f, 0.08f, 0.06f, 0.058f, 0.058f, 0.058f, 0.058f, 0.058f, 0.058f, 0.058f, 0.058f, 0.058f, 0.058f, 0.058f, 0.058f, 0.06f}; //欄位比例
			
			writer = PdfWriter.getInstance(document, new SmbFileOutputStream(payfile));
			document.open();			
			PdfContentByte cb = writer.getDirectContent();
			
			//中文設定，載入字型
			// 載入難字字型			
    		BaseFont bfChinese = BaseFont.createFont(ttfDir, BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);
    		// 載入難字字型
    		FontFactory.register(ttfDir);
			Font fontTitle = new Font( bfChinese, 18, Font.BOLD ); //標題字型設定，size為18
			Font fontTitle14 = new Font( bfChinese, 14, Font.NORMAL ); //標題字型設定，size為16
			Font fontTitle10 = new Font( bfChinese, 10, Font.NORMAL ); //標題字型設定，size為10
			Font fontColTitlA = new Font( bfChinese, 8, Font.NORMAL ); //標題字型設定，size為8
			Font fontColTitlB = new Font( bfChinese, 7, Font.NORMAL ); //標題字型設定，size為7
			Font fontColTitlC = new Font( bfChinese, 5, Font.NORMAL ); //標題字型設定，size為6
			Font fontColCent = new Font( bfChinese, 8, Font.NORMAL ); //字型設定，size為8
			Font fontColCentB = new Font( bfChinese, 8, Font.BOLD ); //字型設定，size為8
			
			//加入標題
			//建立內容表格 並設定其欄位數
			PdfPTable contentTable = new PdfPTable(pdfCol);
			
			//設定table的寬度
			contentTable.setWidthPercentage(100f);
			contentTable.setSpacingBefore(0f);//前間距
			contentTable.setSpacingAfter(0f);//後間距
			//設定預設的cell高度為20f
			contentTable.getDefaultCell().setFixedHeight(20f);
			contentTable.setWidths(floatCol);
			
			//Cell設定			
			PdfPCell mTitleCell ;
			//第1行
			mTitleCell= new PdfPCell();
			String strMT_hosptial=hospInfo.nmFull;
			mTitleCell.setPhrase( new Phrase(strMT_hosptial , fontTitle));
			mTitleCell.setBorder(0);
			mTitleCell.setColspan(10);
			mTitleCell.setFixedHeight(25f);
			mTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT); 
			contentTable.addCell(mTitleCell);
			
			mTitleCell= new PdfPCell();
			String strprttm=signDateFormat.format(new Date()) ;
			mTitleCell.setPhrase( new Phrase("列印日期:"+strprttm , fontTitle10) );			
			mTitleCell.setColspan(7);
			mTitleCell.setBorder(Rectangle.NO_BORDER);
			mTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT); 
			contentTable.addCell(mTitleCell);
			
			//第2行
			mTitleCell= new PdfPCell(new Phrase("扣款表" , fontTitle14));
			mTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置中
			mTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
			mTitleCell.setBorder(Rectangle.NO_BORDER);
			mTitleCell.setColspan(9);
			mTitleCell.setFixedHeight(20f);
			contentTable.addCell(mTitleCell);
			
			prtobj=paprtdtlAry.get(0);
			String dateString = prtobj.getPaybank().toString();
			tmp="發薪年月:"+dateString.substring(0,4)+"年"+dateString.substring(5, 7)+"月";
			mTitleCell= new PdfPCell(new Phrase( tmp , fontTitle14));
			mTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER); //垂直置中
			mTitleCell.setBorder( Rectangle.NO_BORDER);
			mTitleCell.setColspan(7);
			contentTable.addCell(mTitleCell);
 			
 			mTitleCell.setPhrase( new Phrase("單位:元" , fontTitle10) );			
			mTitleCell.setHorizontalAlignment( Element.ALIGN_RIGHT);
			contentTable.addCell(mTitleCell);
			//設定table的寬度
			//設定預設的cell高度為20f
			contentTable.getDefaultCell().setFixedHeight(20f);
			
			//標題
			PdfPCell sTitleCell ;			
			for (int i=0;i<titleName.length;i++){
				
				sTitleCell= new PdfPCell();
				sTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER); //垂直置中
				sTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
				//長度>5 有字體=6
				if (String.valueOf(i).matches("4|13"))
					sTitleCell.setPhrase(new Phrase( titleName[i], fontColTitlC));
				//長度>=5 有字體=7
				else if (String.valueOf(i).matches("5|6|7|8|10"))
					sTitleCell.setPhrase(new Phrase( titleName[i], fontColTitlB));
				else
					sTitleCell.setPhrase(new Phrase( titleName[i], fontColTitlA));			
				contentTable.addCell(sTitleCell);
			}
			
			PdfPCell cellContent ;
			cellContent= new PdfPCell();
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置右
			cellContent.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
			cellContent.setFixedHeight(23);
			for (Paprtdtl data : paprtdtlAry){
				//人數
				intPer++;
				//公保費PAYGEINS,公共設施使用費PAYGYM
				intCellSum_1A += data.getPaygeins();
				intCellSum_1B += data.getPaygym();
				//健保費PAYDPINS,代扣停車費PAYPARK1
				intCellSum_2A += data.getPaydpins();
				intCellSum_2B += data.getPaypark1();
				//勞保費PAYLBINS,眷舍管理費PAYDORM1+PAYDORM2+PAYDORM3+payother2PGY眷舍管理費 
				intCellSum_3A += data.getPaylbins();
				intCellSum_3B += data.getPaydorm1()+data.getPaydorm2()+data.getPaydorm3()+data.getPayother2();
				//退撫離職金PAYRETIR,餐費PAYMEAL
				intCellSum_4A += data.getPayretir();
				intCellSum_4B += data.getPaymeal();
				//所得稅PAYTAX,製卡費PAYCARDF
				intCellSum_5A += data.getPaytax();
				intCellSum_5B += data.getPaycardf();
				//公教儲金PAYGEDP,惠民捐款PAYAGEN1
				intCellSum_6A += data.getPaygedp();
				intCellSum_6B += data.getPayagen1();
				//醫療互助金PAYNSPFD,醫療糾紛PAYOTHER3
				intCellSum_7A += data.getPaynspfd();
				intCellSum_7B += data.getPayother3();
				//國宅貸款PAYHLOA1+PAYHLOA2+PAYHLOA3+PAYHLOA4+PAYHLOA5+PAYHLOA6,體適能中心費用PAYVETD
				intCellSum_8A += data.getPayhloa1()+data.getPayhloa2()+data.getPayhloa3()+data.getPayhloa4()+data.getPayhloa5()+data.getPayhloa6();
				intCellSum_8B += data.getPayvetd1();
				//自繳勞退PAYDLOAN
				intCellSum_9A += data.getPaydloan();
				//補充保險費PAYOTHER1
				intCellSum_10A += data.getPayother1();
				//房屋津貼PAYMHOUS
				intCellSum_11A += data.getPaymhous();
				//清償債款PAYLIEN
				intCellSum_12A += data.getPaylien();
				//扣款合計PAYTLDED
				intCellSum_13A += data.getPaytlded();	
				
				//總計,不用列各列明細
				if (!blnSum){
					//職號,卡號
					cellContent.setPhrase(new Phrase(data.getPayempno()+"\n"+data.getPaycard(), fontColCent));			
					contentTable.addCell(cellContent);
					//階級
					tmp=StringUtil.nullToString(data.getRankabbr())+StringUtil.nullToString(data.getPayrnk2n());
					if (tmp.equals("00")) tmp="";
					if (StringUtil.nullToString(data.getPayrank3()).equals("1"))
						tmp+="本";
					else if (StringUtil.nullToString(data.getPayrank3()).equals("2"))
						tmp+="功";
					else
						tmp+="　";
					if (data.getPaydegr2()==0) tmp+=" ";
					else tmp=tmp+data.getPaydegr2();
					tmp+=" ";
					if (data.getPaydegre()==0) tmp+=" ";
					else tmp=tmp+data.getPaydegre();
	
					cellContent.setPhrase(new Phrase(tmp, fontColCent));
					cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
					contentTable.addCell(cellContent);
					//職稱
					cellContent.setPhrase(new Phrase(StringUtil.nullToString(data.getPaytitlp()), fontColCent));
					contentTable.addCell(cellContent);
					//姓名
					cellContent.setPhrase(new Phrase(data.getPaynamec(), fontColCentB));			
					contentTable.addCell(cellContent);
					
					//公保費PAYGEINS,公共設施使用費PAYGYM
					cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置右
					tmp = String.valueOf( data.getPaygeins() )+"\n"+String.valueOf(data.getPaygym());
					cellContent.setPhrase(new Phrase( tmp , fontColCent));			
					contentTable.addCell(cellContent);
					//健保費PAYDPINS,代扣停車費PAYPARK1
					tmp=String.valueOf( data.getPaydpins() )+"\n"+String.valueOf(data.getPaypark1() );
					cellContent.setPhrase(new Phrase( tmp , fontColCent));			
					contentTable.addCell(cellContent);
					//勞保費PAYLBINS,眷舍管理費PAYDORM1+PAYDORM2+PAYDORM3+payother2PGY眷舍管理費 2025.8.20 
					tmp = String.valueOf( data.getPaylbins() )+"\n"+String.valueOf( data.getPaydorm1()+data.getPaydorm2()+data.getPaydorm3()+data.getPayother2() );
					cellContent.setPhrase(new Phrase(tmp, fontColCent));
					contentTable.addCell(cellContent);
					//退撫離職金PAYRETIR,餐費PAYMEAL
					tmp = String.valueOf( data.getPayretir() ) +"\n" + String.valueOf( data.getPaymeal() );
					cellContent.setPhrase(new Phrase(tmp, fontColCent));			
					contentTable.addCell(cellContent);
					//所得稅PAYTAX,製卡費PAYCARDF
					tmp=String.valueOf( data.getPaytax() )+"\n"+String.valueOf( data.getPaycardf() );
					cellContent.setPhrase(new Phrase(tmp, fontColCent));		
					contentTable.addCell(cellContent);
					//公教儲金PAYGEDP,惠民捐款PAYAGEN1
					tmp=String.valueOf( data.getPaygedp() )+"\n"+String.valueOf( data.getPayagen1() );
					cellContent.setPhrase(new Phrase(tmp, fontColCent));
					contentTable.addCell(cellContent);
					//醫療互助金PAYNSPFD,醫療糾紛PAYOTHER3
					tmp=String.valueOf( data.getPaynspfd() )+"\n"+ String.valueOf( data.getPayother3()  );
					cellContent.setPhrase(new Phrase(tmp, fontColCent));			
					contentTable.addCell(cellContent);
					//國宅貸款PAYHLOA1+PAYHLOA2+PAYHLOA3+PAYHLOA4+PAYHLOA5+PAYHLOA6,體適能中心費用PAYVETD
					tmp=String.valueOf( data.getPayhloa1()+data.getPayhloa2()+data.getPayhloa3()+data.getPayhloa4()+data.getPayhloa5()+data.getPayhloa6() )+"\n"+ String.valueOf( data.getPayvetd1() );
					cellContent.setPhrase(new Phrase(tmp, fontColCent));		
					contentTable.addCell(cellContent);
					//自繳勞退PAYDLOAN
					tmp=String.valueOf( data.getPaydloan())+"\n　" ;
					cellContent.setPhrase(new Phrase(tmp, fontColCent));		
					contentTable.addCell(cellContent);
					//補充保險費PAYOTHER1
					tmp=String.valueOf( data.getPayadrtn() ) +"\n　";
					cellContent.setPhrase(new Phrase(tmp, fontColCent));
					contentTable.addCell(cellContent);
					//房屋津貼PAYMHOUS
					tmp=String.valueOf( data.getPaymhous() ) +"\n　"  ;
					cellContent.setPhrase(new Phrase(tmp, fontColCent));			
					contentTable.addCell(cellContent);
					//清償債款PAYLIEN
					tmp=String.valueOf( data.getPaylien() ) +"\n　"  ;
					cellContent.setPhrase(new Phrase(tmp, fontColCent));			
					contentTable.addCell(cellContent);
					//扣款合計PAYTLDED
					tmp=fm.format(data.getPaytlded());
					cellContent.setPhrase(new Phrase( tmp , fontColCent));			
					contentTable.addCell(cellContent);
				}			
			}
			//每頁頂端列,除了總計頁,每頁標題固定3列
			if (!blnSum)
				contentTable.setHeaderRows(3);
			
			//建立各項小計 並設定其欄位數
			PdfPTable sumTable = new PdfPTable(pdfCol);			
			//設定table的寬度
			sumTable.setWidthPercentage(100f);
			//設定預設的cell高度
			sumTable.getDefaultCell().setFixedHeight(50f);
			//各欄寬度
			sumTable.setWidths(floatCol);
			
			PdfPCell sumCell ;
			sumCell= new PdfPCell();
			sumCell.setHorizontalAlignment(Element.ALIGN_CENTER); //垂直置中
			sumCell.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
			sumCell.setPhrase(new Phrase("人數合計:"+fm.format(intPer), fontColTitlA));
			sumCell.setColspan(3);
			sumCell.setFixedHeight(37f);
			sumTable.addCell(sumCell);
			
			if (blnSum)
				sumCell.setPhrase(new Phrase("總計:", fontColTitlA));
			else
				sumCell.setPhrase(new Phrase("合計:", fontColTitlA));
			sumCell.setColspan(1);
			sumTable.addCell(sumCell);
			
			sumCell.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置中
			//公保費PAYGEINS,公共設施使用費PAYGYM
			tmp = String.valueOf(intCellSum_1A)+"\n\n"+String.valueOf(intCellSum_1B);
			sumCell.setPhrase(new Phrase( tmp , fontColCent));
			sumTable.addCell(sumCell);
			//健保費PAYDPINS,代扣停車費PAYPARK1
			tmp = String.valueOf(intCellSum_2A)+"\n\n"+String.valueOf(intCellSum_2B);
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//勞保費PAYLBINS,眷舍管理費PAYDORM1+PAYDORM2+PAYDORM3+payother2PGY眷舍管理費 2025.8.20 
			tmp = String.valueOf(intCellSum_3A)+"\n\n"+String.valueOf(intCellSum_3B);
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//退撫離職金PAYRETIR,餐費PAYMEAL
			tmp = String.valueOf(intCellSum_4A)+"\n\n"+String.valueOf(intCellSum_4B);
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//所得稅PAYTAX,製卡費PAYCARDF
			tmp = String.valueOf(intCellSum_5A)+"\n\n"+String.valueOf(intCellSum_5B);
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//公教儲金PAYGEDP,惠民捐款PAYAGEN1
			tmp = String.valueOf(intCellSum_6A)+"\n\n"+String.valueOf(intCellSum_6B);
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//醫療互助金PAYNSPFD,醫療糾紛PAYOTHER3
			tmp = String.valueOf(intCellSum_7A)+"\n\n"+String.valueOf(intCellSum_7B);
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//國宅貸款PAYHLOA1+PAYHLOA2+PAYHLOA3+PAYHLOA4+PAYHLOA5+PAYHLOA6,體適能中心費用PAYVETD
			tmp = String.valueOf(intCellSum_8A)+"\n\n"+String.valueOf(intCellSum_8B);
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//自繳勞退PAYDLOAN
			tmp = String.valueOf(intCellSum_9A)+"\n\n　";
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//補充保險費PAYOTHER1
			tmp = String.valueOf(intCellSum_10A)+"\n\n　";
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//房屋津貼PAYMHOUS
			tmp = String.valueOf(intCellSum_11A)+"\n\n　";
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//清償債款PAYLIEN
			tmp = String.valueOf(intCellSum_12A)+"\n\n　";
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//扣款合計PAYTLDED
			tmp = fm.format(intCellSum_13A);
			sumCell.setPhrase(new Phrase( tmp , fontColCent));
			sumTable.addCell(sumCell);
			
			document.add(contentTable); //加入內容Table
			document.add(sumTable);		//加入小計table
			

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.info(me + " fail, " + e);
			throw new FileNotFoundException(me + ", fail!!." + e);
		} catch (DocumentException e) {
			e.printStackTrace();
			log.info(me + " fail, " + e);
			throw new DocumentException(me + ", fail!!." + e);
		} catch (IOException e) {
			e.printStackTrace();
			log.info(me + " fail, " + e);
			throw new IOException(me + ", fail!!." + e);
		} catch (Exception e) {
			e.printStackTrace();
			log.info(me + " fail, " + e);
			throw new Exception(me + ", fail!!." + e);
		} finally {
			if ( document !=null ) document.close();
			if ( writer !=null ) writer.close();
		}
				
	}
	
	
}
