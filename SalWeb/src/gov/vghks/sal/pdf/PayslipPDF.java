package gov.vghks.sal.pdf;

import gov.vghks.common.util.StringUtil;
import gov.vghks.sal.Hospitals;
import gov.vghks.sal.dao.SalNTDAO;
import gov.vghks.sal.model.Paprtdtl;
import gov.vghks.sal.util.FileUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

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

public class PayslipPDF {
	
	String ttfDir = "font\\VGHKH_KAI.TTF";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat signDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	NumberFormat fm = NumberFormat.getNumberInstance();  //千分位
	public PayslipPDF() {
		
	}

	public PayslipPDF( ArrayList<Paprtdtl> paprtdtlAry ,String hid,String salnum) {
		
		Document document = null;
		PdfWriter writer = null;
		
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		String strDir = hospInfo.fileSRV+"prt\\";
		
		FileUtil fileUtil = new FileUtil();
		
		
		try {
			//新增資料夾路徑
			if (!FileUtil.FileToSmbFile(strDir,hid).exists()){
				fileUtil.FileToSmbFile(strDir,hid).mkdirs();
			}	
			//建立PDF檔案			
			String pdfFileName = "smb:" + (strDir+"薪津表_"+salnum+".pdf").replace("\\", "/");
			SmbFile payfile = fileUtil.FileToSmbFile(pdfFileName,hid);
			
			// 步驟1 - 設定產生的PDF文件大小
			document = new Document(PageSize.A4.rotate(), 15,15,15,15);
			
			int pdfCol = 18; // PDF有幾欄
			float[] floatCol = {0.05f, 0.06f, 0.08f, 0.06f, 0.02f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.07f, 0.07f, 0.08f}; //欄位比例
			int pdfHCol = 4; // PDF有幾欄
			float[] floatHCol = {0.8f, 0.8f, 0.5f, 0.5f}; //欄位比例
			writer = PdfWriter.getInstance(document, new SmbFileOutputStream(payfile));
			document.open();			
			PdfContentByte cb = writer.getDirectContent();
			
			//中文設定，載入字型(新細明體)
			// 載入難字字型
    		BaseFont bfChinese = BaseFont.createFont("font\\VGHKH_KAI.TTF", BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);
    		// 載入難字字型
    		FontFactory.register("font\\VGHKH_KAI.TTF");
			Font fontTitle = new Font( bfChinese, 18, Font.BOLD ); //標題字型設定，size為18
			Font fontTitle14 = new Font( bfChinese, 14, Font.NORMAL ); //標題字型設定，size為16
			Font fontTitle10 = new Font( bfChinese, 10, Font.NORMAL ); //標題字型設定，size為10
			Font fontColTitlA = new Font( bfChinese, 8, Font.BOLD ); //標題字型設定，size為8
			Font fontColTitlB = new Font( bfChinese, 7, Font.BOLD ); //標題字型設定，size為7
			Font fontColCent = new Font( bfChinese, 8, Font.NORMAL ); //字型設定，size為6
			Font fontColCentB = new Font( bfChinese, 8, Font.BOLD ); //字型設定，size為6
			
			//加入標題
			PdfPTable mTable = new PdfPTable(pdfHCol);
			mTable.setWidthPercentage(100);//寬度100%
			mTable.setSpacingBefore(10f);//前間距
			mTable.setSpacingAfter(10f);//後間距
			//設定每個欄位的寬度
			mTable.setWidths( floatHCol );
			
			//Cell設定			
			PdfPCell mainTitle ;
			//第1行
			mainTitle= new PdfPCell(new Phrase("" , fontTitle));
			mainTitle.setHorizontalAlignment(Element.ALIGN_CENTER); //垂直置中
			mainTitle.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
			mainTitle.setBorder(0);
			mainTitle.setFixedHeight(20f);
			mTable.addCell(mainTitle);
			
			String strMT_hosptial=hospInfo.nmFull;
			mainTitle.setPhrase( new Phrase(strMT_hosptial , fontTitle));
			mainTitle.setBorder( Rectangle.NO_BORDER);
			mainTitle.setHorizontalAlignment(Element.ALIGN_CENTER); //垂直置中
			mainTitle.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
			mainTitle.setFixedHeight(25f);
			mTable.addCell(mainTitle);
			
			mainTitle= new PdfPCell(new Phrase("" , fontTitle));
			mainTitle.setHorizontalAlignment(Element.ALIGN_CENTER); //垂直置中
			mainTitle.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
			mainTitle.setBorder(0);
			mainTitle.setFixedHeight(20f);
			mTable.addCell(mainTitle);
 			
 			String strprttm=signDateFormat.format(new Date()) ;
			mainTitle.setPhrase( new Phrase("列印日期:"+strprttm , fontTitle10) );			
			mainTitle.setHorizontalAlignment( Element.ALIGN_RIGHT);
			mainTitle.setBorder(0);
			mainTitle.setFixedHeight(20f);
			mTable.addCell(mainTitle);
			
			//第2行
			mainTitle= new PdfPCell(new Phrase("" , fontTitle14));
			mainTitle.setHorizontalAlignment(Element.ALIGN_CENTER); //垂直置中
			mainTitle.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
			mainTitle.setBorder(0);
			mainTitle.setFixedHeight(20f);
			mTable.addCell(mainTitle);
			
			mainTitle.setPhrase( new Phrase("薪津表" , fontTitle14));
			mainTitle.setBorder( Rectangle.NO_BORDER);
			mainTitle.setHorizontalAlignment(Element.ALIGN_CENTER); //垂直置中
			mainTitle.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
			mainTitle.setFixedHeight(20f);
			mTable.addCell(mainTitle);
			
			mainTitle= new PdfPCell(new Phrase("發薪年月:2025年7月" , fontTitle14));
			mainTitle.setHorizontalAlignment(Element.ALIGN_CENTER); //垂直置中
			mainTitle.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
			mainTitle.setBorder(0);
			mainTitle.setFixedHeight(20f);
			mTable.addCell(mainTitle);
 			
 			mainTitle.setPhrase( new Phrase("單位:元" , fontTitle10) );			
			mainTitle.setHorizontalAlignment( Element.ALIGN_RIGHT);
			mainTitle.setBorder(0);
			mainTitle.setFixedHeight(20f);
			mTable.addCell(mainTitle);
			
			//建立內容表格 並設定其欄位數
			PdfPTable contentTable = new PdfPTable(pdfCol);
			
			//設定table的寬度
			contentTable.setWidthPercentage(100f);
			contentTable.setSpacingBefore(0f);//前間距
			contentTable.setSpacingAfter(0f);//後間距
			//設定預設的cell高度為20f
			contentTable.getDefaultCell().setFixedHeight(20f);
			contentTable.setWidths(floatCol);
			
			
			PdfPCell sTitleCell ;
			sTitleCell= new PdfPCell(new Phrase("" , fontTitle));
			sTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER); //垂直置中
			sTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
			
			sTitleCell.setPhrase(new Phrase("職號\n卡號", fontColTitlA));			
			contentTable.addCell(sTitleCell);
			sTitleCell.setPhrase(new Phrase("階級", fontColTitlA));
			contentTable.addCell(sTitleCell);
			sTitleCell.setPhrase(new Phrase("職稱", fontColTitlA));
			contentTable.addCell(sTitleCell);
			sTitleCell.setPhrase(new Phrase("姓名", fontColTitlA));
			contentTable.addCell(sTitleCell);
			sTitleCell.setPhrase(new Phrase("支給\n日數", fontColCent));
			contentTable.addCell(sTitleCell);
			sTitleCell.setPhrase(new Phrase("薪俸\n超額獎金", fontColTitlA));
			contentTable.addCell(sTitleCell);
			sTitleCell.setPhrase(new Phrase("專業加給\n超盈餘獎金", fontColTitlB));
			contentTable.addCell(sTitleCell);
			sTitleCell.setPhrase(new Phrase("主管加給\n考績獎金", fontColTitlA));
			contentTable.addCell(sTitleCell);
			sTitleCell.setPhrase(new Phrase("實習費\n年終獎金", fontColTitlA));
			contentTable.addCell(sTitleCell);
			sTitleCell.setPhrase(new Phrase("工作獎金\n各項津貼", fontColTitlA));
			contentTable.addCell(sTitleCell);
			sTitleCell.setPhrase(new Phrase("專勤獎金\n病歷罰扣", fontColTitlA));
			contentTable.addCell(sTitleCell);
			sTitleCell.setPhrase(new Phrase("加班費\n各項獎勵", fontColTitlA));
			contentTable.addCell(sTitleCell);
			sTitleCell.setPhrase(new Phrase("夜班費\n各項補助", fontColTitlA));
			contentTable.addCell(sTitleCell);
			sTitleCell.setPhrase(new Phrase("不休假獎金", fontColTitlB));
			contentTable.addCell(sTitleCell);
			sTitleCell.setPhrase(new Phrase("休假補助", fontColTitlA));
			contentTable.addCell(sTitleCell);
			sTitleCell.setPhrase(new Phrase("應發數", fontColTitlA));
			contentTable.addCell(sTitleCell);
			sTitleCell.setPhrase(new Phrase("扣款數", fontColTitlA));
			contentTable.addCell(sTitleCell);
			sTitleCell.setPhrase(new Phrase("實發數", fontColTitlA));
			contentTable.addCell(sTitleCell);
			
			PdfPCell cellContent ;
			cellContent= new PdfPCell(new Phrase("" , fontTitle));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置右
			cellContent.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
			
			for (Paprtdtl data : paprtdtlAry){
				cellContent.setPhrase(new Phrase(data.getPayempno()+"\n"+data.getPaycard(), fontColCent));			
				contentTable.addCell(cellContent);
				cellContent.setPhrase(new Phrase(data.getPayempno(), fontColCent));
				cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
				contentTable.addCell(cellContent);
				cellContent.setPhrase(new Phrase(StringUtil.nullToString(data.getPaytitlp()), fontColCent));
				cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
				contentTable.addCell(cellContent);
				cellContent.setPhrase(new Phrase(data.getPaynamec(), fontColCentB));			
				contentTable.addCell(cellContent);
				cellContent.setHorizontalAlignment(Element.ALIGN_CENTER); //垂直置左
				cellContent.setPhrase(new Phrase(String.valueOf(data.getPaydayno()), fontColCent));			
				contentTable.addCell(cellContent);
				cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置右
				cellContent.setPhrase(new Phrase(String.valueOf(data.getPaybase()), fontColCent));			
				contentTable.addCell(cellContent);
				cellContent.setPhrase(new Phrase(data.getPayempno(), fontColCent));			
				contentTable.addCell(cellContent);
				cellContent.setPhrase(new Phrase(data.getPayempno(), fontColCent));			
				contentTable.addCell(cellContent);
				cellContent.setPhrase(new Phrase(data.getPayempno(), fontColCent));			
				contentTable.addCell(cellContent);
				cellContent.setPhrase(new Phrase(data.getPayempno(), fontColCent));			
				contentTable.addCell(cellContent);
				cellContent.setPhrase(new Phrase(data.getPayempno(), fontColCent));			
				contentTable.addCell(cellContent);
				cellContent.setPhrase(new Phrase(data.getPayempno(), fontColCent));			
				contentTable.addCell(cellContent);
				cellContent.setPhrase(new Phrase(data.getPayempno(), fontColCent));			
				contentTable.addCell(cellContent);
				cellContent.setPhrase(new Phrase(data.getPayempno(), fontColCent));			
				contentTable.addCell(cellContent);
				cellContent.setPhrase(new Phrase(data.getPayempno(), fontColCent));			
				contentTable.addCell(cellContent);
				cellContent.setPhrase(new Phrase(data.getPayempno(), fontColCent));			
				contentTable.addCell(cellContent);
				cellContent.setPhrase(new Phrase(data.getPayempno(), fontColCent));			
				contentTable.addCell(cellContent);
				cellContent.setPhrase(new Phrase(data.getPayempno(), fontColCent));			
				contentTable.addCell(cellContent);				
			}
			document.add(mTable); //加入標題Table
			document.add(contentTable); //加入標題Table

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ( document !=null ) document.close();
			if ( writer !=null ) writer.close();
		}
				
	}
	
	//數字轉千分位
	public String valueToThousands (Object value){
		NumberFormat fm = NumberFormat.getNumberInstance();  //千分位
		String result = "無金額";
		try{
			result = "$" + fm.format(Integer.parseInt(value.toString()));
	    } catch(NumberFormatException e){
	    } catch (ClassCastException e) {
	    } catch (Exception e) {
//	    	System.out.println(e);
	    }
		return result;
	}
	
	/*//刪除PDF
	public void removeBePDF(String oldBeid, String newBeid) {
		String type = oldBeid.substring(0, 1);
		String year = oldBeid.substring(1, 5);
		
		String oldDir = (global.getBeFilePath()+"/apply/" + year + "/" + type + "/" + oldBeid + "/").replace("\\", "/");	
		String pdfFileName = "smb:" + (oldDir + oldBeid +".pdf").replace("\\", "/");
		String newDir = (global.getBeFilePath()+"/apply/" + newBeid.substring(1, 5) + "/" + newBeid.substring(0,1) + "/" + newBeid + "/").replace("\\", "/");
		try {		
			//刪除舊有PDF檔案
			SmbFile mealfile = new SmbFile(pdfFileName, global.getSmbSuth() );
			if (mealfile.exists()) {
				mealfile.delete();
			}
			//搬移檔案資料
			SmbFile oldfile = new SmbFile("smb:" + oldDir, global.getSmbSuth() );
			SmbFile newfile = new SmbFile("smb:" + newDir, global.getSmbSuth() );
			if(newfile.exists()) {
				newfile.delete(); //如有新資料夾則刪除
			}
			oldfile.renameTo( newfile );
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (SmbException e) {
			e.printStackTrace();
		}
	}*/
	
	//null轉換為空白
	public static String nullToString( Object oldStr )
	{
		String newStr = "";
		try
		{
			newStr = ( oldStr == null ) ? "" : oldStr.toString().replaceAll( "　" , "" ).trim();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}

		return newStr;
	}
	
	
}
