package gov.vghks.sal.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import jcifs.CIFSContext;
import jcifs.config.PropertyConfiguration;
import jcifs.context.BaseContext;
import jcifs.smb.NtlmPasswordAuthenticator;
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

import gov.vghks.sal.Globals;
import gov.vghks.sal.Hospitals;

public class FileUtil {
	static Logger log=Logger.getLogger(FileUtil.class);  //呼叫log4j
	String ttf_kaiu="font\\VGHKH_KAI.TTF";
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
			Hospitals hospInfo = Hospitals.getOneHosp(hid);
			login=hospInfo.gap_id;
			pass =hospInfo.gap_pwd;
						
			Properties prop = new Properties();
            prop.setProperty("jcifs.smb.client.enableSMB2", "true");
            prop.setProperty("jcifs.smb.client.disableSMB1", "true");
            CIFSContext base = new BaseContext(new PropertyConfiguration(prop));

            CIFSContext auth = base.withCredentials(new NtlmPasswordAuthenticator("", login, pass));
            
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
	//刪除檔案
	@SuppressWarnings("finally")
	public boolean delPDF(String sfile,String hid) throws IOException{		 
		boolean delok=true;
		SmbFile file = null;
		try{
			file = FileToSmbFile(sfile,hid);
			if (file.exists()){
				file.delete();
			}
		} catch (IOException e) {
			System.out.println("----->IOException--"+e.getMessage());
			e.printStackTrace();
			delok=false;
			throw e;
		} catch (Exception  e1) { // !!
			System.out.println("----->Exception--"+e1.getMessage());
			e1.printStackTrace();
			delok=false;
			throw e1;
		} finally{
			return delok;	
		}
    }
	//檔案重新命名
	@SuppressWarnings("finally")
	public boolean renamePDF(String _pathFrom, String _pathTo,String hid) throws IOException{		 
		boolean renamed = false;
		SmbFile smbFileFrom = null;
		SmbFile smbFileTo = null;
		try{
			
			smbFileFrom = FileToSmbFile(_pathFrom,hid);
		    smbFileTo = FileToSmbFile(_pathTo,hid);
		    if (smbFileTo.exists()) {
		      smbFileTo.delete();
		    }
		    smbFileFrom.renameTo(smbFileTo);

//		}catch (SmbException _ex) {
//		    throw new NestedIOException("Failed to rename [" + _pathFrom + "] to [" + _pathTo + "].", _ex);
		} 
		catch (IOException e) {
			System.out.println("----->IOException--"+e.getMessage());
			e.printStackTrace();
			renamed=false;
			throw e;
		} catch (Exception  e1) { // !!
			System.out.println("----->Exception--"+e1.getMessage());
			e1.printStackTrace();
			renamed=false;
			throw e1;
		} finally{
			return renamed;	
		}
    }
	/**
	 * 每頁右下角標註頁數
	 * @param userid, dirPath,direction(true:橫,false:直),傳回:總頁數
	 * @throws Exception
	 */
	
	public boolean markPrtPg(String userid, boolean direction,String dirPath,String hid) throws Exception {	
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
            pageContent.setFontAndSize(BaseFont.createFont(ttf_kaiu,BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 8);
            pageContent.setColorFill(BaseColor.BLUE);
            //pageContent.showTextAligned(Element.ALIGN_CENTER, waterText , 475, 10 , 0);
            //true:橫,false:直
            if (direction)
            	pageContent.showTextAligned(Element.ALIGN_CENTER, i + "/" + allPage , document.getPageSize().getHeight() / 2, 10 , 0);
            else 
            	pageContent.showTextAligned(Element.ALIGN_CENTER, i + "/" + allPage , document.getPageSize().getWidth() / 2, 10 , 0);
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
}
