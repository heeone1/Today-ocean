package com.example.hangangapplication

import com.example.hangangapplication.XmlResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//https://www.meis.go.kr/service/OceansBeachSeawaterService/getOceansBeachSeawaterInfo?pageNo=1&numOfRows=10&resultType=xml&SIDO_NM=%EB%B6%80%EC%82%B0&RES_YEAR=2023
//&ServiceKey=I8VFeYpWskK5fz9BymrORWuv8bBSzMgWFFcyXccJXHjECKEzJ1FzDbvT8qvG0d97sclSsCfANM8C1pDR4KZPgg%3D%3D&SG_APIM=2ug8Dm9qNBfD32JLZGPN64f3EoTlkpD8kSOHWfXpyrY
//http://apis.data.go.kr/1192000/service/OceansBeachSeawaterService1/getOceansBeachSeawaterInfo1?pageNo=1&numOfRows=10&resultType=xml&SIDO_NM=충남&RES_YEAR=2016&ServiceKey

//http://apis.data.go.kr/1192000/service/OceansBeachInfoService1/getOceansBeachInfo1?pageNo=1&numOfRows=10&resultType=xml&SIDO_NM=제주&ServiceKey=서비스키
interface NetworkService {
    @GET("getOceansBeachSeawaterInfo1")
    fun getXmlList(
        @Query("pageNo") pageNo:Int,
        @Query("numOfRows") numOfRows:Int,
        @Query("resultType") resultType:String,
        @Query("SIDO_NM") name:String,
        @Query("RES_YEAR") year:Int,
        @Query("ServiceKey") apikey:String
    ) : Call<XmlResponse>
}