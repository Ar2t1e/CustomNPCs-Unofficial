package net.minecraft.network;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import noppes.npcs.dimensions.CustomWorldInfo;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

// wrapper
public class FriendlyByteBuf extends ByteBuf {

    private final ByteBuf source;

    public FriendlyByteBuf() { source = Unpooled.buffer(); }

    public FriendlyByteBuf(ByteBuf parentIn) { source = parentIn; }

    @Override
    public int capacity() { return source.capacity(); }

    @Override
    public ByteBuf capacity(int newCapacity) { return source.capacity(newCapacity); }

    @Override
    public int maxCapacity() { return source.maxCapacity(); }

    @Override
    public ByteBufAllocator alloc() { return source.alloc(); }

    @Override
    @Deprecated
    public ByteOrder order() { return source.order(); }

    @Override
    @Deprecated
    public ByteBuf order(ByteOrder endianness) { return source.order(endianness); }

    @Override
    public ByteBuf unwrap() { return source.unwrap(); }

    @Override
    public boolean isDirect() { return source.isDirect(); }

    @Override
    public boolean isReadOnly() { return source.isReadOnly(); }

    @Override
    public ByteBuf asReadOnly() { return source.asReadOnly(); }

    @Override
    public int readerIndex() { return source.readerIndex(); }

    @Override
    public ByteBuf readerIndex(int readerIndex) { return source.readerIndex(readerIndex); }

    @Override
    public int writerIndex() { return source.writerIndex(); }

    @Override
    public ByteBuf writerIndex(int writerIndex) { return source.writerIndex(writerIndex); }

    @Override
    public ByteBuf setIndex(int readerIndex, int writerIndex) { return source.setIndex(readerIndex, writerIndex); }

    @Override
    public int readableBytes() { return source.readableBytes(); }

    @Override
    public int writableBytes() { return source.writableBytes(); }

    @Override
    public int maxWritableBytes() { return source.maxWritableBytes(); }

    @Override
    public boolean isReadable() { return source.isReadable(); }

    @Override
    public boolean isReadable(int size) { return source.isReadable(); }

    @Override
    public boolean isWritable() { return source.isWritable(); }

    @Override
    public boolean isWritable(int size) { return source.isWritable(size); }

    @Override
    public ByteBuf clear() { return source.clear(); }

    @Override
    public ByteBuf markReaderIndex() { return source.markReaderIndex(); }

    @Override
    public ByteBuf resetReaderIndex() { return source.resetReaderIndex(); }

    @Override
    public ByteBuf markWriterIndex() { return source.markWriterIndex(); }

    @Override
    public ByteBuf resetWriterIndex() { return source.resetWriterIndex(); }

    @Override
    public ByteBuf discardReadBytes() { return source.discardReadBytes(); }

    @Override
    public ByteBuf discardSomeReadBytes() { return source.discardSomeReadBytes(); }

