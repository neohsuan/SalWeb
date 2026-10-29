<!DOCTYPE HTML>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<html>
<head>
<title>員工績效</title>
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
var codeTbl;
var code_data;
var tracod;
var targetPage = null; // 用來記錄需要強制跳轉的頁碼
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
		//初始化表單驗證
		$("#subsalWkpfForm,#subPABASPSN_BonusForm,#subPATRAPSN_BonusForm").validate();
	});
	
	//tab設定
	$('#pills-tab a').on('click', function (e) {
	  e.preventDefault();
	  $(this).tab('show');
	  tabid=$(this).attr('id');
	  console.log(tabid);
	  //檔案產製
	  if (tabid=="pills-fileProd-tab"){
	  	tmp = "";	
		if (pahid == "2A0"){
			tmp = pahid;
		}
		logtmp="[發薪明細]要有產出,才能產製.<br>檔案放置位置:";
		if(selCode == "1"){	//績效組_醫師
			codeTbl="tblSALPPFList";
			logtmp+="\\\\ap_pd1.vghks.gov.tw\\Apps_Exe\\SALPPF\\DocBas"+tmp+".TXT" ;		  	
	  	}else if (selCode == "2"){	//績效組_員工
	  		codeTbl="tblSALEPFList";
	  		logtmp+="\\\\ap_pd1.vghks.gov.tw\\Apps_Exe\\SALEPF\\EmpEff"+tmp+".txt" ;		  	
		}  	  	
	  	$("#explanProd_1").html(logtmp);
	  }
	  if (tabid=="pills-wkpf-tab"){
		  $("#qryWkpftyp").trigger( "change" );
		  if(selCode == "1"){	//1工作獎金績分
			  codeTbl="tblCspfList";
		  }else if (selCode == "2"){	//2全院績效彙總		  		
		  	  codeTbl="tblPftlList";
		  }
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
		if (pahid == "2A0"){
			tmp = pahid;
		}
		logtmp="[發薪明細]要有產出,才能產製.<br>檔案放置位置:";
		if(selCode == "1"){	//績效組_醫師
			codeTbl="tblSALPPFList";
			logtmp+="\\\\ap_pd1.vghks.gov.tw\\Apps_Exe\\SALPPF\\DocBas"+tmp+".TXT" ;		  	
	  	}else if (selCode == "2"){	//績效組_員工
	  		codeTbl="tblSALEPFList";
	  		logtmp+="\\\\ap_pd1.vghks.gov.tw\\Apps_Exe\\SALEPF\\EmpEff"+tmp+".txt" ;		  	
		}  	  	
	  	$("#explanProd_1").html(logtmp);
	  	$('#'+codeTbl).show();		
		$("#btn_qryCode").trigger( "click" );	
		
	});
	//工作獎金 選單切換
	$("#qryWkpftyp").change(function(){
		$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
		$("#divfield_1").addClass("d-none");	//隱藏
		selCode=$("#qryWkpftyp option:selected").val().split(':')[0];//取得選單編號,存起來
		if (typeof(selCode)=="undefined" || '' == selCode){
			selCode="1";
		}
		tblname=$("#qryWkpftyp option:selected").val().split(':')[1];//取得選單table,存起來	
		if (tblname=="PAPFTL"){			//全院績效彙總 
			codeTbl="tblPftlList";
		}else if (tblname=="PACSPF"){	//工作獎金績分 
			codeTbl="tblCspfList";
			$("#divfield_1").removeClass("d-none");
		}
		
		console.log("codeTbl:"+codeTbl);
		$('#'+codeTbl).show();		
				 			
	});
	//工作獎金Y,show pfkey 工作獎金N show pfdoc
	$("#baspsn_pacsflag").change(function(){
		$("#div_pfkey").addClass("d-none");
		$("#div_pfdoc").addClass("d-none");
		if ($(this).val()=='Y'){
			$("#div_pfkey").removeClass("d-none");
		}	
		else if ($(this).val()=='N'){	
			$("#div_pfdoc").removeClass("d-none");
		}
	});
	//工作獎金Y,show pfkey 工作獎金N show pfdoc--新
	$("#trapsn_pacsflag").change(function(){
		$("#div_trapsn_pfkey").addClass("d-none");
		$("#div_trapsn_pfdoc").addClass("d-none");
		if ($(this).val()=='Y'){
			$("#div_trapsn_pfkey").removeClass("d-none");
		}	
		else if ($(this).val()=='N'){	
			$("#div_trapsn_pfdoc").removeClass("d-none");
		}
	});
	//工作獎金Y,show pfkey 工作獎金N show pfdoc--原
	$("#trapsn_paocsfla").change(function(){
		$("#div_trapsn_pokey").addClass("d-none");
		$("#div_trapsn_podoc").addClass("d-none");
		if ($(this).val()=='Y'){
			$("#div_trapsn_pokey").removeClass("d-none");
		}	
		else if ($(this).val()=='N'){	
			$("#div_trapsn_podoc").removeClass("d-none");
		}
	});
	$.getLoadCodeTbl = function(item,flag){
		//載入訊息欄說明
		tmp = "";	
		if (pahid == "2A0"){
			tmp = pahid;
		}
		logtmp="[發薪明細]要有產出,才能產製.<br>檔案放置位置:";
		logtmp+="\\\\ap_pd1.vghks.gov.tw\\Apps_Exe\\SALPPF\\DocBas"+tmp+".TXT" ;
		$("#explanProd_1").html(logtmp);
				
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
					select = $('#wkpfTitle');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alTitleCodeTbl, function(idx) {
						options = "<option value='" + data.alTitleCodeTbl[idx].paytitlk + "'>" + data.alTitleCodeTbl[idx].patitsub+"-"+data.alTitleCodeTbl[idx].paytitlk+"-"+data.alTitleCodeTbl[idx].paytitlc ;
						select.append(options);
					});
					select.selectpicker('refresh');
				}
				
				//餉單編號(2個月內)
				if (data.alBankDtTbl_2M[0]!=null){
					select = $('#file_SalNum');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alBankDtTbl_2M, function(idx) {
						options = "<option value='" + data.alBankDtTbl_2M[idx].salnum +":"+ data.alBankDtTbl_2M[idx].paybank.dts +"'>" + data.alBankDtTbl_2M[idx].paybank.dts+" "+data.alBankDtTbl_2M[idx].salnum ;
						select.append(options);
					});
					
				}
				if (data.alexpfTbl[0]!=null){
					select = $('#qry_baspsn_expfkey1,#baspsn_payexpfkey1,#baspsn_payexpfkey2,#baspsn_payexpfkey3,#trapsn_payexpfkey1,#trapsn_payexpfkey2,#trapsn_payexpfkey3,#trapsn_paoexpfkey1,#trapsn_paoexpfkey2,#trapsn_paoexpfkey3');
					select.empty();
					$.each(data.alexpfTbl, function(idx) {
						options = "<option value='" + data.alexpfTbl[idx].expfkey + "'>" + data.alexpfTbl[idx].expfname ;
						select.append(options);
					});
					select.selectpicker('refresh');
				}
				//單位
				if (data.alDeptCodeTbl[0]!=null){
					select = $('#qry_baspsn_dept');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alDeptCodeTbl, function(idx) {
						options = "<option value='" + data.alDeptCodeTbl[idx].padepcod + "'>" + data.alDeptCodeTbl[idx].pacstno+"-"+data.alDeptCodeTbl[idx].padepnmc ;
						select.append(options);
					});
					select.selectpicker('refresh');
					
					select = $('#baspsn_paydept,#trapsn_paydept');
					select.empty();
					options = "<option value=''>單位";
					select.append(options);
					$.each(data.alDeptCodeTbl, function(idx) {
						options = "<option value='" + data.alDeptCodeTbl[idx].padepcod + "'>" + data.alDeptCodeTbl[idx].pacstno+"-"+data.alDeptCodeTbl[idx].padepnmc ;
						select.append(options);
					});
				}
				if (data.altracdTbl[0]!=null){
					select = $('#qry_trapsn_patracd');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.altracdTbl, function(idx) {
						options = "<option value='" + data.altracdTbl[idx].tracode + "'>" + data.altracdTbl[idx].tracode+" "+data.altracdTbl[idx].traname ;
						select.append(options);
					});
					select.selectpicker('refresh');
				}
			}  
		});
	};
	/*預先載入代碼檔 */
  	$.getLoadCodeTbl('bonus','Y');
  	 	
	//編輯欄位檢查
  	$.checkFields = function(btn){
  		msg = '';
  		var isChecked=0;
  		if (btn== 'btn_qryFile'){	//檔案產製
  			if ($("#file_cond").val()=='' || typeof($("#file_cond").val())=='undefined' ){
  				msg = '請輸入項目!!';
  			}
  			if ($("#file_SalNum").val()=='' || typeof($("#file_SalNum").val())=='undefined')
  			    msg += '[餉單編號_發薪日]請勿空白!!';
  		}
  		else if (btn== 'btn_qryWkpf'){	//代碼設定_查詢
  			//1工作獎金績分
			if (selCode=='1'){
  				if ($("#wkpfTitle option:selected").val()=='' || typeof($("#wkpfTitle option:selected").val())=='undefined')
  				 	msg += '請輸入[職稱]!!';
			}	
  		}
  		else if (btn== 'btn_qryBasPsn'){	//績效_基本_查詢
  			if($("#qry_baspsn_cardno").val()=='' && $("#qry_baspsn_idtno").val()=='' && $("#qry_baspsn_namec").val()=='' && 
		 	   $("#qry_baspsn_csflag option:selected").val()=='' && $("#qry_baspsn_lvefg option:selected").val()=='' &&  
		 	   $("#qry_baspsn_expfsp option:selected").val()=='' && $("#qry_baspsn_perf  option:selected").val()=='' &&
		 	   ( typeof($("#qry_baspsn_dept option:selected").val())=='undefined' || $("#qry_baspsn_dept option:selected").val()=='' ) && 
		 	   ( typeof($("#expfkey1  option:selected")).val()=='undefined' || $("#expfkey1  option:selected").val()=='') ){
		 		msg = '請輸入條件值!!';
		 	}
  		}else if (btn== 'subPABASPSN_Updbtn'){
 		
		}else if (btn== 'btn_qryTraPsn'){	//績效_異動_查詢
  			if($("#qry_trapsn_cardno").val()=='' && $("#qry_trapsn_idtno").val()=='' && $("#qry_trapsn_namec").val()=='' && 
  				$("#qry_trapsn_tradt").val()=='' && $("#qry_trapsn_perf  option:selected").val()=='' &&
  				($("#qry_trapsn_patracd option:selected").val()=='' || typeof($("#qry_trapsn_patracd option:selected").val())=='undefined') ){
  			 	msg = '請輸入條件值!!';
  	  		}else if( $("#qry_trapsn_cardno").val()=='' && $("#qry_trapsn_idtno").val()=='' && $("#qry_trapsn_namec").val()=='' && 
  	  				$("#qry_trapsn_tradt").val()=='' && $("#qry_trapsn_perf  option:selected").val()!=''  ){
  	  			msg = '只有輸入[職稱],請再補其他條件項!!';
  	  		}else if( $("#qry_trapsn_cardno").val()=='' && $("#qry_trapsn_idtno").val()=='' && $("#qry_trapsn_namec").val()=='' && 
  	  				$("#qry_trapsn_tradt").val()=='' && $("#qry_trapsn_perf  option:selected").val()=='' && 
  	  				$("#qry_trapsn_patracd  option:selected").val()!='' ){
  	  			msg = '只有輸入[異動原因],請再補其他條件項!!';
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
	        data_id  = $(this).attr("id");
	        data_nm  = $(this).attr("name");
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
			    	if ( tblname=="PABASPSN_Bonus" || tblname=="PATRAPSN_Bonus"){
			    		if (typeof data_nm === "undefined" ||data_nm == "undefined")
			    			console.log("undefined"+data_id);
			    		obj[data_nm] = data_val; //NAME+數值
			    	}else{	
	        			obj[data_id] = data_val; //ID+數值
			    	}
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
	};
	
	//輸入代碼,找是否重複 新增才要檢查
	$(".findkey").blur(function(){
		if($(this).attr("readOnly")==undefined || $(this).attr("readOnly")==false){	//未設定readOnly屬性
			var key=$(this).val();
			console.log("key:"+key);
			code_data="";
			if ( key != ""){
				//tblname=$("#qryWkpftyp option:selected").val().split(':')[1];
				if (tblname=="PACSPF"){						
					if ($('#pacskey').val()=="" ){
						return false;
					}else{
			        	code_data={
			        		"key":$('#pacskey').val(),
			        	}	
		        	}
		        }
				else if (tblname=="PAPFTL"){
					if ($('#paypfmon').val()=="" ){
						return false;
					}else{
			        	code_data={
			        		"key":$('#paypfmon').val(),
			        	}	
		        	}
				}
				if (!$("#btn_subTblWkpfIns").prop("disabled")){
					$.ajax({				
							url:"${pageContext.request.contextPath}/salController.do",
							data:{
								method  :"qryCodeList",
								hid     : pahid,
								userid  : paUserid,
								code_data : JSON.stringify(code_data),
								tblname : tblname						
							},
			
						type:"POST",		//type:POST, GET(default:GET)
						dataType:"json",	//dataType: text, xml, html, script, json, jsonp
						contentType: "application/x-www-form-urlencoded;charset=utf-8",  
						async: false,
						cache: false,			
						success:function(data) {
							if (data.codeData!=null &&data.codeData.length>0){
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
	//[實際支領KEY]對應
	$(".calCSPF").change(function(){
		var mrnk2n="";
		var mrank="";
		var mrank3="";
		var mdegr2="";
		var mjt="";
		var mtradt;
		var mcsflag = $(this).val();//工作獎金YN
		var mcsid=$(this).prop('id');
		if (mcsid=="baspsn_pacsflag"){	//基本檔
			mtradt="9999-12-31";
			$("#baspsn_pfkey").val("");
			$("#baspsn_pfbas").val("");
			$("#baspsn_pfdat").val("");
			if (mcsflag=="Y"){
				mrnk2n = $("#baspsn_payrnk2n").val();
				mrank  = $("#baspsn_payrank").val();
				mrank3 = $("#baspsn_payrank3").val();
				mdegr2 = $("#baspsn_paydegr2").val();
				mjt    = $("#baspsn_paytitle").val().split("-")[1];
				$.doCsPf(mjt,mrnk2n,mrank,mdegr2,mrank3,mtradt,'bas');
			}
		}else if (mcsid=="trapsn_pacsflag"){	//異動檔_新
			mtradt=$("#trapsn_patradt").val();
			$("#trapsn_pfkey").val("");
			$("#trapsn_pfbas").val("");
			$("#trapsn_pfdat").val("");
			if (mcsflag=="Y"){
				mrnk2n = $("#trapsn_payrnk2n").val();
				mrank  = $("#trapsn_payrank").val();
				mrank3 = $("#trapsn_payrank3").val();
				mdegr2 = $("#trapsn_paydegr2").val();
				mjt    = $("#trapsn_paytitle").val().split("-")[1];
				$.doCsPf(mjt,mrnk2n,mrank,mdegr2,mrank3,mtradt,'tra_N');
			}
		}else if (mcsid=="trapsn_paocsfla"){	//異動檔_原
			mtradt=$("#trapsn_patradt").val();
			$("#trapsn_pokey").val("");
			$("#trapsn_pobas").val("");
			$("#trapsn_podat").val("");
			if (mcsflag=="Y"){
				mrnk2n = $("#trapsn_paornk2n").val();
				mrank  = $("#trapsn_paorank").val();
				mrank3 = $("#trapsn_paorank3").val();
				mdegr2 = $("#trapsn_paodegr2").val();
				mjt    = $("#trapsn_paotitle").val().split("-")[1];
				$.doCsPf(mjt,mrnk2n,mrank,mdegr2,mrank3,mtradt,'tra_O');
			}
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
					if (item=="bas"){
						$("#baspsn_pfkey").val(data.cspfData.pfkey);
						$("#baspsn_pfbas").val(data.cspfData.pfbas);
						$("#baspsn_pfdat").val(data.cspfData.pfdat);
						if (data.cspfData.pfbas==""){
							$("#baspsn_pfbas").addClass('bg-danger');
							$("#baspsn_pfdat").addClass('bg-danger');
						}else{
							$("#baspsn_pfbas").removeClass('bg-danger');
							$("#baspsn_pfdat").removeClass('bg-danger');
						}	
					}else if (item=="tra_N"){
						$("#trapsn_pfkey").val(data.cspfData.pfkey);
						$("#trapsn_pfbas").val(data.cspfData.pfbas);
						$("#trapsn_pfdat").val(data.cspfData.pfdat);
						if (data.cspfData.pfbas==""){
							$("#trapsn_pfbas").addClass('bg-danger');
							$("#trapsn_pfdat").addClass('bg-danger');
						}else{
							$("#trapsn_pfbas").removeClass('bg-danger');
							$("#trapsn_pfdat").removeClass('bg-danger');
						}
					}else if (item=="tra_O"){
						$("#trapsn_pokey").val(data.cspfData.pfkey);
						$("#trapsn_pobas").val(data.cspfData.pfbas);
						$("#trapsn_podat").val(data.cspfData.pfdat);
						if (data.cspfData.pfbas==""){
							$("#trapsn_pobas").addClass('bg-danger');
							$("#trapsn_podat").addClass('bg-danger');
						}else{
							$("#trapsn_pobas").removeClass('bg-danger');
							$("#trapsn_podat").removeClass('bg-danger');
						}
					}
					
					
				}
			},
			error: function(xhr){	
				alert("搜尋[實際支領KEY]過程中，程式發生錯誤!!!\nerror code:"+ xhr.status);
			}
		});
	};
	//jQuery按紐圖示
	$(".btn").button().click(
		function(event) {event.preventDefault();}
	);
	$(".btn").click(function(){
		var id = $(this).attr('id');
		
		if(id == 'btn_clearWkpf'){
			cleanTable("byWkpfMain");
			$('.selectpicker').selectpicker('val', '');
			$('.selectpicker').selectpicker('refresh');
			$("label.error").hide();
			$(".error").removeClass("error");
			if (codeTbl && $('#' + codeTbl).length > 0) {
				// 2. 檢查該元素是否已經是 DataTables 實例
			    if ($.fn.DataTable.isDataTable('#' + codeTbl)) {
			        $('#' + codeTbl).dataTable().fnClearTable();
			    }
			}	
			return false;
		}
		else if(id == 'btn_clnBasPsn'){		//基本檔_清空
			cleanTable("byBaspfMain");
			$('.selectpicker').selectpicker('val', '');
			$('.selectpicker').selectpicker('refresh');
			$("label.error").hide();
			$(".error").removeClass("error");
			$('#TblBasPsnList' ).dataTable().fnClearTable();
			return false;
		}
		else if(id == 'btn_clnTraPsn'){		//異動檔_清空
			cleanTable("byTrapfMain");
			$('.selectpicker').selectpicker('val', '');
			$('.selectpicker').selectpicker('refresh');
			$("label.error").hide();
			$(".error").removeClass("error");
			$('#TblTraPsnList' ).dataTable().fnClearTable();
			return false;
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
					if(selCode == "1"){	//績效組_醫師
						codeTbl="tblSALPPFList";
				  	}else if (selCode == "2"){	//績效組_員工
				  		codeTbl="tblSALEPFList";
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
						if(selCode == "1"){	//績效組_醫師
 							oTable = $('#' + codeTbl ).dataTable(objCopyA);
 					  	}
						else if (selCode == "2"){	//績效組_員工
					  		oTable = $('#' + codeTbl ).dataTable(objCopyB);
						}
						
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
		else if(id == 'btn_qryWkpf'){		//工作獎金_查詢
			msg = $.checkFields(id);
			if(msg == ''){
				//selCode = $('#qryWkpftyp').val().split(':')[0];
				//tblname=$("#qryWkpftyp option:selected").val().split(':')[1];
				if (selCode=="1"){		//工作獎金績分,強制要有值才能查詢
					txtKEY=$('#wkpfTitle').val();
					code_data={
			        		"key":txtKEY
			        	};
				}else{
					code_data={
			        		"key":""
			        	};
				}
								
				console.log("tblname:"+tblname+" selcode:"+selCode+" codeTbl:"+codeTbl);
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'qryCodeList',
						hid     : pahid,
						userid  : paUserid,
						selCode  :selCode,
						tblname  :tblname,
						code_data  :JSON.stringify(code_data)
					},
					type:"POST",	//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						//更新查詢清單
						$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
						$('#'+codeTbl).show();
						if (selCode=="1"){	//工作獎金績分 						
							oTable = $('#' + codeTbl ).dataTable(objWkpfB);						
						}
						else if (selCode=="2"){	//全院績效彙總 
							oTable = $('#' + codeTbl ).dataTable(objWkpfA);					
						}
						
						oTable.fnClearTable(0);		//清空資料
								
						if (data.codeData!=null && data.codeData.length>0){	
							oTable.fnAddData(data.codeData);//重新給資料
						}
						oTable.fnDraw();
						//解決標題不一致問題
						/* var table = $('#' + codeTbl ).DataTable();
				 		$('#container').css( 'display', 'block' );
						table.columns.adjust().draw(false); */								
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
		else if(id=='btn_subTblWkpfIns'||id=='btn_subTblWkpfUpd'){					//工作獎金 新增一筆 更改
			if ($("#subsalWkpfForm").valid()){
				msg = $.checkFields(id);
				if(msg == ''){
					//將key取消readOnly
					$("#pacskey").prop("readOnly", false) ;
					$("#paypfmon").prop("readOnly", false) ;
					//取得目前的資料
					var jsonString = $.getDataInfo(tblname); 
					if (id =='btn_subTblWkpfUpd')		
						$('#Wkpfitem').val("update");
					else if (id =='btn_subTblWkpfIns')		
						$('#Wkpfitem').val("insert");
						
					oTable = $('#' + codeTbl ).dataTable().api();
					uTable = $('#' + codeTbl ).dataTable();
					info = oTable.page.info();		//記錄目前頁碼
					if (info!=null||typeof(info)!="undefined"){	
						currentPage=info.page;
					}
					//return false;
					$.ajax({
						url:"${pageContext.request.contextPath}/salController.do",
						data:{
							method  : 'updTblCode',
							tabJson : jsonString,
							hid     : pahid,
							userid  : paUserid,
							item    : $('#Wkpfitem').val()
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
								$("#addWkpfDialog").modal("hide");
								uTable.fnClearTable(0);		//清空資料
								if ($("#wkpfTitle").val()=="" && selCode==1){	//工作獎金績分
									tmp=$("#pacskey").val().toUpperCase().substring(0, 2);
									$("#wkpfTitle").val( tmp );
								}
								$("#btn_qryWkpf").trigger( "click" );
								if (currentPage!=null||typeof(currentPage)!="undefined"){	  
									uTable.fnPageChange(currentPage);		//切到原先某碼
								}
							}	
						},
						error: function(xhr){					
							alert("支扣項目"+$('#Wkpfitem').val()+"失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
						}
					});
				}else{
					alert(msg);
					return false;
				}
			}
		}
		else if(id=='btn_addcodeWkpf'){					//工作獎金_新增 啟動dialog
 			if (typeof(selCode)=="undefined" || '' == selCode){
				selCode="1";
			}
			$("#btn_clearWkpf").trigger( "click" );
			$("#btn_subTblWkpfIns").prop("disabled", false);
			$("#btn_subTblWkpfUpd").prop("disabled", true);
			$(".tblEditgrp").hide();
			if (typeof(selCode)=="undefined"||'' == selCode||'1' == selCode){
				$("#model_Wkpf_header_title").html("新增[工作獎金績分]");
				$("#PACSPF").show();
				$("#pacskey").prop("readOnly",false);  //將key.readOnly 取消				
			}else if ('2' == selCode){
				$("#model_Wkpf_header_title").html("新增[全院績效彙總]");
				$("#PAPFTL").show();
				$("#paypfmon").prop("readOnly",false);  //將key.readOnly 取消
 			}
 			//tblname=$("#qryWkpftyp option:selected").val().split(':')[1];
		}
		else if(id=='btn_qryBasPsn'){	//績效_基本檔_查詢
			msg = $.checkFields(id);
			if(msg == ''){
				cardno  = $("#qry_baspsn_cardno").val().toUpperCase();
				idtno   = $("#qry_baspsn_idtno").val().toUpperCase();
				namec   = $("#qry_baspsn_namec").val();
				paydept = $.getSelectVals("qry_baspsn_dept");
				var csflag  =$("#qry_baspsn_csflag option:selected").val();	//工獎YN
				var expfsp  =$("#qry_baspsn_expfsp option:selected").val();	//額外點數
				var expfkey1=$.getSelectVals("qry_baspsn_expfkey1");		//額外代碼
				var lvefg   =$("#qry_baspsn_lvefg option:selected").val();	//Y離職
				var perf    =$("#qry_baspsn_perf option:selected").val();	//1:醫師,2:員工
				
				selCode="bonusBas";
				selNm="績效_基本檔查詢";
				in_data="";
				in_data={
						lvefg	 :lvefg,
						cardno   :cardno,
		        		idtno    :idtno,
		        		namec    :namec,
		        		expfkey1 :expfkey1,
		        		csflag   :csflag,
		        		expfsp   :expfsp,
		        		paydept  :paydept,
		        		perf     :perf,
		        	};
				console.log("績效_基本資料_查詢:in_data:"+JSON.stringify(in_data));
				//return false;
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'handelBonus',
						fun     : selCode,
						in_data : JSON.stringify(in_data),
						hid     : pahid,
						userid  : paUserid
					},
					type:"POST",	    //type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						//更新查詢清單
						//$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
						$('#TblBasPsnList').show();
						oTable = $('#TblBasPsnList').dataTable(objBasPsn);
						
						oTable.fnClearTable(0);		//清空資料
						
						if (data.bonusBasList!=null && data.bonusBasList.length>0){	
							oTable.fnAddData(data.bonusBasList);//重新給資料
						}
						oTable.fnDraw();
					},
					error: function(xhr){					
						alert("績效組_基本檔查詢失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
					}
				});
			}
			else{
				alert(msg);
				return false;
			}
		}
		else if (id == 'subPABASPSN_Updbtn'){	//績效_基本檔更改
			if ( $("#subPABASPSN_BonusForm").valid() ){
				msg = $.checkFields(id);
				if(msg == ''){
					//將key取消readOnly
					$("#baspsn_payidno").attr("readOnly",false);
					tblname="PABASPSN_Bonus";
					var jsonString = $.getDataInfo(tblname); //取得目前的資料
					var pk_data={
			        		payidno : $('#baspsn_payidno').val(),
			        		hid		:$('#baspsn_hid').val()
			        	};
					$('#method').val("update");
					
					uTable = $('#TblBasPsnList').dataTable();
					oTable = $('#TblBasPsnList').dataTable().api();
					info = oTable.page.info();		//記錄目前頁碼
					currentPage=info.page;
					//return false;
					$.ajax({
						url:"${pageContext.request.contextPath}/salController.do",
						data:{
							method	: "handelBonus",
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
								console.log("1:"+currentPage);
								$("#baspsnitemModal").modal("hide");
								 // 將目前頁碼賦值給全域變數，供表格重繪時讀取
						        targetPage = currentPage; 
								
								$("#btn_qryBasPsn").trigger( "click" );
								
								alert(data.msg);
								console.log("2:"+$('#TblBasPsnList').dataTable().api().page.info().page);
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
		else if(id=='btn_qryTraPsn'){	//績效_異動_查詢
			msg = $.checkFields(id);
			if(msg == ''){
				cardno  = $("#qry_trapsn_cardno").val().toUpperCase();
				idtno   = $("#qry_trapsn_idtno").val().toUpperCase();
				namec   = $("#qry_trapsn_namec").val();
				perf    = $("#qry_trapsn_perf option:selected").val();	//1:醫師,2:員工
				tracod  = $.getSelectVals("qry_trapsn_patracd");
				var tradt = $("#qry_trapsn_tradt").val();
				selCode="bonusTra";
				selNm="績效_異動_查詢";
				in_data="";
				in_data={
						lvefg	 :lvefg,
						cardno   :cardno,
		        		idtno    :idtno,
		        		namec    :namec,
		        		perf     :perf,
		        		tradt    :tradt,
		        		tracod   :tracod
		        	};
				console.log("績效_異動_查詢:in_data:"+JSON.stringify(in_data));
				//return false;
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'handelBonus',
						fun     : selCode,
						in_data : JSON.stringify(in_data),
						hid     : pahid,
						userid  : paUserid
					},
					type:"POST",	    //type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						//更新查詢清單
						$('#TblTraPsnList').show();
						oTable = $('#TblTraPsnList').dataTable(objTraPsn);
						
						oTable.fnClearTable(0);		//清空資料
						
						if (data.bonusTraList!=null && data.bonusTraList.length>0){	
							oTable.fnAddData(data.bonusTraList);//重新給資料
						}
						oTable.fnDraw();
					},
					error: function(xhr){					
						alert("績效_異動_查詢失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
					}
				});
			}
			else{
				alert(msg);
				return false;
			}
		}
		else if (id == 'subPATRAPSN_Updbtn'){	//績效_異動檔更改
			if ( $("#subPATRAPSN_BonusForm").valid() ){
				msg = $.checkFields(id);
				if(msg == ''){
					//將key取消readOnly
					$("#trapsn_payidno").attr("readOnly",false);
					$("#trapsn_patradt").attr("readOnly",false);
					$("#trapsn_patracod").attr("readOnly",false);
					tblname="PATRAPSN_Bonus";
					var jsonString = $.getDataInfo(tblname); //取得目前的資料
					var pk_data={
			        		payidno : $('#trapsn_payidno').val(),
			        		patradt : $('#trapsn_patradt').val(),
			        		patracod: $('#trapsn_patracod').val(),
			        		hid		:$('#trapsn_hid').val()
			        	};
					console.log("pk_data:"+JSON.stringify(pk_data));
					$('#method').val("update");
					
					uTable = $('#TblTraPsnList').dataTable();
					oTable = $('#TblTraPsnList').dataTable().api();
					info = oTable.page.info();		//記錄目前頁碼
					currentPage=info.page;
					//return false;
					$.ajax({
						url:"${pageContext.request.contextPath}/salController.do",
						data:{
							method	: "handelBonus",
							fun	    : "updTraPsn",
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
								console.log("1:"+currentPage);
								$("#trapsnitemModal").modal("hide");
								 // 將目前頁碼賦值給全域變數，供表格重繪時讀取
						        targetPage = currentPage; 
								
								$("#btn_qryTraPsn").trigger( "click" );
								
								alert(data.msg);
								console.log("2:"+$('#TblTraPsnList').dataTable().api().page.info().page);
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
	
	//檔案產製_績效組_醫師
 	var objCopyA = $.extend(true, {aaSorting: []}, dataTableConfig);
 	
	objCopyA.aoColumns = [	
					{ "sTitle":"","mData": null},
					{ "sTitle":"發薪日","mData": "paybank"},
					{ "sTitle":"單位","mData": "paydept"},
					{ "sTitle":"單位中文","mData": "padepnmc"},
					{ "sTitle":"卡號","mData": "paycard"},
					{ "sTitle":"姓名","mData": "paynamec"},
					{ "sTitle":"職稱","mData": "paytitle"},
					{ "sTitle":"職稱中文","mData": "paytitlp"},
					{ "sTitle":"職等","mData": "payrnk2n"},
					{ "sTitle":"本功","mData": "payrank3"},
					{ "sTitle":"級別","mData": "paydegr2"},
					{ "sTitle":"現職日","mData": "paysgndt"},
					{ "sTitle":"專1額度","mData": "pfdoc"},
					{ "sTitle":"專2額度","mData": "pfopen"},
					{ "sTitle":"專1補發","mData": "vp019"},
					{ "sTitle":"專2補發","mData": "vp049"},
					{ "sTitle":"專1培訓","mData": "fp440"},
					{ "sTitle":"教職獎勵","mData": "teach_00"},
					{ "sTitle":"教職獎勵_補","mData": "teach_09"},
					{ "sTitle":"論文獎勵第1篇","mData": "thesis_10"},
					{ "sTitle":"論文獎勵第1篇_補","mData": "thesis_19"},
					{ "sTitle":"論文獎勵第2篇","mData": "thesis_20"},
					{ "sTitle":"論文獎勵第2篇_補","mData": "thesis_29"},
					{ "sTitle":"離職日","mData": "payrsgdt"},
					
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
	             }
	});
	/* $('#tblSALPPFList').dataTable(objCopyA); */	
	//檔案產製_績效組_職員
 	var objCopyB = $.extend(true, {aaSorting: []}, dataTableConfig);
 	
	objCopyB.aoColumns = [	
					{ "sTitle":"","mData": null},
					{ "sTitle":"發薪日","mData": "paybank"},
					{ "sTitle":"單位","mData": "paydept"},
					{ "sTitle":"單位中文","mData": "padepnmc"},
					{ "sTitle":"卡號","mData": "paycard"},
					{ "sTitle":"姓名","mData": "paynamec"},
					{ "sTitle":"職稱","mData": "paytitle"},
					{ "sTitle":"職稱中文","mData": "paytitlp"},
					{ "sTitle":"職等","mData": "payrnk2n"},
					{ "sTitle":"本功","mData": "payrank3"},
					{ "sTitle":"級別","mData": "paydegr2"},
					{ "sTitle":"現職日","mData": "paysgndt"},
					{ "sTitle":"基本績分","mData": "pfbas"},
					{ "sTitle":"俸級績分","mData": "pfdat"},
					{ "sTitle":"其他績分","mData": "pfsp"},
					{ "sTitle":"工二","mData": "pfopen"},
					{ "sTitle":"教職獎勵","mData": "teach_00"},
					{ "sTitle":"教職獎勵_補","mData": "teach_09"},
					{ "sTitle":"論文獎勵第1篇","mData": "thesis_10"},
					{ "sTitle":"論文獎勵第1篇_補","mData": "thesis_19"},
					{ "sTitle":"論文獎勵第2篇","mData": "thesis_20"},
					{ "sTitle":"論文獎勵第2篇_補","mData": "thesis_29"},
					{ "sTitle":"工作_住宿津貼","mData": "fp80"},
					{ "sTitle":"獎金差額","mData": "pfdiff"},
					{ "sTitle":"總醫師","mData": "pfcr"},
				 ];
						 						 		
	objCopyB.iDisplayLength=10;
						    
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
	             }
	});
	/* $('#tblSALPPFList').dataTable(objCopyB); */
	//全院績效彙總
	var objWkpfA = $.extend(true, {aaSorting: []}, dataTableConfig);
	objWkpfA.aoColumns = [	
							{ "sTitle":"","mData": null},
							{ "sTitle":"西年元月份"   ,"mData": "paypfmon"},		
							{ "sTitle":"總點數"      ,"mData": "paypfpf"},
							{ "sTitle":"契約人員"     ,"mData": "paypfunt"},		
							{ "sTitle":"公職一般"     ,"mData": "papfamt"},
							{ "sTitle":"聘用住院醫師"  ,"mData": "paypfrev"},
							{ "sTitle":"不分科住院醫師","mData": "paypfd"},
							{ "sTitle":"公職衛生技術"  ,"mData": "paypfe"},
							{ "sTitle":"時戳"        ,"mData": "paypfymd"}
							];
	objWkpfA = $.extend(true,objWkpfA, {
				dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
						't'+
						'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
				buttons: [
				   'excelHtml5' 
				],
				"fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
 						$('td:eq(0)', nRow).html( '<button type="button" class="edit btn btn-primary" data-toggle="modal"><i class="fa fa-edit"></i></button>&emsp;<button type="button" class="delCodebtn btn btn-danger"><i class="fa fa-minus"></i></button>');
				}, 
				"fnDrawCallback": function () {					 
				 	//等datatable draw完成後，補上button 初始化 
				 	//jQuery按鈕圖示，先取消onclick預設的動作
					//刪除	
					$(".delCodebtn")
						.click(function(){
							delCheck = confirm("請確認是否刪除！！\n\n若是，請按【確定】．\n\n若不是，請按【取消】");
							if (delCheck) {		
								oTable = $('#' + codeTbl ).dataTable().api();
						        tr = $(this).parents('tr');
						        rowIndex = oTable.row(tr).index();
						        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data
						        code_data="";
						        if (tblname=="PAPFTL"){			
						        	code_data={
						        		"key":rowData.paypfmon
						        	};
						        }
						   		console.log(JSON.stringify(code_data));    
						        $.ajax({
									url:"${pageContext.request.contextPath}/salController.do",
									data:{
											method  : 'delTblCode',
											hid     : pahid,
											tblname : tblname,
											"code_data" : JSON.stringify(code_data)
									},
									type:"GET",			//type:POST, GET(default:GET)
									dataType:"json",	//dataType: text, xml, html, script, json, jsonp
									contentType: "application/x-www-form-urlencoded;charset=utf-8",  
									success:function(data) {
										oTable = $('#' + codeTbl ).dataTable();
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
							oTable = $('#' + codeTbl ).dataTable().api();
							info = oTable.page.info();		//記錄目前頁碼
					        tr = $(this).parents('tr');
					        rowIndex = oTable.row(tr).index();
					        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data
					        $("#paypfmon").val(rowData.paypfmon);
							$("#paypfpf").val(rowData.paypfpf);
							$("#papfamt").val(rowData.papfamt);
							$("#paypfunt").val(rowData.paypfunt);
							$("#paypfrev").val(rowData.paypfrev);
							$("#paypfd").val(rowData.paypfd);
							$("#paypfe").val(rowData.paypfe);
							$("#paypfymd").val(rowData.paypfymd);
							$("#model_header_title").html("修改[全院績效彙總]");
							$(".tblEditgrp").hide();
							$("#PAPFTL").show();		//把要顯示的table show
							$("#paypfmon").prop("readOnly",true);//將key設成readOnly
							$("label.error").hide();
 							$(".error").removeClass("error");
					        $("#addWkpfDialog").modal("show");
							$("#btn_subTblWkpfIns").prop("disabled", true);
							$("#btn_subTblWkpfUpd").prop("disabled", false);
						});	
				 	}							
		      });
	//工作獎金績分	      
	var objWkpfB = $.extend(true, {aaSorting: []}, dataTableConfig);
	objWkpfB.aoColumns = [	
							{ "sTitle":"",         "mData": null},
							{ "sTitle":"工作獎金代碼","mData": "pacskey"},		
							{ "sTitle":"基本績分",   "mData": "pacsbase"},
							{ "sTitle":"俸級績分",   "mData": "pacsdata"},		
							{ "sTitle":"職稱中文",   "mData": "pacsname"},
							{ "sTitle":"時戳",      "mData": "zpacspf"}
							];
	objWkpfB = $.extend(true,objWkpfB, {
				dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
						't'+
						'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
				buttons: [
				   'excelHtml5' 
				],
				"fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
 						$('td:eq(0)', nRow).html( '<button type="button" class="edit btn btn-primary" data-toggle="modal"><i class="fa fa-edit"></i></button>&emsp;<button type="button" class="delCodebtn btn btn-danger"><i class="fa fa-minus"></i></button>');
				}, 
				"fnDrawCallback": function () {					 
				 	//等datatable draw完成後，補上button 初始化 
				 	//jQuery按鈕圖示，先取消onclick預設的動作
					//刪除	
					$(".delCodebtn")
						.click(function(){
							delCheck = confirm("請確認是否刪除！！\n\n若是，請按【確定】．\n\n若不是，請按【取消】");
							if (delCheck) {		
								oTable = $('#' + codeTbl ).dataTable().api();
						        tr = $(this).parents('tr');
						        rowIndex = oTable.row(tr).index();
						        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data
						        code_data="";
						        if (tblname=="PACSPF"){			
						        	code_data={
						        		"key":rowData.pacskey
						        	};
						        }
						   		console.log(JSON.stringify(code_data));    
						        $.ajax({
									url:"${pageContext.request.contextPath}/salController.do",
									data:{
											method  : 'delTblCode',
											hid     : pahid,
											tblname : tblname,
											"code_data" : JSON.stringify(code_data)
									},
									type:"GET",			//type:POST, GET(default:GET)
									dataType:"json",	//dataType: text, xml, html, script, json, jsonp
									contentType: "application/x-www-form-urlencoded;charset=utf-8",  
									success:function(data) {
										oTable = $('#' + codeTbl ).dataTable();
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
							oTable = $('#' + codeTbl ).dataTable().api();
							info = oTable.page.info();		//記錄目前頁碼
					        tr = $(this).parents('tr');
					        rowIndex = oTable.row(tr).index();
					        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data
					        $("#pacskey").val(rowData.pacskey);
							$("#pacsbase").val(rowData.pacsbase);
							$("#pacsdata").val(rowData.pacsdata);
							$("#pacsname").val(rowData.pacsname);
							$("#zpacspf").val(rowData.zpacspf);
							$("#model_header_title").html("修改[工作獎金績分]");
							$(".tblEditgrp").hide();
							$("#PACSPF").show();		//把要顯示的table show
							$("#pacskey").prop("readOnly",true);//將key設成readOnly
							$("label.error").hide();
 							$(".error").removeClass("error");
					        $("#addWkpfDialog").modal("show");
							$("#btn_subTblWkpfIns").prop("disabled", true);
							$("#btn_subTblWkpfUpd").prop("disabled", false);
						});	
				 	}							
		      });
	//績效_基本
	var objBasPsn = $.extend(true, {aaSorting: []}, dataTableConfig);
	objBasPsn.aoColumns = [	
						{ "sTitle":"","mData": null },
						{ "sTitle":"離職","mData": "paylvefg" },
						{ "sTitle":"卡號","mData": "paycard" },
						{ "sTitle":"職號","mData": "payempno" },
						{ "sTitle":"身分證","mData": "payidno" },
						{ "sTitle":"姓名","mData": "paynamec" },
						{ "sTitle":"單位","mData": "padepnmc" },
						{ "sTitle":"職稱","mData": "paytitlp" },
						{ "sTitle":"階別","mData": "ranknm" },
						{ "sTitle":"職等","mData": "payrnk2n" },
						{ "sTitle":"本功","mData": "rank3nm" },
						{ "sTitle":"級別","mData": "paydegr2" },
						{ "sTitle":"俸點","mData": "paydegre" },
						{ "sTitle":"權理","mData": "payrank2" },
						{ "sTitle":"主管權理","mData": "payrankc" },
						{ "sTitle":"比照","mData": "rnk4nm" },
						{ "sTitle":"新/舊","mData": "contract" },
						{ "sTitle":"公職日","mData": "paygovdt" },
						{ "sTitle":"到院日","mData": "payempdt" },
						{ "sTitle":"現職日","mData": "paysgndt" },
						{ "sTitle":"離職日","mData": "payrsgdt" },
						{ "sTitle":"異動日","mData": "patradt" },
						{ "sTitle":"異動項目","mData": "traname" },
						{ "sTitle":"支領","mData": "pacsflag" },
						{ "sTitle":"基本績分","mData": "pfbas" },
						{ "sTitle":"俸級績分","mData": "pfdat" },
						{ "sTitle":"額外1","mData": "paypfsp1" },
						{ "sTitle":"額外2","mData": "paypfsp2" },
						{ "sTitle":"額外3","mData": "paypfsp3" },
						{ "sTitle":"專勤額度","mData": "paypfdoc" },
						{ "sTitle":"專2/工2","mData": "pareNm" }							
					 ];
	objBasPsn = $.extend(true,objBasPsn, {
				dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
						't'+
						'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
				buttons: [
				   'excelHtml5' 
				],
				"fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
					// 1. 產生最精準的自動序號（iDisplayIndexFull 會自動累加頁碼，從 0 開始，所以 +1）
			        $('td:eq(0)', nRow).html(iDisplayIndexFull + 1);
					//2.
					$('td:eq(4)', nRow).html('<a href = "#baspsnData" class= "baspsnData">'+aData.payidno+'</a>' );
				},
				"fnDrawCallback": function () {
					var api = this.api();
					// 2. 自訂跳頁輸入框（加上判斷避免重複 append）
			        if ($("#changePageA").length === 0) {
			            $("#TblBasPsnList_paginate").append("  到第 <input style='height:28px;line-height:28px;width:40px;' class='margin text-center' id='changePageA' type='text'> 頁  <a class='btn btn-success shiny' style='margin-bottom:5px' href='javascript:void(0);' id='dataTable-btn'>確認</a>");
			            
			            // 使用新版 API 綁定事件，避免重複綁定
			            $('#dataTable-btn').on('click', function(e) {
			                var val = $("#changePageA").val();
			                var redirectpage = (val && val > 0) ? (parseInt(val, 10) - 1) : 0;
							api.page(redirectpage).draw(false); // 使用新版 API 換頁
			            });
			        }
			     	// 3. 【核心修正】檢查是否有 Ajax 傳過來的目標頁碼
			        if (typeof targetPage !== 'undefined' && targetPage !== null) {
					    var tempPage = targetPage;
			            targetPage = null; // 必須在發動 draw 之前完全清空
			            // 使用 setTimeout 錯開重繪時間軸，徹底避免非同步更新衝突與無窮迴圈
			            setTimeout(function() {
			                api.page(tempPage).draw(false);
			            }, 10);
			        }
             	}
	});
	$('#TblBasPsnList').dataTable(objBasPsn);
	//顯示pa.PABASPSN明細
	$('#TblBasPsnList tbody').on('click', '.baspsnData', function () {
		oTable = $('#TblBasPsnList').dataTable().api();
		info = oTable.page.info();		//記錄目前頁碼
		currentPage=info.page;
        var tr = $(this).parents('tr');
        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data
        $.doBaspsnDetail(rowData);        
    });
	//顯示pa.pabaspsn明細
	$.doBaspsnDetail = function(mData){
		$("#baspsn_payidno").val(mData.payidno);
		$("#baspsn_hid").val(mData.hid);
		$("#baspsn_paycard").val(mData.paycard);
		$("#baspsn_payempno").val(mData.payempno);
		$("#baspsn_paynamec").val(mData.paynamec);
		$("#baspsn_paygovdt").val(mData.paygovdt);
		$("#baspsn_payempdt").val(mData.payempdt);
		$("#baspsn_payrsgdt").val(replaceDefaultDate(mData.payrsgdt));
		$("#baspsn_paysgndt").val(mData.paysgndt);
		$("#baspsn_paylvefg").val(mData.paylvefg);
		$("#baspsn_patradt").val(mData.patradt);
 		$("#baspsn_traname").val(mData.traname);
 		$("#baspsn_paydept").val(mData.paydept);
 		$("#baspsn_paytitle").val(mData.patitsub+"-"+mData.paytitle);
 		$("#baspsn_paytitlp").val(mData.paytitlp);
 		$("#baspsn_payrank").val(mData.payrank);
 		$("#baspsn_rankname").val(mData.ranknm);
 		$("#baspsn_payrnk2n").val(mData.payrnk2n);
 		$("#baspsn_payrank3").val(mData.rank3nm);
 		$("#baspsn_paydegr2").val(mData.paydegr2);
 		$("#baspsn_paydegre").val(mData.paydegre);
 		$("#baspsn_contract").val(mData.contract);
 		$("#baspsn_payrank2").val(mData.payrank2);
 		$("#baspsn_payrankc").val(mData.payrankc);
 		$("#baspsn_rnk4name").val(mData.rnk4nm);
 		$("#baspsn_pacsflag").val(mData.pacsflag);
 		$("#baspsn_pfkey").val(mData.pfkey);
 		$("#baspsn_pfbas").val(mData.pfbas);
 		$("#baspsn_pfdat").val(mData.pfdat);
 		$("#baspsn_paypfsp1").val(mData.paypfsp1);
 		$("#baspsn_paypfsp2").val(mData.paypfsp2);
 		$("#baspsn_paypfsp3").val(mData.paypfsp3);
		$('#baspsn_payexpfkey1').selectpicker('val', mData.payexpfkey1);
		$('#baspsn_payexpfkey2').selectpicker('val', mData.payexpfkey2);
		$('#baspsn_payexpfkey3').selectpicker('val', mData.payexpfkey3);
		$("#baspsn_paypfdoc").val(mData.paypfdoc);
		$("#baspsn_paretention").val(mData.paretention);
		
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
 		
		//工作獎金Y,show pfkey 工作獎金N show pfdoc
		$("#div_pfkey").addClass("d-none");
		$("#div_pfdoc").addClass("d-none");
		$(".cspf").addClass("d-none");
		if (mData.pacsflag=='Y'){
			$("#div_pfkey").removeClass("d-none");
			$(".cspf").removeClass("d-none");
		}	
		else if (mData.pacsflag=='N'){	
			$("#div_pfdoc").removeClass("d-none");
		}
			
		$("label.error").hide();
		$(".error").removeClass("error");
        $("#baspsnitemModal").modal("show");
		$("#subPABASPSN_Updbtn").prop("disabled", false);
	};
	//績效_異動
	var objTraPsn = $.extend(true, {aaSorting: []}, dataTableConfig);
	objTraPsn.aoColumns = [	
						{ "sTitle":"","mData": null },
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
						{ "sTitle":"原職稱","mData": "paytitlo" },
						{ "sTitle":"原階別","mData": "oranknm" },
						{ "sTitle":"原職等","mData": "paornk2n" },
						{ "sTitle":"原本功","mData": "orank3nm" },
						{ "sTitle":"原級別","mData": "paodegr2" },
						{ "sTitle":"原俸點","mData": "paodegre" },
						{ "sTitle":"原權理","mData": "paorank2" },
						{ "sTitle":"原主管權理","mData": "paorankc" },
						{ "sTitle":"原比照","mData": "ornk4nm" },
						{ "sTitle":"原支領","mData": "paocsfla" },
						{ "sTitle":"原基本績分","mData": "pobas" },
						{ "sTitle":"原俸級績分","mData": "podat" },
						{ "sTitle":"原額外1","mData": "paopfsp1" },
						{ "sTitle":"原額外2","mData": "paopfsp2" },
						{ "sTitle":"原額外3","mData": "paopfsp3" },
						{ "sTitle":"原專勤額度","mData": "paopfdoc" },
						{ "sTitle":"新職稱","mData": "paytitlp" },
						{ "sTitle":"新階別","mData": "ranknm" },
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
						{ "sTitle":"新專勤額度","mData": "paypfdoc" }						
					 ];						
					 
	objTraPsn = $.extend(true,objTraPsn, {
				dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
						't'+
						'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
				buttons: [
				   'excelHtml5' 
				],
				"fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
					// 1. 產生最精準的自動序號（iDisplayIndexFull 會自動累加頁碼，從 0 開始，所以 +1）
			        $('td:eq(0)', nRow).html(iDisplayIndexFull + 1);
					//2.
					$('td:eq(6)', nRow).html('<a href = "#trapsnData" class= "trapsnData">'+aData.payidno+'</a>' );
				},
				"fnDrawCallback": function () {
					var api = this.api();
					// 2. 自訂跳頁輸入框（加上判斷避免重複 append）
			        if ($("#changePageB").length === 0) {
			            $("#TblTraPsnList_paginate").append("  到第 <input style='height:28px;line-height:28px;width:40px;' class='margin text-center' id='changePageB' type='text'> 頁  <a class='btn btn-success shiny' style='margin-bottom:5px' href='javascript:void(0);' id='dataTableB-btn'>確認</a>");
			            
			            // 使用新版 API 綁定事件，避免重複綁定
			            $('#dataTableB-btn').on('click', function(e) {
			                var val = $("#changePageB").val();
			                var redirectpage = (val && val > 0) ? (parseInt(val, 10) - 1) : 0;
							api.page(redirectpage).draw(false); // 使用新版 API 換頁
			            });
			        }
			     	// 3. 【核心修正】檢查是否有 Ajax 傳過來的目標頁碼
			        if (typeof targetPage !== 'undefined' && targetPage !== null) {
					    var tempPage = targetPage;
			            targetPage = null; // 必須在發動 draw 之前完全清空
			            // 使用 setTimeout 錯開重繪時間軸，徹底避免非同步更新衝突與無窮迴圈
			            setTimeout(function() {
			                api.page(tempPage).draw(false);
			            }, 10);
			        }
             	}
	});
	$('#TblTraPsnList').dataTable(objTraPsn);
	//顯示pa.PATRAPSN明細
	$('#TblTraPsnList tbody').on('click', '.trapsnData', function () {
		oTable = $('#TblTraPsnList').dataTable().api();
		info = oTable.page.info();		//記錄目前頁碼
		currentPage=info.page;
        var tr = $(this).parents('tr');
        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data
        $.doTrapsnDetail(rowData);        
    });
	//顯示pa.patrapsn明細
	$.doTrapsnDetail = function(mData){
		tracod=getInt(mData.patracod);
		
		$("#trapsn_payidno").val(mData.payidno);
		$("#trapsn_hid").val(mData.hid);
		$("#trapsn_paycard").val(mData.paycard);
		$("#trapsn_payempno").val(mData.payempno);
		$("#trapsn_paynamec").val(mData.paynamec);
		$("#trapsn_paygovdt").val(mData.paygovdt);
		$("#trapsn_payempdt").val(mData.payempdt);
		$("#trapsn_payrsgdt").val(replaceDefaultDate(mData.payrsgdt));
		$("#trapsn_paysgndt").val(mData.paysgndt);
		$("#trapsn_paylvefg").val(mData.paylvefg);
		$("#trapsn_patradt").val(mData.patradt);
		$("#trapsn_patracod").val(mData.patracod);
 		$("#trapsn_traname").val(mData.traname);
 		$("#trapsn_paydept").val(mData.paydept);
 		//原
 		$("#trapsn_paotitle").val(mData.paotitsub+"-"+mData.paotitle);
 		$("#trapsn_paytitlo").val(mData.paytitlo);
 		$("#trapsn_paorank").val(mData.paorank);
 		$("#trapsn_oranknm").val(mData.oranknm);
 		$("#trapsn_paornk2n").val(mData.paornk2n);
 		$("#trapsn_orank3nm").val(mData.orank3nm);
 		$("#trapsn_paodegr2").val(mData.paodegr2);
 		$("#trapsn_paodegre").val(mData.paodegre);
 		$("#trapsn_contraco").val(mData.contraco);
 		$("#trapsn_paorank2").val(mData.paorank2);
 		$("#trapsn_paorankc").val(mData.paorankc);
 		$("#trapsn_ornk4nm").val(mData.ornk4nm);
 		$("#trapsn_paocsfla").val(mData.paocsfla);
 		$("#trapsn_pokey").val(mData.pokey);
 		$("#trapsn_pobas").val(mData.pobas);
 		$("#trapsn_podat").val(mData.podat);
 		$("#trapsn_paopfsp1").val(mData.paopfsp1);
 		$("#trapsn_paopfsp2").val(mData.paopfsp2);
 		$("#trapsn_paopfsp3").val(mData.paopfsp3);
		$('#trapsn_paoexpfkey1').selectpicker('val', mData.paoexpfkey1);
		$('#trapsn_paoexpfkey2').selectpicker('val', mData.paoexpfkey2);
		$('#trapsn_paoexpfkey3').selectpicker('val', mData.paoexpfkey3);
		$("#trapsn_paopfdoc").val(mData.paopfdoc);
		
		//新
 		$("#trapsn_paytitle").val(mData.patitsub+"-"+mData.paytitle);
 		$("#trapsn_paytitlp").val(mData.paytitlp);
 		$("#trapsn_payrank").val(mData.payrank);
 		$("#trapsn_ranknm").val(mData.ranknm);
 		$("#trapsn_payrnk2n").val(mData.payrnk2n);
 		$("#trapsn_rank3nm").val(mData.rank3nm);
 		$("#trapsn_paydegr2").val(mData.paydegr2);
 		$("#trapsn_paydegre").val(mData.paydegre);
 		$("#trapsn_payrank2").val(mData.payrank2);
 		$("#trapsn_payrankc").val(mData.payrankc);
 		$("#trapsn_rnk4name").val(mData.rnk4name);
 		 		
 		$("#trapsn_contract").val(mData.contract);
 		
 		$("#trapsn_pacsflag").val(mData.pacsflag);
 		$("#trapsn_pfkey").val(mData.pfkey);
 		$("#trapsn_pfbas").val(mData.pfbas);
 		$("#trapsn_pfdat").val(mData.pfdat);
 		$("#trapsn_paypfsp1").val(mData.paypfsp1);
 		$("#trapsn_paypfsp2").val(mData.paypfsp2);
 		$("#trapsn_paypfsp3").val(mData.paypfsp3);
		$('#trapsn_payexpfkey1').selectpicker('val', mData.payexpfkey1);
		$('#trapsn_payexpfkey2').selectpicker('val', mData.payexpfkey2);
		$('#trapsn_payexpfkey3').selectpicker('val', mData.payexpfkey3);
		$("#trapsn_paypfdoc").val(mData.paypfdoc);
		
		//工作獎金Y,show pfkey 工作獎金N show pfdoc
		$("#div_trapsn_pokey").addClass("d-none");
		$("#div_trapsn_podoc").addClass("d-none");
		$("#div_trapsn_pfkey").addClass("d-none");
		$("#div_trapsn_pfdoc").addClass("d-none");
		//新
		if (mData.pacsflag=='Y'){
			$("#div_trapsn_pfkey").removeClass("d-none");
		}	
		else if (mData.pacsflag=='N'){	
			$("#div_trapsn_pfdoc").removeClass("d-none");
		}
		//原 工作獎金YN
		if (mData.paocsfla=='Y'){
			$("#div_trapsn_pokey").removeClass("d-none");
		}	
		else if (mData.paocsfla=='N'){	
			$("#div_trapsn_podoc").removeClass("d-none");
		}
		
		//契約,不含醫師,才有新舊制(Y/N)
		if (mData.contract!=''){
			$("#div_trapsn_contract").removeClass("d-none");
			$("#div_trapsn_authority").addClass("d-none");
		}	
		else{
			if (mData.paytitle=='CG'){	//契約主治不列,權理	
				$("#div_trapsn_authority").addClass("d-none");
			}	
			else{
				$("#div_trapsn_authority").removeClass("d-none");
			}	
			$("#div_trapsn_contract").addClass("d-none");
		}
		//原
		if (mData.contraco!=''){
			$("#div_trapsn_contraco").removeClass("d-none");
			$("#div_trapsn_authority_o").addClass("d-none");
		}	
		else{
			if (mData.paotitle=='CG'){	//契約主治不列,權理	
				$("#div_trapsn_authority_o").addClass("d-none");
			}	
			else{
				$("#div_trapsn_authority_o").removeClass("d-none");
			}	
			$("#div_trapsn_contraco").addClass("d-none");
		}	
		//額外點數
		if ( getInt(mData.paopfdoc) == 0 )
			$(".cspf_o").removeClass("d-none");
		else
			$(".cspf_o").addClass("d-none");	
		if ( getInt(mData.paypfdoc) == 0 )
			$(".cspf_n").removeClass("d-none");
		else
			$(".cspf_n").addClass("d-none");
		//依異動碼設定disabled
		$(".trapsn_pf").prop("disabled", false);
		$(".trapsn_po").prop("disabled", false);
		if (tracod == 21 || tracod == 21 || tracod==23 || tracod==24){
			$(".trapsn_pf").prop("disabled", true);
		}else{
			$(".trapsn_po").prop("disabled", true);
		}
			
		$("label.error").hide();
		$(".error").removeClass("error");
        $("#trapsnitemModal").modal("show");
		$("#subPATRAPSN_Updbtn").prop("disabled", false);
	};
});

</script>
</head>
<body>

<ul class="nav nav-pills mb-3" id="pills-tab" role="tab">
  <li class="nav-item">
    <a class="nav-link active" id="pills-fileProd-tab" data-toggle="pill" href="#byFileProdMain" role="tab" aria-controls="pills-home" aria-selected="true">檔案產製</a>
  </li>
  <li class="nav-item">
     <a class="nav-link" id="pills-wkpf-tab" data-toggle="pill" href="#byWkpfMain" role="tab" aria-controls="pills-profile" aria-selected="false">代碼設定</a> 
  </li>
  <li class="nav-item">
     <a class="nav-link" id="pills-baspf-tab" data-toggle="pill" href="#byBaspfMain" role="tab" aria-controls="pills-profile" aria-selected="false">績效_基本</a> 
  </li>
  <li class="nav-item">
     <a class="nav-link" id="pills-trapf-tab" data-toggle="pill" href="#byTrapfMain" role="tab" aria-controls="pills-profile" aria-selected="false">績效_異動</a> 
  </li>    
</ul>

<div class="tab-content" id="pills-tabContent">
	<!-- 檔案產製 -->
	<div class="tab-pane fade show active" id="byFileProdMain" role="tabpanel" aria-labelledby="pills-fileProd-tab">
		<div class="container-fluid">
			<div class="row" >
				<div class="col-10">
					<div class="form-inline">
						<div class="input-group">
							<span class="input-group-text">項目</span>	
							<select id="file_cond" class="form-control-sm" title="條件">
								<option value="1:績效組_醫師:Perf_1">1:績效組_醫師</option>
			     				<option value="2:績效組_員工:Perf_2">2:績效組_員工</option>
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
						<table id="tblSALPPFList" class="table table-striped table-bordered table-hover" style="text-align:center;display:none;">
							<thead>						
							</thead>
						</table>
						<table id="tblSALEPFList" class="table table-striped table-bordered table-hover" style="text-align:center;display:none;">
							<thead>						
							</thead>
						</table>
					</div>
				</div>	
			</div>
		</div>		 
	</div>
	<!-- 工作獎金 -->
	<div class="tab-pane fade" id="byWkpfMain" role="tabpanel" aria-labelledby="pills-wkpf-tab">
		<div class="container-fluid">
		 	<div class="row">
				<div class="col-12">
		    		<div class="form-inline">
						<div class="input-group">
							<span class="input-group-text">工作獎金</span>
							<select id="qryWkpftyp" name="qryWkpftyp" class="form-control-sm" title="條件">
								<option value="1:PACSPF">1工作獎金績分</option>
								<option value="2:PAPFTL">2全院績效彙總</option>										     					
							</select>
						</div>
						<div class="input-group" id="divfield_1">
							<select id="wkpfTitle" name="wkpfTitle" class="selectpicker" data-width="fit" data-live-search="true" title="職稱">
								<option value=""></option>
			     				<c:forEach var="uitem" items="${alTitleCodeTbl}" varStatus="loop">
									<option value="${uitem.paytitlk}">${uitem.patitsub}-${uitem.paytitlk}-${uitem.paytitlc}</option>
								</c:forEach>
							</select>
						</div>
						<button id="btn_qryWkpf" class="btn btn-info form-control" type="button" ><i class="fa fa-search"></i>查詢</button>
						<button id="btn_clearWkpf" class="btn btn-warning mr-auto form-control" type="button"><i class="fa fa-eraser"></i>清空</button>
						<button id="btn_addcodeWkpf" class="btn btn-success ml-auto form-control" type="button" data-toggle="modal" data-target="#addWkpfDialog"><i class="fa fa-edit"></i>新增</button>
					</div>
				</div>		
			</div>
			
			<div class="row">
				<div class="col-md-12">
					<div id="codeWkpfDiv" class="codeDiv">
						<table id="tblCspfList" class="table table-striped table-bordered table-hover" style="text-align:center;display:none;">
							<thead><th></th><th></th><th></th><th></th><th></th><th></th></thead>
						</table>
						<table id="tblPftlList" class="table table-striped table-bordered table-hover" style="text-align:center;display:none;">						
							<thead><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th></thead>
						</table>
					</div>
				</div>
			</div>
		</div>
		<!-- 新增dialog -->
		<div class="modal fade" id="addWkpfDialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<form id="subsalWkpfForm" >
				<INPUT type="hidden" id="Wkpfitem" name ="item">
				<div class="modal-dialog modal-lg">
					<!-- Modal content-->
	                <div class="modal-content">
	                	<!-- Modal Header -->
				        <div class="modal-header">
				          <h4 class="modal-title"><span id="model_Wkpf_header_title"></span></h4>
				          <button type="button" class="close" data-dismiss="modal">&times;</button>
				        </div>	
	                    <div class="modal-body">
							<div class="container-fluid">
		   						<div class="row justify-content-md-center">
		                       		<table id="PAPFTL" class="tblEditgrp table table-striped table-bordered table-sm" style="text-align:center;">
										<thead class="table-primary">
											<tr class="div-green">
												<th class="table-title">西年元月份</th>
												<th class="table-title">總點數</th>
												<th class="table-title">契約人員</th>
												<th class="table-title">公職一般</th>
												<th class="table-title">聘用住院醫師</th>										
												<th class="table-title">不分科住院醫師</th>
												<th class="table-title">公職衛生技術</th>										
												<th class="table-title">時戳</th>										
											</tr>
										</thead>
										<tbody>
											<tr>
												<td><input type="text" id="paypfmon" name="paypfmon" class="form-control digits findkey required" value="" size="6" maxlength="6"></input></td>
												<td><input type="text" id="paypfpf" name="paypfpf" class="form-control digits" value="" size="7"  maxlength="7"></input></td>
												<td><input type="text" id="paypfunt" name="paypfunt" class="form-control digits required" value="" size="3"  maxlength="3" ></input></td>
												<td><input type="text" id="papfamt" name="papfamt" class="form-control digits required" value="" size="3"  maxlength="3" ></input></td>
												<td><input type="text" id="paypfrev" name="paypfrev" class="form-control digits required" value="" size="3"  maxlength="3"></input></td>
												<td><input type="text" id="paypfd" name="paypfd" class="form-control digits required" value="" size="3"  maxlength="3"></input></td>
												<td><input type="text" id="paypfe" name="paypfe" class="form-control digits required" value="" size="3"  maxlength="3"></input></td>										
												<td><input type="text" id="paypfymd" name="paypfymd" class="form-control" value="" size="26"  maxlength="26" readOnly></input></td>
											</tr>
										</tbody>							
									</table>
								</div>
								<div class="row justify-content-md-center">
									<table id="PACSPF" class="tblEditgrp table table table-striped table-bordered" style="text-align:center;">
										
										<thead class="table-primary">
											<tr class="div-green">
												<th class="table-title">工作獎金代碼</th>
												<th class="table-title">基本績分</th>
												<th class="table-title">俸級績分</th>
												<th class="table-title">職稱中文</th>
												<th class="table-title">時戳</th>										
											</tr>
										</thead>
										<tbody>
											<tr>
												<td><input type="text" id="pacskey" name="pacskey" class="form-control text-uppercase findkey required" value="" size="7" maxlength="7"></input></td>
												<td><input type="text" id="pacsbase" name="pacsbase" class="form-control number required" value="0" size="4"  maxlength="4"></input></td>
												<td><input type="text" id="pacsdata" name="pacsdata" class="form-control number required" value="0" size="4"  maxlength="4" ></input></td>
												<td><input type="text" id="pacsname" name="pacsname" class="form-control required" value="" size="11"  maxlength="22" ></input></td>
												<td><input type="text" id="zpacspf" name="zpacspf" class="form-control" value="" size="26"  maxlength="26"readOnly></input></td>
											</tr>
										</tbody>
										<tfoot>
											 <tr>
	            								<td colspan="5" style="text-align: left;">
				                					工作獎金代碼=2碼職稱+2碼職等+1碼(1本2功)+2碼級數,例:組員_薦六本1=6906101,契約驗光師=EH00000
	            								</td>
	        								</tr>
										</tfoot>
										
									</table>
								</div>
							</div>
	                    </div>
	                    <!-- Modal footer -->
				        <div class="modal-footer">
	                          <button type="button" class="btn btn-primary" id="btn_subTblWkpfIns"><i class="fa fa-save"></i>確認儲存</button>
	                          <button type="button" class="btn btn-primary" id="btn_subTblWkpfUpd"><i class="fa fa-refresh"></i>更改</button>
	                          <button type="button" class="btn btn-secondary" data-dismiss="modal"><i class="fa fa-undo"></i>返回</button>
	                    </div>
	                </div>
	            </div>
			</form>
		</div>
	</div>
	<!-- 績效_基本 -->
	<div class="tab-pane fade" id="byBaspfMain" role="tabpanel" aria-labelledby="pills-baspf-tab">
		<div class="container-fluid">
			<div class="row" id="qryBasPsnItem">
				<div class="col-10">
					<div class="form-inline">
		    			<div class="input-group">
		    				<div class="filter-item">
		    					<input type="text" id="qry_baspsn_cardno" class="form-control form-control-sm text-uppercase" placeholder="卡號" maxlength="4" size="4">
		    				</div>
		    				<div class="filter-item">
		    					<input type="text" id="qry_baspsn_idtno" class="form-control form-control-sm text-uppercase" placeholder="身分證" maxlength="10" size="10">
		    				</div>
		    				<div class="filter-item">
			    				<input type="text" id="qry_baspsn_namec" class="form-control form-control-sm" placeholder="姓名" maxlength="12" size="12">
		    				</div>
		    				<div class="filter-item">
								<select id="qry_baspsn_lvefg" class="selectpicker" data-width="100%" title="離職" >
									<option value=""></option>
									<option value="N" selected>N</option>
									<option value="Y">Y</option>
								</select>
							</div>
		    				<div class="filter-item filter-item-wide">
								<select id="qry_baspsn_dept" name="qry_baspsn_dept" class="selectpicker" data-width="100%" data-live-search="true" multiple title="單位">
									<option value=""></option>
									<c:forEach var="uitem" items="${alDeptCodeTbl}" varStatus="loop">
										<option value="${uitem.padepcod}">${uitem.pacstno}-${uitem.padepnmc}</option>
									</c:forEach>
								</select>
							</div>
		    				<div class="filter-item">
			    				<select id="qry_baspsn_perf" class="selectpicker" data-width="100%" title="職稱">
									<option value=""></option>
									<option value="1">醫師</option>
									<option value="2">員工</option>
								</select>
		    				</div>
		    				<div class="filter-item">
			    				<select id="qry_baspsn_csflag" class="selectpicker" data-width="100%" title="工作獎金">
									<option value=""></option>
									<option value="Y">Y</option>
									<option value="N">N</option>
								</select>
		    				</div>
							<div class="filter-item">
								<select id="qry_baspsn_expfsp" class="selectpicker" data-width="100%" title="額外點數">
									<option value=""></option>
									<option value="Y">Y</option>
									<option value="N">N</option>
								</select>
							</div>	
							<div class="filter-item ">
								<select id="qry_baspsn_expfkey1" class="selectpicker" data-width="100%" title="額外代碼">
									<c:forEach var="uitem" items="${alexpfTbl}" varStatus="loop">
										<option value="${uitem.expfkey}">${uitem.expfname}</option>
									</c:forEach>
								</select>
							</div>
							<a href="#explan_baspsn" class="btn btn-danger" data-toggle="collapse">注意事項</a>
							<div id="explan_baspsn" class="collapse">
					  			<div id="explan_baspsn" class="text-danger">
						  			查詢說明:<br>
						  			1.請輸入條件值,不能空值.<br>2.重條件組合,單位可多選.<br>3.[離職]=N,列出在職人員,[職稱]=區分醫師及員工.
						  		</div>
						  	</div>
						</div>
					</div>
				</div>
				<div class="col-2">
					<button id="btn_qryBasPsn" class="btn btn-info" type="button" ><i class="fa fa-search"></i>查詢</button>
					<button id="btn_clnBasPsn" class="btn btn-warning" type="button"><i class="fa fa-eraser"></i>清空</button>
				</div>
			</div>
			<div class="row">
				<div class="col-12 border border-dark">
					<!-- pa.pabaspsn清單 -->
					<table id="TblBasPsnList" class="table table-striped table-bordered table-hover" >
						<thead>							
						</thead>
					</table>
				</div>
			</div>	
		</div>
	</div>
	<!-- 績效_異動 -->
	<div class="tab-pane fade" id="byTrapfMain" role="tabpanel" aria-labelledby="pills-trapf-tab">
		<div class="container-fluid">
			<div class="row" id="qryTraPsnItem">
				<div class="col-10">
					<div class="form-inline">
		    			<div class="input-group">
		    				<div class="filter-item">
		    					<input type="text" id="qry_trapsn_cardno" class="form-control form-control-sm text-uppercase" placeholder="卡號" maxlength="4" size="4">
		    				</div>
		    				<div class="filter-item">
		    					<input type="text" id="qry_trapsn_idtno" class="form-control form-control-sm text-uppercase" placeholder="身分證" maxlength="10" size="10">
		    				</div>
		    				<div class="filter-item">
			    				<input type="text" id="qry_trapsn_namec" class="form-control form-control-sm" placeholder="姓名" maxlength="12" size="12">
		    				</div>
		    				<div class="filter-item">
								<input type="text" id="qry_trapsn_tradt" size="10" class="form-control gclsCalTxt" placeholder="異動日" >
							</div>
		    				<div class="filter-item">
			    				<select id="qry_trapsn_perf" class="selectpicker" data-width="100%" title="職稱">
									<option value=""></option>
									<option value="1">醫師</option>
									<option value="2">員工</option>
								</select>
		    				</div>
		    				<div class="filter-item">
			    				<select id="qry_trapsn_patracd" class="selectpicker" data-width="fit" multiple title="異動原因">									
								</select>
		    				</div>
		    				<a href="#explan_trapsn" class="btn btn-danger" data-toggle="collapse">注意事項</a>
							<div id="explan_trapsn" class="collapse">
					  			<div id="explan_trapsn" class="text-danger">
						  			查詢說明:<br>
						  			1.請輸入條件值,不能空值.<br>2.[職稱]=區分醫師及員工.
						  		</div>
						  	</div>
						</div>
					</div>
				</div>
				<div class="col-2">
					<button id="btn_qryTraPsn" class="btn btn-info" type="button" ><i class="fa fa-search"></i>查詢</button>
					<button id="btn_clnTraPsn" class="btn btn-warning" type="button"><i class="fa fa-eraser"></i>清空</button>
				</div>
			</div>
			<div class="row">
				<div class="col-12 border border-dark">
					<!-- pa.patrapsn清單 -->
					<table id="TblTraPsnList" class="table table-striped table-bordered table-hover" >
						<thead>							
						</thead>
					</table>
				</div>
			</div>	
		</div>
	</div>	
</div>
<!-- 績效_基本個人明細 -->
<div class="modal fade"  id="baspsnitemModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog" >
		<!-- Modal content-->
		<div class="modal-content" id="baspsnContent">
			<form id="subPABASPSN_BonusForm">
				<INPUT type="hidden" id ="baspsn_hid"  value="">
				<INPUT type="hidden" id ="method"  value="">
				<div class="modal-header">
					<h5 class="modal-title">基本個人明細</h5>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">×</span></button>
				 </div>
				<div class="modal-body">
					<div class="container-fluid">
						<table id="PABASPSN_Bonus" class="table table-striped table-bordered table-condensed table-responsive table-sm caption-top tblEditgrp" style="text-align:center;">
							<tbody>
			            		<tr>
				            		<td>
					            		<div class="basother-panel">
											<div class="form-row form-row--equal">
												<div class="form-group">
													<label for="baspsn_payidno" >身分證字號</label>
													<input type="text" id="baspsn_payidno" name="payidno" readonly class="form-control" maxlength="10" size="10"  />
												</div>
												<div class="form-group">
													<label for="baspsn_paycard" >卡號</label>
													<input type="text" id="baspsn_paycard" name="paycard" readonly class="form-control" maxlength="4" size="4"  />
												</div>
												<div class="form-group">
													<label for="baspsn_paynamec" >姓名</label>
													<input type="text" id="baspsn_paynamec" name="paynamec" readonly class="form-control" maxlength="12" size="12"  />
												</div>
												<div class="form-group">
													<label for="baspsn_payempno" >職號</label>
													<input type="text" id="baspsn_payempno" name="payempno" readonly class="form-control" maxlength="7" size="7"  />
												</div>
											</div>
										</div>
				            			<div class="basother-panel">
											<div class="form-row form-row--equal">
												<div class="form-group">
													<label for="baspsn_paylvefg" >離職否</label>
													<input type="text" id="baspsn_paylvefg" readonly class="form-control"  maxlength="1" size="1"  />
												</div>
												<div class="form-group">
													<label for="baspsn_paygovdt" >初任公職日</label>
													<input type="text" id="baspsn_paygovdt" readonly class="form-control"  maxlength="10" size="10"  />
												</div>
												<div class="form-group">
													<label for="baspsn_payempdt">到院日</label>
													<input type="text" id="baspsn_payempdt" readonly class="form-control"  maxlength="10" size="10"  />
												</div>
												<div class="form-group">
													<label for="baspsn_paysgndt">現職日</label>
													<input type="text" id="baspsn_paysgndt" readonly class="form-control"  maxlength="10" size="10"  />
												</div>
												<div class="form-group">
													<label for="baspsn_payrsgdt">離職日</label>
													<input type="text" id="baspsn_payrsgdt" readonly class="form-control"  maxlength="10" size="10"  />
												</div>
												
												<div class="form-group">
													<label for="baspsn_paygovdt" >異動日</label>
													<input type="text" id="baspsn_patradt" readonly class="form-control"  maxlength="10" size="10"  />
													<input type="text" id="baspsn_traname" readonly class="form-control"  maxlength="8" size="8"  />
												</div>
											</div>
										</div>
										<div class="basother-panel">
											<div class="form-row form-row--equal">
																								
											</div>
										</div>
										<div class="basother-panel">
											<div class="form-row form-row--equal">				
												<div class="form-group">
													<label for="baspsn_paydept">單位</label>
													<select id="baspsn_paydept" class="form-control" data-width="fit" readonly>
													</select>
												</div>
												<div class="form-group">
													<label for="baspsn_paytitle" >職稱</label>
													<input type="text" id="baspsn_paytitle" readonly class="form-control" maxlength="4" size="4"  />
													<input type="text" id="baspsn_paytitlp" readonly class="form-control w-auto" maxlength="22" size="22"  />
												</div>
											</div>
										</div>
										<div class="basother-panel">
											<div class="form-row form-row--equal">
												<div class="form-group">
													<label for="baspsn_rankname">階別</label>
													<INPUT type="hidden" id ="baspsn_payrank" name="payrank" readonly value="">
													<input type="text" id="baspsn_rankname" readonly  class="form-control" maxlength="8" size="8"  />
												</div>
												<div class="form-group">
													<label for="baspsn_payrnk2n" >職等</label>
													<input type="text" id="baspsn_payrnk2n" readonly class="form-control" maxlength="2" size="2"  />
												</div>
												<div class="form-group">
													<label for="baspsn_payrank3" >本功別</label>
													<input type="text" id="baspsn_payrank3" readonly class="form-control" maxlength="1" size="1"  />
												</div>
												<div class="form-group">
													<label for="baspsn_paydegr2" >級別</label>
													<input type="text" id="baspsn_paydegr2" readonly class="form-control" maxlength="2" size="2"  />
												</div>
												<div class="form-group">
													<label for="baspsn_paydegre" >俸點</label>
													<input type="text" id="baspsn_paydegre" readonly class="form-control" maxlength="2" size="2"  />
												</div>
												<div id="div_contract" class="form-group d-none">
													<label for="baspsn_contract" >新Y/舊N</label>
													<input type="text" id="baspsn_contract" readonly class="form-control" maxlength="1" size="1"  />
												</div>
											</div>
											<div class="form-row form-row--equal div_authority d-none">
												<div class="form-group">
													<label for="baspsn_payrank2" >權理</label>
													<input type="text" id="baspsn_payrank2" readonly class="form-control" maxlength="2" size="2"  />
												</div>
												<div class="form-group">
													<label for="baspsn_payrankc" >主管(權理)</label>
													<input type="text" id="baspsn_payrankc" readonly class="form-control" maxlength="2" size="2"  />
												</div>
												<div class="form-group">
													<label for="baspsn_rnk4name" >適用專業加給表</label>
													<input type="text" id="baspsn_rnk4name" readonly class="form-control" maxlength="15" size="15"  />
												</div>
											</div>
										</div>
										<div class="basother-panel">
											<div class="form-row">
												<div class="form-group">
													<label for="baspsn_pacsflag" class="table-warning">工作獎金</label>
													<select id="baspsn_pacsflag" name="pacsflag" class="form-control calCSPF" >
														<option value="">請選擇</option>
														<option value="Y">Y</option>
								     					<option value="N">N</option>
													</select>
												</div>
												<div class="form-group" id="div_pfkey">	
													<label for="baspsn_pfkey">實際支領KEY</label>
													<input type="text" id="baspsn_pfkey" readonly class="form-control w-auto" maxlength="7" size="7"  />
													<label for="baspsn_pfbas" >基本點數</label>
													<input type="text" id="baspsn_pfbas" readonly class="form-control" maxlength="3" size="3"  />
													<label for="baspsn_pfdat" >職等點數</label>
													<input type="text" id="baspsn_pfdat" readonly class="form-control" maxlength="3" size="3"  />
												</div>													
											</div>
										</div>
										<div class="basother-panel">	
											<div class="form-row">
												<div class="form-group ">
													<label for="baspsn_paypfsp1" class="table-warning">額外點數</label>															
													<select id="baspsn_payexpfkey1"  name="payexpfkey1" class="selectpicker" data-width="fit" title="額外1">																
													</select>
													<input type="text" id="baspsn_paypfsp1" name="paypfsp1" class="form-control number" maxlength="3" size="3"  />
													<select id="baspsn_payexpfkey2"  name="payexpfkey2" class="selectpicker" data-width="fit" title="額外2">																
													</select>
													<input type="text" id="baspsn_paypfsp2" name="paypfsp2" class="form-control number" maxlength="3" size="3"  />
													<select id="baspsn_payexpfkey3"  name="payexpfkey3" class="selectpicker" data-width="fit" title="額外3">																
													</select>
													<input type="text" id="baspsn_paypfsp3" name="paypfsp3" class="form-control number" maxlength="3" size="3"  />														
												</div>
											</div>
										</div>
										<div class="basother-panel">
								  			<div class="form-row">
												<div class="form-group" id="div_pfdoc">
													<label for="baspsn_paypfdoc" class="table-warning">專勤基本額度</label>
													<input type="text" id="baspsn_paypfdoc" name="paypfdoc" class="form-control number" maxlength="8" size="8"  />
													&emsp;
													<label for="baspsn_paretention" class="table-warning">專2/工2</label>
<!-- 													<input type="text" id="baspsn_paretention" name="paretention" class="form-control text-uppercase" maxlength="1" size="1"  /> -->
													<select id="baspsn_paretention"  name="paretention"  title="專2/工2">
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
				            		</td>
			            		</tr>
		            		</tbody>
		            	</table>
		            </div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" id="subPABASPSN_Updbtn">更改</button>
				   	<button type="button" class="btn btn-secondary" data-dismiss="modal">返回</button>
				</div>
			</form>	
		</div>
	</div>
</div>
<!-- 績效_異動個人明細 -->
<div class="modal fade"  id="trapsnitemModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog" >
		<!-- Modal content-->
		<div class="modal-content" id="trapsnContent">
			<form id="subPATRAPSN_BonusForm">
				<INPUT type="hidden" id ="trapsn_hid" value="">
				<INPUT type="hidden" id ="method"  value="">
				<div class="modal-header">
					<h5 class="modal-title">異動個人明細</h5>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">×</span></button>
				 </div>
				<div class="modal-body">
					<div class="container-fluid">
						<table id="PATRAPSN_Bonus" class="table table-striped table-bordered table-condensed table-responsive table-sm caption-top tblEditgrp" style="text-align:center;">
							<tbody>
			            		<tr>
				            		<td>
					            		<div class="basother-panel">
											<div class="form-row form-row--equal">
												<div class="form-group">
													<label for="trapsn_payidno" >身分證字號</label>
													<input type="text" id="trapsn_payidno" name="payidno" readonly class="form-control" maxlength="10" size="10"  />
												</div>
												<div class="form-group">
													<label for="trapsn_paycard" >卡號</label>
													<input type="text" id="trapsn_paycard" name="paycard" readonly class="form-control" maxlength="4" size="4"  />
												</div>
												<div class="form-group">
													<label for="trapsn_paynamec" >姓名</label>
													<input type="text" id="trapsn_paynamec" name="paynamec" readonly class="form-control" maxlength="12" size="12"  />
												</div>
												<div class="form-group">
													<label for="trapsn_payempno" >職號</label>
													<input type="text" id="trapsn_payempno" name="payempno" readonly class="form-control" maxlength="7" size="7"  />
												</div>
												<div class="form-group">
													<label for="trapsn_paylvefg" >離職否</label>
													<input type="text" id="trapsn_paylvefg" readonly class="form-control"  maxlength="1" size="1"  />
												</div>
												<div class="form-group">
													<label for="trapsn_paygovdt" >初任公職日</label>
													<input type="text" id="trapsn_paygovdt" readonly class="form-control"  maxlength="10" size="10"  />
												</div>
												<div class="form-group">
													<label for="trapsn_payempdt">到院日</label>
													<input type="text" id="trapsn_payempdt" readonly class="form-control"  maxlength="10" size="10"  />
												</div>
												<div class="form-group">
													<label for="trapsn_paysgndt">現職日</label>
													<input type="text" id="trapsn_paysgndt" readonly class="form-control"  maxlength="10" size="10"  />
												</div>
												<div class="form-group">
													<label for="trapsn_payrsgdt">離職日</label>
													<input type="text" id="trapsn_payrsgdt" readonly class="form-control"  maxlength="10" size="10"  />
												</div>
												
												<div class="form-group">
													<label for="trapsn_paygovdt" >異動日</label>
													<input type="text" id="trapsn_patradt" name="patradt" readonly class="form-control w-auto"  maxlength="10" size="10"  />
													<input type="text" id="trapsn_patracod" name="patracod" readonly class="form-control w-auto"  maxlength="2" size="2"  />
													<input type="text" id="trapsn_traname" readonly class="form-control w-auto"  maxlength="8" size="8"  />
												</div>
												<div class="form-group">
													<label for="trapsn_paydept">單位</label>
													<select id="trapsn_paydept" class="form-control" data-width="fit" readonly>
													</select>
												</div>
											</div>
										</div>
									</td>
								</tr>
								<tr class="table-info">
									<td>『原』</td>
								</tr>
								<tr>
									<td>	
										<div class="basother-panel">
											<div class="form-row form-row--equal">
												<div class="form-group">
													<label for="trapsn_paotitle" >職稱</label>
													<input type="text" id="trapsn_paotitle" readonly class="form-control" maxlength="4" size="4"  />
													<input type="text" id="trapsn_paytitlo" readonly class="form-control w-auto" maxlength="22" size="22"  />
												</div>
											
												<div class="form-group">
													<label for="trapsn_oranknm">階別</label>
													<INPUT type="hidden" id ="trapsn_paorank" name="payrank" readonly value="">
													<input type="text" id="trapsn_oranknm" readonly  class="form-control w-auto" maxlength="8" size="8"  />
												</div>
												<div class="form-group">
													<label for="trapsn_paornk2n" >職等</label>
													<input type="text" id="trapsn_paornk2n" readonly class="form-control w-auto" maxlength="2" size="2"  />
												</div>
												<div class="form-group">
													<label for="trapsn_orank3nm" >本功別</label>
													<input type="text" id="trapsn_orank3nm" readonly class="form-control w-auto" maxlength="1" size="1"  />
												</div>
												<div class="form-group">
													<label for="trapsn_paodegr2" >級別</label>
													<input type="text" id="trapsn_paodegr2" readonly class="form-control w-auto" maxlength="2" size="2"  />
												</div>
												<div class="form-group">
													<label for="trapsn_paodegre" >俸點</label>
													<input type="text" id="trapsn_paodegre" readonly class="form-control w-auto" maxlength="2" size="2"  />
												</div>
											</div>	
											<div class="form-row form-row--equal div_trapsn_authority_o d-none">	
												<div class="form-group">
													<label for="trapsn_paorank2" >權理</label>
													<input type="text" id="trapsn_paorank2" readonly class="form-control w-auto" maxlength="2" size="2"  />
												</div>
												<div class="form-group">
													<label for="trapsn_paorankc" >主管(權理)</label>
													<input type="text" id="trapsn_paorankc" readonly class="form-control w-auto" maxlength="2" size="2"  />
												</div>
												<div class="form-group">
													<label for="trapsn_ornk4nm" >適用專業加給表</label>
													<input type="text" id="trapsn_ornk4nm" readonly class="form-control w-auto" maxlength="15" size="15"  />
												</div>
												<div id="div_trapsn_contraco" class="form-group d-none">
													<label for="trapsn_contraco" >新Y/舊N</label>
													<input type="text" id="trapsn_contraco" readonly class="form-control w-auto" maxlength="1" size="1"  />
												</div>
											</div>
											<div class="form-row form-row--equal ">
												<div class="form-group">
													<label for="trapsn_paocsfla" class="table-warning">工作獎金</label>
													<select id="trapsn_paocsfla" name="paocsfla" class="form-control calCSPF trapsn_po w-auto" >
														<option value="">請選擇</option>
														<option value="Y">Y</option>
								     					<option value="N">N</option>
													</select>
												</div>
												<div class="form-group " id="div_trapsn_pokey">	
													<label for="trapsn_pokey">實際支領KEY</label>
													<input type="text" id="trapsn_pokey" readonly class="form-control w-auto" maxlength="7" size="7"  />
													<label for="trapsn_pobas" >基本點數</label>
													<input type="text" id="trapsn_pobas" readonly class="form-control" maxlength="3" size="3"  />
													<label for="trapsn_podat" >職等點數</label>
													<input type="text" id="trapsn_podat" readonly class="form-control" maxlength="3" size="3"  />
												</div>													
												<div class="form-group" id="div_trapsn_podoc">
													<label for="trapsn_paopfdoc" class="table-warning">專勤基本額度</label>
													<input type="text" id="trapsn_paopfdoc" name="paopfdoc" class="form-control trapsn_po number w-auto" maxlength="8" size="8"  />													
												</div>
											</div>
											<div class="form-row form-row--equal cspf_o d-none ">	
												<div class="form-group ">
													<label for="trapsn_paypfsp1" class="table-warning">額外點數</label>															
													<select id="trapsn_paoexpfkey1"  name="paoexpfkey1" class="selectpicker trapsn_po" data-width="fit" title="額外1">																
													</select>
													<input type="text" id="trapsn_paopfsp1" name="paopfsp1" class="form-control trapsn_po number" maxlength="3" size="3"  />
													<select id="trapsn_paoexpfkey2"  name="paoexpfkey2" class="selectpicker trapsn_po" data-width="fit" title="額外2">																
													</select>
													<input type="text" id="trapsn_paopfsp2" name="paopfsp2" class="form-control trapsn_po number" maxlength="3" size="3"  />
													<select id="trapsn_paoexpfkey3"  name="paoexpfkey3" class="selectpicker trapsn_po" data-width="fit" title="額外3">																
													</select>
													<input type="text" id="trapsn_paopfsp3" name="paopfsp3" class="form-control trapsn_po number" maxlength="3" size="3"  />														
												</div>
											</div>
										</div>
									</td>
								</tr>
								<tr class="table-info">
									<td>『新』</td>
								</tr>
								<tr>
									<td>		
										<div class="basother-panel">
											<div class="form-row form-row--equal">
												<div class="form-group">
													<label for="trapsn_paytitle" >職稱</label>
													<input type="text" id="trapsn_paytitle" readonly class="form-control" maxlength="4" size="4"  />
													<input type="text" id="trapsn_paytitlp" readonly class="form-control w-auto" maxlength="22" size="22"  />
												</div>
												<div class="form-group">
													<label for="trapsn_ranknm">階別</label>
													<INPUT type="hidden" id ="trapsn_payrank" name="payrank" readonly value="">
													<input type="text" id="trapsn_ranknm" readonly  class="form-control w-auto" maxlength="8" size="8"  />
												</div>
												<div class="form-group">
													<label for="trapsn_payrnk2n" >職等</label>
													<input type="text" id="trapsn_payrnk2n" readonly class="form-control w-auto" maxlength="2" size="2"  />
												</div>
												<div class="form-group">
													<label for="trapsn_rank3nm" >本功別</label>
													<input type="text" id="trapsn_rank3nm" readonly class="form-control w-auto" maxlength="1" size="1"  />
												</div>
												<div class="form-group">
													<label for="trapsn_paydegr2" >級別</label>
													<input type="text" id="trapsn_paydegr2" readonly class="form-control w-auto" maxlength="2" size="2"  />
												</div>
												<div class="form-group">
													<label for="trapsn_paydegre" >俸點</label>
													<input type="text" id="trapsn_paydegre" readonly class="form-control w-auto" maxlength="2" size="2"  />
												</div>
											</div>	
											<div class="form-row form-row--equal div_trapsn_authority d-none">	
												<div class="form-group">
													<label for="trapsn_payrank2" >權理</label>
													<input type="text" id="trapsn_payrank2" readonly class="form-control w-auto" maxlength="2" size="2"  />
												</div>
												<div class="form-group">
													<label for="trapsn_payrankc" >主管(權理)</label>
													<input type="text" id="trapsn_payrankc" readonly class="form-control w-auto" maxlength="2" size="2"  />
												</div>
												<div class="form-group">
													<label for="trapsn_rnk4name" >適用專業加給表</label>
													<input type="text" id="trapsn_rnk4name" readonly class="form-control w-auto" maxlength="15" size="15"  />
												</div>
												<div id="div_trapsn_contract" class="form-group d-none">
													<label for="trapsn_contract" >新Y/舊N</label>
													<input type="text" id="trapsn_contract" readonly class="form-control" maxlength="1" size="1"  />
												</div>
											</div>
										
											<div class="form-row form-row--equal ">
												<div class="form-group">
													<label for="trapsn_pacsflag" class="table-warning">工作獎金</label>
													<select id="trapsn_pacsflag" name="pacsflag" class="form-control calCSPF trapsn_pf w-auto" >
														<option value="">請選擇</option>
														<option value="Y">Y</option>
								     					<option value="N">N</option>
													</select>
												</div>
												<div class="form-group" id="div_trapsn_pfkey">	
													<label for="trapsn_pfkey">實際支領KEY</label>
													<input type="text" id="trapsn_pfkey" readonly class="form-control w-auto" maxlength="7" size="7"  />
													<label for="trapsn_pfbas" >基本點數</label>
													<input type="text" id="trapsn_pfbas" readonly class="form-control" maxlength="3" size="3"  />
													<label for="trapsn_pfdat" >職等點數</label>
													<input type="text" id="trapsn_pfdat" readonly class="form-control" maxlength="3" size="3"  />
												</div>
												<div class="form-group" id="div_trapsn_pfdoc">
													<label for="trapsn_paypfdoc" class="table-warning">專勤基本額度</label>
													<input type="text" id="trapsn_paypfdoc" name="paypfdoc" class="form-control trapsn_pf number w-auto" maxlength="8" size="8"  />													
												</div>													
											</div>
											<div class="form-row form-row--equal cspf_n d-none">
												<div class="form-group ">
													<label for="trapsn_paypfsp1" class="table-warning">額外點數</label>															
													<select id="trapsn_payexpfkey1"  name="payexpfkey1" class="selectpicker trapsn_pf" data-width="fit" title="額外1">																
													</select>
													<input type="text" id="trapsn_paypfsp1" name="paypfsp1" class="form-control trapsn_pf number" maxlength="3" size="3"  />
													<select id="trapsn_payexpfkey2"  name="payexpfkey2" class="selectpicker trapsn_pf" data-width="fit" title="額外2">																
													</select>
													<input type="text" id="trapsn_paypfsp2" name="paypfsp2" class="form-control trapsn_pf number" maxlength="3" size="3"  />
													<select id="trapsn_payexpfkey3"  name="payexpfkey3" class="selectpicker trapsn_pf" data-width="fit" title="額外3">																
													</select>
													<input type="text" id="trapsn_paypfsp3" name="paypfsp3" class="form-control trapsn_pf number" maxlength="3" size="3"  />														
												</div>
											</div>
										</div>	
				            		</td>
			            		</tr>
		            		</tbody>
		            	</table>
		            </div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" id="subPATRAPSN_Updbtn">更改</button>
				   	<button type="button" class="btn btn-secondary" data-dismiss="modal">返回</button>
				</div>
			</form>	
		</div>
	</div>
</div>
</body>
</html>