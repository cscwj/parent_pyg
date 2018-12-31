app.service("contentService",function($http){
	this.findByCategoryId = function(categoryId){
		return $http.get("content/findByCategoryId.do?categoryId="+categoryId);
	}
    this.savecount=function(){
        return $http.get('../content/savecount.do');
    }
});