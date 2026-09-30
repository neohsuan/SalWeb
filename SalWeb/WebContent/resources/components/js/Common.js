/*傳入日期vDate，傳回中國年日的型式。
* 如民國51年10月24日
* vSplitChar：年月日間的分隔符號，如 '-' , '/'
*/
function getChineseDate(vDate, vSplitChar){
	var arrDate=vDate.split(vSplitChar);
	var chYear=parseInt(arrDate[0],10)-1911;
	var mm=parseInt(arrDate[1],10);
	var dd=parseInt(arrDate[2],10);
	return "民國 " + chYear + " 年 " + mm + " 月 " + dd + " 日 " ;
} 
//驗証結束時間是否大於結束日期, 是則傳回true
function dayRange(fromDateId, toDateId){		
		var objFromDate=eval(document.getElementById(fromDateId));
		var objToDate=eval(document.getElementById(toDateId));
		var vFromDate = objFromDate.value;
 		var vToDate = objToDate.value;
 		var arrFromDate=vFromDate.split('-');
 		var arrToDate=vToDate.split('-'); 		 		
 		var dtFromDate = new Date();
 		var dtToDate = new Date(); 	 		
 		dtFromDate.setFullYear(parseInt(arrFromDate[0],10));	
 		dtFromDate.setMonth(parseInt(arrFromDate[1],10)-1);
  		dtFromDate.setDate(parseInt(arrFromDate[2],10));
 		dtToDate.setFullYear(parseInt(arrToDate[0],10));	
 		dtToDate.setMonth(parseInt(arrToDate[1],10)-1);
  		dtToDate.setDate(parseInt(arrToDate[2],10)); 	  
  		var difference=dtToDate.getTime() - dtFromDate.getTime();  		
  		difference=Math.floor(difference/(1000*60*60*24));
  		if (difference < 0 ) {
  			alert("結束日期不可小於起始日期！");
 			return false;
  		} return true;
}

//驗証輸入日期不可大於系統日期
function chkAfDate(objFromDate){	
		var vFromDate = objFromDate.value;
		var arrFromDate=vFromDate.split('-');		 		
		var dtFromDate = new Date();
		var dtToDate = new Date(); 	 		
		dtFromDate.setFullYear(parseInt(arrFromDate[0],10));	
		dtFromDate.setMonth(parseInt(arrFromDate[1],10)-1);
		dtFromDate.setDate(parseInt(arrFromDate[2],10));
		var difference=dtToDate.getTime() - dtFromDate.getTime();  		
		difference=Math.floor(difference/(1000*60*60*24));
		if (difference < 0 ) {
			alert("輸入日期不可大於今日！");
			return false;
		} return true;
}

//驗証輸入日期、時間不可大於系統時間
function chkAfTime(dateObj,timeObj){
	//alert(chkTimeFormat(timeObj));
	if (chkTimeFormat(timeObj)){
		var vTime = timeObj.value;
		var chkDate = new Date(); //前端傳來日期
		var now = new Date();
		var arrFromDate=dateObj.value.split('-');
		chkDate.setFullYear(parseInt(arrFromDate[0],10));	
		chkDate.setMonth(parseInt(arrFromDate[1],10)-1);
		chkDate.setDate(parseInt(arrFromDate[2],10));
 		chkDate.setHours(vTime.substring(0,2));
 		chkDate.setMinutes(vTime.substring(3,5));
  		var difference=now.getTime() - chkDate.getTime();
  		difference=Math.floor(difference/(1000*60*60*24));
  		//alert(difference);
  		if (difference < 0 ) {
  			alert("不可大於目前時間！");
  			timeObj.value = "";
  			timeObj.focus();
  			
  		}
	}
}

//*******************************************************************************
//函數名稱：AddDay(addD)
//目        的：今日日期加減日數後的日期
//傳入參數：傳入addD(加減天數)
//傳  回  值：例: AddDay(-10) 今天是2012-01-08 回傳2011-12-29
//*******************************************************************************
function AddDay(addD){
	var now = new Date();
	var thisMonth=getThisMonth(1);
	var thisDay=now.getDate();	
	var day = thisMonth + "/" + thisDay+"/"+getThisYear(2);
	var a = new Date(day);
	a = a.valueOf();
	a = a + addD * (24 * 60 * 60 * 1000);
	a = new Date(a);
	var years = a.getFullYear();
	var months = a.getMonth()+1;
	if (months<10) months="0" + months;
	var days = a.getDate();
	if (days<10) days="0" + days;
	return years+"-"+months+"-"+days;
}

//*******************************************************************************
//函數名稱：AddDay(theDate,addD)
//目        的：某日期加減日數後的日期
//傳入參數：傳入theDate(某日期yyyy-MM-ss)  addD(加減天數)
//傳  回  值：例: AddDay(-10) 今天是2012-01-08 回傳2011-12-29
//*******************************************************************************
function AddDay(theDate,addD){	
	var a = new Date(theDate.split('-')[1]+"/"+theDate.split('-')[2]+"/"+theDate.split('-')[0]);// 月/日/年	
	a = a.valueOf();
	a = a + addD * (24 * 60 * 60 * 1000);
	a = new Date(a);
	var years = a.getFullYear();
	var months = a.getMonth()+1;	
	if (months<10) months="0" + months;
	var days = a.getDate();
	if (days<10) days="0" + days;
	return years+"-"+months+"-"+days;
}


//*******************************************************************************
//函數名稱：AddTime(addTime,addT)
//目        的：傳入時間加減小時
//傳入參數：傳入addTime,addT(加減小時)
//傳  回  值：例: AddDay(16:22:12,2)  回傳18:22:12
//*******************************************************************************
function AddTime(addTime,addMM){
	var timeAry=addTime.split(':');
	var timeHH=(parseInt(timeAry[0],10))+ addMM;
	if(timeHH>23){
		timeHH=timeHH-24;
	}
	if(timeHH<10) timeHH="0"+timeHH;
	return timeHH+":"+timeAry[1];
}

