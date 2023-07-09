package fr.epita.assistants.myide.rest.requests;

import java.nio.file.Path;

public class WriteRequest {
    public Path path;
    public String content;

    public WriteRequest(){
        this.path = null;
        this.content = "";
    }

    public WriteRequest(String path, String content){
        this.path = Path.of(path);
        this.content = content;
    }
}
