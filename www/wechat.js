/**
 * 微信支付插件
 */
var exec = require('cordova/exec');

var wechat = function() {
};

/**
 * 微信支付
 * @param data {String} JSON格式支付信息
 * @param success {Function} 成功回调
 * @param error {Function} 失败回调，返回错误信息
 */
wechat.pay = function(data, success, error) {
    exec(success, error, "WechatPlugin", "pay", [data]);
};

/**
 * 分享至微信
 * @param data {Object} 分享配置对象
 * {
 *   type: 0,         分享类型，0会话，1朋友圈
 *   title: '',       标题
 *   content: '',     内容
 *   pic: '',         缩略图
 *   url: ''          跳转URL
 * }
 * @param success {Function} 成功回调
 * @param error {Function} 失败回调，返回错误信息
 */
wechat.share = function(data, success, error) {
    exec(success, error, "WechatPlugin", "share",
        [data.type, data.title, data.content, data.pic, data.url]);
};

/**
 * 微信联合登录
 * @param success {Function} 成功回调
 * @param error {Function} 失败回调，返回错误信息
 */
wechat.login = function(success, error) {
    exec(success, error, "WechatPlugin", "login", []);
};

/**
 * 检查应用是否已安装
 * @param success {Function} 成功回调
 * @param error {Function} 失败回调，返回错误信息
 */
wechat.checkAppInstalled = function(success, error) {
    exec(success, error, "WechatPlugin", "checkAppInstalled", []);
};

module.exports = wechat;
