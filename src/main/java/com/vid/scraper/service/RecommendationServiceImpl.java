package com.vid.scraper.service;

import com.vid.scraper.model.entity.Like;
import com.vid.scraper.model.entity.RecommendationView;
import com.vid.scraper.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CityBlockSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    @Override
    public List<RecommendedItem> findRecommendations(User user, List<Like> likes, List<RecommendationView> views, Integer numberOfRecommendations) {
        List<String> stringVotes = new ArrayList<>();
        for (Like like : likes) {
            stringVotes.add(like.toString());
        }
        for (RecommendationView view : views) {
            stringVotes.add(view.toString());
        }
        String text = String.join("\n", stringVotes);
        File dataFile = new File("src/main/resources/data.csv");
        try {
            FileWriter fileWriter = new FileWriter(dataFile);
            fileWriter.write(text);
            fileWriter.close();
            DataModel dataModel = new FileDataModel(dataFile);
            CityBlockSimilarity similarity = new CityBlockSimilarity(dataModel);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, dataModel);
            UserBasedRecommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
            List<RecommendedItem> recommendations = recommender.recommend(user.getId(), numberOfRecommendations);
            if (!recommendations.isEmpty()) {
                return recommendations;
            }
        } catch (IOException | TasteException e) {
            e.printStackTrace();
        }
        return null;
    }
}
