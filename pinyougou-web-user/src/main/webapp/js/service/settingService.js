//服务层
app.service('settingService',function($http){
	    	
	//加载用户信息
	this.findMessage=function(){
		return $http.get('../setting/findMessage.do');
	}

});
