package com.example.drew.popularmovies;

public class Movie {
    private String image;
    private String title;
    private String desc;
    private String year;
    private String rating;
    private String id;

    public Movie() {super();}

    public String getImage(){return image;}

    public void setImage(String image) {this.image=image;}

    public String getTitle() {return title;}

    public void setTitle(String title) {this.title=title;}

    public String getDesc(){return desc;}

    public void setDesc(String desc){this.desc=desc;}

    public String getYear(){return year;}

    public void setYear(String year){this.year=year;}

    public String getRating(){return rating;}

    public void setRating (String rating){this.rating=rating;}

    public String getMovieID(){return id;}

    public void setMovieID(String id){this.id=id;}



}