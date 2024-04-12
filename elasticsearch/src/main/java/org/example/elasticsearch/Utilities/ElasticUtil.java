package org.example.elasticsearch.Utilities;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ElasticUtil {
    public static Supplier<Query> supplier() {
        Supplier<Query> supplier = () -> Query.of(q -> q.matchAll(matchAllQuery()));
        return supplier;
    }

    public static MatchAllQuery matchAllQuery() {
        val matchAllQuery = new MatchAllQuery.Builder();
        return matchAllQuery.build();
    }

    public static Supplier<Query> supplierQueryForBoolQuery(String title, String source) {
        Supplier<Query> supplier = () -> Query.of(q -> q.bool(boolQuery(title, source)));
        return supplier;
    }

    public static BoolQuery boolQuery(String title, String source) {
        val boolQuery = new BoolQuery.Builder();
        return boolQuery.must(termQuery("title", title)).must(termQuery("source", source)).build();
    }

    public static Query termQuery(String field, String value) {
        return Query.of(q -> q.term(term -> term.field(field).value(value)));
    }

    public static List<Query> matchQuery(String source) {
        final List<Query> matches = new ArrayList<>();
        val matchQuery = new MatchQuery.Builder();
        matches.add(Query.of(q -> q.match(matchQuery.field("source").query(source).build())));
        return matches;
    }
}
