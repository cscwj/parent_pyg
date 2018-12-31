//控制层
app.controller('poiController', function ($scope, $controller, poiService, uploadService) {

  $controller('baseController', {$scope: $scope});//继承

  //读取列表数据绑定到表单中
  $scope.userExToExcel = function () {
    poiService.userExToExcel().success(
        function (response) {
          if (response.flag) {
            alert("导出成功!")
            location.href = response.message;
            // window.open(response.message);

          } else {
            alert(response.message);
          }
        }
    );
  }
  $scope.goodsExToExcel = function () {
    poiService.goodsExToExcel().success(
        function (response) {
          if (response.flag) {
            alert("导出成功!")
            location.href = response.message;
            // window.open(response.message);
          } else {
            alert(response.message);
          }
        }
    );
  }

  $scope.orderExToExcel = function () {
    poiService.orderExToExcel().success(
        function (response) {
          if (response.flag) {
            alert("导出成功!");
            location.href = response.message;
            // window.open(response.message);
          } else {
            alert(response.message);
          }
        }
    );
  }

  // 品牌   规格   模板      分类
  $scope.t = {type: '0', index: ['', 'brand', 'specification', 'type_template', 'item_cat']};//定义条件对象

  $scope.show = function () {
    console.log($scope.t.type)
  }

  // 文件上传的方法:
  $scope.uploadFile = function () {
    if($scope.t.type == '0'){
      alert('请选择类型');
      return;
    }
    var type = $scope.t.index[$scope.t.type];
    uploadService.uploadExcel(type).success(function (response) {
      if (response.flag) {
        alert(response.message);
      } else {
        alert(response.message);
      }
    });
  }


});
