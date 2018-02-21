package ru.ifmo.rain.pevnev.walk;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.stream.Stream;

public class Walk {

    private final static int PRIME = 0x01000193; // unsigned 16777619

    private final Path src;
    private final Path dst;

    public Walk(final Path src, final Path dst) {
        if (src == null) {
            throw new NullPointerException();
        }

        if (dst == null) {
            throw new NullPointerException();
        }

        this.src = src.normalize();
        this.dst = dst.normalize();
    }

    private String processFile(final String name) {
        int hash = 0x811c9dc5; // unsigned 2166136261

        Path path;

        try {
            path = Paths.get(name);
        } catch (InvalidPathException e) {
            hash = 0;
            return String.format("%08x", hash) + " " + name;
        }

        try (ByteChannel channel = Files.newByteChannel(path)) {
            ByteBuffer bb = ByteBuffer.allocate(1024);


            while (channel.read(bb) >= 0) {
                bb.flip();

                while (bb.hasRemaining()) {
                    hash = (hash * PRIME) ^ (bb.get() & 0x000000ff); // unsigned cast
                }

                bb.clear();
            }
        } catch (IOException e) {
            hash = 0;
        }
        return String.format("%08x", hash) + " " + path.toString();
    }

    private void walk() throws WalkerException {
        try {
            Path parent = dst.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            throw new WalkerException("unable to create", dst);
        }

        try (
                Stream<String> paths = Files.lines(src, StandardCharsets.UTF_8);
                PrintWriter out = new PrintWriter(Files.newBufferedWriter(dst, StandardCharsets.UTF_8))
        ) {
            paths
                    .map(this::processFile)
                    .forEach(out::println);

            if (out.checkError()) {
                throw new WalkerException("unable to finish writing", dst);
            }

        } catch (NoSuchFileException e) {
            throw new WalkerException("unable to find", e);
        } catch (AccessDeniedException e) {
            throw new WalkerException("unable to access", e);
        } catch (IOException e) {
            throw new WalkerException("oops, something bad happened", e);
        }
    }


    public static void main(final String[] args) {
        if (args.length != 2) {
            System.err.println("incorrect usage, should be \"java Walk <input file> <output file>\"");
            return;
        }

        Path src;
        Path dst;

        try {
            src = Paths.get(args[0]);
            dst = Paths.get(args[1]);

            new Walk(src, dst).walk();
        } catch (InvalidPathException e) {
            System.err.println("unable to parse input/output file names");
        } catch (WalkerException e) {
            System.err.println(e.getMessage());
        } catch (Throwable e) {
            System.err.println("oops something very bad happened :(");
            System.err.println(e.getMessage());
        }
    }
}