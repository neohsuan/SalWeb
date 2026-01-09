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


//銀行媒體傳輸_聯醫
public class BankMedia_3A0 {
	Logger log = Logger.getLogger(SalNTDAO.class);  //呼叫log4j
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public BankMedia_3A0() {
		
	}

	public BankMedia_3A0(String strDir,String txtFileName,ArrayList<Paprtdtl> paprtdtlAry ,String hid) throws Exception {
		String me = getClass().getName() + ".BankMedia_3A0()";
		
		Paprtdtl prtobj=null;
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		BufferedWriter bfw = null;
		FileUtil fileUtil = new FileUtil();
		String tmp="";
		String strline="";
		String strbankdt="";//入帳日期 民國年月日
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
			strbankdt=DateUtil.getHcDT(prtobj.getPaybank());	//民國年月日,
			
			//loop 第2筆之後
			/*' 1 ~ 8 入帳日期 (8碼)ex,01130501
            ' 9 ~ 12 主辦行  (4碼)ex,0299
            ' 13 ~ 22 營利編號  (10碼)ex,15757714
            ' 23 ~ 38 銀行帳號  (16碼)ex,0000299968302893
            ' 39 ~ 48 ID  (10碼)ex,R221421873
            ' 49 ~ 49 入扣帳別  (1碼)ex,C
            ' 50 ~ 51 轉帳別  (2碼)ex,01
            ' 52 ~ 64 入帳金額  (13碼)ex,0000000037256
            ' 65 ~ 114 委託者專用區  (50碼)
            ' 115 ~ 117 明細類別  (3碼)
            ' 118 ~ 120 摘要類別  (3碼)ex,C01
            ' 121 ~ 130 存款人資料  (10碼)
            ' 131 ~ 140 銀行保留區  (10碼)
*/			for (int i=1;i<paprtdtlAry.size();i++){
				prtobj=paprtdtlAry.get(i);
				strline = StringUtil.addZero(strbankdt,8);
				strline += "0299";
				strline += "15757714  ";
				strline += StringUtil.addZero(StringUtil.nullToString(prtobj.getAcno()),16);
				strline += prtobj.getPayidno();
				strline += "C01";
				strline += StringUtil.addZero(String.valueOf(prtobj.getPayamt()),13);
				strline += StringUtil.addSpace(" ", 53);
				strline += "C01";
				strline += StringUtil.addSpace(" ", 20);
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
