package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {
    //get all incidents
//@GET("/incident")
//    Call<List<Incident>> getIncidents();

    //get incident with id
    @GET("/incident/{id}")
    Call<Incident> getIncident(@Path("id") String number);

    //get all incidents
//@GET("/incident/")
//Call<List<Incident>> getIncidentsUsingQuery(@Query("number") String number, @Query("state") String state);

    //post to an incident
    @POST("/incident")
    Call<Incident> postIncident(@Body Incident incident);
}
