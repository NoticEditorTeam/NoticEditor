package com.noticeditorteam.noticeditor.model;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.noticeditorteam.noticeditor.io.JsonFormat;
import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

public class NoticeTreeItemBenchmarksTest {

    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

    private static final int NESTING_LEVEL = 6000;
    private static NoticeTreeItem root;

    @BeforeClass
    public static void beforeClass() {
        root = new NoticeTreeItem("root");
        NoticeTreeItem branch = root;
        for (int i = 0; i < NESTING_LEVEL; i++) {
            NoticeTreeItem node = new NoticeTreeItem("branch " + i);
            branch.addChild(node);
            branch = node;
        }
    }

    @BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 1)
    @Ignore
    @Test
    public void testJsonExport() throws JSONException {
        JsonFormat.with(null).export(root);
    }
}
