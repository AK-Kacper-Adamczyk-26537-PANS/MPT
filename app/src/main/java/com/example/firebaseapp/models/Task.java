package com.example.firebaseapp.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;
import java.util.List;

public class Task implements Parcelable {
    private String id; // ID zadania
    private String title;
    private String description;
    private Date dueDate;
    private Date startDate;      // Nowe pole: Data rozpoczęcia
    private Date endDate;        // Nowe pole: Data zakończenia
    private String priority;     // Nowe pole: Priorytet
    private int progress;        // Nowe pole: Postęp (1-5)
    private String createdBy;
    private String clubName;
    private List<String> assignedTo;

    // Pusty konstruktor wymagany przez Firebase
    public Task() {}

    // Konstruktor z parametrami
    public Task(String title, String description, Date dueDate, Date startDate, Date endDate,
                String priority, int progress, String createdBy, String clubName, List<String> assignedTo) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.priority = priority;
        this.progress = progress;
        this.createdBy = createdBy;
        this.clubName = clubName;
        this.assignedTo = assignedTo;
    }

    // Konstruktor używany podczas parcelowania
    protected Task(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        long tmpDueDate = in.readLong();
        dueDate = tmpDueDate != -1 ? new Date(tmpDueDate) : null;
        long tmpStartDate = in.readLong();
        startDate = tmpStartDate != -1 ? new Date(tmpStartDate) : null;
        long tmpEndDate = in.readLong();
        endDate = tmpEndDate != -1 ? new Date(tmpEndDate) : null;
        priority = in.readString();
        progress = in.readInt();
        createdBy = in.readString();
        clubName = in.readString();
        assignedTo = in.createStringArrayList();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) { return new Task(in); }

        @Override
        public Task[] newArray(int size) { return new Task[size]; }
    };

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeLong(dueDate != null ? dueDate.getTime() : -1);
        dest.writeLong(startDate != null ? startDate.getTime() : -1);
        dest.writeLong(endDate != null ? endDate.getTime() : -1);
        dest.writeString(priority);
        dest.writeInt(progress);
        dest.writeString(createdBy);
        dest.writeString(clubName);
        dest.writeStringList(assignedTo);
    }

    // Gettery i Settery

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

    public List<String> getAssignedTo() { return assignedTo; }
    public void setAssignedTo(List<String> assignedTo) { this.assignedTo = assignedTo; }
}
