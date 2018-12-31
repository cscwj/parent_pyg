4// 定义控制器:
app.controller("orderController",function($scope,$controller,$location,$http,orderService){
	// AngularJS中的继承:伪继承
	$controller('baseController',{$scope:$scope});
	
	// 查询所有的品牌列表的方法:
	$scope.findAll = function(){
		// 向后台发送请求:
        orderService.findAll().success(function(response){
			$scope.list = response;
		});
	}

	// 分页查询
	$scope.findPage = function(page,rows){
		// 向后台发送请求获取数据:
        orderService.findPage(page,rows).success(function(response){
			$scope.paginationConf.totalItems = response.total;
			$scope.list = response.rows;
		});
	}
	
	// 保存品牌的方法:
	$scope.update = function(){

        orderService.update($scope.entity).success(function(response){
			// {flag:true,message:xxx}
			// 判断保存是否成功:
			if(response.flag){
				// 保存成功
				alert(response.message);
				$scope.reloadList();
			}else{
				// 保存失败
				alert(response.message);
			}
		});
	}
	
	// 查询一个:
	$scope.findById = function(id){
        orderService.findOne(id).success(function(response){
			// {id:xx,name:yy,firstChar:zz}
			$scope.entity = response;
		});
	}
	
	// 删除品牌:
	$scope.dele = function(){
        orderService.dele($scope.selectIds).success(function(response){
			// 判断保存是否成功:
			if(response.flag==true){
				// 保存成功
				// alert(response.message);
				$scope.reloadList();
				$scope.selectIds = [];
			}else{
				// 保存失败
				alert(response.message);
			}
		});
	}
	
	$scope.searchEntity={};
	
	// 假设定义一个查询的实体：searchEntity
	$scope.search = function(page,rows){
        var searchEntity = $location.search()['searchEntity'];
        if(null==searchEntity){
            //保存,这个方法是初始化,保存也跳转这个页面
            orderService.search(page,rows,$scope.searchEntity).success(function(response){
                $scope.paginationConf.totalItems = response.total;
                $scope.list = response.rows;

            });

        }
        // $scope.searchEntity
		// 向后台发送请求获取数据:
        orderService.search(page,rows,searchEntity).success(function(response){
			$scope.paginationConf.totalItems = response.total;
			$scope.list = response.rows;

		});
	}

    $scope.search1 = function(){

        // 向后台发送请求获取数据:
        orderService.search1($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage,$scope.searchEntity).success(function(response){
            $scope.paginationConf.totalItems = response.total;
            $scope.list = response.rows;

        });
    }


    $scope.findSellerList = function(){
        // 向后台发送请求获取数据:
        orderService.findSellerList().success(function(response){
            $scope.sellerList = response;

        });
    }
    // 路径配置

    $scope.time = [];
    $scope.item = [];
  $scope.Count = function(){
		orderService.countOrder($scope.searchEntity).success(function(response){
			$scope.orderCount = response;
            $scope.time = response.time;
		})
  }
    $scope.legend = ["销售额", "订单数"];
    $scope.item =$scope.time;
    $scope.data = [
        [-1, 1, 3, 7, 13, 16, 18, 16, 15, 9, 4, 2],
        [0, 1, 4, 7, 12, 15, 16, 15, 15, 10, 6, 5]
    ];

});
app.directive('line', function() {
    return {
        scope: {
            id: "@",
            legend: "=",
            item: "=",
            data: "="
        },
        restrict: 'E',
        template: '<div style="height:400px;"></div>',
        replace: true,
        link: function($scope, element, attrs, controller) {
            var option = {
                // 提示框，鼠标悬浮交互时的信息提示
                tooltip: {
                    show: true,
                    trigger: 'item'
                },
                // 图例
                legend: {
                    data: $scope.legend
                },
                // 横轴坐标轴
                xAxis: [{
                    type: 'category',
                    data: $scope.item
                }],
                // 纵轴坐标轴
                yAxis: [{
                    type: 'value'
                }],
                // 数据内容数组
                series: function(){
                    var serie=[];
                    for(var i=0;i<$scope.legend.length;i++){
                        var item = {
                            name : $scope.legend[i],
                            type: 'line',
                            data: $scope.data[i]
                        };
                        serie.push(item);
                    }
                    return serie;
                }()
            };
            var myChart = echarts.init(document.getElementById($scope.id),'macarons');
            myChart.setOption(option);
        }
    };
});
