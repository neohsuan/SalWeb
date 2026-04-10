<!DOCTYPE HTML>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Calendar"%>
<%
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	Calendar now = Calendar.getInstance();
	String today = sdf.format(now.getTime());
	Integer today_y=new Integer(today.substring(0, 4)).intValue() ;
%>	

<html>
<head>
<title>所得稅</title>
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

var objCopy;
var oTable;
var hid;
var tblname="";
var tr;
var rowData;
var typ;
var paycitation;			
var payear;
var payear_old="";
var paymonth;
var paydept;
var payformat;
var cardno;
var idtno;
var namec;
var array = new Array();
var value = [];
var obj = {};
var keyobj = {};
var $th ;	
var data_id;
var data_val;
var tableID;
var data_id;
var data_val;
var table_id; 
var index;
var jsonArray;
var selCode="1";
var url;
var selEn;
var selNm;

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
		//初始化表單驗證
		$("#subsalCodeForm,#subTAXPSNForm").validate();
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
					
			 	if (data.alTaxYrTbl[0]!=null){
			 		select = $('#qry_viewYear,#qry_sumYear,#qry_taskYear,#payear,#cash_payear,#handing_Taxyr,#qry_StatYear');
					select.empty();
					//options = "<option value=''>";
					select.append(options);
					$.each(data.alTaxYrTbl, function(idx) {
						options = "<option value='" + data.alTaxYrTbl[idx] + "'>" + data.alTaxYrTbl[idx] ;
						select.append(options);
					});
					$('#qry_viewYear,#qry_sumYear,#qry_taskYear,#payear,#handing_Taxyr,#qry_StatYear').selectpicker('refresh');
				}	
				//設定今年
				$('#handing_Taxyr').selectpicker('val','<%=today_y%>');;
