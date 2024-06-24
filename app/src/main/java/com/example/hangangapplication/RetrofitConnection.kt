package com.example.hangangapplication


import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import retrofit2.Retrofit

//http://apis.data.go.kr/1192000/service/OceansBeachInfoService1/getOceansBeachInfo1?pageNo=1&numOfRows=10&resultType=xml&SIDO_NM=부산&ServiceKey=서비스키
//&ServiceKey=I8VFeYpWskK5fz9BymrORWuv8bBSzMgWFFcyXccJXHjECKEzJ1FzDbvT8qvG0d97sclSsCfANM8C1pDR4KZPgg%3D%3D&SG_APIM=2ug8Dm9qNBfD32JLZGPN64f3EoTlkpD8kSOHWfXpyrY
//http://apis.data.go.kr/1192000/service/OceansBeachSeawaterService1/getOceansBeachSeawaterInfo1?pageNo=1&numOfRows=10&resultType=xml&SIDO_NM=충남&RES_YEAR=2016&ServiceKey
class RetrofitConnection{

    //객체를 하나만 생성하는 싱글턴 패턴을 적용합니다.
    companion object {
        //API 서버의 주소가 BASE_URL이 됩니다.
        private const val BASE_URL = "http://apis.data.go.kr/1192000/service/OceansBeachSeawaterService1/"

        var xmlNetworkService : NetworkService
        val parser = TikXml.Builder().exceptionOnUnreadXml(false).build()
        val xmlRetrofit : Retrofit
            get() = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(TikXmlConverterFactory.create(parser))
                .build()

        init{
            xmlNetworkService = xmlRetrofit.create(NetworkService::class.java)
        }
    }
}