//服务层
app.service('poiService', function ($http) {

  //读取列表数据绑定到表单中
  this.userExToExcel = function () {
    return $http.get('../poi/user/aExcelOut.do');
  }
  this.goodsExToExcel = function () {
    return $http.get('../poi/goods/aExcelOut.do');
  }
  this.orderExToExcel = function () {
    return $http.get('../poi/order/aExcelOut.do');
  }

});
