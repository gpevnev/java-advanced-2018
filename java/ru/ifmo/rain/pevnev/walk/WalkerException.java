package ru.ifmo.rain.pevnev.walk;

import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Path;

public class WalkerException extends IOException {

    public WalkerException() {
        super();
    }

    public WalkerException(final String message) {
        super(message);
    }

    public WalkerException(String message, Throwable cause) {
        super(message, cause);
    }

    public WalkerException(String message, Path path) {
       super(message + " file \"" + path + '\"');
    }

    public WalkerException(final String message, final FileSystemException fse) {
        super(message + " file \"" + fse.getFile() + '\"', fse);
    }
}
