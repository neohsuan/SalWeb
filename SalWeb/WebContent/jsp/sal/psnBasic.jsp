<!DOCTYPE HTML>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<html>
<head>
<title>薪資基本檔</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<jsp:include page="../include.jsp"></jsp:include>
<script>
var tabid="";
var objCopy;

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
	
	$.getLoadCodeTbl = function(item){		
		$.ajax({
			url:"${pageContext.request.contextPath}/salController.do",
			data:{
				method  : 'getLoadCodeTbl',
 				item    : item
// 				flag    :flag
			},
			type:"POST",			//type:POST, GET(default:GET)
			dataType:"json",	//dataType: text, xml, html, script, json, jsonp
			contentType: "application/x-www-form-urlencoded;charset=utf-8",
			success:function(data) {
				var options ="";
				var select;
				//資料賦值
				if (data.alBankDtTbl[0]!=null){
					select = $('#base_patradt,#filter_patradt');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alBankDtTbl, function(idx) {
						options = "<option value='" + data.alBankDtTbl[idx].paybank.dts + "'>" + data.alBankDtTbl[idx].paybank.dts ;
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
			}  
		});
	};
 	/*預先載入代碼檔 */
  	$.getLoadCodeTbl("psnBasic");
 	
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
   		array.push( { "tblname": tblname, "value": value } ); //將動態表格資料轉換為json		    
		    
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
		
		if(id == 'btn_cln_Base_BasPsn' || id == 'btn_cln_filter_BasPsn'){
			$(":text,textarea").val("");
			$(":checkbox,:radio").prop("checked", false);
			
			//所有select預設選擇第一項			
// 			$('#qryCheckItem select').each(function() {
// 				$(this).prop("selectedIndex", 0);
// 		    });
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
						code_data :JSON.stringify(code_data)
					},
					type:"POST",	//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						//更新查詢清單
						$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
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
				var expfkey2=$.getSelectVals("filter_expfkey2");
				var expfkey3=$.getSelectVals("filter_expfkey3");
				
 				console.log("卡號:"+cardno+" 身分證:"+idtno+" 姓名:"+namec+" 職號:"+empno+" patradt:"+patradt+" payrank:"+payrank+" paytitle:"+paytitle+" paydept:"+paydept+" expfkey1:"+expfkey1+" expfkey2:"+expfkey2+" expfkey3:"+expfkey3+" 支領:"+csflag+" 點數:"+expfsp+" 專2:"+paret+" 異動原因:"+tracod+" 保險補助:"+inflg);

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
		        		expfkey2   :expfkey2,
		        		expfkey3   :expfkey3,
		        		csflag     :csflag,
		        		expfsp     :expfsp,
		        		paret      :paret,
		        		tracod     :tracod,
		        		inflg      :inflg
		        	};
				console.log("基本資料_篩選_查詢:code_data:"+code_data);
				
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'getBasPsnList',
						item    : '2',
						code_data :JSON.stringify(code_data)
					},
					type:"POST",	//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						//更新查詢清單
						$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
						$('#TblBasPsnFilter').show();
						oTable = $('#TblBasPsnFilter').dataTable(objCopyD);
						
						oTable.fnClearTable(0);		//清空資料
								
						if (data.basPsnFilter!=null && data.basPsnFilter.length>0){	
							oTable.fnAddData(data.basPsnFilter);//重新給資料
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
// 					else if (id.substring(id.indexOf('_')+1) =='Insbtn')		
// 						$('#method').val("insert");

					 $.ajax({
						url:"${pageContext.request.contextPath}/salController.do",
						data:{
							method	: "updPATblByKey",
							tblname : tblname,
							pk_data : JSON.stringify(pk_data),
							item  : $('#method').val(),
							tabJson : jsonString
						},
						type:"POST",			//type:POST, GET(default:GET)
						dataType:"json",	//dataType: text, xml, html, script, json, jsonp
						async: true,	//逐步執行
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
				
	});
	
	//個人明細
 	var objCopyA = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyA.aoColumns = [	
							{ "mData": "payidno" },
							{ "mData": "paycard" },
							{ "mData": "paynamec" },
							{ "mData": "pacstno" }
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
		code_data="";
		code_data={
        		idtno      :idtno,
        		hid        :hid
        	};
		console.log("基本資料pabaspsn查詢:code_data:"+code_data);
		
		$.getBasPsnByKey(code_data);
		
	});
	
	//依KEY找出pa.pabaspsn
	$.getBasPsnByKey = function(code_data){
		$.ajax({
			url:"${pageContext.request.contextPath}/salController.do",
			data:{
				method  : 'getBasPsnByKey',
				code_data :JSON.stringify(code_data)
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
		$("#basPsn_paydpno").val(mData.paydpno);
		$("#basPsn_paytitle").val(mData.patitsub+"-"+mData.paytitle);
		$("#basPsn_paytitlp").val(mData.paytitlp);
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
		$("#basPsn_paydpno").val(mData.paydpno);
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
			
		//總院才需列出
		if (mData.hid=='1A0'){
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
							{ "mData": "paylvefg" },
							{ "mData": "paycard" },
							{ "mData": "payempno" },
							{ "mData": "payidno" },
							{ "mData": "paynamec" },
							{ "mData": "padepnmc" },
							{ "mData": "paytitlp" },
							{ "mData": "rankname" },
							{ "mData": "payrnk2n" },
							{ "mData": "rank3name" },
							{ "mData": "paydegr2" },
							{ "mData": "paydegre" },
							{ "mData": "payrank2" },
							{ "mData": "payrankc" },
							{ "mData": "rnk4name" },
							{ "mData": "contract" },
							{ "mData": "paygovdt" },
							{ "mData": "payempdt" },
							{ "mData": "paysgndt" },
							{ "mData": "payrsgdt" },
							{ "mData": "patradt" },
							{ "mData": "traname" },
							{ "mData": "paydpno" },
							{ "mData": "paytaxno" },
							{ "mData": "payinflg" },
							{ "mData": "insuamt" },
							{ "mData": "pacsflag" },
							{ "mData": "pfbas" },
							{ "mData": "pfdat" },
							{ "mData": "paypfsp1" },
							{ "mData": "paypfsp2" },
							{ "mData": "paypfsp3" },
							{ "mData": "paypfdoc" },
							{ "mData": "paretention" }							
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
  <li class="nav-item">
    <a class="nav-link active" id="pills-basic-tab" data-toggle="pill" href="#byPsnBaseDetailMain" role="tab" aria-controls="pills-home" aria-selected="true">基本資料</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-query-tab" data-toggle="pill" href="#byPsnBaseFilterMain" role="tab" aria-controls="pills-profile" aria-selected="false">篩選</a>
  </li>
</ul>

<div class="tab-content" id="pills-tabContent">
	<!--基本資料-->
  	<div class="tab-pane fade show active" id="byPsnBaseDetailMain" role="tabpanel" aria-labelledby="pills-basic-tab">
		<div class="container-fluid">
			<div class="row" id="baseItem">
				<div class="col-10">
					<div class="form-inline">
		    			<div class="input-group">
							
		    				<input type="text" id="base_card" size="4" class="form-control-sm text-uppercase" placeholder="卡號" >
							<input type="text" id="base_idno" size="10" class="form-control-sm text-uppercase" placeholder="身分證" >
							<input type="text" id="base_namec" size="12" class="form-control-sm " placeholder="姓名" >
							<input type="text" id="base_empno" size="7" class="form-control-sm text-uppercase" placeholder="職號" >
							<select id="base_lvefg"  class="selectpicker" data-width="fit" title="離職">
								<option value=""></option>
								<option value="N" selected>N</option>
									<option value="Y">Y</option>
							</select>
							<select id="base_dept" name="base_dept" class="selectpicker PAYDEPT" data-width="fit" data-live-search="true" multiple title="單位">
								<option value=""></option>
			     				<c:forEach var="uitem" items="${alDeptCodeTbl}" varStatus="loop">
									<option value="${uitem.padepcod}">${uitem.pacstno}-${uitem.padepnmc}</option>
								</c:forEach>
							</select>
							<select id="base_title" name="base_title" class="selectpicker JTCODE" data-width="fit" data-live-search="true" multiple title="職稱">
								<option value=""></option>
			     				<c:forEach var="uitem" items="${alTitleCodeTbl}" varStatus="loop">
									<option value="${uitem.paytitlk}">${uitem.patitsub}-${uitem.paytitlk}-${uitem.paytitlc}</option>
								</c:forEach>
							</select>
							
							<select id="base_payrank"  class="selectpicker RANKCODE" data-width="fit" data-live-search="true" multiple title="階級">
								<option value=""></option>
								<c:forEach var="uitem" items="${alRankCodeTbl}" varStatus="loop">
									<option value="${uitem.rankcode}">${uitem.rankcode}-${uitem.rankname}</option>
								</c:forEach>
							</select>
							<select id="base_patradt" class="selectpicker PATRADT" data-width="fit"  data-live-search="true" multiple title="異動日">
								<option value=""></option>
								<c:forEach var="bitem" items="${alBankDtTbl}" varStatus="loop">
									<option value="${bitem.paybank}">${bitem.paybank}</option>
								</c:forEach>
							</select>
						</div>
					</div>
				</div>
				<div class="col-2">
					<button id="btn_qry_Base_BasPsn" class="btn btn-success" type="button" ><i class="fa fa-search"></i>查詢</button>
					<button id="btn_cln_Base_BasPsn" class="btn btn-warning" type="button"><i class="fa fa-eraser"></i>清空</button>
				</div>
			</div>	
			<div class="row">
				<div class="col-4 border border-dark">
					<div id="baspsnListDiv_1" class="baspsnListDiv">
					<!-- pa.pabaspsn清單 -->
					<table id="TblBasPsnList" class="table table-striped table-bordered table-hover" >
						<thead>
							<tr>
								<th>身分證</th>
								<th>卡號</th>	
								<th>姓名</th>
								<th>單位</th>
							</tr>
						</thead>
					</table>				
					</div>
				</div>
				
 				<div class="col-8 border border-dark">
 					<div class="container-fluid">
 					<div id="accordion">
					    <div class="card">
					      <div class="card-header">
					        <a class="card-link" data-toggle="collapse" href="#collapseOne">
					          	基本資料					          	
					        </a>
					        <button type="button" class="btn btn-primary" id="subPABASPSN_Updbtn" disabled>更改</button>
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
											<tr><td>
												<div class="form-inline">
													<div class="form-group">
														<label for="basPsn_paycard" >卡號:</label>
														<input type="text" id="basPsn_paycard" readonly class="text-uppercase form-control" maxlength="4" size="4"  />
													</div>
													<div class="form-group">
														<label for="paynamec" class="form-inline table-warning">姓名:</label>
														<input type="text" id="paynamec" class="form-control required" maxlength="12" size="12"  />
													</div>
													<div class="form-group">
														<label for="payempno" class="form-inline table-warning required">職號:</label>
														<input type="text" id="payempno" class="text-uppercase form-control" maxlength="7" size="7"  />
													</div>
													<div class="form-group">
														<label for="basPsn_paylvefg" >離職:</label>
														<input type="text" id="basPsn_paylvefg" readonly class="form-control"  maxlength="1" size="1"  />
													</div>
												</div>
											</td></tr>
											<tr><td>
												<div class="form-inline">
													<div class="form-group">
														<label for="basPsn_payidno" >身份證字號:</label>
														<input type="text" id="basPsn_payidno" readonly class="text-uppercase form-control" maxlength="10" size="10"  />
													</div>
													<div class="form-group">
														<label for="basPsn_paygovdt" >初任公職日:</label>
														<input type="text" id="basPsn_paygovdt" readonly class="form-control"  maxlength="10" size="10"  />
													</div>
													<div class="form-group">
														<label for="basPsn_payempdt" >到院日:</label>
														<input type="text" id="basPsn_payempdt" readonly class="form-control"  maxlength="10" size="10"  />
													</div>
												</div>
											</td></tr>
											<tr><td>
												<div class="form-inline">		
													<div class="form-group">
														<label for="paysgndt" class="table-warning">現職日:</label>
														<input type="text" id="paysgndt" class="gclsCalTxt form-control required"  maxlength="10" size="10"  />
													</div>
													<div class="form-group">
														<label for="basPsn_payrsgdt" >離職日:</label>
														<input type="text" id="basPsn_payrsgdt" readonly class="form-control"  maxlength="10" size="10"  />
													</div>
													<div class="form-group">
														<label for="basPsn_patradt" >異動日:</label>
														<input type="text" id="basPsn_patradt" readonly class="form-control"  maxlength="10" size="10"  />
														<input type="text" id="basPsn_traname" readonly class="form-control"  maxlength="8" size="8"  />
													</div>
												</div>
											</td></tr>
											
											<tr><td>
												<div class="form-inline">		
													<div class="form-group">
														<label for="paytaxno" class="table-warning">扶養人數:</label>
														<input type="text" id="paytaxno" class="form-control" maxlength="2" size="2"  />
													</div>
													<div class="form-group">
														<label for="basPsn_paydpno" >健保人數:</label>
														<input type="text" id="basPsn_paydpno" readonly class="form-control" maxlength="2" size="2"  />
													</div>
													<div class="form-group">
														<label for="insuamt" class="table-warning">投保金額:</label>
														<input type="text" id="insuamt" class="form-control" maxlength="6" size="6"  />
													</div>
													<div class="form-group">
														<label for="basPsn_inflgnm" >保險費比率:</label>
														<input type="text" id="basPsn_inflgnm" readonly class="form-control" maxlength="13" size="13"  />
													</div>
												</div>
											</td></tr>	
											
											<TR>
											<td>
												<div class="form-inline">		
													<div class="form-group">
														<label for="paydept" class="table-warning">單位:</label>
														<select id="paydept" name="paydept" class="form-control" data-width="fit">
														</select>
													</div>
													<div class="form-group">
														<label for="basPsn_paytitle" >職稱:</label>
														<input type="text" id="basPsn_paytitle" readonly class="form-control" maxlength="4" size="4"  />
														<input type="text" id="basPsn_paytitlp" readonly class="form-control" maxlength="22" size="22"  />
													</div>
												</div>
											</td>
											<tr><td>
												<div class="form-inline">
													<div class="form-group">
														<label for="basPsn_rankname">階別:</label>
														<input type="text" id="basPsn_rankname" readonly  class="form-control" maxlength="8" size="8"  />
													</div>
													<div class="form-group">
														<label for="basPsn_payrnk2n" >職等:</label>
														<input type="text" id="basPsn_payrnk2n" readonly class="form-control" maxlength="2" size="2"  />
													</div>
													<div class="form-group">
														<label for="basPsn_payrank3" >本功別:</label>
														<input type="text" id="basPsn_payrank3" readonly class="form-control" maxlength="1" size="1"  />
													</div>
													<div class="form-group">
														<label for="basPsn_paydegr2" >級別:</label>
														<input type="text" id="basPsn_paydegr2" readonly class="form-control" maxlength="2" size="2"  />
													</div>
													<div class="form-group">
														<label for="basPsn_paydegre" >俸點:</label>
														<input type="text" id="basPsn_paydegre" readonly class="form-control" maxlength="2" size="2"  />
													</div>
													<div id="div_contract" class="form-group d-none">
														<label for="basPsn_contract" >新Y/舊N:</label>
														<input type="text" id="basPsn_contract" readonly class="form-control" maxlength="1" size="1"  />
													</div>
												</div>
												</td>
											</tr>
											<tr><td >
												<div class="form-inline d-none" id="div_authority">
													<div class="form-group">
														<label for="basPsn_payrank2" >權理:</label>
														<input type="text" id="basPsn_payrank2" readonly class="form-control" maxlength="2" size="2"  />
													</div>
													<div class="form-group">
														<label for="basPsn_payrankc" >主管(權理):</label>
														<input type="text" id="basPsn_payrankc" readonly class="form-control" maxlength="2" size="2"  />
													</div>
													<div class="form-group">
														<label for="basPsn_rnk4name" >適用專業加給表:</label>
														<input type="text" id="basPsn_rnk4name" readonly class="form-control" maxlength="15" size="15"  />
													</div>
												</div>
												</td>
											</tr>
											<TR><td>
												<div class="form-inline">
													<div class="form-group">
														<label for="pacsflag" class="table-warning">工作獎金:</label>
														<select id="pacsflag" name="pacsflag" class="form-control" >
															<option value="">請選擇</option>
															<option value="Y">Y</option>
											     			<option value="N">N</option>
														</select>
													</div>	
													<div class="form-group" id="div_pfkey">	
														<label for="basPsn_pfkey" readonly >實際支領KEY:</label>
														<input type="text" id="basPsn_pfkey" readonly class="form-control" maxlength="7" size="7"  />
														<label for="basPsn_pfbas" >基本點數:</label>
														<input type="text" id="basPsn_pfbas" readonly class="form-control" maxlength="3" size="3"  />
														<label for="basPsn_pfdat" >職等點數:</label>
														<input type="text" id="basPsn_pfdat" readonly class="form-control" maxlength="3" size="3"  />
													</div>
												</div>
												<TD>
											</TR>
											<tr><td>
												<div class="cspf d-none">
													<div class="form-inline">
														<div class="form-group ">
															<label for="paypfsp1" class="table-warning">額外點數:</label>															
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
													<div class="form-inline">	
														<div class="form-group" id="div_pfdoc">
															<label for="paypfdoc" class="table-warning">專勤基本額度:</label>
															<input type="text" id="paypfdoc" class="form-control" maxlength="8" size="8"  />
															<label for="paretention" class="table-warning">留職留薪,支領專2(Y/N):</label>
															<input type="text" id="paretention" class="form-control text-uppercase" maxlength="1" size="1"  />
														</div>
													</div>
												</div>
												</td>
											</tr>
															
											<tr>
												<td>
													<a href="#basother" class="btn btn-info" data-toggle="collapse">其他</a>
											  		<div id="basother" class="collapse">
											  			<div class="form-inline">
											  				<div class="form-group">
											    				<label for="payctcbacno" class="table-warning">中國信託薪資帳號:</label>
																<input type="text" id="payctcbacno" class="form-control" maxlength="12" size="12"  />
																<label for="payctcbgedno" class="form-inline table-warning">中國信託公儲帳號:</label>
																<input type="text" id="payctcbgedno" class="form-control" maxlength="12" size="12"  />
															</div>
														</div>	
														<div class="form-inline">	
															<div class="form-group">
																<label for="payaddrs" class="table-warning">戶籍地址::</label>
																<input type="text" id="payaddrs" class="form-control" maxlength="62" size="62"  />													
															</div>
														</div>	
														<div class="form-inline">	
															<div class="form-group">
																<label for="basPsn_payupdid" >更改者:</label>
																<input type="text" id="basPsn_payupdid" readonly class="form-control" maxlength="4" size="4"  />
																<label for="basPsn_updtm" >更改時間:</label>
																<input type="text" id="basPsn_updtm" readonly class="form-control" maxlength="20" size="20"  />																
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
			<div class="col-10">
				<div class="form-inline">
	    			<div class="input-group">
						
	    				<input type="text" id="filter_card" size="4" class="form-control-sm text-uppercase" placeholder="卡號" >
						<input type="text" id="filter_idno" size="10" class="form-control-sm text-uppercase" placeholder="身分證" >
						<input type="text" id="filter_namec" size="12" class="form-control-sm " placeholder="姓名" >
						<input type="text" id="filter_empno" size="7" class="form-control-sm text-uppercase" placeholder="職號" >
						<select id="filter_lvefg"  class="selectpicker" data-width="fit" title="離職">
							<option value=""></option>
							<option value="N" selected>N</option>
								<option value="Y">Y</option>
						</select>
						<select id="filter_dept" name="filter_dept" class="selectpicker PAYDEPT" data-width="fit" data-live-search="true" multiple title="單位">
							<option value=""></option>
		     				<c:forEach var="uitem" items="${alDeptCodeTbl}" varStatus="loop">
								<option value="${uitem.padepcod}">${uitem.pacstno}-${uitem.padepnmc}</option>
							</c:forEach>
						</select>
						<select id="filter_title" name="filter_title" class="selectpicker JTCODE" data-width="fit" data-live-search="true" multiple title="職稱">
							<option value=""></option>
		     				<c:forEach var="uitem" items="${alTitleCodeTbl}" varStatus="loop">
								<option value="${uitem.paytitlk}">${uitem.patitsub}-${uitem.paytitlk}-${uitem.paytitlc}</option>
							</c:forEach>
						</select>
						
						<select id="filter_payrank"  class="selectpicker RANKCODE" data-width="fit" data-live-search="true" multiple title="階級">
							<option value=""></option>
							<c:forEach var="uitem" items="${alRankCodeTbl}" varStatus="loop">
								<option value="${uitem.rankcode}">${uitem.rankcode}-${uitem.rankname}</option>
							</c:forEach>
						</select>
						<select id="filter_patradt" class="selectpicker PATRADT" data-width="fit"  data-live-search="true" multiple title="異動日">
							<option value=""></option>
							<c:forEach var="bitem" items="${alBankDtTbl}" varStatus="loop">
								<option value="${bitem.paybank}">${bitem.paybank}</option>
							</c:forEach>
						</select>
						<select id="filter_patracd" class="selectpicker" data-width="fit"  title="異動原因">
							<option value=""></option>
							<c:forEach var="bitem" items="${altracdTbl}" varStatus="loop">
								<option value="${bitem.tracode}">${bitem.traname}</option>
							</c:forEach>
						</select>
						<select id="filter_pacsflag"  class="selectpicker" data-width="fit" title="工作獎金">
							<option value=""></option>
							<option value="Y">Y</option>
							<option value="N">N</option>
						</select>
						<select id="filter_expfsp"  class="selectpicker" data-width="fit" title="點數">
							<option value=""></option>
							<option value="Y">Y</option>
							<option value="N">N</option>
						</select>
						<select id="filter_expfkey1"  class="selectpicker" data-width="fit" title="額外1">
							<c:forEach var="uitem" items="${alexpfTbl}" varStatus="loop">
								<option value="${uitem.expfkey}">${uitem.expfname}</option>
							</c:forEach>
						</select>
						<select id="filter_expfkey2"  class="selectpicker" data-width="fit" title="額外2">
							<c:forEach var="uitem" items="${alexpfTbl}" varStatus="loop">
								<option value="${uitem.expfkey}">${uitem.expfname}</option>
							</c:forEach>
						</select>
						<select id="filter_expfkey3"  class="selectpicker" data-width="fit" title="額外3">
							<c:forEach var="uitem" items="${alexpfTbl}" varStatus="loop">
								<option value="${uitem.expfkey}">${uitem.expfname}</option>
							</c:forEach>
						</select>
						<select id="filter_paret"  class="selectpicker" data-width="fit" title="專2">
							<option value=""></option>
							<option value="Y">Y</option>
							<option value="N">N</option>
						</select>
						<select id="filter_inflg"  class="selectpicker" data-width="fit" title="保險補助">
							<option value=""></option>
							<option value="Y">Y</option>
						</select>
					</div>
				</div>
			</div>
			<div class="col-2">
				<button id="btn_qry_filter_BasPsn" class="btn btn-success" type="button" ><i class="fa fa-search"></i>查詢</button>
				<button id="btn_cln_filter_BasPsn" class="btn btn-warning" type="button"><i class="fa fa-eraser"></i>清空</button>
			</div>
		</div>
		<div class="row">
			<div class="col border border-dark">
				<div id="baspsnListDiv_2" class="baspsnListDiv">
					<!-- pa.pabaspsn篩選清單 -->
					<table id="TblBasPsnFilter" class="table table-striped table-hover" >
						<thead>
							<tr>
								<th>離職</th>
								<th>卡號</th>	
								<th>職號</th>
								<th>身分證</th>
								<th>姓名</th>
								<th>單位</th>
								<th>職稱</th>
								<th>階別</th>	
								<th>職等</th>
								<th>本功</th>
								<th>級別</th>
								<th>俸點</th>	
								<th>權理</th>
								<th>主管權理</th>
								<th>比照</th>
								<th>新/舊</th>
								<th>公職日</th>
								<th>到院日</th>
								<th>現職日</th>
								<th>離職日</th>
								<th>異動日</th>
								<th>異動項目</th>
								<th>健保數</th>
								<th>扶養數</th>
								<th>保險補助</th>
								<th>投保金額</th>
								<th>支領</th>
								<th>基本績分</th>
								<th>俸級績分</th>
								<th>額外1</th>
								<th>額外2</th>
								<th>額外3</th>
								<th>專勤額度</th>
								<th>專2</th>
							</tr>
						</thead>
					</table>				
				</div>
			</div>
		</div>		
 	</div>
  </div>
  
</div>

</body>
</html>