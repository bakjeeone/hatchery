<%@page import="java.util.*"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <title>Hatchery v0.1</title>

    <!-- Bootstrap core CSS -->
    <link rel="stylesheet" href="static/bootstrap.min.css">

    <!-- Library CSS -->
    <link rel="stylesheet" type="text/css" href="static/lib/treant-js/Treant.css">
	<link rel="stylesheet" type="text/css" href="static/lib/json-viewer/jquery.json-viewer.css">
	
    <!-- Custom styles for this template -->
    <link href="static/main.css" rel="stylesheet">

  </head>

	<body>
		<nav class="navbar navbar-inverse navbar-fixed-top">
			<div class="container">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
						<span class="sr-only">Toggle navigation</span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
					</button>
					<a class="navbar-brand" href="#">Hatchery v0.34</a>
				</div>
				<div id="navbar" class="collapse navbar-collapse">
					<ul class="nav navbar-nav">
						<li class="active"><a href="#">Home</a></li>
						<li><a href="#about">About</a></li>
						<li><a href="#contact">Contact</a></li>
	          		</ul>
	        	</div><!--/.nav-collapse -->
      		</div>
		</nav>
		
	<div class="container" id="maincontent">
		<div class="row">
			<div class="col-md-5">
				<ul class="nav nav-pills nav-justified">
				  <li class="active" id="nameTab" ><a href="#col_items" data-toggle="tab">Name</a></li>
				  <li id="indexTab"><a href="#col_items" data-toggle="tab">Index</a></li>
				</ul>
			</div>			
		</div>
		<div class="row" id="inputcontent">
			<div class="col-md-5 section tab-content">
				<div id ="col_items" class="tab-pane fade in active">
					<select name="from[]" id="multiselect" class="form-control" size="25" multiple="multiple">		
			        </select>
				</div>
				<button type="button" id="jsonDetail" class="btn btn-default pull-left hidden">JSON Configuration</button>							
			</div>
			<div class="col-md-2">
		        <button type="button" id="multiselect_rightAll" class="btn btn-block">Select All</button>
		        <button type="button" id="multiselect_rightSelected" class="btn btn-block">Select</button>
		        <button type="button" id="multiselect_leftSelected" class="btn btn-block">Remove</button>
		        <button type="button" id="multiselect_leftAll" class="btn btn-block">Remove All</button>
		    </div>
			<div class="pull-right col-md-5 section">
				<form action = "/makeFile" method = "post">
					<select name="to[]" id="multiselect_to" class="form-control" size="25" multiple="multiple"></select>
	    			<select name="fileType" class="form-control">
						<option>csv</option>
						<option>json</option>
						<option>parquet</option>
					</select>
					<input type="text" id="outputName" placeholder="Ouput file name..." name="outputPath" class="form-control"  />
					<button type="button" id="confirm" class="btn btn-default pull-right">Confirm</button>	
				</form>							
			</div>
      	</div>
    </div><!-- /.container -->
    
    <div class="container" id="bottomcontent">
    	
    </div><!-- /.container -->
    
	<!-- Cluster Path Modal -->
	<div class="modal fade" id="clusterModal" data-backdrop="static" tabindex="-1" role="dialog" aria-labelledby="modalTitle" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title" id="modalTitle">Cluster Setup</h4>
				</div>
				<div class="modal-body">
					<div class="clearfix">
						<div class="form-inline pull-right">
							<label class="radio-inline">
								<input type="radio" name="clusterType" id="clusterType_D" value="Drillbit" checked="checked" > Drillbit
							</label>
							<label class="radio-inline">
								<input type="radio" name="clusterType" id="clusterType_Z" value="Zookeeper"> Zookeeper
							</label>
						</div>
						<h2>Type : Drillbit</h2>					
					</div>
					
					<table class="table table-bordered" id="clusterTable">
						<thead>
							<tr>
								<th>Index</th>
								<th>URL</th>
							</tr>	
						</thead>
						<tbody></tbody>				
					</table>
					<form class="form-horizontal well text-right">
						<div class="form-group">
							<label for="clusterURL" class="col-sm-1 control-label">URL</label>
							<div class="col-sm-11">
								<input type="url" class="form-control" id="clusterURL" placeholder="URL">
							</div>							
						</div>
						<button type="button" id="addCluster" class="btn btn-primary">+</button>														
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" id="clusterNext" class="btn btn-primary">Next</button>
				</div>
			</div>
		</div>
	</div>

	<!-- File Path Modal -->
	<div class="modal fade" id="pathModal" data-backdrop="static" tabindex="-1" role="dialog" aria-labelledby="modalTitle" aria-hidden="true">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-header">
	        <h4 class="modal-title" id="modalTitle">Insert input path(hdfs or local) <br>ex) hdfs://localhost:9000/test.json, /home/koodin/test.json</h4>
	        <div class="checkbox">
				<label>
	        		<input type="checkbox" name = "hive" id="hiveCheckBox"/> hive 메타스토어
				</label>
			</div>
	      </div>
	      <div class="modal-body">
	        <form>		        
	          <div class="form-group">
	            <label for="inputPath" class="control-label">location:</label>
	            <input type="text" class="form-control" id="inputPath">
	          </div> 
	          <div class="form-group hidden" id="hiveInputGroup">
	            <label for="inputDB_URL" class="control-label">Hive MetaStore ConnectionURL:</label>
	            <input type="text" class="form-control" id="inputDB_URL">
   	            <label for="inputHiveMetastorePath" class="control-label">Hive MetaStore WarehouseDir:</label>
	            <input type="text" class="form-control" id="inputHiveMetastorePath">
	            <label for="inputDefaultFS" class="control-label">Default File System:</label>
	            <input type="text" class="form-control" id="inputDefaultFS">	            
	          </div>     
	        </form>
	      </div>
	      <div class="modal-footer">
	        <button type="button" id="pathModalConfirm" class="btn btn-primary">Confirm</button>
	      </div>
	    </div>
	  </div>
	</div>

	<!-- Ask schema Modal-->
	<div class="modal fade" id="askSchemaModal" tabindex="-1"
	    role="dialog" aria-hidden="true" data-backdrop="static">
	    <div class="modal-dialog modal-sm">
	        <div class="modal-content">
	            <div class="modal-header">
	                <h4 class="modal-title">
	                    같은 스키마 입니까?
	                 </h4>
	            </div>
	            <div class="modal-body text-center">
	                <div class="btn-group" role="group" aria-label="...">
						<button id = "askSchemaYes" type="button" class="btn btn-default">YES</button>
						<button id = "askSchemaNo" type="button" class="btn btn-default">NO</button>
					</div>
	            </div>
	        </div>
	    </div>
	</div>
	
	<!-- Ask jsonConfig Modal-->
	<div class="modal fade" id="jsonConfigModal" tabindex="-1" role="dialog" aria-hidden="true" data-backdrop="static">
	    <div class="modal-dialog modal-lg"><!-- style="width:95vw;" -->
	        <div class="modal-content">
	        	<div class="modal-header">
		            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
	        		<h4 class="modal-title">JSON Config</h4>
        		</div>
        		<div class="modal-body">
       				<div role="tabpanel">
						<!-- Nav tabs -->
						<ul class="nav nav-pills " role="tablist">
							<li role="presentation" class="active"><a href="#jsonStructure" aria-controls="jsonStructure" role="tab" data-toggle="tab">JSON Structure</a></li>
							<li role="presentation" id="previewTableTab"><a href="#previewTable" aria-controls="previewTable" role="tab" data-toggle="tab">Preview Table</a></li>
						</ul>						
						<!-- Tab panes -->
						<div class="tab-content">
							<div role="tabpanel" class="tab-pane fade in active" id="jsonStructure">
							<pre class="pre-scrollable"></pre>
							</div>
							<div role="tabpanel" class="tab-pane fade" id="previewTable">
								
							</div>
						</div>						
					</div>
        		</div>
		        <div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					<button type="button" class="btn btn-primary" id="jsonConfigSave">Save changes</button>
				</div>         
	        </div>	        
	    </div>
	</div>

	<!-- Join Modal -->
	<div class="modal fade" id="joinModal" data-backdrop="static" tabindex="-1" role="dialog" aria-labelledby="modalTitle" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title" id="modalTitle">Join Setup</h4>
				</div>
				<div class="modal-body">
					<table class="table table-bordered" id="joinFilesTable">
						<thead>
							<tr>
								<th>Index</th>
								<th>File name</th>
							</tr>	
						</thead>
						<tbody></tbody>				
					</table>
					<table class="table table-bordered" id="joinListTable">
						<thead>
							<tr>
								<th>Index</th>
								<th>File name 1</th>
								<th>File name 2</th>
								<th>on</th>
							</tr>	
						</thead>
						<tbody></tbody>				
					</table>
					<form class="form-horizontal well text-right">
						<div class="form-group">
							<div class="col-sm-2">
								<input type="number" class="form-control" id="joinIndex1">
							</div>
							<label for="clusterURL" class="col-sm-1 control-label">join</label>
							<div class="col-sm-2">
								<input type="number" class="form-control" id="joinIndex2">
							</div>
							<label for="clusterURL" class="col-sm-1 control-label">on</label>
							<div class="col-sm-4">
								<input type="text" class="form-control" id="joinColumn" placeholder="Column name">
							</div>
							<div class="col-sm-2">
								<button type="button" id="addJoinList" class="btn btn-primary">+</button>																
							</div>			
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" id="joinNext" class="btn btn-primary">Next</button>
				</div>
			</div>
		</div>
	</div>
	<!-- matchTable Modal -->
	<div class="modal fade" id="matchTableModal" data-backdrop="static" tabindex="-1" role="dialog" aria-labelledby="modalTitle" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title" id="modalTitle">Matching table</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal">						
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" id="matchTableModalNext" class="btn btn-primary">Next</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Wait Modal-->
	<div class="modal fade" id="myPleaseWait" tabindex="-1"
	    role="dialog" aria-hidden="true" data-backdrop="static">
	    <div class="modal-dialog modal-sm">
	        <div class="modal-content">
	            <div class="modal-header">
	                <h4 class="modal-title">
	                    Please Wait
	                 </h4>
	            </div>
	            <div class="modal-body">
	                <div class="progress">
	                    <div class="progress-bar progress-bar-info
	                    progress-bar-striped active"
	                    style="width: 100%">
	                    </div>
	                </div>
	            </div>
	        </div>
	    </div>
	</div>
	
	<!-- Error Modal-->
	<div class="modal fade" id="errorModal" tabindex="-1" role="dialog" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content panel-danger">
				<div class="modal-header panel-heading">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">Error!</h4>
				</div>
				<div class="modal-body" id="errorModalBody">
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				</div>
			</div><!-- /.modal-content -->
		</div><!-- /.modal-dialog -->
	</div><!-- /.modal -->
  
  
	<!-- Modal ends Here -->
    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
	<script type="text/javascript">
		var searchFlag = "name";
	</script>
    <script src="static/jquery.min.js"></script>
    <script src="static/lib/multiselect.js"></script>
    <script type="text/javascript" src="static/lib/treant-js/Treant.js"></script>
	<script type="text/javascript" src="static/lib/treant-js/vendor/raphael.js"></script>
	<script type="text/javascript" src="static/lib/json-viewer/jquery.json-viewer.js"></script>
    <script type="text/javascript">
		$(document).ready(function() {
			var urlCount = 0;
			var joinCount = 0;
			var joinFlag = 0;
			
	    	var colIdx_tmp = [];
	    	var colName_tmp = [];
	    	
		    $('#multiselect').multiselect({
		    	search: {
		            left: '<input type="text" name="q" class="form-control" placeholder="Search..." />',
		            right: '<input type="text" name="q" class="form-control" placeholder="Search..." />',
		       }		        
		    });

		    
			//탭 전환 
		    function sortIndex(){
		    	$('#multiselect option').sort(function(a, b) {
				    var aVal = parseInt($(a).val()), bVal = parseInt($(b).val());
				    return aVal < bVal ? -1 : aVal > bVal ? 1 : 0;
				}).appendTo('#multiselect');
		    }
		    
		    function sortName(){
		    	$('#multiselect option').sort(function(a, b) {
				    var aVal = $(a).text(), bVal = $(b).text();
				    return aVal < bVal ? -1 : aVal > bVal ? 1 : 0;
				}).appendTo('#multiselect');
		    }
		    
		    $("#nameTab").click(function(){
		    	searchFlag = "name";
		    	$('#myPleaseWait').modal('show');
		    	sortName();
				$('#myPleaseWait').modal('hide');
		    });			
		    $("#indexTab").click(function(){
		    	searchFlag = "index";
		    	$('#myPleaseWait').modal('show');
		    	sortIndex();
		    	$('#myPleaseWait').modal('hide');
		    });
		    
		    
		    
		    $('#clusterModal').modal('show');

			//클러스터 추가
			$("#addCluster").click(function(){
		    	var clusterURL = $("#clusterURL").val();
		    	urlCount = urlCount + 1;
		    	$("#clusterTable tbody").append("<tr><td>"+urlCount+"</td><td>"+clusterURL+"</td></tr>");
		    });
			//클러스터 타입 
			$("input[name='clusterType']").click(function(){
				$("#clusterModal h2").html("Type : " + $('input[name="clusterType"]:checked').val());
			});
		    //클러스터 확인 		    
		    $("#clusterNext").click(function(){
		    	var clusterType = $('input[name="clusterType"]:checked').val();
			    $('#clusterModal').modal('hide');
		    	$('#myPleaseWait').modal('show');

				var myRows = [];
				var $headers = $("#clusterTable th");
				var $rows = $("#clusterModal tbody tr").each(function(index) {
				  $cells = $(this).find("td");
				  myRows[index] = {};
				  $cells.each(function(cellIndex) {
				    myRows[index][$($headers[cellIndex]).html()] = $(this).html();
				  });
				});

				// Let's put this in the object like you want and convert to JSON (Note: jQuery will also do this for you on the Ajax request)

				var myObj = {};
				myObj = myRows;

			    $.ajax({  
					type: "post", 
				    url: "/clusterSetup",		    
				    data: {clusterData: JSON.stringify(myObj), clusterType: clusterType},
				    success: function(response){
				    	$('#myPleaseWait').modal('hide');
					    $('#pathModal').modal('show');
				    },
				    error: ajaxFailed
				    }
				);
		    });
		    
		    
		    function sendGetColumnList(){
		    	$.ajax({
					type: "get", 
				    url: "/getColumnList",
				    success: function(response){
				    	$('#myPleaseWait').modal('hide');							    
			    		$("#multiselect").html(response);
				    	sortName();
				    },
				    error: ajaxFailed
				    }
				);
		    }
		    	
			//처음패스 입력 
		    $("#pathModalConfirm").click(function(){
		    	$('#pathModal').modal('hide');
		    	$('#myPleaseWait').modal('show');
		    	
		    	var inputData = {};
		    	inputData.hiveIsUse = $("#hiveCheckBox").is(":checked");
		    	inputData.inputPath = $("#inputPath").val();
		    	if($("#hiveCheckBox").is(":checked")){
		    		inputData.DB_URL = $("#inputDB_URL").val();
		    		inputData.hiveMetastorePath = $("#inputHiveMetastorePath").val();
		    		inputData.defaultFs = $("#inputDefaultFS").val();
		    	}
		    			    	
		    	$.ajax({  
					type: "get", 
				    url: "/fileInput",		    
				    data: {inputData: JSON.stringify(inputData)},
				    success: function(response){
				    	if(response == "Directory"){
					    	$('#myPleaseWait').modal('hide');
				    		$('#askSchemaModal').modal('show');
				    	} else {
				    		if(response == "JSONFile"){
						    	$("#jsonDetail").removeClass('hidden');				    			
				    		}
			    			sendGetColumnList();
				    	}				    	
				    },
				    error: ajaxFailed
				    }
				);		    	    	    	
		    });		    
		    
		    $("#hiveCheckBox").click(function(){
		    	if($(this).is(":checked")){
		    		$("#inputPath").prev().html("table:");
		    		$("#hiveInputGroup").removeClass("hidden");
		    	} else {
		    		$("#inputPath").prev().html("location:");
		    		$("#hiveInputGroup").addClass("hidden");
		    	} 			    	    	    	
		    });	
		    
			
			
		  	//Ask Schema YES, Ask Json No!
			$("#askSchemaYes").click(function(){
				$('#askSchemaModal').modal('hide');
		    	$('#myPleaseWait').modal('show');
		    	sendGetColumnList();		    	
		    });
			
		  	//Ask Schema NO!		  	
		    $("#askSchemaNo").click(function(){
				$('#askSchemaModal').modal('hide');
		    	$('#myPleaseWait').modal('show');		    
		    	$.ajax({  
					type: "get", 
				    url: "/getFileList",		    
				    success: function(response){
				    	$('#myPleaseWait').modal('hide');	
				    	$("#joinFilesTable tbody").html(response);
				    	$('#joinModal').modal('show');
				    },
				    error: ajaxFailed
				    }
				);	
		    });
		  	
		  //Json Detail!		  	
		    $("#jsonDetail").click(function(){
		    	$('#myPleaseWait').modal('show');
		    	$.ajax({
					type: "get", 
				    url: "/getColumnList",
				    data: {jsonFlag : "getDetail"},
				    success: function(response){
				    	$('#myPleaseWait').modal('hide');

				    	$("#jsonStructure pre").jsonViewer(response, {collapsed: true});
				    	
				    	$("#jsonStructure pre input[type=checkbox]").each(function() {
				    		if ($(this).parent().parent().parent().prop("tagName") == "LI")
				    			$(this).remove();
				    	})
				    					    					    					    					    					    	
			    		$("#jsonConfigModal").modal('show');						  						    	
				    },
				    error: ajaxFailed
				    }
				);
		    });
		  		  			    
		    
		    $("#previewTableTab, #jsonConfigSave").click(function(){
		    	$('#myPleaseWait').modal('show');
		    	var jsonChecked = [];
		    	var jsonCheckedTmpName = [];
		    	$("#jsonStructure pre").find("input:checked").each(function(){
	    			jsonChecked.push($(this).parent().parent().parent().prev().html());
	    			jsonCheckedTmpName.push($(this).parent().next().val());
	    		});
		    	var tabOrSave;
		    	if($(this).is("li")){
		    		tabOrSave = "li";
		    	} else {
		    		tabOrSave = "save";
		    	}
		    	$.ajax({
					type: "get", 
				    url: "/getColumnList",
				    data: {jsonFlag : "getTempTable", flattenDataTemp : jsonChecked, flattenNameTemp : jsonCheckedTmpName, tabOrSave : tabOrSave},
				    success: function(response){
				    	$('#myPleaseWait').modal('hide');
				    	if (tabOrSave == "li") {
				    		var columntest = response.columnNames;
					    	var datatest = response.columnDatas;				    					    	
							
							var html = '<table class="table table-bordered"><thead><tr>';
							var i = 0;
							for(; i < columntest.length; i++){
					    		html += '<th>'+ columntest[i] + '</th>';
					    	}
							html += "</tr></thead><tbody><tr>";
							i = 0;
							for(; i < columntest.length; i++){
					    		html += '<td>'+ datatest[i] + '</td>';
					    	}
							html += "</tr></tbody></table>";

							$("#previewTable").html(html);
				    	} else {
				    		$('#myPleaseWait').modal('hide');
				    		var columntest = response.columnNames;
				    		var i;
				    		var html = "";
				    		for(i = 0; i < columntest.length; i++){
				    			html += "<option value="+i+">"+columntest[i]+"</option>";
					    	}
				    		$("#jsonConfigModal").modal('hide');						  						    	
				    		$("#multiselect").html(html);
				    		if(searchFlag == "name"){
						    	sortName();
				    		} else{
				    			sortIndex();
				    		}
						}
				    	
				    },
				    error: ajaxFailed
				    }
				);
						    	
	    	});
		    
		    
		  
		  
			//Join 추가
			$("#addJoinList").click(function(){
		    	var joinIndex1 = $("#joinIndex1").val();
		    	var joinIndex2 = $("#joinIndex2").val();
		    	var joinColumn = $("#joinColumn").val();

		    	var fileNames = [];		    			    	

		    	var $rows = $("#joinFilesTable tbody tr").each(function(index) {
					$cells = $(this).find("td");			
					$cells.each(function(cellIndex) {
						if($(this).html() == joinIndex1 || $(this).html() == joinIndex2){
							fileNames[$(this).html()] = $(this).next().html();
						}
					}); 
				});

		    	if(1 <= joinIndex1 && joinIndex1 <= $rows.length && 1 <= joinIndex2 && joinIndex2 <= $rows.length){
					var content = "";
			    	joinCount = joinCount + 1;
			    	$("#joinListTable tbody").append("<tr><td>"+joinCount+"</td><td value='"+joinIndex1+"'>"+fileNames[joinIndex1]+"</td><td value='"+joinIndex2+"'>"+fileNames[joinIndex2]+"</td><td>"+joinColumn+"</td></tr>");
		    	} else {
		    		alert("올바른 인덱스를 적어주세요.");
		    	}									    	
		    });

		    //join 확인
		    $("#joinNext").click(function(){
			    $('#joinModal').modal('hide');
		    	$('#myPleaseWait').modal('show');

				var myRows = [];
				var $headers = $("#joinListTable th");
				var $rows = $("#joinListTable tbody tr").each(function(index) {
				  $cells = $(this).find("td");
				  myRows[index] = {};
				  $cells.each(function(cellIndex) {
				    myRows[index][$($headers[cellIndex]).html()] = $(this).html();
				  });    
				});												

				var myObj = {};
				myObj = myRows;
				
		    	$.ajax({  
					type: "get", 
				    url: "/getColumnList",		
				    data: {joinData: JSON.stringify(myObj)},
				    success: function(response){
				    	joinFlag = 1;
				    	$('#myPleaseWait').modal('hide');
				    	$("#multiselect").html(response);
				    	sortName();
				    },
				    error: ajaxFailed
				    }
				);
		    });
		    		   		   			
		    
		    //파일 만들기 
		    $("#confirm").click(function(){
		    	$('#myPleaseWait').modal('show');
		    	colIdx_tmp = [];
		    	colName_tmp = [];
		    	$("#multiselect_to option").each(function(){
		    		colIdx_tmp.push($(this).val());
		    		colName_tmp.push($(this).html());
    			});
		    	if(joinFlag == 1){
			    	$('#myPleaseWait').modal('hide');
			    	var i = 0;
			    	var html = "";
			    	for(; i < colName_tmp.length; i++){
			    		html += '<div class="form-group">';
			    		html += '<label for="colName_'+i+'" class="col-sm-3 control-label">'+colName_tmp[i]+'</label>';
			    		html += '<div class="col-sm-9">';
			    		html += '<input type="email" class="form-control" id="colName_'+i+'" placeholder="File name">';
			    		html += '</div></div>';
			    	}
		    		$('#matchTableModal .modal-body .form-horizontal').html(html);
			    	$('#matchTableModal').modal('show');			    	
	    		} else {
	    			$.ajax({  
						type: "post", 
					    url: "/makeFile",		    
					    data: {colIdx: colIdx_tmp, fileType: $('select[name="fileType"] option:selected').val(), 
					    	colName: colName_tmp, outputName: $("#outputName").val()
					    },
					    success: function(response){
					    	$('#myPleaseWait').modal('hide');
				    		alert("success");	    					    	
					    },
					    error: ajaxFailed
					    }
					);
	    		}
		    });
		    
		    //matching table 
		     $("#matchTableModalNext").click(function(){
		    	$('#matchTableModal').modal('hide');
		    	$('#myPleaseWait').modal('show');
				var i = 0;
				for(i; i < colIdx_tmp.length; i++){
					var filename = $("#colName_" + i).val();
					filename = filename.replace(/[.]/g, "");
					colName_tmp[i] = filename + "." +colName_tmp[i];
					console.log(colName_tmp[i]);
				}
				$.ajax({  
					type: "post", 
					url: "/makeFile",		    
					data: {colIdx: colIdx_tmp, fileType: $('select[name="fileType"] option:selected').val(), 
							colName: colName_tmp, outputName: $("#outputName").val()
					},
					success: function(response){
						$('#myPleaseWait').modal('hide');
						joinFlag = 0;
						alert("success");
					},
					error: ajaxFailed
					}
				);
		     });
		    function ajaxFailed(ajax, exception) {
		    	$('#myPleaseWait').modal('hide');
		    	
		    	var errorMessage = "Error making Ajax request:\n\n";
		    	errorMessage += "Exception: " + exception.message;
		    	errorMessage += "\nServer status:\n" + ajax.status + "<br><div class='well'>"+ajax.statusText+"</div>";
				
		    	$('#errorModalBody').html(errorMessage);		   
		    	$('#errorModal').modal('show');

		    }
		});
	</script>
    <!-- 모든 컴파일된 플러그인을 포함합니다 (아래), 원하지 않는다면 필요한 각각의 파일을 포함하세요 -->
    <script src="static/bootstrap.min.js"></script>
  </body>
</html>