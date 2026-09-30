<!DOCTYPE HTML>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<title>薪資異動檔</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<jsp:include page="../include.jsp"></jsp:include>
<script>
//檢查使否有代理人，如有代理人則取代原資料
var agent = "${sessionScope.agent}";	//代理人卡號
var paUserid = ("" != agent) ? agent : userid;            //使用者卡號
var paUsername = ("" != agent) ? "${sessionScope.agentname}" : username;            //使用者姓名
var pahid = ("" != agent) ? "${sessionScope.agenthid}" : hid;            //醫院別
var pacostid = ("" != agent) ? "${sessionScope.agentcostid}" : costid;            //使用者成本中心
var padeptnm = ("" != agent) ? "${sessionScope.agentdeptnm}" : deptnm;            //使用者單位名稱
var padeptno = ("" != agent) ? "${sessionScope.agentdeptno}" : deptno;            //使用者單位名稱 英文
var today = new Date();

var tabid="";
var objCopy;
var patradt;
var salnum;
var patracod;
var code_data;
var in_data;
var itmTbl;
var oTable;
var uTable;
var payTbl;
var rowData;
var rowIndex=0;
var delCheck;
var pk_data;
var i;
var o_basPay;
var salNm;
var selEn;
var selCode;
var traItm;
//考績獎金月數
var pflgAOptions = {
    '0' : '0',
    '1' : '0.5',
    '2' : '1',
    '3' : '1.5',
    '4' : '2'
};
//年終獎金比例
var pflgBOptions = {
    '0' : '0',
    '1' : '1/3',
    '2' : '2/3',
    '3' : '1',
    '4' : '3/4'
};
//本功別
var rank3Options = [
  { val: "0", text: "" },{ val: "1", text: "本" },{ val: "2", text: "功" }
];
//職等
var rnk2nOptions = [
    { val: "00", text: "00" },{ val: "01", text: "01" },{ val: "02", text: "02" },{ val: "03", text: "03" },{ val: "04", text: "04" }, { val: "05", text: "05" },{ val: "06", text: "06" }, { val: "07", text: "07" },
    { val: "08", text: "08" },{ val: "09", text: "09" },{ val: "10", text: "10" },{ val: "11", text: "11" },{ val: "12", text: "12" }, { val: "13", text: "13" },{ val: "14", text: "14" }, { val: "P1", text: "P1" },
    { val: "P2", text: "P2" },{ val: "P3", text: "P3" },{ val: "T1", text: "T1" }
];

