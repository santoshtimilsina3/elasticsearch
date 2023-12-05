package com.elk.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.elk.model.SearchHit;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequestScoped
public class GlobalQueriesService {
    @Inject
    private ElasticsearchClient elasticsearchClient;

    public Collection<SearchHit> searchInApplication(String text) throws IOException {
        SearchResponse<Map> newSearch = elasticsearchClient.search(
                SearchRequest.of(
                        builder -> builder.index("ind*")
                                .query(q ->
                                        q.multiMatch(m ->
                                                m.query(text).fields(List.of("*"))))
                ), Map.class);
        List<Hit<Map>> hits = newSearch.hits().hits();
        List<SearchHit> allresult = new ArrayList<>();
        hits.forEach(hit -> mapToSearchHits(hit,allresult));
        return allresult;
    }

    private void mapToSearchHits(Hit<Map> hit, List<SearchHit> allResult) {
        SearchHit searchHit = new SearchHit();
        searchHit.setId(hit.id());
        searchHit.setIndex(hit.index());
        searchHit.setScore(hit.score());
        searchHit.setSource(hit.source());
        allResult.add(searchHit);
    }
}
