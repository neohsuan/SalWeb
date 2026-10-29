<!DOCTYPE HTML>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="gov.vghks.sal.Globals"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<% double xRate = Globals.gNHIRate; %>
<% int xBaseMin = Globals.gBasSalMin; %>
<html>
<head>
<title>補充保險費</title>
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

var xRate=<%=xRate%>;/*補充費率*/
var xBaseMin=<%=xBaseMin%>;/*最低工資*/
var msg;
var cardno;
var namec;
var idtno;
var paydt;//給付日期
var payno;//製票號
var panhiyr;//年度
var paymon;//月份
var painsu;//保險別
var payitem;//給付所得類別62獎金 63兼職
var selCode;
var selNm;
var selEn;
var in_data;
var oTable;
var info;
var currentPage;
var tblname;
var jsonString;
var pk_data;
var salnum;
var paybank;
$(function(){
		
// 	$(".selectpicker").selectpicker({
// 		noneSelectedText : '請選擇'//預設顯示內容
//     });

	//tab設定
	$('#pills-tab a').on('click', function (e) {
	  e.preventDefault();
	  $(this).tab('show');
	  tabid=$(this).attr('id');
	  console.log(tabid);
	  //計算匯出
	  if (tabid=="pills-nhiprod-tab"){
	  	var logtmp="log資料夾:\\\\ap_pd1\\Apps_Exe\\SalProd\\" ;
	  	logtmp = logtmp.concat(pahid,"\\log\\");
		logtmp = logtmp.concat(" ", "data資料夾:\\\\ap_pd1\\Apps_Exe\\SalProd\\補充費\\");
		logtmp = logtmp.concat(pahid,"\\data\\");
		$("#explanProd_1").html(logtmp);
	  }
	  
	});
	    	
	$(".gclsCalTxt").datepicker({ 
		changeMonth: true,
      	changeYear: true,
      	dateFormat:"yy-mm-dd"});
      	
	//代碼選單,2026.3.12先把VB版改成WEB版,製票號先暫緩
	$("#prod_cond").change(function(){
		$(".prod_1,.prod_2,.prod_3").addClass("d-none");	//隱藏
		selCode=(typeof($("#prod_cond option:selected").val().split(':')[0]) == "undefined" )? "0":$("#prod_cond option:selected").val().split(':')[0];
  		console.log(selCode);
		if (selCode == 1 || selCode == 2 || selCode == 3 || selCode == 4)
			$(".prod_1").removeClass("d-none");
		else
			$(".prod_2").removeClass("d-none");	
// 		else if (selCode == 5 || selCode == 6 || selCode == 7)
// 			$(".prod_2").removeClass("d-none");
// 		else	 
// 			$(".prod_3").removeClass("d-none");
  	});		
	$.getLoadCodeTbl = function(item,flag){		
		$.ajax({
			url:"${pageContext.request.contextPath}/salController.do",
			data:{
				method  : 'getLoadCodeTbl',
 				item    : item,
 				flag    : flag,
 				hid     : pahid,
 				userid   : paUserid
			},
			type:"POST",			//type:POST, GET(default:GET)
			dataType:"json",	//dataType: text, xml, html, script, json, jsonp
			contentType: "application/x-www-form-urlencoded;charset=utf-8",
			success:function(data) {
				var options ="";
				var select;
				//資料賦值
				//給付日期
				/* if (data.alPayDtTbl[0]!=null){
			 		select = $('#qry_nhical_paydt');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alPayDtTbl, function(idx) {
						options = "<option value='" + data.alPayDtTbl[idx] + "'>" + data.alPayDtTbl[idx] ;
						select.append(options);
					});
					//$('#qry_nhical_paydt').selectpicker('refresh');
				}*/
				//年度
				if (data.alPayYrTbl[0]!=null){
			 		$('.nhiyr').empty();
					$('.nhiyr')
				          .append($('<option>', { value : '' })
				          .text('')); 
					$.each(data.alPayYrTbl, function(idx) {
						$('.nhiyr')
				          .append($('<option>', { value : data.alPayYrTbl[idx] })
				          .text(data.alPayYrTbl[idx]));
					});
					$('#qry_nhical_paydt').selectpicker('refresh');
				} 
				//健保投保級距
				if (data.alDpinTbl[0]!=null){
					$('.gdpin').empty();
					$('.gdpin')
				          .append($('<option>', { value : '' })
				          .text('')); 
					$.each(data.alDpinTbl, function(idx) {   
					     $('.gdpin')
				          .append($('<option>', { value : data.alDpinTbl[idx].dpinhigh })
				          .text(data.alDpinTbl[idx].dpinhigh)); 
					});
				}
				//餉單編號(2個月內)
				if (data.alBankDtTbl_2M[0]!=null){
					select = $('#prod_nhi_SalNum');
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
  	$.getLoadCodeTbl('payNhi','Y');
  		
	//編輯欄位檢查
  	$.checkFields = function(btn){
  		msg = '';
  		if (btn== 'btn_qryNhical'){	//補充保險費_查詢
/*   		typeof($("#qry_nhical_paydt option:selected").val())=='undefined' && typeof($("#qry_nhical_nhiyr option:selected").val())=='undefined' &&*/
   			if ( ($("#qry_nhical_cardno").val()=='' && $("#qry_nhical_idtno").val()=='' && $("#qry_nhical_namec").val()=='') &&
  				 $("#qry_nhical_paydt").val()=='' && 
  				 (typeof($("#qry_nhical_nhiyr option:selected").val())=='undefined' || $("#qry_nhical_nhiyr option:selected").val()=="") && 
  				 (typeof($("#qry_nhical_insu option:selected").val())=='undefined' || $("#qry_nhical_insu option:selected").val()=="") && 
  				 (typeof($("#qry_nhical_payitem option:selected").val())=='undefined' || $("#qry_nhical_payitem option:selected").val()=="") ){
  				msg = '請輸入條件值!!';
  			}	
  			//除了給付日期,其他查詢項,要加年度,	
			else if ( $("#qry_nhical_cardno").val()!='' || $("#qry_nhical_idtno").val()!='' || $("#qry_nhical_namec").val()!='' ||
				      $("#qry_nhical_insu option:selected").val() !='' ||
				      $("#qry_nhical_payitem option:selected").val() !='' )  {
				if ( $("#qry_nhical_paydt").val()=='' && 
				     ( typeof($("#qry_nhical_nhiyr option:selected").val())=='undefined' || $("#qry_nhical_nhiyr option:selected").val()=="" ) ) {
					msg="請設定[年度]";
				}	
			}	 
  		}else if (btn == 'subPANHICAL_Insbtn'||btn == 'subPANHICAL_Updbtn'){	//補充保險費[獎金]_編輯_新增/更改
  		
 		}else if (btn == 'subPANHIDET_Insbtn'||btn == 'subPANHIDET_Updbtn'){	//申報扣費明細_編輯_新增/更改
 			
 		}else if (btn== 'btn_qryNhidet'){	//申報扣費明細_查詢
 			if ( ($("#qry_nhidet_cardno").val()=='' && $("#qry_nhidet_idtno").val()=='' && $("#qry_nhidet_namec").val()=='') &&
  				 $("#qry_nhidet_paydt").val()=='' && 
  				 (typeof($("#qry_nhidet_nhiyr option:selected").val())=='undefined' || $("#qry_nhidet_nhiyr option:selected").val()=="") && 
  				 (typeof($("#qry_nhidet_payitem option:selected").val())=='undefined' || $("#qry_nhidet_payitem option:selected").val()=="") ){
  				msg = '請輸入條件值!!';
  			}	
  			//除了給付日期,其他查詢項,要加年度,	
			else if ( $("#qry_nhidet_cardno").val()!='' || $("#qry_nhidet_idtno").val()!='' || $("#qry_nhidet_namec").val()!='' ||
				      $("#qry_nhidet_payitem option:selected").val() !='' )  {
				if ( $("#qry_nhidet_paydt").val()=='' && 
				     ( typeof($("#qry_nhidet_nhiyr option:selected").val())=='undefined' || $("#qry_nhidet_nhiyr option:selected").val()=="" ) ) {
					msg="請設定[年度]";
				}	
			} 
 		}else if (btn== 'btn_prodNhi'){	//計算/匯出
 			if (typeof($("#prod_cond option:selected").val()) == 'undefined' ){
  				msg+=' 請勾選[項目]!!';  		
			}else{
				selCode=$("#prod_cond option:selected").val().split(':')[0];
				if (selCode == 1 || selCode == 2 || selCode == 3 || selCode == 4){
					if($("#prod_nhi_SalNum").val()=='')
  						msg = '[餉單編號]請勿空白';
  				}
  				else {
   					if($("#prod_nhi_paydt").val()=='')
 						msg = '[給付日期]請勿空白';
   				}
//   				else if (selCode == 8 || selCode == 9 ){
//   					if($("#prod_nhi_payno").val()=='')
//   						msg = '[製票號]請勿空白';  						
//   				}else if (selCode == 5 || selCode == 6 || selCode == 7 || selCode == 10){
//   					if($("#prod_nhi_paydt").val()=='')
//   						msg = '[給付日期]請勿空白';
//   				}	
			}			
 		}else if (btn== 'btn_qryAccu'){	//累積數_查詢
 			if ( ($("#qry_accu_cardno").val()=='' && $("#qry_accu_idtno").val()=='' && $("#qry_accu_namec").val()=='') &&
  				 (typeof($("#qry_accu_nhiyr option:selected").val())=='undefined' || $("#qry_accu_nhiyr option:selected").val()=="") ){
  				msg = '請輸入條件值!!';
  			}	
  			//要加年度,	
			else if ( $("#qry_accu_cardno").val()!='' || $("#qry_accu_idtno").val()!='' || $("#qry_accu_idtno").val()!=''  )  {
				if ( typeof($("#qry_accu_nhiyr option:selected").val())=='undefined' || $("#qry_accu_nhiyr option:selected").val()=="" )  {
					msg="請設定[年度]";
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
		var $th ;	
		var data_nm;
		var data_val;
		
		if (tblname=="PANHICAL" || tblname=="PANHIDET"){//補充保險費[獎金]PANHICAL 申報扣費明細PANHIDET
			
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
	}
	
	//下拉式投保級距 => txt 4倍投保額
	$(".ref").change(function(){
		var inref=$(this).attr('id');
		var inig=inref.substring(0,11)+'ig';
 		var tmp=(typeof($('#'+ inref + ' option:selected').val()) == "undefined" )? "":$('#'+ inref + ' option:selected').text();
 		$('#' + inig).val(tmp);
 		var amt4=0;
 		if (getInt(tmp)>0){	
 			amt4= getInt(tmp) * 4 ;
 		}	
 		$('#nhical_moninsu4').val(amt4); 		
	});
	
	//62獎金--依4倍投保額計算 63兼職,依當次獎金>最低工資,獎金*費率
	//(B)4倍投保額 nhical_moninsu4 (C)單次獎金nhical_bonusamt (D)累積獎金nhical_jackpot (E=D-B)累積逾4倍投保額nhical_mtinsu
	//(F)min(E,C)費基nhical_suplybase (G=F*費率)補充保險費nhical_nhiamt 
 	$(".chgnhi").change(function(){
 		var tmp;
 		var payitem;
 		data_id =$(this).attr('id');
		tblname = data_id.substring(0,data_id.indexOf('_'));
 		//補充保費_獎金
 		if ( tblname =="nhical"){
 			//所得類別 62獎金 63兼職
 			payitem=$("#nhical_payitem option:selected").val();
 			if (payitem == '62'){
	 			//(D)累積獎金nhical_jackpot 抓當年度累加數有放在hidden
	 			if ( getInt( $('#nhical_lastjackpot').val()) > 0){
		 			tmp = getInt( $('#nhical_lastjackpot').val() )  + getInt( $('#nhical_bonusamt').val() ) ;
		 			$('#nhical_jackpot').val( tmp );
	 			}
		 		//(E=D-B)累積逾4倍投保額nhical_mtinsu
		 		tmp = getInt( $('#nhical_jackpot').val() )  - getInt( $('#nhical_moninsu4').val() ) ;
		 		if (tmp < 0) tmp=0;
		 		$('#nhical_mtinsu').val( tmp );
		 		//(F)min(E,C)費基nhical_suplybase
		 		if ( tmp  > getInt( $('#nhical_bonusamt').val() ) )
		 			tmp = getInt( $('#nhical_bonusamt').val() );
		 		$('#nhical_suplybase').val( tmp );
		 		//(G=F*費率)補充保險費nhical_nhiamt	
		 		tmp= Math.round(tmp * xRate);
		 		$('#nhical_nhiamt').val( tmp );
		 	}else if (payitem == '63'){
		 		$('#nhical_dpinig').val( 0 );	
		 		$('#nhical_moninsu4').val( 0 );
		 		$('#nhical_jackpot').val( 0 );
		 		$('#nhical_mtinsu').val( 0 );
		 		$('#nhical_suplybase').val( 0 );
		 		//補充費=(給付金額>最低工資)*費率
		 		tmp = getInt( $('#nhical_bonusamt').val() );
		 		if (tmp > xBaseMin){
		 			tmp= Math.round(tmp * xRate);
		 			$('#nhical_nhiamt').val( tmp );
		 		}else{
		 			$('#nhical_nhiamt').val( 0 );
		 		}
		 	}
 		}else if ( tblname =="nhidet"){
 			payitem=$("#nhidet_payitem option:selected").val();
 			
	 		if (payitem == '62'){
	 			//累積獎金nhidet_jackpot 抓當年度累加數有放在hidden
	 			if ( getInt( $('#nhidet_lastjackpot').val()) > 0){
		 			tmp = getInt( $('#nhidet_lastjackpot').val() )  + getInt( $('#nhidet_payamt').val() ) ;
		 			$('#nhidet_jackpot').val( tmp );
	 			}
		 	}else if (payitem == '63'){
		 		$('#nhidet_dpinig').val( 0 );	
		 		$('#nhidet_jackpot').val( 0 );
		 		//補充費=(給付金額>最低工資)*費率
		 		tmp = getInt( $('#nhidet_payamt').val() );
		 		if (tmp > xBaseMin){
		 			tmp= Math.round(tmp * xRate);
		 			$('#nhidet_nhiamt').val( tmp );
		 		}else{
		 			$('#nhidet_nhiamt').val( 0 );
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
		        
				if (!$("#subPANHICAL_Insbtn").prop("disabled")){
					$.ajax({				
							url:"${pageContext.request.contextPath}/salController.do",
							data:{
								method   : 'handelNhi',
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
								if (tblname == "nhical")
									$.doNhiCalDetail(data.basPsnItem,data.intjack,"ins");
								else if (tblname == "nhidet")
									$.doNhiDetDetail(data.basPsnItem,data.intjack,"ins");
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
	    //清空金額合計數
		$(".itemsum").html("");	    
	}
	
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
	
	//jQuery按紐圖示
	$(".btn").button().click(
		function(event) {event.preventDefault();}
	);
	
	$(".btn").click(function(){
		var id = $(this).attr('id');
		
		if(id == 'btn_clnNhical'){
			cleanTable("byNhiCalMain");
			//清空datatable
		 	$("#TblNhiCalList").dataTable().fnClearTable();
		 	return false;
		}else if(id == 'btn_clnNhidet'){
			cleanTable("byNhiDetMain");
			//清空datatable
		 	$("#TblNhiDetList").dataTable().fnClearTable();
		 	return false;
		}else if(id == 'btn_clnAccu'){
			cleanTable("byNhiAccuMain");
			//清空datatable
		 	$("#TblAccuList").dataTable().fnClearTable();
		 	return false;
		}
		else if(id == 'btn_addNhical'){		//補充保險費[獎金]_新增一筆
			cleanTable("subPANHICALForm");
			cleanTable("byNhiCalMain");
			$("#nhicalItemModal").find('.modal-title').text("新增[補充保險費[獎金]]");
			//key
			$("#nhical_papaydt").attr("readOnly",false);	
			$("#nhical_paysource").attr("readOnly",false);	
			$("#nhical_itemnum").attr("readOnly",false);	
			$("#nhical_payidno").attr("readOnly",false);
			//hid
			$("#nhical_hid").val(pahid);
					
			$("label.error").hide();
			$(".error").removeClass("error");
	        $("#nhicalItemModal").modal("show");
			$("#subPANHICAL_Insbtn").prop("disabled", false);
			$("#subPANHICAL_Updbtn").prop("disabled", true);
		}
		else if(id == 'btn_addNhidet'){		//申報扣費明細_新增一筆
			cleanTable("subPANHIDETForm");
			cleanTable("byNhiDetMain");
			$("#nhidetItemModal").find('.modal-title').text("新增[申報扣費明細]");
			//key
			$("#nhidet_papaydt").attr("readOnly",false);	
			$("#nhidet_payitem").attr("readOnly",false);	
			$("#nhidet_payidno").attr("readOnly",false);
			//hid
			$("#nhidet_hid").val(pahid);
					
			$("label.error").hide();
			$(".error").removeClass("error");
	        $("#nhidetItemModal").modal("show");
			$("#subPANHIDET_Insbtn").prop("disabled", false);
			$("#subPANHIDET_Updbtn").prop("disabled", true);
		}
		else if(id == 'btn_qryNhidet'){		//申報扣費明細_查詢
			msg = $.checkFields(id);
			if(msg == ''){
				cardno=$("#qry_nhidet_cardno").val().toUpperCase();
				idtno=$("#qry_nhidet_idtno").val().toUpperCase();
				namec=$("#qry_nhidet_namec").val();
				payyr=$.getSelectVals("qry_nhidet_nhiyr");
				paymon=$.getSelectVals("qry_nhidet_nhimon");  
				paydt=$("#qry_nhidet_paydt").val();
				payitem=$("#qry_nhidet_payitem option:selected").val();
				selCode="NhiDet";
				selNm="申報扣費明細";
					
				console.log("卡號:"+cardno+" 身分證:"+idtno+" 姓名:"+namec+" 給付日期:"+paydt+" 年度:"+payyr+" 月:"+paymon+" 類別:"+payitem);
				//return false;
				
				in_data={
		        		cardno     :cardno,
		        		idtno      :idtno,
		        		namec      :namec,
		        		payyr      :payyr,
		        		paymon     :paymon,
		        		paydt      :paydt,
		        		payitem    :payitem,
		        		selCode    :selCode,
		        		selNm      :selNm
		        	};
								
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'handelNhi',
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
						$('#TblNHIDetList').show();
						oTable = $('#TblNhiDetList').dataTable(objCopyB);
						
						oTable.fnClearTable(0);		//清空資料
								
						if (data.NHIDetList!=null && data.NHIDetList.length>0){	
							oTable.fnAddData(data.NHIDetList);//重新給資料
						}
						oTable.fnDraw(false);
							
						//給付總額,補充費
						$('#GEINFPayDet').html(data.GEINFPayDet);
						$('#GEINFNHIDet').html(data.GEINFNHIDet);
						$('#LBINFPayDet').html(data.LBINFPayDet);
						$('#LBINFNHIDet').html(data.LBINFNHIDet);
						$('#PartPayDet').html(data.PartPayDet);
						$('#PartNHIDet').html(data.PartNHIDet);	
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
		
		else if(id == 'btn_qryNhical'){		//補充保險費[獎金]_查詢
			msg = $.checkFields(id);
			if(msg == ''){
				cardno=$("#qry_nhical_cardno").val().toUpperCase();
				idtno=$("#qry_nhical_idtno").val().toUpperCase();
				namec=$("#qry_nhical_namec").val();
				/* paydt=$.getSelectVals("qry_nhical_paydt");*/
				payyr=$.getSelectVals("qry_nhical_nhiyr"); 
				paymon=$.getSelectVals("qry_nhical_nhimon"); 
				paydt=$("#qry_nhical_paydt").val();
				painsu=$("#qry_nhical_insu option:selected").val();
				payitem=$("#qry_nhical_payitem option:selected").val();
				selCode="NhiCal";
				selNm="補充保險費[獎金]";
				//保險別=A,值改為空白
				if (painsu=="A") painsu="";
					
				console.log("卡號:"+cardno+" 身分證:"+idtno+" 姓名:"+namec+" 給付日期:"+paydt+" 年度:"+payyr+" 月:"+paymon+" 保險別:"+painsu+" 類別:"+payitem);
				//return false;
				
				in_data={
		        		cardno     :cardno,
		        		idtno      :idtno,
		        		namec      :namec,
		        		payyr      :payyr,
		        		paymon     :paymon,
		        		paydt      :paydt,
		        		painsu     :painsu,
		        		payitem    :payitem,
		        		selCode    :selCode,
		        		selNm      :selNm
		        	};
								
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'handelNhi',
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
						$('#TblNHICalList').show();
						oTable = $('#TblNhiCalList').dataTable(objCopyA);
						
						oTable.fnClearTable(0);		//清空資料
								
						if (data.NHICalList!=null && data.NHICalList.length>0){	
							oTable.fnAddData(data.NHICalList);//重新給資料
						}
						oTable.fnDraw(false);
							
						//給付總額,補充費
						$('#GEINFPayAmt').html(data.GEINFPayAmt);
						$('#GEINFNHIAmt').html(data.GEINFNHIAmt);
						$('#LBINFPayAmt').html(data.LBINFPayAmt);
						$('#LBINFNHIAmt').html(data.LBINFNHIAmt);
						$('#PartPayAmt').html(data.PartPayAmt);
						$('#PartNHIAmt').html(data.PartNHIAmt);	
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
		else if(id == 'subPANHICAL_Updbtn'||id == 'subPANHICAL_Insbtn'){	//補充保險費[獎金] 更改 新增
			if ($("#subPANHICALForm").valid()){
				msg = $.checkFields(id);
				if(msg == ''){
					//將key取消readOnly
					$("#nhical_papaydt").attr("readOnly",false);	
					$("#nhical_paysource").attr("readOnly",false);	
					$("#nhical_itemnum").attr("readOnly",false);	
					$("#nhical_payidno").attr("readOnly",false);
					$("#nhical_hid").attr("readOnly",false);
					
					tblname=id.substring(3,id.indexOf('_'));
					console.log("tblname:"+tblname);
					jsonString = $.getDataInfo(tblname); //取得目前的資料
					
					pk_data={
		        		papaydt   : $('#nhical_papaydt').val(),
		        		paysource : $('#nhical_paysource').val(),
		        		itemnum   : $('#nhical_itemnum').val(),
		        		payidno   : $('#nhical_payidno').val(),
		        		hid		  : $('#nhical_hid').val()
		        	};
					
					
					if (id.substring(id.indexOf('_')+1) =='Updbtn')		
						$('#nhicalitem').val("update");
					else if (id.substring(id.indexOf('_')+1) =='Insbtn')		
						$('#nhicalitem').val("insert");
					oTable = $('#TblNhiCalList').dataTable().api();
					uTable = $('#TblNhiCalList').dataTable();
					info = oTable.page.info();		//記錄目前頁碼
					currentPage=info.page;
					//return false;
					
					$.ajax({
						url:"${pageContext.request.contextPath}/salController.do",
						data:{
							method   : 'handelNhi',
							fun      : 'updNhiCal',
							selNm    : '補充保險費[獎金] ',
							editMode : $('#nhicalitem').val(),
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
								$("#nhicalItemModal").modal("hide");
									
								uTable.fnClearTable(0);		//清空資料
								//新增時,查詢項目空白,要塞新增的身分證
								if (id == 'subPANHICAL_Insbtn'){
									
									$("#qry_nhical_idtno").val($("#nhical_payidno").val());
									if ($("#nhical_papaydt").val() !=""){
										$("#qry_nhical_nhiyr").selectpicker('val',$("#nhical_papaydt").val().substring(0,4));
									}
									
									console.log($("#qry_nhical_idtno").val());
								}
								$("#btn_qryNhical").trigger( "click" );

// 								uTable.fnDraw(false);
								uTable.fnPageChange(currentPage,true);		//切到原先某碼<==失效,待找
								
								console.log("2:"+$('#TblNhiCalList').dataTable().api().page.info().page);  
								
								if (data.msg!=null){
									alert(data.msg);
								}
									
							}
						},
						error: function(xhr){					
							alert("補充保險費[獎金] 更改 新增 失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
						}
					});	
				}	
				else{
					alert(msg);
					return false;
				}
			}
		}
		else if(id == 'subPANHIDET_Updbtn'||id == 'subPANHIDET_Insbtn'){	//申報扣費明細 更改 新增
			if ($("#subPANHIDETForm").valid()){
				msg = $.checkFields(id);
				if(msg == ''){
					//將key取消readOnly
					$("#nhidet_papaydt").attr("readOnly",false);	
					$("#nhidet_payitme").attr("readOnly",false);	
					$("#nhidet_hid").attr("readOnly",false);	
					$("#nhidet_payidno").attr("readOnly",false);
					$("#nhidet_serial").attr("readOnly",false);
					
					tblname=id.substring(3,id.indexOf('_'));
					console.log("tblname:"+tblname);
					jsonString = $.getDataInfo(tblname); //取得目前的資料
					
					pk_data={
		        		papaydt   : $('#nhidet_papaydt').val(),
		        		payitem   : $('#nhidet_payitem').val(),
		        		payidno   : $('#nhidet_payidno').val(),
		        		hid		  : $('#nhidet_hid').val()
		        	};
		        	
					if (id.substring(id.indexOf('_')+1) =='Updbtn')		
						$('#nhidetitem').val("update");
					else if (id.substring(id.indexOf('_')+1) =='Insbtn')		
						$('#nhidetitem').val("insert");
					oTable = $('#TblNhiDetList').dataTable().api();
					uTable = $('#TblNhiDetList').dataTable();
					info = oTable.page.info();		//記錄目前頁碼
					currentPage=info.page;
					//return false;
					
					$.ajax({
						url:"${pageContext.request.contextPath}/salController.do",
						data:{
							method   : 'handelNhi',
							fun      : 'updNhiDet',
							selNm    : '申報扣費明細 ',
							editMode : $('#nhidetitem').val(),
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
								console.log("1:currentPage>>"+currentPage);
								$("#nhidetItemModal").modal("hide");
									
								uTable.fnClearTable(0);		//清空資料
								//新增時,查詢項目空白,要塞新增的身分證
								if (id == 'subPANHIDET_Insbtn'){
									
									$("#qry_nhidet_idtno").val($("#nhidet_payidno").val());
									if ($("#nhidet_papaydt").val() !=""){
										$("#qry_nhidet_nhiyr").selectpicker('val',$("#nhidet_papaydt").val().substring(0,4));
									}
									console.log($("#qry_nhidet_idtno").val());
								}
								$("#btn_qryNhidet").trigger( "click" );
								uTable.fnDraw(false);
								uTable.fnPageChange(currentPage,true);		//切到原先某碼<==失效,待找
								
								console.log("2reloadPage:"+$('#TblNhiDetList').dataTable().api().page.info().page);  
								
								if (data.msg!=null){
									alert(data.msg);
								}
							}
						},
						error: function(xhr){					
							alert("申報扣費明細 更改 新增 失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
						}
					});	
				}	
				else{
					alert(msg);
					return false;
				}
			}
		}
		else if(id == 'btn_prodNhi'){	//計算/匯出--執行
			msg = $.checkFields(id);
			if(msg == ''){
				selCode=$("#prod_cond option:selected").val().split(':')[0];
				selNm  =$("#prod_cond option:selected").val().split(':')[1];
				selEn  =$("#prod_cond option:selected").val().split(':')[2];
				salnum =$("#prod_nhi_SalNum").val().split(':')[0];
				paybank =$("#prod_nhi_SalNum").val().split(':')[1];
				paydt =$("#prod_nhi_paydt").val();
				payno =$("#prod_nhi_payno").val();
				in_data="";
				in_data={
						selNm   : selNm,
						selEn   : selEn,
						salnum  : salnum,
						paybank : paybank,
						paydt   : paydt,
						payno   : payno,
						hid     : pahid,
						userid  : paUserid
					   };
				console.log("[產生]計算/匯出_執行  selCode:"+selCode+" in_data:"+JSON.stringify(in_data) );					   
				//return false;
				$.ajax({
						url:"${pageContext.request.contextPath}/salController.do",
						data:{
							method   : 'handelNhi',
							fun      : 'prodNhi',
							selNm    : selNm,
							in_data  : JSON.stringify(in_data),
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
								if (data.msg!=null){
									alert(data.msg);
								}				
							}
						},
						error: function(xhr){					
							alert("[產生]計算/匯出_執行["+selNm+"] 失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
						}
					});	
			}	
			else{
				alert(msg);
				return false;
			}
		}
		else if(id == 'btn_qryAccu'){		//累積數_查詢
			msg = $.checkFields(id);
			if(msg == ''){
				cardno=$("#qry_accu_cardno").val().toUpperCase();
				idtno=$("#qry_accu_idtno").val().toUpperCase();
				namec=$("#qry_accu_namec").val();
				payyr=$.getSelectVals("qry_accu_nhiyr"); 
				selCode="NhiAccu";
				selNm="補充保險費[累積數]";
						
				console.log("卡號:"+cardno+" 身分證:"+idtno+" 姓名:"+namec+" 年度:"+payyr);
				//return false;
				
				in_data={
		        		cardno     :cardno,
		        		idtno      :idtno,
		        		namec      :namec,
		        		payyr      :payyr,
		        		selCode    :selCode,
		        		selNm      :selNm
		        	};
								
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method  : 'handelNhi',
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
						$('#TblAccuList').show();
						oTable = $('#TblAccuList').dataTable(objCopyC);
						
						oTable.fnClearTable(0);		//清空資料
								
						if (data.NHIAccuList!=null && data.NHIAccuList.length>0){	
							oTable.fnAddData(data.NHIAccuList);//重新給資料
						}
						oTable.fnDraw(false);
						
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
	 //補充保險費[獎金]
 	var objCopyA = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyA.aoColumns = [	
							{ "sTitle":"","mData": null },
							{ "sTitle":"給付日期",	"mData": "papaydt" },
							{ "sTitle":"來源",		"mData": "paysource" },
							{ "sTitle":"餉單編號",	"mData": "itemnum" },
							{ "sTitle":"身份證字號",	"mData": "payidno" },
							{ "sTitle":"類別",		"mData": "payitem" },
							{ "sTitle":"卡號",		"mData": "paycard" },
							{ "sTitle":"姓名",		"mData": "paynamec" },
							{ "sTitle":"投保額",		"mData": "moninsu" },								
							{ "sTitle":"4投保額",	"mData": "moninsu4" },	
							{ "sTitle":"單次獎金",	"mData": "bonusamt" },
							{ "sTitle":"累積獎金",	"mData": "jackpot" },
							{ "sTitle":"逾4倍",		"mData": "mtinsu" },
							{ "sTitle":"費基",		"mData": "suplybase" },
							{ "sTitle":"補充費",		"mData": "nhiamt" },
							{ "sTitle":"保險",		"mData": "insunm" },
							{ "sTitle":"時戳",		"mData": "payupdat" },
							{ "sTitle":"職號",		"mData": "payempno" }												
						 ];
	objCopyA.iDisplayLength=10;
	
	objCopyA = $.extend(true,objCopyA, {
				  dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
							't'+
						'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
					buttons: [
					   'excelHtml5'/*  , {extend: 'csv',text: 'CSV',bom: true} */
					],
				"fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
				 	$('td:eq(6)', nRow).html('<a href = "#nhicalData" class= "nhicalData">'+aData.paycard+'</a>' );
				 	$('td:eq(0)', nRow).html( '<button type="button" class="delPANHICALbtn btn btn-danger"><i class="fa fa-minus"></i></button>');
				 },	
	  			 "fnDrawCallback": function () {
	  			 		//第1欄序列
						/* var api = this.api();
						var startIndex=0;
                    	api.column(0).nodes().each(function(cell, i) {
                        	cell.innerHTML = startIndex + i + 1;
                    	}); */
                    	//到某頁
					 	$("#TblNhiCalList_paginate").append("  到第 <input style='height:28px;line-height:28px;width:40px;' class='margin text-center' id='changePageA' type='text'> 頁  <a class='btn btn-success shiny' style='margin-bottom:5px' href='javascript:void(0);' id='dataTable-btn'>確認</a>");
					 	oTable = $("#TblNhiCalList").dataTable();
					 	$('#dataTable-btn').click(function(e) {
					 		if($("#changePageA").val() && $("#changePageA").val() > 0) {
					 			var redirectpage = $("#changePageA").val() - 1;
					 		} else {
					 			var redirectpage = 0;
					 		}	
					 		oTable.fnPageChange(redirectpage);		
					 	});
					 	//等datatable draw完成後，補上button 初始化 <button type="button" class="edit btn btn-primary" data-toggle="modal"><i class="fa fa-edit"></i>編輯</button>&emsp; 
				 		//jQuery按鈕圖示，先取消onclick預設的動作
				 		//刪除	
						$(".delPANHICALbtn")
							.click(function(){
								delCheck = confirm("請確認是否刪除！！\n\n若是，請按【確定】．\n\n若不是，請按【取消】");
								if (delCheck) {		
									oTable = $('#TblNhiCalList').dataTable().api();
									uTable = $('#TblNhiCalList').dataTable();
									info = oTable.page.info();		//記錄目前頁碼
									currentPage=info.page;
							        tr = $(this).parents('tr');
							        rowIndex = oTable.row(tr).index();
							        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data
							        tblname='PANHICAL';
							        
							        pk_data={
							        		"papaydt"  :rowData.papaydt,
							        		"paysource":rowData.paysource,
							        		"itemnum"  :rowData.itemnum,
							        		"payidno"  :rowData.payidno,
							        		"hid"      :pahid,
							        };
							        //return false;
							        $.ajax({
										url:"${pageContext.request.contextPath}/salController.do",
										data:{
											method   : 'handelNhi',
											fun      : 'updNhiCal',
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
											$("#btn_qryNhical").trigger( "click" );
											uTable.fnPageChange(currentPage);		//切到原先某碼
										},
										error:function(xhr) {
											alert("刪除失敗，程式發生錯誤!!!\nerror code:"+ xhr.status);  
										}
									});
								}
							});
					 	}
                 }    							
			);
				    
	$('#TblNhiCalList').dataTable(objCopyA);
	//顯示pa.panhical明細
	$('#TblNhiCalList tbody').on('click', '.nhicalData', function () {
		oTable = $('#TblNhiCalList').dataTable().api();
		info = oTable.page.info();		//記錄目前頁碼
		currentPage=info.page;
        var tr = $(this).parents('tr');
        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data
        $.doNhiCalDetail(rowData,0,"upd");        
    });
    //補充保費_獎金  jack=累積獎金
    $.doNhiCalDetail = function(rowData,jack,flag){    
     	$("#nhical_payidno").val(rowData.payidno);
     	$("#nhical_paycard").val(rowData.paycard);
     	$("#nhical_paynamec").val(rowData.paynamec);
     	$("#nhical_payempno").val(rowData.payempno);
     	$("#nhical_hid").val(rowData.hid);
     	
		if (flag=="upd"){
			$("#nhical_dpinref").selectpicker('val',rowData.moninsu);
			$("#nhical_dpinig").val(rowData.moninsu);
			$("#nhical_moninsu4").val(rowData.moninsu4);
			$("#nhical_insuident").val(rowData.insuident);
		
			$("#nhical_papaydt").val(rowData.papaydt);			
			$("#nhical_paysource").val(rowData.paysource);
			$("#nhical_itemnum").val(rowData.itemnum);
			$("#nhical_payitem").val(rowData.payitem);
			$("#nhical_bonusamt").val(rowData.bonusamt);
			$("#nhical_jackpot").val(rowData.jackpot);
			$("#nhical_mtinsu").val(rowData.mtinsu);
			$("#nhical_suplybase").val(rowData.suplybase);
			$("#nhical_nhiamt").val(rowData.nhiamt);
			$("#nhical_payupdat").val(rowData.payupdat);
			
			//modal標題:卡號+姓名+給付日期
			$("#nhicalItemModal").find('.modal-title').text("修改[補充保費_獎金] "+rowData.paycard+rowData.paynamec+rowData.papaydt);
		
			$(".tblEditgrp").hide();
			//將key設成readOnly
			$("#nhical_papaydt").attr("readOnly",true);	
			$("#nhical_paysource").attr("readOnly",true);	
			$("#nhical_itemnum").attr("readOnly",true);	
			$("#nhical_payidno").attr("readOnly",true);	
			
			$("#PANHICAL").show();
				
			$("label.error").hide();
			$(".error").removeClass("error");
	        $("#nhicalItemModal").modal("show");
			$("#subPANHICAL_Insbtn").prop("disabled", true);
			$("#subPANHICAL_Updbtn").prop("disabled", false);
		}else if (flag=="ins"){
			//modal標題:卡號+姓名+給付日期
			$("#nhicalItemModal").find('.modal-title').text("新增[補充保費_獎金] "+rowData.paycard+rowData.paynamec);
		
			$("#nhical_dpinref").selectpicker('val',rowData.insuamt);
			$("#nhical_dpinig").val(rowData.insuamt);
			//4倍投保額
			var amt4=0;
	 		if (getInt(rowData.insuamt)>0){	
	 			amt4= getInt(rowData.insuamt) * 4 ;
	 		}	
			$("#nhical_moninsu4").val(amt4);
			//身分別
			var tmprank=rowData.payrank;
			var insuident=0;
			if (tmprank=="1" || tmprank =="2" || tmprank =="3" || tmprank =="4" || tmprank =="8" || tmprank =="9")
				insuident=1;//公保
			else if (tmprank=="5" || tmprank =="6" || tmprank =="A" || tmprank =="B" || tmprank =="E" || tmprank =="F")
				insuident=2;//勞保	
			$("#nhical_insuident").val(insuident);
			//累積獎金,放在hidden
			$("#nhical_lastjackpot").val(jack);			
		}	
	};
    //申報扣費明細
 	var objCopyB = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyB.aoColumns = [	
							{ "sTitle":"","mData": null },
							{ "sTitle":"給付日期",	"mData": "papaydt" },
							{ "sTitle":"身份證字號",	"mData": "payidno" },
							{ "sTitle":"類別",		"mData": "payitem" },
							{ "sTitle":"卡號",		"mData": "paycard" },
							{ "sTitle":"姓名",		"mData": "paynamec" },
							{ "sTitle":"投保額",		"mData": "moninsu" },								
							{ "sTitle":"累積獎金",	"mData": "jackpot" },
							{ "sTitle":"給付金額",	"mData": "payamt" },
							{ "sTitle":"補充費",		"mData": "nhiamt" },
							{ "sTitle":"保險",		"mData": "insunm" },
							{ "sTitle":"流水序號",	"mData": "serial" },
							{ "sTitle":"時戳",		"mData": "payupdat" }	
						 ];
	objCopyB.iDisplayLength=10;
	
	objCopyB = $.extend(true,objCopyB, {
				  dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
							't'+
						'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
					buttons: [
					   'excelHtml5'/*  , {extend: 'csv',text: 'CSV',bom: true} */
					],
				"fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
				 	$('td:eq(4)', nRow).html('<a href = "#nhidetData" class= "nhidetData">'+aData.paycard+'</a>' );
				 	$('td:eq(0)', nRow).html( '<button type="button" class="delPANHIDETbtn btn btn-danger"><i class="fa fa-minus"></i></button>');
				 },	
	  			 "fnDrawCallback": function () {
	  			 		//到某頁
					 	$("#TblNhiDetList_paginate").append("  到第 <input style='height:28px;line-height:28px;width:40px;' class='margin text-center' id='changePageA' type='text'> 頁  <a class='btn btn-success shiny' style='margin-bottom:5px' href='javascript:void(0);' id='dataTable-btn'>確認</a>");
					 	oTable = $("#TblNhiDetList").dataTable();
					 	$('#dataTable-btn').click(function(e) {
					 		if($("#changePageA").val() && $("#changePageA").val() > 0) {
					 			var redirectpage = $("#changePageA").val() - 1;
					 		} else {
					 			var redirectpage = 0;
					 		}	
					 		oTable.fnPageChange(redirectpage);		
					 	});
					 	//等datatable draw完成後，補上button 初始化 <button type="button" class="edit btn btn-primary" data-toggle="modal"><i class="fa fa-edit"></i>編輯</button>&emsp; 
				 		//jQuery按鈕圖示，先取消onclick預設的動作
				 		//刪除	
						$(".delPANHIDETbtn")
							.click(function(){
								delCheck = confirm("請確認是否刪除！！\n\n若是，請按【確定】．\n\n若不是，請按【取消】");
								if (delCheck) {		
									oTable = $('#TblNhiDetList').dataTable().api();
									uTable = $('#TblNhiDetList').dataTable();
									info = oTable.page.info();		//記錄目前頁碼
									currentPage=info.page;
							        tr = $(this).parents('tr');
							        rowIndex = oTable.row(tr).index();
							        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data
							        tblname='PANHIDET';
							        
							        pk_data={
							        		"papaydt"  :rowData.papaydt,
							        		"payidno"  :rowData.payidno,
							        		"payitem"  :rowData.payitem,
							        		"hid"      :pahid,
							        };
							        //return false;
							        $.ajax({
										url:"${pageContext.request.contextPath}/salController.do",
										data:{
											method   : 'handelNhi',
											fun      : 'updNhiDet',
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
											$("#btn_qryNhidet").trigger( "click" );
											uTable.fnPageChange(currentPage);		//切到原先某碼
										},
										error:function(xhr) {
											alert("刪除失敗，程式發生錯誤!!!\nerror code:"+ xhr.status);  
										}
									});
								}
							});
					 	}
                 }    							
			);
				    
	$('#TblNhiDetList').dataTable(objCopyB);
	//顯示pa.panhidet明細
	$('#TblNhiDetList tbody').on('click', '.nhidetData', function () {
		oTable = $('#TblNhiDetList').dataTable().api();
		info = oTable.page.info();		//記錄目前頁碼
		currentPage=info.page;
        var tr = $(this).parents('tr');
        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data
        $.doNhiDetDetail(rowData,0,"upd");        
    });
    //申報扣費明細
    $.doNhiDetDetail = function(rowData,jack,flag){
    	$("#nhidet_payidno").val(rowData.payidno);		
     	$("#nhidet_paycard").val(rowData.paycard);
     	$("#nhidet_paynamec").val(rowData.paynamec);
		$("#nhidet_hid").val(rowData.hid);
		if (flag=="upd"){
			$("#nhidet_papaydt").val(rowData.papaydt);
			$("#nhidet_payitem").val(rowData.payitem);
			$("#nhidet_serial").val(rowData.serial);
			$("#nhidet_payamt").val(rowData.payamt);
			$("#nhidet_nhiamt").val(rowData.nhiamt);
			$("#nhidet_jackpot").val(rowData.jackpot);
			$("#nhidet_payupdat").val(rowData.payupdat);
		
			$("#nhidet_dpinref").selectpicker('val',rowData.moninsu);
			$("#nhidet_dpinig").val(rowData.moninsu);
			$("#nhidet_insuident").val(rowData.insuident);
			
			//modal標題:卡號+姓名+給付日期
			$("#nhidetItemModal").find('.modal-title').text("修改[申報扣費明細] "+rowData.paycard+rowData.paynamec+rowData.papaydt);
		
			//$("#model_header_title").html("修改[補充保費_獎金]");
			$(".tblEditgrp").hide();
			//將key設成readOnly
			$("#nhidet_papaydt").attr("readOnly",true);	
			$("#nhidet_payitem").attr("readOnly",true);	
			$("#nhidet_payidno").attr("readOnly",true);	
			//設成readOnly
			$("#nhidet_serial").attr("readOnly",true);
			
			$("#PANHIDET").show();
				
			$("label.error").hide();
			$(".error").removeClass("error");
	        $("#nhidetItemModal").modal("show");
			$("#subPANHIDET_Insbtn").prop("disabled", true);
			$("#subPANHIDET_Updbtn").prop("disabled", false);
		}else if (flag=="ins"){
			$("#nhidet_dpinref").selectpicker('val',rowData.insuamt);
			$("#nhidet_dpinig").val(rowData.insuamt);
			
			//身分別
			var tmprank=rowData.payrank;
			var insuident=0;
			if (tmprank=="1" || tmprank =="2" || tmprank =="3" || tmprank =="4" || tmprank =="8" || tmprank =="9")
				insuident=1;//公保
			else if (tmprank=="5" || tmprank =="6" || tmprank =="A" || tmprank =="B" || tmprank =="E" || tmprank =="F")
				insuident=2;//勞保	
			$("#nhidet_insuident").val(insuident);
			//累積獎金,放在hidden
			$("#nhidet_lastjackpot").val(jack);
		}	
	};
	
	 //補充保險費[獎金]
 	var objCopyC = $.extend(true, {aaSorting: []}, dataTableConfig);			
	objCopyC.aoColumns = [	
							{ "sTitle":"","mData": null },
							{ "sTitle":"年度",	"mData": "yy" },
							{ "sTitle":"身份證字號",	"mData": "payidno" },
							{ "sTitle":"卡號",		"mData": "paycard" },
							{ "sTitle":"姓名",		"mData": "paynamec" },
							{ "sTitle":"投保額",		"mData": "moninsu" },								
							{ "sTitle":"4投保額",	"mData": "moninsu4" },	
							{ "sTitle":"累積獎金",	"mData": "bonusamt" }
																			
						 ];
	objCopyC.iDisplayLength=10;
	
	objCopyC = $.extend(true,objCopyC, {
				  dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
							't'+
						'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
					buttons: [
					   'excelHtml5'/*  , {extend: 'csv',text: 'CSV',bom: true} */
					],
				 "fnDrawCallback": function () {
	  			 		//第1欄序列
						 var api = this.api();
						var startIndex=0;
                    	api.column(0).nodes().each(function(cell, i) {
                        	cell.innerHTML = startIndex + i + 1;
                    	}); 
                    	//到某頁
					 	$("#TblNhiCalList_paginate").append("  到第 <input style='height:28px;line-height:28px;width:40px;' class='margin text-center' id='changePageC' type='text'> 頁  <a class='btn btn-success shiny' style='margin-bottom:5px' href='javascript:void(0);' id='dataTable-btn'>確認</a>");
					 	oTable = $("#TblNhiCalList").dataTable();
					 	$('#dataTable-btn').click(function(e) {
					 		if($("#changePageC").val() && $("#changePageC").val() > 0) {
					 			var redirectpage = $("#changePageC").val() - 1;
					 		} else {
					 			var redirectpage = 0;
					 		}	
					 		oTable.fnPageChange(redirectpage);		
					 	});
					}
                 }    							
			);
				    
	$('#TblAccuList').dataTable(objCopyC);    	      
});

</script>
</head>
<body>

<ul class="nav nav-pills mb-3" id="pills-tab" role="tab">
  <li class="nav-item">
    <a class="nav-link active" id="pills-nhiprod-tab" data-toggle="pill" href="#byNhiProdMain" role="tab" aria-controls="pills-home" aria-selected="true">計算/匯出</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-nhical-tab" data-toggle="pill" href="#byNhiCalMain" role="tab" aria-controls="pills-profile" aria-selected="false">補充保險費</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-nhidet-tab" data-toggle="pill" href="#byNhiDetMain" role="tab" aria-controls="pills-profile" aria-selected="false">申報扣費明細</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-nhiaccu-tab" data-toggle="pill" href="#byNhiAccuMain" role="tab" aria-controls="pills-profile" aria-selected="false">累積獎金</a>
  </li>
</ul>

<div class="tab-content" id="pills-tabContent">
	<!--補充保險費-->
  	<div class="tab-pane fade" id="byNhiCalMain" role="tabpanel" aria-labelledby="pills-nhical-tab">
		<div class="container-fluid">
			<div class="row" id="qryNhiCalItem">
				<div class="col-7">
					<div class="form-inline">
		    			<div class="input-group">
		    				<input type="text" id="qry_nhical_paydt" size="10" class="form-control-sm gclsCalTxt" placeholder="給付日期" >
		    				<span class="input-group-text">年度</span>
		    				<select id="qry_nhical_nhiyr" class="form-control-sm nhiyr" title="年度">
								
							</select>
							<select id="qry_nhical_nhimon" class="selectpicker" data-width="fit" multiple title="月">
								<option value="" ></option>
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
		    				<span class="input-group-text">保險</span>
		    				<select id="qry_nhical_insu" class="form-control"  >
		    					<option value="" ></option>
								<option value="0:無" >0:無</option>
								<option value="1:公保">1:公保</option>
								<option value="2:勞保">2:勞保</option>						
							</select>
							<span class="input-group-text">類別</span>
							<select id="qry_nhical_payitem" class="form-control" >
		    					<option value="" ></option>
								<option value="62:獎金">62:獎金</option>
								<option value="63:兼職">63:兼職</option>						
							</select>
		    				<input type="text" id="qry_nhical_cardno" size="4" class="form-control-sm text-uppercase" placeholder="卡號" >
							<input type="text" id="qry_nhical_idtno" size="10" class="form-control-sm text-uppercase" placeholder="身分證" >
							<input type="text" id="qry_nhical_namec" size="12" class="form-control-sm " placeholder="姓名" >
							
						</div>		    		
			    		
					</div>
				</div>
				<div class="col-3">
					<table class="sortable">
						<thead><td></td><th>公保</th><th>勞保</th><th>兼職</th></thead>
						<tr>
							<th>給付總額</th><td id="GEINFPayAmt" style="text-align: right" class="itemsum"></td><td id="LBINFPayAmt" style="text-align: right" class="itemsum"></td><td id="PartPayAmt" style="text-align: right" class="itemsum"></td>
						</tr>
						<tr>
							<th>補充費</th><td id="GEINFNHIAmt" style="text-align: right" class="itemsum"></td><td id="LBINFNHIAmt" style="text-align: right" class="itemsum"></td><td id="PartNHIAmt" style="text-align: right" class="itemsum"></td>
						</tr>
					</table>
				</div>	
				<div class="col-2">
					<button id="btn_qryNhical" class="btn btn-info" type="button" ><i class="fa fa-search"></i>查詢</button>
					<button id="btn_addNhical" class="btn btn-primary" type="button" data-toggle="modal" data-target="#nhicalItemModal"><i class="fa fa-edit"></i>新增</button>
					<button id="btn_clnNhical" class="btn btn-warning" type="button"><i class="fa fa-eraser"></i>清空</button>
				</div>
			</div>
				
			<div class="row">
				<div class="col-12 border border-dark">
					<!-- pa.panhical清單 -->
					<table id="TblNhiCalList" class="table table-striped table-bordered table-hover" >
						<thead>							
						</thead>
					</table>
				</div>
			</div>
 		</div>	
	</div>
	
	<!--申報扣費明細-->
	<div class="tab-pane fade" id="byNhiDetMain" role="tabpanel" aria-labelledby="pills-nhidet-tab">
		<div class="container-fluid">
			<div class="row" id="qryNhiDetItem">
				<div class="col-7">
					<div class="form-inline">
		    			<div class="input-group">
		    				<input type="text" id="qry_nhidet_paydt" size="10" class="form-control-sm gclsCalTxt" placeholder="給付日期" >
		    				<select id="qry_nhidet_nhiyr" class="form-control nhiyr"  title="年度">
								
							</select>
							<select id="qry_nhidet_nhimon" class="selectpicker" data-width="fit" multiple title="月">
								<option value="" ></option>
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
		    				<span class="input-group-text">類別</span>
							<select id="qry_nhidet_payitem" class="form-control" >
		    					<option value="" ></option>
								<option value="62:獎金">62:獎金</option>
								<option value="63:兼職">63:兼職</option>						
							</select>
		    				<input type="text" id="qry_nhidet_cardno" size="4" class="form-control-sm text-uppercase" placeholder="卡號" >
							<input type="text" id="qry_nhidet_idtno" size="10" class="form-control-sm text-uppercase" placeholder="身分證" >
							<input type="text" id="qry_nhidet_namec" size="12" class="form-control-sm " placeholder="姓名" >
							
						</div>		    		
			    		
					</div>
				</div>
				<div class="col-3">
					<table class="sortable">
						<thead><td></td><th>公保</th><th>勞保</th><th>兼職</th></thead>
						<tr>
							<th>給付總額</th><td id="GEINFPayDet" style="text-align: right" class="itemsum"></td><td id="LBINFPayDet" style="text-align: right" class="itemsum"></td><td id="PartPayDet" style="text-align: right" class="itemsum"></td>
						</tr>
						<tr>
							<th>補充費</th><td id="GEINFNHIDet" style="text-align: right" class="itemsum"></td><td id="LBINFNHIDet" style="text-align: right" class="itemsum"></td><td id="PartNHIDet" style="text-align: right" class="itemsum"></td>
						</tr>
					</table>
				</div>	
				<div class="col-2">
					<button id="btn_qryNhidet" class="btn btn-info" type="button" ><i class="fa fa-search"></i>查詢</button>
					<button id="btn_addNhidet" class="btn btn-primary" type="button" data-toggle="modal" data-target="#nhidetItemModal"><i class="fa fa-edit"></i>新增</button>
					<button id="btn_clnNhidet" class="btn btn-warning" type="button"><i class="fa fa-eraser"></i>清空</button>
				</div>
			</div>
				
			<div class="row">
				<div class="col-12 border border-dark">
					<!-- pa.panhidet清單 -->
					<table id="TblNhiDetList" class="table table-striped table-bordered table-hover" >
						<thead>							
						</thead>
					</table>
				</div>
			</div>
 		</div>
	</div>
	<!--計算/匯出-->
	<div class="tab-pane fade show active" id="byNhiProdMain" role="tabpanel" aria-labelledby="pills-nhidet-tab">
		<div class="container-fluid">
			<div class="row" >
				<div class="col-10">
					<div class="form-inline">
						<div class="input-group">
							<span class="input-group-text">項目</span>	
							<select id="prod_cond" class="form-control-sm" title="條件">
								<option value="1:補充費_[計算寫入](餉單編單):Cal_1">1:補充費_[計算寫入](餉單編單)</option>
			     				<option value="2:補充費_[刪除](餉單編單):Cal_2">2:補充費_[刪除](餉單編單)</option>
								<option value="3:補充費_[產生]至批次檔(餉單編號):Cal_3">3:補充費_[產生]至批次檔(餉單編號)</option>
								<option value="4:補充費_[刪除]批次檔(餉單編號):Cal_4">4:補充費_[刪除]批次檔(餉單編號)</option>	
								<option value="5:申報扣費明細_[產生](給付日期):Det_1">5:申報扣費明細_[產生](給付日期)</option>	
								<option value="6:申報扣費明細_[刪除](給付日期):Det_2">6:申報扣費明細_[刪除]給付日期)</option>	
								<option value="7:申報扣費明細_[產生]媒體申報檔(給付日期):Det_3">7:申報扣費明細_[產生]媒體申報檔(給付日期)</option>	
								<option value="8:所得稅_[寫入]轉帳項目35 57 58(給付日期):Tax_1">8:所得稅_[寫入]轉帳項目:35 57 58(給付日期)</option>
								<option value="9:所得稅_[寫入]轉帳項目59(給付日期):Tax_2">9:所得稅_[寫入]轉帳項目:59(給付日期)</option>
								<option value="10:所得稅_[刪除](給付日期):Tax_3">10:所得稅_[刪除](給付日期)</option>							
							</select>
						
							<span class="input-group-text prod_1">餉單編號-發薪日</span>
							<select id="prod_nhi_SalNum" class="form-control prod_1"  data-width="fit">
								
							</select>
							<span class="input-group-text prod_2 d-none">給付日期</span>
							<input type="text" id="prod_nhi_paydt" size="10" class="form-control gclsCalTxt prod_2 d-none"  >
							<span class="input-group-text prod_3 d-none">製票號</span>
							<input type="text" id="prod_nhi_payno" size="10" class="form-control prod_3 d-none"  >
						</div>
					</div>
				</div>	
				<div class="col-2">	
					<button id="btn_prodNhi" class="btn btn-info " type="button" ><i class="fa fa-cog"></i>執行</button>
					<button id="btn_clnNhi" class="btn btn-warning " type="button"><i class="fa fa-eraser"></i>清空</button>
				</div>	
			</div>
			<div class="row">
			<div class="col-md-12">
				<div id="explanProd_1" class="text-danger"></div>
			</div>
			</div>
		</div>
	</div>
	<!--累積獎金-->
	<div class="tab-pane fade" id="byNhiAccuMain" role="tabpanel" aria-labelledby="pills-nhiaccu-tab">
		<div class="container-fluid">
			
			<div class="row">
				<div class="col-12">
					<a href="#explan_NhiAccu" class="btn btn-danger" data-toggle="collapse">注意事項</a>
			  		<div id="explan_NhiAccu" class="collapse">
			  			<div id="explanAccu_1" class="text-danger">
				  			1.年+卡號/身分證/姓名+【查詢】:依[年]+[卡號/身分證/姓名],列出年度累積獎金<br>
				  			2.身分證:唯一值,卡號/姓名:會有重複問題<br>					  			
				  		</div>
				  	</div>	
				</div>
			</div>
				
			<div class="row">
				<div class="col-10">
					<div class="form-inline">
		    			<div class="input-group">
		    				<span class="input-group-text">年度</span>
		    				<select id="qry_accu_nhiyr" class="form-control-sm nhiyr" title="年度">
								
							</select>
							<input type="text" id="qry_accu_cardno" size="4" class="form-control-sm text-uppercase" placeholder="卡號" >
							<input type="text" id="qry_accu_idtno" size="10" class="form-control-sm text-uppercase" placeholder="身分證" >
							<input type="text" id="qry_accu_namec" size="12" class="form-control-sm " placeholder="姓名" >
							
						</div>		    		
			    		
					</div>
				</div>
				<div class="col-2">
					<button id="btn_qryAccu" class="btn btn-info" type="button" ><i class="fa fa-search"></i>查詢</button>
					<button id="btn_clnAccu" class="btn btn-warning" type="button"><i class="fa fa-eraser"></i>清空</button>
				</div>
			</div>
				
			<div class="row">
				<div class="col-12 border border-dark">
					<!-- pa.panhical清單 -->
					<table id="TblAccuList" class="table table-striped table-bordered table-hover" >
						<thead>							
						</thead>
					</table>
				</div>
			</div>
 		</div>	
	</div>
</div>

<!-- 補充保費_獎金 -->
<div class="modal fade"  id="nhicalItemModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog" >
		<!-- Modal content-->
		<div class="modal-content" id="nhicalContent">
			<form id="subPANHICALForm">
				<INPUT type="hidden" id ="nhicalitem"  value="">
				<div class="modal-header">
					<h5 class="modal-title">補充保費_獎金</h5>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">×</span></button>
				 </div>
				<div class="modal-body">
					<div class="container-fluid">
						<table id="PANHICAL" class="table table-striped table-bordered table-condensed table-responsive table-sm caption-top tblEditgrp" style="text-align:center;">
							<INPUT type="hidden" id ="nhical_hid"  value="">
							<INPUT type="hidden" id ="nhical_lastjackpot"  value=""><!--同一人上期累加獎金-->
							<tbody>
		            		<tr>
		            			<th>*給付日期</th>		
		            			<Td class="text-left"><input type="text" id="nhical_papaydt" name="papaydt" class="form-control gclsCalTxt required" maxlength="10" size="10"/></td>
		            			<th>*資料來源</th>
		            			<Td><select id="nhical_paysource" name="paysource" class="form-control required" >
		            					<option value=""></option> 
										<option value="DAT">DAT</option>
						     			<option value="TAX">TAX</option>						     		
							     	</select>
							    <th>*餉單編號</th>	
		            			<Td><input type="text" id="nhical_itemnum" name="itemnum" class="form-control digits required" maxlength="4" size="4"  /></Td>	
		            			<th>*身分證字號</th>	
		            			<Td><input type="text" id="nhical_payidno" name="payidno" class="form-control findkey text-uppercase required" maxlength="10" size="10"  /></Td>
		            		</tr>
		            		<tr>
		            			<th>所得類別</th>	
		            			<Td>
			            			<select id="nhical_payitem" name="payitem" class="form-control required" style="width:auto;" >
				            			<option value="" ></option>
										<option value="62">62:獎金</option>
										<option value="63">63:兼職</option>
									</select></Td>
								<th>身份別</th>	
		            			<Td><select id="nhical_insuident" name="insuident" class="form-control required" >
			    					<option value="" ></option>
									<option value="0" >0:無</option>
									<option value="1">1:公保</option>
									<option value="2">2:勞保</option>						
								</select></Td>		
		            			<th>卡號</th>	
		            			<Td><input type="text" id="nhical_paycard" name="paycard"class="form-control findkey text-uppercase" maxlength="4" size="4"  /></Td>
		            			<th>姓名</th>	
		            			<Td><input type="text" id="nhical_paynamec" name="paynamec"class="form-control text-uppercase required" maxlength="12" size="12"  /></Td>
		            			
		            		</tr>
		            		<tr>	
		            			<th>投保金額(A)</th>	
		            			<Td>
			            			<select id="nhical_dpinref" class="form-control gdpin ref" style="width:auto;"> 
															     		
							     	</select>
		            				<input type="text" id="nhical_dpinig" name="moninsu"class="form-control required digits" maxlength="7" size="7"  /></Td>
		            			<th>4倍投保額(B)</th>
		            			<Td><input type="text" id="nhical_moninsu4" name="moninsu4"class="form-control required digits" maxlength="7" size="7"  /></Td>
		            			<th>單次獎金(C)</th>
		            			<Td><input type="text" id="nhical_bonusamt" name="bonusamt"class="form-control required digits chgnhi" maxlength="7" size="7"  /></Td>
		            			<th>累積獎金(D)</th>
		            			<Td><input type="text" id="nhical_jackpot" name="jackpot"class="form-control required digits chgnhi" maxlength="7" size="7"  /></Td>
		            		</tr>
		            		<tr>
		            			<th>累積逾4倍投保額<br>(E)=D-B</th>
		            			<Td><input type="text" id="nhical_mtinsu" name="mtinsu"class="form-control required digits" maxlength="7" size="7"  /></Td>
		            			<th>費基(F)min(E,C)</th>
		            			<Td><input type="text" id="nhical_suplybase" name="suplybase"class="form-control required digits" maxlength="7" size="7"  /></Td>
		            			<th>補充保險費(G=F*費率)</th>
		            			<Td><input type="text" id="nhical_nhiamt" name="nhiamt"class="form-control required digits" maxlength="7" size="7"  /></Td>
		            		</tr>
		            		</tbody>
						</table>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" id="subPANHICAL_Insbtn">確認儲存</button>
			        <button type="button" class="btn btn-primary" id="subPANHICAL_Updbtn">更改</button>
				   	<button type="button" class="btn btn-secondary" data-dismiss="modal">返回</button>
				</div>
			</form>	
		</div>
	</div>
</div>	
<!-- 申報扣費明細 -->
<div class="modal fade"  id="nhidetItemModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog" >
		<!-- Modal content-->
		<div class="modal-content" id="nhidetContent">
			<form id="subPANHIDETForm">
				<INPUT type="hidden" id ="nhidetitem"  value="">
				<div class="modal-header">
					<h5 class="modal-title">申報扣費明細</h5>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">×</span></button>
				 </div>
				<div class="modal-body">
					<div class="container-fluid">
						<table id="PANHIDET" class="table table-striped table-bordered table-condensed table-responsive table-sm caption-top tblEditgrp" style="text-align:center;">
							<INPUT type="hidden" id ="nhidet_hid"  value="">
							<INPUT type="hidden" id ="nhidet_lastjackpot"  value=""><!--同一人上期累加獎金-->
							<tbody>
		            		<tr>
		            			<th>*給付日期</th>		
		            			<Td class="text-left"><input type="text" id="nhidet_papaydt" name="papaydt" class="form-control gclsCalTxt required" maxlength="10" size="10"/></td>
		            			<th>*身分證字號</th>	
		            			<Td><input type="text" id="nhidet_payidno" name="payidno" class="form-control findkey text-uppercase required" maxlength="10" size="10"  /></Td>
		            			<th>*所得類別</th>	
		            			<Td>
			            			<select id="nhidet_payitem" name="payitem" class="form-control required" style="width:auto;" >
				            			<option value="" ></option>
										<option value="62">62:獎金</option>
										<option value="63">63:兼職</option>
									</select></Td>
		            		</tr>
		            		<tr>
								<th>身份別</th>	
		            			<Td><select id="nhidet_insuident" name="insuident" class="form-control required" >
			    					<option value="" ></option>
									<option value="0" >0:無</option>
									<option value="1">1:公保</option>
									<option value="2">2:勞保</option>						
								</select></Td>		
		            			<th>卡號</th>	
		            			<Td><input type="text" id="nhidet_paycard" name="paycard"class="form-control findkey text-uppercase" maxlength="4" size="4"  /></Td>
		            			<th>姓名</th>	
		            			<Td><input type="text" id="nhidet_paynamec" name="paynamec"class="form-control text-uppercase required" maxlength="12" size="12"  /></Td>
		            			
		            		</tr>
		            		<tr>	
		            			<th>投保金額</th>	
		            			<Td>
			            			<select id="nhidet_dpinref" class="form-control gdpin selectpicker ref " style="width:auto;"> 
															     		
							     	</select>
		            				<input type="text" id="nhidet_dpinig" name="moninsu"class="form-control required digits" maxlength="7" size="7"  /></Td>
		            			<th>單次獎金</th>
		            			<Td><input type="text" id="nhidet_payamt" name="payamt"class="form-control required digits chgnhi" maxlength="7" size="7"  /></Td>
		            			<th>累積獎金</th>
		            			<Td><input type="text" id="nhidet_jackpot" name="jackpot"class="form-control required digits" maxlength="7" size="7"  /></Td>
		            		</tr>
		            		<tr>
		            			<th>補充保險費</th>
		            			<Td><input type="text" id="nhidet_nhiamt" name="nhiamt"class="form-control required digits" maxlength="7" size="7"  /></Td>
		            			<th>流水序號</th>
		            			<Td><input type="text" readonly  id="nhidet_serial" name="serial"class="form-control digits" maxlength="7" size="7"  /></Td>
		            		</tr>
						</table>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" id="subPANHIDET_Insbtn">確認儲存</button>
			        <button type="button" class="btn btn-primary" id="subPANHIDET_Updbtn">更改</button>
				   	<button type="button" class="btn btn-secondary" data-dismiss="modal">返回</button>
				</div>
			</form>	
		</div>
	</div>
</div>	
</body>
</html>