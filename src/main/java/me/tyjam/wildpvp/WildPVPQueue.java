package me.tyjam.wildpvp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class WildPVPQueue {
    private static final List<UUID> queue = new ArrayList<>();

    public static List<UUID> getQueue() {
        return Collections.unmodifiableList(queue);
    }

    public static boolean isQueued(UUID uuid) {
        return queue.contains(uuid);
    }

    public static void add(UUID uuid) {
        if (!queue.contains(uuid)) queue.add(uuid);
    }

    public static void remove(UUID uuid) {
        queue.remove(uuid);
    }
}

