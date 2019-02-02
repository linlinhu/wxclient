function scratchCard(p) {
	var imgSrc = p.imgSrc ? p.imgSrc : '',
		frontImgSrc = p.frontImgSrc ? p.frontImgSrc : (base + 'img/scratch-front.png'),
		scratchId = p.id ? p.id : '',
		card = p.id ? document.querySelectorAll('#' + p.id + ' img')[0] : null,
		canvas = p.id ? document.querySelectorAll('#' + p.id + ' canvas')[0] : null,
		admitScratch = p.admitScratch ? true : false;
		cardOnload = function(e) {
			var img = new Image();
			img.src = imgSrc;
			img.addEventListener('load', function(e) {
				var ctx,
					w = 285,
					h = 142,
					offsetX = canvas.offsetLeft,
					offsetY = canvas.offsetTop,
					mousedown = false,
					eventDown = function (e) {
						e.preventDefault();
						mousedown=true;
					},
					eventUp = function (e) {
						e.preventDefault();
						//得到canvas的全部数据
					    var canvasObj = ctx.getImageData(0, 0, canvas.width, canvas.height);
					    var j=0;
					    for(var i = 3; i < canvasObj.data.length; i += 4){
					        if(canvasObj.data[i]==0) j++;
					    }
					    //当被刮开的区域等于一半时，则可以开始处理结果
					    if(j >= canvasObj.data.length / 8){
					    	ctx.clearRect(0,0,w,h);
					    	if (imgSrc.indexOf('unlucky') < 0) {
						    	$('#' + scratchId + ' .see-detail').show();
					    	}
					    }
						mousedown=false;
					},
					eventMove = function (e) {
						e.preventDefault();
						if(mousedown){
							if(e.changedTouches){
								e=e.changedTouches[e.changedTouches.length-1];
							}
							var x = (e.clientX + document.body.scrollLeft || e.pageX) - offsetX || 0, y = (e.clientY + document.body.scrollTop || e.pageY) - offsetY || 0;

							with(ctx){
								beginPath()
								arc(x, y, 25, 0, Math.PI * 2);
								fill();
							} 
						}
					}; 

				canvas.width=w;
				canvas.height=h;
				canvas.style.backgroundImage='url('+img.src+')';
				ctx=canvas.getContext('2d');
				if (!admitScratch) {
					return false;
				}
				ctx.fillStyle='transparent';
				ctx.fillRect(0, 0, w, h);
		        ctx.fillStyle = '#BBBBBB';
		        ctx.strokeStyle = '#ffffff';
		        ctx.fillRect(0, 0, w, h);
		        ctx.font="20px Arial";
		        ctx.fillStyle = '#FFF';
		        ctx.fillText("梦想还是要有的",70,60);
		        ctx.fillText("刮一次试一试",78,100);
		        
		        
				ctx.globalCompositeOperation = 'destination-out';
				canvas.addEventListener('touchstart', eventDown);
				canvas.addEventListener('touchend', eventUp);
				canvas.addEventListener('touchmove', eventMove);
				canvas.addEventListener('mousedown', eventDown);
				canvas.addEventListener('mouseup', eventUp);
				canvas.addEventListener('mousemove', eventMove);
			});
		};
	
	if (!imgSrc || !card || !canvas) {
		console.error('参数缺失');
		return false;
	}
	card.src = base + 'img/ggk_border.png';
	card.addEventListener('load', cardOnload);
}