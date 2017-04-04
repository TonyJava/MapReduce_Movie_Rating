
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


public class Predict {
    
    Map<Integer, ArrayList<Movie>> userData;
    Map<Integer, ArrayList<User>> movieData;
    Map<Integer, HashMap<Integer,Double>> weights;
    Map<Integer, Double> userVoteMean;
    
    private class Movie {
        
        int movieID;
        double rating;
        
        public Movie(int movieID, double rating) {
            this.movieID = movieID;
            this.rating = rating;
        }
        
        @Override
        public boolean equals(Object obj) {
            if(obj==null)
                return false;
            else if(obj.getClass()!=this.getClass())
                return false;
            else
                return movieID == ((Movie)obj).movieID; //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    
    private class User {
        
        int userID;
        double rating;
        
        public User(int userID, double rating) {
            this.userID = userID;
            this.rating = rating;
        }
    }
    
    
    public Predict()
    {
        userData = new HashMap<>();
        movieData = new HashMap<>();
        weights = new HashMap<>();
        userVoteMean = new HashMap<>();
    }
    
    /**
     * Reads from the file and populates userData and movieData maps
     */
    public void readFromFile(File file) throws FileNotFoundException
    {
        Scanner scanner = new Scanner(file);
        List<String> words;
        words = new ArrayList<>();
        String temp;
        String[] data;
        
        int userID, userIDPrev;
        int movieID;
        double rating;
        
        //Get the first line from the file
        temp = scanner.nextLine();
        data = temp.split("[,]");
        userIDPrev = Integer.parseInt(data[0]);
        movieID = Integer.parseInt(data[1]);
        rating = Double.parseDouble(data[2]);
        ArrayList<Movie> movies;
        ArrayList<User> users;
        userData.put(userIDPrev, movies = new ArrayList<>());
        movies.add(new Movie(movieID,rating));
        movieData.put(userIDPrev, users = new ArrayList<>());
        users.add(new User(userIDPrev,rating));
        
        
        while (scanner.hasNextLine())
        {
            temp = scanner.nextLine();
            data = temp.split("[,]");
            userID = Integer.parseInt(data[0]);
            movieID = Integer.parseInt(data[1]);
            rating = Double.parseDouble(data[2]);
            
            //Populate the userData
            if(userID!=userIDPrev)
            {
                userData.put(userID, movies = new ArrayList<>());
                movies.add(new Movie(movieID, rating));
            }
            else
            {
                movies.add(new Movie(movieID,rating));
            }
            
            //Populate the movieData
            users = movieData.get(movieID);
            if(users == null)
            {
                movieData.put(movieID, users = new ArrayList<>());
            }
            users.add(new User(userID,rating));
            
            userIDPrev = userID;
        }
    }
    
    
    
    public void populateVoteMeanForAllUser()
    {
        List<Movie> movies = new ArrayList<>();
        Set<Integer> usersAll = userData.keySet();
        double voteCount,sum, votesMean;
        for(int userID: usersAll)
        {
            movies = userData.get(userID);
            voteCount = movies.size();
            sum = 0;
            for(Movie movie:movies)
            {
                sum = sum + movie.rating;
            }
            votesMean = sum/voteCount;
            userVoteMean.put(userID, votesMean);
        }
    }
    
    public void calculateWeights(File file) throws FileNotFoundException {
        String temp;
        List<Integer> userIDs = new ArrayList<>(userData.keySet());
        Collections.sort(userIDs);
        int user1, user2;
        double weight;
        Scanner scanner = new Scanner(file);
        for(int i=0;i<userIDs.size()-1;i++) {
            weights.put(userIDs.get(i), new HashMap<>());
        }
        while (scanner.hasNextLine()) {
            temp = scanner.nextLine();
            String[] data = temp.split("[(,) ]");
            user1 = Integer.parseInt(data[1]);
            user2 = Integer.parseInt(data[2]);
            weight = Double.parseDouble(data[3]);
            
            if (user1 > user2) {
                user1 = user1 + user2;
                user2 = user1 - user2;
                user1 = user1 - user2;
            }
            if(weights.get(user1)!=null) {
                weights.get(user1).put(user2,weight);
            }
        }
    }
    public double PredictRating(int userID, int movieID)
    {
        double sum = 0.0, sumWeight = 0.0, weight, temp;
        double vaMean = userVoteMean.get(userID);
        List<User> users = movieData.get(movieID);
        
        for(User user: users)
        {
            if(userID<user.userID)
            {
                if(weights.get(userID)!=null&&weights.get(userID).get(user.userID)!=null) {
                    weight = weights.get(userID).get(user.userID);
                temp = user.rating - userVoteMean.get(user.userID);
                sum += temp*weight;
                    sumWeight += Math.abs(weight);}
            }
            else if(userID > user.userID)
            {
                if(weights.get(userID)!=null&&weights.get(userID).get(user.userID)!=null) {
                weight = weights.get(user.userID).get(userID);
                temp = user.rating - userVoteMean.get(user.userID);
                sum += (temp*weight);
                sumWeight += Math.abs(weight);
                }}
        }
        if (sumWeight == 0) {return vaMean;}
        double prediction = vaMean + sum/sumWeight;
        return prediction;
    }
    
    
    public static void main(String[] args) throws FileNotFoundException
    {
        File trainFile = new File(args[0]);
        File similarity = new File("similarity.txt");
        Predict cf = new Predict();
        cf.readFromFile(trainFile);
        cf.populateVoteMeanForAllUser();
        cf.calculateWeights(similarity);
        
        
        System.out.println("Error on the Test Data:");
        
        String[] data;
        File testFile = new File(args[1]);
        Scanner scanner = new Scanner(testFile);
        String temp;
        int userID,movieID, count = 0;
        double rating, predictedRating, meanError = 0, meanSquareError = 0;
        
        while(scanner.hasNextLine())
        {
            count++;
            temp = scanner.nextLine();
            data = temp.split("[,]");
            userID = Integer.parseInt(data[0]);
            movieID = Integer.parseInt(data[1]);
            rating = Double.parseDouble(data[2]);
            predictedRating = cf.PredictRating(userID, movieID);

            meanError += Math.abs(predictedRating - rating);
            meanSquareError += Math.pow(predictedRating - rating, 2);
        }
        System.out.println("MAE :" + meanError/count);
        System.out.println("RMSE :" + Math.sqrt(meanSquareError/count));
    }
}