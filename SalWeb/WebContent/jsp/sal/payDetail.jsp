<!DOCTYPE HTML>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<html>
<head>
<title>發薪明細/檢驗</title>
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

var tabid="";
var objCopy;
var in_data;
var codeTbl;
var oTable;
var paybank;
var salnum;
var cardno;
var idtno;
var namec;
var paydept;
var paycode;
var salNm;
var selEn;
var selPs;
var selCode; 
var hid;
var patradt;
var rowData;
var msg;
var logtmp;
var val;
var tmp;
var jsonString;
$(function(){
	$('#collapseOne').collapse('show');	
	$(".gclsCalTxt").datepicker({ 
		changeMonth: true,
      	changeYear: true,
      	dateFormat:"yy-mm-dd"});
      	
	$(".selectpicker").selectpicker({
		noneSelectedText : '請選擇'//預設顯示內容
    });
    //初始化重新整理資料
	$(window).on('load', function() {
		$('.selectpicker').selectpicker('refresh');
	});
	
	//tab設定
	$('#pills-tab a').on('click', function (e) {
	  e.preventDefault();
	  $(this).tab('show');
	  tabid=$(this).attr('id');
	  console.log(tabid);
	  var logtmp;
	  //檔案產製
	  if (tabid=="pills-fileProd-tab"){
	  	tmp = "";	
		if (pahid != "1A0"){
			tmp = pahid;
		}
	  	logtmp="\\\\ap_pd1.vghks.gov.tw\\Apps_Exe\\Sal\\pay"+tmp +".txt";	
		$("#explanProd_1").html(logtmp);
	  }
	  
	});
	//檔案產製代碼切換
	$("#file_cond").change(function(){
		$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
		selCode=$("#file_cond option:selected").val().split(':')[0];
		if (typeof(selCode)=="undefined" || '' == selCode){
			selCode="1";
		}
		tmp = "";	
		if (pahid != "1A0"){
			tmp = pahid;
		}
		
		if (selCode == "1"){	//主計室發放
			codeTbl="tblBASPayList";
			logtmp="\\\\ap_pd1.vghks.gov.tw\\Apps_Exe\\Sal\\pay"+tmp +".txt";		  	
	  	}else if (selCode == "2"){	//主計室扣款
	  		codeTbl="tblBASDeductList";
	  		logtmp="\\\\ap_pd1.vghks.gov.tw\\Apps_Exe\\Sal\\DEDUCT"+tmp +".txt";		  	
		}  	
	  	$("#explanProd_1").html(logtmp);
	  	$('#'+codeTbl).show();		
		$("#btn_qryCode").trigger( "click" );	
		
	});
	//非1A0刪除比對中Value='2'的Option
	if (pahid == "2A0" || pahid == "3A0"){
		$("#bank_cond option[value='2']").remove(); 
	}	
	//2A0 銀行媒體傳輸 格式 901 薪轉附言
	if (pahid == "2A0"){
		$("#bank_ps").removeClass("d-none");
	}
	//代碼選單
	$("#bank_cond").change(function(){
		$("#bankUpload,#bank_ps").addClass("d-none");	//隱藏
		selCode=(typeof($("#bank_cond option:selected").val().split(':')[0]) == "undefined" )? "1":$("#bank_cond option:selected").val().split(':')[0];
  		console.log(selCode);
		
  		if (pahid == "1A0" ){			//1A0 比對,檔案上傳
			if (selCode == 2){
				$("#bankUpload").removeClass("d-none");
				$("#bank_item").removeClass("d-none");
			}	
		}	 
  	});
  		
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
				//階級	
			 	if (data.alRankCodeTbl[0]!=null){
					select = $('#qry_payrank');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alRankCodeTbl, function(idx) {
						options = "<option value='" + data.alRankCodeTbl[idx].rankcode + "'>" + data.alRankCodeTbl[idx].rankcode+"-"+data.alRankCodeTbl[idx].rankname ;
						select.append(options);
					});
					select.selectpicker('refresh');			 	
			 	}	
				//職稱
				if (data.alTitleCodeTbl[0]!=null){
					select = $('#qryTitle');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alTitleCodeTbl, function(idx) {
						options = "<option value='" + data.alTitleCodeTbl[idx].paytitlk + "'>" + data.alTitleCodeTbl[idx].patitsub+"-"+data.alTitleCodeTbl[idx].paytitlk+"-"+data.alTitleCodeTbl[idx].paytitlc ;
						select.append(options);
					});
					select.selectpicker('refresh');
				}
				//單位
				if (data.alDeptCodeTbl[0]!=null){
					select = $('#qryDept,#qry_itmPay_Dept');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alDeptCodeTbl, function(idx) {
						options = "<option value='" + data.alDeptCodeTbl[idx].padepcod + "'>" + data.alDeptCodeTbl[idx].pacstno+"-"+data.alDeptCodeTbl[idx].padepnmc ;
						select.append(options);
					});
					select.selectpicker('refresh');
				}
				
				//餉單編號
				/* if (data.alBankDtTbl[0]!=null){
					select = $('#qry_padatpsn_salnum,#qry_check_salnum');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alBankDtTbl, function(idx) {
						options = "<option value='" + data.alBankDtTbl[idx].salnum + "'>" + data.alBankDtTbl[idx].salnum ;
						select.append(options);
					});
					select.selectpicker('refresh');
					
					select = $('#qry_padatpsn_paybank,#qry_check_paybank');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alBankDtTbl, function(idx) {
						options = "<option value='" + data.alBankDtTbl[idx].paybank.dts + "'>" + data.alBankDtTbl[idx].paybank.dts ;
						select.append(options);
					});
					select.selectpicker('refresh');
				} */
				//餉單編號(2個月內)
				if (data.alBankDtTbl_2M[0]!=null){
					select = $('#qry_set_datyr,#prod_SalNum,#prt_SalNum,#print_SalNum,#file_SalNum');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alBankDtTbl_2M, function(idx) {
						options = "<option value='" + data.alBankDtTbl_2M[idx].salnum +":"+ data.alBankDtTbl_2M[idx].paybank.dts +"'>" + data.alBankDtTbl_2M[idx].paybank.dts+" "+data.alBankDtTbl_2M[idx].salnum ;
						select.append(options);
					});
					
				}
				
				/* 健保級距 */
 				if (data.alDpinTbl[0]!=null){
					select = $('#qry_insuamt');
					select.empty();
					options = "<option value='0'>0";
					select.append(options);
					$.each(data.alDpinTbl, function(idx) {
						options = "<option value='" + data.alDpinTbl[idx].dpinhigh + "'>" + data.alDpinTbl[idx].dpinhigh ;
						select.append(options);
					});
					select.selectpicker('refresh');			 	
			 	}
			 	//支扣代碼
				if (data.alpayitmTbl[0]!=null){
					select = $('#qry_itmPay_Pay');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alpayitmTbl, function(idx) {
						options = "<option value='" + data.alpayitmTbl[idx].paycode + "'>" + data.alpayitmTbl[idx].paycode+" "+data.alpayitmTbl[idx].pacodnmc ;
						select.append(options);
					});
					select.selectpicker('refresh');
				}
			}  
		});
	};
	/*預先載入代碼檔 */
  	$.getLoadCodeTbl('payDetail','Y');
  	 	
	//編輯欄位檢查
  	$.checkFields = function(btn){
  		msg = '';
  		var isChecked=0;
  		if (btn== 'btn_qrySalNum'){	//餉單編號設定_查詢
  			if (typeof($("#prod_selCode option:selected").val())=='undefined'){
  				msg = '請勾選[性質]項目!!';
  			}
  			if ($("#qry_set_salnum").val()==''){
  				msg += '[餉單編號]請勿空白,請填>=0 !!';
  			}
  			if ($("#qry_set_patradt").val()==''){
  				msg += '[發薪日]請勿空白 !!';
  			}
  		}
  		else if (btn== 'btn_printPrt'){	//報表列印
  			if($("#print_SalNum").val()=='')
  				msg = '報表列印--[餉單編號]請勿空白';
			if (typeof($("#qry_prt_item option:selected").val()) == 'undefined' ){
  				msg+=' 請勾選[報表列印]項目!!';  		
			}
  		}
  		else if (btn== 'btn_prodPrtSal'){	//報表產生_執行
  			if($("#prt_SalNum").val()=='')
  				msg = '報表產生_執行--[餉單編號]請勿空白';
			//取得現在時間
			var now = new Date();
			var month = now.getMonth() + 1;
			if(month < 10)
				month = '0'+month;
			var day = now.getDate();
			if(day < 10)
				day = '0' + day;
 			var today = now.getFullYear() + '-' + month + '-' + day;
 			patradt = $("#prt_SalNum").val().split(':')[1];
  			/*if (patradt < today) {
  				msg+=' 請勾選 大於 今日!!';
  			}*/
  			if (typeof($("#qry_prt_cond option:selected").val()) == 'undefined' ){
  				msg+=' 請勾選[條件]項目!!';  		
			}
  		}
  		else if (btn== 'btn_prodBank'){	//銀行作業_執行 1媒體傳輸 2比對(高榮)
  			if($("#prt_SalNum").val()=='')
  				msg = '銀行作業_執行--[餉單編號]請勿空白';
			if (typeof($("#qry_prt_cond option:selected").val()) == 'undefined' ){
  				msg+=' 請勾選[條件]項目!!';  		
			}
			if (pahid == '1A0'){
				selCode=$("#bank_cond option:selected").val();
				//比對
				if (selCode=='2'){
					var file = $("#fileInput").val();			
					if (file.length == 0) {
						msg+='請確認是否有選檔案';				
					}
				}
			}
  		}
  		else if (btn== 'btn_prodPayroll'){	//發薪明細_執行
  			if($("#prod_SalNum").val()=='')
  				msg = '[餉單編號]請勿空白';
			//取得現在時間
			var now = new Date();
			var month = now.getMonth() + 1;
			if(month < 10)
				month = '0'+month;
			var day = now.getDate();
			if(day < 10)
				day = '0' + day;
 			var today = now.getFullYear() + '-' + month + '-' + day;
 			patradt = $("#prod_SalNum").val().split(':')[1];
  			if (patradt < today) {
  				msg+=' 請勾選 大於 今日!!';
  			} 
  			
  			if (typeof($("#qry_prod_cond option:selected").val()) == 'undefined' ){
  				msg+=' 請勾選[條件]項目!!';  		
			}
  		}
  		else if (btn== 'btn_prodSalNum'){	//餉單編號設定_執行
  			if( $("#prod_set_SalNum").val()==''){
  				msg = '[新編號值]請勿空白';
  			}
  			
  			val=$("#qry_set_salnum").val();	
  			if ( !isNaN(parseFloat(val)) ){
  				//原餉單編號>0,要判斷勾選筆數
  				if  ( parseInt(val) > 0 && $("#qry_set_patradt").val()!=''){
	  				isChecked=$('input[name="chkupdA"]:checked').length;
					if (isChecked==0) 
						msg+=' 請至少勾選1個';
				}			  		
			}else{
				msg = '[餉單編號]為數字.';
			}
  		}
  		else if (btn== 'btn_qryDatPsn'){	//發薪明細_查詢
  			//($("#qry_padatpsn_salnum option:selected").val()=='' || typeof($("#qry_padatpsn_salnum option:selected").val())=='undefined' )&& 
  			//  ($("#qry_padatpsn_paybank option:selected").val()=='' || typeof($("#qry_padatpsn_paybank option:selected").val())=='undefined' )&&
  			if($("#qryCardno").val()==''  && $("#qryIdtno").val()=='' && $("#qryNamec").val()=='' && 
  			   $("#qry_padatpsn_salnum").val()==''   && $("#qry_padatpsn_paybank").val()==''  && 
  			  ($("#qry_payrank option:selected").val()=='' || typeof($("#qry_payrank option:selected").val())=='undefined') && 
  			  ($("#qryDept option:selected").val()=='' || typeof($("#qryDept option:selected").val())=='undefined')  && 
  			  ($("#qryTitle option:selected").val()=='' || typeof($("#qryTitle option:selected").val())=='undefined') && 
  			  ($("#qry_insuamt option:selected").val()=='' || typeof($("#qry_insuamt option:selected").val())=='undefined') )
  			  {
  				msg = '請輸入條件值!!';
  			}
  		}
  		else if (btn== 'btn_qryCheck'){	//檢驗_查詢
//   			if( ( $("#qry_check_salnum option:selected").val()=='' || typeof($("#qry_check_salnum option:selected").val())=='undefined' ) && 
//   			    ( $("#qry_check_paybank option:selected").val()=='' ||typeof($("#qry_check_paybank option:selected").val())=='undefined' ) ){
			if (  $("#qry_check_salnum").val()=='' && $("#qry_check_paybank").val()=='' ){
 				msg = '請輸入條件值!!';
 			}	
 		}
		else if (btn== 'btn_qryProdPrt'){	//報表作業_查詢
			if(($("#prt_SalNum").val()==''  ) ){
  				msg = '請輸入條件值!!';
  			}
  		}else if (btn== 'btn_qryItmPay'){	//支扣明細查詢
  			if($("#qry_itmPay_Pay").val()==''){
  				msg = '請輸入[支扣代碼]條件值!!';
  			}
  			if($("#qry_itmPay_Cardno").val()==''  && $("#qry_itmPay_Idtno").val()=='' && $("#qry_itmPay_Namec").val()=='' && 
  			  ($("#qry_itmPay_paybankBeg").val()=='' || typeof($("#qry_itmPay_paybankBeg").val())=='undefined' )&& 
  			  ($("#qry_itmPay_paybankEnd").val()=='' || typeof($("#qry_itmPay_paybankEnd").val())=='undefined' )&& 
  			  ($("#qry_itmPay_Dept").val()=='' || typeof($("#qry_itmPay_Dept option:selected").val())=='undefined') )
  			  {
  				msg += '請輸入條件值!!';
  			}
  		}
  		else if (btn== 'btn_qryFile'){	//檔案產製
  			if($("#file_cond").val()=='' || typeof($("#file_cond").val())=='undefined' ){
  				msg = '請輸入項目!!';
  			}
  			if($("#file_SalNum").val()=='' || typeof($("#file_SalNum").val())=='undefined')
  			    msg += '[餉單編號_發薪日]請勿空白!!';
  		}
  		return msg;	
	};
	//將下拉式選單,多選,存成字串,以[,]分開
	$.getSelectVals = function(item){
		var selectedValues = "";
		$("#"+item+" option:selected").each(function () {
			if (selectedValues!="") selectedValues=selectedValues+",";
		    selectedValues=selectedValues + $(this).val();
		});
		return selectedValues;
	};	
	//jQuery按紐圖示
	$(".btn").button().click(
		function(event) {event.preventDefault();}
	);
	$(".btn").click(function(){
		var id = $(this).attr('id');
		
		if(id == 'btn_clnDatPsn'){
			cleanTable("byPayDetailMain");
			$('.selectpicker').selectpicker('val', '');
			$('.selectpicker').selectpicker('refresh');
			$("#TblDatPsnList").dataTable().fnClearTable();
			return false;
		}
		else if(id == 'btn_clnCheck'){
			cleanTable("byCheckMain");
			$('.selectpicker').selectpicker('val', '');
			$('.selectpicker').selectpicker('refresh');
			$("#TblCheckPayList").dataTable().fnClearTable();
			return false;
		}
		else if(id == 'btn_clnSalNum'){
			cleanTable("bySetSalnumMain");
			$('.selectpicker').selectpicker('val', '');
			$('.selectpicker').selectpicker('refresh');
			$("#tblSalNumList").dataTable().fnClearTable();
			$("#salnum_select_all").prop('checked', false);
			return false;
		}
		else if(id == 'btn_clnProdDat'){
			cleanTable("byProdDatMain");
			$('.selectpicker').selectpicker('val', '');
			$('.selectpicker').selectpicker('refresh');
			return false;
		}
		else if(id == 'btn_clnProdPrt'){
			cleanTable("byPordPrtMain");
			$('.selectpicker').selectpicker('val', '');
			$('.selectpicker').selectpicker('refresh');
			$("#TblRepDtlList").dataTable().fnClearTable();
			return false;
		}
		else if(id == 'btn_clnItmPay'){
			cleanTable("byItmPayMain");
			$('.selectpicker').selectpicker('val', '');
			$('.selectpicker').selectpicker('refresh');
			$("#tblItmPayList").dataTable().fnClearTable();
			$('#' + codeTbl ).dataTable().fnClearTable();
			return false;
		}
		/* if(id == 'btn_clnDatPsn' || id == 'btn_clnCheck' || id =='btn_clnSalNum' || id =='btn_clnProdDat' || id =='btn_clnProdPrt'){
			$(":text,textarea").val("");
			$(":checkbox,:radio").prop("checked", false);
			console.log("ss:"+$('#tfoot_taxpay').html());
			//所有select預設選擇第一項			 
			$('#qryCheckItem select').each(function() {
				$(this).prop("selectedIndex", 0);
		    });
		    //所有select預設選擇第一項
		    $('#bySetSalnumMain select').each(function() {
				$(this).prop("selectedIndex", 0);
		    });
		    
		    $('.selectpicker').selectpicker('deselectAll');
		    $('.selectpicker').selectpicker('refresh');
		    
		    		    
		    $("#TblDatPsnList").dataTable().fnClearTable();
			$("#TblDatPaySum").dataTable().fnClearTable();
			$("#TblCheckPayList").dataTable().fnClearTable();
			$("#tblSalNumList").dataTable().fnClearTable();
			$("#TblRepDtlList").dataTable().fnClearTable();
			
			$("#salnum_select_all").prop('checked', false);
			
			return false;
		} */
		else if(id == 'btn_qryDatPsn'){		//發薪明細查詢
			msg = $.checkFields(id);
			if(msg == ''){
				cardno=$("#qryCardno").val().toUpperCase();
				idtno=$("#qryIdtno").val().toUpperCase();
				namec=$("#qryNamec").val();
				salnum=$("#qry_padatpsn_salnum").val();
				paybank=$("#qry_padatpsn_paybank").val();
				//salnum=$.getSelectVals("qry_padatpsn_salnum");				
				//paybank=$.getSelectVals("qry_padatpsn_paybank");
				var payrank=$.getSelectVals("qry_payrank");
				var paytitle=$.getSelectVals("qryTitle");
				paydept=$.getSelectVals("qryDept");
				var payinsuamt=$.getSelectVals("qry_insuamt");
				
 				console.log("卡號:"+cardno+" 身分證:"+idtno+" 姓名:"+namec+" salnum:"+salnum+" paybank:"+paybank+" payrank:"+payrank+" paytitle:"+paytitle+" paydept:"+paydept+" insuamt:"+payinsuamt);
 				
				in_data="";
				in_data={
		        		cardno     :cardno,
		        		idtno      :idtno,
		        		namec      :namec,
		        		salnum     :salnum,
		        		paybank    :paybank,
		        		payrank    :payrank,
		        		paytitle   :paytitle,
		        		paydept    :paydept,
		        		insuamt    :payinsuamt
		        	};
				
				//return false;
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'getDatPsnList',
						in_data :JSON.stringify(in_data),
						hid     : pahid,
						userid  : paUserid,
					},
					type:"POST",	//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						//更新查詢清單
						$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
						$('#TblDatPsnList').show();
						oTable = $('#TblDatPsnList').dataTable(objCopyA);
						
						oTable.fnClearTable(0);		//清空資料
								
						if (data.datPsnList!=null && data.datPsnList.length>0){	
							oTable.fnAddData(data.datPsnList);//重新給資料
						}
						oTable.fnDraw();						
						
						//移至第1筆
						//$("#TblDatPsnList tbody tr:first").find("a").eq(0).trigger("click");
						
						var table = $('#TblDatPsnList').DataTable(); 
						// Hide columns
						if (pahid == "3A0")						
							table.columns( [16,17,18,19,20,21] ).visible( false );
						else if (pahid == "1A0" || pahid == "2A0")
							table.columns( [16,17,18,19,20,21,22,23] ).visible( true );
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
		else if(id == 'btn_qryCheck'){		//檢驗查詢
			msg = $.checkFields(id);
			if(msg == ''){
				salnum=$("#qry_check_salnum").val();
				paybank=$("#qry_check_paybank").val();
				
				console.log("檢驗查詢 salnum:"+salnum+" paybank:"+paybank+" hid:"+pahid);

				in_data="";
				in_data={
						salnum     :salnum,
						paybank    :paybank
					};				
// 				return false;				
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method    : 'getCheckPayList',
						in_data :JSON.stringify(in_data),
						hid     : pahid,
						userid  : paUserid,
					},
					type:"POST",	//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						//更新查詢清單
						$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
						$('#TblCheckPayList').show();
						oTable = $('#TblCheckPayList').dataTable(objCopyC);
						
						oTable.fnClearTable(0);		//清空資料
								
						if (data.checkPayList!=null && data.checkPayList.length>0){	
							oTable.fnAddData(data.checkPayList);//重新給資料
						}
						oTable.fnDraw();
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
		else if(id == 'btn_qrySalNum'){		//餉單編號設定_查詢
			msg = $.checkFields(id);
			if(msg == ''){
				$("#salnum_select_all").removeClass("d-none");
				selCode=$("#prod_selCode option:selected").val().split(':')[0];
				selEn=$("#prod_selCode option:selected").val().split(':')[2];
				selNm=$("#prod_selCode option:selected").val().split(':')[1];
				//salnum =$("#qry_set_datyr option:selected").val() == "" ? "0":$("#qry_set_datyr option:selected").val().split(':')[0];
				salnum =$("#qry_set_salnum").val() ;
				patradt =$("#qry_set_patradt").val() ;
				cardno=$("#qry_set_Cardno").val().toUpperCase();
				in_data="";
				in_data={
						selCode :selCode,
						selNm   :selNm,
						selEn   :selEn,
						salnum  :salnum,
						cardno  :cardno,
						patradt :patradt
					};
				console.log("餉單編號設定_查詢  hid:"+pahid+" in_data:"+JSON.stringify(in_data));
 				//return false;				
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method   : 'getSalnumList',
						in_data  : JSON.stringify(in_data),
						hid     : pahid,
						userid  : paUserid
					},
					type:"POST",	//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						//更新查詢清單
						$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
						$('#tblSalNumList').show();
						oTable = $('#tblSalNumList').dataTable(objCopyE);
						
						oTable.fnClearTable(0);		//清空資料
								
						if (data.salnumList!=null && data.salnumList.length>0){	
							oTable.fnAddData(data.salnumList);//重新給資料
						}
						oTable.fnDraw();
						
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
		else if(id == 'btn_prodSalNum'){		//餉單編號設定_執行
			msg = $.checkFields(id);
			if(msg == ''){
				selCode=$("#prod_selCode option:selected").val().split(':')[0];
				selNm=$("#prod_selCode option:selected").val().split(':')[1];
				selEn=$("#prod_selCode option:selected").val().split(':')[2];
				var salnum_n =$("#prod_set_SalNum").val();
// 				var salnum_o =$("#qry_set_datyr option:selected").val() == "" ? "0":$("#qry_set_datyr option:selected").val().split(':')[0];
// 				var patradt_o=$("#qry_set_datyr option:selected").val() == "" ? "":$("#qry_set_datyr option:selected").val().split(':')[1];
 				var salnum_o =$("#qry_set_salnum").val() == "" ? "0":$("#qry_set_salnum").val();
				var patradt_o=$("#qry_set_patradt").val() ;
				jsonString = "";
				if ( salnum_o !='0' ) {
					var itmTbl="tblSalNumList";
					jsonString = $.getDataInfo(itmTbl,selCode); //取得目前的資料
				}
				in_data="";
				in_data={
						selCode :selCode,
						selNm   :selNm,
						selEn   :selEn,
						salnum_n  :salnum_n,
						salnum_o  :salnum_o,
						patradt_o :patradt_o
					   };
				console.log("餉單編號設定_執行  hid:"+pahid+" indata:"+JSON.stringify(in_data));	   
				//return false;				
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'handelPayroll',
						selNm   : selNm,						
						in_data : JSON.stringify(in_data),
						tabJson : jsonString,
						hid     : pahid,
						userid  : paUserid
					},
					type:"POST",	//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						if (data.status!=null||typeof(data.status)!="undefined"){	//show msg,dialog不用關閉
							alert(data.msg);
							$("#btn_qrySalNum").trigger( "click" ); 
						}else{	
							if (data.msg!=null){
								alert(data.msg);
							}				
						}
						
					},
					error: function(xhr){					
						alert("處理_"+selNm+"失敗!!!程式發生錯誤!!!!\nerror code:"+ xhr.status);		
					}
				});
			}	
			else{
				alert(msg);
				return false;
			}	 
		}		
		else if(id == 'btn_prodPayroll'){		//[產生]發薪明細_執行
			msg = $.checkFields(id);
			if(msg == ''){
				showLoading(); //顯示loading
				selCode=$("#qry_prod_cond option:selected").val().split(':')[0];
				selNm=$("#qry_prod_cond option:selected").val().split(':')[1];
				selEn=$("#qry_prod_cond option:selected").val().split(':')[2];
				salnum =$("#prod_SalNum").val().split(':')[0];
				patradt =$("#prod_SalNum").val().split(':')[1];
								
				console.log("產生發薪明細_執行 selCode:"+selCode+" selNm:"+selNm+" salnum:"+salnum+" patradt:"+patradt+" hid:"+pahid);
				jsonString = "";
				in_data="";
				in_data={
						selCode :selCode,
						selNm   :selNm,
						selEn   :selEn,
						salnum  :salnum,
						patradt :patradt
					   };
				//return false;				
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'handelPayroll',
						selNm   : selNm,						
						in_data : JSON.stringify(in_data),
						tabJson : jsonString,
						hid     : pahid,
						userid  : paUserid
					},
					type:"POST",	//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						if (data.status!=null||typeof(data.status)!="undefined"){	//show msg,dialog不用關閉
							alert(data.msg);
						}else{	
							if (data.msg!=null){
								alert(data.msg);
							}				
						}
						
					},
					error: function(xhr){					
						alert("處理_"+selNm+"失敗!!!程式發生錯誤!!!!\nerror code:"+ xhr.status);		
					},
					complete: function(data) {
							hideLoading(); //隱藏loading
						}
				});
			}	
			else{
				alert(msg);
				return false;
			}	 
		}
		else if(id == 'btn_prodPrtSal'){		//[產生]報表檔_執行
			msg = $.checkFields(id);
			if(msg == ''){
				showLoading(); //顯示loading
				selCode=$("#qry_prt_cond option:selected").val().split(':')[0];
				selNm=$("#qry_prt_cond option:selected").val().split(':')[1];
				selEn=$("#qry_prt_cond option:selected").val().split(':')[2];
				salnum =$("#prt_SalNum").val().split(':')[0];
				patradt =$("#prt_SalNum").val().split(':')[1];
								
				console.log("[產生]報表檔_執行  selCode:"+selCode+" selNm:"+selNm+" salnum:"+salnum+" patradt:"+patradt+" hid:"+pahid);

				in_data="";
				in_data={
						selCode :selCode,
						selNm   :selNm,
						selEn   :selEn,
						salnum  :salnum,
						patradt :patradt
					   };
				//return false;				
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'handelPrtSal',
						in_data : JSON.stringify(in_data),
						hid     : pahid,
						userid  : paUserid,
						fun     : 'handelPrt'
					},
					type:"POST",	//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						if (data.status!=null||typeof(data.status)!="undefined"){	//show msg,dialog不用關閉
							alert(data.msg);
						}else{	
							if (data.msg!=null){
								alert(data.msg);
							}				
						}
						
					},
					error: function(xhr){					
						alert("處理_"+selNm+"失敗!!!程式發生錯誤!!!!\nerror code:"+ xhr.status);		
					},
					complete: function(data) {
							hideLoading(); //隱藏loading
						}
				});
			}	
			else{
				alert(msg);
				return false;
			}	 
		}
		else if(id == 'btn_prodBank'){		//[產生]銀行作業_執行
			msg = $.checkFields(id);
			if(msg == ''){
				showLoading(); //顯示loading
				selCode=$("#bank_cond option:selected").val().split(':')[0];
				patradt =$("#prt_SalNum").val().split(':')[1];
				salnum =$("#prt_SalNum").val().split(':')[0];
				//媒體傳輸
				if (selCode == 1){
					selNm=$("#bank_cond option:selected").val().split(':')[1];
					selEn=$("#bank_cond option:selected").val().split(':')[2];
					salnum =$("#prt_SalNum").val().split(':')[0];
					if (typeof($("#qry_prt_cond option:selected").val()) != 'undefined' ){
						selPs=$("#bank_ps").val();
					}					
					console.log("[產生]銀行作業_執行  selCode:"+selCode+" selEn:"+selEn+" selNm:"+selNm+" salnum:"+salnum+" patradt:"+patradt+" hid:"+pahid+" selPs:"+selPs );
	
					in_data="";
					in_data={
							selCode :selCode,
							selNm   :selNm,
							selEn   :selEn,
							salnum  :salnum,
							patradt :patradt,
							selPs   :selPs
						   };
					//return false;				
					$.ajax({
						url:"${pageContext.request.contextPath}/salController.do",
						data:{
							method  : 'handelPrtSal',
							in_data : JSON.stringify(in_data),
							hid     : pahid,
							userid  : paUserid,
							fun     : 'bankMedia'
						},
						type:"POST",	//type:POST, GET(default:GET)
						dataType:"json",	//dataType: text, xml, html, script, json, jsonp
						contentType: "application/x-www-form-urlencoded;charset=utf-8",  
						success:function(data) {
							if (data.status!=null||typeof(data.status)!="undefined"){	//show msg,dialog不用關閉
								alert(data.msg);
							}else{	
								if (data.msg!=null){
									alert(data.msg);
								}				
							}
							
						},
						error: function(xhr){					
							alert("處理_"+selNm+"失敗!!!程式發生錯誤!!!!\nerror code:"+ xhr.status);		
						},
						complete: function(data) {
								hideLoading(); //隱藏loading
							}
					});
				}
				//比對 1:薪資:Bank_PayC
				else if (selCode == 2){
					selEn=$("#bank_item option:selected").val().split(':')[2];
					url="${pageContext.request.contextPath}/salController.do";
		        	var param = '?method=uploadFile' +
								'&selEn=' + selEn +
								'&patradt=' + patradt +
								'&salnum=' + salnum +
								'&userid=' + userid +
								'&hid=' + pahid;
		        
			        console.log(" url+param:"+url+param);
		        
					$("#uploadfileForm").ajaxSubmit({
						type: "post", //提交方式 
						dataType:"json", //資料型別 
						url: url + param, //請求url 
						success: function (data) { //提交成功的回撥函式 
							alert(data.msg);							
						},
						error: function(xhr){
							alert("檔案上傳過程中，程式發生錯誤!!!\nerror code:"+ xhr.status);							
						},
						complete: function(data) {
							hideLoading(); //隱藏loading
						}
					});
				}
			}	
			else{
				alert(msg);
				return false;
			}	 
		}
		else if(id == 'btn_qryProdPrt'){		//報表檔查詢
			msg = $.checkFields(id);
			if(msg == ''){
				selCode=$("#qry_prt_cond option:selected").val().split(':')[0];
				selNm=$("#qry_prt_cond option:selected").val().split(':')[1];
				selEn=$("#qry_prt_cond option:selected").val().split(':')[2];
				salnum =$("#prt_SalNum").val().split(':')[0];
				patradt =$("#prt_SalNum").val().split(':')[1];

				in_data="";
				in_data={
						selCode :selCode,
						selNm   :selNm,
						selEn   :selEn,
						salnum  :salnum,
						patradt :patradt
					   };
		        	
				console.log("報表檔查詢: selCode:"+selCode+" selNm:"+selNm+" salnum:"+salnum+" patradt:"+patradt+" hid:"+pahid);
// 				return false;				
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'handelPrtSal',
						selNm   : selNm,						
						in_data : JSON.stringify(in_data),
						hid     : pahid,
						userid  : paUserid,
						fun     : 'inqPrt'
					},
					type:"POST",	//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						 if (data.status!=null||typeof(data.status)!="undefined"){	//show msg,dialog不用關閉
							alert(data.msg);
						}else{ 	
							if (data.msg!=null && data.msg!=""){
								alert(data.msg);
							}else{
								//更新查詢清單
								$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
								$('#tblBatPayList').show();
								oTable = $('#TblRepDtlList').dataTable(objCopyF);
								
								oTable.fnClearTable(0);		//清空資料
										
								if (data.prtDtlList!=null && data.prtDtlList.length>0){	
									oTable.fnAddData(data.prtDtlList);//重新給資料
								}
								oTable.fnDraw();
							}				
						}
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
		else if(id == 'btn_printPrt'){		//報表檔產生 PDF
			msg = $.checkFields(id);
			if(msg == ''){
				showLoading(); //顯示loading
				selCode=$("#qry_prt_item option:selected").val().split(':')[0];
				selNm=$("#qry_prt_item option:selected").val().split(':')[1];
				selEn=$("#qry_prt_item option:selected").val().split(':')[2];
				salnum =$("#print_SalNum").val().split(':')[0];
				patradt =$("#print_SalNum").val().split(':')[1];

				in_data="";
				in_data={
						selCode :selCode,
						selNm   :selNm,
						selEn   :selEn,
						salnum  :salnum,
						patradt :patradt
					   };
		        	
				console.log("報表檔PDF: selCode:"+selCode+" selNm:"+selNm+" salnum:"+salnum+" patradt:"+patradt+" hid:"+pahid);
 				//return false;				
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'handelPrtSal',
						in_data : JSON.stringify(in_data),
						hid     : pahid,
						userid  : paUserid,
						fun     : 'printPrt'
					},
					type:"POST",	//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						if (data.status!=null||typeof(data.status)!="undefined"){	//show msg
							alert(data.msg);
						}else{	
							if (data.msg!=null){
								alert(data.msg);
							}				
						}
						hideLoading(); //隱藏loading
					},
					error: function(xhr){					
						alert("查詢失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
					},
					complete: function(data) {
							hideLoading(); //隱藏loading
						}
				});
			}
			else{
				alert(msg);
				return false;
			}
		}
		else if(id == 'btn_qryItmPay'){		//支扣明細查詢
			msg = $.checkFields(id);
			if(msg == ''){
				var pabankdt_beg =$("#qry_itmPay_paybankBeg").val() ;
				var pabankdt_end =$("#qry_itmPay_paybankEnd").val() ;
				paydept=$.getSelectVals("qry_itmPay_Dept");
				paycode =$("#qry_itmPay_Pay").val() ;
				cardno=$("#qry_itmPay_Cardno").val().toUpperCase();
				idtno=$("#qry_itmPay_Idtno").val().toUpperCase();
				namec=$("#qry_itmPay_Namec").val();
				console.log("支扣明細查詢  卡號:"+cardno+" 身分證:"+idtno+" 姓名:"+namec+" paybank:"+pabankdt_beg+"-"+pabankdt_end+" paycode:"+paycode+" hid:"+pahid);

				in_data="";
				in_data={
						pabankdt_beg : pabankdt_beg,
						pabankdt_end : pabankdt_end,
						paydept      : paydept,
						cardno       : cardno,
						idtno        : idtno,
						namec        : namec,
						paycode      : paycode   
					};
				console.log("in_data:"+JSON.stringify(in_data));
 				//return false;				
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'getItmPayList',
						in_data : JSON.stringify(in_data),
						hid     : pahid,
						userid  : paUserid
					},
					type:"POST",	//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						//更新查詢清單
						$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
						$('#tblItmPayList').show();
						oTable = $('#tblItmPayList').dataTable(objCopyG);
						
						oTable.fnClearTable(0);		//清空資料
								
						if (data.itmPayList!=null && data.itmPayList.length>0){	
							oTable.fnAddData(data.itmPayList);//重新給資料
						}
						oTable.fnDraw();
						
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
		else if(id == 'btn_qryFile'){		//檔案產製
			msg = $.checkFields(id);
			if(msg == ''){
				showLoading(); //顯示loading	
				selCode = $("#file_cond option:selected").val().split(':')[0];
				selNm   = $("#file_cond option:selected").val().split(':')[1];
				selEn   = $("#file_cond option:selected").val().split(':')[2];
				salnum  = $("#file_SalNum").val().split(':')[0];
				patradt = $("#file_SalNum").val().split(':')[1];
				console.log("檔案產製: selCode:"+selCode+" selNm:"+selNm+" salnum:"+salnum+" patradt:"+patradt+" hid:"+pahid);
				if (typeof(codeTbl)=="undefined" || '' == codeTbl){
					if (selCode == "1"){	//主計室發放
						codeTbl="tblBASPayList";
				  	}else if (selCode == "2"){	//主計室扣款
				  		codeTbl="tblBASDeductList";
					}
				}	 
				in_data="";
				in_data={
						selCode : selCode,
						selNm   : selNm,
						selEn   : selEn,
						salnum  : salnum,
						patradt : patradt   
					};
				console.log("in_data:"+JSON.stringify(in_data));
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'handelPrtFile',
						in_data : JSON.stringify(in_data),
						hid     : pahid,
						userid  : paUserid
					},
					type:"POST",	//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						//更新查詢清單
						$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
						$('#'+codeTbl).show();
						if (selCode == "1"){	//主計室發放
							oTable = $('#' + codeTbl ).dataTable(objCopyH);
					  	}
 					  	else if (selCode == "2"){	//主計室扣款
 					  		oTable = $('#' + codeTbl ).dataTable(objCopyI);
 						}
						else if(selCode == "3"){	//績效組_醫師
 							oTable = $('#' + codeTbl ).dataTable(objCopyJ);
 					  	}
//						else if (selCode == "4"){	//績效組_員工
// 					  		oTable = $('#' + codeTbl ).dataTable(objCopyK);
// 						}
						
						oTable.fnClearTable(0);		//清空資料
								
						if (data.fileData!=null && data.fileData.length>0){	
							oTable.fnAddData(data.fileData);//重新給資料
						}
						oTable.fnDraw();
						
					},
					error: function(xhr){					
						alert("查詢失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
					},
					complete: function(data) {
						hideLoading(); //隱藏loading
					}
				});
				return false;
				
			}else{
				alert(msg);
				return false;
			} 
		}			
	});
	
	
  	//清除指定頁面資料
	function cleanTable(table_id){
		$(":checkbox,:radio").prop("checked", false);
		//清除所有頁面的內容(enable)，disable的不清除
		$('#'+table_id+' :input:enabled').each(function() {
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
	
	//讀取table欄位內容 selCode:0異動 1批次
  	$.getDataInfo = function(formnm,sel){
		var array = new Array();
		var value = [];
		var obj = {};
		var $th ;	
		var data_id;
		var data_val;
		var tableID;
		if (formnm=="tblSalNumList"){//餉單編號設定
			if (sel == "1")
				tableID="PATRAPSN";
			else if (sel == "2")
				tableID="PABATPAY";
			oTable = $('#' + formnm ).dataTable().api();
			oTable.$('input[type="checkbox"]:checked').each(function(index){
	 			tr = $(this).parents('tr');
				rowData = oTable.row(tr).data();	//抓取目前點選link這筆row的data
				obj[String($(this).attr("id"))] = rowData; //名稱+數值			  
		    });
		    value.push(obj);   	//以整支table寫入	
		    array.push( { "formnm": tableID, "value": value } ); 
   			
		}
   		    
  		var jsonArray = JSON.stringify(array); // 轉換成json
	    	    
	    console.log(jsonArray);
	    //alert("資料送出");
	    
	    return jsonArray;
	};
	
	
	//月支個人明細
 	var objCopyA = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyA.aoColumns = [	
							{ "sTitle":"","mData": null },
							{ "sTitle":"餉單編號","mData": "salnum" },
							{ "sTitle":"發薪日","mData": "paybank" },
							{ "sTitle":"身份證字號","mData": "payidno" },
							{ "sTitle":"卡號","mData": "paycard" },
							{ "sTitle":"職號","mData": "payempno" },
							{ "sTitle":"姓名","mData": "paynamec" },
							{ "sTitle":"單位","mData": "padepnmc" },
							{ "sTitle":"職稱","mData": "paytitlp" },
							{ "sTitle":"階別","mData": "rankNm" },
							{ "sTitle":"職等","mData": "rnk2nNm" },	
							{ "sTitle":"本功","mData": "rank3Nm" },
							{ "sTitle":"級別","mData": "paydegr2" },
							{ "sTitle":"俸點","mData": "paydegre" },
							{ "sTitle":"權理","mData": "payrank2" },
							{ "sTitle":"主管(權理)","mData": "payrankc" },
							{ "sTitle":"投保額","mData": "insuamt" },
							{ "sTitle":"專勤基本額度","mData": "paypfdoc" },
							{ "sTitle":"工作獎金(Y/N)","mData": "pacsflag" },
							{ "sTitle":"基本績分","mData": "pfbas" },
							{ "sTitle":"職等績分","mData": "pfdat" },
							{ "sTitle":"額外點數1","mData": "paypfsp1" },
							{ "sTitle":"額外點數2","mData": "paypfsp2" },
							{ "sTitle":"額外點數3","mData": "paypfsp3" },
							{ "sTitle":"專業加給表","mData": "rnk4name" }							
						 ];
	objCopyA.iDisplayLength=10;
	
	objCopyA = $.extend(true,objCopyA, {
				  dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
							't'+
						'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
					buttons: [
					   'excelHtml5' 
					],
	  			 "fnDrawCallback": function () {
						var api = this.api();
						var startIndex=0;
                    	api.column(0).nodes().each(function(cell, i) {
                        	cell.innerHTML = startIndex + i + 1;
                    	});
                 },
                 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
				 	$('td:eq(4)', nRow).html('<a href = "#datpsnData" class= "datpsnData">'+aData.paycard+'</a>' );
				 }   							
			});
						    
	$('#TblDatPsnList').dataTable(objCopyA);
	
	//顯示pa.padatpsn明細
	$('#TblDatPsnList tbody').on('click', '.datpsnData', function () {
		oTable = $('#TblDatPsnList').dataTable().api();
		info = oTable.page.info();		//記錄目前頁碼
		currentPage=info.page;
        var tr = $(this).parents('tr');
        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data	 		
		idtno=rowData.payidno;
		salnum=rowData.salnum;
		hid=rowData.hid;
		//找pa.padatpsn及pa.padaypay
		in_data="";
		in_data={
        		idtno      :idtno,
        		salnum     :salnum
        	};
		console.log("發薪明細padatpsn查詢:in_data:"+in_data);
		
		$.getDatPsnByKey(in_data);
		
	});
	
	//依KEY找出pa.padatpsn
	$.getDatPsnByKey = function(in_data){
		$.ajax({
			url:"${pageContext.request.contextPath}/salController.do",
			data:{
				method  : 'getDatPsnByKey',
				in_data :JSON.stringify(in_data),
				hid     : pahid,
				userid  : paUserid
			},
			type:"POST",			//type:POST, GET(default:GET)
			dataType:"json",	//dataType: text, xml, html, script, json, jsonp
			contentType: "application/x-www-form-urlencoded;charset=utf-8",
			success:function(data) {
				if (data.datPsnItem!=null){
					//顯示月支個人明細
					$.doDatpsnDetail(data.datPsnItem);
					//pa.padatpay
					$.doDatpayDetail(data.datPayList);
					//應稅所得
					$.doDatpaySum(data.datPaySum);
					//解決標題不一致問題
					var table = new $('#TblDatPayList').DataTable();
			 		$('#container').css( 'display', 'block' );
					table.columns.adjust().draw();
// 					$.fn.dataTable.tables( {visible: true, api: true} ).columns.adjust();
				}
			}  
		}) 
	};
	//顯示pa.padatpsn明細
	$.doDatpsnDetail = function(mData){
		$("#datpsnItemModal").modal("show");
		//modal標題:支扣代碼+中文
		$("#datpsnItemModal").find('.modal-title').text(mData.paycard+mData.paynamec+mData.paybank.dts);
		
		$("#datPsn_salnum").val(mData.salnum);
		$("#datPsn_paybank").val(mData.paybank.dts);
		$("#datPsn_payidno").val(mData.payidno);
		$("#datPsn_paycard").val(mData.paycard);
		$("#datPsn_payempno").val(mData.payempno);
		$("#datPsn_paynamec").val(mData.paynamec);
		$("#datPsn_paydept").val(mData.paydept);
		$("#datPsn_padepnmc").val(mData.padepnmc);
		$("#datPsn_paytitle").val(mData.paytitle);
		$("#datPsn_paytitlp").val(mData.paytitlp);
		$("#datPsn_insuamt").val(mData.insuamt);
		$("#datPsn_rankname").val(mData.rankname);
		$("#datPsn_rnk4name").val(mData.rnk4name);
		$("#datPsn_payrnk2n").val(mData.payrnk2n);
		$("#datPsn_payrank3").val(mData.rank3name);
		$("#datPsn_paydegr2").val(mData.paydegr2);
		$("#datPsn_paydegre").val(mData.paydegre);
		$("#datPsn_payrankc").val(mData.payrankc);
		$("#datPsn_payrank2").val(mData.payrank2);
		$("#datPsn_rnk4name").val(mData.rnk4name);
		$("#datPsn_pacsflag").val(mData.pacsflag);
		$("#datPsn_paypfdoc").val(mData.paypfdoc);
		$("#datPsn_paypfsp1").val(mData.paypfsp1);
		$("#datPsn_paypfsp2").val(mData.paypfsp2);
		$("#datPsn_paypfsp3").val(mData.paypfsp3);
		$("#datPsn_payupdid").val(mData.payupdid);
		$("#datPsn_updtm").val(mData.updtm);
		if (mData.hid=='1A0' || mData.hid=='2A0'){			
			$("#datPsn_pfkey").val(mData.pfkey);
			$("#datPsn_pfbas").val(mData.pfbas);
			$("#datPsn_pfdat").val(mData.pfdat);
		}
		$("#datPsn_contract").val(mData.contract);
		//契約,不含醫制,才有新舊制(Y/N)
		if (mData.contract!=''){
			$("#div_contract").removeClass("d-none");
			$("#div_authority").addClass("d-none");
		}	
		else{
			$("#div_contract").addClass("d-none");
			$("#div_authority").removeClass("d-none");
		}	
		//總院才需列出
		if (mData.hid=='1A0' || mData.hid=='2A0'){
			$(".cspf").removeClass("d-none");
		}else if (mData.pacsflag=='N'){
			$(".cspf").addClass("d-none");
		}
	};
	
	//顯示pa.paDatpay個人支扣明細
	$.doDatpayDetail = function(mData){
		$('#TblDatPaySum').show();
		oTable = $('#TblDatPaySum').dataTable(objCopyB);
		
		oTable.fnClearTable(0);		//清空資料
				
		if (mData!=null && mData.length>0){	
			oTable.fnAddData(mData);//重新給資料
		}
		oTable.fnDraw(false);
		
	};
	
	//顯示pa.paDatpay 應稅所得 淨所得
	$.doDatpaySum = function(mData){
		$('#datPay_taxpay').val(mData.taxpay);
		$('#datPay_netpay').val(mData.netpay);
		$('#datPay_retire').val(mData.retire);
		$('#datPay_thing_money').val(mData.thing_money);
		$('#datPay_rent_house').val(mData.rent_house);
		if (mData.thing_money>0){
			$("#div_thing").removeClass("d-none");
		}	
		else{	
			$('#div_thing').addClass("d-none");
		}
	};
	
	//月支支扣明細
 	var objCopyB = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyB.aoColumns = [	
							{ "sTitle":"項次","mData": null},
							{ "sTitle":"代碼","mData": null},
							{ "sTitle":"項目","mData": "pacodnmc" },
							{ "sTitle":"所得","mData": "payamtp" },
							{ "sTitle":"支出","mData": "payamtd" }
						 ];
						 
	objCopyB.iDisplayLength=100;
	
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
		 
		            // 總所得:Total over this page
		            pTotal = api
		                .column( 3	, { page: 'current'} )
		                .data()
		                .reduce( function (a, b) {
		                    		return intVal(a) + intVal(b);
		                		}, 0 );
		 
		            // Update footer
		            $( api.column( 3 ).footer() ).html(
		                pTotal 
		            );
		            // 總支出:Total over this page
		            dTotal = api
		                .column( 4	, { page: 'current'} )
		                .data()
		                .reduce( function (a, b) {
		                    		return intVal(a) + intVal(b);
		                		}, 0 );
		 
		            // Update footer
		            $( api.column( 4 ).footer() ).html(
		                dTotal 
		            );
		        }   							
			});   
                    
                    					 
	objCopyB.aoColumnDefs= [
 							{
							   'targets': 1,
							   'searchable':false,
							   'orderable':false,
							   'className': 'dt-body-center',
							   'render': function (data, type, row, meta){
							   				var color="black";
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
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
		buttons: [
           'excelHtml5' 
        ]
	}); 
	$('#TblDatPaySum').dataTable(objCopyB);
		      
	//檢驗sum
 	var objCopyC = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyC.aoColumns = [	
							{ "sTitle":"項次","mData": null},
							{ "sTitle":"支扣代碼","mData": "paycode"},
							{ "sTitle":"支扣內容","mData": null },
							{ "sTitle":"所得","mData": "payamtp" },
							{ "sTitle":"支出","mData": "payamtd" },
							{ "sTitle":"人數","mData": "cnt" }
						 ];
						 
	objCopyC.iDisplayLength=100;
	
	objCopyC = $.extend(true,objCopyC, {
				dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
						't'+
						'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
				buttons: [
		           'excelHtml5' ,'print'
		        ],
	  			 "fnDrawCallback": function () {
						var api = this.api();
						var startIndex=0;
                    	api.column(0).nodes().each(function(cell, i) {
                        	cell.innerHTML = startIndex + i + 1;
                    	});
                 },
                 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
				 	$('td:eq(1)', nRow).html('<a href = "#pdata" class= "paydata">'+aData.paycode+'</a>' );
				 },   
                 "footerCallback": function ( row, data, start, end, display ) {
		            var api = this.api(), data;
		 
		            // Remove the formatting to get integer data for summation
		            var intVal = function ( i ) {
		                return typeof i === 'string' ?
		                    		  i.replace(/[\$,]/g, '')*1 :
		                    		  typeof i === 'number' ? i : 0;
		            };
		 
		            // 總所得
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
		           // 總支出
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
                    
                    					 
	objCopyC.aoColumnDefs= [
 							{
							   'targets': 2,
							   'searchable':false,
							   'orderable':false,
							   'className': 'dt-body-left',
							   'render': function (data, type, row, meta){
							   				var color="black";
							   				if (data.paycode!=null && typeof(data.paycode)!="undefined"){
							   				
								   				if (data.paycode.substring(2, 1) == "P" && data.paycode.substring(4, 5) == "9") {
								   					color="blue";
								   				}else if (data.paycode.substring(2, 1) == "D" && data.paycode.substring(4, 5) == "9") {
								   					color="red";	
								   				}
								   			}		
							   				return '<span style="color:' + color + '">' + data.pacodnmc + '</span>';
                                         }
						     },
						     {
							   'targets': [ 3,4,5],
							   'className': 'dt-body-right'
						     }
						    ];
						    
	$('#TblCheckPayList').dataTable(objCopyC);
	
	$('#TblCheckPayList tbody').on('click', '.paydata', function () {
		oTable = $('#TblCheckPayList').dataTable().api();
		info = oTable.page.info();		//記錄目前頁碼
		currentPage=info.page;
        var tr = $(this).parents('tr');
        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data	 		
		salnum=rowData.salnum;
		paycode=rowData.paycode;
		var pacodnmc=rowData.pacodnmc;
		//依salnum+paycode找pa.padaypay清單
		in_data="";
		in_data={
        		salnum     :salnum,
        		paycode    :paycode
        	};
		console.log("[檢驗]依salnum+paycode找pa.padaypay清單:in_data:"+in_data);
		
		$.getDayPayByCode(in_data,paycode,pacodnmc);
		
	});
		 
	//依salnum+paycode找pa.padaypay清單
	$.getDayPayByCode = function(in_data,paycode,pacodnmc){
		$.ajax({
			url:"${pageContext.request.contextPath}/salController.do",
			data:{
				method  : 'getDayPayByCode',
				in_data :JSON.stringify(in_data),
				hid     : pahid,
				userid  : paUserid,
			},
			type:"POST",			//type:POST, GET(default:GET)
			dataType:"json",	//dataType: text, xml, html, script, json, jsonp
			contentType: "application/x-www-form-urlencoded;charset=utf-8",
			success:function(data) {
				if (data.datPayBycodeList!=null){
					//pa.padatpay
					$.doDatpayDetailByCheck(data.datPayBycodeList,paycode,pacodnmc);					
				}
			}  
		}) 
	};
	
	//顯示pa.paDatpay by salnum+paycode清單
	$.doDatpayDetailByCheck = function(mData,paycode,pacodnmc){
		$("#paycodeListModal").modal("show");
		//modal標題:支扣代碼+中文
		$("#paycodeListModal").find('.modal-title').text(paycode+pacodnmc);		
		
		$('#TblDatPayList').show();
		oTable = $('#TblDatPayList').dataTable(objCopyD);
		
		oTable.fnClearTable(0);		//清空資料
				
		if (mData!=null && mData.length>0){	
			oTable.fnAddData(mData);//重新給資料
		}
		oTable.fnDraw();
		
//		解決標題不一致問題
		var table = new $('#TblDatPayList').DataTable();
 		$('#container').css( 'display', 'block' );
		table.columns.adjust().fnDraw();
		
// 		$.fn.dataTable.tables( {visible: true, api: true} ).columns.adjust();	
	};
	
	//檢驗list
 	var objCopyD = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyD.aoColumns = [	
							{ "sTitle":"","mData": null},
							{ "sTitle":"卡號","mData": "paycard"},
							{ "sTitle":"身分證字號","mData": "payidno"},
							{ "sTitle":"職號","mData": "payempno"},
							{ "sTitle":"單位","mData": "padepnmc"},
							{ "sTitle":"職稱","mData": "paytitlc"},
							{ "sTitle":"姓名","mData": "paynamec"},
							{ "sTitle":"職等","mData": "payrnk2n"},
							{ "sTitle":"本功","mData": "payrank3"},
							{ "sTitle":"級別","mData": "paydegr2"},
							{ "sTitle":"俸點","mData": "paydegre"},
							{ "sTitle":"權理","mData": "payrank2"},
							{ "sTitle":"金額","mData": "payamt"},
						 ];
						 
	objCopyD.iDisplayLength=10;
						    
	objCopyD = $.extend(true,objCopyD, {
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
		buttons: [
           'excelHtml5' 
        ]
	});
	objCopyD.aoColumnDefs= [
 							{
							   'targets': [ 12],
							   'className': 'dt-body-right'
						     },
						     {
							   'targets': [ 7,8,9,10,11],
							   'className': 'dt-body-center'
						     },
						    ]; 
	objCopyD = $.extend(true,objCopyD, {
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
		 			//Total over all pages
		 			var total_1 = api
		                .column( 12 )
		                .data()
		                .reduce( function (a, b) {
		                    return intVal(a) + intVal(b);
		                }, 0 );
		            // Total over this page
		            var pTotal = api
		                .column( 12	, { page: 'current'} )
		                .data()
		                .reduce( function (a, b) {
		                    		return intVal(a) + intVal(b);
		                		}, 0 );
		 
		            // Update footer
		            $( api.column( 12 ).footer() ).html(
		            	'$'+pTotal +' <br>( $'+ total_1 +' 合計)'
		            );
				}   							
			});   
			
	$('#TblDatPayList').dataTable(objCopyD);
	
	//讀取餉單編單list style="display:none;"
 	var objCopyE = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyE.aoColumns = [	
							{ "sTitle":' <div><input type="checkbox" id="salnum_select_all" data-to-table="tasks" style="display:none;" > ',     "mData": null },
							{ "sTitle":"性質"    ,"mData": "typnm"},
							{ "sTitle":"卡號"    ,"mData": "paycard"},
							{ "sTitle":"姓名"    ,"mData": "paynamec"},
							{ "sTitle":"發薪日"  ,"mData": "patradt" },
							{ "sTitle":"餉單編單","mData": "salnum" },
							{ "sTitle":"代碼"    ,"mData": "itmnm" },
							
						 ];
	objCopyE.aoColumnDefs= [
							 //勾選
							{
							'targets': 0,
				         	'searchable':false,
				         	'orderable':false,
				         	'className': 'dt-body-center',
				         	'render': function (data, type, row, meta){
				         		if (data.salnum != "0")	//表示要設勾選框 
					         		return '<input type="checkbox" name="chkupdA" id="chkA_' +  meta.row + '" value="' 
			 					                + $('<div/>').text(data).html() + '">';
					            else
					            	return '';                
							    },
					        }
					        							 
						    ];
						    						 
	objCopyE.iDisplayLength=15;
						    
	objCopyE = $.extend(true,objCopyE, {
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
		buttons: [
           'excelHtml5' 
        ]
	});
			
	//$('#TblDatPayList').dataTable(objCopyE);
	
	//checked變色,取消還原,
	$(document).on("click", "input[name='chkupdA']", function(e) {
      	if($(this).prop("checked")){
			$(this).parent().parent().attr("style","background-color:#FFB6C1");
		}else{
			$(this).parent().parent().removeAttr("style");
		}
    });
    
	//餉單編號設定_全選
	$(document).on('change', '#salnum_select_all', function(){
		var rows, checked;
        rows = $('#tblSalNumList').find('tbody tr');
        checked = $(this).prop('checked');
       
        $.each(rows, function() {
            var checkbox = $($(this).find('td').eq(0)).find('input').prop('checked', checked);
            if (checked){
				$(this).attr("style","background-color:#FFB6C1");
			}
            else 
				$(this).removeAttr("style");           
        });
       
		$('input[name="chkupdA"]').prop("disabled", false);
	});
	
	//報表檔 
 	var objCopyF = $.extend(true, {aaSorting: []}, dataTableConfig);
 	if (pahid=="1A0"){			
		objCopyF.aoColumns = [	
			{ "sTitle":"","mData": null},
			{ "sTitle":"餉單編號"    ,"mData": "salnum"},
			{ "sTitle":"身份證"   ,"mData": "payidno"},
			{ "sTitle":"發薪日"  ,"mData": "paybank" },
			{ "sTitle":"卡號"    ,"mData": "paycard"},
			{ "sTitle":"姓名"    ,"mData": "paynamec"},
			{ "sTitle":"職號"    ,"mData": "payempno" },
			{ "sTitle":"帳號"	,"mData": "payacno" },
			{ "sTitle":"公儲"   	,"mData": "paygedno"},
			{ "sTitle":"單位"   	,"mData": "paydept" },
			{ "sTitle":"單位名稱"    ,"mData": "padepnmc"},
			{ "sTitle":"職稱<br>群組"    ,"mData": "patitsub"},
			{ "sTitle":"職稱<br>代碼"    ,"mData": "paytitle" },
			{ "sTitle":"職稱名稱",	"mData": "paytitlp" },
			{ "sTitle":"階別"   ,	"mData": "payrank"},
			{ "sTitle":"階別縮寫"  ,"mData": "rankabbr" },
			{ "sTitle":"職等"    ,	"mData": "payrnk2n"},
			{ "sTitle":"本功別"    	,"mData": "payrank3"},
			{ "sTitle":"級別"    ,	"mData": "paydegr2" },
			{ "sTitle":"俸點",		"mData": "paydegre" },
			{ "sTitle":"離職"   	,"mData": "paylvefg"},
			{ "sTitle":"異動原因"  ,"mData": "traname" },
			{ "sTitle":"生效日期"  	,"mData": "paeffdt" },
			{ "sTitle":"上月<br>支薪日數"    ,"mData": "lastdayno"},
			{ "sTitle":"本月<br>支薪日數"    ,"mData": "paydayno"},
			{ "sTitle":"上月<br>本薪"    ,"mData": "payobase" },
			{ "sTitle":"上月<br>專業加給","mData": "payospsu" },
			{ "sTitle":"上月<br>主管加給","mData": "payochalw" },
			//{ "sTitle":"保留",		"mData": "payostbn" },
			{ "sTitle":"上月<br>實習費"   ,"mData": "payosuply"},
			{ "sTitle":"本薪"  ,			"mData": "paybase" },
			{ "sTitle":"本薪_補"    ,		"mData": "payrbase"},
			{ "sTitle":"專業加給"    ,		"mData": "payspsu"},
			{ "sTitle":"專業加給_補"    ,	"mData": "payrspsu" },
			{ "sTitle":"專業差額",			"mData": "payspsd" },
			{ "sTitle":"專業差額_補",		"mData": "payrspsd" },
			{ "sTitle":"主管加給"   ,		"mData": "paychalw"},
			{ "sTitle":"主管加給_補"  ,		"mData": "payrchalw" },
			{ "sTitle":"工二"    ,			"mData": "paystbnt"},
			{ "sTitle":"專二"    ,			"mData": "payrstbn"},
			{ "sTitle":"津貼"    ,			"mData": "paytrans" },
			{ "sTitle":"實習費",			"mData": "paysuply" },
			{ "sTitle":"實習費_補",			"mData": "payrsuply" },
			{ "sTitle":"工獎"   ,			"mData": "paypfnt"},
			{ "sTitle":"專勤醫師費"  ,		"mData": "paystal" },
			{ "sTitle":"病歷罰扣"    ,		"mData": "paystald"},
			{ "sTitle":"夜班費"    ,		"mData": "paynight"},
			{ "sTitle":"不休假加班費"    ,"mData": "payyear" },
			{ "sTitle":"年終獎金",		"mData": "payaddhl" },
			{ "sTitle":"考績獎金",		"mData": "paygrade" },
			{ "sTitle":"晉級補發"   ,	"mData": "paypromo"},
			{ "sTitle":"休假補助"  ,	"mData": "payadrtn" },
			{ "sTitle":"補助"    ,"mData": "paycplm1"},
			{ "sTitle":"獎勵"    ,"mData": "paycplm2"},
			{ "sTitle":"加班費"    ,"mData": "paycplm3" },
			{ "sTitle":"超額獎金","mData": "payempfd" },
			{ "sTitle":"超盈餘獎金","mData": "paysars" },
			{ "sTitle":"顧問費"   ,"mData": "payextra"},
			{ "sTitle":"公保費"  ,"mData": "paygeins" },
			{ "sTitle":"健保費"    ,"mData": "paydpins"},
			{ "sTitle":"勞保費"    ,"mData": "paylbins"},
			{ "sTitle":"退撫離職金"    ,"mData": "payretir" },
			{ "sTitle":"所得稅","mData": "paytax" },
			{ "sTitle":"公教儲金","mData": "paygedp" },
			{ "sTitle":"體適能"   ,"mData": "payvetd1"},
			//{ "sTitle":"國宅貸款(土銀)"  ,"mData": "payhloa1" },
			//{ "sTitle":"國宅貸款(合庫)"    ,"mData": "payhloa2"},
			//{ "sTitle":"國宅貸款(中信)"    ,"mData": "payhloa3"},
			{ "sTitle":"國宅貸款<br>_第一商銀"    ,"mData": "payhloa4" },
			//{ "sTitle":"國宅貸款(台銀)","mData": "payhloa5" },
			//{ "sTitle":"國宅貸款(富邦)","mData": "payhloa6" },
			{ "sTitle":"自繳退休金"  ,"mData": "paydloan" },
			{ "sTitle":"房租津貼"    ,"mData": "paymhous"},
			{ "sTitle":"醫療互助金"    ,"mData": "paynspfd"},
			{ "sTitle":"代扣停車費"    ,"mData": "paypark1" },
			{ "sTitle":"其他代收款","mData": "paypark2" },//#11504112 2026.5.27
			{ "sTitle":"公共設施使用費"   ,"mData": "paygym"},
			{ "sTitle":"清償債款"  ,"mData": "paylien" },
			{ "sTitle":"餐費"    ,"mData": "paymeal"},
			{ "sTitle":"卡費"    ,"mData": "paycardf"},
			{ "sTitle":"眷舍管理費<br>(10F)"    ,"mData": "paydorm1" },
			{ "sTitle":"眷舍管理費<br>(5F)","mData": "paydorm2" },
			{ "sTitle":"眷舍管理費<br>(單身)","mData": "paydorm3" },
			{ "sTitle":"補充保險費"    ,"mData": "payother1"},
			//{ "sTitle":"保留"    ,"mData": "payother2" },
			{ "sTitle":"醫療糾紛"    ,"mData": "payother3"},
			{ "sTitle":"惠民捐款","mData": "payagen1" },
			{ "sTitle":"總所得(A)","mData": "paytotal" },
			{ "sTitle":"薪津表<br>_補發扣(B)"   ,"mData": "paytotis"},
			{ "sTitle":"扣款數(C)"  ,"mData": "paytlded" },
			{ "sTitle":"薪津表淨所得<br>(D)=(A)-(C)"    ,"mData": "paynet"},
			{ "sTitle":"實發數(D)+(B)"    ,"mData": "payreal"},
							
						 ];
	}
 	//20260604
	else if (pahid=="2A0"){			
		objCopyF.aoColumns = [	
							{ "sTitle":"","mData": null},
							{ "sTitle":"離職"   	,"mData": "paylvefg"},
							{ "sTitle":"異動原因"  ,"mData": "traname" },
							{ "sTitle":"生效日期"  	,"mData": "paeffdt" },
							{ "sTitle":"上月<br>支薪日數"    ,"mData": "lastdayno"},
							{ "sTitle":"本月<br>支薪日數"    ,"mData": "paydayno"},
							{ "sTitle":"發薪日"  ,"mData": "paybank" },
							{ "sTitle":"身份證2A0"   ,"mData": "payidno"},
							{ "sTitle":"發薪日2A0"  ,"mData": "paybank" },
							{ "sTitle":"卡號2A0"    ,"mData": "paycard"},
							{ "sTitle":"姓名2A0"    ,"mData": "paynamec"},
							{ "sTitle":"職號2A0"    ,"mData": "payempno" },
							{ "sTitle":"帳號"	,"mData": "payacno" },
							{ "sTitle":"公儲"   	,"mData": "paygedno"},
							{ "sTitle":"單位代碼"   	,"mData": "paydept" },
							{ "sTitle":"單位中文"    ,"mData": "padepnmc"},
							{ "sTitle":"職稱<br>群組"    ,"mData": "patitsub"},
							{ "sTitle":"職稱<br>代碼"    ,"mData": "paytitle" },
							{ "sTitle":"職稱中文",	"mData": "paytitlp" },
							{ "sTitle":"階別代碼"   ,	"mData": "payrank"},
							{ "sTitle":"階別縮寫"  ,"mData": "rankabbr" },
							{ "sTitle":"職等"    ,	"mData": "payrnk2n"},
							{ "sTitle":"本功"    	,"mData": "payrank3"},
							{ "sTitle":"級別"    ,	"mData": "paydegr2" },
							{ "sTitle":"俸點",		"mData": "paydegre" },
							{ "sTitle":"上月<br>本薪"    ,"mData": "payobase" },
							{ "sTitle":"上月<br>專業加給","mData": "payospsu" },
							{ "sTitle":"上月<br>主管加給","mData": "payochalw" },
							{ "sTitle":"上月<br>證照加給","mData": "payostbn" },
							{ "sTitle":" ","mData": "payosuply" },
							{ "sTitle":"本薪"  ,			"mData": "paybase" },
			                { "sTitle":"本薪補發扣"    ,		"mData": "payrbase"},
			                { "sTitle":"專業加給"    ,		"mData": "payspsu"},
			    			{ "sTitle":"專業加給補發扣"    ,	"mData": "payrspsu" },
			    			{ "sTitle":"專業加給差額",			"mData": "payspsd" },
			    			{ "sTitle":"專業加給差額補發扣",		"mData": "payrspsd" },
			    			{ "sTitle":"主管加給"   ,		"mData": "paychalw"},
			    			{ "sTitle":"主管加給補發扣"  ,		"mData": "payrchalw" },
			    			{ "sTitle":"證照/院區", "mData": "paystbnt" },
			    			{ "sTitle":"證照/院區補發扣","mData": "payrstbn" },
			    			{ "sTitle":"身心障礙","mData": "paytrans" },
			    			{ "sTitle":"特別津貼","mData": "paysuply" },
			    			{ "sTitle":"護理能力進階","mData": "payrsuply" },
			    			{ "sTitle":"獎勵金_行政及醫事","mData": "paypfnt" },
			    			{ "sTitle":"專勤醫師費","mData": "paystal" }, //PAREPDETAIL42
			    			{ "sTitle":"專勤醫師費二"    ,			"mData": "paystald"},//專二跟專勤醫師費二是一樣的嗎??payrstbn
			    			{ "sTitle":"夜班費"    ,		"mData": "paynight"},
			    			{ "sTitle":"不休假加班費"    ,"mData": "payyear" },
			    			{ "sTitle":"年終獎金",		"mData": "payaddhl" },
			    			{ "sTitle":"考績獎金",		"mData": "paygrade" },
			    			{ "sTitle":"晉級補發"   ,	"mData": "paypromo"},
			    			{ "sTitle":"休假補助"  ,	"mData": "payadrtn" },
			    			{ "sTitle":"補助_生日禮金"   ,	"mData": "paycplm1"},
			    			{ "sTitle":"簽約留任金"  ,	"mData": "paycplm2" },
			    			{ "sTitle":"加班費"    ,"mData": "paycplm3" },
			    			{ "sTitle":" "    ,"mData": "payempfd" },
			    			{ "sTitle":"病歷罰扣"  ,	"mData": "paysars" },  //專勤醫師費--病歷罰扣 PAREPDETAIL 43但是屏榮月報表上寫的是paysars 65超盈餘獎金
			    			{ "sTitle":"值班費"  ,"mData": "payextra" }, //屏榮月報表寫對應PAYEXTRA，但PAREPDETAIL 55寫的中文是顧問費
			    			{ "sTitle":"公保費"  ,"mData": "paygeins" },
			                { "sTitle":"健保費"    ,"mData": "paydpins"},
			                { "sTitle":"勞保費"    ,"mData": "paylbins"},
			                { "sTitle":"退撫離職金"    ,"mData": "payretir" },
			                { "sTitle":"所得稅","mData": "paytax" },
			    			{ "sTitle":"公教儲金","mData": "paygedp" },
			    			{ "sTitle":" "    ,"mData": "payvetd1" },  //這七格跟VB版產出的報表檔多一格 VB板只有六格 但是屏榮月報表有7格
			    			{ "sTitle":" "    ,"mData": "payhloa1" },
			    			{ "sTitle":" "    ,"mData": "payhloa2" },
			    			{ "sTitle":" "    ,"mData": "payhloa3" },
			    			{ "sTitle":" "    ,"mData": "payhloa4" },
			    			{ "sTitle":" "    ,"mData": "payhloa5" },
			    			{ "sTitle":" "    ,"mData": "payhloa6" },
			    			{ "sTitle":"勞工退休金"    ,"mData": "paydloan"}, 
			    			{ "sTitle":"房租津貼"    ,"mData": "paymhous"},
			                { "sTitle":"醫療互助金"    ,"mData": "paynspfd"},
			                { "sTitle":"停車費"    ,"mData": "paypark1" },
			                { "sTitle":" "    ,"mData": "paypark2" },
			                { "sTitle":"遺孤認養金"  ,"mData": "paygym" },
			                { "sTitle":"清償債款"  ,"mData": "paylien" },
			                { "sTitle":" "    ,"mData": "paymeal" },
			                { "sTitle":" "    ,"mData": "paycardf" },
			                { "sTitle":"宿舍管理費"    ,"mData": "paydorm1" }, 
			                { "sTitle":"宿舍電費"    ,"mData": "paydorm2" },
			                { "sTitle":"醫療救助捐款"    ,"mData": "paydorm3" },
			                { "sTitle":"補充保險費"    ,"mData": "payother1"},
			                { "sTitle":"高榮代收代付"    ,"mData": "payother2" },
			                { "sTitle":" "    ,"mData": "payother3" },
			                { "sTitle":"弱勢醫療捐款"    ,"mData": "payagen1" },
			                { "sTitle":"總所得","mData": "paytotal" },
			                { "sTitle":"薪津表<br>_補發扣"   ,"mData": "paytotis"},
			                { "sTitle":"扣款數"  ,"mData": "paytlded" },
			                { "sTitle":"薪津表_實發數"    ,"mData": "paynet"}, //這個是要帶paynet還是payreal? 
							{ "sTitle":"餉單編單2A0","mData": "salnum" }
							
							
						 ];
	}
	else if (pahid=="3A0"){
		objCopyF.aoColumns = [	
							{ "sTitle":"","mData": null},
							{ "sTitle":"身份證3A0"   ,"mData": "payidno"},
							{ "sTitle":"發薪日3A0"  ,"mData": "paybank" },
							{ "sTitle":"卡號3A0"    ,"mData": "paycard"},
							{ "sTitle":"姓名3A0"    ,"mData": "paynamec"},
							{ "sTitle":"職號3A0"    ,"mData": "payempno" },
							{ "sTitle":"餉單編單3A0","mData": "salnum" }
							
						 ];
	}						 		    						 
	objCopyF = $.extend(true,objCopyF, {
			  dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
						't'+
					'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
				buttons: [
				   'excelHtml5' 
				],
  			 "fnDrawCallback": function () {
					var api = this.api();
					var startIndex=0;
                   	api.column(0).nodes().each(function(cell, i) {
                       	cell.innerHTML = startIndex + i + 1;
                   	});
                }						
		});
	
	/* 量大時,會很慢才出來,不做處理					    
	objCopyF = $.extend(true,objCopyF, {
				dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
						't'+
						'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
				buttons: [
		           'excelHtml5' 
		        ],
	  			"fnDrawCallback": function () {
						var api = this.api();
						var startIndex=0;
                    	api.column(0).nodes().each(function(cell, i) {
                        	cell.innerHTML = startIndex + i + 1;
                    	});
                    	
                    	formnm="TblRepDtlList";
					 	uTable = $('#' + formnm ).dataTable();
					 	formnm_page = formnm + "_paginate";		
					 	changePage="changePageF";		 
					 	//到某頁
					 	$('#' + formnm_page ).append("  到第 <input style='height:28px;line-height:28px;width:40px;' class='margin text-center' id='" + changePage + "' type='text'> 頁  <a class='btn btn-success shiny' style='margin-bottom:5px' href='javascript:void(0);' id='dataTable-btn'>確認</a>");
					 	
					 	$('#dataTable-btn').click(function(e) {
					 		if($("#"+ changePage).val() && $("#" + changePage ).val() > 0) {
					 			redirectpage = $("#" +changePage).val() - 1;
					 		} else {
					 			redirectpage = 0;
					 		}	
					 		uTable.fnPageChange(redirectpage);		
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
		 
		            // 薪津表_總所得(A)
		            i_pageSum=81;
		            var total_1 = api
		                .column( i_pageSum )
		                .data()
		                .reduce( function (a, b) {
		                    return intVal(a) + intVal(b);
		                }, 0 );
		 
		            // Total over this page
		            var pageTotal_1 = api
		                .column( i_pageSum	, { page: 'current'} )
		                .data()
		                .reduce( function (a, b) {
		                    return intVal(a) + intVal(b);
		                }, 0 );
		                
		 			// Update footer
		            $( api.column( i_pageSum ).footer() ).html(
		                '$'+pageTotal_1 +'<br>( $'+ total_1 +' 合計)'
		            );
		            
		            // 薪津表_補發扣(B)
		            i_pageSum=82;
		            var total_2 = api
		                .column( i_pageSum )
		                .data()
		                .reduce( function (a, b) {
		                    return intVal(a) + intVal(b);
		                }, 0 );
		 
		            // Total over this page
		            var pageTotal_2 = api
		                .column( i_pageSum	, { page: 'current'} )
		                .data()
		                .reduce( function (a, b) {
		                    return intVal(a) + intVal(b);
		                }, 0 );
		                
		 			// Update footer
		            $( api.column( i_pageSum ).footer() ).html(
		                '$'+pageTotal_2 +'<br>( $'+ total_2 +' 合計)'
		            );
		            
		            // 扣款數(C)
		            i_pageSum=83;
		            var total_3 = api
		                .column( i_pageSum )
		                .data()
		                .reduce( function (a, b) {
		                    return intVal(a) + intVal(b);
		                }, 0 );
		 
		            // Total over this page
		            var pageTotal_3 = api
		                .column( i_pageSum	, { page: 'current'} )
		                .data()
		                .reduce( function (a, b) {
		                    return intVal(a) + intVal(b);
		                }, 0 );
		                
		 			// Update footer
		            $( api.column( i_pageSum ).footer() ).html(
		                '$'+pageTotal_3 +'<br>( $'+ total_3 +' 合計)'
		            );
		            
		            // 薪津表_淨所得(D)=(A)-(C)
		            i_pageSum=84;
		            var total_4 = api
		                .column( i_pageSum )
		                .data()
		                .reduce( function (a, b) {
		                    return intVal(a) + intVal(b);
		                }, 0 );
		 
		            // Total over this page
		            var pageTotal_4 = api
		                .column( i_pageSum	, { page: 'current'} )
		                .data()
		                .reduce( function (a, b) {
		                    return intVal(a) + intVal(b);
		                }, 0 );
		                
		 			// Update footer
		            $( api.column( i_pageSum ).footer() ).html(
		                '$'+pageTotal_4 +'<br>( $'+ total_4 +' 合計)'
		            );
		            
		            //實發數(D)+(B)
		            i_pageSum=85;
		            var total_5 = api
		                .column( i_pageSum )
		                .data()
		                .reduce( function (a, b) {
		                    return intVal(a) + intVal(b);
		                }, 0 );
		 
		            // Total over this page
		            var pageTotal_5 = api
		                .column( i_pageSum	, { page: 'current'} )
		                .data()
		                .reduce( function (a, b) {
		                    return intVal(a) + intVal(b);
		                }, 0 );
		                
		 			// Update footer
		            $( api.column( i_pageSum ).footer() ).html(
		                '$'+pageTotal_5 +'<br>( $'+ total_5 +' 合計)'
		            );				           
		        } 							
			});	*/
			
	//支扣明細查詢
 	var objCopyG = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyG.aoColumns = [	
							{ "sTitle":"","mData": null},
							{ "sTitle":"發薪日","mData": "paybank"},
							{ "sTitle":"卡號","mData": "paycard"},
							{ "sTitle":"身分證","mData": "payidno"},
							{ "sTitle":"單位","mData": "padepnmc"},
							{ "sTitle":"職稱","mData": "paytitlc"},
							{ "sTitle":"姓名","mData": "paynamec"},
							{ "sTitle":"金額","mData": "payamt"},
						 ];
						 
	objCopyG.iDisplayLength=10;
						    
	objCopyG = $.extend(true,objCopyG, {
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
		buttons: [
           'excelHtml5' 
        ],
	 	"fnDrawCallback": function () {
			var api = this.api();
			var startIndex=0;
	                	api.column(0).nodes().each(function(cell, i) {
	                    	cell.innerHTML = startIndex + i + 1;
	                	});
	             }
	});
	$('#tblItmPayList').dataTable(objCopyG);
	
	//檔案產製_主計室_發放
 	var objCopyH = $.extend(true, {aaSorting: []}, dataTableConfig);
 	if (pahid=="1A0"){
 		objCopyH.aoColumns = [	
							{ "sTitle":"","mData": null},
							{ "sTitle":"發薪日","mData": "paybank"},
							{ "sTitle":"單位","mData": "paydept"},
							{ "sTitle":"卡號","mData": "paycard"},
							{ "sTitle":"姓名","mData": "paynamec"},
							{ "sTitle":"職稱","mData": "paytitle"},
							{ "sTitle":"職稱中文","mData": "paytitlp"},
							{ "sTitle":"性別","mData": "sex"},
							{ "sTitle":"職號","mData": "payempno"},
							{ "sTitle":"新制公保","mData": "l_Govfg"},
							{ "sTitle":"契約(月尾)","mData": "l_contract"},
							{ "sTitle":"本薪","mData": "P1"},
							{ "sTitle":"專業加給","mData": "P2"},
							{ "sTitle":"主管加給","mData": "P3"},
							{ "sTitle":"工作獎金2","mData": "P4"},
							{ "sTitle":"津貼_保險病歷查詢","mData": "P5"},
							{ "sTitle":"實習費","mData": "P6"},
							{ "sTitle":"工作獎金","mData": "P7"},
							{ "sTitle":"專勤醫師費","mData": "P8"},
							{ "sTitle":"病歷罰扣","mData": "P9"},
							{ "sTitle":"夜班費","mData": "P10"},
							{ "sTitle":"不休假獎金","mData": "P11"},
							{ "sTitle":"年終獎金","mData": "P12"},
							{ "sTitle":"考績獎金","mData": "P13"},
							{ "sTitle":"晉級補發","mData": "P14"},
							{ "sTitle":"休假補助","mData": "P15"},
							{ "sTitle":"子女教育補助","mData": "P16"},
							{ "sTitle":"婚喪生育","mData": "P17"},
							{ "sTitle":"加班費","mData": "P18"},
							{ "sTitle":"顧問費","mData": "P19"},
							{ "sTitle":"超盈餘獎金","mData": "P20"},
							{ "sTitle":"超額獎金","mData": "P21"},
							{ "sTitle":"醫療收入績優獎勵","mData": "P22"},
							{ "sTitle":"專勤醫師費二","mData": "P23"},
							{ "sTitle":"津貼-值班費","mData": "P24"},
							{ "sTitle":"獎勵-教職獎勵","mData": "P25"},
							{ "sTitle":"獎勵-論文獎勵","mData": "P26"},
							{ "sTitle":"總值日值班費","mData": "P27"},
							{ "sTitle":"工獎-團","mData": "P28"},
							{ "sTitle":"生日禮金","mData": "P29"},
							{ "sTitle":"計劃補助","mData": "P30"},
							{ "sTitle":"工獎-團(2)_任務","mData": "P31"},
							{ "sTitle":"工獎-團(3)","mData": "P32"},
							{ "sTitle":"工獎-院長外加","mData": "P33"},
							{ "sTitle":"工獎-留任新進三成","mData": "P34"},
							{ "sTitle":"年度績效獎金","mData": "P35"},
						 ];
	}						 
	else if (pahid=="2A0"){
 		objCopyH.aoColumns = [	
							{ "sTitle":"","mData": null},
							{ "sTitle":"發薪日","mData": "paybank"},
							{ "sTitle":"單位","mData": "paydept"},
							{ "sTitle":"卡號","mData": "paycard"},
							{ "sTitle":"姓名","mData": "paynamec"},
							{ "sTitle":"職稱","mData": "paytitle"},
							{ "sTitle":"職稱中文","mData": "paytitlp"},
							{ "sTitle":"性別","mData": "sex"},
							{ "sTitle":"職號","mData": "payempno"},
							{ "sTitle":"新制公保","mData": "l_Govfg"},
							{ "sTitle":"契約(月尾)","mData": "l_contract"},
						 ];
	}						 
	else if (pahid=="3A0"){
 		objCopyH.aoColumns = [	
							{ "sTitle":"","mData": null},
							{ "sTitle":"發薪日","mData": "paybank"},
							{ "sTitle":"單位","mData": "paydept"},
							{ "sTitle":"卡號","mData": "paycard"},
							{ "sTitle":"姓名","mData": "paynamec"},
							{ "sTitle":"職稱","mData": "paytitle"},
							{ "sTitle":"職稱中文","mData": "paytitlp"},
							{ "sTitle":"性別","mData": "sex"},
							{ "sTitle":"職號","mData": "payempno"},
							{ "sTitle":"新制公保","mData": "l_Govfg"},
							{ "sTitle":"契約(月尾)","mData": "l_contract"},
						 ];
	}						 						 		
	objCopyH.iDisplayLength=10;
						    
	objCopyH = $.extend(true,objCopyH, {
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
		buttons: [
           'excelHtml5' 
        ],
	 	"fnDrawCallback": function () {
			var api = this.api();
			var startIndex=0;
	                	api.column(0).nodes().each(function(cell, i) {
	                    	cell.innerHTML = startIndex + i + 1;
	                	});
	             }
	});
	/* $('#tblBASPayList').dataTable(objCopyH); */
	//檔案產製_主計室_扣款
 	var objCopyI = $.extend(true, {aaSorting: []}, dataTableConfig);
 	if (pahid=="1A0"){
 		objCopyI.aoColumns = [	
							{ "sTitle":"","mData": null},
							{ "sTitle":"發薪日","mData": "paybank"},
							{ "sTitle":"單位","mData": "paydept"},
							{ "sTitle":"卡號","mData": "paycard"},
							{ "sTitle":"姓名","mData": "paynamec"},
							{ "sTitle":"職稱","mData": "paytitle"},
							{ "sTitle":"職稱中文","mData": "paytitlp"},
							{ "sTitle":"性別","mData": "sex"},
							{ "sTitle":"職號","mData": "payempno"},
							{ "sTitle":"新制公保","mData": "l_Govfg"},
							{ "sTitle":"惠民捐款","mData": "D1"},
							{ "sTitle":"公保費","mData": "D2"},
							{ "sTitle":"健保費","mData": "D3"},
							{ "sTitle":"勞保費","mData": "D4"},
							{ "sTitle":"退撫離職金","mData": "D5"},
							{ "sTitle":"所得稅","mData": "D6"},
							{ "sTitle":"體適能中心清潔費","mData": "D7"},
							{ "sTitle":"國宅貸款","mData": "D8"},
							{ "sTitle":"補充保險費","mData": "D9"},
							{ "sTitle":"餐費-員工食堂合作社","mData": "D10"},
							{ "sTitle":"房屋津貼","mData": "D11"},
							{ "sTitle":"醫療互助金","mData": "D12"},
							{ "sTitle":"代扣停車費","mData": "D13"},
							{ "sTitle":"其它代收款","mData": "D14"},
							{ "sTitle":"公共設施使用費","mData": "D15"},
							{ "sTitle":"清償債款","mData": "D16"},
							{ "sTitle":"餐費","mData": "D17"},
							{ "sTitle":"卡費","mData": "D18"},
							{ "sTitle":"眷舍管理費(10F)","mData": "D19"},
							{ "sTitle":"眷舍管理費(5F)","mData": "D20"},
							{ "sTitle":"眷舍管理費(單身)","mData": "D21"},
							{ "sTitle":"保留","mData": "D22"},
							{ "sTitle":"PGY眷舍管理費","mData": "D23"},
							{ "sTitle":"醫療糾紛","mData": "D24"},
							{ "sTitle":"體適能中心訓練費","mData": "D25"},
							{ "sTitle":"餐費-員工食堂","mData": "D26"},
							{ "sTitle":"德齡館使用費","mData": "D27"},
							
						 ];
	}						 
	else if (pahid=="2A0"){
 		objCopyI.aoColumns = [	
							{ "sTitle":"","mData": null},
							{ "sTitle":"發薪日","mData": "paybank"},
							{ "sTitle":"單位","mData": "paydept"},
							{ "sTitle":"卡號","mData": "paycard"},
							{ "sTitle":"姓名","mData": "paynamec"},
							{ "sTitle":"職稱","mData": "paytitle"},
							{ "sTitle":"職稱中文","mData": "paytitlp"},
							{ "sTitle":"性別","mData": "sex"},
							{ "sTitle":"職號","mData": "payempno"},
							{ "sTitle":"新制公保","mData": "l_Govfg"},
							
						 ];
	}						 
	else if (pahid=="3A0"){
 		objCopyI.aoColumns = [	
							{ "sTitle":"","mData": null},
							{ "sTitle":"發薪日","mData": "paybank"},
							{ "sTitle":"單位","mData": "paydept"},
							{ "sTitle":"卡號","mData": "paycard"},
							{ "sTitle":"姓名","mData": "paynamec"},
							{ "sTitle":"職稱","mData": "paytitle"},
							{ "sTitle":"職稱中文","mData": "paytitlp"},
							{ "sTitle":"性別","mData": "sex"},
							{ "sTitle":"職號","mData": "payempno"},
							{ "sTitle":"新制公保","mData": "l_Govfg"},
							
						 ];
	}						 						 		
	objCopyI.iDisplayLength=10;
						    
	objCopyI = $.extend(true,objCopyI, {
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
		buttons: [
           'excelHtml5' 
        ],
	 	"fnDrawCallback": function () {
			var api = this.api();
			var startIndex=0;
	                	api.column(0).nodes().each(function(cell, i) {
	                    	cell.innerHTML = startIndex + i + 1;
	                	});
	             }
	});
	/* $('#tblBASDeductList').dataTable(objCopyI); */
	
});

