4// 定义控制器:
app.controller("orderController",function($scope,$controller,$http,orderService){
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
		// 向后台发送请求获取数据:
        orderService.search(page,rows,$scope.searchEntity).success(function(response){
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
    $scope.time=[];
    $scope.legend =[];
    $scope.orderCount = {"time":[]};
  $scope.Count = function(){
		orderService.countOrder($scope.searchEntity).success(function(response){
			$scope.orderCount = response;
            $scope.time = response.time;
		})
  }
    $scope.legend = ['订单数','总金额'];
	$scope.data= [
        [10, 12, 21, 54, 260, 830, 710],
        [30, 182, 434, 791, 390, 30, 10]
    ];

});
app.directive('sexbar', function() {
    return {
        scope: {
            id: "@",
            legend: "=",
            //item: "=",
            data: "=",
            entity: "@",
            searchEntity: "=",
            time: "="

        },
        restrict: 'E',
        template: '<div style="height:400px;"></div>',
        replace: true,
        link: function($scope, element, attrs, controller) {
            var a = [];

            var option = {
                title : {
                    text: '订单情况',
                    subtext:''
                },
                tooltip : {

                     trigger: 'none'

                },
                legend: {
                    data:$scope.legend
                },
                toolbox: {
                    show : true,
                    feature : {
                        mark : {show: true},
                        dataView : {show: true, readOnly: false},
                        magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
                        restore : {show: true},
                        saveAsImage : {show: true}
                    }
                },
                calculable : true,


                xAxis: [{
                    type: 'category',

                    data : $scope.time,
                    axisLabel: {
                        interval:0,
                        rotate:40
                    }

                }],

                yAxis : [
                    {
                        type : 'value'
                    }
                ],
                // series : [
                //     {
                //         name:'订单数',
                //         type:'line',
                //         smooth:true,
                //         itemStyle: {normal: {areaStyle: {type: 'default'}}},
                //         data:[10, 12, 21, 54, 260, 830, 710]
                //     },
                //     {
                //         name:'总金额',
                //         type:'line',
                //         smooth:true,
                //         itemStyle: {normal: {areaStyle: {type: 'default'}}},
                //         data:[30, 182, 434, 791, 390, 30, 10]
                //     }
                // ],
                series: function() {
                    var serie = [];
                    for (var i = 0; i < $scope.legend.length; i++) {
                        var item = {
                            name: $scope.legend[i],
                            type: 'line',
							smooth:true,
							itemStyle: {normal: {areaStyle: {type: 'default'}}},
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