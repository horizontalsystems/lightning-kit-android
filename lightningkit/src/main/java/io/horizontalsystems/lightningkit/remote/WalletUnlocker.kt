package io.horizontalsystems.lightningkit.remote

import com.github.lightningnetwork.lnd.lnrpc.UnlockWalletRequest
import com.github.lightningnetwork.lnd.lnrpc.UnlockWalletResponse
import com.github.lightningnetwork.lnd.lnrpc.WalletUnlockerGrpc
import com.google.protobuf.ByteString
import io.reactivex.Single

class WalletUnlocker(private val asyncWalletUnlockerStub: WalletUnlockerGrpc.WalletUnlockerStub) {
    object UnlockingException: Exception("Wallet unlock already in progress") {}

    private val unlockWaitTime = 30000L
    private var unlockFinishTime: Long? = null

    fun startUnlock(password: String): Single<Unit> {
        val request = UnlockWalletRequest
            .newBuilder()
            .setWalletPassword(ByteString.copyFromUtf8(password))
            .build()

        return Single
            .create<UnlockWalletResponse> { asyncWalletUnlockerStub.unlockWallet(request, StreamObserverToSingle(it)) }
            .map { Unit }
            .doAfterSuccess { unlockFinishTime = System.currentTimeMillis() }
            .doOnError { unlockFinishTime = null }
    }

    fun isUnlocking(): Boolean {
        unlockFinishTime?.let {
            return it + unlockWaitTime > System.currentTimeMillis()
        }

        return false
    }

}
