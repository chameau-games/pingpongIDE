package fr.epita.assistants.myide.rest.requests;

public class SearchRequest {
    public String target;

    public SearchRequest()
    {
        this.target = "";
    }

    public SearchRequest(String request)
    {
        this.target = request;
    }
}
