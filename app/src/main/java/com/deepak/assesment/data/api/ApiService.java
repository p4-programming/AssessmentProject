package com.deepak.assesment.data.api;

import com.deepak.assesment.dao.DataTable;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

     @GET("posts/{postId}/comments")
     Call<List<DataTable>> getUsers(@Path("postId") int page); // Used @Path for pagination
}
