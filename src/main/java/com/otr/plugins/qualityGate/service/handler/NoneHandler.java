package com.otr.plugins.qualityGate.service.handler;

import com.otr.plugins.qualityGate.config.post.Type;
import com.otr.plugins.qualityGate.config.post.TypeSafeQualifier;
import com.otr.plugins.qualityGate.model.LaunchParam;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * The handler does nothing
 */
@Component
@TypeSafeQualifier(Type.NONE)
public class NoneHandler implements Handler {

    /**
     * return blank map
     */
    @Override
    public Map<ResulType, Result> handle(LaunchParam param) {
        return Handler.super.handle(param);
    }
}
