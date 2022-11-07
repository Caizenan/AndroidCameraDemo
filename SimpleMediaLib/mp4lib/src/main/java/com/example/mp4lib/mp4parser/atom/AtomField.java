package com.example.mp4lib.mp4parser.atom;

public class AtomField<E> {
    private long offset;
    private long size;
    private String fieldName;
    private E fieldValue;

    public AtomField(String fieldName, E fieldValue, long offset, long size) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.offset = offset;
        this.size = size;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public E getFieldValue() {
        return this.fieldValue;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getOffset() {
        return this.offset;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getSize() {
        return this.size;
    }

    public long getEndPosition() {
        return this.offset + this.size;
    }

    public String toString() {
        return this.fieldName + " = " + this.fieldValue;
    }
}