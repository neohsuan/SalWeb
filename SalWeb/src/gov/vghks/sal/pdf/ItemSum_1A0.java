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
//發薪受款者明細表_高榮
public class ItemSum_1A0 {
	Logger log = Logger.getLogger(SalNTDAO.class);  //呼叫log4j
	String ttfDir = "font\\VGHKH_KAI.TTF";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat signDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	NumberFormat fm = NumberFormat.getNumberInstance();  //千分位
	public ItemSum_1A0() {
		
	}

	public ItemSum_1A0(String strDir,String pdfFileName, ArrayList<Paprtdtl> paprtdtlAry ,String hid) throws Exception {
		String me = getClass().getName() + ".ItemSum_1A0()";
		Document document = null;
		PdfWriter writer = null;
		Paprtdtl prtobj=null;	//第1筆
		Paprtdtl nprtobj=null; //沒有銀行帳號,在第2筆
		Hospitals hospInfo = Hospitals.getOneHosp(hid);		
		FileUtil fileUtil = new FileUtil();
		String tmp="";
		int pay=0;
		int gedp=0;
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
			
			String titleName[]={"給付事項","受款者","金額",""};
			int pdfCol = 4; // 內文有幾欄
			float[] floatCol = {0.2f, 0.25f, 0.18f,0.37f}; //欄位比例
			
			float[] sCol = {0.5f, 0.5f}; //欄位比例
			
			writer = PdfWriter.getInstance(document, new SmbFileOutputStream(payfile));
			document.open();			
			PdfContentByte cb = writer.getDirectContent();
			
			//中文設定，載入字型
			// 載入難字字型			
    		BaseFont bfChinese = BaseFont.createFont(ttfDir, BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);
    		// 載入難字字型
    		FontFactory.register(ttfDir);
			Font fontTitle = new Font( bfChinese, 18, Font.BOLD ); //標題字型設定，size為18
			Font font14 = new Font( bfChinese, 14, Font.NORMAL ); //字型設定，size為16
			Font font12 = new Font( bfChinese, 12, Font.NORMAL ); //字型設定，size為12
			Font font10 = new Font( bfChinese, 10, Font.NORMAL ); //字型設定，size為10
			Font font8 = new Font( bfChinese, 8, Font.NORMAL ); //字型設定，size為8
			
			//背景色彩
			BaseColor baseColor=new BaseColor(221,221,221); 
			//加入標題
			//建立內容表格 並設定其欄位數
			PdfPTable contentTable = new PdfPTable(pdfCol);
			contentTable.setHorizontalAlignment(Element.ALIGN_LEFT);
			//設定table的寬度
			contentTable.setWidthPercentage(100f);
			contentTable.setSpacingBefore(0f);//前間距
			contentTable.setSpacingAfter(0f);//後間距
			//設定預設的cell高度
			contentTable.getDefaultCell().setFixedHeight(30f);
			contentTable.setWidths(floatCol);
		
			
			//Cell設定			
			PdfPCell mTitleCell= new PdfPCell() ;
			//第1行
			mTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			mTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
			mTitleCell.setColspan(3);
			mTitleCell.setFixedHeight(25f);
			mTitleCell.setBorder(Rectangle.NO_BORDER);
			tmp=hospInfo.nmFull;
			mTitleCell.setPhrase( new Phrase(tmp , fontTitle));
			contentTable.addCell(mTitleCell);
			
			tmp="";
			mTitleCell= new PdfPCell(new Phrase(tmp , font12));
			mTitleCell.setColspan(1);
			mTitleCell.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(mTitleCell);
			//第2行
			tmp="發薪受款者明細表";
			mTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置右
			mTitleCell.setColspan(1);
			mTitleCell.setFixedHeight(20f);
			mTitleCell= new PdfPCell(new Phrase(tmp , font12));
			mTitleCell.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(mTitleCell);
			//有帳號
			prtobj=paprtdtlAry.get(0);	
			//沒帳號
			if (paprtdtlAry.size()==2){
				nprtobj=paprtdtlAry.get(1);	
			}
			String dateString = prtobj.getPaybank().toString();
			tmp=Integer.parseInt(dateString.substring(0,4))-1911+"年"+dateString.substring(5, 7)+"月";
			mTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER); //垂直置中
			mTitleCell= new PdfPCell(new Phrase( tmp , font12));
			mTitleCell.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(mTitleCell);
			
