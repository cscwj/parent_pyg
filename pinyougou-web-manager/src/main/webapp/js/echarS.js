
console.log("ceshi输出");

app.controller('sexChart', function($scope,$controller,$http,orderService) {
    $controller('baseController',{$scope:$scope});
    $scope.findSellerList = function(){
        // 向后台发送请求获取数据:
        orderService.findSellerList().success(function(response){
            $scope.sellerList = response;

        });
    };
    $scope.legend = ["男", "女"];
    // $scope.item = ['Jan', 'Feb', 'Mar', 'Apr', 'Mar', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    $scope.data = [
        {value:78, name:'男'},{value:56,name:'女'} //Berlin  
    ];
});

app.directive('sexbar', function() {
    return {
        scope: {
            id: "@",
            legend: "=",
            //item: "=",  
            data: "="
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
                    trigger: 'axis'
                },
                legend: {
                    data:['订单数','总金额']
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
                xAxis : [
                    {
                        type : 'category',
                        boundaryGap : false,
                        data : ['周一','周二','周三','周四','周五','周六','周日']
                    }
                ],
                yAxis : [
                    {
                        type : 'value'
                    }
                ],
                series : [
                    {
                        name:'订单数',
                        type:'line',
                        smooth:true,
                        itemStyle: {normal: {areaStyle: {type: 'default'}}},
                        data:[10, 12, 21, 54, 260, 830, 710]
                    },
                    {
                        name:'总金额',
                        type:'line',
                        smooth:true,
                        itemStyle: {normal: {areaStyle: {type: 'default'}}},
                        data:[30, 182, 434, 791, 390, 30, 10]
                    }
                ]
            };

            var myChart = echarts.init(document.getElementById($scope.id),'macarons');
            myChart.setOption(option);
        }
    };
});

