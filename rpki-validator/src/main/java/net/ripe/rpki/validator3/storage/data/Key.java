/**
 * The BSD License
 *
 * Copyright (c) 2010-2018 RIPE NCC
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   - Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   - Neither the name of the RIPE NCC nor the names of its contributors may be
 *     used to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.ripe.rpki.validator3.storage.data;

import com.google.common.primitives.Longs;
import jetbrains.exodus.ArrayByteIterable;
import jetbrains.exodus.ByteIterable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.ripe.rpki.validator3.storage.Binary;
import net.ripe.rpki.validator3.storage.Bytes;
import net.ripe.rpki.validator3.util.Hex;

import java.io.Serializable;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

@EqualsAndHashCode
@Binary
public class Key implements Serializable {
    @Getter
    private final byte[] bytes;

    public Key(ByteBuffer bb) {
        bytes = Bytes.toBytes(bb);
    }

    public Key(ByteIterable bi) {
        bytes = Bytes.toBytes(bi);
    }

    private Key(byte[] bytes) {
        this.bytes = Arrays.copyOf(bytes, bytes.length);
    }

    public Key(long long_) {
        bytes = Longs.toByteArray(long_);
    }

    public static Key of(byte[] bytes) {
        return new Key(bytes);
    }

    public static Key of(long l) {
        return new Key(l);
    }

    public static Key of(String s) {
        return of(s.getBytes(UTF_8));
    }

    public static Key of(BigInteger bi) {
        return of(bi.toByteArray());
    }

    public static Key of(UUID uuid) {
        final ByteBuffer bb = ByteBuffer.allocateDirect(16);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        bb.flip();
        return new Key(bb);
    }

    public static Set<Key> keys(Key k) {
        return Collections.singleton(k);
    }

    public ByteBuffer toByteBuffer() {
        return Bytes.toDirectBuffer(bytes);
    }

    public ByteIterable toByteIterable(){
        return new ArrayByteIterable(bytes);
    }

    public int size() {
        return bytes.length;
    }

    public Key concat(Key key) {
        return concatAll(this, key);
    }

    private static Key concatAll(final Key... keys) {
        final int size = Arrays.stream(keys).mapToInt(Key::size).sum();
        final ByteBuffer combined = ByteBuffer.allocate(size);
        Arrays.stream(keys).forEach(k -> combined.put(k.bytes));
        return new Key(combined);
    }

    @Override
    public String toString() {
        return Hex.format(bytes);
    }

    public long asLong() {
        return Longs.fromByteArray(bytes);
    }
}
