package gov.vghks.sal.txt;

import gov.vghks.common.util.DateUtil;
import gov.vghks.common.util.StringUtil;
import gov.vghks.sal.Hospitals;
import gov.vghks.sal.dao.SalNTDAO;
import gov.vghks.sal.model.Paprtdtl;
import gov.vghks.sal.util.FileUtil;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import org.apache.log4j.Logger;


//銀行媒體傳輸_屏榮
public class BankMedia_2A0 {
	Logger log = Logger.getLogger(SalNTDAO.class);  //呼叫log4j
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public BankMedia_2A0() {
		
	}
	//bankps薪轉附言 預設格式:900(沒有薪轉附言)  901:薪轉附言(有),
	public BankMedia_2A0(String strDir,String txtFileName,ArrayList<Paprtdtl> paprtdtlAry ,String bankps,String hid) throws Exception {
		String me = getClass().getName() + ".BankMedia_2A0()";
		
		Paprtdtl prtobj=null;
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		BufferedWriter bfw = null;
		FileUtil fileUtil = new FileUtil();
		String tmp="";
		String strline="";
		String itm="";
		try {
			//新增資料夾路徑
			if (!FileUtil.FileToSmbFile(strDir,hid).exists()){
				fileUtil.FileToSmbFile(strDir,hid).mkdirs();
			}
			//建立txt檔案			
			SmbFile payfile = fileUtil.FileToSmbFile(txtFileName,hid);
			//薪資轉帳900(沒有薪轉附言)  901:薪轉附言(有),
			itm="900";
			if (!StringUtil.trimWideEmpty(bankps).equals(""))
				itm="901";
			
			bfw = new BufferedWriter(new OutputStreamWriter(new SmbFileOutputStream(payfile, false), "utf-8"));
			
			//取得第1筆記錄,總人數,總金額
			prtobj=paprtdtlAry.get(0);
			
			//第一行
			//1-4企業編號c4 5-18委辦帳號c14 19-26預定匯款日c8 27-29轉帳項目c3 31-42轉帳總金額9(11)v99 43-49轉帳總筆數c7 50-57公司統編C8 58-71空白c14 72傳輸結果
			String firstline="493220303900008881";
			tmp=DateUtil.getHcDT(prtobj.getPaybank());
			firstline+=StringUtil.addZero(tmp, 8);
			firstline+=itm;	//轉帳項目
			firstline+=StringUtil.addZero(String.valueOf(prtobj.getPayamt()),11); //轉帳總金額9(11)v99
			firstline+="00";
			firstline+=StringUtil.addZero(String.valueOf(prtobj.getPayidno()),7);
			firstline+="92362346";	//公司統編
			firstline += StringUtil.addSpace(" ",14);
			firstline+="0";
			bfw.write(firstline);
			bfw.newLine();
			bfw.flush();
			
			//loop 第2筆之後
			//轉帳項目：900=>1-4企業編號c4 5-18受款人帳號c14 19-28身份證字號 c10 c3 29-41轉帳金額9(11)v99 42-44轉帳格式c3 45-71空白C27 72傳輸結果c1
			//轉帳項目：901=>1-4企業編號c4 5-18受款人帳號c14 19-28身份證字號 c10 c3 29-41轉帳金額9(11)v99 42-44轉帳格式c3 45-56薪轉附言C12 57-71空白C15 72傳輸結果c1
			
			for (int i=1;i<paprtdtlAry.size();i++){
				prtobj=paprtdtlAry.get(i);
				strline = "4932";
				strline += StringUtil.addSpace(StringUtil.nullToString(prtobj.getAcno()),14);
				strline += prtobj.getPayidno();
				strline += StringUtil.addZero(String.valueOf(prtobj.getPayamt()),11);
				strline += "00";
				strline += itm;
				if (itm.equals("901")){//薪轉附言
					strline += StringUtil.addSpace(bankps,12);
					strline += StringUtil.addSpace(" ",15);
				}else
					strline += StringUtil.addSpace(" ",27);
				strline += "0";
				
				bfw.write(strline);
	            bfw.newLine();
				bfw.flush();
			}
			

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.info(me + " fail, " + e);
			throw new FileNotFoundException(me + ", fail!!." + e);
		} catch (IOException e) {
			e.printStackTrace();
			log.info(me + " fail, " + e);
			throw new IOException(me + ", fail!!." + e);
		} catch (Exception e) {
			e.printStackTrace();
			log.info(me + " fail, " + e);
			throw new Exception(me + ", fail!!." + e);
		} finally {
			if ( bfw !=null ) bfw.close();
		}
				
	}
	
	
}
