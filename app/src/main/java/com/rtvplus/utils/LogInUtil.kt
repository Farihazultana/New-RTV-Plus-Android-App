import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.rtvplus.data.models.logIn.LogInModule
import com.rtvplus.data.models.logIn.LogInResponseItem
import com.rtvplus.ui.viewmodels.LogInViewModel
import com.rtvplus.utils.AppUtils
import com.rtvplus.utils.ResultType
import com.rtvplus.utils.SharedPreferencesUtil
import javax.inject.Inject

class LogInUtil {
    @Inject
    lateinit var loginInfo: LogInModule
    interface ObserverListener{
        fun observerListener(result:String)
    }

    lateinit var observerListener : ObserverListener
    fun fetchLogInData(
        viewModelOwner: ViewModelStoreOwner,
        phoneText: String,
        enteredPassword: String,

    ) {
        val logInViewModel = ViewModelProvider(viewModelOwner).get(LogInViewModel::class.java)
        logInViewModel.fetchLogInData(phoneText, enteredPassword, "no", "1")
    }

    fun observeLoginData(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        viewModelOwner: ViewModelStoreOwner,
        listener: ObserverListener
    ){
        val logInViewModel = ViewModelProvider(viewModelOwner).get(LogInViewModel::class.java)
        var result = ""
        this.observerListener = listener
        logInViewModel.logInData.observe(lifecycleOwner) {
            when (it) {
                is ResultType.Success -> {
                    val logInResult = it.data[0]
                    result = logInResult.result
                    storeLoginInfo(logInResult)
                    SharedPreferencesUtil.saveData(context, AppUtils.LogInKey, result)
                    observerListener.observerListener(result)
                }

                is ResultType.Error -> {
                    Toast.makeText(
                        context,
                        "Username or Password incorrect. Try Again!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                }
            }

        }
    }

    private fun storeLoginInfo(logInResult: LogInResponseItem) {
        loginInfo[0].audioad = logInResult.audioad
        loginInfo[0].autorenew = logInResult.autorenew
        loginInfo[0].concurrent = logInResult.concurrent
        loginInfo[0].concurrenttext = logInResult.concurrenttext
        loginInfo[0].consent = logInResult.consent
        loginInfo[0].consenttext = logInResult.consenttext
        loginInfo[0].consenturl = logInResult.consenturl
        loginInfo[0].currentversion = logInResult.currentversion
        loginInfo[0].currentversionios = logInResult.currentversionios
        loginInfo[0].email = logInResult.email
        loginInfo[0].enforce = logInResult.enforce
        loginInfo[0].enforcetext = logInResult.enforcetext
        loginInfo[0].extrainfo = logInResult.extrainfo
        loginInfo[0].fullname = logInResult.fullname
        loginInfo[0].liveurl = logInResult.liveurl
        loginInfo[0].msisdn = logInResult.msisdn
        loginInfo[0].packname = logInResult.packname
        loginInfo[0].packtext = logInResult.packtext
        loginInfo[0].packcode = logInResult.packcode
        loginInfo[0].play = logInResult.play
        loginInfo[0].result = logInResult.result
        loginInfo[0].referral = logInResult.referral
        loginInfo[0].referralimage = logInResult.referralimage
        loginInfo[0].showad = logInResult.showad
        loginInfo[0].token = logInResult.token
        loginInfo[0].ugc = logInResult.ugc
    }
}
