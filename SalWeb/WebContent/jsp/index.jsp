<!DOCTYPE HTML>
<%@page language="java" contentType="text/html; charset=UTF-8"pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>  

<html>
<head>
<title>薪資作業管理</title>
<jsp:include page="include.jsp"></jsp:include>
<style type="text/css">

body {
    font-family: "Lato","Helvetica Neue",Helvetica,Arial,sans-serif;
    font-size: 15px;
    line-height: 1.4285;
    color: #2c3e50;
    background-color: #ffffff;
}
.text-success, .text-success:hover {
    color: #18bc9c;
    font-size:26px;
    text-decoration: none;

}
#menulist {
    margin-top: 20px;
    margin-bottom: 3px;
    margin-left: 10px;
}
#menulist li {
    list-style: none;
    float: left;
    width: 33%;

}
#menulist .card-body a {
    color: black;
    padding: 6px 8px;
    text-decoration: none;
    display: block;
    text-align: left;
}
.list-group-item {
    position: relative;
    display: block;
    padding: 10px 15px;
    margin-bottom: -1px;
    background-color: #ffffff;
    border: 1px solid #ecf0f1;
    text-decoration: none;
    color:#2c3e50
}
.card-body a:hover {
	color:black;
	background-color: #e6faff;
}
.panel-body {
    padding: 17px;
    border:1px solid #769dc0
}
</style>
<script>
var agent = "${sessionScope.agent}";	//代理人卡號
var paUserid = ("" != agent) ? agent : userid;            //使用者卡號
var paUsername = ("" != agent) ? "${sessionScope.agentname}" : username;            //使用者姓名
var pahid = ("" != agent) ? "${sessionScope.agenthid}" : hid;            //醫院別
var pacostid = ("" != agent) ? "${sessionScope.agentcostid}" : costid;            //使用者成本中心
var padeptnm = ("" != agent) ? "${sessionScope.agentdeptnm}" : deptnm;            //使用者單位名稱
var padeptno = ("" != agent) ? "${sessionScope.agentdeptno}" : deptno;            //使用者單位名稱 英文
var pa_admin = ("" != agent) ? "${sessionScope.agentAdmin}" : "${sessionScope.user.getPa_adm()}";  
var pa_edit  = ("" != agent) ? "${sessionScope.agentEdit}" : "${sessionScope.user.getPa_edit()}";          
var pa_view = ("" != agent) ? "${sessionScope.agentView}" : "${sessionScope.user.getPa_view()}";
var pa_tax = ("" != agent) ? "${sessionScope.agentTax}" : "${sessionScope.user.getPa_tax()}";
var pa_bones = ("" != agent) ? "${sessionScope.agentBones}" : "${sessionScope.user.getPa_bones()}";

console.log("pa_admin:"+pa_admin+" pa_edit:"+pa_edit+" pa_tax:"+pa_tax+" pa_bone:"+pa_bones);
	
</script>
</head>
<body >
					
