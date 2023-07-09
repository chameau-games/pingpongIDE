package fr.epita.assistants.myide.rest;


import com.google.gson.JsonObject;
import fr.epita.assistants.MyIde;
import fr.epita.assistants.myide.domain.converter.NodeConverter;
import fr.epita.assistants.myide.domain.entity.*;
import fr.epita.assistants.myide.domain.service.NodeServiceClass;
import fr.epita.assistants.myide.domain.service.ProjectService;
import fr.epita.assistants.myide.rest.requests.*;
import io.quarkus.runtime.Quarkus;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class Endpoint {

    private ProjectClass project;
    private ProjectService project_service;
    private NodeServiceClass node_service = NodeServiceClass.INSTANCE;

    @POST
    @Path("newProject")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response CreateNewProject(ProjectRequest src) {
        if (src.name == "" || src.name == null || src.path == null)
            return Response.status(500).build();
        try {
            Process process = Runtime.getRuntime().exec(String.format("dotnet new console -n \"%s\" -o \"%s\"", src.name, src.path.toString()));
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                project_service = MyIde.init(new MyIde.Configuration(null, null));
                project = new ProjectClass(new NodeClass(src.path));
                return Response.status(200).build();
            } else {
                return Response.status(500).build();
            }
        } catch (Exception e) {
            return Response.status(500).entity(e).build();
        }
    }

    @POST
    @Path("newFile")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response CreateNewFile(FileRequest src) {
        if (src == null || src.path == null)
            return Response.status(500).build();
        try {
            if (src.path.toFile().createNewFile()) {
                return Response.status(200).build();
            } else {
                return Response.status(500).build();
            }
        } catch (Exception e) {
            return Response.status(500).entity(e).build();
        }
    }

    @POST
    @Path("newFolder")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response CreateNewFolder(FileRequest src) {
        if (src == null || src.path == null)
            return Response.status(500).build();
        try {
            if (src.path.toFile().mkdirs()) {
                return Response.status(200).build();
            } else {
                return Response.status(500).build();
            }
        } catch (Exception e) {
            return Response.status(500).entity(e).build();
        }
    }

    @POST
    @Path("getChildren")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response GetChildren(FileRequest src) {
        if (src == null || src.path == null)
            return Response.status(500).build();
        try {
            NodeClass requested = new NodeClass(src.path);
            List<Node> childs = requested.getChildren();
            List<NodeConverter> res = new ArrayList<>();
            for (Node child : childs) {
                res.add(new NodeConverter(new NodeClass(child.getPath())));
            }
            return Response.status(200).entity(res).build();
        } catch (Exception e) {
            return Response.status(500).entity(e).build();
        }
    }

    @GET
    @Path("checkDotNet")
    public Response checkDotNet() {
        try {
            Process process = Runtime.getRuntime().exec("dotnet --version");
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return Response.status(200).build();
            } else {
                return Response.status(500).build();
            }
        } catch (Exception e) {
            return Response.status(500).entity(e).build();
        }
    }

    @POST
    @Path("deleteFile")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response DeleteFile(FileRequest src) {
        if (src == null || src.path == null)
            return Response.status(500).build();
        try {
            NodeClass requested = new NodeClass(src.path);
            node_service.delete(requested);
            return Response.status(200).build();
        } catch (Exception e) {
            return Response.status(500).entity(e).build();
        }
    }

    @POST
    @Path("searchAll")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response SearchAll(SearchRequest src) {
        project.getFeature(Mandatory.Features.Any.SEARCH).get().execute(project, src.target);
        return Response.status(200).build();
    }

    @POST
    @Path("file")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response GetFile(FileRequest src) {
        if (src == null || src.path == null) {
            return Response.status(500).build();
        }
        try {
            NodeClass requested = new NodeClass(src.path);
            if (requested.isFolder()) {
                return Response.status(500).build();
            }
            String content = Files.readString(requested.getPath(), StandardCharsets.UTF_8);
            return Response.status(200).entity(content).build();
        } catch (Exception e) {
            return Response.status(500).entity(e).build();
        }
    }

    @POST
    @Path("startProject")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response Start(FileRequest src) {
        if (src == null || src.path == null)
            return Response.status(500).build();
        try {
            Process process = Runtime.getRuntime().exec(String.format("cmd.exe /c cd \"%s\" & start cmd.exe /k dotnet run", src.path));
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return Response.status(200).build();
            } else {
                return Response.status(500).build();
            }
        } catch (Exception e) {
            return Response.status(500).entity(e).build();
        }
    }

    @POST
    @Path("writeFile")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response WriteFile(WriteRequest src) {
        if (src == null || src.path == null)
            return Response.status(500).build();
        try {
            Files.writeString(src.path, src.content, StandardCharsets.UTF_8);
            return Response.status(200).build();
        } catch (Exception e) {
            return Response.status(500).entity(e).build();
        }
    }

    @POST
    @Path("format")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response Format(FileRequest src) {
        if (src == null || src.path == null)
            return Response.status(500).build();
        try {
            /*File workingDirectory = new File(src.root.toString());
            System.out.println(src.root);
            System.out.println(src.path);
            ProcessBuilder processBuilder = new ProcessBuilder(String.format("dotnet format --include %s", src.path));
            processBuilder.directory(workingDirectory);
            int exitCode = processBuilder.start().waitFor();
            System.out.println("ice");*/
            Process process = Runtime.getRuntime().exec(String.format("./astyle.exe --options=options.ini \"%s\"", src.path));
            int exitCode = process.waitFor();
            String content = Files.readString(src.path, StandardCharsets.UTF_8);
            if (exitCode == 0) {
                return Response.status(200).entity(content).build();
            } else {
                return Response.status(500).build();
            }
        } catch (Exception e) {
            return Response.status(500).entity(e).build();
        }
    }

    @GET
    @Path("quit")
    public Response Quit() {
        Quarkus.asyncExit();
        return Response.status(200).build();
    }
}
