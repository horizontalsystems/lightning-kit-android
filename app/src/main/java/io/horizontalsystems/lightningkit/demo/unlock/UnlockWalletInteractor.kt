package io.horizontalsystems.lightningkit.demo.unlock

import io.horizontalsystems.lightningkit.LightningKit
import io.reactivex.disposables.CompositeDisposable

class UnlockWalletInteractor(private val lightningKit: LightningKit) :
    UnlockWalletModule.IInteractor {
    lateinit var delegate: UnlockWalletPresenter
    private val disposables = CompositeDisposable()

    override fun unlock(password: String) {
        disposables.add(
            lightningKit.unlockWallet(password)
                .subscribe({
                    delegate.onUnlockSuccess()
                }, {
                    delegate.onUnlockFailed(it)
                })
        )
    }

    override fun clear() {
        disposables.clear()
    }

}