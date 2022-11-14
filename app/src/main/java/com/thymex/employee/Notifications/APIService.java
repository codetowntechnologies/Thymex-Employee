package com.thymex.employee.Notifications;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAl_W3bsA:APA91bGFdIjek8dPMjCAujPdF3knvx8FYWS1oL_iVOFVkpm6k2E8jut4u8-ZiKRouPHLhnHpYARYMVfOUCxzh7aSHTEZCoH2tb5__htMTMk0OL_Md-UqX1eSjbjh_V4Ap_MTuft0DDBJ"

    })
    @POST("fcm/send")
    Call<MyResponse>sendNotification(@Body Sender body);






}