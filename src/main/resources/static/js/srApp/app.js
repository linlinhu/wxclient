// Theme
var app = null,
	theme = 'auto';

if (document.location.search.indexOf('theme=') >= 0) {
  theme = document.location.search.split('theme=')[1].split('&')[0];
}

// Init App
app = new Framework7({
  id: 'wxclient.scanResult.app',
  root: '#srApp',
  theme: theme
});
/**
 * 页面初始化
 */
app.on('pageInit', function(p) {
	var pageName = $(p.pageEl).attr('data-page'),
		eMsg = null;

	if (pageName == 'sr-detail') {
		eMsg = $($(p.pageEl).find('.eMsg')[0]).val();
		eMsg = eMsg ? ('查询中奖信息失败!原因是：' + eMsg) : '';
	}
	
	if (eMsg) {
		showEMsg(eMsg);
	}
})

if (eMsg) {
	showEMsg(eMsg);
}
/**
 * 显示异常信息
 * @param eMsg
 */
function showEMsg(eMsg) {
	app.dialog.alert(eMsg, '提示', function() {
		
	});
}
/**
 * 跳转详情页面
 * @param ecmId 主体编号
 * @param awardId 奖品编号
 * @param woaId 公众编号
 */
function goSrDetail(ecmId, awardId, woaId) {
	var url = '/scanResult/detail/?ecmId=' + ecmId + '&awardId=' + awardId;
	if (woaId) {
		url += '&woaId=' + woaId;
	}
	app.view.current.router.load({
		url: url
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
		if(event.data == "success"){ // 扫描验证使用成功，关闭消息源请求
			app.dialog.close();
			loadAwardInfo(); // 重新渲染奖券详情
			eventSource.close();
		}
		if (outTime <= 0) { // 链接超时，关闭消息源请求
			layer.msg('连接超时，请重试！');
			eventSource.close();
		}
	}
	
}