//*******************************************************************************
//函數名稱：AddTimeHHMM(addTime,addT)
//目        的：傳入時間加上小時分
//傳入參數：傳入addTime,addT(加減小時)
//傳  回  值：例: AddDay(1622,0020)  回傳1610
//*******************************************************************************
function AddTimeHHMM(addTime,addHHMM){
	var intHH=parseInt(addTime.substring(0,2),10);
	var intMM=parseInt(addTime.substring(2,5),10);
	var intAddHH=parseInt(addHHMM.substring(0,2),10);
	var intAddMM=parseInt(addHHMM.substring(2,5),10);
	var timeMMTmp=intMM + intAddMM;
	var timeHHTmp=intHH + intAddHH;	
	var timeMM=timeMMTmp;
	var timeHH=timeHHTmp;
	if (timeMM>59) //超過一小時
	{
		timeMM=timeMMTmp-60;
		timeHH=timeHHTmp + 1;		
	} 
	if(timeHH>23){
		timeHH=timeHH-24;
	}
	if(timeHH<10) timeHH="0"+timeHH;
	if(timeMM<10) timeMM="0"+timeMM;
	return timeHH+""+timeMM;
	
}
function chkAfTime1(timeObj){
	if (chkTimeFormat(timeObj)){
		var vTime = timeObj.value;
		var chkDate = new Date(); //前端傳來日期
		var now = new Date();
 		chkDate.setHours(vTime.substring(0,2));
 		chkDate.setMinutes(vTime.substring(3,5));
  		var difference=now.getTime() - chkDate.getTime();
  		difference=Math.floor(difference/(1000*60*60*24));
  		if (difference < 0 ) {
  			alert("不可大於目前時間！");
  			timeObj.value = "";
  			timeObj.focus();
  		}
	}
}

function chkAfTimeBln(dateObj,timeObj){
	//alert(chkTimeFormat(timeObj));
	if (chkTimeFormat(timeObj)){
		var vTime = timeObj.value;
		var chkDate = new Date(); //前端傳來日期
		var now = new Date();
		var arrFromDate=dateObj.value.split('-');
		chkDate.setFullYear(parseInt(arrFromDate[0],10));	
		chkDate.setMonth(parseInt(arrFromDate[1],10)-1);
		chkDate.setDate(parseInt(arrFromDate[2],10));
 		chkDate.setHours(vTime.substring(0,2));
 		chkDate.setMinutes(vTime.substring(3,5));
  		var difference=now.getTime() - chkDate.getTime();
  		difference=Math.floor(difference/(1000*60*60*24));
  		//alert(difference);
  		if (difference < 0 ) {
  			alert("不可大於系統時間！");
  			timeObj.value = "";
  			timeObj.focus();
  			return false;
  		}  		
	}
	return true;
}

function chkAfTimeBln2(dateObj,timeObj){
	//alert(chkTimeFormat(timeObj));
		var vTime = timeObj.value;
		var chkDate = new Date(); //前端傳來日期
		var now = new Date();
		var arrFromDate=dateObj.split('-');
		chkDate.setFullYear(parseInt(arrFromDate[0],10));	
		chkDate.setMonth(parseInt(arrFromDate[1],10)-1);
		chkDate.setDate(parseInt(arrFromDate[2],10));
 		chkDate.setHours(timeObj.substring(0,2));
 		chkDate.setMinutes(timeObj.substring(3,5));
  		var difference=now.getTime() - chkDate.getTime();
  		difference=Math.floor(difference/(1000*60*60*24));
  		//alert(difference);
  		if (difference < 0 ) {
  			//alert("不可大於系統時間！");
  			timeObj.value = "";
  			return false;
  		}  		
	return true;
}

function getToday(dateType){
	var now = new Date();
	var thisMonth=getThisMonth(1);
	if (thisMonth<10) thisMonth="0" + thisMonth;
	var thisDay=now.getDate();
	if (thisDay<10) thisDay="0" + thisDay;
	return getThisYear(dateType) + "-" + thisMonth + "-" + thisDay;
}

function getNow(dateType){
	var now = new Date();	
	return getToday(dateType) + " " + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()  ;
}

function getNow2(){
	var now = new Date();	
	return getToday(2) + " " + now.getHours() + ":" + now.getMinutes()  ;
}


function getTime(timeType){
	var now = new Date();
	if(timeType == 1){//HH:MM
		return now.getHours() + ":" + now.getMinutes()  ;
	}else{//HH:MM:SS
		return now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds()  ;
	}
}

function getThisYear(yearType){
	//yearType 1:表示中國年
	//yearType 2:西洋年 
	var now = new Date();
	if (yearType == 1) {
		return now.getFullYear()-1911;
	} else {
		return now.getFullYear();
	}	
}

function getThisMonth(monthType){
	//yearType 1:表示1,2,,3,~12月份
	//yearType 2:表示0,1,2,3,~11月份
	var now = new Date();
	if (monthType == 1) {
		return now.getMonth() + 1;
	} else {	
		return now.getMonth();
	}	
}

//*******************************************************************************
//函數名稱：checkIsNum(inpField)
//目        的：檢查是否全為數字
//傳入參數：欲比對字串
//傳  回  值：無
//限        制：無
//備        註：
//範        例：
//版        本：1.0  
//作　    者：YaHsueh,Chien(2005-01-24)
//*******************************************************************************
function checkIsNum(inpField){
var count=0;
var newstr="";
     for(var  i=0;i<inpField.value.length;i++){
          var  codes=inpField.value.substring(i,i+1);
           if (isNaN(codes))
			{
				if (codes==".") {newstr+=inpField.value.charAt(i);}
				else if (codes==":") {newstr+=inpField.value.charAt(i);}
				else if (codes=="-") {newstr+=inpField.value.charAt(i);}
				else {
				count++;		}
				
			}else{
			    newstr+=inpField.value.charAt(i);
			}
    }
    if(count>0){
       alert("只能輸入數字0~9！"); 
    }
     inpField.value=newstr; 
	 inpField.focus();
}

