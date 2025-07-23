package gov.vghks.sal.util;

import gov.vghks.sal.Globals;
import gov.vghks.sal.Hospitals;
import gov.vghks.common.util.StringUtil;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;
import org.apache.log4j.Logger;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.exceptions.InvalidPdfException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;

public class FileUtil {
	static Logger log=Logger.getLogger(StringUtil.class);  //呼叫log4j
	/**
	 * 將檔案轉換成SmbFile
	 * @param path
	 * @return
	 */
	public static SmbFile FileToSmbFile(String path,String hid){
		SmbFile file = null;
		String login="";
		String pass="";
		try{
			//改用列舉
			//Hospitals hospInfo = Hospitals.getOneHosp(hid);
			login=Globals.gap_id;
			pass =Globals.gap_pwd;
			
			NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("", login, pass);
			
			if(!path.startsWith("smb"))
				path = "smb:" + path.replace("\\", "/");
			
			file = new SmbFile(path, auth);
			if(file.isDirectory()){
				if(path.lastIndexOf("/") != path.length()-1)
					file = new SmbFile(path + "/", auth);
			}
		}catch(Throwable e){
			log.info(e.toString());
			e.printStackTrace();
		}
		return file;
	}
	
	private boolean readF(String sfile,String hid) throws InvalidPdfException{		 
		boolean openok=true;
		try{
			PdfReader reader = new PdfReader(new SmbFileInputStream(FileToSmbFile(sfile,hid)));
		} catch (IOException e) {
			System.out.println("----->IOException--"+e.getMessage());
			openok=false;
		} catch (NullPointerException npe) { // !!
			System.out.println("----->NullPointerException--"+npe.getMessage());
			openok=false;
		} catch (Exception  e1) { // !!
			System.out.println("----->Exception--"+e1.getMessage());
			openok=false;	
		} finally{
			return openok;	
		}
    }
	/**
	 * 合併PDF
	 * @param files (files=\\\\fileserver\\xxx)
	 * @param savepath
	 * @return 
	 * @throws IOException 
	 */
	public boolean mergePDF(String[] files, String savepath,String hid) throws IOException {
		boolean mergeOK = false;
		boolean blnRead=false;
		SmbFileInputStream sStream=null;
		try {
		        com.itextpdf.text.Document document = new com.itextpdf.text.Document ();
		        String dirPath = savepath.substring(0, savepath.lastIndexOf('\\'));
		        if (!FileToSmbFile(dirPath,hid).exists())
		        	FileToSmbFile(dirPath,hid).mkdirs();
		       
		        PdfCopy copy = new PdfCopy(document, new SmbFileOutputStream(FileToSmbFile(savepath,hid)));
		        document.open();
		        for (int i = 0; i < files.length; i++) {
	        		int k=0;
		        	while(!blnRead && k<100){
		        		if (readF(files[i],hid)) blnRead=true;
		        		if (!blnRead){
			        		System.out.println(k++);
			        		try { 
		                      Thread.sleep(3000); 
			        		} catch(InterruptedException e) { 
			                    e.printStackTrace(); 
			                }
		        		}	
		        	}
		        	
		        	PdfReader reader = new PdfReader(new SmbFileInputStream(FileToSmbFile( files[i],hid )	));
		        	if (reader.getFileLength()>0){
		        		int n = reader.getNumberOfPages();
			        	for (int j = 1; j <= n; j++) {
			        		document.newPage();
			        		PdfImportedPage page = copy.getImportedPage(reader, j);
			        		copy.addPage(page);
			        	}
		        	}	
		        }
		        document.close();
		        copy.close();
		        mergeOK = true;
		} catch (IOException e) {
			System.out.println("-----><IOException>--"+e.getMessage());	
			e.printStackTrace();
			throw e;
		} catch (com.itextpdf.text.DocumentException e) {
			// TODO 自動產生 catch 區塊
			e.printStackTrace();
		}
		return mergeOK;
	 }
	
