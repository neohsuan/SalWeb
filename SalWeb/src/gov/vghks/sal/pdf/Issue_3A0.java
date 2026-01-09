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
//薪津冊補發_聯醫
public class Issue_3A0 {
	Logger log = Logger.getLogger(SalNTDAO.class);  //呼叫log4j
	String ttfDir = "font\\VGHKH_KAI.TTF";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat signDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	NumberFormat fm = NumberFormat.getNumberInstance();  //千分位
	public Issue_3A0() {
		
	}

	public Issue_3A0(String strDir,String pdfFileName, boolean blnSum,ArrayList<Paprtdtl> paprtdtlAry ,String hid) throws Exception {
		String me = getClass().getName() + ".Issue_3A0()";
		Document document = null;
		PdfWriter writer = null;
		Paprtdtl prtobj=null;
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		
		FileUtil fileUtil = new FileUtil();
		String tmp="";
		int intPer=0;//人數合計
		int intCellSum_1=0,intCellSum_2=0,intCellSum_3=0,intCellSum_4=0,intCellSum_5=0,intCellSum_6=0;
		try {
			//新增資料夾路徑
			if (!FileUtil.FileToSmbFile(strDir,hid).exists()){
				fileUtil.FileToSmbFile(strDir,hid).mkdirs();
			}	
			//建立PDF檔案			
			SmbFile payfile = fileUtil.FileToSmbFile(pdfFileName,hid);
			
			// 步驟1 - 設定產生的PDF文件大小 橫式
			document = new Document(PageSize.A4.rotate(), 30,25,25,20);//left, right, top, bottom
			
			String titleName[]={"職號","卡號","階級","職稱","姓名","支給\n日數","薪俸","專業加給","主管加給","責任加給","職務加給","實發金額","異動原因","生效日期"};
			int pdfCol = 14; // 內文有幾欄
			float[] floatCol = {0.05f, 0.04f, 0.08f, 0.13f, 0.08f,0.03f, 0.07f, 0.07f, 0.07f, 0.07f, 0.07f, 0.10f, 0.07f, 0.07f}; //欄位比例
			
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
			Font fontColCent = new Font( bfChinese, 8, Font.NORMAL ); //字型設定，size為8
			Font fontColCentB = new Font( bfChinese, 9, Font.BOLD ); //字型設定，size為8
			
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
			mTitleCell.setColspan(7);
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
			mTitleCell= new PdfPCell(new Phrase("薪津表(補發扣)" , fontTitle14));
			mTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置中
			mTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
			mTitleCell.setBorder(Rectangle.NO_BORDER);
			mTitleCell.setColspan(7);
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
			mTitleCell.setColspan(6);
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
				if (i==5)
					sTitleCell.setPhrase(new Phrase( titleName[i], fontColCent));
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
				//薪俸PAYRBASE
				intCellSum_1 += data.getPayrbase();
				//專業加給PAYRSPSU=PAYRSPSU+PAYRSPSD
				intCellSum_2 += data.getPayrspsu()+data.getPayrspsd();
				//主管加給PAYRCHALW
				intCellSum_3 += data.getPayrchalw();
				//責任加給PAYRSTBN
				intCellSum_4 += data.getPayrstbn();
				//職務加給PAYRSUPLY
				intCellSum_5 += data.getPayrsuply();
				//實發金額PAYTOTIS
				intCellSum_6 += data.getPaytotis();
				
				//總計,不用列各列明細
				if (!blnSum){
					//職號
					cellContent.setPhrase(new Phrase(data.getPayempno(), fontColCent));
					cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
					contentTable.addCell(cellContent);
					//卡號
					cellContent.setPhrase(new Phrase(data.getPaycard(), fontColCent));			
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
					contentTable.addCell(cellContent);
					//職稱
					cellContent.setPhrase(new Phrase(StringUtil.nullToString(data.getPaytitlp()), fontColCent));
					contentTable.addCell(cellContent);
					//姓名
					cellContent.setPhrase(new Phrase(data.getPaynamec(), fontColCentB));			
					contentTable.addCell(cellContent);
					//支給日數
					cellContent.setHorizontalAlignment(Element.ALIGN_CENTER); //垂直置左
					cellContent.setPhrase(new Phrase(String.valueOf(data.getPaydayno()), fontColCent));			
					contentTable.addCell(cellContent);
					//薪俸PAYRBASE
					cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置右
					tmp = String.valueOf( data.getPayrbase() );
					cellContent.setPhrase(new Phrase( tmp , fontColCent));			
					contentTable.addCell(cellContent);
					//專業加給PAYRSPSU=PAYRSPSU+PAYRSPSD
					tmp=String.valueOf( data.getPayrspsu()+data.getPayrspsd() );
					cellContent.setPhrase(new Phrase( tmp , fontColCent));			
					contentTable.addCell(cellContent);
					//主管加給PAYRCHALW
					tmp = String.valueOf( data.getPayrchalw() );
					cellContent.setPhrase(new Phrase(tmp, fontColCent));
					contentTable.addCell(cellContent);
					//責任加給PAYRSTBN
					tmp = String.valueOf( data.getPayrstbn() );
					cellContent.setPhrase(new Phrase(tmp, fontColCent));			
					contentTable.addCell(cellContent);
					//職務加給PAYRSUPLY
					tmp = String.valueOf( data.getPayrsuply() );
					cellContent.setPhrase(new Phrase(tmp, fontColCent));			
					contentTable.addCell(cellContent);
					//實發金額PAYTOTIS
					tmp=fm.format( data.getPaytotis() );
					cellContent.setPhrase(new Phrase(tmp, fontColCent));		
					contentTable.addCell(cellContent);
					//異動原因TRANAME
					tmp = String.valueOf( data.getTraname() );
					cellContent.setPhrase(new Phrase(tmp, fontColCent));	
					cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
					contentTable.addCell(cellContent);
					//生效日期PAEFFDT
					tmp = String.valueOf( data.getPaeffdt().toString() );
					cellContent.setPhrase(new Phrase(tmp, fontColCent));			
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
			sumCell.setColspan(4);
			sumCell.setFixedHeight(37f);
			sumTable.addCell(sumCell);
			
			if (blnSum)
				sumCell.setPhrase(new Phrase("總計:", fontColTitlA));
			else
				sumCell.setPhrase(new Phrase("合計:", fontColTitlA));
			sumCell.setColspan(2);
			sumTable.addCell(sumCell);
			
			sumCell.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置中
			//薪俸PAYRBASE
			tmp = String.valueOf(intCellSum_1);
			sumCell.setPhrase(new Phrase( tmp , fontColCent));
			sumCell.setColspan(1);
			sumTable.addCell(sumCell);
			//專業加給PAYRSPSU=PAYRSPSU+PAYRSPSD
			tmp = String.valueOf(intCellSum_2);
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//主管加給PAYRCHALW
			tmp = String.valueOf(intCellSum_3);
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//責任加給PAYRSTBN
			tmp = String.valueOf(intCellSum_4);
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//職務加給PAYRSUPLY
			tmp = fm.format(intCellSum_5);
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//實發金額PAYTOTIS
			tmp = fm.format(intCellSum_6);
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
			
			//document加入table
			document.add(contentTable); //內容Table
			document.add(sumTable);		//小計table
			
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
