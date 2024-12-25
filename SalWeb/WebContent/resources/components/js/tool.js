//*******************************************************************************
//函數名稱：showWait(imageUrl)
//目        的：秀出等待圖示
//參數說明：圖片的位址
//傳  回  值：無
//限        制：
//備        註：
//範        例：
//版        本：1.0
//作　　者：Sin-Sian Li (2012-08-23)
//*******************************************************************************
var ProgressImage = null;


function showWait(imageUrl) {

		var progress = $jQ10("<div id='progress' style=\"position:absolute;display:none;border: 1px black solid;padding:3px 3px 3px 3px;background-color:#FFFFFF;font-family: sans-serif;font-size: 80%;z-index:100000\"><NOBR><img id='progress_img' src='' align='texttop'></NOBR></div>");
		$jQ10("body").append(progress);
		var iLeft = null;
		var iTop = null;
		try{
			iLeft = event.clientX;
			iTop = event.clientY+document.body.scrollTop;
		}catch(e){
			//window.status = "tools.js-showWait():JavaScript Exception:"+e.toString();
		}
		try{
			ProgressImage = new Image();
			ProgressImage.src = imageUrl;
			document.getElementById("progress_img").src = ProgressImage.src;
			
			var Progress = document.getElementById("progress");
			Progress.style.top = ((iTop)?iTop:(document.body.clientHeight-ProgressImage.height)/2) + 'px';
			Progress.style.left = ((iLeft)?iLeft:(screen.width-ProgressImage.width)/2) + 'px';
			Progress.style.display = "block";
		}catch(e){
			hideWait();
		}

	
}

/**
 * 隱藏wait的圖示
 */
function hideWait() {
	try{
		$jQ10("#progress").hide();
	}catch(e){
	}
}

