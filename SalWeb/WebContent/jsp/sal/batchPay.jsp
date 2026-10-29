<!DOCTYPE HTML>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<html>
<head>
<title>批次檔</title>
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
var firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);

var tabid="";
var objCopy;
var in_data;
var codeTbl;
var oTable;
var patradt;
var salnum;
var cardno;
var paycode;
var salNm;
var selEn;
var selPs;
var selCode; 
var hid;
var rowData;
var msg;
var patradt;
var blnCheck;
$(function(){
	$('#collapseOne').collapse('show');
		
	$('body').on('focus', '.datepicker', function() { //以此方式才能動態更改頁面datepicker
          $(this).datepicker( { dateFormat: 'yy-mm-dd' } );
    });
	$(".gclsCalTxt").datepicker({ 
		changeMonth: true,
      	changeYear: true,
      	dateFormat:"yy-mm-dd"
    });
    $(".dateBat").datepicker({ 
		changeMonth: true,
      	changeYear: true,
      	showOtherMonths : true,
	    hideIfNoPrevNext : true,
	    showAnim:"blind",
      	dateFormat:"yy-mm-dd",
      	minDate: 0, 
      	maxDate:"+3M"
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
	//
	if (pahid=="2A0" || pahid=="3A0") {
		$("#pills-teacheraw-tab").hide();
	}else if (pahid=="1A0") {
		$("#pills-teacheraw-tab").show();
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
				//職稱
				if (data.alTitleCodeTbl[0]!=null){
					select = $('#batpay_paytitle');
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
					select = $('#batpay_paydept');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alDeptCodeTbl, function(idx) {
						options = "<option value='" + data.alDeptCodeTbl[idx].padepcod + "'>" + data.alDeptCodeTbl[idx].pacstno+"-"+data.alDeptCodeTbl[idx].padepnmc ;
						select.append(options);
					});
					select.selectpicker('refresh');
				}
				
				//餉單編號#qry_pabatpay_salnum,
				if (data.alBankDtTbl[0]!=null){
					select = $('#batpay_salnum');
					select.empty();
					options = "<option value='0'>無餉單編號";
					select.append(options);
					$.each(data.alBankDtTbl, function(idx) {
						options = "<option value='" + data.alBankDtTbl[idx].salnum + "'>" + data.alBankDtTbl[idx].salnum ;
						select.append(options);
					});
					select.selectpicker('refresh');
					
// 					select = $('#qry_pabatpay_patradt');
// 					select.empty();
// 					$.each(data.alBankDtTbl, function(idx) {
// 						options = "<option value='" + data.alBankDtTbl[idx].paybank.dts + "'>" + data.alBankDtTbl[idx].paybank.dts ;
// 						select.append(options);
// 					});
// 					select.selectpicker('refresh');
				}
				//支扣項目 
				if (data.alpayitmTbl[0]!=null){
					select = $('#qry_pabatpay_paycode,#batpay_paycode,#handing_paycode');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alpayitmTbl, function(idx) {
						options = "<option value='" + data.alpayitmTbl[idx].paycode+":"+data.alpayitmTbl[idx].paupbas+ "'>" + data.alpayitmTbl[idx].paycode+"-"+data.alpayitmTbl[idx].pacodnmc ;
						select.append(options);
					});
					select.selectpicker('refresh');
				}
			}  
		});
	};
	/*預先載入代碼檔 */
  	$.getLoadCodeTbl('batchPay','Y');
  	
  	//代碼選單切換
	$("#qryType").change(function(){
		$("#divfield_1,#divfield_2,#divfield_3").addClass("d-none");	//隱藏
		cleanTable("rowBatHanding_1");
  		selCode=(typeof($("#qryType option:selected").val().split(':')[0]) == "undefined" )? "1":$("#qryType option:selected").val().split(':')[0];
  		console.log(selCode);
  		if (selCode=="1" ){			//1:[匯入]批次檔 
			$("#divfield_1,#divfield_2,#divfield_3").removeClass("d-none");
		}
		else if (selCode=="2"){		//2:[刪除]批次檔 
			$("#divfield_1").removeClass("d-none");			
		}	 
  	});
  	 	
	//編輯欄位檢查
  	$.checkFields = function(btn){
  		msg = '';
  		var isChecked=0;
  		if (btn== 'btn_qryBatPay'){	//批次檔_查詢
  		//( $("#qry_pabatpay_salnum option:selected").val()=='' || typeof($("#qry_pabatpay_salnum option:selected").val())=='undefined' ) && 
  		//( $("#qry_pabatpay_patradt option:selected").val()=='' || typeof($("#qry_pabatpay_patradt option:selected").val())=='undefined') )
  			if( $("#qry_pabatpay_cardno").val()==''  && $("#qry_pabatpay_idtno").val()==''  && 
  			    $("#qry_pabatpay_salnum").val()==''  && $("#qry_pabatpay_patradt").val()=='' ){
  				msg = '請輸入條件值!!';
  			}
  		}
  		else if (btn == 'subPABATPAY_Insbtn'||btn == 'subPABATPAY_Updbtn'){	//批次檔_新增/更改
  			$("#batpay_paupbas").val( $("#batpay_paycode").val().split(':')[1] );
 		}
  		else if (btn== 'btn_handelBat'){	//批次檔處理
  			if (typeof($("#qryType option:selected").val())=='undefined'){
  				msg = '請勾選[選單]項目!!';
  			}
  			else{
  				selCode=$("#qryType option:selected").val().split(':')[0];
  				if (selCode=='1'){		//1:[匯入]批次檔
  					var file = $("#fileInput").val();			
					if (file.length == 0) {
						msg ='請確認是否有選檔案';				
					}
  					if ($("#handing_paycode").val()=='' || typeof($("#handing_paycode option:selected").val())=='undefined'){
  						if (msg!='') msg +="\n";  						
		  				msg += '請勾選[支扣項目]!!';
		  			}
		  			if ($("#handing_Datyr").val()=='' ){
  						if (msg!='') msg +="\n";  						
		  				msg += '請勾選[發薪日]!!';
		  			}
  				}else if (selCode=='2'){	//2[刪除]批次檔
  					if ($("#handing_paycode").val()=='' || typeof($("#handing_paycode option:selected").val())=='undefined'){
  						if (msg!='') msg +="\n";  						
		  				msg += '請勾選[支扣項目]!!';
		  			}
		  			if ($("#handing_Datyr").val()=='' ){
  						if (msg!='') msg +="\n";  						
		  				msg += '請勾選[發薪日]!!';
		  			}
  				}
  			}	
  		}
  		else if (btn== 'btn_qryLeave'){	//事病假扣薪--查詢
  			if ($("#leave_Datyr").val()=='' ){
				msg += '請勾選[發薪日]!!';
  			}
  		}
  		else if (btn== 'btn_wrtLeave'){	//事病假扣薪--寫入
			var rows = $('#tblPayLeaveList').find('tbody tr');        	
	        $.each(rows, function() {
	            var checkbox = $($(this).find('td').eq(0)).find('input').prop('checked', true);                      
	        });
  		}
  		else if (btn== 'btn_qryTeacheraw'){	//教職及論文獎勵 --查詢
  			if ($("#teacheraw_Datyr").val()=='' ){
				msg += '請勾選[發薪日]!!';
  			}
  		}
  		else if (btn== 'btn_wrtTeacheraw'){	//教職及論文獎勵--寫入
			var rows = $('#tblTeacherawList').find('tbody tr');        	
	        $.each(rows, function() {
	            var checkbox = $($(this).find('td').eq(0)).find('input').prop('checked', true);                      
	        });
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
		
		if(id == 'btn_clnBatPay' ){
			cleanTable("byBatTaskMain");
			$("#tblBatPayList").dataTable().fnClearTable();
			$("#leavepay_select_all").prop('checked', false);
			return false;
		}
		else if(id == 'btn_addBatPay'){		//批次檔_新增一筆
			cleanTable("subPABATPAYForm");
			cleanTable("byBatTaskMain");
			$("#batPayItemModal").find('.modal-title').text("新增[批次檔]");
			//key
			$("#batpay_patradt").prop('disabled', false);	
			$("#batpay_payidno").prop("readOnly",false);	
			$('#batpay_salnum').prop('disabled', false).selectpicker('refresh');
			$('#batpay_paycode').prop('disabled', false).selectpicker('refresh');
			$("#batpay_hid").val(pahid);
			//先塞預設值
			$("#batpay_patradt").val(patradt);
			$("#batpay_salnum").selectpicker('val',salnum);
			$("#batpay_paycode").selectpicker('val',paycode);
			
			$("label.error").hide();
			$(".error").removeClass("error");
	        $("#batPayItemModal").modal("show");
			$("#subPABATPAY_Insbtn").prop("disabled", false);
			$("#subPABATPAY_Updbtn").prop("disabled", true);
		}
		else if(id == 'btn_qryBatPay'){		//批次檔查詢
			msg = $.checkFields(id);
			if(msg == ''){
				cardno=$("#qry_pabatpay_cardno").val().toUpperCase();
				var idtno=$("#qry_pabatpay_idtno").val().toUpperCase();
				paycode=$.getSelectVals("qry_pabatpay_paycode");
				salnum=$("#qry_pabatpay_salnum").val();
				patradt=$("#qry_pabatpay_patradt").val();
				//salnum=$.getSelectVals("qry_pabatpay_salnum");
				//patradt=$.getSelectVals("qry_pabatpay_patradt");
				
 				console.log("卡號:"+cardno+" 身分證:"+idtno+" salnum:"+salnum+" patradt:"+patradt+" paycode:"+paycode);
 				//return false;
				in_data="";
				in_data={
		        		cardno     :cardno,
		        		idtno      :idtno,
		        		salnum     :salnum,
		        		patradt    :patradt,
		        		paycode    :paycode
		        	};
				console.log("批次檔查詢:in_data:"+in_data+" hid:"+pahid);
				
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'handelBatch',
						fun     : 'qryBatPay',
						in_data : JSON.stringify(in_data),
						hid     : pahid,
						userid  : paUserid,
					},
					type:"POST",	//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						//更新查詢清單
						$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
						$('#tblBatPayList').show();
						oTable = $('#tblBatPayList').dataTable(objCopyA);
						
						oTable.fnClearTable(0);		//清空資料
								
						if (data.BatPayList!=null && data.BatPayList.length>0){	
							oTable.fnAddData(data.BatPayList);//重新給資料
						}
						oTable.fnDraw();						
						
						//移至第1筆
						//$("#tblBatPayList tbody tr:first").find("a").eq(0).trigger("click");
						
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
		else if(id == 'btn_qryLeave'){		//事病假扣薪_查詢
			msg = $.checkFields(id);
			if(msg == ''){
				patradt=$('#leave_Datyr').val();;
				console.log(" 發薪日:"+patradt);
				//return false;
				in_data="";
				in_data={
		        		patradt    :patradt
		        	};
				console.log("事病假扣薪_查詢:in_data:"+in_data+" hid:"+pahid);
				
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'handelBatch',
						fun     : 'qryleave',
						in_data : JSON.stringify(in_data),
						hid     : pahid,
						userid  : paUserid,
					},
					type:"POST",	//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						//更新查詢清單 
						$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
						$('#tblPayLeaveList').show();
						oTable = $('#tblPayLeaveList').dataTable(objCopyB);						
						oTable.fnClearTable(0);		//清空資料
								
						if (data.PayLeaveList!=null && data.PayLeaveList.length>0){	
							oTable.fnAddData(data.PayLeaveList);//重新給資料
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
		else if(id == 'btn_qryTeacheraw'){		//教職及論文獎勵 _查詢
			msg = $.checkFields(id);
			if(msg == ''){
				patradt=$('#teacheraw_Datyr').val();;
				console.log(" 發薪日:"+patradt);
				//return false;
				in_data="";
				in_data={
		        		patradt    :patradt
		        	};
				console.log("批次檔查詢:in_data:"+in_data+" hid:"+pahid);
				
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'handelBatch',
						fun     : 'qryteacheraw',
						in_data : JSON.stringify(in_data),
						hid     : pahid,
						userid  : paUserid,
					},
					type:"POST",	//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						//更新查詢清單 
						$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
						$('#tblTeacherawList').show();
						oTable = $('#tblTeacherawList').dataTable(objCopyC);						
						oTable.fnClearTable(0);		//清空資料
								
						if (data.TeacherawList!=null && data.TeacherawList.length>0){	
							oTable.fnAddData(data.TeacherawList);//重新給資料
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
		else if(id == 'subPABATPAY_Updbtn'||id == 'subPABATPAY_Insbtn'){	//批次檔 更改 新增
			if ($("#subPABATPAYForm").valid()){
				msg = $.checkFields(id);
				if(msg == ''){
					//將key取消readOnly
					$("#batpay_patradt").prop("disabled",false);	
					$("#batpay_salnum").attr("readOnly",false);	
					$("#batpay_payidno").attr("readOnly",false);	
					$("#batpay_paycode").attr("readOnly",false);
					
					jsonString = $.getDataInfo("subPABATPAYForm"); //取得目前的資料
					
					pk_data={
		        		patradt   : $('#batpay_patradt').val(),
		        		salnum    : $('#batpay_salnum').val(),
		        		payidno   : $('#batpay_payidno').val(),
		        		paycode   : $('#batpay_paycode').val().split(':')[0],
		        		hid		  : $('#batpay_hid').val()
		        	};
		        	
		        	if (id.substring(id.indexOf('_')+1) =='Updbtn')		
						$('#batpayitem').val("update");
					else if (id.substring(id.indexOf('_')+1) =='Insbtn')		
						$('#batpayitem').val("insert");
						
					tblname=id.substring(3,id.indexOf('_'));	
					oTable = $('#tblBatPayList').dataTable().api();
					uTable = $('#tblBatPayList').dataTable();
					info = oTable.page.info();		//記錄目前頁碼
					currentPage=info.page;
					//return false;
					
					$.ajax({
						url:"${pageContext.request.contextPath}/salController.do",
						data:{
							method   : 'handelBatch',
							fun      : 'updBatPay',
							selNm    : '批次檔',
							editMode : $('#batpayitem').val(),
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
							if (data.status!=null||typeof(data.status)!="undefined"){	//show msg,dialog不用關閉
								alert(data.msg);
							}else{
								console.log("1:"+currentPage);
								$("#batPayItemModal").modal("hide");
								//新增時,查詢項目空白,要塞新增的身分證
								if (id == 'subPABATPAY_Insbtn'){
									
									$("#qry_pabatpay_idtno").val($("#batpay_payidno").val());
									if ($("#batpay_patradt").val() !=""){
										//$("#qry_pabatpay_patradt").selectpicker('val', $("#batpay_patradt").val() );
										$("#qry_pabatpay_patradt").val( $("#batpay_patradt").val() );
									}
									if ($("#batpay_paycode").val() !=""){
										$("#qry_pabatpay_paycode").selectpicker('val', $("#batpay_paycode").val().substring(0,5) );
									}
									console.log('idno:'+$("#qry_pabatpay_idtno").val()+' tradt:'+$("#qry_pabatpay_patradt").val()+' paycode:'+$.getSelectVals("qry_pabatpay_paycode"));
								}
								uTable.fnClearTable(0);		//清空資料
								$("#btn_qryBatPay").trigger( "click" );

// 								uTable.fnDraw(false);
								uTable.fnPageChange(currentPage,true);		//切到原先某碼<==失效,待找
								
								console.log("2:"+$('#tblBatPayList').dataTable().api().page.info().page);  
								
								if (data.msg!=null){
									alert(data.msg);
								}
									
							}
						},
						error: function(xhr){					
							alert("批次檔 更改 新增 失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
						}
					});
						
				}
				else{
					alert(msg);
					return false;
				}
			}
		}
		else if(id == 'btn_handelBat'){	//批次檔 處理
			msg = $.checkFields(id);
			if(msg == ''){
				in_data="";
				//selCode:selNm:selEn 1:[匯入]批次檔:ImpPayInBat
				selCode = $("#qryType option:selected").val().split(':')[0];
				selNm   = $("#qryType option:selected").val().split(':')[1];
				selEn   = $("#qryType option:selected").val().split(':')[2];
				patradt = $('#handing_Datyr').val();
        		paycode = $('#handing_paycode').val().split(':')[0];
        		if (selCode == "1"){
					var paupbas = $('#handing_paycode').val().split(':')[1];
	        		var chkbas = $('#handing_chkbas').val();
					var url="${pageContext.request.contextPath}/salController.do";
		        	var param = '?method=uploadFile' +
								'&selEn=' + selEn +
								'&userid=' + userid +
								'&patradt=' + patradt +
								'&paycode=' + paycode +
								'&paupbas=' + paupbas +
								'&chkbas=' + chkbas +
								'&hid=' + pahid;
			    	console.log(" 選項:"+selCode+" url:"+url+param);
			    }else if (selCode == "2"){
			    	in_data={
							selEn   : selEn,
			        		selNm   : selNm,
			        		patradt : patradt,
			        		paycode : paycode
			        };
			    }
			    //return false;
			    //1:[匯入]批次檔
				if (selCode == 1){
					showLoading(); //顯示loading
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
				//2[刪除]批次檔 //selCode:selNm:selEn 1:[匯入]批次檔:ImpPayInBat
				else{
					blnCheck = confirm("請確認是否刪除！！\n\n若是，請按【確定】．\n\n若不是，請按【取消】");
					if (blnCheck) {	
						//showLoading(); //顯示loading
						$.ajax({
							url:"${pageContext.request.contextPath}/salController.do",
							data:{
								method    : 'handelBatch',
								fun       : 'prodBat',
								in_data   : JSON.stringify(in_data),
								selNm     : selNm,
								userid    : paUserid,
								hid       : pahid
							},
							type:"POST",	//type:POST, GET(default:GET) processData:false,
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
								alert("批次檔處理_"+selEn+" 失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
							},
							complete: function(data) {
							//	hideLoading(); //隱藏loading
							}
						});
					}	
				}   
			}	
			else{
				alert(msg);
				return false;
			}
		}
		else if(id == 'btn_wrtLeave'){	//事病假寫入批次檔
			//msg = $.checkFields(id);
			if(msg == ''){
				itmTbl="tblPayLeaveList";
				
				jsonString = $.getDataInfo(itmTbl); //取得目前的資料
				//return false;
				in_data={
						selEn   : "writeBatByLeav"
		        };					
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method	: "handelBatch",
						fun     : 'prodBat',
						in_data : JSON.stringify(in_data),
						tabJson : jsonString,
						userid  : paUserid,
						hid     : pahid
					},
					type:"POST",			//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					async: true,	//逐步執行
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						if (data.msg!=null){
							//$("#btn_qryA").trigger( "click" ); 
							alert(data.msg);
						}	
					},
					error: function(xhr){					
						alert("寫入[事病假寫入批次檔]失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
					}
				}); 
			}
			else{
				alert(msg);
			}		
		}
		else if(id == 'btn_wrtTeacheraw'){	//教職及論文獎勵 寫入批次檔
			//msg = $.checkFields(id);
			if(msg == ''){
				itmTbl="tblTeacherawList";
				
				jsonString = $.getDataInfo(itmTbl); //取得目前的資料
				//return false;
				in_data={
						selEn   : "writeBatByTeach"
		        };					
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method	: "handelBatch",
						fun     : 'prodBat',
						in_data : JSON.stringify(in_data),
						tabJson : jsonString,
						userid  : paUserid,
						hid     : pahid
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
						alert("寫入[教職及論文獎勵 寫入批次檔]失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
					}
				}); 
			}
			else{
				alert(msg);
			}		
		}				
	});
	
	//餉單編號設定_全選
	$(document).on('change', '#leavepay_select_all', function(){
		var rows, checked;
        rows = $('#tblPayLeaveList').find('tbody tr');
        checked = $(this).prop('checked');
       
        $.each(rows, function() {
            var checkbox = $($(this).find('td').eq(0)).find('input').prop('checked', checked);                      
        });
       
		$('input[name="chkupdA"]').prop("disabled", false);
	});
	//輸入id,新增才能執行
	$(".findkey").blur(function(){
		if($(this).attr("readOnly")==undefined || $(this).attr("readOnly")==false){	//未設定readOnly屬性
			var data_val=$(this).val().toUpperCase();
			var data_nm =$(this).attr('name');
			var data_id =$(this).attr('id');
			patradt =$("#batpay_patradt").val();
			in_data="";
			if ( data_val != ""){
				in_data={
		        		data_val: data_val,
		        		data_nm : data_nm,
		        		hid     : pahid,
		        		data_tradt : patradt
		        };
		        console.log("data_val:"+data_val+" data_nm:"+data_nm+" data_tradt:"+patradt);
				if (!$("#subPABATPAY_Insbtn").prop("disabled")){
					$.ajax({				
							url:"${pageContext.request.contextPath}/salController.do",
							data:{
								method   : 'handelBatch',
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
								$.doBatpayDetail(data.basPsnItem,"ins");
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
	//輸入支扣代碼,查找是否有重複
	$("#batpay_paycode").change(function() {
		var data_val=$(this).val().split(':')[0];
		var data_nm =$(this).attr('name');		
		data_tradt =$("#batpay_patradt").val();
		var data_idno =$("#batpay_payidno").val();
		console.log("data_val:"+data_val+" data_nm:"+data_nm+" data_tradt:"+data_tradt+" data_idno:"+data_idno);
		in_data="";
		if ( data_val != "" && data_tradt != "" && data_idno != "" ){
			in_data={
	        		data_val: data_val,
	        		hid     : pahid,
	        		data_idno : data_idno,
	        		data_tradt : patradt
	        };
	        console.log("data_val:"+data_val+" data_nm:"+data_nm+" data_tradt:"+data_tradt+" data_idno:"+data_idno);
	        //return false;
			if (!$("#subPABATPAY_Insbtn").prop("disabled")){
				$.ajax({				
						url:"${pageContext.request.contextPath}/salController.do",
						data:{
							method   : 'handelBatch',
							fun      : 'getDupByKey',
							in_data  : JSON.stringify(in_data),
							hid      : pahid	
						},
		
					type:"POST",		//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					async: false,
					cache: false,			
					success:function(data) {
						if (data.batPayItem!=null ){
							alert("[批次檔]已存在,請重新執行查詢!!")
						}else if (data.traPayItem!=null ){
							alert("[異動資料]已存在,請重新執行查詢!!")
						}	
					},
					error: function(xhr){	
						alert("搜尋[輸入 KEY]過程中，程式發生錯誤!!!\nerror code:"+ xhr.status);
					}
				});
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
	    $('.selectpicker').selectpicker('deselectAll');
	    $('.selectpicker').selectpicker('refresh'); 	    
	}
	//讀取table欄位內容 
  	$.getDataInfo = function(formnm){
		var array = new Array();
		var value = [];
		var obj = {};
		var $th ;	
		var data_nm;
		var data_val;
		
		if (formnm=="tblPayLeaveList" || formnm=="tblTeacherawList"){//事病假寫入批次檔 教職及論文獎勵寫入批次檔
			tableID="PABATPAY";
			oTable = $('#' + formnm ).dataTable().api();
			//oTable.$("input:checked", {"page": "all"}).each(function(index,rows){
			oTable.$( "input", {"page": "all"}).each(function(index){	
			//oTable.$('input[type="checkbox"]:checked').each(function(index){
				tr = $(this).parents('tr');
				rowData = oTable.row(tr).data();	//抓取目前點選link這筆row的data
				obj[String($(this).attr("id"))] = rowData; //名稱+數值			  
		    });
		    
		    value.push(obj);   	//以整支table寫入	
	   		array.push( { "formnm": tableID, "value": value } ); 
		}
		else if (formnm=="subPABATPAYForm"){
			tableID=formnm.substring(3,formnm.indexOf('Form'));
			$( ' select , input:text , input:radio:checked, input:checkbox:checked, textarea,input:hidden ','#'+tableID).each(function() {
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
	   		array.push( { "formnm": tableID, "value": value } ); 
			
	    }
   		    
  		var jsonArray = JSON.stringify(array); // 轉換成json
	    	    
	    console.log(jsonArray);
	    alert("資料送出");
	    
	    return jsonArray;
	};
	
	//批次檔明細
 	var objCopyA = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyA.aoColumns = [	
							{ "sTitle":"",		  "mData": null },
							{ "sTitle":"餉單編號", "mData": "salnum" },
							{ "sTitle":"發薪日",	  "mData": "patradt" },
							{ "sTitle":"身份證字號","mData": "payidno" },
							{ "sTitle":"卡號",	  "mData": "paycard" },
							{ "sTitle":"姓名",	  "mData": "paynamec" },
							{ "sTitle":"單位",	  "mData": null },
							{ "sTitle":"職稱",	  "mData": null },
							{ "sTitle":"支扣代碼", "mData": "paycode" },
							{ "sTitle":"支扣中文", "mData": "pacodnmc" },	
							{ "sTitle":"金額",	  "mData": "payamt" },
							{ "sTitle":"時戳",	  "mData": "payupdat" },
														
						 ];
	objCopyA.iDisplayLength=10;
	
	objCopyA.aoColumnDefs= [
							//單位
							{
							'targets': 6,
				         	'searchable':true,
				         	'orderable':true,
				         	'className': 'dt-body-left',
				         	'render': function (data, type, row, meta){
										return data.paydept + " " +data.padepnmc;
						         	  },
				         	}, 
					        //職稱
							{
							'targets': 7,
				         	'searchable':true,
				         	'orderable':true,
				         	'className': 'dt-body-left',
				         	'render': function (data, type, row, meta){
										return data.paytitle+ " " +data.paytitlp;
						         	  },
				         	}, 
					        //金額
							{
							   'targets': [ 10],
							   'className': 'dt-body-right'
						     }
				         							 
						    ];
	objCopyA = $.extend(true,objCopyA, {
				  dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
							't'+
						'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
					buttons: [
					   'excelHtml5' 
					],
				 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
				 	$('td:eq(4)', nRow).html('<a href = "#batPayData" class= "batPayData">'+aData.paycard+'</a>' );
				 	$('td:eq(0)', nRow).html('<button type="button" class="delPABATPAYbtn roleQry btn btn-danger"><i class="fa fa-minus"></i></button>');
				 }
				 ,	
					
	  			 "fnDrawCallback": function () {
						//到某頁
					 	$("#tblBatPayList_paginate").append("  到第 <input style='height:28px;line-height:28px;width:40px;' class='margin text-center' id='changePageA' type='text'> 頁  <a class='btn btn-success shiny' style='margin-bottom:5px' href='javascript:void(0);' id='dataTable-btn'>確認</a>");
					 	oTable = $("#tblBatPayList").dataTable();
					 	$('#dataTable-btn').click(function(e) {
					 		if($("#changePageA").val() && $("#changePageA").val() > 0) {
					 			var redirectpage = $("#changePageA").val() - 1;
					 		} else {
					 			var redirectpage = 0;
					 		}	
					 		oTable.fnPageChange(redirectpage);		
					 	});
					 	//刪除	
						$(".delPABATPAYbtn")
							.click(function(){
								delCheck = confirm("請確認是否刪除！！\n\n若是，請按【確定】．\n\n若不是，請按【取消】");
								if (delCheck) {		
									oTable = $('#tblBatPayList').dataTable().api();
									uTable = $('#tblBatPayList').dataTable();
									info = oTable.page.info();		//記錄目前頁碼
									currentPage=info.page;
							        tr = $(this).parents('tr');
							        rowIndex = oTable.row(tr).index();
							        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data
							        tblname='PABATPAY';
							        
							        pk_data={
							        		"patradt"  :rowData.patradt,
							        		"payidno"  :rowData.payidno,
							        		"salnum"   :rowData.salnum,
							        		"paycode"  :rowData.paycode,
							        		"hid"      :pahid,
							        };
							        //return false;
							        $.ajax({
										url:"${pageContext.request.contextPath}/salController.do",
										data:{
											method   : 'handelBatch',
											fun      : 'updBatPay',
											editMode : 'delete',
											pk_data  : JSON.stringify(pk_data),
											tblname  : tblname,
											hid      : pahid,
											userid   : paUserid,
										},
										
										type:"GET",			//type:POST, GET(default:GET)
										dataType:"json",	//dataType: text, xml, html, script, json, jsonp
										contentType: "application/x-www-form-urlencoded;charset=utf-8",  
										success:function(data) {
											uTable.fnClearTable(0);		//清空資料
											$("#btn_qryBatPay").trigger( "click" );
											uTable.fnPageChange(currentPage);		//切到原先某碼
										},
										error:function(xhr) {
											alert("刪除失敗，程式發生錯誤!!!\nerror code:"+ xhr.status);  
										}
									});
								}
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
		            //金額總計 
		            pTotal = api
		                .column( 10	)
		                .data()
		                .reduce( function (a, b) {
		                    		return intVal(a) + intVal(b);
		                		}, 0 );
		 			// Total over this page
		            var pageTotal_1 = api
		                .column( 10	, { page: 'current'} )
		                .data()
		                .reduce( function (a, b) {
		                    return intVal(a) + intVal(b);
		                }, 0 );
		            // Update footer
		            $( api.column( 10 ).footer() ).html(
		                '$'+pageTotal_1 +' <br>( $'+ pTotal +' 合計)' 
		            );		            
		        }  							
			});
						    
	$('#tblBatPayList').dataTable(objCopyA);
	
	//顯示pa.paBatPay明細
	$('#tblBatPayList tbody').on('click', '.batPayData', function () {
		oTable = $('#tblBatPayList').dataTable().api();
		info = oTable.page.info();		//記錄目前頁碼
		currentPage=info.page;
        var tr = $(this).parents('tr');
        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data
        $.doBatpayDetail(rowData,"upd");
	});
	
	//顯示pa.paBatPay明細
	$.doBatpayDetail = function(rowData,flag){
		$("#batpay_payidno").val(rowData.payidno);
		$("#batpay_paycard").val(rowData.paycard);
		$("#batpay_paynamec").val(rowData.paynamec);
		$("#batpay_paydept").selectpicker('val',rowData.paydept);
		$("#batpay_paytitle").selectpicker('val',rowData.paytitle);
		$("#batpay_hid").val(rowData.hid);
		$("#batpay_paupbas").val(rowData.paupbas);
		if (flag=="upd"){
			//modal標題:支扣代碼+中文
			$("#batPayItemModal").find('.modal-title').text("修改[批次檔] "+rowData.paycard+rowData.paynamec+rowData.patradt+"("+rowData.salnum+")"+rowData.pacodnmc);
			$("#batpay_patradt").val(rowData.patradt);			
			$("#batpay_salnum").selectpicker('val',rowData.salnum);			
			$("#batpay_paycode").selectpicker('val',rowData.paycode+":"+rowData.paupbas);
			$("#batpay_payamt").val(rowData.payamt);
			$("#batpay_payupdat").val(rowData.payupdat);
		
			//將key設成readOnly
			$("#batpay_patradt").prop('disabled', true);	
			$("#batpay_payidno").prop("readOnly",true);	
			$('#batpay_salnum').prop('disabled', true).selectpicker('refresh');
			$('#batpay_paycode').prop('disabled', true).selectpicker('refresh');
				
			$("label.error").hide();
			$(".error").removeClass("error");
	        $("#batPayItemModal").modal("show");
			$("#subPABATPAY_Insbtn").prop("disabled", true);
			$("#subPABATPAY_Updbtn").prop("disabled", false);
		}else{
			//modal標題:支扣代碼+中文
			$("#batPayItemModal").find('.modal-title').text("新增[批次檔] "+rowData.paycard+rowData.paynamec);
			
		}
	};
	//事病假扣薪
 	var objCopyB = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyB.aoColumns = [	
// 							{ "sTitle":' <div><input type="checkbox" id="leavepay_select_all" data-to-table="tasks"  > ', "mData": null },
							{ "sTitle":"",		  "mData": null },
							{ "sTitle":"請假年月", "mData": "ym" },
							{ "sTitle":"卡號",	  "mData": "paycard" },
							{ "sTitle":"姓名",	  "mData": "empname" },
							{ "sTitle":"分類",	  "mData": null },
							{ "sTitle":"假別",	  "mData": null },
							{ "sTitle":"日數",	  "mData": "offday" },
							{ "sTitle":"累計日數<BR>(不含當月)",  "mData": "offdaysum" },
							{ "sTitle":"不發給<BR>天數", "mData": "nopay" },
							{ "sTitle":"折半天數", "mData": "payhalf" },	
							{ "sTitle":"檢測", 	  "mData": "rtn" },
							{ "sTitle":"月薪", 	  "mData": "msalary" },
							{ "sTitle":"日薪", 	  "mData": "dsalary" },
							{ "sTitle":"折半日薪", "mData": "hsalary" },
							{ "sTitle":"不發給工資<br>天數", 	  "mData": "c_nopay" },
							{ "sTitle":"折半工資<br>天數", 	  "mData": "halfpay" },
							{ "sTitle":"金額", 	  "mData": "payamt" },
						 ];
	objCopyB.iDisplayLength=10;
	
	objCopyB.aoColumnDefs= [
							 //勾選
							{
							'targets': 0,
				         	'searchable':false,
				         	'orderable':false,
				         	'className': 'dt-body-center',
				         	'render': function (data, type, row, meta){
					         			return '<input type="checkbox" name="chkupdA" id="chkA_' +  meta.row + '" value="' 
			 					                + $('<div/>').text(data).html() + '">';            
							         },
					        },
					        //假別
							{
							'targets': 5,
				         	'searchable':true,
				         	'orderable':true,
				         	'className': 'dt-body-left',
				         	'render': function (data, type, row, meta){
										return data.offtype + " " +data.offtypnm;
						         	  },
				         	},
					        //分類
							{
							'targets': 4,
				         	'searchable':true,
				         	'orderable':true,
				         	'className': 'dt-body-left',
				         	'render': function (data, type, row, meta){
										return data.pos + " " +data.posnm;
						         	  },
				         	}							 
						    ];
	
	objCopyB = $.extend(true,objCopyB, {
				  dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
							't'+
						'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
					buttons: [
					   'excelHtml5' 
					],
// 	  			 "fnDrawCallback": function () {
// 						var api = this.api();
// 						var startIndex=0;
//                     	api.column(0).nodes().each(function(cell, i) {
//                         	cell.innerHTML = startIndex + i + 1;
//                     	});
//                  },
				"footerCallback": function ( row, data, start, end, display ) {
		            var api = this.api(), data;
		 
		            // Remove the formatting to get integer data for summation
		            var intVal = function ( i ) {
		                return typeof i === 'string' ?
		                    		  i.replace(/[\$,]/g, '')*1 :
		                    		  typeof i === 'number' ? i : 0;
		            };
		 
		            // 金額
		            var pTotal = api
		                .column( 16	)
		                .data()
		                .reduce( function (a, b) {
		                    		return intVal(a) + intVal(b);
		                		}, 0 );
		 			// Total over this page
		            var pageTotal_1 = api
		                .column( 16	, { page: 'current'} )
		                .data()
		                .reduce( function (a, b) {
		                    return intVal(a) + intVal(b);
		                }, 0 );
		                
		            // Update footer
		            $( api.column( 16 ).footer() ).html(
		            	'$'+pageTotal_1 +' <br>( $'+ pTotal +' 合計)'		                
		            );		        }
	});					    
	$('#tblPayLeaveList').dataTable(objCopyB);
	
	//教職及論文獎勵 
 	var objCopyC = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyC.aoColumns = [	
							{ "sTitle":"",		  "mData": null },
							{ "sTitle":"身分證",	  "mData": "payidno" },
							{ "sTitle":"卡號",	  "mData": "paycard" },
							{ "sTitle":"姓名",	  "mData": "paynamec" },
							{ "sTitle":"項目",	  "mData": "paycode" },
							{ "sTitle":"金額",	  "mData": "payamt" },
							{ "sTitle":"檢測", 	  "mData": "rtn" },
						 ];
	objCopyC.iDisplayLength=10;
	
	objCopyC.aoColumnDefs= [
							 //勾選
							{
							'targets': 0,
				         	'searchable':false,
				         	'orderable':false,
				         	'className': 'dt-body-center',
				         	'render': function (data, type, row, meta){
					         			return '<input type="checkbox" name="chkupdA" id="chkA_' +  meta.row + '" value="' 
			 					                + $('<div/>').text(data).html() + '">';            
							         },
					        }
							];
	
	objCopyC = $.extend(true,objCopyC, {
				  dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
							't'+
						'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
					buttons: [
					   'excelHtml5' 
					],
				
				});					    
	$('#tblTeacherawList').dataTable(objCopyC);
	  	      
});

</script>
</head>
<body>

<ul class="nav nav-pills mb-3" id="pills-tab" role="tab">
  <li class="nav-item">
    <a class="nav-link active" id="pills-battask-tab" data-toggle="pill" href="#byBatTaskMain" role="tab" aria-controls="pills-home" aria-selected="true">批次編輯</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-batHanding-tab" data-toggle="pill" href="#byBatHandingMain" role="tab" aria-controls="pills-profile" aria-selected="false">批次處理</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-payleave-tab" data-toggle="pill" href="#byPayLeaveMain" role="tab" aria-controls="pills-profile" aria-selected="false">事病假扣薪</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-teacheraw-tab" data-toggle="pill" href="#byTeacherawMain" role="tab" aria-controls="pills-profile" aria-selected="true">教職及論文獎勵 </a>
  </li>
</ul>

<div class="tab-content" id="pills-tabContent">
	<!--批次檔編輯-->
  	<div class="tab-pane fade show active" id="byBatTaskMain" role="tabpanel" aria-labelledby="pills-battask-tab">
		<div class="container-fluid">
			<div class="row" id="qryBatchPayItem">
				<div class="col-9">
					<div class="form-inline">
		    			<div class="input-group">
<!-- 		    				<select id="qry_pabatpay_salnum"  class="selectpicker SALNUM" data-width="fit" multiple title="餉單編號"> -->
<!-- 		    					<option value="0">無餉單編號</option> -->
<%-- 								<c:forEach var="bitem" items="${alBankDtTbl}" varStatus="loop"> --%>
<%-- 									<option value="${bitem.salnum}">${bitem.salnum}</option> --%>
<%-- 								</c:forEach> --%>
<!-- 							</select> -->
<!-- 							<select id="qry_pabatpay_patradt" class="selectpicker patradt" data-width="fit"  data-live-search="true" multiple title="發薪日"> -->
<%-- 								<c:forEach var="bitem" items="${alBankDtTbl}" varStatus="loop"> --%>
<%-- 									<option value="${bitem.paybank}">${bitem.paybank}</option> --%>
<%-- 								</c:forEach> --%>
<!-- 							</select> -->
							<input type="text" id="qry_pabatpay_salnum" size="4"
									class="form-control-sm" placeholder="餉單編號">
							<input type="text" id="qry_pabatpay_patradt" size="10"
								class="form-control-sm gclsCalTxt" placeholder="異動日">
							
							<select id="qry_pabatpay_paycode" class="selectpicker" data-width="fit" data-live-search="true" title="支扣項目 ">
								<option value=""></option>
			     				<c:forEach var="uitem" items="${alpayitmTbl}" varStatus="loop">
									<option value="${uitem.paycode}-${uitem.pacodnmc}">${uitem.paycode}-${uitem.pacodnmc}</option>
								</c:forEach>
							</select>
		    				<input type="text" id="qry_pabatpay_cardno" size="4" class="form-control-sm text-uppercase" placeholder="卡號" >
							<input type="text" id="qry_pabatpay_idtno" size="10" class="form-control-sm text-uppercase" placeholder="身分證" >
						</div>		    		
			    		
					</div>
				</div>
				<div class="col-3">
					<button id="btn_qryBatPay" class="btn btn-info" type="button" ><i class="fa fa-search"></i>查詢</button>
					<button id="btn_addBatPay" class="btn btn-success ml-auto roleQry" type="button" data-toggle="modal" data-target="#batPayItemModal"><i class="fa fa-edit"></i>新增一筆</button>
					<button id="btn_clnBatPay" class="btn btn-warning" type="button"><i class="fa fa-eraser"></i>清空</button>
				</div>
			</div>
				
			<div class="row">
				<div class="col-12 border border-dark">
					<!-- pa.paBatPay清單 -->
					<table id="tblBatPayList" class="table table-striped table-bordered table-hover" >
						<thead></thead>
						<tfoot>
			            <tr>
			                <TH colspan=11 style="text-align: right"></TH>					
							<TH></TH>															
			            </tr>
			        	</tfoot>
					</table>
				</div>
			</div>
 		</div>	
	</div>
	<!--批次檔編輯 model  -->
	<div class="modal fade"  id="batPayItemModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog" style="max-width: 70%;" >
			<!-- Modal content-->
			<div class="modal-content">
				<form id="subPABATPAYForm">
					<INPUT type="hidden" id ="batpayitem"  value="">
					<div class="modal-header">
						<h5 class="modal-title">批次檔編輯</h5>
		        		<button type="button" class="close" data-dismiss="modal" aria-label="Close">
		                    <span aria-hidden="true">×</span></button>
					 </div>
					 <div class="modal-body">
						<div class="container">
							<table id="PABATPAY" class="table table-striped table-bordered table-condensed table-responsive table-sm caption-top tblEditgrp" style="text-align:center;">
								<INPUT type="hidden" id ="batpay_hid"  name="hid" value="">
								<INPUT type="hidden" id ="batpay_paupbas" name="paupbas" value="">
								<tbody>
								<tr>
									<th>*餉單編號</th>
			            			<Td >
				            			<select id="batpay_salnum" class="form-control selectpicker" title="餉單編號">				    					
										</select>
									</Td >
			            			<th>*發薪日期</th>		
			            			<Td colspan=3><input type="text" id="batpay_patradt" name="patradt" class="form-control datepicker required" maxlength="10" size="10"/></td>
			            			
								</Tr>
								<tr >	
			            			<th class="text-white bg-dark">*身分證字號</th>	
			            			<Td><input type="text" id="batpay_payidno" name="payidno" class="form-control findkey text-uppercase required" maxlength="10" size="10"  /></Td>
			            			<th class="text-white bg-dark">卡號</th>	
			            			<Td><input type="text" id="batpay_paycard" name="paycard" class="form-control findkey text-uppercase required" maxlength="10" size="10"  /></Td>
			            			<th>姓名</th>	
			            			<Td><input type="text" id="batpay_paynamec" name="paynamec" class="form-control" readonly maxlength="12" size="12"  /></Td>
			            		</tr>
			            		<tr>	
			            			<th>單位</th>
		            				<Td>
			            				<select id="batpay_paydept" name="paydept" class="form-control selectpicker"  disabled title="單位">
										</select>
			            			<th>職稱</th>
			            			<td colspan=3>
										<select id="batpay_paytitle" name="paytitle" class="form-control selectpicker" disabled title="職稱">
										</select>
			            			</td>
			            		</tr>
			            		<tr>	
			            			<th>*支扣項目</th>
		            				<Td>
			            				<select id="batpay_paycode" name="paycode" class="form-control text-left selectpicker required" data-width="fit" data-live-search="true" title="支扣項目" >
										</select>
			            			<th>金額</th>
			            			<td colspan=3>
										<input type="text" id="batpay_payamt" name="payamt" class="form-control number required" maxlength="10" size="10"  />
			            			</td>
			            		</tr>
			            		<tr>	
			            			<th>時戳</th>
			            			<td colspan=5>
										<input type="text" id="batpay_payupdat" name="payupdat" class="form-control" maxlength="30" size="30" readonly/>
			            			</td>
			            		</tr>
								</tbody>
							</table>
						</div>
					</div>
					<div class="modal-footer">
					   <button type="button" class="btn btn-primary roleQry" id="subPABATPAY_Insbtn">確認儲存</button>
				       <button type="button" class="btn btn-primary roleQry" id="subPABATPAY_Updbtn">更改</button>
					   <button type="button" class="btn btn-secondary" data-dismiss="modal">返回</button>
					</div>
				</form>
			</div>
		</div>
	</div>
	<!--批次檔處理-->
  	<div class="tab-pane fade" id="byBatHandingMain" role="tabpanel" aria-labelledby="pills-batHanding-tab">
  		<div class="row p-1 mb-1 bg-secondary">
			<div class="col-md-10">
				<div class="form-inline">
					<div class="input-group">
						<span class="input-group-text">選單</span>
		  	
						<select id="qryType" class="form-control">
							<option value="1:[匯入]批次檔:ImpPayInBat">1[匯入]批次檔</option>
							<option value="2:[刪除]批次檔:DelPayInBat">2[刪除]批次檔</option>     					
						</select>
					</div>
				</div>
			</div>
			<div class="col-md-2">
				<button id="btn_handelBat" class="btn btn-info roleQry" type="button" ><i class="fa fa-cog"></i>執行</button>					
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="form-inline" id="rowBatHanding_1">
					<div class="input-group">
						<div class="input-group" id="divfield_1">
							<label for="handing_Datyr" >發薪日</label>
							<input type="text" id="handing_Datyr" class="form-control dateBat" maxlength="10" size="10"/>
							<label for="handing_paycode" >支扣項目</label>
							<select id="handing_paycode" class="selectpicker" data-width="fit" data-live-search="true" title="支扣項目">
								
							</select>
						</div>
						<div class="input-group" id="divfield_2">
							<label for="handing_chkbas" >檢核基本檔</label>
							<select id="handing_chkbas" class="form-control" title="檢核基本檔">
								<option value="Y">Y</option>
								<option value="N">N</option>
							</select>								
						</div>
						<div class="input-group" id="divfield_3" >
							<form id="uploadfileForm">檔案 	
<!-- 								    <label class="sr-only" for="fileInput">來源</label> -->
							    <input type="file" accept=".csv,text/plain"  id="fileInput" name="fileInput">								   
						    </form> 
					   	 </div>
					   	<a href="#explan_chkbat" class="btn btn-danger" data-toggle="collapse">注意事項</a>
						<div id="explan_chkbat" class="collapse">
				  			<div id="explan_chkbat" class="text-danger">
					  			檢測說明:<br>
					  			1.基本檔在職+異動檔(新進1復職2,納實33,職務調整37,[有效期限內--離職21,退休22,留職停薪23,逾期請假24])
					  		</div>
					  	</div>
					</div>
				</div>
			</div>
		</div>
  	</div>
  	<!--事病假扣薪-->
  	<div class="tab-pane fade" id="byPayLeaveMain" role="tabpanel" aria-labelledby="pills-payleave-tab">
  		<div class="row">
  			<div class="col-md-9">
  				<div class="form-inline">
	  				<div class="input-group">
						<label for="leave_Datyr" >發薪日</label>
						<input type="text" id="leave_Datyr" class="form-control dateBat" maxlength="10" size="10"/>					
					</div>
					<a href="#explan_payLeave" class="btn btn-danger" data-toggle="collapse">注意事項</a>
					<div id="explan_payLeave" class="collapse">
			  			<div id="explan_1" class="text-danger">
				  			1.1個月日數=[30]日,日薪以[無條件]捨去<br>
				  			2.日薪採計,高榮=本薪+專業加給+主管加給<br>
				  			3.屏榮=本俸+專業加給+主管加給+院區加給FP100+身心障礙加給FP110+特別津貼FP120<br>
				  			4.聯醫=本俸+專業加給+主管加給+責任加給FP090+職務加給FP100<br>
				  			5.1月份假檔,日數累計是前一年1-12月累加數<br>
				  			6.寫入前,請先按[卡號]排序,金額0不會寫入.<br>
				  		</div>
				  	</div>
				</div>	
  			</div>
			<div class="col-md-3">
				<button id="btn_qryLeave" class="btn btn-info ms-auto" type="button" ><i class="fa fa-search"></i>查詢假檔</button>
				<button id="btn_wrtLeave" class="btn btn-primary roleQry" type="button" ><i class="fa fa-pen"></i>寫入</button>					
			</div>
			
		</div>
		<div class="row">
			<div class="col-12 border border-dark">
				<table id="tblPayLeaveList" class="table table-striped table-bordered table-hover" >
					<thead>							
					</thead>
					<tfoot>
		            <tr>
		            	<th colspan=17 style="text-align:right"></th>        
		            </tr> 
		        	</tfoot>
				</table>
			</div>
		</div>
  	</div>
  	<!--教職及論文獎勵 -->
  	<div class="tab-pane fade" id="byTeacherawMain" role="tabpanel" aria-labelledby="pills-teacheraw-tab">
  		<div class="row">
  			<div class="col-md-9">
  				<div class="form-inline">
	  				<div class="input-group">
						<label for="teacheraw_Datyr" >發薪日</label>
						<input type="text" id="teacheraw_Datyr" class="form-control dateBat" maxlength="10" size="10"/>					
					</div>
					<a href="#explan_teacheraw" class="btn btn-danger" data-toggle="collapse">注意事項</a>
					<div id="explan_teacheraw" class="collapse">
			  			<div id="explan_teach" class="text-danger">
				  			操作步驟:查詢[N次]->[金額]欄位往左邊,是績效組來源->檢測[要排序]->檢查無誤->寫入[一次性]
				  			<br>檢測說明:<br>
				  			1.[金額更新]:教職檔期限內,和薪資基本支扣[FP420,FP430]比對金額不同<br>				  			
				  			2.[新增一筆]:教職檔期限內,找不到薪資基本支扣[FP420,FP430]<br>
				  			3.[期限已到,新增金額=0]:以薪資基本支扣[FP420,FP430],找教職檔期限內,找不到<br>
				  		</div>
				  	</div>
				</div>	
  			</div>
			<div class="col-md-3">
				<button id="btn_qryTeacheraw" class="btn btn-info ms-auto" type="button" ><i class="fa fa-search"></i>查詢</button>
				<button id="btn_wrtTeacheraw" class="btn btn-primary roleQry" type="button" ><i class="fa fa-pen"></i>寫入</button>					
			</div>
			
		</div>
		<div class="row">
			<div class="col-12 border border-dark">
				<table id="tblTeacherawList" class="table table-striped table-bordered table-hover" >
					<thead>							
					</thead>					
				</table>
			</div>
		</div>
  	</div>
</div>

</body>
</html>