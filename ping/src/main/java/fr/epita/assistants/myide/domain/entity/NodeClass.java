package fr.epita.assistants.myide.domain.entity;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class NodeClass implements fr.epita.assistants.myide.domain.entity.Node {
    private Path path;
    private Type type;

    public NodeClass(Path path) {
        this(path, path.toFile().isFile() ? Types.FILE : Types.FOLDER);
    }

    public NodeClass(Path path, Type type) {
        this.path = path;
        this.type = type;
    }

    /**
     * @return The Node path.
     */
    @Override
    public Path getPath() {
        return path;
    }

    /**
     * @return The Node type.
     */
    @Override
    public Type getType() {
        return type;
    }

    /**
     * If the Node is a Folder, returns a list of its children,
     * else returns an empty list.
     *
     * @return List of node
     */
    @Override
    public List<fr.epita.assistants.myide.domain.entity.@NotNull Node> getChildren() {
        File[] children = path.toFile().listFiles();
        if (isFile() || children == null)
            return new ArrayList<>();
        ArrayList<fr.epita.assistants.myide.domain.entity.Node> res = new ArrayList<>();
        for (File child : children)
            res.add(new NodeClass(child.toPath()));
        return res;
    }

    @Override
    public boolean isFile() {
        return fr.epita.assistants.myide.domain.entity.Node.super.isFile();
    }

    @Override
    public boolean isFolder() {
        return fr.epita.assistants.myide.domain.entity.Node.super.isFolder();
    }
}
