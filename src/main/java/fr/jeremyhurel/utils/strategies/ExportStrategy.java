package fr.jeremyhurel.utils.strategies;

import java.io.IOException;

public interface ExportStrategy<T> {

    void export(T data, String filePath) throws IOException;

    String getFormatName();

    String getFileExtension();
}