			String strprttm=signDateFormat.format(new Date()) ;
			tmp="印表日期:"+strprttm ;
			mTitleCell.setPhrase( new Phrase( tmp , font8) );			
			mTitleCell.setColspan(1);
			mTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			mTitleCell.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(mTitleCell);
			
			tmp="";
			mTitleCell= new PdfPCell(new Phrase(tmp , font12));
			mTitleCell.setColspan(1);
			mTitleCell.setBorder(Rectangle.NO_BORDER);
			contentTable.addCell(mTitleCell);
			
			//設定table的寬度
			//設定預設的cell高度為20f
			contentTable.getDefaultCell().setFixedHeight(20f);
			
			//標題
			PdfPCell sTitleCell = new PdfPCell();
			sTitleCell.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			sTitleCell.setVerticalAlignment(Element.ALIGN_BOTTOM); //水平置中
			sTitleCell.setFixedHeight(30);
			
			for (int i=0;i<titleName.length;i++){
				sTitleCell.setPhrase(new Phrase( titleName[i], font12));
				if (i < titleName.length-1){
					sTitleCell.setBorder(Rectangle.BOTTOM );
				}else
					sTitleCell.setBorder(Rectangle.NO_BORDER);
				contentTable.addCell(sTitleCell);
			}
			//內文
			PdfPCell cellContent = new PdfPCell();
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
			cellContent.setFixedHeight(20);
			cellContent.setBorder(Rectangle.NO_BORDER);
			//第1行
			tmp = "員工薪津";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "中國信託銀行東高雄分行";
			cellContent.setPhrase(new Phrase( tmp , font8));
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPay());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			cellContent.setVerticalAlignment(Element.ALIGN_TOP); //水平置中
			cellContent.setFixedHeight(30f);
			contentTable.addCell(cellContent);
			//扣除沒帳號
			if (nprtobj != null){
				pay=prtobj.getPay()- nprtobj.getPay();
				tmp = "-" + fm.format(nprtobj.getPay())+"="+fm.format( pay)+"\n林聖樺(玉山銀行)";
			}else{
				pay=prtobj.getPay();
				tmp = "" ;
			}
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			contentTable.addCell(cellContent);
						
			//第2行
			tmp = "國宅貸款";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setFixedHeight(20f);
			cellContent.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "第一商業銀行";
			cellContent.setPhrase(new Phrase( tmp , font8));
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPayhloa4());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			//第3行
			tmp = "公保費";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setFixedHeight(20f);
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaygeins());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中
			contentTable.addCell(cellContent);
			
			//第4行
			tmp = "健保費";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaydpins());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			//第5行
			tmp = "勞保費";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaylbins());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			contentTable.addCell(cellContent);

			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			//第6行
			tmp = "補充保險費";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPayother1());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			//第7行
			tmp = "自繳勞退";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaydloan());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			//第8行
			tmp = "退撫離職金";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPayretir());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			//第9行
			tmp = "所得稅";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaytax());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			//第10行
			tmp = "房屋租金";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaymhous());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			//第11行
			tmp = "醫療互助金";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "高榮作業基金本院醫糾金402專戶\n帳號:439350-004615";
			cellContent.setPhrase(new Phrase( tmp , font8));
			cellContent.setFixedHeight(30);
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaynspfd());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			contentTable.addCell(cellContent);

			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			//第12行
			tmp = "代收停車費";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setFixedHeight(20);
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaypark1());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			//第13行
			tmp = "公共設施使用費";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaygym());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			contentTable.addCell(cellContent);

			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			//第14行
			tmp = "清償債款";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaylien());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			contentTable.addCell(cellContent);
			
			//薪資-公教儲金
			PdfPTable sTable = new PdfPTable(2);
			//設定table的寬度
			sTable.setWidthPercentage(80f);
			sTable.setSpacingBefore(0f);//前間距
			sTable.setSpacingAfter(0f);//後間距
			//各欄寬度
			sTable.setWidths(sCol);
			
			PdfPCell sCell = new PdfPCell();
			sCell.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直
			sCell.setVerticalAlignment(Element.ALIGN_MIDDLE); //水平置中				
			sCell.setBorder( Rectangle.NO_BORDER);	//框線
			
			tmp="薪　　資:";
			sCell.setPhrase(new Phrase( tmp, font12));
			sTable.addCell(sCell);
			tmp="$" +fm.format(prtobj.getPay()-prtobj.getPaygedp());
			sCell.setPhrase(new Phrase( tmp, font12));
			sTable.addCell(sCell);
			
			tmp="公教儲金:";
			sCell.setPhrase(new Phrase( tmp, font12));
			sCell.setBorder(Rectangle.BOTTOM);
			sTable.addCell(sCell);
			tmp="$" +fm.format(prtobj.getPaygedp());
			sCell.setPhrase(new Phrase( tmp, font12));
			sCell.setBorder(Rectangle.BOTTOM);
			sTable.addCell(sCell);
			//傳票支付的錢
			tmp="";
			sCell.setPhrase(new Phrase( tmp, font12));
			sCell.setBorder(Rectangle.NO_BORDER);
			sTable.addCell(sCell);
			tmp="$" +fm.format( prtobj.getPay() );
			sCell.setPhrase(new Phrase( tmp, font12));
			sCell.setBorder(Rectangle.NO_BORDER);
			sTable.addCell(sCell);
			//放入cell
			cellContent.addElement(sTable);
			cellContent.setRowspan(5);
			contentTable.addCell(cellContent);
			
			//第15行
			tmp = "餐費";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setRowspan(1);
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaymeal());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置右
			contentTable.addCell(cellContent);
			
