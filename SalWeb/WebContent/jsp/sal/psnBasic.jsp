<!DOCTYPE HTML>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<html>
<head>
<title>薪資基本檔</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<jsp:include page="../include.jsp"></jsp:include>
<style></style> <!--改用include-->
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
var code_data;
var itmTbl;
var oTable;
var uTable;
var patradt;
var rowData;
$(function(){
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
	//依醫院角色,欄位show/hide或標題調整
	if (pahid=="2A0"){
		$('#label_acno').text('台新薪資帳號');
		$('#label_gedno').html('台新薪資公儲帳號');				
	}else if (pahid=="3A0"){
		$('#label_acno').html('玉山薪資帳號');
		$('#label_gedno').html('玉山優惠儲蓄帳號');		
	}else if (pahid=="1A0"){
		$('#label_acno').html('中國信託薪資帳號');
		$('#label_gedno').html('中國信託公儲帳號');
	}
	//總院才需列出
	if (pahid=='1A0' || pahid=='2A0'){
		$(".cspf").removeClass("d-none");
	}	

	//代碼選單切換
	$("#qryType").change(function(){
		$(":text,textarea").val("");
		$("#divfield_1,#divfield_2").addClass("d-none");	//隱藏
  		selCode=(typeof($("#qryType option:selected").val().split(':')[0]) == "undefined" )? "1":$("#qryType option:selected").val().split(':')[0];
  		console.log(selCode);
  		if (selCode=="1" ){			//1:[產生處理]異動及批次資料_匯入主檔
			$("#divfield_ID").removeClass("d-none");			
			$('#msgHintText').text('整批更新匯入主檔');
			$('#btn_handelINSUAMT').text('產生處理');
			$("#divfield_SALNUM").removeClass("d-none");
		}
		else if (selCode=="2"){		//2:[回復]異動及批次資料_回復主檔
			$("#divfield_CARD").removeClass("d-none");
			$('#msgHintText').text('回復');
			$('#btn_handelINSUAMT').text('回復');
			$("#divfield_SALNUM").removeClass("d-none");
		}
		else if (selCode=="3"){		//3:[匯入]投保金額:ImpBasINSUAMT
			$("#divfield_1,#divfield_2").removeClass("d-none");
			$('#msgHintText').text('請先[下載範例檔案]置於硬碟,可更名,修正後,再選擇檔案上傳匯入.');
			$('#btn_handelINSUAMT').text('執行');
			$("#divfield_SALNUM").addClass("d-none");
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
			type:"POST",		//type:POST, GET(default:GET)
			dataType:"json",	//dataType: text, xml, html, script, json, jsonp
			contentType: "application/x-www-form-urlencoded;charset=utf-8",
			success:function(data) {
				var options ="";
				var select;
				//資料賦值
				if (data.alBankDtTbl_3Y[0]!=null){
					select = $('#base_patradt,#filter_patradt');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alBankDtTbl_3Y, function(idx) {
						options = "<option value='" + data.alBankDtTbl_3Y[idx].paybank.dts + "'>" + data.alBankDtTbl_3Y[idx].paybank.dts ;
						select.append(options);
					});
					select.selectpicker('refresh');
				}	
			 	if (data.alRankCodeTbl[0]!=null){
					select = $('#base_payrank,#filter_payrank');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alRankCodeTbl, function(idx) {
						options = "<option value='" + data.alRankCodeTbl[idx].rankcode + "'>" + data.alRankCodeTbl[idx].rankcode+"-"+data.alRankCodeTbl[idx].rankname ;
						select.append(options);
					});
					select.selectpicker('refresh');
			 	}	

				if (data.alTitleCodeTbl[0]!=null){
					select = $('#base_title,#filter_title');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alTitleCodeTbl, function(idx) {
						options = "<option value='" + data.alTitleCodeTbl[idx].paytitlk + "'>" + data.alTitleCodeTbl[idx].patitsub+"-"+data.alTitleCodeTbl[idx].paytitlk+"-"+data.alTitleCodeTbl[idx].paytitlc ;
						select.append(options);
					});
					select.selectpicker('refresh');
					
				}
				if (data.alDeptCodeTbl[0]!=null){
					select = $('#base_dept,#filter_dept');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alDeptCodeTbl, function(idx) {
						options = "<option value='" + data.alDeptCodeTbl[idx].padepcod + "'>" + data.alDeptCodeTbl[idx].pacstno+"-"+data.alDeptCodeTbl[idx].padepnmc ;
						select.append(options);
					});
					select.selectpicker('refresh');
					
					select = $('#paydept');
					select.empty();
					options = "<option value=''>單位";
					select.append(options);
					$.each(data.alDeptCodeTbl, function(idx) {
						options = "<option value='" + data.alDeptCodeTbl[idx].padepcod + "'>" + data.alDeptCodeTbl[idx].pacstno+"-"+data.alDeptCodeTbl[idx].padepnmc ;
						select.append(options);
					});
					//select.selectpicker('refresh');
				}
				if (data.alexpfTbl[0]!=null){
					select = $('#filter_expfkey1,#filter_expfkey2,#filter_expfkey3,#payexpfkey1,#payexpfkey2,#payexpfkey3');
					select.empty();
					$.each(data.alexpfTbl, function(idx) {
						options = "<option value='" + data.alexpfTbl[idx].expfkey + "'>" + data.alexpfTbl[idx].expfname ;
						select.append(options);
					});
					select.selectpicker('refresh');
				}
				if (data.altracdTbl[0]!=null){
					select = $('#filter_patracd');
					select.empty();
					$.each(data.altracdTbl, function(idx) {
						options = "<option value='" + data.altracdTbl[idx].tracode + "'>" + data.altracdTbl[idx].traname ;
						select.append(options);
					});
					select.selectpicker('refresh');
				}
				/* 健保級距 */
 				if (data.alDpinTbl[0]!=null){
					select = $('#filter_insuamt');
					select.empty();
					options = "<option value='0'>0";
					select.append(options);
					$.each(data.alDpinTbl, function(idx) {
						options = "<option value='" + data.alDpinTbl[idx].dpinhigh + "'>" + data.alDpinTbl[idx].dpinhigh ;
						select.append(options);
					});
					select.selectpicker('refresh');			 	
			 	}
				//餉單編號(2個月內)
				if (data.alBankDtTbl_2M[0]!=null){
					//其他
					select = $('#prod_SalNum');
					select.empty();					
					options = "<option value=''>";
					select.append(options);
					$.each(data.alBankDtTbl_2M, function(idx) {
						options = "<option value='" + data.alBankDtTbl_2M[idx].salnum +":"+ data.alBankDtTbl_2M[idx].paybank.dts +"'>" + data.alBankDtTbl_2M[idx].paybank.dts+" "+data.alBankDtTbl_2M[idx].salnum ;
						select.append(options);
					});
				}
			}  
		});
	};
 	/*預先載入代碼檔 */
  	$.getLoadCodeTbl("psnBasic","Y");
 	
	//編輯欄位檢查
  	$.checkFields = function(btn){
  		msg = '';
  		
  		if (btn== 'btn_qry_Base_BasPsn'){	//基本資料_查詢
	 		if($("#base_card").val()=='' && $("#base_idno").val()=='' && $("#base_namec").val()=='' && $("#base_empno").val()=='' && $("#base_lvefg option:selected").val()=='' && $("#base_patradt option:selected").val()=='' && $("#base_payrank option:selected").val()==''){
	 			msg = '請輸入條件值!!';
	 		}	
 		}
 		else if (btn== 'subPABASPSN_Updbtn'){
 		
		}
		else if (btn== 'btn_qry_filter_BasPsn'){	//基本資料_篩選_查詢
	 		if($("#filter_card").val()=='' && $("#filter_idno").val()=='' && $("#filter_namec").val()=='' && $("#filter_empno").val()=='' && 
	 		   $("#expfkey1").val()==''&& $("#expfkey2").val()=='' && $("#expfkey3").val()=='' && $("#filter_title option:selected").val()=='' && 
	 		   $("#filter_dept option:selected").val()==''&& $("#filter_lvefg option:selected").val()=='' && $("#filter_patradt option:selected").val()=='' && 
	 		   $("#filter_payrank option:selected").val()==''&& $("#filter_pacsflag option:selected").val()==''&& $("#filter_expfsp option:selected").val()=='' && 
	 		   $("#filter_paret option:selected").val()=='' && $("#filter_patracd option:selected").val()=='' && $("#filter_inflg option:selected").val()==''){
	 			msg = '請輸入條件值!!';
	 		}	
 		}
		else if (btn== 'btn_handelINSUAMT'){ //匯入投保金額(整批更新)
			var file = $("#fileInput").val();			
			if (file.length == 0) {
				msg ='請確認是否有選擇檔案';				
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

		$( ':input,:checkbox', '#'+tblname).each(function() {
	        data_id = String($(this).attr("id"));
    		data_val = $(this).val();
    		
    		if (typeof data_id === "undefined" || data_id == "undefined") {
	        	
	        }else{
	        	if ($(this).is('[readonly]')){	//readonly不處理
	        	}
	        	else{
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
	        	}
    		}
	       	
	    });
	    
   		value.push(obj);   	//以整支table寫入	
   		array.push( { "formnm": tblname, "value": value } ); //將動態表格資料轉換為json		    
		    
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
	$(".btn").not("#lnkSampleFile").button().click(
		function(event) {event.preventDefault();}
	);
	//$(".btn").button().click(
	//	function(event) {event.preventDefault();}
	//);
	$(".btn").click(function(){
		var id = $(this).attr('id');
		
		if(id == 'btn_cln_Base_BasPsn' || id == 'btn_cln_filter_BasPsn'){
			$(":text,textarea").val("");
			$(":checkbox,:radio").prop("checked", false);
			
			//所有select預設選擇第一項			
 			//$('#qryCheckItem select').each(function() {
 				//$(this).prop("selectedIndex", 0);
 		    //});
		    $('.selectpicker').selectpicker('val', '');
			$('.selectpicker').selectpicker('refresh');
		    $('#base_lvefg,#filter_lvefg').selectpicker('val', 'N');
		    
		    //所有select預設選擇第一項
		    $('#PABASPSN select').each(function() {
				$(this).prop("selectedIndex", 0);
		    });
		    
		    $("#subPABASPSN_Updbtn").prop("disabled", true);
		    		    
		    $("#TblBasPsnList").dataTable().fnClearTable();
			$("#TblBasPayList").dataTable().fnClearTable();
			$("#TblPaHistList").dataTable().fnClearTable();
			$("#TblBasPsnFilter").dataTable().fnClearTable();
			
			return false;
		}
		else if(id == 'btn_qry_Base_BasPsn'){		//基本資料_查詢
			msg = $.checkFields();
			if(msg == ''){
				var cardno=$("#base_card").val().toUpperCase();
				var idtno=$("#base_idno").val().toUpperCase();
				var namec=$("#base_namec").val();
				var empno=$("#base_empno").val().toUpperCase();
				var lvefg=$("#base_lvefg option:selected").val();
				patradt=$.getSelectVals("base_patradt");
				var payrank=$.getSelectVals("base_payrank");
				var paytitle=$.getSelectVals("base_title");
				var paydept=$.getSelectVals("base_dept");
				
 				console.log("卡號:"+cardno+" 身分證:"+idtno+" 姓名:"+namec+" 職號:"+empno+" patradt:"+patradt+" payrank:"+payrank+" paytitle:"+paytitle+" paydept:"+paydept);
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
		        		paydept    :paydept
		        	};
				//console.log("基本資料查詢:code_data:"+code_data);
				
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'getBasPsnList',
						item    : '1',
						hid     : pahid,
						userid  : paUserid,
						code_data :JSON.stringify(code_data)
					},
					type:"POST",	    //type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						//更新查詢清單
						//$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
						$('#TblBasPsnList').show();
						oTable = $('#TblBasPsnList').dataTable(objCopyA);
						
						oTable.fnClearTable(0);		//清空資料
								
						if (data.basPsnList!=null && data.basPsnList.length>0){	
							oTable.fnAddData(data.basPsnList);//重新給資料
						}
						oTable.fnDraw();
						
						//移至第1筆
						$("#TblBasPsnList tbody tr:first").find("a").eq(0).trigger("click");
						
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
		else if(id == 'btn_qry_filter_BasPsn'){		//基本資料_篩選_查詢
			msg = $.checkFields();
			if(msg == ''){
				var cardno=$("#filter_card").val().toUpperCase();
				var idtno=$("#filter_idno").val().toUpperCase();
				var namec=$("#filter_namec").val();
				var empno=$("#filter_empno").val().toUpperCase();
				var lvefg=$("#filter_lvefg option:selected").val();
				var csflag=$("#filter_pacsflag option:selected").val();
				var expfsp=$("#filter_expfsp option:selected").val();
				var paret=$("#filter_paret option:selected").val();
				var tracod=$("#filter_patracd option:selected").val();
				var inflg=$("#filter_inflg option:selected").val();
				patradt=$.getSelectVals("filter_patradt");
				var payrank=$.getSelectVals("filter_payrank");
				var paytitle=$.getSelectVals("filter_title");
				var paydept=$.getSelectVals("filter_dept");
				var expfkey1=$.getSelectVals("filter_expfkey1");
// 				var expfkey2=$.getSelectVals("filter_expfkey2");
// 				var expfkey3=$.getSelectVals("filter_expfkey3");
				var insuamt=$.getSelectVals("filter_insuamt");
				
 				console.log("卡號:"+cardno+" 身分證:"+idtno+" 姓名:"+namec+" 職號:"+empno+" patradt:"+patradt+" payrank:"+payrank+" paytitle:"+paytitle+" paydept:"+paydept+" expfkey1:"+expfkey1+" 支領:"+csflag+" 點數:"+expfsp+" 專2:"+paret+" 異動原因:"+tracod+" 保險補助:"+inflg+" 保險級距:"+insuamt);

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
		        		tracod     :tracod,
		        		inflg      :inflg,
		        		insuamt    :insuamt
		        	};
				console.log("基本資料_篩選_查詢:code_data:"+code_data);
				
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'getBasPsnList',
						item    : '2',
						code_data :JSON.stringify(code_data),
						hid     : pahid,
						userid  : paUserid
					},
					type:"POST",	    //type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						//更新查詢清單
						//$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
						$('#TblBasPsnFilter').show();
						oTable = $('#TblBasPsnFilter').dataTable(objCopyD);
						
						oTable.fnClearTable(0);		//清空資料
						
						if (data.basPsnFilter!=null && data.basPsnFilter.length>0){	
							oTable.fnAddData(data.basPsnFilter);//重新給資料
						}
						oTable.fnDraw();
						
						var table = $('#TblBasPsnFilter').DataTable(); 
						// Hide columns
						if (pahid == "3A0")						
							table.columns( [26,27,28,29,30,31,32,33] ).visible( false );
						else if (pahid == "1A0" || pahid == "2A0")
							table.columns( [26,27,28,29,30,31,32,33] ).visible( true );
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
		else if (id == 'subPABASPSN_Updbtn'){	//基本檔更改
			if ($("#subPABASPSNForm").valid()){
				msg = $.checkFields(id);
				if(msg == ''){
					itmTbl="TblBasPsnList";
					tblname=id.substring(3,id.indexOf('_'));
					console.log("tblname:"+tblname);
					var jsonString = $.getDataInfo(tblname); //取得目前的資料
					
					var pk_data={
		        		payidno : $('#basPsn_payidno').val(),
		        		hid		:$('#basPsn_hid').val()
		        	};
					if (id.substring(id.indexOf('_')+1) =='Updbtn')		
						$('#method').val("update");
					//else if (id.substring(id.indexOf('_')+1) =='Insbtn')		
					//$('#method').val("insert");
					//return false;
					 $.ajax({
						url:"${pageContext.request.contextPath}/salController.do",
						data:{
							method	: "handelBas",
							fun	    : "updBasPsn",
							tblname : tblname,
							pk_data : JSON.stringify(pk_data),
							editMode : $('#method').val(),
							tabJson : jsonString,
							hid     : pahid,
							userid  : paUserid
						},
						type:"POST",			//type:POST, GET(default:GET)
						dataType:"json",	    //dataType: text, xml, html, script, json, jsonp
						async: true,	        //逐步執行
						contentType: "application/x-www-form-urlencoded;charset=utf-8",  
						success:function(data) {
							if (data.msg!=null){
								$("#btn_cln_Base_BasPsn").trigger( "click" ); 
								$("#base_idno").val(pk_data.payidno);
								
								$("#btn_qry_Base_BasPsn").trigger( "click" );  
								alert(data.msg);
							}	
						},
						error: function(xhr){					
							alert("更新[薪資基本檔]失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
						}
					}); 
						
				}else{
					alert(msg);
					return false;
				}
		 	}
		}	
		else if (id == 'btn_handelINSUAMT'){ //匯入投保金額(整批更新)
			msg = $.checkFields(id);
			if(msg == ''){
				selCode=(typeof($("#qryType option:selected").val().split(':')[0]) == "undefined" )? "3":$("#qryType option:selected").val().split(':')[0];
	  	  		console.log("執行："+selCode);
				
	  	  		var url="${pageContext.request.contextPath}/salController.do";
	  	  		var param = '?method=uploadINSUAMTExcelFile' +
							'&selCode=' + selCode +
							'&userid=' + userid +
							'&hid=' + pahid;
	    		console.log(" 選項:"+selCode+" url:"+url+param);
	    		
	    		showPageLoading(); //顯示loading(本頁專屬)
			 	$("#uploadfileForm").ajaxSubmit({
					type: "post", //提交方式 
					dataType:"json", //資料型別 
					url: url + param, //請求url 
					success: function (data) { //提交成功的回撥函式 
						alert(data.msg);
						hidePageLoading();
					},
					error: function(xhr){
						alert("檔案上傳過程中，程式發生錯誤!!!\nerror code:"+ xhr.status);
						hidePageLoading();
					},
					complete: function(data) {
						hidePageLoading(); //隱藏loading
					}
				});
			}else{
				alert(msg);
			}
			
		}		
	});
	
	//個人明細
 	var objCopyA = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyA.aoColumns = [	
							{ "sTitle":"身分證","mData": "payidno" },
							{ "sTitle":"卡號","mData": "paycard" },
							{ "sTitle":"姓名","mData": "paynamec" },
							{ "sTitle":"單位","mData": "pacstno" }
						 ];
	
	objCopyA = $.extend(true,objCopyA, {
				 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
						$('td:eq(0)', nRow).html( '<a href = "#baspsnData" class= "baspsnData">'+aData.payidno+'</a>' );
				 }						
		      });
	
					    
	objCopyA = $.extend(true,objCopyA, {
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr form-inline"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br form-inline"ip>',
		buttons: [
           'excelHtml5' 
        ]
	});
	$('#TblBasPsnList').dataTable(objCopyA);
	
	//顯示pa.pabaspsn明細
	$('#TblBasPsnList tbody').on('click', '.baspsnData', function () {
		oTable = $('#TblBasPsnList').dataTable().api();
		info = oTable.page.info();		//記錄目前頁碼
		currentPage=info.page;
        var tr = $(this).parents('tr');
        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data	 		
		var idtno=rowData.payidno;
		var hid=rowData.hid;
		
		//找pa.pabaspsn及pa.pabaspay
		in_data="";
		in_data={
        		idtno      :idtno,
        		hid        :hid
        	};
		console.log("基本資料pabaspsn查詢:in_data:"+JSON.stringify(in_data));
		
		$.getBasPsnByKey(in_data);
		
	});
	
	//依KEY找出pa.pabaspsn
	$.getBasPsnByKey = function(in_data){
		$.ajax({
			url:"${pageContext.request.contextPath}/salController.do",
			data:{
				method  : 'getBasPsnByKey',
				in_data :JSON.stringify(in_data),
        		hid     : pahid,
				userid  : paUserid,
			},
			type:"POST",			//type:POST, GET(default:GET)
			dataType:"json",	//dataType: text, xml, html, script, json, jsonp
			contentType: "application/x-www-form-urlencoded;charset=utf-8",
			success:function(data) {
				if (data.basPsnItem!=null){
					//顯示基本個人明細
					$.doBaspsnDetail(data.basPsnItem);
					//pa.paBaspay基本支扣明細
					$.doBaspayDetail(data.basPayList);
					//pa.paHist歷史資料
					$.doHistDetail(data.histList);
				}
			}  
		}) 
	};
	//[實際支領KEY]對應
	$(".calCSPF").change(function(){
		var mrnk2n="";
		var mrank="";
		var mrank3="";
		var mdegr2="";
		var mjt="";
		//var mtradt=$("#basPsn_patradt").val();
		var mtradt="9999-12-31";
		var mcsflag = $("#pacsflag").val();//工作獎金
		$("#basPsn_pfkey").val("");
		$("#basPsn_pfbas").val("");
		$("#basPsn_pfdat").val("");
		if (mcsflag=="Y"){
			mrnk2n = $("#basPsn_payrnk2n").val();
			mrank  = $("#basPsn_payrank").val();
			mrank3 = $("#basPsn_payrank3").val();
			mdegr2 = $("#basPsn_paydegr2").val();
			mjt    = $("#basPsn_paytitle").val().split("-")[1];
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
					$("#basPsn_pfkey").val(data.cspfData.pfkey);
					$("#basPsn_pfbas").val(data.cspfData.pfbas);
					$("#basPsn_pfdat").val(data.cspfData.pfdat);
					if (data.cspfData.pfbas==""){
						$("#basPsn_pfbas").addClass('bg-danger');
						$("#basPsn_pfdat").addClass('bg-danger');
					}else{
						$("#basPsn_pfbas").removeClass('bg-danger');
						$("#basPsn_pfdat").removeClass('bg-danger');
					}
				}
			},
			error: function(xhr){	
				alert("搜尋[實際支領KEY]過程中，程式發生錯誤!!!\nerror code:"+ xhr.status);
			}
		});
	};
	//工作獎金Y,show pfkey 工作獎金N show pfdoc
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
	//顯示pa.pabaspsn明細
	$.doBaspsnDetail = function(mData){
		$("#basPsn_paylvefg").val(mData.paylvefg);
		$("#basPsn_payidno").val(mData.payidno);
		$("#basPsn_hid").val(mData.hid);
		$("#basPsn_paycard").val(mData.paycard);
		$("#payempno").val(mData.payempno);
		$("#paynamec").val(mData.paynamec);
		$("#paydept").val(mData.paydept);
		//$('#basPsn_paydept').selectpicker('val', mData.paydept);
		$("#basPsn_padepnmc").val(mData.padepnmc);
		$("#basPsn_paygovdt").val(mData.paygovdt.dts);
		$("#basPsn_payempdt").val(mData.payempdt.dts);
		$("#paysgndt").val(mData.paysgndt.dts);
		$("#basPsn_payrsgdt").val(replaceDefaultDate(mData.payrsgdt.dts));
		$("#basPsn_paybkid").val(mData.paybkid);
		$("#basPsn_payacno").val(mData.payacno);
		$("#basPsn_paygedno").val(mData.paygedno);
		$("#basPsn_payinflg").val(mData.payinflg);
		$("#basPsn_paytitle").val(mData.patitsub+"-"+mData.paytitle);
		$("#basPsn_paytitlp").val(mData.paytitlp);
		$("#basPsn_payrank").val(mData.payrank);
		$("#basPsn_payrnk2n").val(mData.payrnk2n);
		$("#basPsn_payrank3").val(mData.rank3name);
		$("#basPsn_paydegr2").val(mData.paydegr2);
		$("#basPsn_paydegre").val(mData.paydegre);
		$("#basPsn_rankname").val(mData.rankname);
		$("#basPsn_payrank2").val(mData.payrank2);
		$("#basPsn_payrankc").val(mData.payrankc);
		$("#paypfdoc").val(mData.paypfdoc);
		$("#pacsflag").val(mData.pacsflag);
		$("#paypfsp1").val(mData.paypfsp1);
		$("#paypfsp2").val(mData.paypfsp2);
		$("#paypfsp3").val(mData.paypfsp3);
		$('#payexpfkey1').selectpicker('val', mData.payexpfkey1);
		$('#payexpfkey2').selectpicker('val', mData.payexpfkey2);
		$('#payexpfkey3').selectpicker('val', mData.payexpfkey3);
		$("#basPsn_patracod").val(mData.patracod);
		$("#basPsn_traname").val(mData.traname);
		$("#basPsn_patradt").val(mData.patradt.dts);
		$("#basPsn_traname").val(mData.traname);
		$("#basPsn_traname").val(mData.traname);
		$("#basPsn_payinflg").val(mData.payinflg);
		$("#basPsn_inflgnm").val(mData.inflgnm);
		$("#insuamt").val(mData.insuamt);
		$("#paytaxno").val(mData.paytaxno);
		$("#paydpno").val(mData.paydpno);
		$("#paretention").val(mData.paretention);
		
		$("#payaddrs").val(mData.payaddrs);
		$("#payctcbacno").val(mData.payctcbacno);
		$("#payctcbgedno").val(mData.payctcbgedno);
		
		$("#basPsn_rnk4name").val(mData.rnk4name);
		
		$("#basPsn_payupdid").val(mData.payupdid);
		$("#basPsn_updtm").val(mData.updtm);
		$("#basPsn_pfkey").val(mData.pfkey);
		$("#basPsn_pfbas").val(mData.pfbas);
		$("#basPsn_pfdat").val(mData.pfdat);
		$("#basPsn_contract").val(mData.contract);
		
		//工作獎金Y,show pfkey 工作獎金N show pfdoc
		$("#div_pfkey").addClass("d-none");
		$("#div_pfdoc").addClass("d-none");
		if (mData.pacsflag=='Y'){
			$("#div_pfkey").removeClass("d-none");
		}	
		else if (mData.pacsflag=='N'){	
			$("#div_pfdoc").removeClass("d-none");
		}
		//契約,不含醫師,才有新舊制(Y/N)
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
			
		//1A0 2A0列出工獎
		if (mData.hid=='1A0' || mData.hid=='2A0'){
			$(".cspf").removeClass("d-none");
		}else if (mData.pacsflag=='N'){
			$(".cspf").addClass("d-none");
		}
		
		$("#subPABASPSN_Updbtn").prop("disabled", false);
	};
	
	//顯示pa.paBaspay個人支扣明細
	$.doBaspayDetail = function(mData){
		$('#TblBasPayList').show();
		oTable = $('#TblBasPayList').dataTable(objCopyB);
		
		oTable.fnClearTable(0);		//清空資料
				
		if (mData!=null && mData.length>0){	
			oTable.fnAddData(mData);//重新給資料
		}
		oTable.fnDraw();
	};
	
	//顯示pa.paHist個人歷史檔
	$.doHistDetail = function(mData){
		$('#TblPaHistList').show();
		oTable = $('#TblPaHistList').dataTable(objCopyC);
		
		oTable.fnClearTable(0);		//清空資料
				
		if (mData!=null && mData.length>0){	
			oTable.fnAddData(mData);//重新給資料
		}
		oTable.fnDraw();
	};
	
	
	//支扣明細
 	var objCopyB = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyB.aoColumns = [	
							{ "mData": null},
							{ "mData": null},
							{ "mData": "pacodnmc" },
							{ "mData": "payamtp" },
							{ "mData": "payamtd" }
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
							   'orderable':false,
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
	$('#TblBasPayList').dataTable(objCopyB);
	
	//歷史資料
 	var objCopyC = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyC.aoColumns = [	
							{ "mData": "traname"},
							{ "mData": "patradt"},
							{ "mData": "payrank" },
							{ "mData": "payrnk2n" },
							{ "mData": "payrank3" },
							{ "mData": "paydegr2" },
							{ "mData": "paydegre" },
							{ "mData": "payrank2" },
							{ "mData": "paycard" },
							{ "mData": "payempno" },
							{ "mData": "paytitlp" },
							{ "mData": "payempdt" },
							{ "mData": "payrsgdt" },
							{ "mData": "hid" }
						 ];
						 
	objCopyC = $.extend(true,objCopyC, {
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr form-inline"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br form-inline"ip>',
		buttons: [
           'excelHtml5' 
        ]
	}); 
	
	//篩選
 	var objCopyD = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyD.aoColumns = [	
							{ "sTitle":"離職","mData": "paylvefg" },
							{ "sTitle":"卡號","mData": "paycard" },
							{ "sTitle":"職號","mData": "payempno" },
							{ "sTitle":"身分證","mData": "payidno" },
							{ "sTitle":"姓名","mData": "paynamec" },
							{ "sTitle":"單位","mData": "padepnmc" },
							{ "sTitle":"職稱","mData": "paytitlp" },
							{ "sTitle":"階別","mData": "rankname" },
							{ "sTitle":"職等","mData": "payrnk2n" },
							{ "sTitle":"本功","mData": "rank3name" },
							{ "sTitle":"級別","mData": "paydegr2" },
							{ "sTitle":"俸點","mData": "paydegre" },
							{ "sTitle":"權理","mData": "payrank2" },
							{ "sTitle":"主管權理","mData": "payrankc" },
							{ "sTitle":"比照","mData": "rnk4name" },
							{ "sTitle":"新/舊","mData": "contract" },
							{ "sTitle":"公職日","mData": "paygovdt" },
							{ "sTitle":"到院日","mData": "payempdt" },
							{ "sTitle":"現職日","mData": "paysgndt" },
							{ "sTitle":"離職日","mData": "payrsgdt" },
							{ "sTitle":"異動日","mData": "patradt" },
							{ "sTitle":"異動項目","mData": "traname" },
							{ "sTitle":"健保數","mData": "paydpno" },
							{ "sTitle":"扶養數","mData": "paytaxno" },
							{ "sTitle":"保險補助","mData": "payinflg" },
							{ "sTitle":"投保金額","mData": "insuamt" },
							{ "sTitle":"支領","mData": "pacsflag" },
							{ "sTitle":"基本績分","mData": "pfbas" },
							{ "sTitle":"俸級績分","mData": "pfdat" },
							{ "sTitle":"額外1","mData": "paypfsp1" },
							{ "sTitle":"額外2","mData": "paypfsp2" },
							{ "sTitle":"額外3","mData": "paypfsp3" },
							{ "sTitle":"專勤額度","mData": "paypfdoc" },
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
	$('#TblBasPsnFilter').dataTable(objCopyD);
	 		      
});
//開啟loading dialog
function showPageLoading(){
	$("#pageLoadingOverlay").css("display", "flex");
}

//關閉loading dialog
function hidePageLoading(){
	$("#pageLoadingOverlay").css("display", "none");
}
</script>
</head>
<body>

<ul class="nav nav-pills mb-3" id="pills-tab" role="tab">
  <li class="nav-item">
    <a class="nav-link active" id="pills-basic-tab" data-toggle="pill" href="#byPsnBaseDetailMain" role="tab" aria-controls="pills-home" aria-selected="true">基本資料</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-query-tab" data-toggle="pill" href="#byPsnBaseFilterMain" role="tab" aria-controls="pills-profile" aria-selected="false">篩選</a>
  </li>
  <li class="nav-item">
	<a class="nav-link" id="pills-handing-tab" data-toggle="pill" href="#byPsnBaseHandingMain" role="tab" aria-controls="pills-profile" aria-selected="false">基本處理</a>
  </li>
</ul>

<div class="tab-content" id="pills-tabContent">
	<!--基本資料-->
  	<div class="tab-pane fade show active" id="byPsnBaseDetailMain" role="tabpanel" aria-labelledby="pills-basic-tab">
		<div class="container-fluid">
			<div class="row" id="baseItem">
				<div class="col-10">
					<div class="form-inline basother-filterbar">
		    			<div class="d-flex flex-wrap align-items-end basother-filter-group">
							<div class="filter-item">
								<input type="text" id="base_card" class="form-control form-control-sm text-uppercase" placeholder="卡號" maxlength="4" size="4">
							</div>
							<div class="filter-item">
								<input type="text" id="base_idno" class="form-control form-control-sm text-uppercase" placeholder="身分證" maxlength="10" size="10">
							</div>
							<div class="filter-item">
								<input type="text" id=base_namec class="form-control form-control-sm" placeholder="姓名" maxlength="12" size="12">
							</div>
							<div class="filter-item">
								<input type="text" id="base_empno" class="form-control form-control-sm text-uppercase" placeholder="職號" maxlength="7" size="7">
							</div>							
							<div class="filter-item">							
								<select id="base_lvefg" class="selectpicker" data-width="100%" title="離職" >
									<option value=""></option>
									<option value="N" selected>N</option>
									<option value="Y">Y</option>
								</select>
							</div>
							<div class="filter-item filter-item-wide">							
								<select id="base_dept" name="base_dept" class="selectpicker PAYDEPT" data-width="100%" data-live-search="true" multiple title="單位">
									<option value=""></option>
									<c:forEach var="uitem" items="${alDeptCodeTbl}" varStatus="loop">
										<option value="${uitem.padepcod}">${uitem.pacstno}-${uitem.padepnmc}</option>
									</c:forEach>
								</select>
							</div>
							<div class="filter-item filter-item-wide">							
								<select id="base_title" name="base_title" class="selectpicker JTCODE" data-width="100%" data-live-search="true" multiple title="職稱">
									<option value=""></option>
									<c:forEach var="uitem" items="${alTitleCodeTbl}" varStatus="loop">
										<option value="${uitem.paytitlk}">${uitem.patitsub}-${uitem.paytitlk}-${uitem.paytitlc}</option>
									</c:forEach>
								</select>
							</div>
							<div class="filter-item">							
								<select id="base_payrank" class="selectpicker RANKCODE" data-width="100%" data-live-search="true" multiple title="階級">
									<option value=""></option>
									<c:forEach var="uitem" items="${alRankCodeTbl}" varStatus="loop">
										<option value="${uitem.rankcode}">${uitem.rankcode}-${uitem.rankname}</option>
									</c:forEach>
								</select>
							</div>
							<div class="filter-item">							
								<select id="base_patradt" class="selectpicker PATRADT" data-width="100%" data-live-search="true" multiple title="異動日">
									
								</select>
							</div>
						</div>
					</div>
				</div>
				<div class="col-2">
					<button id="btn_qry_Base_BasPsn" class="btn btn-info" type="button" ><i class="fa fa-search"></i>查詢</button>
					<button id="btn_cln_Base_BasPsn" class="btn btn-warning" type="button"><i class="fa fa-eraser"></i>清空</button>
				</div>
			</div>	
			<div class="row basother-detail-row">
				<div class="col-4">
					<div class="baspsnListDiv-wrap">
						<!--  -->
						<div id="baspsnListDiv_1" class="baspsnListDiv">
							<!-- pa.pabaspsn清單 -->
							<table id="TblBasPsnList" class="table table-striped table-bordered table-hover" >
								<thead>
								</thead>
							</table>				
						</div>
					</div>					
				</div>
				
 				<div class="col-8">
 					<div class="container-fluid px-0">
 					<div id="accordion">
					    <div class="card">
					      <div class="card-header basother-card-header">
					      	<a class="card-link" data-toggle="collapse" href="#collapseOne">基本資料</a>
					        <button type="button" class="btn btn-primary roleQry basother-fixed-btn basother-align-under-qry" id="subPABASPSN_Updbtn" disabled>更改</button>
					      </div>
					      <div id="collapseOne" class="collapse show" data-parent="#accordion">
					        <div class="card-body">
					        	<!-- pa.pabaspsn明細 -->
		 						<div class="table-responsive-sm">
		 							<form id="subPABASPSNForm">	 
		 							<INPUT type="hidden" id ="method"  value="">
		 							<INPUT type="hidden" id ="basPsn_hid"  value="">
				 					<table id="PABASPSN" class="table table-striped table-bordered table-hover table-sm table-condensed" >
				 						<tbody>
											<tr>
												<td>
													<div class="basother-panel">
														<div class="form-row form-row--equal">													
															<div class="form-group">
																<label for="basPsn_paycard" >卡號</label>
																<input type="text" id="basPsn_paycard" readonly class="text-uppercase form-control" maxlength="4" size="4"  />
															</div>
															<div class="form-group">
																<label for="paynamec" class="table-warning">姓名</label>
																<input type="text" id="paynamec" class="form-control required" maxlength="12" size="12"  />
															</div>
															<div class="form-group">
																<label for="payempno" class="table-warning required">職號</label>
																<input type="text" id="payempno" class="text-uppercase form-control" maxlength="7" size="7"  />
															</div>															
														</div>
													</div>
												
													<div class="basother-panel">
														<div class="form-row form-row--equal">	
															<div class="form-group">
																<label for="basPsn_payidno" >身份證字號</label>
																<input type="text" id="basPsn_payidno" readonly class="text-uppercase form-control" maxlength="10" size="10"  />
															</div>
															<div class="form-group">
																<label for="basPsn_paygovdt" >初任公職日</label>
																<input type="text" id="basPsn_paygovdt" readonly class="form-control"  maxlength="10" size="10"  />
															</div>
															<div class="form-group">
																<label for="basPsn_payempdt" >到院日</label>
																<input type="text" id="basPsn_payempdt" readonly class="form-control"  maxlength="10" size="10"  />
															</div>
														</div>
													</div>
											
													<div class="basother-panel">
														<div class="form-row form-row--equal">
															<div class="form-group">
																<label for="paysgndt" class="table-warning">現職日</label>
																<input type="text" id="paysgndt" class="gclsCalTxt form-control required"  maxlength="10" size="10"  />
															</div>
															<div class="form-group">
																<label for="basPsn_payrsgdt" >離職日</label>
																<input type="text" id="basPsn_payrsgdt" readonly class="form-control"  maxlength="10" size="10"  />
															</div>
															<div class="form-group">
																<label for="basPsn_patradt" >異動日</label>
																<input type="text" id="basPsn_patradt" readonly class="form-control"  maxlength="10" size="10"  />
																<input type="text" id="basPsn_traname" readonly class="form-control"  maxlength="8" size="8"  />
															</div>
														</div>
													</div>
											
													<div class="basother-panel">
														<div class="form-row form-row--equal">		
															<div class="form-group">
																<label for="paytaxno" class="table-warning">扶養人數</label>
																<input type="text" id="paytaxno" class="form-control" maxlength="2" size="2"  />
															</div>
															<div class="form-group">
																<label for="paydpno" class="table-warning">健保人數</label>
																<input type="text" id="paydpno" class="form-control" maxlength="7" size="7"  />
															</div>
															<div class="form-group">
																<label for="insuamt" class="table-warning">投保金額</label>
																<input type="text" id="insuamt" class="form-control" maxlength="6" size="6"  />
															</div>
															
														</div>
													</div>
													<div class="basother-panel">
														<div class="form-row form-row--equal">
															<div class="form-group">
																<label for="basPsn_inflgnm" >保險費比率</label>
																<input type="text" id="basPsn_inflgnm" readonly class="form-control" maxlength="13" size="13"  />
															</div>
															<div class="form-group">
																<label for="basPsn_paylvefg" >離職</label>
																<input type="text" id="basPsn_paylvefg" readonly class="form-control"  maxlength="1" size="1"  />
															</div>
														</div>
													</div>
													<div class="basother-panel">
														<div class="form-row form-row--equal">				
															<div class="form-group">
																<label for="paydept" class="table-warning">單位</label>
																<select id="paydept" name="paydept" class="form-control" data-width="fit">
																</select>
															</div>
															<div class="form-group">
																<label for="basPsn_paytitle" >職稱</label>
																<input type="text" id="basPsn_paytitle" readonly class="form-control" maxlength="4" size="4"  />
																<input type="text" id="basPsn_paytitlp" readonly class="form-control" maxlength="22" size="22"  />
															</div>
														</div>
													</div>
										
													<div class="basother-panel">
														<div class="form-row form-row--equal">
															<div class="form-group">
																<label for="basPsn_rankname">階別</label>
																<INPUT type="hidden" id ="basPsn_payrank"  value="">
																<input type="text" id="basPsn_rankname" readonly  class="form-control" maxlength="8" size="8"  />
															</div>
															<div class="form-group">
																<label for="basPsn_payrnk2n" >職等</label>
																<input type="text" id="basPsn_payrnk2n" readonly class="form-control" maxlength="2" size="2"  />
															</div>
															<div class="form-group">
																<label for="basPsn_payrank3" >本功別</label>
																<input type="text" id="basPsn_payrank3" readonly class="form-control" maxlength="1" size="1"  />
															</div>
															<div class="form-group">
																<label for="basPsn_paydegr2" >級別</label>
																<input type="text" id="basPsn_paydegr2" readonly class="form-control" maxlength="2" size="2"  />
															</div>
															<div class="form-group">
																<label for="basPsn_paydegre" >俸點</label>
																<input type="text" id="basPsn_paydegre" readonly class="form-control" maxlength="2" size="2"  />
															</div>
															<div id="div_contract" class="form-group d-none">
																<label for="basPsn_contract" >新Y/舊N</label>
																<input type="text" id="basPsn_contract" readonly class="form-control" maxlength="1" size="1"  />
															</div>
														</div>
													</div>
												</td>
											</tr>
											<tr>
												<td>
												<div class="basother-panel d-none" id="div_authority">
													<div class="form-row form-row--equal">												
														<div class="form-group">
															<label for="basPsn_payrank2" >權理</label>
															<input type="text" id="basPsn_payrank2" readonly class="form-control" maxlength="2" size="2"  />
														</div>
														<div class="form-group">
															<label for="basPsn_payrankc" >主管(權理)</label>
															<input type="text" id="basPsn_payrankc" readonly class="form-control" maxlength="2" size="2"  />
														</div>
														<div class="form-group">
															<label for="basPsn_rnk4name" >適用專業加給表</label>
															<input type="text" id="basPsn_rnk4name" readonly class="form-control" maxlength="15" size="15"  />
														</div>
													</div>
												</div>
												</td>
											</tr>
											<tr>
												<td>
												<div class="cspf d-none">
													<div class="basother-panel">
														<div class="form-row">
															<div class="form-group">
																<label for="pacsflag" class="table-warning">工作獎金</label>
																<select id="pacsflag" name="pacsflag" class="form-control calCSPF" >
																	<option value="">請選擇</option>
																	<option value="Y">Y</option>
											     					<option value="N">N</option>
																</select>
															</div>	
															<div class="form-group" id="div_pfkey">	
																<label for="basPsn_pfkey" readonly >實際支領KEY</label>
																<input type="text" id="basPsn_pfkey" readonly class="form-control" maxlength="7" size="7"  />
																<label for="basPsn_pfbas" >基本點數</label>
																<input type="text" id="basPsn_pfbas" readonly class="form-control" maxlength="3" size="3"  />
																<label for="basPsn_pfdat" >職等點數</label>
																<input type="text" id="basPsn_pfdat" readonly class="form-control" maxlength="3" size="3"  />
															</div>
														</div>
													</div>													
												</div>
												<td>
											</tr>
											<tr>
												<td>
												<div class="cspf d-none">
													<div class="basother-panel">
														<div class="form-row">
															<div class="form-group ">
																<label for="paypfsp1" class="table-warning">額外點數</label>															
																<select id="payexpfkey1"  class="selectpicker" data-width="fit" title="額外1">																
																</select>
																<input type="text" id="paypfsp1" class="form-control" maxlength="3" size="3"  />
																<select id="payexpfkey2"  class="selectpicker" data-width="fit" title="額外2">																
																</select>
																<input type="text" id="paypfsp2" class="form-control" maxlength="3" size="3"  />
																<select id="payexpfkey3"  class="selectpicker" data-width="fit" title="額外3">																
																</select>
																<input type="text" id="paypfsp3" class="form-control" maxlength="3" size="3"  />														
															</div>
														</div>
													</div>	
													<div class="basother-panel">
											  			<div class="form-row">
															<div class="form-group" id="div_pfdoc">
																<label for="paypfdoc" class="table-warning">專勤基本額度</label>
																<input type="text" id="paypfdoc" class="form-control" maxlength="8" size="8"  />
																&emsp;
																<label for="paretention" class="table-warning">專2/工2</label>
<!--		 													<input type="text" id="paretention" name="paretention" class="form-control text-uppercase" maxlength="1" size="1"  /> -->
																<select id="paretention"  name="paretention"  title="專2/工2">
																	<option value="">專1(Y)專2(Y)</option>
																	<option value="2">專1(Y)專2(N)</option>
																	<option value="3">專1(N)專2(Y)</option>
																	<option value="4">專1(N)專2(N)</option>
																	<option value="Y">Y</option>
																	<option value="N">N</option>																
																</select>
															</div>
														</div>
													</div>
												</div>
												</td>
											</tr>															
											<tr>
												<td>
													<a href="#basother" class="btn btn-info" data-toggle="collapse">其他</a>
											  		<div id="basother" class="collapse">
											  			<div class="basother-panel">
											  				<div class="form-row">
											  					<div class="form-group">
																	<label for="payctcbacno" class="table-warning" id="label_acno"></label>
																	<input type="text" id="payctcbacno" class="form-control" maxlength="14" size="14"  />
																</div>
																<div class="form-group">
																	<label for="payctcbgedno" class="table-warning" id="label_gedno"></label>
																	<input type="text" id="payctcbgedno" class="form-control" maxlength="14" size="14"  />
																</div>	
											  				</div>
												  			<div class="form-row">
												  				<div class="form-group">
																	<label for="payaddrs" class="table-warning">戶籍地址</label>
																	<input type="text" id="payaddrs" class="form-control" maxlength="62" size="62"  />													
																</div>
												  			</div>
												  			<div class="form-row">
												  				<div class="form-group">
																	<label for="basPsn_payupdid" >更改者</label>
																	<input type="text" id="basPsn_payupdid" readonly class="form-control" maxlength="4" size="12"  />
																</div>
																<div class="form-group">
																	<label for="basPsn_updtm" >更改時間</label>
																	<input type="text" id="basPsn_updtm" readonly class="form-control" maxlength="20" size="20"  />																
																</div>
												  			</div>
														
														</div>	
											  		</div>										
											  </td>
											</tr>
											
										</tbody>
				 					</table>
				 					</form>
		 						</div> 
					        </div>
					      </div>
					    </div>
					    <div class="card">
					      <div class="card-header">
					        <a class="collapsed card-link" data-toggle="collapse" href="#collapseTwo">
					        	支扣明細
					      	</a>
					      </div>
					      <div id="collapseTwo" class="collapse" data-parent="#accordion">
					        <div class="card-body">
					          <!-- pa.paBaspay清單 -->
								<table id="TblBasPayList" class="table table-striped table-bordered table-hover" >
									<thead>
										<tr>
											<th>項次</th>
											<th>代碼</th>
											<th>項目</th>
											<th>所得</th>
											<th>支出</th>
										</tr>
									</thead>
									<tfoot>
							            <tr>
							            	<th colspan=2 style="text-align:right"></th>
							             	<th style="text-align:right"></th>
							                <th style="text-align:right"></th>
							                <th style="text-align:right"></th>
							            </tr>
							        </tfoot>
								</table>
					        </div>
					      </div>
					    </div>
					    <div class="card">
					      <div class="card-header">
					        <a class="collapsed card-link" data-toggle="collapse" href="#collapseThree">
					          	歷史資料
					        </a>
					      </div>
					      <div id="collapseThree" class="collapse" data-parent="#accordion">
					        <div class="card-body">
					          <!-- pa.paHist清單 -->
								<table id="TblPaHistList" class="table table-striped table-bordered table-hover" >
									<thead>
										<tr>
											<th>異動性質</th>
											<th>異動日期</th>
											<th>階別</th>
											<th>職等</th>
											<th>本功</th>
											<th>級別</th>
											<th>俸點</th>
											<th>權理</th>
											<th>卡號</th>
											<th>職務編號</th>
											<th>職稱</th>
											<th>到職日</th>
											<th>離職日</th>
											<th>醫院別</th>
										</tr>
									</thead>
									
								</table>
					        </div>
					      </div>
					    </div>
					  </div>
 					
 					</div>	
 				</div>
 			</div>
 		</div>	
  </div>
  
  <!--篩選-->
  <div class="tab-pane fade" id="byPsnBaseFilterMain" role="tabpanel" aria-labelledby="pills-query-tab">
	<div class="container-fluid">
		<div class="row" id="filterItem">
				<div class="form-inline basother-filterbar">
					<div class="d-flex flex-wrap align-items-end basother-filter-group">
						<div class="filter-item">
							<input type="text" id="filter_card" class="form-control form-control-sm text-uppercase" placeholder="卡號" maxlength="4" size="4">
						</div>
						<div class="filter-item">
							<input type="text" id="filter_idno" class="form-control form-control-sm text-uppercase" placeholder="身分證" maxlength="10" size="10">
						</div>
						<div class="filter-item">
							<input type="text" id="filter_namec" class="form-control form-control-sm" placeholder="姓名" maxlength="12" size="12">
						</div>
						<div class="filter-item">
							<input type="text" id="filter_empno" class="form-control form-control-sm text-uppercase" placeholder="職號" maxlength="7" size="7">
						</div>
						<div class="filter-item">
							<select id="filter_lvefg" class="selectpicker" data-width="100%" title="離職" >
								<option value=""></option>
								<option value="N" selected>N</option>
								<option value="Y">Y</option>
							</select>
						</div>
						<div class="filter-item filter-item-wide">
							<select id="filter_dept" name="filter_dept" class="selectpicker PAYDEPT" data-width="100%" data-live-search="true" multiple title="單位">
								<option value=""></option>
								<c:forEach var="uitem" items="${alDeptCodeTbl}" varStatus="loop">
									<option value="${uitem.padepcod}">${uitem.pacstno}-${uitem.padepnmc}</option>
								</c:forEach>
							</select>
						</div>
						<div class="filter-item filter-item-wide">
							<select id="filter_title" name="filter_title" class="selectpicker JTCODE" data-width="100%" data-live-search="true" multiple title="職稱">
								<option value=""></option>
								<c:forEach var="uitem" items="${alTitleCodeTbl}" varStatus="loop">
									<option value="${uitem.paytitlk}">${uitem.patitsub}-${uitem.paytitlk}-${uitem.paytitlc}</option>
								</c:forEach>
							</select>
						</div>
						<div class="filter-item">
							<select id="filter_payrank" class="selectpicker RANKCODE" data-width="100%" data-live-search="true" multiple title="階級">
								<option value=""></option>
								<c:forEach var="uitem" items="${alRankCodeTbl}" varStatus="loop">
									<option value="${uitem.rankcode}">${uitem.rankcode}-${uitem.rankname}</option>
								</c:forEach>
							</select>
						</div>
						<div class="filter-item">
							<select id="filter_patradt" class="selectpicker PATRADT" data-width="100%" data-live-search="true" multiple title="異動日">
								<option value=""></option>
								<c:forEach var="bitem" items="${alBankDtTbl}" varStatus="loop">
									<option value="${bitem.paybank}">${bitem.paybank}</option>
								</c:forEach>
							</select>
						</div>
						<div class="filter-item">
							<select id="filter_patracd" class="selectpicker" data-width="100%" title="異動原因">
								<option value=""></option>
								<c:forEach var="bitem" items="${altracdTbl}" varStatus="loop">
									<option value="${bitem.tracode}">${bitem.traname}</option>
								</c:forEach>
							</select>
						</div>
						<div class="filter-item">
							<select id="filter_inflg" class="selectpicker" data-width="100%" title="保險補助">
								<option value=""></option>
								<option value="Y">Y</option>
							</select>
						</div>
						<div class="filter-item">
							<select id="filter_insuamt" class="selectpicker" data-width="100%" data-live-search="true" multiple title="保險級距">
								<option value=""></option>
								<c:forEach var="uitem" items="${alDpinTbl}" varStatus="loop">
									<option value="${uitem.dpinhigh}">${uitem.dpinhigh}</option>
								</c:forEach>
							</select>
						</div>
						<div class="filter-item cspf d-none">
							<select id="filter_pacsflag" class="selectpicker" data-width="100%" title="工作獎金">
								<option value=""></option>
								<option value="Y">Y</option>
								<option value="N">N</option>
							</select>
						</div>
						<div class="filter-item cspf d-none">
							<select id="filter_expfsp" class="selectpicker" data-width="100%" title="額外點數">
								<option value=""></option>
								<option value="Y">Y</option>
								<option value="N">N</option>
							</select>
						</div>
						<div class="filter-item cspf d-none">
							<select id="filter_expfkey1" class="selectpicker" data-width="100%" title="額外代碼">
								<c:forEach var="uitem" items="${alexpfTbl}" varStatus="loop">
									<option value="${uitem.expfkey}">${uitem.expfname}</option>
								</c:forEach>
							</select>
						</div>
						<%-- <div class="filter-item cspf d-none">
							<select id="filter_expfkey2" class="selectpicker" data-width="100%" title="額外2">
								<c:forEach var="uitem" items="${alexpfTbl}" varStatus="loop">
									<option value="${uitem.expfkey}">${uitem.expfname}</option>
								</c:forEach>
							</select>
						</div>
						<div class="filter-item cspf d-none">
							<select id="filter_expfkey3" class="selectpicker" data-width="100%" title="額外3">
								<c:forEach var="uitem" items="${alexpfTbl}" varStatus="loop">
									<option value="${uitem.expfkey}">${uitem.expfname}</option>
								</c:forEach>
							</select>
						</div> --%>
						<div class="filter-item cspf d-none">
							<select id="filter_paret" class="selectpicker" data-width="100%" title="專2">
								<option value=""></option>
								<option value="Y">Y</option>
								<option value="N">N</option>
							</select>
						</div>
					</div>
				</div>
		</div>
		<div class="row">
			<div class="d-flex justify-content-center gap-2 basother-btn-group">
				<button id="btn_qry_filter_BasPsn" class="btn btn-info" type="button" ><i class="fa fa-search"></i>查詢</button>
				<button id="btn_cln_filter_BasPsn" class="btn btn-warning" type="button"><i class="fa fa-eraser"></i>清空</button>	
			</div>
		</div>
		<div class="row">
			<div id="baspsnListDiv_2" class="baspsnListDiv">
				<!-- pa.pabaspsn篩選清單 -->
				<table id="TblBasPsnFilter" class="table table-striped table-hover" >
					<thead></thead>
				</table>				
			</div>
		</div>		
 	</div>
  </div>
  <!--基本處理-->
  <div class="tab-pane fade" id="byPsnBaseHandingMain" role="tabpanel" aria-labelledby="pills-handing-tab">
  	<div class="row p-1 mb-1 bg-secondary">
		<div class="col-md-10">
			<div class="form-inline">
				<div class="input-group" style="margin-right:10px;">
					<span class="input-group-text">選單</span>
			  	
					<select id="qryType" class="form-control">
						<option value="1:[產生處理]異動及批次資料_匯入主檔">1[產生處理]異動及批次資料_匯入主檔</option>
						<option value="2:[回復]異動及批次資料_回復主檔">2[回復]異動及批次資料_回復主檔</option>
						<option value="3:[匯入]投保金額:ImpBasINSUAMT">3[匯入]投保金額</option>   					
					</select>
				</div>
                <button id="btn_handelINSUAMT" class="btn btn-success" type="button" >
					<i class="fa fa-cog"></i>執行
				</button>
			</div>
		</div>

	</div>
	<div class="row">
		<div class="col-10">
			<div class="form-inline" id="rowBatHanding_1">	
					<!--餉單編號 -->	
					<div class="col-md-6 mb-3">
						<div class="basother-panel" id="divfield_SALNUM">
							<div class="form-row">
								<div class="form-group">
									<label for="prod_SalNum" class="table-warning">餉單編號-發薪日</label>
									<select id="prod_SalNum" class="form-control" title="餉單編號-發薪日"></select>								
								</div>
								<div class="alert alert-info" id="msgHint" style="display:inline-block; vertical-align:middle; background-color:#d9edf7; border-color:#bce8f1; color:#31708f; padding:6px 12px; margin-bottom:0; font-size:14px; line-height:1.6;">
            						<i class="fa fa-bullhorn"></i><span id="msgHintText">整批更新匯入主檔</span>
        						</div>
							</div>
						</div>
						<!-- 匯入檔案 -->
						<div class="basother-panel d-none" id="divfield_1">
							<div class="form-row">
								<div class="form-group">
									<label class="form-label d-block">匯入檔案（Excel），</label>
									<label class="table-warning">僅支援 .xlsx / .xls 格式</label>
								</div>
							</div>
							<div class="form-row">
								<div class="form-group">
									<form id="uploadfileForm" class="mb-0">
										<div class="custom-file-upload d-flex align-items-center border rounded p-2 bg-light">
                        					<i class="fa fa-file-excel-o text-success mr-2" style="font-size:1.3rem;"></i>
                        					<input type="file" accept=".xlsx,.xls,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-excel" id="fileInput" name="fileInput" class="form-control-file flex-grow-1">
                            			</div>
                        			</form>
								</div>
							</div>
							<div class="form-row">
								<div class="form-group">
									<div class="alert alert-info" style="display:inline-block; vertical-align:middle; background-color:#d9edf7; border-color:#bce8f1; color:#31708f; padding:6px 12px; margin-bottom:0; font-size:14px; line-height:1.6;">
            							<i class="fa fa-bullhorn"></i><span id="msgHintText1">請先[下載範例檔案]置於硬碟，可更名，修正後，再選擇檔案上傳匯入。</span>
        							</div>
								</div>								
							</div>
							<!--下載範例檔案 -->
							<div class="form-row">
								<div class="form-group">
									<a id="lnkSampleFile" href="${pageContext.request.contextPath}/salController.do?method=downloadSample&type=3" class="btn btn-outline-dark">
										<i class="fa fa-download"></i>下載範例檔案
                					</a>
								</div>								
							</div>
						</div>
					</div>
			</div>
		</div>
	</div>
	<!--  -->
  </div>
</div>

</body>
</html>