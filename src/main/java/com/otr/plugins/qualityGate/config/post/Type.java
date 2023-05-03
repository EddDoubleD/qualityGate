package com.otr.plugins.qualityGate.config.post;

/**
 * Handler types, one handler corresponds to one type
 */
@QualifierValue(TypeSafeQualifier.class)
public enum Type {
    // build change log
    CHANGELOG,
    LIST_OF,
    NONE
}