<div class='container-fluid'>
	<div class="card-deck">
		<!-- 例行作業 -->
		<c:choose>
			<c:when	test="${sessionScope.agent eq null or empty sessionScope.agent}"><!--代理 -->
				<c:if test="${ sessionScope.user.getPa_adm()=='Y' or (sessionScope.user.getPa_edit()=='Y') or 
		                     (sessionScope.user.getPa_view()=='Y') }">
				
					<div class="card">
						<div class="card-header">
							<img border="0" src="<c:url value="/resources/images/plan.png"/>" width="45" height="45" style="display:inline;">		 		
							<span class="text-success" style="display: inline;"><b>例行作業</b></span>
						</div>
			  	    	<div class="card-body">
					      	<div class="list-group">
					      		<a class="list-group-item list-group-item-action" id="basePay" href="<c:url value="/jsp/sal/psnBasic.jsp"/>"><span class="big-size">基本資料</span></a>
					      		<a class="list-group-item list-group-item-action" id="tranPay" href="<c:url value="/jsp/sal/tranPay.jsp"/>"><span class="big-size">異動資料</span></a>
					      		<a class="list-group-item list-group-item-action" id="batchPay" href="<c:url value="/jsp/sal/batchPay.jsp"/>"><span class="big-size">批次作業</span></a>
					      		<a class="list-group-item list-group-item-action" id="denailPay" href="<c:url value="/jsp/sal/payDetail.jsp"/>"><span class="big-size">發薪明細/檢驗</span></a>
					      		<a class="list-group-item list-group-item-action" id="paynhi" href="<c:url value="/jsp/sal/paynhi.jsp"/>"><span class="big-size">補充保險費</span></a>
					      		<a class="list-group-item list-group-item-action" id="labretir" href="<c:url value="/jsp/sal/labretir.jsp"/>"><span class="big-size">x勞工退休金</span></a>
					      		<a class="list-group-item list-group-item-action" id="paydebt" href="<c:url value="/jsp/sal/paydebt.jsp"/>"><span class="big-size">x清償債款</span></a>					      									  	
							</div>
				    	</div>
					</div>
				</c:if>					
			</c:when>
			<c:otherwise>
				<c:if test="${ sessionScope.agentEdit=='Y' or (sessionScope.agentView=='Y')  }"><!--代理 -->
					<div class="card">
						<div class="card-header">
							<img border="0" src="<c:url value="/resources/images/plan.png"/>" width="45" height="45" style="display:inline;">		 		
							<span class="text-success" style="display: inline;"><b>例行作業</b></span>
						</div>
			  	    	<div class="card-body border-success">
					      	<div class="list-group">
					      		<a class="list-group-item list-group-item-action" id="basePay" href="<c:url value="/jsp/sal/psnBasic.jsp"/>"><span class="big-size">基本資料</span></a>
					      		<a class="list-group-item list-group-item-action" id="tranPay" href="<c:url value="/jsp/sal/tranPay.jsp"/>"><span class="big-size">異動資料</span></a>
					      		<a class="list-group-item list-group-item-action" id="batchPay" href="<c:url value="/jsp/sal/batchPay.jsp"/>"><span class="big-size">批次作業</span></a>
					      		<a class="list-group-item list-group-item-action" id="detailPay" href="<c:url value="/jsp/sal/payDetail.jsp"/>"><span class="big-size">發薪明細/檢驗</span></a>
					      		<a class="list-group-item list-group-item-action" id="paynhi" href="<c:url value="/jsp/sal/paynhi.jsp"/>"><span class="big-size">補充保險費</span></a>
					      		<a class="list-group-item list-group-item-action" id="labretir" href="<c:url value="/jsp/sal/labretir.jsp"/>"><span class="big-size">x勞工退休金</span></a>
					      		<a class="list-group-item list-group-item-action" id="paydebt" href="<c:url value="/jsp/sal/paydebt.jsp"/>"><span class="big-size">x清償債款</span></a>
							</div>
				    	</div>
					</div>							               
		        </c:if>       
			</c:otherwise>
		</c:choose>
			
		<!-- 所得稅 -->
		<c:choose>
			<c:when	test="${sessionScope.agent eq null or empty sessionScope.agent}">
				<c:if test="${ sessionScope.user.getPa_adm()=='Y' or (sessionScope.user.getPa_edit()=='Y') or 
		                     (sessionScope.user.getPa_view()=='Y') or (sessionScope.user.getPa_tax()=='Y') }">
				
					<div class="card">
						<div class="card-header">
							<img border="0" src="<c:url value="/resources/images/if_Analytics_379550.png"/>" width="45" height="45" style="display:inline;">		 		
							<span class="text-success" style="display: inline;"><b>所得稅</b></span>
						</div>
			  	    	<div class="card-body">
					      	<div class="list-group">
					      		<a class="list-group-item list-group-item-action" id="byTax" href="<c:url value="/jsp/sal/payTax.jsp"/>"><span class="big-size">所得稅</span></a>
							</div>
				    	</div>
					</div>
				</c:if>					
			</c:when>
			<c:otherwise>
				<c:if test="${ sessionScope.agentEdit=='Y' or (sessionScope.agentView=='Y') or (sessionScope.agentTax=='Y') }"><!--代理 -->
                     <div class="card">
						<div class="card-header">
							<img border="0" src="<c:url value="/resources/images/if_Analytics_379550.png"/>" width="45" height="45" style="display:inline;">		 		
							<span class="text-success" style="display: inline;"><b>所得稅</b></span>
						</div>
			  	    	<div class="card-body">
					      	<div class="list-group">
					      		<a class="list-group-item list-group-item-action" id="byTax" href="<c:url value="/jsp/sal/payTax.jsp"/>"><span class="big-size">所得稅</span></a>
							</div>
				    	</div>
					</div>
		        </c:if>             
			</c:otherwise>
		</c:choose>
		
		<!-- 員工績效 -->
		<c:choose>
			<c:when	test="${sessionScope.agent eq null or empty sessionScope.agent}">
				<c:if test="${ sessionScope.user.getPa_adm()=='Y' or (sessionScope.user.getPa_bones()=='Y') }">				
					<div class="card">
						<div class="card-header">
							<img border="0" src="<c:url value="/resources/images/if_Settings-5_379393.png"/>" width="45" height="45" style="display:inline;">		 		
							<span class="text-success" style="display: inline;"><b>員工績效</b></span>
						</div>
			  	    	<div class="card-body">
					      	<div class="list-group">
					      		<a class="list-group-item list-group-item-action" id="bonus2" href="<c:url value="/jsp/sal/bonus.jsp"/>"><span class="big-size">員工績效</span></a>