<%--  				select.selectpicker('val','<%=today_y%>'); --%>
				<%-- $('.selYY').selectpicker('val','<%=today_y%>'); --%>
				 <%-- $('#qry_viewYear,#qry_sumYear,#qry_taskYear,#payear').selectpicker('val','<%=today_y%>');  --%>
 				
 				if (data.alDeptCodeTbl[0]!=null){
					select = $('#paydept,#qry_StatDept');
					select.empty();
					options = "<option value=''>單位";
					select.append(options);
					$.each(data.alDeptCodeTbl, function(idx) {
						options = "<option value='" + data.alDeptCodeTbl[idx].padepcod + "'>" + data.alDeptCodeTbl[idx].pacstno+"-"+data.alDeptCodeTbl[idx].padepnmc ;
						select.append(options);
					});
					select.selectpicker('refresh');
				}
				//
 				if (data.alTaxCatgTbl[0]!=null){
					select = $('.selCatg');
					select.empty();
					options = "<option value=''>轉帳項目";
					select.append(options);
					$.each(data.alTaxCatgTbl, function(idx) {
						options = "<option value='" + data.alTaxCatgTbl[idx].paycatg + "'>" + data.alTaxCatgTbl[idx].paycatg+"-"+data.alTaxCatgTbl[idx].catgname ;
						select.append(options);
					});
					//select.selectpicker('refresh');
				}
				if (data.alBankDtTbl[0]!=null){
					select = $('#handing_Datyr');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alBankDtTbl, function(idx) {
						options = "<option value='" + data.alBankDtTbl[idx].salnum + "'>" + data.alBankDtTbl[idx].paybank.dts+" "+data.alBankDtTbl[idx].salnum ;
						select.append(options);
					});
					//select.selectpicker('refresh');
				}
				//已轉帳年月
				if (data.alTaxTranYrTbl[0]!=null){
					select = $('#handing_TaxTranyr');
					select.empty();
					//select.find('option').remove();    // remove all options 
					var defaultYY="";
					
					$.each(data.alTaxTranYrTbl, function(idx) {
						if (idx==0) defaultYY=data.alTaxTranYrTbl[idx].payear+"-"+data.alTaxTranYrTbl[idx].paymonth+"-"+data.alTaxTranYrTbl[idx].paycatg;
						options = "<option value='"+data.alTaxTranYrTbl[idx].payear+"-"+data.alTaxTranYrTbl[idx].paymonth+"-"+data.alTaxTranYrTbl[idx].paycatg+"'>" + data.alTaxTranYrTbl[idx].payear+"-"+data.alTaxTranYrTbl[idx].paymonth+"["+data.alTaxTranYrTbl[idx].paycatg+"]" ;
						console.log("options:"+options);
						select.append(options);
					});
					console.log("defaultYY:"+defaultYY);
					
					//select.selectpicker('refresh');
					//select.selectpicker('render');
					select.selectpicker('destroy'); // temporary patch!
            		select.selectpicker();          // temporary patch!
            		select.selectpicker('val',defaultYY);
				}
			}  
		});
	};
 	/*預先載入代碼檔 */
  	$.getLoadCodeTbl('payTax','Y');
  	
  	
  	//代碼選單切換
	$("#qryType").change(function(){
		$("#divfield_1,#divfield_2,#divfield_3,#divfield_4,#divfield_5,#explan_1,#explan_2,#explan_3").addClass("d-none");	//隱藏
  		selCode=(typeof($("#qryType option:selected").val().split(':')[0]) == "undefined" )? "1":$("#qryType option:selected").val().split(':')[0];
  		console.log(selCode);
  		if (selCode=="1" ){			//1.匯入_轉帳發薪明細 
			$("#divfield_1,#divfield_2").removeClass("d-none");
			$("#explan_1").removeClass("d-none");
		}
		else if (selCode=="2"){		//2.刪除_轉帳發薪明細 
			$("#divfield_2").removeClass("d-none");
			$("#explan_2").removeClass("d-none");
		}else if (selCode=="3"){	//3[匯入]批次人工入稅
			$("#divfield_3").removeClass("d-none");
			$("#explan_3").removeClass("d-none");
			var logtmp="log資料夾:\\\\ap_pd1\\Apps_Exe\\SalProd\\" ;
			logtmp = logtmp.concat(pahid, "\\log\\");	
			$("#explan_3").html(logtmp);			
		}else if (selCode=="4"){	//4[刪除]批次人工入稅
			$("#divfield_4").removeClass("d-none");
		}else if (selCode=="5" || selCode=="6"){	//5.身分證檢查及流水號編號  6產生媒體申報檔
			$("#divfield_5").removeClass("d-none");
		}	 
  	});
  	
  	//編輯欄位寫入json
  	$.getDataInfo = function(formnm){
		array = new Array();
		value = [];
		obj = {};
		//var $th = $('#'+ tblname + ' th');	
			 
		if (formnm=="subTAXPSNForm"){
			tableID=formnm.substring(3,formnm.indexOf('Form'));
			
			$( ' select , input:text , input:radio:checked, input:checkbox:checked, textarea,input:hidden ','#'+tableID).each(function() {
				data_id = String($(this).attr("id"));
	    		data_val = $(this).val();
	    		if (typeof data_id === "undefined" || data_id == "undefined") {
		        	
		        }else{
		        	if ( null == data_val ){
		        		data_val = "";
		        	}else{
// 		        		if (data_id.substring(0,2)=="F_")
// 		        			data_id=data_id.substring(2);
		        		data_val=data_val.toUpperCase();//轉大寫
		        	}
		        	obj[data_id] = data_val; //名稱+數值	
		        }
		    });    
	    	value.push(obj);   	//以整支table寫入	
	   		array.push( { "formnm": tableID, "value": value } );
		    
		}	
  		jsonArray = JSON.stringify(array); // 轉換成json
	    	    
	    console.log(jsonArray);
	    alert("資料送出");
	    
	    return jsonArray;
	};
	
	//編輯欄位檢查
  	$.checkFields = function(btn){
  		msg = '';
  		if (btn== 'btn_handelTax'){	//應稅處理
  			if (typeof($("#qryType option:selected").val())=='undefined'){
  				msg = '請勾選[選單]項目!!';
  			}
  			else{
  				selCode=$("#qryType option:selected").val().split(':')[0];
  				//1[入稅]轉帳發薪明細
  				if (selCode=='1'){
  					if(typeof($("#handing_Datyr option:selected").val())=='undefined' || $("#handing_Datyr option:selected").val()==''){
			 			msg = '請輸入條件值!!';
			 		}
  				}
  				//2[刪除]轉帳發薪明細
  				else if (selCode=='2'){
  					if(typeof($("#handing_TaxTranyr option:selected").val())=='undefined' || $("#handing_TaxTranyr option:selected").val()==''){
			 			msg = '請輸入條件值!!';
			 		}
  				}
  				//3[匯入]批次人工入稅
  				else if (selCode=='3'){
  					var file = $("#fileInput").val();			
					if (file.length == 0) {
						alert('請確認是否有選檔案');				
					}
  				}
  				//4[刪除]批次人工入稅
  				else if (selCode=='4'){
					if ($("#handing_taskTickno").val()=='') {
						msg ='[傳票製票號]請勿空白';				
					}
  				}
  				//5身分證檢查及流水號編號
  				//6產生媒體申報檔
  				else if (selCode=='5' || selCode=='6'){
  					if(typeof($("#handing_Taxyr option:selected").val())=='undefined' || $("#handing_Taxyr option:selected").val()==''){
			 			msg = '請輸入條件值!!';
			 		}
  				}
  			}
		}
  		else if (btn== 'btn_qryTaxView'){	//所得稅查看_查詢
	 		if(typeof($("#qry_viewYear option:selected").val())=='undefined' && typeof($("#qry_viewMon option:selected").val())=='undefined' && $("#qry_viewTickno").val()==''  && $("#qry_viewNamec").val()=='' && $("#qry_viewId").val()=='' && $("#qry_viewCard").val()=='' && typeof($("#qry_viewFmt option:selected").val())=='undefined' ){
	 			msg = '請輸入條件值!!';
	 		}
		}
		else if (btn== 'btn_qryTaxSum'){	//所得稅彙總_查詢
	 		if(typeof($("#qry_sumYear option:selected").val())=='undefined' && typeof($("#qry_sumMon option:selected").val())=='undefined' && $("#qry_sumFmt").val()==''  && $("#qry_sumTickno").val()==''  ){
	 			msg = '請輸入條件值!!';
	 		}else if( ( typeof($("#qry_sumYear option:selected").val())=='undefined' ||  $("#qry_sumYear option:selected").val()=='' ) && ($("#qry_sumTickno").val()=='') ) {
	 			msg = '[年度]請勿空白!!';
 			}
		}
		else if (btn== 'btn_qryTaxStat'){	//所得稅統計_查詢
	 		if($("#qry_StatNamec").val()=='' && $("#qry_StatId").val()=='' && $("#qry_StatCard").val()=='' &&   
	 		   ($("#qry_StatYear option:selected").val()== '' || typeof($("#qry_StatYear option:selected").val())=='undefined' ) && 
	 		   ($("#qry_StatMon option:selected").val() == '' || typeof($("#qry_StatMon option:selected").val())=='undefined'  ) &&
	 		   ($("#qry_StatDept option:selected").val()== '' || typeof($("#qry_StatDept option:selected").val())=='undefined' ) ) {
	 			msg = '請輸入條件值!!';
	 		}
		}
		else if (btn== 'btn_qryTaxTask'){	//所得稅編輯_查詢
  		
	 		if ((typeof($("#qry_taskYear option:selected").val())=='undefined' && payear_old == "") && 
	 		     $("#qry_taskTickno").val()=='' && $("#qry_taskId").val()=='' && 
	 		     $("#qry_taskCard").val()=='' && 
	 		     $("#qry_taskNamec").val()=='' ) {
	 			msg = '請輸入條件值!!';
 			}
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
	)
	
	$(".btn").click(function(){
		var id = $(this).attr('id');
		console.log("btn:"+id);
		if(id == 'btn_clnTaxView'){
			cleanTable("byTaxViewMain");
			$("#TblTaxView").dataTable().fnClearTable();
			return false;
		}else if(id == 'btn_clnTaxSum'){
			cleanTable("byTaxSumMain");
			$("#TblTaxSum").dataTable().fnClearTable();
			return false;
		}
		else if(id == 'btn_clnTaxTask'){
			cleanTable("byTaxTaskMain");
			$("#TblTaxPsnList").dataTable().fnClearTable();		
			payear_old="";	
			return false;
		}
		else if(id == 'btn_clnTaxStat'){
			cleanTable("byTaxStatMain");
			$("#TblTaxStat").dataTable().fnClearTable();
			return false;
		}
		//所得稅編輯_新增1筆
		else if(id == 'btn_addTaxPsn'){		
			cleanTable("subTAXPSNForm");
			
			$("#model_header_title").html("新增[所得稅個人基本資料]");
			
			$("#payidno").attr("readOnly",false);	//key
			$("#payear").attr("readOnly",false);	//key
				
			$("label.error").hide();
			$(".error").removeClass("error");
	        $("#StaffDialog").modal("show");
			$("#subTAXPSN_Insbtn").prop("disabled", false);
			$("#subTAXPSN_Updbtn").prop("disabled", true);
		}
		//所得稅編輯_查詢
		else if(id == 'btn_qryTaxTask'){		
			msg = $.checkFields(id);
			if(msg == ''){
				cardno=$("#qry_taskCard").val().toUpperCase();
				idtno=$("#qry_taskId").val().toUpperCase();
				namec=$("#qry_taskNamec").val();	
				paycitation=$("#qry_taskTickno").val().toUpperCase();
				//selectpicker 第2次會清空選的值
				payear=$.getSelectVals("qry_taskYear");
				if (payear == ""){
					payear=payear_old;
				}
				payear_old=payear;
				
 				console.log(" 卡號:"+cardno+" 身分證:"+idtno+" 姓名:"+namec+" paycitation:"+paycitation+" payear:"+payear);
 				
				code_data="";
				code_data={
						cardno     :cardno,
		        		idtno      :idtno,
		        		namec      :namec,
		        		paycitation:paycitation,
		        		payear     :payear
		        	};
				console.log("所得稅編輯_查詢:code_data:"+JSON.stringify(code_data)+" hid:"+pahid);
				//return false;
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'getTaxTaskList',
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
						$('#TblTaxPsnList').show();
						oTable = $('#TblTaxPsnList').dataTable(objCopyC);
						
						oTable.fnClearTable(0);		//清空資料
								
						if (data.taxTaskList!=null && data.taxTaskList.length>0){	
							oTable.fnAddData(data.taxTaskList);//重新給資料
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
		else if(id == 'btn_qryTaxView'){		//所得稅查看_查詢
			msg = $.checkFields(id);
			if(msg == ''){
				typ=$("#qry_viewtyp").val();
				cardno=$("#qry_viewCard").val().toUpperCase();
				idtno=$("#qry_viewId").val().toUpperCase();
				namec=$("#qry_viewNamec").val();	
				paycitation=$("#qry_viewTickno").val().toUpperCase();			
				payear=$.getSelectVals("qry_viewYear");
				paymonth=$.getSelectVals("qry_viewMon");
				payformat=$.getSelectVals("qry_viewFmt");
				
 				console.log("所得稅查看_查詢=>項目:"+typ+" 卡號:"+cardno+" 身分證:"+idtno+" 姓名:"+namec+" paycitation:"+paycitation+" payear:"+payear+" paymonth:"+paymonth+" payformat:"+payformat);
 				//return false;
				code_data="";
				code_data={
						typ    	   :typ,
		        		cardno     :cardno,
		        		idtno      :idtno,
		        		namec      :namec,
		        		paycitation:paycitation,
		        		payear     :payear,
		        		paymonth   :paymonth,
		        		payformat  :payformat
		        	};
				console.log("所得稅查看:code_data:"+JSON.stringify(code_data)+" hid:"+pahid);
				
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'getTaxViewList',
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
						$('#TblTaxView').show();
						oTable = $('#TblTaxView').dataTable(objCopyA);
						
						oTable.fnClearTable(0);		//清空資料
								
						if (data.taxViewList!=null && data.taxViewList.length>0){	
							oTable.fnAddData(data.taxViewList);//重新給資料
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
		else if(id == 'btn_qryTaxSum'){		//所得稅彙總_查詢
			msg = $.checkFields(id);
			if(msg == ''){
				typ=$("#qry_sumtyp").val();
				paycitation=$("#qry_sumTickno").val().toUpperCase();			
				payear=$.getSelectVals("qry_sumYear");
				paymonth=$.getSelectVals("qry_sumMon");
				payformat=$.getSelectVals("qry_sumFmt");
				
 				console.log("所得稅彙總: paycitation:"+paycitation+" payear:"+payear+" paymonth:"+paymonth+" payformat:"+payformat);
 				//return false;
				code_data="";
				code_data={
						typ    	   :typ,
						paycitation:paycitation,
		        		payear     :payear,
		        		paymonth   :paymonth,
		        		payformat  :payformat
		        	};
				console.log("所得稅彙總:code_data:"+JSON.stringify(code_data)+" hid:"+pahid);
				
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'getTaxSumList',
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
						$('#TblTaxSum').show();
						oTable = $('#TblTaxSum').dataTable(objCopyB);
						
						oTable.fnClearTable(0);		//清空資料
								
						if (data.taxSumList!=null && data.taxSumList.length>0){	
							oTable.fnAddData(data.taxSumList);//重新給資料
						}
						oTable.fnDraw();
						//解決標題不一致問題
						var table = $('#TblTaxSum'  ).DataTable();
				 		$('#container').css( 'display', 'block' );
						table.columns.adjust().draw();						
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
		else if(id == 'btn_qryTaxStat'){		//所得稅統計_查詢
			msg = $.checkFields(id);
			if(msg == ''){
				cardno=$("#qry_StatCard").val().toUpperCase();
				idtno=$("#qry_StatId").val().toUpperCase();
				namec=$("#qry_StatNamec").val();
				payear=$.getSelectVals("qry_StatYear");
				paymonth=$.getSelectVals("qry_StatMon");
				paydept=$.getSelectVals("qry_StatDept");
				
 				console.log("所得稅統計: payear:"+payear+" paymonth:"+paymonth+" paydept:"+paydept+" 卡號:"+cardno+" 身分證:"+idtno+" 姓名:"+namec);
 				
				code_data="";
				code_data={
						cardno     :cardno,
		        		idtno      :idtno,
		        		namec      :namec,
		        		payear     :payear,
		        		paymonth   :paymonth,
		        		paydept    :paydept
		        	};
				console.log("所得稅統計:code_data:"+JSON.stringify(code_data)+" hid:"+pahid);
				//return false;
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'getTaxStatList',
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
						$('#TblTaxStat').show();
						oTable = $('#TblTaxStat').dataTable(objCopyD);
						
						oTable.fnClearTable(0);		//清空資料
								
						if (data.taxStatList!=null && data.taxStatList.length>0){	
							oTable.fnAddData(data.taxStatList);//重新給資料
						}
						oTable.fnDraw();
						//解決標題不一致問題
						var table = $('#TblTaxStat'  ).DataTable();
				 		$('#container').css( 'display', 'block' );
						table.columns.adjust().draw();						
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
		else if(id=='subTAXPSN_Insbtn'||id=='subTAXPSN_Updbtn'){//所得稅維護 新增一筆 更改
 			if ( $("#subTAXPSNForm").valid() ){
				msg = $.checkFields(id);
				if(msg == ''){
					var formnm = "subTAXPSNForm";
					var jsonString = $.getDataInfo(formnm); //取得目前的資料
					var pk_data={
		        		payidno : $('#payidno').val().toUpperCase(),
		        		payear  : $("#payear option:selected").val(),
		        		hid		: pahid,
		        	};
//					return false;
					
					if (id =='subTAXPSN_Updbtn')		
						$('#tblmethod').val("update");
					else if (id =='subTAXPSN_Insbtn')		
						$('#tblmethod').val("insert");
						
					oTable = $('#TblTaxPsnList').dataTable().api();
					uTable = $('#TblTaxPsnList').dataTable();  	
					info = oTable.page.info();		//記錄目前頁碼
					currentPage=info.page;
					
					$.ajax({
						url:"${pageContext.request.contextPath}/salController.do",
						data:{
							method  : 'updTaxByKey',
							tblname : tblname,
							tabJson : jsonString,
							pk_data : JSON.stringify(pk_data),							
							hid     : pahid,
							userid  : paUserid,
							item    : $('#tblmethod').val()
						},
						type:"POST",			//type:POST, GET(default:GET)
						dataType:"json",	//dataType: text, xml, html, script, json, jsonp
						async: true,	//逐步執行
						contentType: "application/x-www-form-urlencoded;charset=utf-8",  
						success:function(data) {
							if (data.status!=null||typeof(data.status)!="undefined"){	//show msg,dialog不用關閉
								alert(data.msg);
							}else{
								$("#taxpsnItemModal").modal("hide");
								uTable.fnClearTable(0);		//清空資料
								
								$("#btn_qryTaxTask").trigger( "click" );  
								uTable.fnPageChange(currentPage);		//切到原先某碼
								
								if (data.msg!=null){
									alert(data.msg);
								}
							}	
						},
						error: function(xhr){					
							alert("所得稅個人明細維護"+$('#tblmethod').val()+"失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
						}
					});
				}else{
					alert(msg);
					return false;
				}
			}
		}
		else if(id=='btn_handelTax'){//應稅處理
			msg = $.checkFields(id);
			if(msg == ''){
				var in_data="";
				var yy="";
				selCode=$("#qryType option:selected").val().split(':')[0];
				var selNm=$("#qryType option:selected").val().split(':')[1];
				var selEn=$("#qryType option:selected").val().split(':')[2];
				//1[入稅]轉帳發薪明細
				if (selCode == "1"){
					salnum =$("#handing_Datyr option:selected").val();
	 				console.log(" 選項:"+selCode+" 餉單編號:"+salnum);
	 				in_data={
							selEn     :selEn,
			        		salnum    :salnum
			        	};
			    }
		        //2[刪除]轉帳發薪明細	
		        else if (selCode ==2){
		        	yy =$("#handing_TaxTranyr option:selected").val().split('-')[0];
		        	var mm =$("#handing_TaxTranyr option:selected").val().split('-')[1];
		        	var catg =$("#handing_TaxTranyr option:selected").val().split('-')[2];
	 				console.log(" 選項:"+selCode+" yy:"+yy+" mm:"+mm+" catg:"+catg);
	 				in_data={
							selEn:selEn,
			        		yy   :yy,
			        		mm   : mm,
			        		catg : catg
			        };
		        }
		        //3[匯入]批次人工入稅
		        else if (selCode ==3){
		        	url="${pageContext.request.contextPath}/salController.do";
		        	var param = '?method=uploadFile' +
								'&selEn=' + selEn +
								'&userid=' + userid +
								'&hid=' + pahid;
		        
			        console.log(" 選項:"+selCode+" url+param:"+url+param);
		        }
		        //4[刪除]批次人工入稅
		        else if (selCode ==4){
		        	var taskTickno=$("#handing_taskTickno").val();
		        	console.log(" 選項:"+selCode+" handing_taskTickno:"+taskTickno);
	 				in_data={
							selEn    : selEn,
			        		citation : taskTickno
			        };
		        }
		        //5身分證檢查及流水號編號
		        //6產生媒體申報檔	
		        else if (selCode ==5 || selCode ==6){
		        	yy =$("#handing_Taxyr option:selected").val();
		        	console.log(" 選項:"+selCode+" yy:"+yy);
	 				in_data={
							selEn:selEn,
			        		yy   :yy
			        };
		        }	
				
				//return false;
				if (selCode != 3){
					showLoading(); //顯示loading
					$.ajax({
						url:"${pageContext.request.contextPath}/salController.do",
						data:{
							method    : 'handelTax',
							selNm     : selNm,
							in_data   : JSON.stringify(in_data),
							userid    : paUserid,
							hid       : pahid
						},
						type:"POST",	//type:POST, GET(default:GET) processData:false,
						dataType:"json",	//dataType: text, xml, html, script, json, jsonp
						contentType: "application/x-www-form-urlencoded;charset=utf-8", 
						success:function(data) {
							if (data.status!=null||typeof(data.status)!="undefined"){	//show msg,dialog不用關閉
								alert(data.msg);
								$.getLoadCodeTbl('payTax','Y');
							}else{	
								if (data.msg!=null){
									alert(data.msg);
								}				
							}
						},	
						error: function(xhr){					
							alert("應稅處理_"+selEn+" 失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
						},
						complete: function(data) {
							hideLoading(); //隱藏loading
						}
					});
				}
				//3[匯入]批次人工入稅
				else if(selCode == 3){
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
			}	
			else{
				alert(msg);
				return false;
			}	
		}			
	});
		
	$('body').on('change keyup', '.keypay', function() {
		$.sumPay(); 
	});
	//應稅所得,己扣所得稅加總
	$.sumPay = function(){
		
		var tPay=0;	//應稅所徥總和
		var tDeduct=0;	//己扣所得稅cash_paytaxdeduct
		var id="TAXCASH";
		
		$(':input[name*="cash_paytaxpay"]').each(function(){		
	  		tPay=tPay+getInt($(this).val());    	  		
		});
		
		$(':input[name*="cash_paytaxdeduct"]').each(function(){		
	  		tDeduct=tDeduct+getInt($(this).val());    	  		
		});	
		$("#cash_sumtaxpay").val(tPay);
		$("#cash_sumtaxded").val(tDeduct);
	};
	//有修正,flag設成U
    $("#TAXCASH").on("change keyup", function() { 
    	
		tr = $(this).closest('table').find("tbody").find('tr');
		/* if (tr.find(':input[name*="payflag"]').val()==""){
			tr.find(':input[name*="payflag"]').val("U");
		} */
		//第1行,key是空白,要補index
		index = $(this).closest('tr').index();
		if ( index == 0){
			tr.find(':input[name*="cashKpayidno"]').val($("#payidno").val());
			tr.find(':input[name*="cashKhid"]').val(pahid);
		}
    });
    //更正指定的行
    $("#myTabContent").on("click", ".update_row", function() { 
    	var this_table = $(this).closest('table');
    	table_id = this_table.attr("id");
    	var this_form =$(this).closest('form').attr("id");
    	
    	tr = $(this).closest('table').find("tbody").find('tr');
		//第1行,key是空白,要補index
		index = $(this).closest('tr').index();
		if ( index == 0){
			tr.find(':input[name*="cashKpayidno"]').val($("#payidno").val());
			tr.find(':input[name*="cashKhid"]').val(pahid);
		}
    	
    	if ($("#" + this_form + "").valid()){    	
	    	index = $(this).closest('tr').index();
	    	var row = $(this).closest('tr').index() + 1; //取得所選擇到的列
	    	
	    	$(".cashOnly").prop("disabled", false);	//將readOnly->false
	    	
	    	$tr = $('#'+table_id+" tbody").find("tr").eq(index);
 			//$tds = $(tr).find('td');	    	
	    	
	    	console.log("row:"+row+" month:"+$tr.find(':input[name*="cashKpaymonth"]').val());
	    	
 	  		$.updTblByRow(table_id,$tr,"U");
	    }
	    	    	
    });
    //列操作_insert/update/delete
	$.updTblByRow = function(table_id,$tr,item){
		obj = {};
    	array = new Array();
    	keyobj = {};
    	value = [];
    	var data_id="";
    	var strKey="";
    	data_val="";
    	
		$tr.find('input:text,input:radio:checked, select, textarea, input:hidden').each(function (i, el) {
  			//取name第6碼後,ex:cash_payidno
  			//key取name第5碼,ex:cashKpayidno
  			data_id = $(this).attr("name").substring(5);
  			strKey = $(this).attr("name").substring(4,5);
  			data_val = $(this).val();
  			obj[data_id] = data_val; //名稱+數值
  			if (strKey == "K"){	//index
  				keyobj[data_id] = data_val; 
  			}
  			console.log("i:"+i+" data_id:"+data_id+" val:"+data_val);
  		});
  		
	    if (obj != []  ){
	    	value.push(obj);
	    	array.push( { "formnm": table_id, "value": value } ); //將動態表格資料轉換為json
	    }
  		jsonArray = JSON.stringify(array); // 轉換成json
	    	    
	    console.log(jsonArray);
	    console.log("keyobj"+JSON.stringify(keyobj));
	    /* 
		    var pk_data={
		        		payidno     : $tr.find(':input[name*="cash_payidno"]').val().toUpperCase(),
		        		payear      : $tr.find(':input[name*="cash_payear"]').val(),
		        		paymonth    : $tr.find(':input[name*="cash_paymonth"]').val(),
		        		paycatg     : $tr.find(':input[name*="cash_paycatg"]').val(),
		        		payformat   : $tr.find(':input[name*="cash_payformat"]').val(),
		        		paycitation : $tr.find(':input[name*="cash_paycitation"]').val(),
		        	};
		    console.log("pk_data"+JSON.stringify(pk_data)); */  
		//return false;     		
	    $.ajax({
				url:"${pageContext.request.contextPath}/salController.do",
				data:{
					method  : 'updTaxByKey',
					tblname : table_id,
					tabJson : jsonArray,
					pk_data : JSON.stringify(keyobj),							
					hid     : pahid,
					userid  : paUserid,
					item    : item
				},
				type:"POST",			//type:POST, GET(default:GET)
				dataType:"json",	//dataType: text, xml, html, script, json, jsonp
				async: true,	//逐步執行
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
					alert("所得稅_入工_"+item+" 失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
				}
			});
	};
    
    
	//刪除指定的table行
    $("#myTabContent").on("click", ".delete_row", function() { 
    	var this_table = $(this).closest('table');
    	table_id = this_table.attr("id");
    	index = $(this).closest('tr').index();
    	
    	//刪除資料庫
    	$tr = $('#'+table_id+" tbody").find("tr").eq(index);
		//$tds = $(tr).find('td');
	  	$.updTblByRow(table_id,$tr,"D");
	  	
    	//刪除頁面上
    	var tr_length = $(this).closest('table').find("tbody").find('tr').length; //欄位數量

    	//刪除一欄
    	if ( tr_length == 1 ) {
    		cleanTable($(this).closest('table').attr("id")); //清除該頁面所有資料    		
    	} else {
    		$(this).closest("tr").remove();
    	}
    	//重新計算加總
    	$.sumPay(); 	    	
    });
	
	//新增指定的table行
    $('.add_row').on( "click", function() {
    	var this_table = $(this).prev("table");
    	add_row(this_table);	    	
    });
    //指定表格新增一欄
	function add_row(this_table) {
		
		table_id = this_table.attr("id");
		var row_html = "<tr>" + this_table.find("tbody").find("tr").eq(0).html() + "</tr>";
		
		//console.log(row_html);
		
    	this_table.append(row_html);
    	
    	var rows = this_table.find("tbody").find("tr").length;
    	
    	//塞預設值
    	if (table_id=="TAXCASH"){
    		tr = $('#'+table_id+" tbody").find("tr").eq(rows-1);
			tr.find(':input[name*="cashKpayidno"]').val($("#payidno").val());
			tr.find(':input[name*="cashKhid"]').val(pahid);
			
			$tds = $(tr).find('td');
	  		
	  		$tds.find('input:text,input:radio:checked, select, textarea, input:hidden').each(function (i, el) {
	  			$(this).prop("disabled", false);	//將readOnly->false
	  			
	  		});
			
    	}
	}
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
	   <%--  $('.selYY').selectpicker('val','<%=today_y%>'); --%>
	    	    
	} 
	
	//所得稅查看
 	var objCopyA = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyA.aoColumns = [	
							{ "sTitle":"","mData": null },
							{ "sTitle":"年",			"mData": "payear" },
							{ "sTitle":"月",			"mData": "paymonth" },
							{ "sTitle":"身份證字號",	"mData": "payidno" },
							{ "sTitle":"卡號",		"mData": "paycard" },
							{ "sTitle":"姓名",		"mData": "paynamec" },
							{ "sTitle":"職號",		"mData": "payempno" },
							{ "sTitle":"單位",		"mData": null },
							{ "sTitle":"性質",		"mData": "payitem" },
							{ "sTitle":"格式",		"mData": "payformat" },
							{ "sTitle":"轉帳項目",	"mData": "catgname" },													
							{ "sTitle":"應稅所得",	"mData": "paytaxpay" },
							{ "sTitle":"己扣",		"mData": "paytaxdeduct" },
							{ "sTitle":"傳票製票號",	"mData": "paycitation" }	
						 ];
	objCopyA.iDisplayLength=10;
	objCopyA.aoColumnDefs= [
							 {
					         'targets': 7,
					         'searchable':true,
					         'orderable':true,
					         'className': 'dt-body-left',
					         'render': function (data, type, row, meta){
					         		return row.pacstno+" "+row.padepnmc;
					         		}
					         },
					         {
					         'targets': [ 11,12],
					         'searchable':true,
					         'orderable':true,
					         'className': 'dt-body-right',
					         'render': $.fn.dataTable.render.number(',','.',0)
					         }		
     	 					];
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
				"footerCallback": function ( row, data, start, end, display ) {
		            var api = this.api(), data;
		 
		            // Remove the formatting to get integer data for summation
		            var intVal = function ( i ) {
		                return typeof i === 'string' ?
		                    		  i.replace(/[\$,]/g, '')*1 :
		                    		  typeof i === 'number' ? i : 0;
		            };
		 
		            // 應稅所得
		            var pTotal = api
		                .column( 11	)
		                .data()
		                .reduce( function (a, b) {
		                    		return intVal(a) + intVal(b);
		                		}, 0 );
		 			// Total over this page
		            var pageTotal_1 = api
		                .column( 11	, { page: 'current'} )
		                .data()
		                .reduce( function (a, b) {
		                    return intVal(a) + intVal(b);
		                }, 0 );
		                
		            // Update footer
		            $( api.column( 11 ).footer() ).html(
		            	'$'+pageTotal_1 +' <br>( $'+ pTotal +' 合計)'		                
		            );
		            
		           // 已繳所得稅
		            var dTotal = api
		                .column( 12	 )
		                .data()
		                .reduce( function (a, b) {
		                    		return intVal(a) + intVal(b);
		                		}, 0 );
		 			// Total over this page
		            var pageTotal_2 = api
		                .column( 12	, { page: 'current'} )
		                .data()
		                .reduce( function (a, b) {
		                    return intVal(a) + intVal(b);
		                }, 0 );
		            // Update footer
		            $( api.column( 12 ).footer() ).html(
		                '$'+pageTotal_2 +' <br>( $'+ dTotal +' 合計)'	 
		            );		             
		        }   							
			});
						    
	$('#TblTaxView').dataTable(objCopyA);
	
	//所得稅彙總
 	var objCopyB = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyB.aoColumns = [	
							{ "sTitle":"",			"mData": null },
							{ "sTitle":"年",			"mData": "payear" },
							{ "sTitle":"月",			"mData": "paymonth" },
							{ "sTitle":"轉帳項目",	"mData": null },
							{ "sTitle":"應稅所得",	"mData": "taxpay" },
							{ "sTitle":"己扣",		"mData": "taxdeduct" },
							{ "sTitle":"人數",		"mData": "taxper" },
							{ "sTitle":"格式",		"mData": "payformat" }
						 ];
	objCopyB.iDisplayLength=10;
	objCopyB.aoColumnDefs= [
							 {
					         'targets': 3,
					         'searchable':true,
					         'orderable':true,
					         'className': 'dt-body-left',
					         'render': function (data, type, row, meta){
					         		return row.paycatg+" "+row.catgname;
					         		}
					         },
					         {
					         'targets': [ 4,5,6],
					         'searchable':true,
					         'orderable':true,
					         'className': 'dt-body-right',
					         'render': $.fn.dataTable.render.number(',','.',0)
					         }		
     	 					];
	objCopyB = $.extend(true,objCopyB, {
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
				 "footerCallback": function ( row, data, start, end, display ) {
		            var api = this.api(), data;
		 
		            // Remove the formatting to get integer data for summation
		            var intVal = function ( i ) {
		                return typeof i === 'string' ?
		                    		  i.replace(/[\$,]/g, '')*1 :
		                    		  typeof i === 'number' ? i : 0;
		            };
		 
		            // 應稅所得
		            var pTotal = api
		                .column( 4	)
		                .data()
		                .reduce( function (a, b) {
		                    		return intVal(a) + intVal(b);
		                		}, 0 );
		 			// Total over this page
		            var pageTotal_1 = api
		                .column( 4	, { page: 'current'} )
		                .data()
		                .reduce( function (a, b) {
		                    return intVal(a) + intVal(b);
		                }, 0 );
		                
		            // Update footer
		            $( api.column( 4 ).footer() ).html(
		            	'$'+pageTotal_1 +' <br>( $'+ pTotal +' 合計)'		                
		            );
		            
		           // 已繳所得稅
		            var dTotal = api
		                .column( 5	 )
		                .data()
		                .reduce( function (a, b) {
		                    		return intVal(a) + intVal(b);
		                		}, 0 );
		 			// Total over this page
		            var pageTotal_2 = api
		                .column( 5	, { page: 'current'} )
		                .data()
		                .reduce( function (a, b) {
		                    return intVal(a) + intVal(b);
		                }, 0 );
		            // Update footer
		            $( api.column( 5 ).footer() ).html(
		                '$'+pageTotal_2 +' <br>( $'+ dTotal +' 合計)'	 
		            );	             
		        }    							
			});
						    
	$('#TblTaxSum').dataTable(objCopyB);
	
	//所得稅統計
 	var objCopyD = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyD.aoColumns = [	
							{ "sTitle":"",			"mData": null },
							{ "sTitle":"年",			"mData": "payear" },
							{ "sTitle":"身分證",		"mData": "payidno" },
							{ "sTitle":"卡號",		"mData": "paycard" },
							{ "sTitle":"姓名",		"mData": "paynamec" },
							{ "sTitle":"單位",		"mData": "padepnmc" },
							{ "sTitle":"職稱",		"mData": "paytitlp" },
							{ "sTitle":"應稅所得",	"mData": "taxpay" },
							{ "sTitle":"己扣",		"mData": "taxdeduct" }
						 ];
	objCopyD.iDisplayLength=10;
	objCopyD.aoColumnDefs= [
							 {
					         'targets': [ 7,8],
					         'searchable':true,
					         'orderable':true,
					         'className': 'dt-body-right',
					         'render': $.fn.dataTable.render.number(',','.',0)
					         }		
     	 					];
	objCopyD = $.extend(true,objCopyD, {
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
				 "footerCallback": function ( row, data, start, end, display ) {
		            var api = this.api(), data;
		 
		            // Remove the formatting to get integer data for summation
		            var intVal = function ( i ) {
		                return typeof i === 'string' ?
		                    		  i.replace(/[\$,]/g, '')*1 :
		                    		  typeof i === 'number' ? i : 0;
		            };
		 
		            // 應稅所得
		            var pTotal = api
		                .column( 7	)
		                .data()
		                .reduce( function (a, b) {
		                    		return intVal(a) + intVal(b);
		                		}, 0 );
		 			// Total over this page
		            var pageTotal_1 = api
		                .column( 7	, { page: 'current'} )
		                .data()
		                .reduce( function (a, b) {
		                    return intVal(a) + intVal(b);
		                }, 0 );
		                
		            // Update footer
		            $( api.column( 7).footer() ).html(
		            	'$'+pageTotal_1 +' <br>( $'+ pTotal +' 合計)'		                
		            );
		            
		           // 已繳所得稅
		            var dTotal = api
		                .column( 8	 )
		                .data()
		                .reduce( function (a, b) {
		                    		return intVal(a) + intVal(b);
		                		}, 0 );
		 			// Total over this page
		            var pageTotal_2 = api
		                .column( 8	, { page: 'current'} )
		                .data()
		                .reduce( function (a, b) {
		                    return intVal(a) + intVal(b);
		                }, 0 );
		            // Update footer
		            $( api.column( 8 ).footer() ).html(
		                '$'+pageTotal_2 +' <br>( $'+ dTotal +' 合計)'	 
		            );	             
		        }    							
			});
						    
	$('#TblTaxStat').dataTable(objCopyD);
	
	//所得稅查看
 	var objCopyC = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyC.aoColumns = [	
							{ "sTitle":"","mData": null },
							{ "sTitle":"年",			"mData": "payear" },
							{ "sTitle":"身份證字號",	"mData": "payidno" },
							{ "sTitle":"卡號",		"mData": "paycard" },
							{ "sTitle":"姓名",		"mData": "paynamec" },
							{ "sTitle":"職號",		"mData": "payempno" },
							{ "sTitle":"單位",		"mData": null },								
							{ "sTitle":"起日",		"mData": "paybegin" },	
							{ "sTitle":"訖日",		"mData": "payend" },
							{ "sTitle":"自繳退休金",	"mData": "paysereamt" },
							{ "sTitle":"流水號50",	"mData": "payserno" },
							{ "sTitle":"流水號9A",	"mData": "payserno9a" },
							{ "sTitle":"流水號9B",	"mData": "payserno9b" },
							{ "sTitle":"流水號91",	"mData": "payserno91" },
							{ "sTitle":"流水號92",	"mData": "payserno92" },
							{ "sTitle":"流水號51",	"mData": "payserno51" },
							{ "sTitle":"註記",		"mData": "payidflg" }												
						 ];
	objCopyC.iDisplayLength=10;
	objCopyC.aoColumnDefs= [
							 {
					         'targets': 6,
					         'searchable':true,
					         'orderable':true,
					         'className': 'dt-body-left',
					         'render': function (data, type, row, meta){
					         		return row.pacstno+" "+row.padepnmc;
					         		}
					         },
					         {
					         'targets': [ 9],
					         'searchable':true,
					         'orderable':true,
					         'className': 'dt-body-right',
					         'render': $.fn.dataTable.render.number(',','.',0)
					         }			
     	 					];
	objCopyC = $.extend(true,objCopyC, {
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
                    	//到某頁
					 	$("#TblTaxPsnList_paginate").append("  到第 <input style='height:28px;line-height:28px;width:40px;' class='margin text-center' id='changePageA' type='text'> 頁  <a class='btn btn-success shiny' style='margin-bottom:5px' href='javascript:void(0);' id='dataTable-btn'>確認</a>");
					 	oTable = $("#TblTaxPsnList").dataTable();
					 	$('#dataTable-btn').click(function(e) {
					 		if($("#changePageA").val() && $("#changePageA").val() > 0) {
					 			var redirectpage = $("#changePageA").val() - 1;
					 		} else {
					 			var redirectpage = 0;
					 		}	
					 		oTable.fnPageChange(redirectpage);		
					 	});
                 },
                 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
				 	$('td:eq(2)', nRow).html('<a href = "#taxpsnData" class= "taxpsnData">'+aData.payidno+'</a>' );
				 },
				"footerCallback": function ( row, data, start, end, display ) {
		            var api = this.api(), data;
		 
		            // Remove the formatting to get integer data for summation
		            var intVal = function ( i ) {
		                return typeof i === 'string' ?
		                    		  i.replace(/[\$,]/g, '')*1 :
		                    		  typeof i === 'number' ? i : 0;
		            };
		        }   							
			});
				    
	$('#TblTaxPsnList').dataTable(objCopyC);
	
	//顯示pa.pataxpsn明細
	$('#TblTaxPsnList tbody').on('click', '.taxpsnData', function () {
		oTable = $('#TblTaxPsnList').dataTable().api();
		info = oTable.page.info();		//記錄目前頁碼
		currentPage=info.page;
        tr  = $(this).parents('tr');
        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data	 		
		idtno  =rowData.payidno;
		payear =rowData.payear;
		hid    =rowData.hid;
		//找pa.pataxpsn及pa.pataxtran,pa.pataxcash
		code_data="";
		code_data={
        		idtno      :idtno,
        		payear     :payear
        	};
		console.log("所得稅pataxpsn查詢:code_data:"+JSON.stringify(code_data));
		
		$.getTaxPsnByKey(code_data);
		
	});
	//依KEY找出pa.padatpsn
	$.getTaxPsnByKey = function(code_data){
		$.ajax({
			url:"${pageContext.request.contextPath}/salController.do",
			data:{
				method  : 'getTaxPsnByKey',
				code_data :JSON.stringify(code_data),
				hid     : pahid,
				userid  : paUserid,
			},
			type:"POST",			//type:POST, GET(default:GET)
			dataType:"json",	//dataType: text, xml, html, script, json, jsonp
			contentType: "application/x-www-form-urlencoded;charset=utf-8",
			success:function(data) {
				if (data.taxPsnItem!=null){
					//顯示月支個人明細,轉帳所得稅
					cleanTable("TAXTRAN");
					cleanTable("TAXCASH");
					
					$.doTaxpsnDetail(data.taxPsnItem,data.taxTranList,data.taxCashList,"upd");
					
					//解決標題不一致問題
					/*var table = new $('#TblDatPayList').DataTable();
			 		$('#container').css( 'display', 'block' );
					table.columns.adjust().draw(); */
// 					$.fn.dataTable.tables( {visible: true, api: true} ).columns.adjust();
				}
			}  
		}) 
	};
	//顯示pa.padatpsn明細
	$.doTaxpsnDetail = function(mData,mTran,mCash,flag){
		//modal標題:支扣代碼+中文+mData.paynamec+mData.payear
		$("#taxpsnItemModal").find('.modal-title').text(mData.payear+"年"+mData.paynamec);
		
 		$("#payear").selectpicker('val', mData.payear);
		$("#payidno").val(mData.payidno);
		$("#paycard").val(mData.paycard);
		$("#paynamec").val(mData.paynamec);		
		$("#payempno").val(mData.payempno);
		$("#paydept").val(mData.paydept);
		$("#payidflg").val(mData.payidflg);
		$("#paybegin").val(mData.paybegin.dts);
		$("#payend").val(mData.payend.dts);
		$("#payserno").val(mData.payserno);
		$("#payupdid").val(mData.payupdid);
		$("#payupdtm").val(mData.payupdtm);
		$("#payserno9a").val(mData.payserno9a);
		$("#payserno9b").val(mData.payserno9b);
		$("#paysereamt").val(mData.paysereamt);
		$("#payserno91").val(mData.payserno91);
		$("#payserno92").val(mData.payserno92);
		$("#hid").val(mData.hid);
		$("#payserno51").val(mData.payserno51);
		$("#payaddrs").val(mData.payaddrs);
		if (flag=="upd"){
			$("#payear").attr("readOnly",true);	//將key設成readOnly
			$("#payidno").attr("readOnly",true);	//將key設成readOnly
			$("label.error").hide();
			$(".error").removeClass("error");
	        $("#taxpsnItemModal").modal("show");
	        $("#subTAXPSN_Insbtn").prop("disabled", true);
			$("#subTAXPSN_Updbtn").prop("disabled", false);
		}
		
		
		//轉帳所得稅明細
		var mrcontdy = mTran;
		var row_html="" ;
		var sumtaxpay=0;
		var sumtaxded=0;
		if (mrcontdy!=null && mrcontdy.length>0){
			
			$.each(mrcontdy, function(idx) {
				var id="TAXTRAN";
				
				if ( idx!=0) {
					row_html = "<tr>" + $('#'+id +" tbody").find("tr").eq(0).html() + "</tr>";
	    			$('#'+id+" tbody").append(row_html);
				}
				//帶入動態表格數值
				tr = $('#'+id+" tbody").find("tr").eq(idx);
				tr.find(':input[name*="tran_payear"]').val(mrcontdy[idx].payear);
				tr.find(':input[name*="tran_paymonth"]').val(mrcontdy[idx].paymonth);
				tr.find(':input[name*="tran_paycatg"]').val(mrcontdy[idx].paycatg);
				tr.find(':input[name*="tran_paytaxpay"]').val(mrcontdy[idx].paytaxpay); 
				tr.find(':input[name*="tran_paytaxdeduct"]').val(mrcontdy[idx].paytaxdeduct);
				tr.find(':input[name*="tran_payupdtm"]').val(mrcontdy[idx].payupdtm);
				
				//console.log(row_html);
			});
			
			$(':input[name*="tran_paytaxpay"]').each(function(){		//應稅所得加總
	  			sumtaxpay=sumtaxpay+getInt($(this).val());
	  		});	
	  		
	  		$(':input[name*="tran_paytaxdeduct"]').each(function(){		//己扣稅加總
		  		sumtaxded=sumtaxded+getInt($(this).val());    	  		
			});
			$("#tran_sumtaxpay").val(sumtaxpay);
			$("#tran_sumtaxded").val(sumtaxded);
		}
		
		//現金所得稅明細
		mrcontdy = mCash;
		row_html="" ;
		sumtaxpay=0;
		sumtaxded=0;
		$(".cashOnly").prop("disabled", false);	//將key設成readOnly
		if (mrcontdy!=null && mrcontdy.length>0){
			console.log("mCash:"+mrcontdy.length);
			$.each(mrcontdy, function(idx) {
				var id="TAXCASH";
				if ( idx!=0) {
					row_html = "<tr>" + $('#'+id +" tbody").find("tr").eq(0).html() + "</tr>";
	    			$('#'+id+" tbody").append(row_html);
				}
				
				//帶入動態表格數值
				tr = $('#'+id+" tbody").find("tr").eq(idx);
				tr.find(':input[name*="cashKpayidno"]').val(mrcontdy[idx].payidno);
				tr.find(':input[name*="cashKpayear"]').val(mrcontdy[idx].payear);
				tr.find(':input[name*="cashKpaymonth"]').val(mrcontdy[idx].paymonth);
				tr.find(':input[name*="cashKpaycatg"]').val(mrcontdy[idx].paycatg);
				tr.find(':input[name*="cashKpayformat"]').val(mrcontdy[idx].payformat);
				tr.find(':input[name*="cash_paytaxpay"]').val(mrcontdy[idx].paytaxpay); 
				tr.find(':input[name*="cash_paytaxdeduct"]').val(mrcontdy[idx].paytaxdeduct);
				tr.find(':input[name*="cashKpaycitation"]').val(mrcontdy[idx].paycitation);
				tr.find(':input[name*="cash_payupdtm"]').val(mrcontdy[idx].payupdtm); 
				tr.find(':input[name*="cashKhid"]').val(mrcontdy[idx].hid);
				//console.log(row_html);
			});
			
			$(".cashOnly").prop("disabled", true);	//將key設成readOnly
			$(':input[name*="cash_paytaxpay"]').each(function(){		//應稅所得加總
	  			sumtaxpay=sumtaxpay+getInt($(this).val());
	  		});	
	  		
	  		$(':input[name*="cash_paytaxdeduct"]').each(function(){		//己扣稅加總
		  		sumtaxded=sumtaxded+getInt($(this).val());    	  		
			});
			
			$("#cash_sumtaxpay").val(sumtaxpay);
			$("#cash_sumtaxded").val(sumtaxded);
		} 
	};
	
	$('#payidno').blur(function(){
		var idno = $(this).val();
		if (idno !=""){
			//用正規表達式判斷身分證
			reCheckCase = /(^[a-zA-Z]{1,2}[0-9]{1,9}$)|(^[0-9]{1,10}$)/ ;//包含以1-2個英文字為字首加上1-8個數字、1-10個數字
			//reCheckCase = /^[A-Za-z][12]\d{8}$/ ;   //10碼身分證可以,但統一編號尚未
			if(!reCheckCase.test(idno)){
				alert('輸入的身分證字號格式有誤，請重新輸入');
				return;
			}
		}
	});	
	//輸入代碼,找是否重複 新增才要檢查
	$(".findkey").blur(function(){
// 		var key=$(this).attr('id');
		console.log("readOnly:"+$(this).attr("readOnly"));
		
		if($(this).attr("readOnly")==undefined || $(this).attr("readOnly")==false){	//未設定readOnly屬性
			var key=$(this).val().toUpperCase();
			console.log("key:"+key);
			var code_data="";
			if ( key != ""){
				tblname="TAXPSN";
				if (tblname=="TAXPSN"){						
					if ($("#payear option:selected").val()=="" && $('#payidno').val()==""  ){
						return false;
					}else{
			        	code_data={
			        		"payear"  :$("#payear option:selected").val(),
			        		"idtno"   :key
			        	}
			        	console.log("所得稅findkey:code_data:"+JSON.stringify(code_data));	
		        	}
		        }
		        //return false;	
				if (!$("#subTAXPSN_Insbtn").prop("disabled")){
					$.ajax({				
							url:"${pageContext.request.contextPath}/salController.do",
							data:{
								method  : 'getTaxPsnByKey',
								code_data :JSON.stringify(code_data),
								hid     : pahid,
								userid  : paUserid,
							},
			
							type:"POST",		//type:POST, GET(default:GET)
							dataType:"json",	//dataType: text, xml, html, script, json, jsonp
							contentType: "application/x-www-form-urlencoded;charset=utf-8",  
							async: false,
							cache: false,			
							success:function(data) {
								if ( data.taxPsnItem!=null ){
									alert("重複");
								}	
							},
							error: function(xhr){	
								alert("搜尋[輸入 KEY,找是否重複]過程中，程式發生錯誤!!!\nerror code:"+ xhr.status);
							}
						});
				}
			}
		}			
	});
	//取得整數
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
		var reg = /^[\d]+$/;
		if (!reg.test(result)) { //不是整數
			result = 0;
		}
		
		return parseInt(result);
	}	      
});
</script>
</head>
<body>

<ul class="nav nav-pills mb-3" id="pills-tab" role="tab">
  <li class="nav-item">
    <a class="nav-link active" id="pills-taxhanding-tab" data-toggle="pill" href="#byTaxHandingMain" role="tab" aria-controls="pills-taxhanding" aria-selected="true">應稅處理</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-taxtask-tab" data-toggle="pill" href="#byTaxTaskMain" role="tab" aria-controls="pills-taxtask" aria-selected="false">所得稅編輯</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-taxview-tab" data-toggle="pill" href="#byTaxViewMain" role="tab" aria-controls="pills-taxview" aria-selected="false">所得稅查看</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-taxsum-tab" data-toggle="pill" href="#byTaxSumMain" role="tab" aria-controls="pills-taxsum" aria-selected="false">所得稅彙總</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-taxstat-tab" data-toggle="pill" href="#byTaxStatMain" role="tab" aria-controls="pills-taxstat" aria-selected="false">所得稅統計</a>
  </li>
</ul>

<div class="tab-content" id="pills-tabContent">
	<!--應梲處理-->
  	<div class="tab-pane fade show active" id="byTaxHandingMain" role="tabpanel" aria-labelledby="pills-taxhanding-tab">
		<div class="container-fluid">
			<div class="row p-1 mb-1 bg-secondary">
				<div class="col-md-10">
					<div class="form-inline">
						<div class="input-group">
							<span class="input-group-text">選單</span>
			  				<select id="qryType" class="form-control">
								<option value="1:[入稅]轉帳發薪明細:InTran">1[入稅]轉帳發薪明細</option>
								<option value="2:[刪除]轉帳發薪明細:DelTran">2[刪除]轉帳發薪明細</option>
								<option value="3:[匯入]批次人工入稅:InBatCash">3[匯入]批次人工入稅</option>
		     					<option value="4:[刪除]批次人工入稅:DelBatCash">4[刪除]批次人工入稅</option>	
								<option value="5:身分證檢查及流水號編號:ChkID">5身分證檢查及流水號編號</option>
		     					<option value="6:產生媒體申報檔:MediaTax">6產生媒體申報檔</option>
		     					     					
							</select>
						</div>
					</div>
				</div>
				<div class="col-md-2">
					<button id="btn_handelTax" class="btn btn-info" type="button" ><i class="fa fa-cog"></i>執行</button>					
				</div>
			</div>
			<div class="row">
			<div class="col-md-12">
				<div id="explan_1" class="text-danger">同一月份,若有2筆以上餉單編號,分別處理</div>
				<div id="explan_2" class="text-danger d-none" >回復處理:將已匯入的所得稅刪除</div>
				<div id="explan_3" class="text-danger d-none" ></div>
			</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<div class="form-inline">
						<div class="input-group">
							<div class="input-group" id="divfield_1">
								<label for="handing_Datyr" >餉單編號-發薪日</label>
								<select id="handing_Datyr" class="form-control" title="餉單編號-發薪日">
									
								</select>
							</div>
							<div class="input-group" id="divfield_2">	
								<label for="handing_TaxTranyr" >已轉帳年月</label>
								<select id="handing_TaxTranyr"class=" form-control" title="已轉帳年月">
									
								</select>							
							</div>
							<div class="input-group d-none" id="divfield_5" >	
								<label for="handing_Taxyr" >西元年</label>
								<select id="handing_Taxyr" class=" form-control" title="西元年">
									
								</select>							
							</div>
							<div class="input-group d-none" id="divfield_3" >
								<form id="uploadfileForm">檔案 	
<!-- 								    <label class="sr-only" for="fileInput">來源</label> -->
								    <input type="file" accept=".csv,text/plain"  id="fileInput" name="fileInput">								   
							    </form> 
						   	 </div>
						   	 <div class="input-group d-none" id="divfield_4" >	
							    <label for="handing_Tickno" >傳票製票號</label>
								<input type="text" id="handing_taskTickno" size="9" class="form-control text-uppercase" placeholder="傳票製票號">
						   	 </div>
						</div>
					</div>
				</div>
				
			</div>	
		</div>
	</div>
 	<!--所得稅編輯-->
	<div class="tab-pane fade" id="byTaxTaskMain" role="tabpanel" aria-labelledby="pills-taxtatsk-tab">
		<div class="row" id="qryTaxTaskItem">
			<div class="col-9">
				<div class="form-inline">
	    			<select id="qry_taskYear" class="selectpicker selYY" data-width="fit"  data-live-search="true" multiple title="年">
<!-- 						<option value=""></option> -->
<%-- 						<c:forEach var="bitem" items="${alTaxYrTbl}" varStatus="loop"> --%>
<%-- 							<option value="${bitem.paybank}">${bitem.paybank}</option> --%>
<%-- 						</c:forEach> --%>
					</select>
					<input type="text" id="qry_taskTickno" size="9" class="form-control text-uppercase" placeholder="傳票製票號">
					<input type="text" id="qry_taskId" size="10" class="form-control text-uppercase" placeholder="身分證">
					<input type="text" id="qry_taskCard" size="4" class="form-control text-uppercase" placeholder="卡號">
					<input type="text" id="qry_taskNamec" size="12" class="form-control" placeholder="姓名" >						
				</div>
			</div>
			<div class="col-3">
				<button id="btn_qryTaxTask" class="btn btn-info" type="button" ><i class="fa fa-search"></i>查詢</button>
				<button id="btn_addTaxPsn" class="btn btn-success ml-auto" type="button" data-toggle="modal" data-target="#taxpsnItemModal"><i class="fa fa-edit"></i>新增一筆</button>
				<button id="btn_clnTaxTask" class="btn btn-warning" type="button"><i class="fa fa-eraser"></i>清空</button>
			</div>
		</div>
		<div class="row">
			<div class="col-12 border border-dark">
				<table id="TblTaxPsnList" class="table table-striped table-bordered table-hover" style="width="100%">
					<thead>
					</thead>
				</table>
			</div>
		</div>
 	</div>
 	
 	<!--所得稅編輯 model-->
	<div class="modal fade" id="taxpsnItemModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
	        <!-- Modal content-->
			<div class="modal-content" id="myTabContent">
	            <!-- Modal Header -->
				<div class="modal-header">
					<h3><span class="modal-title badge badge-success">個人明細</span></h3>					      							    
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
			 	</div>
			 	<!-- Modal Body -->
	            <div class="modal-body">
					<div id="accordion">
						
                 		<div class="card">							
							<INPUT type="hidden" id="tblmethod" name ="tblmethod">
							<div class="card-header">
								<div class="form-inline">
									<a class="card-link" data-toggle="collapse" href="#collapseOne">
							          	個人明細				          	
							        </a>
							        <button type="button" class="btn btn-primary ml-auto" id="subTAXPSN_Insbtn"><i class="fa fa-save"></i>確認儲存</button>
								   	<button type="button" class="btn btn-primary " id="subTAXPSN_Updbtn"><i class="fa-light fa-pen-to-square"></i>更改</button>
								   	<button type="button" class="btn btn-secondary " data-dismiss="modal"><i class="fa fa-undo"></i>返回</button>
							   	</div>
					        </div>
					        <div id="collapseOne" class="collapse show" data-parent="#accordion">
						        <div class="card-body">
						        	<div class="table-responsive-sm">
						        		<form id="subTAXPSNForm" >
								        	<table id="TAXPSN" class="table table-striped table-bordered table-hover table-sm form-inline">
												<tbody>
													<tr>
														<td><span class="text-danger">*</span>西元年:
															<select id="payear" name="payear" class="selectpicker required selYY" data-width="fit" >
																<c:forEach var="bitem" items="${alTaxYrTbl}" varStatus="loop">
																	<option value="${bitem.payear}">${bitem.payear}</option>
																</c:forEach>
															</select>
														</td>
														<td><span class="text-danger">*</span>身份證字號:
															<input type="text" id="payidno" name=payidno" class="form-control text-uppercase findkey required" value="" maxlength="10" size="10" />
														</td>
														<td>姓名:
															<input type="text" id="paynamec" name="paynamec" class="form-control form-control-sm required" value="" maxlength="60" size="60"  />
														</td>												
													</tr>
													<tr>	
														<td colspan=3>
															<div class="form-inline">
																<div class="form-group">
																	<label for="paycard" >卡號:</label>
																	<input type="text" id="paycard" name="paycard" class="form-control text-uppercase" value="" maxlength="4" size="4"  />
																</div>
																<div class="form-group">
																	<label for="payempno" >職號:</label>
																	<input type="text" id="payempno" name="payempno" class="form-control text-uppercase" value="" maxlength="7" size="7"  />
																</div>														
																<div class="form-group">
																	<label for="paydept" >單位:</label>
																	<select id="paydept"  class="form-control" data-width="fit">
																	</select>
																</div>
																<div class="form-group">
																	<label for="payidflg" >身分證檢查碼註記:</label>
																	<input type="text" id="payidflg" class="form-control" value=""  maxlength="1" size="1"  />															
																</div>											
															</div>									
														</td>
													</tr>
													<tr>
														<td>
															所得開始日期:<input type="text" id="paybegin" name="paybegin" class="form-control form-inline gclsCalTxt required" value=""  maxlength="10" size="10" />
														</td>
														<td>
															所得結束日期:<input type="text" id="payend" class="form-control gclsCalTxt required" value=""  maxlength="10" size="10"  />
														</td>
														<td>
															<div class="form-inline">
																<div class="form-group">
																	<label for="paysereamt">自繳退休金/退撫金:</label>
																	<input type="text" id="paysereamt" class="form-control" value=""  maxlength="7" size="7"  />
																</div>
				 											</div>
														</td>
													</tr>
													
													<tr>
														<td colspan=3>
															<div class="form-inline">
																<span class="text-warning bg-dark">流水號</span>
															                
																<div class="form-group">
																	<label for="payserno" >50:</label>
																	<input type="text" id="payserno" class="form-control" value=""  maxlength="5" size="5"  />
																</div>
																<div class="form-group">
																	<label for="payserno9a" >9A:</label>
																	<input type="text" id="payserno9a" class=" form-control" value=""  maxlength="5" size="5"  />
																</div>
																<div class="form-group">
																	<label for="payserno9b" >9B:</label>
																	<input type="text" id="payserno9b" class=" form-control" value=""  maxlength="5" size="5"  />
																</div>
																<div class="form-group">
																	<label for="payserno91" >91:</label>
																	<input type="text" id="payserno91" class=" form-control" value=""  maxlength="5" size="5"  />
																</div>
																<div class="form-group">
																	<label for="payserno92" >92:</label>
																	<input type="text" id="payserno92" class=" form-control" value=""  maxlength="5" size="5"  />
																</div>
																<div class="form-group">
																	<label for="payserno51" >51:</label>
																	<input type="text" id="payserno51" class=" form-control" value=""  maxlength="5" size="5"  />
																</div>													
															</div>
														</td>
													</tr>
													<tr>
														<td colspan=3>
															<div class="form-inline">
																<div class="form-group">
																	<label for="payaddrs" class="form-inline">戶籍地址:</label>
																	<input type="text" id="payaddrs" class="form-control form-control-sm" value=""  maxlength="60" size="60"  />
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
								<div class="form-inline">
						        <a class="collapsed card-link" data-toggle="collapse" href="#collapseThree">
						        	人工
						      	</a>
						      	</div>
						    </div>
					      	<div id="collapseThree" class="collapse" data-parent="#accordion">
						        <div class="card-body">
						        	<form id="subTAXCASHForm" >
						          	<!-- pa.pataxcash清單 -->
									<!-- 一組開頭 -->
										<table id="TAXCASH" class="table table-sm table-bordered dynamic ">
											<thead>
												<tr class="info">
													<th class="table-title" width="5%">刪除</th>
													<th class="table-title" width="5%">更正</th>
													<th class="table-title">年</th>
													<th class="table-title">月</th>
													<th class="table-title">轉帳項目</th>
													<th class="table-title">格式</th>
													<th class="table-title">應稅所得</th>
													<th class="table-title">已扣稅</th>
													<th class="table-title">傳票製票號</th>
													<th class="table-title">時戳</th>
													<th class="table-title"></th>
												</tr>
											</thead>
											<tbody>
												<tr>
													<td>
														<!-- 刪除 -->
														<button class="btn btn-sm btn-danger delete_row" type="button" style=""><i class="fa fa-minus"></i></button>
														<input class="form-control cashOnly" type="hidden" id="cash_payidno" name="cashKpayidno" value="">													
													</td>
													<td>
														<!-- 更正  -->
														<button class="btn btn-sm btn-warning update_row" type="button" style=""><i class="fa fa-inbox"></i></button>
																										
													</td>
													<td>
														<!-- 年-->
														<select id="cash_payear" name="cashKpayear" class="form-control required cashOnly">
															<c:forEach var="bitem" items="${alTaxYrTbl}" varStatus="loop">
																<option value="${bitem.payear}">${bitem.payear}</option>
															</c:forEach>														
														</select>
													</td>
													<td>
														<!-- 月 -->
														<select id="cash_paymonth" name="cashKpaymonth" class="form-control required cashOnly">
															<option value=""></option>
															<option value="1">1</option>
															<option value="2">2</option>
															<option value="3">3</option>
															<option value="4">4</option>
															<option value="5">5</option>
															<option value="6">6</option>
															<option value="7">7</option>
															<option value="8">8</option>
															<option value="9">9</option>
															<option value="10">10</option>
															<option value="11">11</option>
															<option value="12">12</option>	
														</select>
													</td>
													<td>
														<!-- 轉帳項目-->
														<select id="cash_paycatg" name="cashKpaycatg" class="form-control required selCatg cashOnly" >
														
															<c:forEach var="bitem" items="${alTaxCatgTbl}" varStatus="loop">
																<option value="${bitem.paycatg}">${bitem.paycatg}-${bitem.catgname}</option>
															</c:forEach>
														</select>
													</td>
													<td>
														<!-- 格式-->
														<select id="cash_sumFmt"  class="form-control required cashOnly" id="cash_payformat" name="cashKpayformat"   title="格式">
															<option value=""></option>
										     				<option value="50">50</option>
															<option value="9A">9A</option>
															<option value="9B">9B</option>
															<option value="92">92</option>
															<option value="91">91</option>
															<option value="51">51</option>	
														</select>
														
													</td>
													<td>
														<!-- 應稅收入-->
														<input class="form-control required digits keypay" type="text" style="text-align: right" id="cash_paytaxpay" name="cash_paytaxpay" value="" maxlength="7" size="7">
													</td>
													<td>
														<!-- 已扣稅-->
														<input class="form-control required digits keypay" type="text" style="text-align: right" id="cash_paytaxdeduct" name="cash_paytaxdeduct" value="0" maxlength="7" size="7">
													</td>
													<td>
														<!-- 傳票製票號-->
														<input class="form-control digits cashOnly" type="text"  id="cash_paycitation" name="cashKpaycitation" value="" maxlength="9" size="9">
													</td>
													<td>
														<!-- 時戳-->
														<input class="form-control" type="text" id="payupdtm" name="cash_payupdtm" value=""  size="10">
													</td>
													<td>
														<!-- 存放hidden-->
														<input class="form-control" type="hidden" id="cash_hid" name="cashKhid" value="">
														
													</td>
												</tr>
											</tbody>
											<tfoot>
												<tr>
												<td colspan=6 style="text-align: right">小計</td>
												<td><input class="form-control" type="text" style="text-align: right" id="cash_sumtaxpay" name="cash_sumtaxpay" value="" maxlength="10" size="10"></td>
												<td><input class="form-control" type="text" style="text-align: right" id="cash_sumtaxded" name="cash_sumtaxded" value="" maxlength="10" size="10"></td>
												</tr>
											</tfoot>
										</table>
										<button class="btn btn-sm btn-info add_row" type="button" style="">新增一筆</button>
									</form>
						        </div>
					        </div>
						</div>
						<div class="card">
							<div class="card-header">
						        <a class="collapsed card-link" data-toggle="collapse" href="#collapseTwo">
						        	轉帳
						      	</a>
						    </div>
					      	<div id="collapseTwo" class="collapse" data-parent="#accordion">
						        <div class="card-body">
						          <!-- pa.pataxtran清單 -->
									<!-- 一組開頭 -->
									<table id="TAXTRAN" class="table table-sm table-bordered  ">
										<thead>
											<tr class="info">
												<th class="table-title">年</th>
												<th class="table-title">月</th>
												<th class="table-title">轉帳項目</th>
												<th class="table-title">應稅所得</th>
												<th class="table-title">已扣稅</th>
												<th class="table-title">時戳</th>
											</tr>
										</thead>
										<tbody>
											<tr>
												<td>
													<!-- 年-->
													<input class="form-control" type="text" id="tran_payear" name="tran_payear" value="" maxlength="4" size="4">
												</td>
												<td>
													<!-- 月 -->
													<input class="form-control" type="text" id="tran_paymonth" name="tran_paymonth" value="" maxlength="2" size="2">
												</td>
												<td>
													<!-- 轉帳項目-->
													<input class="form-control" type="text" id="tran_paycatg" name="tran_paycatg" value="" maxlength="2" size="2">
												</td>
												<td>
													<!-- 應稅收入-->
													<input class="form-control" type="text" style="text-align: right" id="tran_paytaxpay" name="tran_paytaxpay" value="" maxlength="7" size="7">
												</td>
												<td>
													<!-- 已扣稅-->
													<input class="form-control" type="text" style="text-align: right" id="tran_paytaxdeduct" name="tran_paytaxdeduct" value="" maxlength="7" size="7">
												</td>
												<td>
													<!-- 時戳-->
													<input class="form-control" type="text" id="tran_payupdtm" name="tran_payupdtm" value="" maxlength="10" size="10">
												</td>
											</tr>
										</tbody>
										<tfoot>
											<TR>
											<td colspan=3 style="text-align: right">小計</td>
											<td><input class="form-control" type="text" style="text-align: right" id="tran_sumtaxpay" name="tran_sumtaxpay" value="" maxlength="10" size="10"></td>
											<td><input class="form-control" type="text" style="text-align: right" id="tran_sumtaxded" name="tran_sumtaxpay" value="" maxlength="10" size="10"></td>
											</TR>
										</tfoot>
									</table>
						        </div>
					        </div>
						</div>						
					</div>
	                
                 <!-- Modal Body end-->  
				</div>
				<!-- Modal footer -->
	        	<div class="modal-footer">
	        	
                </div>
            </div>
       </div>
 	</div>
 	
  	<!--所得稅查看-->
	<div class="tab-pane fade" id="byTaxViewMain" role="tabpanel" aria-labelledby="pills-taxview-tab">
		<div class="container-fluid">
			<div class="row" id="qryTaxViewItem">
				<div class="col-10">
					<div class="form-inline">
		    			<div class="input-group">
		    				<span class="input-group-text">項目</span>
		    				<select id="qry_viewtyp" name="qryViewstyp">
		    					<option value="1">1轉帳</option>
								<option value="2">2人工</option>
								<option value="3">3轉帳+人工</option>		     					
							</select>
						</div>
						<select id="qry_viewYear" class="selectpicker selYY" data-width="fit"  data-live-search="true" multiple title="年">
							<option value=""></option>
							<c:forEach var="bitem" items="${alTaxYrTbl}" varStatus="loop">
								<option value="${bitem.paybank}">${bitem.paybank}</option>
							</c:forEach>
						</select>
						<select id="qry_viewMon" class="selectpicker" data-width="fit"  data-live-search="true" multiple title="月">
							<option value=""></option>
							<option value="1">1</option>
							<option value="2">2</option>
							<option value="3">3</option>
							<option value="4">4</option>
							<option value="5">5</option>
							<option value="6">6</option>
							<option value="7">7</option>
							<option value="8">8</option>
							<option value="9">9</option>
							<option value="10">10</option>
							<option value="11">11</option>
							<option value="12">12</option>	
						</select>
						<select id="qry_viewFmt"  class="selectpicker PAYFORMAT" data-width="fit" data-live-search="true" multiple title="格式">
							<option value=""></option>
		     				<option value="50">50</option>
							<option value="9A">9A</option>
							<option value="9B">9B</option>
							<option value="92">92</option>
							<option value="91">91</option>
							<option value="51">51</option>	
						</select>
						<input type="text" id="qry_viewTickno" size="9" class="form-control text-uppercase" placeholder="傳票製票號">
						<input type="text" id="qry_viewId" size="10" class="form-control text-uppercase" placeholder="身分證">
						<input type="text" id="qry_viewCard" size="4" class="form-control text-uppercase" placeholder="卡號">
						<input type="text" id="qry_viewNamec" size="12" class="form-control " placeholder="姓名" >						
					</div>
				</div>
				<div class="col-2">
					<button id="btn_qryTaxView" class="btn btn-info" type="button" ><i class="fa fa-search"></i>查詢</button>
					<button id="btn_clnTaxView" class="btn btn-warning" type="button"><i class="fa fa-eraser"></i>清空</button>
				</div>
			</div>
			<div class="row">
				<div class="col-12 border border-dark">
					<table id="TblTaxView" class="table table-striped table-bordered table-hover" style="width="100%">
						<thead>
						</thead>
						<tfoot>
				            <tr>
				            	<th colspan=11></th>
				            	<th style="text-align:right"></th>
				                <th style="text-align:right"></th>
				                <th></th>				                
				            </tr>
				        </tfoot>
					</table>
				</div>
			</div>		
	 	</div>
	</div>
  
  	<!--所得稅彙總-->
  	<div class="tab-pane fade" id="byTaxSumMain" role="tabpanel" aria-labelledby="pills-taxsum-tab">
		<div class="container-fluid">
			<div class="row" id="qryTaxSumItem">
				<div class="col-10">
					<div class="form-inline">
						<div class="input-group">
		    				<span class="input-group-text">項目</span>
		    				<select id="qry_sumtyp" name="qryViewstyp">
		    					<option value="1">1轉帳</option>
								<option value="2">2人工</option>
								<option value="3">3轉帳+人工</option>		     					
							</select>
						</div>
		    			<select id="qry_sumYear" class="selectpicker selYY" data-width="fit"  data-live-search="true" multiple title="年">
							<option value=""></option>
							<c:forEach var="bitem" items="${alTaxYrTbl}" varStatus="loop">
								<option value="${bitem.paybank}">${bitem.paybank}</option>
							</c:forEach>
						</select>
						<select id="qry_sumMon" class="selectpicker" data-width="fit"  data-live-search="true" multiple title="月">
							<option value=""></option>
							<option value="1">1</option>
							<option value="2">2</option>
							<option value="3">3</option>
							<option value="4">4</option>
							<option value="5">5</option>
							<option value="6">6</option>
							<option value="7">7</option>
							<option value="8">8</option>
							<option value="9">9</option>
							<option value="10">10</option>
							<option value="11">11</option>
							<option value="12">12</option>	
						</select>
						<select id="qry_sumFmt"  class="selectpicker PAYFORMAT" data-width="fit" data-live-search="true" multiple title="格式">
							<option value=""></option>
		     				<option value="50">50</option>
							<option value="9A">9A</option>
							<option value="9B">9B</option>
							<option value="92">92</option>
							<option value="91">91</option>
							<option value="51">51</option>	
						</select>
						<input type="text" id="qry_sumTickno" size="9" class="form-control text-uppercase" placeholder="傳票製票號">												
					</div>
				</div>
				<div class="col-2">
					<button id="btn_qryTaxSum" class="btn btn-info" type="button" ><i class="fa fa-search"></i>查詢</button>
					<button id="btn_clnTaxSum" class="btn btn-warning" type="button"><i class="fa fa-eraser"></i>清空</button>
				</div>
			</div>
			<div class="row">
				<div class="col-12 border border-dark">
					<table id="TblTaxSum" class="table table-striped table-bordered table-hover" style="width=100%">
						<thead>
						</thead>
						<tfoot>
				            <tr>
				            	<th colspan=4></th>
				            	<th style="text-align:right"></th>
				            	<th style="text-align:right"></th>
				            	<th colspan=2></th>			                
				            </tr> 
				        </tfoot>
					</table>
				</div>
			</div>
		</div>	
	</div>
	<!--所得稅統計-->
  	<div class="tab-pane fade" id="byTaxStatMain" role="tabpanel" aria-labelledby="pills-taxstat-tab">
		<div class="container-fluid">
			<div class="row" id="qryTaxStatItem">
				<div class="col-10">
					<div class="form-inline">
						<select id="qry_StatYear" class="selectpicker selYY" data-width="fit"  data-live-search="true" multiple title="年">
							<option value=""></option>
							<c:forEach var="bitem" items="${alTaxYrTbl}" varStatus="loop">
								<option value="${bitem.paybank}">${bitem.paybank}</option>
							</c:forEach>
						</select>
						<select id="qry_StatMon" class="selectpicker" data-width="fit"  data-live-search="true" multiple title="月">
							<option value=""></option>
							<option value="1">1</option>
							<option value="2">2</option>
							<option value="3">3</option>
							<option value="4">4</option>
							<option value="5">5</option>
							<option value="6">6</option>
							<option value="7">7</option>
							<option value="8">8</option>
							<option value="9">9</option>
							<option value="10">10</option>
							<option value="11">11</option>
							<option value="12">12</option>	
						</select>
						<select id="qry_StatDept" name="qry_StatDept" class="selectpicker" data-width="fit"  data-live-search="true" multiple title="單位">							
						</select>
						<input type="text" id="qry_StatId" size="10" class="form-control text-uppercase" placeholder="身分證">
						<input type="text" id="qry_StatCard" size="4" class="form-control text-uppercase" placeholder="卡號">
						<input type="text" id="qry_StatNamec" size="12" class="form-control " placeholder="姓名" >																	
					</div>
				</div>
				<div class="col-2">
					<button id="btn_qryTaxStat" class="btn btn-info" type="button" ><i class="fa fa-search"></i>查詢</button>
					<button id="btn_clnTaxStat" class="btn btn-warning" type="button"><i class="fa fa-eraser"></i>清空</button>
				</div>
			</div>
			<div class="row">
			<div class="col-md-12">
				<div id="explanStat_1" class="text-danger">1.查詢項[單位]=所得稅主檔.單位;2.清單[職稱]=基本檔.職稱;3.以所得格式50為統計</div>				
			</div>
			</div>
			<div class="row">
				<div class="col-12 border border-dark">
					<table id="TblTaxStat" class="table table-striped table-bordered table-hover" style="width="100%">
						<thead></thead>
						<tfoot>
				            <tr>
				            	<th colspan=7></th>
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

</body>
</html>