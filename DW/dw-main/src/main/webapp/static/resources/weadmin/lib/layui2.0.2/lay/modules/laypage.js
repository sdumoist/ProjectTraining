/** layui-v2.0.2 MIT License By http://www.layui.com */
 ;layui.define(function(e){"use strict";var a=document,t="getElementById",r="getElementsByTagName",n="laypage",i="layui-disabled",u=function(e){var a=this;a.config=e||{},a.config.index=++s.index,a.render(!0)};u.prototype.type=function(){var e=this.config;if("object"==typeof e.elem)return void 0===e.elem.length?2:3},u.prototype.view=function(){var e=this,a=e.config;a.layout="object"==typeof a.layout?a.layout:["prev","page","next"],a.count=0|a.count,a.curr=0|a.curr||1,a.groups=0|a.groups||5,a.limits="object"==typeof a.limits?a.limits:[10,20,30,40,50],a.limit=0|a.limit||10,a.pages=Math.ceil(a.count/a.limit)||1,a.curr>a.pages&&(a.curr=a.pages),a.groups<0?a.groups=0:a.groups>a.pages&&(a.groups=a.pages),a.prev="prev"in a?a.prev:"&#x4E0A;&#x4E00;&#x9875;",a.next="next"in a?a.next:"&#x4E0B;&#x4E00;&#x9875;";var t=a.pages>a.groups?Math.ceil((a.curr+(a.groups>1?1:0))/(a.groups>0?a.groups:1)):1,r={prev:function(){return a.prev?'<a href="javascript:;" class="layui-laypage-prev'+(1==a.curr?" "+i:"")+'" data-page="'+(a.curr-1)+'">'+a.prev+"</a>":""}(),page:function(){var e=[];if(a.count<1)return"";t>1&&a.first!==!1&&0!==a.groups&&e.push('<a href="javascript:;" class="layui-laypage-first" data-page="1"  title="&#x9996;&#x9875;">'+(a.first||1)+"</a>");var r=Math.floor((a.groups-1)/2),n=t>1?a.curr-r:1,i=t>1?function(){var e=a.curr+(a.groups-r-1);return e>a.pages?a.pages:e}():a.groups;for(i-n<a.groups-1&&(n=i-a.groups+1),a.first!==!1&&n>2&&e.push('<span class="layui-laypage-spr">&#x2026;</span>');n<=i;n++)n===a.curr?e.push('<span class="layui-laypage-curr"><em class="layui-laypage-em" '+(/^#/.test(a.theme)?'style="background-color:'+a.theme+';"':"")+"></em><em>"+n+"</em></span>"):e.push('<a href="javascript:;" data-page="'+n+'">'+n+"</a>");return a.pages>a.groups&&a.pages>i&&a.last!==!1&&(i+1<a.pages&&e.push('<span class="layui-laypage-spr">&#x2026;</span>'),0!==a.groups&&e.push('<a href="javascript:;" class="layui-laypage-last" title="&#x5C3E;&#x9875;"  data-page="'+a.pages+'">'+(a.last||a.pages)+"</a>")),e.join("")}(),next:function(){return a.next?'<a href="javascript:;" class="layui-laypage-next'+(a.curr==a.pages?" "+i:"")+'" data-page="'+(a.curr+1)+'">'+a.next+"</a>":""}(),count:'<span class="layui-laypage-count">共 '+a.count+" 条</span>",limit:function(){var e=['<span class="layui-laypage-limits"><select lay-ignore>'];return layui.each(a.limits,function(t,r){e.push('<option value="'+r+'"'+(r===a.limit?"selected":"")+">"+r+" 条/页</option>")}),e.join("")+"</select></span>"}(),skip:function(){return['<span class="layui-laypage-skip">&#x5230;&#x7B2C;','<input type="text" min="1" value="'+a.curr+'" class="layui-input">','&#x9875;<button type="button" class="layui-laypage-btn">&#x786e;&#x5b9a;</button>',"</span>"].join("")}()};return['<div class="layui-box layui-laypage layui-laypage-'+(a.theme?/^#/.test(a.theme)?"molv":a.theme:"default")+'" id="layui-laypage-'+a.index+'">',function(){var e=[];return layui.each(a.layout,function(a,t){r[t]&&e.push(r[t])}),e.join("")}(),"</div>"].join("")},u.prototype.jump=function(e,a){if(e){var t=this,n=t.config,i=e.children,u=e[r]("button")[0],p=e[r]("input")[0],l=e[r]("select")[0],o=function(){var e=0|p.value.replace(/\s|\D/g,"");e&&(n.curr=e,t.render())};if(a)return o();for(var c=0,g=i.length;c<g;c++)"a"===i[c].nodeName.toLowerCase()&&s.on(i[c],"click",function(){var e=0|this.getAttribute("data-page");e<1||e>n.pages||(n.curr=e,t.render())});l&&s.on(l,"change",function(){var e=this.value;n.curr*e>n.count&&(n.curr=Math.ceil(n.count/e)),n.limit=e,t.render()}),u&&s.on(u,"click",function(){o()})}},u.prototype.skip=function(e){if(e){var a=this,t=e[r]("input")[0];t&&s.on(t,"keyup",function(t){var r=this.value,n=t.keyCode;/^(37|38|39|40)$/.test(n)||(/\D/.test(r)&&(this.value=r.replace(/\D/,"")),13===n&&a.jump(e,!0))})}},u.prototype.render=function(e){var r=this,n=r.config,i=r.type(),u=r.view();2===i?n.elem&&(n.elem.innerHTML=u):3===i?n.elem.html(u):a[t](n.elem)&&(a[t](n.elem).innerHTML=u),n.jump&&n.jump(n,e);var s=a[t]("layui-laypage-"+n.index);r.jump(s),n.hash&&!e&&(location.hash="!"+n.hash+"="+n.curr),r.skip(s)};var s={render:function(e){var a=new u(e);return a.index},index:layui.laypage?layui.laypage.index+1e4:0,on:function(e,a,t){return e.attachEvent?e.attachEvent("on"+a,function(a){t.call(e,a)}):e.addEventListener(a,t,!1),this}};e(n,s)});