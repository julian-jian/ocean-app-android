package com.sky.lamp.http;

import com.sky.lamp.response.*;

import okhttp3.RequestBody;
import retrofit2.http.*;
import rx.Observable;

public interface MyApi {

    /**
     * 获取产品列表
     * @return
     */
    @GET("/index/getGoodsCate")
    Observable<PowerListResponse> powerList(@Query("page") int page,@Query("key") String key);

    @GET("/index/getGoods")
    Observable<ProductListResponse> getGoods(@Query("page") int page,@Query("cate_id")String cate_id);
    @GET("/index/getReGoods")
    Observable<ProductListResponse> getReGoods();

    @GET("/index/getImgs")
    Observable<BannerResponse> getImgs(@Query("type")int type);

    @GET("/index/goodsDetail")
    Observable<ProductDetailsResponse> goodsDetail(@Query("id") String id);

    @GET("/index/articleDetail")
    Observable<StudyDetailsResponse> articleDetail(@Query("a_id") String id);

    @GET("/index/getArticle")
    Observable<StudyListResponse> getArticle(@Query("page") int page);

    @FormUrlEncoded
    @POST("/login/loginUser")
    Observable<LoginResponse> login(@Field("user_name") String phone, @Field("password") String pwd);

    @FormUrlEncoded
    @POST("/login/updatePassword")
    Observable<UpdatePwdResponse> updatePassword(@Field("user_name") String phone, @Field("oldPassword") String oldpwd,@Field("newPassword") String newPassword);

    @GET("/index//searchGoods")
    Observable<ProductListResponse> searchGoods(@Query("page") int page, @Query("key") String key);

    @POST("/api/login/userRegister")
    Observable<RegResponse> reg(@Body RequestBody route);

    @POST("/api/login/userLogin")
    Observable<RegResponse> login(@Body RequestBody body);

}
