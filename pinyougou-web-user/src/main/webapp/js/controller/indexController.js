//首页控制器
app.controller('indexController',function($scope,loginService){
	$scope.showName=function(){
			loginService.showName().success(
					function(response){
						$scope.loginName=response.loginName;
					}
			);
	}

    $scope.showMessage=function () {

        loginService.showMessage().success(

            function(response){
                $scope.user=response;
            }
        )
    }

	$scope.findAddress=function () {

		loginService.findAddress().success(
			function (response) {
				$scope.Address=response;
            }
		)
    }

    $scope.setDefault=function (id) {
		loginService.setDefault(id).success(
			function (response) {
				if(response.flag){
                    // location.reload();
                    loginService.findAddress().success(
                        function (response) {
                            $scope.Address=response;
                        }
                    )


				}else {
					alert(response.message);
				}
            }
		)
    }

    $scope.delete=function (id) {
		loginService.delete(id).success(
			function (response) {
                if(response.flag){
                    // location.reload();
                    loginService.findAddress().success(
                        function (response) {
                            $scope.Address=response;
                        }
                    )


                }else {
                    alert(response.message);
                }
            }
		)
    }

    $scope.myOrders=function () {
        loginService.myOrders().success(
            function (response) {
                $scope.Orders=response;
            }
        )
    }

    $scope.save = function(){
        // 区分是保存还是修改
        //alert($scope.entity.cityId);
        var object;
        if(null != $scope.entity.id){
            // 更新
            object = loginService.update($scope.entity);
        }else{
            // 保存
            object = loginService.add($scope.entity);
        }
        object.success(function(response){
            // {flag:true,message:xxx}
            // 判断保存是否成功:
            if(response.flag){
                // 保存成功
                alert(response.message);
                loginService.findAddress().success(
                    function (response) {
                        $scope.Address=response;
                    }
                )
            }else{
                // 保存失败
                alert(response.message);
            }
        });
    }

});