<!DOCTYPE HTML>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<html>
<head>
<title>代碼檔維護</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<jsp:include page="../include.jsp"></jsp:include>

<!-- <style>
	/* ui設定 */
	.dataTables_wrapper {font-family: tahoma;font-size: 12px;}
	.ui-widget-header{font-size: 11px !important;}
	.dataTables_wrapper .ui-toolbar{padding:2px;}
	.ui-widget-content{font-size: 12px !important;} 
	.ui-datepicker{ width: 17em; padding: .2em .2em 0; display: none; font-size: 70%!important;}
	.ui-widget input, .ui-widget select, .ui-widget textarea, .ui-widget button{font-size: 12px !important;font-family: Verdana, Arialx !important;}
	
	
	
#tabs { 
    height:98%;
    margin-top:0px;
    margin-left:0px;
    margin-right:auto;
    margin-bottom:0px; 
    border: 0px solid 000000;}
</style> -->
<script>
//檢查使否有代理人，如有代理人則取代原資料
var agent = "${sessionScope.agent}";	//代理人卡號
var paUserid = ("" != agent) ? agent : userid;            //使用者卡號
var paUsername = ("" != agent) ? "${sessionScope.agentname}" : username;            //使用者姓名
var pahid = ("" != agent) ? "${sessionScope.agenthid}" : hid;            //醫院別
var pacostid = ("" != agent) ? "${sessionScope.agentcostid}" : costid;            //使用者成本中心
var padeptnm = ("" != agent) ? "${sessionScope.agentdeptnm}" : deptnm;            //使用者單位名稱
var padeptno = ("" != agent) ? "${sessionScope.agentdeptno}" : deptno;            //使用者單位名稱 英文

var oTable;
var uTable;
var codeTbl;
var tr;
var currentPage;
var rowIndex;
var rowData;
var selCode="1";
var tblname="";
var code_data;
var codeTblNm="";
var tabid="";
var delCheck;
var titsubary = [{id:"V",text:"V 醫師"},{id:"N",text:"N 護士"},{id:"S",text:"S 研究人員"},{id:"T",text:"T 醫務技術"},{id:"U",text:"U 工程人員"},{id:"W",text:"W 醫學工程"},{id:"D",text:"D 藥師"},{id:"G",text:"G 行政"},{id:"B",text:"B 社工"},{id:"C",text:"C 資訊"},{id:"H",text:"H 約聘僱"},{id:"I",text:"I 契約"},{id:"P",text:"P 駐警"},{id:"Y",text:"Y 技工友"},{id:"A",text:"A 兼職人員"}];
var rankary = [{id:"0",text:"Ｏ"},{id:"1",text:"1 簡"},{id:"2",text:"2 薦"},{id:"3",text:"3 委"},{id:"4",text:"4 雇"},{id:"5",text:"5 技"},{id:"6",text:"6 工"},{id:"7",text:"7 臨"},{id:"8",text:"8 師"},{id:"9",text:"9 士"},{id:"A",text:"A 約(醫/技)"},{id:"B",text:"B 約(護理)"},{id:"C",text:"C 約半日(醫/技)"},{id:"D",text:"D 約半日(護理)"},{id:"E",text:"E 約聘醫師"},{id:"F",text:"F 契約人員"},{id:"G",text:"G 兼職人員"}];

