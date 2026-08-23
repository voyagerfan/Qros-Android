package com.dev.qros.model

data class ContactInfo(
    val fullName: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val company: String? = null,
    val jobTitle: String? = null,
    val url: String? = null,
)

// TODO: create function to create ContactInfo intent to fire activity using info.
// TODO: this object gets stapled to cameraUIState in viewmodel, then used in UI to fire intent if user wants to add contact.