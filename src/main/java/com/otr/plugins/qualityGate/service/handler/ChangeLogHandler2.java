package com.otr.plugins.qualityGate.service.handler;

import com.otr.plugins.qualityGate.config.post.Type;
import com.otr.plugins.qualityGate.config.post.TypeSafeQualifier;
import com.otr.plugins.qualityGate.model.LaunchParam;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@TypeSafeQualifier(Type.CHANGELOG)
@Qualifier("changeLog2")
public class ChangeLogHandler2 implements Handler {

    @Override
    public Map<String, Object> handle(LaunchParam param) {
        return new HashMap<>() {{
            put("SUCCESS", "WORK is hard");
        }};
    }
}
