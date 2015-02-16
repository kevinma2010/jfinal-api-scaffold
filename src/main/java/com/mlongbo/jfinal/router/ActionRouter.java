package com.mlongbo.jfinal.router;

import com.jfinal.config.Routes;
import com.mlongbo.jfinal.action.IndexAction;

/**
 * @author malongbo
 * @date 2015/2/13
 * @package com.snailbaba.router
 */
public class ActionRouter extends Routes{
    @Override
    public void config() {
        add("/", IndexAction.class, "/");
    }
}
