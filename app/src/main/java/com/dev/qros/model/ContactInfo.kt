package com.dev.qros.model

import android.content.ContentValues
import android.content.Intent
import android.provider.ContactsContract

data class ContactInfo(
    val fullName: String? = null,
    val phone: ContactNumbers? = null,
    val email: List<String?> = emptyList(),
    val company: String? = null,
    val jobTitle: String? = null,
    val url: List<String?>? = emptyList(),
) {
    companion object {
        fun toContactAppsIntent(contact: ContactInfo): Intent {
            return Intent(Intent.ACTION_INSERT).apply {
                putExtra(ContactsContract.Intents.Insert.NAME, contact.fullName)
                putExtra(ContactsContract.Intents.Insert.COMPANY, contact.company)
                putExtra(ContactsContract.Intents.Insert.JOB_TITLE, contact.jobTitle)

                // create array of ContentValues() to populate CommonDataKinds types
                val dataRows = ArrayList<ContentValues>()

                contact.phone?.phoneNums?.forEach { phoneNumber ->
                    if (!phoneNumber.isNullOrBlank()) {
                        dataRows.add(ContentValues().apply {
                            put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber )
                            put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        })
                    }
                }

                contact.phone?.faxNums?.forEach { faxNumber ->
                    if (!faxNumber.isNullOrBlank()) {
                        dataRows.add(ContentValues().apply {
                            put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            put(ContactsContract.CommonDataKinds.Phone.NUMBER, faxNumber)
                            put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK)
                        })
                    }
                }

                contact.email.forEach { emailAddress ->
                    if (!emailAddress.isNullOrBlank()) {
                        dataRows.add(ContentValues().apply {
                            put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                            put(ContactsContract.CommonDataKinds.Email.ADDRESS, emailAddress)
                        })
                    }
                }

                contact.url?.forEach { websiteUrl ->
                    if (!websiteUrl.isNullOrBlank()) {
                        dataRows.add(ContentValues().apply {
                            put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
                            put(ContactsContract.CommonDataKinds.Website.URL, websiteUrl)
                            put(ContactsContract.CommonDataKinds.Website.TYPE, ContactsContract.CommonDataKinds.Website.TYPE_PROFILE)
                        })
                    }
                }

                // Pass the bulk data array into the intent
                putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, dataRows)
            }
        }
    }
}

data class ContactNumbers(
    val faxNums: List<String?> = emptyList(),
    val phoneNums: List<String?> = emptyList()
)