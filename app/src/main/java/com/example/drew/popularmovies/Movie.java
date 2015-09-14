package com.example.drew.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable{
    private String image;
    private String title;
    private String desc;
    private String year;
    private String rating;
    private String id;


    private Integer dbid;

    public Movie() {super();}



    private int mData;

    public int describeContents() {
        return 0;
    }



    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };



    public Movie(String title, String id, String year, String rating, String desc, String image) {
        super();
        this.title = title;
        this.id = id;
        this.rating=rating;
        this.desc=desc;
        this.image=image;
        this.year=year;
    }

    private Movie(Parcel in) {
        title=in.readString();
        id=in.readString();
        rating=in.readString();
        desc=in.readString();
        image=in.readString();
        year=in.readString();

    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(id);
        out.writeString(rating);
        out.writeString(desc);
        out.writeString(image);
        out.writeString(year);




    }


    @Override
    public String toString() {
        desc=desc.replace("\"","'");
        title=title.replace("\"","'");
        return "{\"poster_path\":\"" + image + "\",\"title\":\"" + title + "\",\"overview\":\"" + (desc) + "\",\"release_date\":\"" + year + "\",\"vote_average\":\"" + rating + "\",\"id\":"+id+"}";


    }


    public void setDbid(Integer dbid) {
        this.dbid = dbid;
    }

    public Integer getDbid(){return dbid;}

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