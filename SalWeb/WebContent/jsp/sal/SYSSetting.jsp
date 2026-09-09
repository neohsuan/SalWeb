<!DOCTYPE HTML>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<html>
<head>
<title>系統作業</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<jsp:include page="../include.jsp"></jsp:include>
<script>
//檢查使否有代理人，如有代理人則取代原資料
var agent = "${sessionScope.agent}";	//代理人卡號
var paUserid = ("" != agent) ? agent : userid;            //使用者卡號
var paUsername = ("" != agent) ? "${sessionScope.agentname}" : username;          //使用者姓名
var pahid = ("" != agent) ? "${sessionScope.agenthid}" : hid;                     //醫院別
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
	

  	//代碼選單切換
	$("#qryType1").change(function(){
		$(":text,textarea").val("");
		$("#divfield_ID,#divfield_CARD").addClass("d-none");	//隱藏
  		selCode=(typeof($("#qryType1 option:selected").val().split(':')[0]) == "undefined" )? "1":$("#qryType1 option:selected").val().split(':')[0];
  		console.log(selCode);
  		if (selCode=="1" ){			//1:[整批更新]卡號 
			$("#divfield_ID").removeClass("d-none");			
			$('#msgHintText').text('卡號變更僅更新"基本個人明細"');
		}
		else if (selCode=="2"){		//2:[整批更新]身份證
			$("#divfield_CARD").removeClass("d-none");
			$('#msgHintText').text('身份證變更將更新"基本個人明細、異動支扣明細、批次檔、所得稅個人明細、住址檔"');
		}
  	});
  	 	
	
  	//代碼選單切換
	$("#qryType").change(function(){
		$("#divfield_1,#divfield_2,#divfield_3").addClass("d-none");	//隱藏
		cleanTable("rowBatHanding_1");
  		selCode=(typeof($("#qryType option:selected").val().split(':')[0]) == "undefined" )? "1":$("#qryType option:selected").val().split(':')[0];
  		console.log(selCode);
  		if (selCode=="1"){			//1:[匯入]批次檔 
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
  		
  		if (btn== 'btn_qryBatPay'){	//執行 1.整批更新卡號
  			selCode=(typeof($("#qryType1 option:selected").val().split(':')[0]) == "undefined" )? "1":$("#qryType1 option:selected").val().split(':')[0];
  	  		console.log(selCode);
  		
  			if (selCode=="1"){
  				if( $("#qry_pabatpay_cardno").val()=='' || $("#qry_pabatpay_cardno1").val()=='' || $("#qry_pabatpay_cardno").val().length < 4 || $("#qry_pabatpay_cardno1").val().length < 4 ){
  		  				msg = '請輸入卡號(長度不足4碼)!!';
  		  			}
  			}else if(selCode=="2"){
  				if( $("#qry_pabatpay_idtno").val()=='' || $("#qry_pabatpay_idtno1").val()=='' || $("#qry_pabatpay_idtno").val().length < 10 || $("#qry_pabatpay_idtno1").val().length < 10 ){
		  				msg = '請輸入身份證字號(長度不足10碼)!!';
		  			}
			}
  		}
  		return msg;	
	};

	//jQuery按紐圖示
	$(".btn").button().click(
		function(event) {event.preventDefault();}
	);
	$(".btn").click(function(){
		var id = $(this).attr('id');		
		
		if(id == 'btn_clnBatPay' ){
  			$(":text,textarea").val("");
			return false;
		}else if(id == 'btn_qryBatPay'){	//執行 1.整批更新卡號 2.整批更新身份證
			msg = $.checkFields(id);
			if(msg == ''){
				
				selCode=(typeof($("#qryType1 option:selected").val().split(':')[0]) == "undefined" )? "1":$("#qryType1 option:selected").val().split(':')[0];
	  	  		console.log("執行："+selCode);
	  	  		
	  	  		cardno=$("#qry_pabatpay_cardno").val().toUpperCase();
	  	  		cardno_new=$("#qry_pabatpay_cardno1").val().toUpperCase();
	  	  		idtno=$("#qry_pabatpay_idtno").val().toUpperCase();
	  	  		idtno_new=$("#qry_pabatpay_idtno1").val().toUpperCase();	  	  	
	  	  		console.log("卡號old："+cardno+" 卡號new："+cardno_new+" 身份證old:"+idtno+" 身份證new:"+idtno_new);
	  	  						
				in_data={
						cardno     : cardno,
						cardno_new : cardno_new,
						idtno      : idtno,
						idtno_new  : idtno_new
		        };					
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method	: "updateIDCard",
						editMode : selCode,
						in_data : JSON.stringify(in_data),						
						userid  : paUserid,
						hid     : pahid
					},
					type:"POST",		//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp					
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						if (data.msg!=null){
							alert(data.msg);
						}else{
							alert("TEST");		
						}
					},
					error: function(xhr){					
						alert("更新[整批變更卡號或身份證]失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
					}
				}); 
			}
			else{
				alert(msg);
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
	
	
     
});

</script>
</head>
<body>

<ul class="nav nav-pills mb-3" id="pills-tab" role="tab">
  <li class="nav-item">
    <a class="nav-link active" id="pills-battask-tab" data-toggle="pill" href="#byBatTaskMain" role="tab" aria-controls="pills-home" aria-selected="true">更改作業</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-bathanding-tab" data-toggle="pill" href="#byBatHandingMain" role="tab" aria-controls="pills-home" aria-selected="true">批次匯入</a>
  </li>

</ul>

<div class="tab-content" id="pills-tabContent">
	<!--更改作業-->
  	<div class="tab-pane fade show active" id="byBatTaskMain" role="tabpanel" aria-labelledby="pills-battask-tab">
		<div class="container-fluid">
			<div class="row" id="qryBatchPayItem">
				<div class="col-8">
					<div class="form-inline basother-filterbar">
						<div class="input-group" style="margin-right:10px;">
							<span class="input-group-text">選單</span>		  	
							<select id="qryType1" class="form-control">
								<option value="1:[整批更新]卡號:UpdCard">1整批更新卡號</option>
								<option value="2:[整批更新]身份證:UpdID">2整批更新身份證</option>     					
							</select>
						</div>
						
		    			<div class="input-group " id="divfield_ID" style="margin-right:10px;">		    				
		    				<input type="text" id="qry_pabatpay_cardno" class="text-uppercase form-control" maxlength="4" size="6" placeholder="原卡號" >
		    				<span class="input-group-text">→</span>
		    				<input type="text" id="qry_pabatpay_cardno1" class="text-uppercase form-control" maxlength="4" size="6" placeholder="新卡號" >		    										
						</div>    		
			    		<div class="input-group d-none" id="divfield_CARD" style="margin-right:10px;">			    							
							<input type="text" id="qry_pabatpay_idtno" class="text-uppercase form-control" maxlength="10" size="10" placeholder="原身份證" >
							<span class="input-group-text">→</span>
							<input type="text" id="qry_pabatpay_idtno1" class="text-uppercase form-control" maxlength="10" size="10" placeholder="新身份證" >
						</div>
						<!-- 訊息提示框 -->
						<div class="alert alert-info" id="msgHint" style="display:inline-block; vertical-align:middle; background-color:#d9edf7; border-color:#bce8f1; color:#31708f; padding:6px 12px; margin-bottom:0; font-size:14px; line-height:1.6;">
            				<i class="fa fa-bullhorn"></i><span id="msgHintText">卡號變更僅更新"基本個人明細"</span>
        				</div>
						
					</div>
					<div class="form-inline basother-filterbar" style="margin-top:10px; text-align:left;">
						<button id="btn_qryBatPay" class="btn btn-success" type="button" style="margin-right:8px;">
        					<i class="fa fa-cog"></i> 執行
						</button>
						<button id="btn_clnBatPay" class="btn btn-warning roleQry basother-fixed-btn" type="button">
        					<i class="fa fa-eraser"></i> 清空	
    					</button>
    				</div>
				</div>
			</div>				

 		</div>	
	</div>
	
	<!--批次匯入-->
  	<div class="tab-pane fade show" id="byBatHandingMain" role="tabpanel" aria-labelledby="pills-bathanding-tab">
  		<div class="row p-1 mb-1 bg-secondary">
			<div class="col-md-10">
				<div class="form-inline">
					<div class="input-group">
						<span class="input-group-text">選單</span>
		  	
						<select id="qryType" class="form-control">
							<option value="1:[匯入]階級支扣數:ImpPayInBat">1[匯入]階級支扣數</option>
							<option value="2:[匯入]公務員健保:DelPayInBat">2[匯入]公務員健保</option>     					
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
					   	
					</div>
				</div>
			</div>
		</div>
  	</div>


</div>

</body>
</html>