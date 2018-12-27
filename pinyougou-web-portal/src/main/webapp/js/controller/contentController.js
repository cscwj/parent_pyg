app.controller("contentController",function($scope,contentService){
	$scope.contentList = [];
	//               轮播图 今日推荐 数组里面嵌套了集合
	// 根据分类ID查询广告的方法:
	$scope.findByCategoryId = function(categoryId){
		contentService.findByCategoryId(categoryId).success(function(response){
			$scope.contentList[categoryId] = response; //list<conterrt>
		});
	}
	
	//搜索  （传递参数）
	$scope.search=function(){
		location.href="http://localhost:9103/search.html#?keywords="+$scope.keywords;
	}
	
});