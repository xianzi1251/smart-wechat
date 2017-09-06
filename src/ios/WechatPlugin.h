#import <Cordova/CDV.h>
#import "WXApi.h"

@interface WechatPlugin : CDVPlugin <WXApiDelegate>

@property (nonatomic, strong) NSString *currentCallbackId;

- (void)pay:(CDVInvokedUrlCommand*)command;
- (void)share:(CDVInvokedUrlCommand*)command;
- (void)login:(CDVInvokedUrlCommand*)command;
- (void)checkAppInstalled:(CDVInvokedUrlCommand*)command;

@end
