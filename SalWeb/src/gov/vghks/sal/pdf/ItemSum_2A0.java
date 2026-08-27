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
//發薪受款者明細表_屏榮
public class ItemSum_2A0 {
	Logger log = Logger.getLogger(SalNTDAO.class);  //呼叫log4j
	String ttfDir = "font\\VGHKH_KAI.TTF";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat signDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	NumberFormat fm = NumberFormat.getNumberInstance();  //千分位
	public ItemSum_2A0() {
		
	}

	public ItemSum_2A0(String strDir,String pdfFileName, ArrayList<Paprtdtl> paprtdtlAry ,String hid) throws Exception {
		String me = getClass().getName() + ".ItemSum_2A0()";
		Document document = null;
		PdfWriter writer = null;
		Paprtdtl prtobj=null;
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		
		FileUtil fileUtil = new FileUtil();
		String tmp="";
		try {
			//新增資料夾路徑
			if (!FileUtil.FileToSmbFile(strDir,hid).exists()){
				fileUtil.FileToSmbFile(strDir,hid).mkdirs();
			}	
			//建立PDF檔案			
			SmbFile payfile = fileUtil.FileToSmbFile(pdfFileName,hid);
			
			// 步驟1 - 設定產生的PDF文件大小 直式
			document = new Document(PageSize.A4, 30,25,25,20);//left, right, top, bottom
			String signName[]={"製表","人事室","主計室","機關首長及其授權人"};
			int pdfSCol = 4; // 簽名處有幾欄
			float[] floatColSign = {0.25f, 0.25f, 0.25f, 0.25f}; //欄位比例
			
			String titleName[]={"給付事項","受款者","金額"};
			int pdfCol = 3; // 內文有幾欄
			float[] floatCol = {0.3f, 0.45f, 0.25f}; //欄位比例
			
			writer = PdfWriter.getInstance(document, new SmbFileOutputStream(payfile));
			document.open();			
			PdfContentByte cb = writer.getDirectContent();
			
			//中文設定，載入字型
			// 載入難字字型			
    		BaseFont bfChinese = BaseFont.createFont(ttfDir, BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);
    		// 載入難字字型
    		FontFactory.register(ttfDir);
			Font fontTitle = new Font( bfChinese, 18, Font.BOLD ); //標題字型設定，size為18
			Font font14 = new Font( bfChinese, 14, Font.NORMAL ); //標題字型設定，size為16
			Font font12 = new Font( bfChinese, 12, Font.NORMAL ); //標題字型設定，size為12
			Font font10 = new Font( bfChinese, 10, Font.NORMAL ); //標題字型設定，size為10
			Font font8 = new Font( bfChinese, 8, Font.NORMAL ); //字型設定，size為8
			
			//背景色彩
			BaseColor baseColor=new BaseColor(221,221,221); 
			//加入標題
			//建立內容表格 並設定其欄位數
			PdfPTable contentTable = new PdfPTable(pdfCol);
			contentTable.setHorizontalAlignment(Element.ALIGN_LEFT);
			//設定table的寬度
			contentTable.setWidthPercentage(65f);
			contentTable.setSpacingBefore(0f);//前間距
			contentTable.setSpacingAfter(0f);//後間距
			//設定預設的cell高度
			contentTable.getDefaultCell().setFixedHeight(30f);
			contentTable.setWidths(floatCol);
			
			//Cell設定			
			PdfPCell mTitleCell ;
			//第1行
			mTitleCell= new PdfPCell();
			String strMT_hosptial=hospInfo.nmFull;
			mTitleCell.setPhrase( new Phrase(strMT_hosptial , fontTitle));
			mTitleCell.setBorder(0);
			mTitleCell.setColspan(3);
			mTitleCell.setFixedHeight(25f);
			mTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER); 
			contentTable.addCell(mTitleCell);
			//第2行
			mTitleCell= new PdfPCell(new Phrase("發薪受款者明細表" , font12));
			mTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置中
			mTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
			mTitleCell.setBorder(Rectangle.NO_BORDER);
			mTitleCell.setColspan(1);
			mTitleCell.setFixedHeight(20f);
			contentTable.addCell(mTitleCell);
			
			prtobj=paprtdtlAry.get(0);
			String dateString = prtobj.getPaybank().toString();
			tmp=Integer.parseInt(dateString.substring(0,4))-1911+"年"+dateString.substring(5, 7)+"月";
			mTitleCell= new PdfPCell(new Phrase( tmp , font12));
			mTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER); //垂直置中
			mTitleCell.setBorder( Rectangle.NO_BORDER);
			contentTable.addCell(mTitleCell);
			