<%-- 					      		<a class="list-group-item list-group-item-action" id="bonus1" href="<c:url value="/jsp/sal/bonus1.jsp"/>"><span class="big-size">x代碼維護</span></a> --%>
<%-- 					      		<a class="list-group-item list-group-item-action" id="bonus2" href="<c:url value="/jsp/sal/bonus2.jsp"/>"><span class="big-size">x績效彙總設定</span></a>					      		 --%>
							</div>
				    	</div>
					</div>
				</c:if>			
			</c:when>
			<c:otherwise>
				<c:if test="${ (sessionScope.agentBones=='Y') }"><!--代理 -->
                     <div class="card">
						<div class="card-header">
							<img border="0" src="<c:url value="/resources/images/if_Settings-5_379393.png"/>" width="45" height="45" style="display:inline;">		 		
							<span class="text-success" style="display: inline;"><b>員工績效</b></span>
						</div>
			  	    	<div class="card-body">
					      	<div class="list-group">
					      		<a class="list-group-item list-group-item-action" id="bonus2" href="<c:url value="/jsp/sal/bonus.jsp"/>"><span class="big-size">員工績效</span></a>
<%-- 					      		<a class="list-group-item list-group-item-action" id="bonus1" href="<c:url value="/jsp/sal/bonus1.jsp"/>"><span class="big-size">代碼維護</span></a> --%>
<%-- 					      		<a class="list-group-item list-group-item-action" id="bonus2" href="<c:url value="/jsp/sal/bonus2.jsp"/>"><span class="big-size">績效彙總設定</span></a> --%>
							</div>
				    	</div>
					</div>
		        </c:if>             
			</c:otherwise>
		</c:choose>
		
		<!-- 相關功能 -->
		<c:choose>
			<c:when	test="${sessionScope.agent eq null or empty sessionScope.agent}">
				<c:if test="${ sessionScope.user.getPa_adm()=='Y' or (sessionScope.user.getPa_edit()=='Y') }">
				
					<div class="card ">
						<div class="card-header">
							<img border="0" src="<c:url value="/resources/images/manager.png"/>" width="45" height="45" style="display:inline;">		 		
							<span class="text-success" style="display: inline;"><b>相關功能</b></span>
						</div>
			  	    	<div class="card-body ">
					      	<div class="list-group">
					      		<a class="list-group-item list-group-item-action" id="CodeList" href="<c:url value="/jsp/sal/codeSetting.jsp"/>"><span class="big-size">代碼設定</span></a>
					      		<a class="list-group-item list-group-item-action" id="tranPay3" href="<c:url value="/jsp/sal/tranPay.jsp"/>"><span class="big-size">x年度作業</span></a>
					      		<a class="list-group-item list-group-item-action" id="batchPay3" href="<c:url value="/jsp/sal/batchPay.jsp"/>"><span class="big-size">x法令變更</span></a>		      		
							</div>
				    	</div>
					</div>
				</c:if>					
			</c:when>
			<c:otherwise>
				<c:if test="${ sessionScope.agentEdit=='Y' }"><!--代理 -->
					<div class="card">
						<div class="card-header">
							<img border="0" src="<c:url value="/resources/images/manager.png"/>" width="45" height="45" style="display:inline;">		 		
							<span class="text-success" style="display: inline;"><b>相關功能</b></span>
						</div>
			  	    	<div class="card-body">
					      	<div class="list-group">
					      		<a class="list-group-item list-group-item-action" id="CodeList" href="<c:url value="/jsp/sal/codeSetting.jsp"/>"><span class="big-size">代碼設定</span></a>
					      		<a class="list-group-item list-group-item-action" id="tranPay3" href="<c:url value="/jsp/sal/tranPay.jsp"/>"><span class="big-size">x年度作業</span></a>
					      		<a class="list-group-item list-group-item-action" id="batchPay3" href="<c:url value="/jsp/sal/batchPay.jsp"/>"><span class="big-size">x法令變更</span></a>		      		
							</div>
				    	</div>
					</div>
		        </c:if>             
			</c:otherwise>
		</c:choose>
	</div>
</div>

</body>
</html>