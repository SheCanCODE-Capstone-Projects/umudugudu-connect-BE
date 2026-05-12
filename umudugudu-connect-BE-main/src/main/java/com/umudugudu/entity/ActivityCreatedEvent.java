package com.umudugudu.entity;

public class ActivityCreatedEvent {
    private Activity activity;

    public ActivityCreatedEvent(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
