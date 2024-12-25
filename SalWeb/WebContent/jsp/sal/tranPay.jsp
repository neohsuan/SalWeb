<!DOCTYPE HTML>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<html>
<head>
<title>薪資異動檔</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<jsp:include page="../include.jsp"></jsp:include>
<script>
var tabid="";
var objCopy;
var patradt;
var salnum;
var patracod;
var code_data;
var itmTbl;
var oTable;
var uTable;
var payTbl;
var rowData;
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
				}					
			 	if (data.alTitleCodeTbl[0]!=null){
					select = $('#paytitle,#paotitle');
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
					select = $('#tran_patracd,#filter_patracd,#patracod');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.altracdTbl, function(idx) {
						options = "<option value='" + data.altracdTbl[idx].tracode + "'>" + data.altracdTbl[idx].tracode+" "+data.altracdTbl[idx].traname ;
						select.append(options);
					});
					select.selectpicker('refresh');
				}
				if (data.alpayitmTbl[0]!=null){
					select = $('#patrapay_paycode');
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
  	$.getLoadCodeTbl("tranPay");
 	
	//編輯欄位檢查
  	$.checkFields = function(btn){
  		msg = '';
  		
  		if (btn== 'btn_qry_tran_TranPsn'){	//異動資料_查詢
	 		if($("#tran_card").val()=='' && $("#tran_idno").val()=='' && $("#tran_namec").val()=='' && $("#tran_empno").val()=='' && $("#tran_salnum option:selected").val()=='' && $("#tran_patradt option:selected").val()=='' && $("#tran_patracd option:selected").val()==''){
	 			msg = '請輸入條件值!!';
	 		}	
 		}
 		else if (btn== 'subPATranPsn_Updbtn'){
 		
		}
		else if (btn== 'btn_qry_filter_TranPsn'){	//異動資料_篩選_查詢
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
	        data_id = String($(this).attr("id")).split('_')[1];
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
		
		if(id == 'btn_cln_tran_TranPsn' || id == 'btn_cln_filter_TranPsn'){
			$(":text,textarea").val("");
			$(":checkbox,:radio").prop("checked", false);
			
			//所有select預設選擇第一項			
// 			$('#qryCheckItem select').each(function() {
// 				$(this).prop("selectedIndex", 0);
// 		    });
		    $('.selectpicker').selectpicker('val', '');
			$('.selectpicker').selectpicker('refresh');
		    $('#tran_lvefg,#filter_lvefg').selectpicker('val', 'N');
		    
		    //所有select預設選擇第一項
		    $('#PATranPsn select').each(function() {
				$(this).prop("selectedIndex", 0);
		    });
		    
		    $("#subPATranPsn_Updbtn").prop("disabled", true);
		    		    
		    $("#TblTranPsnList").dataTable().fnClearTable();
			$("#TblBasPayList").dataTable().fnClearTable();
			$("#TblPaHistList").dataTable().fnClearTable();
			$("#TblTranPsnFilter").dataTable().fnClearTable();
			
			return false;
		}
		else if(id == 'btn_qry_tran_TranPsn'){		//異動資料_查詢
			msg = $.checkFields();
			if(msg == ''){
				salnum=$.getSelectVals("tran_salnum");
				patradt=$.getSelectVals("tran_patradt");
				patracod=$.getSelectVals("tran_patracd");
				var cardno=$("#tran_card").val().toUpperCase();
				var idtno=$("#tran_idno").val().toUpperCase();
				var namec=$("#tran_namec").val();
				var empno=$("#tran_empno").val().toUpperCase();				
				var paydept=$.getSelectVals("tran_dept");
				
 				console.log("卡號:"+cardno+" 身分證:"+idtno+" 姓名:"+namec+" 職號:"+empno+" patradt:"+patradt+" salnum:"+salnum+" patracod:"+patracod+" paydept:"+paydept);
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
				
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'getTranPsnList',
						item    : '1',
						code_data :JSON.stringify(code_data)
					},
					type:"POST",	//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						//更新查詢清單
						$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
						$('#TblTranPsnList').show();
						oTable = $('#TblTranPsnList').dataTable(objCopyA);
						
						oTable.fnClearTable(0);		//清空資料
								
						if (data.traPsnList!=null && data.traPsnList.length>0){	
							oTable.fnAddData(data.traPsnList);//重新給資料
						}
						oTable.fnDraw();
						
						//移至第1筆
						$("#TblTranPsnList tbody tr:first").find("a").eq(0).trigger("click");
						
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
				console.log("異動資料_篩選_查詢:code_data:"+JSON.stringify(code_data));
				
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'getTranPsnList',
						item    : '2',
						code_data :JSON.stringify(code_data)
					},
					type:"POST",	//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						//更新查詢清單
						$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
						$('#TblTranPsnFilter').show();
						oTable = $('#TblTranPsnFilter').dataTable(objCopyD);
						
						oTable.fnClearTable(0);		//清空資料
								
						if (data.TranPsnFilter!=null && data.TranPsnFilter.length>0){	
							oTable.fnAddData(data.TranPsnFilter);//重新給資料
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
		else if (id == 'subPATranPsn_Updbtn'){	//異動檔更改
			if ($("#subPATranPsnForm").valid()){
				msg = $.checkFields(id);
				if(msg == ''){
					itmTbl="TblTranPsnList";
					tblname=id.substring(3,id.indexOf('_'));
					console.log("tblname:"+tblname);
					var jsonString = $.getDataInfo(tblname); //取得目前的資料
					
					var pk_data={
		        		payidno : $('#payidno').val(),
		        		hid		: $('#hid').val()
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
								$("#btn_cln_tran_TranPsn").trigger( "click" ); 
								$("#tran_idno").val(pk_data.payidno);
								
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
				
	});
	
	//個人明細
 	var objCopyA = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyA.aoColumns = [	
							{ "mData": "payidno" },
							{ "mData": "paycard" },
							{ "mData": "paynamec" },
							{ "mData": "traname" },							
							{ "mData": "patradt" }
						 ];
	
	objCopyA = $.extend(true,objCopyA, {
				 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
						$('td:eq(0)', nRow).html( '<a href = "#tranpsnData" class= "tranpsnData">'+aData.payidno+'</a>' );
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
	$('#TblTranPsnList').dataTable(objCopyA);
	
	//顯示pa.paTranPsn明細
	$('#TblTranPsnList tbody').on('click', '.tranpsnData', function () {
		oTable = $('#TblTranPsnList').dataTable().api();
		info = oTable.page.info();		//記錄目前頁碼
		currentPage=info.page;
        var tr = $(this).parents('tr');
        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data	 		
		var idtno=rowData.payidno;
		var tradt=rowData.patradt;
		var tranum=rowData.tranum;
		var tracod=rowData.patracod;
		var hid=rowData.hid;
		
		//找pa.paTranPsn及pa.patrapay
		code_data="";
		code_data={
        		idtno  : idtno,
        		tradt  : tradt,
        		tracod : tracod,
        		tranum : tranum,
        		hid    : hid
        	};
		console.log("異動資料paTranPsn查詢:code_data:"+JSON.stringify(code_data));
		
		$.getTraPsnByKey(code_data);
		
	});
	
	//依KEY找出pa.paTranPsn
	$.getTraPsnByKey = function(code_data){
		$.ajax({
			url:"${pageContext.request.contextPath}/salController.do",
			data:{
				method  : 'getTraPsnByKey',
				code_data :JSON.stringify(code_data)
			},
			type:"POST",			//type:POST, GET(default:GET)
			dataType:"json",	//dataType: text, xml, html, script, json, jsonp
			contentType: "application/x-www-form-urlencoded;charset=utf-8",
			success:function(data) {
				if (data.traPsnItem!=null){
					//顯示異動個人明細
					$.doTraPsnDetail(data.traPsnItem);
					//pa.paTrapay原異動支扣明細
					$.doTraPayDetail("TblOTraPayList",data.otraPayList);
					//pa.paTrapay新異動支扣明細
					payTbl="TblNTraPayList";
					$.doTraPayDetail(payTbl,data.ntraPayList);					
				}
			}  
		}) 
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
	
	//顯示pa.paTranPsn明細
	$.doTraPsnDetail = function(mData){
		$("#div_paypflag").addClass("d-none");
		$("#paymonno_lbl").html("追補扣日數");
		//年終
		if (mData.patracod=='51'){
			$("#div_paypflag").removeClass("d-none");
			$("#paymonno_lbl").html("核發月數");
			$("#paypflag_lbl").html("發放年終獎金比例");
			select = $('#paypflag');
			select.empty();
			$.each(pflgBOptions, function(val, text) {
			    options = "<option value='" + val + "'>" +text ;
				select.append(options);
			});
			select.selectpicker('refresh');
		}
		//考績獎金
		else if (mData.patracod=='11' || mData.patracod=='12'){			
			$("#div_paypflag").removeClass("d-none");
			$("#paypflag_lbl").html("發放考績獎金月數");
			select = $('#paypflag');
			select.empty();
			$.each(pflgAOptions, function(val, text) {			    
			    options = "<option value='" + val + "'>" + text ;
				select.append(options);
			});
			
			select.selectpicker('refresh');
			
		}
		$("#hid").val(mData.hid);
		$("#patradt").val(mData.patradt.dts);
		$("#patracod").selectpicker('val',mData.patracod);
		$("#salnum").val(mData.salnum);
		$("#paylvefg").val(mData.paylvefg);
		
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
		$("#payinflg").val(mData.payinflg);
		
		$("#paytaxno").val(mData.paytaxno);
		$("#paydpno").val(mData.paydpno);
		$("#insuamt").val(mData.insuamt);
		$("#paypflag").selectpicker('val',mData.paypflag);
		
		$("#paydept").selectpicker('val',mData.paydept);
		
		//基本資料
		$("#paotitle").selectpicker('val',mData.paotitle);
		$("#ranknamo").val(mData.ranknamo);
		$("#paornk2n").val(mData.paornk2n);
		$("#paorank3").val(mData.paorank3);
		$("#paodegr2").val(mData.paodegr2);
		$("#paodegre").val(mData.paodegre);
		$("#TranPsn_contraco").val(mData.contraco);
		
		$("#paorank2").val(mData.paorank2);
		$("#paorankc").val(mData.paorankc);
		$("#rnk4namo").val(mData.rnk4namo);
		
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
		$("#rankname").val(mData.rankname);
		$("#payrnk2n").val(mData.payrnk2n);
		$("#payrank3").val(mData.rank3name);
		$("#paydegr2").val(mData.paydegr2);
		$("#paydegre").val(mData.paydegre);
		$("#TranPsn_contract").val(mData.contract);
		
		$("#payrank2").val(mData.payrank2);
		$("#payrankc").val(mData.payrankc);
		$("#rnk4name").val(mData.rnk4name);
		
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
		//原
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
			
		//總院才需列出
		if (mData.hid=='1A0'){
			$(".cspf").removeClass("d-none");
		}else if (mData.pacsflag=='N'){
			$(".cspf").addClass("d-none");
		}
		
		$("#subPATranPsn_Updbtn").prop("disabled", false);
	};
	//
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
    <a class="nav-link active" id="pills-basic-tab" data-toggle="pill" href="#byPsnBaseDetailMain" role="tab" aria-controls="pills-home" aria-selected="true">異動資料</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-query-tab" data-toggle="pill" href="#byPsnBaseFilterMain" role="tab" aria-controls="pills-profile" aria-selected="false">篩選</a>
  </li>
</ul>

<div class="tab-content" id="pills-tabContent">
	<!--異動資料-->
  	<div class="tab-pane fade show active" id="byPsnBaseDetailMain" role="tabpanel" aria-labelledby="pills-basic-tab">
		<div class="container-fluid">
			<div class="row" id="baseItem">
				<div class="col-10">
					<div class="form-inline">
		    			<div class="input-group">
							<select id="tran_salnum"  class="selectpicker SALNUM" data-width="fit" multiple title="餉單編號">
		    					<option value="0">0</option>
								<c:forEach var="bitem" items="${alBankDtTbl}" varStatus="loop">
									<option value="${bitem.salnum}">${bitem.salnum}</option>
								</c:forEach>
							</select>
							<select id="tran_patradt" class="selectpicker PATRADT" data-width="fit"  data-live-search="true" multiple title="異動日">
								<option value=""></option>
								<c:forEach var="bitem" items="${alBankDtTbl}" varStatus="loop">
									<option value="${bitem.paybank}">${bitem.paybank}</option>
								</c:forEach>
							</select>
							<select id="tran_patracd" class="selectpicker" data-width="fit" data-live-search="true" multiple title="異動原因">
								<option value=""></option>
								<c:forEach var="bitem" items="${altracdTbl}" varStatus="loop">
									<option value="${bitem.tracode}">${bitem.traname}</option>
								</c:forEach>
							</select>
		    				<input type="text" id="tran_card" size="4" class="form-control-sm text-uppercase" placeholder="卡號" >
							<input type="text" id="tran_idno" size="10" class="form-control-sm text-uppercase" placeholder="身分證" >
							<input type="text" id="tran_namec" size="12" class="form-control-sm " placeholder="姓名" >
							<input type="text" id="tran_empno" size="7" class="form-control-sm text-uppercase" placeholder="職號" >
							
							<select id="tran_dept" name="tran_dept" class="selectpicker PAYDEPT" data-width="fit" data-live-search="true" multiple title="單位">
								<option value=""></option>
			     				<c:forEach var="uitem" items="${alDeptCodeTbl}" varStatus="loop">
									<option value="${uitem.padepcod}">${uitem.pacstno}-${uitem.padepnmc}</option>
								</c:forEach>
							</select>
							
						</div>
					</div>
				</div>
				<div class="col-2">
					<button id="btn_qry_tran_TranPsn" class="btn btn-success" type="button" ><i class="fa fa-search"></i>查詢</button>
					<button id="btn_cln_tran_TranPsn" class="btn btn-warning" type="button"><i class="fa fa-eraser"></i>清空</button>
				</div>
			</div>	
			<div class="row">
				<div class="col-4 border border-dark">
					<div id="TranPsnListDiv_1" class="TranPsnListDiv">
					<!-- pa.paTranPsn清單 -->
					<table id="TblTranPsnList" class="table table-striped table-bordered table-hover" >
						<thead>
							<tr>
								<th>身分證</th>
								<th>卡號</th>	
								<th>姓名</th>
								<th>異動別</th>
								<th>發薪日</th>
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
					          	異動資料					          	
					        </a>
					        <button type="button" class="btn btn-success" id="subPATranPsn_Insbtn" disabled><i class="fa fa-save"></i>新增一筆</button>
					        <button type="button" class="btn btn-info" id="subPATranPsn_Cpybtn" disabled><i class="fa fa-copy"></i>複製</button>
					        <button type="button" class="btn btn-primary" id="subPATranPsn_Updbtn" disabled><i class="fa fa-edit"></i>更改</button>
					        <button type="button" class="btn btn-danger" id="subPATranPsn_Delbtn" disabled><i class="fa fa-trash-alt"></i>刪除</button>
					      </div>
					      <div id="collapseOne" class="collapse show" data-parent="#accordion">
					        <div class="card-body">
					        	<!-- pa.paTranPsn明細 -->
		 						<div class="table-responsive-sm">
		 							<form id="subPATranPsnForm">	 
		 							<INPUT type="hidden" id ="method"  value="">
		 							<INPUT type="hidden" id ="hid"  value="">
				 					<table id="PATranPsn" class="table table-striped table-bordered table-hover table-sm table-condensed" >
				 						<tbody>
											<tr><td>
												<div class="form-inline">
													<div class="form-group">
														<label for="patradt" class="form-inline table-warning">發薪日:</label>
														<input type="text" id="patradt" class="form-control table-warning"  maxlength="10" size="10"  />
														<label for="patracod" class="form-inline table-warning">異動原因:</label>
														<select id="patracod" name="patracod" class="form-control selectpicker required" data-width="fit" title="異動原因">
														</select>
														<label for="salnum">餉單編號:</label>
														<input type="text" id="salnum" readonly class="form-control" maxlength="4" size="4"  />
														<label for="paylvefg" >離職:</label>
														<input type="text" id="paylvefg" readonly class="form-control"  maxlength="1" size="1"  />
													</div>
												</div>
											</td></tr>
											<tr><td>	
												<div class="form-inline">
													<div class="form-group">
														<label for="payidno" class="table-warning">身份證字號:</label>
														<input type="text" id="payidno" class="text-uppercase form-control required" maxlength="10" size="10"  />
													</div>												
													<div class="form-group">
														<label for="paycard" class="form-inline table-warning">卡號:</label>
														<input type="text" id="paycard" class="text-uppercase form-control required" maxlength="4" size="4"  />
													</div>
													<div class="form-group">
														<label for="payempno" class="form-inline table-warning">職號:</label>
														<input type="text" id="payempno" class="text-uppercase form-control required" maxlength="7" size="7"  />
													</div>
													<div class="form-group">
														<label for="paynamec" class="form-inline table-warning">姓名:</label>
														<input type="text" id="paynamec" class="form-control required" maxlength="12" size="12"  />
													</div>
													
												</div>
											</td></tr>
											<tr><td>
												<div class="form-inline">
													
													<div class="form-group">
														<label for="paygovdt" class="table-warning">初任公職日:</label>
														<input type="text" id="paygovdt" class="gclsCalTxt form-control required"  maxlength="10" size="10"  />
													</div>
													<div class="form-group">
														<label for="payempdt" class="table-warning">到院日:</label>
														<input type="text" id="payempdt" class="gclsCalTxt form-control required"  maxlength="10" size="10"  />
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
														<label for="payrsgdt" class="table-warning">離職日:</label>
														<input type="text" id="payrsgdt" class="gclsCalTxt form-control"  maxlength="10" size="10"  />
													</div>
													
												</div>
											</td></tr>
											<tr><td>
												<div class="form-inline">		
													<div class="form-group">
														<label for="paeffdt" class="table-warning">生效日:</label>
														<input type="text" id="paeffdt" class="gclsCalTxt form-control required"  maxlength="10" size="10"  />
													</div>
													<div class="form-group">
														<label for="paymonno" id="paymonno_lbl" class="table-warning">追補扣日數:</label>
														<input type="text" id="paymonno" class="form-control"  maxlength="3" size="3" />月
														<input type="text" id="paydayno" class="form-control"  maxlength="3" size="3" />日
													</div>
													<div class="form-group" id="div_paypflag">
														<label for="paypflag" id="paypflag_lbl" class="table-warning">發放考績獎金月數:</label>
														<select id="paypflag" class="form-control selectpicker" >
															<option value="">請選擇</option>
															<option value="0">0</option>
											     			<option value="1">0.5</option>
											     			<option value="2">1</option>
											     			<option value="3">1.5</option>
											     			<option value="4">2</option>
														</select>
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
														<label for="paydpno" class="table-warning">健保人數:</label>
														<input type="text" id="paydpno" class="form-control" maxlength="2" size="2"  />
													</div>
													<div class="form-group">
														<label for="insuamt" class="table-warning">投保金額:</label>
														<input type="text" id="insuamt" class="form-control" maxlength="6" size="6"  />
													</div>
													<div class="form-group">
														<label for="payinflg" class="table-warning">保險費比率:</label>
														<select id="payinflg"  class="form-control selectpicker" >
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
											</td></tr>	
											
											<TR>
											<td>
												<div class="form-inline">		
													<div class="form-group">
														<label for="paydept" class="table-warning">單位:</label>
														<select id="paydept" name="paydept" class="form-control selectpicker required" data-width="fit" title="單位">
														</select>
													</div>
												</div>
											</td>
											<tr class="table-info"><td>基本資料</td></tr>
											<tr><td>
												<div class="form-inline">		
													<div class="form-group">
														<label for="paotitle" class="table-warning">職稱:</label>
														<select id="paotitle" name="paotitle" class="form-control selectpicker" data-width="fit" title="職稱">
														</select>
														
													</div>
												</div>
												<div class="form-inline">
													<div class="form-group">
														<label for="ranknamo" class="table-warning">階別:</label>
														<input type="text" id="ranknamo"  class="form-control" maxlength="8" size="8"  />
													</div>
													<div class="form-group">
														<label for="paornk2n" class="table-warning">職等:</label>
														<input type="text" id="paornk2n" class="form-control" maxlength="2" size="2"  />
													</div>
													<div class="form-group">
														<label for="paorank3" class="table-warning">本功別:</label>
														<input type="text" id="paorank3" class="form-control" maxlength="1" size="1"  />
													</div>
													<div class="form-group">
														<label for="paodegr2" class="table-warning">級別:</label>
														<input type="text" id="paodegr2" class="form-control" maxlength="2" size="2"  />
													</div>
													<div class="form-group">
														<label for="paodegre" class="table-warning">俸點:</label>
														<input type="text" id="paodegre" class="form-control" maxlength="2" size="2"  />
													</div>
													<div id="div_contraco" class="form-group d-none">
														<label for="TranPsn_contraco" >新Y/舊N:</label>
														<input type="text" id="TranPsn_contraco" readonly class="form-control" maxlength="1" size="1"  />
													</div>
												</div>
												</td>
											</tr>
											<tr><td>
												<div class="form-inline d-none" id="div_authorito">
													<div class="form-group">
														<label for="paorank2" class="table-warning">權理:</label>
														<input type="text" id="paorank2" class="form-control" maxlength="2" size="2"  />
													</div>
													<div class="form-group">
														<label for="paorankc" class="table-warning">主管(權理):</label>
														<input type="text" id="paorankc" class="form-control" maxlength="2" size="2"  />
													</div>
													<div class="form-group">
														<label for="rnk4namo" class="table-warning">適用專業加給表:</label>
														<input type="text" id="rnk4namo" class="form-control" maxlength="15" size="15"  />
													</div>
												</div>
												</td>												
											</tr>
											<TR><td>
												<div class="form-inline">
													<div class="form-group">
														<label for="paocsfla" class="table-warning">工作獎金:</label>
														<select id="paocsfla"  class="form-control" >
															<option value="">請選擇</option>
															<option value="Y">Y</option>
											     			<option value="N">N</option>
														</select>
													</div>	
													<div class="form-group" id="div_pokey">	
														<label for="TranPsn_pokey" >實際支領KEY:</label>
														<input type="text" id="TranPsn_pokey" readonly class="form-control" maxlength="7" size="7"  />
														<label for="TranPsn_pobas" >基本點數:</label>
														<input type="text" id="TranPsn_pobas" readonly class="form-control" maxlength="3" size="3"  />
														<label for="TranPsn_podat" >職等點數:</label>
														<input type="text" id="TranPsn_podat" readonly class="form-control" maxlength="3" size="3"  />
													</div>
												</div>
												<TD>
											</TR>
											<tr><td>
												<div class="cspf d-none">
													<div class="form-inline">
														<div class="form-group ">
															<label for="paopfsp1" class="table-warning">額外點數:</label>															
															<select id="paoexpfkey1"  class="selectpicker" data-width="fit" title="額外1">																
															</select>
															<input type="text" id="paopfsp1" class="form-control" maxlength="3" size="3"  />
															
															<select id="paoexpfkey2"  class="selectpicker" data-width="fit" title="額外2">																
															</select>
															<input type="text" id="paopfsp2" class="form-control" maxlength="3" size="3"  />
															
															<select id="paoexpfkey3"  class="selectpicker" data-width="fit" title="額外3">																
															</select>
															<input type="text" id="paopfsp3" class="form-control" maxlength="3" size="3"  />														
														</div>
													</div>	
													<div class="form-inline">	
														<div class="form-group" id="div_podoc">
															<label for="paopfdoc" class="table-warning">專勤基本額度:</label>
															<input type="text" id="paopfdoc" class="form-control" maxlength="8" size="8"  />															
														</div>
													</div>
												</div>
												</td>
											</tr>
											<tr class="table-success"><td>新</td></tr>
											<tr><td>
												<div class="form-inline">		
													<div class="form-group">
														<label for="paytitle" class="table-warning">職稱:</label>
														<select id="paytitle" name="paytitle" class="form-control selectpicker" data-width="fit" title="職稱">
														</select>
													</div>
												</div>
												<div class="form-inline">
													<div class="form-group">
														<label for="rankname" class="table-warning">階別:</label>
														<input type="text" id="rankname"  class="form-control" maxlength="8" size="8"  />
													</div>
													<div class="form-group">
														<label for="payrnk2n" class="table-warning">職等:</label>
														<input type="text" id="payrnk2n" class="form-control" maxlength="2" size="2"  />
													</div>
													<div class="form-group">
														<label for="payrank3" class="table-warning">本功別:</label>
														<input type="text" id="payrank3" class="form-control" maxlength="1" size="1"  />
													</div>
													<div class="form-group">
														<label for="paydegr2" class="table-warning">級別:</label>
														<input type="text" id="paydegr2" class="form-control" maxlength="2" size="2"  />
													</div>
													<div class="form-group">
														<label for="paydegre" class="table-warning">俸點:</label>
														<input type="text" id="paydegre" class="form-control" maxlength="2" size="2"  />
													</div>
													<div id="div_contract" class="form-group d-none">
														<label for="TranPsn_contract" >新Y/舊N:</label>
														<input type="text" id="TranPsn_contract" readonly class="form-control" maxlength="1" size="1"  />
													</div>
												</div>
												</td>
											</tr>
											<tr><td >
												<div class="form-inline d-none" id="div_authority">
													<div class="form-group">
														<label for="payrank2" class="table-warning">權理:</label>
														<input type="text" id="payrank2" class="form-control" maxlength="2" size="2"  />
													</div>
													<div class="form-group">
														<label for="payrankc" class="table-warning">主管(權理):</label>
														<input type="text" id="payrankc" class="form-control" maxlength="2" size="2"  />
													</div>
													<div class="form-group">
														<label for="rnk4name" class="table-warning">適用專業加給表:</label>
														<input type="text" id="rnk4name" class="form-control" maxlength="15" size="15"  />
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
														<label for="TranPsn_pfkey" >實際支領KEY:</label>
														<input type="text" id="TranPsn_pfkey" readonly class="form-control" maxlength="7" size="7"  />
														<label for="TranPsn_pfbas" >基本點數:</label>
														<input type="text" id="TranPsn_pfbas" readonly class="form-control" maxlength="3" size="3"  />
														<label for="TranPsn_pfdat" >職等點數:</label>
														<input type="text" id="TranPsn_pfdat" readonly class="form-control" maxlength="3" size="3"  />
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
																<label for="payctcbgedno" class="table-warning">中國信託公儲帳號:</label>
																<input type="text" id="payctcbgedno" class="form-control" maxlength="12" size="12"  />
															</div>
														</div>	
														<div class="form-inline">	
															<div class="form-group">
																<label for="payaddrs" class="table-warning">戶籍地址:</label>
																<input type="text" id="payaddrs" class="form-control" maxlength="62" size="62"  />													
															</div>
														</div>	
														<div class="form-inline">	
															<div class="form-group">
																<label for="TranPsn_payupdid" >更改者:</label>
																<input type="text" id="TranPsn_payupdid" readonly class="form-control" maxlength="4" size="4"  />
																<label for="TranPsn_updtm" >更改時間:</label>
																<input type="text" id="TranPsn_updtm" readonly class="form-control" maxlength="20" size="20"  />																
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
					        	[原]支扣明細
					      	</a>
					      </div>
					      <div id="collapseTwo" class="collapse" data-parent="#accordion">
					        <div class="card-body">
					          <!-- pa.paTrapay原清單 -->
								<table id="TblOTraPayList" class="table table-striped table-bordered table-hover" >
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
					        	[新]支扣明細
					      	</a>
					      	<button id="btn_addpay" class="btn btn-success ml-auto" type="button" data-toggle="modal" data-target="#addPayeDialog"><i class="fa fa-edit"></i>新增一筆</button>
					      </div>
					      <div id="collapseThree" class="collapse" data-parent="#accordion">
					        <div class="card-body">
					          <!-- pa.paTrapay新清單 -->
								<table id="TblNTraPayList" class="table table-striped table-bordered table-hover" >
									<thead>
										<tr>
											<th>項次</th>
											<th>代碼</th>
											<th>項目</th>
											<th>金額</th>
											<th>編輯</th>
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
 			<!-- 新增dialog -->
			<div class="modal fade" id="addPayeDialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	
				<form id="subtraPayForm" >
					<INPUT type="hidden" id="citem" name ="item">
					<div class="modal-dialog modal-lg">					
						<!-- Modal content-->
						<div class="modal-content">
	                   		 <!-- Modal Header -->
					        <div class="modal-header">
								<h4 class="modal-title"><span id="model_header_title"></span></h4>
								<button type="button" class="close" data-dismiss="modal">&times;</button>
					        </div>	
							<div class="modal-body">
								<div class="container-fluid">
									<div class="row justify-content-md-center">                       		
										<table id="PATRAPAY" class="table table-striped table-bordered" style="text-align:center;">
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
													<td>
														<input type="text" id="patrapay_payidno" name="payidno" class="form-control required" value="" size="10"  maxlength="10"></input>
														<input type="hidden" id="patrapay_tranum" name="tranum" class="form-control required" value="" size="10"  maxlength="10"></input>
														<input type="hidden" id="patrapay_paysort" name="paysort" class="form-control required" value="N" size="1"  maxlength="1"></input>
														<input type="hidden" id="patrapay_payauth" name="payauth" size="8" ></input>
														<input type="hidden" id="patrapay_payiftax" name="payiftax" size="1" ></input>
														<input type="hidden" id="patrapay_payflag" name="payflag" size="1" ></input>
														
													</td>
													<td><input type="text" id="patrapay_patradt" name="patradt" class="form-control findkey gclsCalTxt required" value="" size="10" placeholder="請輸入發薪日" maxlength="10"></input></td>
													<td>
														<select id="patrapay_paycode" name="paycode" class="selectpicker findkey required" data-width="fit"  data-live-search="true" title="支扣代碼">																
														</select>
													</td>
													<td><input type="text" id="patrapay_payamt" name="payamt" class="form-control required" value="" size="6" placeholder="金額" maxlength="6"></input></td>
													<td><input type="text" id="patrapay_hid" name="hid" class="form-control" value="" size="3"  maxlength="3" readOnly></input></td>
													<td><input type="text" id="patrapay_payupdtm" name="payupdtm" class="form-control" value="" size="26"  maxlength="26" readOnly></input></td>
												</tr>
											</tbody>							
										</table>
									</div>	
								</div>	
	                       </div>
	                       <!-- Modal footer -->
					        <div class="modal-footer">
	                           <button type="button" class="btn btn-primary" id="btn_subTraPayIns"><i class="fa fa-save"></i>確認儲存</button>
	                           <button type="button" class="btn btn-primary" id="btn_subTraPayUpd"><i class="fa fa-refresh"></i>更改</button>
	                           <button type="button" class="btn btn-secondary" data-dismiss="modal"><i class="fa fa-undo"></i>返回</button>
	                       </div>
	                   </div>
	               </div>
				</form>
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
				<button id="btn_qry_filter_TranPsn" class="btn btn-success" type="button" ><i class="fa fa-search"></i>查詢</button>
				<button id="btn_cln_filter_TranPsn" class="btn btn-warning" type="button"><i class="fa fa-eraser"></i>清空</button>
			</div>
		</div>
		<div class="row">
			<div class="col border border-dark">
				<div id="TranPsnListDiv_2" class="TranPsnListDiv">
					<!-- pa.paTranPsn篩選清單 -->
					<table id="TblTranPsnFilter" class="table table-striped table-hover" >
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