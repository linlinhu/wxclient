var theme = 'auto',
	page = 1,
	limit = 20, // 分页查询条数
	layer = null,
	layTpl = null, // 模板
	eventSource = null; // 消息接收事件源

layui.use(['layer', 'laytpl'], function(){
	layer = layui.layer;
	laytpl = layui.laytpl;
});

if (document.location.search.indexOf('theme=') >= 0) {
	theme = document.location.search.split('theme=')[1].split('&')[0];
}
// 初始化客户中心
var app = new Framework7({
  id: 'wxclient.customer.app',
  root: '#customerApp',
  theme: theme,
  routes: routes,
});
// 页面初始化事件监听
app.on('pageInit', function(p) {
	var pageName = $(p.pageEl).attr('data-page'),
		params = p.route.params; // 页面跳转参数
	
	/**
	 * 加载券列表
	 */
	if (params.ecmId && params.openId) {
		if (pageName == 'customer-Lotteries') {
			$('#lotterries-view').attr('ecmId', params.ecmId);
			$('#lotterries-view').attr('openId', params.openId);
			$('#lotterries-view').attr('woaId', params.woaId);
			loadAwardList(1);
		}
	
		if (pageName == 'customer-coupons') {
			$('#cuppons-view').attr('ecmId', params.ecmId);
			$('#cuppons-view').attr('openId', params.openId);
			$('#cuppons-view').attr('woaId', params.woaId);
			loadAwardList(2);
		}
	
		if (pageName == 'customer-exchangeds') {
			$('#exchangeds-view').attr('ecmId', params.ecmId);
			$('#exchangeds-view').attr('openId', params.openId);
			$('#exchangeds-view').attr('woaId', params.woaId);
			loadAwardList(0);
		}
	} else {
		app.router.back();
	}
	/**
	 * 加载券详情
	 */
	if (params.ecmId && params.id && params.woaId) {
		if (pageName == 'customer-awardinfo') {
			$('#awardInfo-view').attr('ecm-id', params.ecmId);
			$('#awardInfo-view').attr('data-id', params.id);
			$('#awardInfo-view').attr('woa-id', params.woaId);
			loadAwardInfo();
		}
	} else {
		app.router.back();
	}
});

/**
 * 监听页面重新渲染，进行数据更新
 */
app.on('pageReinit', function(p) {
	var pageName = $(p.pageEl).attr('data-page'),
		params = p.route.params;
	
	if (pageName == 'customer-Lotteries') {
		loadAwardList(1);
	}

	if (pageName == 'customer-coupons') {
		loadAwardList(2);
	}

	if (pageName == 'customer-exchangeds') {
		loadAwardList(0);
	}
});

/**
 * 根据类型加载券列表
 * @param type 1：奖券 2：优惠券 0：已使用券
 */
function loadAwardList(type) {
	var tpl, view, page, maxPage = 1, 
		title, datas;
	
	if (type == 1) {
		title = '奖券';
		tpl = lotteriesData.innerHTML;
		view = $('#lotterries-view');
	} else if (type == 2) {
		title = '优惠券';
		tpl = cupponsData.innerHTML;
		view = $('#cuppons-view');
	} else {
		title = '已使用券';
		tpl = lotteriesData.innerHTML;
		view = $('#exchangeds-view');
	}
	page = parseInt(view.attr('cur'));
	maxPage = 1;
	
	$.ajax({
		url: base + 'customer/awardList',
		data: {
			ecmId: view.attr('ecmId'),
			openId: view.attr('openId'),
			awardType: type,
			page: page
		},
		success: function(res){
			if (typeof res == 'string') {
				res = JSON.parse(res);
			}
			
			if (!res.success) {
				app.dialog.alert('加载我的' + title + '失败！' + (res.message != null ? '原因是：' + res.message : ''), '提示');
			} else {
				res = res.result;
				maxPage = parseInt(res.totalPageNum);
				datas = res.resultList;
				datas.ecmId = res.ecmId;
				datas.woaId = view.attr('woaId');
				datas.title = title;
				laytpl(tpl).render(datas, function(html){
					if (page != 1) {
						html = view.html() + html;
					}
					view.html(html);
					if (page <= maxPage) {
						view.attr('cur', page == maxPage ? maxPage : page + 1);
						view.attr('hasNextPage', page == maxPage ? false : true);
					}
				})
			}
		}
	});
}
/**
 * 加载奖券详情
 */
function loadAwardInfo() {
	var ecmId = $('#awardInfo-view').attr('ecm-id'),
		id = $('#awardInfo-view').attr('data-id'),
		woaId = $('#awardInfo-view').attr('woa-id'),
		tpl = awardInfoData.innerHTML,
		view = $('#awardInfo-view');
	
	$.ajax({
		url: base + 'customer/getAwardInfo',
		data: {
			ecmId: ecmId,
			id: id,
			woaId: woaId
		},
		success: function(res){
			if (typeof res == 'string') {
				res = JSON.parse(res);
			}
			
			if (!res.success) {
				app.dialog.alert('加载详情失败！' + (res.message != null ? '原因是：' + res.message : ''), '提示');
			} else {
				laytpl(tpl).render(res.result, function(html){
					view.html(html);
				})
			}
		}
	});
	
}

/**
 * 将券码显示给核销方
 * @param code
 */
function showAwardCode(code) {
	//打开弹窗生成二维码
	app.dialog.create({
		title: '将二维码出示给商家',
		text: '<div id="showCouponCode" style="padding: 30px 0 20px;">' + $('#myCouponCode').html() + '</div>',
		buttons: [
			{
			  text: '关闭'
			}
		],
		verticalButtons: true
	}).open();
	var qrcode = new QRCode(document.querySelectorAll('#showCouponCode .code-area')[0], {
        width : 96,//设置宽高
        height : 96
    });
	qrcode.makeCode(code);
	//设置最多可操作时间为10分钟
	var outTime = 200;
	//每三秒验证券码的状态
	eventSource = new EventSource(base + "awardCode/valid?awardCode=" + code);
	eventSource.onmessage = function(event){
		outTime--;
		if(event.data != "false"){ // 扫描验证使用成功，关闭消息源请求
			var res = JSON.parse(event.data);
			app.dialog.close();
			if (res.success) {
				loadAwardInfo(); // 重新渲染奖券详情	
			} else {
				var exceptionMessage = '';
				switch (res.reasonCode) {
					case 'CROSS_REGION':
						exceptionMessage = '该券码无法在此门店使用';
					break;
					case 'NOT_AVAILABLE':
						exceptionMessage = '该券码还未生效';
					break;
					case 'EXPIRED':
						exceptionMessage = '该券码已过期';
					break;
					default:
						exceptionMessage = '兑奖失败';
				}
				
				app.dialog.create({
					title: '扫码结果',
					text: exceptionMessage,
					buttons: [
						{
						  text: '关闭',
						  onClick: function() {
							  app.dialog.close();
							  app.router.back();
						  }
						}
					],
					verticalButtons: true
				}).open();
			}
			eventSource.close();

		}
		if (outTime <= 0) { // 链接超时，关闭消息源请求
			layer.msg('连接超时，请重试！');
			eventSource.close();
		}
	}
	
}

