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
//薪津異動明細表_聯醫
public class Chgdtl_2A0 {
	Logger log = Logger.getLogger(SalNTDAO.class);  //呼叫log4j
	String ttfDir = "font\\VGHKH_KAI.TTF";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat signDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	NumberFormat fm = NumberFormat.getNumberInstance();  //千分位
	public Chgdtl_2A0() {
		
	}

	public Chgdtl_2A0(String strDir,String pdfFileName, ArrayList<Paprtdtl> paprtdtlAry ,String hid) throws Exception {
		String me = getClass().getName() + ".Chgdtl_2A0()";
		Document document = null;
		PdfWriter writer = null;
		Paprtdtl prtobj=null;
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		
		FileUtil fileUtil = new FileUtil();
		String tmp="";
		int intPer=0;//人數合計
		int intCellSum_1A=0,intCellSum_1B=0,intCellSum_1C=0,intCellSum_2A=0,intCellSum_2B=0,intCellSum_2C=0;
		int intCellSum_3A=0,intCellSum_3B=0,intCellSum_3C=0,intCellSum_4A=0,intCellSum_4B=0,intCellSum_4C=0;
		try {
			//新增資料夾路徑
			if (!FileUtil.FileToSmbFile(strDir,hid).exists()){
				fileUtil.FileToSmbFile(strDir,hid).mkdirs();
			}	
			//建立PDF檔案			
			SmbFile payfile = fileUtil.FileToSmbFile(pdfFileName,hid);
			
			// 步驟1 - 設定產生的PDF文件大小 橫式
			document = new Document(PageSize.A4.rotate(), 30,25,25,20);//left, right, top, bottom
			
			String titleName[]={"職號\n卡號","階級","職稱","姓名","支給\n日數","薪俸","","專業加給","","主管加給","","院區加給","","異動原因","生效日期"};
			int pdfCol = 15; // 內文有幾欄
			float[] floatCol = {0.05f, 0.07f, 0.095f, 0.07f, 0.035f, 0.075f, 0.075f, 0.075f, 0.075f, 0.075f, 0.075f, 0.075f, 0.075f, 0.08f, 0.07f}; //欄位比例
			
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
			mTitleCell.setColspan(8);
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
			mTitleCell= new PdfPCell(new Phrase("薪津異動明細表" , fontTitle14));
			mTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置中
			mTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
			mTitleCell.setBorder(Rectangle.NO_BORDER);
			mTitleCell.setColspan(8);
			mTitleCell.setFixedHeight(20f);
			contentTable.addCell(mTitleCell);
			
			prtobj=paprtdtlAry.get(0);
			String dateString = prtobj.getPaybank().toString();
			tmp="發薪年月:"+dateString.substring(0,4)+"年"+dateString.substring(5, 7)+"月";
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
			
			//表格標題
			PdfPCell sTitleCell ;
			//第1行
			for (int i=0;i<titleName.length;i++){
				
				sTitleCell= new PdfPCell();
				sTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER); //垂直置中
				sTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
				if (i==4)
					sTitleCell.setPhrase(new Phrase( titleName[i], fontColCent));
				else
					sTitleCell.setPhrase(new Phrase( titleName[i], fontColTitlA));
				
				if (String.valueOf(i).matches("0|1|2|3|4|13|14"))
					sTitleCell.setRowspan(3);
				else if (String.valueOf(i).matches("5|7|9|11")){
					sTitleCell.setPhrase(new Phrase( titleName[i], fontColTitlA));
					sTitleCell.setColspan(2);
					i++;
				}	
				else
					sTitleCell.setPhrase(new Phrase( titleName[i], fontColTitlA));
				
				contentTable.addCell(sTitleCell);
			}
			//第2行
			for (int i=0;i<4;i++){
				
				sTitleCell= new PdfPCell(new Phrase( "本月", fontColTitlA));
				sTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER); //垂直置中
				sTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
				contentTable.addCell(sTitleCell);
				
				sTitleCell= new PdfPCell(new Phrase( "補發項", fontColTitlA));
				sTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER); //垂直置中
				contentTable.addCell(sTitleCell);
			}
			//第3行
			for (int i=0;i<4;i++){
				
				sTitleCell= new PdfPCell(new Phrase( "上月", fontColTitlA));
				sTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER); //垂直置中
				sTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
				contentTable.addCell(sTitleCell);
				
				sTitleCell.setPhrase(new Phrase( "", fontColTitlA));
				contentTable.addCell(sTitleCell);
			}
			//內文
			PdfPCell cellContent ;
			cellContent= new PdfPCell();
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置右
			cellContent.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
			cellContent.setFixedHeight(23);
			for (Paprtdtl data : paprtdtlAry){
				//人數
				intPer++;
				//薪俸
				if (data.getPaybase() != data.getPayobase()){
					intCellSum_1A += data.getPaybase();
					intCellSum_1B += data.getPayobase();
				}
				intCellSum_1C += data.getPayrbase();
				//專業加給
				if (data.getPayspsu() != data.getPayospsu()){
					intCellSum_2A += data.getPayspsu();
					intCellSum_2B += data.getPayospsu();
				}
				intCellSum_2C += data.getPayrspsu();
				//主管加給
				if (data.getPaychalw() != data.getPayochalw()){
					intCellSum_3A += data.getPaychalw();
					intCellSum_3B += data.getPayochalw();
				}
				intCellSum_3C += data.getPayrchalw();
				//院區加給PAYSTBNT
				if (data.getPaystbnt() != data.getPayostbn()){
					intCellSum_4A += data.getPaystbnt();
					intCellSum_4B += data.getPayostbn();
				}
				intCellSum_4C += data.getPayrstbn();
				
				//職號,卡號
				cellContent.setPhrase(new Phrase(data.getPayempno()+"\n"+data.getPaycard(), fontColCent));
				cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
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
				tmp = String.valueOf( data.getPaydayno() )+"\n"+String.valueOf(data.getLastdayno() );
				cellContent.setHorizontalAlignment(Element.ALIGN_CENTER); //垂直置左
				cellContent.setPhrase(new Phrase(tmp, fontColCent));			
				contentTable.addCell(cellContent);
				//薪俸_本月 上月 0不顯示
				if (data.getPaybase() != data.getPayobase()){
					if (data.getPaybase() == 0) tmp="";
					else tmp = String.valueOf( data.getPaybase() );
					tmp += "\n";
					if (data.getPayobase() == 0) tmp+="";
					else tmp += String.valueOf( data.getPayobase() );
				}	
				else
					tmp = "";
				cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置右
				cellContent.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
				cellContent.setPhrase(new Phrase( tmp , fontColCent));			
				contentTable.addCell(cellContent);
				//薪俸_補發項
				if (data.getPayrbase() == 0) tmp="　";
				else tmp = String.valueOf( data.getPayrbase() )+"\n　";
				cellContent.setPhrase(new Phrase( tmp , fontColCent));
				cellContent.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
				contentTable.addCell(cellContent);
				
				//專業加給_本月 上月
				if (data.getPayspsu() != data.getPayospsu()){
					if (data.getPayspsu() == 0) tmp="";
					else tmp = String.valueOf( data.getPayspsu() );
					tmp += "\n";
					if (data.getPayospsu() == 0) tmp+="";
					else tmp += String.valueOf( data.getPayospsu() );
				}	
				else
					tmp = "";
				cellContent.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
				cellContent.setPhrase(new Phrase(tmp, fontColCent));
				
				contentTable.addCell(cellContent);
				//專業加給_補發項
				if (data.getPayrspsu() == 0) tmp="　";
				else tmp = String.valueOf( data.getPayrspsu() )+"\n　";
				cellContent.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
				cellContent.setPhrase(new Phrase(tmp, fontColCent));			
				contentTable.addCell(cellContent);
				
				//主管加給_本月 上月
				if (data.getPaychalw() != data.getPayochalw()){
					if (data.getPaychalw() == 0) tmp="";
					else tmp = String.valueOf( data.getPaychalw() );
					tmp += "\n";
					if (data.getPayochalw() == 0) tmp+="";
					else tmp += String.valueOf( data.getPayochalw() );
				}	
				else
					tmp = "";
				cellContent.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
				cellContent.setPhrase(new Phrase(tmp, fontColCent));		
				contentTable.addCell(cellContent);
				//主管加給_補發項
				if (data.getPayrchalw() == 0) tmp="　";
				else tmp = String.valueOf( data.getPayrchalw() )+"\n　";
				cellContent.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
				cellContent.setPhrase(new Phrase(tmp, fontColCent));
				contentTable.addCell(cellContent);
				
				//院區加給PAYSTBNT_本月 上月
				if (data.getPaystbnt() != data.getPayostbn()){
					if (data.getPaystbnt() == 0) tmp="";
					else tmp = String.valueOf( data.getPaystbnt() );
					tmp += "\n";
					if (data.getPayostbn() == 0) tmp+="";
					else tmp += String.valueOf( data.getPayostbn() );
				}	
				else
					tmp = "";
				cellContent.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
				cellContent.setPhrase(new Phrase(tmp, fontColCent));			
				contentTable.addCell(cellContent);
				//院區加給_補發項
				if (data.getPayrstbn() == 0) tmp="　";
				else tmp = String.valueOf( data.getPayrstbn() )+"\n　";
				cellContent.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
				cellContent.setPhrase(new Phrase(tmp, fontColCent));		
				contentTable.addCell(cellContent);
				
				//異動原因TRANAME
				tmp = String.valueOf( data.getTraname() );
				cellContent.setBorder(Rectangle.BOX);
				cellContent.setPhrase(new Phrase(tmp, fontColCent));	
				cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
				contentTable.addCell(cellContent);
				
				//生效日期PAEFFDT
				tmp = String.valueOf( data.getPaeffdt().toString() );
				cellContent.setPhrase(new Phrase(tmp, fontColCent));			
				contentTable.addCell(cellContent);					
									
			}
			//每頁頂端列,每頁標題固定5列
			contentTable.setHeaderRows(5);
			
			//建立各項小計 並設定其欄位數
			PdfPTable sumTable = new PdfPTable(pdfCol);			
			//設定table的寬度
			sumTable.setWidthPercentage(100f);
			//設定預設的cell高度
			sumTable.getDefaultCell().setFixedHeight(50f);
			//各欄寬度
			sumTable.setWidths(floatCol);
			
			PdfPCell sumCell ;
			//第1行
			sumCell= new PdfPCell();
			sumCell.setHorizontalAlignment(Element.ALIGN_CENTER); //垂直置中
			sumCell.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
			sumCell.setPhrase(new Phrase("人數合計:"+fm.format(intPer), fontColTitlA));
			sumCell.setColspan(3);
			//sumCell.setFixedHeight(37f);
			sumTable.addCell(sumCell);
			
			sumCell.setPhrase(new Phrase("本月金額\n\n上月金額", fontColTitlA));
			sumCell.setColspan(2);
			sumTable.addCell(sumCell);
			
			sumCell.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置中
			
			//本薪 本月,上月
			tmp = String.valueOf(intCellSum_1A)+"\n\n"+String.valueOf(intCellSum_1B);
			sumCell.setPhrase(new Phrase( tmp , fontColCent));
			sumCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
			sumCell.setColspan(1);
			sumTable.addCell(sumCell);
			//本薪 補發項
			tmp = String.valueOf(intCellSum_1C)+"\n\n　";
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
			sumTable.addCell(sumCell);
			//專業加給 本月,上月
			tmp = String.valueOf(intCellSum_2A)+"\n\n"+String.valueOf(intCellSum_2B);
			sumCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//專業加給 補發項
			tmp = String.valueOf(intCellSum_2C)+"\n\n　";
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
			sumTable.addCell(sumCell);
			//主管加給 本月,上月
			tmp = String.valueOf(intCellSum_3A)+"\n\n"+String.valueOf(intCellSum_3B);
			sumCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//主管加給 補發項
			tmp = String.valueOf(intCellSum_3C)+"\n\n　";
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
			sumTable.addCell(sumCell);
			//院區加給 本月,上月
			tmp = String.valueOf(intCellSum_4A)+"\n\n"+String.valueOf(intCellSum_4B);
			sumCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumTable.addCell(sumCell);
			//院區加給 補發項
			tmp = String.valueOf(intCellSum_4C)+"\n\n　";
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
			sumTable.addCell(sumCell);
			//
			tmp = "　";
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumCell.setColspan(2);
			sumTable.addCell(sumCell);
			//第2行
			tmp = "　";
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
			sumCell.setColspan(3);
			sumCell.setBorder(Rectangle.BOX);
			sumTable.addCell(sumCell);
			tmp = "變動數";
			sumCell.setPhrase(new Phrase( tmp , fontColCent));
			sumCell.setHorizontalAlignment(Element.ALIGN_CENTER); //垂直置中
			sumCell.setColspan(2);
			sumTable.addCell(sumCell);
			//薪俸
			tmp = String.valueOf(intCellSum_1A-intCellSum_1B);
			sumCell.setPhrase(new Phrase( tmp , fontColCent));
			sumCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			sumCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
			sumCell.setColspan(1);
			sumTable.addCell(sumCell);
			tmp = "　";
			sumCell.setPhrase(new Phrase( tmp , fontColCent));
			sumCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
			sumTable.addCell(sumCell);
			//專業加給
			tmp = String.valueOf(intCellSum_2A-intCellSum_2B);
			sumCell.setPhrase(new Phrase( tmp , fontColCent));
			sumCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
			sumTable.addCell(sumCell);
			tmp = "　";
			sumCell.setPhrase(new Phrase( tmp , fontColCent));
			sumCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
			sumTable.addCell(sumCell);
			//主管加給
			tmp = String.valueOf(intCellSum_3A-intCellSum_3B);
			sumCell.setPhrase(new Phrase( tmp , fontColCent));
			sumCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
			sumTable.addCell(sumCell);
			tmp = "　";
			sumCell.setPhrase(new Phrase( tmp , fontColCent));
			sumCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
			sumTable.addCell(sumCell);
			//院區加給
			tmp = String.valueOf(intCellSum_4A-intCellSum_4B);
			sumCell.setPhrase(new Phrase( tmp , fontColCent));
			sumCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
			sumTable.addCell(sumCell);
			tmp = "　";
			sumCell.setPhrase(new Phrase( tmp , fontColCent));
			sumCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
			sumTable.addCell(sumCell);
			tmp = "　";
			sumCell.setPhrase(new Phrase( tmp, fontColCent));
			sumCell.setColspan(2);
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
