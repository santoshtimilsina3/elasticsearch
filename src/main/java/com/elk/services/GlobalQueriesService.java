package com.elk.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.elk.exception.DatabaseOperationFailed;
import com.elk.model.SearchHit;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequestScoped
public class GlobalQueriesService {

    Logger logger = Logger.getLogger(this.getClass().getName());
    @Inject
    private ElasticsearchClient elasticsearchClient;

    public Collection<SearchHit> searchInApplication(String text) throws IOException, ElasticsearchException {
        List<SearchHit> allresult = new ArrayList<>();

        try {
            SearchResponse<Map> newSearch = elasticsearchClient.search(
                    SearchRequest.of(
                            builder -> builder.index("*")
                                    .query(q ->
                                            q.multiMatch(m ->
                                                    m.query(text).fields(List.of("*"))))
                    ), Map.class);
            List<Hit<Map>> hits = newSearch.hits().hits();
            hits.forEach(hit -> mapToSearchHits(hit, allresult));
        } catch (ElasticsearchException e) {
            logger.log(Level.INFO, "Exception occured while getting data from ES" + String.format(e.getMessage()));
        } catch (IOException e) {
            logger.log(Level.INFO, "I/O  occured while getting data " + String.format(e.getMessage()));
        } catch (Exception e) {
            logger.log(Level.INFO, "Exception occured while getting data " + String.format(e.getMessage()));
        }
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

    public Collection<SearchHit> searchTypos(String text) {
        List<SearchHit> allresult = new ArrayList<>();

        try {
            MultiMatchQuery matchQuery = MultiMatchQuery.of(multi -> multi
                    .query(text)
                    .fields(List.of("*"))
                    .boost(2f)
                    .fuzziness("2")
                    .tieBreaker(0.3)
            );
            Query query = new Query(matchQuery);
            SearchResponse<Map> search = elasticsearchClient.search(SearchRequest.of(builder -> builder
                    .index("ind*")
                    .query(query)
            ), Map.class);

            List<Hit<Map>> hits = search.hits().hits();
            hits.forEach(hit -> mapToSearchHits(hit, allresult));
        } catch (IOException e) {
            throw new DatabaseOperationFailed("Unable to fetch data");
        }
        return allresult;
    }
}
