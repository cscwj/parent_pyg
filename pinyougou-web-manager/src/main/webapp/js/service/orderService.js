// 定义服务层:
app.service("orderService",function($http){
	this.findAll = function(){
		return $http.get("../brand/findAll.do");
	}
	
	this.findPage = function(page,rows){
		return $http.get("../brand/findPage.do?pageNum="+page+"&pageSize="+rows);
	}
	
	this.add = function(entity){
		return $http.post("../brand/add.do",entity);
	}
	
	this.update=function(entity){
		return $http.post("../order/update.do",entity);
	}
	
	this.findOne=function(id){
		return $http.get("../order/findOne.do?id="+id);
	}
	
	this.dele = function(ids){
		return $http.get("../brand/deleteBrand.do?ids="+ids);
	}
	
	this.search = function(page,rows,searchEntity){
		return $http.post("../order/search.do?pageNum="+page+"&pageSize="+rows,searchEntity);
	}
	
	this.selectOptionList = function(){
		return $http.get("../brand/selectOptionList.do");
	}

	this.findSellerList = function () {
		return $http.get("../order/findSellerList.do")
    }
    this.countOrder = function(searchEntity){
		return $http.post("../order/orderCount.do",searchEntity);
	}
});