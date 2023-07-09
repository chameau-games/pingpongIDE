package fr.epita.assistants.myide.rest.requests;

import java.nio.file.Path;

public class FormatRequest {
    public Path path;
    public Path root;
    public FormatRequest()
    {
        this.path = null;
    }
    public FormatRequest(String path, String root)
    {
        this.path = Path.of(path);
        this.root = Path.of(root);
    }
}
