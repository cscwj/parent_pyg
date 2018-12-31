// 定义服务层:
app.service("userService",function($http){
	this.findAll = function(){
		return $http.get("../userManager/findAll.do");
	}
	
	this.findPage = function(page,rows){
		return $http.get("../userManager/findPage.do?pageNum="+page+"&pageSize="+rows);
	}
	
	this.add = function(entity){
		return $http.post("../userManager/add.do",entity);
	}
	
	this.update=function(entity){
		return $http.post("../userManager/update.do",entity);
	}
	
	this.findOne=function(id){
		return $http.get("../userManager/findOne.do?id="+id);
	}
	
	this.dele = function(ids){
		return $http.get("../userManager/delete.do?ids="+ids);
	}
	this.freeze = function(ids){
		return $http.get("../userManager/freeze.do?ids="+ids);
	}
	this.unfreeze = function(ids){
		return $http.get("../userManager/unfreeze.do?ids="+ids);
	}
	
	this.search = function(page,rows,searchEntity){
		return $http.post("../userManager/search.do?pageNum="+page+"&pageSize="+rows,searchEntity);
	}
	
	this.selectOptionList = function(){
		return $http.get("../userManager/selectOptionList.do");
	}
	this.findUserNum = function(){
		return $http.get("../userManager/findUserNum.do");
	}
	this.findLineUserNum = function(){
		return $http.get("../userManager/findLineUserNum.do");
	}
});