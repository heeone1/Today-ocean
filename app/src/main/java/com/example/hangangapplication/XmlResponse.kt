package com.example.hangangapplication

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name="response")
data class XmlResponse(
    @Element
    val body : myXmlBody
)

@Xml(name="body")
data class myXmlBody(
    @Element
    val items : myXmlItems
)

@Xml(name="items")
data class myXmlItems(
    @Element
    val item : MutableList<myXmlItem>
)

@Xml(name="item")
data class myXmlItem(
    @PropertyElement
    val sidoNm:String?, //시도명
    @PropertyElement
    val gugunNm:String?, //구군명
    @PropertyElement
    val staNm:String?, //정점명
    @PropertyElement
    val resYn:String?, //적합여부
    @PropertyElement
    val resLocDetail:String?, //조사지점
    @PropertyElement
    val lat:String?, //위도
    @PropertyElement
    val lon:String?, //경도
) {
    constructor() : this(null, null, null, null, null, null, null)
}
