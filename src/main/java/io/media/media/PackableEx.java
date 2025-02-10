package io.media.media;

public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}