//*******************************************************************************
//函數名稱：isNumber(strWord)
//目        的：檢查字串中是否全為數字
//傳入參數：欲檢查字串
//傳  回  值：true：全是數字/false：不全是數字
//副  作  用：無
//備        註：
//範        例：
//版        本：1.0  
//作　　者：(2004-08-05)
//*******************************************************************************
function isNumber(strWord)
{
   var digitStr= "0123456789.-";
   var allValid = true;        
   for (i = 0;  i < strWord.length;  i++)
   {
      ch = strWord.charAt(i);
      for (j = 0;  j < digitStr.length; j++)
          if (ch == digitStr.charAt(j))
          break;        
	  if (j == digitStr.length)
	  {
	       allValid = false;
	       break;
	  }        
    }
	 if (!allValid)   	 
	     return (false);
	 else
	      return true;
}
//*******************************************************************************
//函數名稱：isOnlyNumber(strWord)
//目        的：檢查字串中是否全為數字
//傳入參數：欲檢查字串
//傳  回  值：true：全是數字/false：不全是數字
//副  作  用：無
//備        註：
//範        例：
//版        本：1.0  
//作　　者：(2011-05-30)
//*******************************************************************************
function isOnlyNumber(strWord)
{
   var digitStr= "0123456789";
   var allValid = true;        
   for (i = 0;  i < strWord.length;  i++)
   {
      ch = strWord.charAt(i);
      for (j = 0;  j < digitStr.length; j++)
          if (ch == digitStr.charAt(j))
          break;        
	  if (j == digitStr.length)
	  {
	       allValid = false;
	       break;
	  }        
    }
	 if (!allValid)   	 
	     return (false);
	 else
	     return true;
}
//*******************************************************************************
//函數名稱：isTime(str)
//目        的：檢查是否正確時間
//傳入參數：欲檢查字串
//傳  回  值：true：正確/false：不正確
//副  作  用：無
//備        註：
//範        例：
//版        本：1.0  
//作　　者：(2011-05-31)
//*******************************************************************************
function isTime(str){
	var a = str.match(/^(\d{1,2})(:)?(\d{1,2})$/);
		if(a==null){
			return false;//not時間格式 
		}else if(a[1]>24 || a[3]>60){
			return false; //非正確時間
		}else{
			return true;
		}
}
//*******************************************************************************
//函數名稱：isDateNumber(strWord)
//目        的：檢查日期字串中是否為數字或"-"
//傳入參數：欲檢查字串
//傳  回  值：true：全是數字/false：不全是數字
//副  作  用：無
//備        註：
//範        例：
//版        本：1.0  
//作　　者：(2004-08-05)
//*******************************************************************************
function isDateNumber(strWord)
{
//alert(strWord);
   var digitStr= "0123456789-()";
   var allValid = true;        
   for (i = 0;  i < strWord.length;  i++)
   {
      ch = strWord.charAt(i);
      for (j = 0;  j < digitStr.length; j++)
          if (ch == digitStr.charAt(j))
          break;        
	  if (j == digitStr.length)
	  {
	       allValid = false;
	       break;
	  }        
    }
	 if (!allValid)   	      
	     return (false);
	 else
	      return true;
}

//*******************************************************************************
//函數名稱：chkIsDateNumber(strWord)
//目        的：檢查日期字串中是否為數字或"-"
//傳入參數：欲檢查字串
//傳  回  值：true：全是數字/false：不全是數字
//副  作  用：無
//備        註：
//範        例：
//版        本：1.0  
//作　　者：(2007-12-10)
//*******************************************************************************
function chkIsDateNum(strWord)
{
   var digitStr= "0123456789-";
   var newStr="";
   var allValid = true;        
   for (i = 0;  i < strWord.length;  i++)  {
      ch = strWord.charAt(i);
      alert("for="+i+" ch="+ch);
      for (j = 0;  j < digitStr.length; j++) {
          if (ch == digitStr.charAt(j)) {
          	newStr=newStr+ch;
          	break;        
          }
		  if (j == digitStr.length) {
		       allValid = false;
		       break;
		  }
	  }        
    }
    alert("allValid="+allValid);
    alert(newStr);
	 if (!allValid) {
	 	strWord.value=newStr;
	    alert('只能輸入0~9或-!!');
	 }
}

 //*******************************************************************************
// 函數名稱：isNull(strTxt)
// 目        的：判斷傳入字串是否為null，並將null轉成''
// 參數說明：[String] 欲判斷字串
// 傳  回  值：[String]  傳入字串若為null，傳回''，若不為null，傳回原值。
// 備        註：
// 範        例： isNull("abc") ==>>return abc;
// 版        本：1.0
// 作        者：YaHsueh,Chien(2004-08-12)
//*******************************************************************************

function isNull(strTxt){
   if(strTxt==null){
      return '';
   }else{
      return strTxt;
   }
}

//*******************************************************************************
// 函數名稱：isNullAry(aryTxt)
// 目        的：判斷傳入陣列是否為空陣列，若為null，傳回一空陣列
// 參數說明：[Array]欲判斷陣列
// 傳  回  值：[Array]傳入陣列若為null，傳回一新陣列，若不為null，傳回原陣列。
// 備        註：
// 範        例：isNull(null) ==>>return new Array();
// 版        本：1.0
// 作        者：YaHsueh,Chien(2004-08-12)
//*******************************************************************************

