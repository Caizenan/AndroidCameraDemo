package com.example.mp4lib.mp4parser.atom;

public class TrackAtom extends ContainerAtom{
    public TrackAtom() {
    }

    @Override
    public String getName() {
        return "track";
    }
}