	/**
	 * 合併PDF
	 * @param files (files=smb://xxx)
	 * @param savepath
	 * @return 
	 */
	public boolean mergeSmbPDF(String[] smbfiles, String savepath, String cover, String bookmark, String[] fileNM,String hid) throws IOException{
		boolean mergeOK = false;
		try {
		        Document document = new Document ();
		        String dirPath = savepath.substring(0, savepath.lastIndexOf('\\'));
			    if (!FileToSmbFile(dirPath,hid).exists())
			        FileToSmbFile(dirPath,hid).mkdirs();
				PdfCopy copy = new PdfCopy(document, new SmbFileOutputStream(FileToSmbFile(savepath,hid)));
				document.open();
				
				if (cover !=""){
					PdfReader reader0 = new PdfReader(new SmbFileInputStream(FileToSmbFile(cover,hid)));
					PdfImportedPage page0 = copy.getImportedPage(reader0, 1);
					copy.addPage(page0);
				}
				BaseFont bfChinese = BaseFont.createFont(
						"jsp\\font\\VGHKH_KAI.TTF", BaseFont.IDENTITY_H,
						BaseFont.NOT_EMBEDDED);
				Font textN = new Font(bfChinese, 12, Font.NORMAL);
				int chNum = 1;
				Chapter chapter = null;
		        for (int i = 0; i < smbfiles.length; i++) {
		        	//加書籤
		        	if ("Y".equals(bookmark)){
		        		chapter = new Chapter(new Paragraph(fileNM[i], textN), chNum++);
		        		document.add(chapter);
		        	}
		        	PdfReader reader = new PdfReader(new SmbFileInputStream(FileToSmbFile(smbfiles[i].replace("smb://", "\\\\").replace("/", "\\"),hid)));  
		        	
//		        	if ("Y".equals(bookmark)){
//		        		document.add(chapter);
//		        	}
		        	//進行檔案複製合併
		        	int n = reader.getNumberOfPages();
		        	for (int j = 1; j <= n; j++) {
		        		document.newPage();
		        		PdfImportedPage page = copy.getImportedPage(reader, j);
		        		copy.addPage(page);
		        	}
		        	

		        	//加目錄
//		        	document.newPage();
//		        	Chunk c_no = new Chunk(String.valueOf(copy.getPageNumber()) + ". ", textN).setLocalGoto(fileNM[i]);
//	                Chunk title = new Chunk(fileNM[i], textN).setLocalGoto(fileNM[i]);
//	                Chunk doted = new Chunk(new DottedLineSeparator()).setLocalGoto(fileNM[i]);
//	                Chunk number = new Chunk(String.valueOf(copy.getPageNumber()), textN).setLocalGoto(fileNM[i]);
//
//	                Paragraph content = new Paragraph();
//	                content.add(c_no);
//	                content.add(title);
//	                content.add(doted);
//	                content.add(number);
//	                content.setIndentationLeft(50);
//	                content.setIndentationRight(50);
//	                document.add(content);
		        }
		        
		        document.close();
		        copy.close();
		        mergeOK = true;
		} catch (IOException e) {
 	           e.printStackTrace();
 	          throw e;
		} catch (com.itextpdf.text.DocumentException e) {
			// TODO 自動產生 catch 區塊
			e.printStackTrace();
		}
		return mergeOK;
	 }
	
	
	/**
	 * 每頁右下角標註logo浮水印 (由影印機印表會在左下角出現印表資訊，註記資訊改放右下角)
	 * @param userid, dirPath,傳回:總頁數
	 * @throws Exception
	 */
	
