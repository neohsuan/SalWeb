/** 顯示等待圖示 */
$.showWait = function() {
	$('html').showLoading();
};

/** 隱藏等待圖示 */
$.hideWait = function() {
	$('html').hideLoading();
};
/** 顯示等待圖示-使用者資訊異常，請重新點選上方功能列或重新登入 */
$.showWaitError = function() {
	$('html').showLoading("","1");
};

//-------------------按紐CSS處理---------------------------------------------------------------------------
/**
 * 改變按鈕選單CLASS
 */
function changeClass(obj) {
    var objs=document.getElementById("TRID");
    var tagObjs = objs.getElementsByTagName("INPUT");
	for (var i=0; i<tagObjs.length; i++){
		tagObjs[i].className ="buttonStyle";
	}
    obj.className ="buttonStyleB";
}
//-------------------按紐CSS處理---------------------------------------------------------------------------

/*依卡號取得RSA資訊*/
$.getRSAInfo = function(CardNO){
	var rsaInfo = "";
	$.ajax({
		url:"${pageContext.request.contextPath}/itAction.do",
		data:{"reqCode":"getRSAInfo","CardNO":CardNO},
		contentType: "application/x-www-form-urlencoded;charset=UTF-8",//加這個存中文才不會變亂碼
     async	:false,//逐步執行		
		error:function(xhr) {
			var show = "Status code: " + xhr.status + "\r\nException Message: " + xhr.responseText;	
			alert("錯誤-"+show);
		},
		success:function(data) {
			rsaInfo=data;
		}
	});
	parent.postMessage("reset","*");
	return rsaInfo ;
};

//檢查session資料是否正確
$.checksession = function(userid){
    if ( userid!="") {
		$.ajax({
			url:"${pageContext.request.contextPath}/itAction.do",
			data:{"reqCode":"checksession","itUsrId":userid},
			contentType: "application/x-www-form-urlencoded;charset=UTF-8",//加這個存中文才不會變亂碼
            async	:false,//逐步執行		
			error:function(xhr) {
				var show = "Status code: " + xhr.status + "\r\nException Message: " + xhr.responseText;	
				alert("錯誤-"+show);
			},
			success:function(data) {
			  if (data == "false") {
		   	      $.showWaitError() ;				  
			  }
			}
		});
		parent.postMessage("reset","*");
		return true ;	    
    } else 
        return false ;
};


