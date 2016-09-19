package com.pineone.icbms.so.profile.pr;

/**
 * Created by melvin on 2016. 8. 23..
 * NOTE: 외부에 표현될 Profile 데이터
 */
public class ProfileTransFormData {

    private String id;

    public ProfileTransFormData(String id) {
        this.id = id;
    }

    public ProfileTransFormData() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ProfileTransFormData{" +
                "id='" + id + '\'' +
                '}';
    }
}