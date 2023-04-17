package com.otr.plugins.qualityGate.utils;

import com.otr.plugins.qualityGate.model.gitlab.Commit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class TaskFilterTest {

    @Test
    void commitParserTest() {
        assertEquals(2, TaskFilter.parseCommit(new Commit(null,
                "EXP-123", "Task by ticket SP-4107", null)).size());
        assertEquals(1, TaskFilter.parseCommit(new Commit(null,
                "Merge branch 'feature-EXP-255859' into 'master'", "some text about task", null))
                .size());
        assertEquals(1, TaskFilter.parseCommit(new Commit(null,
                        "Merge branch 'feature-EXP-255859' into 'master'", "EXP-255859", null))
                .size());
    }

}