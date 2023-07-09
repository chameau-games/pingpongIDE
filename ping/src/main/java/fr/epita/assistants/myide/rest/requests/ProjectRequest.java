package fr.epita.assistants.myide.rest.requests;


import java.nio.file.Path;

public class ProjectRequest {
    public String name;
    public Path path;

    public ProjectRequest() {
        this.name = "";
        this.path = Path.of("");
    }

    public ProjectRequest(String name,String path) {
        this.name = name;
        this.path = Path.of(path);
    }
}
