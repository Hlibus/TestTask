package org.example;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    List<Document> documents = new ArrayList<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if(document.getId().isEmpty()){
            String id = Integer.toString(documents.size() + 1);
            document.setId(id);
            documents.add(document);
        }

        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        return documents.stream()
                .filter(document -> matchesTitlePrefix(document, request.getTitlePrefixes()))
                .filter(document -> matchesContainsContent(document, request.getContainsContents()))
                .filter(document -> matchesAuthorsId(document, request.getAuthorIds()))
                .filter(document -> matchesCreatedDate(document, request.getCreatedFrom(), request.getCreatedTo()))
                .collect(Collectors.toList());
    }

    private boolean matchesTitlePrefix(Document document, List<String> titlePrefixes){
        if(titlePrefixes.isEmpty()){
            return true;
        }
        for(String prefix : titlePrefixes){
            if(document.getTitle().startsWith(prefix)){
                return true;
            }
        }

        return false;
    }

    private boolean matchesContainsContent(Document document, List<String> containsContents){
        if(containsContents.isEmpty()){
            return true;
        }
        for(String content : containsContents){
            if(document.getContent().contains(content)){
                return true;
            }
        }

        return false;
    }

    private boolean matchesAuthorsId(Document document, List<String> authorIds){
        if(authorIds.isEmpty()){
            return true;
        }

        return authorIds.contains(document.getAuthor().getId());
    }

    private boolean matchesCreatedDate(Document document, Instant createdFrom, Instant createdTo){
        Instant created = document.getCreated();
        return created.isBefore(createdTo) && created.isAfter(createdFrom);
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return documents.stream()
                .filter(document -> document.getId().equals(id))
                .findFirst();
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}