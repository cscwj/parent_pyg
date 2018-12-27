package cn.itcast.core.controller;

import cn.itcast.core.service.PayService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private PayService payService;

    //生成二维码
    @RequestMapping("/createNative")
    public Map<String, String> queryPayStatus() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return payService.createNative(name);
    }

    //查询二维码状态
    @RequestMapping("queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {

        try {
            //方式一:腾讯通知你,但这是内网腾讯主动找我们找不到,所以方式二我们不停询问
            //不停调用方法,问腾讯有没有付款
            int i = 0;
            while (true) {
                //交易状态 	trade_state  :  SUCCESS—支付成功,REFUND—转入退款,NOTPAY—未支付,CLOSED—已关闭,REVOKED—已撤销（付款码支付）,USERPAYING--用户支付中（付款码支付）,PAYERROR--支付失败(其他原因，如银行返回失败),支付状态机请见下单API页面
                //这里从简,只判断成功和其他状态
                Map<String, String> map = payService.queryPayStatus(out_trade_no);
                System.out.println("支付状态:" + map.get("trade_state"));

                if ("SUCCESS".equals(map.get("trade_state"))) {
                    return new Result(true, "付款成功");

                } else {
                    Thread.sleep(3000);  //睡眠3秒,腾讯不予许过于频繁
                    i++;
                    if (i > 100) {
                        //TODO 这里需要调用,关闭订单的api
                        //这里前台跳转页面,掩饰一下,实际验证码2小时生命周期
                        return new Result(false, "二维码超时");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "查询失败");
        }

    }


}
