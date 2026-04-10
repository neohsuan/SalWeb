<!DOCTYPE HTML>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<html>
<head>
<title>發薪明細/檢驗</title>
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
		if(selCode == "1"){	//績效組_醫師
			codeTbl="tblSALPPFList";
			logtmp="\\\\ap_pd1.vghks.gov.tw\\Apps_Exe\\SALPPF\\DocBas"+tmp+".TXT" ;		  	
	  	}else if (selCode == "2"){	//績效組_員工
	  		codeTbl="tblSALEPFList";
	  		logtmp="\\\\ap_pd1.vghks.gov.tw\\Apps_Exe\\SALEPF\\EmpEff"+tmp+".txt" ;		  	
		}  	  	
	  	$("#explanProd_1").html(logtmp);
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
		if(selCode == "1"){	//績效組_醫師
			codeTbl="tblSALPPFList";
			logtmp="\\\\ap_pd1.vghks.gov.tw\\Apps_Exe\\SALPPF\\DocBas"+tmp+".TXT" ;		  	
	  	}else if (selCode == "2"){	//績效組_員工
	  		codeTbl="tblSALEPFList";
	  		logtmp="\\\\ap_pd1.vghks.gov.tw\\Apps_Exe\\SALEPF\\EmpEff"+tmp+".txt" ;		  	
		}  	  	
	  	$("#explanProd_1").html(logtmp);
	  	$('#'+codeTbl).show();		
		$("#btn_qryCode").trigger( "click" );	
		
	});
		
	$.getLoadCodeTbl = function(item,flag){
		//載入訊息欄說明
		tmp = "";	
		if (pahid == "2A0"){
			tmp = pahid;
		}
		logtmp="\\\\ap_pd1.vghks.gov.tw\\Apps_Exe\\SALPPF\\DocBas"+tmp+".TXT" ;
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
				//單位
				if (data.alDeptCodeTbl[0]!=null){
					select = $('#qryDept,#qry_itmPay_Dept');
					select.empty();
					options = "<option value=''>";
					select.append(options);
					$.each(data.alDeptCodeTbl, function(idx) {
						options = "<option value='" + data.alDeptCodeTbl[idx].padepcod + "'>" + data.alDeptCodeTbl[idx].pacstno+"-"+data.alDeptCodeTbl[idx].padepnmc ;
						select.append(options);
					});
					select.selectpicker('refresh');
				}
				
				//餉單編號
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
				//餉單編號(2個月內)
				if (data.alBankDtTbl_2M[0]!=null){
					select = $('#qry_set_datyr,#prod_SalNum,#prt_SalNum,#print_SalNum,#file_SalNum');
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
  	$.getLoadCodeTbl('bonus','Y');
  	 	
	//編輯欄位檢查
  	$.checkFields = function(btn){
  		msg = '';
  		var isChecked=0;
  		if (btn== 'btn_qryFile'){	//檔案產製
  			if($("#file_cond").val()=='' || typeof($("#file_cond").val())=='undefined' ){
  				msg = '請輸入項目!!';
  			}
  			if($("#file_SalNum").val()=='' || typeof($("#file_SalNum").val())=='undefined')
  			    msg += '[餉單編號_發薪日]請勿空白!!';
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
	};	
	//jQuery按紐圖示
	$(".btn").button().click(
		function(event) {event.preventDefault();}
	);
	$(".btn").click(function(){
		var id = $(this).attr('id');
		
		if(id == 'btn_clnDatPsn'){
			cleanTable("byPayDetailMain");
			$('.selectpicker').selectpicker('val', '');
			$('.selectpicker').selectpicker('refresh');
			$("#TblDatPsnList").dataTable().fnClearTable();
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
					{ "sTitle":"俸點","mData": "paydegr2"},
					{ "sTitle":"現職日","mData": "paysgndt"},
					{ "sTitle":"基本額度","mData": "paypfdoc"},
					{ "sTitle":"專2額度","mData": "paysgndt"},
					{ "sTitle":"VP049","mData": "paysgndt"},
					{ "sTitle":"FP440","mData": "paysgndt"},
					{ "sTitle":"教職獎勵","mData": "paysgndt"},
					{ "sTitle":"教職獎勵_補","mData": "paysgndt"},
					{ "sTitle":"論文獎勵第1篇","mData": "paysgndt"},
					{ "sTitle":"論文獎勵第1篇_補","mData": "paysgndt"},
					{ "sTitle":"論文獎勵第2篇","mData": "paysgndt"},
					{ "sTitle":"論文獎勵第1篇_補","mData": "paysgndt"},
					{ "sTitle":"離職日","mData": "paysgndt"},
					
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
					{ "sTitle":"俸點","mData": "paydegr2"},
					{ "sTitle":"現職日","mData": "paysgndt"},
					{ "sTitle":"基本績分","mData": "paysgndt"},
					{ "sTitle":"俸級績分","mData": "paysgndt"},
					{ "sTitle":"工二","mData": "paysgndt"},
					{ "sTitle":"教職獎勵","mData": "paysgndt"},
					{ "sTitle":"教職獎勵_補","mData": "paysgndt"},
					{ "sTitle":"論文獎勵第1篇","mData": "paysgndt"},
					{ "sTitle":"論文獎勵第1篇_補","mData": "paysgndt"},
					{ "sTitle":"論文獎勵第2篇","mData": "paysgndt"},
					{ "sTitle":"論文獎勵第1篇_補","mData": "paysgndt"},
					{ "sTitle":"工作_住宿津貼","mData": "paysgndt"},
					{ "sTitle":"獎金差額","mData": "paysgndt"},
					{ "sTitle":"總醫師","mData": "paysgndt"},
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
});

</script>
</head>
<body>

<ul class="nav nav-pills mb-3" id="pills-tab" role="tab">
  <li class="nav-item">
    <a class="nav-link active" id="pills-fileProd-tab" data-toggle="pill" href="#byFileProdMain" role="tab" aria-controls="pills-home" aria-selected="true">檔案產製</a>
  </li>
<!--   <li class="nav-item">
    <a class="nav-link" id="pills-fileProd-tab" data-toggle="pill" href="#byFileProdMain" role="tab" aria-controls="pills-profile" aria-selected="false">檔案產製</a>
  </li> -->
</ul>

<div class="tab-content" id="pills-tabContent">
	<!-- 檔案產製 -->
	<div class="tab-pane fade show active" id="byFileProdMain" role="tabpanel" aria-labelledby="pills-fileProd-tab">
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

</body>
</html>