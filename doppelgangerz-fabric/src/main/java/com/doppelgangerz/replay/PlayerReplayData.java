package com.doppelgangerz.replay;

import java.util.ArrayList;
import java.util.List;

public class PlayerReplayData {

    public final List<ReplayEvent> events = new ArrayList<>();
    public long recordingStartTick = -1;
    public int blocksPlacedSinceStart = 0;
    public boolean firstBlockDetected = false;
    public boolean doppelgangerSpawned = false;

    public void addEvent(ReplayEvent event, int maxEvents) {
        events.add(event);
        // Secao 1: evitar consumo desnecessario de RAM - buffer limitado
        while (events.size() > maxEvents) {
            events.remove(0);
        }
    }
}
