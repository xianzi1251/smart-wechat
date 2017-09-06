# 微信支付的 Cordova 插件

## 1. Support

- Android √
- iOS     √

内置 SDK 版本号为 `v1.6`

## 2. Installation

```
$ cordova plugin add cordova-plugin-comall-wechat-pay --variable WECHAT_APP_ID=wx7777777777777777
```

另外，因为在 Xcode 7 中默认禁止 http 请求，因此需要安装 `cordova-plugin-transport-security` 插件来解决这个问题。

```
$ cordova plugin add cordova-plugin-transport-security
```

## 3. Usage

首先需要在 config.xml 文件中配置在微信开发平台中注册应用时生成的 AppID。

```xml
<preference name="WECHAT_APPKEY" value="wx7777777777777777" />
```

之后便可在 javascript 调用相应接口：

```javascript
/**
 * 检测当前客户端系统中是否有安装微信应用
 */
wechatPay.checkAppInstalled(function(isInstall) {
    if (isInstall === 'true') {
        console.info('已安装微信应用');
    }
    else {
        console.info('没有安装微信应用');
    }
});

/**
 * 调用支付操作
 */
wechatPay.pay(
    // 支付信息，使用 JSON 格式编码的字符串
    'payInfo',

    // 支付成功回调
    function success() {
    },

    // 支付失败回调
    function error() {
    }
);
```
