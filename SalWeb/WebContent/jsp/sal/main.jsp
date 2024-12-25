<!DOCTYPE HTML>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<title>手術流程</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<jsp:include page="../include.jsp"></jsp:include>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<style>
	/* ui設定 */
	.dataTables_wrapper {font-family: tahoma;font-size: 12px;}
	.ui-widget-header{font-size: 11px !important;}
	.dataTables_wrapper .ui-toolbar{padding:2px;}
	.ui-widget-content{font-size: 12px !important;} 
	.ui-datepicker{ width: 17em; padding: .2em .2em 0; display: none; font-size: 70%!important;}
	.ui-widget input, .ui-widget select, .ui-widget textarea, .ui-widget button{font-size: 12px !important;font-family: Verdana, Arialx !important;}
	.alignCenter { text-align: center !important;}
	.alignRight { text-align: right !important;}
	
#tabs { 
    height:98%;
    margin-top:0px;
    margin-left:0px;
    margin-right:auto;
    margin-bottom:0px; 
    border: 0px solid 000000;}
</style>
<script>
var oTable;
var uTable;
var tr;
var rowIndex;
var rowData;

$(function(){
	$("#tabs").tabs();
	
	$.getOpRoleDeptUI = function(flag){
		
		$.ajax({
			url:"${pageContext.request.contextPath}/opsController.do",
			data:{
				method  : 'getOpRoleDept',
				flag:flag,
				selCode:$('#cCode').val()},
			type:"POST",			//type:POST, GET(default:GET)
			dataType:"json",	//dataType: text, xml, html, script, json, jsonp
			contentType: "application/x-www-form-urlencoded;charset=utf-8",
			success:function(data) {
				if (data.alOpRoleDept[0]!=null){
					$('#aDEPT').empty();
					$('#aDEPT')
				          .append($('<option>', { value : '' })
				          .text('')); 
					$.each(data.alOpRoleDept, function(idx) {   
					     $('#aDEPT')
				          .append($('<option>', { value : data.alOpRoleDept[idx].dept })
				          .text(data.alOpRoleDept[idx].dept)); 
					});
					$('.COST').empty();
					$('.COST')
				          .append($('<option>', { value : '' })
				          .text('')); 
					$.each(data.alCost, function(idx) {   
					     $('.COST')
				          .append($('<option>', { value : data.alCost[idx].dept })
				          .text(data.alCost[idx].dept+"-"+data.alCost[idx].username)); 
					});
				}
			}  
		});
	};

	$.getOpRoleDeptUI("N");
		
	//jQuery按紐圖示
	$(".btn").button().click(
		function(event) {event.preventDefault();}
	);
	$(".btn").click(function(){
		var id = $(this).attr('id');
		if(id == 'aclearbtn'||id == 'cclearbtn'){
			$(":text,textarea").val("");
			$("#opRoleList").dataTable().fnClearTable();
			$("#aDEPT").prop("selectedIndex", 0);
			$("label.error").hide();
 			$(".error").removeClass("error");
 			$(".codeDiv").hide();
		}
		else if(id == 'asendbtn'){		//手術室角色分派_查詢
			var deptId = $('#aDEPT').val();
			var userId = $('#aUSERID').val();
			$.ajax({
				url:"${pageContext.request.contextPath}/opsController.do",
				data:{
					method  : 'qryOpRoleDept',
					deptId  :deptId,
					userId  :userId},
				type:"POST",	//type:POST, GET(default:GET)
				dataType:"json",	//dataType: text, xml, html, script, json, jsonp
				contentType: "application/x-www-form-urlencoded;charset=utf-8",  
				success:function(data) {
					//更新查詢清單
					oTable = $('#opRoleList').dataTable();															
					oTable.fnClearTable(0);		//清空資料		
					if (data.oproleData.length>0){	
						oTable.fnAddData(data.oproleData);//重新給資料
					}
					oTable.fnDraw();					
				},
				error: function(xhr){					
					alert("查詢失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
				}
			});
		}
		else if(id=='aAddOnebtn'){					//手術室角色分派 新增 啟動dialog
			$("label.error").hide();
 			$(".error").removeClass("error");
			$("#bUSERID").val("");
			$("#bUSERNAME_show").val("");
			$("#bUSERNAME").val("");
			$("#bPASSWORD").val("");
			$("#bDEPT").val("");
			$("#bAUTHORITY").val("");
			$("#bZOPROLE").val("");
			$("#opRoleDialog").show();
			$("#opRoleDialog").dialog("open");
			$("#subopRoleUpdbtn").prop("disabled", true);
			$("#subopRoleInsbtn").prop("disabled", false);
			$("#bUSERID").prop('readonly', false) ;
		}
		else if(id=='subopRoleInsbtn'||id== 'subopRoleUpdbtn'){					//手術室角色分派 新增一筆 更改
			if ($("#subopRoleForm").valid()){
				msg = $.checkFields(id);
				if(msg == ''){
					$(":text,textarea,select,#subopRoleForm").prop("disabled", false);
					if (id =='subopRoleUpdbtn')		
						$('#bitem').val("update");
					else if (id =='subopRoleInsbtn')		
						$('#bitem').val("insert");
					oTable = $('#opRoleList').dataTable().api();	
					info = oTable.page.info();		//記錄目前頁碼
					currentPage=info.page;
					
					$.ajax({
						url:"${pageContext.request.contextPath}/opsController.do",
						data:$('#subopRoleForm').serialize(),
						type:"POST",			//type:POST, GET(default:GET)
						dataType:"json",	//dataType: text, xml, html, script, json, jsonp
						contentType: "application/x-www-form-urlencoded;charset=utf-8",  
						success:function(data) {
							if (data.status!=null||typeof(data.status)!="undefined"){	//show msg,dialog不用關閉
								alert(data.msg);
							}else{
								$("#opRoleDialog").dialog("close");     	
								oTable = $('#opRoleList').dataTable();//更新查詢清單
								oTable.fnClearTable(0);		//清空資料
								
								$("#asendbtn").trigger( "click" );  
								oTable.fnPageChange(currentPage);		//切到原先某碼
								
								if (data.msg!=null){
									alert(data.msg);
								}
							}	
						},
						error: function(xhr){					
							alert("手術室角色分派 "+$('#bmethod').val()+"失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
						}
					});
				}else{
					alert(msg);
					return false;
				}
			}
		}
		else if(id == 'csendbtn'){		//代碼維護_查詢
			var codeId = $('#cCode').val();
			var txtKEY = $('#txtKEY').val();	
			$.ajax({
				url:"${pageContext.request.contextPath}/opsController.do",
				data:{
					method  : 'qryOpCodeList',
					codeId  :codeId,
					txtKEY  :txtKEY},
				type:"POST",	//type:POST, GET(default:GET)
				dataType:"json",	//dataType: text, xml, html, script, json, jsonp
				contentType: "application/x-www-form-urlencoded;charset=utf-8",  
				success:function(data) {
					//更新查詢清單
					if (codeId=="1"){	//身份代碼
						oTable = $('#codeIdList').dataTable(objCopyB);
						$("#codeIdDiv").show();
					}else if (codeId=="2"){	//病房代碼
						oTable = $('#codeWardList').dataTable();
					}
					oTable.fnClearTable(0);		//清空資料		
					if (data.codeData.length>0){	
						oTable.fnAddData(data.codeData);//重新給資料
					}
					oTable.fnDraw();					
				},
				error: function(xhr){					
					alert("查詢失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
				}
			});
		}
		else if(id=='cAddOnebtn'){					//代碼維護_新增 啟動dialog
 			$("label.error").hide();
  			$(".error").removeClass("error");
  			  					
			<c:choose>
				<c:when test="${selCode == '1'}">
					$("#cid").val("");
					$("#cid_name").val("");
					$("#cid").prop('readonly', false) ;
				</c:when>
			</c:choose>	  			
 			
			$("#opCodeDialog").show();
			$("#opCodeDialog").dialog("open");
			$("#subopCodeUpdbtn").prop("disabled", true);
			$("#subopCodeInsbtn").prop("disabled", false);
		}
		else if(id=='subopCodeUpdbtn'||id== 'subopCodeInsbtn'){					//代碼維護 新增一筆 更改
			if ($("#subopCodeForm").valid()){
				msg = $.checkFields(id);
				if(msg == ''){
					$(":text,textarea,select,#opCodeDialog").prop("disabled", false);
					$('#cselCode').val($('#cCode').val());
					if (id =='subopCodeUpdbtn')		
						$('#citem').val("update");
					else if (id =='subopCodeInsbtn')		
						$('#citem').val("insert");
					<c:choose>
						<c:when test="${selCode == '1'}">	
							oTable = $('#opRoleList').dataTable().api();
							uTable = $('#opRoleList').dataTable();
						</c:when>
					</c:choose>	  	
					info = oTable.page.info();		//記錄目前頁碼
					currentPage=info.page;
					
					$.ajax({
						url:"${pageContext.request.contextPath}/opsController.do",
						data:$('#subopCodeForm').serialize(),
						type:"POST",			//type:POST, GET(default:GET)
						dataType:"json",	//dataType: text, xml, html, script, json, jsonp
						contentType: "application/x-www-form-urlencoded;charset=utf-8",  
						success:function(data) {
							if (data.status!=null||typeof(data.status)!="undefined"){	//show msg,dialog不用關閉
								alert(data.msg);
							}else{
								$("#opCodeDialog").dialog("close");     	
								uTable.fnClearTable(0);		//清空資料
								
								$("#csendbtn").trigger( "click" );  
								uTable.fnPageChange(currentPage);		//切到原先某碼
								
								if (data.msg!=null){
									alert(data.msg);
								}
							}	
						},
						error: function(xhr){					
							alert("代碼維護"+$('#bmethod').val()+"失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
						}
					});
				}else{
					alert(msg);
					return false;
				}
			}
		}	
	});
		
	//編輯欄位檢查
  	$.checkFields = function(btn){
  		msg = '';
  		if (btn== 'subopRoleInsbtn'||btn== 'subopRoleUpdbtn'){//手術室角色分派 新增一筆 更改
  			if (typeof($("#bDEPT option:selected").val())=="undefined")
				msg += '['+$("#bDEPT").attr('title')+']不得為空白 \n\r';
			if ($("#bUSERID").val()=="")
				msg += '[卡號]不得為空白 \n\r';	
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
			url:"${pageContext.request.contextPath}/opsController.do",
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
								url:"${pageContext.request.contextPath}/opsController.do",
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
		$("#bUSERID").prop('readonly', true) ;
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
							{ "mData": "id"},		//0身分代碼
							{ "mData": "id_name"},	//1身分中文
							{ "mData": null }
							];
	objCopyB = $.extend(true,objCopyB, {
				 "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
						$('td:eq(0)', nRow).html( '<a href = "#codeIdList" class= "codeIdList">'+aData.id+'</a>' );
 						$('td:eq(2)', nRow).html( '<button class= "delcodeIdbtn">刪除</button>');
				 }, "fnDrawCallback": function () {					 
				 	//等datatable draw完成後，補上button 初始化 
				 	//jQuery按鈕圖示，先取消onclick預設的動作
					//刪除	
					$(".delcodeIdbtn")
						.css({
							  "text-align": "center",
							  "background": "#ffe69e",
							  "font-size": "12px" 
							})
						.click(function(){	
							oTable = $('#codeIdList').dataTable().api();
					        tr = $(this).parents('tr');
					        rowIndex = oTable.row(tr).index();
					        rowData = oTable.row(tr).data();//抓取目前點選link這筆row的data
					        
					        $.ajax({
								url:"${pageContext.request.contextPath}/opsController.do",
								data:{
										method  : 'delcodeId',
										key     :rowData.id
								},
								type:"GET",			//type:POST, GET(default:GET)
								dataType:"json",	//dataType: text, xml, html, script, json, jsonp
								contentType: "application/x-www-form-urlencoded;charset=utf-8",  
								success:function(data) {
									oTable = $('#codeIdList').dataTable();
									oTable.fnDeleteRow(rowIndex);
								},
								error:function(xhr) {
									alert("刪除失敗，程式發生錯誤!!!\nerror code:"+ xhr.status);  
								}
							});
						});	
				 }							
		      });
	//代碼維護
	$('#opCodeDialog').dialog({
		title:"代碼維護",
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
		      
});
</script>
</head>
<body>
<div id="tabs">
 	<ul>
   		<li><a href="#byRole">角色設定</a></li>
   		<li><a href="#byCodeMain">代碼維護</a></li>
 	</ul>
  	<div id="byRole">
   		<table class="multiA" style="margin-left:auto;margin-right:auto;">
			<tr>
				<th>單位</th>		
				<td>
					<select id='aDEPT' name="aDEPT" >
					    <OPTION></OPTION>
      					    <c:forEach var="uitem" items="${alOpRoleDept}" varStatus="loop">
							<option value="${uitem.dept}">${uitem.dept}-${uitem.username}</option>
						</c:forEach>
					</select>
				</td>
				<th>卡號</th>		
				<td><input type="text" id="aUSERID" name="USERID" title="請輸入[卡號]" maxlength="5" size="5"/>
				</td>
				<td><input type="button" id="asendbtn" class="btn" value="查詢">						
				<input type="button" id="aAddOnebtn" class="btn" value="新增一筆">
				<input type="button" id="aclearbtn" class="btn" value="清除"></td>
				
			</tr>
		</table>
		
		<table id="opRoleList" class="display" style="margin-left:auto;margin-right:auto;">
			<thead>
				<tr>
					<th>卡號</th>
					<th>姓名</th>
					<th>單位代碼</th>
					<th>權限</th>
					<th>建檔時間</th>
					<th></th>
				</tr>
			</thead>
		</table>
 	</div>
 	<!-- dialog 顯示詳細查詢資訊(手術室角色分派)-->
	<div id="opRoleDialog" style="display: none" align="center">
		<form id="subopRoleForm" method="POST">
			<INPUT type="hidden" name ="method" value="updOpRoleDept">
			<INPUT type="hidden" id="bitem" name ="item" value="">
			<TABLE class="multiA">
				<tr><td colspan="2" style="text-align: center">
					<input type="button" id="subopRoleUpdbtn" class="btn" value="更改">
					<input type="button" id="subopRoleInsbtn" class="btn" value="新增一筆"></td>
				</tr>
				<TR><TH>卡號</TH><Td><input type="text" id="bUSERID" name="USERID" class="searchNm" title="請輸入[卡號]" maxlength="5" size="5" readonly/>
									<span id="bUSERNAME_show"></span>
									<input type="hidden" id="bUSERNAME" name="USERNAME" >
								</Td></tr>
				<TR><th>密碼</th><Td><input type="text" id="bPASSWORD" name="PASSWORD"  size="5" maxlength="5" />
				</Td></tr>
				<TR><TH>單位</TH><Td>
					<select id='bDEPT' name="DEPT" class="COST required" title="請輸入[單位代碼]">
						<c:forEach var="uitem" items="${alCost}" varStatus="loop">
							<option value="${uitem.dept}">${uitem.dept}-${uitem.username}</option>
						</c:forEach>
					</select>
				</Td></tr>
				<TR><TH>權限</TH><Td>
					<select id='bAUTHORITY' name="AUTHORITY" title="請輸入[權限]" class="required">
						<option value="C">C-一般使用者</option>
     					<option value="B">B-單位管理者</option>
     					<c:if test="${gAdmin eq 'Y'}">
     						<option value="A">A-系統管理者</option>
						</c:if>
					</select>
					</Td></tr>
				<TR><th>更改者</th><Td><input type="text" id="bUPDID" name="UPDID"  size="4" maxlength="4" readonly/>	
				<TR><TH>最後更改時間</TH><Td><input type="text" id="bZOPROLE" name="ZOPROLE" maxlength="19" size="19" readonly/></Td>	</tr>
			</TABLE>
		</form>
	</div>
 	<div id="byCodeMain">
 		<table class="multiA" style="margin-left:auto;margin-right:auto;">
			<tr>
				<th>代碼選項</th>		
				<td>
					<select id='cCode' name="cCode" >
					    <option value="1">1身分代碼</option>
      					<option value="2">2病房代碼</option>
      					<option value="3">3單位代碼</option>
      					<option value="4">4麻醉代碼</option>
      					<option value="5">5開刀種類代碼</option>
      					<option value="6">6異常原因代碼</option>
      					<option value="7">7手術取消原因代碼</option>
      					<option value="8">8術前抗生素</option>
      					<option value="9">9術前訪視</option>
      					<option value="10">10部分負擔身分代碼</option>
      					<option value="11">11恢復室特殊事件代碼</option>
					</select>
				</td>
				<th>KEY</th>		
				<td><input type="text" id="txtKEY" name="txtKEY" title="請輸入[KEY]" maxlength="5" size="5"/>
				</td>
				<td><input type="button" id="csendbtn" class="btn" value="查詢">						
				<input type="button" id="cAddOnebtn" class="btn" value="新增一筆">
				<input type="button" id="cclearbtn" class="btn" value="清除"></td>
			</tr>
			
		</table>
		<div id="codeIdDiv" class="codeDiv" style="display: none;">
			<table id="codeIdList" class="display" style="margin-left:auto;margin-right:auto;">
				<thead>
					<tr>
						<th>身分代碼</th>
						<th>身分中文</th>
						<th></th>
					</tr>
				</thead>
			</table>
		</div>
		<div id="codeIdDiv" class="codeDiv" style="display: none;">
			<table id="codeWardList" class="display" style="margin-left:auto;margin-right:auto">
				<thead>
					<tr>
						<th>病房代碼</th>
						<th></th>
					</tr>
				</thead>
			</table>
		</div>	
  	</div>
  	<!-- dialog 顯示詳細查詢資訊(代碼維護)-->
	<div id="opCodeDialog" style="display: none" align="center">
		<form id="subopCodeForm" method="POST">
			<INPUT type="hidden" name ="method" value="updOpCodeTbl">
			<INPUT type="hidden" id="citem" name ="item">
			<INPUT type="hidden" id="cselCode" name ="selCode">
			<TABLE class="multiA">
				<tr><td colspan="2" style="text-align: center">
					<input type="button" id="subopCodeUpdbtn" class="btn" value="更改">
					<input type="button" id="subopCodeInsbtn" class="btn" value="新增一筆"></td>
				</tr>
				${selCode}
				<c:choose>
					<c:when test="${selCode == '1'}">
						<TR><TH>身分代碼</TH>
						<Td><input type="text" id="cid" name="id" title="請輸入[身分代碼]" maxlength="4" size="4" readonly/>
						<TR><TH>身分中文</TH>
						<Td><input type="text" id="cid_name" name="id_name" title="請輸入[身分中文]" maxlength="10" size="10" />
					</c:when>
				</c:choose>	
				
			</TABLE>
		</form>
	</div>
</div>
</body>
</html>