	public int markPDF(String userid, String dirPath, String addNum, boolean hasCover,String hid) throws Exception {
		int allPage=0;	
		Document document = new Document();
		Date date = new Date();
		SimpleDateFormat Tformat = new SimpleDateFormat("yyyyMMddHHmmss");
		String strDate = Tformat.format(date);
        //Read the existing PDF document
		PdfReader pdfReader = new PdfReader(new SmbFileInputStream(FileToSmbFile(dirPath,hid)));	
        //Get the PdfStamper object
        PdfStamper pdfStamper = new PdfStamper(pdfReader, new SmbFileOutputStream(FileToSmbFile(dirPath,hid)));
        //欲註記文字
		String waterText = "調閱 "+userid +"-"+strDate; //卡號+時戳 
        //欲加上的圖片檔
		Image image = Image.getInstance (GlobalProp.getProp("root")+"/Page/images/vghks_logo.jpg");
		//圖片位置
		image.setAbsolutePosition(400, 10);	
		image.scalePercent(20f);  //圖片縮放比例 
		
        //Get the PdfContentByte type by pdfStamper.
		int iniNo=0; //起始頁
		if (hasCover){
			allPage=pdfReader.getNumberOfPages()-1;
			iniNo = 1;
		}else{
			allPage=pdfReader.getNumberOfPages();
			
		}
		for (int i = 1; i <= allPage; i++) {
            PdfContentByte pageContent = pdfStamper.getOverContent(iniNo+i);
            pageContent.setGState(this.getPdfGState());
            pageContent.beginText();
            pageContent.setFontAndSize(BaseFont.createFont("C:/WINDOWS/Fonts/KAIU.TTF",BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 8);
            pageContent.setColorFill(BaseColor.BLUE);
     //     pageContent.showTextAligned(Element.ALIGN_CENTER, waterText , document.getPageSize().getWidth() / 2, 10 , 0); //置中
            pageContent.showTextAligned(Element.ALIGN_CENTER, waterText , 475, 20 , 0);
            //頁碼
            if ("Y".equals(addNum)){
            	pageContent.setFontAndSize(BaseFont.createFont("C:/WINDOWS/Fonts/KAIU.TTF",BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 14);
                pageContent.setColorFill(BaseColor.DARK_GRAY);
            	pageContent.showTextAligned(Element.ALIGN_CENTER, i + "/" + allPage , document.getPageSize().getWidth() / 2, 10 , 0);
            }
            pageContent.endText();
            pageContent.addImage(image);
            
        }
        pdfStamper.close();
        return allPage;
	}
	
	/**
	 * 每頁右下角標註列印者
	 * @param userid, dirPath,傳回:總頁數
	 * @throws Exception
	 */
	
	public boolean markPrtInfo(String userid, String dirPath,String hid) throws Exception {	
		boolean success = false;
		int allPage=0;
		try {
		Document document = new Document(PageSize.A4);
		document.open();
		Date date = new Date();
		SimpleDateFormat Tformat = new SimpleDateFormat("yyyyMMddHHmmss");
		String strDate = Tformat.format(date);
        //Read the existing PDF document
		PdfReader pdfReader = new PdfReader(new SmbFileInputStream(FileToSmbFile(dirPath,hid)));	
        //Get the PdfStamper object
        PdfStamper pdfStamper = new PdfStamper(pdfReader, new SmbFileOutputStream(FileToSmbFile(dirPath,hid)));
        //欲註記文字
		String waterText = "列印 "+userid +"-"+strDate; //卡號+時戳 
		//Get the PdfContentByte type by pdfStamper.
		allPage=pdfReader.getNumberOfPages();
		for (int i = 1, pdfPageSize = pdfReader.getNumberOfPages(); i <= pdfPageSize; i++) {
            PdfContentByte pageContent = pdfStamper.getOverContent(i);
            pageContent.setGState(this.getPdfGState());
            pageContent.beginText();
            pageContent.setFontAndSize(BaseFont.createFont("C:/WINDOWS/Fonts/KAIU.TTF",BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 8);
            pageContent.setColorFill(BaseColor.BLUE);
            pageContent.showTextAligned(Element.ALIGN_CENTER, waterText , 475, 10 , 0);
            pageContent.endText();
        }
        pdfStamper.close();
        success = true;
        document.close();
		}catch (IOException e) {
	           e.printStackTrace();
	          throw e;
		}      
        return success;
	}
	
	private PdfGState getPdfGState() {		 
        PdfGState graphicState = new PdfGState();
        graphicState.setFillOpacity(0.7f);
        graphicState.setStrokeOpacity(1f);
         return graphicState;
    }
	
	public boolean copyFile(String xmlFileNm,String sourceImgPath,String hid){
		boolean copyOK = false;
		try{
			String tmpFileNm =xmlFileNm+".xml";
			SmbFile sf = FileToSmbFile(tmpFileNm,hid);
        	int bytesRead = 0; 
			byte[] buffer = new byte[5120000]; 
			SmbFileInputStream in = new SmbFileInputStream(sf);
			SmbFileOutputStream out = new SmbFileOutputStream(sourceImgPath);
			while((bytesRead = in.read(buffer, 0, 5120000)) != -1) { 
				  out.write(buffer, 0, bytesRead); 
			} 
				in.close(); 
            out.flush();
            out.close();
            copyOK = true;
		}catch(Exception ce){
			log.debug("    ====================>> copy file ee=="+ce.getMessage());
		}
		return copyOK;
	}
	
	/**
	 * 確認file server目錄下是否有檔案
	 * @param idno
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	/*public boolean chkFile(String idno, String type,String hid) throws SQLException{
		boolean hasFile = false;
		//String filepath = "\\\\nfs01p\\psm\\";
		String filepath = Globals.gfileSRV+"\\";
		String suffix = idno.substring(idno.length()-1);
		filepath += suffix + idno + "\\License\\" + type + "\\";
		try {
			if (FileToSmbFile(filepath,hid).exists()){
	 	   	    SmbFile[] filelist = FileToSmbFile(filepath,hid).listFiles(); 
	 	   	    if (filelist.length>0){
	 	   	    	hasFile = true;
	 	   	    }
			}
		} catch (SmbException e) {
			// TODO 自動產生 catch 區塊
			e.printStackTrace();
		}
		return hasFile;
	}*/
	
}
