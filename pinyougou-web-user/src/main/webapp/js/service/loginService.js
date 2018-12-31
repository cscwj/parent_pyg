//服务层
app.service('loginService',function($http){
	//读取列表数据绑定到表单中
	this.showName=function(){
		return $http.get('../login/showName.do');
	}

	this.findAddress=function () {

		return $http.get('../login/findAddress.do');
    }

    this.showMessage=function () {
		//alert(1);
        return $http.get('../login/showMessage.do');
    }
    this.setDefault=function (id) {
        return $http.get("../login/setDefault.do?id="+id);
    }

    this.delete=function (id) {
		return $http.get("../login/delete.do?id="+id);
    }
//List<Map>
    this.myOrders=function () {
        return $http.get("../login/myOrders.do");
    }

    this.add = function(entity){
        return $http.post("../login/add.do",entity);
    }

    this.update=function(entity){
        return $http.post("../login/update.do",entity);
    }

});