$(function(){
	//tab設定
	$('#pills-tab a').on('click', function (e) {
	  e.preventDefault();
	  $(this).tab('show');
	  tabid=$(this).attr('id');
	  console.log(tabid);		
	  if (tabid=="pills-insu-tab"){
	  	$("#qryInsutyp").trigger( "change" );	
	  }else if (tabid=="pills-code-tab"){
	  	$("#qrycodetyp").trigger( "change" );
	  }else if (tabid=="pills-items-tab"){
	  	$("#qryItemstyp").trigger( "change" );
	  }else if (tabid=="pills-wkpf-tab"){
	  	$("#qryWkpftyp").trigger( "change" );
	  }
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
	});
	//下拉式選單啟動 https://www.py4u.net/discuss/905582
 	$('.selectpicker').selectpicker();
	
	//驗證	
	$("subsalCodeForm,#subsalInsuForm,#subsalItemsForm,#subsalWkpfForm").validate();
	
	//datatable設定
	//datetimepicker設定 (日期設定)
	//$( ".datepicker" ).datepicker( { dateFormat: 'yy-mm-dd' } );
	$('body').on('focus', '.datepicker', function() { //以此方式才能動態更改頁面datepicker
          $(this).datepicker( { dateFormat: 'yy-mm-dd' } );
    });
	
	$('#titsubKey,#coddelKey').selectpicker('hide');
	
	$.getLoadCodeTbl = function(tbl,flag){		
		$.ajax({
			url:"${pageContext.request.contextPath}/salController.do",
			data:{
				method  : 'getLoadCodeTbl',
				tbl     :tbl,
				flag    :flag,
				hid     :pahid
			},
			type:"POST",			//type:POST, GET(default:GET)
			dataType:"json",	//dataType: text, xml, html, script, json, jsonp
			contentType: "application/x-www-form-urlencoded;charset=utf-8",
			success:function(data) {
				if (tbl=="PARANKCODE"){
					if (data.alRankCodeTbl[0]!=null){
						$('.RANKCODE').empty();
						$('.RANKCODE')
					          .append($('<option>', { value : '' })
					          .text('')); 
						$.each(data.alRankCodeTbl, function(idx) {   
						     $('.RANKCODE')
					          .append($('<option>', { value : data.alRankCodeTbl[idx].rankcode })
					          .text(data.alRankCodeTbl[idx].rankcode+"-"+data.alRankCodeTbl[idx].rankname)); 
						});
					}
				}else if (tbl=="PATITLE"){
					if (data.alTitleCodeTbl[0]!=null){
						$('.JTCODE').empty();
						$('.JTCODE')
					          .append($('<option>', { value : '' })
					          .text('')); 
						$.each(data.alTitleCodeTbl, function(idx) {   
						     $('.JTCODE')
					          .append($('<option>', { value : data.alTitleCodeTbl[idx].paytitlk })
					          .text(data.alTitleCodeTbl[idx].patitsub+"-"+data.alTitleCodeTbl[idx].paytitlk+"-"+data.alTitleCodeTbl[idx].paytitlc)); 
						});
					}
				}	
			}  
		});
	};
	/*預先載入代碼檔 */
	<c:if test="${alRankCodeTbl eq null or empty alRankCodeTbl}">
		console.log("預先載入代碼檔:alRankCodeTbl"); 	
		$.getLoadCodeTbl("PARANKCODE","N");	
 	</c:if>
 	<c:if test="${alTitleCodeTbl eq null or empty alTitleCodeTbl}">
 		console.log("預先載入代碼檔:alTitleCodeTbl");
		$.getLoadCodeTbl("PATITLE","N");	
 	</c:if>
 	// Remove the option with value 'val_1'
 	console.log("pa_admin:"+pa_admin+" agent:"+agent+" pahid:"+pahid+" agenthid:"+"${sessionScope.agenthid}"); 	
 	if (pa_admin=="Y" && agent ==""){
 	
	}else if (pahid=="2A0" ) {
		$("#qrycodetyp option[value='8:PAEXPFTBL']").remove();
       	$("#qrycodetyp option[value='9:PARNK4TBL']").remove();
		$("#qrycodetyp option[value='10:PATRACODE']").remove();
		$("#qryWkpftyp option[value='2:PAPFTL']").remove();        	
	}
	else if (pahid=="3A0") {
		$("#qrycodetyp option[value='8:PAEXPFTBL']").remove();
	    $("#qrycodetyp option[value='9:PARNK4TBL']").remove();
		$("#qrycodetyp option[value='10:PATRACODE']").remove();        	
	    $("#pills-wkpf-tab").hide();
    }   
    else if (pahid=="1A0") {
       	$("#qrycodetyp option[value='10:PATRACODE']").remove();
	}
	
	//代碼選單切換
	$("#qrycodetyp").change(function(){
		$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
		$("#txtKEY1").hide();
//  		$('#titsubKey,#coddelKey').selectpicker('hide');
		$('.qryCodeSelect').selectpicker('hide');
		
		selCode=$("#qrycodetyp option:selected").val().split(':')[0];
		if (typeof(selCode)=="undefined" || '' == selCode){
			selCode="1";
		}
		            
		tblname=$("#qrycodetyp option:selected").val().split(':')[1];
		if (tblname=="PATRACODE"){
			codeTbl="tblCodeTraList";
		}else if (tblname=="PADEPT"){
			codeTbl="tblCodeDeptList";
		}else if (tblname=="PARANKCODE"){
			codeTbl="tblCodeRankList";
		}else if (tblname=="PACATGTBL"){
			codeTbl="tblCodeCatgList";
		}else if (tblname=="PAEXPFTBL"){
			codeTbl="tblCodeExpfList";
		}else if (tblname=="PARNK4TBL"){
			codeTbl="tblCodeRnk4List";
		}else if (tblname=="PABANK"){
			codeTbl="tblBankDtList";
		}else if (tblname=="PATAXTBL"){
			codeTbl="tblTaxTblList";
			$("#txtKEY1").show();
		}else if (tblname=="PAPAYITM"){
			codeTbl="tblPayItmList";
			$('#coddelKey').selectpicker('show');
 			$('#coddelKey').selectpicker('refresh');
		}else if (tblname=="PATITLE"){
			codeTbl="tblCodeTitList";
 			$('.qryCodeSelect').selectpicker('show');
 			$('.qryCodeSelect').selectpicker('refresh');
		}
		
		console.log(codeTbl);
		$('#'+codeTbl).show();		
		$("#btn_qryCode").trigger( "click" );		 			
	});	
	
	//保費項目選單切換
	$("#qryInsutyp").change(function(){
		$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
		selCode=$("#qryInsutyp option:selected").val().split(':')[0];
		if (typeof(selCode)=="undefined" || '' == selCode){
			selCode="1";
		}
		tblname=$("#qryInsutyp option:selected").val().split(':')[1];
		if (tblname=="PADPINTBL"){	//健保
			codeTbl="tblInsuDpinList";
		}else if (tblname=="PALBINTBL"){	//勞保
			codeTbl="tblInsuLbinList";
		}else if (tblname=="PALBRPTBL"){	//勞退
			codeTbl="tblInsuLbrpList";
		}
		
		console.log(codeTbl);
		$('#'+codeTbl).show();		
		$("#btn_qryInsu").trigger( "click" );		 			
	});
	
	//支扣數 選單切換
	$("#qryItemstyp").change(function(){
		$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
		$("#items_txtKEY2").hide();
		selCode=$("#qryItemstyp option:selected").val().split(':')[0];
		if (typeof(selCode)=="undefined" || '' == selCode){
			selCode="1";
		}
		tblname=$("#qryItemstyp option:selected").val().split(':')[1];
		if (tblname=="PAPOSI"){	//職等支扣--專業加給及主管加給
			codeTbl="tblPosiList";
		}else if (tblname=="PARANK"){	//階級支扣
			codeTbl="tblRankList";
			$("#items_txtKEY2").show();
		}
		
		console.log("codeTbl:"+codeTbl);
		$('#'+codeTbl).show();		
		$("#btn_qryItems").trigger( "click" );		 			
	});
	//工作獎金 選單切換
	$("#qryWkpftyp").change(function(){
		$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
		$("#cspf_jt").hide();
		selCode=$("#qryWkpftyp option:selected").val().split(':')[0];
		if (typeof(selCode)=="undefined" || '' == selCode){
			selCode="1";
		}
		tblname=$("#qryWkpftyp option:selected").val().split(':')[1];
		if (tblname=="PAPFTL"){			//全院績效彙總 
			codeTbl="tblPftlList";
		}else if (tblname=="PACSPF"){	//工作獎金績分 
			codeTbl="tblCspfList";
			$("#cspf_jt").show();
		}
		
		console.log("codeTbl:"+codeTbl);
		$('#'+codeTbl).show();		
		$("#btn_qryWkpf").trigger( "click" );		 			
	});
	//jQuery按紐圖示
	$(".btn").button().click(
		function(event) {event.preventDefault();}
	);
	$(".btn").click(function(){
		var id = $(this).attr('id');
		var key1="";
		var key2="";
		if(id == 'btn_clear' || id =='btn_clearInsu' || id =='btn_clearItems' || id =='btn_clearWkpf'){		//代碼維護_清空
			$(":text,textarea").val("");
			$("label.error").hide();
			$('.qryCodeSelect').selectpicker('val', [1]);
			$('#cspf_jt').prop("selectedIndex", 0);
 			$(".error").removeClass("error");
 			$('#' + codeTbl ).dataTable().fnClearTable();
		}	
		else if(id == 'btn_qryCode'){		//代碼維護_查詢

			selCode = $('#qrycodetyp').val().split(':')[0];
			tblname=$("#qrycodetyp option:selected").val().split(':')[1];
			if (typeof(codeTbl)=="undefined" || '' == codeTbl){
				if (tblname=="PATRACODE"){
					codeTbl="tblCodeTraList";
				}else if (tblname=="PADEPT"){
					codeTbl="tblCodeDeptList";
				}else if (tblname=="PARANKCODE"){
					codeTbl="tblCodeRankList";
				}else if (tblname=="PACATGTBL"){
					codeTbl="tblCodeCatgList";
				}else if (tblname=="PAEXPFTBL"){
					codeTbl="tblCodeExpfList";
				}else if (tblname=="PARNK4TBL"){
					codeTbl="tblCodeRnk4List";
				}else if (tblname=="PABANK"){
					codeTbl="tblBankDtList";
				}else if (tblname=="PAPAYITM"){
					codeTbl="tblPayItmList";
				}
				else if (tblname=="PATAXTBL"){
					codeTbl="tblTaxTblList";
					$("#txtKEY1").show();
				}else if (tblname=="PATITLE"){
					codeTbl="tblCodeTitList";
				}
			}
			var txtKEY = $('#txtKEY').val().toUpperCase();
			var code_data="";
			if (tblname=="PATAXTBL"){
				code_data={
	        		"sallow" :txtKEY,
	        		"kindred":$('#txtKEY1').val()
	        	};
			}else if (tblname=="PATITLE"){
				if ($('#titsubKey').val()==null){
					
				}else
					key1=$('#titsubKey').val();
					
				if ($('#coddelKey').val()==null){
					
				}else
					key2=$('#coddelKey').val();
				code_data={
	        		"paytitlk" :txtKEY,
	        		"patitsub":key1,
	        		"pacoddel":key2
	        	};
			}else if (tblname=="PAPAYITM"){
				if ($('#coddelKey').val()==null){
					
				}else
					key1=$('#coddelKey').val();
				code_data={
	        		"paycode" :txtKEY,
	        		"pacoddel":key1
	        	};
			}
			else{
				code_data={
	        		"key":txtKEY
	        	};
			}
			console.log(code_data);	
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
					
					if (selCode=="1"){	//發薪日及入帳日對應檔 
						oTable = $('#' + codeTbl ).dataTable(objCopyH);					
					}else if (selCode=="2"){	//單位代碼codeDeptList
						oTable = $('#' + codeTbl ).dataTable(objCopyC);						
					}else if (selCode=="3"){	//職稱代碼codeTitList
						oTable = $('#' + codeTbl ).dataTable(objCopyK);						
					}else if (selCode=="4"){	//4.階別代碼tblCodeRankList
						oTable = $('#' + codeTbl ).dataTable(objCopyD);						
					}else if (selCode=="5"){	//5.人工造冊應計轉帳項目代碼 
						oTable = $('#' + codeTbl ).dataTable(objCopyE);
					}else if (selCode=="6"){	//6薪資所得扣繳稅額表
						oTable = $('#' + codeTbl ).dataTable(objCopyI);						
					}else if (selCode=="7"){	//7支扣項目代碼
						oTable = $('#' + codeTbl ).dataTable(objCopyJ);						
					}else if (selCode=="8"){	//8績效額外點數對照表  
						oTable = $('#' + codeTbl ).dataTable(objCopyF);						
					}else if (selCode=="9"){	//9適用專業加給對照表 
						oTable = $('#' + codeTbl ).dataTable(objCopyG);						
					}else if (selCode=="10"){	//10異動代碼
						oTable = $('#' + codeTbl ).dataTable(objCopyB);						
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
		}
		else if(id == 'btn_qryInsu'){		//保費對照檔_查詢			
			selCode = $('#qryInsutyp').val().split(':')[0];
			tblname=$("#qryInsutyp option:selected").val().split(':')[1];
			console.log("tblname:"+tblname+" selcode:"+selCode+" codeTbl:"+codeTbl);
			$.ajax({
				url:"${pageContext.request.contextPath}/salController.do",
				data:{
					method  : 'qryCodeList',
					hid     : pahid,
					userid  : paUserid,
					selCode  :selCode,
					tblname  :tblname
				},
				type:"POST",	//type:POST, GET(default:GET)
				dataType:"json",	//dataType: text, xml, html, script, json, jsonp
				contentType: "application/x-www-form-urlencoded;charset=utf-8",  
				success:function(data) {
					//更新查詢清單
					$(".dataTables_wrapper").hide();	//把全部的datatables隱藏起來
					$('#'+codeTbl).show();
					
					if (selCode=="1"){	//健保 
						oTable = $('#' + codeTbl ).dataTable(objInsuA);					
					}else if (selCode=="2"){	//勞保
						oTable = $('#' + codeTbl ).dataTable(objInsuB);						
					}else if (selCode=="3"){	//勞退
						oTable = $('#' + codeTbl ).dataTable(objInsuC);						
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
		}
		else if(id == 'btn_qryItems'){		//支扣數_查詢			
			selCode = $('#qryItemstyp').val().split(':')[0];
			tblname=$("#qryItemstyp option:selected").val().split(':')[1];
			var itemsKEY = $('#items_txtKEY').val().toUpperCase();
			var itemsKEY1 = $('#items_txtKEY1').val().toUpperCase();
			var itemsKEY2 = $('#items_txtKEY2').val().toUpperCase();
			var code_data="";
			if (tblname=="PAPOSI"){
				code_data={
	        		"payposik" :itemsKEY,
	        		"degrlow"  :itemsKEY1
	        	};
			}else if (tblname=="PARANK"){
				code_data={
	        		"payrankv" :itemsKEY,
	        		"paylev1v" :itemsKEY1,
	        		"paylev2v" :itemsKEY2,
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
					
					if (selCode=="1"){	//階級支扣
						oTable = $('#' + codeTbl ).dataTable(objItemsB);					
					}else if (selCode=="2"){	//職等支扣--專業加給及主管加給						
						oTable = $('#' + codeTbl ).dataTable(objItemsA);						
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
		}
		else if(id == 'btn_qryWkpf'){		//工作獎金_查詢			
			selCode = $('#qryWkpftyp').val().split(':')[0];
			tblname=$("#qryWkpftyp option:selected").val().split(':')[1];
			var txtKEY = $('#Wkpf_txtKEY').val().toUpperCase();
			var blnWkpf=true;
			if (selCode=="1"){		//工作獎金績分,強制要有值才能查詢 						
				if (txtKEY==""){
					txtKEY = $('#cspf_jt').val();
				}	
				if (txtKEY==""){
					//alert("請輸入查詢項,不得為空白")
					blnWkpf=false;
				}							
			}
			if (blnWkpf){					
				var code_data="";
				code_data={
		        		"key":txtKEY
		        	};
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
						
						if (selCode=="2"){	//全院績效彙總 
							oTable = $('#' + codeTbl ).dataTable(objWkpfA);					
						}else if (selCode=="1"){	//工作獎金績分 						
							oTable = $('#' + codeTbl ).dataTable(objWkpfB);						
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
			}				
		}
		else if(id=='btn_addcodeInsu'){					//保費對照檔_新增 啟動dialog
 			if (typeof(selCode)=="undefined" || '' == selCode){
				selCode="1";
			}
			$("#btn_clearInsu").trigger( "click" );
			$("#btn_subTblInsuIns").prop("disabled", false);
			$("#btn_subTblInsuUpd").prop("disabled", true);
			$(".tblEditgrp").hide();
			if (typeof(selCode)=="undefined"||'' == selCode||'1' == selCode){
				$("#model_insu_header_title").html("新增[健保參照檔]");
				$("#PADPINTBL").show();				 				
				$("#dpinlow").attr("readOnly",false);  //將key.readOnly 取消
			}else if ('2' == selCode){
				$("#model_insu_header_title").html("新增[勞保參照檔]");
				$("#PALBINTBL").show();
				$("#lbinlow").attr("readOnly",false);  //將key.readOnly 取消												
 			}else if ('3' == selCode){
				$("#model_insu_header_title").html("新增[勞工退休金月提繳工資分級表]");
				$("#PALBRPTBL").show();
				$("#lbrplow").attr("readOnly",false);  //將key.readOnly 取消
 			}
 			tblname=$("#qryInsutyp option:selected").val().split(':')[1];
		}
		else if(id=='btn_addcodeItems'){					//支扣項目_新增 啟動dialog
 			if (typeof(selCode)=="undefined" || '' == selCode){
				selCode="1";
			}
			$("#btn_clearItems").trigger( "click" );
			$("#btn_subTblItemsIns").prop("disabled", false);
			$("#btn_subTblItemsUpd").prop("disabled", true);
			$(".tblEditgrp").hide();
			if (typeof(selCode)=="undefined"||'' == selCode||'1' == selCode){
				$("#model_Items_header_title").html("新增[職等支扣--專業加給及主管加給]");
				$("#PAPOSI").show();				 				
				$("#payposik,#degrlow").attr("readOnly",false);  //將key.readOnly 取消
			}else if ('2' == selCode){
				$("#model_Items_header_title").html("新增[階級支扣]");
				$("#PARANK").show();
				$("#paylev1v,#paylev2v,#payrankv").attr("readOnly",false);  //將key.readOnly 取消
				$('#payrankv').selectpicker('val', []);	
				$("#paybasev,#paygeinf,#paygeinn,#paylbinf,#paydpinv,#payrebef,#payreben").val("0");											
 			}
 			tblname=$("#qryItemstyp option:selected").val().split(':')[1];
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
				$("#model_Wkpf_header_title").html("新增[工作獎金績分 ]");
				$("#PACSPF").show();
				$("#pacskey").attr("readOnly",false);  //將key.readOnly 取消				
			}else if ('2' == selCode){
				$("#model_Wkpf_header_title").html("新增[新增[全院績效彙總]]");
				$("#PAPFTL").show();
				$("#paypfmon").attr("readOnly",false);  //將key.readOnly 取消
 			}
 			tblname=$("#qryWkpftyp option:selected").val().split(':')[1];
		}
		else if(id=='btn_addcode'){					//代碼維護_新增 啟動dialog
 			if (typeof(selCode)=="undefined" || '' == selCode){
				selCode="1";
			}
			$("#btn_clear").trigger( "click" );
			$("#btn_subTblCodeIns").prop("disabled", false);
			$("#btn_subTblCodeUpd").prop("disabled", true);
			$(".tblEditgrp").hide();
			if (typeof(selCode)=="undefined"||'' == selCode||'1' == selCode){
				$("#model_header_title").html("新增[發薪日及入帳日對應檔]");
				$("#PABANK").show();
				$("#salnum").attr("readOnly",false);  //將key.readOnly 取消				
			}else if ('2' == selCode){
				$("#model_header_title").html("新增[單位]");
				$("#PADEPT").show();
				$("#padepcod").attr("readOnly",false);  //將key.readOnly 取消				
 			}else if ('3' == selCode){
				$("#model_header_title").html("新增[職稱]");
				$("#PATITLE").show();
				$("#paytitlk").attr("readOnly",false);  //將key.readOnly 取消				
				$('.selectpicker').selectpicker('val', [1]);
 			}else if ('7' == selCode){
				$("#model_header_title").html("新增[支扣項目]");
				$("#PAPAYITM").show();
				$("#paycode").attr("readOnly",false);  //將key.readOnly 取消				
				$('.selectpicker').selectpicker('val', [1]);
 			}else if ('4' == selCode){
				$("#model_header_title").html("新增[階別]");
				$("#PARANKCODE").show();
				$("#rankcode").attr("readOnly",false);  //將key.readOnly 取消				
 			}else if ('5' == selCode){
				$("#model_header_title").html("新增[人工造冊應計轉帳項目]");
				$("#PACATGTBL").show();
				$("#paycatg").attr("readOnly",false);  //將key.readOnly 取消				
 			}else if ('9' == selCode){
				$("#model_header_title").html("新增[適用專業加給對照表]");
				$("#PARNK4TBL").show();				
				$("#rnk4key").attr("readOnly",false);  //將key.readOnly 取消
 			}else if ('6' == selCode){
				$("#model_header_title").html("新增[薪資所得扣繳稅額表]");
				$("#PATAXTBL").show();
				$("#sallow").attr("readOnly",false);  //將key.readOnly 取消				
 			}else if ('10' == selCode){
				$("#model_header_title").html("新增[異動]");
				$("#PATRACODE").show();
				$("#tracode").attr("readOnly",false);  //將key.readOnly 取消								
 			}else if ('8' == selCode){
				$("#model_header_title").html("新增[績效額外點數對照表]");
				$("#PAEXPFTBL").show();
				$("#expfkey").attr("readOnly",false);  //將key.readOnly 取消				
 			}
 			tblname=$("#qrycodetyp option:selected").val().split(':')[1];
		}
		else if(id=='btn_subTblInsuIns'||id=='btn_subTblInsuUpd'){					//保費參考檔 新增一筆 更改
			tblname = $('#qryInsutyp').val().split(':')[1];
			if ($("#subsalInsuForm").valid()){
				msg = $.checkFields(id);
				if(msg == ''){
					var jsonString = $.getCodeInfo(tblname); //取得目前的資料
					if (id =='btn_subTblInsuUpd')		
						$('#insuitem').val("update");
					else if (id =='btn_subTblInsuIns')		
						$('#insuitem').val("insert");
						
					oTable = $('#' + codeTbl ).dataTable().api();
					uTable = $('#' + codeTbl ).dataTable();
					info = oTable.page.info();		//記錄目前頁碼
					if (info!=null||typeof(info)!="undefined"){
						currentPage=info.page;
					}
					$.ajax({
						url:"${pageContext.request.contextPath}/salController.do",
						data:{
							method  : 'updTblCode',
							tabJson : jsonString,
							hid     : pahid,
							userid  : paUserid,
							item    : $('#insuitem').val()
						},
						type:"POST",			//type:POST, GET(default:GET)
						dataType:"json",	//dataType: text, xml, html, script, json, jsonp
						async: true,	//逐步執行
						contentType: "application/x-www-form-urlencoded;charset=utf-8",  
						success:function(data) {
							if (data.status!=null||typeof(data.status)!="undefined"){	//show msg,dialog不用關閉
								alert(data.msg);
							}else{
								$("#addInsuDialog").modal("hide");
								uTable.fnClearTable(0);		//清空資料
								
								$("#btn_qryInsu").trigger( "click" );
								if (currentPage!=null||typeof(currentPage)!="undefined"){  
									uTable.fnPageChange(currentPage);		//切到原先某碼
								}
								if (data.msg!=null){
									alert(data.msg);
								}
							}	
						},
						error: function(xhr){					
							alert("保費參考檔"+$('#insuitem').val()+"失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
						}
					});
				}else{
					alert(msg);
					return false;
				}
			}
		}
		else if(id=='btn_subTblItemsIns'||id=='btn_subTblItemsUpd'){					//支扣數 新增一筆 更改
			tblname = $('#qryItemstyp').val().split(':')[1];
			if ($("#subsalItemsForm").valid()){
				msg = $.checkFields(id);
				if(msg == ''){
					var jsonString = $.getCodeInfo(tblname); //取得目前的資料
					if (id =='btn_subTblItemsUpd')		
						$('#Itemsitem').val("update");
					else if (id =='btn_subTblItemsIns')		
						$('#Itemsitem').val("insert");
						
					oTable = $('#' + codeTbl ).dataTable().api();
					uTable = $('#' + codeTbl ).dataTable();
					info = oTable.page.info();		//記錄目前頁碼
					if (info!=null||typeof(info)!="undefined"){	
						currentPage=info.page;
					}
					$.ajax({
						url:"${pageContext.request.contextPath}/salController.do",
						data:{
							method  : 'updTblCode',
							tabJson : jsonString,
							hid     : pahid,
							userid  : paUserid,
							item    : $('#Itemsitem').val()
						},
						type:"POST",			//type:POST, GET(default:GET)
						dataType:"json",	//dataType: text, xml, html, script, json, jsonp
						async: true,	//逐步執行
						contentType: "application/x-www-form-urlencoded;charset=utf-8",  
						success:function(data) {
							if (data.status!=null||typeof(data.status)!="undefined"){	//show msg,dialog不用關閉
								alert(data.msg);
							}else{
								$("#addItemsDialog").modal("hide");
								uTable.fnClearTable(0);		//清空資料
								
								$("#btn_qryItems").trigger( "click" );
								if (currentPage!=null||typeof(currentPage)!="undefined"){  
									uTable.fnPageChange(currentPage);		//切到原先某碼
								}
								if (data.msg!=null){
									alert(data.msg);
								}
							}	
						},
						error: function(xhr){					
							alert("支扣項目"+$('#Itemsitem').val()+"失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
						}
					});
				}else{
					alert(msg);
					return false;
				}
			}
		}
		else if(id=='btn_subTblWkpfIns'||id=='btn_subTblWkpfUpd'){					//工作獎金 新增一筆 更改
			tblname = $('#qryWkpftyp').val().split(':')[1];
			if ($("#subsalWkpfForm").valid()){
				msg = $.checkFields(id);
				if(msg == ''){
					var jsonString = $.getCodeInfo(tblname); //取得目前的資料
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
								$("#addWkpfDialog").modal("hide");
								uTable.fnClearTable(0);		//清空資料
								
								$("#btn_qryWkpf").trigger( "click" );
								if (currentPage!=null||typeof(currentPage)!="undefined"){	  
									uTable.fnPageChange(currentPage);		//切到原先某碼
								}
								if (data.msg!=null){
									alert(data.msg);
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
		else if(id=='btn_subTblCodeIns'||id=='btn_subTblCodeUpd'){					//代碼維護 新增一筆 更改
			tblname = $('#qrycodetyp').val().split(':')[1];
			if ($("#subsalCodeForm").valid()){
				msg = $.checkFields(id);
				if(msg == ''){
					var jsonString = $.getCodeInfo(tblname); //取得目前的資料
					if (id =='btn_subTblCodeUpd')		
						$('#citem').val("update");
					else if (id =='btn_subTblCodeIns')		
						$('#citem').val("insert");
						
					oTable = $('#' + codeTbl ).dataTable().api();
					uTable = $('#' + codeTbl ).dataTable();
					info = oTable.page.info();		//記錄目前頁碼
					if (info!=null||typeof(info)!="undefined"){
						currentPage=info.page;
					}
					$.ajax({
						url:"${pageContext.request.contextPath}/salController.do",
						data:{
							method  : 'updTblCode',
							tabJson : jsonString,
							hid     : pahid,
							userid  : paUserid,
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
								if (data.msg!=null){
									alert(data.msg);
								}
								$("#addCodeDialog").modal("hide");
								uTable.fnClearTable(0);		//清空資料
								
								$("#btn_qryCode").trigger( "click" );
								if (currentPage!=null||typeof(currentPage)!="undefined"){  
									uTable.fnPageChange(currentPage);		//切到原先某碼
								}
							}	
						},
						error: function(xhr){					
							alert("代碼維護"+$('#citem').val()+"失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
						}
					});
				}else{
					alert(msg);
					return false;
				}
			}
		}
			
	});
	
	//讀取table欄位內容
  	$.getCodeInfo = function(tblname){
		var array = new Array();
		var value = [];
		var obj = {};
		var $th = $('#'+ tblname + ' th');	
		var data_val;
		var data_id;	 

			$( ':input', '#'+tblname).each(function() {
		        var name = $(this).attr("name");
	    		data_id = String($(this).attr("id"));
	    		//欄名有重名,加tableID區分,取id ex:F_pabank_hid 取hid 
	    		if (data_id.substring(0,2)=="F_"){
	    			data_id=data_id.substring(data_id.indexOf( '_', 2 )+1);
	    		}
	    		data_val = $(this).val();
// 	    		var tableID = $(this).closest('table').attr('id'); // 表格名稱
// 	    		var result = {"tableID": tableID, "value": val};
	    		if (typeof data_id === "undefined" || data_id == "undefined") {
		        	
		        }else{
		        	if ( null == data_val ) data_val = "";
		        	if (tblname=="PARANKCODE"||tblname=="PAPOSI") data_val=data_val.toUpperCase(); 
		        	obj[data_id] = data_val; //名稱+數值	
		        }
		    });
		    
		      
/*		    $('#' + tblname + ' tbody tr').each(function(i, tr){
	   			var $tds = $(tr).find('td');
			    $th.each(function(index, th){
// 			    	var indexName = (index.toString().length==1) ? "0"+(index) : (index);
			        data_val = $tds.eq(index).children('input:text,input:radio:checked, select , textarea').val();
			        if (tblname=="PARANKCODE") data_val=data_val.toUpperCase();
 			        data_id = $tds.eq(index).children('input:text,input:radio:checked, select, textarea').attr('id');
 			        
//			        console.log("id:"+data_id+" val:"+data_val);
			        if (typeof data_id === "undefined") {
			        	$tr.each(function(k, td){
			        		console.log(td.attr('id'));			        		
			        	});
			        	
			        }else{
			        	if ( null == data_val ) data_val = ""; 
			        	obj[data_id] = data_val; //名稱+數值	
			        }			        
			    });
			    
	   		});*/	
	   		value.push(obj);   	//以整支table寫入	
	   		array.push( { "formnm": tblname, "value": value } ); //將動態表格資料轉換為json		    
		    
// 		}	
  		var jsonArray = JSON.stringify(array); // 轉換成json
	    	    
	    console.log(jsonArray);
	    alert("資料送出");
	    
	    return jsonArray;
	};
	//編輯欄位檢查
  	$.checkFields = function(btn){
  		msg = '';
  		if (btn== 'btn_subTblCodeIns'||btn== 'btn_subTblCodeIns'){
  			if (selCode=="1"){	
  				if ($("#salnum").val()=="")
					msg += '[餉單編號]不得為空白 \n\r';
				if ($("#paybank").val()=="")
					msg += '[發薪日期]不得為空白 \n\r';
				if ($("#paysaldt").val()=="")
					msg += '[入帳日期]不得為空白 \n\r';
  			}else if (selCode=="2"){
  				if ($("#padepcod").val()=="")
					msg += '[代碼]不得為空白 \n\r';
				if ($("#pacstno").val()=="")
					msg += '[成本代碼]不得為空白 \n\r';
				if ($("#padepnmc").val()=="")
					msg += '[單位中文]不得為空白 \n\r';
  			}else if (selCode=="3"){
  				if ($("#paytitlk").val()=="")
					msg += '[代碼]不得為空白 \n\r';
				if ($("#patitsub").val()=="")
					msg += '[群組]不得為空白 \n\r';
				if ($("#pachief").val()=="")
					msg += '[主管Y/N]不得為空白 \n\r';
				if ($("#paytitlp").val()=="")
					msg += '[報表用名稱]不得為空白 \n\r';
				if ($("#paytitlc").val()=="")
					msg += '[內部用名稱]不得為空白 \n\r';
				if ($("#pacoddel").val()=="")
					msg += '[刪除Y/N]不得為空白 \n\r';			
  			}else if (selCode=="7"){
  				if ($("#paycode").val()=="")
					msg += '[代碼]不得為空白 \n\r';
				if ($("#pacodnmc").val()=="")
					msg += '[名稱]不得為空白 \n\r';
				if ($("#papayitm_pacoddel").val()=="")
					msg += '[刪除Y/N]不得為空白 \n\r';	
				if ($("#payiftax").val()=="")
					msg += '[列入所得Y/N]不得為空白 \n\r';
				if ($("#paupbas").val()=="")
					msg += '[更新主檔Y/N]不得為空白 \n\r';
				if ($("#payreward").val()=="")
					msg += '[列5%稅]不得為空白 \n\r';
				if ($("#payauth").val()=="")
					msg += '[二代健保]不得為空白 \n\r';	
							
  			}else if (selCode=="4"){
  				if ($("#rankcode").val()=="")
					msg += '[階別代碼]不得為空白 \n\r';
				if ($("#rankname").val()=="")
					msg += '[階別中文]不得為空白 \n\r';
				if ($("#rankabbr").val()=="")
					msg += '[階別簡稱]不得為空白 \n\r';
  			}else if (selCode=="5"){
  				if ($("#paycatg").val()=="")
					msg += '[轉帳項目代碼]不得為空白 \n\r';
				if ($("#catgname").val()=="")
					msg += '[轉帳項目中文]不得為空白 \n\r';				
  			}else if (selCode=="7"){
  				if ($("#expfkey").val()=="")
					msg += '[績效額外點數代碼]不得為空白 \n\r';
				if ($("#expfname").val()=="")
					msg += '[績效額外點數中文]不得為空白 \n\r';				
  			}else if (selCode=="9"){
  				if ($("#rnk4key").val()=="")
					msg += '[適用專業加給對照代碼]不得為空白 \n\r';
				if ($("#rnk4name").val()=="")
					msg += '[適用專業加給對照中文]不得為空白 \n\r';				
  			}else if (selCode=="10"){	//異動代碼
  				if ($("#tracode").val()=="")
					msg += '[異動代碼]不得為空白 \n\r';
				if ($("#traname").val()=="")
					msg += '[異動代碼中文]不得為空白 \n\r';
  			}else if (selCode=="6"){	//薪資所得扣繳稅額表
  				if ($("#sallow").val()=="")
					msg += '[最低薪資所得]不得為空白 \n\r';
				if ($("#salhigh").val()=="")
					msg += '[最高薪資所得]不得為空白 \n\r';
				if ($("#kindred").val()=="")
					msg += '[扶養人數]不得為空白 \n\r';
				if ($("#taxamt").val()=="")
					msg += '[扣繳稅額]不得為空白 \n\r';	
  			}
  			else if (btn== 'btn_subTblInsuIns'||id=='btn_subTblInsuUpd'){//保費參考 新增一筆 更改
	  			if (selCode=="1"){	
	  				if ($("#dpinlow").val()=="")
						msg += '[最低月投保金額]不得為空白 \n\r';
					if ($("#dpinhigh").val()=="")
						msg += '[最高月投保金額]不得為空白 \n\r';
					if ($("#dpinamt").val()=="")
						msg += '[健保費]不得為空白 \n\r';
					if ($("#dpindept1").val()=="")
						msg += '[單位負擔公保身份]不得為空白 \n\r';
					if ($("#dpindept3").val()=="")
						msg += '[單位負擔勞保身份]不得為空白 \n\r';
	  			}else if (selCode=="2"){
	  				if ($("#lbinlow").val()=="")
						msg += '[最低月投保金額]不得為空白 \n\r';
					if ($("#lbinhigh").val()=="")
						msg += '[最高月投保金額]不得為空白 \n\r';
					if ($("#lbinamt").val()=="")
						msg += '[勞保費]不得為空白 \n\r';
					if ($("#lbindept").val()=="")
						msg += '[單位負擔]不得為空白 \n\r';
	  			}else if (selCode=="3"){
	  				if ($("#lbrplow").val()=="")
						msg += '[最低實際工資]不得為空白 \n\r';
					if ($("#lbrphigh").val()=="")
						msg += '[最高實際工資]不得為空白 \n\r';
					if ($("#lbrpamt").val()=="")
						msg += '[月提繳工資]不得為空白 \n\r';
					if ($("#masteramt").val()=="")
						msg += '[公提退休金]不得為空白 \n\r';
					if ($("#assocc").val()=="")
						msg += '[職業災害保險費]不得為空白 \n\r';	
	  			}
	  		}
	  		else if (btn== 'btn_subTblItemsIns'||id=='btn_subTblItemsUpd'){//支扣項目 新增一筆 更改
	  			if (selCode=="1"){	//1:PAPOSI">1職等支扣--專業加給及主管加給
	  				if ($("#poposik").val()=="")
						msg += '[職等代碼]不得為空白 \n\r';
					if ($("#degrlow").val()=="")
						msg += '[最低俸點]不得為空白 \n\r';					
	  			}else if (selCode=="2"){	//2:PARANK">2階級支扣
	  				if ($("#paylev1v").val()=="")
						msg += '[級別]不得為空白 \n\r';
					if ($("#paylev2v").val()=="")
						msg += '[俸級]不得為空白 \n\r';
					if ($("#paydegrv").val()=="")
						msg += '[俸點]不得為空白 \n\r';
					if ($("#payrankv").val()=="")
						msg += '[階級]不得為空白 \n\r';
					if ($("#paybasev").val()=="")
						msg += '[本薪]不得為空白 \n\r';	
	  			}
	  		}
	  		else if (btn== 'btn_subTblWkpfUpd'||id=='btn_subTblWkpfUpd'){//工作獎金 新增一筆 更改
	  			if (selCode=="2"){	//2:PAPFTL">2全院績效彙總
	  				if ($("#paypfmon").val()=="")
						msg += '[年度月份]不得為空白 \n\r';
					if ($("#paypfunt").val()=="")
						msg += '[績效點數金額]不得為空白 \n\r';					
	  			}else if (selCode=="1"){	//1:PACSPF">1工作獎金績分 
	  				if ($("#pacskey").val()=="")
						msg += '[工作獎金代碼]不得為空白 \n\r';
					if ($("#pacsbase").val()=="")
						msg += '[基本積分]不得為空白 \n\r';
					if ($("#pacsdata").val()=="")
						msg += '[俸級積分]不得為空白 \n\r';
					if ($("#pacsname").val()=="")
						msg += '[職稱中文]不得為空白 \n\r';
	  			}
	  		}		
  		}	
		return msg;	
	};
	
	//輸入卡號,找出對應姓名
	$(".searchNm").blur(function(){
		var search = $(this);
		var span = $(this).next();
		var hidden = span.next();
		//用正規表達式判斷卡號
	 	reCheckCase = /^\d{4}$|^[A-Za-z]\d{3}$|^$/;
		if(!reCheckCase.test(search.val())){
			alert('輸入的卡號格式有誤，請重新輸入');
			search.val('');
			span.empty();
			hidden.val('');
			return;
		}else if(search.val() == ''){
			search.val('');
			span.empty();
			hidden.val('');
			return;
		}

		$.ajax({
			url:"${pageContext.request.contextPath}/salController.do",
			data:{
				method  : 'searchNm',
				cardno  :search.val()
			},

			type:"POST",		//type:POST, GET(default:GET)
			dataType:"json",	//dataType: text, xml, html, script, json, jsonp
			contentType: "application/x-www-form-urlencoded;charset=utf-8",  
			async:false,
			success:function(data) {
				if (data.namedata[0]!=null){
					span.empty();
					span.append(data.namedata[0].name);
					hidden.val(data.namedata[0].name);
				}else{
					alert("找不到姓名");
	       			search.val('');
					span.empty();
					hidden.val('');
				}
			},
			error: function(xhr){	
				alert("搜尋卡號過程中，程式發生錯誤!!!\nerror code:"+ xhr.status);
			}
		});
	});
		
 	var objCopyA = $.extend(true, {aaSorting: []}, dataTableConfig);
	objCopyA.aoColumns = [	
							{ "mData": "userid"},	//0卡號
							{ "mData": "username"},	//1姓名
// 							{ "mData": "password"},	//2密碼
							{ "mData": "dept"},		//3成本中心代碼
							{ "mData": "authority"},//4權限
							{ "mData": "zoprole"},
							{ "mData": null }
							];
	objCopyA = $.extend(true,objCopyA, {
				 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
						$('td:eq(0)', nRow).html( '<a href = "#opRoleListxx" class= "opRoleListxx">'+aData.userid+'</a>' );
 						$('td:eq(5)', nRow).html( '<button class= "delopRolebtn">刪除</button>');
				 }, "fnDrawCallback": function () {					 
				 	//等datatable draw完成後，補上button 初始化 
				 	//jQuery按鈕圖示，先取消onclick預設的動作
					//刪除	
					$(".delopRolebtn")
						.css({
							  "text-align": "center",
							  "background": "#ffe69e",
							  "font-size": "12px" 
							})
						.click(function(){	
							oTable = $('#opRoleList').dataTable().api();
					        tr = $(this).parents('tr');
					        rowIndex = oTable.row(tr).index();
					        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data
					        
					        $.ajax({
								url:"${pageContext.request.contextPath}/salController.do",
								data:{
										method  : 'delOpRoleDept',
										userid  :rowData.userid
								},
								type:"GET",			//type:POST, GET(default:GET)
								dataType:"json",	//dataType: text, xml, html, script, json, jsonp
								contentType: "application/x-www-form-urlencoded;charset=utf-8",  
								success:function(data) {
									oTable = $('#opRoleList').dataTable();
									oTable.fnDeleteRow(rowIndex);
									$.getOpRoleDeptUI("N");
								},
								error:function(xhr) {
									alert("刪除失敗，程式發生錯誤!!!\nerror code:"+ xhr.status);  
								}
							});
						});	
				 }							
		      });
	objCopyA = $.extend(true,objCopyA, {
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
		buttons: [
            'excelHtml5' 
        ]
	});	      						
	$('#opRoleList').dataTable(objCopyA);
	
	//依userid+dept開啟dialog
	$('#opRoleList tbody').on('click', '.opRoleListxx', function () {
		oTable = $('#opRoleList').dataTable().api();
		info = oTable.page.info();		//記錄目前頁碼
		currentPage=info.page;
        tr = $(this).parents('tr');
        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data	 		
 		$("#bUSERID").val(rowData.userid);
		$("#bUSERNAME_show").html(rowData.username);
		$("#bUSERNAME").val(rowData.username);
		$("#bPASSWORD").val(rowData.password);
		$("#bDEPT").val(rowData.dept);
		$("#bAUTHORITY").val(rowData.authority);
		$("#bUPDID").val(rowData.updid);
		$("#bZOPROLE").val(rowData.zoprole);
		$("#opRoleDialog").show();
		$("#opRoleDialog").dialog("open");
		$("#subopRoleInsbtn").prop("disabled", true);
		$("#subopRoleUpdbtn").prop("disabled", false);
		$("#bUSERID").prop('readOnly', true) ;
		$("#bDEPT").prop("disabled", true);
	});
	//角色設定Dialog
	$('#opRoleDialog').dialog({
		title:"角色設定",
		autoOpen: false,
		modal: true,
		resizable: true,
		width: 450,
		height: 350,
		buttons: {
            "關閉": function() {                      
                $(this).dialog("close");                    
            }
        } 
	});
	
	var objCopyB = $.extend(true, {aaSorting: []}, dataTableConfig);
	objCopyB.aoColumns = [	
							{ "sTitle":"","mData": null},
							{ "sTitle":"異動代碼","mData": "tracode"},	//0異動代碼,"width":"20%"
							{ "sTitle":"異動中文","mData": "traname"}		//1異動中文"className": "edit"
							];
	objCopyB = $.extend(true,objCopyB, {
				 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
				 		$('td:eq(0)', nRow).html( '<button type="button" class="edit btn btn-primary" data-toggle="modal"><i class="fa fa-edit"></i></button>&emsp;<button type="button" class="delCodebtn btn btn-danger"><i class="fa fa-minus"></i></button>' );
				 }, "fnDrawCallback": function () {					 
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
						        if (tblname=="PATRACODE"){
						        	code_data={
						        		"key":rowData.tracode
						        	};
						        }
						   		    
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
					        $("#tracode").val(rowData.tracode);
							$("#traname").val(rowData.traname);
							$("#model_header_title").html("修改[異動]");
							$(".tblEditgrp").hide();
							$("#tracode").attr("readOnly",true);	//將key設成readOnly
							$("#PATRACODE").show();
							$("label.error").hide();
 							$(".error").removeClass("error");
					        $("#addCodeDialog").modal("show");
							$("#btn_subTblCodeIns").prop("disabled", true);
							$("#btn_subTblCodeUpd").prop("disabled", false);
						});	
				 }							
		      });
    objCopyB = $.extend(true,objCopyB, {
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
		buttons: [
            'excelHtml5' 
        ]
	});	
	//單位  
    var objCopyC = $.extend(true, {aaSorting: []}, dataTableConfig);
	objCopyC.aoColumns = [	
							{ "sTitle":"", "mData": null},
							{ "sTitle":"單位代碼", "mData": "padepcod"},		//"width":"20%"
							{ "sTitle":"成本碼", "mData": "pacstno"},		//className": "edit"
							{ "sTitle":"單位中文", "mData": "padepnmc"},
							{ "sTitle":"人事代碼","mData": "pbwkcode"},
							{ "sTitle":"醫院別","mData": "hid"},
							{ "sTitle":"更新者","mData": "payupdid"},
							{ "sTitle":"時戳","mData": "payupdat"}
							];
	objCopyC = $.extend(true,objCopyC, {
				 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
				 		$('td:eq(0)', nRow).html( '<button type="button" class="edit btn btn-primary" data-toggle="modal"><i class="fa fa-edit"></i></button>&emsp;<button type="button" class="delCodebtn btn btn-danger"><i class="fa fa-minus"></i></button>' );
				 }, "fnDrawCallback": function () {					 
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
						   		code_data={
						        	"key":rowData.padepcod
						        }    
						        $.ajax({
									url:"${pageContext.request.contextPath}/salController.do",
									data:{
											method  : 'delTblCode',
											hid     : pahid,
											tblname : 'PADEPT',
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
					        $("#padepcod").val(rowData.padepcod);
							$("#pacstno").val(rowData.pacstno);
							$("#padepnmc").val(rowData.padepnmc);
							$("#pbwkcode").val(rowData.pbwkcode);
							$("#F_padept_hid").val(rowData.hid);
							$("#paydept_payupdid").val(rowData.payupdid);
							$("#paydept_payupdat").val(rowData.payupdat);
							$("#model_header_title").html("修改[單位]");
							$(".tblEditgrp").hide();
							$("#padepcod").attr("readOnly",true);	//將key設成readOnly
							$("#PADEPT").show();
							$("label.error").hide();
 							$(".error").removeClass("error");
					        $("#addCodeDialog").modal("show");
							$("#btn_subTblCodeIns").prop("disabled", true);
							$("#btn_subTblCodeUpd").prop("disabled", false);
						});		
				 }							
		      });
    objCopyC = $.extend(true,objCopyC, {
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
		buttons: [
            'excelHtml5' 
        ]
	});
	
	//階別代碼
	var objCopyD = $.extend(true, {aaSorting: []}, dataTableConfig);
	objCopyD.aoColumns = [	
							{ "sTitle":"","mData": null},
							{ "sTitle":"階別代碼","mData": "rankcode"},		
							{ "sTitle":"中文說明","mData": "rankname"},		
							{ "sTitle":"階別簡稱","mData": "rankabbr"}		
							
							];
	objCopyD = $.extend(true,objCopyD, {
				 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
// 						$('td:eq(0)', nRow).html( '<a href = "#tblCodeList" class= "tblCodeList">'+aData.id+'</a>' );
 						$('td:eq(0)', nRow).html( '<button type="button" class="edit btn btn-primary" data-toggle="modal"><i class="fa fa-edit"></i></button>&emsp;<button type="button" class="delCodebtn btn btn-danger"><i class="fa fa-trash-alt"></i></button>');
				 }, "fnDrawCallback": function () {					 
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
						        if (tblname=="PARANKCODE"){
						        	code_data={
						        		"key":rowData.rankcode
						        	};
						        }
						   		    
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
					        $("#rankcode").val(rowData.rankcode);
							$("#rankname").val(rowData.rankname);
							$("#rankabbr").val(rowData.rankabbr);
							$("#model_header_title").html("修改[階別]");
							$(".tblEditgrp").hide();
							$("#rankcode").attr("readOnly",true);	//將key設成readOnly
							$("#PARANKCODE").show();
							$("label.error").hide();
 							$(".error").removeClass("error");
					        $("#addCodeDialog").modal("show");
							$("#btn_subTblCodeIns").prop("disabled", true);
							$("#btn_subTblCodeUpd").prop("disabled", false);
						});	
				 }							
		      });
  	//轉帳項目代碼
  	var objCopyE = $.extend(true, {aaSorting: []}, dataTableConfig);
	objCopyE.aoColumns = [	
							{ "sTitle":"", "mData": null},
							{ "sTitle":"轉帳項目代碼", "mData": "paycatg"},		
							{ "sTitle":"中文說明", "mData": "catgname"},		
							{ "sTitle":"更新者", "mData": "payupdid"},
							{ "sTitle":"時戳", "mData": "payupdat"},		
							
							];
	objCopyE = $.extend(true,objCopyE, {
				 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
 						$('td:eq(0)', nRow).html( '<button type="button" class="edit btn btn-primary" data-toggle="modal"><i class="fa fa-edit"></i></button>&emsp;<button type="button" class="delCodebtn btn btn-danger"><i class="fa fa-trash-alt"></i></button>');
				 }, "fnDrawCallback": function () {					 
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
						        if (tblname=="PACATGTBL"){
						        	code_data={
						        		"key":rowData.paycatg
						        	};
						        }
						   		    
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
					        $("#paycatg").val(rowData.paycatg);
							$("#catgname").val(rowData.catgname);
							$("#pacatgtbl_payupdid").val(rowData.payupdid);
							$("#pacatgtbl_payupdat").val(rowData.payupdat);							
							$("#model_header_title").html("修改[人工造冊應計轉帳項目]");
							$(".tblEditgrp").hide();
							$("#PACATGTBL").show();
							$("#paycatg").attr("readOnly",true);	//將key設成readOnly
							$("label.error").hide();
 							$(".error").removeClass("error");
					        $("#addCodeDialog").modal("show");
							$("#btn_subTblCodeIns").prop("disabled", true);
							$("#btn_subTblCodeUpd").prop("disabled", false);
						});	
				 }							
		      });
	//績效額外點數代碼
	var objCopyF = $.extend(true, {aaSorting: []}, dataTableConfig);
	objCopyF.aoColumns = [	
							{ "sTitle":"", "mData": null},
							{ "sTitle":"績效額外點數代碼", "mData": "expfkey"},		
							{ "sTitle":"中文說明", "mData": "expfname"},		
							{ "sTitle":"更新者", "mData": "payupdid"},
							{ "sTitle":"時戳", "mData": "payupdat"},
							];
	objCopyF = $.extend(true,objCopyF, {
				 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
 						$('td:eq(0)', nRow).html( '<button type="button" class="edit btn btn-primary" data-toggle="modal"><i class="fa fa-edit"></i></button>&emsp;<button type="button" class="delCodebtn btn btn-danger"><i class="fa fa-trash-alt"></i></button>');
				 }, "fnDrawCallback": function () {					 
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
						        if (tblname=="PAEXPFTBL"){
						        	code_data={
						        		"key":rowData.expfkey
						        	};
						        }
						   		    
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
					        $("#expfkey").val(rowData.expfkey);
							$("#expfname").val(rowData.expfname);
							$("#paexpftbl_payupdid").val(rowData.payupdid);
							$("#paexpftbl_payupdat").val(rowData.payupdat);
							$("#model_header_title").html("修改[績效額外點數對照表 ]");
							$(".tblEditgrp").hide();
							$("#PAEXPFTBL").show();
							$("#expfkey").attr("readOnly",true);	//將key設成readOnly
							$("label.error").hide();
 							$(".error").removeClass("error");
					        $("#addCodeDialog").modal("show");
							$("#btn_subTblCodeIns").prop("disabled", true);
							$("#btn_subTblCodeUpd").prop("disabled", false);
						});	
				 }							
		      });
	//適用專業加給對照表
	var objCopyG = $.extend(true, {aaSorting: []}, dataTableConfig);
	objCopyG.aoColumns = [	
							{ "sTitle":"","mData": null},
							{ "sTitle":"適用專業加給對照表","mData": "rnk4key"},		
							{ "sTitle":"中文說明","mData": "rnk4name"},
							{ "sTitle":"時戳","mData": "payupdtm"}
							];
	objCopyG = $.extend(true,objCopyG, {
				 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
 						$('td:eq(0)', nRow).html( '<button type="button" class="edit btn btn-primary" data-toggle="modal"><i class="fa fa-edit"></i></button>&emsp;<button type="button" class="delCodebtn btn btn-danger"><i class="fa fa-trash-alt"></i></button>');
				 }, "fnDrawCallback": function () {					 
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
						        if (tblname=="PARNK4TBL"){
						        	code_data={
						        		"key":rowData.rnk4key
						        	};
						        }
						   		    
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
					        $("#rnk4key").val(rowData.rnk4key);
							$("#rnk4name").val(rowData.rnk4name);
							$("#parnk4tbl_payupdtm").val(rowData.payupdtm);
							$("#model_header_title").html("修改[適用專業加給對照表]");
							$(".tblEditgrp").hide();
							$("#PARNK4TBL").show();
							$("#rnk4key").attr("readOnly",true);	//將key設成readOnly
							$("label.error").hide();
 							$(".error").removeClass("error");
					        $("#addCodeDialog").modal("show");
							$("#btn_subTblCodeIns").prop("disabled", true);
							$("#btn_subTblCodeUpd").prop("disabled", false);
						});	
				 }							
		      });
	//發薪日及入帳日對應檔	      
	var objCopyH = $.extend(true, {aaSorting: []}, dataTableConfig);
	objCopyH.aoColumns = [	
							{ "sTitle":"","mData": null},
							{ "sTitle":"餉單編號","mData": "salnum"},		
							{ "sTitle":"發薪日期","mData": "paybank"},
							{ "sTitle":"入帳日期","mData": "paysaldt"},
							{ "sTitle":"醫院別","mData": "hid"},		
							{ "sTitle":"更新者","mData": "payupdid"},
							{ "sTitle":"時戳","mData": "payupdat"}
							];
	objCopyH = $.extend(true,objCopyH, {
				 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
				 	$('td:eq(0)', nRow).html( '<button type="button" class="edit btn btn-primary roleQry" data-toggle="modal"><i class="fa fa-edit"></i></button>&emsp;<button type="button" class="delCodebtn btn btn-danger"><i class="fa fa-trash-alt"></i></button>'); 						
				 }, "fnDrawCallback": function () {					 
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
						        if (tblname=="PABANK"){			
						        	code_data={
						        		"key":rowData.salnum
						        	};
						        }
						   		    
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
					        $("#salnum").val(rowData.salnum);
							$("#paybank").val(rowData.paybank);
							$("#paysaldt").val(rowData.paysaldt);
							$("#F_pabank_hid").val(rowData.hid);
							$("#pabank_payupdid").val(rowData.payupdid);
							$("#pabank_payupdat").val(rowData.payupdat);
							$("#model_header_title").html("修改[發薪日及入帳日對應檔 ]");
							$(".tblEditgrp").hide();
							$("#PABANK").show();		//把要顯示的table show
							$("#salnum").attr("readOnly",true);	//將key設成readOnly
							$("label.error").hide();
 							$(".error").removeClass("error");
					        $("#addCodeDialog").modal("show");
							$("#btn_subTblCodeIns").prop("disabled", true);
							$("#btn_subTblCodeUpd").prop("disabled", false);
						});	
				 	}							
		      });
    //薪資所得扣繳稅額表 
	var objCopyI = $.extend(true, {aaSorting: []}, dataTableConfig);
	objCopyI.aoColumns = [	
							{ "sTitle":"","mData": null},
							{ "sTitle":"最低薪資所得","mData": "sallow"},		
							{ "sTitle":"最高薪資所得","mData": "salhigh"},
							{ "sTitle":"扶養人數","mData": "kindred"},
							{ "sTitle":"扣繳稅額","mData": "taxamt"},		
							{ "sTitle":"更新者","mData": "payupdid"},
							{ "sTitle":"時戳","mData": "payupdat"}
							];
	objCopyI = $.extend(true,objCopyI, {
				 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
 						$('td:eq(0)', nRow).html( '<button type="button" class="edit btn btn-primary" data-toggle="modal"><i class="fa fa-edit"></i></button>&emsp;<button type="button" class="delCodebtn btn btn-danger"><i class="fa fa-trash-alt"></i></button>');
				 }, "fnDrawCallback": function () {					 
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
						        if (tblname=="PATAXTBL"){			
						        	code_data={
						        		"sallow":rowData.sallow,
						        		"kindred":rowData.kindred
						        	};
						        }
						   		    
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
					        $("#sallow").val(rowData.sallow);
							$("#salhigh").val(rowData.salhigh);
							$("#kindred").val(rowData.kindred);
							$("#taxamt").val(rowData.taxamt);
							$("#pataxtbl_payupdid").val(rowData.payupdid);
							$("#pataxtbl_payupdat").val(rowData.payupdat);
							$("#model_header_title").html("修改[薪資所得扣繳稅額表  ]");
							$(".tblEditgrp").hide();
							$("#PATAXTBL").show();		//把要顯示的table show
							$("#sallow").attr("readOnly",true);	//將key設成readOnly
							$("label.error").hide();
 							$(".error").removeClass("error");
					        $("#addCodeDialog").modal("show");
							$("#btn_subTblCodeIns").prop("disabled", true);
							$("#btn_subTblCodeUpd").prop("disabled", false);
						});	
				 	}							
		      });
    objCopyI = $.extend(true,objCopyI, {
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
		buttons: [
            'excelHtml5' 
        ]
	});	      
	//7支扣項目代碼 
	var objCopyJ = $.extend(true, {aaSorting: []}, dataTableConfig);
	objCopyJ.aoColumns = [	
							{ "sTitle":"","mData": null},
							{ "sTitle":"代碼","mData": "paycode"},		
							{ "sTitle":"名稱","mData": "pacodnmc"},
							{ "sTitle":"刪除否","mData": "pacoddel"},
							{ "sTitle":"列入所得","mData": "payiftax"},
							{ "sTitle":"更新至主檔","mData": "paupbas"},
							{ "sTitle":"列5%稅","mData": "payreward"},
							{ "sTitle":"二代健保","mData": "payauth"},
							{ "sTitle":"更新者","mData": "payupdid"},
							{ "sTitle":"時戳","mData": "payupdat"}
							];
	objCopyJ.aoColumnDefs= [
							 { "sClass": "alignLeft", "aTargets": [ 2] }							 
						    ];						
	objCopyJ = $.extend(true,objCopyJ, {
				 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
 						$('td:eq(0)', nRow).html( '<button type="button" class="edit btn btn-primary" data-toggle="modal"><i class="fa fa-edit"></i></button>&emsp;<button type="button" class="delCodebtn btn btn-danger"><i class="fa fa-trash-alt"></i></button>');
				 }, "fnDrawCallback": function () {					 
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
						        if (tblname=="PAPAYITM"){			
						        	code_data={
						        		"key":rowData.paycode
						        	};
						        }
						   		    
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
					        $("#paycode").val(rowData.paycode);
							$("#pacodnmc").val(rowData.pacodnmc);
							$("#F_papayitm_pacoddel").selectpicker('val',rowData.pacoddel);
							$("#payiftax").selectpicker('val',rowData.payiftax);
							$("#paupbas").selectpicker('val',rowData.paupbas);
							$("#payreward").selectpicker('val',rowData.payreward);
							$("#payauth").val(rowData.payauth);
							$("#F_papayitm_payupdid").val(rowData.payupdid);
							$("#F_papayitm_payupdat").val(rowData.payupdat);
							$("#F_papayitm_hid").val(rowData.hid);
							$("#model_header_title").html("修改[支扣項目]");
							$(".tblEditgrp").hide();
							$("#PAPAYITM").show();		//把要顯示的table show
							$("#sallow").attr("readOnly",true);	//將key設成readOnly
							$("label.error").hide();
 							$(".error").removeClass("error");
					        $("#addCodeDialog").modal("show");
							$("#btn_subTblCodeIns").prop("disabled", true);
							$("#btn_subTblCodeUpd").prop("disabled", false);
						});	
				 	}							
		      });
    objCopyJ = $.extend(true,objCopyJ, {
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
		buttons: [
            'excelHtml5' 
        ]
	});
	
	//職稱
	var objCopyK = $.extend(true, {aaSorting: []}, dataTableConfig);
	objCopyK.aoColumns = [	
							{ "sTitle":"","mData": null},
							{ "sTitle":"代碼","mData": "paytitlk"},		
							{ "sTitle":"分類","mData": "patitsub"},		
							{ "sTitle":"主管否","mData": "pachief"},							
							{ "sTitle":"報表用","mData": "paytitlp"},
							{ "sTitle":"內部用","mData": "paytitlc"},
							{ "sTitle":"刪除否","mData": "pacoddel"},
							{ "sTitle":"醫院代碼","mData": "hid"},
							{ "sTitle":"更新者","mData": "payupdid"},
							{ "sTitle":"時戳","mData": "payupdat"}
							];
	 objCopyK.aoColumnDefs= [
	 						{ "sClass": "alignLeft", "aTargets": [3,4] },
						     //分類patitsub
					         {
							   'targets': 2,
							   'searchable':false,
							   'className': 'dt-body-left',
							   'render': function (data, type, row, meta){
// 							   				console.log("data:"+data+" meta.row:"+meta.row);
							    			var $select = $("<select></select>", {
								                id :  meta.row ,
								                value: data
								            });
								            
							            	$.each(titsubary, function(k,v){
								                var $option = $("<option></option>", {
								                    "text": v.text,
								                    "value": v.id
								                });
								                if(row.patitsub === v.id){
								                    $option.attr("selected", "selected")
								                }
								                $select.append($option);
								            });
											return $select.prop("outerHTML");
                                         }
						     }
						   ]; 						
							

	objCopyK = $.extend(true,objCopyK, {
				 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
 						$('td:eq(0)', nRow).html( '<button type="button" class="edit btn btn-primary" data-toggle="modal"><i class="fa fa-edit"></i></button>&emsp;<button type="button" class="delCodebtn btn btn-danger"><i class="fa fa-trash-alt"></i></button>');
				 }, "fnDrawCallback": function () {					 
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
						   		code_data={
						        	"key":rowData.paytitlk
						        }    
						        $.ajax({
									url:"${pageContext.request.contextPath}/salController.do",
									data:{
											method  : 'delTblCode',
											hid     : pahid,
											tblname : 'PATITLE',
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
					        $("#paytitlk").val(rowData.paytitlk);
							$("#patitsub").selectpicker('val',rowData.patitsub);
							$("#pachief").selectpicker('val',rowData.pachief);
							$("#paytitlp").val(rowData.paytitlp);
							$("#paytitlc").val(rowData.paytitlc);
							$("#pacoddel").selectpicker('val',rowData.pacoddel);
							$("#F_patitle_hid").val(rowData.hid);
							$("#F_patitle_payupdid").val(rowData.payupdid);
							$("#F_patitle_payupdat").val(rowData.payupdat);
							$("#model_header_title").html("修改[職稱]");
							$(".tblEditgrp").hide();
							$("#paytitlk").attr("readOnly",true);	//將key設成readOnly
							$("#PATITLE").show();
							$("label.error").hide();
 							$(".error").removeClass("error");
					        $("#addCodeDialog").modal("show");
							$("#btn_subTblCodeIns").prop("disabled", true);
							$("#btn_subTblCodeUpd").prop("disabled", false);
						});		
				 }							
		      });
    objCopyK = $.extend(true,objCopyK, {
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
		buttons: [
            'excelHtml5' 
        ]
	});	      
	//全民健康保險負擔金額表	      
	var objInsuA = $.extend(true, {aaSorting: []}, dataTableConfig);
	objInsuA.aoColumns = [	
							{ "sTitle":"","mData": null},
							{ "sTitle":"最低","mData": "dpinlow"},		
							{ "sTitle":"最高","mData": "dpinhigh"},
							{ "sTitle":"健保費","mData": "dpinamt"},
							{ "sTitle":"單位_公保","mData": "dpindept1"},		
							{ "sTitle":"單位_勞保","mData": "dpindept3"},
							{ "sTitle":"更新者","mData": "payupdid"},
							{ "sTitle":"時戳","mData": "zpadpintbl"}
							];
	objInsuA = $.extend(true,objInsuA, {
				 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
 						$('td:eq(0)', nRow).html( '<button type="button" class="edit btn btn-primary" data-toggle="modal"><i class="fa fa-edit"></i></button>&emsp;<button type="button" class="delCodebtn btn btn-danger"><i class="fa fa-minus"></i></button>');
				 }, "fnDrawCallback": function () {					 
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
						        if (tblname=="PADPINTBL"){			
						        	code_data={
						        		"key":rowData.dpinlow
						        	};
						        }
						   		    
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
					        $("#dpinlow").val(rowData.dpinlow);
							$("#dpinhigh").val(rowData.dpinhigh);
							$("#dpinamt").val(rowData.dpinamt);
							$("#dpindept1").val(rowData.dpindept1);
							$("#dpindept3").val(rowData.dpindept3);
							$("#padpintbl_payupdid").val(rowData.payupdid);
							$("#zpadpintbl").val(rowData.padpintbl);
							$("#model_insu_header_title").html("修改[全民健康保險負擔金額表]");
							$(".tblEditgrp").hide();
							$("#PADPINTBL").show();		//把要顯示的table show
							$("#dpinlow").attr("readOnly",true);//將key設成readOnly
							$("label.error").hide();
 							$(".error").removeClass("error");
					        $("#addInsuDialog").modal("show");
							$("#btn_subTblInsuIns").prop("disabled", true);
							$("#btn_subTblInsuUpd").prop("disabled", false);
						});	
				 	}							
		      });
    objInsuA = $.extend(true,objInsuA, {
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
		buttons: [
            'excelHtml5' 
        ]
	});
	//勞保投保金額對照表
	var objInsuB = $.extend(true, {aaSorting: []}, dataTableConfig);
	objInsuB.aoColumns = [	
							{ "sTitle":"","mData": null},	
							{ "sTitle":"最低","mData": "lbinlow"},		
							{ "sTitle":"最高","mData": "lbinhigh"},
							{ "sTitle":"勞保費","mData": "lbinamt"},
							{ "sTitle":"單位負擔","mData": "lbindept"},
							{ "sTitle":"時戳","mData": "zpalbintbl"},		
							
							];
	objInsuB = $.extend(true,objInsuB, {
				 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
 						$('td:eq(0)', nRow).html( '<button type="button" class="edit btn btn-primary" data-toggle="modal"><i class="fa fa-edit"></i></button>&emsp;<button type="button" class="delCodebtn btn btn-danger"><i class="fa fa-minus"></i></button>');
				 }, "fnDrawCallback": function () {					 
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
						        if (tblname=="PALBINTBL"){			
						        	code_data={
						        		"key":rowData.lbinlow
						        	};
						        }
						   		    
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
					        $("#lbinlow").val(rowData.lbinlow);
					        $("#lbinhigh").val(rowData.lbinhigh);
							$("#lbinamt").val(rowData.lbinamt);
							$("#lbindept").val(rowData.lbindept);
							$("#zpalbintbl").val(rowData.zpalbintbl);
							$("#model_insu_header_title").html("修改[勞保投保金額對照表]");
							$(".tblEditgrp").hide();
							$("#PALBINTBL").show();		//把要顯示的table show
							$("#lbinlow").attr("readOnly",true);//將key設成readOnly							
							$("label.error").hide();
 							$(".error").removeClass("error");
					        $("#addInsuDialog").modal("show");
							$("#btn_subTblInsuIns").prop("disabled", true);
							$("#btn_subTblInsuUpd").prop("disabled", false);
						});	
				 	}							
		      });
    objInsuB = $.extend(true,objInsuB, {
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
		buttons: [
            'excelHtml5' 
        ]
	});	   
	//勞工退休金月提繳工資分級表	   
	var objInsuC = $.extend(true, {aaSorting: []}, dataTableConfig);
	objInsuC.aoColumns = [				
							{ "sTitle":"","mData": null},	
							{ "sTitle":"最低","mData": "lbrplow"},		
							{ "sTitle":"最高","mData": "lbrphigh"},
							{ "sTitle":"月提繳工資","mData": "lbrpamt"},
							{ "sTitle":"公提退休金","mData": "masteramt"},
							{ "sTitle":"職業災害保險費","mData": "assocc"},
							{ "sTitle":"時戳","mData": "zpalbrptbl"}
							];
	objInsuC = $.extend(true,objInsuC, {
				 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
 						$('td:eq(0)', nRow).html( '<button type="button" class="edit btn btn-primary" data-toggle="modal"><i class="fa fa-edit"></i></button>&emsp;<button type="button" class="delCodebtn btn btn-danger"><i class="fa fa-minus"></button>');
				 }, "fnDrawCallback": function () {					 
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
						        if (tblname=="PAlbrpTBL"){			
						        	code_data={
						        		"key":rowData.lbrplow
						        	};
						        }
						   		    
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
					        $("#lbrplow").val(rowData.lbrplow);
					        $("#lbrphigh").val(rowData.lbrphigh);
							$("#lbrpamt").val(rowData.lbrpamt);
							$("#masteramt").val(rowData.masteramt);
							$("#assocc").val(rowData.assocc);
							$("#zpalbrptbl").val(rowData.zpalbrptbl);
							$("#model_insu_header_title").html("修改[勞工退休金月提繳工資分級表]");
							$(".tblEditgrp").hide();
							$("#PALBRPTBL").show();				//把要顯示的table show
							$("#lbrplow").attr("readOnly",true);//將key設成readOnly							
							$("label.error").hide();
 							$(".error").removeClass("error");
					        $("#addInsuDialog").modal("show");
							$("#btn_subTblInsuIns").prop("disabled", true);
							$("#btn_subTblInsuUpd").prop("disabled", false);
						});	
				 	}							
		      });
    objInsuC = $.extend(true,objInsuC, {
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
		buttons: [
            'excelHtml5' 
        ]
	});	   	   
	//階級支扣
	var objItemsA = $.extend(true, {aaSorting: []}, dataTableConfig);
	objItemsA.aoColumns = [	
							{ "sTitle":"","mData": null},
							{ "sTitle":"階級","mData": "payrankv"},
							{ "sTitle":"級別","mData": "paylev1v"},		
							{ "sTitle":"俸級","mData": "paylev2v"},
							{ "sTitle":"俸點","mData": "paydegrv"},
							{ "sTitle":"本薪","mData": "paybasev"},
							{ "sTitle":"公保費","mData": "paygeinf"},
							{ "sTitle":"新_公保費","mData": "paygeinn"},		
							{ "sTitle":"勞保費","mData": "paylbinf"},
							{ "sTitle":"健保費","mData": "paydpinv"},
							{ "sTitle":"退撫","mData": "payrebef"},
							{ "sTitle":"新_退撫","mData": "payreben"},
							{ "sTitle":"更新者","mData": "payuseid"},
							{ "sTitle":"時戳","mData": "payupdat"},		
							
							];
	objItemsA.aoColumnDefs= [
	 						 //分類payrankv
					         {
							   'targets': 1,
							   'searchable':false,
							   'className': 'dt-body-left',
							   'render': function (data, type, row, meta){
							   				var $out="";
							   				$.each(rankary, function(k,v){
								                if(row.payrankv === v.id){
								                    $out=v.text;
								                }
								            });
							 				return $out;
							   			}
							  } 				
							   /*'render': function (data, type, row, meta){
// 							   				console.log("data:"+data+" meta.row:"+meta.row);
							    			var $select = $("<select></select>", {
								                id :  meta.row ,
								                value: data
								            });
								            
							            	$.each(rankary, function(k,v){
								                var $option = $("<option></option>", {
								                    "text": v.text,
								                    "value": v.id
								                });
								                if(row.payrankv === v.id){
								                    $option.attr("selected", "selected")
								                }
								                $select.append($option);
								            });
											return $select.prop("outerHTML");
                                         }*/
						     
						   ]; 
	objItemsA = $.extend(true,objItemsA, {
				 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
 						$('td:eq(0)', nRow).html( '<button type="button" class="edit btn btn-primary" data-toggle="modal"><i class="fa fa-edit"></i></button>&emsp;<button type="button" class="delCodebtn btn btn-danger"><i class="fa fa-minus"></button>');
				 }, "fnDrawCallback": function () {					 
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
						        if (tblname=="PARANK"){			
						        	code_data={
						        		"paylev1v":rowData.paylev1v,
						        		"paylev2v":rowData.paylev2v,
						        		"payrankv":rowData.payrankv
						        	};
						        }
						   		    
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
					        $("#paylev1v").val(rowData.paylev1v);
							$("#paylev2v").val(rowData.paylev2v);
							$("#paydegrv").val(rowData.paydegrv);
							$("#payrankv").selectpicker('val',rowData.payrankv);
							$("#paybasev").val(rowData.paybasev);
							$("#paygeinf").val(rowData.paygeinf);
							$("#paygeinn").val(rowData.paygeinn);
							$("#paylbinf").val(rowData.paylbinf);
							$("#paydpinv").val(rowData.paydpinv);
							$("#payrebef").val(rowData.payrebef);
							$("#payreben").val(rowData.payreben);
							$("#payuseid").val(rowData.payuseid);
							$("#parank_payupdat").val(rowData.payupdat);
							$("#model_items_header_title").html("修改[階級支扣]");
							$(".tblEditgrp").hide();
							$("#PARANK").show();		//把要顯示的table show
							$("#paylev1v,#paylev2v,#payrankv").attr("readOnly",true);//將key設成readOnly
							$("label.error").hide();
 							$(".error").removeClass("error");
					        $("#addItemsDialog").modal("show");
							$("#btn_subTblItemsIns").prop("disabled", true);
							$("#btn_subTblItemsUpd").prop("disabled", false);
						});	
				 	}							
		      });
    objItemsA = $.extend(true,objItemsA, {
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
		buttons: [
            'excelHtml5' 
        ]
	});
	//職等支扣--專業加給及主管加給
	var objItemsB = $.extend(true, {aaSorting: []}, dataTableConfig);
	objItemsB.aoColumns = [	{ "sTitle":"","mData": null},
							{ "sTitle":"職等","mData": "payposik"},		
							{ "sTitle":"俸點(低)","mData": "degrlow"},
							{ "sTitle":"俸點(高)","mData": "degrhigh"},
							{ "sTitle":"主管","mData": "paypsp5"},
							{ "sTitle":"醫師研究_24-1","mData": "paypsp1"},		
							{ "sTitle":"醫務技術_26","mData": "paypsp2"},
							{ "sTitle":"行政_1","mData": "paypsp3"},		
							{ "sTitle":"資訊_20","mData": "paypsp4"},
							{ "sTitle":"醫學工程_3","mData": "paypsp6"},
							{ "sTitle":"工程人員_7","mData": "paypsp7"},
							{ "sTitle":"社工_2","mData": "paypsp8"},
							{ "sTitle":"更新者","mData": "payposid"},
							{ "sTitle":"時戳","mData": "payupdat"}
							];
	objItemsB = $.extend(true,objItemsB, {
				 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
 						$('td:eq(0)', nRow).html( '<button type="button" class="edit btn btn-primary" data-toggle="modal"><i class="fa fa-edit"></i></button>&emsp;<button type="button" class="delCodebtn btn btn-danger"><i class="fa fa-minus"></button>');
				 }, "fnDrawCallback": function () {					 
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
						        if (tblname=="PAPOSI"){			
						        	code_data={
						        		"payposik":rowData.payposik,
						        		"degrlow":rowData.degrlow
						        	};
						        }
						   		    
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
					        $("#payposik").val(rowData.payposik);
							$("#degrlow").val(rowData.degrlow);
							$("#degrhigh").val(rowData.degrhigh);
							$("#paypsp1").val(rowData.paypsp1);
							$("#paypsp2").val(rowData.paypsp2);
							$("#paypsp3").val(rowData.paypsp3);
							$("#paypsp4").val(rowData.paypsp4);
							$("#paypsp5").val(rowData.paypsp5);
							$("#paypsp6").val(rowData.paypsp6);
							$("#paypsp7").val(rowData.paypsp7);
							$("#paypsp8").val(rowData.paypsp8);
							$("#payposid").val(rowData.payposid);
							$("#paposi_payupdat").val(rowData.payupdat);
							$("#model_items_header_title").html("修改[職等支扣--專業加給及主管加給]");
							$(".tblEditgrp").hide();
							$("#PAPOSI").show();		//把要顯示的table show
							$("#payposik,#degrlow").attr("readOnly",true);//將key設成readOnly
							$("label.error").hide();
 							$(".error").removeClass("error");
					        $("#addItemsDialog").modal("show");
							$("#btn_subTblItemsIns").prop("disabled", true);
							$("#btn_subTblItemsUpd").prop("disabled", false);
						});	
				 	}							
		      });
    objItemsB = $.extend(true,objItemsB, {
		dom: 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl  ui-corner-tr"lfr>'+
				't'+
				'<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
		buttons: [
            'excelHtml5' 
        ]
	});
	
	var objWkpfA = $.extend(true, {aaSorting: []}, dataTableConfig);
	objWkpfA.aoColumns = [	
							{ "sTitle":"","mData": null},
							{ "sTitle":"年度月份","mData": "paypfmon"},		
							{ "sTitle":"績效總點數","mData": "paypfpf"},
							{ "sTitle":"契約人員","mData": "paypfunt"},		
							{ "sTitle":"正職","mData": "papfamt"},
							{ "sTitle":"住院醫師","mData": "paypfrev"},
							{ "sTitle":"契約護理","mData": "paypfd"},
							{ "sTitle":"聘實","mData": "paypfe"},
							{ "sTitle":"時戳","mData": "paypfymd"}
							];
	objWkpfA = $.extend(true,objWkpfA, {
				 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
 						$('td:eq(0)', nRow).html( '<button type="button" class="edit btn btn-primary" data-toggle="modal"><i class="fa fa-edit"></i></button>&emsp;<button type="button" class="delCodebtn btn btn-danger"><i class="fa fa-minus"></i></button>');
				 }, "fnDrawCallback": function () {					 
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
							$("#paypfmon").attr("readOnly",true);//將key設成readOnly
							$("label.error").hide();
 							$(".error").removeClass("error");
					        $("#addWkpfDialog").modal("show");
							$("#btn_subTblWkpfIns").prop("disabled", true);
							$("#btn_subTblWkpfUpd").prop("disabled", false);
						});	
				 	}							
		      });
		      
	var objWkpfB = $.extend(true, {aaSorting: []}, dataTableConfig);
	objWkpfB.aoColumns = [	
							{ "sTitle":"","mData": null},
							{ "sTitle":"工作獎金代碼","mData": "pacskey"},		
							{ "sTitle":"基本績分","mData": "pacsbase"},
							{ "sTitle":"俸級績分","mData": "pacsdata"},		
							{ "sTitle":"職稱中文","mData": "pacsname"},
							{ "sTitle":"時戳","mData": "zpacspf"}
							];
	objWkpfB = $.extend(true,objWkpfB, {
				 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
 						$('td:eq(0)', nRow).html( '<button type="button" class="edit btn btn-primary" data-toggle="modal"><i class="fa fa-edit"></i></button>&emsp;<button type="button" class="delCodebtn btn btn-danger"><i class="fa fa-minus"></i></button>');
				 }, "fnDrawCallback": function () {					 
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
							$("#pacskey").attr("readOnly",true);//將key設成readOnly
							$("label.error").hide();
 							$(".error").removeClass("error");
					        $("#addWkpfDialog").modal("show");
							$("#btn_subTblWkpfIns").prop("disabled", true);
							$("#btn_subTblWkpfUpd").prop("disabled", false);
						});	
				 	}							
		      });
		      
	
		      
