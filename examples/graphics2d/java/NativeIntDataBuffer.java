package io.github.humbleui.jwm.examples;

import static java.lang.foreign.ValueLayout.JAVA_INT;

import java.awt.image.DataBuffer;
import java.lang.foreign.MemorySegment;

/// Custom DataBuffer wrapping a native memory segment
/// as backing data "array".
/// 
public class NativeIntDataBuffer extends DataBuffer {

    private final MemorySegment segment;

    public NativeIntDataBuffer(long dataIntPtr, int size) {
        super(DataBuffer.TYPE_INT, size);
        this.segment = MemorySegment.ofAddress(dataIntPtr)
                .reinterpret(size * JAVA_INT.byteSize());
    }

	@Override
    public int getElem(int bank, int i) {
        // Read directly from native memory space
        return segment.getAtIndex(JAVA_INT, i);
    }

    @Override
    public void setElem(int bank, int i, int val) {
        // Write directly to native memory space
        segment.setAtIndex(JAVA_INT, i, val);
    }

}
