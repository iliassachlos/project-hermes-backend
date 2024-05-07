package org.example.machinelearning.Util;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@Slf4j
public class MachineLearningUtil {
    public static Double sentimentAnalysis(String content){
        //Replace double quotes with single quotes
        content = content.replaceAll("\"", "'");

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        Annotation annotation = new Annotation(content);
        pipeline.annotate(annotation);

        // Calculate overall sentiment score
        Integer numSentences = 0;
        Integer sentimentScore = 0;

        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            sentimentScore += getSentimentScore(sentiment);
            numSentences++;
        }

        // Calculate average sentiment score
        double averageSentimentScore = (double) sentimentScore / numSentences;
        log.info("Finished sentiment analysis");
        return averageSentimentScore;
    }

    private static Integer getSentimentScore(String sentiment) {
        switch (sentiment) {
            case "Very negative":
                return -2;
            case "Negative":
                return -1;
            case "Neutral":
                return 0;
            case "Positive":
                return 1;
            case "Very positive":
                return 2;
            default:
                return 0;
        }
    }
}
