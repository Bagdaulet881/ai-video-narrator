package kz.narrator.app.utils.validation

import android.util.Patterns

//import java.util.regex.Pattern

fun isEmailValid(email: String?): Boolean {

    if (email.isNullOrEmpty()) {
        return false
    }

    return Patterns.EMAIL_ADDRESS.matcher(email).matches()


//    val rfc2822 =
//        Pattern.compile("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$")
//
//    return rfc2822.matcher(email.toLowerCase()).matches()

}

fun isPhoneValid(phone: String?): Boolean {
    if (phone.isNullOrEmpty()) {
        return false
    }

    return phone.length == 11
}