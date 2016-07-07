package com.noticeditorteam.noticeditor.plugin.attachments;

import com.noticeditorteam.noticeditor.controller.NoticeController;
import com.noticeditorteam.noticeditor.io.IOUtil;
import com.noticeditorteam.noticeditor.model.Attachment;
import com.noticeditorteam.noticeditor.model.Attachments;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public final class FromURLImporter extends AttachmentImporter {

    public FromURLImporter(ResourceBundle resources) {
        super(resources);
        System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1; WOW64)"
                + " AppleWebKit/537.36 (KHTML, like Gecko)"
                + " Chrome/51.0.2704.103 Safari/537.36");
    }

    @Override
    public String name() {
        return resources.getString("import_from_urls");
    }

    @Override
    protected Attachments call() throws Exception {
        final String[] lines = getTextData().split("\n");

        // Split urls into groups
        final int numberOfGroups = Runtime.getRuntime().availableProcessors();
        final int limitItems = (int) Math.ceil(lines.length / (double)numberOfGroups);
        final List<List<String>> urlsGroup = splitUrlsIntoGroups(lines, limitItems);
        // Actual number of groups
        final int groupsSize = urlsGroup.size();

        // Download in separate threads
        final Logger logger = NoticeController.getLogger();
        final Thread[] threads = new Thread[groupsSize];
        final AtomicLong downloadsCount = new AtomicLong(0L);
        final List<Attachment> attachments = new CopyOnWriteArrayList<>();
        for (int i = 0; i < groupsSize; i++) {
            final List<String> urls = urlsGroup.get(i);
            threads[i] = new Thread(() -> {
                final int urlsSize = urls.size();
                for (String url : urls) {
                    if (isCancelled()) return;

                    try {
                        final Attachment att = new Attachment(
                                getFilenameFrom(url),
                                IOUtil.download(url));
                        attachments.add(att);
                    } catch (IOException ioe) {
                        logger.log(Level.SEVERE, url, ioe);
                    }
                    updateProgress(downloadsCount.incrementAndGet(), urlsSize);
                }
            });
            threads[i].start();
        }

        // Wait until complete
        for (Thread thread : threads) {
            thread.join();
        }

        return new Attachments(attachments);
    }

    private List<List<String>> splitUrlsIntoGroups(String[] lines, int limitItems) {
        return Stream.of(lines)
                .filter(str -> str.startsWith("http"))
                .collect(ArrayList::new,
                        (list, url) -> {
                            final boolean needNewList = (list.isEmpty() || (list.get(list.size() - 1).size() >= limitItems));
                            final List<String> innerList = needNewList
                                    ? new ArrayList<>(limitItems)
                                    : list.get(list.size() - 1);
                            innerList.add(url);
                            if (needNewList) {
                                list.add(innerList);
                            }
                        },
                        (list1, list2) -> list1.addAll(list2));
    }

    private static String getFilenameFrom(final String url) {
        String result = url;
        final int lastSlash = url.lastIndexOf('/');
        if (lastSlash > 0) {
            result = url.substring(lastSlash + 1);
        }
        if (result.length() > 60) {
            result = result.substring(result.length() - 60);
        }
        return result;
    }

}
