package io.horizontalsystems.lightningkit.demo.unlock

import io.horizontalsystems.lightningkit.ILndNode
import io.horizontalsystems.lightningkit.LightningKit
import io.horizontalsystems.lightningkit.demo.core.App
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class UnlockWalletInteractor(private val lightningKit: LightningKit) :
    UnlockWalletModule.IInteractor {
    lateinit var delegate: UnlockWalletPresenter
    private val disposables = CompositeDisposable()

    override fun unlock(password: String) {
        disposables.add(
            lightningKit.unlockWallet(password)
                .subscribe({
                    subscribeToKitStatus()
                }, {
                    delegate.onUnlockFailed(it)
                })
        )
    }

    override fun clear() {
        disposables.clear()
    }

    private fun subscribeToKitStatus() {
        val subscription = App.lightningKit.statusObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it == ILndNode.Status.RUNNING || it == ILndNode.Status.SYNCING) {
                    delegate.onUnlockSuccess()
                }
            }

        disposables.add(subscription)
    }

}