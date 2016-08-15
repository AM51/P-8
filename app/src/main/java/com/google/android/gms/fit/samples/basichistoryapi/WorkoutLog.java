package com.google.android.gms.fit.samples.basichistoryapi;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by archit.m on 03/08/16.
 */
public class WorkoutLog implements Parcelable,Comparable {

    private String exercise;

    private int repetitions;

    private float weight;

    public WorkoutLog() {
    }

    public WorkoutLog(String exercise, int repetitions, float weight) {
        this.exercise = exercise;
        this.repetitions = repetitions;
        this.weight = weight;
    }




    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    protected WorkoutLog(Parcel in) {
        exercise = in.readString();
        repetitions = in.readInt();
        weight = in.readFloat();
    }

    public static final Creator<WorkoutLog> CREATOR = new Creator<WorkoutLog>() {
        @Override
        public WorkoutLog createFromParcel(Parcel in) {
            return new WorkoutLog(in);
        }

        @Override
        public WorkoutLog[] newArray(int size) {
            return new WorkoutLog[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(exercise);
        dest.writeInt(repetitions);
        dest.writeFloat(weight);

    }

    @Override
    public int compareTo(Object workoutLog) {
        return this.exercise.compareTo(((WorkoutLog)workoutLog).exercise);
    }
}
