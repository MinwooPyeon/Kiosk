package com.harang.data.model.dto.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SendDementiaTestResultRequest(
    @SerializedName("surveyId")
    val surveyId: Long,
    @SerializedName("dementia_question1")
    val question1: String,
    @SerializedName("dementia_question2")
    val question2: String,
    @SerializedName("dementia_question3")
    val question3: String,
    @SerializedName("dementia_question4")
    val question4: String,
    @SerializedName("dementia_question5")
    val question5: String,
    @SerializedName("dementia_question6")
    val question6: String,
    @SerializedName("dementia_question7")
    val question7: String,
    @SerializedName("dementia_question8")
    val question8: String,
    @SerializedName("dementia_question9")
    val question9: String,
    @SerializedName("dementia_question10")
    val question10: String,
    @SerializedName("dementia_question11")
    val question11: String,
    @SerializedName("dementia_question12")
    val question12: String,
    @SerializedName("dementia_question13")
    val question13: String,
    @SerializedName("dementia_question14")
    val question14: String,
    @SerializedName("dementia1")
    val dementia1: Int = 0,
    @SerializedName("dementia2")
    val dementia2: Int = 0,
    @SerializedName("dementia3")
    val dementia3: Int = 0,
    @SerializedName("dementia4")
    val dementia4: Int = 0,
    @SerializedName("dementia5")
    val dementia5: Int = 0,
    @SerializedName("dementia6")
    val dementia6: String = "",
    @SerializedName("dementia7")
    val dementia7: String = "",
    @SerializedName("dementia8")
    val dementia8: String = "",
    @SerializedName("dementia9")
    val dementia9: String = "",
    @SerializedName("dementia10")
    val dementia10: String = "",
) : Serializable