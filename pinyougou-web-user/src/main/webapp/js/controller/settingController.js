 //控制层 
app.controller('settingController' ,function($scope,$controller   ,settingService){
	$scope.abc = {};
	//注册用户
	$scope.findMessage=function(){
    settingService.findMessage().success(
    		function (response) {
          $scope.user = response;
        }
		);
	}
	
});	
