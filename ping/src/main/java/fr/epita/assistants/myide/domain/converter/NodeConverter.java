package fr.epita.assistants.myide.domain.converter;

import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.NodeClass;

import java.nio.file.Path;

public class NodeConverter {
    public String path;
    public String type;

    public NodeConverter(NodeClass src)
    {
        this.path = src.getPath().toString();
        this.type = src.getType().toString();
    }
}