function isNullAry(aryTxt){
   if(aryTxt==null){
      return new Array();
   }else{
      return aryTxt;
   }
}
//*******************************************************************************
//函數名稱：isExit(ary1,str1)
//目        的：檢查某陣列中是否包含某字串
//傳入參數：欲比對陣列，欲檢查字串
//傳  回  值：boolean
//限        制：無
//備        註：
//範        例：
//版        本：1.0  
//作　　者：YaHsueh,Chien(2005-01-24)
//*******************************************************************************
function isExit(ary1,str1){
    for(var i=0;i<ary1.length;i++){
            if(ary1[i]==str1){
                 return true;
            }
    }
    return false;
}

//***************************************************************************************************************************
//*函數名稱:isDataEmpty
//*目    的:檢查是否有輸入或者選擇資料
//*參數說明:strObjCol： 物件名稱集合使用【, ?】作為分隔字元
//*回轉值:true/false
//*範例: isDataEmpty('document.frmDataAll.txtAP_ID?應用系統,document.frmDataAll.txtUSR_ID?使用者代碼,document.frmDataAll.txtUSR_NAME?使用者名稱')
//由於在JSP直接代入isDataEmpty('document.getElementById('agentId')')會有錯誤，因此以document.getElementById(#agentId#)之後再轉回原來的形式
//*版本變更：	        					       
//*  xx. YYYY/MM/DD   VER     AUTHOR      COMMENTS	
//*    1. 2000/10/01      2.0       CRD              CREATE
//      2.2005/7/13                     mhhsu
//***************************************************************************************************************************
function isDataEmpty(strObjCol){
    strObjCol=replace(strObjCol,"#", "'");    
	var arrPerObj=strObjCol.split(',');
	var arrObj;
	var obj;
	var blnRtn=true;
	
	for (var i = 0; i < arrPerObj.length; i++){
		arrObj=arrPerObj[i].split('?');
		obj = eval(arrObj[0]);
		if (obj.tagName.toUpperCase()=='SELECT') {
			if (obj.selectedIndex==-1) blnRtn=false;
		}
		if (((obj.tagName.toUpperCase()=='INPUT') && (obj.type.toUpperCase()=='TEXT')) || (obj.type.toUpperCase()=='PASSWORD') || (obj.tagName.toUpperCase()=='TEXTAREA')){		
			if (strTrim(obj.value)=='') blnRtn=false;
		}	
		if (!blnRtn){
			alert(arrObj[1] + "不可為空值！");
			obj.focus();
			return false;}
	}
	return true;
}   
//JavaScript 本身沒有取代的函數，所以另外寫一個  
function replace(string,text,by) {
// Replaces text with by in string
    var strLength = string.length, txtLength = text.length;
    if ((strLength == 0) || (txtLength == 0)) return string;

    var i = string.indexOf(text);
    if ((!i) && (text != string.substring(0,txtLength))) return string;
    if (i == -1) return string;

    var newstr = string.substring(0,i) + by;

    if (i+txtLength < strLength)
        newstr += replace(string.substring(i+txtLength,strLength),text,by);

    return newstr;
}


//=============================================================================
function storeCaret (textEl) {
       if (textEl.createTextRange){ 
         textEl.caretPos = document.selection.createRange().duplicate();
        
         }
 }
 
//*******************************************************************************
//函數名稱：chkMaxLen(varWord,intMaxLen,blnNeedFocus,strFieldName)
//目        的：檢查輸入欄位長度是否過長
//傳入參數：欲檢查字串(物件)，最大長度，需不需要focus回原欄位，欄位描述
//傳  回  值：int 剩餘長度
//限        制：無
//備        註：
//範        例：
//版        本：1.0  
//作　　者：YaHsueh,Chien　(2005-05-01)
//*******************************************************************************
// 檢查多行文字方塊長度===================================================
function chkMaxLength(varWord,intMaxLen,blnNeedFocus,strFieldName){
   var intLen=0;
   var strWord;
   var newstr="";
   var addascii=0;
   var addbig5=0;
   if(typeof(varWord)=='object'){
     	strWord=varWord.value;
   } else{
   	    strWord=varWord;
   }
    var oldLen=strWord.length;
    var flg=0;
    var intLen2=0; 
    if(event!=null && event.type=="paste"){ // 加上貼上的文字內容
   		 strWord=strWord+window.clipboardData.getData("Text");
    }
   for(var  i=0;i<strWord.length  ;i++){
          var  codes=strWord.charCodeAt(i);
          if(i==oldLen) intLen2=intLen;
           if((codes>=48 && codes<=122) || !isNaN(strWord.charAt(i))){
                 addascii++;
           }else{            
                 addbig5+=2;
           }
            if(addascii+addbig5<=intMaxLen){//小於最大長度
                intLen=addascii+addbig5; 
           }
    }
    if(addascii+addbig5>intMaxLen){// 超出最大長度
        alert(strFieldName+"內容長度太長!");
      	event.returnValue=false; // event傳回false
    }else{
        intLen2=intLen; // 長度未超出
    } 
    if(event!=null ){ 
          if(event.type=="keydown"){//經由key in
		       if( (event.keyCode==8 || event.keyCode==46) && strWord.length>1){
		            if(strWord.charCodeAt(strWord.length-1)>=48 && strWord.charCodeAt(strWord.length-1)<=122){
		               intLen2=intLen-1;
		            }else{
		               intLen2=intLen-2;
		            }  
		    	}else if(event.keyCode>=48 && event.keyCode<=122){
		    	     intLen2=intLen+1;
		    	}else{
		    	     intLen2=intLen+2;
		    	}
		        if(intLen2>intMaxLen ){// 上下左右鍵及Backspace、Del鍵可用
		              if( event.keyCode!=8 && event.keyCode!=46 &&  event.keyCode!=37 &&  event.keyCode!=38 &&  event.keyCode!=39 &&  event.keyCode!=40 ) event.returnValue=false;
		        }
		   }
    }
     return (intMaxLen-intLen2);
}
//*******************************************************************************
//函數名稱：chkMaxLen(varWord,intMaxLen,blnNeedFocus,strFieldName)
//目        的：檢查輸入欄位長度是否過長(包含0e.0f)
//傳入參數：欲檢查字串(物件)，最大長度，需不需要focus回原欄位
//傳  回  值：int 剩餘長度
//限        制：無
//備        註：
//範        例：
//版        本：1.0  
//作　　者：YaHsueh,Chien　(2005-05-01)
//*******************************************************************************
// 檢查多行文字方塊長度===================================================
function chkMaxLen(varWord,intMaxLen,blnNeedFocus){
   var intLen=0;
   var strWord;
   var addascii=0;
   var addbig5=0;
   if(typeof(varWord)=='object'){
     	strWord=varWord.value;
   } else{
   	    strWord=varWord;
   }
    var chFlg=false;//中文註記

 
   for(var  i=0;i<strWord.length  ;i++){
          var  codes=strWord.charCodeAt(i);
          if((codes>=33 && codes<=122) || !isNaN(strWord.charAt(i))){ // 英文.數字 
                 addascii++;
                 if(chFlg) addascii+=1; // 前一個字是中文(0f)
                 chFlg = false;
         }else{ //中文           
                 addbig5+=2;
                 if(!chFlg) addbig5+=1;//前一個字是英文 or 目前為第一個字元且是中文 (0e)
                 chFlg = true;
         }
    }
    intLen=addascii+addbig5; 
    return (intMaxLen-intLen);//剩餘長度
}

