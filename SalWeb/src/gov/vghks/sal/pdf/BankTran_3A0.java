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
//銀行轉帳名冊_聯醫
public class BankTran_3A0 {
	Logger log = Logger.getLogger(SalNTDAO.class);  //呼叫log4j
	String ttfDir = "font\\VGHKH_KAI.TTF";
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat signDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	NumberFormat fm = NumberFormat.getNumberInstance();  //千分位
	public BankTran_3A0() {
		
	}
	//itm,PrtNm=>0:薪津,1:公儲
	public BankTran_3A0(String strDir,String pdfFileName,int itm,String PrtNm,ArrayList<Paprtdtl> paprtdtlAry ,String hid) throws Exception {
		String me = getClass().getName() + ".BankTran_3A0()";
		Document document = null;
		PdfWriter writer = null;
		Paprtdtl prtobj=null;
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		
		FileUtil fileUtil = new FileUtil();
		String tmp="";
		int intPer=0;//人數合計
		int intPage=0;
		int intCellSum=0;
		try {
			//新增資料夾路徑
			if (!FileUtil.FileToSmbFile(strDir,hid).exists()){
				fileUtil.FileToSmbFile(strDir,hid).mkdirs();
			}	
			//建立PDF檔案			
			SmbFile payfile = fileUtil.FileToSmbFile(pdfFileName,hid);
			
			// 步驟1 - 設定產生的PDF文件大小 橫式
			document = new Document(PageSize.A4, 30,25,25,20);//left, right, top, bottom
			
			String titleName[]={"卡號","姓名","職稱","身分證字號","受款人帳號","轉帳金額"};
			int pdfCol = 6; // 內文有幾欄
			float[] floatCol = {0.1f, 0.12f,0.26f, 0.17f, 0.19f, 0.16f}; //欄位比例
			
			writer = PdfWriter.getInstance(document, new SmbFileOutputStream(payfile));
			document.open();			
			PdfContentByte cb = writer.getDirectContent();
			
			//中文設定，載入字型
			// 載入難字字型			
    		BaseFont bfChinese = BaseFont.createFont(ttfDir, BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);
    		// 載入難字字型
    		FontFactory.register(ttfDir);
			Font fontTitle = new Font( bfChinese, 12, Font.BOLD ); //標題字型設定，size為12
			Font fontTitle10 = new Font( bfChinese, 10, Font.NORMAL ); //標題字型設定，size為10
			Font fontColCent = new Font( bfChinese, 9, Font.NORMAL ); //字型設定，size為8
			
			prtobj=paprtdtlAry.get(0);
			String dateString = prtobj.getPaybank().toString();
			
			//加入標題
			//建立內容表格 並設定其欄位數
			PdfPTable contentTable = new PdfPTable(pdfCol);
			contentTable.setHorizontalAlignment(Element.ALIGN_LEFT);
			//設定table的寬度
			contentTable.setWidthPercentage(75f);
			contentTable.setSpacingBefore(0f);//前間距
			contentTable.setSpacingAfter(0f);//後間距
			//設定預設的cell高度為20f
			contentTable.getDefaultCell().setFixedHeight(20f);
			contentTable.setWidths(floatCol);
			
			//Cell設定			
			PdfPCell mTitleCell= new PdfPCell(); ;
			//第1行
			if (itm == 0)
				tmp=hospInfo.nmFull+"\n員工薪資清冊";
			else if (itm == 1)
				tmp=hospInfo.nmFull+"\n玉山優惠儲蓄存款明細";
			mTitleCell.setPhrase( new Phrase(tmp , fontTitle));
			mTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER); //垂直置中
			mTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
			mTitleCell.setBorder(Rectangle.NO_BORDER);
			mTitleCell.setColspan(6);
			mTitleCell.setFixedHeight(35f);
			contentTable.addCell(mTitleCell);
			
			//第2行
			String strprttm=sdf.format(new Date()) ;
			tmp="列印日期:"+strprttm;			
			mTitleCell.setPhrase(new Phrase(tmp, fontTitle10));
			mTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置右
			mTitleCell.setFixedHeight(14f);
			mTitleCell.setColspan(6);
			contentTable.addCell(mTitleCell);
			//第3行
			tmp="資料年月:"+prtobj.getPaybank().toString();			
			mTitleCell.setPhrase(new Phrase(tmp, fontTitle10));
			mTitleCell.setColspan(6);
			contentTable.addCell(mTitleCell);
			
			//設定table的寬度
			//設定預設的cell高度為20f
			contentTable.getDefaultCell().setFixedHeight(20f);
			
			//標題
			PdfPCell sTitleCell ;
			sTitleCell= new PdfPCell();
			sTitleCell.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			sTitleCell.setVerticalAlignment(Element.ALIGN_BOTTOM); //水平置中
			sTitleCell.setFixedHeight(17f);
			for (int i=0;i<titleName.length;i++){
				sTitleCell.setPhrase(new Phrase( titleName[i], fontTitle10));
				sTitleCell.setBorder(Rectangle.BOTTOM );
				contentTable.addCell(sTitleCell);
			}
			
			PdfPCell cellContent ;
			cellContent= new PdfPCell();
			cellContent.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setFixedHeight(13f);
			cellContent.setBorder(Rectangle.NO_BORDER);
			for (Paprtdtl data : paprtdtlAry){
				//人數
				intPer++;
				//金額
				intCellSum += data.getPayamt();
				//卡號
				tmp=String.valueOf( data.getPaycard());
				cellContent.setPhrase(new Phrase( tmp , fontColCent));
				cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
				contentTable.addCell(cellContent);
				//姓名 
				tmp = String.valueOf( data.getPaynamec() ) ;
				cellContent.setPhrase(new Phrase(tmp, fontColCent));			
				contentTable.addCell(cellContent);
				//職稱 
				tmp = String.valueOf( data.getPaytitlp() ) ;
				cellContent.setPhrase(new Phrase(tmp, fontColCent));			
				contentTable.addCell(cellContent);
				//身分證
				tmp=String.valueOf( data.getPayidno());
				cellContent.setPhrase(new Phrase( tmp , fontColCent));			
				contentTable.addCell(cellContent);
				//受款人帳號
				tmp = String.valueOf( data.getPayacno() );
				cellContent.setPhrase(new Phrase( tmp , fontColCent));			
				contentTable.addCell(cellContent);
				//轉帳金額payamt
				tmp = "$" +fm.format( data.getPayamt());
				cellContent.setPhrase(new Phrase(tmp, fontColCent));
				cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
				contentTable.addCell(cellContent);
			}
			//每頁頂端列,每頁標題固定
			contentTable.setHeaderRows(4);
			
			//建立各項小計 並設定其欄位數
			PdfPTable sumTable = new PdfPTable(pdfCol);
			//表格靠左
			sumTable.setHorizontalAlignment(Element.ALIGN_LEFT);
			//設定table的寬度
			sumTable.setWidthPercentage(75f);
			//各欄寬度
			sumTable.setWidths(floatCol);
			//第1行
			PdfPCell sumCell= new PdfPCell() ;
			sumCell.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置中
			sumCell.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
			sumCell.setBorder(Rectangle.TOP );
			tmp = "總筆數:"+fm.format(intPer);
			sumCell.setPhrase(new Phrase( tmp , fontColCent));
			sumCell.setColspan(4);
			sumTable.addCell(sumCell);
			
			tmp = "總金額:"+"$" +fm.format( intCellSum );
			sumCell.setPhrase(new Phrase( tmp , fontColCent));
			sumCell.setHorizontalAlignment(Element.ALIGN_RIGHT); 
			sumCell.setColspan(2);
			sumTable.addCell(sumCell);
			//第2行
			tmp = "製表:";
			sumCell.setPhrase(new Phrase( tmp , fontColCent));
			sumCell.setBorder(Rectangle.NO_BORDER);
			sumCell.setVerticalAlignment(Element.ALIGN_BOTTOM); //底部對齊
			sumCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			sumCell.setColspan(3);
			sumCell.setFixedHeight(40f);
			sumTable.addCell(sumCell);
			tmp = "總務室:";
			sumCell.setPhrase(new Phrase( tmp , fontColCent));
			sumCell.setColspan(3);
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