//			tmp = "";
//			cellContent.setPhrase(new Phrase( tmp , font12));
//			contentTable.addCell(cellContent);

			//第16行
			tmp = "卡費";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaycardf());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置右
			contentTable.addCell(cellContent);
			
//			tmp = "";
//			cellContent.setPhrase(new Phrase( tmp , font12));
//			contentTable.addCell(cellContent);

			//第17行
			tmp = "醫療糾紛";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPayother3());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置右
			contentTable.addCell(cellContent);
			
//			tmp = "";
//			cellContent.setPhrase(new Phrase( tmp , font12));
//			contentTable.addCell(cellContent);
			
			//第18行
			tmp = "惠民捐款";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPayagen1());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			contentTable.addCell(cellContent);
			
//			tmp = "";
//			cellContent.setPhrase(new Phrase( tmp , font12));
//			contentTable.addCell(cellContent);
			//第19行
			tmp = "體適能中心清潔費";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPayvetd1());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			//第20行
			tmp = "應發總金額(一)";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getNt1());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			BaseColor old_bgColor =cellContent.getBackgroundColor();
			cellContent.setBackgroundColor(baseColor);
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setBackgroundColor(old_bgColor);
			contentTable.addCell(cellContent);
			//第21行
			tmp = "十樓眷舍管理費";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			cellContent.setBackgroundColor(old_bgColor);
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaydorm1());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			//第22行
			tmp = "五樓眷舍管理費";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaydorm2());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置右
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			//第23行
			tmp = "單身眷舍管理費";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPaydorm3());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置右
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			//第24行
			tmp = "PGY眷舍管理費";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getPayother2());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置右
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			//第25行
			tmp = "應發總金額(二)";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_LEFT); //垂直置左		
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			contentTable.addCell(cellContent);
			
			tmp = "$" +fm.format( prtobj.getNt2());
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setHorizontalAlignment(Element.ALIGN_RIGHT); //垂直置右
			cellContent.setBackgroundColor(baseColor);
			contentTable.addCell(cellContent);
			
			tmp = "";
			cellContent.setPhrase(new Phrase( tmp , font12));
			cellContent.setBackgroundColor(old_bgColor);
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
