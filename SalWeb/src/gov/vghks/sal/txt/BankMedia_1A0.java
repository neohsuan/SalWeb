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


//銀行媒體傳輸_高榮
public class BankMedia_1A0 {
	Logger log = Logger.getLogger(SalNTDAO.class);  //呼叫log4j
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public BankMedia_1A0() {
		
	}

	public BankMedia_1A0(String strDir,String txtFileName,ArrayList<Paprtdtl> paprtdtlAry ,String hid) throws Exception {
		String me = getClass().getName() + ".BankMedia_1A0()";
		
		Paprtdtl prtobj=null;
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		BufferedWriter bfw = null;
		FileUtil fileUtil = new FileUtil();
		String tmp="";
		String strline="";
		
		try {
			//新增資料夾路徑
			if (!FileUtil.FileToSmbFile(strDir,hid).exists()){
				fileUtil.FileToSmbFile(strDir,hid).mkdirs();
			}
			//建立txt檔案			
			SmbFile payfile = fileUtil.FileToSmbFile(txtFileName,hid);
			
			bfw = new BufferedWriter(new OutputStreamWriter(new SmbFileOutputStream(payfile, false), "utf-8"));
			//取得第1筆記錄
			prtobj=paprtdtlAry.get(0);
			
			//第一行
			String firstline="439350006888";
			tmp=DateUtil.getHcDT(prtobj.getPaybank());
			firstline+=StringUtil.addZero(tmp, 10);
			firstline+="A";
			
			bfw.write(firstline);
			bfw.newLine();
			bfw.flush();
			
			//loop
			//1-12(銀行帳號) 13-22(金額) 23-32(空白) 33-42(ID),44-45( Y)
			for (Paprtdtl data : paprtdtlAry){
				
				strline = StringUtil.nullToString(data.getPayacno());
				strline += StringUtil.addZero(String.valueOf(data.getPayamt()),10);
				strline += "          ";
				strline += data.getPayidno();
				strline += " Y";
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
