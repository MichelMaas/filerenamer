package nl.maas.filerenamer.frontend.services

import jakarta.servlet.http.Cookie
import org.apache.wicket.Session
import org.apache.wicket.request.cycle.RequestCycle
import org.apache.wicket.request.http.WebRequest
import org.apache.wicket.request.http.WebResponse
import org.apache.wicket.util.cookies.CookieUtils

class CookieUtil {

    companion object {

        private val languageCookie = "bankBookLanguageCookie"

        fun languageCookieExists(): Boolean {
            return CookieUtils().load(languageCookie) != null
        }


        fun saveLanguageCookie(language: String) {
            val webResponse = RequestCycle.get().response as WebResponse
            val cookie = Cookie(languageCookie, language)
            clearLanguageCooky(webResponse)
        }

        private fun clearLanguageCooky(webResponse: WebResponse) {
            if (languageCookieExists()) {
                val webRequest: WebRequest = RequestCycle.get().request as WebRequest
//                val oldCookie: Cookie = webRequest.getCookie(languageCookie)
//                webResponse.clearCookie(oldCookie)
            }
            Session.get().invalidateNow()
        }

        fun readLanguageCookie(): String? {
//            val webRequest: WebRequest = RequestCycle.get().request as WebRequest
//            val cookie: Cookie = webRequest.getCookie(languageCookie)
            return CookieUtils().load(languageCookie)
        }

    }
}