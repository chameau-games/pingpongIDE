package fr.epita.assistants.myide.rest.requests;

import java.nio.file.Path;

public class FileRequest {
    public Path path;
    public FileRequest()
    {
        this.path = null;
    }
    public FileRequest(String path)
    {
        this.path = Path.of(path);
    }
}
