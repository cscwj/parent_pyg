app.service("loginService",function($http){
	
	this.showName = function(){
		return $http.get("../login/showName.do");
	}

	this.findAddress=function () {
		return $http.get("../login/findAddress.do");
    }

    this.showMessage=function () {
        return $http.get("../login/showMessage.do");
    }
    this.setDefault=function (id) {
		return $http.get("../login/setDefault.do?id="+id);
    }
    this.add = function(entity){
        return $http.post("../login/add.do",entity);
    }

    this.update=function(entity){
        return $http.post("../login/update.do",entity);
    }
});