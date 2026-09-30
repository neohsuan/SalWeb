<!DOCTYPE HTML>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="gov.vghks.sal.Globals"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<% double wkRateMax = Globals.gMWorkRetirRate*100; %>
<html>
<head>
<title>勞工退休金</title>
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
var wkRateMax=<%=wkRateMax%>;/*勞工退休金--公提退休金比率 原值是小數點*/
var cardno;
var in_data;
var oTable;
var idtno;
var objCopy;
var selEn;
var selNm;
var salnum;
var patradt;
var currentPage;
var tblname;
var jsonString;
var pk_data;

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
	
	//tab設定
	$('#pills-tab a').on('click', function (e) {
	  e.preventDefault();
	  $(this).tab('show');
	  tabid=$(this).attr('id');
	  console.log(tabid);
	  //產製
	  if (tabid=="pills-wkProd-tab"){
		if(selCode == "1"){	//寫入勞工退休金
			logtmp="\\\\ap_pd1.vghks.gov.tw\\Apps_Exe\\SalProd\\1A0\\log\\勞工退休金" ;		  	
	  	}else if (selCode == "2"){	//寫入批次檔
	  		logtmp="VD060自繳退休金,\\\\ap_pd1.vghks.gov.tw\\Apps_Exe\\SalProd\\1A0\\log\\勞工退休金_批次檔" ;		  	
		}  	  	
	  	$("#explanProd_1").html(logtmp);
	  }
	  
	});
	//產製代碼切換
	$("#wk_cond").change(function(){
		$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
		$("#exp_1,#exp_3").addClass("d-none");	//隱藏
		selCode=$("#wk_cond option:selected").val().split(':')[0];
		if (typeof(selCode)=="undefined" || '' == selCode){
			selCode="1";
		}
		if(selCode == "1"){	//寫入勞工退休金
			$("#exp_1").removeClass("d-none");					  	
	  	}else if (selCode == "3"){	//寫入批次檔
	  		$("#exp_3").removeClass("d-none");		  	
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
				//職稱
				if (data.alTitleCodeTbl[0]!=null){
					select = $('#wkretir_paytitle');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alTitleCodeTbl, function(idx) {
						options = "<option value='" + data.alTitleCodeTbl[idx].paytitlk + "'>" + data.alTitleCodeTbl[idx].patitsub+"-"+data.alTitleCodeTbl[idx].paytitlk+"-"+data.alTitleCodeTbl[idx].paytitlc ;
						select.append(options);
					});
					select.selectpicker('refresh');
				}
				//月提繳工資
				if (data.alLbrpTbl[0]!=null){
					select = $('#wkretir_wrinref');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alLbrpTbl, function(idx) {
						options = "<option value='" + data.alLbrpTbl[idx].lbrpamt+":"+data.alLbrpTbl[idx].masteramt + "'>" + data.alLbrpTbl[idx].lbrpamt ;
						select.append(options);
					});
					select.selectpicker('refresh');
				}
				//餉單編號(2個月內)
				if (data.alBankDtTbl_2M[0]!=null){
					select = $('#wk_SalNum');
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
  	$.getLoadCodeTbl('wkRetir','Y');
  	 	
	//編輯欄位檢查
  	$.checkFields = function(btn){
  		msg = '';
  		var isChecked=0;
  		if (btn== 'btn_prodWk'){	//產製
  			if ( $("#wk_cond").val()=='' || typeof($("#wk_cond").val())=='undefined' )
  				msg = '請輸入項目!!';
  			
  			if ( $("#wk_SalNum").val()=='' || typeof($("#wk_SalNum").val())=='undefined' )
  			    msg += '[餉單編號_發薪日]請勿空白!!';
  			
  		}else if (btn== 'btn_qryWkRetir'){	//查詢
  			if($("#qry_patradt").val()=='' && $("#qry_cardno").val()=='' && $("#qry_idtno").val()=='' )
  				msg = '請輸入條件!!';
  		}else if (btn == 'subPAWKRETIR_Insbtn'||btn == 'subPAWKRETIR_Updbtn'){	//勞工退休金_編輯_新增/更改
  	  		
  		}	
  		return msg;	
	};
	//讀取table欄位內容
  	$.getDataInfo = function(tblname){
		var array = new Array();
		var value = [];
		var obj = {};
		var $th ;	
		var data_nm;
		var data_val;
		
		if (tblname=="PAWKRETIR"){//勞工退休金
			
			$( ' select , input:text , input:radio:checked, input:checkbox:checked, textarea,input:hidden ','#'+tblname).each(function() {
				data_nm = String($(this).attr("name"));
	    		data_val = $(this).val();
	    		if (typeof data_nm === "undefined" || data_nm == "undefined") {
		        	
		        }else{
		        	if ( null == data_val ){
		        		data_val = "";
		        	}else{
		        		data_val=data_val.toUpperCase();//轉大寫
		        	}	
		        	obj[data_nm] = data_val; //名稱+數值	
		        }
		    });    
	    	value.push(obj);   	//以整支table寫入	
	   		array.push( { "formnm": tblname, "value": value } ); 
			
	    }
   		    
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
	};	
	//jQuery按紐圖示
	$(".btn").button().click(
		function(event) {event.preventDefault();}
	);
	$(".btn").click(function(){
		var id = $(this).attr('id');
		
		if(id == 'btn_clnWkRetir'){
			cleanTable("byWkRetirMain");
			$('.selectpicker').selectpicker('val', '');
			$('.selectpicker').selectpicker('refresh');
			$("#TblWkretirList").dataTable().fnClearTable();
			return false;
		}
		else if(id == 'btn_addWkRetir'){		//新增一筆
			cleanTable("subPAwkretirForm");
			cleanTable("byWkRetirMain");
			$('.selectpicker').selectpicker('val', '');
			$('.selectpicker').selectpicker('refresh');
			$("#wkretirItemModal").find('.modal-title').text("新增[勞工退休金]");
			//key
			$("#wkretir_patradt").prop('readOnly', false);	
			$("#wkretir_payidno").prop("readOnly",false);	
			$('#wkretir_paysort').prop('readOnly', false);
			$("#wkretir_hid").val(pahid);
			
			$("label.error").hide();
			$(".error").removeClass("error");
	        $("#wkretirItemModal").modal("show");
			$("#subPAWKRETIR_Insbtn").prop("disabled", false);
			$("#subPAWKRETIR_Updbtn").prop("disabled", true);
			
			$("#wkretir_paymonno").val(0);
			$("#wkretir_paydayno").val(0);
			$("#wkretir_paysort").val(1);
			$("#wkretir_payflag").val("I");
			$("#wkretir_selfexrate").val(0);//預設
			$("#wkretir_masteramt").val(0);//預設
		}
		else if(id == 'btn_qryWkRetir'){		//查詢
			msg = $.checkFields(id);
			if(msg == ''){
				cardno =$("#qry_cardno").val().toUpperCase();
				idtno  =$("#qry_idtno").val().toUpperCase();
				patradt=$("#qry_patradt").val();
				selCode="WkRetirQry";
				selNm  ="勞工退休金";
				in_data={
		        		cardno     :cardno,
		        		idtno      :idtno,
		        		patradt    :patradt,
		        		selCode    :selCode,
		        		selNm      :selNm
		        	};
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'handelWkRetir',
						fun     : selCode,
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
						$('#TblWkretirList').show();
						oTable = $('#TblWkretirList').dataTable(objCopyA);
						
						oTable.fnClearTable(0);		//清空資料
								
						if (data.WkRetirList!=null && data.WkRetirList.length>0){	
							oTable.fnAddData(data.WkRetirList);//重新給資料
						}
						oTable.fnDraw(false);
							
					},
					error: function(xhr){					
						alert("查詢失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
					}
				});
				
				
			}else{
				alert(msg);
				return false;
			}
		}
		else if(id == 'btn_prodWk'){		//產製
			msg = $.checkFields(id);
			if(msg == ''){
				showLoading(); //顯示loading	
				selCode = $("#wk_cond option:selected").val().split(':')[0];
				selNm   = $("#wk_cond option:selected").val().split(':')[1];
				selEn   = $("#wk_cond option:selected").val().split(':')[2];
				salnum  = $("#wk_SalNum").val().split(':')[0];
				patradt = $("#wk_SalNum").val().split(':')[1];
				console.log("產製: selCode:"+selCode+" selNm:"+selNm+" salnum:"+salnum+" patradt:"+patradt+" hid:"+pahid);
					 
				in_data="";
				in_data={
						selCode : selCode,
						selNm   : selNm,
						selEn   : selEn,
						salnum  : salnum,
						patradt : patradt   
					};
				console.log("in_data:"+JSON.stringify(in_data));
				//return false;
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'handelWkRetir',
						fun     : 'execWk',
						selNm   : selNm,
						in_data : JSON.stringify(in_data),
						hid     : pahid,
						userid  : paUserid
					},
					type:"POST",	//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						if (data.msg!=null){
							
							alert(data.msg);
						}	
					},
					error: function(xhr){					
						alert("[勞工退休金產製_"+selNm+"]失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
					},
					complete: function(data) {
						hideLoading(); //隱藏loading
					}
				});
				hideLoading(); //隱藏loading
				return false;
				
			}else{
				alert(msg);
				return false;
			} 
		}
		else if(id == 'subPAWKRETIR_Updbtn'||id == 'subPAWKRETIR_Insbtn'){	//補充保險費[獎金] 更改 新增
			if ($("#subPAwkretirForm").valid()){
				msg = $.checkFields(id);
				if(msg == ''){
					//將key取消readOnly
					$("#wkretir_patradt").attr("readOnly",false);	
					$("#wkretir_payidno").attr("readOnly",false);	
					$("#wkretir_paysort").attr("readOnly",false);
					$("#wkretir_hid").attr("readOnly",false);
					tblname=id.substring(3,id.indexOf('_'));
					jsonString = $.getDataInfo(tblname); //取得目前的資料
					pk_data={
							patradt : $('#wkretir_patradt').val(),
							payidno : $('#wkretir_payidno').val(),
							paysort : $('#wkretir_paysort').val(),
			        		hid		: $('#wkretir_hid').val()
			        	};
					if (id.substring(id.indexOf('_')+1) =='Updbtn')		
						$('#wkretiritem').val("update");
					else if (id.substring(id.indexOf('_')+1) =='Insbtn')		
						$('#wkretiritem').val("insert");
					oTable = $('#TblWkretirList').dataTable().api();
					uTable = $('#TblWkretirList').dataTable();
					info = oTable.page.info();		//記錄目前頁碼
					currentPage=info.page;
					//return false;
					$.ajax({
						url:"${pageContext.request.contextPath}/salController.do",
						data:{
							method   : 'handelWkRetir',
							fun      : 'updWkretir',
							selNm    : '勞工退休金 ',
							editMode : $('#wkretiritem').val(),
							pk_data  : JSON.stringify(pk_data),
							tblname  : tblname,
							tabJson  : jsonString,
							hid      : pahid,
							userid   : paUserid,
						},
						type:"POST",	//type:POST, GET(default:GET)
						dataType:"json",	//dataType: text, xml, html, script, json, jsonp
						async: true,	//逐步執行
						contentType: "application/x-www-form-urlencoded;charset=utf-8",  
						success:function(data) {
							//data.status!=null||typeof(data.status)!="undefined" ||
							if ( data.status=="N"){	//show msg,dialog不用關閉
								if (data.msg!=null){
									alert(data.msg);
								}
							}else{
								console.log("1:"+currentPage);
								$("#wkretirItemModal").modal("hide");
									
								uTable.fnClearTable(0);		//清空資料
								//新增時,查詢項目空白,要塞新增的身分證
								if (id == 'subPAWKRETIR_Insbtn'){
									$("#qry_idtno").val($("#wkretir_payidno").val());
									$("#qry_patradt").val($("#wkretir_patradt").val());
									console.log($("#qry_idtno").val());
								}
								$("#btn_qryWkRetir").trigger( "click" );

// 								uTable.fnDraw(false);
								uTable.fnPageChange(currentPage,true);		//切到原先某碼<==失效,待找
								
								console.log("2:"+$('#TblWkretirList').dataTable().api().page.info().page);	
							}
						},
						error: function(xhr){					
							alert("補充保險費 更改/新增 失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
						}
					});
					
					
				}
				else{
					alert(msg);
					return false;
				}
			}
		}
	});
	//輸入id,新增才能執行
	$(".findkey").blur(function(){
		if($(this).attr("readOnly")==undefined || $(this).attr("readOnly")==false){	//未設定readOnly屬性
			var data_val=$(this).val().toUpperCase();
			var data_nm =$(this).attr('name');
			var data_id =$(this).attr('id');
			tblname = data_id.substring(0,data_id.indexOf('_'));
			in_data="";
			if ( data_val != ""){
				in_data={
		        		data_val: data_val,
		        		data_nm : data_nm,
		        		hid     : pahid,
		        };
		        
				if (!$("#subPAWKRETIR_Insbtn").prop("disabled")){
					$.ajax({				
							url:"${pageContext.request.contextPath}/salController.do",
							data:{
								method   : 'handelWkRetir',
								fun      : 'getBasByKey',
								in_data  : JSON.stringify(in_data),
								hid      : pahid	
							},
			
						type:"POST",		//type:POST, GET(default:GET)
						dataType:"json",	//dataType: text, xml, html, script, json, jsonp
						contentType: "application/x-www-form-urlencoded;charset=utf-8",  
						async: false,
						cache: false,			
						success:function(data) {
							if (data.basPsnItem!=null ){
								$.doWkretirDetail(data.basPsnItem,"ins");
							}	
						},
						error: function(xhr){	
							alert("搜尋[輸入 KEY]過程中，程式發生錯誤!!!\nerror code:"+ xhr.status);
						}
					});
				}
				
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
  	
	//下拉式投保級距 => txt 公提
	$(".ref").change(function(){
		var inref=$(this).attr('id');
		var inig=inref.substring(0,12)+'ig';
 		var tmp=(typeof($('#'+ inref + ' option:selected').val()) == "undefined" )? "":$('#'+ inref + ' option:selected').val();
 		$('#' + inig).val(tmp.split(':')[0]);
 		$('#wkretir_masteramt').val(tmp.split(':')[1]);	
 		$("#wkretir_selfexrate").trigger("change");
	});
	//自提提繳率,1.上限, 2.[計算]自繳退休金 
 	$("#wkretir_selfexrate").change(function(){
 		var wrinig = $('#wkretir_wrinig').val();
 		var selfvol = parseFloat($(this).val());
 		var tmp;
 		if (wrinig!=""){
 			if (selfvol > wkRateMax){
 				alert("自提提繳率 [超過]上限."); 				
 				return false;
 			}
 			tmp= Math.round( wrinig * selfvol / 100);	
 		}
 		$("#wkretir_selfamt").val( tmp );
 	});
	//勞工退休金
 	var objCopyA = $.extend(true, {aaSorting: []}, dataTableConfig);
 	
	objCopyA.aoColumns = [	
					{ "sTitle":"","mData": null},
					{ "sTitle":"發薪日","mData": "patradt"},
					{ "sTitle":"身分證","mData": "payidno"},
					{ "sTitle":"卡號","mData": "paycard"},
					{ "sTitle":"姓名","mData": "paynamec"},
					{ "sTitle":"階級","mData": "payrank"},
					{ "sTitle":"職稱","mData": "paytitle"},
					{ "sTitle":"職稱中文","mData": "paytitlp"},					
					{ "sTitle":"級距","mData": "monpay"},
					{ "sTitle":"公提","mData": "masteramt"},
					{ "sTitle":"自提率","mData": "selfexrate"},
					{ "sTitle":"自繳退休金","mData": "selfamt"},
					{ "sTitle":"flag","mData": "payflag"},
					{ "sTitle":"分類","mData": "paysort"},
					{ "sTitle":"補發月數","mData": "paymonno"},
					{ "sTitle":"補發日數","mData": "paydayno"},
					{ "sTitle":"UsrID","mData": "padutyid"},
					{ "sTitle":"UPDtm","mData": "payupdat"},
				 ];
						 						 		
	objCopyA.iDisplayLength=10;
						    
	objCopyA = $.extend(true,objCopyA, {
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
		buttons: [
           'excelHtml5' 
        ],
        "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
		 	$('td:eq(6)', nRow).html('<a href = "#wkretirData" class= "wkretirData">'+aData.paycard+'</a>' );
		 	$('td:eq(0)', nRow).html( '<button type="button" class="delWkretirBtn btn btn-danger"><i class="fa fa-minus"></i></button>');
		 },
	 	"fnDrawCallback": function () {
	 		/* //第1欄序列
	 		var api = this.api();
			var startIndex=0;
            api.column(0).nodes().each(function(cell, i) {
               	cell.innerHTML = startIndex + i + 1;
            }); */
	             
			//到某頁
		 	$("#TblWkretirList_paginate").append("  到第 <input style='height:28px;line-height:28px;width:40px;' class='margin text-center' id='changePageA' type='text'> 頁  <a class='btn btn-success shiny' style='margin-bottom:5px' href='javascript:void(0);' id='dataTable-btn'>確認</a>");
		 	oTable = $("#TblWkretirList").dataTable();
		 	$('#dataTable-btn').click(function(e) {
		 		if($("#changePageA").val() && $("#changePageA").val() > 0) {
		 			var redirectpage = $("#changePageA").val() - 1;
		 		} else {
		 			var redirectpage = 0;
		 		}	
		 		oTable.fnPageChange(redirectpage);		
		 	});
		 	//刪除	
			$(".delWkretirBtn")
				.click(function(){
					delCheck = confirm("請確認是否刪除！！\n\n若是，請按【確定】．\n\n若不是，請按【取消】");
					if (delCheck) {		
						oTable = $('#TblWkretirList').dataTable().api();
						uTable = $('#TblWkretirList').dataTable();
						info = oTable.page.info();		//記錄目前頁碼
						currentPage=info.page;
				        tr = $(this).parents('tr');
				        rowIndex = oTable.row(tr).index();
				        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data
				        tblname='PAWKRETIR';
				        
				        pk_data={
				        		"patradt"  :rowData.patradt,
				        		"payidno"  :rowData.payidno,
				        		"paysort"  :rowData.paysort,
				        		"hid"      :pahid,
				        };
				        //return false;
				        $.ajax({
							url:"${pageContext.request.contextPath}/salController.do",
							data:{
								method   : 'handelWkRetir',
								fun      : 'updWkretir',
								editMode : 'delete',
								pk_data  : JSON.stringify(pk_data),
								tblname  : tblname,
								tabJson  : jsonString,
								hid      : pahid,
								userid   : paUserid,
							},
							
							type:"GET",			//type:POST, GET(default:GET)
							dataType:"json",	//dataType: text, xml, html, script, json, jsonp
							contentType: "application/x-www-form-urlencoded;charset=utf-8",  
							success:function(data) {
								uTable.fnClearTable(0);		//清空資料
								$("#btn_qryWkRetir").trigger( "click" );
								uTable.fnPageChange(currentPage);		//切到原先某碼
							},
							error:function(xhr) {
								alert("刪除失敗，程式發生錯誤!!!\nerror code:"+ xhr.status);  
							}
						});
					}
				});
	 	}	
	});
	$('#TblWkretirList').dataTable(objCopyA);
	//顯示pa.pawkretir明細
	$('#TblWkretirList tbody').on('click', '.wkretirData', function () {
		oTable = $('#TblWkretirList').dataTable().api();
		info = oTable.page.info();		//記錄目前頁碼
		currentPage=info.page;
        var tr = $(this).parents('tr');
        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data
        $.doWkretirDetail(rowData,"upd");        
    });
	//勞工退休金
    $.doWkretirDetail = function(rowData,flag){    
     	$("#wkretir_payidno").val(rowData.payidno);
     	$("#wkretir_paycard").val(rowData.paycard);
     	$("#wkretir_paynamec").val(rowData.paynamec);
     	$("#wkretir_paytitle").selectpicker('val',rowData.paytitle);
		$("#wkretir_hid").val(rowData.hid);
     	
		if (flag=="upd"){
			$("#wkretir_patradt").val(rowData.patradt);
			$("#wkretir_paysort").val(rowData.paysort);
			$("#wkretir_wrinref").selectpicker('val',rowData.monpay+":"+rowData.masteramt);
			$("#wkretir_wrinig").val(rowData.monpay);
			$("#wkretir_payrank").val(rowData.payrank);
			$("#wkretir_masteramt").val(rowData.masteramt);
			$("#wkretir_selfexrate").val(rowData.selfexrate);
			$("#wkretir_selfamt").val(rowData.selfamt);
			$("#wkretir_paymonno").val(rowData.paymonno);
			$("#wkretir_paydayno").val(rowData.paydayno);
			$("#wkretir_padutyid").val(rowData.padutyid);
			$("#wkretir_payupdat").val(rowData.payupdat);
			$("#wkretir_payflag").val(rowData.payflag);
			
			//modal標題:卡號+姓名+發薪日期
			$("#wkretirItemModal").find('.modal-title').text("修改[勞工退休金] "+rowData.paycard+rowData.paynamec+rowData.patradt);
		
			$(".tblEditgrp").hide();
			//將key設成readOnly
			$("#wkretir_patradt").attr("readOnly",true);
			$("#wkretir_payidno").attr("readOnly",true);
			$("#wkretir_paysort").attr("readOnly",true);	
			
			$("#PAWKRETIR").show();
				
			$("label.error").hide();
			$(".error").removeClass("error");
	        $("#wkretirItemModal").modal("show");
			$("#subPAWKRETIR_Insbtn").prop("disabled", true);
			$("#subPAWKRETIR_Updbtn").prop("disabled", false);
		}
		else if (flag=="ins"){
			//modal標題:卡號+姓名
			$("#wkretirItemModal").find('.modal-title').text("新增[勞工退休金] "+rowData.paycard+rowData.paynamec);
			
			$("#wkretir_wrinig").val(rowData.insuamt);
			$("#wkretir_payrank").val(rowData.rank);			
		}	
	};
});

</script>
</head>
<body>

<ul class="nav nav-pills mb-3" id="pills-tab" role="tab">
  <li class="nav-item">
    <a class="nav-link active" id="pills-wkProd-tab" data-toggle="pill" href="#bylabProdMain" role="tab" aria-controls="pills-home" aria-selected="true">產製</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-wkRetir-tab" data-toggle="pill" href="#byWkRetirMain" role="tab" aria-controls="pills-profile" aria-selected="false">勞工退休金</a>
  </li>
</ul>

<div class="tab-content" id="pills-tabContent">
	<!-- 產製 -->
	<div class="tab-pane fade show active" id="bylabProdMain" role="tabpanel" aria-labelledby="pills-wkProd-tab">
		<div class="row" >
			<div class="col-10">
				<div class="form-inline">
					<div class="input-group">
						<span class="input-group-text">項目</span>	
						<select id="wk_cond" class="form-control-sm" title="條件">
							<option value="1:[寫入]勞工退休金:Lab_1">1:[寫入]勞工退休金</option>
							<option value="2:[刪除]勞工退休金:Lab_2">2:[刪除]勞工退休金</option>
		     				<option value="3:[寫入]批次檔:Lab_3">3:[寫入]批次檔</option>
		     				<option value="4:[刪除]批次檔:Lab_3">4:[刪除]批次檔</option>
						</select>
						
						<label for="wk_SalNum" >餉單編號-發薪日</label>
						<select id="wk_SalNum" class="form-control" title="餉單編號-發薪日">
							
						</select>
					</div>
				</div>
			</div>	
			<div class="col-2">
				<button id="btn_prodWk" class="btn btn-info roleQry" type="button" ><i class="fa fa-search"></i>執行</button>									
			</div>	
		</div>
		<div class="row">
			<div class="col-md-12">
				<div id="exp_1" class="text-danger">1.以發薪明細(勞保身分),找人事保險檔.勞退級距<br>2.工級2026/7/17自提勞退<br>3.留停沒有自提<BR>4.找不到人事保險檔,flag=X</div>
				<div id="exp_3" class="text-danger d-none">1.以勞工退休金檔有[自願提繳率],寫入至批次檔VD060</div>
			</div>
		</div>
	</div>
	<!--勞工退休金-->
  	<div class="tab-pane fade" id="byWkRetirMain" role="tabpanel" aria-labelledby="pills-wkRetir-tab">
		<div class="container-fluid">
			<div class="row" >
				<div class="col-9">
					<div class="form-inline">
		    			<div class="input-group">
							<input type="text" id="qry_patradt" size="10" class="form-control-sm gclsCalTxt" placeholder="發薪日">
							<input type="text" id="qry_cardno" size="4" class="form-control-sm text-uppercase" placeholder="卡號" >
							<input type="text" id="qry_idtno" size="10" class="form-control-sm text-uppercase" placeholder="身分證" >
						</div>
					</div>	
				</div>		
				<div class="col-3">
					<button id="btn_qryWkRetir" class="btn btn-info" type="button" ><i class="fa fa-search"></i>查詢</button>
					<button id="btn_addWkRetir" class="btn btn-success ml-auto roleQry" type="button" data-toggle="modal" data-target="#wkretirItemModal"><i class="fa fa-edit"></i>新增一筆</button>
					<button id="btn_clnWkRetir" class="btn btn-warning" type="button"><i class="fa fa-eraser"></i>清空</button>
				</div>
			</div>
			<div class="row">
				<div class="col-12 border border-dark">
					<!-- pa.pawkretir清單 -->
					<table id="TblWkretirList" class="table table-striped table-bordered table-hover" >
						<thead>							
						</thead>
					</table>
				</div>
			</div>
		</div>
	</div>
	<!-- 勞工退休金 -->
	<div class="modal fade"  id="wkretirItemModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog" >
			<!-- Modal content-->
			<div class="modal-content" id="wkretirContent">
				<form id="subPAwkretirForm">
					<INPUT type="hidden" id ="wkretiritem"  value="">
					<div class="modal-header">
						<h5 class="modal-title">勞工退休金</h5>
						<button type="button" class="close" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">×</span></button>
					 </div>
					<div class="modal-body">
						<div class="container-fluid">
							<table id="PAWKRETIR" class="table table-primary  table-striped table-bordered table-sm caption-top tblEditgrp" style="text-align:center;">
								<INPUT type="hidden" id ="wkretir_hid"  value="">
								<tbody>
			            		<tr>
			            			<th>*發薪日</th>		
			            			<Td class="text-left"><input type="text" id="wkretir_patradt" name="patradt" class="form-control gclsCalTxt required" maxlength="10" size="10"/></td>
			            			<th>*身分證字號</th>	
			            			<Td><input type="text" id="wkretir_payidno" name="payidno" class="form-control findkey text-uppercase required" maxlength="10" size="10"  /></Td>
			            			<th>卡號</th>	
			            			<Td><input type="text" id="wkretir_paycard" name="paycard"class="form-control findkey text-uppercase" maxlength="4" size="4"  /></Td>
			            			</tr>
			            		<tr>
			            			<th>姓名</th>	
			            			<Td><input type="text" id="wkretir_paynamec" name="paynamec"class="form-control text-uppercase " maxlength="12" size="12" readonly /></Td>
		            				<th>階級</th>
			            			<Td><input type="text" id="wkretir_payrank" name="payrank" class="form-control" maxlength="11" size="11"  /></Td>	
			            			<th>職稱</th>
			            			<td>
									<select id="wkretir_paytitle" name="paytitle" class="form-control selectpicker" disabled title="職稱">
									</select>
			            			</td>
		            			</tr>
		            			<tr>
			            			<th>*性質</th>
			            			<Td><select id="wkretir_paysort" name="paysort" class="form-control required" >
			            					<option value=""></option> 
											<option value="1">固定</option>
							     			<option value="2">補發</option>						     		
								     	</select></Td>
								    <th>補發天數</th>
								    <Td class="d-flex align-items-center gap-2"><input type="text" id="wkretir_paymonno" name="paymonno" class="form-control" maxlength="2" size="2" value="0" />月
								    <input type="text" id="wkretir_paydayno" name="paydayno" class="form-control" maxlength="2" size="2"  value="0" />日</Td>	
		            				<th>旗標</th>	
			            			<Td><input type="text" id="wkretir_payflag" name="payflag" class="form-control" maxlength="1" size="1" readonly /></Td>
			            			
		            			</tr>
			            		<tr>
			            			<th>月提繳金額</th>	
			            			<Td class="d-flex align-items-center gap-2">
				            			<select id="wkretir_wrinref" class="form-control selectpicker ref required" style="width:auto;" >
					            		</select>
					            		<input type="text" id="wkretir_wrinig" name="monpay" class="form-control required" maxlength="5" size="5"  />
									</Td>
									<th>公提退休金6%</th>	
			            			<Td><input type="text" id="wkretir_masteramt" name="masteramt" class="form-control required" maxlength="5" size="5"  /></Td>
		            				<th>UsrId</th>
			            			<Td><input type="text" id="wkretir_padutyid" name="padutyid"class="form-control" maxlength="4" size="4" readonly /></Td>
			            			
		            			</tr><tr>		
			            			<th>自提提繳率</th>
			            			<Td class="d-flex align-items-center gap-2"><input type="text" id="wkretir_selfexrate" name="selfexrate" class="form-control" maxlength="5" size="5"  />%</Td>
				            		<th>自提退休金</th>
				            		<Td><input type="text" id="wkretir_selfamt" name="selfamt" class="form-control" maxlength="5" size="5"  /></Td>
				            			
			            			<th>時戳</th>
			            			<Td><input type="text" id="wkretir_payupdat" name="payupdat"class="form-control" maxlength="19" size="19" readonly /></Td>
			            		</tr>
			            		</tbody>
							</table>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-primary" id="subPAWKRETIR_Insbtn">確認儲存</button>
				        <button type="button" class="btn btn-primary" id="subPAWKRETIR_Updbtn">更改</button>
					   	<button type="button" class="btn btn-secondary" data-dismiss="modal">返回</button>
					</div>
				</form>	
			</div>
		</div>
	</div>			
</div>

</body>
</html>