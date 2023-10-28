import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.rtvplus.RtvApplication
import com.rtvplus.data.models.logIn.LogInModuleItem
import com.rtvplus.data.models.logIn.LogInResponseItem
import com.rtvplus.ui.fragments.subscription.SubscriptionFragment
import com.rtvplus.ui.viewmodels.LogInViewModel
import com.rtvplus.utils.AppUtils
import com.rtvplus.utils.ResultType
import com.rtvplus.utils.SharedPreferencesUtil
import javax.inject.Inject



class LogInUtil {
    interface ObserverListener {
        fun observerListener(result: String)
    }

    /*@Inject
    lateinit var loginInfo: LogInModuleItem*/

    lateinit var observerListener: ObserverListener


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
    ) {
        val logInViewModel = ViewModelProvider(viewModelOwner)[LogInViewModel::class.java]
        var result = "my result new"
        this.observerListener = listener

        logInViewModel.logInData.observe(lifecycleOwner) {
            when (it) {
                is ResultType.Loading -> {

                }

                is ResultType.Success -> {
                    val logInResult = it.data[0]
                    result = logInResult.result
                    Log.i("Newton", "ResultType success observeLoginData: $result")

                    this.observerListener.observerListener(result)
                    SharedPreferencesUtil.saveData(context, AppUtils.LogInKey, result)


                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_packcode,logInResult.packcode)
                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_packname,logInResult.packname)
                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_packtext,logInResult.packtext)

                    SubscriptionFragment().subscription()

                   // SharedPreferencesUtil.saveData(context,AppUtils.LogIn_audioad,logInResult.audioad)

                    //logInViewModel.logInData.postValue(null)


                   // SharedPreferencesUtil.saveData(context,AppUtils.LogInModule, result)


                  //  SharedPreferencesUtil.saveData(context,AppUtils.LogIn_autorenew,logInResult.autorenew)
                  //  SharedPreferencesUtil.saveData(context,AppUtils.LogIn_concurrent,logInResult.concurrent.toString())
//                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_concurrenttext,logInResult.concurrenttext)
//                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_consent,logInResult.consent)
//                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_consenttext,logInResult.consenttext)
//                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_consenturl,logInResult.consenturl)
//                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_currentversion,logInResult.currentversion)
//                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_currentversionios,logInResult.currentversionios)
//                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_email,logInResult.email)
//                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_enforce,logInResult.enforce)
//                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_enforcetext,logInResult.enforcetext)
//                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_extrainfo,logInResult.extrainfo)
//                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_fullname,logInResult.fullname)
//                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_liveurl,logInResult.liveurl)
//                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_msisdn,logInResult.msisdn)
//                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_packcode,logInResult.packcode)
//                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_packname,logInResult.packname)
//                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_packtext,logInResult.packtext)
//                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_play,logInResult.play)
//                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_referral,logInResult.referral)
//                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_referralimage,logInResult.referralimage)
//                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_result,logInResult.result)
//                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_showad,logInResult.showad)
//                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_token,logInResult.token)
//                    SharedPreferencesUtil.saveData(context,AppUtils.LogIn_ugc,logInResult.ugc)



//                    try {
//                        RtvApplication().storeLoginInfo(logInResult)
//                    }
//                    catch (e: Exception)
//                    {
//                        Log.e("tags", e.toString())
//                    }

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

   /* private fun storeLoginInfo(logInResult: LogInResponseItem) {
        Log.i("Panda", "storeLoginInfo: $logInResult")
        loginInfo = LogInModuleItem(
            audioad = logInResult.audioad,
            autorenew = logInResult.autorenew,
            concurrent = logInResult.concurrent,
            concurrenttext = logInResult.concurrenttext,
            consent = logInResult.consent,
            consenttext = logInResult.consenttext,
            consenturl = logInResult.consenturl,
            currentversion = logInResult.currentversion,
            currentversionios = logInResult.currentversionios,
            email = logInResult.email,
            enforce = logInResult.enforce,
            enforcetext = logInResult.enforcetext,
            extrainfo = logInResult.extrainfo,
            fullname = logInResult.fullname,
            liveurl = logInResult.liveurl,
            msisdn = logInResult.msisdn,
            packcode = logInResult.packcode,
            packname = logInResult.packname,
            packtext = logInResult.packtext,
            play = logInResult.play,
            referral = logInResult.referral,
            referralimage = logInResult.referralimage,
            result = logInResult.result,
            showad = logInResult.showad,
            token = logInResult.token,
            ugc = logInResult.ugc
        )
    }*/

}