//*******************************************************************************
//函數名稱：calLen(strWord,intMaxLen) 計算已輸長度，回傳剩餘長度(不包含0e.0f)
//目        的：計算欄位長度
//傳入參數：欲檢查字串，最大長度
//傳  回  值：int 剩餘長度
//限        制：無
//備        註：
//範        例：
//版        本：1.0  
//作　　者：YaHsueh,Chien　(2005-05-01)
//*******************************************************************************
function calLen(strWord,intMaxLen){
    var addascii=0;
    var addbig5=0;
    var intLen=0;
    for(var  i=0;i<strWord.length  ;i++){
          var  codes=strWord.charCodeAt(i);
           if((codes>=65 && codes<=122) || !isNaN(strWord.charAt(i))){
                 addascii++;
           }else{            
                 addbig5+=2;
           }
            if(addascii+addbig5<=intMaxLen){//小於最大長度
                intLen=addascii+addbig5; 
           }
    }
      return (intMaxLen-intLen);
}

//*******************************************************************************
//函數名稱：chkOBJMaxLength(varWordID,intMaxLen,blnNeedFocus,strFieldName,strFieldName,toalID)
//目        的：檢查輸入欄位長度是否過長
//傳入參數：varWordID:欲檢查字串(物件ID)，
//          intMaxLen:最大長度，
//          blnNeedFocus:需不需要focus回原欄位(true/false)，
//          strFieldName:欄位描述，
//          toalID:放剩餘字數的位置(物件ID) 若為空則改return
//傳  回  值：toalID若為空 return int 剩餘長度
//*******************************************************************************
function chkOBJMaxLength(varWordID,intMaxLen,blnNeedFocus,strFieldName,toalID){  
     var intLen=0;
     var strWord;
     var newstr="";
     var addascii=0;
     var addbig5=0;
    
     strWord=document.getElementById(varWordID).value;      
     strWord=strTrim(strWord);//去空白
     //alert(varWord.value+"___"+intMaxLen+"___"+blnNeedFocus+"___"+strFieldName);
      var oldLen=strWord.length;
      var flg=0;
      var intLen2=0; 
      var maxi = 0;

     if(event.type=="paste"){ // 加上貼上的文字內容
         strWord=strWord+window.clipboardData.getData("Text");
     }
     var chFlg=false;//中文註記
     
     for(var  i=0;i<strWord.length  ;i++){
          var  codes=strWord.charCodeAt(i);
          if(i==oldLen) intLen2=intLen;
          if((codes>=33 && codes<=122) || !isNaN(strWord.charAt(i))){
               addascii++;
               if(chFlg) addascii+=1; // 前一個字是中文(0f)
               chFlg = false;
          }else{            
               addbig5+=2;
               if(!chFlg) addbig5+=1;//前一個字是英文 or 目前為第一個字元且是中文 (0e)
               chFlg = true;
          }
          
          if(addascii+addbig5<=intMaxLen){//小於最大長度
              intLen=addascii+addbig5;
              maxi = i;
          }
      }
      if(addascii+addbig5>intMaxLen){// 超出最大長度
          //alert(strFieldName+"的字數已超過"+intMaxLen+"字! 請減少字數，感謝!");
          strWord.value = strWord.substr(0,maxi);
          //if(event!=null )event.returnValue=false; // event傳回false
          //intLen2=intLen;
          intLen2=addascii+addbig5;
      }else{
          intLen2=intLen; // 長度未超出
      } 
      if(event!=null ){ 
            if(event.type=="keydown"){//經由key in
               if( (event.keyCode==8 || event.keyCode==46) && strWord.length>1){//8:BackSpace ;46:del
                    if(strWord.charCodeAt(strWord.length-1)>=48 && strWord.charCodeAt(strWord.length-1)<=122){
                       intLen2=intLen-1;
                    }else{
                       intLen2=intLen-2;
                    }  
                }else if(event.keyCode>=48 && event.keyCode<=122){
                     intLen2=intLen+1;
                }else{
                     intLen2=intLen+2;
                }
                if(intLen2>intMaxLen ){// 上下左右鍵及Backspace、Del鍵可用
                      if( event.keyCode!=8 && event.keyCode!=46 &&  event.keyCode!=37 &&  event.keyCode!=38 &&  event.keyCode!=39 &&  event.keyCode!=40 ) event.returnValue=false;
                }
           }          
      }
    if(toalID==""){
        return (intMaxLen-intLen2);
    }else{      
        document.getElementById(toalID).innerHTML=(intMaxLen-intLen2);
    }     
}

