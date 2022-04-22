package de.odinoxin.dyntrack;

public class DynTrackAPI {
    private final DynTrack DYNTRACK;

    DynTrackAPI(DynTrack dynTrack) {
        this.DYNTRACK = dynTrack;
    }

    public static String getVersion() {
        return DynTrack.getVersion();
    }
}