package io.horizontalsystems.lightningkit.demo.remoteconnection

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.horizontalsystems.lightningkit.demo.core.SingleLiveEvent
import io.horizontalsystems.lightningkit.remote.RemoteLndCredentials

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
        host.value = "192.168.4.22"
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
MIICgDCCAiagAwIBAgIQJIoAqJ7+VlIEhGDvXJYxwTAKBggqhkjOPQQDAjBJMR8w
HQYDVQQKExZsbmQgYXV0b2dlbmVyYXRlZCBjZXJ0MSYwJAYDVQQDEx1Fc2VuYmVr
cy1NYWNCb29rLVByby1pNy5sb2NhbDAeFw0yMDAxMjEwNTIzMTlaFw0yMTAzMTcw
NTIzMTlaMEkxHzAdBgNVBAoTFmxuZCBhdXRvZ2VuZXJhdGVkIGNlcnQxJjAkBgNV
BAMTHUVzZW5iZWtzLU1hY0Jvb2stUHJvLWk3LmxvY2FsMFkwEwYHKoZIzj0CAQYI
KoZIzj0DAQcDQgAEWF+bLu2uBUcJeWiHmQ9RJ9EaNUMiKOb7z9VndePs5dydGFZv
0lX1VbfsaOpyrlOdlZcdIL0/hqMutHAoUlhfoKOB7zCB7DAOBgNVHQ8BAf8EBAMC
AqQwDwYDVR0TAQH/BAUwAwEB/zCByAYDVR0RBIHAMIG9gh1Fc2VuYmVrcy1NYWNC
b29rLVByby1pNy5sb2NhbIIJbG9jYWxob3N0ggR1bml4ggp1bml4cGFja2V0ggdi
dWZjb25uhwR/AAABhxAAAAAAAAAAAAAAAAAAAAABhxD+gAAAAAAAAAAAAAAAAAAB
hxD+gAAAAAAAAAQ2NH/9JUkfhwTAqAQWhxD+gAAAAAAAAGDgDP/+0yIdhxD+gAAA
AAAAALntMem5RM5khxD+gAAAAAAAAK7eSP/+ABEiMAoGCCqGSM49BAMCA0gAMEUC
IQC+9DLaZqNUWDF0KLQDGF1KPCLd4MFD+XxHssj0DUHt1wIgclsIHiR3hUtrPFLi
ZfD6wdSSgNsNh1iIHbxXbdVjGYU=""".trimIndent()

        macaroon.value =
            "0201036C6E6402EB01030A10B9522644E5DE0461B6CBD21FE2B5246F1201301A160A0761646472657373120472656164120577726974651A130A04696E666F120472656164120577726974651A170A08696E766F69636573120472656164120577726974651A140A086D616361726F6F6E120867656E65726174651A160A076D657373616765120472656164120577726974651A170A086F6666636861696E120472656164120577726974651A160A076F6E636861696E120472656164120577726974651A140A057065657273120472656164120577726974651A180A067369676E6572120867656E657261746512047265616400000620D374F1780292FC4A2A2D8BA44B739C4DA185C4EDA8235B00AAD37A49CC2E66DC"

        macaroon.value =
            "0201036C6E6402EB01030A10DF2F1EA29964D32ED377D7F5E188CABB1201301A160A0761646472657373120472656164120577726974651A130A04696E666F120472656164120577726974651A170A08696E766F69636573120472656164120577726974651A140A086D616361726F6F6E120867656E65726174651A160A076D657373616765120472656164120577726974651A170A086F6666636861696E120472656164120577726974651A160A076F6E636861696E120472656164120577726974651A140A057065657273120472656164120577726974651A180A067369676E6572120867656E657261746512047265616400000620C7CFE79C930DDDDE2995CBC207D768657A663D7D04F6B1EF548C23D35C9DD30E"
    }

    fun connect(view: View) {
        if (arrayOf(host, port, certificate, macaroon).any { it.value.isNullOrBlank() }) {
            error.value = "All fields are required"
            return
        }

        val credentials =
            RemoteLndCredentials(host.value!!, port.value!!.toInt(), certificate.value!!, macaroon.value!!)

        interactor.validateConnection(credentials)
    }

    // IInteractorDelegate

    override fun onValidationSuccess(remoteLndCredentials: RemoteLndCredentials) {
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
