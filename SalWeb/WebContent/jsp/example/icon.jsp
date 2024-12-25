<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Icon</title>
<script src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery-ui-1.9.2.js"></script>
<script src="${pageContext.request.contextPath}/bootstrap-4.0.0/js/bootstrap.min.js"></script>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/jquery-ui.min.css">
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap-4.0.0/css/bootstrap.min.css">
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/icons.css">
<style type="text/css">
	body{
		background-color: #efe9ea;
	}
</style>
</head>
<body>
	<div class="container">
		<table class="table table-bordered">
			<thead>
				<tr>
					<td>＼</td>
					<td>門</td>
					<td>住急</td>
					<td>護理</td>
					<td><span class="icon-vghks icon-done-outline"></span>建議</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>新增</td>
					<td>
						<img alt="" src="../css/icon_o/add.JPG">
					</td>
					<td></td>
					<td>
						<img alt="" src="../css/icon_n/add-n.png">
					</td>
					<td>
						<button class="btn btn-primary" type="button">新增<span class="icon-vghks icon-add"></span></button>
					</td>
				</tr>
				<tr>
					<td>修改/編輯</td>
					<td>
						<img alt="" src="../css/icon_o/edit.JPG">
					</td> 
					<td></td>
					<td>
						<img alt="" src="../css/icon_n/edit-n.png">
					</td>
					<td>
						<button class="btn btn-primary" type="button">修改<span class="icon-vghks icon-edit"></span></button>
					</td>
				</tr>
				<tr>
					<td>刪除</td>
					<td>
						<img alt="" src="../css/icon_o/delete.JPG">
					</td>
					<td>
						<img alt="" src="../css/icon_ae/delete-a.png">
					</td>
					<td></td>
					<td>
						<button class="btn btn-primary" type="button">刪除<span class="icon-vghks icon-delete"></span></button>
					</td>
				</tr>
				<tr>
					<td>查詢</td>
					<td>
						<img alt="" src="../css/icon_o/search.JPG">
					</td>
					<td>
						<img alt="" src="../css/icon_ae/search-a.png">
					</td>
					<td>
						<img alt="" src="../css/icon_n/search-n.png">
					</td>
					<td>
						<button class="btn btn-primary" type="button">查詢<span class="icon-vghks icon-search"></span></button>
					</td>
				</tr>
				<tr>
					<td>儲存</td>
					<td>
						<img alt="" src="../css/icon_o/save.JPG">
					</td>
					<td></td>
					<td>
						<img alt="" src="../css/icon_n/save-n.png">
					</td>
					<td>
						<button class="btn btn-primary" type="button">儲存<span class="icon-vghks icon-save"></span></button>
					</td>
				</tr>
				<tr>
					<td>取消/關閉</td>
					<td>
						<img alt="" src="../css/icon_o/cancel.JPG">
					</td>
					<td></td>
					<td></td>
					<td>
						<button class="btn btn-primary" type="button">取消<span class="icon-vghks icon-cancel"></span></button>
					</td>
				</tr>
				<tr>
					<td>列印</td>
					<td>
						<img alt="" src="../css/icon_o/print.JPG">
					</td>
					<td></td>
					<td></td>
					<td>
						<button class="btn btn-primary" type="button">列印<span class="icon-vghks icon-print"></span></button>
					</td>
				</tr>
				<tr>
					<td>上傳</td>
					<td>
						<img alt="" src="../css/icon_o/upload.JPG">
					</td>
					<td></td>
					<td></td>
					<td>
						<button class="btn btn-primary" type="button">上傳<span class="icon-vghks icon-upload"></span></button>
					</td>
				</tr>
				<tr>
					<td>下載</td>
					<td>
						
					</td>
					<td></td>
					<td></td>
					<td>
						<button class="btn btn-primary" type="button">下載<span class="icon-vghks icon-download"></span></button>
					</td>
				</tr>
				<tr>
					<td>訊息</td>
					<td></td>
					<td>
						<img alt="" src="../css/icon_ae/info-ae.png">
					</td>
					<td>
						<img alt="" src="../css/icon_n/info-n.png">
					</td>
					<td>
						<button class="btn btn-primary" type="button">訊息<span class="icon-vghks icon-info"></span></button>
					</td>
				</tr>
				<tr>
					<td>附件</td>
					<td></td>
					<td></td>
					<td></td>
					<td>
						<button class="btn btn-primary" type="button">附件<span class="icon-vghks icon-attach-file"></span></button>
					</td>
				</tr>
				<tr>
					<td>展開格子</td>
					<td></td>
					<td></td>
					<td></td>
					<td>
						<span style="width: 32px; height: 32px;" class="icon-vghks icon-expand"></span>
					</td>
				</tr>
				<tr>
					<td>等待中</td>
					<td>
						<img style="max-width: 60px; max-height: 60px;" alt="" src="../css/icon_o/laoding.gif">
					</td>
					<td></td>
					<td></td>
					<td>
						<div>
							<div class="loader"></div>
							<span>Loading....</span>
						</div>
						
					</td>
				</tr>
			</tbody>
		</table>
	</div>
</body>
</html>