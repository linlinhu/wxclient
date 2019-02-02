var emin = {},
	ua = window.navigator.userAgent.match(/MicroMessenger/i);

emin.wx = undefined;
if (ua && ua[0] == "MicroMessenger" && wxConfig) {
	var appID = wxConfig.appId,
		noncestr = wxConfig.nonceStr;
		timestamp = wxConfig.timestamp,
		signature = wxConfig.signature,
		jsAPITicket = wxConfig.jsAPITicket;
		 
	wx.config({
	    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
	    appId: appID, // 必填，公众号的唯一标识
	    timestamp: timestamp, // 必填，生成签名的时间戳
	    nonceStr: noncestr, // 必填，生成签名的随机串
	    signature: signature,// 必填，签名，见附录1
	    jsApiList: [
            'checkJsApi',
            'onMenuShareTimeline',
            'onMenuShareAppMessage',
            'onMenuShareQQ',
            'onMenuShareWeibo',
            'hideMenuItems',
            'showMenuItems',
            'hideAllNonBaseMenuItem',
            'showAllNonBaseMenuItem',
            'translateVoice',
            'startRecord',
            'stopRecord',
            'onRecordEnd',
            'playVoice',
            'pauseVoice',
            'stopVoice',
            'uploadVoice',
            'downloadVoice',
            'chooseImage',
            'previewImage',
            'uploadImage',
            'downloadImage',
            'getNetworkType',
            'openLocation',
            'getLocation',
            'hideOptionMenu',
            'showOptionMenu',
            'closeWindow',
            'scanQRCode',
            'chooseWXPay',
            'openProductSpecificView',
            'addCard',
            'chooseCard',
            'openCard'
        ] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2

	});

	wx.ready(function () {
		emin.wx = {};
		/**
		 * 检查api是否可用
		 * @param jsApiList api，数组形式
		 */
		emin.wx.checkJsApi = function(jsApiList) {
			console.info(12345);
			wx.checkJsApi({
				jsApiList: jsApiList,
				success: function (res) {
					if (res.checkJsApi == 'ok') {
						return res.checkResult;
					}
					
				}
			});
		}
		/**
		 * 分享
		 * @param o 分享内容对象
		 * {
		 * 	title  标题
		 * 	desc 描述
		 * 	link 点击链接
		 * 	imgUrl 封面
		 * }
		 * @param f 分享类型
		 * friends 微信好友
		 * timeline 朋友圈
		 * qq qq好友
		 * qzone qq空间
		 * weibo 新浪微博
		 * 
		 */
		emin.wx.share = function(o, f) {
			var c = {
				  title: o.title ? o.title : '',
			      desc: o.title ? o.title : '',
			      link: o.title ? o.title : '',
			      imgUrl: o.title ? o.title : '',
			      trigger: function (res) {
			    	  console.info('用户点击发送')
			      },
			      success: function (res) {
			    	  alert('已分享');
			      },
			      cancel: function (res) {
			    	  alert('已取消');
			      },
			      fail: function (res) {
			    	  alert('分享失败，原因是：' + JSON.stringify(res));
			      }
			}
			switch(f) {
			case 'friends':
				wx.onMenuShareAppMessage(c);
				break;
			case 'timeline':
				wx.onMenuShareTimeline(c);
				break;
			case 'qq':
				wx.onMenuShareQQ(c);
				break;
			case 'qzone':
				wx.onMenuShareQZone(c);
				break;
			case 'weibo':
				wx.onMenuShareWeibo(c);
				break;
			default:
				break;
			}
		};
		/**
		 * 音频接口
		 */
		emin.wx.voice = {
			start: function() {
				wx.startRecord({
				      cancel: function () {
				    	  alert('用户拒绝授权录音');
				      }
				});	
				wx.onVoiceRecordEnd({//监听录音完成
					complete: function (res) {
						emin.wx.voice.localId = res.localId;
						alert('录音时间已超过一分钟');
					}
				});
			},
			/**
			 * 结束录音
			 * @param callback 回调返回录制的音频在本地的id
			 */
			stop: function(callback) {
				wx.stopRecord({
				      success: function (res) {
				    	  callback(res.localId);
				      }
				});
			},
			/**
			 * 翻译
			 * @param localId 音频Id
			 * @param callback 回调返回翻译结果
			 */
			translate: function(localId, callback) {
				if (!localId) {
					alert('参数错误，无法翻译！');
					return false;
				}
				wx.translateVoice({
					localId: localId,
					complete: function (res) {
						if (res.hasOwnProperty('translateResult')) {
							callback(res.translateResult)
				        } else {
							callback('无法识别')
				        }
					}
				});
			},
			/**
			 * 播放
			 * @param localId 音频id
			 * @param f
			 * pause 暂停
			 * stop 结束播放
			 */
			play: function(localId, f) {
				if (!localId) {
					alert('参数错误，无法进行有效操作！');
					return false;
				}
				var c = {localId: localId};
				switch(f) {
				case 'pause':
					wx.pauseVoice(c);
					break;
				case 'stop':
					wx.stopVoice(c);
					break;
				default:
					wx.playVoice(c);
					wx.onVoicePlayEnd({
					    complete: function (res) {
					    	alert('录音（' + res.localId + '）播放结束');
					    }
					});
					break;
				}
				
			},
			/**
			 * 上传
			 * @param localId 音频id
			 * @param callback 回调返回音频上传后存在服务器的id
			 */
			upload: function(localId, callback) {
				if (!localId) {
					alert('参数错误，无法上传！');
					return false;
				}
				wx.uploadVoice({
				      localId: localId,
				      success: function (res) {
				    	  callback(res.serverId);
				      }
				});
			},
			/**
			 * 下载
			 * @param serverId 服务器音频id
			 * @param callback 回调返回音频下载后存在本地的id
			 */
			download: function(serverId, callback) {
				if (!serverId) {
					alert('参数错误，无法下载！');
					return false;
				}
				wx.downloadVoice({
				      serverId: serverId,
				      success: function (res) {
				    	  callback(res.localId);
				      }
				});
			}
		};
		
		/**
		 * 图片
		 */
		emin.wx.images = {
		    localIds: [],
		    serverIds: [],
		    choose: function(callback) {
		    	wx.chooseImage({
		    	      success: function (res) {
		    	    	  callback(res.localIds);
		    	      }
		    	});
		    },
		    preview: function(localIds, cur) {
		    	wx.previewImage({
		    	      current: localIds[cur],
		    	      urls: localIds
		    	});
		    },
		    upload: function(localIds) {
		    	var i = 0, length = localIds.length;
		    	if (length == 0) {
					alert('没有可上传的项目！');
		    		return false;
		    	}
		    	emin.wx.images.serverIds = [];
		        function up() {
		          wx.uploadImage({
		            localId: localIds[i],
		            success: function (res) {
		              i++;
		              alert('已上传：' + i + '/' + length);
		              emin.wx.images.serverIds.push(res.serverId);
		              if (i < length) {
		            	  up();
		              }
		            },
		            fail: function (res) {
		              alert('上传失败，原因是：' + JSON.stringify(res));
		            }
		          });
		        }
		        up();
		    },
		    download: function(serverIds) {
		    	var i = 0, length = serverIds.length;
		    	if (length == 0) {
					alert('没有可下载的项目！');
		    		return false;
		    	}
		    	emin.wx.images.localIds = [];
		        function dn() {
		          wx.downloadImage({
		            serverId: serverIds[i],
		            success: function (res) {
		              i++;
		              alert('已下载：' + i + '/' + length);
		              emin.wx.images.localIds.push(res.localId);
		              if (i < length) {
		            	  dn();
		              }
		            }
		          });
		        }
		        dn();
		    }
		};
		/**
		 * 获取网络类型
		 */
		emin.wx.getNetworkType = function() {
			wx.getNetworkType({
			      success: function (res) {
			    	  alert('网络类型获取成功，是：' + res.networkType);
			      },
			      fail: function (res) {
			    	  alert('网络类型获取失败，原因是：' + JSON.stringify(res));
			      }
			});
		};
		/**
		 * 位置
		 */
		emin.wx.location = {
			open: function(o) {
				alert(o.latitude + '!' + o.longitude);
				if (!o.latitude || !o.longitude) {
					alert('信息不全，无法打开该地址')
					return false;
				}
				wx.openLocation({
				      latitude: o.latitude,
				      longitude: o.longitude,
				      name: o.name ? o.name : '',
				      address: o.address ? o.address : '',
				      scale: o.scale ? o.scale : '',
				      infoUrl: o.infoUrl ? o.infoUrl :''
				});
			},
			get: function() {
				wx.getLocation({
				      success: function (res) {
				    	  alert(JSON.stringify(res));
				      },
				      cancel: function (res) {
				    	  alert('用户拒绝授权获取地理位置');
				      }
				});
			}
		}
		
		/**
		 * 设置菜单项
		 * @param f
		 * hide
		 * show
		 * @param items 数组形式
		 * 所有菜单项列表

			基本类
			
			举报: "menuItem:exposeArticle"
			
			调整字体: "menuItem:setFont"
			
			日间模式: "menuItem:dayMode"
			
			夜间模式: "menuItem:nightMode"
			
			刷新: "menuItem:refresh"
			
			查看公众号（已添加）: "menuItem:profile"
			
			查看公众号（未添加）: "menuItem:addContact"
			
			传播类
			
			发送给朋友: "menuItem:share:appMessage"
			
			分享到朋友圈: "menuItem:share:timeline"
			
			分享到QQ: "menuItem:share:qq"
			
			分享到Weibo: "menuItem:share:weiboApp"
			
			收藏: "menuItem:favorite"
			
			分享到FB: "menuItem:share:facebook"
			
			分享到 QQ 空间/menuItem:share:QZone
			
			保护类
			
			编辑标签: "menuItem:editTag"
			
			删除: "menuItem:delete"
			
			复制链接: "menuItem:copyUrl"
			
			原网页: "menuItem:originPage"
			
			阅读模式: "menuItem:readMode"
			
			在QQ浏览器中打开: "menuItem:openWithQQBrowser"
			
			在Safari中打开: "menuItem:openWithSafari"
			
			邮件: "menuItem:share:email"
			
			一些特殊公众号: "menuItem:share:brand"
		 * 
		 */
		emin.wx.setMenuItems = function(f, items) {
			var c = {
				success: function (res) {
					alert('设置成功');
				},
				fail: function (res) {
					alert(JSON.stringify(res));
				}
			};
			if (items) {
				c.menuList = items;
				if (f == 'show') {
					wx.showMenuItems(c);
				}
				
				if (f == 'hide') {
					wx.hideMenuItems(c);
				}
			} else {
				if (f == 'show') {
					wx.showOptionMenu();
				}
				
				if (f == 'hide') {
					wx.hideAllNonBaseMenuItem(c);
				    wx.hideOptionMenu();
				}

				if (f == 'showNoneBase') {
					wx.showAllNonBaseMenuItem(c);
				}
				
				if (f == 'hideNoneBase') {
					wx.hideAllNonBaseMenuItem(c);
				}
			}
		}
		/**
		 * 关闭窗口
		 */
		emin.wx.closeWindow = function() {
			wx.closeWindow();
		}
		/**
		 * 调用微信扫码界面
		 * @param needResult 是否需要返回扫描结果
		 * @param codeType 指定扫码类型，数组类型
		 */
		emin.wx.scanCode = function(needResult, codeType, callback) {
			var c = {
			    needResult: needResult ? needResult : 0, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
			    scanType: codeType ? codeType : ["qrCode","barCode"], // 可以指定扫二维码还是一维码，默认二者都有
			    success: function (res) {
				    var result = res.resultStr; // 当needResult 为 1 时，扫码返回的结果
			    	callback(result);
			    }
			}
			wx.scanQRCode(c);
		}
		/**
		 * 支付
		 * @param o 支付信息
		 */
		emin.wx.pay = function(o) {
			wx.chooseWXPay({
			    timestamp: o.timeStamp, // 支付签名时间戳，注意微信jssdk中的所有使用timestamp字段均为小写。但最新版的支付后台生成签名使用的timeStamp字段名需大写其中的S字符
			    nonceStr: o.nonceStr, // 支付签名随机串，不长于 32 位
			    package: o.package, // 统一支付接口返回的prepay_id参数值，提交格式如：prepay_id=***）
			    signType: o.signType, // 签名方式，默认为'SHA1'，使用新版支付需传入'MD5'
			    paySign: o.paySign, // 支付签名
			    success: function (res) {			
			    	alert('支付成功');
			    },
			    cancel:function(res){		
			    	alert('取消支付');
			    },					    
			    fail:function(res){	
			    	alert('支付失败');
			    }
			});
		}
		/**
		 * 跳转微信商品页面接口
		 * @param id 商品id
		 * @param t 0.默认值，普通商品详情页1.扫一扫商品详情页2.小店商品详情页
		 */
		emin.wx.proView = function (id, t) {
			wx.openProductSpecificView({
				productId: id,
				viewType: t ? t : 0
			})
		}
		
		emin.wx.card = {
			codes:[],
			/**
			 * [{
		          cardId: 'pDF3iY9tv9zCGCj4jTXFOo1DxHdo',
		          cardExt: '{"code": "", "openid": "", "timestamp": "1418301401", "signature":"f6628bf94d8e56d56bfa6598e798d5bad54892e5"}'
		      	},
		      	{
		          cardId: 'pDF3iY9tv9zCGCj4jTXFOo1DxHdo',
		          cardExt: '{"code": "", "openid": "", "timestamp": "1418301401", "signature":"f6628bf94d8e56d56bfa6598e798d5bad54892e5"}'
		      	}]
			 */
			add: function(cardLst) {
				wx.addCard({
				      cardList: cardLst,
				      success: function (res) {
				        alert('已添加卡券：' + JSON.stringify(res.cardList));
				      },
				      cancel: function (res) {
				        alert(JSON.stringify(res))
				      }
				});
			},
			choose: function(cardId) {
				wx.chooseCard({
				      cardSign: jsAPITicket,
				      timestamp: timestamp,
				      nonceStr: noncestr,
				      success: function (res) {
				    	  res.cardList = JSON.parse(res.cardList);
				    	  var encrypt_code = res.cardList[0]['encrypt_code'];
				    	  alert('已选择卡券：' + JSON.stringify(res.cardList));
				    	  decryptCode(encrypt_code, function (code) {
				    		  emin.wc.card.codes.push(code);
				    	  });
				      },
				      cancel: function (res) {
				      		alert(JSON.stringify(res))
				      }
				});
			},
			open: function(cardId, codes) {
				var cardList = [];
			    for (var i = 0; i < codes.length; i++) {
			    	cardList.push({
				        cardId: cardId,
				        code: codes[i]
			    	});
			    }
			    wx.openCard({
			    	cardList: cardList,
			    	cancel: function (res) {
			    		alert(JSON.stringify(res))
			    	}
			    });
			}	
		}
		
		function decryptCode(code, callback) {
		    $.getJSON('/jssdk/decrypt_code.php?code=' + encodeURI(code), function (res) {
			      if (res.errcode == 0) {
			    	  emin.wc.card.codes.push(res.code);
			      }
		    });
		}
		
	});

	wx.error(function (res) {
		alert(res.errMsg);
	});
}





