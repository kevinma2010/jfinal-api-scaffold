package com.mlongbo.jfinal.handler;

import com.jfinal.core.JFinal;
import com.jfinal.handler.Handler;
import com.jfinal.render.RenderFactory;
import com.mlongbo.jfinal.common.bean.Code;
import com.mlongbo.jfinal.common.bean.BaseResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * 处理404接口*
 * @author malongbo
 * @date 15-1-18
 * @package com.pet.project
 */
public class APINotFoundHandler extends Handler {
    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        if (!target.startsWith("/api")) {
            this.nextHandler.handle(target, request, response, isHandled);
            return;
        }
        
        if (JFinal.me().getAction(target, new String[1]) == null) {
            isHandled[0] = true;
            try {
                request.setCharacterEncoding("utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            RenderFactory.me().getJsonRender(new BaseResponse(Code.NOT_FOUND, "resource is not found")).setContext(request, response).render();
        } else {
            this.nextHandler.handle(target, request, response, isHandled);
        }
    }
}