    @Override
    public ByteBuf ensureWritable(int minWritableBytes) { return source.ensureWritable(minWritableBytes); }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) { return source.ensureWritable(minWritableBytes, force); }

    @Override
    public boolean getBoolean(int index) { return source.getBoolean(index); }

    @Override
    public byte getByte(int index) { return source.getByte(index); }

    @Override
    public short getUnsignedByte(int index) { return source.getUnsignedByte(index); }

    @Override
    public short getShort(int index) { return source.getShort(index); }

    @Override
    public short getShortLE(int index) { return source.getShortLE(index); }

    @Override
    public int getUnsignedShort(int index) { return source.getUnsignedShort(index); }

    @Override
    public int getUnsignedShortLE(int index) { return source.getUnsignedShortLE(index); }

    @Override
    public int getMedium(int index) { return source.getMedium(index); }

    @Override
    public int getMediumLE(int index) { return source.getMediumLE(index); }

    @Override
    public int getUnsignedMedium(int index) { return source.getUnsignedMedium(index); }

    @Override
    public int getUnsignedMediumLE(int index) { return source.getUnsignedMediumLE(index); }

    @Override
    public int getInt(int index) { return source.getInt(index); }

    @Override
    public int getIntLE(int index) { return source.getIntLE(index); }

    @Override
    public long getUnsignedInt(int index) { return source.getUnsignedInt(index); }

    @Override
    public long getUnsignedIntLE(int index) { return source.getUnsignedIntLE(index); }

    @Override
    public long getLong(int index) { return source.getLong(index); }

    @Override
    public long getLongLE(int index) { return source.getLongLE(index); }

    @Override
    public char getChar(int index) { return source.getChar(index); }

    @Override
    public float getFloat(int index) { return source.getFloat(index); }

    @Override
    public double getDouble(int index) { return source.getDouble(index); }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst) { return source.getBytes(index, dst); }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int length) { return source.getBytes(index, dst, length); }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) { return source.getBytes(index, dst, dstIndex, length); }

    @Override
    public ByteBuf getBytes(int index, byte[] dst) { return source.getBytes(index, dst); }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) { return source.getBytes(index, dst, dstIndex, length); }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) { return source.getBytes(index, dst); }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException { return source.getBytes(index, out, length); }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException { return source.getBytes(index, out, length); }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException { return source.getBytes(index, out, position, length); }

    @Override
    public CharSequence getCharSequence(int index, int length, Charset charset) { return source.getCharSequence(index, length, charset); }

    @Override
    public ByteBuf setBoolean(int index, boolean value) { return source.setBoolean(index, value); }

    @Override
    public ByteBuf setByte(int index, int value) { return source.setByte(index, value); }

    @Override
    public ByteBuf setShort(int index, int value) { return source.setShort(index, value); }

    @Override
    public ByteBuf setShortLE(int index, int value) { return source.setShortLE(index, value); }

    @Override
    public ByteBuf setMedium(int index, int value) { return source.setMedium(index, value); }

    @Override
    public ByteBuf setMediumLE(int index, int value) { return source.setMediumLE(index, value); }

    @Override
    public ByteBuf setInt(int index, int value) { return source.setInt(index, value); }

    @Override
    public ByteBuf setIntLE(int index, int value) { return source.setIntLE(index, value); }

    @Override
    public ByteBuf setLong(int index, long value) { return source.setLong(index, value); }

    @Override
    public ByteBuf setLongLE(int index, long value) { return source.setLongLE(index, value); }

    @Override
    public ByteBuf setChar(int index, int value) { return source.setChar(index, value); }

    @Override
    public ByteBuf setFloat(int index, float value) { return source.setFloat(index, value); }

    @Override
    public ByteBuf setDouble(int index, double value) { return source.setDouble(index, value); }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src) { return source.setBytes(index, src); }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int length) { return source.setBytes(index, src, length); }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) { return source.setBytes(index, src, srcIndex, length); }

    @Override
    public ByteBuf setBytes(int index, byte[] src) { return source.setBytes(index, src); }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) { return source.setBytes(index, src, srcIndex, length); }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) { return source.setBytes(index, src); }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException { return source.setBytes(index, in, length); }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException { return source.setBytes(index, in, length); }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException { return source.setBytes(index, in, position, length); }

    @Override
    public ByteBuf setZero(int index, int length) { return source.setZero(index, length); }

    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) { return source.setCharSequence(index, sequence, charset); }

    @Override
    public boolean readBoolean() { return source.readBoolean(); }

    @Override
    public byte readByte() { return source.readByte(); }

    @Override
    public short readUnsignedByte() { return source.readUnsignedByte(); }

    @Override
    public short readShort() { return source.readShort(); }

    @Override
    public short readShortLE() { return source.readShortLE(); }

    @Override
    public int readUnsignedShort() { return source.readUnsignedShort(); }

    @Override
    public int readUnsignedShortLE() { return source.readUnsignedShortLE(); }

    @Override
    public int readMedium() { return source.readMedium(); }

    @Override
    public int readMediumLE() { return source.readMediumLE(); }

    @Override
    public int readUnsignedMedium() { return source.readUnsignedMedium(); }

    @Override
    public int readUnsignedMediumLE() { return source.readUnsignedMediumLE(); }

    @Override
    public int readInt() { return source.readInt(); }

    @Override
    public int readIntLE() { return source.readIntLE(); }

    @Override
    public long readUnsignedInt() { return source.readUnsignedInt(); }

    @Override
    public long readUnsignedIntLE() { return source.readUnsignedIntLE(); }

    @Override
    public long readLong() { return source.readLong(); }

    @Override
    public long readLongLE() { return source.readLongLE(); }

    @Override
    public char readChar() { return source.readChar(); }

    @Override
    public float readFloat() { return source.readFloat(); }

    @Override
    public double readDouble() { return source.readDouble(); }

    @Override
    public ByteBuf readBytes(int length) { return source.readBytes(length); }

    @Override
    public ByteBuf readSlice(int length) { return source.readSlice(length); }

    @Override
    public ByteBuf readRetainedSlice(int length) { return source.readRetainedSlice(length); }

    @Override
    public ByteBuf readBytes(ByteBuf dst) { return source.readBytes(dst); }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int length) { return source.readBytes(dst, length); }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) { return source.readBytes(dst, dstIndex, length); }

    @Override
    public ByteBuf readBytes(byte[] dst) { return source.readBytes(dst); }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) { return source.readBytes(dst, dstIndex, length); }

    @Override
    public ByteBuf readBytes(ByteBuffer dst) { return source.readBytes(dst); }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException { return source.readBytes(out, length); }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException { return source.readBytes(out, length); }

    @Override
    public CharSequence readCharSequence(int length, Charset charset) { return source.readCharSequence(length, charset); }

    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException { return source.readBytes(out, position, length); }

    @Override
    public ByteBuf skipBytes(int length) { return source.skipBytes(length); }

    @Override
    public ByteBuf writeBoolean(boolean value) { return source.writeBoolean(value); }

    @Override
    public ByteBuf writeByte(int value) { return source.writeByte(value); }

    @Override
    public ByteBuf writeShort(int value) { return source.writeShort(value); }

    @Override
    public ByteBuf writeShortLE(int value) { return source.writeShortLE(value); }

    @Override
    public ByteBuf writeMedium(int value) { return source.writeMedium(value); }

    @Override
    public ByteBuf writeMediumLE(int value) { return source.writeMediumLE(value); }

    @Override
    public ByteBuf writeInt(int value) { return source.writeInt(value); }

    @Override
    public ByteBuf writeIntLE(int value) { return source.writeIntLE(value); }

    @Override
    public ByteBuf writeLong(long value) { return source.writeLong(value); }

    @Override
    public ByteBuf writeLongLE(long value) { return source.writeLongLE(value); }

    @Override
    public ByteBuf writeChar(int value) { return source.writeChar(value); }

    @Override
    public ByteBuf writeFloat(float value) { return source.writeFloat(value); }

    @Override
    public ByteBuf writeDouble(double value) { return source.writeDouble(value); }

    @Override
    public ByteBuf writeBytes(ByteBuf src) { return source.writeBytes(src); }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int length) { return source.writeBytes(src, length); }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) { return source.writeBytes(src, srcIndex, length); }

    @Override
    public ByteBuf writeBytes(byte[] src) { return source.writeBytes(src); }

    @Override
    public ByteBuf writeBytes(byte[] src, int srcIndex, int length) { return source.writeBytes(src, srcIndex, length); }

    @Override
    public ByteBuf writeBytes(ByteBuffer src) { return source.writeBytes(src); }

    @Override
    public int writeBytes(InputStream in, int length) throws IOException { return source.writeBytes(in, length); }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException { return source.writeBytes(in, length); }

    @Override
    public int writeBytes(FileChannel in, long position, int length) throws IOException { return source.writeBytes(in, position, length); }

    @Override
    public ByteBuf writeZero(int length) { return source.writeZero(length); }

    @Override
    public int writeCharSequence(CharSequence sequence, Charset charset) { return source.writeCharSequence(sequence, charset); }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) { return source.indexOf(fromIndex, toIndex, value); }

    @Override
    public int bytesBefore(byte value) { return source.bytesBefore(value); }

    @Override
    public int bytesBefore(int length, byte value) { return source.bytesBefore(length, value); }

    @Override
    public int bytesBefore(int index, int length, byte value) { return source.bytesBefore(index, length, value); }

    @Override
    public int forEachByte(ByteProcessor processor) { return source.forEachByte(processor); }

    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) { return source.forEachByte(index, length, processor); }

    @Override
    public int forEachByteDesc(ByteProcessor processor) { return source.forEachByteDesc(processor); }

    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) { return source.forEachByteDesc(index, length, processor); }

    @Override
    public FriendlyByteBuf copy() { return new FriendlyByteBuf(source.copy()); }

    @Override
    public FriendlyByteBuf copy(int index, int length) { return new FriendlyByteBuf(source.copy(index, length)); }

    @Override
    public FriendlyByteBuf slice() { return new FriendlyByteBuf(source.slice()); }

    @Override
    public FriendlyByteBuf retainedSlice() { return new FriendlyByteBuf(source.retainedSlice()); }

    @Override
    public FriendlyByteBuf slice(int index, int length) { return new FriendlyByteBuf(source.slice(index, length)); }

    @Override
    public FriendlyByteBuf retainedSlice(int index, int length) { return new FriendlyByteBuf(source.retainedSlice(index, length)); }

    @Override
    public FriendlyByteBuf duplicate() { return new FriendlyByteBuf(source.duplicate()); }

    @Override
    public FriendlyByteBuf retainedDuplicate() { return new FriendlyByteBuf(source.retainedDuplicate()); }

    @Override
    public int nioBufferCount() { return source.nioBufferCount(); }

    @Override
    public ByteBuffer nioBuffer() { return source.nioBuffer(); }

    @Override
    public ByteBuffer nioBuffer(int index, int length) { return source.nioBuffer(index, length); }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) { return source.internalNioBuffer(index, length); }

    @Override
    public ByteBuffer[] nioBuffers() { return source.nioBuffers(); }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) { return source.nioBuffers(index, length); }

    @Override
    public boolean hasArray() { return source.hasArray(); }

    @Override
    public byte[] array() { return source.array(); }

    @Override
    public int arrayOffset() { return source.arrayOffset(); }

    @Override
    public boolean hasMemoryAddress() { return source.hasMemoryAddress(); }

    @Override
    public long memoryAddress() { return source.memoryAddress(); }

    @Override
    public String toString(Charset charset) { return source.toString(charset); }

    @Override
    public String toString(int index, int length, Charset charset) { return source.toString(index, length, charset); }

    @Override
    public int hashCode() { return source.hashCode(); }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ByteBuf && source != null) {
            return source.equals(obj);
        }
        return false;
    }

    @Override
    public int compareTo(ByteBuf buffer) { return source.compareTo(buffer); }

    @Override
    public String toString() { return source.toString(); }

    @Override
    public ByteBuf retain(int increment) { return source.retain(); }

    @Override
    public int refCnt() { return source.refCnt(); }

    @Override
    public ByteBuf retain() { return source.retain(); }

    @Override
    public ByteBuf touch() { return source.touch(); }

    @Override
    public ByteBuf touch(Object hint) { return source.touch(hint); }

    @Override
    public boolean release() { return source.release(); }

    @Override
    public boolean release(int decrement) { return source.release(decrement); }

    // in 1.20.1
    public ITextComponent readComponent() {
        return Component.jsonToComponent(readUtf(262144)).getParent();
    }

    public void writeComponent(ITextComponent component) {
        writeUtf(ITextComponent.Serializer.componentToJson(component), 262144);
    }

    @SuppressWarnings("unused")
    public int[] readIntArray() {
        int[] a = new int[source.readInt()];
        for (int i = 0; i < a.length; i++) { a[i] = source.readInt(); }
        return a;
    }

    @SuppressWarnings("unused")
    public FriendlyByteBuf writeIntArray(int[] a) {
        source.writeInt(a.length);
        for (int i : a) { source.writeInt(i); }
        return this;
    }

    public NBTTagCompound readAnySizeNbt() { return this.readNbt(NBTSizeTracker.INFINITE); }

    public NBTTagCompound readNbt() { return this.readNbt(new NBTSizeTracker(2097152L)); }

    public NBTTagCompound readNbt(NBTSizeTracker sizeTracker) {
        byte[] bytes = new byte[source.readInt()];
        source.readBytes(bytes);
        try (DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes))))) {
            return CompressedStreamTools.read(datainputstream, sizeTracker);
        }
        catch (Exception e) { return new NBTTagCompound(); }
    }

    public FriendlyByteBuf writeNbt(NBTTagCompound compound) {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        try (DataOutputStream dataoutputstream = new DataOutputStream(new GZIPOutputStream(bytearrayoutputstream))) {
            CompressedStreamTools.write(compound, dataoutputstream);
        }
        catch (Exception ignored) { }
        byte[] bytes = bytearrayoutputstream.toByteArray();
        source.writeInt(bytes.length);
        source.writeBytes(bytes);
        return this;
    }

    public String readUtf() { return readUtf(32767); }

    public String readUtf(int length) {
        int i = getMaxEncodedUtfLength(length);
        int j = readVarInt();
        if (j > i) { throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + j + " > " + i + ")"); }
        else if (j < 0) { throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!"); }
        else {
            String s = toString(readerIndex(), j, Charsets.UTF_8);
            readerIndex(readerIndex() + j);
            if (s.length() > length) { throw new DecoderException("The received string length is longer than maximum allowed (" + s.length() + " > " + length + ")"); }
            else { return s; }
        }
    }

    private static int getMaxEncodedUtfLength(int length) { return length * 3; }

    public FriendlyByteBuf writeUtf(String s) {
        writeUtf(s, 32767);
        return this;
    }

    public FriendlyByteBuf writeUtf(String s, int length) {
        if (s.length() > length) {
            int var10002 = s.length();
            throw new EncoderException("String too big (was " + var10002 + " characters, max " + length + ")");
        } else {
            byte[] abyte = s.getBytes(StandardCharsets.UTF_8);
            int i = getMaxEncodedUtfLength(length);
            if (abyte.length > i) { throw new EncoderException("String too big (was " + abyte.length + " bytes encoded, max " + i + ")"); }
            else {
                writeVarInt(abyte.length);
                source.writeBytes(abyte);
            }
        }
        return this;
    }

    public CustomWorldInfo readWorldInfo() { return new CustomWorldInfo(ByteBufUtils.readTag(source)); }

    public void writeWorldInfo(WorldInfo wi) {
        ByteBufUtils.writeTag(source, wi.cloneNBTCompound(wi.getPlayerNBTTagCompound()));
    }

    public UUID readUUID() { return new UUID(source.readLong(), source.readLong()); }

    public FriendlyByteBuf writeUUID(UUID uuid) {
        source.writeLong(uuid.getMostSignificantBits());
        source.writeLong(uuid.getLeastSignificantBits());
        return this;
    }

    public BlockPos readBlockPos() { return BlockPos.fromLong(source.readLong()); }

    public FriendlyByteBuf writeBlockPos(BlockPos pos) {
        source.writeLong(pos.toLong());
        return this;
    }

    public <T extends Enum<T>> T readEnum(Class<T> enumClass) { return enumClass.getEnumConstants()[readVarInt()]; }

    public FriendlyByteBuf writeEnum(Enum<?> enumVar) {
        writeVarInt(enumVar.ordinal());
        return this;
    }

    public int readVarInt() {
        int i = 0;
        int j = 0;
        byte b0;
        do {
            b0 = source.readByte();
            i |= (b0 & 127) << j++ * 7;
            if (j > 5) { throw new RuntimeException("VarInt too big"); }
        }  while((b0 & 128) == 128);
        return i;
    }

    public FriendlyByteBuf writeVarInt(int ordinal) {
        while((ordinal & -128) != 0) {
            source.writeByte(ordinal & 127 | 128);
            ordinal >>>= 7;
        }
        source.writeByte(ordinal);
        return this;
    }

    public FriendlyByteBuf writeResourceLocation(ResourceLocation location) {
        writeUtf(location.toString());
        return this;
    }

    public ResourceLocation readResourceLocation() { return new ResourceLocation(readUtf()); }

    public FriendlyByteBuf writeItemStack(ItemStack stack, boolean ignoredLimitedTag) {
        if (stack.isEmpty()) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            NBTTagCompound compound = new NBTTagCompound();
            writeNbt(stack.writeToNBT(compound));
        }
        return this;
    }

    public ItemStack readItem() {
        if (!readBoolean()) { return ItemStack.EMPTY; }
        return new ItemStack(readNbt());
    }

    public ByteBuf getSource() { return source; }

}
