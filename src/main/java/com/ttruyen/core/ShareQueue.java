package com.ttruyen.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.ttruyen.model.Content;

public final class ShareQueue {
    public static ConcurrentLinkedDeque<String> shareQueue = new ConcurrentLinkedDeque<>();

    public static void addItem(List<Content> listContent) {
        if (shareQueue.size() < Const.TOP_CHAPTER) {
            for (Content ct : listContent) {
                String link = ct.getId() + "|" + ct.getLink();
                if (!shareQueue.contains(link)) {
                    shareQueue.add(link);
                }
            }
        }
    }

    public static List<String> getItem() {
        List<String> listItem = new ArrayList<>();

        int size = shareQueue.size() > Const.SHARE_QUEUE_SIZE ? Const.SHARE_QUEUE_SIZE : shareQueue.size();

        for (int i = 0; i < size; i++) {
            listItem.add(shareQueue.poll());
        }

        return listItem;
    }
}
