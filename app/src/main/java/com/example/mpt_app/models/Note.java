package com.example.mpt_app.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;
import java.util.List;

public class Note implements Parcelable {
    private String id;
    private String title;
    private String description;
    private Date date;
    private String googleDriveLink;
    private String clubName;
    private List<String> members;
    private String creatorId;

    // Pusty konstruktor wymagany przez Firebase
    public Note() {}

    // Konstruktor z siedmioma parametrami (bez id)
    public Note(String title, String description, Date date, List<String> members, String googleDriveLink, String clubName, String creatorId) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.members = members;
        this.googleDriveLink = googleDriveLink;
        this.clubName = clubName;
        this.creatorId = creatorId;
    }

    // Konstruktor używany podczas parcelowania
    protected Note(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        long tmpDate = in.readLong();
        date = tmpDate != -1 ? new Date(tmpDate) : null;
        googleDriveLink = in.readString();
        clubName = in.readString();
        members = in.createStringArrayList();
        creatorId = in.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) { return new Note(in); }

        @Override
        public Note[] newArray(int size) { return new Note[size]; }
    };

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeLong(date != null ? date.getTime() : -1);
        dest.writeString(googleDriveLink);
        dest.writeString(clubName);
        dest.writeStringList(members);
        dest.writeString(creatorId);
    }

    // Gettery i Settery

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getGoogleDriveLink() { return googleDriveLink; }
    public void setGoogleDriveLink(String googleDriveLink) { this.googleDriveLink = googleDriveLink; }

    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

    public List<String> getMembers() { return members; }
    public void setMembers(List<String> members) { this.members = members; }

    public String getCreatorId() { return creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
}
