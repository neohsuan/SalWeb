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
//薪津冊_屏榮
//2026.2.11#11501312簽約留任金PAYCPLM2
public class Payslip_2A0 {
	Logger log = Logger.getLogger(SalNTDAO.class);  //呼叫log4j
	String ttfDir = "font\\VGHKH_KAI.TTF";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat signDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	NumberFormat fm = NumberFormat.getNumberInstance();  //千分位
	public Payslip_2A0() {
		
	}

	public Payslip_2A0(String strDir,String pdfFileName, boolean blnSum,ArrayList<Paprtdtl> paprtdtlAry ,String hid) throws Exception {
		String me = getClass().getName() + ".Payslip_2A0()";
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
		int intCellSum_9A=0,intCellSum_9B=0,intCellSum_10A=0,intCellSum_11A=0,intCellSum_12A=0,intCellSum_13A=0;
		try {
			//新增資料夾路徑
			if (!FileUtil.FileToSmbFile(strDir,hid).exists()){
				fileUtil.FileToSmbFile(strDir,hid).mkdirs();
			}	
			//建立PDF檔案			
			SmbFile payfile = fileUtil.FileToSmbFile(pdfFileName,hid);
			
			// 步驟1 - 設定產生的PDF文件大小
			document = new Document(PageSize.A4.rotate(), 30,25,25,20);//left, right, top, bottom
			
			int pdfSCol = 7; // 簽名處有幾欄
			float[] floatColSign = {0.14f, 0.14f, 0.14f, 0.14f, 0.14f, 0.14f, 0.16f}; //欄位比例
			String signName[]={"製表","總務室","人事審核","人事室","主計審核","主計室","機關首長及其授權人"};
			String titleName[]={"職號\n卡號","階級","職稱","姓名","支給\n日數","薪俸\n不休假獎金","專業加給\n休假補助","主管加給\n考績獎金","院區加給\n年終獎金","身障加給\n各項補助","特別津貼\n各項津貼","各項津貼\n護理能力進階","專勤獎金\n病歷罰扣","加班費\n簽約留任金","夜班費\n　","應發數","扣款數","實發數"};
			int pdfCol = 18; // 內文有幾欄
			float[] floatCol = {0.045f, 0.06f, 0.08f, 0.06f, 0.03f, 0.055f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.055f, 0.05f, 0.05f, 0.05f, 0.07f, 0.065f, 0.07f}; //欄位比例
			
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
			Font fontColTitlC = new Font( bfChinese, 6, Font.NORMAL ); //標題字型設定，size為6
			Font fontColCent = new Font( bfChinese, 7, Font.NORMAL ); //字型設定，size為8
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
			mTitleCell.setColspan(8);
			mTitleCell.setBorder(Rectangle.NO_BORDER);
			mTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT); 
			contentTable.addCell(mTitleCell);
			
			//第2行
			mTitleCell= new PdfPCell(new Phrase("薪津表" , fontTitle14));
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
			mTitleCell.setColspan(8);
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
				if (i==4)	//支給日數
					sTitleCell.setPhrase(new Phrase( titleName[i], fontColCent));
				else if (i==5 || i==13 )	//長度=5
					sTitleCell.setPhrase(new Phrase( titleName[i], fontColTitlB));
				else if (i==11 )	//長度=6
					sTitleCell.setPhrase(new Phrase( titleName[i], fontColTitlC));
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
				//薪俸PAYBASE+PAYPROMO,不休假獎金PAYYEAR
				intCellSum_1A += data.getPaybase()+data.getPaypromo();
				intCellSum_1B += data.getPayyear();
				//專業加給PAYSPSU+PAYSPSD,休假補助PAYADRTN 
				intCellSum_2A += data.getPayspsu()+data.getPayspsd();
				intCellSum_2B += data.getPayadrtn();
				//主管加給PAYCHALW,考績獎金PAYGRADE
				intCellSum_3A += data.getPaychalw();
				intCellSum_3B += data.getPaygrade();
				//院區加給PAYSTBNT,年終獎金PAYADDHL
				intCellSum_4A += data.getPaystbnt();
				intCellSum_4B += data.getPayaddhl();
				//身障加給PAYTRANS,各項補助PAYCPLM1         
				intCellSum_5A += data.getPaytrans();
				intCellSum_5B += data.getPaycplm1();
				//特別津貼PAYSUPLY,各項津貼PAYEXTRA    
				intCellSum_6A += data.getPaysuply();
				intCellSum_6B += data.getPayextra();
				//員工獎勵金PAYPFNT,護理能力進階PAYRSUPLY   
				intCellSum_7A += data.getPaypfnt();
				intCellSum_7B += data.getPayrsuply();
				//專勤獎金PAYSTALD+PAYSTAL ,病歷罰扣PAYSARS   ,
				intCellSum_8A += data.getPaystald()+data.getPaystal();
				intCellSum_8B += data.getPaysars();
				//加班費PAYCPLM3,(2026.2.11#11501312簽約留任金PAYCPLM2)
				intCellSum_9A += data.getPaycplm3();
				intCellSum_9B += data.getPaycplm2();
				//夜班費PAYNIGHT
				intCellSum_10A += data.getPaynight();
				//應發數PAYTOTAL
				intCellSum_11A += data.getPaytotal();
				//扣款數PAYTLDED
				intCellSum_12A += data.getPaytlded();
				//實發數
				intCellSum_13A += data.getPaynet();	
				
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
					//cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
					contentTable.addCell(cellContent);
					//姓名
					cellContent.setPhrase(new Phrase(data.getPaynamec(), fontColCentB));			
					contentTable.addCell(cellContent);
					//支給日數
					cellContent.setHorizontalAlignment(Element.ALIGN_CENTER); //垂直置左
					cellContent.setPhrase(new Phrase(String.valueOf(data.getPaydayno()), fontColCent));			
					contentTable.addCell(cellContent);
					//薪俸PAYBASE+PAYPROMO,不休假獎金PAYYEAR
					cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置右
					tmp = String.valueOf( data.getPaybase()+data.getPaypromo() )+"\n"+String.valueOf(data.getPayyear());
					cellContent.setPhrase(new Phrase( tmp , fontColCent));			
					contentTable.addCell(cellContent);
					//專業加給PAYSPSU+PAYSPSD,休假補助PAYADRTN 
					tmp=String.valueOf( data.getPayspsu()+data.getPayspsd())+"\n"+String.valueOf(data.getPayadrtn());
					cellContent.setPhrase(new Phrase( tmp , fontColCent));			
					contentTable.addCell(cellContent);
					//主管加給PAYCHALW,考績獎金PAYGRADE
					tmp = String.valueOf( data.getPaychalw())+"\n"+String.valueOf(data.getPaygrade() );
					cellContent.setPhrase(new Phrase(tmp, fontColCent));
					contentTable.addCell(cellContent);
					//院區加給PAYSTBNT,年終獎金PAYADDHL
					tmp = String.valueOf( data.getPaystbnt() ) +"\n" + String.valueOf( data.getPayaddhl() );
					cellContent.setPhrase(new Phrase(tmp, fontColCent));			
					contentTable.addCell(cellContent);
					//身障加給PAYTRANS,各項補助PAYCPLM1    
					tmp=String.valueOf( data.getPaytrans() )+"\n"+String.valueOf( data.getPaycplm1() );
					cellContent.setPhrase(new Phrase(tmp, fontColCent));		
					contentTable.addCell(cellContent);
					//特別津貼PAYSUPLY 各項津貼PAYEXTRA  
					tmp=String.valueOf( data.getPaysuply() )+"\n"+String.valueOf( data.getPayextra() );
					cellContent.setPhrase(new Phrase(tmp, fontColCent));
					contentTable.addCell(cellContent);
					//員工獎勵金PAYPFNT,護理能力進階PAYRSUPLY 
					tmp=String.valueOf( data.getPaypfnt() )+"\n"+String.valueOf( data.getPayrsuply() );
					cellContent.setPhrase(new Phrase(tmp, fontColCent));			
					contentTable.addCell(cellContent);
					//專勤獎金PAYRSTBN+PAYSTAL,病歷罰扣PAYSARS 
					tmp=String.valueOf( data.getPayrstbn()+data.getPaystal() )+"\n"+String.valueOf( data.getPaysars() );
					cellContent.setPhrase(new Phrase(tmp, fontColCent));		
					contentTable.addCell(cellContent);
					//加班費PAYCPLM3(2026.2.11#11501312簽約留任金PAYCPLM2)
					tmp=String.valueOf( data.getPaycplm3() )+"\n"+String.valueOf( data.getPaycplm2() );
					cellContent.setPhrase(new Phrase(tmp, fontColCent));		
					contentTable.addCell(cellContent);
					//夜班費PAYNIGHT
					tmp=String.valueOf( data.getPaynight() )+"\n　" ;
					cellContent.setPhrase(new Phrase(tmp, fontColCent));
					contentTable.addCell(cellContent);
					//應發數PAYTOTAL
					tmp=fm.format( data.getPaytotal() ) ;
					cellContent.setPhrase(new Phrase(tmp, fontColCent));			
					contentTable.addCell(cellContent);
					//扣款數PAYTLDED
					tmp=fm.format( data.getPaytlded() ) ;
					cellContent.setPhrase(new Phrase(tmp, fontColCent));			
					contentTable.addCell(cellContent);
					//實發數
					tmp=fm.format(data.getPaynet());
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
			sumCell.setColspan(2);
			sumTable.addCell(sumCell);
			
			sumCell.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置中
			//薪俸PAYBASE+PAYPROMO,不休假獎金PAYYEAR
			tmp = String.valueOf(intCellSum_1A)+"\n\n"+String.valueOf(intCellSum_1B);
			sumCell.setPhrase(new Phrase( tmp , fontColTitlA));
			sumCell.setColspan(1);
			sumTable.addCell(sumCell);
			//專業加給PAYSPSU+PAYSPSD,休假補助PAYADRTN 
			tmp = String.valueOf(intCellSum_2A)+"\n\n"+String.valueOf(intCellSum_2B);
			sumCell.setPhrase(new Phrase( tmp, fontColTitlA));
			sumTable.addCell(sumCell);
			//主管加給PAYCHALW,考績獎金PAYGRADE
			tmp = String.valueOf(intCellSum_3A)+"\n\n"+String.valueOf(intCellSum_3B);
			sumCell.setPhrase(new Phrase( tmp, fontColTitlA));
			sumTable.addCell(sumCell);
			//院區加給PAYSTBNT,年終獎金PAYADDHL
			tmp = String.valueOf(intCellSum_4A)+"\n\n"+String.valueOf(intCellSum_4B);
			sumCell.setPhrase(new Phrase( tmp, fontColTitlA));
			sumTable.addCell(sumCell);
			//身障加給PAYTRANS,各項補助PAYCPLM1 
			tmp = String.valueOf(intCellSum_5A)+"\n\n"+String.valueOf(intCellSum_5B);
			sumCell.setPhrase(new Phrase( tmp, fontColTitlA));
			sumTable.addCell(sumCell);
			//特別津貼PAYSUPLY 各項津貼PAYEXTRA 
			tmp = String.valueOf(intCellSum_6A)+"\n\n"+String.valueOf(intCellSum_6B);
			sumCell.setPhrase(new Phrase( tmp, fontColTitlA));
			sumTable.addCell(sumCell);
			//員工獎勵金PAYPFNT,護理能力進階PAYRSUPLY
			tmp = String.valueOf(intCellSum_7A)+"\n\n"+String.valueOf(intCellSum_7B);
			sumCell.setPhrase(new Phrase( tmp, fontColTitlA));
			sumTable.addCell(sumCell);
			//專勤獎金PAYRSTBN+PAYSTAL,病歷罰扣PAYSARS 
			tmp = String.valueOf(intCellSum_8A)+"\n\n"+String.valueOf(intCellSum_8B);
			sumCell.setPhrase(new Phrase( tmp, fontColTitlA));
			sumTable.addCell(sumCell);
			//加班費PAYCPLM3,(2026.2.11#11501312簽約留任金PAYCPLM2)
			tmp = String.valueOf(intCellSum_9A)+"\n\n"+String.valueOf(intCellSum_9B);
			sumCell.setPhrase(new Phrase( tmp, fontColTitlA));
			sumTable.addCell(sumCell);
			//夜班費PAYNIGHT
			tmp = String.valueOf(intCellSum_10A)+"\n\n　";
			sumCell.setPhrase(new Phrase( tmp, fontColTitlA));
			sumTable.addCell(sumCell);
			//應發數
			tmp = fm.format(intCellSum_11A);
			sumCell.setPhrase(new Phrase( tmp, fontColTitlA));
			sumTable.addCell(sumCell);
			//扣款數
			tmp = fm.format(intCellSum_12A);
			sumCell.setPhrase(new Phrase( tmp, fontColTitlA));
			sumTable.addCell(sumCell);
			//實發數
			tmp = fm.format(intCellSum_13A);
			sumCell.setPhrase(new Phrase( tmp , fontColTitlA));
			sumTable.addCell(sumCell);
			
			//總計頁 要加 蓋章欄
			PdfPTable signTable = new PdfPTable(pdfSCol);
			//設定table的寬度
			signTable.setWidthPercentage(100f);
			
			//設定預設的cell高度
			signTable.getDefaultCell().setFixedHeight(40f);
			//各欄寬度
			signTable.setWidths(floatColSign);
			//簽名
			PdfPCell signCell ;			
			for (int i=0;i<signName.length;i++){
				signCell= new PdfPCell();
				signCell.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置中
				signCell.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
				signCell.setFixedHeight(50f);
				//框線
				signCell.setBorder( Rectangle.NO_BORDER);
				signCell.setPhrase(new Phrase( signName[i], fontColCentB));
				signTable.addCell(signCell);
			}
			
			document.add(contentTable); //加入內容Table
			document.add(sumTable);		//加入小計table
			if (blnSum)
				document.add(signTable);		//加入簽名table

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
