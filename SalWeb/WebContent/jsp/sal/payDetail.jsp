<!DOCTYPE HTML>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<html>
<head>
<title>發薪明細/檢驗</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<jsp:include page="../include.jsp"></jsp:include>
<script>
var tabid="";
var objCopy;

var code_data;
var codeTbl;
var oTable;
var paybank;
var salnum;
var hid;
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
				if (data.alDeptCodeTbl[0]!=null){
					select = $('#qryDept');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alDeptCodeTbl, function(idx) {
						options = "<option value='" + data.alDeptCodeTbl[idx].padepcod + "'>" + data.alDeptCodeTbl[idx].pacstno+"-"+data.alDeptCodeTbl[idx].padepnmc ;
						select.append(options);
					});
					select.selectpicker('refresh');
				}
				

				if (data.alBankDtTbl[0]!=null){
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
				}
 
			}  
		});
	};
/* 	/*預先載入代碼檔 */
  	$.getLoadCodeTbl('payDetail');
 	
	//編輯欄位檢查
  	$.checkFields = function(btn){
  		msg = '';
 		if($("#qryCardno").val()=='' && $("#qryIdtno").val()=='' && $("#qryNamec").val()=='' && $("#qryEmpno").val()=='' && $("#qry_padatpsn_salnum option:selected").val()=='' && $("#qry_padatpsn_paybank option:selected").val()=='' && $("#qry_payrank option:selected").val()==''){
 			msg = '請輸入條件值!!';
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
	}	
		//jQuery按紐圖示
	$(".btn").button().click(
		function(event) {event.preventDefault();}
	);
	$(".btn").click(function(){
		var id = $(this).attr('id');
		
		if(id == 'btn_clnDatPsn' || id == 'btn_clnCheck'){
			$(":text,textarea").val("");
			$(":checkbox,:radio").prop("checked", false);
			console.log("ss:"+$('#tfoot_taxpay').html());
			//所有select預設選擇第一項			 
			$('#qryCheckItem select').each(function() {
				$(this).prop("selectedIndex", 0);
		    });
		    $('.selectpicker').selectpicker('deselectAll');
		    $('.selectpicker').selectpicker('refresh');
		    
		    		    
		    $("#TblDatPsnList").dataTable().fnClearTable();
			$("#TblDatPaySum").dataTable().fnClearTable();
			$("#TblCheckPayList").dataTable().fnClearTable();
			
			return false;
		}
		else if(id == 'btn_qryDatPsn'){		//查詢
			msg = $.checkFields();
			if(msg == ''){
				var cardno=$("#qryCardno").val().toUpperCase();
				var idtno=$("#qryIdtno").val().toUpperCase();
				var namec=$("#qryNamec").val();
				var empno=$("#qryEmpno").val().toUpperCase();
				salnum=$.getSelectVals("qry_padatpsn_salnum");				
				paybank=$.getSelectVals("qry_padatpsn_paybank");
				var payrank=$.getSelectVals("qry_payrank");
				var paytitle=$.getSelectVals("qryTitle");
				var paydept=$.getSelectVals("qryDept");
				
 				console.log("卡號:"+cardno+" 身分證:"+idtno+" 姓名:"+namec+" 職號:"+empno+" salnum:"+salnum+" paybank:"+paybank+" payrank:"+payrank+" paytitle:"+paytitle+" paydept:"+paydept);
// 				return false;
				code_data="";
				code_data={
		        		cardno     :cardno,
		        		idtno      :idtno,
		        		namec      :namec,
		        		empno      :empno,
		        		salnum     :salnum,
		        		paybank    :paybank,
		        		payrank    :payrank,
		        		paytitle   :paytitle,
		        		paydept    :paydept
		        	};
				console.log("發薪明細查詢:code_data:"+code_data);
				
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'getDatPsnList',
						code_data :JSON.stringify(code_data)
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
						$("#TblDatPsnList tbody tr:first").find("a").eq(0).trigger("click");
						
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
			msg = $.checkFields();
			if(msg == ''){
				salnum=$("#qry_check_salnum").val();
				paybank=$("#qry_check_paybank").val();
				
				console.log(" salnum:"+salnum+" paybank:"+paybank);
// 				return false;
				code_data="";
				code_data={
						salnum     :salnum,
						paybank    :paybank
					};
				console.log("檢驗查詢:code_data:"+code_data);
				
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'getCheckPayList',
						code_data :JSON.stringify(code_data)
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
	});
	
	//月支個人明細
 	var objCopyA = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyA.aoColumns = [	
							{ "mData": "paycard" },
							{ "mData": "paybank" },
							{ "mData": "paynamec" },
							{ "mData": "pacstno" }
						 ];
	
	objCopyA = $.extend(true,objCopyA, {
				 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
						$('td:eq(0)', nRow).html( '<a href = "#datpsnData" class= "datpsnData">'+aData.paycard+'</a>' );
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
	$('#TblDatPsnList').dataTable(objCopyA);
	
	//顯示pa.padatpsn明細
	$('#TblDatPsnList tbody').on('click', '.datpsnData', function () {
		oTable = $('#TblDatPsnList').dataTable().api();
		info = oTable.page.info();		//記錄目前頁碼
		currentPage=info.page;
        var tr = $(this).parents('tr');
        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data	 		
		var idtno=rowData.payidno;
		salnum=rowData.salnum;
		hid=rowData.hid;
		//找pa.padatpsn及pa.padaypay
		code_data="";
		code_data={
        		idtno      :idtno,
        		salnum     :salnum,
        		hid        :hid
        	};
		console.log("發薪明細padatpsn查詢:code_data:"+code_data);
		
		$.getDayPsnByKey(code_data);
		
	});
	
	//依KEY找出pa.padatpsn
	$.getDayPsnByKey = function(code_data){
		$.ajax({
			url:"${pageContext.request.contextPath}/salController.do",
			data:{
				method  : 'getDayPsnByKey',
				code_data :JSON.stringify(code_data)
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
				}
			}  
		}) 
	};
	//顯示pa.padatpsn明細
	$.doDatpsnDetail = function(mData){
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
		$("#datPsn_pfkey").val(mData.pfkey);
		$("#datPsn_pfbas").val(mData.pfbas);
		$("#datPsn_pfdat").val(mData.pfdat);
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
		if (mData.hid=='1A0'){
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
		oTable.fnDraw();
		
	};
	
	//顯示pa.paDatpay 應稅所得 淨所得
	$.doDatpaySum = function(mData){
		$('#datPay_taxpay').val(mData.taxpay);
		$('#datPay_netpay').val(mData.netpay);
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
	$('#TblDatPaySum').dataTable(objCopyB);
		      
	//檢驗sum
 	var objCopyC = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyC.aoColumns = [	
							{ "mData": null},
							{ "mData": null},
							{ "mData": null },
							{ "mData": "payamtp" },
							{ "mData": "payamtd" }
						 ];
						 
	objCopyC.iDisplayLength=100;
	
	objCopyC = $.extend(true,objCopyC, {
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
                    
                    					 
	objCopyC.aoColumnDefs= [
 							{
							   'targets': 2,
							   'searchable':false,
							   'orderable':false,
							   'className': 'dt-body-left',
							   'render': function (data, type, row, meta){
							   				var color;
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
							   'targets': [ 3,4],
							   'className': 'dt-body-right'
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
	$('#TblCheckPayList').dataTable(objCopyC);
	
	$('#TblCheckPayList tbody').on('click', '.paydata', function () {
		oTable = $('#TblCheckPayList').dataTable().api();
		info = oTable.page.info();		//記錄目前頁碼
		currentPage=info.page;
        var tr = $(this).parents('tr');
        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data	 		
		salnum=rowData.salnum;
		var paycode=rowData.paycode;
		var pacodnmc=rowData.pacodnmc;
		//依salnum+paycode找pa.padaypay清單
		code_data="";
		code_data={
        		salnum     :salnum,
        		paycode    :paycode
        	};
		console.log("[檢驗]依salnum+paycode找pa.padaypay清單:code_data:"+code_data);
		
		$.getDayPayByCode(code_data,paycode,pacodnmc);
		
	});
		 
	//依salnum+paycode找pa.padaypay清單
	$.getDayPayByCode = function(code_data,paycode,pacodnmc){
		$.ajax({
			url:"${pageContext.request.contextPath}/salController.do",
			data:{
				method  : 'getDayPayByCode',
				code_data :JSON.stringify(code_data)
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
		var table = $('#TblDatPayList').DataTable();
 		$('#container').css( 'display', 'block' );
		table.columns.adjust().draw();	
	};
	
	//檢驗list
 	var objCopyD = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyD.aoColumns = [	
							{ "mData": null},
							{ "mData": "paycard"},
							{ "mData": "payidno"},
							{ "mData": "payempno"},
							{ "mData": "padepnmc"},
							{ "mData": "paytitlc"},
							{ "mData": "paynamec"},
							{ "mData": "payrnk2n"},
							{ "mData": "payrank3"},
							{ "mData": "paydegr2"},
							{ "mData": "paydegre"},
							{ "mData": "payrank2"},
							{ "mData": "payamt"},
						 ];
						 
	objCopyD.iDisplayLength=30;
						    
	objCopyD = $.extend(true,objCopyD, {
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr form-inline"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br form-inline"ip>',
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
		 
		            // Total over this page
		            var pTotal = api
		                .column( 12	, { page: 'current'} )
		                .data()
		                .reduce( function (a, b) {
		                    		return intVal(a) + intVal(b);
		                		}, 0 );
		 
		            // Update footer
		            $( api.column( 12 ).footer() ).html(
		                '總金額:'+pTotal 
		            );
				}   							
			});   
			
	$('#TblDatPayList').dataTable(objCopyD);		      
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
</ul>

<div class="tab-content" id="pills-tabContent">
	<!--發薪明細-->
  	<div class="tab-pane fade show active" id="byPayDetailMain" role="tabpanel" aria-labelledby="pills-pay-tab">
		<div class="container-fluid">
			<div class="row" id="qryPsnDetailItem">
				<div class="col-10">
					<div class="form-inline">
		    			<div class="input-group">
		    				<select id="qry_padatpsn_salnum"  class="selectpicker SALNUM" data-width="fit" multiple title="餉單編號">
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
							</select>
		    				<input type="text" id="qryCardno" size="4" class="form-control-sm text-uppercase" placeholder="卡號" >
							<input type="text" id="qryIdtno" size="10" class="form-control-sm text-uppercase" placeholder="身分證" >
							<input type="text" id="qryNamec" size="12" class="form-control-sm " placeholder="姓名" >
							<input type="text" id="qryEmpno" size="7" class="form-control-sm text-uppercase" placeholder="職號" >
							<select id="qryDept" name="qryDept" class="selectpicker JTCODE" data-width="fit" data-live-search="true" multiple title="單位">
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
							
						</div>		    		
			    		
					</div>
				</div>
				<div class="col-2">
					<button id="btn_qryDatPsn" class="btn btn-success" type="button" ><i class="fa fa-search"></i>查詢</button>
					<button id="btn_clnDatPsn" class="btn btn-warning" type="button"><i class="fa fa-eraser"></i>清空</button>
				</div>
			</div>	
			<div class="row">
				<div class="col-4 border border-dark">
					<div id="datpsnListDiv_1" class="datpsnListDiv">
					<!-- pa.padatpsn清單 -->
					<table id="TblDatPsnList" class="table table-striped table-bordered table-hover" >
						<thead>
							<tr>
								<th>卡號</th>
								<th>發薪日</th>
								<th>姓名</th>
								<th>單位</th>
							</tr>
						</thead>
					</table>				
					</div>
				</div>
				
 				<div class="col-8 border border-dark">
 					<div class="container-fluid">
 						<div class="row justify-content-md-center  form-inline border-dark">
<!--  					<div class="col-12 border border-dark"> -->
 					<!-- pa.padatpsn明細 -->
		 					<div class="table-responsive-sm">
		 								 
			 					<table id="tblDatPsnData" class="table table-striped table-bordered table-hover table-sm" >
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
												</div>
												<div id="div_thing" class="form-group d-none">
													<label for="datPay_thing_money" class="form-inline">實物代金:</label>
													<input type="text" id="datPay_thing_money" class="form-control text-info" maxlength="3" size="3"  />
													<label for="datPay_rent_house" class="form-inline">房租:</label>
													<input type="text" id="datPay_rent_house" class="form-control text-info" maxlength="3" size="3"  />
												</div>
											</div>
											
											</td>
										</tr>
									</tbody>
			 					</table>
		 					</div>
	 					</div>
	 					<div class="row border border-dark  form-inline">
	 						<div class="col-12 border border-dark">
								<!-- pa.padatpay清單 -->
								<table id="TblDatPaySum" class="table table-striped table-bordered table-hover" >
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
 				</div>

 			</div>
 		</div>	
  </div>
  
  <!--檢驗-->
  <div class="tab-pane fade" id="byCheckMain" role="tabpanel" aria-labelledby="pills-check-tab">
	<div class="container-fluid">
		<div class="row" id="qryCheckItem">
			<div class="col-10">
				<div class="form-inline">
	    			<div class="input-group">
	    				<select id="qry_check_salnum"  class="selectpicker SALNUM" data-width="fit" title="餉單編號">
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
						</select>
					</div>		 
				</div>
			</div>
			<div class="col-2">
				<button id="btn_qryCheck" class="btn btn-success" type="button" ><i class="fa fa-search"></i>查詢</button>
				<button id="btn_clnCheck" class="btn btn-warning" type="button"><i class="fa fa-eraser"></i>清空</button>
			</div>
		</div>
		<div class="row">
			<div class="col-12 border border-dark">
				<table id="TblCheckPayList" class="table table-striped table-bordered table-hover" >
					<thead>
						<tr>
							<th>項次</th>
							<th>支扣代碼</th>
							<th>支扣內容</th>
							<th>所得</th>
							<th>支出</th>
						</tr>
					</thead>
					<tfoot>
			            <tr>
			            	<th colspan=4 style="text-align:right"></th>
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
				<div class="container">
					<!-- pa.padatpay清單 -->
					<table id="TblDatPayList" class="table table-striped table-bordered table-hover" >
						<thead>
							<tr>
								<th></th>
								<th>卡號</th>
								<th>身分證字號</th>
								<th>職號</th>
								<th>單位</th>
								<th>職稱</th>
								<th>姓名</th>
								<th>職等</th>
								<th>本功</th>
								<th>級別</th>
								<th>俸點</th>
								<th>權理</th>
								<th>金額</th>
							</tr>
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
  
  
  
  
</div>

</body>
</html>