//*******************************************************************************
//函數名稱：strLtrim
//目        的：去除字串左邊的空白虛格
//參數說明：欲去除空白之字串
//傳  回  值：已去除左邊空白之字串
//副  作  用：無
//備        註：
//範        例：strLtrim("   test")  ==>>傳回【test】
//版        本：1.0  
//作　　者：YaHsueh,Chien  (2004-08-05)
//*******************************************************************************
function strLtrim(instr){
  return instr.replace(/^[\s]*/gi,"");
}

//*******************************************************************************
//函數名稱：strRtrim
//目        的：去除字串右邊的空白虛格
//參數說明：欲去除空白之字串
//傳  回  值：已去除右邊空白之字串
//副  作  用：無
//備        註：
//範        例：strRtrim("test   ")  ==>>傳回【test】
//版        本：1.0  
//作　　者：YaHsueh,Chien  (2004-08-05)
//*******************************************************************************
function strRtrim(instr){
  return instr.replace(/[\s]*$/gi,"");
}

//*******************************************************************************
//函數名稱：strTrim
//目        的：去除字串前後的空白虛格
//參數說明：欲去除空白之字串
//傳  回  值：已去除空白之字串
//副  作  用：無
//備        註：
//範        例：strTrim("    test   ")  ==>>傳回【test】
//版        本：1.0  
//作　　者：YaHsueh,Chien  (2004-08-05)
//*******************************************************************************
function strTrim(instr){
  instr = strLtrim(instr);
  instr = strRtrim(instr);
  return instr;
}

//*******************************************************************************
//函數名稱：Calendar_get_daysofmonth(monthNo, p_year)
//目        的：找出該月最後一天是幾號
//傳入參數：月，年
//傳  回  值：int 日（格式為：１．２．.......）
//限        制：無
//備        註：
//範        例：
//版        本：1.0  
//作　　者：YaHsueh,Chien　(2005-01-24)
//*******************************************************************************
function Calendar_get_daysofmonth(monthNo, p_year) {
    if ((p_year % 4) == 0) {
       if ((p_year % 100) == 0 && (p_year % 400) != 0) return Calendar.DOMonth[monthNo];
	   else return Calendar.lDOMonth[monthNo];
    } else { return Calendar.DOMonth[monthNo]; }
}
//*******************************************************************************
//函數名稱：changeBgColor(newTr,oldColor)
//目        的：滑鼠按下，改變focus列之背景顏色
//參數說明：物件index，舊顏色
//傳  回  值：無
//限        制：
//備        註：
//範        例：
//版        本：1.0
//作　　者：YaHsueh,Chien(2004-08-12)
//*******************************************************************************
var currentTr= 0;
function changeBgColor(newTr,oldColor,newColor) {
//var newColor = '#FFCC99' ;
    //var oldColor= '#ccccff';
    if(currentTr==0 ){               
        newTr.style.backgroundColor = newColor;
        chgTagBgColor(newTr,newColor);
        currentTr=newTr;    
    }else if (currentTr != newTr  ) {
        currentTr.style.backgroundColor =oldColor;
        chgTagBgColor(currentTr,oldColor);
        newTr.style.backgroundColor = newColor;
        chgTagBgColor(newTr,newColor);
        currentTr=newTr;        
    }
}
function chgTagBgColor(objs,newColor){
	var tagObjs = objs.getElementsByTagName("INPUT");
	for (var i=0; i<tagObjs.length; i++){
		tagObjs[i].style.backgroundColor = newColor;
	}
}
//*******************************************************************************
//函數名稱：sortNode(SortNode,strTitle,strTable,strType)
//目        的：將資料依表格欄位排序
//參數說明：按下之物件，排序欄位列物件TR，欲排序表格物件，欲排序之標題類型(如:TD or TH)
//傳  回  值：無
//限        制：需依實際需求更改changeImage(img1)中img的名稱與路徑（不使用圖示則不需變更）
//備        註：
//範        例：1.include KS.js
//                    2.在TR物件上增加onclick 事件，第三個參數可能需要變更，依實際Table所在的層次來更動。
//                       <tr onclick="sortNode(event.srcElement,this,(this.parentElement).parentElement,'TH');" >
//                    3.在標題列中的每個TH中增加一顯示排序狀況的圖示(遞增or遞減)（PS：不使用圖示即可不需增加此行指令）
//                      <TH><img  name="sort" border="0" src="images/down.gif" width="14" height="9" align="texttop"  ></TH>
//版        本：1.0
//作　　者：YaHsueh,Chien(2004-08-12)
//*******************************************************************************
function sortNode(SortNode,strTitle,strTable,strType){
     if(strType==null)  strType="TR";
   // oTR = eval(strTable).childNodes[1].childNodes;//tbody之子集合(即含有資料之列(TR)集合)
    oTR = (strTable.childNodes[1]).getElementsByTagName("tr");//tbody之子集合(即含有資料之列(TR)集合)
     ary = []; 
     var count=0;
     for(var i=0, j=oTR.length; i<j; i++){//collect TR nodes in Array    
                 ary[count]= oTR[i];  
                 count++;
     }    
	  var time = new Date();
	  var tempa, tempb, oTD, orders,tmpIndex=0; 
	  var tmpObj;
	  tmpObj=SortNode;  // 觸發事件之物件
	  //如果點選在表格欄位上，
	  if( SortNode.nodeName==strType){   
	     tmpIndex = SortNode.sourceIndex;
	  }else{//如果點選在非表格欄位上，則往上找到父節點名稱為TD之節點
	      while(tmpObj.sourceIndex >0){
	          tmpObj=tmpObj.parentNode;         
	          if(tmpObj.nodeName==strType){              
	              tmpIndex = tmpObj.sourceIndex;//名稱為TD之節點索引
	              SortNode = tmpObj;//將目前按下之物件更改為其節點名稱為TD之父節點
	              break;
	          } 
	      }
	   }
	   //變更排序圖示=======================================================
	   var fireSrcObjs =tmpObj.getElementsByTagName("span");//取得TD中之img物件
	   for (var j=0;j<fireSrcObjs.length;j++){ //此TD中之img物件迴圈
	 		changeImage(fireSrcObjs[j]);
	   }
	   //===================================================================
	   //計算按下之TD物件為該TR下之第幾個?(從0開始)
	   for(var m=0;m<strTitle.childNodes.length;m++){
	        if(strTitle.childNodes[m].sourceIndex==tmpIndex) oTD=m;
	   }
	  // oTD = SortNode.sourceIndex-SortNode.parentElement.sourceIndex-1;//which TD we Click
  	   if(SortNode.order==null || SortNode.order==""){SortNode.order=1;} // 93/11/01
   	   orders = SortNode.order = -SortNode.order;//遞增或遞減flag
	   //var myary = 'ary'+intCount;
	  ary.sort(
	     function(a,b){       
	   	//tempa = a.getElementsByTagName("td")[oTD];
	    // tempb = b.getElementsByTagName("td")[oTD];  
	      // alert(" a.childNodes[oTD]=="+ a.childNodes[oTD].type+"   "+a.childNodes[oTD].tagName);
	      //  alert(" b.childNodes[oTD]=="+ b.childNodes[oTD].type+"   "+b.childNodes[oTD].tagName);
	       if(a.childNodes[oTD].getElementsByTagName("input").length>0){ // TD中仍包含text的欄位
	            tempa = a.childNodes[oTD].getElementsByTagName("input")[0].value;//you can use   a.childNodes[oTD].innerText.length   or other things to sort
	      		tempb = b.childNodes[oTD].getElementsByTagName("input")[0].value;  
	       }else{ 
	       		tempa = a.childNodes[oTD].innerText;//you can use   a.childNodes[oTD].innerText.length   or other things to sort
	      		tempb = b.childNodes[oTD].innerText;  
	       }  
	    /*  if(isNumber(strTrim(tempa)) || isNumber(strTrim(tempb)) ){ // 其中一項是數字
		          if(!isNumber(strTrim(tempa))){
		                temp =-orders;
		          }else if(!isNumber(strTrim(tempb))){
		                temp =orders;
		          }else{ //兩者皆為數字
		             temp = parseInt(eval(strTrim(tempa)),10 )<parseInt(eval(strTrim(tempb)),10)?-orders:tempa==tempb?0:orders;
		          }
	       }else */
	       if(tempa==null){
	           // alert("tempa 是null");
	             temp=orders;
	       }else if(tempb==null){
	          // alert("tempb 是null");
	             temp=-orders;
	       }
	       else{         
	             //  alert("其它");    
	         	  temp = tempa<tempb?-orders:tempa==tempb?0:orders;
	       } 
	       // temp = tempa<tempb?-orders:tempa==tempb?0:orders;
	       return temp;    
  
	    }
	  )
	  for(i=0; i<oTR.length; i++)
	  oTR[i].swapNode(ary[i]);//swap TR nodes
}

