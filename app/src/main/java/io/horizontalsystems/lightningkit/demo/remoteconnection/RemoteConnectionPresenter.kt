package io.horizontalsystems.lightningkit.demo.remoteconnection

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.horizontalsystems.lightningkit.demo.core.SingleLiveEvent

class RemoteConnectionPresenter(private val interactor: RemoteConnectionModule.IInteractor) : ViewModel(),
    RemoteConnectionModule.IInteractorDelegate {

    val host = MutableLiveData<String>()
    val port = MutableLiveData<String>()
    val certificate = MutableLiveData<String>()
    val macaroon = MutableLiveData<String>()

    val error = SingleLiveEvent<String>()
    val navigateToHome = SingleLiveEvent<Unit>()

    init {
        sampleData()
    }

    private fun sampleData() {
        host.value = "178.62.239.195"
        host.value = "192.168.4.31"
        port.value = "10009"

        certificate.value = """
MIICFTCCAbugAwIBAgIRAJT4YZZH/HHXezyRd4+4dX8wCgYIKoZIzj0EAwIwOjEf
MB0GA1UEChMWbG5kIGF1dG9nZW5lcmF0ZWQgY2VydDEXMBUGA1UEAxMObGlnaHRu
aW5nLW5vZGUwHhcNMjAwMTE2MTEzNjU5WhcNMjEwMzEyMTEzNjU5WjA6MR8wHQYD
VQQKExZsbmQgYXV0b2dlbmVyYXRlZCBjZXJ0MRcwFQYDVQQDEw5saWdodG5pbmct
bm9kZTBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABA+v2Rm60/Zqwnpla6cLwAy2
n4ztJols2d3ALcV8+47haA8vVcIu4bJ3PKDQ6Udd5J/4TVqUsxJsp+iND+ttMLmj
gaEwgZ4wDgYDVR0PAQH/BAQDAgKkMA8GA1UdEwEB/wQFMAMBAf8wewYDVR0RBHQw
coIObGlnaHRuaW5nLW5vZGWCCWxvY2FsaG9zdIIEdW5peIIKdW5peHBhY2tldIIH
YnVmY29ubocEfwAAAYcQAAAAAAAAAAAAAAAAAAAAAYcEsj7vw4cEChIACIcErBEA
AYcQ/oAAAAAAAAC88H7//hurYTAKBggqhkjOPQQDAgNIADBFAiAqyFPlCozGra+c
M106mloId9YnouH2r8gEkino3XRgkwIhAN0pM2KGK0zDkP2wmWlcV3XZzz+igxn3
AAV6WuiK2iau
        """.trimIndent()

        certificate.value = """
MIICSDCCAe6gAwIBAgIRAOfr+wt7n48UxpriCG1etfIwCgYIKoZIzj0EAwIwPDEf
MB0GA1UEChMWbG5kIGF1dG9nZW5lcmF0ZWQgY2VydDEZMBcGA1UEAxMQQmFreXQt
aU1hYy5sb2NhbDAeFw0yMDAxMjAwNDU1MjBaFw0yMTAzMTYwNDU1MjBaMDwxHzAd
BgNVBAoTFmxuZCBhdXRvZ2VuZXJhdGVkIGNlcnQxGTAXBgNVBAMTEEJha3l0LWlN
YWMubG9jYWwwWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAATwInDtZWelhrk8X9Tm
LUjdMMij6vKrRUMdYVJYNXzHFnlp5ZnFqZjLtw+W2eJixr9JyYcTRtGryskUnBU+
6ysTo4HQMIHNMA4GA1UdDwEB/wQEAwICpDAPBgNVHRMBAf8EBTADAQH/MIGpBgNV
HREEgaEwgZ6CEEJha3l0LWlNYWMubG9jYWyCCWxvY2FsaG9zdIIEdW5peIIKdW5p
eHBhY2tldIIHYnVmY29ubocEfwAAAYcQAAAAAAAAAAAAAAAAAAAAAYcQ/oAAAAAA
AAAAAAAAAAAAAYcQ/oAAAAAAAAAIGuh8nIXGp4cEwKgEH4cQ/oAAAAAAAADUEyr/
/sByiIcQ/oAAAAAAAABu5Z4wQAY+FDAKBggqhkjOPQQDAgNIADBFAiEAjRU9oHpB
DT8BMSrLZJxzA5ipr61O2Jvw9uCpL+cuFPYCIF53btWNPCu4oX8uteeDgLa6Y6Jm
MDxSuFfkTxl/EOUd""".trimIndent()

        macaroon.value =
            "0201036C6E6402EB01030A10B9522644E5DE0461B6CBD21FE2B5246F1201301A160A0761646472657373120472656164120577726974651A130A04696E666F120472656164120577726974651A170A08696E766F69636573120472656164120577726974651A140A086D616361726F6F6E120867656E65726174651A160A076D657373616765120472656164120577726974651A170A086F6666636861696E120472656164120577726974651A160A076F6E636861696E120472656164120577726974651A140A057065657273120472656164120577726974651A180A067369676E6572120867656E657261746512047265616400000620D374F1780292FC4A2A2D8BA44B739C4DA185C4EDA8235B00AAD37A49CC2E66DC"

        macaroon.value =
            "0201036C6E6402EB01030A10F724E63CECE4436807B6C5D7860BB4C11201301A160A0761646472657373120472656164120577726974651A130A04696E666F120472656164120577726974651A170A08696E766F69636573120472656164120577726974651A140A086D616361726F6F6E120867656E65726174651A160A076D657373616765120472656164120577726974651A170A086F6666636861696E120472656164120577726974651A160A076F6E636861696E120472656164120577726974651A140A057065657273120472656164120577726974651A180A067369676E6572120867656E65726174651204726561640000062085F7C1B09F78BEC43E3F1E93E2556ED48E9DF8F7B25E49598B86E89B52675B23"

    }

    fun connect(view: View) {
        if (arrayOf(host, port, certificate, macaroon).any { it.value.isNullOrBlank() }) {
            error.value = "All fields are required"
            return
        }

        val connectionParams =
            ConnectionParams(host.value!!, port.value!!.toInt(), certificate.value!!, macaroon.value!!)

        interactor.validateConnection(connectionParams)
    }

    // IInteractorDelegate

    override fun onValidationSuccess(connectionParams: ConnectionParams) {
        interactor.saveConnectionParams(connectionParams)
        navigateToHome.postValue(Unit)
    }

    override fun onValidationFailed(e: Throwable) {
        error.postValue(e.message)
    }

    // ViewModel

    override fun onCleared() {
        interactor.clear()
    }
}

data class ConnectionParams(val host: String, val port: Int, val certificate: String, val macaroon: String)