//     $("#qrycodetyp").trigger( "change" );
    
    //輸入代碼,找是否重複 新增才要檢查
	$(".findkey").blur(function(){
// 		var key=$(this).attr('id');
//		console.log($(this).attr("readOnly"));
		
		if($(this).attr("readOnly")==undefined || $(this).attr("readOnly")==false){	//未設定readOnly屬性
			var key=$(this).val();
			console.log("key:"+key);
			var code_data="";
			if ( key != ""){
				if (tblname=="")$('#qrycodetyp').val().split(':')[1];
				if (tblname=="PATAXTBL"){						
					if ($('#sallow').val()=="" || $("#kindred").val()==""){
						return false;
					}else{
			        	code_data={
			        		"sallow":$('#sallow').val(),
			        		"kindred":$('#kindred').val()
			        	}	
		        	}
		        }else if (tblname=="PATITLE"){						
					if ($('#paytitlk').val()==""){
						return false;
					}else{
			        	code_data={
			        		"paytitlk":$('#paytitlk').val()
			        	}	
		        	}
		        }else if (tblname=="PAPAYITM"){						
					if ($('#paycode').val()==""){
						return false;
					}else{
			        	code_data={
			        		"paycode":$('#paycode').val()
			        	}	
		        	}
		        }
		        else if (tblname=="PARANK"){						
					if ($('#paylev1v').val()==""||$('#paylev2v').val()==""||$('#payrankv').val()==""){
						return false;
					}else{
			        	code_data={
			        		"paylev1v":$('#paylev1v').val(),
			        		"paylev2v":$('#paylev2v').val(),
			        		"payrankv":$('#payrankv').val()
			        	}	
		        	}
		        }else if (tblname=="PAPOSI"){						
					if ($('#payposik').val()==""||$('#degrlow').val()==""){
						return false;
					}else{
			        	code_data={
			        		"payposik":$('#payposik').val().toUpperCase(),
			        		"degrlow":$('#degrlow').val()
			        	}	
		        	}
		        }else if (tblname=="PADEPT"){						
					if ($('#padepcod').val()==""){
						return false;
					}else{
			        	code_data={
			        		"key":$('#padepcod').val()
			        	}	
		        	}
		        }
		        else{
		        	code_data={
		        		"key":key
		        	};
		        }	
				if (!$("#btn_subTblCodeIns").prop("disabled")){
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
		      
});
</script>
</head>
<body>

<ul class="nav nav-pills mb-3" id="pills-tab" role="tab">
  <li class="nav-item">
    <a class="nav-link active" id="pills-code-tab" data-toggle="pill" href="#byCodeMain" role="tab" aria-controls="pills-home" aria-selected="true">代碼維護</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-insu-tab" data-toggle="pill" href="#byInsuMain" role="tab" aria-controls="pills-profile" aria-selected="false">保費對照檔</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-items-tab" data-toggle="pill" href="#byItemsMain" role="tab" aria-controls="pills-items" aria-selected="false">支扣數</a>
  </li>
  <!-- <li class="nav-item">
    <a class="nav-link" id="pills-wkpf-tab" data-toggle="pill" href="#byWkpfMain" role="tab" aria-controls="pills-wkpf" aria-selected="false">工作獎金</a>
  </li> -->  
</ul>
<div class="tab-content" id="pills-tabContent">
  <!-- 代碼維護 -->
  <div class="tab-pane fade show active" id="byCodeMain" role="tabpanel" aria-labelledby="pills-code-tab">
 		<!-- 輸入 查詢 項目 -->
 	<div class="container-sm">	
		<div class="row">
	    	<div class="col-md-12">
				<form class="form-inline"><!-- form-inline 文字在同一行 -->
					<div class="input-group">
						<span class="input-group-text">代碼類型</span>
		  	
						<select id="qrycodetyp" class="form-control">
							<option value="1:PABANK">1發薪日及入帳日對應檔</option>
							<option value="2:PADEPT">2單位代碼</option>
							<option value="3:PATITLE">3職稱代碼</option>
	     					<option value="4:PARANKCODE">4階級代碼</option>
	     					<option value="5:PACATGTBL">5人工造冊應計轉帳項目代碼</option>
	     					<option value="6:PATAXTBL">6薪資所得扣繳稅額表</option>
	     					<option value="7:PAPAYITM">7支扣項目代碼</option>
	     					<option value="8:PAEXPFTBL">8績效額外點數對照表</option>
	     					<option value="9:PARNK4TBL">9適用專業加給對照表</option>	     					
	     					<option value="10:PATRACODE">10異動代碼</option>
						</select>
					</div>
					
					<input type="text" id="txtKEY" class="form-control text-uppercase" placeholder="查詢項">
					<input type="text" id="txtKEY1" class="form-control"style="display:none;" placeholder="查詢項">
					
					<select id="titsubKey" class="qryCodeSelect selectpicker form-control" title="分類" >
					  <option value="" selected>分類</option>
				      <option value="V">V 醫師</option>
				      <option value="N">N 護師</option>
				      <option value="S">S 研究人員</option>
				      <option value="U">U 工程人員</option>
				      <option value="W">W 醫學工程</option>
				      <option value="D">D 藥師</option>
				      <option value="G">G 行政</option>
				      <option value="B">B 社工</option>
				      <option value="C">C 資訊</option>
				      <option value="H">H 約聘僱</option>
				      <option value="I">I 契約</option>
				      <option value="P">P 駐警</option>
				      <option value="Y">Y 技工友</option>
				      <option value="A">A 兼職</option>
				    </select>
				    <select id="coddelKey" class="qryCodeSelect selectpicker form-control" title="刪除否">
					  <option value="" selected>刪除否</option>
					  <option value="N">N</option>
				      <option value="Y">Y</option>
				    </select>
					<button id="btn_qryCode" class="btn btn-info" type="button" ><i class="fa fa-search"></i>查詢</button>
					<button id="btn_clear" class="btn btn-warning mr-auto" type="button"><i class="fa fa-eraser"></i>清空</button>
					<button id="btn_addcode" class="btn btn-success ml-auto" type="button" data-toggle="modal" data-target="#addCodeDialog"><i class="fa fa-edit"></i>新增代碼</button>
					
				</form>
			</div>
		</div>
 	
		<div class="row">
			<div class="col-md-12">
			<div id="codeIdDiv" class="codeDiv">
				<table id="tblCodeTraList" class="table table-striped table-bordered table-hover" style="text-align:center;display:none;">
					<thead>						
					</thead>
				</table>
				<table id="tblCodeDeptList" class="table table-striped table-bordered table-hover" style="text-align:center;display:none;">
					<thead>						
					</thead>
				</table>
				<table id="tblCodeRankList" class="table table-striped table-bordered table-hover" style="text-align:center;display:none;">
					<thead>
					</thead>
				</table>
				<table id="tblCodeCatgList" class="table table-striped table-bordered table-hover" style="text-align:center;display:none;">
					<thead>
					</thead>
				</table>
				<table id="tblCodeExpfList" class="table table-striped table-bordered table-hover" style="text-align:center;display:none;">
					<thead>						
					</thead>
				</table>
				<table id="tblCodeRnk4List" class="table table-striped table-bordered table-hover" style="text-align:center;display:none;">
					<thead>
					</thead>
				</table>
				<table id="tblBankDtList" class="table table-striped table-bordered table-hover" style="text-align:center;display:none;">
					<thead>
					</thead>
				</table>
				<table id="tblTaxTblList" class="table table-striped table-bordered table-hover" style="text-align:center;display:none;">
					<thead>
					</thead>
				</table>
				<table id="tblPayItmList" class="table table-striped table-bordered table-hover" style="text-align:center;display:none;">
					<thead>
					</thead>
				</table>
				<table id="tblCodeTitList" class="table table-striped table-bordered table-hover" style="text-align:center;display:none;">
					<thead>
					</thead>
				</table>
			</div>
			</div>
		</div>
		<!-- 新增dialog -->
		<div class="modal fade" id="addCodeDialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">

			<form id="subsalCodeForm" >
				<INPUT type="hidden" id="citem" name ="item">
				<div class="modal-dialog modal-lg">

                   <!-- Modal content-->
                   <div class="modal-content">
                   		 <!-- Modal Header -->
				        <div class="modal-header">
				          <h4 class="modal-title"><span id="model_header_title"></span>代碼</h4>
				          <button type="button" class="close" data-dismiss="modal">&times;</button>
				        </div>	
                       <div class="modal-body">
                       	<div class="container-sm">
    					<div class="row justify-content-md-center">
                       		<table id="PATRACODE" class="tblEditgrp table table-striped table-bordered table-hover" style="text-align:center;">
								<thead class="table-primary">
									<tr>
										<th class="text-center">代碼</th>
										<th class="text-center">中文</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td><input type="text" id="tracode" name="tracode" class="form-control findkey required,digits" value="" placeholder="請輸入異動代碼" maxlength="2" max="80"></input></td>
										<td><input type="text" id="traname" name="traname" class="form-control required" value=""  placeholder="請輸入異動代碼中文" maxlength="4"></input></td>
									</tr>
								</tbody>							
							</table>
							<table id="PADEPT" class="tblEditgrp table table-striped table-bordered" style="text-align:center;">
								<thead class="table-primary">
									<tr class="div-green">
										<th class="table-title">單位代碼</th>
										<th class="table-title">成本中心</th>
										<th class="table-title">單位中文</th>
										<th class="table-title">人事單位代碼</th>
										<th class="table-title">醫院代碼</th>
										<th class="table-title">更新者</th>
										<th class="table-title">時戳</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td><input type="text" id="padepcod" name="padepcod" class="form-control findkey required" value="" size="4" placeholder="請輸入單位代碼" maxlength="4"></input></td>
										<td><input type="text" id="pacstno" name="pacstno" class="form-control required" value="" size="6" placeholder="請輸入成本代碼" maxlength="6"></input></td>
										<td><input type="text" id="padepnmc" name="padepnmc" class="form-control required" value="" size="22" placeholder="11中文長度" maxlength="11"></input></td>
										<td><input type="text" id="pbwkcode" name="pbwkcode" class="form-control" value="" size="6" placeholder="請輸入人事單位代碼" maxlength="6"></input></td>
										<td><input type="text" id="F_padept_hid" name="hid" class="form-control" value="" size="3"  maxlength="3" readOnly></input></td>
										<td><input type="text" id="F_padept_payupdid" name="payupdid" class="form-control" value="" size="8"  maxlength="8" readOnly></input></td>
										<td><input type="text" id="F_padept_payupdat" name="payupdat" class="form-control" value="" size="26"  maxlength="26" readOnly></input></td>
									</tr>
								</tbody>							
							</table>
							<table id="PARANKCODE" class="tblEditgrp table table-striped table-bordered" style="text-align:center;">
								<thead class="table-primary">
									<tr class="div-green">
										<th class="table-title">階別代碼</th>
										<th class="table-title">中文說明</th>
										<th class="table-title">階別簡稱</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td><input type="text" id="rankcode" name="rankcode" class="form-control text-uppercase findkey required" value="" size="1" maxlength="1"></input></td>
										<td><input type="text" id="rankname" name="rankname" class="form-control required" value="" size="14" placeholder="7中文長度" maxlength="10"></input></td>
										<td><input type="text" id="rankabbr" name="rankabbr" class="form-control required" value="" size="2" placeholder="1中文長度" maxlength="1"></input></td>
									</tr>
								</tbody>							
							</table>
							<table id="PACATGTBL" class="tblEditgrp table table-striped table-bordered" style="text-align:center;">
								<thead class="table-primary">
									<tr class="div-green">
										<th class="table-title">轉帳項目代碼</th>
										<th class="table-title">中文說明</th>
										<th class="table-title">更新者</th>
										<th class="table-title">時戳</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td><input type="text" id="paycatg" name="paycatg" class="form-control text-uppercase findkey required" value="" size="3" maxlength="3"></input></td>
										<td><input type="text" id="catgname" name="catgname" class="form-control required" value="" size="14" placeholder="7中文長度" maxlength="7"></input></td>
										<td><input type="text" id="pacatgtbl_payupdid" name="payupdid" class="form-control" value="" size="8"  maxlength="8" readOnly></input></td>
										<td><input type="text" id="pacatgtbl_payupdat" name="payupdat" class="form-control" value="" size="26"  maxlength="26" readOnly></input></td>
									</tr>
								</tbody>							
							</table>
							<table id="PAEXPFTBL" class="tblEditgrp table table-striped table-bordered" style="text-align:center;">
								<thead class="table-primary">
									<tr class="div-green">
										<th class="table-title">績效額外點數代碼</th>
										<th class="table-title">中文說明</th>
										<th class="table-title">更新者</th>
										<th class="table-title">時戳</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td><input type="text" id="expfkey" name="expfkey" class="form-control text-uppercase findkey required" value="" size="3" maxlength="3"></input></td>
										<td><input type="text" id="expfname" name="expfname" class="form-control required" value="" size="30" placeholder="15中文長度" maxlength="15"></input></td>
										<td><input type="text" id="paexpftbl_payupdid" name="payupdid" class="form-control" value="" size="8"  maxlength="8" readOnly></input></td>
										<td><input type="text" id="paexpftbl_payupdat" name="payupdat" class="form-control" value="" size="26"  maxlength="26" readOnly></input></td>
									</tr>
								</tbody>							
							</table>
							<table id="PARNK4TBL" class="tblEditgrp table table-striped table-bordered" style="text-align:center;">
								<thead class="table-primary">
									<tr class="div-green">
										<th class="table-title">適用專業加給對照表</th>
										<th class="table-title">中文說明</th>
										<th class="table-title">時戳</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td><input type="text" id="rnk4key" name="rnk4key" class="form-control text-uppercase findkey required" value="" size="3" maxlength="3"></input></td>
										<td><input type="text" id="rnk4name" name="rnk4name" class="form-control required" value="" size="30" placeholder="15中文長度" maxlength="15"></input></td>
										<td><input type="text" id="parnk4tbl_payupdtm" name="payupdtm" class="form-control" value="" size="26"  maxlength="26" readOnly></input></td>
									</tr>
								</tbody>							
							</table>
							<table id="PABANK" class="tblEditgrp table table-striped table-bordered" style="text-align:center;">
								<thead class="table-primary">
									<tr class="div-green">
										<th class="table-title">餉單編號</th>
										<th class="table-title">發薪日期</th>
										<th class="table-title">入帳日期</th>
										<th class="table-title">醫院代碼</th>
										<th class="table-title">更新者</th>
										<th class="table-title">時戳</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td><input type="text" id="salnum" name="salnum" class="form-control text-uppercase findkey required" value="" size="4" maxlength="4"></input></td>
										<td><input type="text" id="paybank" name="paybank" class="form-control datepicker required" value="" size="10"  maxlength="10"></input></td>
										<td><input type="text" id="paysaldt" name="paysaldt" class="form-control datepicker required" value="" size="10"  maxlength="10" ></input></td>
										<td><input type="text" id="F_pabank_hid" name="hid" class="form-control" value="" size="3"  maxlength="3" readOnly></input></td>
										<td><input type="text" id="F_pabank_payupdid" name="payupdid" class="form-control" value="" size="8"  maxlength="8" readOnly></input></td>
										<td><input type="text" id="F_pabank_payupdat" name="payupdat" class="form-control" value="" size="26"  maxlength="26" readOnly></input></td>
									</tr>
								</tbody>							
							</table>
							<table id="PATAXTBL" class="tblEditgrp table table-striped table-bordered" style="text-align:center;">
								<thead class="table-primary">
									<tr class="div-green">
										<th class="table-title">最低薪資所得</th>
										<th class="table-title">最高薪資所得</th>
										<th class="table-title">扶養人數</th>
										<th class="table-title">扣繳稅額</th>
										<th class="table-title">更新者</th>
										<th class="table-title">時戳</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td><input type="text" id="sallow" name="sallow" class="form-control digits findkey required" value="" size="7" maxlength="7"></input></td>
										<td><input type="text" id="salhigh" name="salhigh" class="form-control digits required" value="" size="7"  maxlength="7"></input></td>
										<td><input type="text" id="kindred" name="kindred" class="form-control digits findkey required" value="" size="2"  maxlength="2" ></input></td>
										<td><input type="text" id="taxamt" name="taxamt" class="form-control digits findkey required" value="" size="7"  maxlength="7" ></input></td>
										<td><input type="text" id="pataxtbl_payupdid" name="payupdid" class="form-control" value="" size="8"  maxlength="8" readOnly></input></td>
										<td><input type="text" id="pataxtbl_payupdat" name="payupdat" class="form-control" value="" size="26"  maxlength="26" readOnly></input></td>
									</tr>
								</tbody>							
							</table>
							
							<table id="PATITLE" class="tblEditgrp table-responsive-md table-striped table-bordered table-warning" >
									<tr>
										<th class="text-center">職稱代碼</th>
										<td><input type="text" id="paytitlk" name="paytitlk" class="form-control required findkey" value="" maxlength="2" size="2"></input></td>										 
									</tr>	
									<tr>
										<th class="text-center">分類</th>
										<td>
										<select id="patitsub" name="patitsub" class="selectpicker required" title="分類">
											  <option value="" disabled selected>分類</option>
										      <option value="V">V醫師</option>
										      <option value="N">N護師</option>
										      <option value="S">S研究人員</option>
										      <option value="U">U工程人員</option>
										      <option value="W">W醫學工程</option>
										      <option value="D">D藥師</option>
										      <option value="G">G行政</option>
										      <option value="B">B社工</option>
										      <option value="C">C資訊</option>
										      <option value="H">H約聘僱</option>
										      <option value="I">I契約</option>
										      <option value="P">P駐警</option>
										      <option value="Y">Y技工友</option>
										      <option value="A">A兼職</option>
										    </select>	
										</td>
									</tr>	
									<tr>	
										<th class="text-center">主管否</th>
										<td><select id="pachief" name="pachief" class="selectpicker required" title="主管否">
											  <option value="N">N</option>
										      <option value="Y">Y</option>
										    </select></td>
									</tr>	
									<tr>
										<th class="text-center">報表用名稱</th>
										<td><input type="text" id="paytitlp" name="paytitlp" class="form-control required" value=""  maxlength="22" size="11" placeholder="11中文字"></input></td>
									</tr>
									<tr>
										<th class="text-center">內部用名稱</th>
										<td><input type="text" id="paytitlc" name="paytitlc" class="form-control required" value=""  maxlength="22" size="11" placeholder="11中文字"></input></td>
									</tr>	
									<tr>	
										<th class="text-center">刪除否</th>
										<td><select id="pacoddel" name="pacoddel"class="selectpicker required" title="刪除否">
											  <option value="N">N</option>
										      <option value="Y">Y</option>
										    </select></td>
									</tr>	
									<tr>	
										<th class="text-center">更新者</th>
										<td><input type="text" id="patitle_payupdid" name="payupdid" class="form-control" value=""  maxlength="10" size="10"readOnly></input></td>
									</tr>	
									<tr>	
										<th class="text-center">時戳</th>
										<td><input type="text" id="patitle_payupdat" name="payupdat" class="form-control" value="" size="26"  maxlength="26" readOnly></input></td>									
									</tr>
									<tr>	
										<th class="text-center">醫院別</th>
										<td><input type="text" id="F_patitle_hid" name="hid" class="form-control" value=""  maxlength="3" size="3"readOnly></input></td>
									</tr>						
								</table>
								
								<table id="PAPAYITM" class="tblEditgrp table-responsive-md table-striped table-bordered table-warning" >
									<tr>
										<th class="text-center">代碼</th>
										<td><input type="text" id="paycode" name="paycode" class="form-control findkey required" value="" size="5" maxlength="5"></input></td>
									</tr>
									<tr>
										<th class="text-center">名稱</th>
										<td><input type="text" id="pacodnmc" name="pacodnmc" class="form-control required" value=""  maxlength="22" size="11" placeholder="11中文字"></input></td>
									</tr>	
									<tr>	
										<th class="text-center">刪除否</th>
										<td><select id="F_papayitm_pacoddel" name="pacoddel"class="selectpicker required" >
											  <option value="N">N</option>
										      <option value="Y">Y</option>
										    </select></td>
									</tr>
									<tr>	
										<th class="text-center">列入所得</th>
										<td><select id="payiftax" name="payiftax"class="selectpicker required" >
											  <option value="N">N</option>
										      <option value="Y">Y</option>
										    </select></td>
									</tr>
									<tr>	
										<th class="text-center">更新主檔</th>
										<td><select id="paupbas" name="paupbas"class="selectpicker required" >
											  <option value="N">N</option>
										      <option value="Y">Y</option>
										    </select></td>
									</tr>
									<tr>	
										<th class="text-center">列5%稅</th>
										<td><select id="payreward" name="payreward"class="selectpicker required" >
											  <option value="N">N</option>
										      <option value="Y">Y</option>
										    </select></td>
									</tr>
									<tr>	
										<th class="text-center">二代健保<br>1公保2勞保3兼職</th>
										<td><input type="text" id="payauth" name="payauth" class="form-control required" value=""  maxlength="3" size="3"></input></td>
									</tr>	
									<tr>	
										<th class="text-center">更新者</th>
										<td><input type="text" id="F_papayitm_payupdid" name="payupdid" class="form-control" value=""  maxlength="10" size="10"readOnly></input></td>
									</tr>	
									<tr>	
										<th class="text-center">時戳</th>
										<td><input type="text" id="F_papayitm_payupdat" name="payupdat" class="form-control" value="" size="26"  maxlength="26" readOnly></input></td>									
									</tr>
									<tr>	
										<th class="text-center">醫院別</th>
										<td><input type="text" id="F_papayitm_hid" name="hid" class="form-control" value=""  maxlength="3" size="3"readOnly></input></td>
									</tr>					
								</table>
							</div>	
						</div>	
                       </div>
                       <!-- Modal footer -->
				        <div class="modal-footer">
                           <button type="button" class="btn btn-primary" id="btn_subTblCodeIns"><i class="fa fa-save"></i>確認儲存</button>
                           <button type="button" class="btn btn-primary" id="btn_subTblCodeUpd"><i class="fa fa-refresh"></i>更改</button>
                           <button type="button" class="btn btn-secondary" data-dismiss="modal"><i class="fa fa-undo"></i>返回</button>
                       </div>
                   </div>
               </div>
			</form>
        </div>
     </div>     
  </div>
  <!-- 保費對照檔 -->
  <div class="tab-pane fade" id="byInsuMain" role="tabpanel" aria-labelledby="pills-insu-tab">
 	<div class="container-sm">
		<div class="row">
	    	<div class="col-md-12">
				<div id="pursign-search">
					<form class="form-inline"><!-- form-inline 文字在同一行 -->
						<div class="input-group">
							<span class="input-group-text">保費項目</span>
							<select id="qryInsutyp" name="qryInsutyp">
								<option value="1:PADPINTBL">1全民健康保險負擔金額表  </option>
								<option value="2:PALBINTBL">2勞保投保金額對照表 </option>
		     					<option value="3:PALBRPTBL">3勞工退休金月提繳工資分級表 </option>		     					
							</select>
						</div>
						<button id="btn_qryInsu" class="btn btn-info" type="button" ><i class="fa fa-search"></i>查詢</button>
						<button id="btn_clearInsu" class="btn btn-warning mr-auto" type="button"><i class="fa fa-eraser"></i>清空</button>
						<button id="btn_addcodeInsu" class="btn btn-success ml-auto" type="button" data-toggle="modal" data-target="#addInsuDialog"><i class="fa fa-edit"></i>新增保費</button>
						
					</form>
				</div>
			</div>
		</div>
 	
		<div class="row">
			<div class="col-md-12">
			<div id="codeInsuDiv" class="codeDiv">
				<table id="tblInsuDpinList" class="table table-striped table-bordered table-hover" style="text-align:center;display:none;">
					<thead>
					</thead>
				</table>
				<table id="tblInsuLbinList" class="table table-striped table-bordered table-hover" style="text-align:center;display:none;">
					<thead>
					</thead>
				</table>
				<table id="tblInsuLbrpList" class="table table-striped table-bordered table-hover" style="text-align:center;display:none;">
					<thead>
					</thead>
				</table>
				</div>
			</div>
		</div>
		<!-- 新增dialog -->
		<div class="modal fade" id="addInsuDialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">

			<form id="subsalInsuForm" >
				<INPUT type="hidden" id="insuitem" name ="item">
				<div class="modal-dialog modal-lg">

                   <!-- Modal content-->
                   <div class="modal-content">
                   		 <!-- Modal Header -->
				        <div class="modal-header">
				          <h4 class="modal-title"><span id="model_insu_header_title"></span>參考檔</h4>
				          <button type="button" class="close" data-dismiss="modal">&times;</button>
				        </div>	
                       <div class="modal-body">
                       	 <div class="container-fluid">
    						<div class="row justify-content-md-center">
	                       		<table id="PADPINTBL" class="tblEditgrp table-responsive-md table-striped table-bordered table-warning" >
									<tr>
										<th class="text-center">最低月投保金額</th>
										<td><input type="text" id="dpinlow" name="dpinlow" class="form-control required findkey digits" value="" maxlength="6" size="6"></input></td>
									</tr>	
									<tr>
										<th class="text-center">最高月投保金額</th>
										<td><input type="text" id="dpinhigh" name="dpinhigh" class="form-control required digits" value=""  maxlength="6" size="6"></input></td>
									</tr>	
									<tr>	
										<th class="text-center">健保費</th>
										<td><input type="text" id="dpinamt" name="dpinamt" class="form-control required digits" value=""  maxlength="6" size="6"></input></td>
									</tr>	
									<tr>
										<th class="text-center">單位負擔公保身份</th>
										<td><input type="text" id="dpindept1" name="dpindept1" class="form-control required digits" value=""  maxlength="6" size="6"></input></td>
									</tr>	
									<tr>	
										<th class="text-center">單位負擔勞保身份</th>
										<td><input type="text" id="dpindept3" name="dpindept3" class="form-control required digits" value=""  maxlength="6" size="6"></input></td>
									</tr>	
									<tr>	
										<th class="text-center">更新者</th>
										<td><input type="text" id="padpintbl_payupdid" name="payupdid" class="form-control" value=""  maxlength="10" size="10"readOnly></input></td>
									</tr>	
									<tr>	
										<th class="text-center">時戳</th>
										<td><input type="text" id="zpadpintbl" name="zpadpintbl" class="form-control" value="" size="26"  maxlength="26" readOnly></input></td>									
									</tr>						
								</table>
							</div>
							<div class="row justify-content-md-center">
								<table id="PALBINTBL" class="tblEditgrp table-responsive-md table-striped table-bordered table-warning">
									<tr>
										<th class="text-center">最低月投保金額</th>
										<td><input type="text" id="lbinlow" name="lbinlow" class="form-control required findkey digits" value="" maxlength="6" size="6"></input></td>
									</tr>	
									<tr>
										<th class="text-center">最高月投保金額</th>
										<td><input type="text" id="lbinhigh" name="lbinhigh" class="form-control required digits" value=""  maxlength="6" size="6"></input></td>
									</tr>	
									<tr>	
										<th class="text-center">勞保費</th>
										<td><input type="text" id="lbinamt" name="lbinamt" class="form-control required digits" value=""  maxlength="6" size="6"></input></td>
									</tr>	
									<tr>
										<th class="text-center">單位負擔</th>
										<td><input type="text" id="lbindept" name="lbindept" class="form-control required digits" value=""  maxlength="6" size="6"></input></td>
									</tr>	
									<tr>	
										<th class="text-center">時戳</th>
										<td><input type="text" id="zpalbintbl" name="zpalbintbl" class="form-control" value="" size="26"  maxlength="26" readOnly></input></td>									
									</tr>					
								</table>
							</div>
							<div class="row justify-content-md-center">
								<table id="PALBRPTBL" class="tblEditgrp table-responsive-md table-striped table-bordered table-warning">
									<tr>
										<th class="text-center">最低月投保金額</th>
										<td><input type="text" id="lbrplow" name="lbrplow" class="form-control findkey required digits" value="" maxlength="6" size="6"></input></td>
									</tr>	
									<tr>
										<th class="text-center">最高月投保金額</th>
										<td><input type="text" id="lbrphigh" name="lbrphigh" class="form-control required digits" value=""  maxlength="6" size="6"></input></td>
									</tr>	
									<tr>	
										<th class="text-center">月提繳工資</th>
										<td><input type="text" id="lbrpamt" name="lbrpamt" class="form-control required digits" value=""  maxlength="6" size="6"></input></td>
									</tr>	
									<tr>
										<th class="text-center">公提退休金<br>=月提繳工資*6%</th>
										<td><input type="text" id="masteramt" name="masteramt" class="form-control required digits" value=""  maxlength="6" size="6"></input></td>
									</tr>
									<tr>
										<th class="text-center">職業災害保險費</th>
										<td><input type="text" id="assocc" name="assocc" class="form-control required digits" value=""  maxlength="6" size="6"></input></td>
									</tr>		
									<tr>	
										<th class="text-center">時戳</th>
										<td><input type="text" id="zpalbrptbl" name="zpalbrptbl" class="form-control" value="" size="26"  maxlength="26" readOnly></input></td>									
									</tr>							
								</table>
							</div>
						</div>
                       </div>
                       <!-- Modal footer -->
				        <div class="modal-footer">
                           <button type="button" class="btn btn-primary" id="btn_subTblInsuIns"><i class="fa fa-save"></i>確認儲存</button>
                           <button type="button" class="btn btn-primary" id="btn_subTblInsuUpd"><i class="fa fa-refresh"></i>更改</button>
                           <button type="button" class="btn btn-secondary" data-dismiss="modal"><i class="fa fa-undo"></i>返回</button>
                       </div>
                   </div>
               </div>
			</form>
        </div>
     </div>
  </div>
  
  <!-- 支扣數 -->
  <div class="tab-pane fade" id="byItemsMain" role="tabpanel" aria-labelledby="pills-items-tab">
  	<div class="container-fluid">
 		<!-- 輸入 查詢 項目 -->
		<div class="row">
			<div class="col-12">
	    		<div class="form-group">
					<form class="form-inline"><!-- form-inline 文字在同一行 -->
						<div class="input-group">
							<span class="input-group-text">支扣項目</span>
							<select id="qryItemstyp" name="qryItemstyp">
								<option value="1:PAPOSI">1職等支扣--專業加給及主管加給</option>
								<option value="2:PARANK">2階級支扣 </option>		     					
							</select>
						</div>
						<input type="text" id="items_txtKEY" size="4" class="form-control text-uppercase" placeholder="查詢項" >
						<input type="text" id="items_txtKEY1" size="4" class="form-control" placeholder="查詢項">
						<input type="text" id="items_txtKEY2" size="4" class="form-control" style="display:none;" placeholder="查詢項">
						
						<button id="btn_qryItems" class="btn btn-info form-control" type="button" ><i class="fa fa-search"></i>查詢</button>
						<button id="btn_clearItems" class="btn btn-warning mr-auto form-control" type="button"><i class="fa fa-eraser"></i>清空</button>
						<button id="btn_addcodeItems" class="btn btn-success ml-auto form-control" type="button" data-toggle="modal" data-target="#addItemsDialog"><i class="fa fa-edit"></i>新增支扣數</button>
						
					</form>
				</div>
			</div>
		</div>
 	
		<div class="row">
			<div class="col-md-12">
			<div id="codeItemsDiv" class="codeDiv">
				<table id="tblPosiList" class="table table-striped table-bordered table-hover" style="text-align:center;display:none;">
					<thead>
					</thead>
				</table>
				<table id="tblRankList" class="table table-striped table-bordered table-hover" style="text-align:center;display:none;">
					<thead>
					</thead>
				</table>
				
				</div>
			</div>
		</div>
		<!-- 新增dialog -->
		<div class="modal fade" id="addItemsDialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">

			<form id="subsalItemsForm" >
				<INPUT type="hidden" id="Itemsitem" name ="item">
				<div class="modal-dialog modal-lg">

                   <!-- Modal content-->
                   <div class="modal-content">
                   		 <!-- Modal Header -->
				        <div class="modal-header">
				          <h4 class="modal-title"><span id="model_Items_header_title"></span>支扣項目</h4>
				          <button type="button" class="close" data-dismiss="modal">&times;</button>
				        </div>	
                       <div class="modal-body">
                       	 <div class="container-fluid">
    						<div class="row justify-content-md-center">
	                       		<table id="PARANK" class="tblEditgrp table-responsive-md table-striped table-bordered table-warning" >
	                       			<tr>
										<th class="text-center">階級</th>
										<td>
										<select id="payrankv" name="payrankv" class="RANKCODE selectpicker required" title="階級">
											<option value="">請選擇</option>
						     				<c:forEach var="uitem" items="${alRankCodeTbl}" varStatus="loop">
												<option value="${uitem.rankcode}">${uitem.rankcode}-${uitem.rankname}</option>
											</c:forEach>
										</select>	
										
										</td>
									</tr>
									<tr>
										<th class="text-center">級別</th>
										<td><input type="text" id="paylev1v" name="paylev1v" class="form-control required findkey" value="" maxlength="2" size="2"></input></td>
									</tr>	
									<tr>
										<th class="text-center">俸級</th>
										<td><input type="text" id="paylev2v" name="paylev2v" class="form-control required findkey digits" value=""  maxlength="3" size="3"></input></td>
									</tr>	
									<tr>	
										<th class="text-center">俸點</th>
										<td><input type="text" id="paydegrv" name="paydegrv" class="form-control required findkey digits" value=""  maxlength="6" size="6"></input></td>
									</tr>	
										
									<tr>	
										<th class="text-center">本薪</th>
										<td><input type="text" id="paybasev" name="paybasev" class="form-control required digits" value="0"  maxlength="6" size="6"></input></td>
									</tr>
									<tr>	
										<th class="text-center">公保費</th>
										<td><input type="text" id="paygeinf" name="paygeinf" class="form-control digits" value="0"  maxlength="6" size="6"></input></td>
									</tr>
									<tr>	
										<th class="text-center">新制公保費</th>
										<td><input type="text" id="paygeinn" name="paygeinn" class="form-control digits" value="0"  maxlength="6" size="6"></input></td>
									</tr>
									<tr>	
										<th class="text-center">勞保費</th>
										<td><input type="text" id="paylbinf" name="paylbinf" class="form-control digits" value="0"  maxlength="6" size="6"></input></td>
									</tr>
									<tr>	
										<th class="text-center">健保費</th>
										<td><input type="text" id="paydpinv" name="paydpinv" class="form-control digits" value="0"  maxlength="6" size="6"></input></td>
									</tr>									
									<tr>
										<th class="text-center">退撫基金/離職金</th>
										<td><input type="text" id="payrebef" name="payrebef" class="form-control digits" value="0"  maxlength="6" size="6"></input></td>
									</tr>
									<tr>
										<th class="text-center">新制退撫離職金</th>
										<td><input type="text" id="payreben" name="payreben" class="form-control digits" value="0"  maxlength="6" size="6"></input></td>
									</tr>	
									<tr>	
										<th class="text-center">更新者</th>
										<td><input type="text" id="payuseid" name="payuseid" class="form-control" value=""  maxlength="10" size="10"readOnly></input></td>
									</tr>	
									<tr>	
										<th class="text-center">時戳</th>
										<td><input type="text" id="parank_payupdat" name="payupdat" class="form-control" value="" size="26"  maxlength="26" readOnly></input></td>									
									</tr>						
								</table>
							</div>
							<div class="row justify-content-md-center">
								<table id="PAPOSI" class="tblEditgrp table-responsive-md table-striped table-bordered table-warning">
									<tr>
										<th class="text-center">職等代碼</th>
										<td><input type="text" id="payposik" name="payposik" class="form-control text-uppercase required findkey" value="" maxlength="2" size="2"></input></td>
									</tr>	
									<tr>
										<th class="text-center">最低俸點</th>
										<td><input type="text" id="degrlow" name="degrlow" class="form-control required digits findkey" value=""  maxlength="6" size="6"></input></td>
									</tr>	
									<tr>	
										<th class="text-center">最高俸點</th>
										<td><input type="text" id="degrhigh" name="degrhigh" class="form-control digits" value=""  maxlength="6" size="6"></input></td>
									</tr>
									<tr>
										<th class="text-center">主管特支</th>
										<td><input type="text" id="paypsp5" name="paypsp5" class="form-control digits" value=""  maxlength="6" size="6"></input></td>
									</tr>	
									<tr>
										<th class="text-center">醫師研究(24-1)</th>
										<td><input type="text" id="paypsp1" name="paypsp1" class="form-control digits" value=""  maxlength="6" size="6"></input></td>
									</tr>
									<tr>
										<th class="text-center">醫務技術(26)</th>
										<td><input type="text" id="paypsp2" name="paypsp2" class="form-control digits" value=""  maxlength="6" size="6"></input></td>
									</tr>
									<tr>
										<th class="text-center">行政人員(1)</th>
										<td><input type="text" id="paypsp3" name="paypsp3" class="form-control digits" value=""  maxlength="6" size="6"></input></td>
									</tr>
									<tr>
										<th class="text-center">資訊人員(20)</th>
										<td><input type="text" id="paypsp4" name="paypsp4" class="form-control digits" value=""  maxlength="6" size="6"></input></td>
									</tr>
									<tr>
										<th class="text-center">醫學工程(3)</th>
										<td><input type="text" id="paypsp6" name="paypsp6" class="form-control digits" value=""  maxlength="6" size="6"></input></td>
									</tr>
									<tr>
										<th class="text-center">工程人員(7)</th>
										<td><input type="text" id="paypsp7" name="paypsp7" class="form-control digits" value=""  maxlength="6" size="6"></input></td>
									</tr>
									<tr>
										<th class="text-center">社工(2)</th>
										<td><input type="text" id="paypsp8" name="paypsp8" class="form-control digits" value=""  maxlength="6" size="6"></input></td>
									</tr>
									<tr>	
										<th class="text-center">更新者</th>
										<td><input type="text" id="payposid" name="payposid" class="form-control" value=""  maxlength="10" size="10"readOnly></input></td>
									</tr>	
									<tr>	
										<th class="text-center">時戳</th>
										<td><input type="text" id="paposi_payupdat" name="payupdat" class="form-control" value="" size="26"  maxlength="26" readOnly></input></td>									
									</tr>					
								</table>
							</div>
							
						</div>
                       </div>
                       <!-- Modal footer -->
				        <div class="modal-footer">
                           <button type="button" class="btn btn-primary" id="btn_subTblItemsIns"><i class="fa fa-save"></i>確認儲存</button>
                           <button type="button" class="btn btn-primary" id="btn_subTblItemsUpd"><i class="fa fa-refresh"></i>更改</button>
                           <button type="button" class="btn btn-secondary" data-dismiss="modal"><i class="fa fa-undo"></i>返回</button>
                       </div>
                   </div>
               </div>
			</form>
        </div>
		<!-- 新增dialog -->  
  	</div>
  </div>
  <!-- 工作獎金 -->
  <div class="tab-pane fade" id="byWkpfMain" role="tabpanel" aria-labelledby="pills-wkpf-tab">
  	<div class="container-sm">
 		<!-- 輸入 查詢 項目 -->
		<div class="row">
			<div class="col-12">
	    		<div class="form-group">
					<form class="form-inline"><!-- form-inline 文字在同一行 -->
						<div class="input-group">
							<span class="input-group-text">工作獎金</span>
							<select id="qryWkpftyp" name="qryWkpftyp">
								<option value="1:PACSPF">1工作獎金績分</option>
								<option value="2:PAPFTL">2全院績效彙總</option>										     					
							</select>
						</div>
						<input type="text" id="Wkpf_txtKEY" size="4" class="form-control text-uppercase" placeholder="查詢項" >
						<select id="cspf_jt" name="cspf_jt" class="form-control JTCODE" style="display:none;" title="職稱">
							<option value="">請選擇</option>
		     				<c:forEach var="uitem" items="${alTitleCodeTbl}" varStatus="loop">
								<option value="${uitem.paytitlk}">${uitem.patitsub}-${uitem.paytitlk}-${uitem.paytitlc}</option>
							</c:forEach>
						</select>
						<button id="btn_qryWkpf" class="btn btn-info form-control" type="button" ><i class="fa fa-search"></i>查詢</button>
						<button id="btn_clearWkpf" class="btn btn-warning mr-auto form-control" type="button"><i class="fa fa-eraser"></i>清空</button>
						<button id="btn_addcodeWkpf" class="btn btn-success ml-auto form-control" type="button" data-toggle="modal" data-target="#addWkpfDialog"><i class="fa fa-edit"></i>新增</button>
						
					</form>
				</div>
			</div>
		</div>
 	
		<div class="row">
			<div class="col-md-12">
			<div id="codeWkpfDiv" class="codeDiv">
				<table id="tblPftlList" class="table table-striped table-bordered table-hover" style="text-align:center;display:none;">
					<thead>
					</thead>
				</table>
				<table id="tblCspfList" class="table table-striped table-bordered table-hover" style="text-align:center;display:none;">
					<thead>
					</thead>
				</table>
				
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
				          <h4 class="modal-title"><span id="model_Wkpf_header_title"></span>支扣項目</h4>
				          <button type="button" class="close" data-dismiss="modal">&times;</button>
				        </div>	
                       <div class="modal-body">
                       	 <div class="container-fluid">
    						<div class="row justify-content-md-center">
	                       		<table id="PAPFTL" class="tblEditgrp table table-striped table-bordered table-condensed table-responsive table-sm" style="text-align:center;">
									<thead class="table-primary">
										<tr class="div-green">
											<th class="table-title">年度月份</th>
											<th class="table-title">總點數</th>
											<th class="table-title">契約人員</th>
											<th class="table-title">正職</th>
											<th class="table-title">住院醫師</th>										
											<th class="table-title">聘實</th>
											<th class="table-title">契約護理</th>										
											<th class="table-title">時戳</th>										
										</tr>
									</thead>
									<tbody>
										<tr>
											<td><input type="text" id="paypfmon" name="paypfmon" class="form-control digits findkey required" value="" size="6" maxlength="6"></input></td>
											<td><input type="text" id="paypfpf" name="paypfpf" class="form-control digits" value="" size="7"  maxlength="7"></input></td>
											<td><input type="text" id="paypfunt" name="paypfunt" class="form-control digits required" value="" size="3"  maxlength="3" ></input></td>
											<td><input type="text" id="papfamt" name="papfamt" class="form-control digits required" value="" size="3"  maxlength="3" ></input></td>
											<td><input type="text" id="paypfrev" name="paypfrev" class="form-control digits " value="" size="3"  maxlength="3"></input></td>
											<td><input type="text" id="paypfd" name="paypfd" class="form-control digits " value="" size="3"  maxlength="3"></input></td>
											<td><input type="text" id="paypfe" name="paypfe" class="form-control digits " value="" size="3"  maxlength="3"></input></td>										
											<td><input type="text" id="paypfymd" name="paypfymd" class="form-control" value="" size="26"  maxlength="26" readOnly></input></td>
										</tr>
									</tbody>							
								</table>
							</div>
							<div class="row justify-content-md-center">
								<table id="PACSPF" class="tblEditgrp table table-striped table table-striped table-bordered" style="text-align:center;">
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
											<td><input type="text" id="pacskey" name="pacskey" class="form-control findkey required" value="" size="7" maxlength="7"></input></td>
											<td><input type="text" id="pacsbase" name="pacsbase" class="form-control digits" value="" size="4"  maxlength="4"></input></td>
											<td><input type="text" id="pacsdata" name="pacsdata" class="form-control digits" value="" size="4"  maxlength="4" ></input></td>
											<td><input type="text" id="pacsname" name="pacsname" class="form-control required" value="" size="11"  maxlength="22" ></input></td>
											<td><input type="text" id="zpfcspf" name="zpfcspf" class="form-control" value="" size="26"  maxlength="26"readOnly></input></td>
										</tr>
									</tbody>							
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
		<!-- 新增dialog -->  
  	</div>
  </div>  
</div>

</body>
</html>