//*******************************************************************************
//函數名稱： changeImage(img1)
//目        的：變更排序圖示
//參數說明：排序圖示物件
//傳  回  值：無
//備        註：
//範        例：
//版        本：1.0
//作　　者：YaHsueh,Chien(2004-08-12)
//*******************************************************************************
function changeImage(span){
   var sortspan=document.getElementsByTagName("span");//sort image物件 
	for(var j=0;j<sortspan.length;j++){
	    sortspan[j].innerHTML = '&nbsp;&nbsp;&nbsp;';
	}
   if (span.getAttribute("sortdir") == 'down') {
        ARROW = '&nbsp;&nbsp;&uarr;';
        span.setAttribute('sortdir','up');
    } else {
        ARROW = '&nbsp;&nbsp;&darr;';
        span.setAttribute('sortdir','down');
    }
    span.innerHTML = ARROW;
}


// EMRQ.js

//function: openPopup(url,top,left,width,height) 開啟視窗，可以指定位置
function openPopup(url,t,l,w,h) {
    var myPopup = '';   
	var fireId = window.event.srcElement.parentElement.parentElement.id
	if (fireId=='')  fireId = 0;			
	
	var para = 'top='+t+', left='+l+', width='+w+', height='+h+', toolbar=0, menubar=0, scrollbars=0, location=0, directories=0, status=0, topmargin=0';
			
	myPopup = window.open(url,fireId,para);		    		
   	myPopup.focus();   			
   	if (!myPopup.opener)
    	myPopup.opener = self; 
} 