$(function(){
	$(".gclsCalTxt").datepicker({ 
		changeMonth: true,
      	changeYear: true,
      	dateFormat:"yy-mm-dd"
    });
    
    $(".dateT").datepicker({ 
		changeMonth: true,
      	changeYear: true,
      	showOtherMonths : true,
	    hideIfNoPrevNext : true,
	    showAnim:"blind",
      	dateFormat:"yy-mm-dd",
      	minDate: 0, 
      	maxDate:"+3M"
    });
    
      	
    $('body').on('focus', '.datepicker', function() { //以此方式才能動態更改頁面datepicker
          $(this).datepicker( { dateFormat: 'yy-mm-dd' } );
    });
    $('body').on('change keyup', '.keypay', function() {
		$.sumPay(); 
	});  	
	$(".selectpicker").selectpicker({
		noneSelectedText : '請選擇'//預設顯示內容
    });
    //初始化重新整理資料
	$(window).on('load', function() {
		$('.selectpicker').selectpicker('refresh');
	});
//查詢角色,將編輯功能隱藏
	if (pa_view=="Y"){
		$(".roleQry").addClass("d-none");	
	}else{
		$(".roleQry").removeClass("d-none");
	}
	//編輯角色,將編輯功能show
	if ((pa_admin=="Y" && agent =="") || pa_edit=="Y" || (agent !="" && "${sessionScope.agentEdit}"=="Y" )){
		$(".roleQry").removeClass("d-none");	
	}else{
		$(".roleQry").addClass("d-none");
	}
	
	$.getLoadCodeTbl = function(item,flag){		
		$.ajax({
			url:"${pageContext.request.contextPath}/salController.do",
			data:{
				method  : 'getLoadCodeTbl',
 				item    : item,
 				flag    : flag,
 				hid     : pahid
			},
			type:"POST",			//type:POST, GET(default:GET)
			dataType:"json",	//dataType: text, xml, html, script, json, jsonp
			contentType: "application/x-www-form-urlencoded;charset=utf-8",
			success:function(data) {
				var options ="";
				var select;
				//資料賦值
				/*
				if (data.alBankDtTbl[0]!=null){
					select = $('#tran_salnum');
					select.empty();
					options = "<option value='0'>0";
					select.append(options);
					$.each(data.alBankDtTbl, function(idx) {
						options = "<option value='" + data.alBankDtTbl[idx].salnum + "'>" + data.alBankDtTbl[idx].salnum ;
						select.append(options);
					});
					select.selectpicker('refresh'); 
					
					select = $('#tran_patradt,#filter_patradt');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alBankDtTbl, function(idx) {
						options = "<option value='" + data.alBankDtTbl[idx].paybank.dts + "'>" + data.alBankDtTbl[idx].paybank.dts ;
						select.append(options);
					});
					select.selectpicker('refresh');
				}*/
				//餉單編號(2個月內)
				if (data.alBankDtTbl_2M[0]!=null){
					select = $('#handingA_tradt,#handingB_tradt');
					select.empty();
					select.append(options);
					$.each(data.alBankDtTbl_2M, function(idx) {
						options = "<option value='" + data.alBankDtTbl_2M[idx].paybank.dts +"'>" + data.alBankDtTbl_2M[idx].paybank.dts;
						select.append(options);
					});
				}
									
			 	if (data.alTitleCodeTbl[0]!=null){
					select = $('#paytitle,#paotitle');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alTitleCodeTbl, function(idx) {
						options = "<option value='" + data.alTitleCodeTbl[idx].paytitlk + "'>" + data.alTitleCodeTbl[idx].paytitlk+"-"+data.alTitleCodeTbl[idx].patitsub+"-"+data.alTitleCodeTbl[idx].paytitlc ;
						select.append(options);
					});
					select.selectpicker('refresh');
				}
				if (data.alRankCodeTbl[0]!=null){
					select = $('#payrank,#paorank');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alRankCodeTbl, function(idx) {
						options = "<option value='" + data.alRankCodeTbl[idx].rankcode + "'>" + data.alRankCodeTbl[idx].rankcode+"-"+data.alRankCodeTbl[idx].rankname ;
						select.append(options);
					});
					//select.selectpicker('refresh');
				}
				//適用專業加給表
				if (data.alRnk4Tbl[0]!=null){
					select = $('#paorank4,#payrank4');
					select.empty();
					options = "<option value=''>"; 
					select.append(options);
					$.each(data.alRnk4Tbl, function(idx) {
						options = "<option value='" + data.alRnk4Tbl[idx].rnk4key + "'>" + data.alRnk4Tbl[idx].rnk4name ;
						select.append(options);
					});
					//select.selectpicker('refresh');
				}
				if (data.alDeptCodeTbl[0]!=null){
					select = $('#tran_dept,#filter_dept,#paydept');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alDeptCodeTbl, function(idx) {
						options = "<option value='" + data.alDeptCodeTbl[idx].padepcod + "'>" + data.alDeptCodeTbl[idx].pacstno+"-"+data.alDeptCodeTbl[idx].padepnmc ;
						select.append(options);
					});
					select.selectpicker('refresh');
					
				}
				if (data.alexpfTbl[0]!=null){
					select = $('#payexpfkey1,#payexpfkey2,#payexpfkey3,#paoexpfkey1,#paoexpfkey2,#paoexpfkey3');
					select.empty();
					$.each(data.alexpfTbl, function(idx) {
						options = "<option value='" + data.alexpfTbl[idx].expfkey + "'>" + data.alexpfTbl[idx].expfname ;
						select.append(options);
					});
					select.selectpicker('refresh');
				}
				if (data.altracdTbl[0]!=null){
					select = $('#tran_patracd,#filter_patracd');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.altracdTbl, function(idx) {
						options = "<option value='" + data.altracdTbl[idx].tracode + "'>" + data.altracdTbl[idx].tracode+" "+data.altracdTbl[idx].traname ;
						select.append(options);
					});
					select.selectpicker('refresh');
				}
				if (data.altracdTbl[0]!=null){
					select = $('#patracod');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.altracdTbl, function(idx) {
						options = "<option value='" + data.altracdTbl[idx].tracode + "'>" + data.altracdTbl[idx].tracode+" "+data.altracdTbl[idx].traname ;
						select.append(options);
					});
				}
				if (data.alpayitmTbl[0]!=null){
					var payitmHtml ='<option value="">請選擇</option>';
					select = $('#patrapay_paycode');
					//select = $('.paycodeSelect');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alpayitmTbl, function(idx) {
						options = "<option value='" + data.alpayitmTbl[idx].paycode + "'>" + data.alpayitmTbl[idx].paycode+" "+data.alpayitmTbl[idx].pacodnmc ;
						payitmHtml = payitmHtml + "<option value='" + data.alpayitmTbl[idx].paycode +":"+ data.alpayitmTbl[idx].payauth +":"+data.alpayitmTbl[idx].payiftax + "'>" + data.alpayitmTbl[idx].paycode+" "+data.alpayitmTbl[idx].pacodnmc ;
 						select.append(options);
					});
					//console.log(payitmHtml);
 					$('.paycodeSelect').html( payitmHtml ); //設定支扣代碼
					select.selectpicker('refresh');
				}
				
				//職等,權理,主管權理
				select = $('#paornk2n,#payrnk2n,#payrank2,#paorank2,#paorankc,#payrankc');
				select.empty();
				options = "<option value=''>請擇擇";
				$.each(rnk2nOptions, function(index, one) {			    
				    options += "<option value='" + one.val + "'>" + one.text ;
				});
				select.append(options);
				
				//本功別
				select = $('#paorank3,#payrank3');
				select.empty();
				options = "<option value=''>請擇擇";
				$.each(rank3Options, function(index, one) {			    
				    options += "<option value='" + one.val + "'>" + one.text ;					
				});
				select.append(options);
				//級別
				select = $('#paodegr2,#paydegr2');
				select.empty();
				options = "<option value=''>請擇擇";
				if (pahid=="1A0" || pahid =="3A0"){
					for (i=1; i<=40;i++) { //高榮 聯醫1-40  
						options += "<option value='"+i+"'>"+i+"</option>";						
					}
				}else if (pahid=="2A0"){
					for (i=201; i<=240;i++) { // 屏榮201-240 1-26 103-136 
						options += "<option value='"+i+"'>"+i+"</option>";						
					}
					for (i=1; i<=40;i++) {  
						options += "<option value='"+i+"'>"+i+"</option>";
					}
					for (i=101; i<=140;i++) {  
						options += "<option value='"+i+"'>"+i+"</option>";
					}	
				}
				select.append(options);
				//健保投保級距
				if (data.alDpinTbl[0]!=null){
					select = $('#insuamt');
					select.empty();
					options = "<option value=''>請擇擇";
					select.append(options);
					$.each(data.alDpinTbl, function(idx) {
						options = "<option value='" + data.alDpinTbl[idx].dpinhigh + "'>" + data.alDpinTbl[idx].dpinhigh ;
						select.append(options);
					});	
				}
			}  
		});
	};
 	/*預先載入代碼檔 */
  	$.getLoadCodeTbl("tranPay","Y");
  	
	//代碼選單切換
	$("#qryType").change(function(){
		$("#divfield_1,#divfield_2,#explan_1,#explan_2,#explan_3,#explan_4").addClass("d-none");	//隱藏
  		selCode=(typeof($("#qryType option:selected").val().split(':')[0]) == "undefined" )? "1":$("#qryType option:selected").val().split(':')[0];
  		console.log(selCode);
  		if (selCode=="1" ){			//1.[匯入]晉級調薪 
			$("#divfield_1").removeClass("d-none");
			$("#explan_1").removeClass("d-none");
		}
		else if (selCode=="2"){		//2.[刪除]晉級調薪 
			$("#divfield_1").removeClass("d-none");
			$("#explan_2").removeClass("d-none");
		}
		else if (selCode=="3" ){	//3:[匯入]待遇調整:ImpRemuInTra
			$("#divfield_2").removeClass("d-none");
			$("#explan_3").removeClass("d-none");
			$("#handingB_monno_lbl,#handingB_monno,#handingB_effdt_lbl,#handingB_effdt").removeClass("d-none");
		}
		else if (selCode=="4"){		//4:[刪除]待遇調整:DelRemuInTra
			$("#divfield_2").removeClass("d-none");
			$("#explan_4").removeClass("d-none");
			$("#handingB_monno_lbl,#handingB_monno,#handingB_effdt_lbl,#handingB_effdt").addClass("d-none");
		}	 
  	});
  	 	
	//編輯欄位檢查
  	$.checkFields = function(btn){
  		msg = '';
  		
  		if (btn== 'btn_qry_tran_TranPsn'){	//異動資料_查詢
  		//( $("#tran_salnum option:selected").val()=='' || typeof($("#tran_salnum option:selected").val())=='undefined' ) &&
  		//( $("#tran_patradt option:selected").val()=='' || typeof($("#tran_patradt option:selected").val())=='undefined' ) &&
	 		if($("#tran_card").val()=='' && $("#tran_idno").val()=='' && $("#tran_namec").val()=='' && $("#tran_empno").val()=='' && 
	 		   $("#tran_salnum").val()=='' && $("#tran_patradt").val()=='' &&
	 		  ( $("#tran_patracd option:selected").val()=='' || typeof($("#tran_patracd option:selected").val())=='undefined' ) &&
	 		  ( $("#tran_dept option:selected").val()=='' || typeof($("#tran_dept option:selected").val())=='undefined' )){
	 			msg = '請輸入條件值!!';
	 		}	
 		}
 		else if (btn== 'subPATRAPSN_Insbtn'){		
 		
		}
		else if (btn== 'btn_qry_filter_TranPsn'){	//異動資料_篩選_查詢
			//查詢項:全部空白
			//($("#filter_patradt option:selected").val()=='' || typeof($("#filter_patradt option:selected").val())=='undefined') &&
	 		if($("#filter_lvefg option:selected").val()=='' && $("#filter_card").val()=='' && $("#filter_idno").val()=='' && $("#filter_namec").val()=='' && $("#filter_empno").val()=='' &&
               $("#filter_paeffdt_bng").val()=='' && $("#filter_paeffdt_end").val()=='' && $("#filter_patradt").val()=='' &&
               ($("#filter_dept option:selected").val()=='' || typeof($("#filter_dept option:selected").val())=='undefined') &&
               ($("#filter_title option:selected").val()=='' || typeof($("#filter_title option:selected").val())=='undefined') &&
               ($("#filter_payrank option:selected").val()=='' || typeof($("#filter_payrank option:selected").val())=='undefined') &&
               ($("#filter_patracd option:selected").val()=='' || typeof($("#filter_patracd option:selected").val())=='undefined') &&
               $("#filter_pacsflag option:selected").val()==''&& $("#filter_expfsp option:selected").val()=='' &&  
               $("#filter_expfkey1").val()==''&& $("#filter_expfkey2").val()=='' && $("#filter_expfkey3").val()=='' &&  
	 		   $("#filter_paret option:selected").val()=='' &&  $("#filter_inflg option:selected").val()=='' 
	 		   ) {
	 			msg = '請輸入條件值!!';
	 		}
	 		//只有KEY 離職否
	 		else if ($("#filter_lvefg option:selected").val()!='' && $("#filter_card").val()=='' && $("#filter_idno").val()=='' && $("#filter_namec").val()=='' && $("#filter_empno").val()=='' &&
               $("#filter_paeffdt_bng").val()=='' && $("#filter_paeffdt_end").val()=='' && $("#filter_patradt").val()=='' &&
               ($("#filter_dept option:selected").val()=='' || typeof($("#filter_dept option:selected").val())=='undefined') &&
               ($("#filter_title option:selected").val()=='' || typeof($("#filter_title option:selected").val())=='undefined') &&
               ($("#filter_payrank option:selected").val()=='' || typeof($("#filter_payrank option:selected").val())=='undefined') &&
               ($("#filter_patracd option:selected").val()=='' || typeof($("#filter_patracd option:selected").val())=='undefined') &&
               $("#filter_pacsflag option:selected").val()==''&& $("#filter_expfsp option:selected").val()=='' &&  
               $("#filter_expfkey1").val()==''&& $("#filter_expfkey2").val()=='' && $("#filter_expfkey3").val()=='' &&  
	 		   $("#filter_paret option:selected").val()=='' &&  $("#filter_inflg option:selected").val()=='' 
	 		   ){
	 		   msg = '請輸入條件值太少!!'
	 		}
	 		//生效日起訖
	 		else if($("#filter_patradt").val()=='' && ($("#filter_paeffdt_bng").val()=='' || $("#filter_paeffdt_end").val()=='') ){
	 		   msg = '請輸入生效日起訖!!'
	 		}	
 		}
 		else if (btn== 'btn_calDegre'){	//取得新支扣--依新階別標準表,專業加給標準表,補發扣
 			if($("#patradt").val()=='')
 				msg = '請輸入[發薪日]!!';
 			if($("#paygovdt").val()=='')
 				msg += '請輸入[初任公職日]!!';
 			if($("#paeffdt").val()=='')
 				msg += '請輸入[生效日]!!';	
 			else if( ($("#paymonno").val() == '' && $("#paydayno").val() == '') && ($("#paeffdt").val()=='' ) )
 				msg += '請輸入[生效日]!!';
 			if (hid=="1A0" && $("#payidno").val()=='')//要擷取人事保險檔,要有IDNO
 				msg += '請輸入[身份證字號]!!';
 			patracod=getInt($("#patracod option:selected").val());
			if (patracod >=21 && patracod <=22){//21離職 22退出
				if ($("#payrsgdt").val()=='' ){
					msg += "請輸入[離職日]";
				}
			}	
			else if (patracod ==51){//年終獎金
				if ( $("#paymonno").val() == '' || $("#paypflag option:selected").val()== '' ) {
					msg += "請輸入[核發月數]/[發放比例]";
				}
			}	
			else if (patracod ==12){//考績獎金
				if ( $("#paypflag option:selected").val()== '' ){
					msg += "請輸入[考績獎金月數]";
				}
				if ( $("#paypflag option:selected").val()== '0' ){
					msg += "[考績獎金月數]=0,請確認.";
				}
			} 
 
 		}
 		else if (btn == 'btn_handelTra'){	//異動處理--1[匯入]晉級調薪,2[刪除]晉級調薪,3[匯入]待遇調整 4[刪除]待遇調整
			if (typeof($("#qryType option:selected").val())=='undefined'){
  				msg = '請勾選[選單]項目!!';
  			}
  			else{
  				selCode=$("#qryType option:selected").val().split(':')[0];
  				if (selCode=='1' || selCode=='2'){		//1:[匯入]晉級調薪 2:[刪除]晉級調薪
  					if ($("#handingA_tradt").val()=='' ){
		  				msg += '請勾選[發薪日]!!';
		  			}
  				}
				else if (selCode=='3' || selCode=='4'){		//3[匯入]待遇調整 4[刪除]待遇調整
					if ($("#handingB_tradt").val()=='' ){
		  				msg += '請勾選[發薪日]!!';
		  			}
					if (typeof($("#handingB_range option:selected").val())=='undefined'){
						msg += '請勾選[範圍]!!';
					}	
					if( selCode=='3' ){
						if ($("#handingB_effdt").val()=='' ){
			  				msg += '請勾選[生效日]!!';
			  			}
						if ($("#handingB_monno").val()=='' ){
		  					msg += '[追補月數]空白!!';
		  				}	
					}
				}	
  			}
		}
  		return msg;	
	};
	
	//讀取table欄位內容
  	$.getDataInfo = function(tblname){
		var array = new Array();
		var value = [];
		var obj = {};
		var $th = $('#'+ tblname + ' th');	
		var data_val;
		var data_id;
		var data_nm;	 
		var data_item;
		$( 'select,:input,:checkbox', '#'+tblname).each(function() {
	        //data_id = String($(this).attr("id")).split('_')[1];
	        data_id  = $(this).attr("id");
    		data_val = $(this).val();
    		
    		if (typeof data_id === "undefined" || data_id == "undefined") {
	        	
	        }else{
// 	        	if ($(this).is('[readonly]')){	//readonly不處理
// 	        	}
// 	        	else{
	        		if ( null == data_val ){
		        		//因radio取不到val值，故取label
		    			if ($(this).is(":checked")){
						  	data_val = $(this).next("label").text();
						}
						else{
							data_val = "";
						}
		        	}
		        	else if ($(this).is(":checkbox")){
		    			if ($(this).is(":checked")){
			    			data_val = "Y";
			    		}else{
			    			data_val = "N";
			    		}
			    	}else
			    		data_val=data_val.toUpperCase(); 
			    		
	        		obj[data_id] = data_val; //名稱+數值  
	        	//}
    		}
	       	
	    });
	    
   		value.push(obj);   	//以整支table寫入	
   		array.push( { "formnm": tblname, "value": value } ); //將動態表格資料轉換為json		    
		 
		//儲存動態表格中的資料
	   $( ' .dynamic' ).each(function() {
	    	tableID = $(this).closest('table').attr('id'); // 表格名稱
	    	value = [];
	    	$th = $('#'+ tableID + ' th');
	    		    	
	   		$('#' + tableID + ' tbody tr').each(function(i, tr){
	   			obj = {}, $tds = $(tr).find('td');
	   			data_val="";
			    $th.each(function(index, th){
			    	data_id = $tds.eq(index).children('input:text, select, textarea, input:hidden').attr('id');
			    	data_nm = $tds.eq(index).children('input:text, select, textarea, input:hidden').attr('name');
			    	
			    	//var indexName = (index.toString().length==1) ? "0"+(index) : (index);
			    	if (typeof data_id === "undefined" || data_id == "undefined" || data_id =='') {
	        	
	        		}else{
	        			//id前加o_(原支扣明細) n_(新支扣明細) paycode.第2碼=P,收入,要取name=payamtP paycode.第2碼=D,支出,要取name=payamtD
	        			if (data_id.substring(0,2)=="o_" || data_id.substring(0,2)=="n_")
	        				data_id=data_id.substring(2);
	        			data_val = $tds.eq(index).children('input[type="text"], select, textarea, input:hidden').val();
				        if(data_val != null && data_val != "" ) {//只儲存有資料
				        	data_val=data_val.toUpperCase();//轉大寫
				        	if ( data_id=="paycode" )	//取代碼內容,依第2碼 //data_item=依paycode判斷data_val要取P D	
	        					data_item = data_val.substring(1,2);
				        	
				        	if ( data_id=="payamt"){	//金額,P取[所得欄],D取[支出欄]	ex. FP000抓取payamtP,FD020取payamtD
				        		if ( data_item !="" && data_item != data_nm.substring(6,7) )
				        			data_val="";
				        	}
				        	if (data_val != ""){
				        		obj[data_id] = data_val; //名稱+數值
				        	}	
				        }	
				     }
				     //console.log(i+" data_id:"+data_id+" data_name:"+data_nm+" data_val:"+data_val);   
			    });
			   
			    if (obj != null  ){
			    	value.push(obj);
			    }	
	   		});
	   		
	   		if (obj != null  ){   		
	   			array.push( { "formnm": tableID, "value": value } ); //將動態表格資料轉換為json
	   		}		   		
	    }); 
		    
  		var jsonArray = JSON.stringify(array); // 轉換成json
	    	    
	    console.log(jsonArray);
	    alert("資料送出");
	    
	    return jsonArray;
	};
	
	//將下拉式選單,多選,存成字串,以[,]分開
	$.getSelectVals = function(item){
		var selectedValues = "";
		$("#"+item+" option:selected").each(function () {
			if (selectedValues!="") selectedValues=selectedValues+",";
		    selectedValues=selectedValues + $(this).val();
		});
		return selectedValues;
	}	
	//jQuery按紐圖示
	$(".btn").button().click(
		function(event) {event.preventDefault();}
	);
	$(".btn").click(function(){
		var id = $(this).attr('id');
		
		if(id == 'btn_cln_tran_TranPsn'){
			cleanTable("byTranDetailMain");
			//pa.paTrapay異動支扣明細--新
			cleanTable("TraPayN");
			//pa.paTrapay異動支扣明細--原
			cleanTable("TraPayO");
			 //文字格有紅色,要移除 
			$(".bgcol").removeClass('bg-danger text-white');
			
			$('.selectpicker').selectpicker('val', '');
			$('.selectpicker').selectpicker('refresh');
			
		    $("#subPATRAPSN_Updbtn,#subPATRAPSN_Delbtn,#subPATRAPSN_Insbtn").prop("disabled", true);
		    $("#tblTranPsnList").dataTable().fnClearTable();
		    //button啟用
			$("#subPATRAPSN_Newbtn").prop("disabled", false);  
		    
			return false;
		}
		else if( id == 'btn_cln_filter_TranPsn'){
			cleanTable("byTranFilterMain");
			$('.selectpicker').selectpicker('val', '');
			$('.selectpicker').selectpicker('refresh');			
			$("#tblTranPsnFilter").dataTable().fnClearTable();
			return false;			
		}
		else if( id == 'subPATRAPSN_Newbtn'){	//異動資料_新增一筆
			o_basPay="";//清空原支支扣
			//清空
			cleanTable("byTranDetailMain");
			//button啟用
			$("#subPATRAPSN_Insbtn").prop("disabled", false);
			$("#btn_calDegre").prop("disabled", false);
			//button禁用
			$("#subPATRAPSN_Newbtn,#subPATRAPSN_Updbtn,#subPATRAPSN_Delbtn").prop("disabled", true);
			
			$('.selectpicker').selectpicker('val', '');
			$('.selectpicker').selectpicker('refresh');
			
			//key
			$("#patradt").attr("readOnly",false);	
			$("#patracod").attr("disabled",false);	
			$("#payidno").attr("readOnly",false);
			$("#hid").val(pahid);
			
			$("label.error").hide();
			$(".error").removeClass("error");
			//移至
			$("#patradt").focus();
		}
		else if(id == 'btn_qry_tran_TranPsn'){		//異動資料_查詢
			msg = $.checkFields(id);
			if(msg == ''){
				//salnum=$.getSelectVals("tran_salnum");
				//patradt=$.getSelectVals("tran_patradt");
				patradt=$("#tran_patradt").val();
				salnum=$("#tran_salnum").val();
				patradt=$("#tran_patradt").val();
				patracod=$.getSelectVals("tran_patracd");
				var cardno=$("#tran_card").val().toUpperCase();
				var idtno=$("#tran_idno").val().toUpperCase();
				var namec=$("#tran_namec").val();
				var empno=$("#tran_empno").val().toUpperCase();				
				var paydept=$.getSelectVals("tran_dept");
				
 				code_data="";
				code_data={
						salnum     :salnum,
						patradt    :patradt,
		        		cardno     :cardno,
		        		idtno      :idtno,
		        		namec      :namec,
		        		empno      :empno,
		        		patracod   :patracod,
		        		paydept    :paydept
		        	};
				console.log("異動資料查詢:code_data:"+JSON.stringify(code_data));
				//return false;
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'getTranPsnList',
						item    : '1',
						code_data :JSON.stringify(code_data),
						hid     : pahid,
						userid  : paUserid
					},
					type:"POST",	//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						//更新查詢清單
						$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
						$('#tblTranPsnList').show();
						oTable = $('#tblTranPsnList').dataTable(objCopyA);
						
						oTable.fnClearTable(0);		//清空資料
								
						if (data.traPsnList!=null && data.traPsnList.length>0){	
							oTable.fnAddData(data.traPsnList);//重新給資料
						}else{
							alert("無資料");
							$("#btn_cln_tran_TranPsn").trigger( "click" );
						} 
						oTable.fnDraw();
						
						//以rowIndex優先,若無,設為第1筆
						if (rowIndex != 0){
							$("#tblTranPsnList tbody tr:first").find("a").eq(rowIndex).trigger("click");
							rowIndex = 0;
						}else
							$("#tblTranPsnList tbody tr:first").find("a").eq(0).trigger("click");
						
					},
					error: function(xhr){					
						alert("查詢失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
					}
				});
			}
			else{
				alert(msg);
				return false;
			}
		}
		else if (id=='btn_addpay'){	//新增支扣代碼
			$(":text,textarea PATRAPAY").val("");
			$("label.error").hide();
			$('#patrapay_paycode').selectpicker('val', [1]);			
 			$(".error").removeClass("error");
 			$("#btn_subTraPayIns").prop("disabled", false);
			$("#btn_subTraPayUpd").prop("disabled", true);
			$("#model_header_title").html("新增[新支扣代碼]");
			$("#patrapay_payidno").val($("#payidno").val());
			$("#patrapay_tranum").val($("#tranum").val());
			$("#patrapay_paysort").val("N");
			$("#patrapay_patradt").val($("#patradt").val());
			$("#patrapay_patradt").attr("readOnly",false);	//將key.readOnly 取消
			$("#patrapay_paysort").attr("readOnly",false);
			$("#patrapay_paycode").attr("readOnly",false);
			
		}
		else if (id=='btn_subTraPayIns'||id=='btn_subTraPayUpd'){	//新增支扣代碼
			tblname="PATRAPAY";
			if ($("#subtraPayForm").valid()){
				msg = $.checkFields(id);
				if(msg == ''){
					var jsonString = $.getDataInfo(tblname); //取得目前的資料
					if (id =='btn_subTraPayUpd')		
						$('#citem').val("update");
					else if (id =='btn_subTblCodeIns')		
						$('#citem').val("insert");
						
					/* oTable = $('#' + codeTbl ).dataTable().api();
					uTable = $('#' + codeTbl ).dataTable();  	
					info = oTable.page.info();		//記錄目前頁碼
					currentPage=info.page;
					
					$.ajax({
						url:"${pageContext.request.contextPath}/salController.do",
						data:{
							method  : 'updTblCode',
							tabJson : jsonString,
							item    : $('#citem').val()
						},
						type:"POST",			//type:POST, GET(default:GET)
						dataType:"json",	//dataType: text, xml, html, script, json, jsonp
						async: true,	//逐步執行
						contentType: "application/x-www-form-urlencoded;charset=utf-8",  
						success:function(data) {
							if (data.status!=null||typeof(data.status)!="undefined"){	//show msg,dialog不用關閉
								alert(data.msg);
							}else{
								$("#addCodeDialog").modal("hide");
								uTable.fnClearTable(0);		//清空資料
								
								$("#btn_qryCode").trigger( "click" );  
								uTable.fnPageChange(currentPage);		//切到原先某碼
								
								if (data.msg!=null){
									alert(data.msg);
								}
							}	
						},
						error: function(xhr){					
							alert("代碼維護"+$('#citem').val()+"失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
						} 
					});*/
				}
				else{
					alert(msg);
					return false;
				}
			}
		}
		else if(id == 'btn_qry_filter_TranPsn'){		//異動資料_篩選_查詢
			msg = $.checkFields(id);
			if(msg == ''){
				var cardno=$("#filter_card").val().toUpperCase();
				var idtno=$("#filter_idno").val().toUpperCase();
				var namec=$("#filter_namec").val();
				var empno=$("#filter_empno").val().toUpperCase();
				var effdt_bng=$("#filter_paeffdt_bng").val();
				var effdt_end=$("#filter_paeffdt_end").val();
				var lvefg=$("#filter_lvefg option:selected").val();
				var csflag=$("#filter_pacsflag option:selected").val();
				var expfsp=$("#filter_expfsp option:selected").val();
				var paret=$("#filter_paret option:selected").val();
				//var patracod=$("#filter_patracd option:selected").val();
				patracod=$.getSelectVals("filter_patracd");
				var inflg=$("#filter_inflg option:selected").val();
				//patradt=$.getSelectVals("filter_patradt");
				patradt=$("#filter_patradt").val();
				var payrank=$.getSelectVals("filter_payrank");
				var paytitle=$.getSelectVals("filter_title");
				var paydept=$.getSelectVals("filter_dept");
				var expfkey1=$.getSelectVals("filter_expfkey1");
// 				var expfkey2=$.getSelectVals("filter_expfkey2");
// 				var expfkey3=$.getSelectVals("filter_expfkey3");
				
 				code_data="";
				code_data={
						lvefg	   :lvefg,
		        		cardno     :cardno,
		        		idtno      :idtno,
		        		namec      :namec,
		        		empno      :empno,
		        		patradt    :patradt,
		        		payrank    :payrank,
		        		paytitle   :paytitle,
		        		paydept    :paydept,
		        		expfkey1   :expfkey1,
// 		        		expfkey2   :expfkey2,
// 		        		expfkey3   :expfkey3,
		        		csflag     :csflag,
		        		expfsp     :expfsp,
		        		paret      :paret,
		        		patracod   :patracod,
		        		inflg      :inflg,
		        		effdt_bng  :effdt_bng,
		        		effdt_end  :effdt_end,	
		        	};
				console.log("異動資料_篩選_查詢:code_data:"+JSON.stringify(code_data));
				//return false;
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'getTranPsnList',
						item    : '2',
						code_data :JSON.stringify(code_data),
						hid     : pahid,
						userid  : paUserid,
					},
					type:"POST",	//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						//更新查詢清單
						$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
						$('#tblTranPsnFilter').show();
						oTable = $('#tblTranPsnFilter').dataTable(objCopyD);
						
						oTable.fnClearTable(0);		//清空資料
								
						if (data.traPsnFilter!=null && data.traPsnFilter.length>0){	
							oTable.fnAddData(data.traPsnFilter);//重新給資料
						}
						oTable.fnDraw();
						
					},
					error: function(xhr){					
						alert("篩選失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
					}
				});
			}
			else{
				alert(msg);
				return false;
			}
		}
		else if (id == 'subPATRAPSN_Updbtn' ||id == 'subPATRAPSN_Insbtn'){	//異動檔更改 存檔
			if ($("#subPATraPsnForm").valid()){
				msg = $.checkFields(id);
				if(msg == ''){
					//將key取消readOnly
					$("#patradt").prop('disabled', false);
					$("#patracod").prop("disabled", false) ;
					$("#payidno").prop("readOnly", false) ;
					
					itmTbl="tblTranPsnList";
					tblname=id.substring(3,id.indexOf('_'));
					console.log("tblname:"+tblname);
					var jsonString = $.getDataInfo(tblname); //取得目前的資料
					
					pk_data={
		        		payidno  : $('#payidno').val(),
		        		patradt  : $('#patradt').val(),
		        		patracod : $('#patracod').val(),
		        		hid		 : $('#hid').val()
		        	};
					if (id.substring(id.indexOf('_')+1) =='Updbtn')		
						$('#trapsnfun').val("update");
 					else if (id.substring(id.indexOf('_')+1) =='Insbtn')		
 						$('#trapsnfun').val("insert");
					console.log("異動檔更改 pk_data:"+JSON.stringify(pk_data));
					currentPage = 0;
					uTable = $('#' + itmTbl ).dataTable();
					if (oTable != null ){
						info = oTable.page.info();		//記錄目前頁碼
						currentPage=info.page;
					}
					//return false;
					$.ajax({
						url:"${pageContext.request.contextPath}/salController.do",
						data:{
							method	: "handelTran",
							fun      : 'updTraPsn',
							selNm    : '異動檔',
							editMode : $('#trapsnfun').val(),
							pk_data : JSON.stringify(pk_data),
							tblname : tblname,
							tabJson : jsonString,
							hid     : pahid,
							userid  : paUserid
						},
						type:"POST",			//type:POST, GET(default:GET)
						dataType:"json",	//dataType: text, xml, html, script, json, jsonp
						async: true,	//逐步執行
						contentType: "application/x-www-form-urlencoded;charset=utf-8",  
						success:function(data) {
							if (data.msg!=null){
								$("#btn_cln_tran_TranPsn").trigger( "click" ); 
								$("#tran_idno").val( pk_data.payidno ); 
								$("#tran_patradt").val( pk_data.patradt );
								$("#btn_qry_tran_TranPsn").trigger( "click" );
								
								alert(data.msg);
							}	
						},
						error: function(xhr){					
							alert("更新[薪資異動檔]失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
						}
					}); 
						
				}else{
					alert(msg);
					return false;
				}
		 	}
		}
		else if (id == 'subPATRAPSN_Delbtn'){	//異動檔刪除(刪除patrapsn,patrapay)
			delCheck = confirm("請確認是否刪除！！\n\n若是，請按【確定】．\n\n若不是，請按【取消】");
			if (delCheck) {
				itmTbl="tblTranPsnList";
				tblname=id.substring(3,id.indexOf('_'));
				
				pk_data={
		        		payidno : $('#payidno').val(),
		        		patradt : $('#patradt').val(),
		        		patracod : $('#patracod').val(),
		        		hid		: $('#hid').val()
		        	};
		        console.log("異動檔刪除 tblname:"+tblname+" pk_data:"+JSON.stringify(pk_data));	
				//return false;
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method	 : "handelTran",
						fun      : 'updTraPsn',
						selNm    : '異動檔',
						editMode : 'delete',
						pk_data  : JSON.stringify(pk_data),
						tblname  : tblname,
						hid      : pahid,
						userid   : paUserid
					},
					type:"POST",			//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					async: true,	//逐步執行
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						//$("#btn_cln_tran_TranPsn").trigger( "click" );
						if (data.msg!=null){
							alert(data.msg);
							$("#btn_qry_tran_TranPsn").trigger( "click" );
						}	
					},
					error: function(xhr){					
						alert("刪除[薪資異動檔]失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
					}
				}); 	
			}
		}	
		else if (id == 'btn_calDegre'){	//取得新支扣--依新階別標準表,專業加給標準表 //找出新支扣 本薪,專業加給,主管加給,公保,健保,勞保,退撫
			var mdegr2="";
			var fun="B"; //B取得新支扣+補發,C補發
			if ($("#paydegr2 option:selected").val()!='' && typeof($("#paydegr2 option:selected").val())!='undefined'){
				mdegr2 = $("#paydegr2 option:selected").val();
			}
			msg = $.checkFields(id);
			if(msg == ''){
				//rnk2n職等  rank階別 rank3本功別 degr2級別 jt職稱 rank2權理 degre俸點
				//inflg保險補助比率  dpno健保人數 rankc主管(權理) rank4適用專用加給表 govdt初任公職日
				//o_basPay原支支扣 monno補發月數 dayno補發日數  ojt原職稱 orank原階別 odegre原俸點 effdt生效日期 pflag考績獎金月數/年終獎金比例
				in_data="";
				in_data={
		        		jt    : $("#paytitle").val(),
		        		rank  : $("#payrank").val(),
		        		rank3 : $("#payrank3").val(),
		        		rank4 : $("#payrank4").val(),
		        		rnk2n : $("#payrnk2n").val(),
		        		degr2 : $("#paydegr2").val(),
						rank2 : $("#payrank2").val(),
						rankc : $("#payrankc").val(),
						degre : $("#paydegre").val(),
						odegre: $("#paodegre").val(),
						inflg : $("#payinflg").val(),
						dpno  : $("#paydpno").val(),
						govdt : $("#paygovdt").val(),
						o_basPay : o_basPay,
						idno  : $("#payidno").val(),
						tracod: $("#patracod").val(),
						monno : $("#paymonno").val(),
						dayno : $("#paydayno").val(),
						ojt   : $("#paotitle").val(),
						orank : $("#paorank").val(),
						effdt : $("#paeffdt").val(),
						pflag : $("#paypflag").val(),
						taxno : $("#paytaxno").val(),
		        	};
		        //21..24退出,51年終獎金12考績獎金要算補發扣,	
		        patracod=getInt($("#patracod option:selected").val());
		        if ((patracod>=21 && patracod <=24) || patracod ==51 || patracod ==12){
		        	fun="C";
		        }
				$.doDegre(in_data,fun,patracod);
			
			}else{
				alert(msg);
				return false;
			} 
		}
		else if (id == 'btn_handelTra'){	//異動處理--1[匯入]晉級調薪,2[刪除]晉級調薪,3[匯入]待遇調整 4[刪除]待遇調整
			msg = $.checkFields(id);
			if(msg == ''){
				in_data="";
				//selCode:selNm:selEn 1:[匯入]晉級調薪:ImpPromoInTra
				selCode = $("#qryType option:selected").val().split(':')[0];
				selNm   = $("#qryType option:selected").val().split(':')[1];
				selEn   = $("#qryType option:selected").val().split(':')[2];
				if (selCode=='1' || selCode=='2'){
					patradt = $('#handingA_tradt').val();
					in_data={
							selCode :selCode,
							selNm   :selNm,
							selEn   :selEn,
							patradt :patradt
						   };
				}	
				else if (selCode=='3' || selCode=='4'){		//3[匯入]待遇調整 4[刪除]待遇調整
					patradt       = $('#handingB_tradt').val();
					var effdt     = $('#handingB_effdt').val();
					var rangeNm   = $("#handingB_range option:selected").val().split(':')[1];
					var rangeEn   = $("#handingB_range option:selected").val().split(':')[2];
					var monno     = $('#handingB_monno').val();
					in_data={
							selCode :selCode,
							selNm   :selNm,
							selEn   :selEn,
							patradt :patradt,
							effdt   :effdt,
							rangeNm   :rangeNm,
							rangeEn   :rangeEn,
							monno     :monno   	
						   };
				}
        		
				console.log("異動處理:in_data:"+JSON.stringify(in_data));		   
			    //return false;
			    
			    $.ajax({
						url:"${pageContext.request.contextPath}/salController.do",
						data:{
							method	: "handelTran",
							fun     : 'execTra',
							selNm   : selNm,
							hid     : pahid,
							userid  : paUserid,
							in_data : JSON.stringify(in_data),
						},
						type:"POST",			//type:POST, GET(default:GET)
						dataType:"json",	//dataType: text, xml, html, script, json, jsonp
						async: true,	//逐步執行
						contentType: "application/x-www-form-urlencoded;charset=utf-8",  
						success:function(data) {
							if (data.msg!=null){
								
								alert(data.msg);
							}	
						},
						error: function(xhr){					
							alert("[異動處理_"+selNm+"]失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
						}
				});
			}	
			else{
				alert(msg);
				return false;
			}    
		}
		
				
	});
	//取得新支扣
	$.doDegre = function(in_data,fun,tracod){
		$.ajax({
			url:"${pageContext.request.contextPath}/salController.do",
				data:{
					method : 'getDegreByRank',
					fun     : fun, 
					in_data : JSON.stringify(in_data),
					hid     : pahid,
					userid  : paUserid, 
				},

			type:"POST",		//type:POST, GET(default:GET)
			dataType:"json",	//dataType: text, xml, html, script, json, jsonp
			contentType: "application/x-www-form-urlencoded;charset=utf-8",  
			async:false,
			success:function(data) {
				//pa.paTrapay異動支扣明細--新
				cleanTable("TraPayN");
				$.doTraPayTbl("TraPayN",data.payItemList);
				//投保級距label
				$("#lblInsuamt").removeClass('bg-danger text-white');
				//退出,41支扣,12考績獎金,51年終獎金 不用修改[投保級距]
				if ((tracod >=21 && tracod<=24) ||tracod==41 || tracod==12 || tracod==51){
				
				}else{
					var tmpinsu="";
					if ($("#insuamt option:selected").val()=='' || typeof($("#insuamt option:selected").val())=='undefined')
						tmpinsu=""; 
					else
						tmpinsu= $("#insuamt option:selected").val();
						
					if ( data.insu_dpin != tmpinsu ){
						$("#insuamt").val(data.insu_dpin);
						$("#lblInsuamt").addClass('bg-danger text-white');
					}
				}		
			},
			error: function(xhr){	
				alert("搜尋[俸點對應]過程中，程式發生錯誤!!!\nerror code:"+ xhr.status);
			}
		});
	};
	//個人明細
 	var objCopyA = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyA.aoColumns =  [	
							{ "sTitle":"身分證","mData": "payidno" },
							{ "sTitle":"卡號/姓名","mData":null },
							{ "sTitle":"異動別","mData": "traname" },							
							{ "sTitle":"發薪日","mData": "patradt" },
							{ "sTitle":"餉單","mData": "salnum" },
						 ];
	objCopyA.aoColumnDefs= [
							//卡號/姓名
							{
							'targets': 1,
				         	'searchable':true,
				         	'orderable':true,
				         	'className': 'dt-body-left',
				         	'render': function (data, type, row, meta){
										return data.paycard+data.paynamec;
						         	  },
				         	}
				         	];
					    
	objCopyA = $.extend(true,objCopyA, {
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr form-inline"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br form-inline"ip>',
		buttons: [
           'excelHtml5' 
        ],
        "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
						$('td:eq(0)', nRow).html( '<a href = "#tranpsnData" class= "tranpsnData">'+aData.payidno+'</a>' );
				 }
	});
	$('#tblTranPsnList').dataTable(objCopyA);
	
	//顯示pa.paTraPsn明細
	$('#tblTranPsnList tbody').on('click', '.tranpsnData', function () {
		oTable = $('#tblTranPsnList').dataTable().api();
		info = oTable.page.info();		//記錄目前頁碼
		currentPage=info.page;
        var tr = $(this).parents('tr');
        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data
        rowIndex = oTable.row(tr).index();//抓取目前點選link這筆row的index			 		
		var idtno=rowData.payidno;
		var tradt=rowData.patradt;
		var tranum=rowData.tranum;
		var tracod=rowData.patracod;
		var hid=rowData.hid;
		
		//找pa.paTraPsn及pa.patrapay
		in_data="";
		in_data={
        		idtno  : idtno,
        		tradt  : tradt,
        		tracod : tracod,
        		tranum : tranum,
        		hid    : hid
        	};
		//console.log("異動資料paTraPsn查詢:code_data:"+JSON.stringify(code_data));
		
		$.getTraPsnByKey(in_data);
		
	});
	
	//依KEY找出pa.PATraPsn
	$.getTraPsnByKey = function(in_data){
		$.ajax({
			url:"${pageContext.request.contextPath}/salController.do",
			data:{
				method	: "handelTran",
				fun     : 'getTraPsnByKey',
				in_data :JSON.stringify(in_data),
				hid     : pahid,
				userid  : paUserid,
			},
			type:"POST",			//type:POST, GET(default:GET)
			dataType:"json",	//dataType: text, xml, html, script, json, jsonp
			contentType: "application/x-www-form-urlencoded;charset=utf-8",
			success:function(data) {
				if (data.traPsnItem!=null){
					//顯示異動個人明細
					$.doTraPsnDetail(data.traPsnItem,"tra",0);
					//pa.paTrapay異動支扣明細--原
					cleanTable("TraPayO");
					$.doTraPayTbl("TraPayO",data.otraPayList);
					//pa.paTrapay異動支扣明細--新
					cleanTable("TraPayN");
					$.doTraPayTbl("TraPayN",data.ntraPayList);
					//pa.paTrapay原異動支扣明細
					//$.doTraPayDetail("TblOTraPayList",data.otraPayList);
					//pa.paTrapay新異動支扣明細
					//payTbl="TblNTraPayList";
					//$.doTraPayDetail(payTbl,data.ntraPayList);					
				}
			}  
		}) 
	};
	
	//顯示pa.paTrapay個人支扣明細
	$.doTraPayTbl = function(tbl,mData){
		var row_html="" ;
		var sumPay_p=0,sumPay_d=0;
		if (mData!=null && mData.length>0){
			$.each(mData, function(idx) {
				if ( idx!=0) {
					row_html = "<tr>" + $('#'+tbl +" tbody").find("tr").eq(0).html() + "</tr>";
	    			$('#'+tbl+" tbody").append(row_html);
				}
				//帶入動態表格數值
				tr = $('#'+tbl+" tbody").find("tr").eq(idx);
				if (tbl=="TraPayO"){
					tr.find(':input[name*="paycode"]').val(mData[idx].paycode);
					tr.find(':input[name*="codenm"]').val(mData[idx].pacodnmc);
					tr.find(':input[name*="paysort"]').val("O");
				
				}if (tbl=="TraPayN"){
					tr.find(':input[name*="paycode"]').val(mData[idx].paycode+":"+mData[idx].payauth+":"+mData[idx].payiftax);
					tr.find(':input[name*="paysort"]').val("N");
				}
				tr.find(':input[name*="payauth"]').val(mData[idx].payauth);	
				tr.find(':input[name*="payiftax"]').val(mData[idx].payiftax);
				tr.find(':input[name*="hid"]').val($("#hid").val());
				tr.find(':input[name*="payidno"]').val($("#payidno").val());	//新支扣帶入參考檔,沒有idno
				tr.find(':input[name*="tranum"]').val(mData[idx].tranum);		
				tr.find(':input[name*="payamtP"]').val(mData[idx].payamtp);
				tr.find(':input[name*="payamtD"]').val(mData[idx].payamtd);
				//console.log(row_html);
			});
			//收入
			$('#'+tbl+' :input[name*="payamtP"]').each(function(){						
		  		sumPay_p=sumPay_p+getInt($(this).val());    	  		
			});
			
			//支出
			$('#'+tbl+' :input[name*="payamtD"]').each(function(){						
		  		sumPay_d=sumPay_d+getInt($(this).val());    	  		
			});
			
			if (tbl=="TraPayO"){
				$("#o_sumpay_p").html(sumPay_p);
				$("#o_sumpay_d").html(sumPay_d);	
			}else if (tbl=="TraPayN"){
				$("#n_sumpay_p").html(sumPay_p);
				$("#n_sumpay_d").html(sumPay_d);
			}
		}
	};
	//新支扣--全部金額加總
	$.sumPay = function(){
		var sumPay_p=0,sumPay_d=0;
		var tbl="TraPayN";  
		//收入
		$('#'+tbl+' :input[name*="payamtP"]').each(function(){						
	  		sumPay_p=sumPay_p+getInt($(this).val());    	  		
		});
		
		//支出
		$('#'+tbl+' :input[name*="payamtD"]').each(function(){						
	  		sumPay_d=sumPay_d+getInt($(this).val());    	  		
		});
		
		$("#n_sumpay_p").html(sumPay_p);
		$("#n_sumpay_d").html(sumPay_d);		
	};
	//刪除指定的table行
    $("#TraPayN").on("click", ".delete_row", function() { 
    	var this_table = $(this).closest('table');
    	var index = $(this).closest('tr').index();
    	var tr_length = $(this).closest('table').find("tbody").find('tr').length; //欄位數量

    	//刪除一欄
    	if ( tr_length == 1 ) {
    		cleanTable($(this).closest('table').attr("id")); //清除該頁面所有資料    		
    	} else {
    		$(this).closest("tr").remove();
    	}
    	//小計重算
    	$.sumPay(); 	    	
    });
	
	//指定表格新增一欄
	function add_row(this_table) {
		
		var table_id = this_table.attr("id");
		var row_html = "<tr>" + this_table.find("tbody").find("tr").eq(0).html() + "</tr>";
		
		//console.log(row_html);
		
    	this_table.append(row_html);
    	
    	var rows = this_table.find("tbody").find("tr").length;
    	
    	//塞預設值
    	if (table_id=="TraPayN"){
    		tr = $('#'+table_id+" tbody").find("tr").eq(rows-1);
			tr.find(':input[name*="payidno"]').val($("#payidno").val());
			tr.find(':input[name*="hid"]').val($("#hid").val());
    	}
	}
	
	//新增指定的table行
    $('.add_row').on( "click", function() {
    	var this_table = $(this).prev("table");
    	add_row(this_table);
    });
    
	//取得正負整數
	function getInt( data ) {
		var result;
		//var result = parseInt(data.toString().replace(",","")); //移除千份位
		if (typeof(data) == "undefined")
			result = 0;	
		else
			result = data.toString().replace( /,/g , '' ); //移除千份位
		/* if ( isNaN(result) ) {
			result = 0;
		} */
		//var reg = /^[\d]+$/;
		var reg = /^[+-]?\d+$/;
		if (!reg.test(result)) { //不是整數
			result = 0;
		}
		
		return parseInt(result);
	}
	//清除指定頁面資料
	function cleanTable(table_id){
		$(":checkbox,:radio").prop("checked", false);
		//清除所有頁面的內容(enable)，disable的不清除
// 		$('#'+table_id+' :input:enabled').each(function() {
// 			$(this).val("");
// 	    });
		//清除所有頁面的內容
	    $('#'+table_id+' :input').each(function() {
			$(this).val("");
	    });
	    //清除tfoot
	    $('#'+table_id+' >tfoot').each(function() {
	    	$(this).find("td").each(function(index, td) { 
	    		$(this).html('');
			});
	    	
	    });
	    
	    //所有select預設選擇第一項
	    $('#'+table_id+' select').each(function() {
	    	$(this).prop("selectedIndex", 0);
	    });
	    
	    //清除所有table的row，只留第一行
	    $('#'+table_id+' >tbody').each(function() {
	    	var tableID = $(this).closest('table').attr("id");
	    	$(this).find("tr").each(function(index, tr) { 
	    		if (index >= 1 ) {
		    			$(this).remove(); //清除第一行以外的row
		    	}	    		
			});
	    	
	    });	    
	}
	//新[實際支領KEY]對應
	$(".calCSPF").change(function(){
		var mrnk2n="";
		var mrank="";
		var mrank3="";
		var mdegr2="";
		var mjt="";
		var mtradt=$("#patradt").val();
		var mcsflag_New = $("#pacsflag").val();//新_工作獎金
		var mcsflag_Old = $("#paocsfla").val();//原_工作獎金
		$("#TranPsn_pokey").val("");
		$("#TranPsn_pobas").val("");
		$("#TranPsn_podat").val("");
		//原
		if (mcsflag_Old=="Y"){
			mrnk2n = $("#paornk2n").val();
			mrank  = $("#paorank").val();
			mrank3 = $("#paorank3").val();
			mdegr2 = $("#paodegr2").val();
			mjt    = $("#paotitle").val();
			$.doCsPf(mjt,mrnk2n,mrank,mdegr2,mrank3,mtradt,'O');				
		}
		//新
		$("#TranPsn_pfkey").val("");
		$("#TranPsn_pfbas").val("");
		$("#TranPsn_pfdat").val("");
		if (mcsflag_New=="Y"){
			mrnk2n = $("#payrnk2n").val();
			mrank  = $("#payrank").val();
			mrank3 = $("#payrank3").val();
			mdegr2 = $("#paydegr2").val();
			mjt    = $("#paytitle").val();
			$.doCsPf(mjt,mrnk2n,mrank,mdegr2,mrank3,mtradt,'N');
		}
	});
	
	//實際支領KEY 基本點數 職等點數
	$.doCsPf = function(mjt,mrnk2n,mrank,mdegr2,mrank3,mtradt,item){
		$.ajax({
			url:"${pageContext.request.contextPath}/salController.do",
				data:{
					method : 'getCsPfBykey',
					jt  : mjt,
					rnk2n  : mrnk2n,
					rank   : mrank,
					degr2  : mdegr2,
					rank3  : mrank3,
					tradt  : mtradt 
				},

			type:"POST",		//type:POST, GET(default:GET)
			dataType:"json",	//dataType: text, xml, html, script, json, jsonp
			contentType: "application/x-www-form-urlencoded;charset=utf-8",  
			async:false,
			success:function(data) {
				if (data.cspfData!=null){
					if (item=='O'){
						$("#TranPsn_pokey").val(data.cspfData.pfkey);
						$("#TranPsn_pobas").val(data.cspfData.pfbas);
						$("#TranPsn_podat").val(data.cspfData.pfdat);
					}else if (item=='N'){	
						$("#TranPsn_pfkey").val(data.cspfData.pfkey);
						$("#TranPsn_pfbas").val(data.cspfData.pfbas);
						$("#TranPsn_pfdat").val(data.cspfData.pfdat);
						if (data.cspfData.pfbas==""){
							$("#TranPsn_pfbas").addClass('bg-danger');
							$("#TranPsn_pfdat").addClass('bg-danger');
						}else{
							$("#TranPsn_pfbas").removeClass('bg-danger');
							$("#TranPsn_pfdat").removeClass('bg-danger');
						}
					}
				}
			},
			error: function(xhr){	
				alert("搜尋[實際支領KEY]過程中，程式發生錯誤!!!\nerror code:"+ xhr.status);
			}
		});
	};
	
	//新[俸點]對應
	$(".calRank").change(function(){
		var mdegr2="";
		var jt=$("#paytitle").val();
		if ($("#paydegr2 option:selected").val()!='' && typeof($("#paydegr2 option:selected").val())!='undefined'){
			mdegr2 = $("#paydegr2 option:selected").val();
		}
		if (jt=="AD" || jt=="BK" || jt=="91"|| jt==""){//AD進修醫師,BK實習醫師91顧問<==不用設新俸點
			$("#payrank").val("0");			
			$("#payrank3").val("0");
			$("#paydegr2").val("0");
			$("#paydegre").val("0");
		}else{
			//rnk2n職等  rank階別 rank3本功別 degr2級別 jt職稱 
			in_data="";
			in_data={
	        		jt    : jt,
	        		rank  : $("#payrank").val(),
	        		rank3 : $("#payrank3").val(),
	        		rnk2n : $("#payrnk2n").val(),
	        		degr2 : mdegr2,
	        	};
	        $.doRank(in_data,'N');
		}
		
	});
	//顯示俸點對應
	$.doRank = function(in_data,item){
		//console.log("doRank顯示俸點對應:code_data:"+JSON.stringify(in_data));	
		$.ajax({
			url:"${pageContext.request.contextPath}/salController.do",
				data:{
					method : 'getDegreByRank',
					fun     : 'A', 
					in_data : JSON.stringify(in_data),
					hid     : pahid,
					userid  : paUserid, 
				},

			type:"POST",		//type:POST, GET(default:GET)
			dataType:"json",	//dataType: text, xml, html, script, json, jsonp
			contentType: "application/x-www-form-urlencoded;charset=utf-8",  
			async:false,
			success:function(data) {
				if (data.rankItem!=null){
					if (item=='O'){
						$("#paodegre").val(data.rankItem.paydegrv);
						$("#paodegre").removeClass('bg-danger');
					}else if (item=='N'){	
						$("#paydegre").val(data.rankItem.paydegrv);
						$("#paydegre").removeClass('bg-danger');
					}
				}else{
					if (item=='O'){
						$("#paodegre").addClass('bg-danger');
					}else if (item=='N'){	
						$("#paydegre").addClass('bg-danger');
					}
				}
				
			},
			error: function(xhr){	
				alert("搜尋[俸點對應]過程中，程式發生錯誤!!!\nerror code:"+ xhr.status);
			}
		});
	};
	//新_工作獎金Y,show pfkey 工作獎金N show pfdoc
	$("#pacsflag").change(function(){
		$("#div_pfkey").addClass("d-none");
		$("#div_pfdoc").addClass("d-none");
		if ($(this).val()=='Y'){
			$("#div_pfkey").removeClass("d-none");
		}	
		else if ($(this).val()=='N'){	
			$("#div_pfdoc").removeClass("d-none");
		}
	});
	//原_工作獎金Y,show pfkey 工作獎金N show pfdoc
	$("#paocsfla").change(function(){
		$("#div_pokey").addClass("d-none");
		$("#div_podoc").addClass("d-none");
		if ($(this).val()=='Y'){
			$("#div_pokey").removeClass("d-none");
		}	
		else if ($(this).val()=='N'){	
			$("#div_podoc").removeClass("d-none");
		}
	});
	
	//顯示pa.paTraPsn明細,來源item=tra(顯示patrapsn),bas(抓取pabaspsn),tracod異動代碼=item(bas)用,
	$.doTraPsnDetail = function(mData,item,tracod){
		$("#div_paypflag").addClass("d-none");
		$("#paymonno_lbl").html("追補扣日數");
		//button禁用
		$("#btn_calDegre").prop("disabled", true);
		//年終
 		if (tracod==51 || mData.patracod=='51'){
 			$("#div_paypflag").removeClass("d-none");
 			$("#paymonno_lbl").html("核發月數");
 			$("#paypflag_lbl").html(" 發放年終獎金比例");
 			select = $('#paypflag');
 			select.empty();
 			$.each(pflgBOptions, function(val, text) {
 			    options = "<option value='" + val + "'>" +text ;
 				select.append(options);
 			});
 			select.selectpicker('refresh');
 			if(tracod==51){	//線上輸入[年終輸入],帶入預設值
 				$("#paymonno").val("12");
 				$("#paypflag").selectpicker('val','3');
 			}
 		}
		//考績獎金 
		else if (tracod==11 || tracod==12 || mData.patracod=='11' || mData.patracod=='12' ){			
			$("#div_paypflag").removeClass("d-none");
			$("#paypflag_lbl").html(" 發放考績獎金月數");
			select = $('#paypflag');
			select.empty();
			$.each(pflgAOptions, function(val, text) {			    
			    options = "<option value='" + val + "'>" + text ;
				select.append(options);
			});
			
			select.selectpicker('refresh');
			if(tracod==12){	//線上輸入[考績輸入],帶入預設值
 				$("#paypflag").selectpicker('val','0');
 			}
		}
			
		if ( item == "tra" ){
			$("#hid").val(mData.hid);
			$("#patradt").val(mData.patradt.dts);
			//$("#patracod").selectpicker('val',mData.patracod);
			$("#patracod").val(mData.patracod);
			$("#salnum").val(mData.salnum);
			$("#paylvefg").val(mData.paylvefg);
			$("#tranum").val(mData.tranum);
			
			$("#payidno").val(mData.payidno);
			$("#paycard").val(mData.paycard);		
			$("#payempno").val(mData.payempno);
			$("#paynamec").val(mData.paynamec);
			
			$("#paygovdt").val(mData.paygovdt.dts);
			$("#payempdt").val(mData.payempdt.dts);
			
			$("#paysgndt").val(mData.paysgndt.dts);
			$("#payrsgdt").val(replaceDefaultDate(mData.payrsgdt.dts));
			
			$("#paeffdt").val(mData.paeffdt.dts);
			$("#paymonno").val(mData.paymonno);
			$("#paydayno").val(mData.paydayno);
			
			$("#paytaxno").val(mData.paytaxno);
			$("#paydpno").val(mData.paydpno);
			$("#insuamt").val(mData.insuamt);	//投保級距
			$("#paypflag").selectpicker('val',mData.paypflag);
			$("#payinflg").val(mData.payinflg);	//保險費比率
			
			$("#paydept").selectpicker('val',mData.paydept);
			
			//原
			$("#paotitle").selectpicker('val',mData.paotitle);
			$("#paorank").val(mData.paorank);	//階別
			$("#paornk2n").val(mData.paornk2n);	//職等
			$("#paorank3").val(mData.paorank3);	//本功別
			$("#paorank2").val(mData.paorank2);	//權理
			$("#paodegr2").val(mData.paodegr2);	//級別
			$("#paorankc").val(mData.paorankc);	//主管權理
			$("#paorank4").val(mData.paorank4);	//適用專業加給表
			$("#paodegre").val(mData.paodegre);
			
			$("#TranPsn_contraco").val(mData.contraco);
			//
			$("#paocsfla").val(mData.paocsfla);
			$("#TranPsn_pokey").val(mData.pokey);
			$("#TranPsn_pobas").val(mData.pobas);
			$("#TranPsn_podat").val(mData.podat);
			
			$("#paopfsp1").val(mData.paopfsp1);
			$("#paopfsp2").val(mData.paopfsp2);
			$("#paopfsp3").val(mData.paopfsp3);
			$('#paoexpfkey1').selectpicker('val', mData.paoexpfkey1);
			$('#paoexpfkey2').selectpicker('val', mData.paoexpfkey2);
			$('#paoexpfkey3').selectpicker('val', mData.paoexpfkey3);
			
			$("#paopfdoc").val(mData.paopfdoc);
			
			//新
			$("#paytitle").selectpicker('val',mData.paytitle);
			$("#payrank").val(mData.payrank);	//階別
			$("#payrnk2n").val(mData.payrnk2n);	//職等
			$("#payrank3").val(mData.payrank3);	//本功別
			$("#payrank2").val(mData.payrank2);	//權理
			$("#paydegr2").val(mData.paydegr2);	//級別
			$("#payrankc").val(mData.payrankc);	//主管權理
			$("#payrank4").val(mData.payrank4);	//適用專業加給表
			$("#paydegre").val(mData.paydegre);
			
			$("#TranPsn_contract").val(mData.contract);
			
			$("#pacsflag").val(mData.pacsflag);
			$("#TranPsn_pfkey").val(mData.pfkey);
			$("#TranPsn_pfbas").val(mData.pfbas);
			$("#TranPsn_pfdat").val(mData.pfdat);
			
			$("#paypfsp1").val(mData.paypfsp1);
			$("#paypfsp2").val(mData.paypfsp2);
			$("#paypfsp3").val(mData.paypfsp3);
			$('#payexpfkey1').selectpicker('val', mData.payexpfkey1);
			$('#payexpfkey2').selectpicker('val', mData.payexpfkey2);
			$('#payexpfkey3').selectpicker('val', mData.payexpfkey3);
			
			$("#paypfdoc").val(mData.paypfdoc);
			
			$("#payctcbacno").val(mData.payctcbacno);
			$("#payctcbgedno").val(mData.payctcbgedno);
			$("#payaddrs").val(mData.payaddrs);
			
			$("#TranPsn_payupdid").val(mData.payupdid);
			$("#TranPsn_updtm").val(mData.updtm);
			
			//工作獎金Y,show pfkey 工作獎金N show pfdoc
			$("#div_pfkey").addClass("d-none");
			$("#div_pfdoc").addClass("d-none");
			$("#div_pokey").addClass("d-none");
			$("#div_podoc").addClass("d-none");
			//新
			if (mData.pacsflag=='Y'){
				$("#div_pfkey").removeClass("d-none");
			}	
			else if (mData.pacsflag=='N'){	
				$("#div_pfdoc").removeClass("d-none");
			}
			//原 工作獎金YN
			if (mData.paocsfla=='Y'){
				$("#div_pokey").removeClass("d-none");
			}	
			else if (mData.paocsfla=='N'){	
				$("#div_podoc").removeClass("d-none");
			}
			
			//契約,不含醫師,才有新舊制(Y/N)
			//新
			if (mData.contract!=''){
				$("#div_contract").removeClass("d-none");
				$("#div_authority").addClass("d-none");
			}	
			else{
				if (mData.paytitle=='CG'){	//契約主治不列,權理	
					$("#div_authority").addClass("d-none");
				}	
				else{
					$("#div_authority").removeClass("d-none");
				}	
				$("#div_contract").addClass("d-none");
			}
			//原
			if (mData.contraco!=''){
				$("#div_contraco").removeClass("d-none");
				$("#div_authorito").addClass("d-none");
			}	
			else{
				if (mData.paotitle=='CG'){	//契約主治不列,權理	
					$("#div_authorito").addClass("d-none");
				}	
				else{
					$("#div_authorito").removeClass("d-none");
				}	
				$("#div_contraco").addClass("d-none");
			}
				
			//工獎 1A0 2A0才需列出,專勤額度=0
			if (mData.hid=='1A0' || mData.hid=='2A0'){
				if ( getInt(mData.paopfdoc) == 0 )
					$(".cspf_o").removeClass("d-none");
				else
					$(".cspf_o").addClass("d-none");	
				if ( getInt(mData.paypfdoc) == 0 )
					$(".cspf_n").removeClass("d-none");
				else
					$(".cspf_n").addClass("d-none");
					
			}else{
				if (mData.pacsflag=='N'){
					$(".cspf_n").addClass("d-none");			
				}
				if (mData.paocsfla=='N'){
					$(".cspf_o").addClass("d-none");
				}
			}	
			//button啟用
			$("#subPATRAPSN_Updbtn,#subPATRAPSN_Delbtn").prop("disabled", false);
			//key readonly/disabled
			$("#patradt").prop('disabled', true);
			$("#patracod").prop("disabled", true) ;
			$("#payidno").prop("readOnly", true) ;	
		}
		else if ( item == "bas" ){
			$("#hid").val(mData.hid);
			$("#payidno").val(mData.payidno);//ID
			$("#paycard").val(mData.paycard);//卡號		
			$("#payempno").val(mData.payempno);//職號
			$("#paynamec").val(mData.paynamec);//姓名			
			$("#paydept").selectpicker('val',mData.paydept);//單位
			$("#paygovdt").val(mData.paygovdt.dts);//初任公職日
			$("#payempdt").val(mData.payempdt.dts);//到院日			
			$("#paysgndt").val(mData.paysgndt.dts);//到職日
			if ( tracod == 1 ){
				$("#paeffdt").val(mData.paysgndt.dts);//新進,生效日=現職日
			}else{
				$("#paeffdt").val(replaceDefaultDate("9999-12-31"));//生效日
			}	
			$("#payinflg").val(mData.payinflg);	//保險費比率
			$("#paytaxno").val(mData.paytaxno);//扶養數
			$("#paydpno").val(mData.paydpno);	//健保數
			$("#payctcbacno").val(mData.payctcbacno);	//薪資銀行帳號
			$("#payctcbgedno").val(mData.payctcbgedno);	//公儲銀行帳號
			$("#payaddrs").val(mData.payaddrs);	//戶籍地址
			$("#insuamt").val(mData.insuamt);	//投保級距
			$("#payrsgdt").val(replaceDefaultDate("9999-12-31"));//離職日
			
			//工作獎金Y,show pfkey 工作獎金N show pfdoc
			$("#div_pfkey").addClass("d-none");
			$("#div_pfdoc").addClass("d-none");
			$("#div_pokey").addClass("d-none");
			$("#div_podoc").addClass("d-none");
			$(".cspf_o").addClass("d-none");
				
			//pabaspsn基本資料,依異動碼:1新進/2復職,放入[新],其他異動碼放[原]
			if ( !(tracod >=1 && tracod < 10) ){	
				//-----異動碼:11-51,[原]
				$("#paotitle").selectpicker('val',mData.paytitle);//職稱
				$("#paorank").val(mData.payrank);	//原階別
				$("#paorank2").val(mData.payrank2);	//原權理
				$("#paornk2n").val(mData.payrnk2n);	//原職等
				$("#paorankc").val(mData.payrankc);	//原主管權理
				$("#paorank3").val(mData.payrank3);	//原本功別
				$("#paodegre").val(mData.paydegre); //原俸點
				$("#paodegr2").val(mData.paydegr2);	//級別
				$("#paorank4").val(mData.payrank4);	//原適用專業加給表
				if (mData.contract != ''){//月尾
					$("#div_contraco").removeClass("d-none");//月頭尾
					$("#div_authorito").addClass("d-none");	//權理					
				}	
				else{//月頭
					$("#div_contraco").addClass("d-none");	
					if (mData.paytitle=='CG')	//契約主治不列,權理	
						$("#div_authorito").addClass("d-none");
					else//月尾
						$("#div_authorito").removeClass("d-none");					
				}
				$("#TranPsn_contraco").val(mData.contract);	//月尾Y 月頭N/空白
				
				if (tracod !=41 && tracod !=51){
					//工獎(額外點數) .....專勤額度=0,1A0 2A0才需列出
					if (mData.hid=='1A0' || mData.hid=='2A0'){
						if ( getInt(mData.paypfdoc) == 0 )
							$(".cspf_o").removeClass("d-none");						
					}
					
					//工作獎金YN
					if (mData.pacsflag=='Y'){
						$("#div_pokey").removeClass("d-none");
					}	
					else if (mData.pacsflag=='N'){	
						$("#div_podoc").removeClass("d-none");
					}
					$("#paocsfla").val(mData.pacsflag);
					$("#TranPsn_pokey").val(mData.pfkey);
					$("#TranPsn_pobas").val(mData.pfbas);
					$("#TranPsn_podat").val(mData.pfdat);
					$('#paoexpfkey1').selectpicker('val', mData.payexpfkey1);
					$('#paoexpfkey2').selectpicker('val', mData.payexpfkey2);
					$('#paoexpfkey3').selectpicker('val', mData.payexpfkey3);
					$("#paopfdoc").val(mData.paypfdoc);
					$("#paopfsp1").val(mData.paypfsp1);
					$("#paopfsp2").val(mData.paypfsp2);
					$("#paopfsp3").val(mData.paypfsp3);					
				}
				//[新]11考績晉級31晉級調薪 32改任換敘 33納實 34考績升等 35待遇調整 36晉級 37職務調整 38降級---和[原]放一樣的值
				if (tracod ==11 || (tracod >30 && tracod < 40 ) ){
					$("#paytitle").selectpicker('val',mData.paytitle);//職稱
					$("#payrank").val(mData.payrank);	//原階別
					$("#payrank2").val(mData.payrank2);	//原權理
					$("#payrnk2n").val(mData.payrnk2n);	//原職等
					$("#payrankc").val(mData.payrankc);	//原主管權理
					$("#payrank3").val(mData.payrank3);	//原本功別
					$("#paydegre").val(mData.paydegre); //原俸點
					$("#paydegr2").val(mData.paydegr2);	//級別
					$("#payrank4").val(mData.payrank4);	//原適用專業加給表
					if (mData.contract!=''){//月尾
						$("#div_contraco").removeClass("d-none");//月頭尾
						$("#div_authorito").addClass("d-none");	//權理
					}	
					else{//月頭
						$("#div_contract").addClass("d-none");
						if (mData.paytitle=='CG')	//契約主治不列,權理	
							$("#div_authority").addClass("d-none");
						else
							$("#div_authority").removeClass("d-none");
					}
					
					//工獎 .....專勤額度=0,1A0 2A0才需列出
					if (mData.hid=='1A0' || mData.hid=='2A0'){
						if ( getInt(mData.paypfdoc) == 0 )
							$(".cspf_n").removeClass("d-none");
						else
							$(".cspf_n").addClass("d-none");
					}
					else if(mData.hid=='3A0'){
						$(".cspf_n").addClass("d-none");
					}
					//工作獎金YN
					if (mData.pacsflag=='Y'){
						$("#div_pfkey").removeClass("d-none");
					}	
					else if (mData.pacsflag=='N'){	
						$("#div_pfdoc").removeClass("d-none");
					}
					$("#pacsflag").val(mData.pacsflag);
					$("#TranPsn_pfkey").val(mData.pfkey);
					$("#TranPsn_pfbas").val(mData.pfbas);
					$("#TranPsn_pfdat").val(mData.pfdat);
					$('#payexpfkey1').selectpicker('val', mData.payexpfkey1);
					$('#payexpfkey2').selectpicker('val', mData.payexpfkey2);
					$('#payexpfkey3').selectpicker('val', mData.payexpfkey3);
					$("#paypfdoc").val(mData.paypfdoc);
					$("#paypfsp1").val(mData.paypfsp1);
					$("#paypfsp2").val(mData.paypfsp2);
					$("#paypfsp3").val(mData.paypfsp3);					
				}
			}
			else{//1新進2復職--基本資料放[新]
				$("#paytitle").selectpicker('val',mData.paytitle);//職稱
				$("#payrank").val(mData.payrank);	//原階別
				$("#payrank2").val(mData.payrank2);	//原權理
				$("#payrnk2n").val(mData.payrnk2n);	//原職等
				$("#payrankc").val(mData.payrankc);	//原主管權理
				$("#payrank3").val(mData.payrank3);	//原本功別
				$("#paydegre").val(mData.paydegre); //原俸點
				$("#paydegr2").val(mData.paydegr2);	//級別
				$("#payrank4").val(mData.payrank4);	//原適用專業加給表
				if (mData.contract!=''){//月尾
					$("#div_contraco").removeClass("d-none");//月頭尾
					$("#div_authorito").addClass("d-none");	//權理
				}	
				else{//月頭
					$("#div_contract").addClass("d-none");
					if (mData.paytitle=='CG')	//契約主治不列,權理	
						$("#div_authority").addClass("d-none");
					else
						$("#div_authority").removeClass("d-none");
				}
				
				//工獎 .....專勤額度=0,1A0 2A0才需列出
				if (mData.hid=='1A0' || mData.hid=='2A0'){
					if ( getInt(mData.paypfdoc) == 0 )
						$(".cspf_n").removeClass("d-none");
					else
						$(".cspf_n").addClass("d-none");
				}
				else if(mData.hid=='3A0'){
					$(".cspf_n").addClass("d-none");
				}
				if (mData.pacsflag=='Y'){
					$("#div_pfkey").removeClass("d-none");
				}	
				else if (mData.pacsflag=='N'){	
					$("#div_pfdoc").removeClass("d-none");
				}
				$("#pacsflag").val(mData.pacsflag);
				$("#TranPsn_pfkey").val(mData.pfkey);
				$("#TranPsn_pfbas").val(mData.pfbas);
				$("#TranPsn_pfdat").val(mData.pfdat);
				$('#payexpfkey1').selectpicker('val', mData.payexpfkey1);
				$('#payexpfkey2').selectpicker('val', mData.payexpfkey2);
				$('#payexpfkey3').selectpicker('val', mData.payexpfkey3);
				$("#paypfdoc").val(mData.paypfdoc);
				$("#paypfsp1").val(mData.paypfsp1);
				$("#paypfsp1").val(mData.paypfsp2);
				$("#paypfsp3").val(mData.paypfsp3);				
			}
			
			$("#payrsgdt").prop('disabled', true);
			$("#paylvefg").val("N");
			//離職註記 21離職22退休23留職停薪24逾期請假
			if (tracod ==21 || tracod == 22 ){
				$("#payrsgdt").prop('disabled', false);
				$("#paylvefg").val("Y");
			}else if (tracod ==23 || tracod == 24){
				$("#paylvefg").val("Y");
			}
			//key readonly/disabled false
			$("#patradt").prop('disabled', false);
			$("#patracod").prop("disabled", false) ;
			$("#payidno").prop("readOnly", false) ;
			//button啟用
			if (tracod !=41 ){
				$("#btn_calDegre").prop("disabled", false);
			}
		}
		
	};
	//顯示pa.paTrapay個人原支扣明細
	$.doTraPayDetail = function(tbl,mData){
		$('#'+tbl).show();
		if (tbl=="TblOTraPayList")
			oTable = $('#'+tbl).dataTable(objCopyB);
		else if(tbl=="TblNTraPayList")
			oTable = $('#'+tbl).dataTable(objCopyC);
			
		oTable.fnClearTable(0);		//清空資料
				
		if (mData!=null && mData.length>0){	
			oTable.fnAddData(mData);//重新給資料
		}
		oTable.fnDraw();
	};
	//用正規表達式判斷身分證
	$('#payidno').blur(function(){
		$(this).val( $(this).val().toUpperCase() ) ;
		var tmp = $(this).val();
		if (tmp !=""){
			//用正規表達式判斷身分證 舊式居留證 --格式檢查（Regex）
			//國民身分證：第一碼英文字母，第二碼為 1 或 2，總長 10 碼  /^[A-Z][12]\d{8}$/
			//新式居留證:1碼英文 + 8或9 + 8碼數字                                /^[A-Z][89]\d{8}$/
			//舊式居留證:1碼英文 + A-D + 8碼數字 			   /^[A-Z][A-D]\d{8}$/
			//複合型 (全部)                               ^[A-Z][1289ABCD]\d{8}$
			//第 1 碼：必定為大寫英文字母 [A-Z]
			//第 2 碼：本國人/新式居留證為數字 [1-28-9]舊式居留證為字母 [A-D]合併後寫法：[A-D1-28-9]後 8 碼：必定為數字 \d{8}
			reCheckCase = /^[A-Z][1289ABCD]\d{8}$/ ;
			
			if(!reCheckCase.test(tmp)){
				alert('輸入的身分證字號格式有誤，請重新輸入');
				return;
			}
			//檢查碼演算法（Mod 10）
			checkId = isIdNo($(this).val());
			if (checkId == false){
				return false;
			}
		}
	});
	//用正規表達式判斷卡號
	$('#paycard').blur(function(){
		$(this).val( $(this).val().toUpperCase() )
		var tmp = $(this).val();
		if (tmp !=""){
			//reCheckCase = /^\d{4}$|^[A-Za-z]\d{3}|^[A-Za-z][A-Za-z]\d{2}$|^$/;
			reCheckCase = /^\d{4}$|^[A-Za-z]\d{3}|^[A-Za-z0-9][A-Za-z0-9]\d{2}$|^$/;
			if(!reCheckCase.test(tmp)){
				alert('輸入的卡號格式有誤，請重新輸入');
				return;
			}
		}
	});	
	//輸入ID/CARD,找基本檔  異動代碼要先KEY,才能找
	$(".findkey").blur(function(){
		patracod=getInt($("#patracod option:selected").val());
		if (patracod == 0){
			alert("請先勾選【異動代碼】");
			return false;
		}
		var blnHave=false;
		var f_id=$(this).attr('id');
 		
		console.log("id:"+f_id+" readOnly:"+$(this).attr("readOnly")+" btn:"+ $("#subPATRAPSN_Insbtn").prop("disabled") );
		
		//未設定readOnly屬性 
		if($(this).prop("readOnly")==undefined || $(this).prop("readOnly")==false){	
			var key=$(this).val().toUpperCase();
			var code_data="";
			if ( key != ""){
				if(f_id=="payidno"){
					code_data={
		        		idtno      :key,
		        		hid        :pahid
		        	};
	        	}else if(f_id=="paycard"){
	        		if ( $("#payidno").val()=="" ){
		        		code_data={
			        		cardno     :key,
			        		hid        :pahid
			        	};
			        }else
			        	return false;	
		        }	
	        	console.log("基本檔findkey:code_data:"+JSON.stringify(code_data));	
		    
		        //return false;	
		        //有按[新增一筆]->[存檔]disable=false
				if (!$("#subPATRAPSN_Insbtn").prop("disabled")){
					$.ajax({				
							url:"${pageContext.request.contextPath}/salController.do",
							data:{
								method  : 'getBasPsnByKey',
								code_data :JSON.stringify(code_data),
								hid     : pahid,
								userid  : paUserid,
								tracod  : patracod
							},
			
							type:"POST",		//type:POST, GET(default:GET)
							dataType:"json",	//dataType: text, xml, html, script, json, jsonp
							contentType: "application/x-www-form-urlencoded;charset=utf-8",  
							async: false,
							cache: false,			
							success:function(data) {
								if ( data.basPsnItem!=null ){
									blnHave=true;
									//薪資基本檔有找到
									if (data.basPsnItem.paylvefg=="Y" && data.basPsnItem.payrsgdt.dts != "9999-12-31" ){
										alert("此員曾離職過");
										blnHave=false;
									}
									if (patracod >= 1 && patracod < 10){//新進
										if (data.basPsnItem.paylvefg=="N" && patracod==1){
											alert("重複");
											blnHave=false;
											//return false;
										}	
										else if (data.basPsnItem.patracod==23 && patracod==1){
											alert("基本檔_留停,請確認【異動代碼】");						
											return false;		
										}			
									}
									//顯示異動個人明細
									$.doTraPsnDetail(data.basPsnItem,"bas",patracod);
									//pa.paTrapay異動支扣明細--原
									cleanTable("TraPayO");
									o_basPay=data.basPayList
									$.doTraPayTbl("TraPayO",o_basPay);									
								}	
							},
							error: function(xhr){	
								alert("搜尋[輸入 KEY,找是否重複]過程中，程式發生錯誤!!!\nerror code:"+ xhr.status);
							}
						});
						
					if ((!blnHave && patracod == 1 )  )  {	
					//找人事基本檔1.薪資基本檔[沒]找到+1新進
					//         2.薪資基本檔[有]找到+33納實37職務調整 || ( blnHave && (patracod == 33 || patracod == 37 
// 						if(patracod == 33 || patracod == 37){
// 							code_data={
// 				        		idtno      :data.basPsnItem.payidno,
// 				        		hid        :pahid
// 				        	};
// 						}else{ 
							code_data={
				        		idtno      :key,
				        		hid        :pahid
				        	};
// 			        	}
						$.ajax({				
							url:"${pageContext.request.contextPath}/salController.do",
							data:{
								method  : 'getPSBasicById',
								code_data :JSON.stringify(code_data),
								hid     : pahid,
								userid  : paUserid
							},
			
							type:"POST",		//type:POST, GET(default:GET)
							dataType:"json",	//dataType: text, xml, html, script, json, jsonp
							contentType: "application/x-www-form-urlencoded;charset=utf-8",  
							async: false,
							cache: false,			
							success:function(data) {
								if ( data.basPsnItem!=null ){
									//顯示異動個人明細
									$.doTraPsnDetail(data.basPsnItem,"bas",patracod);
									//pa.paTrapay異動支扣明細--原
									cleanTable("TraPayO");																		
								}	
							},
							error: function(xhr){	
								alert("搜尋[輸入 KEY,找是否重複]過程中，程式發生錯誤!!!\nerror code:"+ xhr.status);
							}
						}); 
					} 	
				}
			}
		}			
	});
	//
	$('#patracod').blur(function(){
		//[存檔]disabled,設預設值
		if( $("#subPATRAPSN_Insbtn").prop("disabled")==false){
			patracod=getInt($("#patracod option:selected").val());
			$("#paymonno_lbl").html("追補扣日數");
			$("#div_paypflag").addClass("d-none");
			$("#paymonno,#paydayno").val("0");//追補扣日數
			//新進
			if (patracod == 1){
				$("#paytaxno").val("0");//扶養人數
				$("#paydpno").val("1");	//健保人數
			}
			//年終獎金
			else if (patracod==51){
				$("#div_paypflag").removeClass("d-none");
				$("#paymonno_lbl").html("核發月數");
				$("#paypflag_lbl").html(" 發放年終獎金比例");
				select = $('#paypflag');
				select.empty();
				$.each(pflgBOptions, function(val, text) {
				    options = "<option value='" + val + "'>" +text ;
					select.append(options);
				});
				select.selectpicker('refresh');
				$("#paypflag").selectpicker('val','3');
				$("#paymonno").val("12");
			}
			//考績獎金
			else if (patracod==11 || patracod==12){			
				$("#div_paypflag").removeClass("d-none");
				$("#paypflag_lbl").html(" 發放考績獎金月數");
				select = $('#paypflag');
				select.empty();
				$.each(pflgAOptions, function(val, text) {			    
				    options = "<option value='" + val + "'>" + text ;
					select.append(options);
				});
				
				select.selectpicker('refresh');
			}
			
		}	
	});
	//原支扣明細
 	var objCopyB = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyB.aoColumns = [	
							{ "mData": null},
							{ "mData": null},
							{ "mData": "pacodnmc" },
							{ "mData": "payamtp" },
							{ "mData": "payamtd" }
						 ];
						 
	objCopyB.iDisplayLength=20;
	
	objCopyB = $.extend(true,objCopyB, {
	  			 "fnDrawCallback": function () {
						var api = this.api();
						var startIndex=0;
                    	api.column(0).nodes().each(function(cell, i) {
                        	cell.innerHTML = startIndex + i + 1;
                    	});
                 },   
                 "footerCallback": function ( row, data, start, end, display ) {
		            var api = this.api(), data;
		 
		            // Remove the formatting to get integer data for summation
		            var intVal = function ( i ) {
		                return typeof i === 'string' ?
		                    		  i.replace(/[\$,]/g, '')*1 :
		                    		  typeof i === 'number' ? i : 0;
		            };
		 
		            // Total over this page
		            pTotal = api
		                .column( 3	, { page: 'current'} )
		                .data()
		                .reduce( function (a, b) {
		                    		return intVal(a) + intVal(b);
		                		}, 0 );
		 
		            // Update footer
		            $( api.column( 3 ).footer() ).html(
		                '總所得:'+pTotal 
		            );
		            // Total over this page
		            dTotal = api
		                .column( 4	, { page: 'current'} )
		                .data()
		                .reduce( function (a, b) {
		                    		return intVal(a) + intVal(b);
		                		}, 0 );
		 
		            // Update footer
		            $( api.column( 4 ).footer() ).html(
		                '總支出:'+dTotal 
		            );
		        }   							
			});   
                    
                    					 
	objCopyB.aoColumnDefs= [
 							{
							   'targets': 1,
							   'searchable':false,
							   'orderable':true,
							   'className': 'dt-body-center',
							   'render': function (data, type, row, meta){
							   				var color;
							   				if (data.paycode!=null && typeof(data.paycode)!="undefined"){
							   				
								   				if (data.paycode.substring(2, 1) == "P" && data.paycode.substring(4, 5) == "9") {
								   					color="blue";
								   				}else if (data.paycode.substring(2, 1) == "D" && data.paycode.substring(4, 5) == "9") {
								   					color="red";	
								   				}
								   			}		
							   				return '<span style="color:' + color + '">' + data.paycode + '</span>';
                                         }
						     },
						     {
							   'targets': [ 3,4],
							   'className': 'dt-body-right'
						     }
						    ];
						    
	objCopyB = $.extend(true,objCopyB, {
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr form-inline"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br form-inline"ip>',
		buttons: [
           'excelHtml5' 
        ]
	}); 
	$('#TblOTraPayList').dataTable(objCopyB);
	
	//新支扣明細
 	var objCopyC = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyC.aoColumns = [	
							{ "mData": null},
							{ "mData": null},
							{ "mData": "pacodnmc" },
							{ "mData": "payamt" },
							{ "mData": null},
						 ];
						 
	objCopyC.iDisplayLength=20;
	
	objCopyC = $.extend(true,objCopyC, {
				 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
 						$('td:eq(4)', nRow).html( '<button type="button" class="edit btn btn-primary" data-toggle="modal"><i class="fa fa-edit"></i>編輯</button>&emsp;<button type="button" class="delPaybtn btn btn-danger"><i class="fa fa-trash-alt"></i>刪除</button>');
				 }, 
	  			 "fnDrawCallback": function () {
					var api = this.api();
					var startIndex=0;
                   	api.column(0).nodes().each(function(cell, i) {
                       	cell.innerHTML = startIndex + i + 1;
                   	});
                   	//等datatable draw完成後，補上button 初始化 
				 	//jQuery按鈕圖示，先取消onclick預設的動作
					//刪除	
					$(".delPaybtn")
						.click(function(){
							delCheck = confirm("請確認是否刪除！！\n\n若是，請按【確定】．\n\n若不是，請按【取消】");
							if (delCheck) {		
								oTable = $('#' + payTbl ).dataTable().api();
						        tr = $(this).parents('tr');
						        rowIndex = oTable.row(tr).index();
						        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data
						        code_data="";
						   		code_data={
						        	tranum : rowData.tranum,
						        	patradt : rowData.patradt,
						        	paysort : rowData.paysort,
						        	paycode : rowData.paycode,
						        	hid     : rowData.hid
						        }    
						        $.ajax({
									url:"${pageContext.request.contextPath}/salController.do",
									data:{
											method  : 'delTblCode',
											tblname : 'PATRAPAY',
											"code_data" : JSON.stringify(code_data)
									},
									type:"GET",			//type:POST, GET(default:GET)
									dataType:"json",	//dataType: text, xml, html, script, json, jsonp
									contentType: "application/x-www-form-urlencoded;charset=utf-8",  
									success:function(data) {
										oTable = $('#' + payTbl ).dataTable();
										oTable.fnDeleteRow(rowIndex);
									},
									error:function(xhr) {
										alert("刪除失敗，程式發生錯誤!!!\nerror code:"+ xhr.status);  
									}
								});
							}
						});
					//編輯	
					$(".edit")
						.click(function(){	
							oTable = $('#' + payTbl ).dataTable().api();
							info = oTable.page.info();		//記錄目前頁碼
					        tr = $(this).parents('tr');
					        rowIndex = oTable.row(tr).index();
					        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data
					        $("#patrapay_payidno").val(rowData.payidno);
							$("#patrapay_tranum").val(rowData.tranum);
							$("#patrapay_paysort").val(rowData.paysort);
							$("#patrapay_patradt").val(rowData.patradt);
							$('#patrapay_paycode').selectpicker('val', rowData.paycode);
							$("#patrapay_payamt").val(rowData.payamt);
							$("#patrapay_payauth").val(rowData.payauth);
							
							$("#patrapay_payiftax").val(rowData.payiftax);
							$("#patrapay_payflag").val(rowData.payflag)
							$("#patrapay_hid").val(rowData.hid);
							$("#patrapay_payupdtm").val(rowData.payupdtm);
							$("#model_header_title").html("修改[新 支扣明細]");
							
							$("#patrapay_patradt").attr("readOnly",true);	//將key設成readOnly
							$("#patrapay_paysort").attr("readOnly",true);
							$("#patrapay_paycode").attr("readOnly",true);
							$("#patrapay_payidno").attr("readOnly",true);
							
							$("label.error").hide();
 							$(".error").removeClass("error");
					        $("#addPayeDialog").modal("show");
							$("#btn_subTraPayIns").prop("disabled", true);
							$("#btn_subTraPayUpd").prop("disabled", false);
						});	
                 }   							
			});   
                    					 
	objCopyC.aoColumnDefs= [
 							{
							   'targets': 1,
							   'searchable':false,
							   'orderable':true,
							   'className': 'dt-body-center',
							   'render': function (data, type, row, meta){
							   				var color;
							   				if (data.paycode!=null && typeof(data.paycode)!="undefined"){
							   				
								   				if (data.paycode.substring(2, 1) == "P" && data.paycode.substring(4, 5) == "9") {
								   					color="blue";
								   				}else if (data.paycode.substring(2, 1) == "D" && data.paycode.substring(4, 5) == "9") {
								   					color="red";	
								   				}
								   			}		
							   				return '<span style="color:' + color + '">' + data.paycode + '</span>';
                                         }
						     }
						    ];
						    
	objCopyC = $.extend(true,objCopyC, {
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr form-inline"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br form-inline"ip>',
		buttons: [
           'excelHtml5' 
        ]
	}); 
	$('#TblNTraPayList').dataTable(objCopyC);
	
	//篩選
 	var objCopyD = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyD.aoColumns = [	
							{ "sTitle":"異動日","mData": "patradt" },
							{ "sTitle":"異動項目","mData": "traname" },
							{ "sTitle":"離職","mData": "paylvefg" },
							{ "sTitle":"卡號","mData": "paycard" },
							{ "sTitle":"職號","mData": "payempno" },
							{ "sTitle":"身分證","mData": "payidno" },
							{ "sTitle":"姓名","mData": "paynamec" },
							{ "sTitle":"初任公職日","mData": "paygovdt" },
							{ "sTitle":"到院日","mData": "payempdt" },
							{ "sTitle":"現職日","mData": "paysgndt" },
							{ "sTitle":"離職日","mData": "payrsgdt" },
							{ "sTitle":"生效日","mData": "paeffdt" },
							{ "sTitle":"單位","mData": "padepnmc" },
							{ "sTitle":"新/舊","mData": "contract" },							
							{ "sTitle":"健保數","mData": "paydpno" },
							{ "sTitle":"扶養數","mData": "paytaxno" },
							{ "sTitle":"保險補助","mData": "payinflg" },
							{ "sTitle":"投保級距","mData": "insuamt" },
							{ "sTitle":"原職稱","mData": "paotitlp" },
							{ "sTitle":"原階別","mData": "oranknm" },
							{ "sTitle":"原職等","mData": "paornk2n" },
							{ "sTitle":"原本功","mData": "orank3nm" },
							{ "sTitle":"原級別","mData": "paodegr2" },
							{ "sTitle":"原俸點","mData": "paodegre" },
							{ "sTitle":"原權理","mData": "paorank2" },
							{ "sTitle":"原主管權理","mData": "paorankc" },
							{ "sTitle":"原比照","mData": "ornk4nm" },
							{ "sTitle":"原支領","mData": "paocsfla" },
							{ "sTitle":"原基本績分","mData": "pfobas" },
							{ "sTitle":"原俸級績分","mData": "pfodat" },
							{ "sTitle":"原額外1","mData": "paopfsp1" },
							{ "sTitle":"原額外2","mData": "paopfsp2" },
							{ "sTitle":"原額外3","mData": "paopfsp3" },
							{ "sTitle":"專勤額度","mData": "paypfdoc" },
							{ "sTitle":"新職稱","mData": "paytitlp" },
							{ "sTitle":"新階別","mData": "rankname" },
							{ "sTitle":"新職等","mData": "payrnk2n" },
							{ "sTitle":"新本功","mData": "rank3nm" },
							{ "sTitle":"新級別","mData": "paydegr2" },
							{ "sTitle":"新俸點","mData": "paydegre" },
							{ "sTitle":"新權理","mData": "payrank2" },
							{ "sTitle":"新主管權理","mData": "payrankc" },
							{ "sTitle":"新比照","mData": "rnk4nm" },
							{ "sTitle":"新支領","mData": "pacsflag" },
							{ "sTitle":"新基本績分","mData": "pfbas" },
							{ "sTitle":"新俸級績分","mData": "pfdat" },
							{ "sTitle":"新額外1","mData": "paypfsp1" },
							{ "sTitle":"新額外2","mData": "paypfsp2" },
							{ "sTitle":"新額外3","mData": "paypfsp3" },
							{ "sTitle":"新專勤額度","mData": "paypfdoc" },
							{ "sTitle":"專2","mData": "paretention" }							
						 ];
	
	objCopyD = $.extend(true,objCopyD, {
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr form-inline"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br form-inline"ip>',
		buttons: [
           'excelHtml5' 
        ]
	});
	 		      
});
</script>
</head>
<body>

	<ul class="nav nav-pills mb-3" id="pills-tab" role="tab">
		<li class="nav-item"><a class="nav-link active" id="pills-basic-tab"   data-toggle="pill" href="#byTranDetailMain"  role="tab" aria-controls="pills-home" aria-selected="true">異動資料</a></li>
		<li class="nav-item"><a class="nav-link"        id="pills-query-tab"   data-toggle="pill" href="#byTranFilterMain"  role="tab" aria-controls="pills-profile" aria-selected="false">篩選</a></li>
		<li class="nav-item"><a class="nav-link"        id="pills-handing-tab" data-toggle="pill" href="#byTranHandingMain" role="tab" aria-controls="pills-profile" aria-selected="false">異動處理</a></li>
	</ul>

	<div class="tab-content" id="pills-tabContent">
		<!--異動資料-->
		<div class="tab-pane fade show active" id="byTranDetailMain"
			role="tabpanel" aria-labelledby="pills-basic-tab">
			<div class="container-fluid">
				<div class="row" id="baseItem">
					<div class="col-10">
						<div class="form-inline">
							<div class="input-group">
								<%-- <select id="tran_salnum" class="selectpicker SALNUM"
									data-width="fit" multiple title="餉單編號">
									<option value="0">0</option>
									<c:forEach var="bitem" items="${alBankDtTbl}" varStatus="loop">
										<option value="${bitem.salnum}">${bitem.salnum}</option>
									</c:forEach>
								</select> <select id="tran_patradt" class="selectpicker PATRADT"
									data-width="fit" data-live-search="true" multiple title="異動日">
									<option value=""></option>
									<c:forEach var="bitem" items="${alBankDtTbl}" varStatus="loop">
										<option value="${bitem.paybank}">${bitem.paybank}</option>
									</c:forEach>
								</select>  --%>
								<input type="text" id="tran_salnum" size="4"
									class="form-control-sm" placeholder="餉單編號">
								<input type="text" id="tran_patradt" size="10"
									class="form-control-sm gclsCalTxt" placeholder="異動日">
								<select id="tran_patracd" class="selectpicker" data-width="fit"
									data-live-search="true" multiple title="異動原因">
									<option value=""></option>
									<c:forEach var="bitem" items="${altracdTbl}" varStatus="loop">
										<option value="${bitem.tracode}">${bitem.traname}</option>
									</c:forEach>
								</select> <input type="text" id="tran_card" size="4"
									class="form-control-sm text-uppercase" placeholder="卡號">
								<input type="text" id="tran_idno" size="10"
									class="form-control-sm text-uppercase" placeholder="身分證">
								<input type="text" id="tran_namec" size="12"
									class="form-control-sm " placeholder="姓名"> <input
									type="text" id="tran_empno" size="7"
									class="form-control-sm text-uppercase" placeholder="職號">

								<select id="tran_dept" name="tran_dept"
									class="selectpicker PAYDEPT" data-width="fit"
									data-live-search="true" multiple title="單位">
									<option value=""></option>
									<c:forEach var="uitem" items="${alDeptCodeTbl}"
										varStatus="loop">
										<option value="${uitem.padepcod}">${uitem.pacstno}-${uitem.padepnmc}</option>
									</c:forEach>
								</select>

							</div>
						</div>
					</div>
					<div class="col-2">
						<button id="btn_qry_tran_TranPsn" class="btn btn-success" type="button"><i class="fa fa-search"></i>查詢</button>
						<button id="btn_cln_tran_TranPsn" class="btn btn-warning"type="button"><i class="fa fa-eraser"></i>清空</button>
					</div>
				</div>
				<div class="row">
					<div class="col-4 border border-dark">
						<div id="TranPsnListDiv_1" class="TranPsnListDiv">
							<!-- pa.paTraPsn清單 -->
							<table id="tblTranPsnList" class="table table-striped table-bordered table-hover">								
							</table>
						</div>
					</div>

					<div class="col-8 border border-dark">
						<div class="container-fluid">
							<form id="subPATraPsnForm" class="form-inline">
								<div id="accordion">
									<div class="card">
										<div class="card-header">
											<a class="card-link" data-toggle="collapse" href="#collapseOne">【異動個人明細】 </a>
											<button type="button" class="btn btn-success roleQry" id="subPATRAPSN_Newbtn">
												<i class="fa fa-plus"></i>新增一筆
											</button>
											<button type="button" class="btn btn-success roleQry" id="subPATRAPSN_Insbtn" disabled>
												<i class="fa fa-save"></i>存檔
											</button>
											<button type="button" class="btn btn-primary roleQry" id="subPATRAPSN_Updbtn" disabled>
												<i class="fa fa-edit"></i>更改
											</button>
											<button type="button" class="btn btn-danger roleQry" id="subPATRAPSN_Delbtn" disabled>
												<i class="fa fa-trash-alt"></i>刪除
											</button>
										</div>
										<div id="collapseOne" class="collapse show"	data-parent="#accordion">
											<div class="card-body">
												<!-- pa.paTraPsn明細 -->
												<div class="table-responsive-sm">
													<INPUT type="hidden" id="trapsnfun" value="">
													<table id="PATRAPSN"
														class="table table-striped table-bordered table-hover table-sm table-condensed">
														<INPUT type="hidden" id="hid" value="">
														<INPUT type="hidden" id="tranum" value="">
														<tbody>
															<tr>
																<td>
																	<div class="form-inline">
																		<div class="form-group">
																			<label for="patradt"class="form-inline table-warning">*發薪日:</label> 
																			<input type="text" id="patradt" class="form-control dateT table-warning required" maxlength="10" size="10" /> 
																			<label for="patracod" class="form-inline table-warning">*異動原因:</label> 
																			<select id="patracod" name="patracod" class="form-control required" data-width="fit" title="異動原因">
																			</select> 
																			<label for="salnum">餉單編號:</label> 
																			<input type="text" id="salnum" readonly class="form-control" maxlength="4" size="4" /> 
																			<label for="paylvefg">離職:</label>
																			<input type="text" id="paylvefg" readonly class="text-uppercase form-control" maxlength="1" size="1" />
																		</div>
																	</div>
																</td>
															</tr>
															<tr>
																<td>
																	<div class="form-inline">
																		<div class="form-group">
																			<label for="payidno" class="table-warning">*身份證字號:</label>
																			<input type="text" id="payidno"
																				class="text-uppercase form-control required findkey"
																				maxlength="10" size="10" />
																		</div>
																		<div class="form-group">
																			<label for="paycard"
																				class="form-inline table-warning">卡號:</label> <input
																				type="text" id="paycard"
																				class="text-uppercase form-control required findkey"
																				maxlength="4" size="4"
																				style="font-weight: bold; font-size: 30px;" />
																		</div>
																		<div class="form-group">
																			<label for="payempno"
																				class="form-inline table-warning">職號:</label> <input
																				type="text" id="payempno"
																				class="text-uppercase form-control required"
																				maxlength="7" size="7" />
																		</div>
																		<div class="form-group">
																			<label for="paynamec"
																				class="form-inline table-warning">姓名:</label> <input
																				type="text" id="paynamec"
																				class="form-control required" maxlength="12"
																				size="12" />
																		</div>

																	</div>
																</td>
															</tr>
															<tr>
																<td>
																	<div class="form-inline">
																		<div class="form-group">
																			<label for="paygovdt" class="table-warning">初任公職日:</label>
																			<input type="text" id="paygovdt" class="gclsCalTxt form-control required" maxlength="10" size="10" />
																		</div>
																		<div class="form-group">
																			<label for="payempdt" class="table-warning">到院日:</label>
																			<input type="text" id="payempdt" class="gclsCalTxt form-control required" maxlength="10" size="10" />
																		</div>
																		<div class="form-group">
																			<label for="paysgndt" class="table-warning">現職日:</label>
																			<input type="text" id="paysgndt" class="gclsCalTxt form-control required" maxlength="10" size="10" />
																		</div>
																		<div class="form-group">
																			<label for="payrsgdt" class="table-warning">離職日:</label>
																			<input type="text" id="payrsgdt" class="gclsCalTxt form-control" maxlength="10" size="10" />
																		</div>
																	</div>
																</td>
															</tr>

															<tr>
																<td>
																	<div class="form-inline">
																		<div class="form-group">
																			<label for="paeffdt" class="table-warning">生效日:</label>
																			<input type="text" id="paeffdt"
																				class="gclsCalTxt form-control required"
																				maxlength="10" size="10" />
																		</div>
																		<div class="form-group">
																			<label for="paymonno" id="paymonno_lbl"
																				class="table-warning">追補扣日數:</label> <input
																				type="text" id="paymonno" class="form-control"
																				maxlength="3" size="3" />月 <input type="text"
																				id="paydayno" class="form-control" maxlength="3"
																				size="3" />日
																		</div>
																		<div class="form-group" id="div_paypflag">
																			<label for="paypflag" id="paypflag_lbl"
																				class="table-warning">發放考績獎金月數:</label> <select
																				id="paypflag" name="paypflag" class="form-control selectpicker">
																				<option value="">請選擇</option>
																				<option value="0">0</option>
																				<option value="1">0.5</option>
																				<option value="2">1</option>
																				<option value="3">1.5</option>
																				<option value="4">2</option>
																			</select>
																		</div>
																	</div>
																</td>
															</tr>
															<tr>
																<td>
																	<div class="form-inline">
																		<div class="form-group">
																			<label for="paytaxno" class="table-warning">扶養人數:</label>
																			<input type="text" id="paytaxno" class="form-control"
																				maxlength="2" size="2" />
																		</div>
																		<div class="form-group">
																			<label for="paydpno" class="table-warning">健保人數:</label>
																			<input type="text" id="paydpno" class="form-control"
																				maxlength="7" size="7" />
																		</div>
																		<div class="form-group">
																			<label id="lblInsuamt" class="table-warning bgcol">投保級距:</label>
																			<select id="insuamt" name="insuamt"
																				class="form-control " title="投保級距"></select>
																		</div>
																		<div class="form-group">
																			<label for="payinflg" class="table-warning">保險費比率:</label>
																			<select id="payinflg" class="form-control">
																				<option value="">請選擇</option>
																				<option value="0">0</option>
																				<option value="1">1/4</option>
																				<option value="2">1/2</option>
																				<option value="3">全額</option>
																				<option value="4">全額有上限</option>
																				<option value="5">公勞1/4、健免</option>
																				<option value="6">公勞1/2、健免</option>
																				<option value="7">公勞有、健免</option>
																				<option value="8">公勞有、健1/4</option>
																				<option value="9">公勞有、健1/2</option>
																			</select>

																		</div>
																	</div>
																</td>
															</tr>

															<TR>
																<td>
																	<div class="form-inline">
																		<div class="form-group">
																			<label for="paydept" class="table-warning">單位:</label>
																			<select id="paydept" name="paydept"
																				class="form-control selectpicker required"
																				data-width="fit" data-live-search="true" title="單位"></select>
																		</div>
																	</div>
																</td>
															<tr class="table-info">
																<td>『原』</td>
															</tr>
															<tr>
																<td>
																	<div class="form-inline">
																		<div class="form-group">
																			<label for="paotitle" class="table-warning">職稱:</label>
																			<select id="paotitle" name="paotitle"
																				class="form-control selectpicker" data-width="fit"
																				title="職稱" disabled></select>
																		</div>
																	</div>
																	<div class="form-inline">
																		<div class="form-group">
																			<label for="paorank" class="table-warning">階別:</label>
																			<select id="paorank" name="paorank"
																				class="form-control " title="階別" disabled></select>
																		</div>
																		<div class="form-group">
																			<label for="paornk2n" class="table-warning">職等:</label>
																			<select id="paornk2n" name="paornk2n"
																				class="form-control " data-width="fit" title="職等"
																				disabled></select>
																		</div>
																		<div class="form-group">
																			<label for="paorank3" class="table-warning">本功別:</label>
																			<select id="paorank3" name="paorank3"
																				class="form-control " title="本功別" disabled></select>
																		</div>
																		<div class="form-group">
																			<label for="paodegr2" class="table-warning">級別:</label>
																			<select id="paodegr2" name="paodegr2"
																				class="form-control " title="級別" disabled></select>
																		</div>
																		<div class="form-group">
																			<label for="paodegre" class="table-warning">俸點:</label>
																			<input type="text" id="paodegre" class="bgcol form-control"
																				maxlength="2" size="2" readonly />
																		</div>
																		<div id="div_contraco" class="form-group d-none">
																			<label for="TranPsn_contraco">月尾薪Y/月頭薪N:</label> <input
																				type="text" id="TranPsn_contraco" readonly
																				class="form-control" maxlength="1" size="1" />
																		</div>
																	</div>
																</td>
															</tr>
															<tr>
																<td>
																	<div class="form-inline d-none" id="div_authorito">
																		<div class="form-group">
																			<label for="paorank2" class="table-warning">權理:</label>
																			<select id="paorank2" name="paorank2"
																				class="form-control " title="權理" disabled></select>
																		</div>
																		<div class="form-group">
																			<label for="paorankc" class="table-warning">主管(權理):</label>
																			<select id="paorankc" name="paorankc"
																				class="form-control " title="主管(權理)" disabled></select>
																		</div>
																		<div class="form-group">
																			<label for="paorank4" class="table-warning">適用專業加給表:</label>
																			<select id="paorank4" name="paorank4"
																				class="form-control " title="適用專業加給表" disabled></select>
																		</div>
																	</div>
																</td>
															</tr>
															<TR>
																<td>
																	<div class="form-inline">
																		<div class="form-group">
																			<label for="paocsfla" class="table-warning">工作獎金:</label>
																			<select id="paocsfla" name="paocsfla" class="form-control calCSPF">
																				<option value="">請選擇</option>
																				<option value="Y">Y</option>
																				<option value="N">N</option>
																			</select>
																		</div>
																		<div class="form-group" id="div_pokey">
																			<label for="TranPsn_pokey">實際支領KEY:</label> <input
																				type="text" id="TranPsn_pokey" readonly
																				class="form-control" maxlength="7" size="7" /> <label
																				for="TranPsn_pobas">基本點數:</label> <input
																				type="text" id="TranPsn_pobas" readonly
																				class="form-control" maxlength="3" size="3" /> <label
																				for="TranPsn_podat">職等點數:</label> <input
																				type="text" id="TranPsn_podat" readonly
																				class="form-control" maxlength="3" size="3" />
																		</div>
																		<div class="form-group" id="div_podoc">
																			<label for="paopfdoc" class="table-warning">專勤基本額度:</label>
																			<input type="text" id="paopfdoc" class="form-control"
																				maxlength="8" size="8" />
																		</div>
																	</div>
																<TD>
															</TR>
															<tr>
																<td>
																	<div class="cspf_o d-none">
																		<div class="form-inline">
																			<div class="form-group ">
																				<label for="paopfsp1" class="table-warning">額外點數:</label>
																				<select id="paoexpfkey1" name="paoexpfkey1"  class="selectpicker" data-width="fit" title="額外1"></select> 
																				<input type="text" id="paopfsp1" class="form-control number" maxlength="3" size="3" /> 
																				<select id="paoexpfkey2" name="paoexpfkey2" class="selectpicker" data-width="fit" title="額外2"> </select> 
																				<input type="text" id="paopfsp2" class="form-control number" maxlength="3" size="3" /> 
																				<select id="paoexpfkey3" name="paoexpfkey3" class="selectpicker" data-width="fit" title="額外3"> </select> 
																				<input type="text" id="paopfsp3" class="form-control number" maxlength="3" size="3" />
																			</div>
																		</div>
																	</div>
																</td>
															</tr>
															<tr class="table-info">
																<td>『新』</td>
															</tr>
															<tr class="table-warning">
																<td>
																	<div class="form-inline">
																		<div class="form-group">
																			<label for="paytitle" class="table-warning">職稱:</label>
																			<select id="paytitle" name="paytitle"
																				class="form-control selectpicker calRank calCSPF "
																				data-width="fit" data-live-search="true" title="職稱">
																			</select>
																			<button type="button" class="btn btn-info roleQry" id="btn_calDegre" disabled> <i class="fa fa-arrows-alt"></i>取得新支扣</button>
																		</div>
																	</div>
																	<div class="form-inline">
																		<div class="form-group">
																			<label for="payrank" class="table-warning">階別:</label>
																			<select id="payrank" name="payrank"
																				class="form-control calCSPF calRank" title="階別"></select>

																		</div>
																		<div class="form-group">
																			<label for="payrnk2n" class="table-warning">職等:</label>
																			<select id="payrnk2n" name="payrnk2n"
																				class="form-control calCSPF calRank"
																				data-width="fit" title="職等"></select>
																			<!--<input type="text" id="payrnk2n" class="form-control" maxlength="2" size="2"  /> -->
																		</div>
																		<div class="form-group">
																			<label for="payrank3" class="table-warning">本功別:</label>
																			<select id="payrank3" name="payrank3"
																				class="form-control calCSPF calRank" title="本功別"></select>
																		</div>
																		<div class="form-group">
																			<label for="paydegr2" class="table-warning">級別:</label>
																			<select id="paydegr2" name="paydegr2"
																				class="form-control calCSPF calRank" title="級別"></select>
																		</div>
																		<div class="form-group">
																			<label for="paydegre" class="table-warning">俸點:</label>
																			<input type="text" id="paydegre" class="bgcol form-control"
																				maxlength="2" size="2" />
																		</div>
																		<div id="div_contract" class="form-group d-none">
																			<label for="TranPsn_contract">月尾薪Y/月頭薪N:</label> <input
																				type="text" id="TranPsn_contract" readonly
																				class="form-control" maxlength="1" size="1" />
																		</div>
																	</div>
																</td>
															</tr>
															<tr class="table-warning">
																<td>
																	<div class="form-inline" id="div_authority">
																		<div class="form-group">
																			<label for="payrank2" class="table-warning">權理:</label>
																			<select id="payrank2" name="payrank2"
																				class="form-control " data-width="fit" title="權理"></select>
																		</div>
																		<div class="form-group">
																			<label for="payrankc" class="table-warning">主管(權理):</label>
																			<select id="payrankc" name="payrankc"
																				class="form-control " data-width="fit"
																				title="主管(權理)"></select>
																		</div>
																		<div class="form-group">
																			<label for="payrank4" class="table-warning">適用專業加給表:</label>
																			<select id="payrank4" name="payrank4"
																				class="form-control " data-width="fit"
																				title="適用專業加給表"></select>
																		</div>
																	</div>
																</td>
															</tr>
															<TR class="table-warning">
																<td>
																	<div class="form-inline">
																		<div class="form-group">
																			<label for="pacsflag" class="table-warning">工作獎金:</label>
																			<select id="pacsflag" name="pacsflag"
																				class="form-control calCSPF">
																				<option value="">請選擇</option>
																				<option value="Y">Y</option>
																				<option value="N">N</option>
																			</select>
																		</div>
																		<div class="form-group" id="div_pfkey">
																			<label for="TranPsn_pfkey">實際支領KEY:</label> 
																			<input type="text" id="TranPsn_pfkey" readonly class="form-control" maxlength="7" size="7" /> 
																			<label for="TranPsn_pfbas">基本點數:</label> 
																			<input type="text" id="TranPsn_pfbas" readonly class="bgcol form-control" maxlength="3" size="3" /> 
																			<label for="TranPsn_pfdat">職等點數:</label> 
																			<input type="text" id="TranPsn_pfdat" readonly class="bgcol form-control" maxlength="3" size="3" />
																		</div>
																		<div class="form-group" id="div_pfdoc">
																			<label for="paypfdoc" class="table-warning">專勤基本額度:</label>
																			<input type="text" id="paypfdoc" class="form-control"
																				maxlength="8" size="8" />
																		</div>
																	</div>
																<TD>
															</TR>
															<tr class="table-warning">
																<td>
																	<div class="cspf_n d-none">
																		<div class="form-inline">
																			<div class="form-group ">
																				<label for="payexpfkey1" class="table-warning">額外點數:</label>
																				<select id="payexpfkey1" name="payexpfkey1" class="selectpicker"
																					data-width="fit" title="額外1"></select> 
																				<input type="text" id="paypfsp1" class="form-control number" maxlength="3" size="3" /> 
																				<select id="payexpfkey2" name="payexpfkey2" class="selectpicker" data-width="fit" title="額外2"></select>
																				<input type="text" id="paypfsp2" class="form-control number" maxlength="3" size="3" /> 
																				<select id="payexpfkey3" name="payexpfkey3" class="selectpicker" data-width="fit" title="額外3"></select> 
																				<input type="text" id="paypfsp3" class="form-control number" maxlength="3" size="3" />
																			</div>
																		</div>

																	</div>
																</td>
															</tr>

															<tr>
																<td><a href="#basother" class="btn btn-info" data-toggle="collapse" id="otherCollapse">其他</a>
																	<div id="basother" class="collapse">
																		<div class="form-inline">
																			<div class="form-group">
																				<label for="payctcbacno" class="table-warning">薪資帳號:</label>
																				<input type="text" id="payctcbacno"
																					class="form-control number" maxlength="14" size="14" />
																				<label for="payctcbgedno" class="table-warning">公儲帳號:</label>
																				<input type="text" id="payctcbgedno"
																					class="form-control number" maxlength="14" size="14" />
																			</div>
																		</div>
																		<div class="form-inline">
																			<div class="form-group">
																				<label for="payaddrs" class="table-warning">戶籍地址:</label>
																				<input type="text" id="payaddrs"
																					class="form-control" maxlength="62" size="62" />
																			</div>
																		</div>
																		<div class="form-inline">
																			<div class="form-group">
																				<label for="TranPsn_payupdid">更改者:</label> <input
																					type="text" id="TranPsn_payupdid" readonly
																					class="form-control" maxlength="4" size="4" /> <label
																					for="TranPsn_updtm">更改時間:</label> <input
																					type="text" id="TranPsn_updtm" readonly
																					class="form-control" maxlength="20" size="20" />
																			</div>
																		</div>
																	</div>
																</td>
															</tr>

														</tbody>
													</table>
												</div>
											</div>
										</div>
									</div>
									<div class="card">
										<div class="card-header">
											<a class="collapsed card-link" data-toggle="collapse"
												href="#collapseTwo">支扣明細 </a>
										</div>
										<div id="collapseTwo" class="collapse"
											data-parent="#accordion">
											<div class="card-body">
												<!-- pa.paTrapay原清單 -->
												<!-- 一組開頭 style="border-spacing: 1px 1px;padding:0px;"-->
												<div class="row">
													<div class="col-sm-12">
														<table id="TraPayO"	class="table table-sm table-striped table-bordered table-condensed table-responsive dynamic">
															<thead>
																<tr>
																	<th>項目代碼</th>
																	<th> [原]項目中文</th>
																	<th>所得</th>
																	<th>支出</th>
																	<th></th>
																	<th></th>
																	<th></th>
																	<th></th>
																	<th></th>
																	<th></th>
																</tr>
															</thead>
															<tfoot>
																<tr>
																	<td></td>
																	<td></td>
																	<td id="o_sumpay_p" class="text-right"></td>
																	<td id="o_sumpay_d" class="text-right"></td>
																	<td></td>
																	<td></td>
																	<td></td>
																	<td></td>
																	<td></td>
																	<td></td>
																</tr>
															</tfoot>
															<tbody>
																<tr>
																	<td>
																		<!-- 支扣代碼--> <!-- <div class="input-group"> -->
																		<input class="form-control" type="text" id="o_paycode"
																		name="paycode" value="" maxlength="5" size="5"
																		disabled>
																	</td>
																	<td><input class="form-control" type="text"
																		id="o_codenm" name="codenm" value="" maxlength="22"
																		size="22" disabled></td>
																	<td>
																		<!-- 收入--> <input class="form-control text-right "
																		type="text" id="o_payamt" name="payamtP" value=""
																		maxlength="10" size="10" disabled>
																	</td>
																	<td>
																		<!-- 支出--> <input class="form-control text-right "
																		type="text" id="o_payamt" name="payamtD" value=""
																		maxlength="10" size="10" disabled>
																	</td>
																	<td>
																		<!-- hidden--> <input class="form-control"
																		type="hidden" id="o_paysort" name="paysort" value="O">
																	</td>
																	<td><input class="form-control" type="hidden"
																		id="o_payauth" name="payauth" value=""></td>
																	<td><input class="form-control" type="hidden"
																		id="o_payiftax" name="payiftax" value=""></td>
																	<td><input class="form-control" type="hidden"
																		id="o_payidno" name="payidno" value=""></td>
																	<td><input class="form-control" type="hidden"
																		id="o_hid" name="hid" value=""></td>
																	<td><input class="form-control" type="hidden"
																		id="o_tranum" name="tranum" value=""></td>
																</tr>
															</tbody>
														</table>
													</div>
													<!-- <div class="col-sm-12"> -->
												</div>
												<!-- row -->
												<div class="row">
													<!-- pa.paTrapay新清單 -->
													<!-- 一組開頭 -->
													<table id="TraPayN"
														class="table table-sm table-striped table-warning table-bordered border-primary dynamic">
														<thead>
															<tr>
																<th width="5%">刪除</th>
																<th class="text-center">[新]項目</th>
																<th>所得</th>
																<th>支出</th>
																<th></th>
																<th></th>
																<th></th>
																<th></th>
															</tr>
														</thead>
														<tfoot>
															<tr>
																<td></td>
																<td></td>
																<td id="n_sumpay_p" class="text-right"></td>
																<td id="n_sumpay_d" class="text-right"></td>
																<td></td>
																<td></td>
																<td></td>
																<td></td>
															</tr>
														</tfoot>
														<tbody>
															<tr>
																<td>
																	<!-- Table刪除 -->
																	<button class="btn btn-sm btn-danger delete_row" type="button" style="">
																		<i class="fa fa-minus"></i>
																	</button>
																</td>
																<td>
																	<!-- 支扣代碼 --> <select class="form-control paycodeSelect" 
																	id="n_paycode" name="paycode"></select>
																</td>
																<td>
																	<!-- 收入--> <input class="form-control text-right keypay"
																	type="text" id="n_payamt" name="payamtP" value=""
																	maxlength="10" size="10">
																</td>
																<td>
																	<!-- 支出--> <input class="form-control text-right keypay"
																	type="text" id="n_payamt" name="payamtD" value=""
																	maxlength="10" size="10">
																</td>
																<td>
																	<!-- hidden--> <input class="form-control" type="hidden"
																	id="n_paysort" name="paysort" value="N">
																</td>
																<td><input class="form-control" type="hidden"
																	id="n_payidno" name="payidno" value=""></td>
																<td><input class="form-control" type="hidden"
																	id="n_hid" name="hid" value=""></td>
																<td><input class="form-control" type="hidden"
																	id="n_tranum" name="tranum" value=""></td>
															</tr>
														</tbody>
													</table>
													<!-- 一組結尾 -->
													<button class="btn btn-sm btn-info add_row" type="button" style="">新增項目</button>
												</div>
												<!-- row -->
											</div>
											<!-- <div class="card-body"> -->
										</div>
										<!-- div id="collapseTwo" -->
									</div>
									<!-- card -->									
								</div>
							</form>
						</div>
					</div>
				</div>
				<!-- 新增dialog -->
				<div class="modal fade" id="addPayeDialog" tabindex="-1"
					role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">

					<form id="subtraPayForm">
						<INPUT type="hidden" id="citem" name="item">
						<div class="modal-dialog modal-lg">
							<!-- Modal content-->
							<div class="modal-content">
								<!-- Modal Header -->
								<div class="modal-header">
									<h4 class="modal-title">
										<span id="model_header_title"></span>
									</h4>
									<button type="button" class="close" data-dismiss="modal">&times;</button>
								</div>
								<div class="modal-body">
									<div class="container-fluid">
										<div class="row justify-content-md-center">
											<table id="PATRAPAY"
												class="table table-striped table-bordered"
												style="text-align: center;">
												<thead class="table-primary">
													<tr class="div-green">
														<th class="table-title">身份證</th>
														<th class="table-title">發薪日</th>
														<th class="table-title">支扣代碼</th>
														<th class="table-title">金額</th>
														<th class="table-title">醫院碼</th>
														<th class="table-title">時戳</th>
													</tr>
												</thead>
												<tbody>
													<tr>
														<td><input type="text" id="patrapay_payidno" name="payidno" class="form-control required" value="" size="10" maxlength="10"></input> 
															<input type="hidden" id="patrapay_tranum" name="tranum" class="form-control required" value="" size="10" maxlength="10"></input> 
															<input type="hidden" id="patrapay_paysort" name="paysort" class="form-control required" value="N" size="1" maxlength="1"></input> 
															<input type="hidden" id="patrapay_payauth" name="payauth" size="8"></input> 
															<input type="hidden" id="patrapay_payiftax" name="payiftax" size="1"></input> 
															<input type="hidden" id="patrapay_payflag" name="payflag" size="1"></input>
														</td>
														<td><input type="text" id="patrapay_patradt" class="form-control findkey gclsCalTxt required" value=""size="10" placeholder="請輸入發薪日" maxlength="10"></input></td>
														<td><select id="patrapay_paycode" name="paycode" class="selectpicker findkey required" data-width="fit" data-live-search="true" title="支扣代碼"></select></td>
														<td><input type="text" id="patrapay_payamt" name="payamt" class="form-control required" value="" size="6" placeholder="金額" maxlength="6"></input></td>
														<td><input type="text" id="patrapay_hid" name="hid" class="form-control" value="" size="3" maxlength="3"readOnly></input></td>
														<td><input type="text" id="patrapay_payupdtm"name="payupdtm" class="form-control" value="" size="26"maxlength="26" readOnly></input></td>
													</tr>
												</tbody>
											</table>
										</div>
									</div>
								</div>
								<!-- Modal footer -->
								<div class="modal-footer">
									<button type="button" class="btn btn-primary" id="btn_subTraPayIns"> <i class="fa fa-save"></i>確認儲存</button>
									<button type="button" class="btn btn-primary" id="btn_subTraPayUpd"><i class="fa fa-refresh"></i>更改</button>
									<button type="button" class="btn btn-secondary"data-dismiss="modal"><i class="fa fa-undo"></i>返回</button>
								</div>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>

		<!--篩選-->
		<div class="tab-pane fade" id="byTranFilterMain" role="tabpanel" aria-labelledby="pills-query-tab">
			<div class="container-fluid">
				<div class="row" id="filterItem">
					<div class="col-10">
						<div class="form-inline">
							<div class="input-group">

								<input type="text" id="filter_card" size="4"
									class="form-control-sm text-uppercase" placeholder="卡號">
								<input type="text" id="filter_idno" size="10"
									class="form-control-sm text-uppercase" placeholder="身分證">
								<input type="text" id="filter_namec" size="12"
									class="form-control-sm " placeholder="姓名"> <input
									type="text" id="filter_empno" size="7"
									class="form-control-sm text-uppercase" placeholder="職號">
								<select id="filter_lvefg" class="selectpicker" data-width="fit"
									title="離職">
									<option value=""></option>
									<option value="N">N</option>
									<option value="Y">Y</option>
								</select> <select id="filter_dept" name="filter_dept"
									class="selectpicker PAYDEPT" data-width="fit"
									data-live-search="true" multiple title="單位">
									<option value=""></option>
									<c:forEach var="uitem" items="${alDeptCodeTbl}"
										varStatus="loop">
										<option value="${uitem.padepcod}">${uitem.pacstno}-${uitem.padepnmc}</option>
									</c:forEach>
								</select> <select id="filter_title" name="filter_title"
									class="selectpicker JTCODE" data-width="fit"
									data-live-search="true" multiple title="職稱">
									<option value=""></option>
									<c:forEach var="uitem" items="${alTitleCodeTbl}"
										varStatus="loop">
										<option value="${uitem.paytitlk}">${uitem.patitsub}-${uitem.paytitlk}-${uitem.paytitlc}</option>
									</c:forEach>
								</select> <select id="filter_payrank" class="selectpicker RANKCODE"
									data-width="fit" data-live-search="true" multiple title="階級">
									<option value=""></option>
									<c:forEach var="uitem" items="${alRankCodeTbl}"
										varStatus="loop">
										<option value="${uitem.rankcode}">${uitem.rankcode}-${uitem.rankname}</option>
									</c:forEach>
								</select> 
								<%-- <select id="filter_patradt" class="selectpicker PATRADT"
									data-width="fit" data-live-search="true" multiple title="異動日">
									<option value=""></option>
									<c:forEach var="bitem" items="${alBankDtTbl}" varStatus="loop">
										<option value="${bitem.paybank}">${bitem.paybank}</option>
									</c:forEach>
								</select> --%>
								<input type="text" id="filter_patradt" size="10" class="form-control gclsCalTxt" placeholder="異動日" >
								<select id="filter_patracd" class="selectpicker"
									data-width="fit" multiple title="異動原因">
									<option value=""></option>
									<c:forEach var="bitem" items="${altracdTbl}" varStatus="loop">
										<option value="${bitem.tracode}">${bitem.traname}</option>
									</c:forEach>
								</select> <select id="filter_pacsflag" class="selectpicker"
									data-width="fit" title="工作獎金">
									<option value=""></option>
									<option value="Y">Y</option>
									<option value="N">N</option>
								</select> <select id="filter_expfsp" class="selectpicker"
									data-width="fit" title="額外點數">
									<option value=""></option>
									<option value="Y">Y</option>
									<option value="N">N</option>
								</select> <select id="filter_expfkey1" class="selectpicker"
									data-width="fit" title="額外代碼">
									<c:forEach var="uitem" items="${alexpfTbl}" varStatus="loop">
										<option value="${uitem.expfkey}">${uitem.expfname}</option>
									</c:forEach>
								</select> 
								<%-- <select id="filter_expfkey2" class="selectpicker"
									data-width="fit" title="額外2">
									<c:forEach var="uitem" items="${alexpfTbl}" varStatus="loop">
										<option value="${uitem.expfkey}">${uitem.expfname}</option>
									</c:forEach>
								</select> <select id="filter_expfkey3" class="selectpicker"
									data-width="fit" title="額外3">
									<c:forEach var="uitem" items="${alexpfTbl}" varStatus="loop">
										<option value="${uitem.expfkey}">${uitem.expfname}</option>
									</c:forEach>
								</select>  --%>
								<select id="filter_paret" class="selectpicker" data-width="fit" title="專2">
									<option value=""></option>
									<option value="Y">Y</option>
									<option value="N">N</option>
								</select> 
								<select id="filter_inflg" class="selectpicker" data-width="fit" title="保險補助">
									<option value=""></option>
									<option value="Y">Y</option>
								</select> 
								<input type="text" id="filter_paeffdt_bng" size="10" class="form-control-sm gclsCalTxt" placeholder="生效日_起">
								<input type="text" id="filter_paeffdt_end" size="10" class="form-control-sm gclsCalTxt" placeholder="生效日_訖">
							</div>
						</div>
					</div>
					<div class="col-2">
						<button id="btn_qry_filter_TranPsn" class="btn btn-success" type="button">
							<i class="fa fa-search"></i>查詢
						</button>
						<button id="btn_cln_filter_TranPsn" class="btn btn-warning" type="button">
							<i class="fa fa-eraser"></i>清空
						</button>
					</div>
				</div>
				<div class="row">
					<div class="col border border-dark">
						<div id="TranPsnListDiv_2" class="TranPsnListDiv">
							<!-- pa.paTraPsn篩選清單 -->
							<table id="tblTranPsnFilter"
								class="table table-striped table-hover">

							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
	<!--異動處理-->
	  	<div class="tab-pane fade" id="byTranHandingMain" role="tabpanel" aria-labelledby="pills-handing-tab">
	  		<div class="row p-1 mb-1 bg-secondary">
				<div class="col-md-10">
					<div class="form-inline">
						<div class="input-group">
							<span class="input-group-text">選單</span>
			  	
							<select id="qryType" class="form-control">
								<option value="1:[匯入]晉級調薪:ImpPromoInTra">1[匯入]晉級調薪</option>
								<option value="2:[刪除]晉級調薪:DelPromoInTra">2[刪除]晉級調薪</option>
								<option value="3:[匯入]待遇調整:ImpRemuInTra">3[匯入]待遇調整</option>
								<option value="4:[刪除]待遇調整:DelRemuInTra">4[刪除]待遇調整</option>     					
							</select>
						</div>
					</div>
				</div>
				<div class="col-md-2">
					<button id="btn_handelTra" class="btn btn-info roleQry" type="button" ><i class="fa fa-cog"></i>執行</button>					
				</div>
			</div>
			
	  		<div class="row">
				<div class="col-md-12">
					<div id="explan_1" class="text-danger">執行產生:請選擇[發薪日]</div>
					<div id="explan_2" class="text-danger d-none">回復處理:依[發薪日]刪除</div>
					<div id="explan_3" class="text-danger d-none">1.階級支扣數、職等支扣數要先設定。<br>2.執行產生:[範圍][發薪日][生效日][追補月日],4項欄位必填.</div>
					<div id="explan_4" class="text-danger d-none">回復處理:[範圍][發薪日],2項欄位必填,依條件刪除</div>
				</div>
			</div>
			
			<div class="row">
				<div class="col-md-12">
					<div class="form-inline">
						<div class="input-group">
							<!--晉級調薪 -->
							<div class="input-group" id="divfield_1">	
								<label for="handingA_tradt" class="table-warning">發薪日</label>
								<select id="handingA_tradt" class="form-control" title="發薪日">									
								</select>							
							</div>
							<!--待遇調整 -->
							<div class="input-group d-none" id="divfield_2">
								<label for="handingB_range" class="table-warning">範圍</label>
								<select id="handingB_range" class="form-control w-auto" title="範圍">
									<option value="1:公保正式人員:Remu_1">1:公保正式人員</option>
									<option value="2:勞保正式人員:Remu_2">2:勞保正式人員</option>
									<option value="3:聘用住院醫師、聘實習醫師:Remu_3">3:聘用住院醫師、聘實習醫師</option>
									<option value="4:契約人員_月頭:Remu_4">4:契約人員_月頭</option>
									<option value="5:契約人員_月尾:Remu_5">5:契約人員_月尾</option>
									<option value="6:契約人員_住院醫師:Remu_6">6:契約人員_住院醫師</option>
									<option value="7:契約人員_主治醫師:Remu_7">7:契約人員_主治醫師</option>
									<option value="8:屏榮契約_不含醫師,半日:Remu_8">8:屏榮契約_不含醫師,半日</option>
									<option value="9:聯醫契約_不含半日:Remu_9">9:聯醫契約_不含半日</option>
								</select>
								<label for="handingB_tradt" class="table-warning">發薪日</label>
								<select id="handingB_tradt" class="form-control w-auto" title="發薪日">
									
								</select>	
								<label for="handingB_effdt" id="handingB_effdt_lbl" class="table-warning">生效日</label>
								<input type="text" id="handingB_effdt" class="gclsCalTxt form-control w-auto" maxlength="10" size="10"/>
								<label for="handingB_monno" id="handingB_monno_lbl" class="table-warning">追補月數</label> 
								<input type="text" id="handingB_monno" class="form-control" maxlength="3" size="3" />		
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

</body>
</html>