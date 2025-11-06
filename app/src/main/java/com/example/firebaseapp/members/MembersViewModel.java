package com.example.firebaseapp.members;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.firebaseapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class MembersViewModel extends ViewModel {

    private final MutableLiveData<List<User>> membersLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public LiveData<List<User>> getMembersLiveData() {
        return membersLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setMembers(List<User> members) {
        membersLiveData.setValue(members);
        Log.d("MembersViewModel", "Members list set with " + members.size() + " members");
    }

    public void addMembers(List<User> newMembers) {
        List<User> currentMembers = membersLiveData.getValue();
        if (currentMembers == null) {
            currentMembers = new ArrayList<>();
        }
        currentMembers.addAll(newMembers);
        membersLiveData.setValue(currentMembers);
        Log.d("MembersViewModel", "Added " + newMembers.size() + " new members");
    }

    public void setIsLoading(boolean loading) {
        isLoading.setValue(loading);
        Log.d("MembersViewModel", "Set isLoading to " + loading);
    }

    /**
     * Ustawia dane testowe dla celów debugowania.
     */
    public void setMockData() {
        List<User> mockUsers = new ArrayList<>();
        mockUsers.add(new User("Jan", "Kowalski", "Informatyka", 25, "123456789", "jan.kowalski@example.com", "member"));
        mockUsers.add(new User("Anna", "Nowak", "Matematyka", 22, "987654321", "anna.nowak@example.com", "admin"));
        membersLiveData.setValue(mockUsers);
        Log.d("MembersViewModel", "Mock data set with " + mockUsers.size() + " members");
    }
}