function openPopupAutoScroll(url,t,l,w,h) {
    var myPopup = '';   
	var fireId = window.event.srcElement.parentElement.parentElement.id
	
	if (fireId=='')  fireId = 0;		
	//alert("fireId" + fireId +"AA");	
	var para = 'top='+t+', left='+l+', width='+w+', height='+h+', toolbar=0, menubar=0, location=0, directories=0,  topmargin=0';
			
	myPopup = window.open(url,fireId,para);		    		
   	myPopup.focus();   			
   	if (!myPopup.opener)
    	myPopup.opener = self; 
}
function openPopupAutoScrol2(url,t,l,w,h) {
    var myPopup = '';   	
	var para = 'top='+t+', left='+l+', width='+w+', height='+h+', toolbar=0, menubar=0, location=0, directories=0,  topmargin=0';			
	myPopup = window.open(url,'',para);	    		
   	return false;	
   	 
}
//function: doSubmit() 將更改資料傳送至後端伺服器
function doSubmit(dofrmName,  action, target) {
	document.all(dofrmName).action=action;
	document.all(dofrmName).target=target;	
	document.all(dofrmName).submit();
 	//document.forms[0].target= t; 
	//document.forms[0].action= url;
    //document.forms[0].submit();
}
function doDefaultSubmit(url,t) {
 	document.forms[0].target= t; 
	document.forms[0].action= url;
    document.forms[0].submit();
}
//function: count(val) 計算以符號 c 分隔之元素個數
function count(val,c) {
    var i = 0;
	if (val.length > 0) {
		while (val.indexOf(c) > 0) {
		   val = val.substring(val.indexOf(c)+1,val.length);
		   i++;
		} 
		i++;
    }
    return i;
} 
//function: doClose() 開閉視窗
function doClose() {
    window.close();    
} 

//********************************************************************************
//函數名稱：chkTimeFormat(tmObj)	
//目　　的：檢查時間格式是否正確（0800==>08:00）
//參數說明：
//傳回值  ：
//副作用  ：無
//備   註 ：無
//範   例 ：chkTimeFormat('0800')
//*******************************************************************************
function chkTimeFormat(tmObj){
	var time = tmObj.value.substring(0,2);
	var minute;
	var chk = true;
	checkIsNum(tmObj);
	if (tmObj.value.length==4){
			//tmObj.value = time+":"+tmObj.value.substring(2);
			minute = tmObj.value.substring(2);
	}else if (tmObj.value.length==5){
		minute = tmObj.value.substring(3);
	}else{
		alert("時間格式需為: ttmm 或 tt:mm !!");
		tmObj.value = "";
		chk = false;
	}
	//alert("chk1"+ chk);
	//check 時
	if(time>24 || time<0){
		alert("時間數值需介於 00 ~ 24");
		chk = false;
	}
	
	//check 分鐘
	if(minute>60 || minute<0){
		alert("分鐘數值需介於 00 ~ 60");
		chk = false;
	}
	if(chk){
		tmObj.value = time+":"+minute;
	}else{
		tmObj.value = "";
		tmObj.focus();
	}
	//alert("chk2"+ chk);
	return chk;
}

//回傳兩個日期差(塞入後者日期較大)
function dayRange1(fromDateId, toDateId){
	var objFromDate=fromDateId;
	var objToDate=toDateId;
	var arrFromDate=objFromDate.split('-');
	var arrToDate=objToDate.split('-');
	var dtFromDate = new Date();
	var dtToDate = new Date();
	dtFromDate.setFullYear(parseInt(arrFromDate[0],10));
	dtFromDate.setMonth(parseInt(arrFromDate[1],10)-1);
	dtFromDate.setDate(parseInt(arrFromDate[2],10));
	dtToDate.setFullYear(parseInt(arrToDate[0],10));
	dtToDate.setMonth(parseInt(arrToDate[1],10)-1);
	dtToDate.setDate(parseInt(arrToDate[2],10));
	var difference=dtToDate.getTime() - dtFromDate.getTime();  		
	difference=Math.round(difference/(1000*60*60*24));
	return difference; 
}


//return the value of the radio button that is checked
//return an empty string if none are checked, or
//there are no radio buttons
function getCheckedValue(radioObj) {
	if(!radioObj)
		return "";
	var radioLength = radioObj.length;
	if(radioLength == undefined)
		if(radioObj.checked)
			return radioObj.value;
		else
			return "";
	for(var i = 0; i < radioLength; i++) {
		if(radioObj[i].checked) {
			return radioObj[i].value;
		}
	}
	return "";
}
function replaceDefaultDate(oldDate){
	var newDate="";
	if(oldDate=="2910-12-31" || oldDate=="9999-12-31"){
		newDate="";
	}else{
		newDate=oldDate;
	}
	return newDate;
}
//*******************************************************************************
//函數名稱：isIdNo(strIdno)
//目    的：檢查身分証字號
//傳入參數：id身份証字號
//傳 回 值：true：身份証字號正確/false：身份証字號錯誤
//副 作 用：無
//備    註：
//範    例：IsId('L121478918') ==>>傳回false
//版    本：1.0  
//作　　者：(2004-08-05)
//*******************************************************************************
function isIdNo(id) {
	if(typeof(id)=='object') {id=id.value;}
	else if (typeof(id)=='string') {}
	else {return false;}	
 check:{
	if (id.length != 10) break check;
	var no=id.split("");
	no[0]=no[0].toUpperCase();
	if ((no[0]<"A") || (no[0]>"Z")) break check;
	if ((no[1]<"1") || (no[1]>"2")) break check;
	for (var i=1;i<10;i++) if ((no[i]<"0" || no[i]>"9")) break check; else no[i]=Number(no[i]);
	no[0]=no[0].charCodeAt(0);
	if (no[0]<73) no[0]-=55;
	   else if (no[0]==73) no[0]=34;
	      else if (no[0]<79) no[0]-=56;
	         else if (no[0]==79) no[0]=35;
	            else if (no[0]<87) no[0]-=57;
	               else if (no[0]==87) no[0]=32;
	                  else if (no[0<90]) no[0]-=58; else no[0]=33;
	var sum=Math.floor((no[0]/10))+(no[0]%10)*9;
	for (var i=1;i<9;i++) sum+=no[i]*(9-i);
	sum+=no[9];
	if (sum%10 != 0) break check;
	return true;
 }
	alert("您輸入的身分證字號不正確!\n請重新輸入,謝謝!!");
	//id.value="";
	//id.focus();
	return false;
}