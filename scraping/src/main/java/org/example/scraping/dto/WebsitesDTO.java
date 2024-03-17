package org.example.scraping.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebsitesDTO {

    public static final Map<String, Map<String, String>> websites = new HashMap<>();

    static {
        // Define categories for newsit
        HashMap<String, String> newsitCategories = new HashMap<>();
        newsitCategories.put("politics", "https://www.newsit.gr/category/politikh/");
        newsitCategories.put("economy", "https://www.newsit.gr/category/oikonomia/");
        newsitCategories.put("sports", "https://www.newsit.gr/category/athlitika/");
        newsitCategories.put("lifestyle", "https://www.newsit.gr/category/lifestyle/");
        newsitCategories.put("health", "https://www.newsit.gr/category/ygeia/");
        newsitCategories.put("technology", "https://www.newsit.gr/category/texnologia/");
        websites.put("newsit", newsitCategories);

        // Define categories for enikos
        HashMap<String, String> enikosCategories = new HashMap<>();
        enikosCategories.put("politics", "https://www.enikos.gr/politics/");
        enikosCategories.put("economy", "https://www.enikos.gr/economy/");
        enikosCategories.put("sports", "https://www.enikos.gr/sports/");
        enikosCategories.put("lifestyle", "https://www.enikos.gr/lifestyle/");
        enikosCategories.put("health", "https://www.enikos.gr/ygeia/");
        enikosCategories.put("technology", "https://www.enikos.gr/technology/");
        websites.put("enikos", enikosCategories);

        // Define categories for reporter
        HashMap<String, String> reporterCategories = new HashMap<>();
        reporterCategories.put("politics", "https://www.reporter.gr/Eidhseis/Politikh");
        reporterCategories.put("economy", "https://www.reporter.gr/Eidhseis/Oikonomia");
        reporterCategories.put("health", "https://www.reporter.gr/Eidhseis/Ygeia");
        reporterCategories.put("technology", "https://www.reporter.gr/Eidhseis/technologia");
        websites.put("reporter", reporterCategories);

        // Define categories for kathimerini
        HashMap<String, String> kathimeriniCategories = new HashMap<>();
        kathimeriniCategories.put("politics", "https://www.kathimerini.gr/politics/");
        kathimeriniCategories.put("economy", "https://www.kathimerini.gr/economy/");
        kathimeriniCategories.put("sports", "https://www.kathimerini.gr/athletics/");
        kathimeriniCategories.put("lifestyle", "https://www.kathimerini.gr/life/");
        kathimeriniCategories.put("health", "https://www.kathimerini.gr/life/");
        kathimeriniCategories.put("technology", "https://www.kathimerini.gr/life/technology/");
        websites.put("kathimerini", kathimeriniCategories);

        // Define categories for newsbomb
        HashMap<String, String> newsbombCategories = new HashMap<>();
        newsbombCategories.put("politics", "https://www.newsbomb.gr/politikh");
        newsbombCategories.put("economy", "https://www.newsbomb.gr/oikonomia");
        newsbombCategories.put("sports", "https://www.newsbomb.gr/sports");
        newsbombCategories.put("lifestyle", "https://www.newsbomb.gr/tag/lifestyle");
        newsbombCategories.put("health", "https://www.newsbomb.gr/ygeia");
        newsbombCategories.put("technology", "https://www.newsbomb.gr/bombplus/texnologia");
        websites.put("newsbomb", newsbombCategories);

        // Define categories for cnn
        HashMap<String, String> cnnCategories = new HashMap<>();
        cnnCategories.put("politics", "https://www.cnn.gr/politiki");
        cnnCategories.put("economy", "https://www.cnn.gr/oikonomia/chrima");
        cnnCategories.put("sports", "https://www.cnn.gr/sports");
        cnnCategories.put("lifestyle", "https://www.cnn.gr/style");
        cnnCategories.put("health", "https://www.cnn.gr/tag/ygeia");
        cnnCategories.put("technology", "https://www.cnn.gr/tech");
        websites.put("cnn", cnnCategories);

        // Define categories for tromaktiko
        HashMap<String, String> tromaktikoCategories = new HashMap<>();
        tromaktikoCategories.put("politics", "https://www.tromaktiko.gr/category/politiki/");
        tromaktikoCategories.put("economy", "https://www.tromaktiko.gr/category/ikonomia/");
        tromaktikoCategories.put("sports", "https://www.tromaktiko.gr/category/athlitika/");
        tromaktikoCategories.put("lifestyle", "https://www.tromaktiko.gr/category/lifestyle/");
        tromaktikoCategories.put("health", "https://www.tromaktiko.gr/category/health/");
        tromaktikoCategories.put("technology", "https://www.tromaktiko.gr/category/technologia/");
        websites.put("tromaktiko", tromaktikoCategories);

        // Define categories for enimerosi24
        HashMap<String, String> enimerosi24Categories = new HashMap<>();
        enimerosi24Categories.put("politics", "https://www.enimerosi24.gr/category/%cf%80%ce%bf%ce%bb%ce%b9%cf%84%ce%b9%ce%ba%ce%b7/");
        enimerosi24Categories.put("economy", "https://www.enimerosi24.gr/category/%ce%bf%ce%b9%ce%ba%ce%bf%ce%bd%ce%bf%ce%bc%ce%b9%ce%b1/");
        enimerosi24Categories.put("lifestyle", "https://www.enimerosi24.gr/category/media/");
        websites.put("enimerosi24", enimerosi24Categories);

        // Define categories for mononews
        HashMap<String, String> mononewsCategories = new HashMap<>();
        mononewsCategories.put("politics", "https://www.mononews.gr/category/politics");
        mononewsCategories.put("economy", "https://www.mononews.gr/category/oikonomia");
        mononewsCategories.put("sports", "https://www.mononews.gr/category/sports");
        mononewsCategories.put("lifestyle", "https://www.mononews.gr/category/life-style");
        mononewsCategories.put("health", "https://www.mononews.gr/category/health");
        mononewsCategories.put("technology", "https://www.mononews.gr/tag/technologia");
        websites.put("mononews", mononewsCategories);

        // Define categories for dealnews
        HashMap<String, String> dealnewsCategories = new HashMap<>();
        dealnewsCategories.put("politics", "https://www.dealnews.gr/category/politiki/");
        dealnewsCategories.put("economy", "https://www.dealnews.gr/category/oikonomia/");
        dealnewsCategories.put("sports", "https://www.dealnews.gr/category/athlitika/");
        dealnewsCategories.put("lifestyle", "https://www.dealnews.gr/category/ygeia-kai-diatrofi/");
        dealnewsCategories.put("technology", "https://www.dealnews.gr/category/texnologia/");
        websites.put("dealnews", dealnewsCategories);

        // Define categories for tovima
        HashMap<String, String> tovimaCategories = new HashMap<>();
        tovimaCategories.put("politics", "https://www.tovima.gr/category/politics/");
        tovimaCategories.put("economy", "https://www.tovima.gr/category/finance/");
        tovimaCategories.put("lifestyle", "https://www.tovima.gr/category/culture/");
        tovimaCategories.put("technology", "https://www.tovima.gr/category/science/");
        websites.put("tovima", tovimaCategories);

        HashMap<String, String> newsweekCategories = new HashMap<>();
        newsweekCategories.put("politics", "https://www.newsweek.com/world");
        newsweekCategories.put("health", "https://www.newsweek.com/health");
        newsweekCategories.put("lifestyle", "https://www.newsweek.com/life");
        newsweekCategories.put("technology", "https://www.newsweek.com/tech-science");
        websites.put("newsweek", newsweekCategories);
    }
}
