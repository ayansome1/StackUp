package com.example.this_is_ayan.stackup;

import java.util.List;


public class Question
{
    private String profile_image,title,display_name,link,last_update,questionID;
    private List<String> tags;
    private boolean like;
    private int score,quota_max,quota_remaining;
    private long last_activity_date;
   /* private String searchString;
    private int pageNumber;
    private int id;*/

    public Question() {
        super();
    }

    public String getImage() {
        return profile_image;
    }
    public void setImage(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTags()
    {
        return tags;
    }
    public void setTags(List<String> tags)
    {
        this.tags=tags;
    }

    public String getUser() {
        return display_name;
    }
    public void setUser(String display_name) {
        this.display_name = display_name;
    }

    public boolean getLiked()
    {
        return  like;
    }
    public  void setLiked(boolean like)
    {
        this.like=like;
    }

    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }

    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }

    public long getLastActivityDate() {
        return last_activity_date;
    }
    public void setLastActivityDate(long  last_activity_date) {
        this.last_activity_date = last_activity_date;
    }
    public String getLastUpdate()
    {
        return last_update;
    }

    public void setLastUpdate(long q)
    {
        last_update=calculateTimeAgo(q);
        //this.last_update = last_update;
    }




    public static String calculateTimeAgo(long timeStamp) {

        long timeDiffernce;
        long unixTime = System.currentTimeMillis() / 1000L;  //get current time in seconds.
        int j;
        String[] periods = {"s", "m", "h", "d", "w", "m", "y", "d"};
        // you may choose to write full time intervals like seconds, minutes, days and so on
        double[] lengths = {60, 60, 24, 7, 4.35, 12, 10};
        timeDiffernce = unixTime - timeStamp;
        String tense = "ago";
        for (j = 0; timeDiffernce >= lengths[j] && j < lengths.length - 1; j++) {
            timeDiffernce /= lengths[j];
        }
        return timeDiffernce + periods[j] + " " + tense;
    }



    public String getQuestionID()
    {
        return questionID;
    }

    public void setQuestionID(String  questionID)
    {
    this.questionID=questionID;
    }









}
