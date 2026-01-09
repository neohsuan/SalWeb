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
//扣款表_聯醫
public class Deduct_3A0 {
	Logger log = Logger.getLogger(SalNTDAO.class);  //呼叫log4j
	String ttfDir = "font\\VGHKH_KAI.TTF";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat signDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	NumberFormat fm = NumberFormat.getNumberInstance();  //千分位
	public Deduct_3A0() {
		
	}

	public Deduct_3A0(String strDir,String pdfFileName,boolean blnSum,ArrayList<Paprtdtl> paprtdtlAry ,String hid) throws Exception {
		String me = getClass().getName() + ".Deduct_3A0()";
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
			
			String titleName[]={"職號\n卡號","階級","職稱","姓名","公保費","健保費","勞保費","退撫基金","勞工退休金","補充保險費","所得稅","優惠存款","清償債款","　","　","　","扣款合計"};
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
			String strrank=prtobj.getPayrank();
			tmp="發薪年月:"+dateString.substring(0,4)+"年"+dateString.substring(5, 7)+"月";
			if ("F".equals(strrank)){
				tmp+="(契約人員薪資為前一個月)";
			}
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
				//長度>=5 有字體=7
				if (String.valueOf(i).matches("8|9|10"))
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

				//公保費PAYGEINS    
				intCellSum_1A += data.getPaygeins();
				//健保費PAYDPINS
				intCellSum_2A += data.getPaydpins();
				//勞保費PAYLBINS
				intCellSum_3A += data.getPaylbins();
				//退撫基金PAYRETIR  
				intCellSum_4A += data.getPayretir();
				//勞工退休金PAYDLOAN 
				intCellSum_5A += data.getPaydloan();
				//補充保險費PAYOTHER1
				intCellSum_6A += data.getPayother1();
				//所得稅PAYTAX
				intCellSum_7A += data.getPaytax();
				//優惠存款PAYGEDP
				intCellSum_8A += data.getPaygedp();
				//清償債款PAYLIEN
				intCellSum_9A += data.getPaylien();
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
					
					//公保費PAYGEINS 
					cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置右
					tmp = String.valueOf( data.getPaygeins() );
					cellContent.setPhrase(new Phrase( tmp , fontColCent));			
					contentTable.addCell(cellContent);
					//健保費PAYDPINS
					tmp=String.valueOf( data.getPaydpins() );
					cellContent.setPhrase(new Phrase( tmp , fontColCent));			
					contentTable.addCell(cellContent);
					//勞保費PAYLBINS 
					tmp = String.valueOf( data.getPaylbins() );
					cellContent.setPhrase(new Phrase(tmp, fontColCent));
					contentTable.addCell(cellContent);
					//退撫離職金PAYRETIR  
					tmp = String.valueOf( data.getPayretir() );
					cellContent.setPhrase(new Phrase(tmp, fontColCent));			
					contentTable.addCell(cellContent);
					//勞工退休金PAYDLOAN 
					tmp=String.valueOf( data.getPaydloan() );
					cellContent.setPhrase(new Phrase(tmp, fontColCent));		
					contentTable.addCell(cellContent);
					//補充保險費PAYOTHER1
					tmp=String.valueOf( data.getPayother1() );
					cellContent.setPhrase(new Phrase(tmp, fontColCent));
					contentTable.addCell(cellContent);
					//所得稅PAYTAX
					tmp=String.valueOf( data.getPaytax() );
					cellContent.setPhrase(new Phrase(tmp, fontColCent));			
					contentTable.addCell(cellContent);
					//優惠存款PAYGEDP
					tmp=String.valueOf( data.getPaygedp() )+"\n　" ;
					cellContent.setPhrase(new Phrase(tmp, fontColCent));		
					contentTable.addCell(cellContent);
					//清償債款PAYLIEN
					tmp=String.valueOf( data.getPaylien());
					cellContent.setPhrase(new Phrase(tmp, fontColCent));		
					contentTable.addCell(cellContent);
					//
					tmp="　" ;
					cellContent.setPhrase(new Phrase(tmp, fontColCent));
					contentTable.addCell(cellContent);
					//
					tmp="　"  ;
					cellContent.setPhrase(new Phrase(tmp, fontColCent));			
					contentTable.addCell(cellContent);
					//
					tmp="　"  ;
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
			sumCell.setFixedHeight(30f);
			sumTable.addCell(sumCell);
			
			if (blnSum)
				sumCell.setPhrase(new Phrase("總計:", fontColTitlA));
			else
				sumCell.setPhrase(new Phrase("合計:", fontColTitlA));
			sumCell.setColspan(1);
			sumTable.addCell(sumCell);
			
			sumCell.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置中
			//公保費PAYGEINS 
			tmp = String.valueOf(intCellSum_1A);
			sumCell.setPhrase(new Phrase( tmp , fontColCent));
			sumTable.addCell(sumCell);
			//健保費PAYDPINS
			tmp = String.valueOf(intCellSum_2A);
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//勞保費PAYLBINS
			tmp = String.valueOf(intCellSum_3A);
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//退撫基金PAYRETIR
			tmp = String.valueOf(intCellSum_4A);
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//勞工退休金PAYDLOAN 
			tmp = String.valueOf(intCellSum_5A);
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//補充保險費PAYOTHER1 
			tmp = String.valueOf(intCellSum_6A);
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//所得稅PAYTAX
			tmp = String.valueOf(intCellSum_7A);
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//優惠存款PAYGEDP
			tmp = String.valueOf(intCellSum_8A);
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//清償債款PAYLIEN
			tmp = String.valueOf(intCellSum_9A);
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			// 
			tmp = "　";
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//
			tmp = "　";
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//
			tmp = "　";
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
