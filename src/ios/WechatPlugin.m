#import "WechatPlugin.h"
#import "WXApi.h"
#import "WXApiObject.h"
#import <Cordova/CDV.h>

@implementation WechatPlugin

- (void)pluginInitialize {
    NSDictionary *settings = [self.commandDelegate settings];
    NSString *wechatAppKey = [settings objectForKey:@"wechat_appkey"];

    // 微信初始化
    [WXApi registerApp: wechatAppKey];
}

- (void)pay:(CDVInvokedUrlCommand*)command
{
     NSDictionary *dict = [NSDictionary dictionaryWithDictionary:[command.arguments objectAtIndex:0]];
     NSMutableString *stamp  = [dict objectForKey:@"timeStamp"];
     PayReq *request = [[PayReq alloc] init];
     request.openID = [dict objectForKey:@"appid"];
     request.partnerId = [dict objectForKey:@"partnerId"];
     request.prepayId= [dict objectForKey:@"prepayId"];
     request.package = [dict objectForKey:@"packageValue"];
     request.nonceStr= [dict objectForKey:@"nonceStr"];
     request.timeStamp= stamp.intValue;
     request.sign= [dict objectForKey:@"sign"];
     [WXApi sendReq:request];

     self.currentCallbackId = command.callbackId;
}

- (void)share:(CDVInvokedUrlCommand*)command
{
    NSNumber* type = [command.arguments objectAtIndex:0];
    NSString* title = [command.arguments objectAtIndex:1];
    NSString* content = [command.arguments objectAtIndex:2];
    NSString* pic = [command.arguments objectAtIndex:3];
    NSString* url = [command.arguments objectAtIndex:4];

    dispatch_async(dispatch_get_main_queue(), ^{
        SendMessageToWXReq *request = [[SendMessageToWXReq alloc] init];
        request.bText = NO;
        request.scene = type.intValue == 0 ? WXSceneSession : WXSceneTimeline;

        WXMediaMessage *urlMessage = [WXMediaMessage message];
        urlMessage.title = title;
        urlMessage.description = content;
        UIImage *thumb = [[UIImage alloc] initWithData:[NSData dataWithContentsOfURL:[NSURL URLWithString:pic]]];
        UIGraphicsBeginImageContext(CGSizeMake(150, 150));
        [thumb drawInRect:CGRectMake(0, 0, 150, 150)];
        UIImage* scaledThumb = UIGraphicsGetImageFromCurrentImageContext();
        UIGraphicsEndImageContext();
        [urlMessage setThumbImage:scaledThumb];

        WXWebpageObject *webObj = [WXWebpageObject object];
        webObj.webpageUrl = url;

        urlMessage.mediaObject = webObj;
        request.message = urlMessage;
        [WXApi sendReq:request];
    });

    self.currentCallbackId = command.callbackId;
}

- (void)login:(CDVInvokedUrlCommand*)command
{
    SendAuthReq* req = [[SendAuthReq alloc] init];
    req.scope = @"snsapi_userinfo";
    req.state = @"wechat_login";
    [WXApi sendReq:req];
    self.currentCallbackId = command.callbackId;
}

- (void)checkAppInstalled:(CDVInvokedUrlCommand *)command {
    BOOL isInstalled = [WXApi isWXAppInstalled];
    NSString *isInstalledStr = isInstalled == YES ? @"true" : @"false";

    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:isInstalledStr];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)onResp:(BaseResp*)resp {
    if (!self.currentCallbackId) {
        return;
    }

    NSString *successMessage = nil;
    NSString *cancelMessage = nil;

    if([resp isKindOfClass:[PayResp class]]) {
        cancelMessage = @"支付已取消";
    } else if ([resp isKindOfClass:[SendMessageToWXResp class]]) {
        cancelMessage = @"用户已取消";
    } else if ([resp isKindOfClass:[SendAuthResp class]]) {
        cancelMessage = @"授权已取消";
        SendAuthResp *response = (SendAuthResp *)resp;
        successMessage = response.code;
    }

    CDVPluginResult* result = nil;
    switch (resp.errCode) {
        case WXSuccess:
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:successMessage];
            break;
        case WXErrCodeUserCancel:
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:cancelMessage];
            break;
        default:
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:resp.errStr];
            break;
    }

    [self.commandDelegate sendPluginResult:result callbackId:self.currentCallbackId];
    self.currentCallbackId = nil;
}

- (void)handleOpenURL:(NSNotification *)notification {
    NSURL* url = [notification object];
    NSString* scheme = [url scheme];

    NSLog(@"Scheme: %@", scheme);
    if ([scheme hasPrefix:@"wx"]) {
        [WXApi handleOpenURL:url delegate:self];
    }
}

@end