</script>
</head>
<body>

<ul class="nav nav-pills mb-3" id="pills-tab" role="tab">
  <li class="nav-item">
    <a class="nav-link active" id="pills-pay-tab" data-toggle="pill" href="#byPayDetailMain" role="tab" aria-controls="pills-home" aria-selected="true">發薪明細</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-check-tab" data-toggle="pill" href="#byCheckMain" role="tab" aria-controls="pills-profile" aria-selected="false">檢驗</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-setSalnum-tab" data-toggle="pill" href="#bySetSalnumMain" role="tab" aria-controls="pills-profile" aria-selected="false">餉單編號設定</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-prodDat-tab" data-toggle="pill" href="#byProdDatMain" role="tab" aria-controls="pills-profile" aria-selected="false">產生發薪明細</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-prodPrt-tab" data-toggle="pill" href="#byPordPrtMain" role="tab" aria-controls="pills-profile" aria-selected="false">報表作業</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-printPrt-tab" data-toggle="pill" href="#byPrintPrtMain" role="tab" aria-controls="pills-profile" aria-selected="false">報表列印</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-itmPay-tab" data-toggle="pill" href="#byItmPayMain" role="tab" aria-controls="pills-profile" aria-selected="false">支扣項目查詢</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-fileProd-tab" data-toggle="pill" href="#byFileProdMain" role="tab" aria-controls="pills-profile" aria-selected="false">檔案產製</a>
  </li>
