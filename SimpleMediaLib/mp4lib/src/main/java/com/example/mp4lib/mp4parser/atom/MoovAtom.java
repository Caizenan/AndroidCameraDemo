package com.example.mp4lib.mp4parser.atom;

public class MoovAtom extends ContainerAtom{
    public MoovAtom() {
    }

    @Override
    public String getName() {
        return "moov";
    }
}
