package com.otr.plugins.qualityGate.service.handler;

import com.otr.plugins.qualityGate.config.post.Type;
import com.otr.plugins.qualityGate.config.post.TypeSafeQualifier;
import com.otr.plugins.qualityGate.model.LaunchParam;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
@TypeSafeQualifier(Type.NONE)
@Qualifier("easy")
public class EasyHandler implements Handler {

    @Override
    public Map<String, Object> handle(LaunchParam param) {
        return new HashMap<>() {{
            put("SUCCESS", "work is easy");
        }};
    }
}
