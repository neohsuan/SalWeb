
//轉換date格式，不足2位補0
var pad = function(num) {
	return ('00'+num).slice(-2);
}

//轉換IE可以正常顯示的日期
var IEDate = function(strDate) {
	strDate = strDate.replace(new RegExp(/-/gm) ,"/"); //將所有-轉換為/，避免IE NaN
	strDate = strDate.substr(0, strDate.indexOf(".")); //刪除豪秒以避免IE NaN
	return strDate;
}

//千分位顯示
function thousandNumber(n) {
    n += "";
    var arr = n.split(".");
    var re = /(\d{1,3})(?=(\d{3})+$)/g;
    return arr[0].replace(re, "$1,") + (arr.length == 2 ? "." + arr[1] : "");
}

//百分率轉換，有小數點就*100再百分比，沒有小數點則直接加百分率
function toPercent(num) {
	
	var value;
	if ( num.indexOf(".") != -1 ) {
		value = Number(num*100).toFixed(2) + "%";
	} else if ( num.indexOf("%") != -1 ) {
		value = num; //有百分比維持原狀
	} else {
		value = Number(num).toFixed(0) + "%"; //直接加%
	}	
	
	return value;
}

//千分率轉換，有小數點就*100再千分率，沒有小數點則直接加千分率
function toPercentage(num){
	var value;
	if ( num.indexOf(".") != -1 ) {
		value = Number(num*1000).toFixed(2) + "‰";
	} else if ( num.indexOf("‰") != -1 ) {
		value = num; //有千分率維持原狀
	} else {
		value = Number(num).toFixed(0) + "‰"; //直接加%
	}	
	
	return value;	
}
/**
 * 清除default date
 * @param oldDate
 * @returns {String}
 */
function replaceDefaultDate(oldDate){
	var newDate="";
	if(oldDate=="2910-12-31" || oldDate=="9999-12-31"){
		newDate="";
	}else{
		newDate=oldDate;
	}
	return newDate;
}