</ul>

<div class="tab-content" id="pills-tabContent">
	<!--發薪明細-->
  	<div class="tab-pane fade show active" id="byPayDetailMain" role="tabpanel" aria-labelledby="pills-pay-tab">
		<div class="container-fluid">
			<div class="row" id="qryPsnDetailItem">
				<div class="col-10">
					<div class="form-inline">
		    			<div class="input-group">
		    				<%-- <select id="qry_padatpsn_salnum"  class="selectpicker SALNUM" data-width="fit" multiple title="餉單編號">
		    					<option value=""></option>
								<c:forEach var="bitem" items="${alBankDtTbl}" varStatus="loop">
									<option value="${bitem.salnum}">${bitem.salnum}</option>
								</c:forEach>
							</select>
							<select id="qry_padatpsn_paybank" class="selectpicker PAYBANK" data-width="fit"  data-live-search="true" multiple title="發薪日">
								<option value=""></option>
								<c:forEach var="bitem" items="${alBankDtTbl}" varStatus="loop">
									<option value="${bitem.paybank}">${bitem.paybank}</option>
								</c:forEach>
							</select> --%>
							<input type="text" id="qry_padatpsn_salnum" size="4" class="form-control" placeholder="餉單編號" >
							<input type="text" id="qry_padatpsn_paybank" size="10" class="form-control gclsCalTxt" placeholder="發薪日" >
		    				<input type="text" id="qryCardno" size="4" class="form-control text-uppercase" placeholder="卡號" >
							<input type="text" id="qryIdtno" size="10" class="form-control text-uppercase" placeholder="身分證" >
							<input type="text" id="qryNamec" size="12" class="form-control" placeholder="姓名" >

							<select id="qryDept" name="qryDept" class="selectpicker" data-width="fit" data-live-search="true" multiple title="單位">
								<option value=""></option>
			     				<c:forEach var="uitem" items="${alDeptCodeTbl}" varStatus="loop">
									<option value="${uitem.padepcod}">${uitem.pacstno}-${uitem.padepnmc}</option>
								</c:forEach>
							</select>
							<select id="qryTitle" name="qryTitle" class="selectpicker " data-width="fit" data-live-search="true" multiple title="職稱">
								<option value=""></option>
			     				<c:forEach var="uitem" items="${alTitleCodeTbl}" varStatus="loop">
									<option value="${uitem.paytitlk}">${uitem.patitsub}-${uitem.paytitlk}-${uitem.paytitlc}</option>
								</c:forEach>
							</select>
							
							<select id="qry_payrank"  class="selectpicker RANKCODE " data-width="fit" data-live-search="true" multiple title="階級">
								<option value=""></option>
								<c:forEach var="uitem" items="${alRankCodeTbl}" varStatus="loop">
									<option value="${uitem.rankcode}">${uitem.rankcode}-${uitem.rankname}</option>
								</c:forEach>
							</select>
							
							<select id="qry_insuamt"  class="selectpicker INSUAMT " data-width="fit" data-live-search="true" multiple title="投保級距">
								<option value=""></option>
								<c:forEach var="uitem" items="${alDpinTbl}" varStatus="loop">
									<option value="${uitem.dpinhigh}">${uitem.dpinhigh}</option>
								</c:forEach>
							</select>
						</div>		    		
			    		
					</div>
				</div>
				<div class="col-2">
					<button id="btn_qryDatPsn" class="btn btn-info" type="button" ><i class="fa fa-search"></i>查詢</button>
					<button id="btn_clnDatPsn" class="btn btn-warning" type="button"><i class="fa fa-eraser"></i>清空</button>
				</div>
			</div>
				
			<div class="row">
				<div class="col-12 border border-dark">
					<!-- pa.padatpsn清單 -->
					<table id="TblDatPsnList" class="table table-striped table-bordered table-hover" >
						<thead>							
						</thead>
					</table>
				</div>
			</div>
 		</div>	
	</div>
	
	<div class="modal fade"  id="datpsnItemModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog" >
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">個人明細</h5>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">×</span></button>
				 </div>
				<div class="modal-body">
					<div class="container-fluid">
						<div class="row align-items-start form-inline border-dark">
							<div class="col-7 border border-dark">
							<!-- pa.padatpsn明細 -->
								<table id="tblDatPsnData" class="table table-striped table-bordered table-hover table-sm " width="100%">
									<tbody>
										<tr>
											<th>餉單編號</th>
											<td><input type="text" id="datPsn_salnum" class="form-control" maxlength="4" size="4"  /></td>
											<th>發薪日</th>
											<td><input type="text" id="datPsn_paybank" class="gclsCalTxt form-control"  maxlength="10" size="10"  /></td>
											<th>身份證字號</th>
											<td><input type="text" id="datPsn_payidno" class="text-uppercase form-control" maxlength="10" size="10"  /></td>											
										</tr>
										<tr>
											<th>卡號</th>											
											<td><input type="text" id="datPsn_paycard" class="text-uppercase form-control" maxlength="4" size="4"  /></td>
											<th>職號</th>											
											<td><input type="text" id="datPsn_payempno" class="text-uppercase form-control" maxlength="7" size="7"  /></td>
											<th>姓名</th>
											<td><input type="text" id="datPsn_paynamec" class="form-control" maxlength="12" size="12"  /></td>
										</tr>
										<tr><td colspan=6>
											<div class="form-inline">
												<div class="form-group">	
													<label for="datPsn_paydept" class="form-inline">單位:</label>
													<input type="text" id="datPsn_paydept" class="form-control" maxlength="4" size="4"  />
													<input type="text" id="datPsn_padepnmc" class="form-control" maxlength="22" size="22"  />
												</div>
												</div>
											</td>	
										</tr>
										<tr><td colspan=6>
											<div class="form-inline">
												<div class="form-group">
													<label for="datPsn_paytitle" class="form-inline">職稱:</label>
													<input type="text" id="datPsn_paytitle" class="form-control" maxlength="2" size="2"  />
													<input type="text" id="datPsn_paytitlp" class="form-control" maxlength="22" size="22"  />
												</div>
												<div class="form-group">
													<label for="datPsn_paytitle" class="form-inline">投保金額:</label>
													<input type="text" id="datPsn_insuamt" class="form-control" maxlength="6" size="6"  />													
												</div>
												</div>	
											</td>
										</tr>
										<tr><td colspan=6>
											<div class="form-inline">
											<div class="form-group">
												<label for="datPsn_rankname" class="form-inline">階別:</label>
												<input type="text" id="datPsn_rankname" class="form-control" maxlength="8" size="8"  />
											</div>
											<div class="form-group">
												<label for="datPsn_payrnk2n" class="form-inline">職等:</label>
												<input type="text" id="datPsn_payrnk2n" class="form-control" maxlength="2" size="2"  />
											</div>
											<div class="form-group">
												<label for="datPsn_payrank3" class="form-inline">本功別</label>
												<input type="text" id="datPsn_payrank3" class="form-control" maxlength="1" size="1"  />
											</div>
											<div class="form-group">
												<label for="datPsn_paydegr2" class="form-inline">級別</label>
												<input type="text" id="datPsn_paydegr2" class="form-control" maxlength="2" size="2"  />
											</div>
											<div class="form-group">
												<label for="datPsn_paydegre" class="form-inline">俸點</label>
												<input type="text" id="datPsn_paydegre" class="form-control" maxlength="2" size="2"  />
											</div>
											<div id="div_contract" class="form-group d-none">
												<label for="datPsn_contract" class="form-inline">新/舊制(Y/N)</label>
												<input type="text" id="datPsn_contract" class="form-control" maxlength="1" size="1"  />
											</div>
											</div>
											</td>
										</tr>
										<tr><td colspan=6>
											<div class="form-inline d-none" id="div_authority">
												<div class="form-group">
													<label for="datPsn_payrank2" class="form-inline">權理:</label>
													<input type="text" id="datPsn_payrank2" class="form-control" maxlength="2" size="2"  />
												</div>
												<div class="form-group">
													<label for="datPsn_payrankc" class="form-inline">主管(權理):</label>
													<input type="text" id="datPsn_payrankc" class="form-control" maxlength="2" size="2"  />
												</div>
												<div class="form-group">
													<label for="datPsn_rnk4name" class="form-inline">適用專業加給表</label>
													<input type="text" id="datPsn_rnk4name" class="form-control" maxlength="15" size="15"  />
												</div>
											</div>
											</td>
										</tr>
										<tr><td colspan=6>
											<div class="form-inline cspf d-none">												
												<div class="form-group">
													<label for="datPsn_pacsflag" class="form-inline">工作獎金:</label>
													<input type="text" id="datPsn_pacsflag" class="form-control" maxlength="1" size="1"  />
													<label for="datPsn_pfkey" class="form-inline">實際支領KEY:</label>
													<input type="text" id="datPsn_pfkey" class="form-control" maxlength="7" size="7"  />
													<label for="datPsn_pfbas" class="form-inline">基本點數:</label>
													<input type="text" id="datPsn_pfbas" class="form-control" maxlength="3" size="3"  />
													<label for="datPsn_pfdat" class="form-inline">職等點數:</label>
													<input type="text" id="datPsn_pfdat" class="form-control" maxlength="3" size="3"  />
												</div>
											</div>
											</td>
										</tr>
										<tr><td colspan=6>
											<div class="form-inline cspf d-none">
												<div class="form-group">
													<label for="datPsn_paypfsp1" class="form-inline">額外點數:</label>
													<input type="text" id="datPsn_paypfsp1" class="form-control" maxlength="3" size="3"  />
													<input type="text" id="datPsn_paypfsp2" class="form-control" maxlength="3" size="3"  />
													<input type="text" id="datPsn_paypfsp3" class="form-control" maxlength="3" size="3"  />
													<label for="datPsn_paypfdoc" class="form-inline">專勤基本額度:</label>
													<input type="text" id="datPsn_paypfdoc" class="form-control" maxlength="8" size="8"  />
												</div>
											</div>
											</td>
										</tr>										
										<tr><td colspan=6>
											<div class="form-inline">
												<div class="form-group">
													<label for="datPsn_payupdid" class="form-inline">更改者:</label>
													<input type="text" id="datPsn_payupdid" class="form-control" maxlength="4" size="4"  />
													<label for="datPsn_updtm" class="form-inline">更改時間:</label>
													<input type="text" id="datPsn_updtm" class="form-control" maxlength="20" size="20"  />
												</div>
											</div>
											</td>
										</tr>
										<tr><td colspan=6>
											<div class="form-inline">
												<div class="form-group">
													<label for="datPay_taxpay" class="form-inline">應稅所得:</label>
													<input type="text" id="datPay_taxpay" class="form-control bg-warning text-dark" maxlength="7" size="7"  />
													<label for="datPay_netpay" class="form-inline">淨所得:</label>
													<input type="text" id="datPay_netpay" class="form-control bg-warning text-dark" maxlength="7" size="7"  />
													<label for="datPay_retire" class="form-inline">退撫/退休金:</label>
													<input type="text" id="datPay_retire" class="form-control bg-warning text-dark" maxlength="7" size="5"  />																										
												</div>
												<div id="div_thing" class="form-group d-none">
													<label for="datPay_thing_money" class="form-inline">實物代金:</label>
													<input type="text" id="datPay_thing_money" class="form-control text-info" maxlength="3" size="3"  />
													<label for="datPay_rent_house" class="form-inline">房租:</label>
													<input type="text" id="datPay_rent_house" class="form-control text-info" maxlength="3" size="3"  />
												</div>
												<div class="form-group">
													<div class="alert alert-info" role="alert">
													    <strong>2021/1</strong>退撫免納入所得稅
													</div>
												</div>
											</div>
											
											</td>
										</tr>
									</tbody>
								</table>
							</div>
							
							<div class="col-5 border border-dark">
								<!-- pa.padatpay清單 -->
								<table id="TblDatPaySum" class="table table-striped table-bordered table-hover table-sm" style="width="100%">
									<thead>
									</thead>
									<tfoot>
										<tr>
											<th colspan=3 style="text-align:right">小計</th>
											<th style="text-align:right"></th>
											<th style="text-align:right"></th>											
										</tr>
									</tfoot>
								</table>
							</div>
						</div>
					</div>
				</div>
				<div class="modal-footer">
				   <button type="button" class="btn btn-secondary" data-dismiss="modal">返回</button>
				</div>
			</div>
		</div>
	</div>
	
  	<!--檢驗-->
	<div class="tab-pane fade" id="byCheckMain" role="tabpanel" aria-labelledby="pills-check-tab">
		<div class="container">
			<div class="row">
				<div class="col-12">
					<a href="#explan_check" class="btn btn-danger" data-toggle="collapse">注意事項</a>
			  		<div id="explan_check" class="collapse">
			  			<div id="explan_3" class="text-danger">
			  				1.查詢:餉單編號,發薪日,擇一查詢,不用2個一起設;2個一起設,以發薪日為主,不會抓餉單編號.	<br>
			  				2.查看明細:點支扣代碼,藍色字體.		  			
				  		</div>
				  	</div>	
				</div>
			</div>
			<div class="row" id="qryCheckItem">
				<div class="col-10">
					<div class="form-inline">
		    			<div class="input-group">
		    				<%-- <select id="qry_check_salnum"  class="selectpicker SALNUM" data-width="fit" title="餉單編號">
		    					<option value=""></option>
								<c:forEach var="bitem" items="${alBankDtTbl}" varStatus="loop">
									<option value="${bitem.salnum}">${bitem.salnum}</option>
								</c:forEach>
							</select>
							<select id="qry_check_paybank" class="selectpicker PAYBANK" data-width="fit"  title="發薪日">
								<option value=""></option>
								<c:forEach var="bitem" items="${alBankDtTbl}" varStatus="loop">
									<option value="${bitem.paybank}">${bitem.paybank}</option>
								</c:forEach>
							</select> --%>
							<input type="text" id="qry_check_salnum" size="4" class="form-control" placeholder="餉單編號" >
							<input type="text" id="qry_check_paybank" size="10" class="form-control gclsCalTxt" placeholder="發薪日" maxlength="10">
		    				
						</div>		 
					</div>
				</div>
				<div class="col-2">
					<button id="btn_qryCheck" class="btn btn-info" type="button" ><i class="fa fa-search"></i>查詢</button>
					<button id="btn_clnCheck" class="btn btn-warning" type="button"><i class="fa fa-eraser"></i>清空</button>
				</div>
			</div>
			<div class="row">
				<div class="col-12 border border-dark">
					<table id="TblCheckPayList" class="table table-striped table-bordered table-hover" style="width="100%">
						<thead>
						</thead>
						<tfoot>
				            <tr>
				            	<th colspan=4 style="text-align:right"></th>
				                <th style="text-align:right"></th>
				                <th style="text-align:right"></th>
				            </tr>
				        </tfoot>
					</table>
				</div>
			</div>		
	 	</div>
	</div>
  
	<div class="modal fade"  id="paycodeListModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog" >
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">清單</h5>
	        		<button type="button" class="close" data-dismiss="modal" aria-label="Close">
	                    <span aria-hidden="true">×</span></button>
				 </div>
				<div class="modal-body">
					<div class="container-fluid">
						<!-- pa.padatpay清單 -->
						<table id="TblDatPayList" class="table table-striped table-bordered table-hover" style="width="100%">
							<thead>							
							</thead>
							<tfoot>
					            <tr>
					            	<th colspan=11 style="text-align:right"></th>
				                	<th colspan=2 style="text-align:right"></th>				            	
					            </tr>
					        </tfoot>
						</table>
								
					</div>
				</div>
				<div class="modal-footer">
				   <button type="button" class="btn btn-secondary" data-dismiss="modal">返回</button>
				</div>
			</div>
		</div>
	</div>
		
  	<!--餉單編號設定-->
	<div class="tab-pane fade" id="bySetSalnumMain" role="tabpanel" aria-labelledby="pills-setSalnum-tab">
		<div class="container-fluid">
			<div class="row">
				<div class="col-12">
					<a href="#explan_setSalNum" class="btn btn-danger" data-toggle="collapse">注意事項</a>
			  		<div id="explan_setSalNum" class="collapse">
			  			<div id="explan_1" class="text-danger">
			  				1.出現尚未上餉單編號的資料:選取[異動或批次]+[餉單編號=0]+[發薪日]→[查詢]<br>
							2.勾選要上餉單編號的項目→[新編號值]輸入餉單號碼→[執行]<br>
							3.查看:選取[異動或批次]→[餉單編號>0]或[發薪日]或[卡號]→[查詢]:出現依條件列出餉單號碼明細<br>
							4.餉單號碼還原為0:選取[異動或批次]→必要項【餉單編號】或[卡號]→[查詢]+名單勾選+[新編號值]=0→[執行]			  			
				  		</div>
				  	</div>	
				</div>
			</div>	
			<div class="row">
				<div class="col-12">
					<div class="form-inline">
						<div class="input-group">
							<span class="input-group-text">性質</span>
							<select id="prod_selCode" class="form-control" title="性質">
								<option value="1:異動資料:TranSetSalnum">1異動資料</option>
								<option value="2:批次資料:BatSetSalnum">2批次資料</option>
							</select>
						</div>	
						<div class="input-group">
							<input type="text" class="form-control" id="qry_set_salnum" size="4"placeholder="餉單編號">
							<input type="text" id="qry_set_patradt" size="10" class="form-control-sm gclsCalTxt" placeholder="發薪日期" >
							<input type="text" id="qry_set_Cardno" size="4" class="form-control text-uppercase" placeholder="卡號" >
							<button id="btn_qrySalNum" class="btn btn-info" type="button" ><i class="fa fa-search"></i>查詢</button>
						</div>
						<div class="input-group">
							<span class="input-group-text">新編號值</span>	
							<input type="text" class="form-control" id="prod_set_SalNum" size="4">
							<button id="btn_prodSalNum" class="btn btn-success" type="button"><i class="fa fa-cog"></i>執行</button>
						</div>	
						<button id="btn_clnSalNum" class="btn btn-warning ml-auto" type="button"><i class="fa fa-eraser"></i>清空</button>	
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-12 border border-dark">
					<table id="tblSalNumList" class="table table-striped table-bordered table-hover" style="width=100%">						
					</table>
				</div>
			</div>
				
		</div>
	</div>
	<!--[產生]發薪明細-->
	<div class="tab-pane fade" id="byProdDatMain" role="tabpanel" aria-labelledby="pills-prodDat-tab">
		<div class="container-fluid">
			<div class="row">
				<div class="col-12">
					<a href="#explan_setSalNum" class="btn btn-danger" data-toggle="collapse">注意事項</a>
			  		<div id="explan_setSalNum" class="collapse">
			  			<div id="explan_2" class="text-danger">
				  			1.發薪明細會先[刪除]原有資料,再重新[產生]。<br>				  			
				  		</div>
				  	</div>	
				</div>
			</div>
			<div class="row" >
				<div class="col-10">
					<div class="form-inline">
						<div class="input-group">
		    				<label for="prod_SalNum" >餉單編號-發薪日</label>
							<select id="prod_SalNum" class="form-control" title="餉單編號-發薪日">
								
							</select>
						</div>
						<div class="input-group">
							<span class="input-group-text">條件</span>	
							<select id="qry_prod_cond" class="form-control" title="條件">
								<option value="1:基本檔+異動檔+批次檔:Pay_1">1:基本檔+異動檔+批次檔</option>
			     				<option value="2:異動檔+批次檔:Pay_2">2:異動檔+批次檔</option>
								<option value="3:批次檔:Pay_3">3:批次檔</option>								
							</select>
						</div>		    															
					</div>
				</div>
				<div class="col-2">
					<button id="btn_prodPayroll" class="btn btn-success" type="button" ><i class="fa fa-cog"></i>執行</button>
					<button id="btn_clnProdDat" class="btn btn-warning" type="button"><i class="fa fa-eraser"></i>清空</button>
				</div>
			</div>			
		</div>
	</div>
	<!--[報表作業]-->
	<div class="tab-pane fade" id="byPordPrtMain" role="tabpanel" aria-labelledby="pills-prodPrt-tab">
		<div class="container-fluid">
			<div class="row" >
				<div class="col-10">
					<div class="form-inline">
						<div class="input-group">
		    				<label for="prt_SalNum" >餉單編號-發薪日</label>
							<select id="prt_SalNum" class="form-control" title="餉單編號-發薪日">
								
							</select>
						</div>
						<div class="input-group">
							<span class="input-group-text">條件</span>	
							<select id="qry_prt_cond" class="form-control" title="條件">
								<option value="1:月報:Prt_M" selected>1:月報</option>
								<option value="2:年報:Prt_Y">2:年報</option>						
							</select>
							<button id="btn_prodPrtSal" class="btn btn-success" type="button" ><i class="fa fa-cog"></i>報表檔</button>
						</div>
						<div class="input-group">
							<span class="input-group-text">銀行</span>	
							<select id="bank_cond" class="form-control" title="項目">
								<option value="1:媒體傳輸:Bank_T" selected>1:媒體傳輸</option>
								<option value="2" >2:比對</option>
							</select>
							<select id="bank_item" class="form-control d-none" title="項目">
								<option value="1:薪資:Bank_PayC" selected>1:薪資</option>
								<option value="2:公儲:Bank_PayS" >2:公儲</option>
							</select>	
							<input type="text" id="bank_ps" size="12" class="form-control d-none" placeholder="薪轉附言" >
							<div id="bankUpload" class="d-none">	
								<form id="uploadfileForm">檔案 	
								    <input type="file" accept=".csv,text/plain"  id="fileInput" name="fileInput">								   
							    </form>
							</div>    
							<button id="btn_prodBank" class="btn btn-success" type="button" ><i class="fa fa-cog"></i>產生</button>
						</div>
						     															
					</div>
				</div>
				<div class="col-2">					
					<button id="btn_qryProdPrt" class="btn btn-info" type="button" ><i class="fa fa-search"></i>查詢</button>
					<button id="btn_clnProdPrt" class="btn btn-warning" type="button"><i class="fa fa-eraser"></i>清空</button>
				</div>
			</div>
			<div class="row">
				<div class="col-12 border border-dark">
					<!-- pa.PAREPDETAIL清單 -->
					<table id="TblRepDtlList" class="table table-striped table-bordered table-hover" style="width="100%">
						<thead></thead>
						<tbody></tbody>						
					</table>
				</div>
			</div>
		</div>
	</div>
	<!--[報表列印]-->
	<div class="tab-pane fade" id="byPrintPrtMain" role="tabpanel" aria-labelledby="pills-printPrt-tab">
		<div class="container-fluid">
			<div class="row" >
				<div class="col-10">
					<div class="form-inline">
						<div class="input-group">
		    				<label for="print_SalNum" >餉單編號-發薪日</label>
							<select id="print_SalNum" class="form-control" title="餉單編號-發薪日">
								
							</select>
						</div>
						<div class="input-group">
							<span class="input-group-text">項目</span>	
							<select id="qry_prt_item" class="form-control" title="項目">
								<option value="1:薪津表:Prt_M_1" selected>1:薪津表</option>
								<option value="2:扣款表:Prt_M_2" >2:扣款表</option>
								<option value="3:薪津表(補發扣):Prt_M_3" >3:薪津表(補發扣)</option>
								<option value="4:薪津異動明細表:Prt_M_4" >4:薪津異動明細表</option>
								<option value="5:發薪受款者明細表:Prt_M_5" >5:發薪受款者明細表</option>
								<option value="6:銀行轉帳明細表:Prt_M_6" >6:銀行轉帳明細表</option>													
							</select>
							<button id="btn_printPrt" class="btn btn-success" type="button" ><i class="fa fa-cog"></i>執行</button>
						</div>    															
					</div>
				</div>
			</div>			
		</div>
	</div>
	<!--支扣明細查詢-->
  	<div class="tab-pane fade" id="byItmPayMain" role="tabpanel" aria-labelledby="pills-pay-tab">
		<div class="container-fluid">
			<div class="row" >
				<div class="col-10">
					<div class="form-inline">
		    			<div class="input-group">
		    				<select class="selectpicker paycodeSelect" id="qry_itmPay_Pay" name="qry_itmPay_Pay"  data-width="fit"  data-live-search="true" title="支扣代碼"></select>
		    				<input type="text" id="qry_itmPay_paybankBeg" class="gclsCalTxt form-control"  maxlength="10" size="10" placeholder="發薪日_起" />
		    				-<input type="text" id="qry_itmPay_paybankEnd" class="gclsCalTxt form-control"  maxlength="10" size="10" placeholder="發薪日_訖" />
		    				<input type="text" id="qry_itmPay_Cardno" size="4" class="form-control-sm text-uppercase" placeholder="卡號" >
							<input type="text" id="qry_itmPay_Idtno" size="10" class="form-control-sm text-uppercase" placeholder="身分證" >
							<input type="text" id="qry_itmPay_Namec" size="12" class="form-control-sm " placeholder="姓名" >
							<select id="qry_itmPay_Dept" name="qry_itmPay_Dept" class="selectpicker" data-width="fit" data-live-search="true" multiple title="單位"></select>
						</div>		    		
			    		
					</div>
				</div>
				<div class="col-2">
					<button id="btn_qryItmPay" class="btn btn-info" type="button" ><i class="fa fa-search"></i>查詢</button>
					<button id="btn_clnItmPay" class="btn btn-warning" type="button"><i class="fa fa-eraser"></i>清空</button>
				</div>
			</div>
				
			<div class="row">
				<div class="col-12 border border-dark">
					<!-- pa.padatpsn清單 -->
					<table id="tblItmPayList" class="table table-striped table-bordered table-hover" >
						<thead>							
						</thead>
					</table>
				</div>
			</div>
 		</div>	
	</div>
	<!-- 檔案產製 -->
	<div class="tab-pane fade" id="byFileProdMain" role="tabpanel" aria-labelledby="pills-fileProd-tab">
		<div class="row" >
			<div class="col-10">
				<div class="form-inline">
					<div class="input-group">
						<span class="input-group-text">項目</span>	
						<select id="file_cond" class="form-control-sm" title="條件">
							<option value="1:主計室_發放:BAS_1">1:主計室_發放</option>
							<option value="2:主計室_扣款:BAS_2">2:主計室_扣款</option>
						</select>
						
						<label for="file_SalNum" >餉單編號-發薪日</label>
						<select id="file_SalNum" class="form-control" title="餉單編號-發薪日">
							
						</select>
					</div>
				</div>
			</div>	
			<div class="col-2">
				<button id="btn_qryFile" class="btn btn-info" type="button" ><i class="fa fa-search"></i>查詢</button>									
			</div>	
		</div>
		<div class="row">
			<div class="col-md-12">
				<div id="explanProd_1" class="text-danger"></div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div id="codeIdDiv" class="codeDiv">
					<table id="tblBASPayList" class="table table-striped table-bordered table-hover" style="text-align:center;display:none;">
						<thead>						
						</thead>
					</table>
					<table id="tblBASDeductList" class="table table-striped table-bordered table-hover" style="text-align:center;display:none;">
						<thead>						
						</thead>
					</table>
				</div>
			</div>	
		</div>	 
	</div>	
</div>

</body>
</html>