			mTitleCell= new PdfPCell();
			String strprttm=signDateFormat.format(new Date()) ;
			mTitleCell.setPhrase( new Phrase("列印日期:"+strprttm , font8) );			
			mTitleCell.setBorder(Rectangle.NO_BORDER);
			mTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT); 
			contentTable.addCell(mTitleCell);
			
			//設定table的寬度
			//設定預設的cell高度為20f
			contentTable.getDefaultCell().setFixedHeight(20f);
			
			//標題
			PdfPCell sTitleCell ;
			sTitleCell= new PdfPCell();
			sTitleCell.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			sTitleCell.setVerticalAlignment(Element.ALIGN_BOTTOM); //水平置中
			sTitleCell.setFixedHeight(30);
			
			for (int i=0;i<titleName.length;i++){
				sTitleCell.setPhrase(new Phrase( titleName[i], font12));
				sTitleCell.setBorder(Rectangle.BOTTOM );
				contentTable.addCell(sTitleCell);
			}
			//內文
			PdfPCell cellContent ;
			cellContent= new PdfPCell();
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
			cellContent.setFixedHeight(20);
			
			//第1行
			tmp = "員工薪津";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "台新國際商業銀行";
			cellContent.setPhrase(new Phrase( tmp , font8));
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPay());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			//第2行
			tmp = "公保費";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaygeins());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			//第3行
			tmp = "健保費";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaydpins());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);

			//第4行
			tmp = "勞保費";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaylbins());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);

			//第5行
			tmp = "補充保險費";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPayother1());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			//第6行
			tmp = "勞工退休金";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaydloan());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			//第7行
			tmp = "退撫離職金";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPayretir());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			//第8行
			tmp = "所得稅";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "財政部南區國稅局";
			cellContent.setPhrase(new Phrase( tmp , font8));
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaytax());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			//第9行
			tmp = "房屋租金";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaymhous());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			//第10行
			tmp = "醫療互助金";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "屏榮作業基金醫糾互助金402專戶";
			cellContent.setPhrase(new Phrase( tmp , font8));
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaynspfd());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);

			//第11行
			tmp = "停車費";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaypark1());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);	
			//第12行
			tmp = "遺孤認養金";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "屏東縣榮民服務處";
			cellContent.setPhrase(new Phrase( tmp , font8));
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaygym());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);

			//第13行
			tmp = "弱勢醫療捐款";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPayagen1());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			//第14行
			tmp = "清償債款";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaylien());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			//第15行
			tmp = "宿舍管理費";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaydorm1());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			

			//第16行
			tmp = "宿舍電費";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaydorm2());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			

			//第17行
			tmp = "醫療救助捐款";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaydorm3());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			//第18行
			tmp = "高榮代收代付";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPayother2());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			//2026-08-12新增第19行長照機構責任險
			tmp = "長照機構責任險";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPayvetd1());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			//第20行
			tmp = "應發總金額";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getNt1());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			BaseColor old_bgColor =cellContent.getBackgroundColor();
			cellContent.setBackgroundColor(baseColor);
			cellContent.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(cellContent);
			
			
			//蓋章欄
			PdfPTable signTable = new PdfPTable(pdfSCol);
			//設定table的寬度
			signTable.setWidthPercentage(100f);
			//前間距
			signTable.setSpacingBefore(40f);
			//設定預設的cell高度
			signTable.getDefaultCell().setFixedHeight(40f);
			//各欄寬度
			signTable.setWidths(floatColSign);
			//簽名
			PdfPCell signCell ;
			signCell= new PdfPCell();
			signCell.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			signCell.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中				
			signCell.setBorder( Rectangle.NO_BORDER);	//框線
			for (int i=0;i<signName.length;i++){
				signCell.setPhrase(new Phrase( signName[i], font14));
				signTable.addCell(signCell);
			}
			
			document.add(contentTable); //加入內容Table
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
