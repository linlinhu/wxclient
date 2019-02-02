var theme = 'auto',
	page = 1,
	limit = 20,
	layer = null,
	layTpl = null, // 模板
	app = null; // 指向当前应用

layui.use(['layer', 'laytpl'], function(){
	layer = layui.layer;
	laytpl = layui.laytpl;
});

if (document.location.search.indexOf('theme=') >= 0) {
	theme = document.location.search.split('theme=')[1].split('&')[0];
}
app = new Framework7({
  id: 'wxclient.consume.app',
  root: '#consumeApp',
  theme: theme,
  routes: routes,//配置在routes.js
});
// 页面初始化监听
app.on('pageInit', function(p) {
	var pageName = $(p.pageEl).attr('data-page'),
		params = p.route.query;

	if (pageName == 'consume-awardinfo') {
		loadAwardInfo(params);
	}
})
/**
 * 核销人员登录
 * @param ecmId 主体编号
 * @param openId 微信个人标志
 */
function consumeLogin(ecmId, openId) {
	var name = $('#consume_person_name').val(), // 用户名
		pwd = $('#consume_person_pwd').val(); // 密码
	
	$.ajax({
		url: base + 'consume/consumeLogin',
		data: {
			ecmId: ecmId,
			openId: openId,
			name: name,
			pwd: md5(pwd)
		},
		success: function(res){
			if (typeof res == 'string') {
				res = JSON.parse(res);
			}
			
			if (!res.success) {
				app.dialog.alert('绑定失败！' + (res.message != null ? '原因是：' + res.message : ''), '提示');
			} else {
				location.reload();
			}
		}
	});
	
}

/**
 * 核销人员登出
 * @param ecmId 主体编号
 * @param openId 微信个人标志
 */
function consumeLogout(ecmId, openId) {
	app.dialog.confirm('是否确认注销当前账号?', '提示', function () {
		$.ajax({
			url: base + 'consume/consumeLogout',
			data: {
				ecmId: ecmId,
				openId: openId
			},
			success: function(res){
				if (typeof res == 'string') {
					res = JSON.parse(res);
				}
				
				if (!res.success) {
					app.dialog.alert('注销失败！' + (res.message != null ? '原因是：' + res.message : ''), '提示');
				} else {
					app.dialog.alert('注销成功！', '提示', function() {
						location.reload();
					})
				}
			}
		});
    });
	
}

/**
 * 扫码兑奖
 * @param ecmId 主体编号
 * @param openId 操作员标识
 */
function showScanCode(ecmId, openId) {
	emin.wx.scanCode(1, ['qrCode', 'barCode'], function(res) {
//		res = '723652507989';
		app.view.current.router.load({
			url: '/consume/awardInfo?ecmId=' + ecmId + '&openId=' + openId + '&awardCode=' + res
		});
		
	});
}
/**
 * 加载奖券详情
 * @param p
 */
function loadAwardInfo(p) {
	var tpl = awardInfoData.innerHTML,
		view = $('#awardInfo-view'),
		openId = p.openId,
		ecmId = p.ecmId,
		awardCode = p.awardCode;
	
	$.ajax({
		url: base + 'consume/getAwardInfo',
		data: {
			ecmId: ecmId,
			awardCode: awardCode
		},
		success: function(res){
			if (typeof res == 'string') {
				res = JSON.parse(res);
			}
			
			if (!res.success) {
				app.dialog.alert('加载商品详情失败！' + (res.message != null ? '原因是：' + res.message : ''), '提示', function() {
					app.router.back();
				});
			} else {
				if (res.result == null) {
					app.dialog.alert('该奖品码不存在！', '提示', function() {
						app.router.back();
					});
					return false;
				}
				res =  res.result;
				res.ecmId = p.ecmId;
				res.openId = p.openId;
				
				laytpl(tpl).render(res, function(html){
					view.html(html);
				})
			}
		}
	});
	
}

/**
 * 确认兑奖
 * @param ecmId
 * @param openId
 * @param awardCode
 */
function consumeAward(ecmId, openId, awardCode) {
	$.ajax({
		url: base + 'consume/consumeAward',
		data: {
			ecmId: ecmId,
			openId: openId,
			awardCode: awardCode
		},
		success: function(res){
			if (typeof res == 'string') {
				res = JSON.parse(res);
			}
			
			if (!res.success) {
				app.dialog.alert('兑奖失败！' + (res.message != null ? '原因是：' + res.message : ''), '提示', function() {
					app.router.back();
				});
			} else {
				app.dialog.alert('已成功兑奖！', '提示', function() {
					app.router.back();
				});
			}
		}
	});
}


